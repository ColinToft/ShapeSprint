package xyz.colintoft.cgraphics.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import xyz.colintoft.cgraphics.Game;
import xyz.colintoft.cgraphics.Util;

public class Panel extends Drawable {

	protected ArrayList<Drawable> drawables;
		
	public Panel(double x, double y, double width, double height) {
		super(x, y, width, height);
		drawables = new ArrayList<Drawable>();
		setBackground(new Color(0, 0, 0, 0)); // Transparent background by default
	}
	
	/** Creates a new Panel with the dimensions of its parentPanel. */
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
		draw(g, 0, 0, 0, 0);
	}
	
	public void draw(Graphics g, Insets insets) {
		draw(g, insets.left, insets.top, insets.right, insets.bottom);
	}
	
	/**
	 * Draws all child components and Drawables to the screen.
	 * @param g The graphics object that is used to draw graphics to the screen.
	 */
	public void draw(Graphics g, int leftInset, int topInset, int rightInset, int bottomInset) {
		
		if (backgroundColor.getAlpha() > 0) {
			g.setColor(backgroundColor);
			g.fillRect(leftInset, topInset, pixelWidth(), pixelHeight());
		}
	
		for (Drawable d: drawables) {
			if (!d.visible) {
				continue;
			}
			if (d instanceof Panel) {
		    	if (((Panel) d).fillsParent()) {
		    		((Panel) d).draw(g);
		    	} else {
		    		g.drawImage(((Panel) d).getImage(), d.pixelX(leftInset), d.pixelY(topInset), d.pixelWidth(), d.pixelHeight(), null);
		    	}
		    } else {
		    	g.drawImage(d.getImage(), d.pixelX(leftInset), d.pixelY(topInset), d.pixelWidth(), d.pixelHeight(), null);
		    }
		}
	}
	
	public BufferedImage getImage() {
		BufferedImage i = Util.getEmptyImage(pixelWidth(), pixelHeight());
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
		return d;
	}
	
	public void dispose() {
		for (Drawable d: drawables) {
			d.dispose();
		}
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
		parentPanel.pauseGame();
	}
	
	/** Resumes/unpauses the game. */
	public void resumeGame() {
		parentPanel.resumeGame();
	}
	
	/** If the game is paused, unpause it, and vice versa. */
	public void togglePaused() {
		parentPanel.togglePaused();
	}
	
	/** Returns true if the game is currently paused. */
	public boolean isPaused() {
		return parentPanel.isPaused();
	}
	
	@Override
	public void onMousePressed(double x, double y, int button) {
		if (visible) {
			for (Drawable d: drawables) {
				if (d.isPointInFrame(x, y)) {
					double objectX = (x - d.x) / d.width;
					double objectY = (y - d.y) / d.height;
					if (d.visible) {
						d.onMousePressed(objectX, objectY, button);
					}
				}
			}
		}
	}
	
	@Override
	public void onMouseReleased(double x, double y, int button) {
		if (visible) {
			for (Drawable d: drawables) {
				if (d.isPointInFrame(x, y)) {
					double objectX = (x - d.x) / d.width;
					double objectY = (y - d.y) / d.height;
					if (d.visible) {
						d.onMouseReleased(objectX, objectY, button);
					}
				}
			}
		}
	}
}
