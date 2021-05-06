package vendedor;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;


public class Vendedor extends Agent {
	private final int TEMPO_PUXA = 10000;
	private HashMap<String, Subasta> subastasDisponibles;
	private ArrayList<AID> poxadoresDisponibles;
	private GUIVendedor guiVendedor;


	private void imprimirMensaxe(String msg){
		System.out.println(String.format("[%s] %s",getName(),msg));
	}

	public void engadirSubasta(Subasta subasta) {
		this.subastasDisponibles.put(subasta.getTitulo(), subasta);
		guiVendedor.engadirSubasta(subasta);
		imprimirMensaxe("Subasta engadida " + subasta.getTitulo() + " por " + subasta.getPrezo());
	}

	public boolean existeSubasta(Subasta subasta) {
		return subastasDisponibles.containsKey(subasta.getTitulo());
	}

	@Override
	public void setup() {
		this.subastasDisponibles = new HashMap<>();
		this.poxadoresDisponibles = new ArrayList<>();
		rexistrarServizo();

		guiVendedor = new GUIVendedor(this);
		guiVendedor.setVisible(true);

	}

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
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
			comprobarGanadores();

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

		private void comprobarGanadores() {
			for (Subasta subasta : subastasDisponibles.values()) {
				if (subasta.getInteresados().size() <= 1 && subasta.getGanadorActual() != null && !subasta.isFinalizada()) {
					ACLMessage finalizacion = new ACLMessage(ACLMessage.REQUEST);
					finalizacion.addReceiver(subasta.getGanadorActual());
					finalizacion.setContent(String.format("%s;%d", subasta.getTitulo(), (subasta.prezoAnterior())));
					subasta.setFinalizada(true);
					subasta.setPrezo(subasta.prezoAnterior());
					guiVendedor.actualizarSubasta(subasta);
					myAgent.send(finalizacion);
					//Informar o resto que son uns perdedores
				}
			}
		}

		private void enviarNotification() {
			for (Subasta subasta : subastasDisponibles.values().stream().filter(s -> !s.isFinalizada()).collect(Collectors.toList())) {
				subasta.eliminarInteresados();
				imprimirMensaxe("Estase a subastar " + subasta.getTitulo() + " por " + subasta.getPrezo());
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				poxadoresDisponibles.forEach(cfp::addReceiver);
				cfp.setConversationId("subasta-notificacion");
				cfp.setContent(String.format("%s;%d", subasta.getTitulo(), subasta.getPrezo()));
				cfp.setReplyWith("cfp" + System.currentTimeMillis());
				myAgent.send(cfp);
			}
		}
	}

	private class XestionSubastas extends CyclicBehaviour{
		private MessageTemplate mt;

		@Override
		public void action() {
			mt = MessageTemplate.or(MessageTemplate.MatchConversationId("subasta-notificacion"), MessageTemplate.MatchConversationId("subasta-baixa"));

			ACLMessage resposta = myAgent.receive(mt);
			if (resposta != null) {
				String[] partes = resposta.getContent().split(";");
				String titulo = partes[0];
				Integer prezo = Integer.parseInt(partes[1]);
				if (!subastasDisponibles.containsKey(titulo)) {
					return;
				}
				Subasta subasta = subastasDisponibles.get(titulo);

				if (resposta.getPerformative() == ACLMessage.PROPOSE)
					propostaPoxador(resposta, subasta, prezo);


			}else {
				block();
			}

		}

		private void propostaPoxador(ACLMessage resposta, Subasta subasta, Integer prezoRecibido) {

			if (!subasta.getInteresados().contains(resposta.getSender()))
				subasta.engadirInteresado(resposta.getSender());

			ACLMessage notificacion = new ACLMessage(ACLMessage.REJECT_PROPOSAL);

			if (prezoRecibido.equals(subasta.getPrezo())) {
				subasta.setGanadorActual(resposta.getSender());
				subasta.engadirIncremento();
				notificacion.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			}


			notificacion.addReceiver(resposta.getSender());
			notificacion.setConversationId("subasta-ronda");
			notificacion.setContent(String.format("%s;%d;%s", subasta.getTitulo(), subasta.prezoAnterior(), subasta.getGanadorActual().getName()));
			myAgent.send(notificacion);

			guiVendedor.actualizarSubasta(subasta);

		}

	}

}
