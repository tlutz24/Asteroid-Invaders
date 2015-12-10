package Main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * 
 * @author Tyler Lutz
 * 
 * TODO: Implement ship in this class so it may be instantiated as an object in the game
 *
 */
public class Ship{
	
	/**Player position and variables*/
	int x, y, xNew, yNew;
	/**Player movement direction variables*/
	int xDirec, yDirec;
	/**Player points variable*/
	int pts;
	protected boolean readyToFire, dead, shot = false;

	/**Bullet 'x' coordinates*/
	List<Integer> bX;
	/**Bullet 'y' coordinates*/
	List<Integer> bY;
	
	/**Rectangle to represent player*/
	Rectangle p;
	
	/**Images for drawing the player*/
	Image player, exploded;
	
	/**List to hold rectangles representing bullets*/
	List<Rectangle> bullets = new ArrayList<Rectangle>();
	
	/**
	 * Constructor for the ship
	 */
	public Ship(){
		//create list for bullet x locations
		bX = new ArrayList<Integer>();
		//create list for bullet y locations
		bY = new ArrayList<Integer>();
		//reset player
		revive();
		readyToFire = true;
		try{
			player = ImageIO	//attempt to read local files
					.read(new File("Images/shipSmall.png"));
			exploded = ImageIO.read(new File("Images/playerExplosion.png"));
			p = new Rectangle(x, y, 30, 30);
		}catch(IOException e){
			player = null;
		}
	}
	
	/**
	 * Method to set location of player
	 * 
	 * @param X		integer to hold new x-coordinate of player
	 * @param Y		integer to hold new y-coordinate of player
	 */
	public void setLocation(int X, int Y){
		x = xNew = X;
		y = yNew = Y;
		p = new Rectangle(x, y, 30, 30);
	}
	
	/**
	 * Method to reset players to initial state
	 */
	public void revive(){
		//set player to not dead
		dead = false;
		//set players initial location
		setLocation(305, 650);
		//set players initial direction to null
		changeDirec('s');
		//reset players points
		pts = 0;
	}
	
	/**
	 * Method to set the direction the player is moving
	 * 
	 * @param direc		character to indicate the direction the player is moving
	 */
	public void changeDirec(char direc){
		switch(direc){
		case('u'):
			yDirec = -1;
			break;
		case('l'):
			xDirec = -1;
			break;
		case('d'):
			yDirec = 1;
			break;
		case('r'):
			xDirec = 1;
			break;
		default:
			xDirec = yDirec = 0;
		}
	}
	
	/**
	 * Method to add bullet to the list of bullets
	 */
	public void addBullet(){
		//if able to fire
		if(readyToFire)
		{
			//if 50 bullets are already created then prevent adding more bullets
			if(bullets.size() > 50)
				readyToFire = false;
			else{
				//otherwise create new bullet at tip of ship
				int y = yNew;
				int x = xNew + 15;
				bY.add(y);
				bX.add(x);
				//add the bullet to list of bullets
				bullets.add(new Rectangle(x, y, 3, 3));
				//set shot to true so bullets will move
				shot = true;
			}
		}
	}
	
	/**
	 * Method to handle moving and removing bullets in list
	 * 
	 * @param barriers		list of barriers on the game board - used to detect collisions
	 */
	public boolean shoot(List<Barrier> barriers)
	{
		//boolean variables to help readability of code
		boolean bulletsNotEmpty, offTop, intersectsBarrier;
		//if bullet has been shot 
		if(shot)
			//for each bullet in bullet list
			for(int i = 0; i < bullets.size(); i++)
			{
				//move it 3 pixels up per cycle
				bullets.get(i).y -= 3;
				//temp variable for code readability
				int y = bullets.get(i).y;
				//int x = bullets.get(i).x;
				//check that there are bullets in list
				bulletsNotEmpty = bullets.size() > 0;
				//check if bullet is off top of screen
				offTop = y < -3;
				//bulletsNotEmpty = bullets.size() > 0;
				//offTop = y < -3;
				//for each barrier in the list of barriers
				for(int j = 0; j < barriers.size(); j++)
				{
					//temp variable for code readability
					Barrier b = barriers.get(j);
					//variable to hold number of bits of barrier are hit
					int barrierHitIndex = b.isHit(bullets.get(i));
					//check that barrier has been hit at least once
					intersectsBarrier = barrierHitIndex != -1;
					//if bullet hits a barrier and the bullet list isn't empty
					if(bulletsNotEmpty && intersectsBarrier)
					{
						//remove bullet
						bullets.remove(i);
						//set ready to fire
						readyToFire = true;
						//remove bits of barrier hit by bullet
						b.hit(barrierHitIndex);
						return true;
					}
					//if bullet goes off the top of the screen and the bullet list isn't empty
					if(bulletsNotEmpty && offTop)
					{
						//remove bullet
						bullets.remove(i);
						//reset offTop
						offTop = false;
						//set ready to fire
						readyToFire = true;
						break;
					}
				
				}
			}
		//detect bullets leaving screen and reset shot
		if(bullets.size() == 0)
			{
				shot = false;
				readyToFire = true;
			}
		return false;
	}
	
	/**
	 * Method to draw the player to the game board
	 * 
	 * @param g		graphics object to draw player image with
	 */
	public void draw(Graphics g)
	{
		//if player is dead
		if(dead)
			//draw exploded image
			g.drawImage(exploded, xNew, yNew, null);
		//otherwise
		else
			//draw player's ship
			g.drawImage(player, xNew, yNew, null);
		//if the player has shot a bullet and is not dead
		if(shot && !dead)
			//for every bullet
			for(int i = 0; i < bullets.size(); i++)
				//draw the bullet to the game board
				g.fillRect(bullets.get(i).x, bullets.get(i).y, bullets.get(i).width, bullets.get(i).height);
	
	}
	
}
