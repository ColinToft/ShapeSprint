package xyz.colintoft.shapesprint;

import java.awt.Dimension;
import java.awt.Toolkit;

import xyz.colintoft.cgraphics.Game;
import xyz.colintoft.shapesprint.scenes.MainMenu;

/**
***********************************************
@Author Colin Toft
@Date December 21st, 2019
@Modified N/A
@Description 
***********************************************
*/
@SuppressWarnings("serial")
public class ShapeSprint extends Game {
	
	public static ShapeSprint game;
	
	@Override
	public void init() {
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
