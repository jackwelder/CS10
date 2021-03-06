package Lab1;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Template for PS-1, Dartmouth CS 10, Spring 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 *
 * @author updated by jackelder and nickwhalley for PS-1 CS10
 * 4/11/16
 */

public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	// a region is a list of points so the identified regions are in a list of lists of points
	private ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public ArrayList<ArrayList<Point>> getRegions() { return regions; }

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
        Deque<Point> stack = new ArrayDeque<Point>(); // list of points to be visited
        BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB); // image that holds visited pixels

        // loop over entire image
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                if ((visited.getRGB(x, y) == 0) && (colorMatch(new Color(image.getRGB(x,y)), targetColor))) { // if conditions match
                    ArrayList<Point> region = new ArrayList<>(); // list of points in region
                    stack.push(new Point(x,y)); // add this point to the stack

           			// as long as there are items on the stack to visit, visit them and add them to regions list if conditions match
                    while (!(stack.isEmpty())){
						// push point off stack add to region
                        Point point = stack.pop();
                        region.add(point);

                        // visit neighbors but be careful not to go outside image (max, min stuff).
                        for (int ny = Math.max(0, (int)point.getY() - 1);
                             ny < Math.min(image.getHeight(), (int)point.getY() + 1 + 1);
                             ny++) {
                            for (int nx = Math.max(0, (int)point.getX() - 1);
                                 nx < Math.min(image.getWidth(), (int)point.getX() + 1 + 1);
                                 nx++) {
                                if ((visited.getRGB(nx, ny) == 0) && (colorMatch(new Color(image.getRGB(nx,ny)), targetColor))) { // if conditions match
                                    stack.push(new Point(nx, ny)); // add this point to the stack
                                }
                                visited.setRGB(nx, ny, 1); // mark point as visited
                            }
                        }

                    }
					// add this region to regions list if big enough
                    if (region.size() >= minRegion) {
                        regions.add(region);
                    }
                }
            }
        }
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the static threshold).
	 * for all 3 colors (R,G,B) return false if difference in values between c1 and c2 is greater than threshold
	 * return true otherwise, meaning colors are similar enough
	 */
	private static boolean colorMatch(Color c1, Color c2){

		if (Math.abs(c1.getRed()-c2.getRed()) > maxColorDiff){
			return false;
		}
		else if (Math.abs(c1.getGreen() - c2.getGreen())> maxColorDiff){
			return false;
		}
		else if (Math.abs(c1.getBlue() - c2.getBlue()) > maxColorDiff){
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> findLargestRegion() {
		ArrayList<Point> maxList = regions.get(0); // get first region and set that as max
		int maxSize = maxList.size(); // get size off first region
		// loop through regions and pull region
		for (ArrayList<Point> list: regions){
			// if any region is larger than the current max region, set that to be the max and it's size to be max size
			if (list.size() > maxSize){
				maxList = list;
				maxSize = list.size();
			}
		}
		return maxList; // return largest region as maxList
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a random uniform color, 
	 * so we can see where they are
	 */
	public void recolorRegions() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null),
                image.getColorModel().isAlphaPremultiplied(), null);

		// recolor by looping through regions and generating a random color for all the points in a region
		for (ArrayList<Point> region: regions){
			int c = (int) (Math.random() * 16777216); // generates random number between 0 and 16777216
            // set all points in a region equal to the random color generated using int c
			for (Point point: region){
				recoloredImage.setRGB((int)point.getX(), (int)point.getY(), c);
			}
		}
    }
}