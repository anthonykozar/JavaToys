import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;


@SuppressWarnings("serial")
public class PolyominoTable extends JFrame
{
	private final static String	defaultWindowName = "New Table";
	private final static int	defaultWindowWidth = 525;
	private final static int	defaultWindowHeight = 550;

	public PolyominoTable()
	{
		super(defaultWindowName);
		
		InteractiveSquareGrid grid = new InteractiveSquareGrid();
		JScrollPane scroller = new JScrollPane(grid, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.setSize(defaultWindowWidth, defaultWindowHeight);
		this.add(scroller, BorderLayout.CENTER);
		this.setVisible(true);
		this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		grid.requestFocusInWindow();
	}
}
