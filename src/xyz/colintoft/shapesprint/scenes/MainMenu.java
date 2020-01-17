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
import xyz.colintoft.shapesprint.ShapeSprint;

/**
***********************************************
@Author Colin Toft
@Date December 21st, 2019
@Modified December 22nd, 2019
@Description 
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
	
	private Clip menuMusic;
	
	private boolean isSwitching = false;
	private Direction switchDirection = Direction.NONE;
	private double velocity = 0;
	private double bounceFactor = 150;
	private double bounceDecay = 0.8;
	
	private final double panelWidth = 0.6;
	private final double panelStartX = (1 - panelWidth) * 0.5;
	
	public MainMenu(int currentLevel) {
		super();
		this.currentLevel = currentLevel;
	}
	
	public MainMenu() {
		this(0);
	}
	
	// Dec 21
	// Mod 22, 24, 26, 27
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
		
		// TODO rocket collision physics??
		// TODO offsets affect click
		// TODO more instructions
		
		// TODO collision with upside down triangle
		// TODO finish levels
		// TODO comments and method headers
		// TODO flow chart
		
		// TODO dots & pads
		// TODO song credits
		// TODO editing checkpoints?
		// TODO endless mode?
		// TODO attemps and jump count when beating level

		menuMusic = Util.getAudioClip(getClass(), "menuLoop.wav");
		menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	@Override
	// 14
	public void dispose() {
		super.dispose();
		menuMusic.stop();
	}
	
	// Dec 21
	// Mod 22, 24
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
	
	// Dec 22
	// Mod 26
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			game.exit();
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Direction direction = e.getKeyCode() == KeyEvent.VK_LEFT ? Direction.LEFT : Direction.RIGHT;
			switchLevel(direction);
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			startLevel(currentLevel);
		}
	}
	
	// Dec 26
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
	
	private void startLevel(int level) {
		menuMusic.stop();
		Clip startLevel = Util.getAudioClip(getClass(), "startLevel.wav");
		startLevel.start();
		
		ShapeSprint ss = (ShapeSprint) game;

		ss.setScene(new PlayLevel(ss.levels[level]));
	}
	
}
