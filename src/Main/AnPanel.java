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
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.sql.*;

/**
 * @author Dylan Steele
 * @author Tyler Lutz
 * @author Jason Liu
 *
 */
class AnPanel extends JPanel implements Runnable {
	
	/**
	 * Files for sounds and method to play file objects
	 */
	// Sound effect for ship hit on barrier
	File Bounce = new File ("SoundEffects/Bounce.WAV");
	// Sound effect for Ship collision
	File ShipExplode = new File("SoundEffects/ShipExplosion.WAV");
	// Sound effect for Asteroid collision
	File AsteroidExplode = new File("SoundEffects/AsteroidsExplosion.WAV");
	// Sound effect for Barrier collision
	File BarrierHit = new File("SoundEffects/HitOnBarrier.WAV"); 
	// Sound effect for Barrier collision
	File Click = new File("SoundEffects/Click.WAV"); 
	//Sound effect for player shot
	File Shoot = new File("SoundEffects/Laser_Shoot.WAV");

	/*
	//background music?
	// This method take the wav file and play it. Problem is that it will not stop until the end of the song, we need to 
	// figure out a way to stop it playing when we need to do that. And it only plays once, but that would be easy to fix
	public void music()
	{
		AudioPlayer MGP = AudioPlayer.player;
		AudioStream BGM;
		
	try{
		//BGM = new AudioStream(new FileInputStream("SoundEffects/titleBGM.wav"));
		//MD = BGM.getData();
		//loop = new ContinuousAudioDataStream(MD);
		
	
		  InputStream test = new FileInputStream("SoundEffects/titleBGM.wav");
          BGM = new AudioStream(test);
          AudioPlayer.player.start(BGM);
          
        
		}  catch(FileNotFoundException e){
            System.out.print(e);
        }	
		catch(IOException error){
			System.out.println(error);
		}		
	}
	*/
	
	/**
	 * Method to play sound clips for game-play sound effects
	 * 
	 * @param Sound		sound file to load into audio system
	 */
	public void PlaySound(File Sound)

	{
		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Sound));
			clip.start();	
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	/**Graphics object used to draw game*/
	private Graphics gImg;
	/**Player points variable*/
	int pts;
	/**Asteroid generation speed variable*/
	int astGenSpeed;
	/**Asteroid generation cap to help playability of game*/
	int astGenCap;
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
	
	protected boolean highScore;//boolean for displaying high score
	
	protected boolean running = false;
	
	private Thread th1;
	
	private int time;
	
	private int count;
	
	private char difficulty;
	
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
	/**Font for game over screen*/
	Font gameO = new Font("Monospaced", Font.ITALIC | Font.BOLD, 75);
	
	/**
	 * Database variables and Database access functions below
	 */
	//JDBC driver string
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//Database URL
	static final String DB_URL = "jdbc:mysql://localhost/high score list";/** TODO:look at db url */
	//User name string
	static final String USER = "root";
	//Password string
	static final String PASS = "";
	//Connection object
	Connection connection = null;
	//Statement object
	Statement statement = null;
	
	//list for holding highScoreStrings
	List<String> scoreStrings;
	int ticks;//used as index when displaying list

	/**
	 * Method to open connection with local database
	 * 
	 * @throws Exception
	 */
	public void connectDatabase() throws Exception {
		//Register the Driver (step 2)
		String driverName = "com.mysql.jdbc.Driver";
	    Class.forName(driverName);
	    
 
	    //Open connection
	    System.out.println("Connecting to db...");
	    connection = DriverManager.getConnection(DB_URL, USER, PASS);
	    
	}
	
	/**
	 * Method to add entry to High Score DB
	 * 
	 * @param name		String holding player's name
	 * @param score		int holding player's score
	 * @param time		int holding player's time alive
	 */
	public void addToDB(String name, int score, int time){
		String command = "INSERT INTO `high score list`.`names, scores, and time` "
				+ "(`Name`, `Score`, `Time`) VALUES ('" + name + "', '" + score + "', '" + time + "');";
		//issue command to sql connection
		try {
			//Create statement
			System.out.println("Adding " + name + " to high scores list");
			statement = connection.createStatement();
			statement.execute(command);
			System.out.println("Added " + name + " to the high scores list");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to pull info from high score DB and print it to the console
	 */
	public void getHighScores(){
		//query will sort by high score in descending order (highest to lowest)
		String query = "SELECT * FROM `names, scores, and time` ORDER BY `names, scores, and time`.`Score` DESC";
		try{
			//System.out.println("----- High Scores List -----");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			while(rs.next()){
				String name = rs.getString("Name");
				int score = rs.getInt("Score");
				int time = rs.getInt("Time");
				//add string to list to print
				scoreStrings.add(name + " - " + score + " - " + time);
				
				//g.drawString(name + " - " + score + " - " + time, xPos, yPos);
				//System.out.println(name +" - "+ score +" - "+ time);
				
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Constructor for class
	 */
	AnPanel() {
		super();
		pts = 0;//player points
		//set difficulty to easy
		difficulty = 'm';
		title = true;//open game menu
		rnd = new Random();
		bX = new ArrayList<Integer>();
		bY = new ArrayList<Integer>();
		asteroids = new ArrayList<Asteroid>();
		barriers = new ArrayList<Barrier>();
		bullets = new ArrayList<Rectangle>();
		scoreStrings = new ArrayList<String>();
		try {
			backImg = ImageIO.read(new File("Images/Asteroids-Background.png"));
			connectDatabase();//connect to db and pull starting high scores
			getHighScores();
		} catch (Exception e) {
			System.out.println(e.toString());
			//backImg = null;
		}
		this.addKeyListener(new InputKeyEvents());
		this.setFocusable(true);
		p1 = new Ship();
		resetGame();
		start();
		
	}
	
	/**
	 * Method to reset game to initial state
	 */
	public void resetGame(){
		//gameOver is false to restart the game
		gameOver = false;
		highScore = false;
		//reset player
		p1.revive();
		//turn off asteroid generation 
		createAst = false;
		//restart timer
		time = 0;
		//to start - generate asteroid once every 12 seconds
		count = astGenSpeed = 10;
		//reset asteroid generation cap 
		astGenCap = 5;
		//place barriers on game board
		setBarriers(4, 3);
		//remove all asteroids from game board
		while(asteroids.size() > 0)
			asteroids.remove(0);
	}

	/**
	 * Method to add a number of barriers to the game board
	 * 
	 * @param numBarriers 		the number of barriers to add to the game board
	 * @param strength 			the size of the barriers to add
	 */
	public void setBarriers(int numBarriers, int strength){
		//create new arrayList for barriers
		barriers = new ArrayList<Barrier>();
		//create a temporary barrier for adding to list
		Barrier bar;//temp for creating barriers
		//for loop to add NumBarriers of barriers
		for(int i = 0; i < numBarriers; i++)
		{
			//create and add barrier to list
			bar = new Barrier(55 + 150*i, 600, strength);
			barriers.add(bar);
		}
	}
	
	/** 
	 * synchronized is used in order to ensure the thread is executed properly
	 * starts thread here instead of inside of the constructor
	 */
	public synchronized void start(){

		if (running) {
			return;
		} else {
			running = true;

			th1 = new Thread(this);
			th1.start();
		}
	}
	
	/**
	 * Method that runs the game
	 */
	@Override
	public void run() {
		
		try{
			while(true)
			{
			int fps = 60;
			//converting nano seconds
			double timePerTick = 1000000000/fps;
			double delta = 0;
			long now;
			//return system time in nano seconds
			long lastTime = System.nanoTime();
			long timer = 0;
			ticks = 0;
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
					
					//increase speed of generation based of players points
					if(p1.pts >= 10000)
					{
						astGenSpeed = 2;
						astGenCap = 50;
					}
					else if(p1.pts >= 5000)
					{
						astGenSpeed = 3;
						astGenCap = 40;
					}
					else if(p1.pts >= 1000)
					{
						astGenSpeed = 4;
						astGenCap = 30;
					}
					else if(p1.pts >= 500)
					{
						astGenSpeed = 5;
						astGenCap = 20;
					}
					else if(p1.pts >= 250)
					{
						astGenSpeed = 6;
						astGenCap = 10;
					}
					
										
					//draw player
					movePlyr();
					//handle shots
					if(p1.shoot(barriers))
						PlaySound(BarrierHit);
					//move or remove asteroids
					handleAsteroids();
					
					delta--;
				}
				//displays the fps counter in the console, which is locked at 60
				if (timer >= 1000000000) {
					//System.out.println("Ticks and Frames: " + ticks);
					//increment ticks to move starting index each second
					ticks++;
					//to make sure the list will continue scrolling after displaying all high scores
					//reset ticks to 0 before it will reference a null index
					if(ticks == scoreStrings.size())
						ticks = 0;
					timer = 0;
					if(!title && !gameOver){
						if(count >= astGenSpeed)
						{
							count = 0;
							if(createAst && asteroids.size() < astGenCap)
								//create asteroid
								createAsteroid();
						}
						while(asteroids.size() > astGenCap)
							asteroids.remove(asteroids.size()-1);
						time++;
						System.out.println("Time is " + time);
						

						count++;
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
	
	/**
	 * Proper stop method for when the program ends to stop the thread
	 */
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

	/**
	 * Method to move player and prevent player from moving when necessary 
	 */
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
		if ((p1.yDirec == -1 && p1.yNew < 550)||(p1.yDirec == 1 && p1.yNew+30 > getSize().height)||(gameOver || title ))
			p1.yDirec = 0;
		//detect reason to prevent x move
		if((p1.xDirec == -1 && p1.xNew < 0)||(p1.xDirec == 1 && p1.xNew+30 > getSize().width)||(gameOver || title ))
			p1.xDirec = 0;
		

		//move player
		p1.xNew += (4*p1.xDirec);
		p1.yNew += (4*p1.yDirec);
		p1.x = p1.xNew;
		p1.y = p1.yNew;
	}

	/**
	 * Method to detect if there is a collision between the player and any of the barriers
	 * 
	 * TODO: look into fix for collision detection so player cannot glitch through barrier
	 * 
	 * @return true for collision detected and false for no collision 
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

	/**
	 * Method to create an asteroid and add it to the game
	 */
	public void createAsteroid(){
		//variables for creating the asteroid
		int x, size, y, xDirec, yDirec;
		//generate random number for x position
		x = rnd.nextInt(650);
		//generate random number for size 
		size = (rnd.nextInt(3)+1) * 25;
		//set the y-position to just off the top of the game screen
		y = -size;
		//randomly generate number until non-zero x-direction is set
		do
			xDirec = rnd.nextInt(6) - 3;
		while(xDirec == 0);
		//generate random number for y-direction
		yDirec = rnd.nextInt(2) + 1;
		//add a new asteroid to the list of asteroids
		asteroids.add(new Asteroid(x, y, xDirec, yDirec, size));
		System.out.println("Asteroid added to game board");
	}
	
	/**
	 * Handles asteroids - moving them and eventually removing them
	 * 
	 * TODO: implement code to remove a proper amount of the barrier based on size of asteroid
	 *  - I think I did finish this, but I'm still in the process of a thorough debug
	 */
	public void handleAsteroids(){
		//variable to remember if current asteroid has already been removed
		boolean removed = false;
		//temporary asteroid to avoid calling asteroids.get(i) constantly
		Asteroid temp;
		for(int i = 0; i < asteroids.size(); i++)
		{
			//set temp to current asteroid
			temp = asteroids.get(i);
			//move active asteroids
			temp.moveAst();
			
			//asteroid hits left edge
			if(temp.x < -50)
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				if(astGenSpeed <= 100)
					temp.yDirec ++;
			}
			
			//asteroid hits right edge
			if(temp.x > 640)
			{
				temp.x = temp.y;
				temp.y = -50;
				temp.xDirec *= -1;
				if(astGenSpeed <= 100)
					temp.yDirec ++;
			}
				
			//asteroid hits bottom edge
			if(!removed && temp.y > 750 && asteroids.size() > 0)
			{
				asteroids.remove(i);
				removed = true;
				createAsteroid();
			}
			
			//asteroid collides with player
			if(!removed && temp.ast.intersects(p1.p) && asteroids.size() > 0 && p1.dead == false){
				PlaySound(ShipExplode);//Play player explode sound
				asteroids.remove(i);
				removed = true;
				gameOver = true;
				p1.dead = true;
				
				//Prompt the user, asking if they want to add their score to the High Score list
				int reply = JOptionPane.showConfirmDialog(this, "Would you like to add your score to the High Score list?");
				if (reply == JOptionPane.YES_OPTION){
		        	String name = "";
		        	String in = JOptionPane.showInputDialog("Enter your name (limit - 6 characters):");
				
		        	for(int c = 0; c < 6 && c < in.length(); c++)
		        		name += in.charAt(c);
		        	addToDB(name, p1.pts, time);
		        }
				highScore = true;//set highScore to true to display list
				getHighScores();//refresh high scores list
				/**
				 * TODO:check that name/score/time is added to db
				 * player is being added properly at the end of each game
				 * -no errors with this yet
				 */
				
				/*
				 * JFrame that will display the High Score Table
				 */
				//HighScore highScoreFrame = new HighScore();
				//I think we should display the high scores in the same window to keep 
				//game esthetic
				
				createAst = !createAst;
			}
			
			//loop to detect bullet collision with asteroid
			for(int j = 0; j < p1.bullets.size(); j++)
				//bullet collides with asteroid
				if(!removed && p1.bullets.get(j).intersects(temp.ast))
				{
					PlaySound(AsteroidExplode);//Play asteroid explode sound
					//remove the bullet that intersected
					p1.bullets.remove(j);
					//if asteroid is at least medium size
					if(temp.size > 25)
					{
						//create two smaller asteroids going opposite directions from same place as hit asteroid 
						asteroids.add(new Asteroid(temp.x, temp.y, temp.xDirec, temp.yDirec, temp.size - 25));
						//goes opposite direction
						asteroids.add(new Asteroid(temp.x, temp.y, -temp.xDirec, temp.yDirec, temp.size - 25));
					}
					//make sure to avoid null index
					if(asteroids.size() > 0) 
					{
						//remove the current asteroid
						asteroids.remove(i);
						//remember that asteroid has been removed
						removed = true;
					}
					//give player points for hit
					p1.pts += 50;
					break;
				}
			
			//second attempt at making ast/barrier collisions better - seems to be working
			//loop for detecting asteroid collision with barrier
			for(int j = 0; j < barriers.size(); j++)
			{
				//variable to count number of barrier bits hit by asteroid
				int hit = barriers.get(j).removeHit(temp.ast);
				//if a barrier was actually hit
				if(hit != 0)
				{
					//Play barrier hit sound
					PlaySound(BarrierHit);
					//remove the current asteroid
					asteroids.remove(i);
					//remember that asteroid has been removed
					removed = true;
					//switch statement based of size of asteroid colliding
					switch(temp.size){
					//if medium size
					case(50):
						//and less than 2 bits were hit
						if(hit < 2)
							//create smaller asteroid in place of removed
							asteroids.add(new Asteroid(temp.x, temp.y, temp.xDirec, temp.yDirec, 25*(2-hit)));
						break;
					//if medium-large size	
					case(75):
						//and less than 3 bits were hit
						if(hit < 3)
							//create smaller asteroid in place of removed
							asteroids.add(new Asteroid(temp.x, temp.y, temp.xDirec, temp.yDirec, 25*(3-hit)));
						break;
					//if large size	
					case(100):
						//and less than 4 bits were hit
						if(hit < 4)
							//create smaller asteroid in place of removed
							asteroids.add(new Asteroid(temp.x, temp.y, temp.xDirec, temp.yDirec, 25*(4-hit)));
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Method used for double buffer graphics (smooth rendering)
	 */
	public void paint(Graphics g){
		buff = createImage(getWidth(), getHeight());
		gImg = buff.getGraphics();
		paintComponent(gImg);
		
		g.drawImage(buff, 0, 0, this);
	}

	/**
	 * Class to draw the game
	 */
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		
		//draw the background first
		g.drawImage(backImg, 0, 0, this);
		
		//set color to red
		g.setColor(Color.red);
		//for each barrier in the list 
		for(int i = 0; i < barriers.size(); i++)
			//call the draw function of the barrier object
			barriers.get(i).draw(g);
		
		//set color to white
		g.setColor(Color.WHITE);
		//display title menu, high score list, or game
		if(highScore)
		{
			int yPos = 230;
			g.drawImage(backImg, 0, 0, this);//clear board
			g.setFont(tFont);//use same font as for title
			g.drawString("--High Scores List--", 30, 150);//header for list
			g.drawString("--------------------", 30, 190);
			
			//for loop is designed to increment it's starting index every second and 
			//show the next 6 entries in the high score list - creating a scrolling like effect
			for(int i = ticks; i < scoreStrings.size() && i < 6 + ticks; i++){
				g.drawString(scoreStrings.get(i), 50, yPos);
				yPos+=50;
			}
			
			g.drawString("--------------------", 30, yPos);//footer for list
			g.drawString("Press Esc for Menu", 50, yPos+50);
			
		}
		else if(title)
		{
			resetGame();
			g.setFont(tFont);//display Menu text
			g.drawString("Asteroid Invaders!", 80, 150);
			g.drawString("Press Enter to Start!", 40, 350);
			/*
			switch(difficulty)
			{
			case('m'):
				g.drawString("- Difficulty: Med. -", 50, 250);
				break;
			case('d'):
				g.drawString("- Difficulty: Hard -", 50, 250);
				break;
			default:
				g.drawString("- Difficulty: Easy -", 50, 250);			
			}*/
			p1.draw(g);
		}
		else
		{
			g.setFont(sFont);//display game
			g.drawString("Generate Asteroids every " + astGenSpeed + " seconds; capped at " + astGenCap + " asteroids.", 20, 10);
			g.drawString("# of Asteroids: " + asteroids.size(), 20, 25);
			g.drawString("Press ESC to quit to menu.", 20, 40);
			g.drawString("Points: " + p1.pts, 20, 55);
			
			p1.draw(g);//draw player and bullets
		}
		
		if(gameOver)
		{
			g.setFont(gameO);
			g.drawString("GAME OVER", 120, 100);
		}
		
		for(int i = 0; i < asteroids.size(); i++)
		{
			Asteroid temp = asteroids.get(i);
			g.drawImage(temp.asteroid, temp.x, temp.y, this);
		}
		
		repaint();
	}
	
	
	/**
	 * 
	 *
	 */
	public class InputKeyEvents extends KeyAdapter {
		public void keyPressed(KeyEvent key) {
			int keys = key.getKeyCode();
			
			if(keys == KeyEvent.VK_W || keys == KeyEvent.VK_UP)//move up
				p1.changeDirec('u');
			if(keys == KeyEvent.VK_A || keys == KeyEvent.VK_LEFT)//move left
				p1.changeDirec('l');
			if(keys == KeyEvent.VK_S || keys == KeyEvent.VK_DOWN)//move down
				p1.changeDirec('d');
			if(keys == KeyEvent.VK_D || keys == KeyEvent.VK_RIGHT)//move right
				p1.changeDirec('r');
			if(keys == KeyEvent.VK_ENTER)//play game
			{
				PlaySound(Click);
				highScore = false;//hide high scores list
				title = false;
				createAst = true;
			}
			if(keys == KeyEvent.VK_ESCAPE)//end game
			{
				highScore = false;//hide high scores list
				title = true;
			}
			if(keys == KeyEvent.VK_SPACE)//shoot
			{
				p1.addBullet();
				PlaySound(Shoot);
			}
			if(keys == KeyEvent.VK_TAB)//decide if difficulty is even necessary 
				switch(difficulty){
				case('e'):
					difficulty = 'm';
					break;
				case('m'):
					difficulty = 'd';
					break;
				default:
					difficulty = 'e';
				}
			if(keys == KeyEvent.VK_F1)
				astGenSpeed --;
			if(keys == KeyEvent.VK_F2)
				astGenSpeed ++;
			if(keys == KeyEvent.VK_F3)
				astGenCap --;
			if(keys == KeyEvent.VK_F4)
				astGenCap ++;
				
		}
		 public void keyTyped(KeyEvent key) { }   
		 public void keyReleased(KeyEvent key) {	
			 int keys = key.getKeyCode();
			 //reset direction to 0 when key is released
			 	if(keys == KeyEvent.VK_W || keys == KeyEvent.VK_UP)
					p1.yDirec = 0;
			 	if(keys == KeyEvent.VK_A || keys == KeyEvent.VK_LEFT)
					p1.xDirec = 0;
			 	if(keys == KeyEvent.VK_S || keys == KeyEvent.VK_DOWN)
					p1.yDirec = 0;
			 	if(keys == KeyEvent.VK_D || keys == KeyEvent.VK_RIGHT)
					p1.xDirec = 0;
				if(keys == KeyEvent.VK_SPACE)
				{
					//readyToFire = false;
					
				}
		 }
	}
}
