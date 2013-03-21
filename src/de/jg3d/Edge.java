package de.jg3d;

import java.awt.Color;

public class Edge {

	private double weight;
	private Node destination;
	private Node source;
	private Color color;
	private String label;
	
	public Edge(Node source, Node destination, double weight) {
		this.destination = destination;
		this.source = source;
		this.weight = weight;
		this.color = Color.green;
	}

	public void setLabel(String label){
		this.label=label;
	}
	
	public String getLabel(){
		return label;
	}
	
	public Node getDestination() {
		return destination;
	}

	public Node getSource() {
		return source;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double w) {
		this.weight = w;
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public Color getColor() {
		int p = (int) (127 - ((source.getPos().getZ() + destination.getPos().getZ()) / 2));
		if (p < 5)
			p = 5;
		else if (p > 250)
			p = 250;
		Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), p);
		return c;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(source.getName()).append("]--(").append(weight).append(")-->[")
				.append(destination.getName()).append(']');
		return sb.toString();
	}

}
