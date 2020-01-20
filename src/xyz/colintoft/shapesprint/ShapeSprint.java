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
@Modified December 22nd, 2019, January 8th, 10th & 17th, 2020
@Description A simple clone of the game Geometry Dash, created with Java and the CGraphics library I have previously created.
***********************************************
*/
@SuppressWarnings("serial")
public class ShapeSprint extends Game {
	
	public static ShapeSprint game; // The main game object
	
	public Level[] levels; // A list of Level objects that the user can choose to play

	private boolean firstTime; // True is this is the user's first time playing the game
	public boolean hasUsedTriangleMode; // True if the user has used triangle mode before
	public boolean hasPausedGame; // True if the user has paused the game before
	public boolean hasUsedPracticeMode; // True if the user has used practice mode before
	
	public static final String saveFile = "/saveGame.txt"; // The file where the user's data should be saved to
	
	public static void main(String[] args) {
		// Run the game
		game = new ShapeSprint();
		game.run();
	}
	
	/** Method Name: init()
	 * @Author Colin Toft
	 * @Date December 21st, 2019
	 * @Modified N/A
	 * @Description Initializes the window, creates the levels and loads the previous user progress.
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: N/A
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void init() {
		// Initialize the list levels
		levels = new Level[] {
			new Level("Dimensional Vortex" , Color.BLUE, "dimensionalvortex.txt", "DimensionalVortex.wav"),
			new Level("Spatial Plane", Color.MAGENTA, "spatialplane.txt", "SpatialPlane.wav"),
			new Level("Temporal Nebula", new Color(255, 230, 0), "temporalnebula.txt", "TemporalNebula.wav")
		};
		
		// Load the previous user progress
		loadProgress();
		
		// Set up the window and open the main menu
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setFrame("Shape Sprint", (int) dim.getWidth(), (int) dim.getHeight());
		//setSize(640, 480);
		setFPS(400);
		setFullscreen(false);
		setScene(new MainMenu());
	}

	/** Method Name: saveProgress()
	 * @Author Colin Toft
	 * @Date January 8th, 2019
	 * @Modified January 17th, 2019
	 * @Description Saves the user's progress in the game to a file.
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: N/A
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void saveProgress() {
		try {
			// Open the save file
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("assets" + saveFile))); // Create a PrintWriter to output to the file
			// Write the progress in each level to the file
			for (Level level: levels) {
				writer.println(level.normalProgress);
				writer.println(level.practiceProgress);
			}
			// Write whether the user has used triangle mode, practice mode and the pause menu so the game knows not to show the help messages for these again
			writer.println(hasUsedTriangleMode);
			writer.println(hasPausedGame);
			writer.println(hasUsedPracticeMode);
			writer.close();
		} catch (IOException e) {
			System.out.println("Unable to save progress to file: ");
			e.printStackTrace();
		}
	}
	
	/** Method Name: loadProgress()
	 * @Author Colin Toft
	 * @Date January 8th, 2019
	 * @Modified January 17th, 2019
	 * @Description Loads the user's progress in the game from the save file.
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: N/A
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void loadProgress() {
		if (Util.fileExists(getClass(), saveFile)) { // First make sure the save file exists
			int i = 0;
			String[] lines = Util.readLinesFromFile(getClass(), saveFile); // Read the information from the file into an array of String
			firstTime = true; // Start by assuming it is their first time, if they have made any progress in a level this value will later be set to false
			for (Level level: levels) {
				// Read the progress from the file and store it in the corresponding Level object
				level.setNormalProgress(Double.valueOf(lines[i++]));
				level.setPracticeProgress(Double.valueOf(lines[i++]));
				
				if (level.normalProgress + level.practiceProgress > 0) {
					// If the user has any progress in the level, it is not their first time
					firstTime = false;
				}
			}
			
			// Read the boolean values from the end of the file
			hasUsedTriangleMode = Boolean.valueOf(lines[i++]);
			hasPausedGame = Boolean.valueOf(lines[i++]);
			hasUsedPracticeMode = Boolean.valueOf(lines[i++]);
		} else {
			// The file does not exist, so this must be the user's first time playing
			firstTime = true;
		}
	}
	
	/** Method Name: isFirstTime()
	 * @Author Colin Toft
	 * @Date January 10th, 2019
	 * @Modified N/A
	 * @Description Returns whether or not this is the user's first time playing the game.
	 * @Parameters N/A
	 * @Returns True if this is the user's first time playing, otherwise false
	 * Data Type: boolean
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean isFirstTime() {
		return firstTime;
	}
	
	/** Method Name: onWindowClosing()
	 * @Author Colin Toft
	 * @Date January 17th, 2019
	 * @Modified N/A
	 * @Description Overrides the Game.onWindowClosing() method to save the user's progress whenever the window is closed.
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: N/A
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void onWindowClosing() {
		// Save the user's progress before the window closes
		saveProgress();
	}

}
