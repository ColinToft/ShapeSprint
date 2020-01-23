package xyz.colintoft.shapesprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.sound.sampled.Clip;

import xyz.colintoft.cgraphics.Util;
import xyz.colintoft.cgraphics.components.Drawable;
import xyz.colintoft.shapesprint.scenes.PlayLevel;

/**
***********************************************
@Author Colin Toft
@Date December 30th, 2019
@Modified December 31st, January 7th, 8th, 9th, 10th, 13th, 14th, 15th, 16th, 17th, 18th, 19th, 21st & 22nd, 2020
@Description A class that renders the level to the screen, including backgrounds, obstacles and the player, as well as playing the game music.
***********************************************
*/
public class LevelView extends Drawable {

	private Level level; // The level that is being played
	
	private final double xSpeed = 10.386; // The constant speed at which the player moves to the right, in blocks per second (taken from a forum about geometry dash physics: http://gdforum.freeforums.net/thread/48749/p1kachu-presents-physics-geometry-dash)
	private final double levelHeight = 11; // Height of the level in blocks
	private final double baseGroundHeight = 0.3; // The starting height of the ground (as a fraction of the screen height)
	private double groundHeight = baseGroundHeight; // Fraction of the height of the screen that the ground takes up
	private final double groundHeightMoveSpeed = 0.04; // The speed at which the ground height will move up and down
	private final double groundHeightThreshold = 0.45; // If the player is above this coordinate (fraction of screen height)
	private final double playerScreenX = 0.34; // The x coordinate where the player is drawn (as a fraction of the screen width)
	private final int levelEndOffset = 8; // How many blocks the end of the level appears after the last obstacle
	private final double backgroundSpeed = 0.125; // The speed of the background compared to the speed of the ground
	
	private final double playerWidth = 1; // The width of the player in blocks
	private int groundTileWidth; // The width of one ground tile in pixels
	
	private final double playerRotationSpeed = xSpeed / (playerWidth * 0.5); // The speed that the player rotates at in radians/s
	private double playerRotation = 0; // The current player rotation
	
	private double playerX = -15; // The player's x coordinate in blocks (0 is the start of the level)
	private double playerY = 0; // The player's y coordinate in blocks (0 is ground level)
	private double lastGroundY = 0; // The y coordinate of the last time the player was on the ground (in blocks)
	
	private boolean hasDied = false; // Whether or not the player has died
	private double deathTimer = 0; // A timer that counts up in seconds after the player has died
	public boolean hasBeatLevel = false; // Whether or not the player has beat the level
	private double winTimer = 0; // A timer that counts up in seconds after the player has beat the level
	private double winAnimationLength = 1; // The length of the level completion animation in seconds
	
	private boolean jumping = false; // Whether the user is currently jumping (pressing space or clicking)
	private boolean holding = false; // Whether the user is holding down the mouse
	public int jumpCount = 0; // How many times the user has jumped
	private boolean hasUsedTriangleMode = false; // Whether or not the user has used triangle mode so far
		
	private boolean playingMusic = false; // Whether music is currently playing
	
	private double ySpeed = 0; // The current y speed of the player in blocks per second
	private final double gravity = 0.876 * xSpeed * xSpeed; // The rate at which gravity affects the y speed (taken from a forum about geometry dash physics)
	private final double minYSpeed = -2.6 * xSpeed; // The minimum y speed, or fastest rate at which the player can fall (taken from a forum about geometry dash physics)
	private final double jumpYSpeed = 2 * xSpeed; // The value that the y speed is set to when the player jumps (taken from a forum about geometry dash physics)
	private final double yellowPadYSpeed = 2.77 * xSpeed;
	private final double triangleMaxYSpeed = 1.4 * xSpeed; // The maximum y speed that a player can reach when in triangle mode (found with trial and error)
	private final double triangleMinYSpeed = -1 * xSpeed; // The minimum y speed that a player can reach when in triangle mode (found with trial and error)
	private final double triangleYSpeedIncrease = 0.4 * xSpeed * xSpeed; // The rate at which y speed increases when the player holds down with the mouse or space bar (found with trial and error)
	
	private boolean practiceMode = false; // Whether the player is currently in practice mode
	private double checkpointX = playerX; // The x coordinate of the most recent practice mode checkpoint, in blocks
	private double checkpointY = playerY; // The y coordinate of the most recent practice mode checkpoint, in blocks
	private double checkpointYSpeed = ySpeed; // The y speed of the player at the most recent practice mode checkpoint, in blocks per second
	private boolean checkpointTriangleMode = false; // Whether the player was in triangle mode at the most recent checkpoint
	private boolean checkpointUpsideDownMode = false; // Whether the player was in upside down mode at the most recent checkpoint
	private int checkpointDeathCount = 0; // How many times the player has died close to the most recent checkpoint
	private double prevCheckpointX = checkpointX; // The x coordinate of the previous practice mode checkpoint, in blocks
	private double prevCheckpointY = checkpointY; // The y coordinate of the previous practice mode checkpoint, in blocks
	private double prevCheckpointYSpeed = checkpointYSpeed; // The y speed of the player at the previous practice mode checkpoint, in blocks per second
	private boolean prevCheckpointTriangleMode = checkpointTriangleMode; // Whether the player was in triangle mode at the previous checkpoint
	private boolean prevCheckpointUpsideDownMode = checkpointUpsideDownMode; // Whether the player was in upside down mode at the previous checkpoint
	
	private boolean triangleMode = false; // Whether the player is currently in triangle mode
	private boolean upsideDownMode = false; // Whether the player is currently in upside down mode
	
	private BufferedImage playerCircleImage, playerTriangleImage; // Player images
	private BufferedImage backgroundImage, groundImage, ceilingImage; // Images for the background of the level
	private BufferedImage checkpointImage; // Image for the practice mode checkpoints
	private double triangleImagePadding = 0.5 * playerWidth; // How much padding to put around the triangle image (needed so that rotation doesn't cut off the image)
		
	HashMap<Obstacle, BufferedImage> images; // A Hashmap that matches an Obstacle type to its corresponding image
	
	private Clip music; // The music for this level
	private Clip practiceMusic; // The practice mode music
	private Clip deathSound; // The sound that plays when the player is killed
	private Clip winSound; // The sound that plays when the player completes the level
	
	/** Method Name: LevelView()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified Jauary 9th, 2020
	 * @Description Creates a new LevelView object
	 * @Parameters
	 *      - double x: the x coordinate of this object (as a percentage of the parent panel's width)
	 *      - double y: the y coordinate of this object (as a percentage of the parent panel's height)
	 *      - double width: the width of this object (as a percentage of the parent panel's width)
	 *      - double height: the height of this object (as a percentage of the parent panel's height)
	 *      - Level level: the level to play
	 * @Returns N/A
	 * Data Type: Drawable, Color, Boolean, Level, Clip
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public LevelView(double x, double y, double width, double height, Level level) {
		super(x, y, width, height);
		setBackground(new Color(0, 0, 0, 0)); // Use a transparent background
		setDynamic(true); // Means this Drawable needs to be constantly redrawn
		
		// Store the level object
		this.level = level;
		
		// Load Music
		music = Util.getAudioClip(getClass(), level.musicFile);
		practiceMusic = Util.getAudioClip(getClass(), "AsItShouldBeLoop.wav");
		deathSound = Util.getAudioClip(getClass(), "explodeSound.wav");
		winSound = Util.getAudioClip(getClass(), "levelCompleteSound.wav");
	}
	
	/** Method Name: LevelView()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified N/A
	 * @Description Creates a new LevelView object with the same dimensions as the parent panel
	 * @Parameters
	 *      - Level level: the level to play
	 * @Returns N/A
	 * Data Type: Drawable, Color, Boolean, Level, Clip
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public LevelView(Level level) {
		// Create a level view object using the parent's dimensions
		this(0, 0, 1, 1, level);
	}
	
	/** Method Name: generateImage()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 9th & 16th, 2020
	 * @Description Overrides Drawable.generateImage(): loads the images needed to display the level (background, ground, ceiling, obstacles and checkpoints)
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: BufferedImage, Graphics, Color, int, Hashmap, Obstacle
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void generateImage() {
		// Load Background Image
		BufferedImage originalBackground = Util.loadImageFromFile(getClass(), "backgrounds/background1classic.png");
		backgroundImage = Util.scaleImage(originalBackground, pixelWidth(), pixelWidth());
		
		// Lay the background color over the background image
		Graphics g = backgroundImage.createGraphics();
		Color bgColor = new Color(level.backgroundColor.getRed(), level.backgroundColor.getGreen(), level.backgroundColor.getBlue(), 205);
	    g.setColor(bgColor);
	    g.fillRect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
	    g.dispose();
	    
	    // Load Ground Image
		groundTileWidth = (int)(pixelHeight() * groundHeight);
		BufferedImage groundTile = Util.loadImageFromFile(getClass(), "backgrounds/ground1.png");
		
		// Draw the ground tile image multiple times to fill up the ground image
		groundImage = Util.getEmptyImage((pixelWidth() / groundTileWidth + 2) * groundTileWidth, groundTileWidth);
		g = groundImage.createGraphics();
		for (int i = 0; i < groundImage.getWidth(); i += groundTileWidth) {
			g.drawImage(groundTile, i, 0, groundTileWidth, groundTileWidth, null);
		}
		
		// Lay the ground Color over the ground image, then a mostly transparent layer of black to make it slightly darker
		g.setColor(bgColor);
	    g.fillRect(0, 0, groundImage.getWidth(), groundImage.getHeight());
	    g.setColor(new Color(0, 0, 0, 50));
	    g.fillRect(0, 0, groundImage.getWidth(), groundImage.getHeight());
	    g.dispose();
		
		// Load Ceiling Image
		BufferedImage ceilingTile = Util.loadImageFromFile(getClass(), "backgrounds/ceiling1.png");
		
		// Draw the ceiling tile image multiple times to fill up the ceiling image
		ceilingImage = Util.getEmptyImage((pixelWidth() / groundTileWidth + 2) * groundTileWidth, groundTileWidth);
		g = ceilingImage.createGraphics();
		for (int i = 0; i < ceilingImage.getWidth(); i += groundTileWidth) {
			g.drawImage(ceilingTile, i, 0, groundTileWidth, groundTileWidth, null);
		}
		
		// Lay the ground Color over the ceiling image, then a mostly transparent layer of black to make it slightly darker
		g.setColor(bgColor);
	    g.fillRect(0, 0, ceilingImage.getWidth(), ceilingImage.getHeight());
	    g.setColor(new Color(0, 0, 0, 50));
	    g.fillRect(0, 0, ceilingImage.getWidth(), ceilingImage.getHeight());
	    g.dispose();
	    
	    // Load player circle image
		BufferedImage originalPlayerImage = Util.loadImageFromFile(getClass(), "players/PlayerCircle.png");
		playerCircleImage = Util.scaleImage(originalPlayerImage, (int)(getBlockSize() * playerWidth), (int)(getBlockSize() * playerWidth), false);
		
		// Load player triangle image
		originalPlayerImage = Util.loadImageFromFile(getClass(), "players/PlayerTriangle.png");
		// Use a larger image in order to add padding around the triangle image
		playerTriangleImage = Util.getEmptyImage((int)(getBlockSize() * (1.5 * playerWidth + triangleImagePadding * 2)), (int)(getBlockSize() * (1 * playerWidth + triangleImagePadding * 2)));
		g = playerTriangleImage.createGraphics();
		g.drawImage(originalPlayerImage, (int)(getBlockSize() * triangleImagePadding), (int)(getBlockSize() * triangleImagePadding), (int)(getBlockSize() * 1.5 * playerWidth), (int)(getBlockSize() * playerWidth), null);
		
		// Load Obstacle Images
		images = new HashMap<Obstacle, BufferedImage>();
		BufferedImage image;
		// Loop through each obstacle type and put its corresponding image in the hashmap
		for (Obstacle type: Obstacle.values()) {
			image = Util.loadImageFromFile(getClass(), type.getImageFilename());
			image = Util.scaleImage(image, (getBlockSize() + 1) / image.getWidth());
			images.put(type, image);
		}
		
		// Load checkpoint image
		checkpointImage = Util.loadImageFromFile(getClass(), "other/checkpoint.png");
		checkpointImage = Util.scaleImage(checkpointImage, getBlockSize() * 0.5 / checkpointImage.getWidth());
		
		super.generateImage();
	}
	
	/** Method Name: draw()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 8th, 9th, 13th, 14th & 15th, 2020
	 * @Description Overrides Drawable.draw(): draws the level and player to the screen
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: BufferedImage, Graphics, int, boolean, BasicStroke, Color, double, AffineTransform, Obstacle
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g; // Convert the Graphics object to Graphics2D in order to use extra commands
	    
		// First draw the background image
		int backgroundX = (int)(-getBlockSize() * (playerX * backgroundSpeed % (backgroundImage.getWidth() / getBlockSize()))); // Calculate the correct position using the player's x coordinate
		if (backgroundX > 0) {
			backgroundX -= backgroundImage.getWidth();
	    }
		// Since the background loops it needs to be drawn twice, one next to the other
	    g2d.drawImage(backgroundImage, backgroundX, pixelHeight() - backgroundImage.getHeight(), null);
	    g2d.drawImage(backgroundImage, backgroundX + backgroundImage.getWidth(), pixelHeight() - backgroundImage.getHeight(), null);
	    
	    // Draw the ground
	    int groundX = (int)(-getBlockSize() * (playerX % (groundTileWidth / getBlockSize()))); // Calculate the correct position using the player's x coordinate
	    if (groundX > 0) {
	    	groundX -= groundTileWidth;
	    }
	    
	    g2d.drawImage(groundImage, groundX, (int)(pixelHeight() * (1 - groundHeight)), null);
	    
	    // Draw a white line across the top of the ground
	    g2d.setStroke(new BasicStroke(2f));
	    g2d.setColor(Color.WHITE);
	    g2d.drawLine(0, (int)(pixelHeight() * (1 - groundHeight)) + 2, pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)) + 2);
	    
	    // Draw the ceiling in the same way if the player is in triangle mode
	    if (triangleMode || upsideDownMode) {
		    g2d.drawImage(ceilingImage, groundX, (int)(pixelHeight() * (0.5 / levelHeight) - ceilingImage.getHeight()), null);
		    // Draw a white line at the bottom of the ceiling
		    g2d.drawLine(0, (int)(pixelHeight() * (0.5 / levelHeight)), pixelWidth(), (int)(pixelHeight() * (0.5 / levelHeight)));
	    }
	    
	    // Draw the player
	    if (!hasDied) {
	    	int playerImageX, playerImageY;
	    	if (hasBeatLevel) {
	    		// If the player has beat the level, animate their x, y and rotation based on the winTimer
	    		int beginX = blockXToPixelX(level.width);
	    		int endX = blockXToPixelX(level.width + levelEndOffset);
	    		double endProgress = Math.pow(winTimer / winAnimationLength, 2.5);
	    		playerRotation = endProgress * Math.PI * 0.7;
			    playerImageX = (int)(beginX + (endX - beginX) * endProgress);
				playerImageY = blockYToPixelY(playerY + playerWidth + -7 * (endProgress) * (endProgress - 1.6));
	    	} else {
	    		// Calculate the pixel coordinates where the player should be drawn
	    		playerImageX = (int)(pixelWidth() * playerScreenX);
	    		playerImageY = blockYToPixelY(playerY + playerWidth);
	    	}
			
	    	// Rotate the player's image and then draw its to the screen
	    	if (!hasBeatLevel || playerImageX < blockXToPixelX(level.width + levelEndOffset + playerWidth)) {
	    		// Create the AffineTransform objects needed to do the rotation
				AffineTransform backup = g2d.getTransform();
				AffineTransform rotate;
				if (triangleMode) {
					// Rotate the player while accounting for the padding on the triangle image, then draw it the at the previously calculated coordinates
					rotate = AffineTransform.getRotateInstance(playerRotation, playerTriangleImage.getWidth() / 2.0, playerTriangleImage.getHeight() / 2.0);
				    AffineTransformOp op = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR);
					int padding = (int)(triangleImagePadding * getBlockSize());
			    	g2d.drawImage(op.filter(playerTriangleImage, null), playerImageX - padding, playerImageY - padding, null);
				} else {
					// Rotate the player then draw it the at the previously calculated coordinates
					rotate = AffineTransform.getRotateInstance(playerRotation, playerCircleImage.getWidth() / 2.0, playerCircleImage.getHeight() / 2.0);
				    AffineTransformOp op = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR);
			    	g2d.drawImage(op.filter(playerCircleImage, null), playerImageX, playerImageY, null);
				}
			   
				// Restore the previous transform so the other images will not appear rotated as well
			    g2d.setTransform(backup);
	    	}
	    }
	    
	    // Draw obstacles
	    Obstacle o;
	    // Loop through the x and y coordinates that are currently in view
	    for (int obstacleX = Math.max(0, (int) screenXToBlockX(0)); obstacleX < Math.min((int) screenXToBlockX(1) + 1, level.width); obstacleX++) {
	    	for (int obstacleY = Math.max(0, (int) screenYToBlockY(1)); obstacleY < Math.min((int) screenYToBlockY(0) + 1, level.height); obstacleY++) {
    			o = level.obstacles[obstacleX][obstacleY]; // Find the obstacle at those coordinates
    			if (o != null) {
    				// If there is an obstacle there, draw it
    				BufferedImage image = images.get(o);
    				g2d.drawImage(image, blockXToPixelX(obstacleX), blockYToPixelY(obstacleY) - image.getHeight(), null);
    			}
	    	}
	    }
	    
	    // Draw the end of the level
	    if (screenXToBlockX(1) > level.width + levelEndOffset) {
	    	// Draw a large black wall
	    	g2d.setColor(Color.BLACK);
	    	g2d.fillRect(blockXToPixelX(level.width + levelEndOffset), 0, pixelWidth() - blockXToPixelX(level.width + levelEndOffset), (int)(pixelHeight() * Math.min(1, 1 - groundHeight)));
	    	// Draw a white line on the left of the wall
	    	g2d.setColor(Color.WHITE);
	    	g2d.fillRect(blockXToPixelX(level.width + levelEndOffset), 0, (int)(pixelWidth() * 0.005), (int)(pixelHeight() * Math.min(1, 1 - groundHeight)));
	    }
	    
	    if (practiceMode) {
	    	// Draw checkpoints if the game is currently in practice mode
	    	if (checkpointX > 0) {
	    		g2d.drawImage(checkpointImage, blockXToPixelX(checkpointX) + (int)(getBlockSize() - checkpointImage.getWidth()) / 2, blockYToPixelY(checkpointY + 1) + (int)(getBlockSize() - checkpointImage.getHeight()) / 2, null);
	    	}
	    	
	    	if (prevCheckpointX > 0) {
	    		g2d.drawImage(checkpointImage, blockXToPixelX(prevCheckpointX) + (int)(getBlockSize() - checkpointImage.getWidth()) / 2, blockYToPixelY(prevCheckpointY + 1) + (int)(getBlockSize() - checkpointImage.getHeight()) / 2, null);
	    	}
	    }
	}
	
	/** Method Name: update()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 7th, 9th, 10th, 13th, 14th, 15th, 17th, 19th & 21st, 2020
	 * @Description Overrides Drawable.update(): updates the level, including updating player position, handling music and calculating physics
	 * @Parameters
	 *      - double dt: The time in seconds since the last time update was called
	 * @Returns N/A
	 * Data Type: boolean, double, int, Clip
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void update(double dt) {
		if (!triangleMode) {
			// If the player is in circle mode, rotate them to simulate rolling along the ground
			playerRotation += playerRotationSpeed * dt;
			playerRotation %= 2 * Math.PI;
		}
		
		if (!hasDied && !hasBeatLevel) {
			// Move the player to the right
			playerX += xSpeed * dt;
		}
		
		
		boolean justLanded = false;
		double minY = getMinY(); // Find the y coordinate of the ground or obstacle beneath the player
		double maxY = getMaxY(); // Find the y coordinate of the ground or obstacle beneath the player
		
		if (playerY > minY || ySpeed != 0) { // If the player is in the air
			if (!triangleMode) {
				// If the player is in circle mode, simulate gravity by lowering their ySpeed
				ySpeed = Math.max(ySpeed - gravity * dt * (upsideDownMode ? -1 : 1), minYSpeed);
			} else if (!jumping) {
				// If the player is in triangle mode but not holding the mouse or space bar, their y speed is also lowered
				if (upsideDownMode) {
					ySpeed = Math.min(ySpeed + triangleYSpeedIncrease * dt, triangleMode ? -triangleMinYSpeed : -minYSpeed);
				} else {
					ySpeed = Math.max(ySpeed - triangleYSpeedIncrease * dt, triangleMode ? triangleMinYSpeed : minYSpeed);
				}
			}
			
			// Adjust the player's y coordinate based on their y speed
			playerY += ySpeed * dt;
		}
				
		boolean circleHitCeiling = false; // True if the player is in circle mode and has gone above the max y coordinate (ran into an obstacle above them)

		if (playerY <= minY && ySpeed <= 0) { // If the player is on the ground
			justLanded = ySpeed < 0; // If the y speed is smaller than 0, the player is falling and must have just landed on the ground
			
			// The player is on the ground, so set y speed to 0 and the y coordinate to the exact y coordinate of the ground
			ySpeed = 0;
			playerY = minY;
			lastGroundY = playerY; // Store the y coordinate of the last time the player was on the ground
			
			if (upsideDownMode && justLanded) {
				circleHitCeiling = true;
			}
			
			if (jumping && !triangleMode) {
				// If the player is jumping (holding down the mouse or space bar), call the jump method
				jump();
				holding = true; // The player is now holding the mouse/space bar
			}
		} else if (isTouchingYellowRing() && jumping && !holding) {
			// The yellow ring has the same effect as the player jumping
			jump();
			holding = true;
		}
		
		
		if (playerY >= maxY - playerWidth && ySpeed >= 0) { // If the player is moving upwards and is running into an obstacle above them
			if (triangleMode) {
				// If the player hits a ceiling in triangle mode, set their y speed to 0 and make their y coordinate equal to the exact position beneath the obstacle
				ySpeed = 0;
				playerY = maxY - playerWidth;
				
			} else {
				if (upsideDownMode) {
					ySpeed = 0;
					playerY = maxY - playerWidth;
					lastGroundY = playerY;
					justLanded = ySpeed > 0; // If the y speed is larger than 0, the player is moving upwards and must have just landed on the ceiling
					
					if (jumping) {
						jump();
						holding = true;
					}
				} else {
					circleHitCeiling = true; // The player is in circle mode and hit a ceiling, meaning they should be killed
				}
			}
		}
		
		double targetGroundHeight; // The height that the ground should be drawn at (in screen coordinates) based on the player's y coordinate
		if (triangleMode) {
			if (jumping) {
				// If the player is holding the mouse/space bar in triangle mode, increase their y speed
				if (upsideDownMode) {
					ySpeed = Math.max(-triangleMaxYSpeed, ySpeed - triangleYSpeedIncrease * dt);
				} else {
					ySpeed = Math.min(triangleMaxYSpeed, ySpeed + triangleYSpeedIncrease * dt);
				}
				if (!hasUsedTriangleMode) {
					hasUsedTriangleMode = true;
					((ShapeSprint) getGame()).hasUsedTriangleMode = true; // Remember that the player has used triangle mode to avoid showing them help messages for it in the future
				}
			}
			if (playerY == 0) {
				playerRotation = Math.max(playerRotation - playerRotationSpeed * 0.5 * dt, 0); // If the player is on the ground, gradually move their rotation to 0
			} else if (playerY == maxY - playerWidth) {
				playerRotation = Math.min(playerRotation + playerRotationSpeed * 0.5 * dt, 0); // If the player is on the ceiling, gradually move their rotation to 0
			} else {
				playerRotation = Math.atan2(-ySpeed, xSpeed); // If the player is in the air, set their rotation according to their speed to make them point in that direction
			}
			
			targetGroundHeight = 0.5 / levelHeight; // In triangle mode the ground height always stays the same
		} else {
			if (upsideDownMode) {
				targetGroundHeight = 0.5 / levelHeight; // In upside down mode the ground height always stays the same
			} else {
				// If the player is in normal circle mode, calculate the ground height based on the player's y coordinate
				targetGroundHeight = Math.min(baseGroundHeight, groundHeightThreshold - (Math.min(lastGroundY, playerY) * getBlockSize() / pixelHeight()));
			}
		}
		
		double groundHeightTolerance = (triangleMode || upsideDownMode) ? 0 : 0.1; // It is acceptable if the ground height is within this amount of its target value
		
		// Move the ground height towards the target ground height if necessary
		if (targetGroundHeight < groundHeight - groundHeightTolerance && !hasBeatLevel && !hasDied) {
			groundHeight = Math.max(targetGroundHeight, groundHeight - groundHeightMoveSpeed * Math.max(ySpeed, xSpeed) * dt);
		} else if (targetGroundHeight > groundHeight + groundHeightTolerance && !hasBeatLevel && !hasDied) {
			groundHeight = Math.min(targetGroundHeight, groundHeight + groundHeightMoveSpeed * Math.max(ySpeed, xSpeed) * dt);
		}

		if (!hasDied && (shouldDie() || circleHitCeiling)) {
			// If the player has been killed, start the death sound and death timer
			hasDied = true;
			deathTimer = 0;
			if (!practiceMode) {
				// Stop the music, but not in practice mode since the music in practice mode should continuously loop
				stopMusic();
			}
			// Play the death sound effect
			deathSound.setFramePosition(0);
			deathSound.start();
		}
		
		updateMode(); // Updates the players mode to triangle mode or circle mode if they are traveling through a portal
		
		if (isTouchingYellowPad()) {
			ySpeed = yellowPadYSpeed;
			lastGroundY = playerY;
		}
		
		if (practiceMode && !hasDied && !hasBeatLevel && (justLanded || triangleMode) && playerX - checkpointX > 15) {
			// If the user is in practice mode, create a checkpoint if they have just landed and are far enough away from the last checkpoint
			createCheckpoint();
		}
		
		if (hasDied) {
			jumping = false;
			// If the player has already died, increase the death timer
			deathTimer += dt;
			if (deathTimer > 1) {
				// After the death timer has reached one second, start the next attempt
				deathSound.stop();
				startNextAttempt();
			}
			return;
		}
		
		if (playerX > level.width && !hasBeatLevel) {
			// The player has reached the end of the level
			hasBeatLevel = true;
			jumping = false;
			winTimer = 0; // Start the win timer to time the ending animation
			if (practiceMode) { // Only stop the music in practice mode, since in normal mode the music ends at the end of the level
				stopMusic();
			}
			// Play the win sound
			winSound.setFramePosition(0);
			winSound.start();
		}
		
		if (hasBeatLevel) {
			// Increase the win timer
			winTimer += dt;
			if (winTimer > winAnimationLength + 0.7) {
				// After the win animation is finished, show the win screen
				((PlayLevel) parentPanel).showWinScreen();
			}
		}
		
		if (!playingMusic && playerX >= 0 + (level.musicOffset * xSpeed) && !hasDied && !hasBeatLevel) {
			// If the music is not playing, start it
			startMusic();
			playerX = Math.max(checkpointX, 0);
		}
	}
	
	/** Method Name: createCheckpoint()
	 * @Author Colin Toft
	 * @Date January 9th, 2020
	 * @Modified January 22nd, 2020
	 * @Description Creates a checkpoint at the current player location.
	 * @Returns N/A
	 * Data Type: double, boolean, int
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private void createCheckpoint() {
		// Assign the values of the most recent checkpoint to the previous checkpoint
		prevCheckpointX = checkpointX;
		prevCheckpointY = checkpointY;
		prevCheckpointYSpeed = checkpointYSpeed;
		prevCheckpointTriangleMode = checkpointTriangleMode;
		prevCheckpointUpsideDownMode = checkpointUpsideDownMode;
		
		// Update the checkpoint to current player position
		checkpointX = playerX;
		checkpointY = playerY;
		checkpointYSpeed = ySpeed;
		checkpointTriangleMode = triangleMode;
		checkpointUpsideDownMode = upsideDownMode;
		checkpointDeathCount = 0;
	}
	
	/** Method Name: deleteCheckpoint()
	 * @Author Colin Toft
	 * @Date January 17th, 2020
	 * @Modified January 22nd, 2020
	 * @Description Deletes the most recent checkpoint.
	 * @Returns N/A
	 * Data Type: double, boolean, int
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private void deleteCheckpoint() {
		checkpointX = prevCheckpointX;
		checkpointY = prevCheckpointY;
		checkpointYSpeed = prevCheckpointYSpeed;
		checkpointTriangleMode = prevCheckpointTriangleMode;
		checkpointUpsideDownMode = prevCheckpointUpsideDownMode;
		checkpointDeathCount = 0;
	}
	
	/** Method Name: getMinY()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 19th, 2020
	 * @Description Finds the y coordinate of the ground beneath the player (highest solid obstacle underneath the player)
	 * @Returns The y coordinate of the ground beneath the player
	 * Data Type: double, Obstacle, boolean
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private double getMinY() {
		double circleRadius = playerWidth * 0.5; // The radius of the player in blocks
		double playerCenterX = playerX + circleRadius; // The x coordinate of the center of the player in blocks
		
		double minY = 0; // The y coordinate of the ground beneath the player
		
		// Loop through all obstacles the player could be touching
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth - 0.00001); obstacleY >= 0; obstacleY--) {
				try {
					if (level.obstacles[obstacleX][obstacleY] != null && level.obstacles[obstacleX][obstacleY].isSolid()) { // If there is a solid obstacle at this location
						double blockMinY;
						if ((int)playerCenterX == obstacleX) {
							// If the player's center is on this block, set the minY to the top of this obstacle
							blockMinY = obstacleY + 1;
						} else {
							double cornerX = Math.round(playerX);
							// Calculate the exact minimum y for the player using math to account for the players circular shape on a corner of a square block
							blockMinY = obstacleY + 1 - Math.abs(Math.cos(Math.asin((cornerX - playerCenterX) / circleRadius))) * circleRadius;
						}
						if (minY < blockMinY) {
							// If the new calculated minY for this block is higher (closer to the player) than the previous value, store it in the minY variable
							minY = blockMinY;
						}
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}
		
		return minY;
	}
	
	/** Method Name: getMaxY()
	 * @Author Colin Toft
	 * @Date January 16th, 2020
	 * @Modified N/A
	 * @Description Finds the y coordinate of the ceiling above the player (lowest solid obstacle above the player)
	 * @Returns The y coordinate of the ceiling above the player
	 * Data Type: double, Obstacle, boolean
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private double getMaxY() {
		double circleRadius = playerWidth * 0.5; // The radius of the player in blocks
		double playerCenterX = playerX + circleRadius; // The x coordinate of the center of the player in blocks
		
		double maxY = (triangleMode || upsideDownMode) ? levelHeight - 1 : 1000000; // The y coordinate of the ceiling or obstacle above the player
		
		// Loop through all obstacles the player could be touching
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth); obstacleY < level.height; obstacleY++) {
				try {
					if (level.obstacles[obstacleX][obstacleY] != null && level.obstacles[obstacleX][obstacleY].isSolid()) { // If there is a solid obstacle at this location
						double blockMaxY;
						if ((int)playerCenterX == obstacleX) { // If the player's center is on this block, set the maxY to the bottom of this obstacle
							blockMaxY = obstacleY;
						} else {
							double cornerX = Math.round(playerX);
							// Calculate the exact maximum y for the player using math to account for the players circular shape on a corner of a square block
							blockMaxY = obstacleY + Math.abs(Math.cos(Math.asin((cornerX - playerCenterX) / circleRadius))) * circleRadius;
						}
						if (maxY > blockMaxY) {
							// If the new calculated maxY for this block is lower (closer to the player) than the previous value, store it in the maxY variable
							maxY = blockMaxY;
						}
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}
		
		return maxY;
	}
	
	/** Method Name: shouldDie()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified N/A
	 * @Description Determines if a player is touching a triangle or is colliding with the side of a solid object
	 * @Returns Whether or not the player should die based on these conditions
	 * Data Type: Area, int, GeneralPath, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private boolean shouldDie() {
		Area playerArea = new Area(new Ellipse2D.Double(playerX, playerY, playerWidth, playerWidth)); // Store the player's area
		
		// Loop through all obstacles that the player could be touching
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth); obstacleY >= 0; obstacleY--) {
				try {
					// If the obstacle is a triangle, store its area and see if it intersects with the player
					if (level.obstacles[obstacleX][obstacleY] == Obstacle.TRIANGLE) {
						GeneralPath triangleShape = new GeneralPath();
						triangleShape.moveTo(obstacleX, obstacleY);
						triangleShape.lineTo(obstacleX + 0.5, obstacleY + 1);
						triangleShape.lineTo(obstacleX + 1, obstacleY);
						triangleShape.closePath();
						Area triangleArea = new Area(triangleShape);
						triangleArea.intersect(playerArea); // Calculate the intersection between the player and the triangle
						
						if (!triangleArea.isEmpty()) {
							return true; // The player intersects with the triangle's area
						}
					// If the obstacle is an upside down triangle, store its area and see if it intersects with the player
					} else if (level.obstacles[obstacleX][obstacleY] == Obstacle.TRIANGLE_UPSIDE_DOWN) {
						GeneralPath triangleShape = new GeneralPath();
						triangleShape.moveTo(obstacleX, obstacleY + 1);
						triangleShape.lineTo(obstacleX + 0.5, obstacleY);
						triangleShape.lineTo(obstacleX + 1, obstacleY + 1);
						triangleShape.closePath();
						Area triangleArea = new Area(triangleShape);
						triangleArea.intersect(playerArea); // Calculate the intersection between the player and the triangle
						
						if (!triangleArea.isEmpty()) {
							return true; // The player intersects with the triangle's area
						}
					// If the obstacle is an left facing triangle, store its area and see if it intersects with the player
					} else if (level.obstacles[obstacleX][obstacleY] == Obstacle.TRIANGLE_LEFT) {
						GeneralPath triangleShape = new GeneralPath();
						triangleShape.moveTo(obstacleX, obstacleY + 0.5);
						triangleShape.lineTo(obstacleX + 1, obstacleY);
						triangleShape.lineTo(obstacleX + 1, obstacleY + 1);
						triangleShape.closePath();
						Area triangleArea = new Area(triangleShape);
						triangleArea.intersect(playerArea); // Calculate the intersection between the player and the triangle
						
						if (!triangleArea.isEmpty()) {
							return true; // The player intersects with the triangle's area
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
			
			if (obstacleX > playerX) { // Check on the right side of the player to see if they are about to run into a square
				for (int obstacleY = (int)(playerY + playerWidth); obstacleY >= (int) playerY; obstacleY--) {
					try {
						if (level.obstacles[obstacleX][obstacleY] != null && level.obstacles[obstacleX][obstacleY].isSolid()) { // If there is a solid obstacle at this location
							// Calculate the area of the right side of the player
							Area rightSidePlayerArea = new Area(new Rectangle2D.Double(playerX + playerWidth * 0.8, playerY, playerWidth * 0.2, playerWidth));
							rightSidePlayerArea.intersect(playerArea);
							
							if (rightSidePlayerArea.intersects(new Rectangle2D.Double(obstacleX, obstacleY, 1, 1))) {
								// The right side of the player intersects with this obstacle
								return true;
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {}
				}
			}
		}
		
		return false;
	}
	
	/** Method Name: isTouchingYellowPad()
	 * @Author Colin Toft
	 * @Date January 19th, 2020
	 * @Modified N/A
	 * @Description Determines if a player is touching a yellow pad
	 * @Returns True the player is currently touching a yellow pad, otherwise false
	 * Data Type: Area, int, Ellipse2D, Rectangle2D, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private boolean isTouchingYellowPad() {
		Area playerArea = new Area(new Ellipse2D.Double(playerX, playerY, playerWidth, playerWidth)); // Calculate the player's area
				
		// Loop through all obstacles near the player
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth); obstacleY >= 0; obstacleY--) {
				try {
					if (level.obstacles[obstacleX][obstacleY] == Obstacle.YELLOW_PAD) {
						Area padArea = new Area(new Rectangle2D.Double(obstacleX, obstacleY, 1, 0.25)); // If the obstacle is a yellow pad, calculate its area
						padArea.intersect(playerArea); // Determine if the pad and player intersect
						
						if (!padArea.isEmpty()) {
							return true; // If there is an intersection, the player is touching the yellow pad
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}
		
		return false;
	}
	
	/** Method Name: isTouchingYellowRing()
	 * @Author Colin Toft
	 * @Date January 19th, 2020
	 * @Modified N/A
	 * @Description Determines if a player is touching a yellow ring
	 * @Returns True the player is currently touching a yellow ring, otherwise false
	 * Data Type: Area, int, Ellipse2D, Rectangle2D, Obstacle
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private boolean isTouchingYellowRing() {
		Area playerArea = new Area(new Ellipse2D.Double(playerX, playerY, playerWidth, playerWidth)); // Calculate the player's area
				
		// Loop through all obstacles near the player
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth); obstacleY >= 0; obstacleY--) {
				try {
					if (level.obstacles[obstacleX][obstacleY] == Obstacle.YELLOW_RING) {
						Area ringArea = new Area(new Ellipse2D.Double(obstacleX - 0.25, obstacleY - 0.25, 1.5, 1.5)); // If the obstacle is a yellow pad, calculate its area
						ringArea.intersect(playerArea); // Determine if the pad and player intersect
						
						if (!ringArea.isEmpty()) {
							return true; // If there is an intersection, the player is touching the yellow pad
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}
		
		return false;
	}
	
	/** Method Name: updateMode()
	 * @Author Colin Toft
	 * @Date January 15th, 2020
	 * @Modified January 16th, 2020
	 * @Description Determines if a player is travelling through a portal and changes their mode appropriately
	 * @Returns N/A
	 * Data Type: int, Obstacle, boolean
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private void updateMode() {
		int xCoord = (int) playerX; // The x coordinate to check
		int bottomY = (int) playerY; // The bottom y coordinate to check (the bottom of the player)
		int topY = bottomY + 1; // The top y coordinate to check (the top of the player)
		
		Obstacle bottomObstacle, topObstacle; // The obstacles at the top and bottom y coordinates, respectively
		try {
			// Find the obstacles at the top and bottom coordinate
			bottomObstacle = level.obstacles[xCoord][bottomY];
			topObstacle = level.obstacles[xCoord][topY];
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		
		if (triangleMode) { // The player is in triangle mode, so look for a circle portal
			if (bottomObstacle != null && bottomObstacle.isCirclePortal() || topObstacle != null && topObstacle.isCirclePortal()) {
				triangleMode = false; // The player is touching a circle portal, so begin circle mode
			}
		} else { // The player is in circle mode, so look for a triangle portal
			if (bottomObstacle != null && bottomObstacle.isTrianglePortal() || topObstacle != null && topObstacle.isTrianglePortal()) {
				triangleMode = true; // The player is touching a triangle portal, so begin triangle mode
				playerRotation = 0;
			}
		}
		
		if (upsideDownMode) { // The player is in upside down mode, so look for a right side up portal
			if (bottomObstacle != null && bottomObstacle.isRightSideUpPortal() || topObstacle != null && topObstacle.isRightSideUpPortal()) {
				upsideDownMode = false; // The player is touching a right side up portal, so turn off upside down mode
			}
		} else { // The player is in circle mode, so look for a triangle portal
			if (bottomObstacle != null && bottomObstacle.isUpsideDownPortal() || topObstacle != null && topObstacle.isUpsideDownPortal()) {
				upsideDownMode = true; // The player is touching an upside down portal, so begin upside down mode
			}
		}
	}
	
	/** Method Name: getBlockSize()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 14th & 15th, 2020
	 * @Description Calculates the width of one block/obstacle in pixels
	 * @Returns The size of one block in pixels
	 * Data Type: int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public double getBlockSize() {
		return pixelHeight() / levelHeight;
	}
	
	/** Method Name: blockXToPixelX()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified N/A
	 * @Description Takes an x value in blocks and converts it to an x value in pixels
	 * @Returns The x value in pixels of the original x coordinate
	 * Data Type: int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public int blockXToPixelX(double blockX) {
		return (int) Math.round((blockX - playerX) * getBlockSize() + (playerScreenX * pixelWidth()));
	}
	
	/** Method Name: blockYToPixelY()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified N/A
	 * @Description Takes an y value in blocks and converts it to a y value in pixels
	 * @Returns The y value in pixels of the original y coordinate
	 * Data Type: int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public int blockYToPixelY(double blockY) {
		return (int) Math.round((1 - groundHeight) * pixelHeight() - blockY * getBlockSize());
	}
	
	/** Method Name: pixelXToBlockX()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified N/A
	 * @Description Takes an x value in pixels and converts it to an x value in blocks
	 * @Returns The x value in blocks of the original x coordinate
	 * Data Type: int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public double pixelXToBlockX(int pixelX) {
		return (pixelX - (playerScreenX * pixelWidth())) / getBlockSize() + playerX;
	}
	
	/** Method Name: pixelYToBlockY()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified N/A
	 * @Description Takes a y value in pixels and converts it to a y value in blocks
	 * @Returns The y value in blocks of the original y coordinate
	 * Data Type: int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public double pixelYToBlockY(int pixelY) {
		return (pixelY - (1 - groundHeight) * pixelHeight()) / -getBlockSize();
	}
	
	/** Method Name: screenXToBlockX()
	 * @Author Colin Toft
	 * @Date January 7th, 2019
	 * @Modified N/A
	 * @Description Takes an x value in screen coordinates (fraction of the screen width) and converts it to an x value in blocks
	 * @Returns The x value in blocks of the original x coordinate
	 * Data Type: int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public double screenXToBlockX(double screenX) {
		return pixelXToBlockX((int) Math.round(screenX * pixelWidth()));
	}
	
	/** Method Name: screenYToBlockY()
	 * @Author Colin Toft
	 * @Date January 7th, 2019
	 * @Modified N/A
	 * @Description Takes a y value in screen coordinates (fraction of the screen height) and converts it to a y value in blocks
	 * @Returns The y value in blocks of the original y coordinate
	 * Data Type: int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public double screenYToBlockY(double screenY) {
		return pixelYToBlockY((int) Math.round(screenY * pixelHeight()));
	}
	
	/** Method Name: keyPressed()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified December 31st, 2019, January 19th, 2020
	 * @Description Overrides Scene.keyPressed() and handles key presses while playing a level (pressing space to jump)
	 * @Parameters
	 *      - KeyEvent e: the event containing data about the key press event
	 * @Returns N/A
	 * Data Type: KeyEvent, boolean
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE && !hasBeatLevel && !hasDied) {
			jumping = true; // When the space key is pressed, start jumping
		}
	}
	
	/** Method Name: keyReleased()
	 * @Author Colin Toft
	 * @Date December 31st, 2019
	 * @Modified N/A
	 * @Description Overrides Scene.keyReleased() and handles key releases while playing a level (releasing space to stop jumping)
	 * @Parameters
	 *      - KeyEvent e: the event containing data about the key press event
	 * @Returns N/A
	 * Data Type: KeyEvent, boolean
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			jumping = false; // When the space key is released, stop jumping
			holding = false;
		}
	}
	
	/** Method Name: onMousePressed()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 19th, 2020
	 * @Description Overrides Scene.onMousePressed() and handles mouse presses while playing a level (clicking to jump)
	 * @Parameters
	 *      - double x: the x coordinate of the mouse (as a fraction of the parent panel's width)
	 *      - double y: the y coordinate of the mouse (as a fraction of the parent panel's height)
	 *      - int button: the button on the mouse that was used
	 * @Returns N/A
	 * Data Type: double, int, boolean
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void onMousePressed(double x, double y, int button) {
		if (!hasBeatLevel && !hasDied) {
			jumping = true; // When the mouse is clicked, start jumping
		}
	}
	
	/** Method Name: onMouseReleased()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified N/A
	 * @Description Overrides Scene.onMouseReleased() and handles mouse releases while playing a level (releasing the mouse to stop jumping)
	 * @Parameters
	 *      - double x: the x coordinate of the mouse (as a fraction of the parent panel's width)
	 *      - double y: the y coordinate of the mouse (as a fraction of the parent panel's height)
	 *      - int button: the button on the mouse that was used
	 * @Returns N/A
	 * Data Type: double, int, boolean
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void onMouseReleased(double x, double y, int button) {
		jumping = false; // When the mouse is released, stop jumping
		holding = false;
	}
	
	/** Method Name: jump()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 10th, 13th & 22nd, 2020
	 * @Description Makes the player jump upwards
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: double, boolean
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	private void jump() {
		ySpeed = upsideDownMode ? -jumpYSpeed : jumpYSpeed; // Set the y speed to the jump y speed value to cause the player to move upwards
		jumpCount++; // Setting this to true will remove the jumping tutorial message
	}
	
	/** Method Name: startNextAttempt()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 9th, 13th, 14th, 15th, 18th & 22nd
	 * @Description Restarts the player from the beginning of the level
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: PlayLevel, double, boolean, int, Clip
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void startNextAttempt() {
		((PlayLevel) parentPanel).restartLevel();
		
		playerRotation = 0;
		
		if (practiceMode) {
			// If the player just after a checkpoint, increase the checkpoint death count
			if (playerX - checkpointX < 7) {
				checkpointDeathCount++;
			}
			
			// If the player keeps dying after the checkpoint, delete it
			if (checkpointDeathCount >= 3 && prevCheckpointX != checkpointX) {
				deleteCheckpoint();
			}
			
			// Respawn from the previous checkpoint
			playerX = checkpointX;
			playerY = checkpointY;
			ySpeed = checkpointYSpeed;
			triangleMode = checkpointTriangleMode;
			upsideDownMode = checkpointUpsideDownMode;
			
			// Set the starting ground height
			if (triangleMode || upsideDownMode) {
				groundHeight = 0.5 / levelHeight;
			} else {
				groundHeight = Math.min(baseGroundHeight, groundHeightThreshold - (playerY * getBlockSize() / pixelHeight()));
			}
			
		} else {
			stopMusic();

			// Begin with the player just to the left of the level
			playerX = -10;
			playerY = 0;
			ySpeed = 0;
			
			// Delete any checkpoints
			checkpointX = playerX;
			checkpointY = playerY;
			checkpointYSpeed = ySpeed;
			prevCheckpointX = checkpointX;
			prevCheckpointY = checkpointY;
			prevCheckpointYSpeed = checkpointYSpeed;
			
			groundHeight = baseGroundHeight; // Set the starting ground height
			triangleMode = false; // The level always starts in circle mode, not triangle mode
		}
		
		lastGroundY = playerY; // Reset the lastGroundY

		// Reset other variables
		jumping = false;
		holding = false;
		hasDied = false;
		hasBeatLevel = false;
		
	}
	
	/** Method Name: restartLevel()
	 * @Author Colin Toft
	 * @Date January 14th, 2020
	 * @Modified N/A
	 * @Description Restarts the player from the beginning of the level and from their first attempt
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: boolean, int
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public void restartLevel() {
		practiceMode = false; // Begin the level in normal mode for the 1st attempt
		winTimer = 0;
		startNextAttempt(); // Call the normal procedure to start an attempt
		playerX = -15; // Player starts a little further to the left than normal on the 1st attempt
	}

	/** Method Name: getScrollSpeed()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 14th
	 * @Description Calculates the level scrolling speed
	 * @Parameters N/A
	 * @Returns Scroll speed in screen widths per second
	 * Data Type: boolean, int, double
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public double getScrollSpeed() {
		if (!hasDied) {
			return xSpeed / (pixelWidth() / getBlockSize());
		} else {
			return 0; // The level does not scroll after the player dies
		}
	}

	/** Method Name: getPlayerProgress()
	 * @Author Colin Toft
	 * @Date January 8th, 2020
	 * @Modified N/A
	 * @Description Calculates the player's current progress in the level
	 * @Parameters N/A
	 * @Returns The players progress in the level on a scale from 0 to 1
	 * Data Type: boolean, int
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	public double getPlayerProgress() {
		return Util.constrain(playerX / level.width, 0, 1);
	}

	/** Method Name: exitingToMenu()
	 * @Author Colin Toft
	 * @Date January 8th, 2020
	 * @Modified January 17th, 2020
	 * @Description Called when the scene is about to exit back to the main menu, stops the currently playing music
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: N/A
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void exitingToMenu() {
		stopMusic();
	}
	
	/** Method Name: onPause()
	 * @Author Colin Toft
	 * @Date January 8th, 2020
	 * @Modified N/A
	 * @Description Overrides Scene.onPause(): stops the music when the level is paused
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: N/A
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void onPause() {
		super.onPause();
		stopMusic();
	}
	
	/** Method Name: onResume()
	 * @Author Colin Toft
	 * @Date January 8th, 2020
	 * @Modified January 19th, 2020
	 * @Description Overrides Scene.onResume(): resumes the music when the level is unpaused
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: N/A
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	@Override
	public void onResume() {
		super.onResume();
		// Resume the music if necessary
		if (playerX >= 0 && !playingMusic && (!hasDied || practiceMode)) {
			resumeMusic();
		}
	}

	/** Method Name: changeMode()
	 * @Author Colin Toft
	 * @Date January 9th, 2020
	 * @Modified N/A
	 * @Description Toggles the mode between practice mode and normal mode
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: boolean
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void changeMode() {
		practiceMode = !practiceMode; // Toggle between practice mode and normal mode
		stopMusic();
		if (!practiceMode) {
			// If the player is switching into normal mode, they need to start the level from the beginning
			startNextAttempt();
		} else if (playerX >= 0) {
			startMusic(); // If the player is switching into practice mode, start the practice mode music
		}
	}
	
	/** Method Name: isPracticeMode()
	 * @Author Colin Toft
	 * @Date January 9th, 2020
	 * @Modified N/A
	 * @Description Returns if the level is currently being played in practice mode
	 * @Parameters N/A
	 * @Returns True if practice mode is enabled, otherwise false
	 * Data Type: boolean
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean isPracticeMode() {
		return practiceMode;
	}
	
	/** Method Name: isTriangleMode()
	 * @Author Colin Toft
	 * @Date January 17th, 2020
	 * @Modified N/A
	 * @Description Returns if the player is currently in triangle mode
	 * @Parameters N/A
	 * @Returns True if triangle mode is enabled, otherwise false
	 * Data Type: boolean
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public boolean isTriangleMode() {
		return triangleMode;
	}
	
	/** Method Name: startMusic()
	 * @Author Colin Toft
	 * @Date January 9th, 2020
	 * @Modified N/A
	 * @Description Starts the music from the beginning (level music if playing in normal mode, otherwise practice music)
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: boolean, Clip
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void startMusic() {
		playingMusic = true;
		// Choose the appropriate music depending on the mode and start playing it from the beginning
		if (practiceMode) {
			practiceMusic.setFramePosition(0);
			practiceMusic.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			music.setFramePosition(0);
			music.start();
		}
	}
	
	/** Method Name: resumeMusic()
	 * @Author Colin Toft
	 * @Date January 9th, 2020
	 * @Modified N/A
	 * @Description Resumes the music (level music if playing in normal mode, otherwise practice music)
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: boolean, Clip
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void resumeMusic() {
		playingMusic = true;
		// Choose the appropriate music depending on the mode and start playing it
		if (practiceMode) {
			practiceMusic.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			music.start();
		}
	}
	
	/** Method Name: stopMusic()
	 * @Author Colin Toft
	 * @Date January 9th, 2020
	 * @Modified N/A
	 * @Description Stops all currently playing music and sounds
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: boolean, Clip
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void stopMusic() {
		// Stop all music and sounds
		music.stop();
		practiceMusic.stop();
		winSound.stop();
		deathSound.stop();
		playingMusic = false;
	}
}
