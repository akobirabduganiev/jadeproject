package dev.akobir;

import jade.Boot;

public class Main {
    public static void main(String[] args) {
        String[] agent1Args = {"-gui", "ClustererAgent:ClusteringAgent"};
        Boot.main(agent1Args);
        String[] agent2Args = {"-container", "CoordinatorAgent:ClusteringAgent"};
        Boot.main(agent2Args);
    }
}