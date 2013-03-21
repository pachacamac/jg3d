package de.jg3d;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import java.util.LinkedList;
import java.util.List;
import javax.swing.JApplet;
import javax.swing.event.MouseInputListener;

import de.jg3d.util.Importer;
import de.jg3d.util.TpsCounter;


public class Main extends JApplet implements Runnable, MouseInputListener, KeyListener {

	Node hit = null;
	boolean shiftIsDown = false;
	boolean ctrlIsDown = false;
	private static final long serialVersionUID = 9101840683353633974L;

	private Thread thread;
	private BufferedImage bimg;

	public Graph graph;

	private TpsCounter tick;

	private Vector totalforce = new Vector();

	private boolean showNodes = true;
	private boolean showNodeNames = false;
	private boolean showEdges = true;
	private boolean showEdgeNames = false;
	private boolean showHud = true;
	private boolean showHelp = false;
	private boolean showEdgeWeights = false;
	private boolean showNodeWeights = false;

	private ForceWorker forceWorker1;
	private ForceWorker forceWorker2;

	public void keyPressed(KeyEvent e) {
		// System.out.println(e);
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			if (!shiftIsDown)
				shiftIsDown = true;
			break;
		case KeyEvent.VK_CONTROL:
			if (!ctrlIsDown)
				ctrlIsDown = true;
			break;
		case KeyEvent.VK_UP:
			graph.getNode(0).alterSelfForceY(-2);
			break;
		case KeyEvent.VK_DOWN:
			graph.getNode(0).alterSelfForceY(2);
			break;
		case KeyEvent.VK_RIGHT:
			graph.getNode(0).alterSelfForceX(2);
			break;
		case KeyEvent.VK_LEFT:
			graph.getNode(0).alterSelfForceX(-2);
			break;
		case KeyEvent.VK_PAGE_UP:
			graph.getNode(0).alterSelfForceZ(2);
			break;
		case KeyEvent.VK_PAGE_DOWN:
			graph.getNode(0).alterSelfForceZ(-2);
			break;
		case KeyEvent.VK_HOME:
			graph.getNode(0).setPos(new Vector(0, 0, 0));
			graph.getNode(0).setFixed(true);
			graph.getNode(0).setSelfForce(new Vector(0, 0, 0));
			break;
		}

		switch (e.getKeyChar()) {
		case '?':
			showHelp = !showHelp;
			break;

		case '-': // pseudo-unzoom
			for (Node n : graph.getNodes())
				n.getPos().setZ(n.getPos().getZ() + 10);
			break;
		case '+': // pseudo-zoom
			for (Node n : graph.getNodes())
				n.getPos().setZ(n.getPos().getZ() - 10);
			break;
		case 'i': // invert all fixings
			for (Node n : graph.getNodes())
				n.setFixed(!n.isFixed());
			break;
		case 'f': // fix all nodes
			for (Node n : graph.getNodes())
				n.setFixed(true);
			break;
		case 'u': // unfix all nodes
			for (Node n : graph.getNodes())
				n.setFixed(false);
			break;
		case 'r': // reduce all edge weights
			for (Edge n : graph.getEdges())
				n.setWeight(n.getWeight() - 0.5);
			break;
		case 't': // enhance all edge weights
			for (Edge n : graph.getEdges())
				n.setWeight(n.getWeight() + 0.5);
			break;
		case 'h':
			showHud = !showHud;
			break;
		case 'n':
			showNodes = !showNodes;
			break;
		case 'l':
			showNodeNames = !showNodeNames;
			break;
		case 'm':
			showNodeWeights = !showNodeWeights;
			break;
		case 'e':
			showEdges = !showEdges;
			break;
		case 'b':
			showEdgeNames = !showEdgeNames;
			break;
		case 'd':
			showEdgeWeights = !showEdgeWeights;
			break;
		case 'q':
			for (Node n : graph.getNodes())
				if (!n.isFixed())
					n.getPos().rotateX(0.05);
			break;
		case 'w':
			for (Node n : graph.getNodes())
				if (!n.isFixed())
					n.getPos().rotateX(-0.05);
			break;
		case 'a':
			for (Node n : graph.getNodes())
				if (!n.isFixed())
					n.getPos().rotateY(0.05);
			break;
		case 's':
			for (Node n : graph.getNodes())
				if (!n.isFixed())
					n.getPos().rotateY(-0.05);
			break;
		case 'y':
			for (Node n : graph.getNodes())
				if (!n.isFixed())
					n.getPos().rotateZ(0.05);
			break;
		case 'x':
			for (Node n : graph.getNodes())
				if (!n.isFixed())
					n.getPos().rotateZ(-0.05);
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT && shiftIsDown) {
			shiftIsDown = false;
			System.out.println("shift released");
		}
		if (e.getKeyCode() == KeyEvent.VK_CONTROL && ctrlIsDown) {
			ctrlIsDown = false;
			System.out.println("ctrl released");
		}
	}

	public void keyTyped(KeyEvent e) {

	}

	public void mouseDragged(MouseEvent e) {
		if (!shiftIsDown)
			hit.setPos(new Vector(e.getX() - 400, e.getY() - 300, 0));
	}

	public void mouseMoved(MouseEvent e) {
		if (ctrlIsDown) {
			if (hit != null) {
				Node tmp = new Node(new Vector(e.getX() - 400, e.getY() - 300, 0));
				if (hit.getPos().distance(tmp.getPos()) > 20) {
					graph.addNode(tmp);
					graph.connect(hit, tmp, 10);
					hit = tmp;
				}
			} else {
				hit = graph.hit(e.getPoint());
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2)
			if (!shiftIsDown)
				graph.addNode(new Node(new Vector(e.getX() - 400, e.getY() - 300, 0)));
			else
				graph.remNode(graph.hit(e.getPoint()));
		if (e.getClickCount() == 3)
			if (shiftIsDown)
				graph.remNodeRec(graph.hit(e.getPoint()));
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		hit = graph.hit(e.getPoint());
		if (hit != null && !shiftIsDown)
			hit.setFixed(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 && !shiftIsDown) {
			if (hit != null)
				hit.setFixed(false);
			hit = null;
		} else if (e.getButton() == MouseEvent.BUTTON1 && shiftIsDown) {
			if (hit != null) {
				Node tmp = hit;
				hit = graph.hit(e.getPoint());
				if (tmp != hit)
					if (!tmp.connectedTo(hit))
						graph.connect(tmp, hit, 10);
					else
						graph.disconnect(tmp, hit);
				hit = tmp;
			} else
				hit = null;
		}
	}

	@Override
	public void init() {
		setBackground(Color.black);
		setFocusable(true); // VERY IMPORTANT for making the keylistener work on linux!!!!
		tick = new TpsCounter(100);

		graph = new Graph();
		
		//examples:
		// grid:
		// nodeGrid(graph,4,4,8,false,false);
		// nodeGrid(graph,7,7,8,false,false);
		// tube:
		// nodeGrid(graph,13,13,8,true,false);
		// torus:
		//nodeGrid(graph, 12, 18, 14, true, true);
		
		//XXX: assuming a dualcore cpu...
		forceWorker1 = new ForceWorker(graph, 0, 2);
		forceWorker2 = new ForceWorker(graph, 1, 2);
	}

	public static void connectNodesToNearestNeighbours(Graph g, double radius) {
		for (Node n : g.getNodes())
			connectNodeToNearestNeighbours(g, n, radius);
	}

	public static void connectNodeToNearestNeighbours(Graph g, Node n, double radius) {
		List<Node> inrange = g.findNodesInRange(n.getPos(), radius);
		for (Node m : inrange)
			if (n != m)
				g.connect(n, m, 5);
	}

	public static void connectNodeGrids(Graph g, List<List<Node>> a, List<List<Node>> b, double ew) {
		for (int i = 0; i < a.size(); i++)
			for (int j = 0; j < a.get(0).size(); j++) {
				b.get(i).get(j).getPos().setPos(a.get(i).get(j).getPos().getX(),
					a.get(i).get(j).getPos().getY() + 10, a.get(i).get(j).getPos().getZ());
				g.connect(a.get(i).get(j), b.get(i).get(j), ew);
			}
	}

	public static List<List<Node>> nodeGrid(Graph g, int width, int height, double ew,
			boolean vconnect, boolean hconnect) {
		List<List<Node>> grid = new LinkedList<List<Node>>();
		List<Node> a, b, first;
		grid.add(first = a = nodeLine(g, width, ew));
		for (int i = 1; i < height; i++) {
			if (hconnect)
				g.connect(a.get(0), a.get(a.size() - 1), ew);
			grid.add(b = nodeLine(g, width, ew));
			if (hconnect)
				g.connect(b.get(0), b.get(a.size() - 1), ew);
			for (int j = 0; j < a.size(); j++)
				g.connect(a.get(j), b.get(j), ew);
			a = b;
		}
		if (vconnect)
			for (int j = 0; j < a.size(); j++)
				g.connect(a.get(j), first.get(j), ew);
		return grid;
	}

	public static List<Node> nodeLine(Graph g, int cnt, double ew) {
		List<Node> nl = new LinkedList<Node>();
		Node a, b;
		a = new Node(new Vector(100.0));
		nl.add(a);
		g.addNode(a);
		for (; cnt > 1; cnt--) {
			b = new Node(new Vector(100.0));
			nl.add(b);
			g.addNode(b);
			g.connect(a, b, ew);
			a = b;
		}
		return nl;
	}

	public static void tree(Graph graph, Node node, int depth) {
		if (depth == 0)
			return;
		Node root = new Node(new Vector(500));
		Node leaf1 = new Node(new Vector(500));
		Node leaf2 = new Node(new Vector(500));
		Node leaf3 = new Node(new Vector(500));
		graph.addNode(root);
		graph.addNode(leaf1);
		graph.addNode(leaf2);
		graph.addNode(leaf3);
		graph.connect(root, leaf1, 3);
		graph.connect(root, leaf2, 3);
		graph.connect(root, leaf3, 3);
		graph.connect(node, root, 3);
		tree(graph, leaf1, depth - 1);
		tree(graph, leaf2, depth - 1);
		tree(graph, leaf3, depth - 1);
	}

	public void transform() {
		for (Node node : graph.getNodes()) {
			node.getPos().rotateX(0.008);
			node.getPos().rotateY(0.01);
			node.getPos().rotateZ(0.02);
		}
	}

	public void project() {
		for (Node node : graph.getNodes())
			node.project(800, 600);
	}

	public void step() {
		tick.tick();

		Vector[] forces = new Vector[graph.getNodes().size()];
		for (int i = 0; i < forces.length; i++)
			forces[i] = new Vector(0, 0, 0);

		forceWorker1.setForces(forces);
		forceWorker2.setForces(forces);

		forceWorker1.run();
		forceWorker2.run();

		try {
			forceWorker1.join();
			forceWorker2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		totalforce = graph.affectForces(forces);
	}

	public void drawScene(int w, int h, Graphics2D g2) {
		if (showEdges)
			for (Node node : graph.getNodes())
				for (Edge edge : node.getAdjacencies()) {
					g2.setColor(edge.getColor());
					g2.drawLine((int) node.getProjection().getX(), (int) node.getProjection()
							.getY(), (int) edge.getDestination().getProjection().getX(), (int) edge
							.getDestination().getProjection().getY());
					if (showEdgeWeights)
						g2.drawString("" + edge.getWeight(), (int) (edge.getSource()
								.getProjection().getX() + edge.getDestination().getProjection()
								.getX()) / 2, (int) (edge.getSource().getProjection().getY() + edge
								.getDestination().getProjection().getY()) / 2);
					if (showEdgeNames && edge.getLabel() != null)
						g2.drawString("" + edge.getLabel(), (int) (edge.getSource().getProjection()
								.getX() + edge.getDestination().getProjection().getX()) / 2,
							(int) (edge.getSource().getProjection().getY() + edge.getDestination()
									.getProjection().getY()) / 2);
				}

		if (showNodes)
			for (Node node : graph.getNodes()) {
				if (node.getAdjacencies().size() <= 1)
					node.setColor(Color.red);
				else if (node.getAdjacencies().size() <= 2)
					node.setColor(Color.yellow);
				else if (node.getAdjacencies().size() <= 3)
					node.setColor(Color.white);
				else if (node.getAdjacencies().size() <= 4)
					node.setColor(Color.cyan);
				else if (node.getAdjacencies().size() <= 5)
					node.setColor(Color.blue);
				else if (node.getAdjacencies().size() <= 6)
					node.setColor(Color.magenta);
				else if (node.getAdjacencies().size() <= 7)
					node.setColor(Color.pink);
				g2.setColor(node.getColor());
				g2.fill(new Ellipse2D.Double(node.getProjection().getX() - node.getRadius(), node
						.getProjection().getY()
						- node.getRadius(), node.getDiameter(), node.getDiameter()));
				if (showNodeWeights)
					g2.drawString("" + node.getWeight(), (int) node.getProjection().getX(),
						(int) node.getProjection().getY());
				if (showNodeNames)
					g2.drawString("" + node.getName(), (int) node.getProjection().getX(),
						(int) node.getProjection().getY());
			}

		if (showHud) {
			drawTextBlock(g2, "FPS : " + tick + "\n" + "Nodes : " + graph.getNodes().size() + "\n"
					+ "Edges : " + graph.getEdges().size() + "\n" + "Force : " + totalforce + " ("
					+ totalforce.sum() + ")", 10, 10, Color.LIGHT_GRAY);

		}
		if (showHelp)
			drawTextBlock(g2, "Help:\n" + "Left mouse and drag:\n"
					+ "  move node near mouse (unfix)\n" + "Right mouse and drag:\n"
					+ "  move node near mouse and fix\n" + "Doubleclick:\n" + "  create new node\n"
					+ "Shift and doubleclick:\n" + "  delete node near mouse\n"
					+ "Drag and drop one node on another:\n" + "  connect them to each other\n"
					+ "Shift and drag/drop one node on another:\n" + "  disconnect them\n"
					+ "Ctrl and mouse-move\n" + "  create a line of nodes\n\n"
					+ "X-Rotation: q / w\n" + "Y-Rotation: a / s\n" + "Z-Rotation: y / x\n"
					+ "Pseudozoom: + / -\n" + "Toggle nodes: n\n" + "Toggle nodeNames: l\n"
					+ "Toggle node weights: m\n" + "Toggle edges: e\n" + "Toggle edgeNames: b\n"
					+ "Toggle edge weights: d\n" + "Toggle hud: h\n" + "Toogle help: ?\n"
					+ "Fix all nodes: f\n" + "Unfix all nodes: u\n" + "Invert node fixations: i\n"
					+ "Decrease edge weights: r\n" + "Enhance edge weights: t\n", 10, 80,
				new Color(255, 0, 0, 127));
	}

	private static void drawTextBlock(Graphics2D g2, String txt, int x, int y, Color c) {
		g2.setColor(c);
		String[] lines = txt.split("\n");
		for (String line : lines)
			g2.drawString(line, x, y += 15);
	}

	public Graphics2D createGraphics2D(int w, int h) {
		Graphics2D g2 = null;
		if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h)
			bimg = (BufferedImage) createImage(w, h);
		g2 = bimg.createGraphics();
		g2.setBackground(getBackground());
		g2.clearRect(0, 0, w, h);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return g2;
	}

	@Override
	public synchronized void paint(Graphics g) {
		Dimension d = getSize();
		step();
		project();
		Graphics2D g2 = createGraphics2D(d.width, d.height);
		drawScene(d.width, d.height, g2);
		g2.dispose();
		g.drawImage(bimg, 0, 0, this);
	}

	@Override
	public void start() {
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Override
	public synchronized void stop() {
		thread = null;
	}

	public void run() {
		Thread me = Thread.currentThread();
		while (thread == me) {
			repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				break;
			}
		}
		thread = null;
	}

	public static void main(String argv[]) {
		final Main demo = new Main();
		demo.init();
		if (argv.length > 0) { //if we got a file, let's try to load it
			Importer.importfile(demo.graph, argv[0]);
		} else { //or show a simple node-grid
			nodeGrid(demo.graph, 7, 7, 8, false, false);
		}
		Frame f = new Frame("jG3D (press ? for help)");
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			@Override
			public void windowDeiconified(WindowEvent e) {
				demo.start();
			}
			@Override
			public void windowIconified(WindowEvent e) {
				demo.stop();
			}
		});

		demo.addMouseListener(demo);
		demo.addMouseMotionListener(demo);
		demo.addKeyListener(demo);
		f.add(demo);
		f.pack();
		f.setSize(new Dimension(800, 600));
		f.setVisible(true);
		demo.start();

	}
}
