package xyz.colintoft.cgraphics.components;

public class Sprite extends Drawable {

	private String spriteName;
	
	public Sprite(double x, double y, double size, String name) {
		super(x, y, size);
		spriteName = name;
		setDynamic(false);
	}
	
	public Sprite(double x, double y, double width, double height, String name) {
		super(x, y, width, height);
		spriteName = name;
		setDynamic(false);
	}

	@Override
	public void generateImage() {
		setImage(spriteName);
	}
	
	
	
}
