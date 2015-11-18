package Main;

import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.File;
import java.util.Random;

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
	protected int x, y, xDirec, yDirec, size;
	Rectangle ast;
	Image asteroid;
	Random rnd;
	Asteroid(int X, int Y, int xDirection, int yDirection, int Size){
		rnd = new Random();
		try{
			switch(Size){
			case(1):
				{
					size = 25;
					asteroid = ImageIO	//attempt to read local files
							.read(new File("Images/Asteroids/smallAst.png"));
					break;
				}
			case(2):
				{
					size = 50;
					asteroid = ImageIO	//attempt to read local files
							.read(new File("Images/Asteroids/mediumAst.png"));
					break;
				}
			case(3):
				{
					size = 100;
					asteroid = ImageIO	//attempt to read local files
							.read(new File("Images/Asteroids/largeAst.png"));
					break;
				}
			default:
				{
					size = 0;
					break;
				}
			}
		}catch(Exception e){
			System.out.println("Error occured: " + e.toString());
		}
		ast = new Rectangle(x = X, y = Y, size, size);
		xDirec = xDirection;
		yDirec = yDirection;
		
	}
	
}
