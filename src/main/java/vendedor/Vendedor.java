package vendedor;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import vendedor.GUIVendedor;


import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;


public class Vendedor extends Agent {
    private final int TEMPO_PUXA = 10000;
    private HashMap<String, Subasta> subastasDisponibles;
    private ArrayList<AID> poxadoresDisponibles;
    private GUIVendedor guiVendedor;
    private EventManager eventManager;


    @Override
    public void setup() {
        this.subastasDisponibles = new HashMap<>();
        this.poxadoresDisponibles = new ArrayList<>();
        rexistrarServizo();

        GUIVendedor guiVendedor=new GUIVendedor(this);
        eventManager=guiVendedor.getEventManager();
        guiVendedor.setVisible(true);

    }
    protected void takeDown() {
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        if(guiVendedor!=null) guiVendedor.dispose();
    }

    public void engadirSubasta(Subasta subasta){
        this.subastasDisponibles.put(subasta.getTitulo(), subasta);
        eventManager.engadirSubasta(subasta);
        System.out.println("Subasta engadida " +subasta.getTitulo());
    }

    public boolean existeSubasta(Subasta subasta) {
        return subastasDisponibles.containsKey(subasta.getTitulo());
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
            serviceDescription.setType("subata-poxador");
            dfAgentDescription.addServices(serviceDescription);
            poxadoresDisponibles = new ArrayList<>();
            try {
                DFAgentDescription[] result = DFService.search(myAgent, dfAgentDescription);
                poxadoresDisponibles.addAll(Arrays.stream(result).map(k -> k.getName()).collect(Collectors.toList()));

                if (!subastasDisponibles.isEmpty())
                    addBehaviour(new XestionSubastas());

            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

        }

        private void comprobarGanadores() {
            ArrayList<String> subastasTerminadas=new ArrayList<String>();
            for(Subasta subasta:subastasDisponibles.values()){
                if(subasta.getPoxadores().size()<=1 && subasta.getGanadorActual()!=null){
                    ACLMessage finalizacion=new ACLMessage(ACLMessage.REQUEST);
                    finalizacion.addReceiver(subasta.getGanadorActual());
                    finalizacion.setContent(String.format("%s;%d",subasta.getTitulo(),(subasta.prezoAnterior())));
                    subastasTerminadas.add(subasta.getTitulo());
                    myAgent.send(finalizacion);
                }
            }

            for (String subasta:subastasTerminadas){
                subastasDisponibles.remove(subasta);
            }

        }

    }

    private class XestionSubastas extends Behaviour {
        private int paso = 0;
        private MessageTemplate mt;
        private int respostasPendentes = poxadoresDisponibles.size() * subastasDisponibles.size();

        @Override
        public boolean done() {
            return (respostasPendentes == 0);
        }

        @Override
        public void action() {
            if (paso == 0) {
                enviarNotification();
            } else if (paso == 1) {
                ACLMessage resposta = myAgent.receive(mt);
                if (resposta == null) {
                    block();
                    return; //REVISAR ESTO XD
                }
                String[] partes = resposta.getContent().split(";");
                String titulo = partes[0];
                Integer prezo = Integer.parseInt(partes[1]);
                if (!subastasDisponibles.containsKey(titulo)) {
                    return;
                }
                Subasta subasta = subastasDisponibles.get(titulo);

                if (resposta.getPerformative() == ACLMessage.PROPOSE)
                    propostaPoxador(resposta, subasta, prezo);
                else if (resposta.getPerformative() == ACLMessage.REFUSE && resposta.getConversationId().equals("subasta-baixa"))
                    retirarPoxador(resposta, subasta, prezo);

                respostasPendentes--;

            }
        }

        private void enviarNotification() {
            for (Subasta subasta : subastasDisponibles.values()) {
                System.out.println("Informar subasta "+subasta.getTitulo());
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                poxadoresDisponibles.forEach(cfp::addReceiver);
                cfp.setConversationId("subasta-notificacion");
                cfp.setContent(String.format("%s;%d", subasta.getTitulo(), subasta.getPrezo()));
                cfp.setReplyWith("cfp" + System.currentTimeMillis());
                myAgent.send(cfp);
            }
            mt = MessageTemplate.or(MessageTemplate.MatchConversationId("subasta-notificacion"), MessageTemplate.MatchConversationId("subasta-baixa"));
            paso = 1;
        }

        private void propostaPoxador(ACLMessage resposta, Subasta subasta, Integer prezoRecibido) {

            if (!subasta.getPoxadores().contains(resposta.getSender()))
                subasta.engadirPoxador(resposta.getSender());


            if (prezoRecibido.equals(subasta.getPrezo())) {
                subasta.setGanadorActual(resposta.getSender());
                subasta.engadirIncremento();
            }

            eventManager.actualizarSubasta(subasta);

            ACLMessage notificacion = new ACLMessage(ACLMessage.INFORM);
            notificacion.addReceiver(resposta.getSender());
            notificacion.setConversationId("subasta-ronda");
            notificacion.setContent(String.format("%s;%d;%s", subasta.getTitulo(), subasta.prezoAnterior(), subasta.getGanadorActual().getName()));
            myAgent.send(notificacion);
        }

        private void retirarPoxador(ACLMessage resposta, Subasta subasta, Integer prezoRecibido) {
            
            subasta.eliminarPoxador(resposta.getSender());

            ACLMessage notificacion = new ACLMessage(ACLMessage.INFORM);
            notificacion.addReceiver(resposta.getSender());
            notificacion.setConversationId("subasta-baixa");
            notificacion.setContent(String.format("%s;%d", subasta.getTitulo(), prezoRecibido));
            myAgent.send(notificacion);
        }

    }

}
