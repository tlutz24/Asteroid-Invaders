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
 * @author Dylan Steele
 * @author Tyler Lutz
 * 
 *
 */
class AnPanel extends JPanel implements Runnable {
	/**Graphics object used to draw game*/
	private Graphics gImg;
	/**Player position and variables*/
//	int x, y, xNew, yNew;
	/**Player movement direction variables*/
//	int xDirec, yDirec;
	/**Player points variable*/
	int pts;
	/**Asteroid generation speed variable*/
	int astGenSpeed;
	/**Bullet 'x' coordinates*/
	List<Integer> bX;
	/**Bullet 'y' coordinates*/
	List<Integer> bY;
	/**Asteroid collection for game*/
	List<Asteroid> asteroids;
	/**Images for background and ship*/
	protected Image img, backImg, buff;//ship and background image
	/**Boolean variables for game flow control*/
	protected boolean title, createAst, readyToFire, shot = false;
	/**Random machine for asteroids*/
	Random rnd;
	/**String to hold game playing info*/
//	String info;
	
	/**Rectangle to represent player*/
	//Rectangle p;
	
	Ship p1;
	
	/**Barrier collection for game*/
	List<Barrier> barriers;
	/**Bullet collection for game*/
	List<Rectangle> bullets;
	
	/**Font for title screen*/
	Font tFont = new Font("Monospaced", Font.ITALIC | Font.BOLD, 45);
	/**Font for statistics in game*/
	Font sFont = new Font("Monospaced", Font.ITALIC, 15);
	
	/**Constructor for class
	 * 
	 */
	AnPanel() {
		super();
//		xDirec = yDirec = 0;//no movement at start
		pts = 0;//player points
		title = true;//open game menu
		rnd = new Random();
		bX = new ArrayList<Integer>();
		bY = new ArrayList<Integer>();
		asteroids = new ArrayList<Asteroid>();
		barriers = new ArrayList<Barrier>();
		bullets = new ArrayList<Rectangle>();
		try {
			//img = ImageIO.read(new File("Images/shipSmall.png"));
			backImg = ImageIO
					.read(new File("Images/Asteroids-Background.png"));
		} catch (IOException e) {
			backImg = null;
		}
		this.addKeyListener(new InputKeyEvents());
		this.setFocusable(true);
		p1 = new Ship();
		resetGame();
		//p = new Rectangle(xNew, yNew, 30, 30);
		//create thread and start it
		Thread th1 = new Thread(this);
		th1.start();
		
	}
	
	public void resetGame(){
		p1.setLocation(305, 650);//set initial position
		createAst = false;
		astGenSpeed = 250;
		setBarriers(4, 3);
		while(asteroids.size() > 0)
			asteroids.remove(0);
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
	
	@Override
	public void run() {
		int count = 0;
		try{
			while(true)
			{
				//draw player
				movePlyr();
				//handle shots
				p1.shoot(barriers);
				//create asteroid
				if(count == astGenSpeed){
					count = 0;
					if(createAst)
						createAsteroids();
				}
				//move or remove asteroids
				handleAsteroids();
				
				//set speed of animation
				Thread.sleep(15);//lower = faster while higher = slower
				count++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error at run: "+ e.toString());
		}	
	}

	public void movePlyr() {
		//check if move is allowed
		//p.setLocation(xNew, yNew);
		p1.setLocation(p1.xNew, p1.yNew);//necessary?
		
		if(detectCollide())
		{
			//prevent player from moving into barrier
			
			if (p1.yDirec == -1)
				p1.yDirec = 1;//reverse move if there is collision
			else if (p1.yDirec == 1)
				p1.yDirec = -1;
			if (p1.xDirec == -1)
				p1.xDirec = 1;
			else if (p1.xDirec == 1)
				p1.xDirec = -1;
			//move player
			p1.xNew += (4*p1.xDirec);
			p1.yNew += (4*p1.yDirec);
			
		}
		else 
		{
			if ((p1.yDirec == -1) && (title || p1.yNew < 550))
				p1.yDirec = 0;//cancel move if not allowed
			if ((p1.xDirec == -1) && (title || p1.xNew < 0))
				p1.xDirec = 0;
			if ((p1.yDirec == 1) && (title || p1.yNew+30 > getSize().height))
				p1.yDirec = 0;
			if ((p1.xDirec == 1) && (title || p1.xNew+30 > getSize().width))
				p1.xDirec = 0;
			//move player
			p1.xNew += (4*p1.xDirec);
			p1.yNew += (4*p1.yDirec);
		}
		p1.x = p1.xNew;
		p1.y = p1.yNew;
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
			if(p1.p.intersects(bar))
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

	public void createAsteroids(){
		int x, size, y, xDirec, yDirec;
		x = rnd.nextInt(650);
		size = rnd.nextInt(3)+1;
		y = -25*size;
		xDirec = rnd.nextInt(6) - 3;
		yDirec = rnd.nextInt(2) + 1;
		asteroids.add(new Asteroid(x, y, xDirec, yDirec, size));
	}
	
	/**Creates asteroids and handles moving them and eventually removing them
	 * 
	 */
	public void handleAsteroids(){
		boolean hit = false;
		Asteroid temp;
		for(int i = 0; i < asteroids.size(); i++)//move active asteroids
		{
			temp = asteroids.get(i);
			temp.y += temp.yDirec;
			temp.x += temp.xDirec;
			for(int j = 0; j < p1.bullets.size(); j++)
				if(p1.bullets.get(j).intersects(temp.ast))
					hit = true;
				else 
					hit = false;
					
			if(temp.y > 750 && asteroids.size() > 0)//bottom edge
			{
				asteroids.remove(i);
				
			}
			
			else if(hit)
			{
				//delete current asteroid and create two smaller ones going opposite directions from same place
				if(asteroids.size() > 0)//make sure to avoid null index 
					asteroids.remove(i);
				asteroids.add(new Asteroid(temp.x, temp.y, temp.xDirec, temp.yDirec, temp.size - 1));
				asteroids.add(new Asteroid(temp.x, temp.y, -temp.xDirec, temp.yDirec, temp.size - 1));//goes opposite direction
				p1.pts += (temp.size * 5);
			}
			
			else if(temp.x < -50)//left edge
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				temp.yDirec ++;
				
			}
			else if(temp.x > 640)//right edge
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				temp.yDirec ++;
			}
			
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
			//g.drawImage(p1.player, p1.xNew, p1.yNew, this);
			p1.draw(g);
		}
		else
		{
			g.setFont(sFont);//display game
			g.drawString("# of Asteroids: " + asteroids.size(), 20, 20);
			g.drawString("Press ESC to quit to menu.", 20, 35);
			g.drawString("Points: " + pts, 20, 50);
			//g.drawImage(p1.player, p1.xNew, p1.yNew, this);
			p1.draw(g);//draw player
			//draw shot
			if(p1.shot)
				for(int i = 0; i < p1.bullets.size(); i++)
					g.fillRect(p1.bullets.get(i).x, p1.bullets.get(i).y, p1.bullets.get(i).width, p1.bullets.get(i).height);
		}
		
		for(int i = 0; i < asteroids.size(); i++)
		{
			Asteroid temp = asteroids.get(i);
			g.drawImage(temp.asteroid, temp.x, temp.y, this);
		}
		
		repaint();
	}
	
	public class InputKeyEvents extends KeyAdapter {
		public void keyPressed(KeyEvent key) {
			int keys = key.getKeyCode();
			
			if(keys == KeyEvent.VK_W)//move up
				p1.changeDirec('u');
			if(keys == KeyEvent.VK_A)//move left
				p1.changeDirec('l');
			if(keys == KeyEvent.VK_S)//move down
				p1.changeDirec('d');
			if(keys == KeyEvent.VK_D)//move right
				p1.changeDirec('r');
			if(keys == KeyEvent.VK_ENTER)//play game
				title = false;
			if(keys == KeyEvent.VK_ESCAPE)//end game
				title = true;
			if(keys == KeyEvent.VK_SPACE){//shoot
				if(p1.readyToFire)
				{
					p1.addBullet();
					shot = true;
				}
			}
			if(keys == KeyEvent.VK_H)
				for(int i = 0; i < 4; i++)
					barriers.get(i).hit();
			if(keys == KeyEvent.VK_P)
				createAst = !createAst;//invert state
			if(keys == KeyEvent.VK_UP)
				astGenSpeed -= 50;
			if(keys == KeyEvent.VK_DOWN)
				astGenSpeed += 50;
				
		}
		 public void keyTyped(KeyEvent key) { }   
		 public void keyReleased(KeyEvent key) {	
			 int keys = key.getKeyCode();
			 //reset direction to 0 when key is released
				if(keys == KeyEvent.VK_W)
					p1.yDirec = 0;
				if(keys == KeyEvent.VK_A)
					p1.xDirec = 0;
				if(keys == KeyEvent.VK_S)
					p1.yDirec = 0;
				if(keys == KeyEvent.VK_D)
					p1.xDirec = 0;
				if(keys == KeyEvent.VK_SPACE)
				{
					//readyToFire = false;
					
				}
		 }
	}
}
