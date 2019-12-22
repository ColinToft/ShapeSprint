package xyz.colintoft.shapesprint.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import xyz.colintoft.cgraphics.*;

/**
***********************************************
@Author Colin Toft
@Date December 21st, 2019
@Modified N/A
@Description 
***********************************************
*/
@SuppressWarnings("serial")
public class MainMenu extends Scene {
	
	Font titleFont = Util.loadFontFromFile(getClass(), "Pusab", 50);
	
	public void init() {
		setBackground(Color.red);
		add(new DrawableRoundedRect(0.2, 0.2, 0.6, 0.3, new Color(0, 0, 0, 70)));
		add(new JLabel("Hi"), 0.5, 0.5, 0.1, 0.1);
		//add(new DrawableEllipse(5, 245, 50, 50, Color.red));
	}
	
	public void update() {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			game.exit();
		}
	}
	
	
}
