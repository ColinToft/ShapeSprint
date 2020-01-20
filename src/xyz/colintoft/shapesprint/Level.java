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
	
	public String name; // The name of the level (displayed to the user)
	public Color backgroundColor; // The background color of the level
	public String filename; // The filename where the level data is stored
	public String musicFile; // The filename where the music for the level is stored
	public double normalProgress = 0; // The user's highest progress for this level in normal mode (from 0 to 1, where 1 means they have completed the level)
	public double practiceProgress = 0; // The user's highest progress for this level in practice mode (from 0 to 1, where 1 means they have completed the level)
	
	public Obstacle[][] obstacles; // A 2D array of obstacles that make up the level
	public int width, height; // The width and height of this level in blocks
	
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
		// Store the given information
		this.name = name;
		this.backgroundColor = backgroundColor;
		this.filename = filename;
		this.musicFile = musicFile;
		// Intialize the level progress to 0 in both modes
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
	 * @Date January 8th, 2020
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
		// Only replace the normal progress value if the user has beaten their previous high score
		if (value > normalProgress) {
			normalProgress = value;
			ShapeSprint.game.saveProgress(); // Save the user's progress to the save file
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
	 * @Date January 8th, 2020
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
		// Only replace the practice progress value if the user has beaten their previous high score
		if (value > practiceProgress) {
			practiceProgress = value;
			ShapeSprint.game.saveProgress(); // Save the user's progress to the save file
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
		// Get a list of each line in the file (each obstacle is on its own line)
		String[] lines = Util.readLinesFromFile(getClass(), "/levels/" + filename);
		
		// First find the necessary width and height based on the largest x and y values
		width = 0;
		height = 0;
		int x, y;
		String[] values;
		// Loop through the list and look at the x and y values of each obstacle. If it is outside the current width or height, make that dimension larger
		for (String line: lines) {
			if (line.length() > 0 && Character.isDigit(line.charAt(0))) { // Make sure the line is valid
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
		
		// Create the obstacles array with the width and height of this level
		obstacles = new Obstacle[width][height];
		
		// Now, read the coordinates and type of each obstacle and store it in the 2D array for the level
		for (String line: lines) {
			if (line.length() > 0 && Character.isDigit(line.charAt(0))) { // Make sure the line is valid
				values = line.split(" ");
				x = Integer.valueOf(values[0]);
				y = Integer.valueOf(values[1]);
				obstacles[x][y] = Obstacle.fromString(values[2]);
				
				// TODO delete this
				if (obstacles[x][y] == null) {
					System.out.println("Null obstacle at " + x + " " + y);
				}
			}
		}
		
		// Go through the squares and assign them to the right square type based on their neighbors
		for (x = 0; x < width; x++) {
			for (y = 0; y < height; y++) {
				if (obstacles[x][y] != null && obstacles[x][y].isSolid()) { // First make sure there is a square in this location
					boolean left, right, up, down, bottomLeft, bottomRight, topLeft, topRight; // The square's neighbors in all directions (true if there is a square in that location, otherwise false)
					
					// Look at each block around the obstacle and store if there is a square in that direction or not
					try {
						left = obstacles[x - 1][y] != null && obstacles[x - 1][y].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						left = false;
					}
					
					try {
						right = obstacles[x + 1][y] != null && obstacles[x + 1][y].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						right = false;
					}
					
					try {
						up = obstacles[x][y + 1] != null && obstacles[x][y + 1].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						up = false;
					}
					
					try {
						down = obstacles[x][y - 1] != null && obstacles[x][y - 1].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						down = false;
					}
					
					try {
						bottomLeft = obstacles[x - 1][y - 1] != null && obstacles[x - 1][y - 1].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						bottomLeft = false;
					}
					
					try {
						bottomRight = obstacles[x + 1][y - 1] != null && obstacles[x + 1][y - 1].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						bottomRight = false;
					}
					
					try {
						topLeft = obstacles[x - 1][y + 1] != null && obstacles[x - 1][y + 1].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						topLeft = false;
					}
					
					try {
						topRight = obstacles[x + 1][y + 1] != null && obstacles[x + 1][y + 1].isSolid();
					} catch (ArrayIndexOutOfBoundsException e) {
						topRight = false;
					}
					
					// Look at the squares neighbors to determine the correct shape
					if (!left && !right && !up && !down) {
						obstacles[x][y] = Obstacle.SQUARE;
					} else if (left && right && !up && down) {
						if (!bottomLeft) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_BOTTOM_LEFT_LINE_TOP;
						} else if (!bottomRight) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_BOTTOM_RIGHT_LINE_TOP;
						} else {
							obstacles[x][y] = Obstacle.SQUARE_TOP_1;
						}
					} else if (left && right && up && !down) {
						if (!topLeft && !topRight) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_TOP_LEFT_TOP_RIGHT_LINE_BOTTOM;
						} else if (!topLeft) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_TOP_LEFT_LINE_BOTTOM;
						} else if (!topRight) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_TOP_RIGHT_LINE_BOTTOM;
						} else {
							obstacles[x][y] = Obstacle.SQUARE_BOTTOM_1;
						}
					} else if (!left && right && up && down) {
						obstacles[x][y] = Obstacle.SQUARE_LEFT_1;
					} else if (left && !right && up && down) {
						obstacles[x][y] = Obstacle.SQUARE_RIGHT_1;
					} else if (!left && right && !up && down) {
						obstacles[x][y] = Obstacle.SQUARE_TOP_LEFT;
					} else if (!left && right && up && !down) {
						obstacles[x][y] = Obstacle.SQUARE_BOTTOM_LEFT;
					} else if (left && !right && !up && down) {
						obstacles[x][y] = Obstacle.SQUARE_TOP_RIGHT;
					} else if (left && !right && up && !down) {
						obstacles[x][y] = Obstacle.SQUARE_BOTTOM_RIGHT;
					} else if (!left && !right && up && down) {
						obstacles[x][y] = Obstacle.SQUARE_VERTICAL;
					} else if (left && right && !up && !down) {
						obstacles[x][y] = Obstacle.SQUARE_HORIZONTAL;
					} else if (left && !right && !up && !down) {
						obstacles[x][y] = Obstacle.SQUARE_RIGHT_3;
					} else if (!left && right && !up && !down) {
						obstacles[x][y] = Obstacle.SQUARE_LEFT_3;
					} else if (!left && !right && up && !down) {
						obstacles[x][y] = Obstacle.SQUARE_BOTTOM_3;
					} else if (!left && !right && !up && down) {
						obstacles[x][y] = Obstacle.SQUARE_TOP_3;
					} else {
						if (!bottomLeft) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_BOTTOM_LEFT;
						} else if (!bottomRight) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_BOTTOM_RIGHT;
						} else if (!topLeft) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_TOP_LEFT;
						} else if (!topRight) {
							obstacles[x][y] = Obstacle.SQUARE_CENTER_TOP_RIGHT;
						} else {
							obstacles[x][y] = Obstacle.SQUARE_CENTER;
						}
					}
				}
			}
		}
	}
}
