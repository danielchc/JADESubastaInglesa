package poxador;

import jade.content.Concept;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ontologia.Ofertar;
import ontologia.SubastaOntology;

import java.util.HashMap;

public class Poxador extends Agent {

	private HashMap<String, Obxectivo> obxectivos;
	private GUIPoxador guiPoxador;
	private SLCodec codec;
	private Ontology onto;

	private void imprimirMensaxe(String msg) {
		System.out.println(String.format("[%s] %s", getName(), msg));
		guiPoxador.imprimirMensaxe(msg);
	}

	public void engadirObxectivo(Obxectivo obxectivo) {
		obxectivos.put(obxectivo.getTitulo(), obxectivo);
		imprimirMensaxe(String.format("Engadido obxectivo: %s por un maximo de %d", obxectivo.getTitulo(), obxectivo.getPrezoMaximo()));
		guiPoxador.engadirObxectivo(obxectivo);
	}

	public void eliminarObxectivo(String obxectivo) {
		obxectivos.remove(obxectivo);
		imprimirMensaxe(String.format("Eliminado obxectivo: %s", obxectivo));
		guiPoxador.eliminarObxectivo(obxectivo);
	}

	public boolean existeObxectivo(String text) {
		return obxectivos.containsKey(text);
	}


	@Override
	public void setup() {
		guiPoxador = new GUIPoxador(this);
		obxectivos = new HashMap<>();
		rexistrarServizo();

		/* ONTOLOGIAS */

		codec = new SLCodec();
		onto = SubastaOntology.getInstance();
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(onto);

		/* ONTOLOGIAS */

		guiPoxador.setVisible(true);

	}

	@Override
	protected void takeDown() {
		if (guiPoxador != null) guiPoxador.dispose();
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
		}
	}

	private void rexistrarServizo() {
		DFAgentDescription dfdAgent = new DFAgentDescription();
		dfdAgent.setName(getAID());
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType("subasta-poxador");
		serviceDescription.setName("Practica6");
		dfdAgent.addServices(serviceDescription);
		try {
			DFService.register(this, dfdAgent);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new ConsultarSubastas());
	}

	private class ConsultarSubastas extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(
					MessageTemplate.MatchPerformative(ACLMessage.CFP),
					MessageTemplate.or(
							MessageTemplate.or(
									MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
									MessageTemplate.MatchPerformative(ACLMessage.INFORM)
							),
							MessageTemplate.or(
									MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
									MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL)
							)
					)
			);


			ACLMessage resposta = myAgent.receive(mt);
			Concept action = null;

			if (resposta != null) {
				if (resposta.getPerformative() == ACLMessage.CFP) {
					try {
						action = ((Action) getContentManager().extractContent(resposta)).getAction();
					} catch (Codec.CodecException e) {
						e.printStackTrace();
					} catch (OntologyException e) {
						e.printStackTrace();
					}
					propostaPoxa(resposta, action, myAgent);
				}

				if (resposta.getPerformative() == ACLMessage.REQUEST || resposta.getPerformative() == ACLMessage.INFORM)
					propostaGanadora(resposta.getContent().split(";"), resposta);
				else if (resposta.getPerformative() == ACLMessage.ACCEPT_PROPOSAL || resposta.getPerformative() == ACLMessage.REJECT_PROPOSAL)
					rondaFinalizada(resposta.getContent().split(";"), resposta);
			} else {
				block();
			}

		}
	}

	private void propostaPoxa(ACLMessage resposta, Concept action, Agent myAgent) {

		Ofertar a = (Ofertar) action;
		String titulo = a.getOferta().getTitulo();
		int prezo = a.getOferta().getPrezo();


		ACLMessage proposta = resposta.createReply();
		proposta.setContent(String.format("%s;%d", titulo, prezo));
		if (!obxectivos.containsKey(titulo)) return;

		if (obxectivos.get(titulo).getPrezoMaximo() >= prezo) {
			imprimirMensaxe(String.format("O vendedor propuxo %s por %d, aceptamos ", titulo, prezo));
			//Se aceptamos a proposta, enviamos o propose
			proposta.setPerformative(ACLMessage.PROPOSE);
			myAgent.send(proposta);
		} else {
			//Se a subasta é demasiado elevada establezco o estado a retirado
			obxectivos.get(titulo).setEstadoObxectivo(Obxectivo.EstadoObxectivo.RETIRADO);
			guiPoxador.actualizarObxectivo(obxectivos.get(titulo));
			imprimirMensaxe(String.format("O vendedor propuxo %s por %d, retiramonos!", titulo, prezo));
		}

	}

	private void rondaFinalizada(String[] contido, ACLMessage resposta) {
		String titulo = contido[0];
		if (!obxectivos.containsKey(titulo)) return;


		Obxectivo obxectivo = obxectivos.get(titulo);
		int prezo = Integer.parseInt(contido[1]);


		String ganador = contido[2];

		obxectivo.setGanadorActual(ganador);
		obxectivo.setPrezoActual(prezo);

		if (resposta.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
			obxectivo.setEstadoObxectivo(Obxectivo.EstadoObxectivo.GANANDO);
			imprimirMensaxe("Vas ganando a poxa " + titulo + " por " + prezo);
		} else if (resposta.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
			obxectivo.setEstadoObxectivo(Obxectivo.EstadoObxectivo.PERDENDO);
			imprimirMensaxe("Vas perdendo a poxa " + titulo + ". Vai ganando " + ganador + " por " + prezo);
		}

		guiPoxador.actualizarObxectivo(obxectivo);

	}

	private void propostaGanadora(String[] contido, ACLMessage resposta) {
		String titulo = contido[0];
		int prezo = Integer.parseInt(contido[1]);
		String ganador = contido[2];


		if (resposta.getPerformative() == ACLMessage.INFORM) {
			imprimirMensaxe("O poxador " + ganador + " ganou a poxa de " + titulo + " por " + prezo);
		} else if (resposta.getPerformative() == ACLMessage.REQUEST) {
			imprimirMensaxe("Ganaches a poxa " + titulo + " por " + prezo);
		}

		if (!obxectivos.containsKey(titulo))
			return;
		Obxectivo obxectivo = obxectivos.get(titulo);
		obxectivo.setPrezoActual(prezo);
		obxectivo.setEstadoObxectivo((resposta.getPerformative() == ACLMessage.INFORM) ? Obxectivo.EstadoObxectivo.PERDIDA : Obxectivo.EstadoObxectivo.GANADA);
		obxectivo.setGanadorActual(ganador);
		obxectivo.setPrezoActual(prezo);
		guiPoxador.actualizarObxectivo(obxectivo);
	}


}
