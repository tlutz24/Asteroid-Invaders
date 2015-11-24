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
	protected boolean readyToFire, shot = false;

	/**Bullet 'x' coordinates*/
	List<Integer> bX;
	/**Bullet 'y' coordinates*/
	List<Integer> bY;
	
	/**Rectangle to represent player*/
	Rectangle p;
	
	Image player;
	
	//Rectangle bullet;
	List<Rectangle> bullets = new ArrayList<Rectangle>();
	
	public Ship(){
		bX = new ArrayList<Integer>();
		bY = new ArrayList<Integer>();
		setLocation(305, 650);
		changeDirec('s');
		readyToFire = true;
		try{
			player = ImageIO	//attempt to read local files
					.read(new File("Images/shipSmall.png"));
			p = new Rectangle(x, y, 30, 30);
		}catch(IOException e){
			player = null;
		}
	}
	
	public void setLocation(int X, int Y){
		x = xNew = X;
		y = yNew = Y;
	}
	
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
	
	public void addBullet(){
		if(bullets.size() > 50)
			readyToFire = false;
		else{
			int y = yNew;
			int x = xNew + 15;
			bY.add(y);
			bX.add(x);
			bullets.add(new Rectangle(x, y, 3, 3));
			shot = true;
		}
	}
	
	public void shoot(List<Barrier> barriers)
	{
		if(shot)//if bullet has been shot move it 3 pixels up per cycle
			for(int i = 0; i < bullets.size(); i++)
			{
				bullets.get(i).y -= 3;
				int y = bullets.get(i).y;
				int x = bullets.get(i).x;
				for(int j = 0; j < barriers.size(); j++){
					Barrier b = barriers.get(j);
					if(y < -3 || (y > b.y && y < b.y + b.height && x > b.x && x < b.x + b.width)){
						bullets.remove(i);
						readyToFire = true;
					}
				}
			}
		if(bullets.size() == 0)//detect bullets leaving screen and reset shot
			{
				shot = false;
				//readyToFire = true;
			}
		
	}
	
	public void draw(Graphics g)
	{

		g.drawImage(player, xNew, yNew, null);
	}
	
}
