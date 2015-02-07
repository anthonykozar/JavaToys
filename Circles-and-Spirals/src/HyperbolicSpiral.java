/*	HyperbolicSpiral.java

	Draws a hyperbolic spiral (a.k.a. a reciprocal spiral), with equation r = a/theta.
	
	http://en.wikipedia.org/wiki/Hyperbolic_spiral
	
	Anthony Kozar
	September 30, 2014

*/

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;


public class HyperbolicSpiral extends JFrame
{
	final private int	WINWIDTH = 400;
	final private int	WINHEIGHT = 400;
	
	protected double	centerx;
	protected double	centery;
	protected double	radius;
	
	public HyperbolicSpiral()
	{
		super("A hyperbolic spiral");
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
		final double a = 120.0;						// "the spiral has an asymptote at y = a" (WP)
		final double arcstart = a / radius;			// angle of beginning of arc
		double angleincr = Math.PI/10000.0;			// increment at which to draw points
		double	r, angle, x, y;
		
		super.paint(g);
		g.setColor( Color.blue );
		
		// the hyperbolic spiral is drawn from the outside in towards the center
		// (i.e. as the angle increases, the distance r from the center shrinks)
		r = a;
		angle = arcstart;
		while (r > 0.5) {
			r = a / angle;
			x = centerx + r * Math.cos(angle);
			y = centery - r * Math.sin(angle);
			drawPoint(g, x, y);
			angle += angleincr;
			angleincr *= 1.001;						// use larger incr as pixels get closer
		}
	}
	
	public static void main(String[] args)
	{
		HyperbolicSpiral app = new HyperbolicSpiral();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
