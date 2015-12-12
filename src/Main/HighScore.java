package Main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class HighScore extends JFrame {
	protected JTextArea name;
	Font Title = new Font("Monospaced", Font.BOLD, 15);
	
	HighScore() {
		super("High Score");
		this.setSize(360, 250);
		this.setResizable(false);//do not allow resizing
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.getContentPane().add( new Panel(), BorderLayout.CENTER);
		
		this.setVisible(true);
	}
	
	public class Panel extends JPanel {
		
		Panel() {
			this.setBackground(Color.WHITE);
			name = new JTextArea(12, 20);
			name.setEditable(false);
			
			this.add(name);
			name.setFont(Title);
			name.setText("High Scores");
			/*
			 * The text area is set to be able to add in the DataBase information
			 * The high scores can be added to this text area that is centered in the JFrame
			 * 
			 */
			this.setVisible(true);
			
		}
		
		
	}

}
