package xyz.colintoft.shapesprint;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
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

// Dec 30
public class LevelView extends Drawable {

	private Level level;
	
	private double ratio;
	
	private final double xSpeed = 10.386;
	private final double levelHeight = 12; // Height of the level in blocks
	private final double groundHeight = 0.3; // Fraction of the height of the screen that the ground takes up
	private final double playerScreenX = 0.3; // Fraction of the width
	
	private final double playerWidth = 1;
	private int groundTileWidth;
	
	private final double playerRotationSpeed = xSpeed / (playerWidth * 0.5);
	private double playerRotation = 0;
	
	private double playerX = -10; // in blocks
	private double playerY = 0; // in blocks (0 is ground level)
	
	private double ySpeed = 0;
	private final double gravity = 0.876 * xSpeed * xSpeed;
	private final double minYSpeed = -2.6 - xSpeed;
	private final double jumpYSpeed = 1.94 * xSpeed;
	
	BufferedImage playerImage, backgroundImage, groundImage;
		
	HashMap<Obstacle.Type, BufferedImage> images;
	
	private Clip music;
	
	// Dec 30
	public LevelView(double x, double y, double width, double height, Level level) {
		super(x, y, width, height);
		setBackground(new Color(0, 0, 0, 0));
		setDynamic(true);
		
		this.level = level;
		
		// Load Obstacle Images
		images = new HashMap<Obstacle.Type, BufferedImage>();
		
		images.put(Obstacle.Type.SQUARE, 				Util.loadImageFromFile(getClass(), "obstacles/BlackSquare.png"));
		images.put(Obstacle.Type.SQUARE_TOP_1, 			Util.loadImageFromFile(getClass(), "obstacles/BlackSquareTop1.png"));
		images.put(Obstacle.Type.SQUARE_BOTTOM_1, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareBottom1.png"));
		images.put(Obstacle.Type.SQUARE_LEFT_1, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareLeft1.png"));
		images.put(Obstacle.Type.SQUARE_RIGHT_1, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareRight1.png"));
		images.put(Obstacle.Type.SQUARE_TOP_3, 			Util.loadImageFromFile(getClass(), "obstacles/BlackSquareTop3.png"));
		images.put(Obstacle.Type.SQUARE_BOTTOM_3, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareBottom3.png"));
		images.put(Obstacle.Type.SQUARE_LEFT_3, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareLeft3.png"));
		images.put(Obstacle.Type.SQUARE_RIGHT_3, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareRight3.png"));
		images.put(Obstacle.Type.SQUARE_TOP_LEFT, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareTopLeft.png"));
		images.put(Obstacle.Type.SQUARE_TOP_RIGHT, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareTopRight.png"));
		images.put(Obstacle.Type.SQUARE_BOTTOM_LEFT, 	Util.loadImageFromFile(getClass(), "obstacles/BlackSquareBottomLeft.png"));
		images.put(Obstacle.Type.SQUARE_BOTTOM_RIGHT, 	Util.loadImageFromFile(getClass(), "obstacles/BlackSquareBottomRight.png"));
		images.put(Obstacle.Type.SQUARE_CENTER, 		Util.loadImageFromFile(getClass(), "obstacles/BlackSquareCenter.png"));
		
		// Load Music
		music = Util.getAudioClip(getClass(), level.musicFile);
	
	}
	
	// Dec 30
	public LevelView(Level level) {
		this(0, 0, 1, 1, level);
	}

	// Dec 30
	@Override
	public void setParentPanel(Panel p) {
		super.setParentPanel(p);
	}
	
	public void start() {
		ratio = (double) parentPanel.pixelWidth() / parentPanel.pixelHeight();
	}
	
	// 30
	@Override
	public void generateImage() {				
		// Load Background Images
		BufferedImage originalBackground = Util.loadImageFromFile(getClass(), "backgrounds/background1classic.png");
		backgroundImage = Util.scaleImage(originalBackground, pixelWidth(), pixelWidth());
		Graphics g = backgroundImage.createGraphics();
		Color bgColor = new Color(level.backgroundColor.getRed(), level.backgroundColor.getGreen(), level.backgroundColor.getBlue(), 210);
	    g.setColor(bgColor);
	    g.fillRect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
	    g.dispose();
		
		groundTileWidth = (int)(pixelHeight() * groundHeight);
		BufferedImage groundTile = Util.loadImageFromFile(getClass(), "backgrounds/ground1.png");
		
		groundImage = Util.getEmptyImage((pixelWidth() / groundTileWidth + 2) * groundTileWidth, groundTileWidth);
		g = groundImage.createGraphics();
		for (int i = 0; i < groundImage.getWidth(); i += groundTileWidth) {
			g.drawImage(groundTile, i, 0, groundTileWidth, groundTileWidth, null);
		}
		
	    g.setColor(bgColor);
	    g.fillRect(0, 0, groundImage.getWidth(), groundImage.getHeight());
	    g.setColor(new Color(0, 0, 0, 40));
	    g.fillRect(0, 0, groundImage.getWidth(), groundImage.getHeight());
	    g.dispose();

		
		BufferedImage originalPlayerImage = Util.loadImageFromFile(getClass(), "players/PlayerCircle.png");
		playerImage = Util.scaleImage(originalPlayerImage, (int)(getBlockSize() * playerWidth), (int)(getBlockSize() * playerWidth), false);
		
		super.generateImage();
	}
	
	// 30
	@Override
	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
	    
	    g2d.drawImage(backgroundImage, 0, pixelHeight() - backgroundImage.getHeight(), null);
	    g2d.drawImage(groundImage, (int)(-getBlockSize() * (playerX % (groundTileWidth / getBlockSize()))), (int)(pixelHeight() * (1 - groundHeight)), null);
	    
	    //g2d.fillRect(0, 0, pixelWidth(), (int)(pixelHeight() * (1 - groundHeight)));
	    
	    int playerImageX = (int)(pixelWidth() * playerScreenX);
		int playerImageY = (int)(pixelHeight() * (1 - groundHeight) - getBlockSize() * (playerY + playerWidth));
		
		
		AffineTransform backup = g2d.getTransform();
	    AffineTransform rotate = AffineTransform.getRotateInstance(playerRotation, playerImage.getWidth() / 2.0, playerImage.getHeight() / 2.0);
	    AffineTransformOp op = new AffineTransformOp(rotate, AffineTransformOp.TYPE_BILINEAR);
	    g2d.drawImage(op.filter(playerImage, null), playerImageX, playerImageY, null);
	    g2d.setTransform(backup);
	}
	
	// 30
	@Override
	public void update(double dt) {
		playerRotation += playerRotationSpeed * dt;
		playerRotation %= 2 * Math.PI;
		
		playerX += xSpeed * dt;
		ySpeed = Math.max(ySpeed - gravity * dt, minYSpeed);
		playerY += ySpeed * dt;
		System.out.println(playerY);
		if (playerY < 0) {
			ySpeed = 0;
			playerY = 0;
		}

		if (!music.isRunning() && playerX > 0) {
			music.start();
		}
		
	}
	
	 // 30
	public double getBlockSize() {
		return ((1 - groundHeight) * pixelHeight()) / levelHeight;
	}
	
	// 30
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			jump();
		}
	}
	
	// 30
	@Override
	public void onMousePressed(double x, double y, int button) {
		jump();
	}
	
	// 30
	private void jump() {
		System.out.println("jump");
		ySpeed = jumpYSpeed;
	}
}
