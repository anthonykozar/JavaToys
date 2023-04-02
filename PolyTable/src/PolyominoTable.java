/*	PolyominoTable.java
	
	A window that displays a grid and controls for placing polyominoes on the grid.
	
	Anthony Kozar
	April 1, 2023
	
 */

import	java.awt.*;
import	java.awt.event.*;
import	javax.swing.*;
import	javax.swing.event.*;


@SuppressWarnings("serial")
public class PolyominoTable extends JFrame
{
	private final static String	defaultWindowName = "New Table";
	private final static int	defaultWindowWidth = 650;
	private final static int	defaultWindowHeight = 575;
	
	private final static String[]  polyominoNames = {"Monomino", "Domino", "I Tromino", "Elbow Tromino",
										"I Tetromino", "L Tetromino", "Square Tetromino", "T Tetromino",
										"S Tetromino"};

	public PolyominoTable()
	{
		super(defaultWindowName);
		
		// center pane contains the scrollable grid
		InteractiveSquareGrid grid = new InteractiveSquareGrid();
		JScrollPane centerpane = new JScrollPane(grid, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		// right pane contains a scrollable list of polyominoes
		JList<String> polylist = new JList<String>(polyominoNames);
		polylist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane rightpane = new JScrollPane(polylist, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		// top pane will contain buttons for colors (and edit modes?)
		JPanel toppane = new JPanel();
		toppane.setLayout(new FlowLayout());
		
		this.setSize(defaultWindowWidth, defaultWindowHeight);
		this.add(centerpane, BorderLayout.CENTER);
		this.add(rightpane, BorderLayout.EAST);
		this.add(toppane, BorderLayout.NORTH);
		this.setVisible(true);
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		grid.requestFocusInWindow();
	}
}
