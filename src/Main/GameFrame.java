package Main;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

/**
 * @author Dylan Steele
 * @author Tyler Lutz
 * @author Jason Liu
 *
 */
public class GameFrame extends JFrame {

	/**
	 * Constructor for class
	 */
	GameFrame()  {
		//set title of frame
		super ("Asteroid Invaders!");
		//set game size
		this.setSize(640, 750);
		//do not allow resizing
		this.setResizable(false);
		
		this.setLocationRelativeTo(null);
		//set background to black
		setBackground(Color.black);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		mainGame = new GamePanel();
		
		this.getContentPane().add(mainGame, BorderLayout.CENTER);
		
		this.setVisible(true);
		
	}
	
	/**
	 * main function to begin game
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameFrame frame = new GameFrame();
	}
	protected GamePanel mainGame;
}
