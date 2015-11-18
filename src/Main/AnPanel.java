package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.*;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * @author Dylan Steele, Tyler Lutz
 *
 */
class AnPanel extends JPanel implements Runnable {
	private Graphics gImg;
	int x, y, xNew, yNew;//player position
	int xDirec, yDirec;//player movement direction
	int pts;//player points
	List<Integer> bX = new ArrayList<Integer>();
	List<Integer> bY = new ArrayList<Integer>();
	//int bX, bY;//bullet location
	List<Asteroid> asteroids = new ArrayList<Asteroid>();
	//int astX, astY, astXDirec, astYDirec; <- moved to Asteroid class
	protected Image img, backImg, buff;//ship and background image
	protected boolean title, createAst, readyToFire, shot = false;
	Random rnd;
	String info;

	Rectangle p;//rectangle to represent player
	
	List<Barrier> barriers = new ArrayList<Barrier>();//create list of barriers
	List<Rectangle> bullets = new ArrayList<Rectangle>();
	//Rectangle bullet;
	
	//Fonts
	Font tFont = new Font("Monospaced", Font.ITALIC | Font.BOLD, 45);//title font
	Font sFont = new Font("Monospaced", Font.ITALIC, 15);//statistics font
	
	AnPanel() {
		super();
		xDirec = yDirec = 0;//no movement at start
		pts = 0;//player points
		title = true;//open game menu
		rnd = new Random();
		try {
			img = ImageIO	//attempt to read local files
					.read(new File("Images/shipSmall.png"));
			backImg = ImageIO
					.read(new File("Images/Asteroids-Background.png"));
			

		} catch (IOException e) {
			img = null;
		}
		this.addKeyListener(new InputKeyEvents());
		this.setFocusable(true);
		resetGame();
		setInfo();
		p = new Rectangle(xNew, yNew, 30, 30);
		//create thread and start it
		Thread th1 = new Thread(this);
		th1.start();
		
	}
	
	public void handleAsteroids(){
		int x, size, y, xDirec, yDirec;
		Asteroid temp;
		if(createAst)
		{
			x = rnd.nextInt(650);
			size = rnd.nextInt(3)+1;
			y = -25*size;
			xDirec = rnd.nextInt(6) - 3;
			yDirec = rnd.nextInt(3);
			asteroids.add(new Asteroid(x, y, xDirec, yDirec, size));
		}
		for(int i = 0; i < asteroids.size(); i++)//move active asteroids
		{
			temp = asteroids.get(i);
			temp.y += 8*temp.yDirec;
			temp.x += 4*temp.xDirec;
			
			if(temp.x < -50)//left edge
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				temp.yDirec += 2;
				
			}
			if(temp.x > 640)
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				temp.yDirec += 2;
			}
			if(temp.y > 750)
				asteroids.remove(i);
			//add overflow from sides to top
		}
	}
	
	public void setBarriers(int numBarriers, int strength){
		barriers = new ArrayList<Barrier>();
		Barrier bar;//temp for creating barriers
		for(int i = 0; i < numBarriers; i++)
		{
			bar = new Barrier(55 + 150*i, 600, strength);
			barriers.add(bar);
		}
	}
	
	public void resetGame(){
		x = xNew = 305;//set initial position
		y = yNew = 650;
		createAst = false;
		setBarriers(4, 3);
		while(asteroids.size() > 0)
			asteroids.remove(0);
	}
	
	public void setInfo(){
		info = "# of Asteroids: " + asteroids.size();
	}
	
	/**
	 * TODO: fix collision detection so player cannot glitch through barrier
	 * @return 
	 */
	public boolean detectCollide(){
		//handle intersections with barriers
		Rectangle bar;
		for(int i = 0; i < barriers.size(); i++){
			bar = barriers.get(i).fullBarrier;
			if(p.intersects(bar))
				return true;
			/*	
			if (p.intersects(bar))
			{
				if((p.x < bar.x && p.x + p.width > bar.x) && (p.y < bar.y + bar.height&& p.y + p.height > bar.y))
					return 'l';
				if((p.x < bar.x + bar.width && p.x + p.width > bar.x + bar.width) && (p.y < bar.y + bar.height&& p.y + p.height > bar.y))
					return 'r';
				if(p.x > bar.x && p.x + p.width > bar.x && (p.y < bar.y + bar.height&& p.y + p.height > bar.y))
					return 'u';
				
			}
				return 'x';
				*/
		}
		return false;
	}
	
	public void movePlyr(int xDir, int yDir) {
		//check if move is allowed
		p.setLocation(xNew, yNew);
		
		if(detectCollide())
		{
			//prevent player from moving into barrier
			
			if (yDir == -1)
				yDir = 1;//reverse move if there is collision
			else if (yDir == 1)
				yDir = -1;
			if (xDir == -1)
				xDir = 1;
			else if (xDir == 1)
				xDir = -1;
			//move player
			xNew += (4*xDir);
			yNew += (4*yDir);
			
		}
		else 
		{
			if ((yDir == -1) && (title || yNew < 550))
				yDir = 0;//cancel move if not allowed
			if ((xDir == -1) && (title || xNew < 0))
				xDir = 0;
			if ((yDir == 1) && (title || yNew+30 > getSize().height))
				yDir = 0;
			if ((xDir == 1) && (title || xNew+30 > getSize().width))
				xDir = 0;
			//move player
			xNew += (4*xDir);
			yNew += (4*yDir);
		}
		x = xNew;
		y = yNew;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int count = 0;
		try{
			while(true)
			{
				//draw player
				movePlyr(xDirec, yDirec);
				setInfo();
				//handle shots
				shoot();
				//handle asteroid creation
				if(count == 15){
					handleAsteroids();
					count = 0;
				}
				//set speed of animation
				Thread.sleep(15);//lower = faster while higher = slower
				count++;
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: "+ e.toString());
		}
		
	}
	
	public void paint(Graphics g){
		buff = createImage(getWidth(), getHeight());
		gImg = buff.getGraphics();
		paintComponent(gImg);
		
		g.drawImage(buff, 0, 0, this);
	}

	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		
		g.drawImage(backImg, 0, 0, this);
		
		//draw barriers
		g.setColor(Color.red);
		for(int i = 0; i < barriers.size(); i++)
			g.fillRect(barriers.get(i).x, barriers.get(i).y, barriers.get(i).width, barriers.get(i).height);
		
		//display title menu or game
		g.setColor(Color.WHITE);
		if(title)
		{
			resetGame();
			g.setFont(tFont);//display Menu text
			g.drawString("Asteroid Invaders!", 90, 150);
			g.drawString("Press Enter to Start!", 50, 350);
			g.drawImage(img, xNew, yNew, this);
		}
		else
		{
			g.setFont(sFont);//display game
			g.drawString(info, 20, 10);
			g.drawString("Press ESC to quit to menu.", 20, 35);
			g.drawString("Points: " + pts, 20, 50);
			g.drawImage(img, xNew, yNew, this);
			//draw shot
			if(shot)
				for(int i = 0; i < bullets.size(); i++)
					g.fillRect(bullets.get(i).x, bullets.get(i).y, bullets.get(i).width, bullets.get(i).height);
		}
		
		for(int i = 0; i < asteroids.size(); i++)
		{
			Asteroid temp = asteroids.get(i);
			g.drawImage(temp.asteroid, temp.x, temp.y, this);
		}
		
		repaint();
	}
	
	public void shoot()
	{
		if(shot)//if bullet has been shot move it 3 pixels up per cycle
			for(int i = 0; i < bullets.size(); i++)
			{
				bullets.get(i).y -= 3;
				int y = bullets.get(i).y;
				int x = bullets.get(i).x;
				for(int j = 0; j < barriers.size(); j++){
					Barrier b = barriers.get(j);
					if(y < -3 || (y > b.y && y < b.y + b.height && x > b.x && x < b.x + b.width))
						bullets.remove(i);
				}
			}
		if(bullets.size() == 0)//detect bullets leaving screen and reset shot
			{
				shot = false;
				readyToFire = true;
			}
		
	}
	
	
	public class InputKeyEvents extends KeyAdapter {
		public void keyPressed(KeyEvent key) {
			int keys = key.getKeyCode();
			int x,y;
			
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
				//if(bullet == null)
				//	readyToFire = true;
				if(readyToFire)
				{
					y = yNew;
					x = xNew + 15;
					bY.add(y);
					bX.add(x);
					//bY = yNew;
					//bX = xNew + 15;
					bullets.add(new Rectangle(x, y, 3, 3));
					shot = true;
				}
			}
			if(keys == KeyEvent.VK_H)
			{
				for(int i = 0; i < 4; i++)
					barriers.get(i).hit();
			}
			if(keys == KeyEvent.VK_P)
			{
				createAst = !createAst;//invert state
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
	
	public class Barrier{
		int x, y, width, height;
		List<Rectangle> barrier = new ArrayList<Rectangle>();
		Rectangle fullBarrier;
		Barrier(int x1, int y1, int strength){
			x = x1;
			y = y1;
			width = 75;
			height = strength * 10;
			for(int i = 0; i < strength; i++)
				barrier.add(new Rectangle(x, y + 10*i, width, 10));
			fullBarrier = new Rectangle(x, y, width, height);
		}
		public void hit(){
			y += 10;
			height -=10;
			if(height >= 0)
				barrier.remove(0);
			fullBarrier = new Rectangle(x, y, width, height);
		}		
	}
}
