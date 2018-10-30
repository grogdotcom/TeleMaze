package Main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Maze.MazeCorner;
import Maze.MazeDisplay;
import Maze.Maze;
import grid.Location;

public abstract class DisplayController {
	
	public Map<Maze, MazeDisplay> displays = new HashMap();
	
	public abstract void appendLocation(Location loc, Maze maze);
	
	public abstract void clearLocations();
	
	public abstract int numStoredLocations();
	
	public abstract void showPath();
	
	protected void drawPath(PathNode path, Color c) 
	{	
		if (path == null)
		{
			return; 
		}
		
		displays.get(path.maze).setColor(path.loc, c); 
		displays.get(path.maze).repaint();
		drawPath(path.getPrevious(), c);	
	}
	
	public void clear()
	{
		clearLocations();
		for (MazeDisplay display: displays.values())
		{
			display.clear();
			display.repaint();
		}
		
	}
	
	public void registerMazeDisplay(Maze maze, MazeDisplay display)
	{
		displays.put(maze, display);
	}
	
	public abstract void keyPressed(int key);
	
	public abstract void keyReleased(int key);
	
	public abstract void mouseClicked(Location loc, Maze maze);
}


