package Main;

import Maze.Maze;
import grid.Location;

public class PathNode {

	public final Location loc;
	public final Maze maze; 
	private PathNode prev;
	
	public int depth = 0;

	public PathNode(Location curr, PathNode prev, Maze maze) {
		this.loc = curr;
		this.maze = maze; 
		setPrevious(prev);
	}
	
	public void setPrevious(PathNode prev)
	{
		this.prev = prev; 
		this.depth = prev == null ? 0 : prev.depth + 1;
	}
	
	public PathNode getPrevious()
	{
		return prev;
	}
	
	public String toString()
	{
		if (prev == null)
			return loc + ""; 
		return loc + " -> " + prev;
	}
	
	public PathNode getLast() 
	{
		PathNode path = this; 
		while (path.prev != null)
		{
			path = path.prev;
		}
		return path; 
	}

}
