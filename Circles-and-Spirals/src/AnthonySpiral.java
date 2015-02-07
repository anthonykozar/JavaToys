/*	AnthonySpiral.java

	Draws a spiral similar to a lituus but with the equation r^0.9 = a/theta.
	
	Anthony Kozar
	October 1, 2014

*/

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;


public class AnthonySpiral extends JFrame
{
	final private int	WINWIDTH = 1000;
	final private int	WINHEIGHT = 1000;
	
	final private double	power = 0.9;
	
	protected double	centerx;
	protected double	centery;
	protected double	radius;
	
	public AnthonySpiral()
	{
		super("Anthony's spiral");
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
		final double a = 6000.0;						// proportional constant 'a'
		final double arcstart = a / Math.pow(radius, 2);// angle of beginning of arc
		double angleincr = Math.PI/1000.0;				// increment at which to draw points
		double	r, angle, x, y;
		
		super.paint(g);
		g.setColor( new Color(128, 0, 160));			// purple
		
		// the lituus is drawn from the outside in towards the center
		// (i.e. as the angle increases, the distance r from the center shrinks)
		r = a;
		angle = arcstart;
		while (r > 0.5) {
			r = Math.pow((a / angle), power);
			// draw the positive arm
			x = centerx + r * Math.cos(angle);
			y = centery - r * Math.sin(angle);
			drawPoint(g, x, y);
			// draw the negative arm
			x = centerx - r * Math.cos(angle);
			y = centery + r * Math.sin(angle);
			drawPoint(g, x, y);
			angle += angleincr;
			angleincr *= 1.001;						// use larger incr as pixels get closer
		}
	}
	
	public static void main(String[] args)
	{
		AnthonySpiral app = new AnthonySpiral();
		app.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	
	}

}
