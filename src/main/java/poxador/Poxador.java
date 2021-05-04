package poxador;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import vendedor.GUIVendedor;

import java.util.HashMap;

public class Poxador extends Agent {

    private HashMap<String, Obxectivo> obxectivos;
    private PoxadorEventManager poxadorEventManager;

    @Override
    public void setup() {
        poxadorEventManager=new PoxadorEventManager() {
            @Override
            public void actualizarObxectivo(Obxectivo obxectivo) {

            }

            @Override
            public void engadirObxectivo(Obxectivo obxectivo) {

            }
        };
        obxectivos=new HashMap<>();
        obxectivos.put("papo",new Obxectivo("papo",10));
        rexistrarServizo();
    }

    public void engadirObxectivo(Obxectivo obxectivo){
        obxectivos.put(obxectivo.getTitulo(),obxectivo);
        poxadorEventManager.engadirObxectivo(obxectivo);
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
        addBehaviour(new ConsultarSubastas());
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

            if (resposta != null) {
                String contido[] = resposta.getContent().split(";");
                if (resposta.getPerformative() == ACLMessage.CFP)
                    propostaPoxa(contido,resposta, myAgent);
                else if (resposta.getPerformative() == ACLMessage.REQUEST)
                    propostaGanadora(contido,resposta);
                else if (resposta.getPerformative() == ACLMessage.INFORM)
                    rondaFinalizada(contido,resposta);
            }else{
                block();
            }

        }
    }

    private void propostaPoxa(String[] contido, ACLMessage resposta, Agent myAgent) {
        String titulo = contido[0];
        int prezo = Integer.parseInt(contido[1]);
        ACLMessage proposta=resposta.createReply();
        proposta.setContent(String.format("%s;%d",titulo,prezo));


        if(!obxectivos.containsKey(titulo)){
            proposta.setPerformative(ACLMessage.REFUSE);
            myAgent.send(proposta);
            return;
        }
        if(obxectivos.get(titulo).getPrezoMaximo()>=prezo){
            proposta.setPerformative(ACLMessage.PROPOSE);
        }else{
            proposta.setPerformative(ACLMessage.REFUSE);
            proposta.setConversationId("subasta-baixa");
        }
        myAgent.send(proposta);
    }

    private void rondaFinalizada(String[] contido, ACLMessage resposta) {
        String titulo = contido[0];
        //Non ten sentido informar de subastas que non estou involucrado
        if(!obxectivos.containsKey(titulo))
            return;
        int prezo = Integer.parseInt(contido[1]);
        if (resposta.getConversationId().equals("subasta-ronda")) {
            String ganador = contido[2];
            System.out.println("Ganou a ronda de " + titulo + " o axente " + ganador + " por " + prezo);

            obxectivos.get(titulo).setGanadorActual(ganador);

        } else if (resposta.getConversationId().equals("subasta-baixa")) {
            System.out.println("Deuse de baixa da subasta " + resposta.getSender().getName() + " de " + titulo + " cuando se poxaba por " + prezo);
        }

    }

    private void propostaGanadora(String[] contido, ACLMessage resposta) {
        String titulo = contido[0];
        int prezo = Integer.parseInt(contido[1]);
        System.out.println("Ganaches a poxa " + titulo + " por " + prezo);
    }
}
