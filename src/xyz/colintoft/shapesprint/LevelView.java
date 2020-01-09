package xyz.colintoft.shapesprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

import xyz.colintoft.cgraphics.Util;
import xyz.colintoft.cgraphics.components.Drawable;
import xyz.colintoft.cgraphics.components.Panel;
import xyz.colintoft.shapesprint.scenes.PlayLevel;

// Dec 30
public class LevelView extends Drawable {

	private Level level;
	
	private double ratio;
	
	private final double xSpeed = 10.386;
	private final double levelHeight = 10; // Height of the level in blocks
	private final double groundHeight = 0.3; // Fraction of the height of the screen that the ground takes up
	private final double playerScreenX = 0.3; // Fraction of the width
	
	private final double playerWidth = 1;
	private int groundTileWidth;
	
	private final double playerRotationSpeed = xSpeed / (playerWidth * 0.5);
	private double playerRotation = 0;
	
	private double playerX = -15; // in blocks
	private double playerY = 0; // in blocks (0 is ground level)
	
	private boolean jumping = false;
	private boolean holding = false;
		
	private double ySpeed = 0;
	private final double gravity = 0.876 * xSpeed * xSpeed;
	private final double minYSpeed = -2.6 * xSpeed;
	private final double jumpYSpeed = 1.94 * xSpeed;
	
	private boolean practiceMode = false;
	private double checkpointX = playerX;
	private double checkpointY = playerY;
	private double checkpointYSpeed = ySpeed;
	private double prevCheckpointX = checkpointX;
	private double prevCheckpointY = checkpointY;
	private double prevCheckpointYSpeed = checkpointYSpeed;
	
	
	BufferedImage playerImage, backgroundImage, groundImage;
		
	HashMap<Obstacle, BufferedImage> images;
	
	private Clip music;
	
	// Dec 30
	public LevelView(double x, double y, double width, double height, Level level) {
		super(x, y, width, height);
		setBackground(new Color(0, 0, 0, 0));
		setDynamic(true);
		
		this.level = level;
		
		// Load Music
		music = Util.getAudioClip(getClass(), level.musicFile);
	}
	
	// Dec 30
	public LevelView(Level level) {
		this(0, 0, 1, 1, level);
	}
	
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
		playerImage = Util.scaleImage(originalPlayerImage, (int)(getBlockSize() * playerWidth), (int)(getBlockSize() * playerWidth), false);
		
		// Load Obstacle Images
		images = new HashMap<Obstacle, BufferedImage>();
		BufferedImage image;
		for (Obstacle type: Obstacle.values()) {
			image = Util.loadImageFromFile(getClass(), type.getImageFilename());
			image = Util.scaleImage(image, (int)getBlockSize() + 1, (int)getBlockSize() + 1);
			images.put(type, image);
		}
		
		super.generateImage();
	}
	
	// 30
	@Override
	public void draw(Graphics g) {
		long start = System.nanoTime();
		Graphics2D g2d = (Graphics2D) g;
	    
	    g2d.drawImage(backgroundImage, 0, pixelHeight() - backgroundImage.getHeight(), null);
	    int groundX = (int)(-getBlockSize() * (playerX % (groundTileWidth / getBlockSize())));
	    if (groundX > 0) {
	    	groundX -= groundTileWidth;
	    }
	    g2d.drawImage(groundImage, groundX, (int)(pixelHeight() * (1 - groundHeight)), null);
	    
	    g2d.setStroke(new BasicStroke(2f));
	    g2d.setColor(Color.WHITE);
	    g2d.drawLine(0, (int)(pixelHeight() * (1 - groundHeight)), pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)));
	    
	    //g2d.fillRect(0, 0, pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)));
	    
	    int playerImageX = (int)(pixelWidth() * playerScreenX);
		int playerImageY = (int)(pixelHeight() * (1 - groundHeight) - getBlockSize() * (playerY + playerWidth));
		
		AffineTransform backup = g2d.getTransform();
	    AffineTransform rotate = AffineTransform.getRotateInstance(playerRotation, playerImage.getWidth() / 2.0, playerImage.getHeight() / 2.0);
	    AffineTransformOp op = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR);
	    g2d.drawImage(op.filter(playerImage, null), playerImageX, playerImageY, null);
	    g2d.setTransform(backup);
	    
	    // Draw obstacles
	    Obstacle o;
	    for (int obstacleX = (int) screenXToBlockX(0); obstacleX < Math.min((int) screenXToBlockX(1) + 1, level.width); obstacleX++) {
	    	if (obstacleX < 0) {
	    		continue;
	    	}
	    	for (int obstacleY = 0; obstacleY < Math.min((int) screenYToBlockY(0) + 1, level.height); obstacleY++) {
    			o = level.obstacles[obstacleX][obstacleY];
    			if (o != null) {
    				g2d.drawImage(images.get(o), blockXToPixelX(obstacleX), blockYToPixelY(obstacleY), null);
    			}
	    	}
	    }
	}
	
	// 30 mod 7
	@Override
	public void update(double dt) {
		playerRotation += playerRotationSpeed * dt;
		playerRotation %= 2 * Math.PI;
		
		playerX += xSpeed * dt;
		
		ySpeed = Math.max(ySpeed - gravity * dt, minYSpeed);
		playerY += ySpeed * dt;
		
		double minY = getMinY();
		
		if (playerY <= minY && ySpeed <= 0) {
			ySpeed = 0;
			playerY = minY;
			if (jumping) {
				jump();
				holding = true;
			}
		}

		if (shouldDie()) {
			die();
			return;
		}
		
		if (!music.isActive() && playerX >= 0) {
			System.out.println("Time to start music");
			music.start();
			playerX = 0;
			
			music.setMicrosecondPosition(0);
		}
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
	
	 // 30
	public double getBlockSize() {
		return ((1 - groundHeight) * pixelHeight()) / levelHeight;
	}
	
	// 31
	public int blockXToPixelX(double blockX) {
		return (int) Math.round((blockX - playerX) * getBlockSize() + (playerScreenX * pixelWidth()));
	}
	
	// 31
	public int blockYToPixelY(double blockY) {
		return (int) Math.round((1 - groundHeight) * pixelHeight() - (blockY + 1) * getBlockSize());
	}
	
	// 31
	public double pixelXToBlockX(int pixelX) {
		return (pixelX - (playerScreenX * pixelWidth())) / getBlockSize() + playerX;
	}
	
	// 31
	public double pixelYToBlockY(int pixelY) {
		return (pixelY - (1 - groundHeight) * pixelHeight()) / -getBlockSize() - 1;
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
	
	// 30
	private void jump() {
		ySpeed = jumpYSpeed;
	}
	
	// 7 mod 9
	private void die() {
		((PlayLevel) parentPanel).restart();

		music.stop();
		
		playerRotation = 0;
		
		if (practiceMode) {
			playerX = checkpointX;
			playerY = checkpointY;
			ySpeed = checkpointYSpeed;
		} else {
			playerX = -10; 
			playerY = 0;
			ySpeed = 0;
			
			checkpointX = playerX;
			checkpointY = playerY;
			prevCheckpointX = checkpointX;
			prevCheckpointY = checkpointY;
		}
		
		jumping = false;
		holding = false;
	}

	// 7
	// Scroll speed in screen widths per second
	public double getScrollSpeed() {
		return xSpeed / (pixelWidth() / getBlockSize());
	}

	// 8
	public double getPlayerProgress() {
		return Util.constrain(playerX / level.width, 0, 1);
	}

	// 8
	public void exitingToMenu() {
		music.stop();
		level.updateNormalProgress(getPlayerProgress());
	}
	
	@Override
	// 8
	public void onPause() {
		super.onPause();
		music.stop();
	}
	
	@Override
	// 8
	public void onResume() {
		super.onResume();
		if (playerX >= 0 && !music.isRunning()) {
			System.out.println("Starting on resume");
			music.start();
		}
	}

	// 9
	public void startPracticeMode() {
		practiceMode = true;
		music.stop();
		music = Util.getAudioClip(getClass(), "AsItShouldBe.wav");
		if (playerX >= 0) {
			System.out.println("Starting practice mode");
			music.start();
		}
	}
}
