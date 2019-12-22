package xyz.colintoft.shapesprint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import xyz.colintoft.cgraphics.Game;
import xyz.colintoft.shapesprint.scenes.MainMenu;

/**
***********************************************
@Author Colin Toft
@Date December 21st, 2019
@Modified December 22nd, 2019
@Description 
***********************************************
*/
@SuppressWarnings("serial")
public class ShapeSprint extends Game {
	
	public static ShapeSprint game;
	
	public String[] levelNames;
	public Color[] levelColors;
	
	@Override
	public void init() {
		
		levelNames = new String[] {"Dimensional Vortex", "Bouncing", "Spatial Plane"};
		levelColors = new Color[] {Color.BLUE, Color.GREEN, Color.MAGENTA};
		
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setFrame("Shape Sprint", (int) dim.getWidth(), (int) dim.getHeight());
		
		setFullscreen(true);
		setScene(new MainMenu());
	}
	
	public void start() {
	}
	
	public static void main(String[] args) {
		game = new ShapeSprint();
		game.run();
	}

}
