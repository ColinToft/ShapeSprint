package xyz.colintoft.cgraphics.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import xyz.colintoft.cgraphics.HorizontalAlign;
import xyz.colintoft.cgraphics.VerticalAlign;

public class DrawableOutlinedText extends DrawableText {
	
	protected Color outlineColor;
	protected float outlineWidth;
	
	public DrawableOutlinedText(double x, double y, String text, Font font, Color color, Color outlineColor, float outlineWidth, HorizontalAlign hAlign, VerticalAlign vAlign) {
		super(x, y, text, font, color, hAlign, vAlign);
		this.outlineColor = outlineColor;
		this.outlineWidth = outlineWidth;
	}
	
	public DrawableOutlinedText(double x, double y, String text, Font font, Color color, Color outlineColor, HorizontalAlign hAlign, VerticalAlign vAlign) {
		this(x, y, text, font, color, outlineColor, 3f, hAlign, vAlign);
	}
	
	@Override
	protected void calculateDimensions() {
		super.calculateDimensions();
		width += pixelToParentWidthFraction((int)Math.ceil(outlineWidth) * 2);
		height += pixelToParentHeightFraction((int)Math.ceil(outlineWidth) * 2);
	}
	
	@Override
	protected void calculateCoordinates() {
		super.calculateCoordinates();
		x -= pixelToParentWidthFraction((int)Math.ceil(outlineWidth));
		y -= pixelToParentWidthFraction((int)Math.ceil(outlineWidth));
	}

	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout textTl = new TextLayout(text, font, frc);
		AffineTransform transform = new AffineTransform();
		Shape outline = textTl.getOutline(null);
		java.awt.Rectangle outlineBounds = outline.getBounds();
		
		transform = g2d.getTransform();
		transform.translate(pixelWidth() / 2 - (outlineBounds.width / 2), pixelHeight() / 2 + (outlineBounds.height / 2));
		g2d.transform(transform);
		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(outlineWidth));
		g2d.fill(outline);
		g2d.setColor(outlineColor);
		g2d.draw(outline);
	}
}
