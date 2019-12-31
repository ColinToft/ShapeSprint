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
	
	public Level[] levels;
	
	@Override
	public void init() {
		
		levels = new Level[] {
			new Level("Dimensional Vortex" , Color.BLUE, "dimensionalvortex.txt", "DimensionalVortex.wav"),
			new Level("Spatial Plane", Color.MAGENTA, "spatialplane.txt", "SpatialPlane.wav"),
			new Level("Temporal Nebula", new Color(255, 230, 0), "temporalnebula.txt", "TemporalNebula.wav"),
			new Level("Endless", Color.GREEN, "dimensionalvortex.txt", "Bouncing.wav")
		};
		
		levels[0].setNormalProgress(0.2748375);
		levels[0].setPracticeProgress(1.0);
		
		levels[1].setNormalProgress(0);
		levels[1].setPracticeProgress(0.49293485234897);
		
		levels[2].setNormalProgress(0.12);
		levels[2].setPracticeProgress(0.37893579);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setFrame("Shape Sprint", (int) dim.getWidth(), (int) dim.getHeight());
		setFPS(100);
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
