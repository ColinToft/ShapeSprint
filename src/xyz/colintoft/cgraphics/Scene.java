package xyz.colintoft.cgraphics;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import xyz.colintoft.cgraphics.components.Drawable;
import xyz.colintoft.cgraphics.components.Panel;

/**
 * A Scene represents one scene of the game, for example a menu, or one level of a game.
 * A Scene takes up the whole window, and can be filled with Panels, JComponents, or Drawables to have them be shown on the screen.
 * @author Colin Toft
 */
public class Scene extends Panel implements KeyListener, MouseListener, ComponentListener {
	
	protected Game game;
	
	protected int pixelWidth, pixelHeight;
	
	public Scene() {
		super();
		setBackground(Color.BLACK); // Black background by default
	}
	
	/**
	 * Sets the game that this Scene is being displayed to.
	 * @author Colin Toft
	 */
	public void setGame(Game g) {
		game = g;
		game.addKeyListener(this);
		game.addMouseListener(this);
		setDimensions(g.getWidth(), g.getHeight());
	}

	@Override
	public boolean hasParentPanel() {
		return game != null;
	}
	
    /**
	 * Adds a Drawable to the scene.
	 * @author Colin Toft
	 */
	@Override
	public Drawable add(Drawable d) {
		game.addMouseListener(d);
		game.addKeyListener(d);
		if (game != null) {
			d.setParentPanel(this);
		}
		return super.add(d);
	}
	
	@Override
	public void dispose() {
		game.removeComponentListener(this);
		game.removeKeyListener(this);
		game.removeMouseListener(this);
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
		setDimensions(game.getWidth(), game.getHeight());
	}
	
	public void setDimensions(int width, int height) {
		if (pixelWidth != width || pixelHeight != height) {
			pixelWidth = width;
			pixelHeight = height;
			for (Drawable d: drawables) {
				if (d instanceof Panel) {
					((Panel) d).onRescale();
				} else {
					d.setParentPanel(this);
				}
			}
		}
	}
	
	@Override
	public int pixelWidth() {
		return pixelWidth;
	}
	
	@Override
	public int pixelHeight() {
		return pixelHeight;
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
	public void mousePressed(MouseEvent e) {
		onMousePressed((double)e.getX() / pixelWidth(), (double)e.getY() / pixelHeight(), e.getButton());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		onMouseReleased((double)e.getX() / pixelWidth(), (double)e.getY() / pixelHeight(), e.getButton());
	}

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
