package Main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author Tyler Lutz
 * 
 * TODO: Implement ship in this class so it may be instantiated as an object in the game
 *
 */
public class Ship implements Runnable {
	
	int x, y, xNew, yNew, xDirec, yDirec;
	protected boolean readyToFire, shot = false;

	List<Integer> bX = new ArrayList<Integer>();
	List<Integer> bY = new ArrayList<Integer>();
	
	//Rectangle bullet;
	List<Rectangle> bullets = new ArrayList<Rectangle>();
	
	public Ship(){
		
	}
	
	public void paint(Graphics g){
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
