package de.jg3d;

public class Vector {
	private double x, y, z;

	public Vector() {
		x = y = z = 0;
	}

	public Vector(Vector p) {
		this.x = p.getX();
		this.y = p.getY();
		this.z = p.getZ();
	}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector add(Vector p) {
		return new Vector(x + p.getX(), y + p.getY(), z + p.getZ());
	}

	public Vector abs() {
		return new Vector(Math.abs(x), Math.abs(y), Math.abs(z));
	}

	public double sum() {
		return x + y + z;
	}

	public Vector multiply(double alpha) {
		return new Vector(x * alpha, y * alpha, z * alpha);
	}

	public Vector invert() {
		return new Vector(-x, -y, -z);
	}

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public Vector(double rndboxsize) {
		this.x = Math.random() * rndboxsize - rndboxsize / 2;
		this.y = Math.random() * rndboxsize - rndboxsize / 2;
		this.z = Math.random() * rndboxsize - rndboxsize / 2;
	}

	@Override
	public String toString() {
		return "[x:" + round(x, 2) + " ; y:" + round(y, 2) + " ; z:" + round(z, 2) + "]"; // ToDo:
																							// Stringbuilder
	}

	private double round(double val, int fraction) {
		double factor = Math.pow(10, fraction);
		return Math.round(val * factor) / factor;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setPos(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
	}

	public void setPos(double x, double y) {
		setX(x);
		setY(y);
		setZ(0);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double distance(Vector p) {
		return Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2)
				+ Math.pow(z - p.getZ(), 2));
	}

	public double absoluteValue() {
		// return distance(new Vector(0, 0, 0));
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector midpoint(Vector p) {
		return new Vector((x + p.getX()) / 2, (y + p.getY()) / 2, (z + p.getZ()) / 2);
	}

	public Vector get2D(double canvasWidth, double canvasHeight) {
		return new Vector(get2Dx(canvasWidth), get2Dy(canvasHeight), 0);
	}

	public double get2Dx(double canvaswidth) {
		return canvaswidth * (x / (z + canvaswidth)) + canvaswidth / 2;
	}

	public double get2Dy(double canvasheight) {
		return canvasheight * (y / (z + canvasheight)) + canvasheight / 2;
	}

	public void rotateX(double beta) {
		double tmpy = y * Math.cos(beta) - z * Math.sin(beta);
		z = y * Math.sin(beta) + z * Math.cos(beta);
		y = tmpy;
	}

	public void rotateY(double beta) {
		double tmpx = z * Math.sin(beta) + x * Math.cos(beta);
		z = z * Math.cos(beta) - x * Math.sin(beta);
		x = tmpx;
	}

	public void rotateZ(double beta) {
		double tmpx = x * Math.cos(beta) - y * Math.sin(beta);
		y = y * Math.cos(beta) + x * Math.sin(beta);
		x = tmpx;
	}

}
