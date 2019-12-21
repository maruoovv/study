package composite;

import java.util.ArrayList;
import java.util.List;

class OrganizationChart {
    private Node node;
    public OrganizationChart(Node node) {
        this.node = node;
    }

    public void print() {
        node.print();
    }
}

interface Node {
    String getName();
    void print();
}

class Person implements Node {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void print() {
        System.out.println(name);
    }
}

class Department implements Node {
    private String name;
    private List<Node> nodes;

    public Department(String name) {
        this.name = name;
        nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void print() {
        for (Node node : nodes) {
            node.print();
        }
    }
}
