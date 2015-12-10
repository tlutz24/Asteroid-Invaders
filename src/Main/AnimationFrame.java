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
public class AnimationFrame extends JFrame {

	/**
	 * Constructor for class
	 */
	AnimationFrame()  {
		super ("Asteroid Invaders!");//set title of frame
		this.setSize(640, 750);//set game size
		this.setResizable(false);//do not allow resizing
		this.setLocationRelativeTo(null);
		setBackground(Color.black);//set background to black
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		anPanel = new AnPanel();
		this.getContentPane().add(anPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
		
	}
	
	/**
	 * main function to begin game
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnimationFrame frame = new AnimationFrame();
	}
	protected AnPanel anPanel;
}
