/*	IncrementalCircle2.java

	An improvement on class IncrementalCircle that uses a controlled frame-rate
	to animate the drawing of a circle with an offscreen buffer.
	
	Anthony Kozar
	October 29, 2015

*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class IncrementalCircle2 extends JFrame implements Runnable, MouseListener, KeyListener
{
	final static private int	WINWIDTH = 800;				// preferred window size
	final static private int	WINHEIGHT = 800;
	final static private int	MARGINSIZE = 5;
	final static private int	DEFAULT_FRAME_RATE = 30;	// frames per second
	final static private double	DEFAULT_DURATION = 3.0;		// total animation duration in seconds
	
	final static private String HELP_MESSAGE = "Press R to redraw, ! to exit";
	
	private Thread 		animationtask;
	
	protected double	centerx;
	protected double	centery;
	protected double	drawingradius;	// maximum distance from the center that we can draw
	protected Rectangle	drawingArea;	// visible area of window minus margins
	protected Color		currentcolor;
	protected Image		currentframe;
	protected Image		nextframe;
	protected int		totalframes;
	protected int		framecount = 0;
	protected int		framerate;
	protected double	totalduration;		// in seconds
	protected long		framedurationms;	// in milliseconds
	
	public IncrementalCircle2()
	{
		super("A randomly-colored circle");
	
		int maxwidth, maxheight;
		
		Rectangle usableSpace = GetAvailableWindowSpace();
		maxwidth = (int)usableSpace.getWidth();
		maxheight = (int)usableSpace.getHeight();
		if (WINWIDTH > maxwidth || WINHEIGHT > maxheight) {
			setSize(maxwidth, maxheight);
		}
		else setSize(WINWIDTH, WINHEIGHT);
		setLocation(usableSpace.getLocation());
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		        WindowResized();
		    }
		});
	
		SetMargins();
		SetOrigin();
		SetScale();
		SetRandomColor();
		CreateOffscreenBuffers();
		CalculateFrameParameters(DEFAULT_FRAME_RATE, DEFAULT_DURATION);
		RestartAnimation();
	}
	
	private void RestartAnimation()
	{
		// FIXME: make sure there is no previous animationtask still running
		framecount = 0;
		ClearOffscreenBuffer(nextframe);
		animationtask = new Thread(this, "Animation Loop");
		animationtask.start();
	}

	public void run()
	{
		long starttime, endtime;	// in nanoseconds
		long timeremaining;			// time (in milliseconds) remaining until the next frame
		Graphics2D	nextframegc, windowgc;
		
		windowgc = (Graphics2D)this.getGraphics();
		// will need to refresh nextframegc in the loop or in showNextFrame()
		// if we ever switch to a buffer swapping scheme
		nextframegc = (Graphics2D)nextframe.getGraphics();
		
		/* main animation loop */
		do {
			starttime = System.nanoTime();
			SetRandomColor();
			renderNextFrame(nextframegc);
			endtime = System.nanoTime();
			timeremaining = framedurationms - ((endtime-starttime)/1000000);
			System.out.printf("Frame: %d  Time remaining: %dms\n", framecount, timeremaining);
			if (timeremaining > 0) {
				try {
					Thread.sleep(timeremaining);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			showNextFrame(windowgc);
		}
		while (++framecount < totalframes);
	}
	
	private Rectangle GetAvailableWindowSpace()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
	}
	
	protected void SetOrigin()
	{
		if (drawingArea == null)  SetMargins();
	
		// use the center of the drawing area as the origin point for drawing
		centerx = drawingArea.getCenterX();
		centery = drawingArea.getCenterY();
	}
	
	protected void SetScale()
	{
		if (drawingArea == null)  SetMargins();
	
		// find a drawing radius that will fit
		drawingradius = (Math.min(drawingArea.width, drawingArea.height) * 0.50) - 30.0;		
	}
	
	protected void SetMargins()
	{
		int top, left, width, height;
		
		// get the size of our visible drawing area
		// and define our own margins within that
		Insets visibleArea = this.getInsets();
		top = visibleArea.top  + MARGINSIZE;
		left = visibleArea.left + MARGINSIZE;
		width = this.getWidth() - (left + visibleArea.right  + MARGINSIZE);
		height = this.getHeight() - (top + visibleArea.bottom + MARGINSIZE);
		drawingArea = new Rectangle(left, top, width, height);
	}
	
	protected void SetRandomColor()
	{
		currentcolor = getRandomHSBColor(1.0f, 0.75f);
	}
	
	protected void CreateOffscreenBuffers()
	{
		if (drawingArea == null)  SetMargins();
		
		currentframe = this.createImage(drawingArea.width, drawingArea.height);
		if (currentframe == null) {
			throw new NullPointerException("Could not create currentframe buffer!");
		}
		ClearOffscreenBuffer(currentframe);

		nextframe = this.createImage(drawingArea.width, drawingArea.height);
		if (nextframe == null) {
			throw new NullPointerException("Could not create nextframe buffer!");
		}
		ClearOffscreenBuffer(nextframe);
	}
	
	protected void ClearOffscreenBuffer(Image buffer)
	{
		if (drawingArea == null)  SetMargins();
		if (buffer == null) {
			throw new NullPointerException("ClearOffscreenBuffer(): buffer is null!");
		}
		else {
			// clear the buffer with background color
			Graphics buffergc = buffer.getGraphics();
			buffergc.setColor(Color.white);
			buffergc.fillRect(0, 0, drawingArea.width, drawingArea.height);
			buffergc.dispose();
		}
	}
	
	public void CalculateFrameParameters(int framesPerSec, double duration)
	{
		framerate = framesPerSec;
		totalduration = duration;
		totalframes = (int)(framesPerSec * duration);
		framedurationms = (long)(1000.0 * totalduration / totalframes);
	}

	public void WindowResized()
	{
		// System.out.println("WindowResized() called");
		SetMargins();
		SetOrigin();
		SetScale();
		CreateOffscreenBuffers();
		RestartAnimation();
	}
	
	protected void drawWindowText(Graphics g)
	{
		String message1 = "Hello World!";
		
		// draw strings with parameter values, highlighting the selected parameter
		FontMetrics  fm = g.getFontMetrics();
		int lineht = fm.getHeight();
		int x = 10;
		
		// draw some message at the top
		g.setColor(Color.black);
		//g.drawString(message1, x, drawingArea.y + lineht);
		
		// draw keyboard help message at the bottom
		g.drawString(HELP_MESSAGE, 10, (int)drawingArea.getMaxY());		
	}
	
	protected void drawPoint(Graphics g, double x, double y)
	{
		// we have to use drawLine() to draw a single point
		g.drawLine((int)Math.round(x), (int)Math.round(y), 
				   (int)Math.round(x), (int)Math.round(y));
	}
	
	protected Color getRandomHSBColor(float	saturation, float brightness)
	{
		float	hue;
		
		hue = (float)Math.random();
		return Color.getHSBColor(hue, saturation, brightness);
	}
	
	public void renderNextFrame(Graphics2D g)
	{
		final double arclen = 2.0*Math.PI/totalframes;	// arclength of arc (in radians)
		final double arcstart = framecount*arclen;		// angle of beginning of arc
		final double arcend = arcstart + arclen;		// angle of end of arc
		final double angleincr = Math.PI/5000.0;		// increment at which to draw points
		double	x, y;

		// draw one segment of the circle into buffer
		g.setColor(currentcolor);
		// draw points along a circle/arc from arcstart to arcend radians
		for (double angle = arcstart; angle <= arcend; angle += angleincr) {
			x = centerx + drawingradius * Math.cos(angle);
			y = centery - drawingradius * Math.sin(angle);
			drawPoint(g, x, y);
		}
	}

	/*	showNextFrame()
		
		Copies the contents of the nextframe buffer to the screen and
		makes it the current frame.  This class uses an incremental
		drawing scheme that only draws the new pixels for each frame, 
		therefore relying on nextframe not to be cleared in between 
		frames. So, nextframe is copied to currentframe too.
		
		If non-incremental drawing were used, with each frame being 
		rendered in its entirety, then this method could just swap
		nextframe and currentframe instead of copying between them.
		
		(The purpose of saving the current frame is in case the paint()
		method is called and the next frame is not ready.  Is this really
		necessary?)
	 */
	public void showNextFrame(Graphics2D g)
	{
		// if (drawingArea == null)  SetMargins();
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	
		drawWindowText(g);
		
		// copy next frame buffer to the window
		g.drawImage(nextframe, 0, 0, null);
		
		// copy nextframe to currentframe
		Graphics currentframegc = currentframe.getGraphics();
		currentframegc.drawImage(nextframe, 0, 0, null);
		currentframegc.dispose();
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		System.out.println("paint() called");
		// if (drawingArea == null)  SetMargins();
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	
		drawWindowText(g);
		
		// copy current frame buffer to the window
		g.drawImage(currentframe, 0, 0, null);
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
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' cause the circle to be redrawn with a new random color
			RestartAnimation();
		}
		else if	(Character.isDigit(key) && key !='0' && key !='1') {
			// number keys don't do anything
		}
		
		return;
	}
	
	/* These 5 methods are the implementation of the MouseListener interface.
	   mouseClicked() causes a new random curve to be drawn.
	 */
	public void mouseClicked(MouseEvent event)	{}
	public void mousePressed(MouseEvent event)	{}
	public void mouseReleased(MouseEvent event)	{}
	public void mouseEntered(MouseEvent event)	{}
	public void mouseExited(MouseEvent event)	{}
	
	public static void main(String[] args)
	{
		IncrementalCircle2 app = new IncrementalCircle2();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
