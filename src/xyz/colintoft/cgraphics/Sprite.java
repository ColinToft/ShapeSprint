package xyz.colintoft.cgraphics;

public class Sprite extends Drawable {

	private String spriteName;
	
	public Sprite(double x, double y, double size, String name) {
		super(x, y, size);
		spriteName = name;
		setDynamic(false);
	}

	@Override
	public void generateImage() {
		setImage(spriteName);
	}
	
	
	
}
