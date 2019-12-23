package xyz.colintoft.cgraphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class Text extends Drawable {

	protected double anchorX, anchorY;
	protected Font font;
	protected Color color;
	protected String text;
	protected HorizontalAlign hAlign;
	protected VerticalAlign vAlign;
	
	public Text(double x, double y, String text, Font font, Color color, HorizontalAlign hAlign, VerticalAlign vAlign) {
		super(x, y, 0, 0);
		this.anchorX = x;
		this.anchorY = y;
		this.font = font;
		this.color = color;
		this.text = text;
		this.hAlign = hAlign;
		this.vAlign = vAlign;
	}
	
	public Text(double x, double y, String text, Font font, Color color) {
		this(x, y, text, font, color, HorizontalAlign.LEFT, VerticalAlign.BOTTOM);
	}

	public Text(double x, double y,  String text, int size, Color color) {
		this(x, y, text, new Font(Font.SANS_SERIF, Font.PLAIN, size), color, HorizontalAlign.LEFT, VerticalAlign.BOTTOM);
	}

	public Text(double x, double y, Color color, String text) {
		this(x, y, text, 12, color);
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String newText) {
		text = newText;
		generateImage();
	}
	
	@Override
	public void setX(double x) {
		anchorX = x;
		switch (hAlign) {
		case CENTER:
			this.x = anchorX - this.width * 0.5; break;
		case LEFT:
			this.x = anchorX; break;
		case RIGHT:
			this.x = anchorX - this.width; break;
		}
	}
	
	@Override
	public void setY(double y) {
		anchorY = y;
		switch (vAlign) {
		case CENTER:
			y = anchorY - this.height * 0.5; break;
		case TOP:
			y = anchorY; break;
		case BOTTOM:
			y = anchorY - this.height; break;
		}
	}
	
	protected void calculateDimensions() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); // Blank image to get font metrics
		this.width = pixelToParentWidthFraction(image.createGraphics().getFontMetrics(font).stringWidth(text));
		this.height = pixelToParentHeightFraction(image.createGraphics().getFontMetrics(font).getHeight());
	}
	
	protected void calculateCoordinates() {
		switch (hAlign) {
		case CENTER:
			x = anchorX - this.width * 0.5; break;
		case LEFT:
			x = anchorX; break;
		case RIGHT:
			x = anchorX - this.width; break;
		}
		
		switch (vAlign) {
		case CENTER:
			y = anchorY - this.height * 0.5; break;
		case TOP:
			y = anchorY; break;
		case BOTTOM:
			y = anchorY - this.height; break;
		}
	}
	
	@Override
	public void generateImage() {
		calculateDimensions();
		calculateCoordinates();
		super.generateImage();
	}
	
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(font);
		g2d.setColor(color);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.drawString(text, 0, g.getFontMetrics(font).getHeight());
	}
}