package xyz.colintoft.cgraphics.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class DrawableProgressBar extends Drawable {
	private Color borderColor, completeColor, incompleteColor;
	private double arcw, arch;
	private float borderWidth;
	private double value;
	
	public DrawableProgressBar(double x, double y, double width, double height, double arcw, double arch, Color borderColor, float borderWidth, Color completeColor, Color incompleteColor) {
		super(x, y, width, height);
		this.borderColor = borderColor;
		this.completeColor = completeColor;
		this.incompleteColor = incompleteColor;
		this.borderWidth = borderWidth;
		this.arcw = arcw;
		this.arch = arch;
		setDynamic(false);
	}
	
	public DrawableProgressBar(double x, double y, double width, double height, double arcw, double arch, Color borderColor, Color completeColor, Color incompleteColor) {
		this(x, y, width, height, arcw, arch, borderColor, 1.0f, completeColor, incompleteColor);
	}

	public DrawableProgressBar(double x, double y, double width, double height, double arcw, double arch, Color color, Color incompleteColor) {
		this(x, y, width, height, arcw, arch, null, color, incompleteColor);
	}
	
	/** Sets the value of this progress bar, from 0 (empty) to 1 (filled). */
	public void setValue(double value) {
		this.value = value;
		if (parentPanel != null) {
			generateImage();
		}
	}
	
	public double getValue() {
		return value;
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		RoundRectangle2D outerRect = new RoundRectangle2D.Double(0, 0, pixelWidth(), pixelHeight(), (int)(arcw * parentPanel.pixelWidth()), (int)(arch * parentPanel.pixelHeight()));
		Rectangle2D innerRect = new Rectangle2D.Double(0, 0, pixelWidth() * value, pixelHeight());
		Area outerArea = new Area(outerRect);
		
		Area innerArea = (Area) outerArea.clone();
		innerArea.intersect(new Area(innerRect));
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setPaint(incompleteColor);
		g2d.fill(outerArea);
		
		g2d.setPaint(completeColor);
		g2d.fill(innerArea);
		
		if (borderColor != null) {
			g2d.setPaint(borderColor);
			g2d.setStroke(new BasicStroke(borderWidth));
			g2d.draw(outerArea);
		}
	}
}
