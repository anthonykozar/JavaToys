/*	ChaosGameCarpet.java

	This program implements the "Chaos Game" method of drawing a Sierpinski carpet
	with varying number of subsquares per side.
		
	Anthony Kozar
	June 3, 2014

	http://anthonykozar.net/
	
	Copyright (c) 2014 Anthony M. Kozar Jr.
	This file is licensed under the BSD 3-Clause License.
	See http://www.opensource.org/licenses/BSD-3-Clause for details.

 */

import	java.awt.*;
import	java.awt.event.*;
import	java.math.*;
import	javax.swing.*;
import	java.util.concurrent.TimeUnit;

public class ChaosGameCarpet extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 800;
	final private int	WINHEIGHT = 800;
	final private int	NUMTHROWOUT = 8;		// how many points to not plot at the beginning
	
	final private String HELP_MESSAGE = "Press 2-9 or +/- to change the number of vertices, " +
	                                    "< or > to change the compression ratio, " +
	                                    "R to reset, ! to exit";
	
	protected boolean	firstpaint = true;
	protected int		numvertices;
	protected int		verticesperedge;
	protected double	compressionratio;		// factor by which distances are shrunk
	protected double	inverseratio;			// 1.0/compressionratio
	protected double	squaretop;
	protected double	squarebottom;
	protected double	squareleft;
	protected double	squareright;
	protected double	squarewidth;
	protected double	currentx;
	protected double	currenty;
	
	protected double[][]	vertices;
	
	
	public ChaosGameCarpet()
	{
		super("Sierpinski Carpet");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
		
		// find the sides of a square that will fit
		int shortestdim = Math.min(WINWIDTH, WINHEIGHT);
		squaretop = squareleft = 40.0;
		squarebottom = squareright = shortestdim - 40.0;
		squarewidth = squareright - squareleft;
		
		verticesperedge = 3;
		CalculateVertices(verticesperedge);
		SetCompressionRatio((double)verticesperedge);
		Reset();
		
		// I had hoped that disabling double-buffering would allow "cumulative drawing"
		// across multiple paint() calls, but it doesn't seem to work.
		// RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
	}

	/* public ChaosGameCarpet(String name, int xpos, int ypos, int width, int height)
	{
		super(name);
		setLocation(xpos, ypos);
		setSize(width, height);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
	}
	*/

	// num is expected to be the number of vertices along each side of the square
	// including the corners and should be at least 2
	protected void CalculateVertices(int num)
	{
		// do nothing if num is not >= 2
		if (num < 2)  return;
		
		verticesperedge = num;
		
		// total number of vertices is 4 corners plus num-2 on each side of square
		numvertices = 4 * (num - 1);
		vertices = new double[numvertices][2];
		
		/* Calculate the vertices of a regular polygon with num sides
		   by finding num equally-spaced points on a circle. The first 
		   vertex is placed 1/2 of the arc length of a side away from
		   the downward-pointing axis so that all of the polygons will
		   appear to be "resting" on a horizontal line.  */
		double vertexspacing = squarewidth / (num - 1);
		int verticesperside = num - 1;
		for (int v = 0; v < verticesperside; v++) {
			int i = v*4;	// base index for these four vertices
			// top side from left to right
			vertices[i][0] = squareleft + v*vertexspacing;
			vertices[i][1] = squaretop;
			// right side from top to bottom
			vertices[i+1][0] = squareright;
			vertices[i+1][1] = squaretop + v*vertexspacing;
			// bottom side from right to left
			vertices[i+2][0] = squareright - v*vertexspacing;
			vertices[i+2][1] = squarebottom;
			// left side from bottom to top
			vertices[i+3][0] = squareleft;
			vertices[i+3][1] = squarebottom - v*vertexspacing;
		}
	}

	protected void SetCompressionRatio(double ratio)
	{
		compressionratio = ratio;
		inverseratio = 1.0/compressionratio;
	}
	
	/* sets currentx & currenty to the next pair of coords */
	protected void CalculateNextPoint()
	{
		// select a random vertex and find the point inverseratio * the distance
		// from that vertex to the current location 
		int v = RandomOn(0, numvertices-1);
		currentx = vertices[v][0] - ((vertices[v][0] - currentx) * inverseratio);
		currenty = vertices[v][1] - ((vertices[v][1] - currenty) * inverseratio);
	
	}
	
	protected void Reset()
	{
		// set current coords to the center of the square
		double halfwidth = 0.5 * squarewidth;
		currentx = squareleft + halfwidth;
		currenty = squaretop + halfwidth;
		firstpaint = true;
	}
	
	protected int RandomOn(int low, int high)
	{
		return (low + ( (int)((high-low+1) * Math.random() )));
	}

	public void paint(Graphics g)
	{
		int v;
		
		// first time only
		if (firstpaint) {
			// It is incorrect not to always call super.paint() but this does 
			// allow us to draw an image cumulatively over many paint() calls.
			super.paint(g);

			// clear the window with background color
			g.setColor(Color.black);
			g.fillRect(0, 0, WINWIDTH, WINHEIGHT);
			firstpaint = false;
			
			// draw keyboard help
			g.setColor(Color.white);
			g.drawString("Vertices per edge: " + verticesperedge + "  Compression ratio: " + compressionratio, 10, WINHEIGHT - 24);
			g.drawString(HELP_MESSAGE, 10, WINHEIGHT - 10);
			
			// plot the vertices
			g.setColor(Color.red);
			for (v = 0; v < numvertices; v++) {
				// System.out.println("(" + (int)vertices[v][0] + "," + (int)vertices[v][1] + ")");
				g.drawLine((int)vertices[v][0], (int)vertices[v][1], (int)vertices[v][0], (int)vertices[v][1]);
			}
			
			// throw out the first few points since they may not be part of the fractal
			for (int i = 0; i < NUMTHROWOUT; i++)  CalculateNextPoint();
		}
		
		// plot the next point in the fractal
		CalculateNextPoint();
		g.setColor(Color.white);
		g.drawLine((int)Math.round(currentx), (int)Math.round(currenty), 
		           (int)Math.round(currentx), (int)Math.round(currenty));	// draws a single point
		
		// calling sleep() in paint() might be a "bad" practice as well?
		/* try {
			// wait 1 milliseconds
			// can also use NANOSECONDS, MICROSECONDS, SECONDS, MINUTES, HOURS, or DAYS
			TimeUnit.MILLISECONDS.sleep(1);
		} catch (InterruptedException e) {
			// ignore exception
		} */

		repaint();
	}

	/* These 3 methods are the implementation of the KeyListener interface.
	   keyTyped() responds to keyboard events as described below.
	 */
	public void keyPressed(KeyEvent event) {}
	public void keyReleased(KeyEvent event) {}
	public void keyTyped(KeyEvent event) 
	{
		char	key = event.getKeyChar();
		
		if	(key == '!') {
			// '!' exits the program
			System.exit(0);
		}
		else if	(key == '-') {
			// '-' decreases the number of vertices by one and resets the graphics
			CalculateVertices(verticesperedge-1);
			Reset();
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the number of vertices by one and resets the graphics
			CalculateVertices(verticesperedge+1);
			Reset();
		}
		else if	(key == '<' || key == ',') {
			// '<' (or ',') decreases the compression ratio by one and resets the graphics
			SetCompressionRatio(compressionratio-1.0);
			Reset();
		}
		else if	(key == '>' || key == '.') {
			// '>' (or '.') increases the compression ratio by one and resets the graphics
			SetCompressionRatio(compressionratio+1.0);
			Reset();
		}
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' reset the graphics
			Reset();
		}
		else if	(Character.isDigit(key) && key !='0' && key !='1') {
			// number keys change the number of vertices, compression ratio, and reset the graphics
			int value = Integer.parseInt("" + key);
			CalculateVertices(value);
			SetCompressionRatio((double)value);
			Reset();
		}
		
		return;
	}

	/* These 5 methods are the implementation of the MouseListener interface.
	   mouseClicked() causes the program to quit.
	 */
	public	void mouseClicked(MouseEvent event)
	{
		// System.exit(0);
	}
	
	public	void mousePressed(MouseEvent event)	{}
	public	void mouseReleased(MouseEvent event){}
	public	void mouseEntered(MouseEvent event)	{}
	public	void mouseExited(MouseEvent event)	{}


	public static void main(String args[])
	{
		ChaosGameCarpet	app = new ChaosGameCarpet();

		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
