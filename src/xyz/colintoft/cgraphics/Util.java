package xyz.colintoft.cgraphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

   
public class Util {
	
	// For creating BufferedImages
	private static GraphicsEnvironment env;
    private static GraphicsDevice device;
    private static GraphicsConfiguration config;
	
	public static Font loadFontFromFile(Class resourceGrabber, String filename, float size) {
		InputStream is = resourceGrabber.getResourceAsStream("/fonts/" + filename);
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(f);
			return f.deriveFont(size);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to load font.");
			return null;
		}
	}
	
	public static BufferedImage loadImageFromFile(Class resourceGrabber, String filename) {
		InputStream is = resourceGrabber.getResourceAsStream("/images/" + filename);
		try {
			return ImageIO.read(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Clip getAudioClip(Class resourceGrabber, String filename) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resourceGrabber.getResource("/audio/" + filename));
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			return clip;
		} catch(Exception e) {
			System.out.println("Unable to load audio file: " + filename);
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage getEmptyImage(int width, int height) {
		return getEmptyImage(width, height, true);
	} 
	
	public static BufferedImage getEmptyImage(int width, int height, boolean fixSides) {
		if (config == null) {
			env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    device = env.getDefaultScreenDevice();
		    config = device.getDefaultConfiguration();
		}
		if (fixSides) {
			return config.createCompatibleImage(width + 1, height + 1, Transparency.TRANSLUCENT);
		} else {
			return config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		}
	}
	
	/** Finds and returns the smallest double in a given array. */
	public static double min(double[] array) {
		double smallest = array[0];
		for (double d: array) {
			if (d < smallest) {
				smallest = d;
			}
		}
		return smallest;
	}
	
	/** Finds and returns the largest double in a given array. */
	public static double max(double[] array) {
		double largest = array[0];
		for (double d: array) {
			if (d > largest) {
				largest = d;
			}
		}
		return largest;
	}
	
	public static String toPercentageString(double value) {
		return Math.round(value * 100) + "%";
	}
	
	public static double sigmoid(double x) {
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	  }

	public static BufferedImage scaleImage(BufferedImage image, int width, int height) {
		return scaleImage(image, width, height, true);
	}
	
	public static BufferedImage scaleImage(BufferedImage image, int width, int height, boolean fixSides) {
		BufferedImage newImage = getEmptyImage(width, height, fixSides);
		Graphics g = newImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		return newImage;
	}
}
