import java.awt.*;
import java.awt.event.*;
import java.math.*;
import javax.swing.*;

public class RandomSquares extends JFrame
{
	public RandomSquares()
	{
		super("Random squares");
		setSize( 500,500 );
		setVisible(true);
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
		g.fillRect( 0, 0, 500, 500 );
		
		// draw 100 randomly sized, positioned, and colored rectangles
		for	( int i = 0; i < 100; i++ )	{
			int	red = RandomOn( 0, 255 ); 
			int	green = RandomOn( 0, 255 ); 
			int	blue = RandomOn( 0, 255 ); 
			g.setColor( new Color( red, green, blue ) );
			
			int	left = RandomOn( 0, 399 );
			int	top = RandomOn( 0, 399 );			
			int	width = RandomOn( 1, 100 );
			g.fillRect( left, top, width, width );
		}
	}

	public static void main( String args[] )
	{
		RandomSquares app = new RandomSquares();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
