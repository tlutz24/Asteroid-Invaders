package Main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tyler
 *
 */

public class Barrier{
	int x, y, width, height;
	List<Rectangle> barrier = new ArrayList<Rectangle>();
	Rectangle fullBarrier;
	Barrier(int x1, int y1, int strength){
		x = x1;
		y = y1;
		width = height = strength;
		
		for(int i = 0; i < width; i++)
			for(int j = 0; j < strength; j++)
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
