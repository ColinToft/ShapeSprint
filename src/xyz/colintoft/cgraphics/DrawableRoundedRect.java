package xyz.colintoft.cgraphics;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class DrawableRoundedRect extends Drawable {

	private Color borderColor, fillColor;
	
	public DrawableRoundedRect(double x, double y, double width, double height, Color borderColor, Color fillColor) {
		super(x, y, width, height);
		this.borderColor = borderColor;
		this.fillColor = fillColor;
		setDynamic(false);
	}

	public DrawableRoundedRect(double x, double y, double width, double height, Color color) {
		this(x, y, width, height, null, color);
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, pixelWidth(), pixelHeight(), 50, 50);
		
		g2d.setPaint(fillColor);
		g2d.fill(rect);
		if (borderColor != null) {
			g2d.setPaint(borderColor);
			g2d.setStroke(new BasicStroke(2.0f));
			g2d.draw(rect);
		}
	}
}