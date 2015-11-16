/**
 * 
 */
package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author Dylan Steele, Tyler Lutz
 *
 */
class AnPanel extends JPanel implements Runnable {
	private Graphics gImg;
	int x, y, xNew, yNew;//player position
	int xDirec, yDirec;//player movement direction
	int pts;//player points
	int bX, bY;//bullet location
	protected Image img, backImg;//ship and background image
	//protected Random rnd; <- Never used
	protected boolean title, readyToFire, shot = false;
	
	Rectangle bullet;
	
	//Fonts
	Font tFont = new Font("Monospaced", Font.ITALIC | Font.BOLD, 45);//title font
	Font sFont = new Font("Monospaced", Font.ITALIC, 15);//statistics font
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(true)
			{
				//draw player
				movePlyr(xDirec, yDirec);
				//handle shots
				shoot();
				//set speed of animation
				Thread.sleep(10);//lower = faster while higher = slower
			}
		}
		catch(Exception e)
		{
			System.out.println("Error");
		}
		
	}
	
	AnPanel() {
		super();
		xDirec = yDirec = 0;//no movement at start
		pts = 0;//player points
		//x = xNew = 305;//initial ship position
		//y = yNew = 650;
		title = true;//open game menu
		try {
			img = ImageIO	//attempt to read local files
					.read(new File("Images/shipSmall.png"));
			//backImg = ImageIO
			//		.read(new File("Images/background.png"));
			

		} catch (IOException e) {
			img = null;
		}
		this.addKeyListener(new InputKeyEvents());
		this.setFocusable(true);
		
		//rnd = new Random(); <- Never used
		
		//create thread and start it
		Thread th1 = new Thread(this);
		th1.start();
		
	}
	
	public void paint(Graphics g){
		backImg = createImage(getWidth(), getHeight());
		gImg = backImg.getGraphics();
		paintComponent(gImg);
		g.drawImage(backImg, 0, 0, this);
	}

	/**
	 * TODO: check intersection code and see if there is a better way of implementing
	 */
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		
		//set and draw barriers
		g.setColor(Color.red);
		Rectangle p = new Rectangle(xNew, yNew, 30, 30);//rectangle to represent player
		Rectangle b1 = new Rectangle(75, 600, 75, 30);//rectangles for barriers
		Rectangle b2 = new Rectangle(200, 600, 75, 30);
		Rectangle b3 = new Rectangle(350, 600, 75, 30);
		Rectangle b4 = new Rectangle(475, 600, 75, 30);
		g.fillRect(b1.x, b1.y,  b1.width,  b1.height);//draw barriers
		g.fillRect(b2.x, b2.y,  b2.width,  b2.height);
		g.fillRect(b3.x, b3.y,  b3.width,  b3.height);
		g.fillRect(b4.x, b4.y,  b4.width,  b4.height);
		
		//display title menu or game
		g.setColor(Color.WHITE);
		if(title)
		{
			x = xNew = 305;//set initial position
			y = yNew = 650;
			g.setFont(tFont);//display Menu text
			g.drawString("Asteroid Invaders!", 90, 150);
			g.drawString("Press Enter to Start!", 50, 350);
			g.drawImage(img, xNew, yNew, this);
		}
		else
		{
			g.setFont(sFont);//display game
			g.drawString("Press ESC to quit to menu.", 20, 35);
			g.drawString("Points: " + pts, 20, 50);
			g.drawImage(img, xNew, yNew, this);
			//draw shot
			if(shot)
				g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
		}
		//handle intersections with barriers
		if(p.intersects(b1) || p.intersects(b2) || p.intersects(b3) || p.intersects(b4))
		{
			if((xNew + 30 > b1.x && xNew < b1.x) && (yNew > b1.y && yNew < b1.y +b1.height))
				xNew = b1.x - 30;
			else if((xNew < b1.x +b1.width && xNew + 30 > b1.x +b1.width) && (yNew > b1.y && yNew < b1.y +b1.height))
				xNew = b1.x +b1.width;
			else if ((yNew + 30 > b1.y && yNew < b1.y))
				yNew = b1.y - 30;
			else if((yNew < b1.y + b1.height))
				yNew = b1.y + b1.height;
		}
		
		x = xNew;
		y = yNew;
		repaint();
	}
	
	public void movePlyr(int xDir, int yDir) {
		//check if move is allowed
		if (yDir == -1 && (title || yNew < 550))
			yDir = 0;//cancel move if not allowed
		if (xDir == -1 && (title || xNew < 0))
			xDir = 0;
		if (yDir == 1 && (title || yNew+30 > getSize().height))
			yDir = 0;
		if (xDir == 1 && (title || xNew+30 > getSize().width))
			xDir = 0;
		//move player
		xNew += (4*xDir);
		yNew += (4*yDir);	
	}
	
	public void shoot()
	{
		if(shot)//if bullet has been shot move it 3 pixels up per cycle
			bullet.y-=3;
		if(bullet != null && bullet.y < -3)//detect bullets leaving screen and reset shot
		{
			bullet.y = -3;
			shot = false;
			readyToFire = true;
		}
	}
	
	
	public class InputKeyEvents extends KeyAdapter {
		public void keyPressed(KeyEvent key) {
			int keys = key.getKeyCode();
			
			if(keys == KeyEvent.VK_W)//move up
				yDirec = -1;
			if(keys == KeyEvent.VK_A)//move left
				xDirec = -1;
			if(keys == KeyEvent.VK_S)//move down
				yDirec = 1;
			if(keys == KeyEvent.VK_D)//move right
				xDirec = 1;
			if(keys == KeyEvent.VK_ENTER)//play game
				title = false;
			if(keys == KeyEvent.VK_ESCAPE)//end game
				title = true;
			if(keys == KeyEvent.VK_SPACE){//shoot
				if(bullet == null)
					readyToFire = true;
				if(readyToFire)
				{
					bY = yNew;
					bX = xNew + 15;
					bullet = new Rectangle(bX, bY, 3, 3);
					shot = true;
				}
			}
		}
		 public void keyTyped(KeyEvent key) { }
	     
		    
		 public void keyReleased(KeyEvent key) {	
			 int keys = key.getKeyCode();
			 //reset direction to 0 when key is released
				if(keys == KeyEvent.VK_W)
					yDirec = 0;
				if(keys == KeyEvent.VK_A)
					xDirec = 0;
				if(keys == KeyEvent.VK_S)
					yDirec = 0;
				if(keys == KeyEvent.VK_D)
					xDirec = 0;
				if(keys == KeyEvent.VK_SPACE)
				{
					//readyToFire = false;
					
				}
					
		 }

	}

	
}
