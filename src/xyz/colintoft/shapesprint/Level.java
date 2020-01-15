package xyz.colintoft.shapesprint;

import java.awt.Color;

import xyz.colintoft.cgraphics.Util;

// Dec 24th, 2019
public class Level {
	
	public String name;
	public Color backgroundColor;
	public String filename;
	public String musicFile;
	public double normalProgress = 0;
	public double practiceProgress = 0;
	
	public Obstacle[][] obstacles;
	public int width, height;
	
	// Dec 24th mod 30
	public Level(String name, Color backgroundColor, String filename, String musicFile) {
		this.name = name;
		this.backgroundColor = backgroundColor;
		this.filename = filename;
		this.musicFile = musicFile;
		normalProgress = 0f;
		practiceProgress = 0f;
	}
	
	// Dec 24th
	public void setNormalProgress(double value) {
		normalProgress = value;
	}
	
	// 8
	public void updateNormalProgress(double value) {
		if (value > normalProgress) {
			normalProgress = value;
			ShapeSprint.game.saveProgress();
		}
	}
	
	// 24
	public void setPracticeProgress(double value) {
		practiceProgress = value;
	}
	
	// 8
	public void updatePracticeProgress(double value) {
		if (value > practiceProgress) {
			practiceProgress = value;
			ShapeSprint.game.saveProgress();
		}
	}
	
	// 30
	public void load() {
		loadObstaclesFromFile();
	}
	
	// 31
	private void loadObstaclesFromFile() {
		String[] lines = Util.readLinesFromFile(getClass(), "/levels/" + filename);
		
		// First find the necessary width and height based on the largest x and y values
		width = 0;
		height = 0;
		int x, y;
		String[] values;
		for (String line: lines) {
			if (line.length() > 0 && Character.isDigit(line.charAt(0))) {
				values = line.split(" ");
				x = Integer.valueOf(values[0]);
				y = Integer.valueOf(values[1]);
				if (x > width) {
					width = x;
				}
				if (y > height) {
					height = y;
				}
			}
		}
		
		width++;
		height++;
		
		obstacles = new Obstacle[width][height];
		
		for (String line: lines) {
			if (line.length() > 0 && Character.isDigit(line.charAt(0))) {
				values = line.split(" ");
				x = Integer.valueOf(values[0]);
				y = Integer.valueOf(values[1]);
				obstacles[x][y] = Obstacle.fromString(values[2]);
			}
		}
	}
}
