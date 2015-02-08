import	java.awt.*;
import	java.awt.event.*;
import	java.math.*;
import	javax.swing.*;
import	java.util.concurrent.TimeUnit;
// import	java.lang.InterruptedException;

public	class	LineBurstSlow extends JFrame implements MouseListener
{
	final private int	WINWIDTH = 600;
	final private int	WINHEIGHT = 600;
	final private int	MAXLINES = 100;
	
	private int			linecount = MAXLINES;
	private int			centerx;
	private int			centery;
	
	
	public	LineBurstSlow()
	{
		super("A burst of lines");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);
		addMouseListener(this);
		
		// I hoped that disabling double-buffering would allow "cumulative drawing"
		// across multiple paint() calls, but it doesn't seem to work.
		// RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
	}

	public	LineBurstSlow( String name, int xpos, int ypos, int width, int height )
	{
		super(name);
		setLocation(xpos, ypos);
		setSize(width, height);
		setVisible(true);
		addMouseListener(this);
	}

	private int	RandomOn( int low, int high )
	{
		return ( low + ( (int)((high-low+1) * Math.random() )) );
	}

	public	void paint( Graphics g )
	{
		// It is incorrect not to call super.paint() but this does allow
		// us to draw an image cumulatively over many paint() calls.
		// super.paint(g);

		// clear the window with random background color every MAXLINES lines
		if (linecount >= MAXLINES) {
			// use muted colors for background
			int	clred = RandomOn(240,255);
			int	clgreen = RandomOn(240,255);
			int	clblue = RandomOn(240,255);
			g.setColor(new Color(clred, clgreen, clblue));
			g.fillRect(0, 0, WINWIDTH, WINHEIGHT);
			linecount = 0;
			
			// choose new random center for line burst
			centerx = RandomOn(0, WINWIDTH-1);
			centery = RandomOn(0, WINHEIGHT-1);
		}


		// for	( int i = 0; i < 2000; i++ )	{
			int	red = RandomOn(0,255);
			int	green = RandomOn(0,255);
			int	blue = RandomOn(0,255);

			int	x = RandomOn(0, WINWIDTH-1);
			int y = RandomOn(0, WINHEIGHT-1);

			g.setColor(new Color(red, green, blue));
			g.drawLine(centerx, centery, x, y);
			++linecount;
		// }
		
		// calling sleep() in paint() might be a "bad" practice as well?
		try {
			// wait 10 milliseconds
			// can also use NANOSECONDS, MICROSECONDS, SECONDS, MINUTES, HOURS, or DAYS
			TimeUnit.MILLISECONDS.sleep(10);
		} catch (InterruptedException e) {
			// ignore exception
		}

		repaint();
	}

	public	void mouseClicked( MouseEvent event )
	{
		System.exit(0);
	}

	public	void mousePressed( MouseEvent event )	{}
	public	void mouseReleased( MouseEvent event )	{}
	public	void mouseEntered( MouseEvent event )	{}
	public	void mouseExited( MouseEvent event )	{}

	public	static	void main( String args[] )
	{
		LineBurstSlow	app = new LineBurstSlow();

		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
