package tak.com;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Piece {

	/**
	 * Possible values for pieces - 10, 20, 30, 40
	 * Possible front colors - orange, blue, green
	 * Possible back colors - black, white
	 */
	
	private int value;
	private Color backColor;
	private Color frontColor;
	
	public Piece() {
		value = 0;
		backColor = Color.white;
		frontColor = Color.orange;
	}
	
	public Piece(int _value, Color _frontColor, Color _backColor) {
		value = _value;
		frontColor = _frontColor;
		backColor = _backColor;
	}
	
	public void setValue(int _value) {
		value = _value;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setBackgroundColor(Color _backColor) {
		backColor = _backColor;
	}
	
	public Color getBackgroundColor() {
		return backColor;
	}
	
	public void setForegroundColor(Color _frontColor) {
		frontColor = _frontColor;
	}
	
	public Color getForegroundColor() {
		return frontColor;
	}
	
	public void draw(Graphics2D g, int x, int y) {
		//Big square is 90, because each space is 100 in length/height
		g.setColor(backColor);
		g.fillRect(x + 10, y + 8, 75, 75);
		
		//Smaller square, need to rotate
		g.setColor(frontColor);
		g.fillRect(x + 28, y + 25, 40, 40);
		
		//Set the color to the opposite of the color of the piece
		g.setColor(new Color(255 - backColor.getRed(), 255 - backColor.getGreen(), 255 - backColor.getBlue()));
		g.setFont(new Font("Arial Bold", Font.BOLD, 18));
		g.drawString(String.valueOf(value), x + 38, y + 51);
	}

}
