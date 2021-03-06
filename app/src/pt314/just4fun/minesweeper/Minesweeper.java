package pt314.just4fun.minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import pt314.just4fun.minesweeper.game.Game;
import pt314.just4fun.minesweeper.gui.MineFieldPanel;
import pt314.just4fun.minesweeper.gui.StatusPanel;
import pt314.just4fun.minesweeper.images.ImageLoader;
import pt314.just4fun.minesweeper.util.Time;

/**
 * Main game class.
 */
public class Minesweeper extends JFrame implements ActionListener {

	private GameOptions options;
	
	private Game game;

	private StatusPanel statusPanel;
	private MineFieldPanel mineFieldPanel;
	
	// menu bar and menus
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenu optionsMenu;
    private JMenu helpMenu;
    
    // game menu items
    private JMenuItem newGameMI;
    private JRadioButtonMenuItem easyMI;
    private JRadioButtonMenuItem mediumMI;
    private JRadioButtonMenuItem hardMI;
    private JMenuItem exitMI;
    
    // option menu items
    private JCheckBoxMenuItem allowQuestionMarksMI;
    private JCheckBoxMenuItem allowRemovingMinesMI;
    private JCheckBoxMenuItem showHiddenMinesMI;
    
    // help menu items
    private JMenuItem aboutMI;

    public Minesweeper() {
		super("Just for fun Minesweeper game!");
		
		// Init options
		options = new GameOptions();
		options.setAllowQuestionMarks(true);
		options.setAllowRemovingMines(false);
		options.setShowHiddenMines(false);

		setResizable(false);
		initMenus();

		startNewGame();
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void initMenus() {
    	menuBar = new JMenuBar();

    	// create game menu
        gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        newGameMI = new JMenuItem("New Game");
        newGameMI.addActionListener(this);
        gameMenu.add(newGameMI);

        gameMenu.addSeparator();
        
        ButtonGroup group = new ButtonGroup();
        easyMI = new JRadioButtonMenuItem("Beginner");
        group.add(easyMI);
        gameMenu.add(easyMI);
        mediumMI = new JRadioButtonMenuItem("Intermediate");
        group.add(mediumMI);
        gameMenu.add(mediumMI);
        hardMI = new JRadioButtonMenuItem("Expert");
        group.add(hardMI);
        gameMenu.add(hardMI);
        mediumMI.setSelected(true); // default to medium

        gameMenu.addSeparator();

        exitMI = new JMenuItem("Exit");
        exitMI.addActionListener(this);
        gameMenu.add(exitMI);

    	// create options menu
        optionsMenu = new JMenu("Options");
        menuBar.add(optionsMenu);

        allowQuestionMarksMI = new JCheckBoxMenuItem("Allow question marks");
        allowQuestionMarksMI.setSelected(options.isAllowQuestionMarks());
        allowQuestionMarksMI.addActionListener(this);
        optionsMenu.add(allowQuestionMarksMI);

        allowRemovingMinesMI = new JCheckBoxMenuItem("Allow removing mines");
        allowRemovingMinesMI.setSelected(options.isAllowRemovingMines());
        allowRemovingMinesMI.addActionListener(this);
        optionsMenu.add(allowRemovingMinesMI);

        showHiddenMinesMI = new JCheckBoxMenuItem("Show hidden mines");
        showHiddenMinesMI.setSelected(options.isShowHiddenMines());
        showHiddenMinesMI.addActionListener(this);
        optionsMenu.add(showHiddenMinesMI);

    	// create help menu
        helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);

        aboutMI = new JMenuItem("About");
        aboutMI.addActionListener(this);
        helpMenu.add(aboutMI);

        // keyboard shortcuts
        
        // mnemonics for navigating menus
        gameMenu.setMnemonic(KeyEvent.VK_G);	// (G)ame
        easyMI.setMnemonic(KeyEvent.VK_B);		// (B)eginner
        mediumMI.setMnemonic(KeyEvent.VK_I);	// (I)ntermediate	
        hardMI.setMnemonic(KeyEvent.VK_E);		// (E)xpert
        exitMI.setMnemonic(KeyEvent.VK_X);		// E(x)it
        helpMenu.setMnemonic(KeyEvent.VK_H);	// (H)elp
        optionsMenu.setMnemonic(KeyEvent.VK_O);	// (O)ptions
        allowQuestionMarksMI.setMnemonic(KeyEvent.VK_Q);
        allowRemovingMinesMI.setMnemonic(KeyEvent.VK_R);
        
        // accelerators to select options without going through the menus
        newGameMI.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.getKeyText(KeyEvent.VK_F2)));	// F2: new game
        aboutMI.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.getKeyText(KeyEvent.VK_F1)));	// F1: help/about
        allowQuestionMarksMI.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.getKeyText(KeyEvent.VK_F3)));	// F3: allow question marks
        allowRemovingMinesMI.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.getKeyText(KeyEvent.VK_F4)));	// F4: allow removing mines
        showHiddenMinesMI.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.getKeyText(KeyEvent.VK_F5)));	// F5: show hidden mines

        // set menu bar
        setJMenuBar(menuBar);
	}
	
	/**
	 * Sets the size of the board and the number of mines.
	 */
	private void setGameDifficulty() {
		if (easyMI.isSelected())
			options.setDifficulty(GameOptions.DIFFICULTY_EASY);
		else if (mediumMI.isSelected())
			options.setDifficulty(GameOptions.DIFFICULTY_MEDIUM);
		else if (hardMI.isSelected())
			options.setDifficulty(GameOptions.DIFFICULTY_HARD);
	}

	// TODO: set mines after first space is cleared???
	private void startNewGame() {
		// init game
		setGameDifficulty();
		int numRows = options.getNumberOfRows();
		int numCols = options.getNumberOfColumns();
		int numMines = options.getNumberOfMines();
		game = new Game(numRows, numCols, numMines);
		
		// clear content pane
		Container contentPane = getContentPane();
		contentPane.removeAll();
		contentPane.setLayout(new BorderLayout());

		// add mine field panel
		mineFieldPanel = new MineFieldPanel(game, options);
		contentPane.add(mineFieldPanel, BorderLayout.CENTER);
		
		// add status panel
		statusPanel = new StatusPanel(game);
		contentPane.add(statusPanel, BorderLayout.NORTH);
		
		//contentPane.revalidate();
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem mItem = (JMenuItem) e.getSource();
		
		if (mItem == newGameMI) {
			startNewGame();
		}
		else if (mItem == exitMI) {
			System.exit(0);
		}
		else if (mItem == allowQuestionMarksMI) {
			options.setAllowQuestionMarks(allowQuestionMarksMI.isSelected());
		}
		else if (mItem == allowRemovingMinesMI) {
			// This makes the game too easy, so ask for confirmation
			if (allowRemovingMinesMI.isSelected()) {
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to allow removing mines?",
						"Removing mines", JOptionPane.YES_NO_OPTION);
				if (confirm != JOptionPane.YES_OPTION)
					allowRemovingMinesMI.setSelected(false);;
			}
			// Only update if changed
			if (allowRemovingMinesMI.isSelected() != options.isAllowRemovingMines())
				options.setAllowRemovingMines(allowRemovingMinesMI.isSelected());
		}
		else if (mItem == showHiddenMinesMI) {
			// This makes the game too easy, so ask for confirmation
			if (showHiddenMinesMI.isSelected()) {
				int confirm = JOptionPane.showConfirmDialog(this,
						"Are you sure you want to show all hidden mines?",
						"Show mines", JOptionPane.YES_NO_OPTION);
				if (confirm != JOptionPane.YES_OPTION)
					showHiddenMinesMI.setSelected(false);;
			}
			// Only update if changed
			if (showHiddenMinesMI.isSelected() != options.isShowHiddenMines()) {
				options.setShowHiddenMines(showHiddenMinesMI.isSelected());
				mineFieldPanel.update();
			}
		}
		else if (mItem == aboutMI) {
			JOptionPane.showMessageDialog(this,
					"https://github.com/pt314/minesweeper", "About",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

    public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				new Minesweeper();
		    }
		});
	}
}
