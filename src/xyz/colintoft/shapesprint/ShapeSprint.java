package xyz.colintoft.shapesprint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import xyz.colintoft.cgraphics.Game;
import xyz.colintoft.cgraphics.Util;
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

	private boolean firstTime;
	public boolean hasUsedTriangleMode;
	public boolean hasPausedGame;
	public boolean hasUsedPracticeMode;
	
	public static final String saveFile = "/saveGame.txt";
	
	@Override
	public void init() {
		
		levels = new Level[] {
			new Level("Dimensional Vortex" , Color.BLUE, "dimensionalvortex.txt", "DimensionalVortex.wav"),
			new Level("Spatial Plane", Color.MAGENTA, "spatialplane.txt", "SpatialPlane.wav"),
			new Level("Temporal Nebula", new Color(255, 230, 0), "temporalnebula.txt", "TemporalNebula.wav"),
			new Level("Endless", Color.GREEN, "dimensionalvortex.txt", "Bouncing.wav")
		};
		
		loadProgress();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setFrame("Shape Sprint", (int) dim.getWidth(), (int) dim.getHeight());
		setSize(640, 480);
		setFPS(400);
		setFullscreen(false);
		setScene(new MainMenu());
	}
	
	public static void main(String[] args) {
		game = new ShapeSprint();
		game.run();
	}

	// 8 mod 17
	public void saveProgress() {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("assets" + saveFile))); //create a filewriter to output to the file
			for (Level level: levels) {
				writer.println(level.normalProgress);
				writer.println(level.practiceProgress);
			}
			writer.println(hasUsedTriangleMode);
			writer.println(hasPausedGame);
			writer.println(hasUsedPracticeMode);
			writer.close();
		} catch (IOException e) {
			System.out.println("Unable to save progress to file: ");
			e.printStackTrace();
		}
	}
	
	// 8
	public void loadProgress() {
		if (Util.fileExists(getClass(), saveFile)) {
			int i = 0;
			String[] lines = Util.readLinesFromFile(getClass(), saveFile);
			firstTime = true;
			for (Level level: levels) {
				level.setNormalProgress(Double.valueOf(lines[i++]));
				level.setPracticeProgress(Double.valueOf(lines[i++]));
				
				if (level.normalProgress + level.practiceProgress > 0) {
					// If the user has any progress in the level, it is not their first time
					firstTime = false;
				}
			}
			hasUsedTriangleMode = Boolean.valueOf(lines[i++]);
			hasPausedGame = Boolean.valueOf(lines[i++]);
			hasUsedPracticeMode = Boolean.valueOf(lines[i++]);
		} else {
			firstTime = true;
		}
	}
	
	// 10
	public boolean isFirstTime() {
		return firstTime;
	}
	
	// 17
	@Override
	public void onWindowClosing() {
		saveProgress();
	}

}
