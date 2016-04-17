package ImagesWebcam;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Driver for interacting with a quadtree:
 * inserting points, viewing the tree, and finding points near a mouse press
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 */
public class BlobTreeGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe
	private static final int blobRadius = 5;			// uniform radius of all blobs
	private static final Color[] rainbow = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA};
			// to color different levels differently

	private BlobTree tree = null;						// holds the blobs
	private char mode = 'a';							// 'a': adding points; 'q': querying with the mouse
	private int mouseX, mouseY;							// current mouse location, when querying
	private int mouseRadius = 10;						// circle around mouse location, for querying
	private ArrayList<Blob> found = null;				// who was found near mouse, when querying
	public BlobTreeGUI() {
		super("blob=tree", width, height);
	}

	/**
	 * DrawingGUI method, here keeping track of the location and redrawing to show it
	 */
	public void handleMouseMotion(int x, int y) {
		if (mode == 'q') {
			mouseX = x; mouseY = y;
			repaint();
		}
	}

	/**
	 * DrawingGUI method, here either adding a new point or querying near the mouse
	 */
	public void handleMousePress(int x, int y) {
		if (mode == 'a') {
			if (tree == null){
				tree = new BlobTree(new Blob(x,y), 0, 0, width, height);
			}
			else{
				tree.insert(new Blob(x,y));
			}

		}
		else if (mode == 'q') {
			// Set "found" to what tree says is near the mouse press
			found = tree.findInCircle(x, y, blobRadius);
		}
		else {
			System.out.println("clicked at "+x+","+y);
		}
		repaint();
	}
	
	/**
	 * DrawingGUI method, here toggling the mode between 'a' and 'q'
	 * and increasing/decresing mouseRadius via +/-
	 */
	public void handleKeyPress(char key) {
		if (key=='a' || key=='q') mode = key;
		else if (key=='+') {
			mouseRadius += 10;
			repaint();
		}
		else if (key=='-') {
			mouseRadius -= 10;
			if (mouseRadius < 0) mouseRadius=0;
			repaint();
		}
	}

	/**
	 * DrawingGUI method, here drawing the quadtree
	 * and if in query mode, the mouse location and any found blobs
	 */
	public void draw(Graphics g) {
		if (tree != null) drawTree(g, tree, 0);
		if (mode == 'q') {
			g.setColor(Color.BLACK);
			g.drawOval(mouseX-mouseRadius, mouseY-mouseRadius, 2*mouseRadius, 2*mouseRadius);
			if (found != null) {
				g.setColor(Color.BLACK);
				for (Blob b : found) b.draw(g);
			}
		}
	}

	/**
	 * Draws the blob tree
	 * @param g		the graphics object for drawing
	 * @param tree	a blob tree (not necessarily root)
	 * @param level	how far down from the root qt is (0 for root, 1 for its children, etc.)
	 */
	public void drawTree(Graphics g, BlobTree tree, int level) {
		// Set the color for this level
		g.setColor(rainbow[level % rainbow.length]);
		// Draw this node's blob and lines through it
		// Recurse with children
        if (tree.noChildren()){
            tree.getBlob().draw(g);
			g.drawLine(tree.getX1(), (int)tree.getBlob().getY(), tree.getX2(), (int)tree.getBlob().getY());
			g.drawLine((int)tree.getBlob().getX(), tree.getY1(), (int)tree.getBlob().getX(), tree.getY2());
        }
		if (tree.hasC1()){
			drawTree(g, tree.getC1(), level+1);
		}
		if (tree.hasC2()){
			drawTree(g, tree.getC2(), level+1);
		}
		if (tree.hasC3()){
			drawTree(g, tree.getC3(), level+1);
		}
		if (tree.hasC4()){
			drawTree(g, tree.getC4(), level+1);
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BlobTreeGUI();
			}
		});
	}
}