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

// Dec 27
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
	
	// Dec 27 mod 30
	public PlayLevel(Level level) {
		super();
		setBackground(new Color(0, 0, 0, 0));
		this.level = level;
	}
	
	// Dec 27 mod 30, 7, 8, 9, 10, 14
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
	
	@Override
	// 7 mod 8, 16
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
	
	// Dec 30 mod 8, 14
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

	// 7 mod 8, 9
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
	
	// 8 mod 9, 17
	public void changeMode() {
		pauseMenu.hide();
		resumeGame();
		levelView.changeMode();
		((ShapeSprint) game).hasUsedPracticeMode = true;
	}
	
	// 10
	private void saveLevelProgress() {
		if (levelView.isPracticeMode()) {
			level.updatePracticeProgress(levelView.getPlayerProgress());
		} else {
			level.updateNormalProgress(levelView.getPlayerProgress());
		}
	}
	
	// 8
	public void exitToMenu() {
		saveLevelProgress();
		
		levelView.exitingToMenu();
		ShapeSprint ss = ((ShapeSprint) game);
		for (int i = 0; i < ss.levels.length; i++) {
			if (ss.levels[i].name.equals(level.name)) {
				game.setScene(new MainMenu(i));
				return;
			}
		}
		game.setScene(new MainMenu());
	}

	// 14
	public void showWinScreen() {
		if (levelView.isPracticeMode()) {
			levelCompleteText.setText("Practice Complete!");
		} else {
			levelCompleteText.setText("Level Complete!");
		}
		winScreen.show();
	}
}
