/**
 * 
 */
package Main;

/**
 * @author Tyler
 *
 */
public class HighScores {

	private String Name;
	private int Score, Time;
	
	HighScores(String name, int score, int time){
		Name = name;
		Score = score;
		Time = time;
	}
	
	
	public String getData(){
		return Name + "\t" + Score + "\t" + Time;
	}
	
	public boolean isHigher(HighScores s1){
		if(s1.Score > Score)
			return false;
		return true;
	}
}
