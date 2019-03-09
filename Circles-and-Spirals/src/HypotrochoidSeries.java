/*	HypotrochoidSeries.java
	
	Draws a series of related hypotrochoid curves.
	
	http://en.wikipedia.org/wiki/Hypotrochoid
	
	Anthony Kozar
	October 7, 2017
	
 */

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;


@SuppressWarnings("serial")
public class HypotrochoidSeries extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 700;
	final private int	WINHEIGHT = 720;

	final private String HELP_MESSAGE = "Click to randomize or use -,+,[,],<,> to adjust the parameters, " +
            "R to redraw, ! to exit";

	final private int	MARGINSIZE = 5;
	protected Insets	drawingArea;				// visible area of window minus margins (right & bottom are coords not insets)
	
	protected double	centerx;
	protected double	centery;
	protected double	drawingradius;				// maximum distance from the center that we can draw
	protected double	outerradius;				// radius of the outer circle
	protected double	innerradius;				// radius of the inner circle
	protected int		numlobes;					// the numerator in the ratio outerradius/innerradius
	protected int		numrevolutions;				// the denominator in the ratio outerradius/innerradius
	protected int		numtrochoids;				// number of trochoids in the series
	protected double	dlobes;						// floating-point copy of numlobes
	protected double	drevolutions;				// floating-point copy of numrevolutions
	protected double	ratioangles;				// the ratio outerradius/innerradius
	protected double	ratioradii;					// the ratio innerradius/outerradius
	protected double	penratio;					// the ratio penlength/innerradius
	protected double	penratiooffset;				// offset of penratio for each extra trochoid
	protected double	penlength;					// distance from the center of inner circle to the "pen"
	protected double	penlenoffset;				// offset of penlength for each extra trochoid
	protected double	rotationoffset;				// amount to rotate each extra trochoid
	
	protected int		iprimaryhue;
	protected int		isecondaryhue;
	protected float		primaryhue;
	protected float		secondaryhue;

	private int			lastx, lasty;				// remember the last point drawn
	
	public HypotrochoidSeries()
	{
		super("Hypotrochoid Series");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);

		// find the center of the window and a radius that will fit
		centerx = WINWIDTH * 0.5;
		centery = WINHEIGHT * 0.5;
		drawingradius = (Math.min(WINWIDTH, WINHEIGHT) * 0.25) - 30.0;
	
		SetDrawingParms(15, 8, 6, 0.85, 0.1, -10.0);
		SetColorParms(6, 5);
	}
	
	private void SetColorParms(int primary, int secondary) {
		iprimaryhue = primary;
		primaryhue = 0.1f * iprimaryhue;
		isecondaryhue = secondary;
		secondaryhue = 0.1f * isecondaryhue;
	}
	
	private void SetDrawingParms(int lobes, int revolutions, int trochoids, double penpos, double penoff, double rotoff)
	{
		// calculate hypotrochoid parameters
		numtrochoids = trochoids;
		numlobes = lobes;
		numrevolutions = revolutions;
		dlobes = (double)lobes;
		drevolutions = (double)revolutions;
		penratio = penpos;
		penratiooffset = penoff;
		rotationoffset = rotoff;
		
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
		penlenoffset = innerradius * penratiooffset;
	}
	
	private void RandomizeParms()
	{
		int curves, lobes, revs, primary, secondary;
		double penpos, penoffset, rotoffset, closeness;
		
		do {
			lobes = RandomOn(1, 50);
			revs = RandomOn(1, 40);
			// Try to avoid certain uninteresting combinations:
			// if lobes and revs are within 15% of each other, select new values.
			closeness = Math.abs(((double)lobes / (double)revs) - 1.0);
		}
		while(closeness < 0.15);
		
		curves = RandomOn(2,40);
		penpos = RandomOn(15, 40) * 0.05;
		penoffset = RandomOn(5, 100) * 0.001;
		rotoffset = RandomOn(-89, 90) * 2.0;
		SetDrawingParms(lobes, revs, curves, penpos, penoffset, rotoffset);
		
		// set the primary & secondary color hues
		primary = RandomOn(0,19);
		secondary = RandomOn(0,10);
		SetColorParms(primary, secondary);
	}
	
	protected int RandomOn(int low, int high)
	{
		return (low + ( (int)((high-low+1) * Math.random() )));
	}

	protected Color getRandomHSBColor(float	saturation, float brightness)
	{
		float	hue;
		
		hue = (float)Math.random();
		return Color.getHSBColor(hue, saturation, brightness);
	}

	protected void setMargins()
	{
		// get the size of our visible drawing area
		// and define our own margins within that
		// (NOTE: we set right & bottom of drawingArea as coordinates, not insets)
		Insets visibleArea = this.getInsets();
		drawingArea = new Insets(visibleArea.top  + MARGINSIZE, 
				                 visibleArea.left + MARGINSIZE, 
				                 this.getHeight() - (visibleArea.bottom + MARGINSIZE),
				                 this.getWidth()  - (visibleArea.right  + MARGINSIZE));
	}

	protected void drawPoint(Graphics g, double x, double y)
	{
		int ix = (int)Math.round(x);
		int iy = (int)Math.round(y);
		
		// skip consecutive duplicate points
		if (ix != lastx || iy != lasty)	{
			// we have to use drawLine() to draw a single point
			g.drawLine(ix, iy, ix, iy);
			lastx = ix;
			lasty = iy;
		}
	}
	
	protected void drawWindowText(Graphics g)
	{
		String parmsMessage1;
		
		// calculate strings with parameter values
		parmsMessage1 = "Curves: " + numtrochoids;
		parmsMessage1 = parmsMessage1 + "  Circle ratios: " + numlobes + "/" + numrevolutions;
		parmsMessage1 = parmsMessage1 + "  Pen position: "  + String.format("%.2f", penratio);
		parmsMessage1 = parmsMessage1 + "  Pen offset: "    + String.format("%.3f", penratiooffset);
		parmsMessage1 = parmsMessage1 + "  Rotation incr: " + String.format("%.1f", rotationoffset);
		parmsMessage1 = parmsMessage1 + "  Spectrum: " + iprimaryhue + "->" + isecondaryhue;
		
		// draw strings with parameter values, highlighting the selected parameter
		FontMetrics  fm = g.getFontMetrics();
		int lineht = fm.getHeight();
		int x = 10;
		
		setMargins();
		g.setColor(Color.black);
		g.drawString(parmsMessage1, x, drawingArea.top + lineht);
		// x += fm.stringWidth(parmsMessage1);
		
		// draw keyboard help message
		g.drawString(HELP_MESSAGE, 10, drawingArea.bottom);
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

		drawWindowText(g);
		
		// draw multiple hypotrochoids
		lenbtwcenters = outerradius - innerradius;				// distance between circle centers
		final float hueincr = (1.0f/numtrochoids) * (secondaryhue-primaryhue);	// FIXME: never reaches 2ndhue?
		for (int i = 0; i < numtrochoids; i++) {
			// Calculate the parameters for this trochoid:
			// The first trochoid is drawn with plen=penlength, no rotation, and in the primary hue.
			// Subsequent trochoids each reduce penlength by the offset, increase the rotation, and
			// shift the color towards the secondary hue.
			double plen = penlength - (penlenoffset * i);
			double angleoffset = i * rotationoffset * Math.PI / 360.0;
			float hue = primaryhue + i * hueincr;
			g.setColor(Color.getHSBColor(hue, 1.0f, 0.75f));
			
			// calculate x and y parametrically based on the total angle of rotation
			for (double angle = arcstart; angle <= arcend; angle += angleincr) {
				innerangle = angle - (angle * ratioangles);			// angle of the pen relative to a horizontal line
				// calculate coordinates of point relative to the origin
				x1 = lenbtwcenters * Math.cos(angle+angleoffset) + plen * Math.cos(innerangle);
				y1 = lenbtwcenters * Math.sin(angle+angleoffset) + plen * Math.sin(innerangle);
				// calculate coordinates relative to our "drawing origin"
				x = centerx + x1;
				y = centery - y1;
				drawPoint(g, x, y);
			}		
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
			SetDrawingParms(numlobes-1, numrevolutions, numtrochoids, penratio, penratiooffset, rotationoffset);
			this.repaint();
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the number of lobes by one and repaints the curve
			SetDrawingParms(numlobes+1, numrevolutions, numtrochoids, penratio, penratiooffset, rotationoffset);
			this.repaint();
		}
		else if	(key == '[' || key == '{') {
			// '[' (or '{') decreases the number of revolutions it takes to draw the curve (and repaints)
			// Don't allow the value to go below 1.
			if (numrevolutions > 1) {
				SetDrawingParms(numlobes, numrevolutions-1, numtrochoids, penratio, penratiooffset, rotationoffset);
				this.repaint();
			}
		}
		else if	(key == ']' || key == '}') {
			// ']' (or '}') increases the number of revolutions it takes to draw the curve (and repaints)
			SetDrawingParms(numlobes, numrevolutions+1, numtrochoids, penratio, penratiooffset, rotationoffset);
			this.repaint();
		}
		else if	(key == ';') {
			// ';' decreases the number of trochoids in the series (and repaints)
			// Don't allow the value to go below 1.
			if (numtrochoids > 1) {
				SetDrawingParms(numlobes, numrevolutions, numtrochoids-1, penratio, penratiooffset, rotationoffset);
				this.repaint();
			}
		}
		else if	(key == '\'') {
			// ' increases the number of trochoids in the series (and repaints)
			SetDrawingParms(numlobes, numrevolutions, numtrochoids+1, penratio, penratiooffset, rotationoffset);
			this.repaint();
		}
		else if	(key == ':') {
			// ':' decreases the rotation offset between each trochoid in the series (and repaints)
			SetDrawingParms(numlobes, numrevolutions, numtrochoids, penratio, penratiooffset, rotationoffset-2.0);
			this.repaint();
		}
		else if	(key == '\"') {
			// " increases the rotation offset between each trochoid in the series (and repaints)
			SetDrawingParms(numlobes, numrevolutions, numtrochoids, penratio, penratiooffset, rotationoffset+2.0);
			this.repaint();
		}
		else if	(key == ',') {
			// ',' decreases the distance between the inner circle's center and the pen (and repaints)
			SetDrawingParms(numlobes, numrevolutions, numtrochoids, penratio-0.05, penratiooffset, rotationoffset);
			this.repaint();
		}
		else if	(key == '.') {
			// '.' increases the distance between the inner circle's center and the pen (and repaints)
			SetDrawingParms(numlobes, numrevolutions, numtrochoids, penratio+0.05, penratiooffset, rotationoffset);
			this.repaint();
		}
		else if	(key == '<') {
			// '<' decreases the distance between each trochoid in the series (and repaints)
			SetDrawingParms(numlobes, numrevolutions, numtrochoids, penratio, penratiooffset-0.005, rotationoffset);
			this.repaint();
		}
		else if	(key == '>') {
			// '>' increases the distance between each trochoid in the series (and repaints)
			SetDrawingParms(numlobes, numrevolutions, numtrochoids, penratio, penratiooffset+0.005, rotationoffset);
			this.repaint();
		}
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' cause the same curve to be redrawn
			// RandomizeParms();
			this.repaint();
		}
		else if	(key == '&') {
			// '&' decrements the primary color hue (don't allow the value to go below 0).
			if (iprimaryhue > 0) {
				SetColorParms(--iprimaryhue, isecondaryhue);
				this.repaint();
			}
		}
		else if	(key == '*') {
			// '*' increments the primary color hue
			SetColorParms(++iprimaryhue, isecondaryhue);
			this.repaint();
		}
		else if	(key == '(') {
			// '(' decrements the secondary color hue (don't allow the value to go below 0).
			if (isecondaryhue > 0) {
				SetColorParms(iprimaryhue, --isecondaryhue);
				this.repaint();
			}
		}
		else if	(key == ')') {
			// '(' increments the secondary color hue
			SetColorParms(iprimaryhue, ++isecondaryhue);
			this.repaint();
		}
		else if	(Character.isDigit(key)) {
			// set the primary & secondary color hues: assign 2nd hue to 1st hue & new value to 2nd hue
			// (takes two key presses to set both to new values)
			int value = Integer.parseInt("" + key);
			SetColorParms(isecondaryhue, value);
			this.repaint();
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
		HypotrochoidSeries app = new HypotrochoidSeries();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

	}

}
