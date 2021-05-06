package poxador;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

public class Poxador extends Agent {

	private HashMap<String, Obxectivo> obxectivos;
	private GUIPoxador guiPoxador;

	private void imprimirMensaxe(String msg) {
		System.out.println(String.format("[%s] %s", getName(), msg));
	}

	public void engadirObxectivo(Obxectivo obxectivo) {
		obxectivos.put(obxectivo.getTitulo(), obxectivo);
		guiPoxador.engadirObxectivo(obxectivo);
	}

	public boolean existeObxectivo(String text) {
		return obxectivos.containsKey(text);
	}

	public void eliminarObxectivo(String obxectivo) {
		obxectivos.remove(obxectivo);
		guiPoxador.eliminarObxectivo(obxectivo);
	}


	@Override
	public void setup() {
		guiPoxador = new GUIPoxador(this);
		obxectivos = new HashMap<>();
		rexistrarServizo();
		guiPoxador.setVisible(true);

	}

	@Override
	protected void takeDown() {
		if (guiPoxador != null) guiPoxador.dispose();
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
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

			if (resposta != null) {
				String contido[] = resposta.getContent().split(";");
				if (resposta.getPerformative() == ACLMessage.CFP)
					propostaPoxa(contido, resposta, myAgent);
				else if (resposta.getPerformative() == ACLMessage.REQUEST || resposta.getPerformative() == ACLMessage.INFORM)
					propostaGanadora(contido, resposta);
				else if (resposta.getPerformative() == ACLMessage.ACCEPT_PROPOSAL || resposta.getPerformative() == ACLMessage.REJECT_PROPOSAL)
					rondaFinalizada(contido, resposta);
			} else {
				block();
			}

		}
	}

	private void propostaPoxa(String[] contido, ACLMessage resposta, Agent myAgent) {
		String titulo = contido[0];
		int prezo = Integer.parseInt(contido[1]);
		ACLMessage proposta = resposta.createReply();
		proposta.setContent(String.format("%s;%d", titulo, prezo));

		if(!obxectivos.containsKey(titulo))return;

		if (obxectivos.get(titulo).getPrezoMaximo() >= prezo) {
			proposta.setPerformative(ACLMessage.PROPOSE);
			myAgent.send(proposta);
		}else{
			obxectivos.get(titulo).setEstadoObxectivo(Obxectivo.EstadoObxectivo.RETIRADO);
			guiPoxador.actualizarObxectivo(obxectivos.get(titulo));
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
            obxectivo.setEstadoObxectivo(Obxectivo.EstadoObxectivo.EN_CABEZA);
            imprimirMensaxe("Vas ganando a poxa " + titulo + " por " + prezo);
        } else if (resposta.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
            obxectivo.setEstadoObxectivo(Obxectivo.EstadoObxectivo.PERDENDO);
            imprimirMensaxe("Vas perdendo a poxa " + titulo + ". Vai ganando " + ganador + " por " + prezo);
        }

		guiPoxador.actualizarObxectivo(obxectivo);

	}

	private void propostaGanadora(String[] contido, ACLMessage resposta) {
		String titulo = contido[0];
		if (!obxectivos.containsKey(titulo)) return;
		int prezo = Integer.parseInt(contido[1]);
		Obxectivo obxectivo = obxectivos.get(titulo);
		obxectivo.setPrezoActual(prezo);

		if(resposta.getPerformative()==ACLMessage.INFORM){
			obxectivo.setEstadoObxectivo(Obxectivo.EstadoObxectivo.PERDIDA);
			imprimirMensaxe("Has perdido a poxa " + titulo + " por " + prezo);
		}else if(resposta.getPerformative()==ACLMessage.REQUEST){
			obxectivo.setEstadoObxectivo(Obxectivo.EstadoObxectivo.GANADA);
			imprimirMensaxe("Ganaches a poxa " + titulo + " por " + prezo);
		}



		guiPoxador.actualizarObxectivo(obxectivo);
	}


}
