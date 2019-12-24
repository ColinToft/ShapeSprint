package xyz.colintoft.cgraphics.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import xyz.colintoft.cgraphics.Util;

public class DrawableShape extends Drawable {
	
	private Color borderColor, fillColor;
	private double xCoords[], yCoords[];
	private float borderWidth;
	
	public DrawableShape(double[] xCoords, double[] yCoords, Color borderColor, float borderWidth, Color fillColor) {
		super(Util.min(xCoords), Util.min(yCoords), Util.max(xCoords) - Util.min(xCoords), Util.max(yCoords) - Util.min(yCoords));
		this.borderColor = borderColor;
		this.fillColor = fillColor;
		this.xCoords = xCoords;
		this.yCoords = yCoords;
		this.borderWidth = borderWidth;
		setDynamic(false);
	}
	
	public DrawableShape(double[] xCoords, double[] yCoords, Color borderColor, Color fillColor) {
		this(xCoords, yCoords, borderColor, 1f, fillColor);
	}

	public DrawableShape(double[] xCoords, double[] yCoords, Color color) {
		this(xCoords, yCoords, null, color);
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(fillColor);

		Shape s = getShape();
		g2d.fill(s);
		
		if (borderColor != null) {
			g2d.setColor(borderColor);
			g2d.setStroke(new BasicStroke(borderWidth));
			g2d.draw(s);
		}
	}
	
	/** Creates a DrawableShape object in the form of a triangle. */
	public static DrawableShape triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color borderColor, Color fillColor) {
		double[] xCoords = {x1, x2, x3};
		double[] yCoords = {y1, y2, y3};
		
		return new DrawableShape(xCoords, yCoords, borderColor, fillColor);
	}
	
	/** Creates a DrawableShape object in the form of a triangle. */
	public static DrawableShape triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color borderColor, float borderWidth, Color fillColor) {
		double[] xCoords = {x1, x2, x3};
		double[] yCoords = {y1, y2, y3};
		
		return new DrawableShape(xCoords, yCoords, borderColor, borderWidth, fillColor);
	}
	
	/** Creates a Shape object from a list of coordinates (the vertices of the shape). */
	private Shape getShape() {
		GeneralPath shape = new GeneralPath();
		shape.moveTo((xCoords[0] - x) * parentPanel.pixelWidth(), (yCoords[0] - x) * parentPanel.pixelHeight());
		System.out.println(shape.getCurrentPoint());
		for (int i = 1; i < xCoords.length; i++) {
			shape.lineTo((xCoords[i] - x) * parentPanel.pixelWidth(), (yCoords[i] - y) * parentPanel.pixelHeight());
			System.out.println(shape.getCurrentPoint());
		}
		shape.closePath();
		System.out.println(shape.contains(new Point2D.Double(5, 5)));
		System.out.println(shape.getBounds());
		return shape;
	}
}
