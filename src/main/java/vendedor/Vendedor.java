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

                if (!subastasDisponibles.isEmpty())
                    addBehaviour(new XestionSubastas());

            } catch (FIPAException fe) {
                fe.printStackTrace();
            }

        }

        private void comprobarGanadores() {
            for (Subasta subasta : subastasDisponibles.values()) {
                if (subasta.getPoxadores().size() <= 1 && subasta.getGanadorActual() != null && !subasta.isFinalizada()) {
                    ACLMessage finalizacion = new ACLMessage(ACLMessage.REQUEST);
                    finalizacion.addReceiver(subasta.getGanadorActual());
                    finalizacion.setContent(String.format("%s;%d", subasta.getTitulo(), (subasta.prezoAnterior())));
                    subasta.setFinalizada(true);
                    subasta.setPrezo(subasta.prezoAnterior());
                    guiVendedor.actualizarSubasta(subasta);
                    myAgent.send(finalizacion);
                }
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
                    else if (resposta.getPerformative() == ACLMessage.REFUSE)
                        retirarPoxador(resposta, subasta, prezo);
                    respostasPendentes--;

                } else {
                    block();
                }
            }
        }

        private void enviarNotification() {
            for (Subasta subasta : subastasDisponibles.values().stream().filter(s -> !s.isFinalizada()).collect(Collectors.toList())) {
                imprimirMensaxe("Estase a subastar " + subasta.getTitulo() + " por " + subasta.getPrezo());
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

            guiVendedor.actualizarSubasta(subasta);

            ACLMessage notificacion = new ACLMessage(ACLMessage.INFORM);
            notificacion.addReceiver(resposta.getSender());
            notificacion.setConversationId("subasta-ronda");
            notificacion.setContent(String.format("%s;%d;%s", subasta.getTitulo(), subasta.prezoAnterior(), subasta.getGanadorActual().getName()));
            myAgent.send(notificacion);
        }

        private void retirarPoxador(ACLMessage resposta, Subasta subasta, Integer prezoRecibido) {
            subasta.eliminarPoxador(resposta.getSender());

            if(!resposta.getConversationId().equals("subasta-baixa"))
                return;

            ACLMessage notificacion = new ACLMessage(ACLMessage.INFORM);
            notificacion.addReceiver(resposta.getSender());
            notificacion.setConversationId("subasta-baixa");
            notificacion.setContent(String.format("%s;%d", subasta.getTitulo(), prezoRecibido));
            myAgent.send(notificacion);
        }

    }

}
