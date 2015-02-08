import java.awt.*;
import java.awt.event.*;
import java.math.*;
import javax.swing.*;

public class ImprovedRandomSquares extends JFrame implements MouseListener
{
	final static int	winWidth = 500;
	final static int	winHeight = 500;
	final static int	maxSqrWidth = 100;
	final static int	marginSize = 10;
	
	public ImprovedRandomSquares()
	{
		super("Improved random squares");
		setSize( winWidth, winHeight );
		setVisible(true);
		addMouseListener(this);
	}
	
	// produce a random integer on the interval [low,high]
	private int	RandomOn( int low, int high )
	{
		return low + (int)((high-low+1) * Math.random());
	}
	
	private int	Minimum( int a, int b )
	{
		return ((a < b)?a:b);
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		// clear the window to white
		g.setColor( Color.white );
		g.fillRect( 0, 0, winWidth, winHeight );

		// original RandomSquares could draw underneath the window frame
		// so, get the size of our visible drawing area
		// and define our own margins within that
		Insets visibleArea = this.getInsets();
		Insets margins = new Insets(visibleArea.top    + marginSize, 
		                            visibleArea.left   + marginSize, 
		                            visibleArea.bottom + marginSize, 
		                            visibleArea.right  + marginSize );
		
		// draw 100 randomly sized, positioned, and colored rectangles
		for	( int i = 0; i < 100; i++ )	{
			int	red = RandomOn( 0, 255 ); 
			int	green = RandomOn( 0, 255 ); 
			int	blue = RandomOn( 0, 255 ); 
			g.setColor( new Color( red, green, blue ) );
			
			// use margins to limit square position
			int	left = RandomOn( margins.left, winWidth - (margins.right + maxSqrWidth + 1) );
			int	top = RandomOn( margins.top, winHeight - (margins.bottom + maxSqrWidth + 1) );			
			int	width = RandomOn( 1, maxSqrWidth );
			g.fillRect( left, top, width, width );
		}
	}

	public	void mouseClicked( MouseEvent event )
	{
		this.repaint();
	}

	public	void mousePressed( MouseEvent event )	{}
	public	void mouseReleased( MouseEvent event )	{}
	public	void mouseEntered( MouseEvent event )	{}
	public	void mouseExited( MouseEvent event )	{}

	public static void main( String args[] )
	{
		ImprovedRandomSquares app = new ImprovedRandomSquares();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
