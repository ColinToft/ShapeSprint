package xyz.colintoft.shapesprint;

import java.awt.Color;

import xyz.colintoft.cgraphics.Util;

/**
***********************************************
@Author Colin Toft
@Date December 24th, 2019
@Modified December 30th & 31st 2019, January 8th, 2020
@Description A class that stores information for a level, including the obstacles, music, colours and save data.
***********************************************
*/
public class Level {
	
	public String name;
	public Color backgroundColor;
	public String filename;
	public String musicFile;
	public double normalProgress = 0;
	public double practiceProgress = 0;
	
	public Obstacle[][] obstacles;
	public int width, height;
	
	/** Method Name: Level()
	 * @Author Colin Toft
	 * @Date December 24th, 2019
	 * @Modified December 30th, 2019
	 * @Description Creates a new Level object
	 * @Parameters
	 * 		- String name: the name of the level (that the user will see)
	 * 		- Color backgroundColor: the background color for the level
	 * 		- String filename: the name of the file containing the level's data
	 * 		- String musicFile: the name of the file containing the song for the level
	 * @Returns A new level object
	 * Data Type: String, Color
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public Level(String name, Color backgroundColor, String filename, String musicFile) {
		this.name = name;
		this.backgroundColor = backgroundColor;
		this.filename = filename;
		this.musicFile = musicFile;
		normalProgress = 0f;
		practiceProgress = 0f;
	}
	
	/** Method Name: setNormalProgress()
	 * @Author Colin Toft
	 * @Date December 24th, 2019
	 * @Modified N/A
	 * @Description Sets the progress in normal mode for this level
	 * @Parameters
	 * 		- double value: The amount of progress the user has achieved
	 * @Returns N/A
	 * Data Type: double
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void setNormalProgress(double value) {
		normalProgress = value;
	}
	
	/** Method Name: updateNormalProgress()
	 * @Author Colin Toft
	 * @Date January 8th, 2019
	 * @Modified N/A
	 * @Description Updates the progress in normal mode for this level and saves it if necessary
	 * @Parameters
	 * 		- double value: The amount of progress the user has achieved
	 * @Returns N/A
	 * Data Type: double
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void updateNormalProgress(double value) {
		if (value > normalProgress) {
			normalProgress = value;
			ShapeSprint.game.saveProgress();
		}
	}
	
	/** Method Name: setPracticeProgress()
	 * @Author Colin Toft
	 * @Date December 24th, 2019
	 * @Modified N/A
	 * @Description Sets the progress in practice mode for this level
	 * @Parameters
	 * 		- double value: The amount of progress the user has achieved
	 * @Returns N/A
	 * Data Type: double
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void setPracticeProgress(double value) {
		practiceProgress = value;
	}
	
	/** Method Name: updatePracticeProgress()
	 * @Author Colin Toft
	 * @Date January 8th, 2019
	 * @Modified N/A
	 * @Description Updates the progress in practice mode for this level and saves it if necessary
	 * @Parameters
	 * 		- double value: The amount of progress the user has achieved
	 * @Returns N/A
	 * Data Type: double
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void updatePracticeProgress(double value) {
		if (value > practiceProgress) {
			practiceProgress = value;
			ShapeSprint.game.saveProgress();
		}
	}
	
	/** Method Name: load()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified N/A
	 * @Description Loads the level data from a file into the 2D array of obstacles
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: String, int, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void load() {
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
