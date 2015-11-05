/**
 * 
 */
package Main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @author Dylan
 *
 */
class AnPanel extends JPanel {
	AnPanel() {
		super();
		x = xNew = 310;
		y = yNew = 300;
		try {
			img = ImageIO
					.read(new File(
							"J:/MacWorkSpace/Game/Images/Ship.png"));
			backImg = ImageIO
					.read(new File(
							"J:/MacWorkSpace/Game/Images/background.png"));
			

		} catch (IOException e) {
			img = null;
		}
		
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
		System.out.println("Painting");
		g.clearRect(x, y, img.getWidth(null), img.getHeight(null));
		g.drawImage(img, xNew, yNew, this);
		x = xNew;
		y = yNew;
	}

	int x, y, xNew, yNew;
	protected Image img, backImg;
	protected Random rnd;
	
}
