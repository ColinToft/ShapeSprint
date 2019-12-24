package xyz.colintoft.cgraphics.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import xyz.colintoft.cgraphics.components.Drawable;

public class DrawableEllipse extends Drawable {

	private Color borderColor, fillColor;
	
	public DrawableEllipse(double x, double y, double width, double height, Color borderColor, Color fillColor) {
		super(x, y, width, height);
		this.borderColor = borderColor;
		this.fillColor = fillColor;
		setDynamic(false);
	}

	public DrawableEllipse(double x, double y, double width, double height, Color fillColor) {
		this(x, y, width, height, fillColor, fillColor);
	}
	
	@Override
	public void draw(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(fillColor);
		g.fillOval(0, 0, pixelWidth(), pixelHeight());
		g.setColor(borderColor);
		g.drawOval(0, 0, pixelWidth(), pixelHeight());
	}
	
	

}
