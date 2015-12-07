package Main;

import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * 
 * @author Tyler Lutz
 * 
 * TODO: Implement the asteroids in this class so they may be instantiated as objects in the game
 * - thoughts - may need to be implemented with multiple png files
 * 		- 1 png per size - look up bitmapping to handle detection
 */
public class Asteroid {
	/**variables to hold the location, direction and size of the asteroid*/
	protected int x, y, xDirec, yDirec, size;
	/**Rectangle to represent asteroid*/
	Rectangle ast;
	/**Image object for asteroid*/
	Image asteroid;
	
	/**
	 * Default Constructor foe Asteroid class
	 * 
	 * @param X				x-position for the asteroid
	 * @param Y				y-position for the asteroid
	 * @param xDirection	x-direction for the asteroid
	 * @param yDirection	y-direction for the asteroid
	 * @param Size			size of the asteroid
	 */
	Asteroid(int X, int Y, int xDirection, int yDirection, int Size){
		//try creating the object
		try{
			//depending on the size of the asteroid to create
			switch(Size){
			//if small
			case(25):
					//set size to 25px
					size = 25;
					//read small image to Image object
					asteroid = ImageIO.read(new File("Images/Asteroids/smallAst.png"));
					break;
			//if medium
			case(50):
					//set size to 50px
					size = 50;
					//read medium image to Image object
					asteroid = ImageIO.read(new File("Images/Asteroids/mediumAst.png"));
					break;
			//if large-medium
			case(75):
					//set size to 75px
					size = 75;
					//read large-medium image to Image object
					asteroid = ImageIO.read(new File("Images/Asteroids/largeMedAst.png"));
					break;
			//if large
			case(100):
					//set size to 100px
					size = 100;
					//read large image to Image object
					asteroid = ImageIO.read(new File("Images/Asteroids/largeAst.png"));
					break;
			default:
					//default to a null asteroid
					size = 0;
			}
		//catch any errors
		}catch(Exception e){
			//and issue an command line print out of error
			System.out.println("Error occured: " + e.toString());
		}
		//set rectangle to represent asteroid
		ast = new Rectangle(x = X, y = Y, size, size);
		//set x-direction
		xDirec = xDirection;
		//set y-direction
		yDirec = yDirection;
		
	}
	
	/**
	 * Method to move asteroid
	 */
	public void moveAst(){
		//move by a unit in the y-direction
		y += yDirec;
		//move by a unit in the x-direction
		x += xDirec;
		//reset the rectangle representing the asteroid
		ast = new Rectangle(x, y, size, size);
	}
	
}
