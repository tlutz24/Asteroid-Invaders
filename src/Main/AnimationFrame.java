/**
 * 
 */
package Main;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * @author Dylan
 *
 */
public class AnimationFrame extends JFrame {

	AnimationFrame()  {
		super ("Astroid Invader");
		this.setSize(640, 400);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		anPanel = new AnPanel();
		this.getContentPane().add(anPanel, BorderLayout.CENTER);
		
		this.setVisible(true);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AnimationFrame frame = new AnimationFrame();
	}
	protected AnPanel anPanel;
}
