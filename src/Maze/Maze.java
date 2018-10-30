package Maze;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import grid.BoundedGrid;
import grid.Grid;
import grid.Location;

import Main.*;

public class Maze extends BoundedGrid<MazeCorner> {

	protected Grid<MazeCorner> mazeGrid;

	int locationRange = 3;
	double[] emptyCellRatioValues = { .65, .95 };
	int visibleRange = 15;
	MazeDisplay display;

	Random random;

	public Maze(int row, int col) {
		super(row, col);
		random = new Random();
	}

	public void setDisplay(MazeDisplay display) {
		this.display = display;
	}

	public Maze(int row, int col, long randomSeed) {
		super(row, col);
		random = new Random(randomSeed);
	}

	/*
	 * 
	 */
	public void generateMaze(Location startLocation) {
		Stack<Location> locationStack = new Stack();
		this.put(startLocation, new MazeCorner(startLocation));
		locationStack.push(startLocation);

		while (!locationStack.isEmpty()) {

			// display.repaint();
			Location loc = locationStack.pop();
			// Location loc =
			// locationStack.remove(random.nextInt(locationStack.size()));

			MazeCorner locCorner = this.get(loc);

			removeWalls(locCorner);
			if (locCorner.numWalls() > 3)
				continue;

			int numRandSpots = 4 - locCorner.numWalls();

			for (int i = 0; i < numRandSpots; i++) {
				List<Location> locationsInRange = this.getAccessableEmptyLocationsInRange(loc, locationRange);
				if (locationsInRange.size() == 0) {
					continue;
				}

				int randIndex = random.nextInt(locationsInRange.size());

				Location to = locationsInRange.get(randIndex);
				PathNode path = generatePathToPoint(loc, to);

				this.put(path.loc, new MazeCorner(path.loc));
				fillPath(path);
				locationStack.add(path.loc);
			}
		}

		fillEmptySpaces();
		fillWalls();
		
	}

	public void fillEmptySpaces() {
		
		List<Location> emptyLocList = this.getEmptyLocations();
		if (emptyLocList.size() < 1) {
			return;
		}
		Collections.shuffle(emptyLocList);
		Location emptyLoc = null;
		Location entryLocation = null;
		int direction = 0;
		int i = 0;
		while (entryLocation == null) {
			emptyLoc = emptyLocList.get(i);
			List<Location> neighborList = new ArrayList();
			for (int d = 0; d < 4; d++) {

				Location neighbor = emptyLoc.getAdjacentLocation(d * 90);
				if (isValid(neighbor) && !isEmpty(neighbor) && get(neighbor).numWalls() < 4) {
					neighborList.add(neighbor);
				}
			}

			if (neighborList.size() > 0) {
				entryLocation = neighborList.get(random.nextInt(neighborList.size()));
				direction = emptyLoc.getDirectionToward(entryLocation) / 90;
			}

			i++;
			if (i == emptyLocList.size()) {
				break;
			}

		}

		if (entryLocation != null) {
			generateMaze(emptyLoc);
			get(emptyLoc).removeWall(direction);
			get(entryLocation).removeWall((direction + 2) % 4);
		}

	}

	public PathNode generatePathToPoint(Location from, Location to) {
		if (!(this.isValid(from) && this.isValid(to)))
			return null;

		PriorityQueue<PathNode> locationQueue = new PriorityQueue(100, new LocationDistanceComparator(to));
		List<Location> visitiedLocationList = new ArrayList();
		PathNode fromTuple = new PathNode(from, null, this);

		locationQueue.add(fromTuple);

		while (!locationQueue.isEmpty()) {
			PathNode path = locationQueue.poll();
			visitiedLocationList.add(path.loc);
			if (path.loc.equals(to)) {
				return path;
			}

			for (int i = 0; i < 4; i++) {

				int direction = i * 90;
				Location next = path.loc.getAdjacentLocation(direction);
				if (!this.isValid(next))
					continue;

				if (path.getPrevious() == null || !next.equals(path.getPrevious().loc)) {
					if (canTravelFromLocationToNeighborLocation(path.loc, next)) {
						if (!visitiedLocationList.contains(next))
							locationQueue.add(new PathNode(next, path, this));
					}
				}
			}

		}
		return null;
	}

	public void fillPath(PathNode path) {

		if (path.getPrevious() == null)
			return;

		Location prevLocation = path.getPrevious().loc;
		MazeCorner locCorner;

		if (this.isEmpty(path.loc)) {
			this.put(path.loc, new MazeCorner(path.loc));
		}

		locCorner = this.get(path.loc);
		int direction = path.loc.getDirectionToward(prevLocation) / 90;
		locCorner.removeWall(direction);

		direction = prevLocation.getDirectionToward(path.loc) / 90;
		if (this.isEmpty(prevLocation)) {
			MazeCorner prevCorner = new MazeCorner(prevLocation);
			prevCorner.removeWall(direction);
			this.put(prevLocation, prevCorner);
		} else {
			// System.out.println(path.loc + " -> " + prevLocation);
			// There should never be a case where the path is blocked by a wall
			// I dont think
			// But if the case comes up here is the spot to handle it

		}

		fillPath(path.getPrevious());

	}

	public List<Location> getEndLocations() {
		List<Location> endCornerList = new ArrayList();
		for (Location loc : this.getOccupiedLocations()) {
			MazeCorner corner = this.get(loc);
			if (corner.numWalls() == 3) {
				endCornerList.add(loc);
			}
		}
		return endCornerList;
	}

	private List<Location> getAccessableEmptyLocationsInRange(Location from, int range) {
		List<Location> emptyLocations = new ArrayList();
		for (PathNode path : getAccessableLocationsInRange(from, range)) {
			Location loc = path.loc;
			if (this.isEmpty(loc)) {
				emptyLocations.add(loc);
			}
		}
		return emptyLocations;
	}

	public List<PathNode> getAccessableLocationsInRange(Location loc, int range) {
		List<PathNode> accessableLocationList = new ArrayList();
		Queue<PathNode> pathQueue = new LinkedList();
		pathQueue.add(new PathNode(loc, null, this));

		while (!pathQueue.isEmpty()) {
			PathNode path = pathQueue.poll();
			accessableLocationList.add(path);

			if (path.depth >= range)
				continue;

			for (int i = 0; i < 4; i++) {
				int direction = i * 90;
				Location next = path.loc.getAdjacentLocation(direction);

				if (path.getPrevious() != null && path.getPrevious().loc.equals(next))
					continue;

				if (canTravelFromLocationToNeighborLocation(path.loc, next)) {

					// if (!accessableLocationList.contains(next))
					// {
					PathNode nextPath = new PathNode(next, path, this);
					pathQueue.add(nextPath);
					// }
				}
			}
		}

		return accessableLocationList;
	}

	boolean canTravelFromLocationToNeighborLocation(Location from, Location to) {
		if (!this.isValid(from))
			return false;
		if (!this.isValid(to))
			return false;

		int direction = from.getDirectionToward(to);

		if (!from.getAdjacentLocation(direction).equals(to))
			return false;

		if (!this.isEmpty(from)) {
			MazeCorner fromCorner = this.get(from);
			if (fromCorner.hasWall(direction / 90)) {
				return false;
			}
		}

		if (!this.isEmpty(to)) {
			MazeCorner toCorner = this.get(to);
			if (toCorner.hasWall(((direction + 180) % 360) / 90)) {
				return false;
			}
		}
		return true;
	}

	private void removeWalls(MazeCorner corner) {
		int numPossiblePassageways = corner.numWalls();
		if (numPossiblePassageways < 1)
			return;

		// double randValue = new Random().nextDouble();

		/*
		 * double emptyCellRatio = (double)this.getNumEmptyLocation() /
		 * (double)(this.getNumCols() * this.getNumRows()); int numPassageways =
		 * 1;
		 * 
		 * for (int i = 0; i < this.emptyCellRatioValues.length; i++) { if
		 * (emptyCellRatio > emptyCellRatioValues[i]) { numPassageways++; } }
		 * 
		 * int bound = 3 - numPassageways; for (int i = 0; i < bound; i++) {
		 * double randValue = random.nextDouble(); if (randValue >
		 * emptyCellRatio) numPassageways++; }
		 * 
		 */
		int[] walls = { 0, 1, 2, 3 };
		Resources.shuffleArray(walls, random);
		for (int j = 0; j < 4; j++) {
			int side = walls[j];
			if (!corner.hasWall(side))
				continue;

			float rand = random.nextFloat();
			// float check = (float)numPassageways /
			// (float)numPossiblePassageways;
			if (rand < .5f) {
				corner.removeWall(side);
				// numPassageways--;
			}
			// numPossiblePassageways--;
		}
	}

	private int getNumEmptyLocation() {
		int num = 0;
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumCols(); c++) {
				Location loc = new Location(r, c);
				if (this.isEmpty(loc)) {
					num++;
				}
			}
		}
		return num;
	}

	public List<Location> getEmptyLocations() {
		List<Location> empty = new ArrayList();
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumCols(); c++) {
				Location loc = new Location(r, c);
				if (this.isEmpty(loc)) {
					empty.add(loc);
				}
			}
		}
		return empty;
	}

	public void fillWalls() {
		List<Location> occupiedLocations = this.getOccupiedLocations();
		for (Location loc : occupiedLocations) {
			MazeCorner corner = this.get(loc);
			for (int side = 0; side < 4; side++) {
				int direction = side * 90;
				Location sideLocation = loc.getAdjacentLocation(direction);
				if (corner.hasWall(side)) {
					if (!this.isValid(sideLocation)) {
						continue;
					}

					if (!this.isEmpty(sideLocation)) {
						MazeCorner sideCorner = this.get(sideLocation);
						if (!sideCorner.hasWall((side + 2) % 4)) {
							sideCorner.addWall((side + 2) % 4);
						}
					}
				} else {

					if (!this.isValid(sideLocation)) {
						corner.addWall(side);
					}
				}
			}
		}
	}

	public String toString() {
		String s = this.getNumRows() + " " + this.getNumCols() + "\n";
		for (int r = 0; r < this.getNumRows(); r++) {
			for (int c = 0; c < this.getNumCols(); c++) {
				MazeCorner corner = this.get(new Location(r, c));
				s += (corner == null ? "0000" : corner) + " ";
			}
			s += "\n";
		}

		return s;
	}

}
