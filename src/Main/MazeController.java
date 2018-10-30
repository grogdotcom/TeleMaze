package Main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Maze.Maze;
import grid.Location;

public class MazeController extends DisplayController {

	private List<Location> locList = new ArrayList();
	
	private Maze maze; 
	
	public MazeController(Maze maze)
	{
		this.maze = maze; 
	}
	
	@Override
	public void appendLocation(Location loc, Maze maze) {
		
		this.locList.add(loc);
	}
	
	public void showPath()
	{
		Location from = locList.get(0);
		Location to = locList.get(1);
		
		PathNode path = maze.generatePathToPoint(from, to);
		
		for (int i = 2; i < this.numStoredLocations(); i++)
		{
			from = to; 
			to = locList.get(i);
			PathNode temp = maze.generatePathToPoint(from, to);
			path.setPrevious(temp);
			//path = temp; 
		}
		
		drawPath(path, Color.CYAN);
	}

	@Override
	public void clearLocations() {
		// TODO Auto-generated method stub
		locList.clear();
	}

	@Override
	public int numStoredLocations() {
		// TODO Auto-generated method stub
		return locList.size();
	}

	@Override
	public void keyPressed(int key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(int key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(Location loc, Maze maze) {
		// TODO Auto-generated method stub
		
	}
	
	//public void drawVisibleLocations()
}
