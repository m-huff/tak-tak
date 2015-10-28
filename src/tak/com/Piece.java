package tak.com;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import tak.window.TakTakWindow;

public class Piece {

	/**
	 * Possible values for pieces - 10, 20, 30, 40
	 * Possible front colors - orange, blue, green
	 * Possible back colors - black, white
	 */
	
	private int value;
	private Color backColor;
	private Color frontColor;
	
	static ImageIcon white = new ImageIcon(Piece.class.getResource("/tak/assets/white.png"));
	static ImageIcon black = new ImageIcon(Piece.class.getResource("/tak/assets/black.png"));
	
	static ImageIcon blue = new ImageIcon(Piece.class.getResource("/tak/assets/blue.png"));
	static ImageIcon green = new ImageIcon(Piece.class.getResource("/tak/assets/green.png"));
	static ImageIcon orange = new ImageIcon(Piece.class.getResource("/tak/assets/orange.png"));
	
	// Going to add an arraylist here of the pieces in the stack. If this piece is not the top
	// of the stack it's in, it will become null and the contents of the array will be added to
	// the array in the piece which is now on top.
	
	// Dictates whether or not this piece is a king, still needs to be implemented.
	// Kings can move backwards, and two spaces forward. However, the stack they are
	// in is limited in value to 100 points, and once the king has been placed on the
	// stack, no other pieces can stack with it - the king will always be on top.
	boolean isKing;
	
	
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

		//Draw background image based on backColor
		g.drawImage((backColor == Color.black ? black.getImage() : white.getImage()), x + 10, y + 8, 75, 75, null);
		
		//Smaller square, image picked from frontColor
		g.drawImage((frontColor == Color.orange ? orange.getImage() : frontColor == Color.blue ?
		blue.getImage() : green.getImage()), x + 10, y + 8, 75, 75, null);

		g.setColor(backColor);
		g.setFont(new Font("Arial Bold", Font.PLAIN, 18));
		g.drawString(String.valueOf(value), x + 38, y + 53);
	}

}
