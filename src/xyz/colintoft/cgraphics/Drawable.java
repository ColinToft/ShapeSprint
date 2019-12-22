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
	
	/** Sets whether the image needs to be updated every frame. */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public void moveLeft(double delta) {
		this.x = this.x - delta;
	}

	public void moveRight(double delta) {
		this.x = this.x + delta;
	}

	public void moveUp(double delta) {
		this.y = this.y - delta;
	}

	public void moveDown(double delta) {
		this.y = this.y + delta;
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
	
	public Drawable(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Drawable(double x, double y, double size) {
		this(x, y, size, size);
	}
	
	/** Called after this Drawable is instantiated and added to a panel. */
	public void start() {}
	
	public void draw(Graphics g) {}
	
	public void update() {}
	
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
	
	public int pixelWidth() {
		return (int) Math.round(width * panel.getWidth());
	}
	
	public int pixelHeight() {
		return (int) Math.round(height * panel.getHeight());
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