/*	StarPolygon.java

	This program draws regular star polygons with any number of sides.
		
	Anthony Kozar
	April 23, 2018

	http://anthonykozar.net/
	
	Copyright (c) 2018 Anthony M. Kozar Jr.
	This file is licensed under the BSD 3-Clause License.
	See http://www.opensource.org/licenses/BSD-3-Clause for details.

 */

import	java.awt.*;
import	java.awt.event.*;
import	java.math.*;
import	javax.swing.*;


@SuppressWarnings("serial")
public class StarPolygon extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 800;
	final private int	WINHEIGHT = 800;
	
	final private String HELP_MESSAGE = "Press 1-9 or +/- to change the number of vertices, " +
	                                    "< or > to change the vertex increment, " +
	                                    "R to reset, ! to exit";
	
	protected int		numvertices;
	protected int		vertexincrement;
	protected double	centerx;
	protected double	centery;
	protected double	currentx;
	protected double	currenty;
	protected double	radius;
	
	protected double[][]	vertices;
	
	
	public StarPolygon()
	{
		super("Star Polygon");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
		
		// find the center of the window and a radius that will fit
		currentx = centerx = WINWIDTH * 0.5;
		currenty = centery = WINHEIGHT * 0.5;
		radius = (Math.min(WINWIDTH, WINHEIGHT) * 0.5) - 30.0;
		
		CalculateVertices(5);
		SetVertexIncrement(2);
	}

	/* public StarPolygon(String name, int xpos, int ypos, int width, int height)
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

	protected void SetVertexIncrement(int incr)
	{
		// do nothing if incr is not positive
		if (incr < 1)  return;

		vertexincrement = incr;
	}
	
	/*  */
	protected int CalculateNextVertex(int lastv)
	{
		int v  = (lastv + vertexincrement) % numvertices;
		return v;
	}
	
	protected void Reset()
	{
		currentx = centerx;
		currenty = centery;
		repaint();
	}
	
	protected int RandomOn(int low, int high)
	{
		return (low + ( (int)((high-low+1) * Math.random() )));
	}
	
	/*	Euclidean algorithm for finding the greatest common divisor.
		Code by Matt <http://stackoverflow.com/users/447191/matt>
		Copied from <http://stackoverflow.com/questions/4009198/java-get-greatest-common-divisor>
	 */
	protected int GCD(int a, int b)
	{
	   if (b==0) return a;
	   return GCD(b,a%b);
	}
	
	protected void DrawPoint(Graphics g, double x, double y)
	{
		// draw a single point
		g.drawLine((int)Math.round(x), (int)Math.round(y), 
		           (int)Math.round(x), (int)Math.round(y));
	}
	
	protected void DrawLine(Graphics g, double x1, double y1, double x2, double y2)
	{
		// draw a line from (x1,y1) to (x2,y2)
		g.drawLine((int)Math.round(x1), (int)Math.round(y1), 
		           (int)Math.round(x2), (int)Math.round(y2));
	}
	
	public void paint(Graphics g)
	{
		int firstv, lastv, nextv, numcycles;
		
		super.paint(g);
		
		// clear the window with background color
		g.setColor(Color.black);
		g.fillRect(0, 0, WINWIDTH, WINHEIGHT);
		
		// draw keyboard help
		g.setColor(Color.white);
		g.drawString("Vertices: " + numvertices + "  Vertex increment: " + vertexincrement, 10, WINHEIGHT - 24);
		g.drawString(HELP_MESSAGE, 10, WINHEIGHT - 10);
		
		// Draw the sides of the star polygon in a connected, cyclic sequence.
		// Need to draw multiple cycles when GCD(numvertices, vertexincrement) != 1
		numcycles = GCD(numvertices, vertexincrement);
		for (firstv = 0; firstv < numcycles; firstv++) {
			// start each cycle with vertex firstv
			lastv = firstv;
			do {
				// plot the next line in the star
				nextv = CalculateNextVertex(lastv);
				DrawLine(g, vertices[lastv][0], vertices[lastv][1], vertices[nextv][0], vertices[nextv][1]);
				lastv = nextv;
			}
			while (lastv != firstv);
		}
		
		return;
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
			CalculateVertices(numvertices-1);
			Reset();
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the number of vertices by one and resets the graphics
			CalculateVertices(numvertices+1);
			Reset();
		}
		else if	(key == '<' || key == ',') {
			// '<' (or ',') decreases the vertex increment by one and resets the graphics
			SetVertexIncrement(vertexincrement-1);
			Reset();
		}
		else if	(key == '>' || key == '.') {
			// '>' (or '.') increases the vertex increment by one and resets the graphics
			SetVertexIncrement(vertexincrement+1);
			Reset();
		}
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' reset the graphics
			Reset();
		}
		else if	(Character.isDigit(key) && key !='0') {
			// number keys change the number of vertices and reset the graphics
			int value = Integer.parseInt("" + key);
			CalculateVertices(value);
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
		StarPolygon	app = new StarPolygon();

		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
