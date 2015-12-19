package Main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tyler
 *
 */

public class Barrier{
	/**Variables to represent the barrier*/
	int x, y, width, height;
	/**List of Rectangles that make up barrier*/
	List<Rectangle> barrier = new ArrayList<Rectangle>();
	
	/**
	 * Default constructor for the Barrier class
	 * 
	 * @param x1		the x-position of the barrier
	 * @param y1		the y-position of the barrier
	 * @param strength	the strength (size) of the barrier
	 */
	Barrier(int x1, int y1, int strength){
		//set x-position
		x = x1;
		//set y-position
		y = y1;
		//set width and height equal to strength
		width = height = strength;
		//for each bit of the width
		for(int i = 0; i < width; i++)
			//and each bit of the height
			for(int j = 0; j < height; j++)
				//add a rectangle to the list representing the barrier
				barrier.add(new Rectangle(x+25*i, y+10*j, 25, 10));
		
	}
	
	/**
	 * Method to detect if the barrier has been hit
	 * 	-Used to detect if player has hit the barrier
	 * 
	 * @param r		rectangle to check for collision
	 * @return		return an integer, -1 for no collision, and the index of the collision otherwise
	 */
	public int isHit(Rectangle r)
	{
		//for each rectangle in the list representing the barrier
		for(int i = 0; i < barrier.size(); i++)
			//if they intersect with the parameter
			if(r.intersects(barrier.get(i)))
				//return the index
				return i;
		//if no hit registers then return a out of bounds index
		return -1;
	}
	
	/**
	 * Method to remove a bit of the barrier 
	 * 
	 * @param i 	index of the bit to remove
	 */
	public void hit(int i){
		//if the barrier isn't empty and the barrier at the index isn't null
		if(barrier.size() > 0 && barrier.get(i) != null)
			//remove the bit of the barrier
			barrier.remove(i);
	}	
	
	
	/**
	 * Method to detect hits and remove the bits of the barrier that were hit
	 * 
	 * @param r		Rectangle to check for collisions
	 * @return		return the number of bits of the barrier that were hit
	 */
	public int removeHit(Rectangle r)
	{
		//variable to count number of hits
		int hit = 0;
		//for each bit in the barrier
		for(int i = 0; i < barrier.size(); i++)
			//if the barrier isn't empty and the bit collides with the parameter
			if(barrier.size() > 0 && r.intersects(barrier.get(i)))
			{
				//remove the bit
				barrier.remove(i);
				//increment the number of hits
				hit++;
			}
		//return the number of hits
		return hit;
	}
	
	/**
	 * Method to draw the Barrier the the game board
	 * 
	 * @param g		Graphics object to draw the Barrier with
	 */
	public void draw(Graphics g){
		//temp variable to help readability
		Rectangle temp;
		//for every bit in the barrier
		for(int i = 0; i < barrier.size(); i++)
		{
			//set the temp variable
			temp = barrier.get(i);
			//draw the bit with the graphics object
			g.fillRect(temp.x, temp.y, temp.width, temp.height);
		}
	}
}
