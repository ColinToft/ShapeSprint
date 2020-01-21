package xyz.colintoft.shapesprint.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.sound.sampled.Clip;

import xyz.colintoft.cgraphics.*;
import xyz.colintoft.cgraphics.components.DrawableOutlinedText;
import xyz.colintoft.cgraphics.components.DrawableProgressBar;
import xyz.colintoft.cgraphics.components.DrawableRoundedRectangle;
import xyz.colintoft.cgraphics.components.DrawableShape;
import xyz.colintoft.cgraphics.components.DrawableText;
import xyz.colintoft.cgraphics.components.Panel;
import xyz.colintoft.cgraphics.components.Sprite;
import xyz.colintoft.shapesprint.ShapeSprint;

/**
***********************************************
@Author Colin Toft
@Date December 21st, 2019
@Modified December 22nd, 24th, 26th & 27th, 2019, January 14th & 18th, 2020
@Description The main menu scene that allows the user to select a level to play.
***********************************************
*/
public class MainMenu extends Scene {
	
	// TODO alignment of text being weird
	// TODO rocket collision physics??
	// TODO different backgrounds for each level

	// TODO finish levels
	// TODO Comment CGraphics
	// TODO resizing windows is weird
	
	// TODO rings
	// TODO upsideDown portals
	// TODO editing checkpoints?
	
	private int currentLevel; // The level that is being displayed

	private Panel panel1; // The panel that displays the currently selected level
	private Panel panel2; // The panel that displays the previously selected level during animations
	private DrawableOutlinedText levelText1, levelText2; // Text that shows the name of a level
	private DrawableProgressBar normalProgressBar1, normalProgressBar2; // A progress bar that shows the user's progress on that level in normal mode
	private DrawableOutlinedText normalPercentageText1, normalPercentageText2; // Text that shows the user's progress on that level in normal mode as a percentage
	private DrawableProgressBar practiceProgressBar1, practiceProgressBar2; // A progress bar that shows the user's progress on that level in practice mode
	private DrawableOutlinedText practicePercentageText1, practicePercentageText2; // Text that shows the user's progress on that level in practice mode as a percentage
	
	private Panel helpScreen; // A help panel that shows some basic tutorial text
	private String tutorialText[] = 
		{"In Shape Sprint, the goal is to avoid oncoming",
		 "obstacles in order to complete a level.     ",
		 "Triangles will kill you but squares are safe",
		 "to jump on! Levels are synchronized with the",
		 "music so use it to help you time your jumps.",
		 "Click on a level to start!           "};
	
	private Panel creditsScreen; // A credits panel that credits the music artists and the original Geometry Dash creator
	private String credits[] = 
		{"Music:                         ",
		"Dimensional Vortex - Owen Crowe (A_D1nGu5)",
		"Spatial Plane - Owen Crowe (A_D1nGu5)",
		"Temporal Nebula - Owen Crowe (A_D1nGu5)",
		"Practice Mode Song - Colin Toft      ",
		"",
		"Menu Song & original Geometry Dash  ",
		"Game - RobTop Games                   "};
		
	private Clip menuMusic; // The menu music that loops while the user is in the menu
	
	private boolean isSwitching = false; // Whether the menu is currently switching between levels
	private Direction switchDirection = Direction.NONE; // The direction that the menu is switching in
	private double velocity = 0; // Current velocity of the panels while being switched
	private double bounceFactor = 150; // How bouncy the animation is
	private double bounceDecay = 0.8; // How quickly the bouncing dies down after the current level is switched
	
	private final double panelWidth = 0.6; // The width of a level panel as a fraction of the screen width
	private final double panelStartX = (1 - panelWidth) * 0.5; // The starting x coordinate of a level panel as a fraction of the screen width

	
	/** Method Name: MainMenu()
	 * @Author Colin Toft
	 * @Date December 21st, 2019
	 * @Modified N/A
	 * @Description Creates a new MainMenu object
	 * @Parameters
	 *      - int currentLevel: The level that should be shown when the menu first appears
	 * @Returns N/A
	 * Data Type: MainMenu, int
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public MainMenu(int currentLevel) {
		super();
		this.currentLevel = currentLevel; // Store the current level to display
	}
	
	/** Method Name: PlayLevel()
	 * @Author Colin Toft
	 * @Date December 21st, 2019
	 * @Modified N/A
	 * @Description Creates a new MainMenu object
	 * @Parameters
	 * @Returns N/A
	 * Data Type: MainMenu, int
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public MainMenu() {
		this(0); // Start by displaying the first level
	}
	
	/** Method Name: init()
	 * @Author Colin Toft
	 * @Date December 21st, 2019
	 * @Modified December 22nd, 24th, 26th & 27th, 2019, January 18th, 2020
	 * @Description Overrides the Scene.init() method, loads the text, music, progress bars and panels in the scene
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: ShapeSprint, Font, Color, Panel, DrawableRoundedRectangle, DrawableOutlinedText, DrawableProgressBar, DrawableShape, Clip
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void init() {
		ShapeSprint ss = (ShapeSprint) game;
		Font titleFont = Util.loadFontFromFile(getClass(), "Pusab.ttf", 100); // Load the font
		
		setBackground(ss.levels[currentLevel].backgroundColor); // Set the background color to the background color of the level being displayed
		
		// Create the first level panel
		panel1 = new Panel(panelStartX, 0.15, panelWidth, 0.7);
		// Add the rectangle and the text with the level's name
		DrawableRoundedRectangle rect = new DrawableRoundedRectangle(0, 0, 1, 0.4, 0.07, 0.125, new Color(0, 0, 0, 70)) { // A rectangle around the name of the level
			@Override
			public void onMouseReleased(double x, double y, int button) {
				startLevel(currentLevel); // When the level is clicked, start playing it
			}
		};
		levelText1 = new DrawableOutlinedText(rect.getCenterX(), rect.getCenterY(), ss.levels[currentLevel].name, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		levelText1.setMaxWidth(rect.getWidth() * 0.9);
		
		// Add the normal mode progress bar and percentage text
		normalProgressBar1 = new DrawableProgressBar(0, 0.6, 1, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.GREEN, new Color(0, 0, 0, 70));
		normalProgressBar1.setValue(ss.levels[currentLevel].normalProgress);
		DrawableOutlinedText normalModeText1 = new DrawableOutlinedText(0.5, normalProgressBar1.getY() - 0.01, "Normal Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		normalModeText1.setMaxHeight(normalProgressBar1.getHeight() * 0.7);
		normalPercentageText1 = new DrawableOutlinedText(normalProgressBar1.getCenterX(), normalProgressBar1.getCenterY(),
				Util.toPercentageString(normalProgressBar1.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		normalPercentageText1.setMaxHeight(normalProgressBar1.getHeight() * 0.7);
		
		// Add the practice mode progress bar and percentage text
		practiceProgressBar1 = new DrawableProgressBar(0, 0.85, 1, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.CYAN, new Color(0, 0, 0, 70));
		practiceProgressBar1.setValue(ss.levels[currentLevel].practiceProgress);
		DrawableOutlinedText practiceModeText1 = new DrawableOutlinedText(0.5, practiceProgressBar1.getY() - 0.01, "Practice Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		practiceModeText1.setMaxHeight(practiceProgressBar1.getHeight() * 0.7);
		practicePercentageText1 = new DrawableOutlinedText(practiceProgressBar1.getCenterX(), practiceProgressBar1.getCenterY(),
				Util.toPercentageString(practiceProgressBar1.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		practicePercentageText1.setMaxHeight(practiceProgressBar1.getHeight() * 0.7);
		
		// Add all the components to the level's panel
		panel1.add(rect);
		panel1.add(levelText1);
		panel1.add(normalProgressBar1);
		panel1.add(normalModeText1);
		panel1.add(normalPercentageText1);
		panel1.add(practiceProgressBar1);
		panel1.add(practiceModeText1);
		panel1.add(practicePercentageText1);
		
		add(panel1);
		
		// Create the second level panel (the same as the first panel)
		panel2 = new Panel(panelStartX + 1, 0.15, panelWidth, 0.7);
		DrawableRoundedRectangle rect2 = new DrawableRoundedRectangle(0, 0, 1, 0.4, 0.07, 0.125, new Color(0, 0, 0, 70));
		levelText2 = new DrawableOutlinedText(rect2.getCenterX(), rect2.getCenterY(), ss.levels[currentLevel + 1].name, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		levelText2.setMaxWidth(rect2.getWidth() * 0.9);
		
		normalProgressBar2 = new DrawableProgressBar(0, 0.6, 1, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.GREEN, new Color(0, 0, 0, 70));
		DrawableOutlinedText normalModeText2 = new DrawableOutlinedText(0.5, normalProgressBar2.getY() - 0.01, "Normal Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		normalModeText2.setMaxHeight(normalProgressBar2.getHeight() * 0.7);
		normalPercentageText2 = new DrawableOutlinedText(normalProgressBar2.getCenterX(), normalProgressBar2.getCenterY(),
				Util.toPercentageString(normalProgressBar2.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		normalPercentageText2.setMaxHeight(normalProgressBar2.getHeight() * 0.7);
		
		practiceProgressBar2 = new DrawableProgressBar(0, 0.85, 1, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.CYAN, new Color(0, 0, 0, 70));
		DrawableOutlinedText practiceModeText2 = new DrawableOutlinedText(0.5, practiceProgressBar2.getY() - 0.01, "Practice Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		practiceModeText2.setMaxHeight(practiceProgressBar2.getHeight() * 0.7);
		practicePercentageText2 = new DrawableOutlinedText(practiceProgressBar2.getCenterX(), practiceProgressBar2.getCenterY(),
				Util.toPercentageString(practiceProgressBar2.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		practicePercentageText2.setMaxHeight(practiceProgressBar2.getHeight() * 0.7);
		
		panel2.add(rect2);
		panel2.add(levelText2);
		panel2.add(normalProgressBar2);
		panel2.add(normalModeText2);
		panel2.add(normalPercentageText2);
		panel2.add(practiceProgressBar2);
		panel2.add(practiceModeText2);
		panel2.add(practicePercentageText2);
		
		add(panel2);
		
				
		// Load the triangles
		double[] xCoords1 = {0.02, 0.06, 0.06};
		double[] yCoords = {0.5, 0.4, 0.6};
		double[] xCoords2 = {0.98, 0.94, 0.94};
		DrawableShape leftTriangle = new DrawableShape(xCoords1, yCoords, Color.black, 3f, Color.white) {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				switchLevel(Direction.LEFT); // The left triangle switches to the level on the left
			}
		};
		DrawableShape rightTriangle = new DrawableShape(xCoords2, yCoords, Color.black, 3f, Color.white) {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				switchLevel(Direction.RIGHT); // The right triangle switches to the level on the right
			}
		};
		// Add the triangles to the menu
		add(leftTriangle);
		add(rightTriangle);
		
		// Add the help button and credits button
		double buttonWidth = 0.1; // The width of the help and credits buttons (as a fraction of the screen width)
		double buttonHeight = parentWidthFractionToParentHeightFraction(buttonWidth); // The height of the help and credits buttons (as a fraction of the screen height)
		
		// Add the help button to the menu
		Sprite helpButton = new Sprite(1 - buttonWidth, 1 - buttonHeight, buttonWidth, buttonHeight, "menuItems/helpButton.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				if (!creditsScreen.isVisible()) {
					helpScreen.show();
				}
			}
		};
		add(helpButton);
		
		// Add the credits button to the menu
		Sprite creditsButton = new Sprite(0, 1 - buttonHeight, buttonWidth * 1.1, buttonHeight, "menuItems/creditsButton.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				if (!helpScreen.isVisible()) {
					creditsScreen.show();
				}
			}
		};
		add(creditsButton);
		
		// Add the help screen: text on top of a black rectangle with an x button to close the screen
		helpScreen = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle helpRect = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, Color.BLACK); // The background rectangle
		DrawableOutlinedText helpTitleText = new DrawableOutlinedText(helpRect.getCenterX(), 0.15, "Help", titleFont.deriveFont(200f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER); // Displays the title "help"
		helpTitleText.setMaxHeight(helpRect.getHeight() * 0.15);
		double textY = 0.35; // The y coordinate of the text
		DrawableText[] helpText = new DrawableText[tutorialText.length]; // Stores the DrawableText objects for the tutorial text
		// Loop through each line of text and load it into a DrawableText object
		for (int i = 0; i < tutorialText.length; i++) {
			helpText[i] = new DrawableText(helpRect.getX() + helpRect.getWidth() * 0.05, textY, tutorialText[i], titleFont, Color.white, HorizontalAlign.LEFT, VerticalAlign.CENTER);
			helpText[i].setMaxWidth(helpRect.getWidth() * 0.9);
			textY += 0.07; // Increase the y coordinate each time
		}		
	
		// Used in the help and credits screens to close them
		Sprite xButton = new Sprite(1 - buttonWidth, 0, buttonWidth, buttonHeight, "menuItems/xButton.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				helpScreen.hide();
				creditsScreen.hide();
			}
		};
		
		// Add the components to the help screen and add the help screen to the main menu
		helpScreen.add(helpRect);
		helpScreen.add(helpTitleText);
		for (DrawableText t: helpText) {
			helpScreen.add(t);
		}
		helpScreen.add(xButton);
		
		add(helpScreen);
		helpScreen.hide();
		
		// Add the help screen: text on top of a black rectangle with an x button to close the screen
		creditsScreen = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle creditsRect = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, Color.BLACK); // The background rectangle
		DrawableOutlinedText creditsTitleText = new DrawableOutlinedText(creditsRect.getCenterX(), 0.15, "Credits", titleFont.deriveFont(200f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER); // Displays the title "credits"
		creditsTitleText.setMaxHeight(helpRect.getHeight() * 0.15);
		textY = 0.35; // The y coordinate of the text
		DrawableText[] creditsText = new DrawableText[credits.length]; // Stores the DrawableText objects for the credits text
		// Loop through each line of text and load it into a DrawableText object
		for (int i = 0; i < credits.length; i++) {
			creditsText[i] = new DrawableText(creditsRect.getX() + creditsRect.getWidth() * 0.05, textY, credits[i], titleFont, Color.white, HorizontalAlign.LEFT, VerticalAlign.CENTER);
			creditsText[i].setMaxWidth(creditsRect.getWidth() * 0.9);
			textY += 0.07; // Increase the y coordinate each time
		}		
	
		// Add the components to the credits screen and add the credits screen to the main menu
		creditsScreen.add(creditsRect);
		creditsScreen.add(creditsTitleText);
		for (DrawableText t: creditsText) {
			creditsScreen.add(t);
		}
		creditsScreen.add(xButton);
		
		add(creditsScreen);
		creditsScreen.hide();

		// Start playing the menu music
		menuMusic = Util.getAudioClip(getClass(), "menuLoop.wav");
		menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	/** Method Name: dispose()
	 * @Author Colin Toft
	 * @Date January 14th, 2020
	 * @Modified N/A
	 * @Description Overrides the Scene.dispose() method which is called when a Scene is about to be closed, stops the menu music.
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: Clip
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void dispose() {
		super.dispose();
		menuMusic.stop();
	}
	
	/** Method Name: update()
	 * @Author Colin Toft
	 * @Date December 21st, 2019
	 * @Modified December 22nd & 24th, 2019
	 * @Description Overrides Scene.update() and updates the switching of the current displayed level
	 * @Parameters
	 *      - double dt: The time that has elapsed since the last time update() was called
	 * @Returns N/A
	 * Data Type: boolean, double, Panel
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void update(double dt) {
		// Update the switching animation if necessary
		if (isSwitching) {
			// If the panels are no longer moving, they are done switching so stop the animation
			if (Math.abs(panel1.getX() - panelStartX) < 0.001 && Math.abs(velocity) < 0.001) {
				isSwitching = false;
				panel1.setX(panelStartX);
			}
			
			switch (switchDirection) {
			case LEFT:
				// Adjust the velocity of the panels based on how far they are from the center
				velocity += bounceFactor * (panelStartX - panel1.getX()) * dt;
				velocity *= Math.pow(bounceDecay, 50 * dt); // Use the bounce decay variable to make the velocity closer to 0 
				
				// Move the panels
				panel1.moveRight(velocity * dt);
				panel2.moveRight(velocity * dt);
				break;
				
			case RIGHT:
				// Adjust the velocity of the panels based on how far they are from the center
				velocity -= bounceFactor * (panelStartX - panel1.getX()) * dt;
				velocity *= Math.pow(bounceDecay, 50 * dt); // Use the bounce decay variable to make the velocity closer to 0 
				
				// Move the panels
				panel1.moveLeft(velocity * dt);
				panel2.moveLeft(velocity * dt);
				break;
				
			default:
				break;
			}
		}
	}
	
	/** Method Name: keyPressed()
	 * @Author Colin Toft
	 * @Date December 22nd, 2019
	 * @Modified December 26th, 2020
	 * @Description Overrides Scene.keyPressed() and handles key presses during the menu
	 * @Parameters
	 *      - KeyEvent e: the event containing data about the key press event
	 * @Returns N/A
	 * Data Type: KeyEvent, int, Game, Direction
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			// When the user presses escape, close the credits/help screen if they are showing, otherwise exit the game
			if (helpScreen.isVisible() || creditsScreen.isVisible()) {
				helpScreen.hide();
				creditsScreen.hide();
			} else {
				game.exit();
			}
		// If the user presses an arrow key, switch the menu in that direction
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Direction direction = e.getKeyCode() == KeyEvent.VK_LEFT ? Direction.LEFT : Direction.RIGHT;
			switchLevel(direction);
		// If they press enter, start the current level
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			startLevel(currentLevel);
		}
	}
	
	/** Method Name: switchLevel()
	 * @Author Colin Toft
	 * @Date December 26th, 2019
	 * @Modified N/A
	 * @Description Begins switching the currently displayed level
	 * @Parameters
	 *      - Direction direction: The direction to switch in (view the level to the left or right)
	 * @Returns N/A
	 * Data Type: Direction, ShapeSprint, int, Panel, DrawableOutlinedText, DrawableProgressBar
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	private void switchLevel(Direction direction) {
		ShapeSprint ss = (ShapeSprint) game;

		// Update the current level based on the switch direction
		if (direction == Direction.RIGHT) {
			currentLevel = (currentLevel + 1) % ss.levels.length;
		} else {
			currentLevel = (currentLevel - 1 + ss.levels.length) % ss.levels.length;
		}
		
		// Update the text and progress bars to reflect the new level that is being displayed
		panel2.setX(panel1.getX()); // Make rect2 start where rect currently is (at the center of the screen), it will then move off to the left
		levelText2.setText(levelText1.getText());
		normalProgressBar2.setValue(normalProgressBar1.getValue());
		normalPercentageText2.setText(Util.toPercentageString(normalProgressBar2.getValue()));
		practiceProgressBar2.setValue(practiceProgressBar1.getValue());
		practicePercentageText2.setText(Util.toPercentageString(practiceProgressBar2.getValue()));
		
		// Update the text and progress bars to reflect the new level that is being displayed
		panel1.moveRight(direction == Direction.RIGHT ? 1 : -1); // Make rect start one screen width to the side (the side depends on the direction), it will then slide onto the screen
		levelText1.setText(ss.levels[currentLevel].name);
		normalProgressBar1.setValue(ss.levels[currentLevel].normalProgress);
		normalPercentageText1.setText(Util.toPercentageString(normalProgressBar1.getValue()));
		practiceProgressBar1.setValue(ss.levels[currentLevel].practiceProgress);
		practicePercentageText1.setText(Util.toPercentageString(practiceProgressBar1.getValue()));
		
		setBackground(ss.levels[currentLevel].backgroundColor); // Set the background color to the color of the new level
		// Store information about switching levels
		isSwitching = true;
		switchDirection = direction;
	}
	
	/** Method Name: startLevel()
	 * @Author Colin Toft
	 * @Date December 26th, 2019
	 * @Modified N/A
	 * @Description Start playing the specified level
	 * @Parameters
	 *      - int level: The index of the level that should be played
	 * @Returns N/A
	 * Data Type: Clip, ShapeSprint, PlayLevel, Level
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	private void startLevel(int level) {
		// Stop the menu music and play the start level sound effect
		menuMusic.stop();
		Clip startLevel = Util.getAudioClip(getClass(), "startLevel.wav");
		startLevel.start();
		
		// Change the scene to the PlayLevel scene
		ShapeSprint ss = (ShapeSprint) game;
		ss.setScene(new PlayLevel(ss.levels[level]));
	}
	
}
