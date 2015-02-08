import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BlueRectangle extends JFrame
{
	public BlueRectangle()
	{
		super("A blue rectangle");
		setSize( 200,200 );
		setVisible(true);
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		g.setColor( Color.blue );
		g.fillRect( 50, 50, 100, 100 );
	}

	public static void main( String args[] )
	{
		BlueRectangle app = new BlueRectangle();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	}

}
