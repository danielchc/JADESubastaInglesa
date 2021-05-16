package vendedor;

import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ontologia.SubastaOntology;
import ontologia.impl.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.stream.Collectors;


public class Vendedor extends Agent {
	private final int TEMPO_PUXA = 10000;
	private HashMap<String, Subasta> subastasDisponibles;
	private ArrayList<AID> poxadoresDisponibles;
	private GUIVendedor guiVendedor;
	private SLCodec codec;
	private Ontology onto;


	private void imprimirMensaxe(String msg) {
		System.out.println(String.format("[%s] %s", getName(), msg));
		guiVendedor.imprimirMensaxe(msg);
	}

	public void engadirSubasta(Subasta subasta) {
		this.subastasDisponibles.put(subasta.getTitulo(), subasta);
		guiVendedor.engadirSubasta(subasta);
		imprimirMensaxe(String.format("Subasta engadida: %s por %d (+%d)", subasta.getTitulo(), subasta.getPrezo(), subasta.getIncremento()));
	}

	public boolean existeSubasta(Subasta subasta) {
		return subastasDisponibles.containsKey(subasta.getTitulo());
	}

	@Override
	public void setup() {
		this.subastasDisponibles = new HashMap<>();
		this.poxadoresDisponibles = new ArrayList<>();
		rexistrarServizo();


		/* ONTOLOGIAS */

		codec = new SLCodec();
		onto = SubastaOntology.getInstance();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(onto);

		/* ONTOLOGIAS */

		guiVendedor = new GUIVendedor(this);
		guiVendedor.setVisible(true);

	}

	@Override
	protected void takeDown() {
		try {
			if (this != null)
				DFService.deregister(this);
		} catch (FIPAException fe) {
		}

		if (guiVendedor != null) guiVendedor.dispose();
	}

	private void rexistrarServizo() {
		DFAgentDescription dfdAgent = new DFAgentDescription();
		dfdAgent.setName(getAID());
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("subasta-subastador");
		serviceDescription.setName("Practica6");
		dfdAgent.addServices(serviceDescription);
		try {
			DFService.register(this, dfdAgent);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new Planificador(this, TEMPO_PUXA));
		addBehaviour(new XestionSubastas());
	}


	private class Planificador extends TickerBehaviour {
		public Planificador(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			for (Subasta subasta : subastasDisponibles.values()) {
				if (subasta.getEstado() == Subasta.EstadoSubasta.FINALIZADA) continue;

				if (!subasta.getInteresados().isEmpty())
					imprimirMensaxe(String.format("Hai %d interesados en %s ", subasta.getInteresados().size(), subasta.getTitulo()));

				if (subasta.getInteresados().size() <= 1 && subasta.getGanadorActual() != null) {
					comprobarGanadores(subasta);
				} else if (subasta.getInteresados().size() > 1) {
					comprobarRonda(subasta);
				}
				guiVendedor.actualizarSubasta(subasta);
				subasta.eliminarInteresados();
			}


			DFAgentDescription dfAgentDescription = new DFAgentDescription();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType("subasta-poxador");
			dfAgentDescription.addServices(serviceDescription);
			poxadoresDisponibles = new ArrayList<>();
			try {
				DFAgentDescription[] result = DFService.search(myAgent, dfAgentDescription);
				poxadoresDisponibles.addAll(Arrays.stream(result).map(DFAgentDescription::getName).collect(Collectors.toList()));
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}

			enviarNotification();
		}

		private void comprobarGanadores(Subasta subasta) {

			int prezo = (subasta.getInteresados().size() == 0) ? subasta.prezoAnterior() : subasta.getPrezo();

			InformarVictoria informarVictoria = new InformarVictoria();
			Oferta oferta = new Oferta();
			oferta.setTitulo(subasta.getTitulo());
			oferta.setPrezo(prezo);
			informarVictoria.setAgenteGanador(subasta.getGanadorActual());
			informarVictoria.setOfertaGanadora(oferta);

			//Notificamos o ganador
			ACLMessage notificar = new ACLMessage(ACLMessage.REQUEST);
			notificar.addReceiver(subasta.getGanadorActual());
			notificar.setOntology(onto.getName());
			notificar.setLanguage(codec.getName());
			try {
				getContentManager().fillContent(notificar, new Action(myAgent.getAID(), informarVictoria));
			} catch (OntologyException | Codec.CodecException e) {
				e.printStackTrace();
			}

			myAgent.send(notificar);


			//Notificamos a toda a sala
			notificar = new ACLMessage(ACLMessage.INFORM);
			poxadoresDisponibles.stream().filter(k -> !k.equals(subasta.getGanadorActual())).forEach(notificar::addReceiver);
			notificar.setOntology(onto.getName());
			notificar.setLanguage(codec.getName());

			try {
				getContentManager().fillContent(notificar, new Action(myAgent.getAID(), informarVictoria));
			} catch (OntologyException | Codec.CodecException e) {
				e.printStackTrace();
			}
			myAgent.send(notificar);


			//Actualizamos a subasta e a interface
			imprimirMensaxe(String.format("O poxador %s ganou a subasta de %s por %d", subasta.getGanadorActual().getName(), subasta.getTitulo(), prezo));
			subasta.setEstado(Subasta.EstadoSubasta.FINALIZADA);
			subasta.setPrezo(prezo);

		}

		private void comprobarRonda(Subasta subasta) {
			AID aidActual = null;
			Iterator<AID> poxadorIterator = subasta.getInteresados().iterator();
			aidActual = poxadorIterator.next();
			subasta.setGanadorActual(aidActual);
			imprimirMensaxe(String.format("O poxador %s aceptou a proposta %s por %d", aidActual.getName(), subasta.getTitulo(), subasta.getPrezo()));
			subasta.engadirIncremento();

			InformarRonda informarRonda = new InformarRonda();
			Oferta oferta = new Oferta();
			oferta.setTitulo(subasta.getTitulo());
			oferta.setPrezo(subasta.getPrezo());
			informarRonda.setOfertaGanadoraRonda(oferta);
			informarRonda.setGanadorRonda(aidActual);


			ACLMessage notificacion = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			notificacion.setLanguage(codec.getName());
			notificacion.setOntology(onto.getName());
			notificacion.addReceiver(aidActual);

			try {
				getContentManager().fillContent(notificacion, new Action(myAgent.getAID(), informarRonda));
			} catch (OntologyException | Codec.CodecException e) {
				e.printStackTrace();
			}
			myAgent.send(notificacion);

			if (poxadorIterator.hasNext()) {
				notificacion = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				notificacion.setLanguage(codec.getName());
				notificacion.setOntology(onto.getName());
				while (poxadorIterator.hasNext()) notificacion.addReceiver(poxadorIterator.next());
				try {
					getContentManager().fillContent(notificacion, new Action(myAgent.getAID(), informarRonda));
				} catch (OntologyException | Codec.CodecException e) {
					e.printStackTrace();
				}
				myAgent.send(notificacion);
			}

		}

		private void enviarNotification() {
			if (poxadoresDisponibles.isEmpty()) return;
			ACLMessage cfp;
			for (Subasta subasta : subastasDisponibles.values().stream().filter(s -> s.getEstado() != Subasta.EstadoSubasta.FINALIZADA).collect(Collectors.toList())) {
				subasta.setEstado(Subasta.EstadoSubasta.ANUNCIADA);
				imprimirMensaxe(String.format("Anunciouse %s por %d", subasta.getTitulo(), subasta.getPrezo()));
				cfp = new ACLMessage(ACLMessage.CFP);
				poxadoresDisponibles.forEach(cfp::addReceiver);
				cfp.setOntology(onto.getName());
				cfp.setLanguage(codec.getName());
				Oferta oferta = new Oferta();
				oferta.setTitulo(subasta.getTitulo());
				oferta.setPrezo(subasta.getPrezo());
				Ofertar ofertar = new Ofertar();
				ofertar.setOfertaEnviar(oferta);
				try {
					getContentManager().fillContent(cfp, new Action(myAgent.getAID(), ofertar));
				} catch (OntologyException | Codec.CodecException e) {
					e.printStackTrace();
				}

				myAgent.send(cfp);
			}
		}

	}

	private class XestionSubastas extends CyclicBehaviour {
		private MessageTemplate mt;

		@Override
		public void action() {
			mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.and(
						MessageTemplate.MatchLanguage(codec.getName()),
						MessageTemplate.MatchOntology(onto.getName())
					)
			);

			ACLMessage resposta = myAgent.receive(mt);
			Proponer proponer = null;
			if (resposta != null) {
				try {
					proponer = (Proponer) (((Action) getContentManager().extractContent(resposta)).getAction());
				} catch (OntologyException | Codec.CodecException e) {
					e.printStackTrace();
				}

				//Se non existe a subasta ignorase a peticion
				if (!subastasDisponibles.containsKey(proponer.getPropostaOferta().getTitulo())) return;

				Subasta subasta = subastasDisponibles.get(proponer.getPropostaOferta().getTitulo());
				if (resposta.getPerformative() == ACLMessage.PROPOSE) {
					if (proponer.getPropostaOferta().getPrezo() >= subasta.getPrezo()) {
						subasta.engadirInteresado(resposta.getSender());
						subasta.setGanadorActual(subasta.getInteresados().get(0));
						guiVendedor.actualizarSubasta(subasta);
					}
				}
			} else {
				block();
			}
		}
	}

}
