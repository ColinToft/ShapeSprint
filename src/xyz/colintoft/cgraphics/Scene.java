package xyz.colintoft.cgraphics;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * A Scene represents one scene of the game, for example a menu, or one level of a game.
 * A Scene takes up the whole window, and can be filled with Panels, JComponents, or Drawables to have them be shown on the screen.
 * @author Colin Toft
 */
public class Scene extends Panel implements KeyListener, MouseListener, ComponentListener {
	
	protected Game game;
	
	/** Used to rescale components when the window is resized. */
	protected int oldWidth, oldHeight;
	
	public Scene() {
		super();
		addComponentListener(this);
	}
	
	/**
	 * Sets the game that this Scene is being displayed to.
	 * @author Colin Toft
	 */
	public void setGame(Game g) {
		game = g;
		oldWidth = g.getWidth();
		oldHeight = g.getHeight();
		game.addKeyListener(this);
		game.addMouseListener(this);
	}

    /**
	 * Adds a drawble to the scene.
	 * @author Colin Toft
	 */
	@Override
	public Drawable add(Drawable d) {
		game.addMouseListener(d);
		game.addKeyListener(d);
		return super.add(d);
	}
	
	/**
	 * Adds a JComponent to the scene.
	 * @author Colin Toft
	 */
	@Override
	public Component add(Component comp) {
		comp = super.add(comp);
		comp.setLocation(comp.getX() + game.getInsets().left, comp.getY() + game.getInsets().top);
		return comp;
	}
	
	/**
	 * Adds a JComponent to the scene at the specified location.
	 * @author Colin Toft
	 */
	@Override
	public Component add(Component comp, double x, double y, double width, double height) {
		comp = super.add(comp, x, y, width, height);
		comp.setLocation(comp.getX() + game.getInsets().left, comp.getY() + game.getInsets().top);
		System.out.println(comp.getLocation());
		return comp;
	}
	
	/**
	 * Pauses the game.
	 * @author Colin Toft
	 */
	@Override
	public void pauseGame() {
		game.pauseGame();
	}
	
	/**
	 * Resumes the game.
	 * @author Colin Toft
	 */
	@Override
	public void resumeGame() {
		game.resumeGame();
	}
	
	/**
	 * Switches the game's state from paused to unpaused, or vice versa.
	 * @author Colin Toft
	 */
	@Override
	public void togglePaused() {
		game.togglePaused();
	}
	
	/**
	 * Pauses the game.
	 * @author Colin Toft
	 */
	@Override
	public boolean isPaused() {
		return game.isPaused();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		for (Component c: components) {
			// rescaleComponent(c);
		}
		
		for (Drawable d: drawables) {
			d.setPanel(this);
		}
		
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {
		pauseGame();
		
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
}
