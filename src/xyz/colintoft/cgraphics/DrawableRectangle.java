package xyz.colintoft.cgraphics;

import java.awt.Color;
import java.awt.Graphics;
import xyz.colintoft.cgraphics.Drawable;

public class DrawableRectangle extends Drawable {

	private Color borderColor, fillColor;
	
	public DrawableRectangle(double x, double y, double width, double height, Color borderColor, Color fillColor) {
		super(x, y, width, height);
		this.borderColor = borderColor;
		this.fillColor = fillColor;
		setDynamic(false);
	}

	public DrawableRectangle(double x, double y, double width, double height, Color color) {
		this(x, y, width, height, color, color);
	}
	
	@Override
	public void draw(Graphics g) {
		g.setColor(fillColor);
		g.fillRect(0, 0, pixelWidth(), pixelHeight());
		g.setColor(borderColor);
		g.drawRect(0, 0, pixelWidth(), pixelHeight());
	}

}
