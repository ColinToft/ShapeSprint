package xyz.colintoft.shapesprint.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.sound.sampled.Clip;

import xyz.colintoft.cgraphics.*;
import xyz.colintoft.shapesprint.ShapeSprint;

/**
***********************************************
@Author Colin Toft
@Date December 21st, 2019
@Modified December 22nd, 2019
@Description 
***********************************************
*/
@SuppressWarnings("serial")
public class MainMenu extends Scene {
	
	private int currentLevel = 0; // The level that is being displayed
	
	private RoundedRectangle rect1; // Main rectangle
	private RoundedRectangle rect2; // Secondary rectangle (used during transitions)
	
	private Text levelName1; // Main level text
	private Text levelName2; // Secondary level text (used for transitions)
	
	private boolean isSwitching = false;
	private Direction switchDirection = Direction.NONE;
	private double velocity = 7;
	private double bounceFactor = 150;
	private double bounceDecay = 0.82;
	
	private final double rectWidth = 0.6;
	private final double rectStartX = (1 - rectWidth) * 0.5;
	
	public void init() {
		ShapeSprint ss = (ShapeSprint) game;
		Font titleFont = Util.loadFontFromFile(getClass(), "Pusab.ttf", 100);
		
		setBackground(ss.levelColors[currentLevel]);
		rect1 = new RoundedRectangle(rectStartX, 0.2, rectWidth, 0.3, new Color(0, 0, 0, 70));
		levelName1 = new OutlinedText(rect1.getCenterX(), rect1.getCenterY(), ss.levelNames[currentLevel], titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		
		// rect2 and levelName 2 start off the screen
		rect2 = new RoundedRectangle(rectStartX + 1, 0.2, rectWidth, 0.3, new Color(0, 0, 0, 70));
		levelName2 = new OutlinedText(rect2.getCenterX(), rect2.getCenterY(), ss.levelNames[currentLevel], titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		
		add(rect1);
		add(levelName1);
		add(rect2);
		add(levelName2);
				
		// TODO triangles
		// TODO level progress bars
		// TODO click on level or press enter
		Clip music = Util.getAudioClip(getClass(), "menuLoop.wav");
		music.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void update(double dt) {
		if (isSwitching) {
			if (Math.abs(rect1.getX() - rectStartX) < 0.001 && Math.abs(velocity) < 0.001) {
				isSwitching = false;
				rect1.setX(rectStartX);
				levelName1.setX(rect1.getCenterX());
			}
			
			switch (switchDirection) {
			case LEFT:
				velocity += bounceFactor * (rectStartX - rect1.getX()) * dt;
				velocity *= bounceDecay;
				
				rect1.moveRight(velocity * dt);
				rect2.moveRight(velocity * dt);
				levelName1.moveRight(velocity * dt);
				levelName2.moveRight(velocity * dt);
				break;
				
			case RIGHT:
				velocity -= bounceFactor * (rectStartX - rect1.getX()) * dt;
				velocity *= bounceDecay;
				
				rect1.moveLeft(velocity * dt);
				rect2.moveLeft(velocity * dt);
				levelName1.moveLeft(velocity * dt);
				levelName2.moveLeft(velocity * dt);
				break;
				
			default:
				break;
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			game.exit();
		}
		
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			ShapeSprint ss = (ShapeSprint) game;
			switchDirection = e.getKeyCode() == KeyEvent.VK_LEFT ? Direction.LEFT : Direction.RIGHT;
			
			if (switchDirection == Direction.RIGHT) {
				currentLevel = (currentLevel + 1) % ss.levelNames.length;
			} else {
				currentLevel = (currentLevel - 1 + ss.levelNames.length) % ss.levelNames.length;
			}
			
			rect2.setX(rect1.getX()); // Make rect2 start where rect1 currently is (at the center of the screen), it will then move off to the left
			levelName2.setText(levelName1.getText()); 
			levelName2.setX(rect1.getCenterX());
			
			rect1.moveRight(switchDirection == Direction.RIGHT ? 1 : -1); // Make rect1 start one screen width to the side (the side depends on the direction), it will then slide onto the screen
			levelName1.setText(ss.levelNames[currentLevel]);
			levelName1.setX(rect1.getCenterX());
			
			setBackground(ss.levelColors[currentLevel]);
			isSwitching = true;
		}
	}
	
	
}
