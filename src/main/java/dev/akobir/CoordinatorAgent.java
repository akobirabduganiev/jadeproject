package dev.akobir;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CoordinatorAgent extends Agent {

    private int[] clusters;

    protected void setup() {
        // add behaviour to send data to ClustererAgent
        addBehaviour(new Behaviour() {
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.setContent("1.2,2.3,3.4,4.5,5.6,6.7");
                msg.addReceiver(new AID("ClustererAgent", AID.ISLOCALNAME));
                send(msg);
            }

            public boolean done() {
                return true;
            }
        });
        // add behaviour to receive clusters from ClustererAgent
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    int cluster = Integer.parseInt(msg.getContent());
                    clusters[cluster]++;
                } else {
                    block();
                }
            }
        });
        // add behaviour to print clusters when all have been received
        addBehaviour(new Behaviour() {
            public void action() {
                if (clusters.length == 3
                ) {
                    System.out.println("Cluster 1: " + clusters[0]);
                    System.out.println("Cluster 2: " + clusters[1]);
                    System.out.println("Cluster 3: " + clusters[2]);
                    myAgent.doDelete();
                } else {
                    block();
                }
            }

            public boolean done() {
                return clusters.length == 3;
            }
        });
    }
}

