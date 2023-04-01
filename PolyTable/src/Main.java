/*	Main.java
	
	Main application class for the PolyTable program.
	
	Anthony Kozar
	April 1, 2023
	
 */

import	java.awt.*;
import	java.awt.event.*;
import	javax.swing.*;
import	javax.swing.event.*;


@SuppressWarnings("serial")
public class Main extends JFrame 
{
	private final static String APP_NAME = "PolyTable";
	
	// menu command constants
	private final static int MenuCmd_Quit					= 1;
	private final static int MenuCmd_About					= 2;
	private final static int MenuCmd_Help					= 3;
	private final static int MenuCmd_New_Table				= 4;

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
		JMenu		file, edit, help;
		
		mbar = new JMenuBar();
		file = new JMenu("File");
		edit = new JMenu("Edit");
		help = new JMenu("Help");
		
		AddMenuItem(file, "New Table", MenuCmd_New_Table, 'N');
		AddMenuItem(file, "Quit", MenuCmd_Quit, 'Q');
		AddMenuItem(help, "About PolyTable...", MenuCmd_About, 'A');
		AddMenuItem(help, "Help...", MenuCmd_Help, 'H');
		
		mbar.add(file);
		mbar.add(edit);
		mbar.add(help);
		
		return mbar;
	}
	
	private void DoMenuItem(int menuCommand)
	{
		JFrame newwindow = null;
		
		switch (menuCommand) {
			case MenuCmd_New_Table:
				newwindow = new PolyominoTable();
				break;
			case MenuCmd_Quit:
				break;
			case MenuCmd_About:
				break;
			case MenuCmd_Help:
				break;
		}
		
		if (newwindow != null) {
			newwindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			newwindow.setJMenuBar(CreateMenubar());
		}
	}
}
