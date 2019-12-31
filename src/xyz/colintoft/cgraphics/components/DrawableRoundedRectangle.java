package xyz.colintoft.cgraphics.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class DrawableRoundedRectangle extends Drawable {

	private Color borderColor, fillColor;
	private double arcw, arch;
	private float borderWidth;
	
	public DrawableRoundedRectangle(double x, double y, double width, double height, double arcw, double arch, Color borderColor, float borderWidth, Color fillColor) {
		super(x, y, width, height);
		this.borderColor = borderColor;
		this.fillColor = fillColor;
		this.borderWidth = borderWidth;
		this.arcw = arcw;
		this.arch = arch;
		setDynamic(false);
	}
	
	public DrawableRoundedRectangle(double x, double y, double width, double height, double arcw, double arch, Color borderColor, Color fillColor) {
		this(x, y, width, height, arcw, arch, borderColor, 1.0f, fillColor);
	}

	public DrawableRoundedRectangle(double x, double y, double width, double height, double arcw, double arch, Color color) {
		this(x, y, width, height, arcw, arch, null, color);
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, pixelWidth(), pixelHeight(), (int)(arcw * parentPanel.pixelWidth()), (int)(arch * parentPanel.pixelHeight()));
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setPaint(fillColor);
		g2d.fill(rect);
		if (borderColor != null) {
			g2d.setPaint(borderColor);
			g2d.setStroke(new BasicStroke(borderWidth));
			g2d.draw(rect);
		}
	}
}