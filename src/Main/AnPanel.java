/**
 * 
 */
package Main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
class AnPanel extends JPanel {
	private Graphics gImg;
	AnPanel() {
		super();
		x = xNew = 310;
		y = yNew = 300;
		try {
			img = ImageIO	//attempt to read local files
					.read(new File("Images/ship.png"));
			backImg = ImageIO
					.read(new File("Images/background.png"));
			

		} catch (IOException e) {
			img = null;
		}
		this.addKeyListener(new InputKeyEvents());
		this.setFocusable(true);
		
		rnd = new Random();

		int delay = 33; // milliseconds
		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// ...Perform a task...
				//xNew = x + 5 - rnd.nextInt(10);
				//yNew = y + 5 - rnd.nextInt(10);
				if (xNew > getSize().width) {
					xNew = 0;
				}
				if (xNew < 0) {
					xNew = getSize().width - 1;
				}
				if (yNew > getSize().height) {
					yNew = 0;
				}
				if (yNew < 0) {
					yNew = getSize().height - 1;
				}
				repaint();

			}
		};
		new Timer(delay, taskPerformer).start();

	}
	

	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D)g1;
		// g.drawString("Animation", x,y);
		//System.out.println("Painting");
		g.setBackground(Color.black);
		g.clearRect(0, 0, 640, 480);
		g.drawImage(img, xNew, yNew, this);
		x = xNew;
		y = yNew;
	}
	
	public void MoveImg(int x, int y) {
		xNew = x + xNew;
		yNew = y + yNew;
	}
	
	int x, y, xNew, yNew;
	protected Image img, backImg;
	protected Random rnd;
	
	public class InputKeyEvents extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keys = e.getKeyCode();
			int x, y;
			if(keys == KeyEvent.VK_W) {
				y = -4;
				x = 0;
				MoveImg(x, y);
				System.out.println("Move Up");
			}
			if(keys == KeyEvent.VK_A) {
				y = 0;
				x = -4;
				MoveImg(x, y);
				System.out.println("Move Left");
			}
			if(keys == KeyEvent.VK_S) {
				y = 4;
				x = 0;
				MoveImg(x, y);
				System.out.println("Move Down");
			}
			if(keys == KeyEvent.VK_D) {
				y = 0;
				x = 4;
				MoveImg(x, y);
				System.out.println("Move Right");
			}
			repaint();
		}
		 public void keyTyped(KeyEvent ke) {}
	     
		    
		 public void keyReleased(KeyEvent ke) {}

	}

	
}
