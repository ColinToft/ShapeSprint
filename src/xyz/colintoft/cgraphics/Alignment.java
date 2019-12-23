package xyz.colintoft.cgraphics;

import java.awt.BorderLayout;

public enum Alignment {
	TOP, BOTTOM, LEFT, CENTER, RIGHT;
	
	public String toBorderLayout() {
		switch (this) {
			case BOTTOM:
				return BorderLayout.SOUTH;
			case CENTER:
				return BorderLayout.CENTER;
			case LEFT:
				return BorderLayout.WEST;
			case RIGHT:
				return BorderLayout.EAST;
			case TOP:
				return BorderLayout.NORTH;
			default:
				return null;
		}
	}
}
