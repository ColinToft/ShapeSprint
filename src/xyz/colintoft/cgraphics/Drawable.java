package xyz.colintoft.cgraphics;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import javax.imageio.ImageIO;

public class Drawable implements KeyListener, MouseListener {

	protected BufferedImage currentImage;
	
	protected double x, y, width, height;
	
	protected Panel panel = null;
	
	protected Color backgroundColor = new Color(0, 0, 0, 0); // Transparent background by default
	
	/** Whether the image needs to be updated every frame. */
	private boolean dynamic = true;
	
	public Drawable(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Drawable(double x, double y, double size) {
		this(x, y, size, size);
	}
	
	/** Sets whether the image needs to be updated every frame. */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getCenterX() {
		return x + width * 0.5;
	}
	
	public double getCenterY() {
		return y + height * 0.5;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public void moveLeft(double delta) {
		x -= delta;
	}

	public void moveRight(double delta) {
		x += delta;
	}

	public void moveUp(double delta) {
		y -= delta;
	}

	public void moveDown(double delta) {
		y += delta;
	}
	
	/**
	 * Sets this drawable's current image to the specified filepath.
	 * Note that this sets this object's "dynamic" property to false, meaning the {@link Drawable#draw(Graphics)} will not be called every frame.
	 * @param name The filepath of the image
	 * @see Drawable#setImage(BufferedImage)
	 * @see Drawable#setDynamic(boolean)
	 */
	public void setImage(String name) {
		InputStream is = getClass().getResourceAsStream("/images/" + name + ".jpg");
		try {
			setImage(ImageIO.read(is));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets this drawable's image. Note that this sets this object's "dynamic" property to false, meaning the {@link Drawable#draw(Graphics)} will not be called every frame.
	 * @param image
	 * @see Drawable#setDynamic(boolean)
	 */
	public void setImage(BufferedImage image) {
		currentImage = image;
		setDynamic(false);
	}
	
	public Image getImage() {
		if (panel == null) {
			System.out.println("No panel defined, cannot create image.");
			return null;
		}
		
		if (dynamic) {
			Graphics2D g = currentImage.createGraphics();
			g.setColor(backgroundColor);
			g.fillRect(0, 0, currentImage.getWidth(), currentImage.getHeight());
			draw(g);
		}
		
		return currentImage;
	}
	
	protected void togglePaused() {
		panel.togglePaused();
	}
	
	protected void pauseGame() {
		panel.pauseGame();
	}
	
	protected void resumeGame() {
		panel.resumeGame();
	}
	
	protected boolean isPaused() {
		return panel.isPaused();
	}
	
	/** Called after this Drawable is instantiated and added to a panel. */
	public void start() {}
	
	public void draw(Graphics g) {}
	
	/**
	 * Updates this drawable. By default, it is empty, but you can override this method to add logic to a Drawable that is updated many times per second.
	 * @param dt The amount of time (in seconds) since this method was last called.
	 * @see Game#setFPS(double)
	 * @see Game#setUpdateFPS(double)
	 */
	public void update(double dt) {}
	
	public void setPanel(Panel p) {
		panel = p;
		generateImage();
	}
	
	/**
	 * Generates the image for this Drawable and stores it in the {@link Drawable#currentImage} variable.
	 * This method is called when the Drawable is added to a Panel and when it is resized.
	 * By default, it calls the {@link Drawable#draw(Graphics)} method to create the image.
	 */
	protected void generateImage() {
		currentImage = new BufferedImage(pixelWidth() + 1, pixelHeight() + 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = currentImage.createGraphics();
		g.setColor(backgroundColor);
		g.fillRect(0, 0, currentImage.getWidth(), currentImage.getHeight());
		draw(g);
	}
	
	public int pixelX(int xOffset) {
		return (int) Math.round(x * panel.getWidth()) + xOffset;
	}
	
	public int pixelY(int yOffset) {
		return (int) Math.round(y * panel.getHeight()) + yOffset;
	}
	
	/**
	 * @return The width of this Drawable in pixels.
	 */
	public int pixelWidth() {
		return (int) Math.round(width * panel.getWidth());
	}
	
	/**
	 * @return The height of this Drawable in pixels.
	 */
	public int pixelHeight() {
		return (int) Math.round(height * panel.getHeight());
	}
	
	/**
	 * Converts a horizontal value from screen coordinates (pixels) to a fraction of the panel width (0 to 1)
	 * @param pixelX The original x coordinate, in pixels
	 * @return The same coordinate, but as a fraction of the panel width (0 is on the very left of the panel, 1 is on the very right)
	 */

	public double pixelToParentWidthFraction(int pixelX) {
		return (double) pixelX / panel.getWidth();
	}
	
	/**
	 * Converts a vertical value from screen coordinates (pixels) to a fraction of the parent height (0 to 1)
	 * @param pixelY The original y coordinate, in pixels
	 * @return The same coordinate, but as a fraction of the panel height (0 is at the very top of the panel, 1 is at the very bottom)
	 */
	public double pixelToParentHeightFraction(int pixelY) {
		return (double) pixelY / panel.getHeight();
	}
	
	protected void setBackground(Color c) {
		backgroundColor = c;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	public void onPause() {}
	
	public void onResume() {}
}