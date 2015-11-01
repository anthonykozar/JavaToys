/*	AnimatedCircles.java

	An animation demo using double buffering and "page flipping"
	to animate one or more circles moving and changing sizes.
	
	Initially based on IncrementalCircle2.java.
	
	Anthony Kozar
	October 30, 2015

*/

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;


@SuppressWarnings("serial")
public class AnimatedCircles extends JFrame implements Runnable, MouseListener, KeyListener
{
	final static private int	WINWIDTH = 1600;				// preferred window size
	final static private int	WINHEIGHT = 1000;
	final static private int	MARGINSIZE = 5;
	final static private int	DEFAULT_FRAME_RATE = 30;	// frames per second
	final static private double	DEFAULT_DURATION = 3.0;		// total animation duration in seconds
	final static private int	NUM_CIRCLES = 10;			// initial number of circles
	
	final static private String HELP_MESSAGE = "Press A to add a circle, P to pause, R to restart, ! to exit";
	
	private Thread 		animationtask;
	private boolean		running = false;
	
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
	
	protected Vector<CircleAnimation>	circles;
	
	private class CircleAnimation
	{
		public double	centerx;
		public double	centery;
		public double	radius;
		public double	speed;					// pixels per second
		public double	direction;				// an angle (in radians) relative to postive horizontal axis
		public double	growth;					// the radius' rate of change per second (may be negative)
		public Color	color;
		
		protected Rectangle	boundingbox;		// bounding box of the circle itself
		protected Rectangle animationbounds;	// bounding box that the animation should stay within
		protected double    dx, dy, dr;			// change in position & radius per frame
		protected int		framerate;
		
		public CircleAnimation(Rectangle animationBounds, int framesPerSecond)
		{
			animationbounds = animationBounds;
			framerate = framesPerSecond;
			SetRandomParameters();
			CalculateBoundingBox();
		}
		
		private void SetRandomParameters()
		{
			centerx   = Math.random()*animationbounds.width + animationbounds.x;
			centery   = Math.random()*animationbounds.height + animationbounds.y;
			radius    = Math.random()*23.0 + Math.random()*23.0 + 4.0;	// 4.0 to 50.0 ?
			speed     = Math.random()*51.0;
			direction = Math.random()*2.0*Math.PI;
			growth    = Math.random()*5.0 + Math.random()*5.0 - 5.0;		// -5.0 to 5.0
			CalculateDeltas();
			color = getRandomHSBColor(1.0f, 0.75f);
		}
		
		private void CalculateDeltas()
		{
			double frameduration = 1.0 / framerate;
			dx = speed * Math.cos(direction) * frameduration;
			dy = - speed * Math.sin(direction) * frameduration;
			dr = growth * frameduration;
		}
		
		private void CalculateBoundingBox()
		{
			int diameter = (int)Math.round(2.0*radius);
			boundingbox = new Rectangle((int)Math.round(centerx - radius),
										(int)Math.round(centery - radius),
										diameter, diameter);
		}
		
		public void tick()
		{
			centerx += dx;
			centery += dy;
			radius  += dr;
			CalculateBoundingBox();
		}
		
		public void render(Graphics2D g)
		{
			g.setColor(color);
			g.drawOval(boundingbox.x, boundingbox.y, boundingbox.width, boundingbox.height);
		}
	}
	
	public AnimatedCircles()
	{
		super("Animated Circles");
	
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
		
		// create a number of animated circles
		circles = new Vector<CircleAnimation>(NUM_CIRCLES);
		for (int i = 0; i < NUM_CIRCLES; i++) {
			circles.add(new CircleAnimation(drawingArea, DEFAULT_FRAME_RATE));
		}
		
		RestartAnimation();
	}
	
	private void RestartAnimation()
	{
		// wait until any previous animationtask stops running
		if (animationtask != null) {
			while (animationtask.isAlive()) {
				running = false;
			}
		}
		if (animationtask == null || !animationtask.isAlive()) {
			System.out.println("Restarting the animation");
			framecount = 0;
			//ClearOffscreenBuffer(nextframe);
			running = true;
			animationtask = new Thread(this, "Animation Loop");
			animationtask.start();
		}
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
			UpdateAnimations();
			RenderNextFrame(nextframegc);
			endtime = System.nanoTime();
			timeremaining = framedurationms - ((endtime-starttime)/1000000);
			// System.out.printf("Frame: %d  Time remaining: %dms\n", framecount, timeremaining);
			if (timeremaining > 0) {
				try {
					Thread.sleep(timeremaining);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			ShowNextFrame(windowgc);
			++framecount;
		}
		while (running);
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
		RestartAnimation();				// buffer & Graphics objects have changed
	}
	
	protected void drawWindowText(Graphics g)
	{
		/* String message1 = "Hello World!";
		
		// draw strings with parameter values, highlighting the selected parameter
		FontMetrics  fm = g.getFontMetrics();
		int lineht = fm.getHeight();
		int x = 10; */
		
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
	
	protected void UpdateAnimations()
	{
		Iterator<CircleAnimation> iter = circles.iterator();
		
		while (iter.hasNext()) {
			iter.next().tick();
		}
	}

	protected void RenderNextFrame(Graphics2D g)
	{
		// clear the frame with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// draw circles
		Iterator<CircleAnimation> iter = circles.iterator();
		
		while (iter.hasNext()) {
			iter.next().render(g);
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
	protected void ShowNextFrame(Graphics2D g)
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
		else if	(key == 'A' || key == 'a') {
			// 'a' and 'A' add a new circle to the animation
			circles.add(new CircleAnimation(drawingArea, DEFAULT_FRAME_RATE));
		}
		else if	(key == 'P' || key == 'p') {
			// 'p' and 'P' pause the animation
			System.out.println("Pausing the animation");
			running = false;
		}
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' restart the animation (if it has stopped)
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
		AnimatedCircles app = new AnimatedCircles();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
