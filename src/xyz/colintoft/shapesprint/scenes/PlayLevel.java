package xyz.colintoft.shapesprint.scenes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.sound.sampled.Clip;

import xyz.colintoft.cgraphics.Scene;
import xyz.colintoft.cgraphics.Util;
import xyz.colintoft.shapesprint.Level;
import xyz.colintoft.shapesprint.LevelView;

// Dec 27
public class PlayLevel extends Scene {
	
	private Level level;
	
	
	private LevelView levelView;
	
	// Dec 27 mod 30
	public PlayLevel(Level level) {
		super();
		setBackground(new Color(0, 0, 0, 0));
		this.level = level;
	}
	
	// Dec 27 mod 30
	public void init() {
		levelView = new LevelView(level);
		add(levelView);
		
	}
	
	// Dec 30
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			game.exit();
		}
	}
}
