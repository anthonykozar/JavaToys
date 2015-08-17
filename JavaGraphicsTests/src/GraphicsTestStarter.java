/*	GraphicsTestStarter.java

	A class with template code for starting new graphics tests.
	
	Anthony Kozar
	August 16, 2015

*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class GraphicsTestStarter extends JFrame implements MouseListener, KeyListener
{
	final private int	WINWIDTH = 800;		// preferred window size
	final private int	WINHEIGHT = 600;
	final private int	MARGINSIZE = 5;
	
	final private String HELP_MESSAGE = "Press R to redraw, ! to exit";
	
	protected double	centerx;
	protected double	centery;
	protected double	drawingradius;	// maximum distance from the center that we can draw
	protected Rectangle	drawingArea;	// visible area of window minus margins
	
	
	public GraphicsTestStarter()
	{
		super("Graphics Test");

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
	
	public void WindowResized()
	{
		// System.out.println("WindowResized() called");
		SetMargins();
		SetOrigin();
		SetScale();
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
	
	public void paint(Graphics g)
	{
		super.paint(g);
		if (drawingArea == null)  SetMargins();
		
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		// g.fillRect(drawingArea.x, drawingArea.y, drawingArea.width, drawingArea.height);
	
		drawWindowText(g);
		g.setColor(new Color(190, 240, 180));
		g.fillOval((int)(centerx - drawingradius), (int)(centery - drawingradius), 
				   (int)(2*drawingradius), (int)(2*drawingradius));
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
			// 'r' and 'R' cause the window to be redrawn
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
		GraphicsTestStarter app = new GraphicsTestStarter();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
