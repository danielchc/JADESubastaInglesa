package vendedor;

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
		guiVendedor.imprimirMensaxe(msg);
	}

	public void engadirSubasta(Subasta subasta) {
		this.subastasDisponibles.put(subasta.getTitulo(), subasta);
		guiVendedor.engadirSubasta(subasta);
		imprimirMensaxe(String.format("Subasta engadida: %s por %d (+%d)",subasta.getTitulo(),subasta.getPrezo(),subasta.getIncremento()));
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
			if(this!=null)
				DFService.deregister(this);
		} catch (FIPAException fe) {}

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
				if (subasta.getInteresados().size() <= 1 && subasta.getGanadorActual() != null && subasta.getEstado()!= Subasta.EstadoSubasta.FINALIZADA) {

					//Notificamos o ganador
					ACLMessage notificar = new ACLMessage(ACLMessage.REQUEST);
					notificar.addReceiver(subasta.getGanadorActual());
					notificar.setContent(String.format("%s;%d;%s", subasta.getTitulo(), subasta.prezoAnterior(),subasta.getGanadorActual().getName()));
					myAgent.send(notificar);

					//Notificamos a toda a sala
					notificar = new ACLMessage(ACLMessage.INFORM);
					poxadoresDisponibles.stream().filter(k->!k.equals(subasta.getGanadorActual())).forEach(notificar::addReceiver);
					notificar.setContent(String.format("%s;%d;%s", subasta.getTitulo(), subasta.prezoAnterior(),subasta.getGanadorActual().getName()));
					myAgent.send(notificar);

					//Actualizamos a subasta e a interface
					imprimirMensaxe(String.format("O poxador %s ganou a subasta de %s por %d",subasta.getGanadorActual().getName(),subasta.getTitulo(), subasta.prezoAnterior()));
					subasta.setEstado(Subasta.EstadoSubasta.FINALIZADA);
					subasta.setPrezo(subasta.prezoAnterior());
					guiVendedor.actualizarSubasta(subasta);
				}
			}
		}

		private void enviarNotification() {
			for (Subasta subasta : subastasDisponibles.values().stream().filter(s -> s.getEstado()!= Subasta.EstadoSubasta.FINALIZADA).collect(Collectors.toList())) {
				subasta.eliminarInteresados();
				subasta.setEstado(Subasta.EstadoSubasta.ANUNCIADA);
				imprimirMensaxe(String.format("Anunciouse %s por %d", subasta.getTitulo(),subasta.getPrezo()));
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				poxadoresDisponibles.forEach(cfp::addReceiver);
				cfp.setContent(String.format("%s;%d", subasta.getTitulo(), subasta.getPrezo()));
//				cfp.setReplyWith("cfp" + System.currentTimeMillis());
				myAgent.send(cfp);
			}
		}
	}

	private class XestionSubastas extends CyclicBehaviour{
		private MessageTemplate mt;

		@Override
		public void action() {
			mt= MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);

			ACLMessage resposta = myAgent.receive(mt);
			if (resposta != null) {
				String[] partes = resposta.getContent().split(";");
				String titulo = partes[0];
				Integer prezo = Integer.parseInt(partes[1]);

				//Se non existe a subasta ignorase a peticion
				if (!subastasDisponibles.containsKey(titulo))return;

				Subasta subasta = subastasDisponibles.get(titulo);
				if (resposta.getPerformative() == ACLMessage.PROPOSE)
					propostaPoxador(resposta, subasta, prezo);


			}else {
				block();
			}

		}

		private void propostaPoxador(ACLMessage resposta, Subasta subasta, Integer prezoRecibido) {
			//Engadimos interesado se non existe
			subasta.engadirInteresado(resposta.getSender());

			//En principio rexeitamos todas as propostas
			ACLMessage notificacion = new ACLMessage(ACLMessage.REJECT_PROPOSAL);

			//Comprobamos se a proposta é ganadora, neste caso aceptamola(o prezo aumentase polo cal asignaraselle o primeiro)
			if (prezoRecibido.equals(subasta.getPrezo())) {
				subasta.setGanadorActual(resposta.getSender());
				subasta.engadirIncremento();
				imprimirMensaxe(String.format("O poxador %s aceptou a proposta %s por %d",resposta.getSender().getName(),subasta.getTitulo(),prezoRecibido));
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
