/*	HypotrochoidTest2.java
	
	Draws a hypotrochoid curve, similar to Spirograph, using parametric equations
	that I derived in rectangular coordinates.
	
	http://en.wikipedia.org/wiki/Hypotrochoid
	
	Anthony Kozar
	October 18, 2014
	
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;


public class HypotrochoidTest2 extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 700;
	final private int	WINHEIGHT = 720;

	final private String HELP_MESSAGE = "Click to randomize or use -,+,[,],<,> to adjust the parameters, " +
            "R to redraw, ! to exit";

	protected double	centerx;
	protected double	centery;
	protected double	drawingradius;				// maximum distance from the center that we can draw
	protected double	outerradius;				// radius of the outer circle
	protected double	innerradius;				// radius of the inner circle
	protected int		numlobes;					// the numerator in the ratio outerradius/innerradius
	protected int		numrevolutions;				// the denominator in the ratio outerradius/innerradius
	protected double	dlobes;						// floating-point copy of numlobes
	protected double	drevolutions;				// floating-point copy of numrevolutions
	protected double	ratioangles;				// the ratio outerradius/innerradius
	protected double	ratioradii;					// the ratio innerradius/outerradius
	protected double	penratio;					// the ratio penlength/innerradius
	protected double	penlength;					// distance from the center of inner circle to the "pen"
	

	public HypotrochoidTest2()
	{
		super("Hypotrochoid Test 2: hypotrochoids in rectangular coordinates");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);

		// find the center of the window and a radius that will fit
		centerx = WINWIDTH * 0.5;
		centery = WINHEIGHT * 0.5;
		drawingradius = (Math.min(WINWIDTH, WINHEIGHT) * 0.25) - 30.0;
	
		SetDrawingParms(48, 17, 0.75);
	}
	
	private void SetDrawingParms(int lobes, int revolutions, double penpos)
	{
		// calculate hypotrochoid parameters
		numlobes = lobes;
		numrevolutions = revolutions;
		dlobes = (double)lobes;
		drevolutions = (double)revolutions;
		penratio = penpos;
		ratioangles = dlobes / drevolutions;
		ratioradii = drevolutions / dlobes;
		if (numlobes > numrevolutions) {
			// inner circle is smaller than outer circle
			outerradius = drawingradius;
			innerradius = drawingradius * ratioradii;
		}
		else {
			// inner circle is larger than (or equal to) outer circle
			// try to scale the figure to compensate
			outerradius = drawingradius * ratioangles;
			innerradius = drawingradius;			
		}
		penlength = innerradius * penratio;
	}
	
	private void RandomizeParms()
	{
		int lobes, revs;
		double penpos, closeness;
		
		do {
			lobes = RandomOn(1, 50);
			revs = RandomOn(1, 40);
			// Try to avoid certain uninteresting combinations:
			// if lobes and revs are within 15% of each other, select new values.
			closeness = Math.abs(((double)lobes / (double)revs) - 1.0);
		}
		while(closeness < 0.15);
		
		penpos = RandomOn(3, 40) * 0.05;
		SetDrawingParms(lobes, revs, penpos);
	}
	
	protected int RandomOn(int low, int high)
	{
		return (low + ( (int)((high-low+1) * Math.random() )));
	}

	protected void drawPoint(Graphics g, double x, double y)
	{
		// we have to use drawLine() to draw a single point
		g.drawLine((int)Math.round(x), (int)Math.round(y), 
				   (int)Math.round(x), (int)Math.round(y));
	}
	
	public void paint(Graphics g)
	{
		final double arcstart = 0.0;							// angle of beginning of arc
		final double arcend = drevolutions * 2.0 * Math.PI;		// angle of end of arc
		final double angleincr = Math.PI/2000.0;				// increment at which to draw points
		double	x1, y1, x, y, lenbtwcenters, innerangle;

		super.paint(g);
		
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, WINWIDTH, WINHEIGHT);

		// draw keyboard help
		g.setColor( Color.black );
		g.drawString("Outer/Inner circle ratio: " + numlobes + "/" + numrevolutions + "  Pen position: " + penratio, 10, WINHEIGHT - 24);
		g.drawString(HELP_MESSAGE, 10, WINHEIGHT - 10);
		
		g.setColor( Color.blue );
		lenbtwcenters = outerradius - innerradius;				// distance between circle centers
		
		// calculate r and theta parametrically based on the total angle of rotation
		for (double angle = arcstart; angle <= arcend; angle += angleincr) {
			innerangle = angle - (angle * ratioangles);			// angle of the pen relative to a horizontal line
			// calculate coordinates of point relative to the origin
			x1 = lenbtwcenters * Math.cos(angle) + penlength * Math.cos(innerangle);
			y1 = lenbtwcenters * Math.sin(angle) + penlength * Math.sin(innerangle);
			// calculate coordinates relative to our "drawing origin"
			x = centerx + x1;
			y = centery - y1;
			drawPoint(g, x, y);
		}
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
			// '-' decreases the number of lobes by one and repaints the curve
			SetDrawingParms(numlobes-1, numrevolutions, penratio);
			this.repaint();
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the number of lobes by one and repaints the curve
			SetDrawingParms(numlobes+1, numrevolutions, penratio);
			this.repaint();
		}
		else if	(key == '[' || key == '{') {
			// '[' (or '{') decreases the number of revolutions it takes to draw the curve (and repaints)
			// Don't allow the value to go below 1.
			if (numrevolutions > 1) {
				SetDrawingParms(numlobes, numrevolutions-1, penratio);
				this.repaint();
			}
		}
		else if	(key == ']' || key == '}') {
			// ']' (or '}') increases the number of revolutions it takes to draw the curve (and repaints)
			SetDrawingParms(numlobes, numrevolutions+1, penratio);
			this.repaint();
		}
		else if	(key == '<' || key == ',') {
			// '<' (or ',') decreases the distance between the inner circle's center and the pen (and repaints)
			SetDrawingParms(numlobes, numrevolutions, penratio-0.05);
			this.repaint();
		}
		else if	(key == '>' || key == '.') {
			// '>' (or '.') increases the distance between the inner circle's center and the pen (and repaints)
			SetDrawingParms(numlobes, numrevolutions, penratio+0.05);
			this.repaint();
		}
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' cause the same curve to be redrawn
			// RandomizeParms();
			this.repaint();
		}
		else if	(Character.isDigit(key) && key !='0' && key !='1') {
			// number keys don't do anything
		}
		
		return;
	}

	/* These 5 methods are the implementation of the MouseListener interface.
	   mouseClicked() causes a new random curve to be drawn.
	 */
	public void mouseClicked( MouseEvent event )
	{
		RandomizeParms();
		this.repaint();
	}

	public void mousePressed( MouseEvent event )	{}
	public void mouseReleased( MouseEvent event )	{}
	public void mouseEntered( MouseEvent event )	{}
	public void mouseExited( MouseEvent event )		{}

	public static void main(String[] args)
	{
		HypotrochoidTest2 app = new HypotrochoidTest2();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

	}

}
