package Main;

import java.awt.Rectangle;
/**
 * 
 * @author Tyler Lutz
 * 
 * TODO: Implement ship in this class so it may be instantiated as an object in the game
 *
 */
public class Ship implements Runnable {
	
	int x, y, xDirec, yDirec;
	protected boolean readyToFire, shot = false;
	
	Rectangle bullet;
	
	public Ship(){
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
