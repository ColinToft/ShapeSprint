package xyz.colintoft.shapesprint;

import java.awt.Color;

// Dec 24th, 2019
public class Level {
	
	public String name;
	public Color backgroundColor;
	public String filename;
	public String musicFile;
	public double normalProgress;
	public double practiceProgress;
	
	public Obstacle[] obstacles;
	
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
	
	// 24
	public void setPracticeProgress(double value) {
		practiceProgress = value;
	}
	
	// 30
	public void load() {
		//loadObstaclesFromFile();
		//sortObstacles();
	}
}
