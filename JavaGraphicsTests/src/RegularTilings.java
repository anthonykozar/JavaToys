/*	RegularTilings.java
	
	Test to draw the 3 regular tilings of the plane.
	
	Anthony Kozar
	August 16, 2015
	
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class RegularTilings extends JFrame implements MouseListener, KeyListener
{
	final static private int	WINWIDTH = 800;		// preferred window size
	final static private int	WINHEIGHT = 600;
	final static private int	MARGINSIZE = 5;

	final static private String HELP_MESSAGE = "Press 1, 2, or 3 to select the tiling, -/+ to resize, R to redraw, ! to exit";
	final static private int	HELP_HEIGHT = 20;
	
	final static protected int	FIRST_TILING = 1;
	final static protected int	SQUARE_TILING = 1;
	final static protected int	TRIANGLE_TILING = 2;
	final static protected int	HEXAGON_TILING = 3;
	final static protected int	LAST_TILING = 3;

	final static protected double	ROOT_3 = Math.sqrt(3.0);
	final static protected double	ONE_HALF_ROOT_3 = ROOT_3 * 0.5;
	
	protected double	centerx;
	protected double	centery;
	protected double	drawingradius;	// maximum distance from the center that we can draw
	protected Rectangle	drawingArea;	// visible area of window minus margins

	protected int		currentTiling = SQUARE_TILING;
	protected double	gridheight = 50.0;
	
	public RegularTilings()
	{
		super("Regular Tilings");

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
		height = this.getHeight() - (top + visibleArea.bottom + MARGINSIZE + HELP_HEIGHT);
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
		String message1 = "";
		
		// draw strings with parameter values, highlighting the selected parameter
		FontMetrics  fm = g.getFontMetrics();
		int lineht = fm.getHeight();
		int x = 10;
		
		// draw some message at the top
		g.setColor(Color.black);
		g.drawString(message1, x, drawingArea.y + lineht);
		
		// draw keyboard help message below the drawing area
		g.drawString(HELP_MESSAGE, 10, (int)drawingArea.getMaxY() + HELP_HEIGHT);
	}
	
	public void drawSquareTiling(Graphics g)
	{
		int x, y, xmin, ymin, xmax, ymax;
		int numvertlines, numhorizlines;
		
		xmin = drawingArea.x;
		ymin = drawingArea.y;
		xmax = (int)drawingArea.getMaxX();
		ymax = (int)drawingArea.getMaxY();
		numvertlines = (int)(drawingArea.getWidth() / gridheight);
		numhorizlines = (int)(drawingArea.getHeight() / gridheight);
		
		// just draw horizontal and vertical lines in a grid
		for (int yline = 0; yline <= numhorizlines; yline++) {
			y = ymin + (int)Math.round(yline*gridheight);
			g.drawLine(xmin, y, xmax, y);
		}
		for (int xline = 0; xline <= numvertlines; xline++) {
			x = xmin + (int)Math.round(xline*gridheight);
			g.drawLine(x, ymin, x, ymax);
		}
	}
	
	public void drawTriangleTiling(Graphics g)
	{
		int x, y, xmin, ymin, xmax, ymax, xchange;
		int numvertlines, numhorizlines;
		
		// height:width ratio of an equilateral triangle is sqrt(3)/2
		double gridwidth = gridheight / ONE_HALF_ROOT_3;
		// slope of diagonal lines is +/- ROOT_3
		xchange = (int)Math.round(drawingArea.getHeight() / ROOT_3);
		
		xmin = drawingArea.x;
		ymin = drawingArea.y;
		xmax = (int)drawingArea.getMaxX();
		ymax = (int)drawingArea.getMaxY();
		numvertlines = (int)((drawingArea.getWidth() + 2*xchange) / gridwidth);
		numhorizlines = (int)(drawingArea.getHeight() / gridheight);
		
		// No need to draw individual triangles,
		// just draw horizontal and two sets of diagonal lines
		for (int yline = 0; yline <= numhorizlines; yline++) {
			y = ymin + (int)Math.round(yline*gridheight);
			g.drawLine(xmin, y, xmax, y);
		}
		// This quick and dirty method to draw the diagonal lines
		// always draws them from ymin to ymax causing some of the
		// lines to overflow the x range of the drawing area. It
		// also draws more lines than needed in one direction on
		// each side of the drawing area.
		// Need to start to the left of the drawing area:
		xmin -= xchange;
		for (int xline = 0; xline <= numvertlines; xline++) {
			x = xmin + (int)Math.round(xline*gridwidth);
			g.drawLine(x, ymin, x+xchange, ymax);
			g.drawLine(x, ymin, x-xchange, ymax);
		}
	}
	
	public void drawHexagonTiling(Graphics g)
	{
		int x0, x1, x2, x3, x4, x5, y0, y1, y2, xmin, ymin, xmax, ymax, xchange;
		int numcolumns, numrows;
		double xoffset, yoffset, hexside, halfhexside, threehalvesside, hexwidth, 
			   hexheight, halfhexheight, columnoffset;
		
		/*	Thanks to Red Blob Games' guide to hexagonal grids for a 
			clear explanation of the geometry & other considerations!
			<http://www.redblobgames.com/grids/hexagons/>
		 
		 	Drawing hexagons in the "flat-topped" orientation:
			Total width of each hexagon is 2 * side length.
			The height:side ratio of a regular hexagon is sqrt(3):1, therefore
			the height:width ratio is sqrt(3):2 (like the equilateral triangle).
		 */
		hexheight = gridheight;
		halfhexheight = 0.5 * hexheight;
		hexwidth = hexheight / ONE_HALF_ROOT_3;
		hexside = 0.5 * hexwidth;
		halfhexside = 0.5 * hexside;
		threehalvesside = 1.5 * hexside;
		
		// The offset between two hexagons at exactly the same vertical position:
		// (offset between adjacent hexagons in "staggered" positions is half this)
		columnoffset = 3.0 * hexside;
		
		xmin = drawingArea.x;
		ymin = drawingArea.y;
		xmax = (int)drawingArea.getMaxX();
		ymax = (int)drawingArea.getMaxY();
		
		// For numcolumns, I'm counting only the hexagons at exactly the same 
		// vertical position.  I'll draw the staggered columns at the same time.
		numcolumns = (int)(drawingArea.getWidth() / columnoffset) + 1;
		numrows = (int)(drawingArea.getHeight() / hexheight) + 1;
		
		/*	Draw one row at a time which consists of numcolumns hexagons in a
			"top layer" and numcolumns hexagons in a "staggered layer" beneath 
			that.  Only need to draw the top half of individual hexagons:
				 __    __    __    __
				/  \__/  \__/  \__/  \__
				   /  \  /  \  /  \  /  \
		 */
		y1 = ymin;
		for (int yhex = 0; yhex < numrows; yhex++) {
			yoffset = yhex*hexheight;
			y0 = ymin + (int)Math.round(yoffset + halfhexheight);
			y2 = ymin + (int)Math.round(yoffset + hexheight);
			x0 = xmin;
			x1 = xmin + (int)Math.round(halfhexside);
			// we do need to draw one missing side on the left edge of each row
			g.drawLine(x0, y0, x1, y2);
			for (int xhex = 0; xhex < numcolumns; xhex++) {
				xoffset = (int)Math.round(xhex*columnoffset);
				x2 = xmin + (int)Math.round(xoffset + threehalvesside);
				x3 = xmin + (int)Math.round(xoffset + hexwidth);
				x4 = xmin + (int)Math.round(xoffset + columnoffset);
				x5 = xmin + (int)Math.round(xoffset + columnoffset + halfhexside);
				// draw half of the top layer hexagon
				g.drawLine(x0, y0, x1, y1);
				g.drawLine(x1, y1, x2, y1);
				g.drawLine(x2, y1, x3, y0);
				// draw half of the staggered layer hexagon
				g.drawLine(x2, y2, x3, y0);
				g.drawLine(x3, y0, x4, y0);
				g.drawLine(x4, y0, x5, y2);
				// reuse values
				x0 = x4;
				x1 = x5;
			}
			y1 = y2;
		}
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		if (drawingArea == null)  SetMargins();
		
		// clear the window with background color
		g.setColor(Color.white);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		drawWindowText(g);
		g.setColor(new Color(160, 210, 150));
		switch(currentTiling) {
			case SQUARE_TILING:
				drawSquareTiling(g);
				break;

			case TRIANGLE_TILING:
				drawTriangleTiling(g);
				break;

			case HEXAGON_TILING:
				drawHexagonTiling(g);
				break;
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
			// '-' decreases the grid height and repaints
			if (gridheight > 10.0) {
				gridheight -= 10.0;
				this.repaint();
			}
		}
		else if	(key == '+' || key == '=') {
			// '+' (or '=') increases the grid height and repaints
			if (gridheight < drawingradius) {
				gridheight += 10.0;
				this.repaint();
			}
		}
		else if	(key == 'R' || key == 'r') {
			// 'r' and 'R' cause the window to be redrawn
			this.repaint();
		}
		else if	(Character.isDigit(key)) {
			// number keys select which tiling to display
			int value = Integer.parseInt("" + key);
			if (value >= FIRST_TILING && value <= LAST_TILING) {
				currentTiling = value;
				this.repaint();
			}
			System.out.println("Displaying tiling " + currentTiling);
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
		RegularTilings app = new RegularTilings();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

	}

}
