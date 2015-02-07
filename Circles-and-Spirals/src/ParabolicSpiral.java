/*	ParabolicSpiral.java
	
	Draws Fermat's spiral (a.k.a. a parabolic spiral), a spiral where the square 
	of the distance from the center at any point is equal or proportional to 
	the angle of rotation.  It has the equation r^2 = a*theta.
	
	http://en.wikipedia.org/wiki/Fermat%27s_spiral
	
	Anthony Kozar
	August 24, 2014
	
*/

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;


public class ParabolicSpiral extends JFrame
{
	final private int	WINWIDTH = 400;
	final private int	WINHEIGHT = 400;
	
	protected double	centerx;
	protected double	centery;
	protected double	radius;
	
	public ParabolicSpiral()
	{
		super("Fermat's spiral");
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
		final double scale = 400.0;
		final double arcstart = 0.0;				// angle of beginning of spiral
		final double arcend = radius*radius/scale;	// angle of end of spiral
		final double angleincr = Math.PI/1200.0;	// increment at which to draw points
		double	r, x, y;
		
		super.paint(g);
		g.setColor( Color.blue );
		
		// draw a parabolic spiral
		for (double angle = arcstart; angle <= arcend; angle += angleincr) {
			r = Math.sqrt(scale*angle);
			// draw the positive arm
			x = centerx + r * Math.cos(angle);
			y = centery - r * Math.sin(angle);
			drawPoint(g, x, y);
			// draw the negative arm
			x = centerx - r * Math.cos(angle);
			y = centery + r * Math.sin(angle);
			drawPoint(g, x, y);
		}
	}
	
	public static void main(String[] args)
	{
		ParabolicSpiral app = new ParabolicSpiral();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
