/*	LogarithmicSpiral.java
	
	Draws a logarithmic spiral, which is a self-similar spiral curve
	with equation r = a*e^(b*theta).
	
	http://en.wikipedia.org/wiki/Logarithmic_spiral

	Anthony Kozar
	August 24, 2014
	
*/

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;


public class LogarithmicSpiral extends JFrame
{
	final private int	WINWIDTH = 400;
	final private int	WINHEIGHT = 400;
	
	protected double	centerx;
	protected double	centery;
	protected double	radius;
	
	public LogarithmicSpiral()
	{
		super("A logarithmic spiral");
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
		final double angleincr = Math.PI/5000.0;	// increment at which to draw points
		double	r, angle, x, y;
		
		super.paint(g);
		g.setColor( Color.blue );
		
		// draws a logarithmic spiral
		r = 0.0;
		angle = arcstart;
		while (r < radius) {
			r = Math.exp(0.2*angle);
			x = centerx + r * Math.cos(angle);
			y = centery - r * Math.sin(angle);
			drawPoint(g, x, y);
			angle += angleincr;
		}
	}
	
	public static void main(String[] args)
	{
		LogarithmicSpiral app = new LogarithmicSpiral();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
