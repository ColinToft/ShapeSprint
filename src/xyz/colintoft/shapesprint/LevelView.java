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

// Dec 30
public class LevelView extends Drawable {

	private Level level;
	
	private double ratio;
	
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
	private double prevCheckpointX = checkpointX;
	private double prevCheckpointY = checkpointY;
	private double prevCheckpointYSpeed = checkpointYSpeed;
	
	private boolean triangleMode = false;
	
	private BufferedImage playerCircleImage, playerTriangleImage;
	private BufferedImage backgroundImage, groundImage, checkpointImage;
	private double triangleImagePadding = 0.5 * playerWidth;
		
	HashMap<Obstacle, BufferedImage> images;
	
	private Clip music, practiceMusic;
	private Clip deathSound, winSound;
	
	// Dec 30 mod 9
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
	
	// Dec 30
	public LevelView(Level level) {
		this(0, 0, 1, 1, level);
	}
	
	// Dec 30
	public void start() {
		ratio = (double) parentPanel.pixelWidth() / parentPanel.pixelHeight();
	}
	
	// 30
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
	
	// 30 mod 8, 9, 13, 14, 15
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
	    
		int backgroundX = (int)(-getBlockSize() * (playerX * backgroundSpeed % (backgroundImage.getWidth() / getBlockSize())));
		if (backgroundX > 0) {
			backgroundX -= backgroundImage.getWidth();
	    }
	    g2d.drawImage(backgroundImage, backgroundX, pixelHeight() - backgroundImage.getHeight(), null);
	    g2d.drawImage(backgroundImage, backgroundX + backgroundImage.getWidth(), pixelHeight() - backgroundImage.getHeight(), null);
	    
	    int groundX = (int)(-getBlockSize() * (playerX % (groundTileWidth / getBlockSize())));
	    if (groundX > 0) {
	    	groundX -= groundTileWidth;
	    }
	    g2d.drawImage(groundImage, groundX, (int)(pixelHeight() * (1 - groundHeight)), null);
	    
	    g2d.setStroke(new BasicStroke(2f));
	    g2d.setColor(Color.WHITE);
	    g2d.drawLine(0, (int)(pixelHeight() * (1 - groundHeight)), pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)));
	    
	    //g2d.fillRect(0, 0, pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)));
	    
	    if (!hasDied) {
	    	int playerImageX, playerImageY;
	    	if (hasBeatLevel) {
	    		int beginX = blockXToPixelX(level.width);
	    		int endX = blockXToPixelX(level.width + levelEndOffset);
	    		double endProgress = winTimer / winAnimationLength;
	    		endProgress *= endProgress;
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
	
	// 30 mod 7, 9, 10, 13, 14, 15
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
		
		if (triangleMode) {
			if (jumping) {
				ySpeed = Math.min(triangleMaxYSpeed, ySpeed + triangleYSpeedIncrease * dt);
			}
			playerRotation = Math.atan2(-ySpeed, xSpeed);
		}
		
		double targetGroundHeight = Math.min(baseGroundHeight, groundHeightThreshold - (lastGroundY * getBlockSize() / pixelHeight()));
		if (targetGroundHeight < groundHeight && !hasBeatLevel && !hasDied) {
			groundHeight = Math.max(targetGroundHeight, groundHeight - groundHeightMoveSpeed * dt);
		} else if (targetGroundHeight > groundHeight && !hasBeatLevel && !hasDied) {
			groundHeight = Math.min(targetGroundHeight, groundHeight + groundHeightMoveSpeed * dt);
		}

		if (!hasDied && shouldDie()) {
			hasDied = true;
			deathTimer = 0;
			if (!practiceMode) {
				stopMusic();
			}
			deathSound.setFramePosition(0);
			deathSound.start();
		}
		
		updateMode();
		
		if (practiceMode && !hasDied && !hasBeatLevel && justLanded && playerX - checkpointX > 15) {
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
			stopMusic();
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
			startMusic();
			playerX = Math.max(checkpointX, 0);
		}
	}
	
	// 9
	private void createCheckpoint() {
		prevCheckpointX = checkpointX;
		prevCheckpointY = checkpointY;
		prevCheckpointYSpeed = checkpointYSpeed;
		checkpointX = playerX;
		checkpointY = playerY;
		checkpointYSpeed = ySpeed;
	}
	
	// 7
	private double getMinY() {
		double circleRadius = playerWidth * 0.5;
		double playerCenterX = playerX + circleRadius;
		
		double minY = 0;
		
		for (int obstacleX = (int) playerX; obstacleX <= (int) playerX + 1; obstacleX++) {
			for (int obstacleY = (int)(playerY + playerWidth); obstacleY >= 0; obstacleY--) {
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
	
	// 7
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
	
	// 15
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
	
	// 30 mod 14, 15
	public double getBlockSize() {
		return pixelHeight() / levelHeight;
	}
	
	// 31
	public int blockXToPixelX(double blockX) {
		return (int) Math.round((blockX - playerX) * getBlockSize() + (playerScreenX * pixelWidth()));
	}
	
	// 31
	public int blockYToPixelY(double blockY) {
		return (int) Math.round((1 - groundHeight) * pixelHeight() - blockY * getBlockSize());
	}
	
	// 31
	public double pixelXToBlockX(int pixelX) {
		return (pixelX - (playerScreenX * pixelWidth())) / getBlockSize() + playerX;
	}
	
	// 31
	public double pixelYToBlockY(int pixelY) {
		return (pixelY - (1 - groundHeight) * pixelHeight()) / -getBlockSize();
	}
	
	// 7
	public double screenXToBlockX(double screenX) {
		return pixelXToBlockX((int) Math.round(screenX * pixelWidth()));
	}
	
	// 7
	public double screenYToBlockY(double screenY) {
		return pixelYToBlockY((int) Math.round(screenY * pixelHeight()));
	}
	
	// 30 mod 31
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			jumping = true;
		}
	}
	
	// 31
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			jumping = false;
			holding = false;
		}
	}
	
	// 30
	@Override
	public void onMousePressed(double x, double y, int button) {
		jumping = true;
	}
	
	@Override
	public void onMouseReleased(double x, double y, int button) {
		jumping = false;
		holding = false;
	}
	
	// 30 mod 10, 13
	private void jump() {
		ySpeed = jumpYSpeed;
		((PlayLevel) parentPanel).hideJumpHelp();
		hasJumped = true;
	}
	
	// 7 mod 9, 13, 14, 15
	public void startNextAttempt() {
		((PlayLevel) parentPanel).restartLevel();
		
		playerRotation = 0;
		
		if (practiceMode) {
			System.out.println("Restarting player from checkpointX " + checkpointX);
			playerX = checkpointX;
			playerY = checkpointY;
			ySpeed = checkpointYSpeed;
			groundHeight = Math.min(baseGroundHeight, groundHeightThreshold - (playerY * getBlockSize() / pixelHeight()));;
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
		}
		
		lastGroundY = playerY;

		jumping = false;
		holding = false;
		hasDied = false;
		hasBeatLevel = false;
		
		triangleMode = false;
	}
	
	// 14
	public void restartLevel() {
		practiceMode = false;
		startNextAttempt();
		playerX = -15; // in blocks
	}

	// 7 mod 14
	// Scroll speed in screen widths per second
	public double getScrollSpeed() {
		if (!hasDied) {
			return xSpeed / (pixelWidth() / getBlockSize());
		} else {
			return 0;
		}
	}

	// 8
	public double getPlayerProgress() {
		return Util.constrain(playerX / level.width, 0, 1);
	}

	// 8
	public void exitingToMenu() {
		stopMusic();
		level.updateNormalProgress(getPlayerProgress());
	}
	
	@Override
	// 8
	public void onPause() {
		super.onPause();
		stopMusic();
	}
	
	@Override
	// 8
	public void onResume() {
		super.onResume();
		if (playerX >= 0 && !playingMusic) {
			startMusic();
		}
	}

	// 9
	public void changeMode() {
		practiceMode = !practiceMode;
		stopMusic();
		if (!practiceMode) {
			startNextAttempt();
		} else if (playerX >= 0) {
			startMusic();
		}
	}
	
	// 9
	public boolean isPracticeMode() {
		return practiceMode;
	}
	
	// 9
	public void startMusic() {
		playingMusic = true;
		if (practiceMode) {
			practiceMusic.setFramePosition(0);
			practiceMusic.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			music.setFramePosition(0);
			music.start();
		}
	}
	
	// 9
	public void resumeMusic() {
		playingMusic = true;
		if (practiceMode) {
			practiceMusic.start();
		} else {
			music.start();
		}
	}
	
	//9
	public void stopMusic() {
		music.stop();
		practiceMusic.stop();
		winSound.stop();
		deathSound.stop();
		playingMusic = false;
	}
}
