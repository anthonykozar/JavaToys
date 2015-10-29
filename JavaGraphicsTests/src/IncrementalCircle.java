/*	IncrementalCircle.java

	A simple class for drawing a circle incrementally using an offscreen buffer.
	
	Anthony Kozar
	October 29, 2015

*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class IncrementalCircle extends JFrame implements MouseListener, KeyListener
{
	final static private int	WINWIDTH = 400;		// preferred window size
	final static private int	WINHEIGHT = 400;
	final static private int	MARGINSIZE = 5;
	
	final static private String HELP_MESSAGE = "Press R to redraw, ! to exit";
	
	protected double	centerx;
	protected double	centery;
	protected double	drawingradius;	// maximum distance from the center that we can draw
	protected Rectangle	drawingArea;	// visible area of window minus margins
	protected Color		currentcolor;
	protected Image		offscrnBuffer;
	protected int		numsegments = 16;
	protected int		cursegment = 0;
	
	public IncrementalCircle()
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
		CreateOffscreenBuffer();
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
	
	protected void CreateOffscreenBuffer()
	{
		if (drawingArea == null)  SetMargins();
		
		offscrnBuffer = this.createImage(drawingArea.width, drawingArea.height);
		if (offscrnBuffer == null) {
			throw new NullPointerException("Could not create offscreen buffer!");
		}
		else {
			// clear the buffer with background color
			Graphics buffergc = offscrnBuffer.getGraphics();
			buffergc.setColor(Color.white);
			buffergc.fillRect(0, 0, drawingArea.width, drawingArea.height);
		}
	}
	
	public void WindowResized()
	{
		// System.out.println("WindowResized() called");
		SetMargins();
		SetOrigin();
		SetScale();
		CreateOffscreenBuffer();
		cursegment = 0;
		this.repaint();
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
		g.drawString(message1, x, drawingArea.y + lineht);
		
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
	
	public void paint(Graphics g)
	{
		final double arclen = 2.0*Math.PI/numsegments;	// arclength of arc (in radians)
		final double arcstart = cursegment*arclen;		// angle of beginning of arc
		final double arcend = arcstart + arclen;		// angle of end of arc
		final double angleincr = Math.PI/5000.0;		// increment at which to draw points
		double	x, y;

		super.paint(g);
		if (drawingArea == null)  SetMargins();
		
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
	
		drawWindowText(g);
		
		// draw one segment of the circle into buffer
		Graphics buffergc = offscrnBuffer.getGraphics();
		buffergc.setColor(currentcolor);
		// draw points along a circle/arc from arcstart to arcend radians
		for (double angle = arcstart; angle <= arcend; angle += angleincr) {
			x = centerx + drawingradius * Math.cos(angle);
			y = centery - drawingradius * Math.sin(angle);
			drawPoint(buffergc, x, y);
		}
		
		// copy buffered image to the window
		g.drawImage(offscrnBuffer, 0, 0, null);
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
			SetRandomColor();
			++cursegment;
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
	public void mouseClicked(MouseEvent event)	{}
	public void mousePressed(MouseEvent event)	{}
	public void mouseReleased(MouseEvent event)	{}
	public void mouseEntered(MouseEvent event)	{}
	public void mouseExited(MouseEvent event)	{}
	
	public static void main(String[] args)
	{
		IncrementalCircle app = new IncrementalCircle();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
