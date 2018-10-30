package Maze;
import grid.Location;

public class MazeCorner {

	public static final int UP = 0; 
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3; 
	
	public enum CornerType {
		PATH, END, VISABLE, VISITED, TELEPORT; 
	}
	
	public CornerType  type = CornerType.PATH; 
	
	private Location location;  
	
	int walls[] = new int[4];
	
	public MazeCorner(Location location)
	{
		this.location = location;
		
		addAllWalls();
	}
	
	public MazeCorner(Location locaction, String walls)
	{
		this.location = location;
		for (int i = 0; i < 4; i++)
		{
			if (walls.charAt(i) == '1')
			{
				addWall(i);
			}
		}
	}
	
	public void addWall(int side) 
	{
		walls[side] = 1; 
	}
	
	public void addAllWalls()
	{
		for (int i = 0; i < 4; i++)
		{
			addWall(i);
		}
	}
	
	public void removeWall(int side)
	{
		walls[side] = 0;
	}
	
	public boolean hasWall(int side)
	{
		return walls[side] > 0; 
	}
	
	public int numWalls()
	{
		int count = 0; 
		for (int i = 0; i < 4; i++)
		{
			if (hasWall(i))
				count++;
		}
		return count; 
	}
	
	public String toString()
	{
		String s = "";
		for (int i = 0; i < 4; i++)
		{
			s += hasWall(i) ? 1 : 0;
		}
		return s;
	}
	
	public MazeCorner copy()
	{
		return new MazeCorner(this.location, toString());
	}
	
	
	
	
}
