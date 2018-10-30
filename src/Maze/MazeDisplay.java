package Maze;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

import Main.DisplayController;
import Main.TeleMazeController;
import grid.BoundedGrid;
import grid.Grid;
import grid.GridDisplay;
import grid.Location;

public class MazeDisplay extends JPanel implements MouseListener, KeyListener{

	private DisplayController controller; 
	
	private Maze maze; 
	private GridDisplay display; 
	
	private int wallLength = 2;
	private int cellSize = 24;
	
	private Grid<Color> colorGrid; 
	
	public MazeDisplay(DisplayController controller, Maze maze, int wallLength, int cellSize)
	{
		this.controller = controller;
		
		this.maze = maze; 
		this.wallLength = wallLength; 
		this.cellSize = cellSize; 
		maze.setDisplay(this);
		
		this.display = new GridDisplay(maze.getNumRows(), maze.getNumCols(), cellSize, 0);
		
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		
		this.setPreferredSize(new Dimension(display.width, display.height));
		
		this.colorGrid = new BoundedGrid(maze.getNumRows(), maze.getNumCols());
		
		for (int r = 0; r < maze.getNumRows(); r++)
		{
			for (int c = 0; c < maze.getNumCols(); c++)
			{
				Location loc = new Location(r, c);
				MazeCorner corner = maze.get(loc);
				
				Color color; 
				if (corner == null || corner.numWalls() == 4 )
				{
					color = Color.BLACK;
				}
				else 
				{
					color = Color.WHITE;
				}
				colorGrid.put(new Location(r, c), color);
			}
		}
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		g.setColor(Color.white);
		g.fillRect(0, 0, display.width, display.height);
		
		g.setColor(Color.black);
		//display.drawGridlines(g2);
		
		for (int r = 0; r < maze.getNumRows(); r++)
		{
			for (int c = 0; c < maze.getNumCols(); c++)
			{
				Location loc = new Location(r, c);
				fillMazeGrid(g2, loc);
				paintWalls(g2, loc);
			}
		}
	}
	

	
	private void paintWalls(Graphics2D g, Location loc)
	{
		MazeCorner corner = maze.get(loc);
		Point p = display.pointForLocation(loc);
		for (int i = 0; i < 4; i++) {
			if (corner == null)
			{
				continue; 
			}
			if (corner.hasWall(i)) {
				g.setColor(Color.black);
				if (i == MazeCorner.UP)
					g.fillRect(p.x, p.y, cellSize, wallLength);
				else if (i == MazeCorner.RIGHT)
					g.fillRect(p.x + cellSize - wallLength, p.y, wallLength, cellSize);
				else if (i == MazeCorner.DOWN)
					g.fillRect(p.x, p.y + cellSize - wallLength, cellSize, wallLength);
				else if (i == MazeCorner.LEFT)
					g.fillRect(p.x, p.y, wallLength, cellSize);
			}
		}
	}
	
	private void fillMazeGrid(Graphics2D g, Location loc)
	{
		Point p = display.pointForLocation(loc);
		
		g.setColor(colorGrid.get(loc));
		g.fillRect(p.x, p.y, cellSize, cellSize);
	}
	
	public void setColor(Location loc, Color c)
	{
		colorGrid.put(loc, c);
	}
	
	public void setColor(List<Location> locs, Color c)
	{
		for (Location loc: locs)
		{
			setColor(loc, c);
		}
	}
	
	public void clear()
	{
		for (int r = 0; r < maze.getNumRows(); r++)
		{
			for (int c = 0; c < maze.getNumCols(); c++)
			{
				Location loc = new Location(r, c);
				if (maze.get(loc) == null || maze.get(loc).numWalls() == 4) 
				{
					continue; 
				}
				setColor(loc, Color.WHITE);
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) { }

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		controller.keyPressed(key);
	}

	@Override
	public void keyReleased(KeyEvent e) { 
		int key = e.getKeyCode();
		controller.keyReleased(key);
	}

	@Override
	public void mouseClicked(MouseEvent e) { 
		Location loc = display.locationForPoint(e.getPoint());
		controller.mouseClicked(loc, maze);
	}

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
}
