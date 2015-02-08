/*	ChaosGamePolygon.java

	This program implements the "Chaos Game" method of drawing a Sierpinski triangle
	and extends the idea to regular polygons with any number of sides.
		
	Anthony Kozar
	April 27, 2014

	http://anthonykozar.net/
	
	Copyright (c) 2014 Anthony M. Kozar Jr.
	This file is licensed under the BSD 3-Clause License.
	See http://www.opensource.org/licenses/BSD-3-Clause for details.

 */

/*  

	This program has been modified by Masterzach32

	April 28, 2014
	
	http://masterzach32.net
	
	Copyright (c) 2014 Masterzach32.net
	This file is licensed under the BSD 3-Clause License.
	See http://www.opensource.org/licenses/BSD-3-Clause for details.
	
 */

import	java.awt.*;
import	java.awt.event.*;
import	java.math.*;
import	javax.swing.*;
import	java.util.concurrent.TimeUnit;

public class ChaosGamePolygon extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 800;
	final private int	WINHEIGHT = 800;
	final private int	NUMTHROWOUT = 8;		// how many points to not plot at the beginning
	
	final private String HELP_MESSAGE = "Press 1-9 or +/- to change the number of vertices, " +
	                                    "< or > to change the compression ratio, " +
	                                    "R to reset, ! to exit";
	
	protected boolean	firstpaint = true;
	protected int		numvertices;
	protected double	compressionratio;		// factor by which distances are shrunk
	protected double	inverseratio;			// 1.0/compressionratio
	protected double	centerx;
	protected double	centery;
	protected double	currentx;
	protected double	currenty;
	protected double	radius;
	
	protected double[][]	vertices;
	
	
	public ChaosGamePolygon()
	{
		super("Chaos Game");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
		System.out.println("Chaos Game Polygon started sucsessfully. Vertices set to 3.");
		
		// find the center of the window and a radius that will fit
		currentx = centerx = WINWIDTH * 0.5;
		currenty = centery = WINHEIGHT * 0.5;
		radius = (Math.min(WINWIDTH, WINHEIGHT) * 0.5) - 30.0;
		
		CalculateVertices(3);
		SetCompressionRatio(2.0);
		
		// I had hoped that disabling double-buffering would allow "cumulative drawing"
		// across multiple paint() calls, but it doesn't seem to work.
		// RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
	}

	/* public ChaosGamePolygon(String name, int xpos, int ypos, int width, int height)
	{
		super(name);
		setLocation(xpos, ypos);
		setSize(width, height);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
	}
	*/

	protected void CalculateVertices(int num)
	{
		// do nothing if num is not positive
		if (num < 1)  return;

		numvertices = num;
		vertices = new double[numvertices][2];
		
		/* Calculate the vertices of a regular polygon with num sides
		   by finding num equally-spaced points on a circle. The first 
		   vertex is placed 1/2 of the arc length of a side away from
		   the downward-pointing axis so that all of the polygons will
		   appear to be "resting" on a horizontal line.  */
		double arclen = 2.0*Math.PI/numvertices;
		double halfarclen = Math.PI/numvertices;
		for (int v = 0; v < numvertices; v++) {
			vertices[v][0] = centerx + radius * Math.sin(halfarclen + v*arclen);
			vertices[v][1] = centery + radius * Math.cos(halfarclen + v*arclen);
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
		currentx = centerx;
		currenty = centery;
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
			g.drawString("Vertices: " + numvertices + "  Compression ratio: " + compressionratio, 10, WINHEIGHT - 24);
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
	public void resetAlert() {
		System.out.println("Graphics Reset");
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
			System.out.println("Shutting down. Thanks for playing!");
			System.exit(0);
		}
		else if	(key == '-') {
			// '-' decreases the number of vertices by one and resets the graphics
			CalculateVertices(numvertices-1);
			System.out.println("Vertices decreased to " + numvertices);
			resetAlert();
			Reset();
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the number of vertices by one and resets the graphics
			CalculateVertices(numvertices+1);
			System.out.println("Vertices increased to " + numvertices);
			resetAlert();
			Reset();
		}
		else if	(key == '<' || key == ',') {
			// '<' (or ',') decreases the compression ratio by one and resets the graphics
			SetCompressionRatio(compressionratio-1.0);
			System.out.println("Compression ratio decreased to " + compressionratio);
			resetAlert();
			Reset();
		}
		else if	(key == '>' || key == '.') {
			// '>' (or '.') increases the compression ratio by one and resets the graphics
			SetCompressionRatio(compressionratio+1.0);
			System.out.println("Compression ratio increased to " + compressionratio);
			resetAlert();
			Reset();
		}
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' reset the graphics
			resetAlert();
			Reset();
		}
		else if	(Character.isDigit(key) && key !='0') {
			// number keys change the number of vertices and reset the graphics
			int value = Integer.parseInt("" + key);
			CalculateVertices(value);
			System.out.println("Vertices changed to " + numvertices);
			resetAlert();
			Reset();
		}
		else {
			// tells the user that key is not a valid input
			System.out.println(key + " is an invalid input. " + HELP_MESSAGE);
			resetAlert();
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
		ChaosGamePolygon	app = new ChaosGamePolygon();

		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
