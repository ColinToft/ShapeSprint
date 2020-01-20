package xyz.colintoft.shapesprint;

/**
***********************************************
@Author Colin Toft
@Date December 30th, 2019
@Modified December 31st 2019, January 7th, 15th, 16th, 17th & 19th, 2020
@Description Represents one of the objects in a level.
***********************************************
*/
public enum Obstacle {
	SQUARE,
	SQUARE_TOP_1, SQUARE_BOTTOM_1, SQUARE_LEFT_1, SQUARE_RIGHT_1,
	SQUARE_TOP_3, SQUARE_BOTTOM_3, SQUARE_LEFT_3, SQUARE_RIGHT_3,
	SQUARE_TOP_LEFT, SQUARE_TOP_RIGHT, SQUARE_BOTTOM_LEFT, SQUARE_BOTTOM_RIGHT,
	SQUARE_VERTICAL, SQUARE_HORIZONTAL,
	SQUARE_CENTER,
	SQUARE_CENTER_TOP_LEFT, SQUARE_CENTER_TOP_RIGHT, SQUARE_CENTER_BOTTOM_LEFT, SQUARE_CENTER_BOTTOM_RIGHT,
	SQUARE_CENTER_TOP_LEFT_LINE_BOTTOM, SQUARE_CENTER_TOP_RIGHT_LINE_BOTTOM, SQUARE_CENTER_TOP_LEFT_TOP_RIGHT_LINE_BOTTOM,
	SQUARE_CENTER_BOTTOM_LEFT_LINE_TOP, SQUARE_CENTER_BOTTOM_RIGHT_LINE_TOP,
	TRIANGLE, TRIANGLE_UPSIDE_DOWN, TRIANGLE_LEFT,
	YELLOW_PAD, YELLOW_RING,
	CIRCLE_PORTAL_BOTTOM, CIRCLE_PORTAL_TOP,
	TRIANGLE_PORTAL_BOTTOM, TRIANGLE_PORTAL_TOP;

	/** Method Name: fromString()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified January 15th & 19th, 2020
	 * @Description Converts the String abbreviation of an obstacle (found in a level file) to an Obstacle.
	 * @Parameters
	 *      - String string: the string specifying the type of obstacle
	 * @Returns An Obstacle that corresponds to the string argument given
	 * Data Type: String, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: Returns null if the string is not a valid abbreviation for an Obstacle type
	 */
	public static Obstacle fromString(String string) {
		// Return the correct obstacle type based on the given string
		switch (string) {
		case "S": return SQUARE;
		
		case "T": return TRIANGLE;
		case "TU": return TRIANGLE_UPSIDE_DOWN;
		case "TL": return TRIANGLE_LEFT;
		
		case "YP": return YELLOW_PAD;
		case "YR": return YELLOW_RING;
		
		case "CPB": return CIRCLE_PORTAL_BOTTOM;
		case "CPT": return CIRCLE_PORTAL_TOP;
		case "TPB": return TRIANGLE_PORTAL_BOTTOM;
		case "TPT": return TRIANGLE_PORTAL_TOP;
		
		default: return null;
		}
	}
	
	/** Method Name: getImageFilename()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified January 15th, 16th & 19th, 2020
	 * @Description Returns the filename of the image of this Obstacle
	 * @Parameters N/A
	 * @Returns The filename of the image of this Obstacle
	 * Data Type: String, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public String getImageFilename() {
		// Return the correct image file name based on the given obstacle type
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
		case SQUARE_CENTER_TOP_LEFT:
			return "obstacles/BlackSquareCenterTopLeft.png";
		case SQUARE_CENTER_TOP_RIGHT:
			return "obstacles/BlackSquareCenterTopRight.png";
		case SQUARE_CENTER_BOTTOM_LEFT:
			return "obstacles/BlackSquareCenterBottomLeft.png";
		case SQUARE_CENTER_BOTTOM_RIGHT:
			return "obstacles/BlackSquareCenterBottomRight.png";
		case SQUARE_CENTER_TOP_LEFT_LINE_BOTTOM:
			return "obstacles/BlackSquareCenterTopLeftLineBottom.png";
		case SQUARE_CENTER_TOP_RIGHT_LINE_BOTTOM:
			return "obstacles/BlackSquareCenterTopRightLineBottom.png";
		case SQUARE_CENTER_TOP_LEFT_TOP_RIGHT_LINE_BOTTOM:
			return "obstacles/BlackSquareCenterTopLeftTopRightLineBottom.png";
		case SQUARE_CENTER_BOTTOM_LEFT_LINE_TOP:
			return "obstacles/BlackSquareCenterBottomLeftLineTop.png";
		case SQUARE_CENTER_BOTTOM_RIGHT_LINE_TOP:
			return "obstacles/BlackSquareCenterBottomRightLineTop.png";
			
		case TRIANGLE:
			return "obstacles/BlackTriangle.png";
		case TRIANGLE_UPSIDE_DOWN:
			return "obstacles/BlackTriangleUpsideDown.png";
		case TRIANGLE_LEFT:
			return "obstacles/BlackTriangleLeft.png";
			
		case YELLOW_PAD:
			return "obstacles/yellowPad.png";
		case YELLOW_RING:
			return "obstacles/yellowOrb.png";
			
		case CIRCLE_PORTAL_BOTTOM:
			return "obstacles/CubePortalBottom.png";
		case CIRCLE_PORTAL_TOP:
			return "obstacles/CubePortalTop.png";
		case TRIANGLE_PORTAL_BOTTOM:
			return "obstacles/RocketPortalBottom.png";
		case TRIANGLE_PORTAL_TOP:
			return "obstacles/RocketPortalTop.png";
			
		default:
			return "";
		}
	}
	
    /** Method Name: killsPlayer()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 15th, 2020
	 * @Description Returns whether this Obstacle kills a player on contact
	 * @Parameters N/A
	 * @Returns Whether this Obstacle kills a player on contact
	 * Data Type: boolean, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean killsPlayer() {
		// Return true if this obstacle is a triangle
		return this == TRIANGLE || this == TRIANGLE_UPSIDE_DOWN || this == TRIANGLE_LEFT;
	}

	/** Method Name: isSolid()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 15th, 16th, 17th & 19th, 2020
	 * @Description Returns whether this Obstacle is solid (player can land on it)
	 * @Parameters N/A
	 * @Returns Whether this Obstacle is solid
	 * Data Type: boolean, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean isSolid() {
		// All blocks are solid (the player can land on them) except for portals and triangles
		return !(killsPlayer() || isCirclePortal() || isTrianglePortal() || this == YELLOW_PAD || this == YELLOW_RING);
	}

	/** Method Name: isCirclePortal()
	 * @Author Colin Toft
	 * @Date January 15th, 2020
	 * @Modified N/A
	 * @Description Returns whether this Obstacle is a circle portal
	 * @Parameters N/A
	 * @Returns True if this Obstacle is a circle portal, otherwise false
	 * Data Type: boolean, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean isCirclePortal() {
		// Return true if this Obstacle is a circle portal
		return this == CIRCLE_PORTAL_BOTTOM || this == CIRCLE_PORTAL_TOP;
	}
	
    /** Method Name: isTrianglePortal()
	 * @Author Colin Toft
	 * @Date January 15th, 2020
	 * @Modified N/A
	 * @Description Returns whether this Obstacle is a triangle portal
	 * @Parameters N/A
	 * @Returns True if this Obstacle is a triangle portal, otherwise false
	 * Data Type: boolean, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean isTrianglePortal() {
		// Return true if this Obstacle is a triangle portal
		return this == TRIANGLE_PORTAL_BOTTOM || this == TRIANGLE_PORTAL_TOP;
	}
}
