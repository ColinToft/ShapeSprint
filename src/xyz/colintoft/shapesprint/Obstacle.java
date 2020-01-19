package xyz.colintoft.shapesprint;

/**
***********************************************
@Author Colin Toft
@Date December 30th, 2019
@Modified December 31st 2019, January 7th, 15th & 16th 2020
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
	SQUARE_CENTER_TOP_LEFT, SQUARE_CENTER_TOP_RIGHT,
	SQUARE_CENTER_TOP_LEFT_LINE_BOTTOM, SQUARE_CENTER_TOP_RIGHT_LINE_BOTTOM,
	TRIANGLE, TRIANGLE_UPSIDE_DOWN,
	CIRCLE_PORTAL_BOTTOM, CIRCLE_PORTAL_TOP,
	TRIANGLE_PORTAL_BOTTOM, TRIANGLE_PORTAL_TOP;

	/** Method Name: fromString()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified January 15th, 2020
	 * @Description Converts the String abbreviation of an obstacle (found in a level file) to an Obstacle.
	 * @Parameters
	 *      - String string: the string specifying the type of obstacle
	 * @Returns An Obstacle that corresponds to the string argument given
	 * Data Type: String, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
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
		case "SCTL": return SQUARE_CENTER_TOP_LEFT;
		case "SCTR": return SQUARE_CENTER_TOP_RIGHT;
		case "SCTLB": return SQUARE_CENTER_TOP_LEFT_LINE_BOTTOM;
		case "SCTRB": return SQUARE_CENTER_TOP_RIGHT_LINE_BOTTOM;
		
		case "T": return TRIANGLE;
		case "TU": return TRIANGLE_UPSIDE_DOWN;
		
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
	 * @Modified January 15th & 16th, 2020
	 * @Description Returns the filename of the image of this Obstacle
	 * @Parameters N/A
	 * @Returns The filename of the image of this Obstacle
	 * Data Type: String, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
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
		case SQUARE_CENTER_TOP_LEFT:
			return "obstacles/BlackSquareCenterTopLeft.png";
		case SQUARE_CENTER_TOP_RIGHT:
			return "obstacles/BlackSquareCenterTopRight.png";
		case SQUARE_CENTER_TOP_LEFT_LINE_BOTTOM:
			return "obstacles/BlackSquareCenterTopLeftLineBottom.png";
		case SQUARE_CENTER_TOP_RIGHT_LINE_BOTTOM:
			return "obstacles/BlackSquareCenterTopRightLineBottom.png";
			
		case TRIANGLE:
			return "obstacles/BlackTriangle.png";
		case TRIANGLE_UPSIDE_DOWN:
			return "obstacles/BlackTriangleUpsideDown.png";
			
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
		return this == TRIANGLE || this == TRIANGLE_UPSIDE_DOWN;
	}

	/** Method Name: isSolid()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 15th, 16th & 17th, 2020
	 * @Description Returns whether this Obstacle is solid (player cannot pass through it)
	 * @Parameters N/A
	 * @Returns Whether this Obstacle is solid
	 * Data Type: boolean, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean isSolid() {
		return !(killsPlayer() || isCirclePortal() || isTrianglePortal());
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
		return this == TRIANGLE_PORTAL_BOTTOM || this == TRIANGLE_PORTAL_TOP;
	}
}
