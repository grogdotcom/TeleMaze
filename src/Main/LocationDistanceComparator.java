package Main;

import java.util.Comparator;

import grid.Location;

public class LocationDistanceComparator implements Comparator<PathNode>{

	private Location relativeLocation;
	
	public LocationDistanceComparator(Location relativeLocation)
	{
		this.relativeLocation = relativeLocation;
	}
	
	@Override
	public int compare(PathNode o1, PathNode o2) {
		// TODO Auto-generated method stub
		double distTo1 = relativeLocation.getDistanceTo(o1.loc);
		double distTo2 = relativeLocation.getDistanceTo(o2.loc);
		
		if ( distTo1 < distTo2)
			return -1; 
		else if ( distTo1 > distTo2)
			return 1;
		return 0; 
	}
}
