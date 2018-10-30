package Main;

import java.io.IOException;

import Maze.Maze;
import grid.Location;

public class MazeGenerator {

	public static final int INIT_MAZE = 0; 
	public static final int MAZE_GENERATOR = 1; 
	
	public static void main(String args[])
	{
		if (args.length < 1)
		{
			System.exit(0); 
		}
		
		int type = new Integer(args[0]);
		
		String path = args[1];
		String fileName = args[2];
		
		if (type == INIT_MAZE)
		{
			int row = new Integer(args[3]);
			int col = new Integer(args[4]);
			
			int startRow = new Integer(args[5]);
			int startCol = new Integer(args[6]);
			
			int numTeleSpots = new Integer(args[7]);
			
			Maze maze = new Maze(row, col);
			maze.generateMaze(new Location(startRow, startCol));
			
			try {
				Resources.printMazeToFile(maze, path, fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
