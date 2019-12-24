package xyz.colintoft.cgraphics.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import xyz.colintoft.cgraphics.Game;

public class Panel extends Drawable {

	protected ArrayList<Drawable> drawables;
	
	private Panel parent = null;
	
	public Panel(double x, double y, double width, double height) {
		super(x, y, width, height);
		drawables = new ArrayList<Drawable>();
		setBackground(new Color(0, 0, 0, 0)); // Transparent background by default
	}
	
	/** Creates a new panel with the dimensions of its parent. */
	public Panel() {
		this(0, 0, 1, 1);
	}
	
	/** Called when this Panel is being initialized, and before it is made visible. Perform any necessary setup code, and add components using the {@link Panel#add(Drawable)} method. */
	public void init() {}
	
	/** Called just after this Panel is made visible. This is where you should add the components to the panel. */
	public void start() {}
	
	/** Sets the parent of this panel. */
	@Override
	public void setParentPanel(Panel p) {
		parentPanel = p;
		
		for (Drawable d: drawables) {
			d.setParentPanel(this);
		}
		
		super.setParentPanel(p);
		
	}

	
	public boolean fillsParent() {
		return x == 0 && y == 0 && width == 1 && height == 1;
	}
	
	/**
	 * Draws all child components and Drawables to the screen.
	 * @param g The graphics object that is used to draw graphics to the screen.
	 */
	public void draw(Graphics g) {
		draw(g, 0, 0);
	}
	
	/**
	 * Draws all child components and Drawables to the screen.
	 * @param g The graphics object that is used to draw graphics to the screen.
	 */
	public void draw(Graphics g, int xOffset, int yOffset) {
		g.setColor(backgroundColor);
		g.fillRect(0, 0, pixelWidth() + xOffset, pixelHeight() + yOffset);
		
	
		for (Drawable d: drawables) {
			if (d instanceof Panel) {
		    	if (((Panel) d).fillsParent()) {
		    		((Panel) d).draw(g, xOffset, yOffset);
		    	} else {
		    		g.drawImage(((Panel) d).getImage(), d.pixelX(xOffset), d.pixelY(yOffset), d.pixelWidth(), d.pixelHeight(), null);
		    	}
		    } else {
		    	g.drawImage(d.getImage(), d.pixelX(xOffset), d.pixelY(yOffset), d.pixelWidth(), d.pixelHeight(), null);
		    }
		}
	}
	
	public BufferedImage getImage() {
		BufferedImage i = new BufferedImage(pixelWidth() + 1, pixelHeight() + 1, BufferedImage.TYPE_INT_ARGB);
		draw(i.createGraphics());
		return i;
	}
	
	/** Called many times per second depending on the games current update FPS, to change it use the {@link Game#setUpdateFPS(double)} method.
	 * You should use it to update game logic (save the drawing for the {@link #draw(Graphics) method}). <br>
	 * Note: if overriding this method, super.update() needs to be called. <br>
	 * Note: This method is not called while the game is paused. 
	 * @param dt The amount of time (in seconds) since update was last called
	 */
	public void update(double dt) {
		for (Drawable d: drawables) {
			d.update(dt);
		}
	}
	
	/** Adds a drawable to the JPanel. */
	public Drawable add(Drawable d) {
		if (parentPanel != null) {
			d.setParentPanel(this);
		}
		drawables.add(d);
		d.start();
		return d;
	}
	
	/** This method will be automatically called whenever the game is paused. If overriding this method, make sure to call super.onPause() inside this method. */
	@Override
	public void onPause() {
		for (Drawable d: drawables) {
			d.onPause();
		}
	}
	
	/** This method will be automatically called whenever the game is resumed (unpaused). If overriding this method, make sure to call super.onResume() inside this method. */
	@Override
	public void onResume() {
		for (Drawable d: drawables) {
			d.onResume();
		}
	}
	
	/** This method will be automatically called whenever the window is rescaled. If overriding this method, make sure to call super.onRescale() inside this method. */
	public void onRescale() {
		// Set the panel of each drawable so they will regenerate their image
		for (Drawable d: drawables) {
			d.setParentPanel(this);
		}
	}
	
	/** Pauses the game. */
	public void pauseGame() {
		parent.pauseGame();
	}
	
	/** Resumes/unpauses the game. */
	public void resumeGame() {
		parent.resumeGame();
	}
	
	/** If the game is paused, unpause it, and vice versa. */
	public void togglePaused() {
		parent.togglePaused();
	}
	
	/** Returns true if the game is currently paused. */
	public boolean isPaused() {
		return parent.isPaused();
	}
}
