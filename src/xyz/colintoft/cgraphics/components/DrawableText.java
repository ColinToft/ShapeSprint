package xyz.colintoft.cgraphics.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import xyz.colintoft.cgraphics.HorizontalAlign;
import xyz.colintoft.cgraphics.Util;
import xyz.colintoft.cgraphics.VerticalAlign;

public class DrawableText extends Drawable {

	protected double anchorX, anchorY;
	protected double minWidth = 0, minHeight = 0;
	protected double maxWidth = -1, maxHeight = -1;
	protected Font font, baseFont;
	protected Color color;
	protected String text;
	protected HorizontalAlign hAlign;
	protected VerticalAlign vAlign;
	
	public DrawableText(double x, double y, String text, Font font, Color color, HorizontalAlign hAlign, VerticalAlign vAlign) {
		super(x, y, 0, 0);
		this.anchorX = x;
		this.anchorY = y;
		this.font = font;
		this.baseFont = font;
		this.color = color;
		this.text = text;
		this.hAlign = hAlign;
		this.vAlign = vAlign;
		setDynamic(false);
	}
	
	public DrawableText(double x, double y, String text, Font font, Color color) {
		this(x, y, text, font, color, HorizontalAlign.LEFT, VerticalAlign.BOTTOM);
	}

	public DrawableText(double x, double y,  String text, int size, Color color) {
		this(x, y, text, new Font(Font.SANS_SERIF, Font.PLAIN, size), color, HorizontalAlign.LEFT, VerticalAlign.BOTTOM);
	}

	public DrawableText(double x, double y, Color color, String text) {
		this(x, y, text, 12, color);
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String newText) {
		if (!text.equals(newText)) {
			text = newText;
			generateImage();
		}
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
	
	@Override
	public void moveLeft(double delta) {
		x -= delta;
		anchorX -= delta;
	}
	
	@Override
	public void moveRight(double delta) {
		x += delta;
		anchorX += delta;
	}
	
	@Override
	public void moveUp(double delta) {
		y -= delta;
		anchorY -= delta;
	}
	
	@Override
	public void moveDown(double delta) {
		y += delta;
		anchorY += delta;
	}
	
	// 8
	private int getWidthLowBound() {
		if (minWidth <= 0) {
			return 0;
		}
		
		BufferedImage image = Util.getEmptyImage(1, 1); // Blank image to get font metrics
		Graphics2D ig = image.createGraphics();

		int lowBound = 0;
		int highBound = parentPanel.pixelHeight() + 5;
		int fontSize = baseFont.getSize();
		
		while (lowBound != highBound) {
			fontSize = (lowBound + highBound) / 2;
			font = font.deriveFont((float) fontSize);
			width = pixelToParentWidthFraction(ig.getFontMetrics(font).stringWidth(text));
			if (width < minWidth) {
				lowBound = fontSize + 1;
			} else {
				highBound = fontSize;
			}
		}
		return lowBound;
	}
	
	// 8
	private int getWidthHighBound() {
		if (maxWidth < 0) {
			return Integer.MAX_VALUE;
		}
		
		BufferedImage image = Util.getEmptyImage(1, 1); // Blank image to get font metrics
		Graphics2D ig = image.createGraphics();

		int lowBound = 0;
		int highBound = parentPanel.pixelHeight() + 5;
		int fontSize = baseFont.getSize();
		
		while (lowBound != highBound) {
			fontSize = (int) Math.ceil((lowBound + highBound) / 2.0);
			font = font.deriveFont((float) fontSize);
			width = pixelToParentWidthFraction(ig.getFontMetrics(font).stringWidth(text));
			if (width > maxWidth) {
				highBound = fontSize - 1;
			} else {
				lowBound = fontSize;
			}
		}
		return highBound;
	}
	
	// 8
	private int getHeightLowBound() {
		if (minHeight <= 0) {
			return 0;
		}
		
		BufferedImage image = Util.getEmptyImage(1, 1); // Blank image to get font metrics
		Graphics2D ig = image.createGraphics();

		int lowBound = 0;
		int highBound = parentPanel.pixelHeight() + 5;
		int fontSize = baseFont.getSize();
		
		while (lowBound != highBound) {
			fontSize = (lowBound + highBound) / 2;
			font = font.deriveFont((float) fontSize);
			height = pixelToParentHeightFraction(ig.getFontMetrics(font).getHeight());
			if (height < minHeight) {
				lowBound = fontSize + 1;
			} else {
				highBound = fontSize;
			}
		}
		return lowBound;
	}
	
	// 8
	private int getHeightHighBound() {
		if (maxHeight < 0) {
			return Integer.MAX_VALUE;
		}
		
		BufferedImage image = Util.getEmptyImage(1, 1); // Blank image to get font metrics
		Graphics2D ig = image.createGraphics();

		int lowBound = 0;
		int highBound = parentPanel.pixelHeight() + 5;
		int fontSize = baseFont.getSize();
		
		while (lowBound != highBound) {
			fontSize = (int) Math.ceil((lowBound + highBound) / 2.0);
			font = font.deriveFont((float) fontSize);
			height = pixelToParentHeightFraction(ig.getFontMetrics(font).getHeight());
			if (height > maxHeight) {
				highBound = fontSize - 1;
			} else {
				lowBound = fontSize;
			}
		}
		return highBound;
	}
	
	protected void calculateDimensions() {
		BufferedImage image = Util.getEmptyImage(1, 1); // Blank image to get font metrics
		Graphics2D ig = image.createGraphics();
		
		this.width = pixelToParentWidthFraction(ig.getFontMetrics(baseFont).stringWidth(text));
		this.height = pixelToParentHeightFraction(ig.getFontMetrics(baseFont).getHeight());
		
		int lowBound = Math.max(getWidthLowBound(), getHeightLowBound());
		int highBound = Math.min(getWidthHighBound(), getHeightHighBound());
		
		int fontSize;
		try {
			fontSize = Util.constrain(baseFont.getSize(), lowBound, highBound);
		} catch (IllegalArgumentException e) {
			System.out.println("Illegal width and height constraints on DrawableText object.");
			fontSize = baseFont.getSize();
		}
		
		font = font.deriveFont((float) fontSize);
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
	
	/** Sets the maximum width of this text. */
	public void setMaxWidth(double maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	/** Sets the maximum height of this text. */
	public void setMaxHeight(double maxHeight) {
		this.maxHeight = maxHeight;
	}
}