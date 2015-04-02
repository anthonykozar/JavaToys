/*	LissajousTrochoid.java

	This class draws polytrochoids as roulettes of a base Lissajous curve.
	
	TODO rewrite the rest of this description ...
	a generalization of the parametric equations
	for a circle such that the X and Y parameters are related like members of a 
	harmonic series.
	
	x = sin(at + p)
	y = sin(bt)
	
	where p is the phase difference between the sinusoids.
	
	
	Anthony Kozar
	April 1, 2015

*/

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

import javax.swing.*;


public class LissajousTrochoid extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 1600;
	final private int	WINHEIGHT = 1000;
	final public int	MAXCIRCLES = 10;
	final public int	POINTSPERSEC = 100000;
	final public int	MAXDENSITY = 10000;

	// parameters of the polytrochoid that is displayed when program first runs
	final private int	 initcircles = 3;
	final private int[]	 initradii = {1, 27, 9};
	final private double initpenpos = 1.0;
	// final private int	 initcircles = 4;
	// final private int[]	 initradii = {43, 24, 8, 2};
	// final private double initpenpos = 0.0;
	
	final private String HELP_MESSAGE = "Click to randomize or use the arrow keys,<,>,[,],-,+ to adjust the parameters, " +
            "A/Q to toggle auto-set, R to redraw, ! to exit";

	final private int	MARGINSIZE = 5;
	protected Insets	drawingArea;				// visible area of window minus margins (right & bottom are coords not insets)
	
	final private int	P_NUM_CIRCLES = -1;			// the numcircles parameter
	protected int		selectedParm;				// the parameter currently selected for editing
	protected boolean	autoSetRevolutions;
	protected boolean	autoSetDensity;
	
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
	protected int		pointdensity;				// how many points to draw per revolution

	protected int		xfrequency;					// frequency of the X oscillator (i.e. "a" in x = sin(at+p))
	protected int		yfrequency;					// frequency of the Y oscillator (i.e. "b" in y = sin(bt))
	protected int		xphaseshift;				// phaseshift in 1/8ths of PI (i.e. "p" in x = sin(at+pπ/8))

	// point diagnostics
	private int			lastx, lasty, totalpoints, duplicatepoints;
	private boolean		showdiagnostics = false, skipduplicates = true;
	
	public LissajousTrochoid()
	{
		super("Lissajous curves");
		// setSize(WINWIDTH, WINHEIGHT);
		Rectangle usableSpace = GetAvailableWindowSpace();
		setSize((int)usableSpace.getWidth(), (int)usableSpace.getHeight());
		setLocation(usableSpace.getLocation());
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		        WindowResized();
		    }
		});
		
		// allocate space for the circle parameters
		iradii = new int[MAXCIRCLES];
		dradii = new double[MAXCIRCLES];
		pixradii = new double[MAXCIRCLES];
		angleratios = new double[MAXCIRCLES];
		radiiratios = new double[MAXCIRCLES];
		radiidiffs = new double[MAXCIRCLES];
		
		// initialize circle parameters
		for (int i = 0; i < MAXCIRCLES; i++)  iradii[i] = 1;
		autoSetRevolutions = true;
		autoSetDensity = true;
		SetOrigin();
		SetScale();
		SetDrawingParms(initcircles, initradii, initpenpos);
		selectedParm = P_NUM_CIRCLES;
		
		// initialize Lissajous parameters
		xfrequency = 1;
		yfrequency = 2;
		xphaseshift = 0;
		autoSetRevolutions = false;
		autoSetDensity = false;
		revolutions = 1.0;
		pointdensity = 10000;
	}
	
	private Rectangle GetAvailableWindowSpace()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	}
	
	private void SetOrigin()
	{
		// use the center of the window as the origin point for drawing
		centerx = this.getWidth()  * 0.5;
		centery = this.getHeight() * 0.5;		
	}

	private void SetScale()
	{
		// find a drawing radius that will fit
		drawingradius = (Math.min(this.getWidth(), this.getHeight()) * 0.25) - 30.0;		
	}
	
	private void SetDensity()
	{
		// try to set the point density to draw solid figures without taking too long to draw
		// (assumes an average drawing speed of 100,000 points/sec on my machine)
		
		/*
		int maxpoints = POINTSPERSEC >> 1;				// try to limit time to half a second
		int numpoints = (int)revolutions * MAXDENSITY;
		
		if (numpoints > maxpoints)  pointdensity = (int)(maxpoints/revolutions);
		else  pointdensity = MAXDENSITY;
		*/
		
		pointdensity = (int)(POINTSPERSEC/revolutions);
		if (pointdensity > MAXDENSITY)  pointdensity = MAXDENSITY;
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
		
		if (autoSetRevolutions)  revolutions = (double)CalculateRevolutions(numcircles, iradii);
		if (autoSetDensity)  SetDensity();
		SetPenLength(penposition);
	}
	
	/* Calculate the number of revolutions needed to produce a closed curve */
	private int CalculateRevolutions(int circles, int[] origradii)
	{
		int gcd, revs, result;
		int[] radii;
		
		// validate arguments
		if (circles < 2) {
			System.err.println("Error in CalculateRevolutions(): circles cannot be less than 2 (was " + circles + ")");
			return 0;
		}
		if (origradii.length < circles) {
			System.err.println("Error in CalculateRevolutions(): fewer than " + circles + 
					           " values in origradii[] (has " + origradii.length + ")");
			return 0;			
		}
		
		// reduce all radii by any common factors
		radii = (int[]) origradii.clone();
		gcd = GCD(radii[0], radii[1]);
		for (int i = 2; i < circles; i++) {
			gcd = GCD(gcd, radii[i]);
		}
		if (gcd > 1) {
			for (int i = 0; i < circles; i++) {
				radii[i] /= gcd;
			}
		}
		// System.out.println(Arrays.toString(origradii) + "  GCD(radii[]) = " + gcd + "  " + Arrays.toString(radii));
		
		// FIXME: this algorithm is currently only correct for 2-3 wheels (?)
		// the 2-wheel solution is just radii[1] reduced by any common factors 
		// that it shares with GCD(radii[0], radii[1]) which is already done
		// if circles == 2
		if (circles == 2) result = radii[1];
		else {
			// check for degenerate cases
			if (radii[0] == radii[1])  result = 1;			// just a single point
			else if (radii[1] == radii[2])  result = 1;		// a circle
			else {
				// start with what would be the num revs if only 2 wheels
				gcd = GCD(radii[0], radii[1]);
				result = radii[1] / gcd;
				// reduce radii[2] by any common factors that it shares with radii[1]
				gcd = GCD(radii[1], radii[2]);
				revs = radii[2] / gcd;
				// reduce radii[2] further by any common factors that it shares with radii[1]-radii[0]
				gcd = GCD(Math.abs(radii[1]-radii[0]), revs);
				revs = revs / gcd;
				// multiply 2-wheel result by the 3rd wheel factor
				result *= revs;
			}
		}
		
		return result;
	}
	
	private void SetPenLength(double penposition)
	{
		// penposition is a scalar for the innermost circle's radius
		penratio = penposition;
		penlength = radiidiffs[numcircles-1] = pixradii[numcircles-1] * penratio;
	}
	
	private void RandomizeParms()
	{
		int circles;
		int[] radii;
		double penpos;
		boolean tryagain = false;
		
		circles = numcircles; //RandomOn(3, 4); // MAXCIRCLES);
		radii = new int[circles];
		do {
			for (int i = 0; i < circles; i++) {
				radii[i] = RandomOn(1, 40);
			}
			// Try to avoid certain uninteresting or slow combinations:
			if (radii[0] == radii[1] || (circles > 2 && radii[1] == radii[2]) || CalculateRevolutions(circles, radii) > 100)
				  tryagain = true;
			else  tryagain = false;
		}
		while(tryagain);
		
		penpos = RandomOn(3, 40) * 0.05;
		SetDrawingParms(circles, radii, penpos);
	}
	
	private void IncrSelectedParm()
	{
		// increment the currently selected parameter
		if (selectedParm == P_NUM_CIRCLES) {
			if (numcircles < MAXCIRCLES)
				++numcircles;
		}
		else if (selectedParm >= 0 && selectedParm < numcircles) {
			// selected parameter is one of the circle radii (0 to numcircles-1)
			++iradii[selectedParm];
		}
		SetDrawingParms(numcircles, iradii, penratio);
	}
	
	private void DecrSelectedParm()
	{
		// decrement the currently selected parameter
		if (selectedParm == P_NUM_CIRCLES) {
			if (numcircles > 1)
				--numcircles;
		}
		else if (selectedParm >= 0 && selectedParm < numcircles) {
			// selected parameter is one of the circle radii (0 to numcircles-1)
			if (iradii[selectedParm] > 1)
				--iradii[selectedParm];
		}
		SetDrawingParms(numcircles, iradii, penratio);
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
	
	protected int RandomOn(int low, int high)
	{
		return (low + ( (int)((high-low+1) * Math.random() )));
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

	private void resetDiagnostics()
	{
		totalpoints = duplicatepoints = 0;
		lastx = lasty = -1;
	}
	
	private void drawDiagnostics(Graphics g, long nscalctime, long nsdrawtime, int numpoints, int numduplicates)
	{
		String	calctimestr, drawtimestr, pointstr, dupstr, ptssecstr;
		double	calctime, drawtime;
		
		calctime = nscalctime * 0.000000001;				// convert from nanoseconds to seconds
		drawtime = nsdrawtime * 0.000000001;
		calctimestr = String.format("Calc time: %.3f", calctime);
		drawtimestr = String.format("Draw time: %.3f", drawtime);
		pointstr = "Points: " + numpoints;
		dupstr = "Duplicates: " + numduplicates;
		ptssecstr = String.format("Points/sec: %.3f", (numpoints-numduplicates)/drawtime);
		
		// measure string widths to determine how much space is needed
		FontMetrics  fm = g.getFontMetrics();
		int lineht = fm.getHeight();
		int x = 100, width;
		
		width = fm.stringWidth(calctimestr);
		if (width > x)  x = width;
		width = fm.stringWidth(drawtimestr);
		if (width > x)  x = width;
		width = fm.stringWidth(pointstr);
		if (width > x)  x = width;
		width = fm.stringWidth(dupstr);
		if (width > x)  x = width;
		width = fm.stringWidth(ptssecstr);
		if (width > x)  x = width;
		
		// draw diagnostic strings in the upper right corner
		setMargins();
		x = drawingArea.right - x;
		g.setColor(Color.black);
		g.drawString(calctimestr,  x, drawingArea.top + lineht);
		g.drawString(drawtimestr,  x, drawingArea.top + 2*lineht);
		g.drawString(pointstr,     x, drawingArea.top + 3*lineht);
		g.drawString(dupstr,       x, drawingArea.top + 4*lineht);
		if (skipduplicates)  g.drawString("(skipped)", x, drawingArea.top + 5*lineht);
		g.drawString(ptssecstr,    x, drawingArea.top + 6*lineht);
	}
	
	protected void drawPoint(Graphics g, double x, double y)
	{
		int ix = (int)Math.round(x);
		int iy = (int)Math.round(y);
		
		++totalpoints;
		if (ix == lastx && iy == lasty)	{
			++duplicatepoints;
			if (!skipduplicates) {
				// we have to use drawLine() to draw a single point
				g.drawLine(ix, iy, ix, iy);				
			}
		}
		else {
			// we have to use drawLine() to draw a single point
			g.drawLine(ix, iy, ix, iy);
			lastx = ix;
			lasty = iy;
		}
	}
	
	protected void drawWindowText(Graphics g)
	{
		String parmsMessage1, selectedValue, parmsMessage2, workstr;
		
		/* POLYTROCHOID 
		// calculate strings with parameter values
		parmsMessage1 = selectedValue = "";
		workstr = "Circles: ";
		if (selectedParm == P_NUM_CIRCLES) {
			parmsMessage1 = workstr;
			selectedValue = "" + numcircles;
			workstr = "  Circle ratios: ";
		}
		else {
			workstr = workstr + numcircles + "  Circle ratios: "; 
		}
		
		for (int i = 0; i < numcircles; i++)   {
			if (selectedParm == i) {
				parmsMessage1 = workstr;
				selectedValue = "" + iradii[i];
				workstr = "";
			}
			else {
				workstr = workstr + iradii[i];
			}
			if (i < numcircles-1)  workstr = workstr + "/";			
		}
		parmsMessage2 = workstr + "  Pen position: " + String.format("%.2f", penratio) + 
						" <>  Revolutions: " + (int)revolutions + " [ ]";
		*/

		parmsMessage1 = "x = sin(" + xfrequency + "t + " + xphaseshift + "π/8)";
		parmsMessage1 = parmsMessage1 + "  ;  y = sin(" + yfrequency + "t)";
		
		parmsMessage2 = "  Revolutions: " + (int)revolutions + " [ ]";
		parmsMessage2 = parmsMessage2 + " (auto-set " + (autoSetRevolutions ? "on": "off") + " (A))";
		parmsMessage2 = parmsMessage2 + "  Point density: " + pointdensity + " -/+";
		parmsMessage2 = parmsMessage2 + " (auto-set " + (autoSetDensity ? "on": "off") + " (Q))";
		
		// draw strings with parameter values, highlighting the selected parameter
		FontMetrics  fm = g.getFontMetrics();
		int lineht = fm.getHeight();
		int x = 10;
		
		setMargins();
		g.setColor(Color.black);
		g.drawString(parmsMessage1, x, drawingArea.top + lineht); // drawingArea.bottom - 24);
		x += fm.stringWidth(parmsMessage1);
		//g.setColor(Color.red);
		//g.drawString(selectedValue, x, drawingArea.top + lineht); // drawingArea.bottom - 24);
		//x += fm.stringWidth(selectedValue);
		g.setColor(Color.black);
		g.drawString(parmsMessage2, x, drawingArea.top + lineht); // drawingArea.bottom - 24);
		
		// draw keyboard help message
		g.drawString(HELP_MESSAGE, 10, drawingArea.bottom);
		
	}
	
	public void paint(Graphics g)
	{
		final double arcstart = 0.0;							// angle of beginning of arc
		final double arcend = revolutions * 2.0 * Math.PI;		// angle of end of arc
		final double angleincr = 2.0 * Math.PI/pointdensity;	// increment at which to draw points
		double	x, y, innerangle, lastangle, phaseshift;

		super.paint(g);
		// System.out.println("paint() called");
		
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		drawWindowText(g);
		g.setColor(Color.blue);
		resetDiagnostics();
		int pass = (showdiagnostics ? 0 : 1);
		long drawingtime = 0, calctime = 0;
		do {
		long start = System.nanoTime();		   
		
		phaseshift = 0.125 * xphaseshift * Math.PI;
		// calculate coordinates parametrically based on the total angle of rotation
		for (double angle = arcstart; angle <= arcend; angle += angleincr) {
			// Calculate coordinates along the Lissajous curve relative to the origin
			// which will be used as the center of the first inner circle.
			// (multiply by radiidiffs[0] instead of drawingradius ?)
			x = drawingradius * Math.sin(xfrequency*angle + phaseshift);
			y = drawingradius * Math.sin(yfrequency*angle);
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
			if (pass == 1) drawPoint(g, x, y);
		}
		
		if (pass == 0)  calctime = System.nanoTime() - start;
		else  drawingtime = System.nanoTime() - start;
		}
		while (++pass < 2);
		
		if (showdiagnostics)  drawDiagnostics(g, calctime, drawingtime, totalpoints, duplicatepoints);
	}

	public void WindowResized()
	{
		// System.out.println("WindowResized() called");
		SetOrigin();
		SetScale();
		SetDrawingParms(numcircles, iradii, penratio);
		this.repaint();
	}
	
	/* These 3 methods are the implementation of the KeyListener interface.
	   keyTyped() responds to keyboard events as described below.
	 */
	public void keyPressed(KeyEvent event)
	{
		int		key = event.getKeyCode();
		// System.out.println("keyPressed event: " + key);
		
		if	(key == KeyEvent.VK_RIGHT) {
			// Right arrow key increases the X oscillator frequency
			// System.out.println("Received tab or right arrow");
			++xfrequency;
			this.repaint();
		}
		else if	(key == KeyEvent.VK_LEFT) {
			// Left arrow key decreases the X oscillator frequency
			// System.out.println("Received left arrow");
			--xfrequency;
			this.repaint();
		}
		else if	(key == KeyEvent.VK_UP) {
			// Up arrow key increases the Y oscillator frequency
			// System.out.println("Received up arrow");
			++yfrequency;
			this.repaint();
		}
		else if	(key == KeyEvent.VK_DOWN) {
			// Down arrow key decreases the Y oscillator frequency
			// System.out.println("Received down arrow");
			--yfrequency;
			this.repaint();
		}

		/* POLYTROCHOID 
		if	(key == KeyEvent.VK_TAB || key == KeyEvent.VK_RIGHT) {
			// Tab and right arrow keys select the next parameter for editing
			// System.out.println("Received tab or right arrow");
			++selectedParm;
			if (selectedParm >= numcircles) selectedParm = P_NUM_CIRCLES;
			this.repaint();
		}
		else if	(key == KeyEvent.VK_LEFT) {
			// Left arrow key selects the previous parameter for editing
			// System.out.println("Received left arrow");
			--selectedParm;
			if (selectedParm < P_NUM_CIRCLES) selectedParm = numcircles - 1;
			this.repaint();
		}
		else if	(key == KeyEvent.VK_UP) {
			// Up arrow key increases the selected parameter by one
			// System.out.println("Received up arrow");
			IncrSelectedParm();
			this.repaint();
		}
		else if	(key == KeyEvent.VK_DOWN) {
			// Down arrow key decreases the selected parameter by one
			// System.out.println("Received down arrow");
			DecrSelectedParm();
			this.repaint();
		}
		*/
	}
	
	public void keyReleased(KeyEvent event)
	{
		int		key = event.getKeyCode();
		// System.out.println("keyReleased event: " + key);
	}
	
	public void keyTyped(KeyEvent event) 
	{
		char	key = event.getKeyChar();
		// System.out.println("keyTyped event: " + key);
		
		if	(key == '!') {
			// '!' exits the program
			System.exit(0);
		}
		else if	(key == '-') {
			// '-' decreases the point density and repaints the curve
			if (pointdensity > 100) {
				pointdensity -= 100;
				this.repaint();
			}
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the point density and repaints the curve
			pointdensity += 100;
			this.repaint();
		}
		else if	(key == '[' || key == '{') {
			// '[' (or '{') decreases the number of revolutions it takes to draw the curve (and repaints)
			// Don't allow the value to go below 1.
			if (revolutions > 1) {
				--revolutions;
				this.repaint();
			}
		}
		else if	(key == ']' || key == '}') {
			// ']' (or '}') increases the number of revolutions it takes to draw the curve (and repaints)
			++revolutions;
			this.repaint();
		}
		else if	(key == '<' || key == ',') {
			// '<' (or ',') decreases the distance between the inner circle's center and the pen (and repaints)
			SetPenLength(penratio-0.05);
			this.repaint();
		}
		else if	(key == '>' || key == '.') {
			// '>' (or '.') increases the distance between the inner circle's center and the pen (and repaints)
			SetPenLength(penratio+0.05);
			this.repaint();
		}
		else if	(key == 'o' || key == 'O') {
			// 'o' (or 'O') decreases the phaseshift of the X oscillator (and repaints)
			--xphaseshift;
			this.repaint();
		}
		else if	(key == 'p' || key == 'P') {
			// 'p' (or 'P') increases the phaseshift of the X oscillator (and repaints)
			++xphaseshift;
			this.repaint();
		}
		else if	(key == 'a' || key == 'A') {
			// 'a' and 'A' toggle the automatic setting of the num of revolutions
			autoSetRevolutions = !autoSetRevolutions;
			this.repaint();
		}
		else if	(key == 'd' || key == 'D') {
			// 'd' and 'D' toggle whether diagnostics are drawn onscreen
			showdiagnostics = !showdiagnostics;
			this.repaint();
		}
		else if	(key == 's' || key == 'S') {
			// 's' and 'S' toggle whether consecutive duplicate points are plotted again
			skipduplicates = !skipduplicates;
			this.repaint();
		}
		else if	(key == 'r' || key == 'R') {
			// 'r' and 'R' cause the same curve to be redrawn
			// RandomizeParms();
			this.repaint();
		}
		else if	(key == 'q' || key == 'Q') {
			// 'q' and 'Q' toggle the automatic setting of the point density
			autoSetDensity = !autoSetDensity;
			this.repaint();
		}
		else if	(Character.isDigit(key) && key !='0' && key !='1') {
			// number keys don't do anything
		}
		else if	(key =='\n') {
			// print out current parameters when 'Enter' is pressed
			System.out.print((int)(revolutions+0.5));
			for (int i = 0; i < numcircles; i++)   {
				System.out.print("\t");
				System.out.print(iradii[i]);
			}
			System.out.print("\n");
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
		LissajousTrochoid app = new LissajousTrochoid();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

	}

}

