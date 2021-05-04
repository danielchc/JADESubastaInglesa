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

    private HashMap<String, Integer> obxectivos;

    @Override
    public void setup() {


    }

    protected void takeDown() {
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
    }

    private class ConsultarSubastas extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.CFP),
                    MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                            MessageTemplate.MatchPerformative(ACLMessage.REQUEST))
            );
            ACLMessage resposta = myAgent.receive(mt);
            if (resposta == null) {
                block();
                return;
                //Comprobar esto xddd
            }
            if (resposta.getPerformative() == ACLMessage.CFP)
                propostaPoxa(resposta, myAgent);
            else if (resposta.getPerformative() == ACLMessage.REQUEST)
                propostaGanadora(resposta);
            else if (resposta.getPerformative() == ACLMessage.INFORM)
                rondaFinalizada(resposta);

        }
    }

    private void rondaFinalizada(ACLMessage resposta) {
        String contido[] = resposta.getContent().split(";");
        String titulo = contido[0];
        int prezo = Integer.parseInt(contido[1]);
        if (resposta.getConversationId().equals("subasta-ronda")) {
            String ganador = contido[2];
            System.out.println("Ganou a ronda de " + titulo + " o axente" + ganador + " por " + prezo);
        } else if (resposta.getConversationId().equals("subasta-baixa")) {
            System.out.println("Se ha dado de baja en la subasta " + resposta.getSender().getName() + " para: " + titulo + " cuando se poxaba por " + prezo);
        }

    }

    private void propostaGanadora(ACLMessage resposta) {
        String contido[] = resposta.getContent().split(";");
        String titulo = contido[0];
        int prezo = Integer.parseInt(contido[1]);
        System.out.println("Ganaches a poxa + " + titulo + " por " + prezo);
    }
}
