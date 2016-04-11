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
		finder = new RegionFinder();
		clearPainting();
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
        if (displayMode == 'w') {
            super.draw(g);
        }
        if (displayMode == 'r') {
            if (targetColor != null) {
                finder.findRegions(targetColor);
                ArrayList<ArrayList<Point>> regions = finder.getRegions();
                for (ArrayList<Point> region : regions) {
                    for (Point point : region) {
                        image.setRGB((int) point.getX(), (int) point.getY(), paintColor.getRGB());

                    }
                }
            }
            super.draw(g);
        }
        if (displayMode == 'p') {
            if (targetColor != null) {
                finder.findRegions(targetColor);
                if (finder.getRegions().size() != 0) {
                    ArrayList<Point> brush = finder.findLargestRegion();
                    for (Point point : brush) {
                        painting.setRGB((int) point.getX(), (int) point.getY(), paintColor.getRGB());
                    }
                }
            }
            g.drawImage(painting, 0, 0, null);
            }
        }




	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
        finder = new RegionFinder(image);
        }

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
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
		else if (k == 'c') { // clear
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