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
@Modified
@Description The scene where the user can play a level. Includes the level view, pause menu, win screen, and help messages.
***********************************************
*/
public class PlayLevel extends Scene {
	
	private Level level;
	private LevelView levelView;
	
	private int attemptNumber = 1;
	private DrawableOutlinedText attemptText;
	private final double attemptTextStartX = 0.9;
	
	private DrawableOutlinedText helpText;
	public boolean needsJumpHelp = true;
	private final String jumpHelpMessage = "Click or tap to jump over obstacles";
	private final String triangleHelpMessage = "Hold the mouse or space bar to fly";
	
	private DrawableOutlinedText pauseMenuText;

	private DrawableProgressBar progressBar;
	private final double progressBarWidth = 0.3;
	private final double progressBarHeight = 0.03;
	
	private final double buttonWidth = 0.1;
	
	private DrawableOutlinedText percentageText;
	
	private Panel pauseMenu;
	private DrawableProgressBar normalProgressBar;
	private DrawableOutlinedText normalPercentageText;
	private DrawableProgressBar practiceProgressBar;
	private DrawableOutlinedText practicePercentageText;
	private Sprite practiceTip;
	
	private Panel winScreen;
	private DrawableOutlinedText levelCompleteText;
	
	private Sprite changeModeButton;

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
		setBackground(new Color(0, 0, 0, 0));
		this.level = level;
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
		level.load();
		
		levelView = new LevelView(level);
		add(levelView);
		
		Font titleFont = Util.loadFontFromFile(getClass(), "Pusab.ttf", 100);
		attemptText = new DrawableOutlinedText(attemptTextStartX, 0.25, "Attempt " + attemptNumber, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		attemptText.setMaxHeight(0.085);
		add(attemptText);
		
		helpText = new DrawableOutlinedText(0.5, 0.4, jumpHelpMessage, titleFont.deriveFont(100f), Color.white, Color.black, 1f, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		helpText.setMaxWidth(0.75);
		needsJumpHelp = ((ShapeSprint) game).isFirstTime();
		if (!needsJumpHelp) {
			helpText.hide();
		}
		add(helpText);
		
		pauseMenuText = new DrawableOutlinedText(0.9, 0.01, "Press escape for more options", titleFont.deriveFont(100f), Color.white, Color.black, 1f, HorizontalAlign.RIGHT, VerticalAlign.TOP);
		pauseMenuText.setMaxWidth(0.5);
		add(pauseMenuText);
		
		progressBar = new DrawableProgressBar(0.5 * (1 - progressBarWidth), 0.02, progressBarWidth, progressBarHeight, progressBarHeight * 0.65, progressBarHeight, Color.WHITE, 2f, Color.red, new Color(0, 0, 0, 0));
		add(progressBar);
				
		percentageText = new DrawableOutlinedText(progressBar.getX() + progressBar.getWidth(), progressBar.getCenterY(), "0%", titleFont.deriveFont(60f), Color.white, Color.black, 1f, HorizontalAlign.LEFT, VerticalAlign.CENTER);
		percentageText.setMaxHeight(progressBarHeight);
		add(percentageText);
		
		pauseMenu = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle rect = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, new Color(0, 0, 0, 180));
		DrawableOutlinedText levelText = new DrawableOutlinedText(rect.getCenterX(), 0.15, level.name, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		levelText.setMaxWidth(rect.getWidth() * 0.9);
		
		normalProgressBar = new DrawableProgressBar(0.1, 0.3, 0.8, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.GREEN, new Color(0, 0, 0, 70));
		normalProgressBar.setValue(level.normalProgress);
		DrawableOutlinedText normalModeText = new DrawableOutlinedText(0.5, normalProgressBar.getY() - 0.01, "Normal Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		normalModeText.setMaxHeight(normalProgressBar.getHeight() * 0.7);
		normalPercentageText = new DrawableOutlinedText(normalProgressBar.getCenterX(), normalProgressBar.getCenterY(),
				Util.toPercentageString(normalProgressBar.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		normalPercentageText.setMaxHeight(normalProgressBar.getHeight() * 0.7);
		
		practiceProgressBar = new DrawableProgressBar(0.1, 0.55, 0.8, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.CYAN, new Color(0, 0, 0, 70));
		practiceProgressBar.setValue(level.practiceProgress);
		DrawableOutlinedText practiceModeText = new DrawableOutlinedText(0.5, practiceProgressBar.getY() - 0.01, "Practice Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		practiceModeText.setMaxHeight(practiceProgressBar.getHeight() * 0.7);
		practicePercentageText = new DrawableOutlinedText(practiceProgressBar.getCenterX(), practiceProgressBar.getCenterY(),
				Util.toPercentageString(practiceProgressBar.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		practicePercentageText.setMaxHeight(practiceProgressBar.getHeight() * 0.7);
		
		double progressBarBottom = practiceProgressBar.getY() + practiceProgressBar.getHeight();
		double buttonHeight = parentWidthFractionToParentHeightFraction(buttonWidth);
		
		changeModeButton = new Sprite(0.25, (1 - progressBarBottom - buttonHeight) / 2 + progressBarBottom, buttonWidth, buttonHeight, "menuItems/practiceMode.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				changeMode();
			}
		};
		
		Sprite resumeButton = new Sprite(0.5 * (1 - buttonWidth * 1.5), (1 - progressBarBottom - buttonHeight * 1.5) / 2 + progressBarBottom, buttonWidth * 1.5, buttonHeight * 1.5, "menuItems/resume.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				pauseMenu.hide();
				resumeGame();
			}
		};
		
		Sprite menuButton = new Sprite(1 - changeModeButton.getX() - buttonWidth, (1 - progressBarBottom - buttonHeight) / 2 + progressBarBottom, buttonWidth, buttonHeight, "menuItems/menu.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				exitToMenu();
			}
		};
		
		practiceTip = new Sprite(changeModeButton.getX() - 0.12, changeModeButton.getY() - 0.01, 0.13, 0.1, "tips/practiceModeTip.png");
		
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
		
		winScreen = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle rect2 = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, new Color(0, 0, 0, 180));
		levelCompleteText = new DrawableOutlinedText(rect.getCenterX(), 0.15, "Level Complete!", titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		levelCompleteText.setMaxWidth(rect.getWidth() * 0.9);
		
		Sprite playAgainButton = new Sprite(0.5 * (1 - buttonWidth * 1.5), (1 - progressBarBottom - buttonHeight * 1.5) / 2 + progressBarBottom, buttonWidth * 1.5, buttonHeight * 1.5, "menuItems/playAgain.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				resumeGame();
				attemptNumber = 0;
				levelView.restartLevel();
				levelView.hasBeatLevel = false;
				winScreen.hide();
			}
		};
		
		Sprite menuButton2 = new Sprite(1 - changeModeButton.getX() - buttonWidth, (1 - progressBarBottom - buttonHeight) / 2 + progressBarBottom, buttonWidth, buttonHeight, "menuItems/menu.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				exitToMenu();
			}
		};
		
		winScreen.add(rect2);
		winScreen.add(levelCompleteText);
		winScreen.add(playAgainButton);
		winScreen.add(menuButton2);
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
		attemptText.moveLeft(dt * levelView.getScrollSpeed());
		progressBar.setValue(levelView.getPlayerProgress());
		percentageText.setText(Util.toPercentageString(levelView.getPlayerProgress()));
		
		ShapeSprint ss = (ShapeSprint) game;

		if (levelView.hasJumped && helpText.getText().equals(jumpHelpMessage) || ss.hasUsedTriangleMode && helpText.getText().equals(triangleHelpMessage)) {
			needsJumpHelp = false;
			helpText.hide();
		}
		
		if (ss.hasPausedGame) {
			pauseMenuText.hide();
		}
	
		if (levelView.isTriangleMode() && !ss.hasUsedTriangleMode) {
			helpText.setText(triangleHelpMessage);
			helpText.show();
		}
	}
	
	/** Method Name: keyPressed()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 8th & 14th, 2020
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
			if (levelView.hasBeatLevel) {
				exitToMenu();
			} else {
				togglePaused();
				if (isPaused()) {
					if (levelView.isPracticeMode()) {
						changeModeButton.setImage("menuItems/normalMode.png");
					} else {
						changeModeButton.setImage("menuItems/practiceMode.png");
					}
					pauseMenu.show();
					pauseMenuText.hide();
					if (ss.hasUsedPracticeMode) {
						practiceTip.hide();
					} else {
						practiceTip.show();
					}
				} else {
					pauseMenu.hide();
					ss.hasPausedGame = true;
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (levelView.hasBeatLevel) {
				resumeGame();
				attemptNumber = 0;
				levelView.restartLevel();
				levelView.hasBeatLevel = false;
				winScreen.hide();
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
		attemptNumber++;
		attemptText.setText("Attempt " + attemptNumber);
		attemptText.setX(attemptTextStartX);
		
		if (attemptNumber > 2 && !levelView.hasJumped) {
			helpText.setText(jumpHelpMessage);
			helpText.show();
		}
		
		saveLevelProgress();
		
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
		pauseMenu.hide();
		resumeGame();
		levelView.changeMode();
		((ShapeSprint) game).hasUsedPracticeMode = true;
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

		saveLevelProgress();
		ss.saveProgress();
		
		levelView.exitingToMenu();
		
		
		for (int i = 0; i < ss.levels.length; i++) {
			if (ss.levels[i].name.equals(level.name)) {
				game.setScene(new MainMenu(i));
				return;
			}
		}
		game.setScene(new MainMenu());
	}

	/** Method Name: showWinScreen()
	 * @Author Colin Toft
	 * @Date January 14th, 2020
	 * @Modified N/A
	 * @Description Shows the win screen (when the user successfully completes a level)
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: LevelView, DrawableOutlinedText, Panel
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void showWinScreen() {
		if (levelView.isPracticeMode()) {
			levelCompleteText.setText("Practice Complete!");
		} else {
			levelCompleteText.setText("Level Complete!");
		}
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
		saveLevelProgress();
		((ShapeSprint) game).saveProgress();
	}
}
