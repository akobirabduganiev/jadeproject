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

/**
 The ClustererAgent is a class that represents an agent capable of performing clustering tasks. In a multi-agent system,
 the ClustererAgent can be responsible for handling and executing clustering algorithms, communicating with other agents to exchange data,
 and sending results to other agents or systems.

 The ClustererAgent typically implements the JADE framework's Agent interface, which provides methods to manage the agent's lifecycle, such as setup(),
 takeDown(), behaviour(), and handleMessage(). These methods can be used to initialize the agent, set up message handlers, and define the agent's behaviors.

 The ClustererAgent may also use one or more clustering algorithms to process data and generate clusters.
 These algorithms can be implemented within the ClustererAgent or can be imported as external libraries.
 The agent can communicate with other agents to exchange data or receive requests for clustering tasks.

 The ClustererAgent can be configured to perform clustering tasks on different types of data and using various clustering algorithms,
 making it a flexible and versatile component in multi-agent systems for data analysis and machine learning.
 **/

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

