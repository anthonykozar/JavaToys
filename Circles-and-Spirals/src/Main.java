/*	Main.java
	
	Simple main program to combine all of the individual programs/classes
	of the Circles and Spirals project into one.
	
	Anthony Kozar
	August 14, 2015
	
 */

import	java.awt.*;
import	java.awt.event.*;
import	javax.swing.*;
import	javax.swing.event.*;


public class Main extends JFrame 
{
	// menu command constants
	private final int MenuCmd_Quit					= 1;
	private final int MenuCmd_About					= 2;
	private final int MenuCmd_Help					= 3;

	private final int MenuCmd_Circle				= 4;
	private final int MenuCmd_Lissajous				= 5;
	
	private final int MenuCmd_Archimedean_Spiral	= 6;
	private final int MenuCmd_Fermats_Spiral		= 7;
	private final int MenuCmd_Logarithmic_Spiral	= 8;
	private final int MenuCmd_Hyperbolic_Spiral		= 9;
	private final int MenuCmd_Lituus				= 10;
	private final int MenuCmd_Double_Lituus			= 11;
	private final int MenuCmd_Anthonys_Spiral		= 12;

	private final int MenuCmd_Hypotrochoids			= 13;
	private final int MenuCmd_Polytrochoids			= 14;
	private final int MenuCmd_Lissajous_Trochoids	= 15;

	private ActionListener	menulistener;

	public static void main(String[] args) {
		new Main().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Main()
	{
        super("Circles and Spirals");

		menulistener = new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				MyMenuItem item;
				
				System.out.println("Menu listener command: " + event.getActionCommand());
				item = (MyMenuItem)event.getSource();
				System.out.println("  command ID: " + item.getCommandID());
				DoMenuItem(item.getCommandID());
			}
		};
		
		CreateMenus();		
		this.setSize(400, 400);
		this.setVisible(true);
	}
	
	/** Simple extension of JMenuItem that allows associating a unique
	 *  command ID with a menu item.  This ID can be used to distinguish
	 *  between menu items in a shared event listener.
	 */
	private class MyMenuItem extends JMenuItem
	{
		private int	commandID;
		
		public MyMenuItem(String text, int commandID)
		{
			super(text);
			this.commandID = commandID;
		}

		public MyMenuItem(String text, int commandID, int mnemonic)
		{
			super(text, mnemonic);
			this.commandID = commandID;
		}
		
		public int getCommandID() { return commandID; };
	}
	
	private void AddMenuItem(JMenu menu, String menutext, int commandID, int mnemonic)
	{
		JMenuItem	menuitem;

		menuitem = new MyMenuItem(menutext, commandID, mnemonic);
		menuitem.addActionListener(menulistener);
		menu.add(menuitem);		
	}
	
	private void AddMenuItem(JMenu menu, String menutext, int commandID)
	{
		JMenuItem	menuitem;

		menuitem = new MyMenuItem(menutext, commandID);
		menuitem.addActionListener(menulistener);
		menu.add(menuitem);		
	}
	
	private void CreateMenus()
	{
		JMenuBar	mbar;
		JMenu		file, edit, curves, spirals, trochoids, help;
		
		mbar = new JMenuBar();
		file = new JMenu("File");
		edit = new JMenu("Edit");
		curves = new JMenu("Curves");
		spirals = new JMenu("Spirals");
		trochoids = new JMenu("Trochoids");
		help = new JMenu("Help");
		
		AddMenuItem(file, "Quit", MenuCmd_Quit, 'Q');		
		AddMenuItem(help, "About Circles and Spirals...", MenuCmd_About, 'A');
		AddMenuItem(help, "Help...", MenuCmd_Help, 'H');
		
		AddMenuItem(curves, "Circle", MenuCmd_Circle);
		AddMenuItem(curves, "Lissajous", MenuCmd_Lissajous);

		AddMenuItem(spirals, "Archimedean Spiral", MenuCmd_Archimedean_Spiral);
		AddMenuItem(spirals, "Fermat's Spiral", MenuCmd_Fermats_Spiral);
		AddMenuItem(spirals, "Logarithmic Spiral", MenuCmd_Logarithmic_Spiral);
		AddMenuItem(spirals, "Hyperbolic Spiral", MenuCmd_Hyperbolic_Spiral);
		AddMenuItem(spirals, "Lituus", MenuCmd_Lituus);
		AddMenuItem(spirals, "Double Lituus", MenuCmd_Double_Lituus);
		AddMenuItem(spirals, "Anthony's Spiral", MenuCmd_Anthonys_Spiral);

		AddMenuItem(trochoids, "Hypotrochoids", MenuCmd_Hypotrochoids);
		AddMenuItem(trochoids, "Polytrochoids", MenuCmd_Polytrochoids);
		AddMenuItem(trochoids, "Lissajous Trochoids", MenuCmd_Lissajous_Trochoids);

		mbar.add(file);
		mbar.add(edit);
		mbar.add(curves);
		mbar.add(spirals);
		mbar.add(trochoids);
		mbar.add(help);
		
		this.setJMenuBar(mbar);
		return;
	}
	
	private void DoMenuItem(int menuCommand)
	{
		JFrame newwindow;
		
		switch (menuCommand) {
			case MenuCmd_Quit:
				break;
			case MenuCmd_About:
				break;
			case MenuCmd_Help:
				break;
			case MenuCmd_Circle:
				new Circle().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Lissajous:
				new LissajousCurve().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;	
			case MenuCmd_Archimedean_Spiral:
				new LinearSpiral().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Fermats_Spiral:
				new ParabolicSpiral().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Logarithmic_Spiral:
				new LogarithmicSpiral().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Hyperbolic_Spiral:
				new HyperbolicSpiral().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Lituus:
				new Lituus().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Double_Lituus:
				new DoubleLituus().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Anthonys_Spiral:
				new AnthonySpiral().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Hypotrochoids:
				new HypotrochoidTest2().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Polytrochoids:
				new PolytrochoidTest2().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
			case MenuCmd_Lissajous_Trochoids:
				new LissajousTrochoid().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				break;
		}
	}
}
