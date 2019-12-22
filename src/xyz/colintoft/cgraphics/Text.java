package xyz.colintoft.cgraphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class Text extends Drawable {

	public Text(double x, double y, Color color, String text, Font font) {
		super(x, y, 0, 0);

		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); // To calculate text height and width
		this.width = image.getGraphics().getFontMetrics(font).stringWidth(text);
		this.height = image.getGraphics().getFontMetrics(font).getHeight();
		
		image = new BufferedImage(pixelWidth() + 1, pixelHeight() + 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setFont(font);
		g2d.setColor(color);
		g2d.drawString(text, 0, image.getGraphics().getFontMetrics(font).getHeight());
		this.setImage(image);
	}

	public Text(double x, double y, Color color, String text, int size) {
		this(x, y, color, text, new Font(Font.SANS_SERIF, Font.PLAIN, size));
	}

	public Text(double x, double y, Color color, String text) {
		this(x, y, color, text, 12);
	}



}