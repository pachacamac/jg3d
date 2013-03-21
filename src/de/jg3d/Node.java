package de.jg3d;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

public class Node {
	private Vector position; // position
	private Vector projection; // projection (z=0)

	private double weight = 25; // ToDo
	private double diameter = 10; // todo

	private String name;
	private Color color;
	private List<Edge> adjacencies;

	private static int instanceCounter = 0;

	private Vector velocity;
	private Vector selfforce;

	public static double maxAttraction = 100;
	public static double maxRepulsion = 100;

	private boolean fixed;

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	private void init(Vector pos, String name, Color color) {
		this.position = pos;
		this.name = name;
		this.color = color;
		velocity = new Vector(0, 0, 0);
		selfforce = new Vector(0, 0, 0);
		adjacencies = new LinkedList<Edge>();
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double w) {
		this.weight = w;
	}

	public double getDiameter() {
		return diameter;
	}

	public double getRadius() {
		return getDiameter() / 2;
	}

	public Node() {
		instanceCounter++;
		init(new Vector(0, 0, 0), Integer.toString(instanceCounter), Color.red);
	}

	public Node(Vector p) {
		instanceCounter++;
		init(p, Integer.toString(instanceCounter), Color.RED);
	}

	public Node(Vector p, String name) {
		instanceCounter++;
		init(p, name, Color.RED);
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public Color getColor() {
		int p = (int) (127 - position.getZ());
		if (p < 5)
			p = 5;
		else if (p > 250)
			p = 250;
		Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), p);
		return c;
	}

	public void setName(String n) {
		this.name = n;
	}

	public void setPos(Vector p) {
		this.position = p;
	}

	public Vector getPos() {
		return position;
	}

	public List<Edge> getAdjacencies() {
		return adjacencies;
	}

	public String getName() {
		return name;
	}

	public boolean connectedTo(Node node) {
		for (Edge edge : adjacencies)
			if (edge.getDestination() == node)
				return true;
		return false;
	}

	public Edge getEdgeTo(Node node) {
		for (Edge edge : adjacencies)
			if (edge.getDestination() == node)
				return edge;
		return null;
	}

	public void project(double canvasWidth, double canvasHeight) {
		projection = position.get2D(canvasWidth, canvasHeight);
	}

	@Override
	public String toString() {
		return new StringBuilder().append('[').append(name).append(']').toString();
	}

	public Vector getProjection() {
		return projection;
	}

	// repulsive force to node
	// distance einbauen => wtf?
	public Vector repulsiveForce(Node node) {
		Vector force = position.add(node.getPos().invert()); // force = a - b
		// (abs(a-b) =
		// distance!!!!)
		force = force.multiply(1 / Math.pow(force.absoluteValue(), 3)); // normalize
		force = force.multiply(weight * node.getWeight()); // weighting

		if (force.absoluteValue() > maxRepulsion) { // reduce extraterrestrial
			// uberforces
			System.out.println("repulsive uberforce: " + force.absoluteValue());
			force = force.multiply(maxRepulsion / force.absoluteValue());
		}
		return force;
	}

	// sum of attractive forces to all adjacencies
	public Vector attractiveForce() {
		Vector force = new Vector(0, 0, 0);
		Vector attraction;
		for (Edge edge : adjacencies) {
			attraction = position.add(edge.getDestination().getPos().invert()); // force
			// =
			// a
			// -
			// b
			attraction = attraction.multiply(1 / Math.pow(attraction.absoluteValue(), 0.5)); // normalize
			force = force.add(attraction.multiply(edge.getWeight()));
		}

		// if (force.absoluteValue() > maxAttraction) { // reduce
		// extraterrestrial uberforces
		// System.out.println("attractive uberforce: "+force.absoluteValue());
		// force = force.multiply(maxAttraction / force.absoluteValue());
		// }

		return force.invert();
	}

	public void affect(Vector force) {
		if (!fixed) {
			velocity = velocity.add(force.multiply(1 / weight)); // inertia
			Vector friction = velocity.multiply(0.025); // 2.5% friction
			velocity = velocity.add(friction.invert());
			velocity = velocity.add(selfforce);
			position = position.add(velocity);
		}
	}

	public void alterSelfForceX(double d) {
		selfforce.setX(selfforce.getX() + d);
	}

	public void alterSelfForceY(double d) {
		selfforce.setY(selfforce.getY() + d);
	}

	public void alterSelfForceZ(double d) {
		selfforce.setZ(selfforce.getZ() + d);
	}

	public void setSelfForce(Vector v) {
		selfforce = v;
	}

	public void setSelfForceX(double d) {
		selfforce.setX(d);
	}

	public void setSelfForceY(double d) {
		selfforce.setY(d);
	}

	public void setSelfForceZ(double d) {
		selfforce.setZ(d);
	}

}
