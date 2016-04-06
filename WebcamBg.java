package Lab1;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Fun with the webcam, built on JavaCV
 * Replaces background (as denoted by mouse press) with scenery
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, Winter 2014, rewritten for Webcam class
 */
public class WebcamBg extends Webcam {
	private static final int bgDiff=250;	// setup: threshold for considering a pixel to be background

	private BufferedImage background;		// the stored background grabbed from the webcam
	private BufferedImage scenery;			// the replacement background
	
	public WebcamBg(BufferedImage scenery) {
		this.scenery = scenery;
	}

	/**
	 * DrawingGUI method, here setting background as a copy of the current image.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) {
			background = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
			System.out.println("set bg");
		}
	}

	/**
	 * Webcam method, here subtracting background.
	 * Updates image so that pixels that look like background are replaced with scenery.
	 */
	@Override
	public void processImage() {
		if (background != null) {
			// Nested loop over every pixel
			for (int y = 0; y < Math.min(image.getHeight(), scenery.getHeight()); y++) {
				for (int x = 0; x < Math.min(image.getWidth(), scenery.getWidth()); x++) {
					// Euclidean distance squared between colors
					Color c1 = new Color(image.getRGB(x,y));
					Color c2 = new Color(background.getRGB(x,y));
					int d = (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed())
							+ (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen())
							+ (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());
					if (d < bgDiff) {
						// Close enough to background, so replace
						image.setRGB(x,y,scenery.getRGB(x,y));
					}
				}
			}
		}
	}

	public static void main(String[] args) { 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Read an image. For best effect, use something same size as webcam (accounting for scale)
				BufferedImage image = loadImage("pictures/baker-640-480.jpg");
				new WebcamBg(image);
			}
		});
	}
}