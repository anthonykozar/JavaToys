/*	Circle.java
	
	A simple class for drawing a circle (or arcs if the parameters are changed).
	  
	Anthony Kozar
	August 22, 2014
	
 */

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;


public class Circle extends JFrame
{
	final private int	WINWIDTH = 400;
	final private int	WINHEIGHT = 400;

	protected double	centerx;
	protected double	centery;
	protected double	radius;

	public Circle()
	{
		super("A blue circle");
		setSize(WINWIDTH, WINHEIGHT);
		setVisible(true);

		// find the center of the window and a radius that will fit
		centerx = WINWIDTH * 0.5;
		centery = WINHEIGHT * 0.5;
		radius = (Math.min(WINWIDTH, WINHEIGHT) * 0.5) - 30.0;
	}
	
	protected void drawPoint(Graphics g, double x, double y)
	{
		// we have to use drawLine() to draw a single point
		g.drawLine((int)Math.round(x), (int)Math.round(y), 
				   (int)Math.round(x), (int)Math.round(y));
	}
	
	public void paint(Graphics g)
	{
		final double arcstart = 0.0;				// angle of beginning of arc
		final double arclen = 2.0*Math.PI;			// arclength of arc (in radians)
		final double arcend = arcstart + arclen;	// angle of end of arc
		final double angleincr = Math.PI/5000.0;	// increment at which to draw points
		double	x, y;

		super.paint(g);
		g.setColor( Color.blue );
		
		// draw points along a circle/arc from arcstart to arcend radians
		for (double angle = arcstart; angle <= arcend; angle += angleincr) {
			x = centerx + radius * Math.cos(angle);
			y = centery - radius * Math.sin(angle);
			drawPoint(g, x, y);
		}
	}

	public static void main(String[] args)
	{
		Circle app = new Circle();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

	}

}
