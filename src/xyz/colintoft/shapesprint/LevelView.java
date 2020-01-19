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
@Modified
@Description A class that renders the level to the screen, including backgrounds, obstacles and the player, as well as playing the game music.
***********************************************
*/
public class LevelView extends Drawable {

	private Level level;
	
	private final double xSpeed = 10.386;
	private final double levelHeight = 11; // Height of the level in blocks
	private final double baseGroundHeight = 0.3;
	private double groundHeight = baseGroundHeight; // Fraction of the height of the screen that the ground takes up
	private final double groundHeightMoveSpeed = 0.5;
	private final double groundHeightThreshold = 0.55;
	private final double playerScreenX = 0.34; // Fraction of the width
	private final int levelEndOffset = 8;
	private final double backgroundSpeed = 0.125;
	
	private final double playerWidth = 1;
	private int groundTileWidth;
	
	private final double playerRotationSpeed = xSpeed / (playerWidth * 0.5);
	private double playerRotation = 0;
	
	private double playerX = -15; // in blocks
	private double playerY = 0; // in blocks (0 is ground level)
	
	private boolean hasDied = false;
	private double deathTimer = 0;
	public boolean hasBeatLevel = false;
	private double winTimer = 0;
	private double winAnimationLength = 1;
	
	private boolean jumping = false;
	private boolean holding = false;
	public boolean hasJumped = false;
	private boolean hasUsedTriangleMode = false;
	private double lastGroundY = 0;
		
	private boolean playingMusic = false;
	
	private double ySpeed = 0;
	private final double gravity = 0.876 * xSpeed * xSpeed;
	private final double minYSpeed = -2.6 * xSpeed;
	private final double jumpYSpeed = 2 * xSpeed;
	private final double triangleMaxYSpeed = 1.4 * xSpeed;
	private final double triangleMinYSpeed = -1 * xSpeed;
	private final double triangleYSpeedIncrease = 0.4 * xSpeed * xSpeed;
	
	private boolean practiceMode = false;
	private double checkpointX = playerX;
	private double checkpointY = playerY;
	private double checkpointYSpeed = ySpeed;
	private boolean checkpointTriangleMode = false;
	private int checkpointDeathCount = 0;
	private double prevCheckpointX = checkpointX;
	private double prevCheckpointY = checkpointY;
	private double prevCheckpointYSpeed = checkpointYSpeed;
	private boolean prevCheckpointTriangleMode = checkpointTriangleMode;
	
	private boolean triangleMode = false;
	
	private BufferedImage playerCircleImage, playerTriangleImage;
	private BufferedImage backgroundImage, groundImage, ceilingImage, checkpointImage;
	private double triangleImagePadding = 0.5 * playerWidth;
		
	HashMap<Obstacle, BufferedImage> images;
	
	private Clip music, practiceMusic;
	private Clip deathSound, winSound;
	
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
		setBackground(new Color(0, 0, 0, 0));
		setDynamic(true);
		
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
		
		Graphics g = backgroundImage.createGraphics();
		Color bgColor = new Color(level.backgroundColor.getRed(), level.backgroundColor.getGreen(), level.backgroundColor.getBlue(), 205);
	    g.setColor(bgColor);
	    g.fillRect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
	    g.dispose();
	    
	    // Load Ground Image
		groundTileWidth = (int)(pixelHeight() * groundHeight);
		BufferedImage groundTile = Util.loadImageFromFile(getClass(), "backgrounds/ground1.png");
		
		groundImage = Util.getEmptyImage((pixelWidth() / groundTileWidth + 2) * groundTileWidth, groundTileWidth);
		g = groundImage.createGraphics();
		for (int i = 0; i < groundImage.getWidth(); i += groundTileWidth) {
			g.drawImage(groundTile, i, 0, groundTileWidth, groundTileWidth, null);
		}
		
		g.setColor(bgColor);
	    g.fillRect(0, 0, groundImage.getWidth(), groundImage.getHeight());
	    g.setColor(new Color(0, 0, 0, 50));
	    g.fillRect(0, 0, groundImage.getWidth(), groundImage.getHeight());
	    g.dispose();
		
		// Load Ceiling Image
		BufferedImage ceilingTile = Util.loadImageFromFile(getClass(), "backgrounds/ceiling1.png");
		
		ceilingImage = Util.getEmptyImage((pixelWidth() / groundTileWidth + 2) * groundTileWidth, groundTileWidth);
		g = ceilingImage.createGraphics();
		for (int i = 0; i < ceilingImage.getWidth(); i += groundTileWidth) {
			g.drawImage(ceilingTile, i, 0, groundTileWidth, groundTileWidth, null);
		}
		
		g.setColor(bgColor);
	    g.fillRect(0, 0, ceilingImage.getWidth(), ceilingImage.getHeight());
	    g.setColor(new Color(0, 0, 0, 50));
	    g.fillRect(0, 0, ceilingImage.getWidth(), ceilingImage.getHeight());
	    g.dispose();
	    
	    // Load Player Image
		BufferedImage originalPlayerImage = Util.loadImageFromFile(getClass(), "players/PlayerCircle.png");
		playerCircleImage = Util.scaleImage(originalPlayerImage, (int)(getBlockSize() * playerWidth), (int)(getBlockSize() * playerWidth), false);
		
		originalPlayerImage = Util.loadImageFromFile(getClass(), "players/PlayerTriangle.png");
		playerTriangleImage = Util.getEmptyImage((int)(getBlockSize() * (1.5 * playerWidth + triangleImagePadding * 2)), (int)(getBlockSize() * (1 * playerWidth + triangleImagePadding * 2)));
		g = playerTriangleImage.createGraphics();
		g.drawImage(originalPlayerImage, (int)(getBlockSize() * triangleImagePadding), (int)(getBlockSize() * triangleImagePadding), (int)(getBlockSize() * 1.5 * playerWidth), (int)(getBlockSize() * playerWidth), null);
		
		// Load Obstacle Images
		images = new HashMap<Obstacle, BufferedImage>();
		BufferedImage image;
		for (Obstacle type: Obstacle.values()) {
			image = Util.loadImageFromFile(getClass(), type.getImageFilename());
			image = Util.scaleImage(image, (int)getBlockSize() + 1, (int)getBlockSize() + 1);
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
		Graphics2D g2d = (Graphics2D) g;
	    
		int backgroundX = (int)(-getBlockSize() * (playerX * backgroundSpeed % (backgroundImage.getWidth() / getBlockSize())));
		if (backgroundX > 0) {
			backgroundX -= backgroundImage.getWidth();
	    }
	    g2d.drawImage(backgroundImage, backgroundX, pixelHeight() - backgroundImage.getHeight(), null);
	    g2d.drawImage(backgroundImage, backgroundX + backgroundImage.getWidth(), pixelHeight() - backgroundImage.getHeight(), null);
	    
	    // Draw the ground
	    int groundX = (int)(-getBlockSize() * (playerX % (groundTileWidth / getBlockSize())));
	    if (groundX > 0) {
	    	groundX -= groundTileWidth;
	    }
	    
	    if (triangleMode) {
		    g2d.drawImage(ceilingImage, groundX, (int)(pixelHeight() * (0.5 / levelHeight) - ceilingImage.getHeight()), null);
		    
		    g2d.setStroke(new BasicStroke(2f));
		    g2d.setColor(Color.WHITE);
		    g2d.drawLine(0, (int)(pixelHeight() * (0.5 / levelHeight)), pixelWidth(), (int)(pixelHeight() * (0.5 / levelHeight)));
	    }
	    
	    g2d.drawImage(groundImage, groundX, (int)(pixelHeight() * (1 - groundHeight)), null);
	    
	    g2d.setStroke(new BasicStroke(2f));
	    g2d.setColor(Color.WHITE);
	    g2d.drawLine(0, (int)(pixelHeight() * (1 - groundHeight)) + 2, pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)) + 2);
	    
	    //g2d.fillRect(0, 0, pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)));
	    
	    if (!hasDied) {
	    	int playerImageX, playerImageY;
	    	if (hasBeatLevel) {
	    		int beginX = blockXToPixelX(level.width);
	    		int endX = blockXToPixelX(level.width + levelEndOffset);
	    		double endProgress = Math.pow(winTimer / winAnimationLength, 2.5);
	    		playerRotation = endProgress * Math.PI * 0.7;
			    playerImageX = (int)(beginX + (endX - beginX) * endProgress);
				playerImageY = blockYToPixelY(playerY + playerWidth + -7 * (endProgress) * (endProgress - 1.6));
	    	} else {
	    		playerImageX = (int)(pixelWidth() * playerScreenX);
	    		playerImageY = blockYToPixelY(playerY + playerWidth);
	    	}
			
	    	if (!hasBeatLevel || playerImageX < blockXToPixelX(level.width + levelEndOffset + playerWidth)) {
				AffineTransform backup = g2d.getTransform();
				AffineTransform rotate;
				if (triangleMode) {
					rotate = AffineTransform.getRotateInstance(playerRotation, playerTriangleImage.getWidth() / 2.0, playerTriangleImage.getHeight() / 2.0);
				    AffineTransformOp op = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR);
					int padding = (int)(triangleImagePadding * getBlockSize());
			    	g2d.drawImage(op.filter(playerTriangleImage, null), playerImageX - padding, playerImageY - padding, null);
				} else {
					rotate = AffineTransform.getRotateInstance(playerRotation, playerCircleImage.getWidth() / 2.0, playerCircleImage.getHeight() / 2.0);
				    AffineTransformOp op = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR);
			    	g2d.drawImage(op.filter(playerCircleImage, null), playerImageX, playerImageY, null);
				}
			   
			    g2d.setTransform(backup);
	    	}
	    }
	    
	    // Draw obstacles
	    Obstacle o;
	    for (int obstacleX = (int) screenXToBlockX(0); obstacleX < Math.min((int) screenXToBlockX(1) + 1, level.width); obstacleX++) {
	    	if (obstacleX < 0) {
	    		continue;
	    	}
	    	for (int obstacleY = 0; obstacleY < Math.min((int) screenYToBlockY(0) + 1, level.height); obstacleY++) {
    			o = level.obstacles[obstacleX][obstacleY];
    			if (o != null) {
    				g2d.drawImage(images.get(o), blockXToPixelX(obstacleX), blockYToPixelY(obstacleY + 1), null);
    			}
	    	}
	    }
	    
	    // Draw the end of the level
	    if (screenXToBlockX(1) > level.width + levelEndOffset) {
	    	g2d.setColor(Color.BLACK);
	    	g2d.fillRect(blockXToPixelX(level.width + levelEndOffset), 0, pixelWidth() - blockXToPixelX(level.width + levelEndOffset), (int)(pixelHeight() * Math.min(1, 1 - groundHeight)));
	    	g2d.setColor(Color.WHITE);
	    	g2d.fillRect(blockXToPixelX(level.width + levelEndOffset), 0, (int)(pixelWidth() * 0.005), (int)(pixelHeight() * Math.min(1, 1 - groundHeight)));
	    }
	    
	    if (practiceMode) {
	    	// Draw checkpoints
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
	 * @Modified January 7th, 9th, 10th, 13th, 14th, 15th, 17th & 19th, 2020
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
			playerRotation += playerRotationSpeed * dt;
			playerRotation %= 2 * Math.PI;
		}
		
		if (!hasDied && !hasBeatLevel) {
			playerX += xSpeed * dt;
		}
		
		double minY = getMinY();
		boolean justLanded = false;
		
		if (playerY > minY || ySpeed != 0) {
			if (!triangleMode) {
				ySpeed = Math.max(ySpeed - gravity * dt, minYSpeed);
			} else if (!jumping) {
				ySpeed = Math.max(ySpeed - triangleYSpeedIncrease * dt, triangleMode ? triangleMinYSpeed : minYSpeed);
			}
			
			playerY += ySpeed * dt;
		}
				
		if (playerY <= minY + 0.03 && ySpeed <= 0) {
			justLanded = ySpeed < 0;
			
			ySpeed = 0;
			playerY = minY;
			lastGroundY = playerY;
			if (jumping && !triangleMode && !hasDied && !hasBeatLevel) {
				jump();
				holding = true;
			}
		}
		
		double maxY = getMaxY();
		boolean circleHitCeiling = false;
		if (playerY >= maxY - playerWidth && ySpeed >= 0) {
			if (triangleMode) {
				ySpeed = 0;
				playerY = maxY - playerWidth;
			} else {
				circleHitCeiling = true;
			}
		}
		
		double targetGroundHeight;
		if (triangleMode) {
			if (jumping) {
				ySpeed = Math.min(triangleMaxYSpeed, ySpeed + triangleYSpeedIncrease * dt);
				if (!hasUsedTriangleMode) {
					((ShapeSprint) getGame()).hasUsedTriangleMode = true;
				}
			}
			if (playerY == 0) {
				playerRotation = Math.max(playerRotation - playerRotationSpeed * 0.5 * dt, 0);
			} else if (playerY == maxY - playerWidth) {
				playerRotation = Math.min(playerRotation + playerRotationSpeed * 0.5 * dt, 0);
			} else {
				playerRotation = Math.atan2(-ySpeed, xSpeed);
			}
			
			targetGroundHeight = 0.5 / levelHeight;
		} else {
			targetGroundHeight = Math.min(baseGroundHeight, groundHeightThreshold - (lastGroundY * getBlockSize() / pixelHeight()));
		}
		
		if (targetGroundHeight < groundHeight && !hasBeatLevel && !hasDied) {
			groundHeight = Math.max(targetGroundHeight, groundHeight - groundHeightMoveSpeed * dt);
		} else if (targetGroundHeight > groundHeight && !hasBeatLevel && !hasDied) {
			groundHeight = Math.min(targetGroundHeight, groundHeight + groundHeightMoveSpeed * dt);
		}

		if (!hasDied && (shouldDie() || circleHitCeiling)) {
			hasDied = true;
			deathTimer = 0;
			if (!practiceMode) {
				stopMusic();
			}
			deathSound.setFramePosition(0);
			deathSound.start();
		}
		
		updateMode();
		
		if (practiceMode && !hasDied && !hasBeatLevel && (justLanded || triangleMode) && playerX - checkpointX > 15) {
			createCheckpoint();
		}
		
		if (hasDied) {
			deathTimer += dt;
			if (deathTimer > 1) {
				deathSound.stop();
				startNextAttempt();
			}
			return;
		}
		
		if (playerX > level.width && !hasBeatLevel) {
			hasBeatLevel = true;
			winTimer = 0;
			if (practiceMode) {
				stopMusic();
			}
			winSound.setFramePosition(0);
			winSound.start();
		}
		
		if (hasBeatLevel) {
			winTimer += dt;
			if (winTimer > winAnimationLength + 0.7) {
				((PlayLevel) parentPanel).showWinScreen();
			}
		}
		
		if (!playingMusic && playerX >= 0 && !hasDied && !hasBeatLevel) {
			music.setFramePosition(0);
			startMusic();
			playerX = Math.max(checkpointX, 0);
		}
	}
	
	/** Method Name: createCheckpoint()
	 * @Author Colin Toft
	 * @Date January 9th, 2020
	 * @Modified N/A
	 * @Description Creates a checkpoint at the current player location.
	 * @Returns N/A
	 * Data Type: double, boolean, int
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	private void createCheckpoint() {
		prevCheckpointX = checkpointX;
		prevCheckpointY = checkpointY;
		prevCheckpointYSpeed = checkpointYSpeed;
		prevCheckpointTriangleMode = triangleMode;
		checkpointX = playerX;
		checkpointY = playerY;
		checkpointYSpeed = ySpeed;
		checkpointTriangleMode = triangleMode;
		checkpointDeathCount = 0;
	}
	
	/** Method Name: deleteCheckpoint()
	 * @Author Colin Toft
	 * @Date January 17th, 2020
	 * @Modified N/A
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
		double circleRadius = playerWidth * 0.5;
		double playerCenterX = playerX + circleRadius;
		
		double minY = 0;
		
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth - 0.00001); obstacleY >= 0; obstacleY--) {
				try {
					if (level.obstacles[obstacleX][obstacleY] != null && level.obstacles[obstacleX][obstacleY].isSolid()) {
						double blockMinY;
						if ((int)playerCenterX == obstacleX) {
							blockMinY = obstacleY + 1;
						} else {
							double cornerX = Math.round(playerX);
							blockMinY = obstacleY + 1 - Math.abs(Math.cos(Math.asin((cornerX - playerCenterX) / circleRadius))) * circleRadius;
						}
						if (minY < blockMinY) {
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
		double circleRadius = playerWidth * 0.5;
		double playerCenterX = playerX + circleRadius;
		
		double maxY = triangleMode ? levelHeight - 1 : 1000000;
		
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth); obstacleY < level.height; obstacleY++) {
				try {
					if (level.obstacles[obstacleX][obstacleY] != null && level.obstacles[obstacleX][obstacleY].isSolid()) {
						double blockMaxY;
						if ((int)playerCenterX == obstacleX) {
							blockMaxY = obstacleY;
						} else {
							double cornerX = Math.round(playerX);
							blockMaxY = obstacleY + Math.abs(Math.cos(Math.asin((cornerX - playerCenterX) / circleRadius))) * circleRadius;
						}
						if (maxY > blockMaxY) {
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
		Area playerArea = new Area(new Ellipse2D.Double(playerX, playerY, playerWidth, playerWidth));
				
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth); obstacleY >= 0; obstacleY--) {
				try {
					if (level.obstacles[obstacleX][obstacleY] == Obstacle.TRIANGLE) {
						GeneralPath triangleShape = new GeneralPath();
						triangleShape.moveTo(obstacleX, obstacleY);
						triangleShape.lineTo(obstacleX + 0.5, obstacleY + 1);
						triangleShape.lineTo(obstacleX + 1, obstacleY);
						triangleShape.closePath();
						Area triangleArea = new Area(triangleShape);
						triangleArea.intersect(playerArea);
						
						if (!triangleArea.isEmpty()) {
							return true;
						}
					} else if (level.obstacles[obstacleX][obstacleY] == Obstacle.TRIANGLE_UPSIDE_DOWN) {
						GeneralPath triangleShape = new GeneralPath();
						triangleShape.moveTo(obstacleX, obstacleY + 1);
						triangleShape.lineTo(obstacleX + 0.5, obstacleY);
						triangleShape.lineTo(obstacleX + 1, obstacleY + 1);
						triangleShape.closePath();
						Area triangleArea = new Area(triangleShape);
						triangleArea.intersect(playerArea);
						
						if (!triangleArea.isEmpty()) {
							return true;
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
			
			if (obstacleX > playerX) { // Check on the right side of the player to see if they are about to run into a square
				for (int obstacleY = (int)(playerY + playerWidth); obstacleY >= (int) playerY; obstacleY--) {
					try {
						if (level.obstacles[obstacleX][obstacleY] != null && level.obstacles[obstacleX][obstacleY].isSolid()) {
							Area rightSidePlayerArea = new Area(new Rectangle2D.Double(playerX + playerWidth * 0.8, playerY, playerWidth * 0.2, playerWidth));
							rightSidePlayerArea.intersect(playerArea);
							
							if (rightSidePlayerArea.intersects(new Rectangle2D.Double(obstacleX, obstacleY, 1, 1))) {
								return true;
							}
						}
					} catch (ArrayIndexOutOfBoundsException e) {}
				}
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
		int xCoord = (int) playerX;
		int bottomY = (int) playerY;
		int topY = bottomY + 1;
		
		Obstacle bottomObstacle, topObstacle;
		try {
			bottomObstacle = level.obstacles[xCoord][bottomY];
			topObstacle = level.obstacles[xCoord][topY];
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		
		if (triangleMode) {
			if (bottomObstacle != null && bottomObstacle.isCirclePortal() || topObstacle != null && topObstacle.isCirclePortal()) {
				triangleMode = false;
			}
		} else {
			if (bottomObstacle != null && bottomObstacle.isTrianglePortal() || topObstacle != null && topObstacle.isTrianglePortal()) {
				triangleMode = true;
				playerRotation = 0;
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
	 * @Modified December 31st, 2019
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
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			jumping = true;
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
			jumping = false;
			holding = false;
		}
	}
	
	/** Method Name: onMousePressed()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified N/A
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
		jumping = true;
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
		jumping = false;
		holding = false;
	}
	
	/** Method Name: jump()
	 * @Author Colin Toft
	 * @Date December 30th, 2019
	 * @Modified January 10th & 13th, 2020
	 * @Description Makes the player jump upwards
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: double, boolean
	 * Dependencies: CGraphics library (by Colin)
	 * Throws/Exceptions: N/A
	 */
	private void jump() {
		ySpeed = jumpYSpeed;
		hasJumped = true;
	}
	
	/** Method Name: startNextAttempt()
	 * @Author Colin Toft
	 * @Date January 7th, 2020
	 * @Modified January 9th, 13th, 14th, 15th & 18th
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
			if (playerX - checkpointX < 7) {
				checkpointDeathCount++;
			}
			
			if (checkpointDeathCount >= 3 && prevCheckpointX != checkpointX) {
				deleteCheckpoint();
			}
			
			playerX = checkpointX;
			playerY = checkpointY;
			ySpeed = checkpointYSpeed;
			triangleMode = checkpointTriangleMode;
			if (triangleMode) {
				groundHeight = 0.5 / levelHeight;
			} else {
				groundHeight = Math.min(baseGroundHeight, groundHeightThreshold - (playerY * getBlockSize() / pixelHeight()));
			}
			
		} else {
			stopMusic();

			playerX = -10;
			playerY = 0;
			ySpeed = 0;
			
			checkpointX = playerX;
			checkpointY = playerY;
			checkpointYSpeed = ySpeed;
			prevCheckpointX = checkpointX;
			prevCheckpointY = checkpointY;
			prevCheckpointYSpeed = checkpointYSpeed;
			
			groundHeight = baseGroundHeight;
			triangleMode = false;
		}
		
		lastGroundY = playerY;

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
		practiceMode = false;
		startNextAttempt();
		playerX = -15; // in blocks
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
			return 0;
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
	 * @Modified N/A
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
		if (playerX >= 0 && !playingMusic) {
			startMusic();
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
		practiceMode = !practiceMode;
		stopMusic();
		if (!practiceMode) {
			startNextAttempt();
		} else if (playerX >= 0) {
			startMusic();
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
	 * @Description Starts the music (level music if playing in normal mode, otherwise practice music)
	 * @Parameters N/A
	 * @Returns N/A
	 * Data Type: boolean, Clip
	 * Dependencies: N/A
	 * Throws/Exceptions: N/A
	 */
	public void startMusic() {
		playingMusic = true;
		if (practiceMode) {
			practiceMusic.setFramePosition(0);
			practiceMusic.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
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
		if (practiceMode) {
			practiceMusic.start();
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
		music.stop();
		practiceMusic.stop();
		winSound.stop();
		deathSound.stop();
		playingMusic = false;
	}
}
