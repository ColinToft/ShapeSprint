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
	
	private Panel panel1; // Main panel
	private Panel panel2; // Secondary panel (used during transitions)
	
	private RoundedRectangle rect1; // Main rectangle
	private RoundedRectangle rect2; // Secondary rectangle (used during transitions)
	
	private Text levelName1; // Main level text
	private Text levelName2; // Secondary level text (used for transitions)
	
	private boolean isSwitching = false;
	private Direction switchDirection = Direction.NONE;
	private double velocity = 7;
	private double bounceFactor = 150;
	private double bounceDecay = 0.82;
	
	private final double panelWidth = 0.6;
	private final double panelStartX = (1 - panelWidth) * 0.5;
	
	public void init() {
		ShapeSprint ss = (ShapeSprint) game;
		Font titleFont = Util.loadFontFromFile(getClass(), "Pusab.ttf", 100);
		
		setBackground(ss.levelColors[currentLevel]);
		
		panel1 = new Panel(panelStartX, 0.2, panelWidth, 0.6);
		rect1 = new RoundedRectangle(0, 0, 1, 0.4, new Color(0, 0, 0, 70));
		levelName1 = new OutlinedText(rect1.getCenterX(), rect1.getCenterY(), ss.levelNames[currentLevel], titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		panel1.add(rect1);
		panel1.add(levelName1);
		
		// rect2 and levelName 2 start off the screen
		panel2 = new Panel(panelStartX + 1, 0.2, panelWidth, 0.6);
		rect2 = new RoundedRectangle(0, 0, 1, 0.4, new Color(0, 0, 0, 70));
		levelName2 = new OutlinedText(rect2.getCenterX(), rect2.getCenterY(), ss.levelNames[currentLevel], titleFont, Color.white, Color.black, HorizontalAlign.CENTER, VerticalAlign.CENTER);
		panel2.add(rect2);
		panel2.add(levelName2);
		
		add(panel1);
		add(panel2);
				
		// TODO triangles
		// TODO level progress bars
		// TODO click on level or press enter
		Clip music = Util.getAudioClip(getClass(), "menuLoop.wav");
		music.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void update(double dt) {
		if (isSwitching) {
			if (Math.abs(panel1.getParentFractionX() - panelStartX) < 0.001 && Math.abs(velocity) < 0.001) {
				isSwitching = false;
				panel1.setX(panelStartX);
			}
			
			switch (switchDirection) {
			case LEFT:
				velocity += bounceFactor * (panelStartX - panel1.getParentFractionX()) * dt;
				velocity *= bounceDecay;
				
				panel1.moveRight(velocity * dt);
				panel2.moveRight(velocity * dt);
				break;
				
			case RIGHT:
				velocity -= bounceFactor * (panelStartX - panel1.getParentFractionX()) * dt;
				velocity *= bounceDecay;
				
				panel1.moveLeft(velocity * dt);
				panel2.moveLeft(velocity * dt);
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
			
			panel2.setX(panel1.getParentFractionX()); // Make rect2 start where rect1 currently is (at the center of the screen), it will then move off to the left
			levelName2.setText(levelName1.getText()); 
			
			panel1.moveRight(switchDirection == Direction.RIGHT ? 1 : -1); // Make rect1 start one screen width to the side (the side depends on the direction), it will then slide onto the screen
			levelName1.setText(ss.levelNames[currentLevel]);
			
			setBackground(ss.levelColors[currentLevel]);
			isSwitching = true;
		}
	}
	
	
}
