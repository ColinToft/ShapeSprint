package xyz.colintoft.cgraphics;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

import javax.swing.*;

import xyz.colintoft.cgraphics.components.Panel;

/**
 * A class that handles window operations and manages Scenes to draw a Game to a Window.
 * @author Colin Toft
 */
public abstract class Game extends JFrame {
	
	/**
	 * Called just after this object is instantiated, and before the window is made visible to the user. In this method you should:
	 * 1. Set a frame title and size (using the setFrame() method is recommended)
	 * 2. Set the starting scene using the setScene() method
	 * 3. Set the desired draw and update FPS using setFPS() method or both the setDrawFPS() and setUpdateFPS() methods
	 */
	public void init() {}
	
	public void start() {}
	
	private Scene currentScene = null;
	
	private JPanel contentPane;
	
	/** The amount of times per second that the draw() method of the current scene will be called. */
	private double drawFPS = 60;
	
	/** The amount of times per second that the update() method of the current scene will be called. */
	private double updateFPS = 60;
	
	private double lastUpdateTime;
	
	private Timer updateTimer, drawTimer;
	
	private boolean paused = false;
	
	private boolean buffersCreated = false;
	
	public Game() {

		setLocationRelativeTo(null);
		
		updateTimer = new Timer((int)(1000 / updateFPS), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				double now = System.nanoTime() / 1000000000.0;
				updateScene(now - lastUpdateTime);
				lastUpdateTime = now;
			}
		});
		
		drawTimer = new Timer((int)(1000 / drawFPS), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				drawScene();
			}
		});
		
		init();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		
		updateTimer.start();
		drawTimer.start();
	}
	
	private void updateScene(double dt) {
		currentScene.update(dt);
	}
	
	public void addNotify() {
        super.addNotify();
        
        do {
        	try {
            	createBufferStrategy(2);  
            	buffersCreated = true;
        	} catch (IllegalStateException e) {}
        } while (!buffersCreated);
    }
	
	private void drawScene() {
		if (!isDisplayable() || !buffersCreated) return; // Avoid errors where buffers have not yet been created
		
		BufferStrategy strategy = getBufferStrategy();
		Graphics g = (Graphics2D) strategy.getDrawGraphics();
		
		g.clearRect(0, 0, getWidth(), getHeight());
		
		if (currentScene != null && currentScene.hasParentPanel()) {
			currentScene.draw(g, getInsets().left, getInsets().top);
		}
		
		g.dispose();
		strategy.show();
	}
	
	/**
	 * Sets the width and height of the Game's frame.
	 */
	protected void setFrame(String title, int width, int height) {
		setTitle(title);
		setSize(width, height);
		
	}
	
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		setPreferredSize(new Dimension(width, height));
	}
	
	/**
	 * Sets whether or not the game is fullscreen.
	 * Note this does not affect the actual dimensions of the window:
	 * use {@link Game#setFrame(String, int, int)} or {@link Game#setSize(int, int)}
	 * to change the window's width and height in pixels.
	 */
	protected void setFullscreen(boolean fullscreen) {
		if (fullscreen) {
			setExtendedState(MAXIMIZED_BOTH); 
			setUndecorated(true);
		} else {
			setExtendedState(NORMAL);
			setUndecorated(false);
		}
	}
	
	/**
	 * Sets the FPS (for both drawing and updating) of the game.
	 */
	protected void setFPS(double fps) {
		setDrawFPS(fps);
		setUpdateFPS(fps);
	}
	
	/**
	 * Sets the drawing FPS (how frequently the current scene is drawn to the screen).
	 */
	protected void setDrawFPS(double fps) {
		drawFPS = fps;
		drawTimer.setDelay((int)(1000 / drawFPS));
	}
	
	/**
	 * Sets the update FPS (how frequently the current scene is update).
	 * Note: the scene is not updated when the game is paused
	 */
	protected void setUpdateFPS(double d) {
		updateFPS = d;
		updateTimer.setDelay((int)(1000 / updateFPS));
	}
	
	public void setScene(Scene s) {
		drawTimer.stop();
		updateTimer.stop();
		
		if (currentScene != null) {
			currentScene.dispose();
		}
		
		currentScene = s;
		currentScene.setGame(this);
		
		contentPane = new JPanel();
		contentPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
		contentPane.addComponentListener(currentScene);
		setContentPane(contentPane);

		currentScene.init();
		
		pack();
		
		drawTimer.restart();
		updateTimer.restart();
		
		currentScene.start();
	}
	
	@Override
	public void removeComponentListener(ComponentListener l) {
		super.removeComponentListener(l);
		contentPane.removeComponentListener(l);
	}
	
	public void run() {
		setVisible(true);
		start();
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * Exits the game. By default, this disposes the frame and calls {@link System#exit(int)} to stop the program.
	 */
	public void exit() {
		setVisible(false);
		dispose();
		System.exit(0);
	}
	
	public void pauseGame() {
		paused = true;
		updateTimer.stop();
		currentScene.onPause();
	}
	
	public void resumeGame() {
		paused = false;
		updateTimer.start();
		currentScene.onResume();
	}
	
	public void togglePaused() {
		if (paused) {
			resumeGame();
		} else {
			pauseGame();
		}
	}
}
