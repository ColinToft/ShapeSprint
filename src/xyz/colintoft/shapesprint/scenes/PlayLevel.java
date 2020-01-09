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
	
	// Dec 27 mod 30
	public PlayLevel(Level level) {
		super();
		setBackground(new Color(0, 0, 0, 0));
		this.level = level;
	}
	
	// Dec 27 mod 30, 7, 8
	public void init() {
		level.load();
		
		levelView = new LevelView(level);
		add(levelView);
		
		Font titleFont = Util.loadFontFromFile(getClass(), "Pusab.ttf", 100);
		attemptText = new DrawableOutlinedText(attemptTextStartX, 0.25, "Attempt " + attemptNumber, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		add(attemptText);
		
		progressBar = new DrawableProgressBar(0.5 - progressBarWidth * 0.5, 0.02, progressBarWidth, progressBarHeight, progressBarHeight * 0.65, progressBarHeight, Color.WHITE, 2f, Color.red, new Color(0, 0, 0, 0));
		add(progressBar);
		
		percentageText = new DrawableOutlinedText(progressBar.getX() + progressBar.getWidth() + 0.002, progressBar.getCenterY(), "0%", titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.LEFT, VerticalAlign.CENTER);
		percentageText.setMaxHeight(progressBarHeight);
		add(percentageText);
		
		pauseMenu = new Panel(0.03, 0.03, 0.94, 0.94);
		DrawableRoundedRectangle rect = new DrawableRoundedRectangle(0, 0, 1, 1, 0.07, 0.125, new Color(0, 0, 0, 180));
		DrawableOutlinedText levelText = new DrawableOutlinedText(rect.getCenterX(), 0.15, level.name, titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		levelText.setMaxWidth(rect.getWidth() * 0.9);
		
		normalProgressBar = new DrawableProgressBar(0.1, 0.3, 0.8, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.GREEN, new Color(0, 0, 0, 70));
		normalProgressBar.setValue(level.normalProgress);
		DrawableOutlinedText normalModeText = new DrawableOutlinedText(0.5, normalProgressBar.getY() - 0.01, "Normal Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		normalPercentageText = new DrawableOutlinedText(normalProgressBar.getCenterX(), normalProgressBar.getCenterY(),
				Util.toPercentageString(normalProgressBar.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		normalPercentageText.setMaxHeight(normalProgressBar.getHeight() * 0.9);
		
		practiceProgressBar = new DrawableProgressBar(0.1, 0.55, 0.8, 0.1, 0.05, 0.16, Color.BLACK, 2f, Color.CYAN, new Color(0, 0, 0, 70));
		practiceProgressBar.setValue(level.practiceProgress);
		DrawableOutlinedText practiceModeText = new DrawableOutlinedText(0.5, practiceProgressBar.getY() - 0.01, "Practice Mode", titleFont.deriveFont(75f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.BOTTOM);
		practicePercentageText = new DrawableOutlinedText(practiceProgressBar.getCenterX(), practiceProgressBar.getCenterY(),
				Util.toPercentageString(practiceProgressBar.getValue()), titleFont.deriveFont(60f), Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		
		double progressBarBottom = practiceProgressBar.getY() + practiceProgressBar.getHeight();
		double buttonHeight = parentWidthFractionToParentHeightFraction(buttonWidth);
		
		Sprite practiceModeButton = new Sprite(0.25, (1 - progressBarBottom - buttonHeight) / 2 + progressBarBottom, buttonWidth, buttonHeight, "menuItems/practiceMode.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				startPracticeMode();
			}
		};
		
		Sprite playButton = new Sprite(0.5 * (1 - buttonWidth * 1.5), (1 - progressBarBottom - buttonHeight * 1.5) / 2 + progressBarBottom, buttonWidth * 1.5, buttonHeight * 1.5, "menuItems/resume.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				startPracticeMode();
			}
		};
		
		Sprite menuButton = new Sprite(1 - practiceModeButton.getX() - buttonWidth, (1 - progressBarBottom - buttonHeight) / 2 + progressBarBottom, buttonWidth, buttonHeight, "menuItems/menu.png") {
			@Override
			public void onMouseReleased(double x, double y, int button) {
				exitToMenu();
			}
		};
		
		pauseMenu.add(rect);
		pauseMenu.add(levelText);
		pauseMenu.add(normalProgressBar);
		pauseMenu.add(normalModeText);
		pauseMenu.add(normalPercentageText);
		pauseMenu.add(practiceProgressBar);
		pauseMenu.add(practiceModeText);
		pauseMenu.add(practicePercentageText);
		pauseMenu.add(practiceModeButton);
		pauseMenu.add(menuButton);
		
		add(pauseMenu);
		pauseMenu.hide();
	}
	
	@Override
	// 7 mod 8
	public void update(double dt) {
		super.update(dt);
		attemptText.moveLeft(dt * levelView.getScrollSpeed());
		progressBar.setValue(levelView.getPlayerProgress());
		percentageText.setText(Util.toPercentageString(levelView.getPlayerProgress()));
	}
	
	// Dec 30 mod 8
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			togglePaused();
			if (isPaused()) {
				pauseMenu.show();
			} else {
				pauseMenu.hide();
			}
		}
	}

	// 7 mod 8
	public void restart() {
		attemptNumber++;
		attemptText.setText("Attempt " + attemptNumber);
		attemptText.setX(attemptTextStartX);
		
		level.updateNormalProgress(levelView.getPlayerProgress());
	}
	
	// 8
	public void startPracticeMode() {
		
	}
	
	// 8
	public void exitToMenu() {
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
}
