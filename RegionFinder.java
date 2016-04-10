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
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();         // a region is a list of points
															// so the identified regions are in a list of lists of points

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

	public ArrayList<ArrayList<Point>> getRegions() {
		return regions;
	}

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

                    // if or whille???
                    while (!(stack.isEmpty())){
                        Point point = stack.pop();
                        region.add(point); // push point off stack add to region

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
                    if (region.size() >= minRegion) {
                        regions.add(region); // add this region to regions list if big enough
                    }

                }
            }
        }
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the static threshold).
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
		ArrayList<Point> maxList = regions.get(0);
		int maxSize = maxList.size();
		for (ArrayList<Point> list: regions){
			if (list.size() > maxSize){
				maxList = list;
				maxSize = list.size();
			}
		}
		return maxList;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a random uniform color, 
	 * so we can see where they are
	 */
	public void recolorRegions() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions
		for (ArrayList<Point> region: regions){
			int c = (int) (Math.random() * 16777216); // generates random number between 0 and 16777216
			for (Point point: region){
				recoloredImage.setRGB((int)point.getX(), (int)point.getY(), c);
			}
		}
    }
}