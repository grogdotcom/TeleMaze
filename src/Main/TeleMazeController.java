package Main;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import Maze.*;
import grid.Location;

public class TeleMazeController extends DisplayController {
		
	private List<Location> locList = new ArrayList();
	private List<TeleMaze> teleMazeList = new ArrayList();
	
	public static boolean canLinkMazesAtLocation(Maze from, Maze to, Location loc)
	{
		int row = from.getNumRows();
		int col = from.getNumCols();
		
		if (row != to.getNumRows() || col != to.getNumCols())
			return false;
		if (from.isValid(loc))
			return false;
		
		MazeCorner fromCorner = from.get(loc);
		MazeCorner toCorner = to.get(loc);
		
		for (int i = 0; i < 4; i++)
		{
			if (!fromCorner.hasWall(i) && !toCorner.hasWall(i))
				return false;
		}
		
		return true;
	}

	public PathNode getPathBetweenTeleMazes(TeleMaze fromMaze, Location fromLoc, TeleMaze destMaze, Location destLoc)
	{
		PathNode mazePath = getTeleMazePath(destMaze, destLoc, fromMaze, fromLoc);
		System.out.println(mazePath);
		if (mazePath == null)
			return null; 
		
		TeleMaze maze = fromMaze; 
		PathNode totalPath = new PathNode(mazePath.loc, null, mazePath.maze);
		while (mazePath.getPrevious() != null)
		{
			Location a = mazePath.loc; 
			Location b = mazePath.getPrevious().loc; 
			
			PathNode path = maze.generatePathToPoint(a, b);
			totalPath.getLast().setPrevious(path); 
			
			mazePath = mazePath.getPrevious(); 
			maze = maze.getNeighbor(b);
		}
		
		return totalPath;
	}
	
	public PathNode getPathBetweenTeleMazes(List<Location> locList, List<TeleMaze> mazeList) 
	{
		Location loc1, loc2;
		TeleMaze maze1, maze2;
		
		loc1 = locList.get(0);
		maze1 = mazeList.get(0);
		
		PathNode path = null;
		
		for (int i = 1; i < locList.size(); i++)
		{
			loc2 = locList.get(i);
			maze2 = mazeList.get(i);
			
			PathNode p = getPathBetweenTeleMazes(maze1, loc1, maze2, loc2);
			if (path == null) 
			{
				path = p;
			}
			else
			{
				p.getLast().setPrevious(path);
				path = p; 
			}
		}
		return path; 
	}
	
	private PathNode getTeleMazePath(TeleMaze fromMaze, Location fromLoc, TeleMaze destMaze, Location destLoc)
	{
		TeleMaze u, v;
		TeleMaze next_u, next_v; 
		
		u = fromMaze; 
		v = destMaze; 
		
		PathNode fromPath, destPath; 


		fromPath = new PathNode(fromLoc, null, fromMaze);
		destPath  = new PathNode(destLoc, null, destMaze);
		
		int dist;
		do 
		{
			next_u = null;
			next_v = null; 
			
			if (u.depth >= v.depth)
			{
				fromLoc = fromMaze.getTeleSpotToPreviousMaze();
				if (fromLoc != null)
				{
					next_u = u.getNeighbor(fromLoc);
					fromPath = new PathNode(fromLoc, fromPath, next_u);
				}			
			}
			if (v.depth >= u.depth)
			{
				destLoc = destMaze.getTeleSpotToPreviousMaze();
				if (destLoc != null)
				{
					next_v = v.getNeighbor(destLoc);
					destPath.getLast().setPrevious(new PathNode(destLoc, null, next_v));
				}	 
			}
			
			u = next_u == null ? u: next_u;
			v = next_v == null ? v: next_v;
	
			if (u.equals(v))
			{
				destPath.getLast().setPrevious(fromPath);
				return destPath;
			}
			dist = Math.max(u.depth, v.depth);
						
		} while (dist > 0);
		
		return null;
	}
	
	public List<PathNode> getVisibleMazeInRange(TeleMaze infMaze, Location viewLocation, int range)
	{
		//Maze visibleMaze = new Maze(infMaze.getNumRows(), infMaze.getNumCols());
		List<PathNode> visibleLocations = new ArrayList();
		
		//List<PathNode> pathsInRange = infMaze.getAccessableLocationsInRange(viewLocation, range);
		Map<TeleMaze, List<PathNode>> mazePathList = new HashMap();
		Map<Location, List<PathNode>> locationNodeMap = new HashMap();
		
		
		Queue<PathNode> mazeSearch = new LinkedList();
		mazeSearch.add(new PathNode(viewLocation, null, infMaze));
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
				//visibleMaze.put(loc, val.get(0).maze.get(loc).copy());
				visibleLocations.add(val.get(0));
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
				visibleLocations.add(minNode);
				
				//MazeCorner corner = minNode.maze.get(loc).copy();
				
				TeleMaze t = (TeleMaze)minNode.maze;
				if (t.isTeleSpot(loc))
				{
					TeleMaze n = t.getNeighbor(loc);
					visibleLocations.add(new PathNode(loc, null, n));
					
					/*MazeCorner cornerN = n.get(loc);
					for (int i = 0; i < 4; i++)
					{
						if (!cornerN.hasWall(i))
						{
							corner.removeWall(i);
						}
					}*/
				}
				
				//visibleMaze.put(loc, corner);
				
			}
		}

		//visibleMaze.fillWalls();
		//return visibleMaze;
		return visibleLocations;
	}

	@Override
	public void showPath() {
		// TODO Auto-generated method stub	
		
		Location from = locList.get(0);
		TeleMaze fromMaze = teleMazeList.get(0);
		
		Location to = locList.get(1);
		TeleMaze toMaze = teleMazeList.get(1);
		
		PathNode path = getPathBetweenTeleMazes(fromMaze, from, toMaze, to);
		drawPath(path, Color.cyan);
	}
	
	public void showVisible()
	{
		Location visible = locList.get(locList.size() - 1);
		TeleMaze visibleMaze = teleMazeList.get(teleMazeList.size() - 1);
		
		List<PathNode> visibleLocations = this.getVisibleMazeInRange(visibleMaze, visible, 7);
		for (PathNode path: visibleLocations)
		{
			drawPath(path, Color.GREEN);
		}
	}

	@Override
	public void appendLocation(Location loc, Maze maze) {
		// TODO Auto-generated method stub
		locList.add(loc);
		teleMazeList.add((TeleMaze)maze);
	}

	@Override
	public void clearLocations() {
		// TODO Auto-generated method stub
		locList.clear();
		teleMazeList.clear();
	}

	@Override
	public int numStoredLocations() {
		// TODO Auto-generated method stub
		return locList.size();
	}

	private Set<Integer> keysHeld = new HashSet();
	
	@Override
	public void keyPressed(int key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(int key) {
		// TODO Auto-generated method stub
		switch (key)
		{
		case KeyEvent.VK_P:
			showPath();
			break;
		case KeyEvent.VK_C: 
			clear(); 
			break; 
		case KeyEvent.VK_V:
			showVisible();
			break;
		}
	}

	@Override
	public void mouseClicked(Location loc, Maze maze) {
		// TODO Auto-generated method stub
		appendLocation(loc, maze);
	}
	
	

	
	
	
	
	
	/*public void displayVisibleMaze(InfiniteMaze infMaze, Location viewPoint)
	{
		
		visibleMaze = new Maze(infMaze.getGrid().getNumRows(), infMaze.getGrid().getNumCols());
		List<Location> visibleLocations = infMaze.getAccessableLocationsInRange(viewPoint, 5); 
		
		for (Location loc: visibleLocations)
		{
			infMaze.getGrid().get(loc).type = MazeCorner.CornerType.VISABLE;
			visibleMaze.getGrid().put(loc, infMaze.getGrid().get(loc));
		}
		
		for (Location loc: infMaze.getMap().keySet())
		{
			InfiniteMaze inf = infMaze.getMap().get(loc);  
			
			if (visibleLocations.contains(loc))
			{
				for (Location vis: ((Maze)inf).getAccessableLocationsInRange(loc, 5))
				{
					visibleMaze.getGrid().put(vis, inf.getGrid().get(vis));
					visibleMaze.getGrid().get(vis).type = MazeCorner.CornerType.VISABLE;
					inf.getGrid().get(vis).type = MazeCorner.CornerType.VISABLE;
				}
			}
		}
		visibleMazeFrame.repaint(); 
		
	}*/
	
	
}
