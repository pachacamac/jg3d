package de.jg3d;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Graph {
	private List<Node> nodes;
	private List<Edge> edges;

	public Graph() {
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
	}

	public Node addNode(Node node) {
		nodes.add(node);
		return nodes.get(nodes.size() - 1);
	}

	public void remNode(Node node) {
		for (Node n : nodes)
			if (n.connectedTo(node)) {
				Edge e = n.getEdgeTo(node);
				disconnect(n, node);
				edges.remove(e);
			}
		nodes.remove(node);
	}

	public void remNodeRec(final Node n) {
		/*
		 * new Thread(new Runnable(){ Node nodenow = n; public void run(){
		 * for(Edge e : nodenow.getAdjacencies())
		 * try{remNode(nodenow=e.getDestination());}catch(Exception ex){} try{
		 * Thread.sleep(50); }catch(Exception ex){} } }).start();
		 */
	}

	public Node getRandomNode() {
		return nodes.get((int) (Math.random() * nodes.size()));
	}

	public Node getNode(int index) {
		return nodes.get(index);
	}
	
	public Node getNode(String name){
		for(Node n : nodes)
			if(n.getName().equals(name))
				return n;
		return null;
	}

	public boolean connectTo(Node a, Node b, double weight) {
		if (a.connectedTo(b)) {
			Edge e = a.getEdgeTo(b);
			System.out.println("ERROR : " + a + " already connected to " + b + " with weight "
					+ e.getWeight());
			return false;
		} else {
			Edge e = null;
			a.getAdjacencies().add(e = new Edge(a, b, weight));
			edges.add(e);
			return true;
		}
	}

	public boolean connect(Node a, Node b, double weight) {
		boolean ret = true;
		ret &= connectTo(a, b, weight);
		ret &= connectTo(b, a, weight);
		return ret;
	}
	
	public boolean disconnectFrom(Node a, Node b) {
		if (a.connectedTo(b)) {
			Edge e = a.getEdgeTo(b);
			edges.remove(e);
			a.getAdjacencies().remove(e);
			System.out.println(a + " disconnected from " + b);
			return true;
		} else {
			System.out.println(a + " is not connected to " + b);
			return false;
		}
	}

	public boolean disconnect(Node a, Node b) {
		boolean ret = true;
		ret &= disconnectFrom(a, b);
		ret &= disconnectFrom(b, a);
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Node node : nodes) {
			sb.append(node);
			for (Edge edge : node.getAdjacencies())
				sb.append(edge);
			sb.append('\n');
		}
		return sb.toString();
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void calcForces(Vector[] forces, int start, int mod) { // actio == reactio
		Vector force;
		for (int i = start; i < forces.length; i += mod) {
			for (int j = i + 1; j < forces.length; j++) {
				force = nodes.get(i).repulsiveForce(nodes.get(j));
				forces[i] = forces[i].add(force);
				forces[j] = forces[j].add(force.multiply(-1));
			}
			forces[i] = forces[i].add(nodes.get(i).attractiveForce());
		}
	}

	public List<Node> findNodesInRange(Vector pos, double radius) {
		List<Node> inrange = new ArrayList<Node>();
		for (Node n : nodes)
			if (pos.distance(n.getPos()) <= radius)
				inrange.add(n);
		return inrange;
	}

	public Node hit(Point p) {
		Vector v = new Vector(p.getX(), p.getY(), 0);
		Node closestNode = null;
		double closestDistance = Double.MAX_VALUE;
		double distance;
		for (int i = 0; i < nodes.size(); i++) {
			distance = nodes.get(i).getProjection().distance(v);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestNode = nodes.get(i);
			}
		}
		return closestNode;
	}

	public Vector affectForces(Vector[] forces) {
		Vector totalforce = new Vector();
		for (int i = 0; i < nodes.size(); i++) {
			nodes.get(i).affect(forces[i]);
			totalforce = totalforce.add(forces[i].abs());
		}
		return totalforce;
	}

}
