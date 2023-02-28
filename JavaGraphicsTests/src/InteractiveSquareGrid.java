/*	InteractiveSquareGrid.java

	Displays a square grid.  The individual cells can be colored by clicking in them.
	
	Anthony Kozar
	February 23, 2023
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class InteractiveSquareGrid extends JPanel implements MouseListener, KeyListener //, MenuHandler
{
	public final static int		CLEAR_COLOR_IDX = 0;

	private final static String	defaultWindowName = "Interactive Square Grid";
	private final static int	defaultWindowWidth = 525;
	private final static int	defaultWindowHeight = 550;
	private final static int	marginSize = 10;
	private final static int	defaultGridSize = 20;
	private final static int	defaultCellSize = 25;
	private final static int	lgNumXOffset1digit = 15;
	private final static int	lgNumXOffset2digit = 6;
	private final static int	lgNumYOffset = 37;
	private final static int	smNumXOffset[] = {7, 22, 37, 7, 22, 37, 7, 22, 37};
	private final static int	smNumYOffset[] = {16, 16, 16, 31, 31, 31, 46, 46, 46};
	private final static Color	selectionColor = new Color(240, 0, 0);
	private final static int	numColors = 9;
	
	enum Direction { UP, RIGHT, DOWN, LEFT, NEXT, PREVIOUS };
	enum EditMode  { VALUES, CLUES, RESERVES, REGIONS };
	
	private Font	largeNumFont;
	private Font	smallNumFont;
	private Color[]	colorPalette;
	private int[][]	cellColors;
	
	private int		winWidth;
	private int		winHeight;
	private int		cellSize;
	private int		gridSize;
	private int		gridTop, gridLeft, gridBottom, gridRight;

	private CellCoord		selectedCell = new CellCoord();		// top left corner (0,0)
	private int				selectedColorIdx = 1;
	private EditMode		editingMode = EditMode.CLUES;
	private	boolean			showCandidates = false;
	private	boolean			showRegionColors = true;
	private boolean			solving = false;	// TEMP -- REMOVE
	private float			saturation = 0.70f;
	

	public InteractiveSquareGrid()
	{
		this(defaultWindowWidth, defaultWindowHeight, defaultGridSize, defaultCellSize);
	}

	public InteractiveSquareGrid(int gridsize, int cellsize)
	{
		this(defaultWindowWidth, defaultWindowHeight, gridsize, cellsize);
	}
	
	public InteractiveSquareGrid(int width, int height, int gridsize, int cellsize)
	{
		super();
		winWidth = width;
		winHeight = height;
		gridSize = gridsize;
		cellSize = cellsize;
		
		setSize(winWidth, winHeight);
		setVisible(true);
		addMouseListener(this);
		addKeyListener(this);
		
		largeNumFont = new Font("Lucida Grande", Font.PLAIN, 30);
		smallNumFont = new Font("Lucida Grande", Font.PLAIN, 13);
		InitializeColors(numColors);
		setGridSize(gridSize);
	}
	
	private void InitializeColors(int num)
	{
		float	hue, /*saturation,*/ brightness, incr;
		
		colorPalette = new Color[num+1];
		colorPalette[0] = Color.white;
		
		// choose N well-spaced (hopefully distinct) background colors for regions
		hue = 0.0f; /*saturation = 0.4f;*/ brightness = 1.0f;
		// incr = (float)(0.5 * (Math.sqrt(5.0) + 1.0));			// the golden ratio
		incr = 1.0f / num;
		for (int i = 1; i <= num; i++) {
			colorPalette[i] = Color.getHSBColor(hue, saturation, brightness);
			hue += incr;
		}
	}
	
	private void CalculateGridPosition()
	{
		// get the size of our visible drawing area to avoid drawing underneath the window frame
		Insets visibleArea = this.getInsets();
		// define our own margins within that
		Insets margins = new Insets(visibleArea.top    + marginSize, 
		                            visibleArea.left   + marginSize, 
		                            visibleArea.bottom + marginSize, 
		                            visibleArea.right  + marginSize);
		
		/* System.out.printf("ViewRect: (%d, %d) (%d, %d)\n",  margins.top,
															margins.left,
															winHeight - margins.bottom,
															winWidth - margins.right); */
		
		gridTop = margins.top;
		gridLeft = margins.left;
		gridBottom = margins.top + cellSize*gridSize;
		gridRight = margins.right + cellSize*gridSize;
		
		this.setPreferredSize(new Dimension(gridRight + marginSize, gridBottom + marginSize));
		this.repaint();
	}
	
	public void setGridSize(int size)
	{
		gridSize = size;
		cellColors = new int[size][size];
		CalculateGridPosition();
	}

	public void setCellColor(CellCoord cell, int coloridx)
	{
		int row, col;
		row = cell.getRow();
		col = cell.getColumn();
		if (row < gridSize && col < gridSize && coloridx >= 0 && coloridx <= numColors) {
			cellColors[row][col] = coloridx;
			this.repaint();
		}
	}
	
	public void paint( Graphics g )
	{
		super.paint(g);
		
		/* final Font defaultFont = g.getFont();
		System.out.printf("Font (logical): %s\n", defaultFont.getName());
		System.out.printf("Font face: %s\n", defaultFont.getFontName());
		System.out.printf("Font size: %f\n", defaultFont.getSize2D());
		System.out.printf("Font style: %s %s %s\n", defaultFont.isPlain() ? "plain" : "",
													defaultFont.isBold() ? "bold" : "",
													defaultFont.isItalic() ? "italic" : ""); */
		
		// clear the window to white
		Dimension winSize = this.getSize();		
		g.setColor(Color.white);
		g.fillRect(0, 0, winSize.width, winSize.height);
	
		// draw Sudoku grid
		g.setColor(Color.black);
		for	( int i = 0; i <= gridSize; i++ )	{
			// horizontal lines
			g.drawLine(gridLeft, gridTop + cellSize*i, gridRight, gridTop + cellSize*i);
			// vertical lines
			g.drawLine(gridLeft + cellSize*i, gridTop, gridLeft + cellSize*i, gridBottom);
			// make region boundaries thicker (only for puzzles with default regions for now) 
			/*
			if (SudokuPuzzle.regionBoxHeights[gridSize] > 0) {
				if (i % SudokuPuzzle.regionBoxHeights[gridSize] == 0) {
					// horizontal
					g.drawLine(gridLeft, gridTop + cellSize*i + 1, gridRight, gridTop + cellSize*i + 1);
					g.drawLine(gridLeft, gridTop + cellSize*i - 1, gridRight, gridTop + cellSize*i - 1);
				}
				if (i % SudokuPuzzle.regionBoxWidths[gridSize] == 0) {
					// vertical
					g.drawLine(gridLeft + cellSize*i - 1, gridTop, gridLeft + cellSize*i - 1, gridBottom);
					g.drawLine(gridLeft + cellSize*i + 1, gridTop, gridLeft + cellSize*i + 1, gridBottom);
				}
			}
			*/
		}
		
		// outline the selected cell
		g.setColor(selectionColor);
		g.drawRect(gridLeft + cellSize*selectedCell.getColumn() + 1, gridTop + cellSize*selectedCell.getRow() + 1, cellSize-2, cellSize-2);
		
		// draw puzzle cell contents
		int cellstatus, cellvalue, curCellX, curCellY, maxCandidate, lgNumXOffset;
		
		maxCandidate = (gridSize < 9) ? gridSize : 9;				// can only display candidates up to gridSize or 9
		for ( int row = 0; row < gridSize; row++ )	{
			for ( int col = 0; col < gridSize; col++ )	{
				// fill background of the cell with the cell's color
				g.setColor(colorPalette[cellColors[row][col]]);
				g.fillRect(gridLeft + cellSize*col + 2, gridTop + cellSize*row + 2, cellSize-3, cellSize-3);
				
				/* 
				cellstatus = puzzleModel.getCellStatus(row, col);
				if (cellstatus == SudokuPuzzle.UNSOLVED) {
					if (showCandidates) {
						// draw (small) candidate numbers
						curCellX = gridLeft + col*cellSize;
						curCellY = gridTop + row*cellSize;
						
						g.setFont(smallNumFont);
						g.setColor(Color.darkGray);
						for ( int i = 0; i < maxCandidate; i++ )	{
							if (puzzleModel.testCellCandidate(row, col, i)) {
								g.drawString(String.valueOf(i+1), curCellX + smNumXOffset[i], curCellY + smNumYOffset[i]);
							}
						}
					}
				}
				else {
					// draw clues and (large) answer numbers
					g.setFont(largeNumFont);
					if (cellstatus == SudokuPuzzle.CLUE)
						 g.setColor(Color.black);
					else g.setColor(Color.blue);
					cellvalue = puzzleModel.getCellValue(row, col);
					lgNumXOffset = (cellvalue < 10) ? lgNumXOffset1digit : lgNumXOffset2digit;
					g.drawString(String.valueOf(cellvalue), gridLeft + col*cellSize + lgNumXOffset, 
								 gridTop + row*cellSize + lgNumYOffset);
				}
				*/
			}
		}
		
		
	}
	
	public void moveSelection(Direction dir)
	{
		int row, col;
		
		switch (dir) {
			case UP:
				// % operator can give a negative result if the dividend is negative!
				selectedCell.setRow((selectedCell.getRow()-1+gridSize) % gridSize);
				break;
			case DOWN:
				selectedCell.setRow((selectedCell.getRow()+1) % gridSize);
				break;
			case LEFT:
				// % operator can give a negative result if the dividend is negative!
				selectedCell.setColumn((selectedCell.getColumn()-1+gridSize) % gridSize);
				break;
			case RIGHT:
				selectedCell.setColumn((selectedCell.getColumn()+1) % gridSize);
				break;
			case NEXT:
				col = selectedCell.getColumn()+1;
				if (col >= gridSize) {
					// wrap around to the next row
					col = 0;
					row = selectedCell.getRow()+1;
					if (row >= gridSize) {
						// wrap back to cell (0,0)
						row = 0;
					}
				}
				else row = selectedCell.getRow();
				selectedCell.setCoord(row, col);
				break;
			case PREVIOUS:
				// FIXME: finish implementing wrapping behavior
				selectedCell.setColumn((selectedCell.getColumn()-1+gridSize) % gridSize);
				break;
		}
		// System.out.printf("Selected cell: (%d, %d)\n", selectedCell.getRow(), selectedCell.getColumn());
		this.repaint();
	
	}
	
	
	public void resetGrid()
	{
		this.repaint();
	}
	
	public void mouseClicked(MouseEvent event)
	{
		int mousex, mousey, row, col;
		
		mousex = event.getX();
		mousey = event.getY();
		// System.out.printf("Clicked point: (%d, %d)\n", mousex, mousey);
		
		// test if point is inside the grid
		if (mousex >= gridLeft && mousex <= gridRight && mousey >= gridTop && mousey <= gridBottom) {
			// determine which cell the click was in
			row = (mousey - gridTop) / cellSize;
			col = (mousex - gridLeft) / cellSize;
			// System.out.printf("Clicked cell: (%d, %d)\n", row, col);
			setCellColor(new CellCoord(row, col), selectedColorIdx);
		}
	}
	
	public void mousePressed(MouseEvent event)	{}
	public void mouseReleased(MouseEvent event)	{}
	public void mouseEntered(MouseEvent event)	{}
	public void mouseExited(MouseEvent event)	{}
	
	/* These 3 methods are the implementation of the KeyListener interface. */
	
	public void keyPressed(KeyEvent event)
	{
		int		key = event.getKeyCode();
		// System.out.println("keyPressed event: " + key);
		
		// Tab and arrow keys change the selected square when editing
		if	(key == KeyEvent.VK_TAB) {
			// System.out.println("Received tab ");
			moveSelection(Direction.NEXT);
		}
		else if	(key == KeyEvent.VK_RIGHT) {
			// System.out.println("Received right arrow");
			moveSelection(Direction.RIGHT);
		}
		else if	(key == KeyEvent.VK_LEFT) {
			// System.out.println("Received left arrow");
			moveSelection(Direction.LEFT);
		}
		else if	(key == KeyEvent.VK_UP) {
			// System.out.println("Received up arrow");
			moveSelection(Direction.UP);
		}
		else if	(key == KeyEvent.VK_DOWN) {
			// System.out.println("Received down arrow");
			moveSelection(Direction.DOWN);
		}
		
	}
	
	public void keyReleased(KeyEvent event)
	{
		int		key = event.getKeyCode();
		// System.out.println("keyReleased event: " + key);
	}
	
	public void keyTyped(KeyEvent event) 
	{
		char	key = event.getKeyChar();
		// System.out.println("keyTyped event: " + key);
		
		if	(key == '!') {
			// '!' exits the program
			System.exit(0);
		}
		else if	(key == '+' || key == '=') {
			saturation += 0.01f;
			System.out.println("saturation = " + saturation);
			InitializeColors(numColors);
			this.repaint();
		}
		else if	(key == '-') {
			saturation -= 0.01f;
			System.out.println("saturation = " + saturation);
			InitializeColors(numColors);
			this.repaint();
		}
		else if	(key == 's' || key == 'S') {
			// 's' and 'S' toggle whether candidates are shown
			showCandidates = !showCandidates;
			this.repaint();
		}
		else if	(key == 'c' || key == 'C') {
			// 'c' and 'C' toggle whether region colors are shown (when not editing regions)
			showRegionColors = !showRegionColors;
			this.repaint();
		}
		else if	(key == 'r' || key == 'R') {
			// 'r' and 'R' reset the grid to all white
			resetGrid();
		}
		else if	(Character.isDigit(key)) {
			// number keys set the current color
			int value = Integer.parseInt("" + key);
			if (value <= numColors) {
				selectedColorIdx = value;
			}
			else {
				// TODO: Beep at user to let them know they entered an invalid value ?
			}
		}
		else if	(key ==' ') {
			// when space is pressed, set the color of the selected cell
			setCellColor(selectedCell, selectedColorIdx);
		}
		else if	(key =='\n') {
			// when 'Enter' is pressed
		}
		else {
			// for all other keys, advance the selection to the next cell
			moveSelection(Direction.NEXT);
		}
		
		return;
	}
	
	// method for the MenuHandler interface
	/*public boolean DoMenuCommand(int menuCommand)
	{
		switch (menuCommand) {
			case MenuHandler.Cmd_Close:
				break;
			case MenuHandler.Cmd_Save:
				break;
			case MenuHandler.Cmd_Save_As:
				break;
			case MenuHandler.Cmd_Export:
				break;
			case MenuHandler.Cmd_Edit_Cell_Values:
				editingMode = EditMode.VALUES;
				this.repaint();
				break;
			case MenuHandler.Cmd_Edit_Clues:
				editingMode = EditMode.CLUES;
				this.repaint();
				break;
			case MenuHandler.Cmd_Edit_Reserved_Cells:
				editingMode = EditMode.RESERVES;
				this.repaint();
				break;
			case MenuHandler.Cmd_Edit_Regions:
				editingMode = EditMode.REGIONS;
				this.repaint();
				break;
			case MenuHandler.Cmd_Reset_Puzzle:
				resetPuzzle();
				break;
			case MenuHandler.Cmd_Clear_Puzzle:
				break;
			default:
				return false;
		}
		
		return true;
	}
	*/
	
	public static void main( String args[] )
	{
		JFrame window = new JFrame(defaultWindowName);
		InteractiveSquareGrid app = new InteractiveSquareGrid();
		JScrollPane scroller = new JScrollPane(app, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		window.setSize(defaultWindowWidth, defaultWindowHeight);
		window.add(scroller, BorderLayout.CENTER);
		window.setVisible(true);
		window.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		app.requestFocusInWindow();
	}
	
}
