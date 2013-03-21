package de.jg3d;

public class ForceWorker extends Thread {
	Graph g;
	int start;
	int mod;
	Vector[] forces;

	public ForceWorker(Graph g, int start, int mod) {
		super();
		this.g = g;
		this.start = start;
		this.mod = mod;		
	}
	
	public void setForces(Vector[] forces) {
		this.forces = forces;
	}

    @Override
	public void run() {
		g.calcForces(forces, start, mod);
	}
        
}
