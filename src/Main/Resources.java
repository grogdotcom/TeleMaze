package Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import Maze.Maze;

public class Resources {

	public static void printMazeToFile(Maze maze, String path, String fileName) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(path + "//" + fileName));
		writer.write(maze.toString());
		     
		writer.close();
	}
	
	 public static void shuffleArray(int[] ar, Random rnd)
	  {
	    // If running on Java 6 or older, use `new Random()` on RHS here
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	
}
