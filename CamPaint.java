package Lab1;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Template for PS-1, Dartmouth CS 10, Spring 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 *
 * @author updated by jackelder and nickwhalley for PS-1 CS10
 * 4/11/16
 */

public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'p': painting, 'r': regions, 'w': webcam
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder(); // initialize new RegionFinder object
		clearPainting(); // clear canvas
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// display normal webcam if mode is 'w'
        if (displayMode == 'w') {
            super.draw(g);
        }
		/**
		 * if display mode is 'r' and a target color has been specified, then find all of the regions with size above
		 * the threshold and recolor them the specified target color (blue in this case)
 		 */
        if (displayMode == 'r') {
			// must have a target color specified via mouse click
            if (targetColor != null) {
				// use findRegions method from RegionFinder to detect regions matching this color
                finder.findRegions(targetColor);
                ArrayList<ArrayList<Point>> regions = finder.getRegions();

				// loop through list of regions and for each region color each point in the region the specified color
                for (ArrayList<Point> region : regions) {
                    for (Point point : region) {
                        image.setRGB((int) point.getX(), (int) point.getY(), paintColor.getRGB());
                    }
                }
            }
            super.draw(g);
        }

		/**
		 * if display mode is 'p' and a target color has been specified, then set the largest region to be the
		 * "paintbrush" and create a blank image to draw on. For every point on the image that the "paintbrush" passes
		 * over, set this point to the target color, allowing a user to "paint" on the image
		 */

        if (displayMode == 'p') {
			// must have a target color specified by mousepress
            if (targetColor != null) {
                finder.findRegions(targetColor);

				// only paint if there exists a region of a specified color, ie a "paintbrush"
                if (finder.getRegions().size() != 0) {
                    ArrayList<Point> brush = finder.findLargestRegion(); // set paintbrush equal to the largest region
					// recolor every point on the painting that the brush passes over
                    for (Point point : brush) {
                        painting.setRGB((int) point.getX(), (int) point.getY(), paintColor.getRGB()); // recolor blue
                    }
                }
            }
            g.drawImage(painting, 0, 0, null); // draw "painting"
            }
        }




	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
        finder = new RegionFinder(image);
        } // initialize new regionfinder

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// set targetColor by grabbing RGB value from point of where mouse is pressed
        if (image != null) {
            targetColor = new Color(image.getRGB(x, y));
        }
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, regions, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear painting
			clearPainting();
		}
		else if (k == 'o') { // save the regions
			saveImage(finder.getRecoloredImage(), "pictures/regions.png", "png");
		}
		else if (k == 's') { // save the drawing
			saveImage(painting, "pictures/webdraw.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}