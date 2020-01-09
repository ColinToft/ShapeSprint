package xyz.colintoft.shapesprint;

import java.awt.image.BufferedImage;

import xyz.colintoft.cgraphics.Util;

// 30 mod 7
public enum Obstacle {
	SQUARE,
	SQUARE_TOP_1, SQUARE_BOTTOM_1, SQUARE_LEFT_1, SQUARE_RIGHT_1,
	SQUARE_TOP_3, SQUARE_BOTTOM_3, SQUARE_LEFT_3, SQUARE_RIGHT_3,
	SQUARE_TOP_LEFT, SQUARE_TOP_RIGHT, SQUARE_BOTTOM_LEFT, SQUARE_BOTTOM_RIGHT,
	SQUARE_VERTICAL, SQUARE_HORIZONTAL, SQUARE_CENTER,
	TRIANGLE;

	// 31
	public static Obstacle fromString(String string) {
		switch (string) {
		case "S": return SQUARE;
		case "ST1": return SQUARE_TOP_1;
		case "SB1": return SQUARE_BOTTOM_1;
		case "SL1": return SQUARE_LEFT_1;
		case "SR1": return SQUARE_RIGHT_1;
		case "ST3": return SQUARE_TOP_3;
		case "SB3": return SQUARE_BOTTOM_3;
		case "SL3": return SQUARE_LEFT_3;
		case "SR3": return SQUARE_RIGHT_3;
		case "STL": return SQUARE_TOP_LEFT;
		case "STR": return SQUARE_TOP_RIGHT;
		case "SBL": return SQUARE_BOTTOM_LEFT;
		case "SBR": return SQUARE_BOTTOM_RIGHT;
		case "SV": return SQUARE_VERTICAL;
		case "SH": return SQUARE_HORIZONTAL;
		case "SC": return SQUARE_CENTER;
		
		case "T": return TRIANGLE;
		
		default: return null;
		}
	}
	
	// 31
	public String getImageFilename() {
		switch (this) {
		case SQUARE:
			return "obstacles/BlackSquare.png";
		case SQUARE_TOP_1:
			return "obstacles/BlackSquareTop1.png";
		case SQUARE_BOTTOM_1:
			return "obstacles/BlackSquareBottom1.png";
		case SQUARE_LEFT_1:
			return "obstacles/BlackSquareLeft1.png";
		case SQUARE_RIGHT_1:
			return "obstacles/BlackSquareRight1.png";
		case SQUARE_TOP_3:
			return "obstacles/BlackSquareTop3.png";
		case SQUARE_BOTTOM_3:
			return "obstacles/BlackSquareBottom3.png";
		case SQUARE_LEFT_3:
			return "obstacles/BlackSquareLeft3.png";
		case SQUARE_RIGHT_3:
			return "obstacles/BlackSquareRight3.png";
		case SQUARE_TOP_LEFT:
			return "obstacles/BlackSquareTopLeft.png";
		case SQUARE_TOP_RIGHT:
			return "obstacles/BlackSquareTopRight.png";
		case SQUARE_BOTTOM_LEFT:
			return "obstacles/BlackSquareBottomLeft.png";
		case SQUARE_BOTTOM_RIGHT:
			return "obstacles/BlackSquareBottomRight.png";
		case SQUARE_VERTICAL:
			return "obstacles/BlackSquareVertical.png";
		case SQUARE_HORIZONTAL:
			return "obstacles/BlackSquareHorizontal.png";
		case SQUARE_CENTER:
			return "obstacles/BlackSquareCenter.png";
			
		case TRIANGLE:
			return "obstacles/BlackTriangle.png";
			
		default:
			return "";
		}
	}
	
	// 7
	public boolean killsPlayer() {
		return this == Obstacle.TRIANGLE;
	}

	// 7
	public boolean isSolid() {
		return this != Obstacle.TRIANGLE;
	}
	
}
