package ImagesWebcam;

import java.util.ArrayList;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * 
 */
public class BlobTree {
	private Blob blob;						// the blob anchoring this nod
	private int x1, y1;						// upper-left corner of the region
	private int x2, y2;						// bottom-right corner of the region
    private BlobTree c1, c2, c3, c4;        // children of blob in respective quadrants
	/**
	 * Initializes a leaf quadtree, holding the blob in the rectangle
	 */
	public BlobTree(Blob blob, int x1, int y1, int x2, int y2) {
		this.blob = blob;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        this.c1 = null; this.c2 = null; this.c3 = null; this.c4 = null;
	}

	public Blob getBlob() {
		return blob;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

    public BlobTree getC1() { return c1; }
    public BlobTree getC2() { return c2; }
    public BlobTree getC3() { return c3; }
    public BlobTree getC4() { return c4; }

	public boolean noChildren() {
        return (c1 == null && c2 == null && c3 == null && c4 == null);
    }

    public boolean hasC1(){
        return c1 != null;
    }
    public boolean hasC2(){
        return c2 != null;
    }
    public boolean hasC3(){
        return c3 != null;
    }
    public boolean hasC4(){
        return c4 != null;
    }

	/**
	 * Inserts the blob into the tree
	 */
	public void insert(Blob b2) {

        // check quadrant 1
        if (b2.getX() > this.getBlob().getX() && (b2.getY() < this.getBlob().getY())){
            if (this.c1 != null) {
                c1.insert(b2);
            }
            else {
                c1 = new BlobTree(b2, (int)this.getBlob().getX(), this.getY1(),
                    this.getX2(), (int)this.getBlob().getY()); }
            }
        // check quadrant 2
        if (b2.getX() < this.getBlob().getX() && b2.getY() < this.getBlob().getY()) {
            if (this.c2 != null) {
                c2.insert(b2);
            }
            else {
                c2 = new BlobTree(b2, this.getX1(), this.getY1(), (int) this.getBlob().getX(),
                        (int) this.getBlob().getY());
            }
        }

        // check quadrant 3
        if (b2.getX() < this.getBlob().getX() && b2.getY() > this.getBlob().getY()){
            if (this.c3 != null){
                c3.insert(b2);
            }
            else {
                c3 = new BlobTree(b2, this.getX1(), (int)this.getBlob().getY(), (int) this.getBlob().getX(),
                        this.getY2());
            }

        }

        // check quadrant 4
        if (b2.getX() > this.getBlob().getX() && b2.getY() > this.getBlob().getY()){
            if (this.c4 != null){
                c4.insert(b2);
            }
            else {
                c4 = new BlobTree(b2, (int) this.getBlob().getX(), (int)this.getBlob().getY(), this.getX2(), this.getY2());
            }
        }
    }

	/**
	 * Uses the quadtree to find all blobs within the circle
	 * @param cx	  circle center x
	 * @param cy  circle center y
	 * @param cr  circle radius
	 * @return    the blobs in the circle (and the rectangle)
	 */
	public ArrayList<Blob> findInCircle(double cx, double cy, double cr) {
        ArrayList<Blob> b= new ArrayList<Blob>();
		if (circleIntersectsRectangle(cx, cy, cr, this.getX1(), this.getY1(), this.getX2(), this.getY2())){
            if (pointInCircle(this.getBlob().getX(), this.getBlob().getY(), cx, cy, cr)){
                addToBlobs(b);
            }
        }
        return b;
	}

    public void addToBlobs(ArrayList<Blob> blobs) {
        if (noChildren()) {
            blobs.add(blob);
        }
        else {
            if (hasC1()) c1.addToBlobs(blobs);
            if (hasC2()) c2.addToBlobs(blobs);
            if (hasC3()) c3.addToBlobs(blobs);
            if (hasC4()) c4.addToBlobs(blobs);
        }
    }

	/**
	 * Returns whether or not the point is within the circle
	 * @param px		point x coord
	 * @param py		point y coord
	 * @param cx		circle center x
	 * @param cy		circle center y
	 * @param cr		circle radius
	 */
	public static boolean pointInCircle(double px, double py, double cx, double cy, double cr) {
		return (px-cx)*(px-cx) + (py-cy)*(py-cy) <= cr*cr;
	}

	/**
	 * Returns whether or not the circle intersects the rectangle
	 * Based on discussion at http://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection
	 * @param cx	circle center x
	 * @param cy	circle center y
	 * @param cr	circle radius
	 * @param x1 	rectangle min x
	 * @param y1  	rectangle min y
	 * @param x2  	rectangle max x
	 * @param y2  	rectangle max y
	 */
	public static boolean circleIntersectsRectangle(double cx, double cy, double cr, double x1, double y1, double x2, double y2) {
		double closestX = Math.min(Math.max(cx, x1), x2);
		double closestY = Math.min(Math.max(cy, y1), y2);
		return (cx-closestX)*(cx-closestX) + (cy-closestY)*(cy-closestY) <= cr*cr;
	}
}