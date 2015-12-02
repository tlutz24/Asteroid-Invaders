package Main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tyler
 *TODO: make barriers more complex - maybe composed of 10-20px blocks
 */

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
		
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 3; j++)
				barrier.add(new Rectangle(x+25*i, y+10*j, 25, 10));
		
	}
	public int isHit(Rectangle r)
	{
		for(int i = 0; i < barrier.size(); i++)
			if(r.intersects(barrier.get(i)))
				return i;
		return -1;
	}
	public void hit(int i){
		if(barrier.size() > 0)
			barrier.remove(i);
		//fullBarrier = new Rectangle(x, y, width, height);
	}	
	public void draw(Graphics g){
		Rectangle temp;
		for(int i = 0; i < barrier.size(); i++)
		{
			temp = barrier.get(i);
			g.fillRect(temp.x, temp.y, temp.width, temp.height);
		}
	}
}
