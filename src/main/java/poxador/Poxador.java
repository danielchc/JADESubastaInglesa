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
import ontologia.SubastaOntology;
import ontologia.impl.InformarRonda;
import ontologia.impl.InformarVictoria;
import ontologia.impl.Ofertar;
import ontologia.impl.Proponer;

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
			if(resposta!=null) {
				Concept concept = null;
				try {
					concept = ((Action) getContentManager().extractContent(resposta)).getAction();
				} catch (OntologyException | Codec.CodecException e) {
					e.printStackTrace();
				}

				if (resposta.getPerformative() == ACLMessage.CFP) {
					propostaPoxa(resposta, concept);
				} else if (resposta.getPerformative() == ACLMessage.REQUEST || resposta.getPerformative() == ACLMessage.INFORM)
					propostaGanadora(resposta, concept);
				else if (resposta.getPerformative() == ACLMessage.ACCEPT_PROPOSAL || resposta.getPerformative() == ACLMessage.REJECT_PROPOSAL)
					rondaFinalizada(resposta, concept);
			}else{
				block();
			}
		}

		private void propostaPoxa(ACLMessage resposta, Concept concept) {
			Ofertar a = (Ofertar) concept;
			if (!obxectivos.containsKey(a.getOfertaEnviar().getTitulo()))
				return;

			ACLMessage proposta = resposta.createReply();
			proposta.setOntology(onto.getName());
			proposta.setLanguage(codec.getName());
			if (obxectivos.get(a.getOfertaEnviar().getTitulo()).getPrezoMaximo() >= a.getOfertaEnviar().getPrezo()) {
				imprimirMensaxe(String.format("O vendedor propuxo %s por %d, aceptamos ", a.getOfertaEnviar().getTitulo(), a.getOfertaEnviar().getPrezo()));
				//Se aceptamos a proposta, enviamos o propose

				Proponer proponer = new Proponer();
				proponer.setPropostaOferta(a.getOfertaEnviar());

				try {
					getContentManager().fillContent(proposta, new Action(myAgent.getAID(), proponer));
				} catch (OntologyException | Codec.CodecException e) {
					e.printStackTrace();
				}
				proposta.setPerformative(ACLMessage.PROPOSE);
				myAgent.send(proposta);

			} else {
				//Se a subasta é demasiado elevada establezco o estado a retirado
				obxectivos.get(a.getOfertaEnviar().getTitulo()).setEstadoObxectivo(Obxectivo.EstadoObxectivo.RETIRADO);
				guiPoxador.actualizarObxectivo(obxectivos.get(a.getOfertaEnviar().getTitulo()));
				imprimirMensaxe(String.format("O vendedor propuxo %s por %d, retiramonos!", a.getOfertaEnviar().getTitulo(), a.getOfertaEnviar().getPrezo()));
			}

		}

		private void rondaFinalizada(ACLMessage resposta,Concept concept) {
			InformarRonda informarRonda = (InformarRonda) concept;
			String titulo = informarRonda.getOfertaGanadoraRonda().getTitulo();
			int prezo = informarRonda.getOfertaGanadoraRonda().getPrezo();
			String ganador = informarRonda.getGanadorRonda().getName();

			if (!obxectivos.containsKey(titulo)) return;


			Obxectivo obxectivo = obxectivos.get(informarRonda.getOfertaGanadoraRonda().getTitulo());
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

		private void propostaGanadora(ACLMessage resposta, Concept concept) {
			InformarVictoria informarVictoria =(InformarVictoria)concept;
			String titulo = informarVictoria.getOfertaGanadora().getTitulo();
			int prezo = informarVictoria.getOfertaGanadora().getPrezo();
			String ganador = informarVictoria.getAgenteGanador().getName();


			if (resposta.getPerformative() == ACLMessage.INFORM)
				imprimirMensaxe("O poxador " + ganador + " ganou a poxa de " + titulo + " por " + prezo);
			else if (resposta.getPerformative() == ACLMessage.REQUEST)
				imprimirMensaxe("Ganaches a poxa " + titulo + " por " + prezo);


			//Se se atopa na miña lista actualizoo
			if (obxectivos.containsKey(titulo)){
				Obxectivo obxectivo = obxectivos.get(titulo);
				obxectivo.setPrezoActual(prezo);
				obxectivo.setEstadoObxectivo((resposta.getPerformative() == ACLMessage.INFORM) ? Obxectivo.EstadoObxectivo.PERDIDA : Obxectivo.EstadoObxectivo.GANADA);
				obxectivo.setGanadorActual(ganador);
				obxectivo.setPrezoActual(prezo);
				guiPoxador.actualizarObxectivo(obxectivo);
			}
		}
	}

}
