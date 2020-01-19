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
@Modified December 22nd
@Description The main menu scene that allows the user to select a level to play.
***********************************************
*/
public class MainMenu extends Scene {
	
	private int currentLevel; // The level that is being displayed

	private Panel panel1, panel2;
	private DrawableOutlinedText levelText1, levelText2;
	private DrawableProgressBar normalProgressBar1, normalProgressBar2;
	private DrawableOutlinedText normalPercentageText1, normalPercentageText2;
	private DrawableProgressBar practiceProgressBar1, practiceProgressBar2;
	private DrawableOutlinedText practicePercentageText1, practicePercentageText2;
	
	private Panel helpScreen;
	private String tutorialText[] = 
		{"In Shape Sprint, the goal is to avoid oncoming",
		 "obstacles in order to complete a level.     ",
		 "Triangles will kill you but squares are safe",
		 "to jump on! Levels are synchronized with the",
		 "music so use it to help you time your jumps.",
		 "Click on a level to start!           "};
	
	private String credits[] = 
		{"Music:                         ",
		"Dimensional Vortex - Owen Crowe (A_D1nGu5)",
		"Spatial Plane - Owen Crowe (A_D1nGu5)",
		"Temporal Nebula - Owen Crowe (A_D1nGu5)",
		"Practice Mode Song - Colin Toft      ",
		"",
		"Menu Song & original Geometry Dash  ",
		"Game - RobTop Games                   "};
	
	private Panel creditsScreen;
	
	private Clip menuMusic;
	
	private boolean isSwitching = false;
	private Direction switchDirection = Direction.NONE;
	private double velocity = 0;
	private double bounceFactor = 150;
	private double bounceDecay = 0.8;
	
	private final double panelWidth = 0.6;
	private final double panelStartX = (1 - panelWidth) * 0.5;

	
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
		this.currentLevel = currentLevel;
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
		this(0);
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
		Font titleFont = Util.loadFontFromFile(getClass(), "Pusab.ttf", 100);
		
		setBackground(ss.levels[currentLevel].backgroundColor);
		
		panel1 = new Panel(panelStartX, 0.15, panelWidth, 0.7);
		DrawableRoundedRectangle rect = new DrawableRoundedRectangle(0, 0, 1, 0.4, 0.07, 0.125, new Color(0, 0, 0, 70)) {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				startLevel(currentLevel);
			}
		};
		levelText1 = new DrawableOutlinedText(rect.getCenterX(), rect.getCenterY(), ss.levels[currentLevel].name, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		levelText1.setMaxWidth(rect.getWidth() * 0.9);
		
		normalProgressBar1 = new DrawableProgressBar(0, 0.6, 1, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.GREEN, new Color(0, 0, 0, 70));
		normalProgressBar1.setValue(ss.levels[currentLevel].normalProgress);
		DrawableOutlinedText normalModeText1 = new DrawableOutlinedText(0.5, normalProgressBar1.getY() - 0.01, "Normal Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		normalModeText1.setMaxHeight(normalProgressBar1.getHeight() * 0.7);
		normalPercentageText1 = new DrawableOutlinedText(normalProgressBar1.getCenterX(), normalProgressBar1.getCenterY(),
				Util.toPercentageString(normalProgressBar1.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		normalPercentageText1.setMaxHeight(normalProgressBar1.getHeight() * 0.7);
		
		practiceProgressBar1 = new DrawableProgressBar(0, 0.85, 1, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.CYAN, new Color(0, 0, 0, 70));
		practiceProgressBar1.setValue(ss.levels[currentLevel].practiceProgress);
		DrawableOutlinedText practiceModeText1 = new DrawableOutlinedText(0.5, practiceProgressBar1.getY() - 0.01, "Practice Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		practiceModeText1.setMaxHeight(practiceProgressBar1.getHeight() * 0.7);
		practicePercentageText1 = new DrawableOutlinedText(practiceProgressBar1.getCenterX(), practiceProgressBar1.getCenterY(),
				Util.toPercentageString(practiceProgressBar1.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		practicePercentageText1.setMaxHeight(practiceProgressBar1.getHeight() * 0.7);
		
		panel1.add(rect);
		panel1.add(levelText1);
		panel1.add(normalProgressBar1);
		panel1.add(normalModeText1);
		panel1.add(normalPercentageText1);
		panel1.add(practiceProgressBar1);
		panel1.add(practiceModeText1);
		panel1.add(practicePercentageText1);
		
		add(panel1);
		
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
		
				
		// Triangles
		double[] xCoords1 = {0.02, 0.06, 0.06};
		double[] yCoords = {0.5, 0.4, 0.6};
		double[] xCoords2 = {0.98, 0.94, 0.94};
		DrawableShape leftTriangle = new DrawableShape(xCoords1, yCoords, Color.black, 3f, Color.white) {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				switchLevel(Direction.LEFT);
			}
		};
		DrawableShape rightTriangle = new DrawableShape(xCoords2, yCoords, Color.black, 3f, Color.white) {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				switchLevel(Direction.RIGHT);
			}
		};
		add(leftTriangle);
		add(rightTriangle);
		
		double buttonWidth = 0.1;
		double buttonHeight = parentWidthFractionToParentHeightFraction(buttonWidth);
		
		Sprite helpButton = new Sprite(1 - buttonWidth, 1 - buttonHeight, buttonWidth, buttonHeight, "menuItems/helpButton.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				helpScreen.show();
			}
		};
		add(helpButton);
		
		Sprite creditsButton = new Sprite(0, 1 - buttonHeight, buttonWidth * 1.1, buttonHeight, "menuItems/creditsButton.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				creditsScreen.show();
			}
		};
		add(creditsButton);
		
		helpScreen = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle helpRect = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, Color.BLACK);
		DrawableOutlinedText helpTitleText = new DrawableOutlinedText(helpRect.getCenterX(), 0.15, "Help", titleFont.deriveFont(200f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		helpTitleText.setMaxHeight(helpRect.getHeight() * 0.15);
		double textY = 0.35;
		DrawableText[] helpText = new DrawableText[tutorialText.length];
		for (int i = 0; i < tutorialText.length; i++) {
			helpText[i] = new DrawableText(helpRect.getX() + helpRect.getWidth() * 0.05, textY, tutorialText[i], titleFont, Color.white, HorizontalAlign.LEFT, VerticalAlign.CENTER);
			helpText[i].setMaxWidth(helpRect.getWidth() * 0.9);
			textY += 0.07;
		}		
	
		Sprite xButton = new Sprite(1 - buttonWidth, 0, buttonWidth, buttonHeight, "menuItems/xButton.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				helpScreen.hide();
				creditsScreen.hide();
			}
		};
		
		helpScreen.add(helpRect);
		helpScreen.add(helpTitleText);
		for (DrawableText t: helpText) {
			helpScreen.add(t);
		}
		helpScreen.add(xButton);
		
		add(helpScreen);
		helpScreen.hide();
		
		creditsScreen = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle creditsRect = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, Color.BLACK);
		DrawableOutlinedText creditsTitleText = new DrawableOutlinedText(creditsRect.getCenterX(), 0.15, "Credits", titleFont.deriveFont(200f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		creditsTitleText.setMaxHeight(helpRect.getHeight() * 0.15);
		textY = 0.35;
		DrawableText[] creditsText = new DrawableText[credits.length];
		for (int i = 0; i < credits.length; i++) {
			creditsText[i] = new DrawableText(creditsRect.getX() + creditsRect.getWidth() * 0.05, textY, credits[i], titleFont, Color.white, HorizontalAlign.LEFT, VerticalAlign.CENTER);
			creditsText[i].setMaxWidth(creditsRect.getWidth() * 0.9);
			textY += 0.07;
		}		
	
		creditsScreen.add(creditsRect);
		creditsScreen.add(creditsTitleText);
		for (DrawableText t: creditsText) {
			creditsScreen.add(t);
		}
		creditsScreen.add(xButton);
		
		add(creditsScreen);
		creditsScreen.hide();
		
		// TODO alignment of text being weird
		// TODO rocket collision physics??

		// TODO finish levels
		// TODO comments including CGraphics
		// TODO flow chart
		// TODO modified dates for classes
		// TODO resizing windows is weird
		
		// TODO dots & pads
		// TODO upsideDown portals
		// TODO editing checkpoints?
		// TODO attempts and jump count when beating level

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
		if (isSwitching) {
			if (Math.abs(panel1.getX() - panelStartX) < 0.001 && Math.abs(velocity) < 0.001) {
				isSwitching = false;
				panel1.setX(panelStartX);
			}
			
			switch (switchDirection) {
			case LEFT:
				velocity += bounceFactor * (panelStartX - panel1.getX()) * dt;
				velocity *= Math.pow(bounceDecay, 50 * dt);
				
				panel1.moveRight(velocity * dt);
				panel2.moveRight(velocity * dt);
				break;
				
			case RIGHT:
				velocity -= bounceFactor * (panelStartX - panel1.getX()) * dt;
				velocity *= Math.pow(bounceDecay, 50 * dt);
				
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
			if (helpScreen.isVisible() || creditsScreen.isVisible()) {
				helpScreen.hide();
				creditsScreen.hide();
			} else {
				game.exit();
			}
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Direction direction = e.getKeyCode() == KeyEvent.VK_LEFT ? Direction.LEFT : Direction.RIGHT;
			switchLevel(direction);
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

		if (direction == Direction.RIGHT) {
			currentLevel = (currentLevel + 1) % ss.levels.length;
		} else {
			currentLevel = (currentLevel - 1 + ss.levels.length) % ss.levels.length;
		}
		
		panel2.setX(panel1.getX()); // Make rect2 start where rect currently is (at the center of the screen), it will then move off to the left
		levelText2.setText(levelText1.getText());
		normalProgressBar2.setValue(normalProgressBar1.getValue());
		normalPercentageText2.setText(Util.toPercentageString(normalProgressBar2.getValue()));
		practiceProgressBar2.setValue(practiceProgressBar1.getValue());
		practicePercentageText2.setText(Util.toPercentageString(practiceProgressBar2.getValue()));
		
		panel1.moveRight(direction == Direction.RIGHT ? 1 : -1); // Make rect start one screen width to the side (the side depends on the direction), it will then slide onto the screen
		levelText1.setText(ss.levels[currentLevel].name);
		normalProgressBar1.setValue(ss.levels[currentLevel].normalProgress);
		normalPercentageText1.setText(Util.toPercentageString(normalProgressBar1.getValue()));
		practiceProgressBar1.setValue(ss.levels[currentLevel].practiceProgress);
		practicePercentageText1.setText(Util.toPercentageString(practiceProgressBar1.getValue()));
		
		setBackground(ss.levels[currentLevel].backgroundColor);
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
		menuMusic.stop();
		Clip startLevel = Util.getAudioClip(getClass(), "startLevel.wav");
		startLevel.start();
		
		ShapeSprint ss = (ShapeSprint) game;

		ss.setScene(new PlayLevel(ss.levels[level]));
	}
	
}
