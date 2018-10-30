package Maze;

import Main.LocationDistanceComparator;
import Main.PathNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import grid.Location;

public class TeleMaze extends Maze {
	
	public static final int NUM_TELE_SPOTS = 4; 
	public static final int TELE_SPOT_RANGE = 5; 
	
	public String name; 
 
	public final int depth; 
	public boolean expanded = false;
	
	public Map<Location, TeleMaze> teleSpots = new HashMap(); 
	
	public TeleMaze(int row, int col, long seed)
	{	
		super(row, col, seed); 
		this.depth = 0; 
	}
	
	public TeleMaze(int row, int col)
	{	
		super(row, col); 
		this.depth = 0; 
	}

	private TeleMaze(Location loc, TeleMaze fromMaze)
	{
		super(fromMaze.getNumRows(), fromMaze.getNumCols());
		
		this.depth = fromMaze.depth + 1;
		
		teleSpots.put(loc, fromMaze);
		joinMazesAtLocation(fromMaze, this, loc);
	}
	
	public void expand(int numTeleSpots, Location startLoc)
	{
		List<Location> endLocations = getEndLocations(); 
		endLocations.remove(startLoc);
		
		if (numTeleSpots > endLocations.size())
			numTeleSpots = endLocations.size();
		
		while (teleSpots.size() < numTeleSpots)
		{
			int randIndex = random.nextInt(endLocations.size());
			Location loc = endLocations.remove(randIndex);
			
			if (loc.getDistanceTo(startLoc) < TELE_SPOT_RANGE)
				continue; 
			
			boolean close = false; 
			for (Location teles: teleSpots.keySet()) {
				if (loc.getDistanceTo(teles) < TELE_SPOT_RANGE) {	
					close = true;
					break; 
				}
			}
			if (close)
				continue;
			
			teleSpots.put(loc, new TeleMaze(loc, this));		
		}
		expanded = true; 
	}
	
	public Location getTeleSpotToPreviousMaze()
	{
		if (depth == 0)
		{
			return null; 
		}
		
		
		for (Location loc: getTeleSpots())
		{
			TeleMaze maze = getNeighbor(loc);
			if (maze.depth < depth)
			{
				return loc;
			}
		}
		
		return null;
	}
	

	private void joinMazesAtLocation(Maze originMaze, Maze teleMaze, Location teleport)
	{
		List<PathNode> visibleLocationsFromOriginMaze = originMaze.getAccessableLocationsInRange(teleport, TELE_SPOT_RANGE); 
		for (PathNode path: visibleLocationsFromOriginMaze)
		{
			teleMaze.put(path.loc, new MazeCorner(path.loc)); 
		}
		teleMaze.generateMaze(teleport);
		
		originMaze.get(teleport).type = MazeCorner.CornerType.TELEPORT;
		teleMaze.get(teleport).type = MazeCorner.CornerType.TELEPORT;
			
	}
	
	/*@Override
	public List<PathNode> getAccessableLocationsInRange(Location from, int range)
	{
		if (!expanded)
		{
			return super.getAccessableLocationsInRange(from, range);
		}
		
		List<PathNode> accessableLocationList = new ArrayList();
		
		Map<TeleMaze, List<PathNode>> mazePathList = new HashMap();
		Map<Location, List<PathNode>> locationNodeMap = new HashMap();
		
		
		Queue<PathNode> mazeSearch = new LinkedList();
		mazeSearch.add(new PathNode(from, null, this));
		List<TeleMaze> seenMazes = new ArrayList();
		
		while(!mazeSearch.isEmpty())
		{
			PathNode check = mazeSearch.poll();
			TeleMaze maze = (TeleMaze)check.maze;
			seenMazes.add(maze);
			mazePathList.put(maze, maze.getAccessableLocationsInRange(check.loc, range - check.depth));
			
			for (PathNode path: mazePathList.get(maze))
			{
				if (!locationNodeMap.containsKey(path.loc))
				{
					locationNodeMap.put(path.loc, new ArrayList<PathNode>());
				}
				locationNodeMap.get(path.loc).add(path);
				
				if (maze.isTeleSpot(path.loc) && !seenMazes.contains(maze.getNeighbor(path.loc)))
				{
					mazeSearch.add(new PathNode(path.loc, path.getPrevious(), maze.getNeighbor(path.loc)));
				}
			}
		}

		for (Location loc: locationNodeMap.keySet())
		{
			List<PathNode> val = locationNodeMap.get(loc);
			if (val.size() == 1)
			{
				accessableLocationList.add(val.get(0));
			}
			else 
			{
				PathNode minNode = val.get(0);
				int minDistance = minNode.depth;
				for (int i = 1; i < val.size(); i++)
				{
					if (val.get(i).depth < minDistance)
					{
						minNode = val.get(i);
						minDistance = val.get(i).depth;
					}
					else if (val.get(i).depth == minDistance)
					{
						System.out.println("whoops");
					}
				}
				
				/*MazeCorner corner = minNode.maze.get(loc).copy();
				
				TeleMaze t = (TeleMaze)minNode.maze;
				if (t.isTeleSpot(loc))
				{
					TeleMaze n = t.getNeighbor(loc);
					
					MazeCorner cornerN = n.get(loc);
					for (int i = 0; i < 4; i++)
					{
						if (!cornerN.hasWall(i))
						{
							corner.removeWall(i);
						}
					}
				}
				
				accessableLocationList.add(minNode);
			}
		}

		return accessableLocationList;
	
	}*/
	
	public String toString()
	{
		return name;
	}
	
	public Set<Location> getTeleSpots()
	{
		return this.teleSpots.keySet();
	}
	
	public boolean isTeleSpot(Location loc)
	{
		return this.teleSpots.keySet().contains(loc);
	}
	
	public TeleMaze getNeighbor(Location teleSpot)
	{
		return teleSpots.get(teleSpot);
	}
	
}
