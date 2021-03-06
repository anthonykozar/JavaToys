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
	private final static String APP_NAME = "Circles and Spirals";
	
	// menu command constants
	private final static int MenuCmd_Quit					= 1;
	private final static int MenuCmd_About					= 2;
	private final static int MenuCmd_Help					= 3;

	private final static int MenuCmd_Circle					= 4;
	private final static int MenuCmd_Lissajous				= 5;
	
	private final static int MenuCmd_Archimedean_Spiral		= 6;
	private final static int MenuCmd_Fermats_Spiral			= 7;
	private final static int MenuCmd_Logarithmic_Spiral		= 8;
	private final static int MenuCmd_Hyperbolic_Spiral		= 9;
	private final static int MenuCmd_Lituus					= 10;
	private final static int MenuCmd_Double_Lituus			= 11;
	private final static int MenuCmd_Anthonys_Spiral		= 12;

	private final static int MenuCmd_Hypotrochoids			= 13;
	private final static int MenuCmd_Polytrochoids			= 14;
	private final static int MenuCmd_Lissajous_Trochoids	= 15;

	private ActionListener	menulistener;

	public static void main(String[] args) {
        // Set application name and system menu bar on Mac OS X 
        String os = System.getProperty("os.name");
        if (os.contains("Mac OS X")) {
        	System.setProperty("apple.laf.useScreenMenuBar", "true");
        	// The next line doesn't work in recent JVMs on OS X,
        	// so we also include -Xdock:name="App name" in the runtime config
        	System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_NAME);
        }
		
        // create main window
        new Main().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Main()
	{
        super(APP_NAME);

        menulistener = new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				MyMenuItem item;
				
				// System.out.println("Menu listener command: " + event.getActionCommand());
				item = (MyMenuItem)event.getSource();
				// System.out.println("  command ID: " + item.getCommandID());
				DoMenuItem(item.getCommandID());
			}
		};
		
		this.setJMenuBar(CreateMenubar());
		this.setSize(400, 75);
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
	
	private JMenuBar CreateMenubar()
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
		
		return mbar;
	}
	
	private void DoMenuItem(int menuCommand)
	{
		JFrame newwindow = null;
		
		switch (menuCommand) {
			case MenuCmd_Quit:
				break;
			case MenuCmd_About:
				break;
			case MenuCmd_Help:
				break;
			case MenuCmd_Circle:
				newwindow = new Circle();
				break;
			case MenuCmd_Lissajous:
				newwindow = new LissajousCurve();
				break;	
			case MenuCmd_Archimedean_Spiral:
				newwindow = new LinearSpiral();
				break;
			case MenuCmd_Fermats_Spiral:
				newwindow = new ParabolicSpiral();
				break;
			case MenuCmd_Logarithmic_Spiral:
				newwindow = new LogarithmicSpiral();
				break;
			case MenuCmd_Hyperbolic_Spiral:
				newwindow = new HyperbolicSpiral();
				break;
			case MenuCmd_Lituus:
				newwindow = new Lituus();
				break;
			case MenuCmd_Double_Lituus:
				newwindow = new DoubleLituus();
				break;
			case MenuCmd_Anthonys_Spiral:
				newwindow = new AnthonySpiral();
				break;
			case MenuCmd_Hypotrochoids:
				newwindow = new HypotrochoidTest2();
				break;
			case MenuCmd_Polytrochoids:
				newwindow = new PolytrochoidTest2();
				break;
			case MenuCmd_Lissajous_Trochoids:
				newwindow = new LissajousTrochoid();
				break;
		}
		
		if (newwindow != null) {
			newwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			newwindow.setJMenuBar(CreateMenubar());
		}
	}
}
