import java.awt.*;
import java.awt.event.*;
import java.math.*;
import javax.swing.*;

public class RandRectangles extends JFrame
{
	public RandRectangles()
	{
		super("Random rectangles");
		setSize( 600,600 );
		setVisible(true);
	}
	
	private int	RandomOn( int low, int high )
	{
		return low + (int)((high-low+1) * Math.random() );
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		for	( int i = 0; i < 100; i++ )	{
			int	red = RandomOn( 0, 255 ); 
			int	green = RandomOn( 0, 255 ); 
			int	blue = RandomOn( 0, 255 ); 
			g.setColor( new Color( red, green, blue ) );
			
			int	left = RandomOn( 0, 599 );
			int	top = RandomOn( 0, 599 );
			int	width = RandomOn( 1, 600-left );
			int	height = RandomOn( 1, 600-top );
			g.fillRect( left, top, width, height );
		}
	}

	public static void main( String args[] )
	{
		RandRectangles app = new RandRectangles();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
