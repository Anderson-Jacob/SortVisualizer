package sortVisualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import sortVisualizer.DrawPanel.Task;

/**
 * The frame for the SortVisualizer, handles all user interactions
 * like pausing, resuming, setting size, etc. All sorting/graphics
 * are performed inside of the DrawPanel class.
 * 
 * @author Jacob Anderson
 * @version 02/20/2024
 */
public class SortFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton startSort;
	private JButton stepThrough;
	private String currentSort = "bubbleSort";
	private JMenu sortMenu;
	
	private JButton setSize;
	private JButton setDelay;
	private DrawPanel plane;
	private JCheckBox[] checkBoxes = null;
	public static final int MAXRECTANGLES = 100;
	
	/**
	 * Builds the GUI for the SortVisualizer
	 */
	public SortFrame() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		plane = new DrawPanel();
		plane.setBackground(Color.WHITE);
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		JPanel panelForGraph = new JPanel();
		panelForGraph.setLayout(new GridLayout());
		
		stepThrough = new JButton();
		stepThrough.addActionListener(this);
		startSort = new JButton();
		//startSort's actionListener in a new thread so sorting
		//can be paused and resumed.
		startSort.addActionListener((e) -> {
			if (!startSort.getText().equals("Pause")) {
				(new Thread(() -> actionPerformed(e))).start();
				return;
			}
			startSort.setText("Resume");
			stepThrough.setEnabled(true);
			plane.performTask(Task.TOGGLEPAUSE);
		});
		startSort.setText("Start Sorting");
		stepThrough.setText("Disable Step by Step");
		JPanel bottomButtonPanel = new JPanel();
		bottomButtonPanel.setLayout(new GridLayout(1, 2));
		bottomButtonPanel.add(startSort);
		bottomButtonPanel.add(stepThrough);
		
		JMenuBar menuBar = new JMenuBar();
		sortMenu = new JMenu();
		sortMenu.setText("bubbleSort");
		JMenuItem[] sortList = new JMenuItem[] { new JMenuItem("quickSort"), 
				new JMenuItem("shellSort"), new JMenuItem("mergeSort"), 
				new JMenuItem("insertionSort"), new JMenuItem("bubbleSort"),
				new JMenuItem("selectionSort") };
		for (JMenuItem item : sortList) {
			sortMenu.add(item);
			item.addActionListener(this);
		}
		menuBar.add(sortMenu);
		
		JLabel selected = new JLabel("Current Sort:");
		
		setSize = new JButton();
		setSize.setSize(new Dimension(250, 350));
		setSize.setEnabled(true);
		setSize.setText("Set Size");
		setSize.addActionListener(this);
		
		setDelay = new JButton();
		setDelay.setSize(new Dimension(250, 350));
		setDelay.setEnabled(true);
		setDelay.setText("Set Speed");
		setDelay.addActionListener(this);
		
		JPanel p = new JPanel();
		p.add(selected);
		p.add(menuBar);
		p.add(new JLabel("      "));
		p.add(setSize);
		p.add(new JLabel("   "));
		p.add(setDelay);
		topPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
		topPanel.add(plane, BorderLayout.CENTER);
		plane.setSize(250, 250);
		topPanel.add(p, BorderLayout.NORTH);
		
		this.setTitle("Sort Visualizer");
		this.setContentPane(topPanel);
		this.pack();
		plane.setGraphics();

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() instanceof JCheckBox box) {
			//only allow 1 check box to be clicked at a time (inside set size pop-up)
			for (JCheckBox checkBox : checkBoxes) {
				checkBox.setSelected(false);
			}
			box.setSelected(true);

			return;
		}
		//change selected sort
		if (e.getSource() instanceof JMenuItem item) {
			currentSort = item.getText();
			sortMenu.setText(currentSort);
			return;
		}
		if (e.getSource().equals(setSize)) {
			changeSize();
			return;
		}
		if (e.getSource().equals(setDelay)) {
			changeDelay();
			return;
		}
		//logic to change text on the bottom two buttons depending
		//on the current state of the program
		JButton newE = (JButton) e.getSource();
		if (newE.equals(stepThrough)) {
			if (stepThrough.getText().equals("Enable Step by Step")) {
				plane.performTask(Task.TOGGLEPAUSE);
				stepThrough.setText("Disable Step by Step");
			} else if (stepThrough.getText().equals("Disable Step by Step")) {
				stepThrough.setText("Enable Step by Step");
				plane.performTask(Task.TOGGLEPAUSE);
			} else {
				if (!plane.hasNextStep()) {
					stepThrough.setEnabled(false);
					startSort.setEnabled(false);
					stepThrough.setText("Sorting finished... regenerate list to restart");
					startSort.setText("Sorting finished... regenerate list to restart");
				} else {
					plane.performTask(Task.NEXTSTEP);
				}
			}
			return;
		} 
		
		if (newE.equals(startSort)) {
			if (startSort.getText().equals("Pause")) {
				startSort.setText("Resume");
				stepThrough.setEnabled(true);
				plane.performTask(Task.TOGGLEPAUSE);
			} else if (startSort.getText().equals("Resume")) {
				startSort.setText("Pause");
				stepThrough.setEnabled(false);
				plane.performTask(Task.TOGGLEPAUSE);
				if (plane.sort()) {
					stepThrough.setEnabled(false);
					startSort.setEnabled(false);
					stepThrough.setText("Sorting finished... regenerate list to restart");
					startSort.setText("Sorting finished... regenerate list to restart");
				}
			} else {
				boolean beginSort = startSort.getText().equals("Start Sorting");
				stepThrough.setText("Next Step");
				stepThrough.setEnabled(plane.isPaused());
				if (plane.isPaused()) {
					startSort.setText("Resume");
				} else {
					startSort.setText("Pause");
				}
				
				if (beginSort) {
					switch (currentSort) {
					case "selectionSort": {
						plane.performTask(Task.SELECTION);
						break;
					}
					case "quickSort": {
						plane.performTask(Task.QUICK);
						break;
					}
					case "mergeSort": {
						plane.performTask(Task.MERGE);
						break;
					}
					case "shellSort": {
						plane.performTask(Task.SHELL);
						break;
					}
					case "insertionSort": {
						plane.performTask(Task.INSERTION);
						break;
					}
					case "bubbleSort": {
						plane.performTask(Task.BUBBLE);
						break;
					}
					}
				}
				if (!plane.isPaused())
					startSort.setText("Pause");
				if (plane.sort()) {
					stepThrough.setEnabled(false);
					startSort.setEnabled(false);
					stepThrough.setText("Sorting finished... regenerate list to restart");
					startSort.setText("Sorting finished... regenerate list to restart");
				}
			}
		}
	}

	/**
	 * Used when the set size button is clicked. Prompts the user to enter a size
	 * select how to order the rectangles.
	 */
	private void changeSize() {
		//setup pop=up GUI
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		checkBoxes = new JCheckBox[3];
		formatter.setValueClass(Integer.class);
		formatter.setMaximum(MAXRECTANGLES);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		JCheckBox permute = new JCheckBox();
		permute.addActionListener(this);
		permute.setSelected(true);
		JCheckBox sorted = new JCheckBox();
		sorted.addActionListener(this);
		JCheckBox reverseSorted = new JCheckBox();
		reverseSorted.addActionListener(this);
		checkBoxes[0] = permute;
		checkBoxes[1] = sorted;
		checkBoxes[2] = reverseSorted;
		JFormattedTextField sizeField = new JFormattedTextField(formatter);
		int currSize = plane.getCount();
		if(currSize==0)
			currSize = 15;
		sizeField.setText(""+currSize);
		Object[] inputField = { "Enter the size (2-" + MAXRECTANGLES + "): ", sizeField, "Generate Permuted? ", 
				permute, "Generate sorted? ", sorted, "Generate reverse-sorted? ", reverseSorted };
		int option = JOptionPane.showConfirmDialog(null, inputField, "How many items?", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		
		//parse the selected size
		if(option != JOptionPane.OK_OPTION || sizeField.getText().isBlank()) {
			plane.setSize(15);
		} else {
			plane.setSize(Integer.parseInt(sizeField.getText()));
		}
		checkBoxes = null;
		if(permute.isSelected())
			plane.performTask(Task.PERMUTE);
		else if(reverseSorted.isSelected())
			plane.performTask(Task.BUILDDESCENDING);
		else
			plane.performTask(Task.BUILDASCENDING);
		plane.killSort(); //end current sort
		startSort.setText("Start Sorting");
		stepThrough.setText("Disable Step by Step");
		startSort.setEnabled(true);
		stepThrough.setEnabled(true);
	}
	/**
	 * Used when the set delay button is clicked. Prompts the user to enter a delay
	 * to put on the sorting speed
	 */
	private void changeDelay() {
		// setup pop-up GUI
		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMaximum(9999);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		JFormattedTextField delayField = new JFormattedTextField(formatter);
		delayField.setText(""+plane.getDelay());
		Object[] inputField = { "Enter the delay (in millis) between each sort step: ", delayField};
		int option = JOptionPane.showConfirmDialog(null, inputField, "Set Speed", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		//parse selected delay
		if (option == JOptionPane.OK_OPTION && !delayField.getText().isBlank()) {
			plane.setDelay(Integer.parseInt(delayField.getText()));
		} 
	}
}
