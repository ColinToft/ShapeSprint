package xyz.colintoft.cgraphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

   
public class Util {
	
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
	
	public static double min(double[] array) {
		double smallest = array[0];
		for (double d: array) {
			if (d < smallest) {
				smallest = d;
			}
		}
		return smallest;
	}
	
	public static double max(double[] array) {
		double largest = array[0];
		for (double d: array) {
			if (d > largest) {
				largest = d;
			}
		}
		return largest;
	}
}
