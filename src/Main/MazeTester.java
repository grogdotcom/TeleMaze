package Main;
import java.awt.Color;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import Maze.Maze;
import Maze.MazeDisplay;
import Maze.TeleMaze;
import grid.Location;

public class MazeTester {

	//static DisplayController cont = new DisplayController();
	
	public static void main(String args[]) 
	{
		int wallLength = 1;
		int cellSize = 12; 
		
		int row, col;
		row = 25; 
		col = 35;
		Location startLocation = new Location(row / 2, col / 2); 
		Maze maze = new Maze(row, col);
		maze.generateMaze(startLocation);
		
		DisplayController cont = new MazeController(maze);
		MazeDisplay mazedisplay = new MazeDisplay(cont, maze, wallLength, cellSize); 
		cont.registerMazeDisplay(maze, mazedisplay);
		displayGridFrame("Single Maze", mazedisplay);
		
		/*int y = 0; 
		while(true)
		{
			y++;
			List<Location> list = maze.getEmptyLocations(); 
			if (list.size() == 0)
			{
				break; 
			}
			
			maze.fillEmptySpaces();
			display.clear();
			display.repaint();
			
			for(long i = 0; i < 125000000; i++)
			{
			
			}
			
		}
		System.out.println(y);
		maze.fillWalls();
		display.repaint();*/
		
		TeleMaze tmaze = new TeleMaze(row, col);
		tmaze.generateMaze(startLocation);
		tmaze.expand(2, startLocation);
		Object[] telespots = tmaze.getTeleSpots().toArray();
		
		TeleMaze neighborTMaze = tmaze.getNeighbor((Location)telespots[0]);
		DisplayController tcont = new TeleMazeController();
		
		MazeDisplay tmazeDisplay = new MazeDisplay(tcont, tmaze, wallLength, cellSize);
		MazeDisplay neighborDisplay = new MazeDisplay(tcont, neighborTMaze, wallLength, cellSize);
		
		tcont.registerMazeDisplay(tmaze, tmazeDisplay);
		tcont.registerMazeDisplay(neighborTMaze, neighborDisplay);
		
		displayGridFrame("root", tmazeDisplay);
		displayGridFrame("Neighbor", neighborDisplay);
		
		/*TeleMaze maze = new TeleMaze(row, col);
		maze.generateMaze(startLocation);
		maze.expand(1, startLocation);
		Set<Location> teleSpots = maze.getTeleSpots();
		MazeDisplay startMazeDisplay =  new MazeDisplay(cont, maze, wallLength, cellSize);
		
		for (Location loc: teleSpots)
		{
			startMazeDisplay.setColor(loc, Color.YELLOW);
		}
		
		displayGridFrame("Basic Maze", startMazeDisplay);
		
		
		for (Location loc: maze.getTeleSpots())
		{
			Location temp = loc.getAdjacentLocation(90);
			MazeDisplay neighborMazeDisplay =  new MazeDisplay(cont, maze.getNeighbor(loc), wallLength, cellSize);
			neighborMazeDisplay.setColor(loc, Color.YELLOW);
			displayGridFrame("NeighborMaze", neighborMazeDisplay);
			
		}
		*/
		
		//Maze visibleMaze = TeleMazeController.getVisibleMazeInRange(maze, startLocation, 7);
		
		//displayGridFrame("Visible Maze", visibleMaze, wallLength, cellSize);
		
		exit(4);
		
	}
	
	
	
	public static void displayGridFrame(String name, MazeDisplay display)
	{
		JFrame window = new JFrame(name);
		window.setDefaultCloseOperation(3);
		window.setContentPane(display);
		window.setVisible(true);
		window.pack();
	}
}
