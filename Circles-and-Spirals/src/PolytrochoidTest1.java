/*	PolytrochoidTest1.java
	
	Draws what I am calling a "polytrochoid" curve, which is similar to a hypotrochoid but 
	has more than two wheels nested and rotating within each other.
	
	This test allows up to ten wheels with random parameters.  It often does not draw 
	enough to complete a closed curve however (which is sometimes nice, but often not).
	
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


public class PolytrochoidTest1 extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 1600;
	final private int	WINHEIGHT = 1000;
	final public int	MAXCIRCLES = 10;

	// parameters of the polytrochoid that is displayed when program first runs
	final private int	 initcircles = 4;
	final private int[]	 initradii = {43, 24, 8, 2};
	final private double initpenpos = 0.0;
	
	final private String HELP_MESSAGE = "Click to randomize or use -,+,[,],<,> to adjust the parameters, " +
            "R to redraw, ! to exit";

	protected double	centerx;
	protected double	centery;
	protected double	drawingradius;				// maximum distance from the center that we can draw
	protected int		numcircles;					// number of circles in use (must be <= MAXCIRCLES)
	protected int[]		iradii;						// integer radii of each circle
	protected double[]	dradii;						// floating-point copy of iradii[]
	protected double[]	pixradii;					// actual radii of each circle in pixels
	protected double[]	angleratios;				// the ratio btw radii of consecutive circles (outer/inner)
	protected double[]	radiiratios;				// the ratio btw radii of consecutive circles (inner/outer)
	protected double[]	radiidiffs;					// the difference btw radii of this & the next circle
	protected double	penratio;					// the ratio penlength/innerradius
	protected double	penlength;					// distance from the center of inner circle to the "pen"
	protected double	revolutions;				// num of revolutions main angle needs to complete the figure

	public PolytrochoidTest1()
	{
		super("Polytrochoid Test 1: polytrochoids in rectangular coordinates");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
		
		// allocate space for the circle parameters
		iradii = new int[MAXCIRCLES];
		dradii = new double[MAXCIRCLES];
		pixradii = new double[MAXCIRCLES];
		angleratios = new double[MAXCIRCLES];
		radiiratios = new double[MAXCIRCLES];
		radiidiffs = new double[MAXCIRCLES];
		
		// find the center of the window and a radius that will fit
		centerx = WINWIDTH * 0.5;
		centery = WINHEIGHT * 0.5;
		drawingradius = (Math.min(WINWIDTH, WINHEIGHT) * 0.25) - 30.0;
	
		SetDrawingParms(initcircles, initradii, initpenpos);
	}
	
	private void SetDrawingParms(int circles, int[] radii, double penposition)
	{
		// validate arguments
		if (circles < 2) {
			System.err.println("Error in SetDrawingParms(): circles cannot be less than 2 (was " + circles + ")");
			return;
		}
		else if (circles > MAXCIRCLES) {
			System.err.println("Error in SetDrawingParms(): circles cannot be greater than MAXCIRCLES (was " + circles + ")");
			return;
		}
		else numcircles = circles;
		if (radii.length < numcircles) {
			System.err.println("Error in SetDrawingParms(): fewer than " + numcircles + " values in radii[] (has " + radii.length + ")");
			return;			
		}
		
		double maxradii = Double.MIN_VALUE;
		
		// calculate polytrochoid parameters
		for (int i = 0; i < numcircles; i++) {
			iradii[i] = radii[i];
			dradii[i] = (double)iradii[i];
			maxradii = Math.max(dradii[i], maxradii);
			if (i > 0) {
				angleratios[i] = dradii[i-1] / dradii[i];
				radiiratios[i] = dradii[i] / dradii[i-1];
			}
			else {
				angleratios[i] = 1.0;
				radiiratios[i] = 1.0;
			}
		}
		
		// try to scale the figure to fit the maxradii
		double	factor = drawingradius / maxradii;
		for (int i = 0; i < numcircles; i++) {
			pixradii[i] = dradii[i] * factor;
		}
		
		// calculate distances between circle centers
		for (int i = 0; i < numcircles-1; i++) {
			radiidiffs[i] = pixradii[i] - pixradii[i+1];
		}
		
		revolutions = dradii[1];	// FIXME
		// penposition is a scalar for the innermost circle's radius
		penratio = penposition;
		penlength = radiidiffs[numcircles-1] = pixradii[numcircles-1] * penratio;
	}
	
	private void RandomizeParms()
	{
		int circles;
		int[] radii;
		double penpos, closeness;
		
		circles = RandomOn(3, MAXCIRCLES);
		radii = new int[circles];
		do {
			for (int i = 0; i < circles; i++) {
				radii[i] = RandomOn(1, 40);
			}
			// Try to avoid certain uninteresting combinations:
			// if lobes and revs are within 15% of each other, select new values.
			closeness = 1.0; // Math.abs(((double)lobes / (double)revs) - 1.0);
		}
		while(closeness < 0.15);
		
		penpos = RandomOn(3, 40) * 0.05;
		SetDrawingParms(circles, radii, penpos);
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
		final double arcend = revolutions * 2.0 * Math.PI;		// angle of end of arc
		final double angleincr = Math.PI/2000.0;				// increment at which to draw points
		double	x, y, innerangle, lastangle;

		super.paint(g);
		
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, WINWIDTH, WINHEIGHT);

		// draw keyboard help
		String parmsMessage = "Circles: " + numcircles + "  Circle ratios: "  + iradii[0];
		for (int i = 1; i < numcircles; i++)  parmsMessage = parmsMessage + "/" + iradii[i];
		parmsMessage = parmsMessage + "  Pen position: " + penratio;
		g.setColor(Color.black);
		g.drawString(parmsMessage, 10, WINHEIGHT - 24);
		g.drawString(HELP_MESSAGE, 10, WINHEIGHT - 10);
		
		g.setColor(Color.blue);		
		// calculate coordinates parametrically based on the total angle of rotation
		for (double angle = arcstart; angle <= arcend; angle += angleincr) {
			// calculate coordinates of first inner circle center relative to the origin
			x = radiidiffs[0] * Math.cos(angle);
			y = radiidiffs[0] * Math.sin(angle);
			lastangle = angle;
			for (int i = 1; i < numcircles; i++) {
				// calculate the angle to the next circle center (or the pen) relative to a horizontal line
				innerangle = lastangle - (lastangle * angleratios[i]);
				// calculate coordinates of next circle center (or the pen) by finding the offsets from last center
				x += radiidiffs[i] * Math.cos(innerangle);
				y += radiidiffs[i] * Math.sin(innerangle);				
				lastangle = innerangle;
			}
			// calculate coordinates relative to our "drawing origin"
			x = centerx + x;
			y = centery - y;
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
		//	SetDrawingParms(numlobes-1, numrevolutions, penratio);
			this.repaint();
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the number of lobes by one and repaints the curve
		//	SetDrawingParms(numlobes+1, numrevolutions, penratio);
			this.repaint();
		}
		else if	(key == '[' || key == '{') {
			// '[' (or '{') decreases the number of revolutions it takes to draw the curve (and repaints)
			// Don't allow the value to go below 1.
		/*	if (numrevolutions > 1) {
				SetDrawingParms(numlobes, numrevolutions-1, penratio);
				this.repaint();
			} */
		}
		else if	(key == ']' || key == '}') {
			// ']' (or '}') increases the number of revolutions it takes to draw the curve (and repaints)
		//	SetDrawingParms(numlobes, numrevolutions+1, penratio);
			this.repaint();
		}
		else if	(key == '<' || key == ',') {
			// '<' (or ',') decreases the distance between the inner circle's center and the pen (and repaints)
			SetDrawingParms(numcircles, iradii, penratio-0.05);
			this.repaint();
		}
		else if	(key == '>' || key == '.') {
			// '>' (or '.') increases the distance between the inner circle's center and the pen (and repaints)
			SetDrawingParms(numcircles, iradii, penratio+0.05);
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
		PolytrochoidTest1 app = new PolytrochoidTest1();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

	}

}
