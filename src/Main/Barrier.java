package Main;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Barrier{
	int x, y, width, height;
	List<Rectangle> barrier = new ArrayList<Rectangle>();
	Rectangle fullBarrier;
	Barrier(int x1, int y1, int strength){
		x = x1;
		y = y1;
		width = 75;
		height = strength * 10;
		for(int i = 0; i < strength; i++)
			barrier.add(new Rectangle(x, y + 10*i, width, 10));
		fullBarrier = new Rectangle(x, y, width, height);
	}
	public void hit(){
		y += 10;
		height -=10;
		if(height >= 0)
			barrier.remove(0);
		fullBarrier = new Rectangle(x, y, width, height);
	}		
}
