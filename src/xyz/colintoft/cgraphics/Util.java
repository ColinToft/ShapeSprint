package xyz.colintoft.cgraphics;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;


public class Util {
	
	public static Font loadFontFromFile(Class resourceGrabber, String name, int size) {
		InputStream is = resourceGrabber.getResourceAsStream("/fonts/" + name + ".ttf");
		System.out.println(is.toString());
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, is);
			return f;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to load font.");
			return null;
		}
	}
	
}
