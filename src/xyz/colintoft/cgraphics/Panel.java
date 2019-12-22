package xyz.colintoft.cgraphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.*;

public class Panel extends JPanel {

	protected ArrayList<Component> components;
	protected ArrayList<Drawable> drawables;
	
	private Panel parent;
	
	public Panel() {
		components = new ArrayList<Component>();
		drawables = new ArrayList<Drawable>();
		setBackground(Color.black);
		setLayout(null);
	}
	
	/** Called when this Panel is being initialized, and before it is made visible. Perform any necessary setup code, but JComponents should be added to the panel in the start() method (can't remember why?). */
	public void init() {}
	
	/** Called just after this Panel is made visible. This is where you should add the components to the panel. */
	public void start() {}
	
	/** Sets the parent of this panel. */
	public void setParent(Panel p) {
		parent = p;
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
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth() + xOffset, getHeight() + yOffset);
		
		super.paintChildren(g); // draws JComponenents

		for (Component c: components) {
		    if (c instanceof Panel) {
			    ((Panel) c).draw(g, xOffset, yOffset);
		    } else {
		        // c.paint(g);
		    }
		}
	
		for (Drawable d: drawables) {
			g.drawImage(d.getImage(), d.pixelX(xOffset), d.pixelY(yOffset), d.pixelWidth(), d.pixelHeight(), null);
		}
	}
	
	/** Called many times per second depending on the games current update FPS, to change it use the {@link Game#setUpdateFPS(double)} method.
	 * You should use it to update game logic (save the drawing for the {@link #draw(Graphics) method}). <br>
	 * Note: if overriding this method, super.update() needs to be called. <br>
	 * Note: This method is not called while the game is paused. 
	 * @param dt The amount of time (in seconds) since update was last called
	 */
	public void update(double dt) {
		for (Component c: components) {
			if (c instanceof Panel) {
			    ((Panel) c).update(dt);
		    }
		}
		
		for (Drawable d: drawables) {
			d.update(dt);
		}
	}
	
	/** Adds a Component to the Panel. */
	public Component add(Component comp) {
		super.add(comp);
		components.add(comp);
		if (comp instanceof Panel) {
			((Panel) comp).setParent(this);
			((Panel) comp).start();
		}
		return comp;
	}
	
	/** Adds a drawable to the JPanel. */
	public Drawable add(Drawable d) {
		d.setPanel(this);
		drawables.add(d);
		d.start();
		return d;
	}
	
	/**
	 * Adds a component to the scene. The x and y coordinates can range from 0 to 1,
	 * where 0 is the origin and 1 is the width or height of the screen, respectively.
	 */
	public Component add(Component comp, double x, double y, double width, double height) {
		int w = this.getWidth();
		int h = this.getHeight();
		comp.setBounds((int)(x * w), (int)(y * h), (int)(width * w), (int)(height * h));
		super.add(comp);
		components.add(comp);
		if (comp instanceof Panel) {
			((Panel) comp).setParent(this);
			((Panel) comp).start();
		}
		return comp;
	}
	
	/** This method will be automatically called whenever the game is paused. If overriding this method, make sure to call super.onPause() inside this method. */
	public void onPause() {
		for (Component c: components) {
			if (c instanceof Panel) {
			    ((Panel) c).onPause();
		    }
		}
		
		for (Drawable d: drawables) {
			d.onPause();
		}
	}
	
	/** This method will be automatically called whenever the game is resumed (unpaused). If overriding this method, make sure to call super.onResume() inside this method. */
	public void onResume() {
		for (Component c: components) {
			if (c instanceof Panel) {
			    ((Panel) c).onResume();
		    }
		}
		
		for (Drawable d: drawables) {
			d.onResume();
		}
	}
	
	/** This method will be automatically called whenever the window is rescaled. If overriding this method, make sure to call super.onRescale() inside this method. */
	public void onRescale() {
		// Set the panel of each drawable so they will regenerate their image
		for (Drawable d: drawables) {
			d.setPanel(this);
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
