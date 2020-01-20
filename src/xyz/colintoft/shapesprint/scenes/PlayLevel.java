package xyz.colintoft.shapesprint.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.sound.sampled.Clip;

import xyz.colintoft.cgraphics.HorizontalAlign;
import xyz.colintoft.cgraphics.Scene;
import xyz.colintoft.cgraphics.Util;
import xyz.colintoft.cgraphics.VerticalAlign;
import xyz.colintoft.cgraphics.components.DrawableText;
import xyz.colintoft.cgraphics.components.Panel;
import xyz.colintoft.cgraphics.components.Sprite;
import xyz.colintoft.cgraphics.components.DrawableOutlinedText;
import xyz.colintoft.cgraphics.components.DrawableProgressBar;
import xyz.colintoft.cgraphics.components.DrawableRectangle;
import xyz.colintoft.cgraphics.components.DrawableRoundedRectangle;
import xyz.colintoft.shapesprint.Level;
import xyz.colintoft.shapesprint.LevelView;
import xyz.colintoft.shapesprint.ShapeSprint;

/**
***********************************************
@Author Colin Toft
@Date December 27th, 2019
@Modified December 30th, 2019, January 7th, 8th, 9th, 10th, 14th, 16th, 17th & 19th, 2020
@Description The scene where the user can play a level. Includes the level view, pause menu, win screen, and help messages.
***********************************************
*/
public class PlayLevel extends Scene {
	
	private Level level; // The level that is currently being played
	private LevelView levelView; // The LevelView object that is rendering the level
	
	private int attemptNumber = 1; // The current attempt number (incremented by 1 each time the player dies)
	private DrawableOutlinedText attemptText; // The text object that shows the attempt number on the screen
	private final double attemptTextStartX = 0.9; // The x coordinate that the attempt text will start at (as a fraction of the screen width)
	
	private DrawableOutlinedText helpText; // Help text that gives the player tips during the level
	public boolean needsJumpHelp = true; // True if the player needs help jumping (the jumping tutorial message needs to be displayed)
	private final String jumpHelpMessage = "Click or tap to jump over obstacles"; // Tip that helps the user learn to jump
	private final String triangleHelpMessage = "Hold the mouse or space bar to fly"; // Tip that helps the user learn to use triangle mode
	private final String pauseMenuHelpMessage = "Press escape for more options"; // Tip that shows the user how to access the pause menu
	
	private DrawableOutlinedText pauseMenuText; // The text that displays the pause menu help message

	private DrawableProgressBar progressBar; // A progress bar that shows the player's progress during the level
	private final double progressBarWidth = 0.3; // The width of the above progress bar
	private final double progressBarHeight = 0.03; // The height of the above progress bar
	
	private DrawableOutlinedText percentageText; // Text that shows the player's progress during the level as a percentage
	
	private final double buttonWidth = 0.1; // The width of the menu buttons in the pause menu and win screen
	
	private Panel pauseMenu; // Gives the user the option to go back to the menu, resume the game, or enter practice mode
	private DrawableProgressBar normalProgressBar; // Shows the user's progress in normal mode on the level (appears in the pause menu)
	private DrawableOutlinedText normalPercentageText; // Shows the user's progress in normal mode on the level (appears in the pause menu)
	private DrawableProgressBar practiceProgressBar; // Shows the user's progress in practice mode on the level (appears in the pause menu)
	private DrawableOutlinedText practicePercentageText; // Shows the user's progress in practice mode on the level (appears in the pause menu)
	private Sprite practiceTip; // An image that shows the user how to enter practice mode while in the pause menu
	private Sprite changeModeButton; // A button in the pause menu that allows the player to switch between normal mode and practice mode
	
	private Panel winScreen; // Tells the player they have completed the level and gives them the option to restart the level or go back to the menu
	private DrawableOutlinedText levelCompleteText; // Text that shows a "Level Complete" message
	private DrawableOutlinedText attemptCountText; // Displays the amount of attempts taken to complete the level
	private DrawableOutlinedText jumpCountText; // Displays the amount of jumps taken to complete the level
	private DrawableOutlinedText elapsedTimeText; // Displays the amount of time taken to complete the level

	/** Method Name: PlayLevel()
	 * @Author Colin Toft
	 * @Date December 27th, 2019
	 * @Modified December 30th, 2019
	 * @Description Creates a new PlayLevel object
	 * @Parameters
	 *      - Level level: The Level object storing the level that will be played
	 * @Returns N/A
	 * Data Type: PlayLevel, Level
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public PlayLevel(Level level) {
		super();
		setBackground(new Color(0, 0, 0, 0)); // Use a transparent background
		this.level = level; // Store the level object
	}
	
	/** Method Name: init()
	 * @Author Colin Toft
	 * @Date December 27th, 2019
	 * @Modified December 30th, 2019, January 7th, 8th, 9th, 10th, 14th & 17th, 2020
	 * @Description Overrides Scene.init() and loads the text, images and menus necessary for this Scene
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: Scene, LevelView, DrawableOutlinedText, DrawableProgressBar, DrawableRoundedRectangle, Sprite, Panel
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void init() {
		level.load(); // Load the level (puts the obstacles into a 2D array)
		
		// Create an add a new LevelView object to render the level
		levelView = new LevelView(level);
		add(levelView);
		
		Font titleFont = Util.loadFontFromFile(getClass(), "Pusab.ttf", 100); // The font object used to draw text
		
		// Create and add the attempt counter
		attemptText = new DrawableOutlinedText(attemptTextStartX, 0.25, "Attempt " + attemptNumber, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		attemptText.setMaxHeight(0.085);
		add(attemptText);
		
		// Create and add the help text
		helpText = new DrawableOutlinedText(0.5, 0.4, jumpHelpMessage, titleFont.deriveFont(100f), Color.white, Color.black, 1f, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		helpText.setMaxWidth(0.75);
		needsJumpHelp = ((ShapeSprint) game).isFirstTime(); // If it is the first time playing, display the jump help message
		if (!needsJumpHelp) {
			helpText.hide(); // If the user does not need help, hide the help text
		}
		add(helpText);
		
		// Create and add the pause menu tip
		pauseMenuText = new DrawableOutlinedText(0.9, 0.01, pauseMenuHelpMessage, titleFont.deriveFont(100f), Color.white, Color.black, 1f, HorizontalAlign.RIGHT, VerticalAlign.TOP);
		pauseMenuText.setMaxWidth(0.5);
		add(pauseMenuText);
		
		// Create and add the progress bar that tracks the user's progress during the level
		progressBar = new DrawableProgressBar(0.5 * (1 - progressBarWidth), 0.02, progressBarWidth, progressBarHeight, progressBarHeight * 0.65, progressBarHeight, Color.WHITE, 2f, Color.red, new Color(0, 0, 0, 0));
		add(progressBar);
				
		// Create and add the percentage text that tracks the user's progress during the level
		percentageText = new DrawableOutlinedText(progressBar.getX() + progressBar.getWidth(), progressBar.getCenterY(), "0%", titleFont.deriveFont(60f), Color.white, Color.black, 1f, HorizontalAlign.LEFT, VerticalAlign.CENTER);
		percentageText.setMaxHeight(progressBarHeight);
		add(percentageText);
		
		// Create and add the pause menu
		pauseMenu = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle rect = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, new Color(0, 0, 0, 180)); // The rectangle which is the background of the pause menu
		DrawableOutlinedText levelText = new DrawableOutlinedText(rect.getCenterX(), 0.15, level.name, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER); // Text to display the name of the level
		levelText.setMaxWidth(rect.getWidth() * 0.9);
		
		// Add the normal mode progress bar and percentage text
		normalProgressBar = new DrawableProgressBar(0.1, 0.3, 0.8, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.GREEN, new Color(0, 0, 0, 70));
		normalProgressBar.setValue(level.normalProgress);
		DrawableOutlinedText normalModeText = new DrawableOutlinedText(0.5, normalProgressBar.getY() - 0.01, "Normal Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		normalModeText.setMaxHeight(normalProgressBar.getHeight() * 0.7);
		normalPercentageText = new DrawableOutlinedText(normalProgressBar.getCenterX(), normalProgressBar.getCenterY(),
				Util.toPercentageString(normalProgressBar.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		normalPercentageText.setMaxHeight(normalProgressBar.getHeight() * 0.7);
		
		// Add the practice mode progress bar and percentage text
		practiceProgressBar = new DrawableProgressBar(0.1, 0.55, 0.8, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.CYAN, new Color(0, 0, 0, 70));
		practiceProgressBar.setValue(level.practiceProgress);
		DrawableOutlinedText practiceModeText = new DrawableOutlinedText(0.5, practiceProgressBar.getY() - 0.01, "Practice Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		practiceModeText.setMaxHeight(practiceProgressBar.getHeight() * 0.7);
		practicePercentageText = new DrawableOutlinedText(practiceProgressBar.getCenterX(), practiceProgressBar.getCenterY(),
				Util.toPercentageString(practiceProgressBar.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		practicePercentageText.setMaxHeight(practiceProgressBar.getHeight() * 0.7);
		
		double progressBarBottom = practiceProgressBar.getY() + practiceProgressBar.getHeight(); // The bottom coordinate of the lower progress bar
		double buttonHeight = parentWidthFractionToParentHeightFraction(buttonWidth); // Calculate the height of a button
		
		// A button that lets the user change between practice and normal mode
		changeModeButton = new Sprite(0.25, (1 - progressBarBottom - buttonHeight) / 2 + progressBarBottom, buttonWidth, buttonHeight, "menuItems/practiceMode.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				changeMode();
			}
		};
		
		// A button that closes the pause menu and resumes the game
		Sprite resumeButton = new Sprite(0.5 * (1 - buttonWidth * 1.5), (1 - progressBarBottom - buttonHeight * 1.5) / 2 + progressBarBottom, buttonWidth * 1.5, buttonHeight * 1.5, "menuItems/resume.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				pauseMenu.hide();
				resumeGame();
			}
		};
		
		// A button that returns to the main menu
		Sprite menuButton = new Sprite(1 - changeModeButton.getX() - buttonWidth, (1 - progressBarBottom - buttonHeight) / 2 + progressBarBottom, buttonWidth, buttonHeight, "menuItems/menu.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				exitToMenu();
			}
		};
		
		// An image that shows the user how to go into practice mode
		practiceTip = new Sprite(changeModeButton.getX() - 0.12, changeModeButton.getY() - 0.01, 0.13, 0.1, "tips/practiceModeTip.png");
		
		// Add the components to the pause menu, then add the pause menu
		pauseMenu.add(rect);
		pauseMenu.add(levelText);
		pauseMenu.add(normalProgressBar);
		pauseMenu.add(normalModeText);
		pauseMenu.add(normalPercentageText);
		pauseMenu.add(practiceProgressBar);
		pauseMenu.add(practiceModeText);
		pauseMenu.add(practicePercentageText);
		pauseMenu.add(changeModeButton);
		pauseMenu.add(resumeButton);
		pauseMenu.add(menuButton);
		pauseMenu.add(practiceTip);
		
		add(pauseMenu);
		pauseMenu.hide();
		
		// Create and add the pause menu
		winScreen = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle rect2 = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, new Color(0, 0, 0, 180)); // The rectangle which is the background of the pause menu
		levelCompleteText = new DrawableOutlinedText(rect.getCenterX(), 0.15, "Level Complete!", titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER); // Text that displayes "Level Complete!"
		levelCompleteText.setMaxWidth(rect.getWidth() * 0.9);
		attemptCountText = new DrawableOutlinedText(rect.getCenterX(), 0.4, "Attempts: " + attemptNumber, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER); // Shows the number of attempts used
		attemptCountText.setMaxWidth(rect.getWidth() * 0.7);
		jumpCountText = new DrawableOutlinedText(rect.getCenterX(), 0.55, "Jumps: " + levelView.jumpCount, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER); // Shows the number of attempts used
		jumpCountText.setMaxWidth(rect.getWidth() * 0.7);
		
		// A button that closes the win screen and restarts the level from the beginning
		Sprite playAgainButton = new Sprite(0.5 * (1 - buttonWidth * 1.5), (1 - progressBarBottom - buttonHeight * 1.5) / 2 + progressBarBottom, buttonWidth * 1.5, buttonHeight * 1.5, "menuItems/playAgain.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				attemptNumber = 0;
				levelView.restartLevel();
				levelView.hasBeatLevel = false;
				winScreen.hide();
				resumeGame();
			}
		};
		
		// Add the components to the win screen and add the win screen to the scene
		winScreen.add(rect2);
		winScreen.add(levelCompleteText);
		winScreen.add(attemptCountText);
		winScreen.add(jumpCountText);
		winScreen.add(playAgainButton);
		winScreen.add(menuButton);
		add(winScreen);
		winScreen.hide();
	}
	
	/** Method Name: update()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 8th & 16th, 2020
	 * @Description Overrides Scene.update() and updates the level progress bar, attempt counter, and help text
	 * @Parameters
	 *      - double dt: The time that has elapsed since the last time update() was called
	 * @Returns N/A
	 * Data Type: Scene, LevelView, ShapeSprint, DrawableOutlinedText, DrawableProgressBar
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void update(double dt) {
		super.update(dt);
		attemptText.moveLeft(dt * levelView.getScrollSpeed()); // Move the attempt text to the left along with the level
		// Update the progress bar and percentage text to reflect the user's progress in the level
		progressBar.setValue(levelView.getPlayerProgress());
		percentageText.setText(Util.toPercentageString(levelView.getPlayerProgress()));
		
		ShapeSprint ss = (ShapeSprint) game;

		// Hide the help text if it is no longer necessary
		if (levelView.jumpCount > 0 && helpText.getText().equals(jumpHelpMessage) || ss.hasUsedTriangleMode && helpText.getText().equals(triangleHelpMessage)) {
			needsJumpHelp = false;
			helpText.hide();
		}
		
		// Show triangle mode help text if needed
		if (levelView.isTriangleMode() && !ss.hasUsedTriangleMode) {
			helpText.setText(triangleHelpMessage);
			helpText.show();
		}
		
		// Hide the pause menu tip if the user has already paused the game before
		if (ss.hasPausedGame) {
			pauseMenuText.hide();
		}
	}
	
	/** Method Name: keyPressed()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 8th, 14th & 19th, 2020
	 * @Description Overrides Scene.keyPressed() and handles key presses while playing a level, mainly showing/hiding the pause menu and win screen
	 * @Parameters
	 *      - KeyEvent e: the event containing data about the key press event
	 * @Returns N/A
	 * Data Type: ShapeSprint, LevelView, Panel, DrawableOutlinedText
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void keyPressed(KeyEvent e) {
		ShapeSprint ss = (ShapeSprint) game;
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (winScreen.isVisible()) {
				// If the win screen is showing, exit to the menu
				exitToMenu();
			} else if (!levelView.hasBeatLevel) {
				// Toggle the pause menu
				togglePaused();
				if (isPaused()) {
					// Set the change mode button's image according to what mode the game is currently in
					if (levelView.isPracticeMode()) {
						changeModeButton.setImage("menuItems/normalMode.png");
					} else {
						changeModeButton.setImage("menuItems/practiceMode.png");
					}
					// Show the pause menu and hide the pause menu tip
					pauseMenu.show();
					pauseMenuText.hide();
					ss.hasPausedGame = true;
					
					// Show the practice mode tip if necessary
					if (ss.hasUsedPracticeMode) {
						practiceTip.hide();
					} else {
						practiceTip.show();
					}
				} else {
					// The game is being resumed, so hide the pause menu
					pauseMenu.hide();
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (winScreen.isVisible()) {
				// If the user presses enter on the win screen, restart the level from the beginning
				attemptNumber = 0;
				levelView.restartLevel();
				levelView.hasBeatLevel = false;
				winScreen.hide();
				resumeGame();
			}
		}
	}

	/** Method Name: restartLevel()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 8th & 9th, 2020
	 * @Description Resets the position of the components when the player restarts the level
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: DrawableOutlinedText, DrawableProgressBar
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void restartLevel() {
		// Increase the attempt number by 1 and reposition the attempt text
		attemptNumber++;
		attemptText.setText("Attempt " + attemptNumber);
		attemptText.setX(attemptTextStartX);
		
		// If the user has died twice without jumping, display the jump help message
		if (attemptNumber > 2 && levelView.jumpCount == 0) {
			helpText.setText(jumpHelpMessage);
			helpText.show();
		}
		
		// Save progress in the level
		saveLevelProgress();
		
		// Update the pause menu progress bars to the current level progress
		normalProgressBar.setValue(level.normalProgress);
		normalPercentageText.setText(Util.toPercentageString(level.normalProgress));
		practiceProgressBar.setValue(level.practiceProgress);
		practicePercentageText.setText(Util.toPercentageString(level.practiceProgress));
	}
	
	/** Method Name: changeMode()
	 * @Author Colin Toft
	 * @Date January 8th, 2020
	 * @Modified January 9th & 17th, 2020
	 * @Description Changes the mode from normal mode to practice mode or vice versa
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: Panel, LevelView, ShapeSprint
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void changeMode() {
		// First, close the pause menu and resume the game
		pauseMenu.hide();
		resumeGame();
		levelView.changeMode(); // Tell the level view to change modes
		((ShapeSprint) game).hasUsedPracticeMode = true; // Remember that practice mode has been used so that the practice mode tip will not be displayed again
	}
	
	/** Method Name: saveLevelProgress()
	 * @Author Colin Toft
	 * @Date January 10th, 2020
	 * @Modified N/A
	 * @Description Stores the current progress in the level to the Level object
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: LevelView, Level
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	private void saveLevelProgress() {
		// Update the level progress based on the corresponding mode
		if (levelView.isPracticeMode()) {
			level.updatePracticeProgress(levelView.getPlayerProgress());
		} else {
			level.updateNormalProgress(levelView.getPlayerProgress());
		}
	}
	
	/** Method Name: exitToMenu()
	 * @Author Colin Toft
	 * @Date January 8th, 2020
	 * @Modified N/A
	 * @Description Exits the PlayLevel Scene and returns to the main menu
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: ShapeSprint, LevelView, Level, Game, MainMenu
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void exitToMenu() {
		ShapeSprint ss = (ShapeSprint) game;

		// Save all progress before closing the level
		saveLevelProgress();
		ss.saveProgress();
		
		levelView.exitingToMenu();
		
		// Search for this level in the list of levels, and have the main menu start on that level
		for (int i = 0; i < ss.levels.length; i++) {
			if (ss.levels[i].name.equals(level.name)) {
				game.setScene(new MainMenu(i)); // Pass in the index of the level to display to the MainMenu object
				return;
			}
		}
		game.setScene(new MainMenu()); // If the level was not found for some reason, create a new MainMenu object without specifiying the index of the level to display
	}

	/** Method Name: showWinScreen()
	 * @Author Colin Toft
	 * @Date January 14th, 2020
	 * @Modified January 19th, 2020
	 * @Description Shows the win screen (when the user successfully completes a level)
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: LevelView, DrawableOutlinedText, Panel
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void showWinScreen() {
		// Set the title of the win screen based on the current mode
		if (levelView.isPracticeMode()) {
			levelCompleteText.setText("Practice Complete!");
		} else {
			levelCompleteText.setText("Level Complete!");
		}
		// Refresh the attempt count and jump count
		attemptCountText.setText("Attempts: " + attemptNumber);
		jumpCountText.setText("Jumps: " + levelView.jumpCount);
		// After the text has been updated, show the win screen
		winScreen.show();
	}
	
	/** Method Name: dispose()
	 * @Author Colin Toft
	 * @Date January 17th, 2020
	 * @Modified N/A
	 * @Description Overrides the Scene.dispose() method which is called when a Scene is about to be closed, saves the current progress in the game.
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: ShapeSprint
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void dispose() {
		super.dispose();
		// Save all progress before exiting the scene
		saveLevelProgress();
		((ShapeSprint) game).saveProgress();
	}
}
