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
	protected boolean title, gameOver, createAst, readyToFire, shot = false;
	
	protected boolean running = false;
	
	private Thread th1;
	
	private int time;
	
	/**Random machine for asteroids*/
	Random rnd;
	
	Ship p1;
	
	/**Barrier collection for game*/
	List<Barrier> barriers;
	/**Bullet collection for game*/
	List<Rectangle> bullets;
	
	/**Font for title screen*/
	Font tFont = new Font("Monospaced", Font.ITALIC | Font.BOLD, 45);
	/**Font for statistics in game*/
	Font sFont = new Font("Monospaced", Font.ITALIC, 15);
	
	Font gameO = new Font("Monospaced", Font.ITALIC | Font.BOLD, 75);
	
	/**Constructor for class
	 * 
	 */
	AnPanel() {
		super();
		pts = 0;//player points
		title = true;//open game menu
		rnd = new Random();
		bX = new ArrayList<Integer>();
		bY = new ArrayList<Integer>();
		asteroids = new ArrayList<Asteroid>();
		barriers = new ArrayList<Barrier>();
		bullets = new ArrayList<Rectangle>();
		try {
			backImg = ImageIO
					.read(new File("Images/Asteroids-Background.png"));
		} catch (IOException e) {
			backImg = null;
		}
		this.addKeyListener(new InputKeyEvents());
		this.setFocusable(true);
		p1 = new Ship();
		resetGame();
		start();
		
	}
	
	public void resetGame(){
		gameOver = false;
		//set initial position
		p1.revive();
		createAst = false;
		time = 0;
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
	
	// synchronized is used in order to ensure the thread is executed properly
	//starts thread here instead of inside of the constructor
	public synchronized void start() {

		if (running) {
			return;
		} else {
			running = true;

			th1 = new Thread(this);
			th1.start();
		}
	}
	
	@Override
	public void run() {
		int count = 0;
		try{
			while(true)
			{
				if(astGenSpeed >= 100)
					astGenSpeed -= p1.pts;
				else
					astGenSpeed = 100;
			
			int fps = 60;
			//converting nano seconds
			double timePerTick = 1000000000/fps;
			double delta = 0;
			long now;
			//return system time in nano seconds
			long lastTime = System.nanoTime();
			long timer = 0;
			int ticks = 0;
			//better game loop that runs at a constant 60fps 
			while (running) {
				now = System.nanoTime();
				//amount of time past since we last called this code
				//then divide by the maximum amount of allowed time
				delta+=(now - lastTime)/timePerTick;
				//fps counter
				timer += now - lastTime;
				
				lastTime = now;
				
				if(delta >= 1) {
					//code from the past loop to run the game
										
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
					
					ticks++;
					delta--;
					count++;
				}
				//displays the fps counter in the console, which is locked at 60
				if (timer >= 1000000000) {
					System.out.println("Ticks and Frames: " + ticks);
					ticks = 0;
					timer = 0;
					if(!title && !gameOver){
						time++;
						System.out.println("Time is " + time);
						if(astGenSpeed > 100)
							astGenSpeed -= time;
					}
				}
				
			}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error at run: "+ e.toString());
			
		}	
	}
	
	//proper stop method for when the program ends to stop the thread
	public synchronized void stop() {
		if (running == false) {
			return;
		} else {
			running = false;
			try {
				th1.join();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}


	public void movePlyr() {
		//check if move is allowed
		p1.setLocation(p1.xNew, p1.yNew);//necessary? - yes - the only reason player collision detection works
		
		if(detectCollide())//player collision with barrier
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
		//detect reason to prevent y move
		else if ((p1.yDirec == -1 && p1.yNew < 550)||(p1.yDirec == 1 && p1.yNew+30 > getSize().height)||(gameOver || title ))
			p1.yDirec = 0;
		//detect reason to prevent x move
		else if((p1.xDirec == -1 && p1.xNew < 0)||(p1.xDirec == 1 && p1.xNew+30 > getSize().width)||(gameOver || title ))
			p1.xDirec = 0;
		

		//move player
		p1.xNew += (4*p1.xDirec);
		p1.yNew += (4*p1.yDirec);
		p1.x = p1.xNew;
		p1.y = p1.yNew;
	}

	/**
	 * TODO: fix collision detection so player cannot glitch through barrier
	 * @return 
	 */
	public boolean detectCollide(){
		//handle player intersections with barriers
		Barrier bar;
		
		for(int i = 0; i < barriers.size(); i++){
			bar = barriers.get(i);
			if(bar.isHit(p1.p) != -1)
				return true;
		}
		return false;
	}

	public void createAsteroids(){
		int x, size, y, xDirec, yDirec;
		x = rnd.nextInt(650);
		size = (rnd.nextInt(3)+1) * 25;
		y = -size;
		xDirec = rnd.nextInt(6) - 3;
		yDirec = rnd.nextInt(2) + 1;
		asteroids.add(new Asteroid(x, y, xDirec, yDirec, size));
	}
	
	/**Creates asteroids and handles moving them and eventually removing them
	 * seems to have minor errors - does not always remove ast 
	 * could use more work to remove a proper amount of the barrier
	 */
	public void handleAsteroids(){
		boolean removed = false;
		Asteroid temp;
		for(int i = 0; i < asteroids.size(); i++)
		{
			temp = asteroids.get(i);//set temp to current asteroid
			temp.moveAst();//move active asteroids
			
			if(temp.x < -50)//asteroid hits left edge
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				if(astGenSpeed <= 100)
					temp.yDirec ++;
				
			}
			
			if(temp.x > 640)//asteroid hits right edge
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				if(astGenSpeed <= 100)
					temp.yDirec ++;
			}
					
			if(!removed && temp.y > 750 && asteroids.size() > 0)//asteroid hits bottom edge
			{
				asteroids.remove(i);
				removed = true;
				createAsteroids();
			}
			
			if(!removed && temp.ast.intersects(p1.p) && asteroids.size() > 0)//asteroid collides with player
			{
				asteroids.remove(i);
				removed = true;
				gameOver = true;
				p1.dead = true;
				createAst = !createAst;
			}
			
			for(int j = 0; j < p1.bullets.size(); j++)//loop to detect bullet collision with asteroid
				if(!removed && p1.bullets.get(j).intersects(temp.ast))//bullet collides with asteroid
				{
					p1.bullets.remove(j);
					//delete current asteroid and create two smaller ones going opposite directions from same place
					asteroids.add(new Asteroid(temp.x, temp.y, temp.xDirec, temp.yDirec, temp.size - 25));
					asteroids.add(new Asteroid(temp.x, temp.y, -temp.xDirec, temp.yDirec, temp.size - 25));//goes opposite direction
					if(asteroids.size() > 0)//make sure to avoid null index 
					{
						asteroids.remove(i);
						removed = true;
					}
					p1.pts += 10;
					break;
				}
			
			for(int j = 0; j < barriers.size(); j++)//loop to detect asteroid collision with barrier
			{
				int barrierHitIndex = barriers.get(j).isHit(temp.ast);//index of barrier hit
				
				if(!removed && barrierHitIndex != -1)//if index isn't out of bounds
				{
						asteroids.remove(i);//remove asteroid
						removed = true;
						barriers.get(j).hit(barrierHitIndex);//remove barrier hit
						break;
				}
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
			barriers.get(i).draw(g);
		
		//display title menu or game
		g.setColor(Color.WHITE);
		if(title)
		{
			resetGame();
			g.setFont(tFont);//display Menu text
			g.drawString("Asteroid Invaders!", 90, 150);
			g.drawString("Press Enter to Start!", 50, 350);
			p1.draw(g);
		}
		else
		{
			g.setFont(sFont);//display game
			g.drawString("Generate Asteroids at " + astGenSpeed + " cycles.", 20, 10);
			g.drawString("# of Asteroids: " + asteroids.size(), 20, 25);
			g.drawString("Press ESC to quit to menu.", 20, 40);
			g.drawString("Points: " + p1.pts, 20, 55);
			
			p1.draw(g);//draw player and bullets
		}
		
		if(gameOver)
		{
			g.setFont(gameO);
			g.drawString("GAME OVER", 120, 350);
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
			{
				title = false;
				createAst = true;
			}
			if(keys == KeyEvent.VK_ESCAPE)//end game
				title = true;
			if(keys == KeyEvent.VK_SPACE)//shoot
				p1.addBullet();
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
