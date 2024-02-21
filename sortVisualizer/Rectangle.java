package sortVisualizer;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Represents a rectangle used inside of the DrawPanel GUI
 * 
 * @author Jacob Anderson
 * @version 02/20/2024
 */
public class Rectangle {
	private int positionX;
	private int positionY;
	private Color color;
	private int width;
	private int height;
	private boolean dontupdate; // prevents rectangle from being redrawn

	/**
	 * Construct a Rectangle with the given position, size, and color
	 * 
	 * @param posX   - x position
	 * @param posY   - y position
	 * @param width
	 * @param height
	 * @param color
	 */
	public Rectangle(int posX, int posY, int width, int height, Color color) {
		positionX = posX;
		positionY = posY;
		this.color = color;
		this.width = width;
		this.height = height;
		dontupdate = false;
	}

	/**
	 * Returns the rectangle's height
	 * 
	 * @return the height of the rectangle
	 */
	public int getHeight() {
		return positionY;
	}

	/**
	 * Creates and returns a new rectangle with the xPosition and width of one
	 * rectangle (copX) and the yPosition, height and color of another rectangle
	 * (orig). also sets both rectangles to no longer update (get redrawn). Used in
	 * merge sort to allow one rectangle to overwrite another, since rectangles are
	 * copied over rather than swapped.
	 * 
	 * @param orig the rectangle whose height/yPosition to copy from
	 * @param pos  the rectangle whose xPosition/width to copy from
	 * @param g    graphics context
	 * @return a new rectangle as described above
	 */
	public static Rectangle mergeTwo(Rectangle orig, Rectangle copX, Graphics g) {
		Rectangle toRet = new Rectangle(copX.positionX, orig.positionY, copX.width, orig.height, orig.color);
		copX.dontupdate = true;
		orig.dontupdate = true;
		toRet.draw(g);
		return toRet;
	}

	/**
	 * Sets a rectangle's color to a new color
	 * 
	 * @param newC the new color
	 * @param g    graphics context
	 */
	public void setColor(Color newC, Graphics g) {
		color = newC;
		draw(g);
	}

	/**
	 * Swaps the X positions of two rectangles
	 * 
	 * @param first
	 * @param second
	 */
	public static void swap(Rectangle first, Rectangle second) {
		int temp = first.positionX;
		int temp2 = first.width;
		first.positionX = second.positionX;
		first.width = second.width;
		second.positionX = temp;
		second.width = temp2;
	}

	/**
	 * Returns the color of this rectangle
	 * 
	 * @return the color of this rectangle
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Clears the column this rectangle currently occupies and redraws this
	 * rectangle
	 * 
	 * @param g graphics context
	 */
	public void draw(Graphics g) {
		if (dontupdate)
			return;
		g.setColor(color);
		g.clearRect(positionX, 0, width, (height + positionY) * 1000);
		g.fillRect(positionX, positionY, width, height);
		g.setColor(Color.BLACK);

	}
}
