package dev.akobir;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import weka.clusterers.SimpleKMeans;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class ClustererAgent extends Agent {

    private SimpleKMeans clusterer;

    protected void setup() {
        // read in data
        ConverterUtils.DataSource source;
        try {
            source = new ConverterUtils.DataSource("data.arff");
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            // initialize clusterer
            clusterer = new SimpleKMeans();
            clusterer.setNumClusters(3);
            clusterer.buildClusterer(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // add behaviour to receive data to be clustered
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    // cluster data and send back clusters
                    String[] dataStrings = msg.getContent().split(",");
                    double[] data = new double[dataStrings.length];
                    for (int i = 0; i < dataStrings.length; i++) {
                        data[i] = Double.parseDouble(dataStrings[i]);
                    }
                    try {
                        Instances dataset = new Instances(clusterer.getClusterCentroids());
                        dataset.setClassIndex(-1);
                        Instance inst = new DenseInstance(1.0, data);
                        dataset.add(inst);
                        int cluster = clusterer.clusterInstance(inst);
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(Integer.toString(cluster));
                        send(reply);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    block();
                }
            }
        });
    }
}

