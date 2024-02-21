package sortVisualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.swing.JPanel;

/**
 * Handles the graphics/sorting operations for the sort visualizer.
 * 
 * @author Jacob Anderson
 * @version 02/20/2024
 */
public class DrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static int delay = 200;
	private ArrayList<Rectangle> rects;
	private Dimension area;
	private Graphics g;
	private boolean paused;
	private SortingIterator currentSort;

	public DrawPanel() {
		super();
		rects = new ArrayList<>();
		paused = true;
		currentSort = null;
	}

	/**
	 * Sets up the graphics and dimensions for this DrawPanel, this method must be
	 * called after the frame has been packed
	 */
	public void setGraphics() {
		g = getGraphics();
		area = this.getSize();
		this.setSize(area);
	}

	/**
	 * Sets the size (number of rectangles to sort) to a given amount
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		g.clearRect(0, 0, area.width, area.height);
		rects.clear();
		int incrementX = area.width / size;
		int incrementY = area.height / size;
		int currY = area.height - incrementY;
		for (int i = 0; i < size; i++) {
			rects.add(new Rectangle(i * incrementX, currY, incrementX, area.height - currY, Color.BLACK));
			rects.get(i).draw(g);
			currY -= incrementY;
		}
	}
	/**
	 * Returns the current number of rectangles
	 */
	public int getCount() {
		return rects.size();
	}

	/**
	 * Returns whether or not the sorting is in its paused mode
	 * 
	 * @return true if paused otherwise false
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Pauses the program for delay (variable) milliseconds
	 */
	private void sleep() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pauses sorting for a set number of milliseconds Recommended to use a multiple
	 * of the delay variable to make the program more consistent.
	 * 
	 * @param millis the number of milliseconds to pause for
	 */
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sets the delay variable to a user-selected value
	 * @param millis the new delay
	 */
	public void setDelay(int millis) {
		delay = millis;
	}
	/**
	 * Retrieves the current delay setting
	 * @return the current delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Sorts the rectangles until the program is paused or until the sorting has
	 * been completed
	 * 
	 * @return true if the sorting completed, false if sorting ended because the
	 *         program was paused
	 */
	public boolean sort() {
		while (!paused && currentSort.hasNextStep()) {
			sleep();
			if (currentSort == null)
				return false;
			currentSort.nextStep();
		}
		return !paused;
	}

	/**
	 * Determines whether or not there is another step remaining in the sort
	 * 
	 * @return true if the current sorting process has another step remaining false
	 *         if it does not or if there is no sort currently occurring.
	 */
	public boolean hasNextStep() {
		if (currentSort == null)
			return false;
		if (!currentSort.hasNextStep()) {
			currentSort = null;
			return false;
		}
		return true;
	}

	/**
	 * Kills the current sort
	 */
	public void killSort() {
		currentSort = null;
		paused = true;
	}

	/**
	 * Performs a given task. Used to toggle the pause attribute, set the currently
	 * selected sort, shuffle/build the list of rectangles, or perform the next step
	 * of the sorting process.
	 * 
	 * @param toDo the task to perform
	 */
	public void performTask(Task toDo) {
		switch (toDo) {
		case TOGGLEPAUSE:
			paused = !paused;
			break;
		case NEXTSTEP: {
			if (currentSort != null)
				currentSort.nextStep();
			break;
		}
		case BUBBLE:
			currentSort = new BubbleSort();
			break;
		case BUILDASCENDING:
			ascending();
			break;
		case BUILDDESCENDING:
			descending();
			break;
		case PERMUTE:
			shuffle();
			break;
		case INSERTION:
			currentSort = new InsertionSort();
			break;
		case MERGE:
			currentSort = new MergeSort();
			break;
		case QUICK:
			currentSort = new QuickSort();
			break;
		case SELECTION:
			currentSort = new SelectionSort();
			break;
		case SHELL:
			currentSort = new ShellSort();
			break;
		}
	}

	// **Sorting Methods**

	/**
	 * Swaps two rectangles
	 * 
	 * @param index1
	 * @param index2
	 */
	private void swap(int index1, int index2) {
		Rectangle r1 = rects.get(index1);
		Rectangle r2 = rects.get(index2);
		rects.set(index1, r2);
		rects.set(index2, r1);
		Rectangle.swap(r1, r2);
		r1.draw(g);
		r2.draw(g);

	}

	/**
	 * Scrambles the positions of the rectangles
	 */
	private void shuffle() {
		Random rng = new Random();
		for (int i = 0; i < rects.size(); i++) {
			swap(i, rng.nextInt(rects.size()));
		}
	}

	/**
	 * Organizes the rectangles in a descending order
	 */
	private void descending() {
		int size = rects.size();
		g.clearRect(0, 0, area.width, area.height);
		rects.clear();
		int incrementX = area.width / size;
		int incrementY = area.height / size;
		int currY = incrementY;
		for (int i = 0; i < size; i++) {
			Rectangle curr = new Rectangle(i * incrementX, currY, incrementX, area.height - currY, Color.BLACK);
			rects.add(curr);
			curr.draw(g);
			currY += incrementY;
		}
	}

	/**
	 * Organizes the rectangles in an ascending order
	 */
	private void ascending() {
		setSize(rects.size());
	}

	/**
	 * Compares the heights of two rectangles, functions like a Comparator
	 * 
	 * @param a rectangle one
	 * @param b rectangle two
	 * @return number<0 if a.height<b.height, 0 if equal or number>0 if
	 *         a.height>b.height
	 */
	private int cmp(int a, int b) {
		return rects.get(b).getHeight() - rects.get(a).getHeight();
	}

	/**
	 * Implements a ShellSort on the rectangles
	 */
	private class ShellSort implements SortingIterator {
		int gapSize;
		int index;

		public ShellSort() {
			gapSize = rects.size() / 2;
			index = gapSize;
		}

		@Override
		public void nextStep() {
			if (index >= rects.size()) {
				gapSize /= 2;
				for (Rectangle curr : rects)
					curr.setColor(Color.BLACK, g);
				index = gapSize;
				if (gapSize == 0) {
					index = rects.size();
					for (Rectangle curr : rects)
						curr.setColor(Color.GREEN, g);
				}
				return;
			}
			for (Rectangle curr : rects)
				if (curr.getColor().equals(Color.RED))
					curr.setColor(Color.BLUE, g);
			rects.get(index).setColor(Color.RED, g);
			int temp = index;
			for (int i = index - gapSize; i > -1; i -= gapSize) {
				if (cmp(i, index) > 0) {
					sleep(2 * delay);
					rects.get(i).setColor(Color.RED, g);
					swap(index, i);
					index = i;
				}
			}
			index = temp + 1;
		}

		@Override
		public boolean hasNextStep() {
			return gapSize != 0 || index < rects.size();
		}

	}

	/**
	 * Implements a SelectionSort on the rectangles
	 */
	private class SelectionSort implements SortingIterator {
		private int i;
		private int j;
		int smallIndex;

		public SelectionSort() {
			i = 0;
			j = 1;
			smallIndex = 0;
			rects.get(smallIndex).setColor(Color.RED, g);
		}

		@Override
		public void nextStep() {
			if (j == rects.size()) {
				rects.get(smallIndex).setColor(Color.GREEN, g);
				swap(i, smallIndex);
				i++;
				j = i + 1;
				smallIndex = i;
				if (smallIndex < rects.size())
					rects.get(smallIndex).setColor(Color.RED, g);
				for (Rectangle curr : rects)
					if (curr.getColor().equals(Color.BLUE))
						curr.setColor(Color.BLACK, g);
				return;
			}
			rects.get(j).setColor(Color.BLUE, g);
			if (cmp(smallIndex, j) > 0) {
				rects.get(smallIndex).setColor(Color.BLUE, g);
				rects.get(j).setColor(Color.RED, g);
				smallIndex = j;
				j++;
			} else {
				j++;
			}
		}

		@Override
		public boolean hasNextStep() {
			return i < rects.size();
		}

	}

	/**
	 * Implements a QuickSort on the rectangles with a random pivot selection
	 */
	private class QuickSort implements SortingIterator {
		private Stack<Pair> stk;
		private int start;
		private int end;
		private int firstIndLarger;
		int i;

		public QuickSort() {
			start = -1;
			end = -1;
			firstIndLarger = -1;
			i = -1;
			stk = new Stack<>();
			stk.add(new Pair(0, rects.size() - 1));
		}

		@Override
		public void nextStep() {
			if (start == -1) {
				Pair curr = stk.pop();
				start = curr.start;
				end = curr.end;
				if (start >= end) {
					rects.get(end).setColor(Color.GREEN, g);
					start = -1;
					return;
				}
				firstIndLarger = start;
				i = start;
				swap(end, (new Random()).nextInt(start, end + 1));
				rects.get(end).setColor(Color.RED, g);
				return;
			}
			if (i == end) {
				swap(end, firstIndLarger);
				rects.get(firstIndLarger).setColor(Color.GREEN, g);
				for (Rectangle curr : rects) {
					if (curr.getColor() != Color.GREEN)
						curr.setColor(Color.BLACK, g);
				}
				if (firstIndLarger + 1 < rects.size())
					stk.push(new Pair(firstIndLarger + 1, end));
				if (firstIndLarger != 0)
					stk.push(new Pair(start, firstIndLarger - 1));
				start = -1;
				return;
			}
			if (cmp(i, end) < 0) {
				rects.get(i).setColor(Color.CYAN, g);
				swap(i, firstIndLarger);
				firstIndLarger++;
			} else {
				rects.get(i).setColor(Color.PINK, g);
			}
			i++;
		}

		@Override
		public boolean hasNextStep() {
			return start != -1 || !stk.isEmpty();
		}

	}

	/**
	 * Implements an Iterative MergeSort on the rectangles
	 */
	private class MergeSort implements SortingIterator {
		private ArrayDeque<Pair> queue;
		private Rectangle[] mem;
		private int count;

		public MergeSort() {
			queue = new ArrayDeque<>();
			for (int i = 0; i < rects.size(); i++) {
				queue.add(new Pair(i, i));
			}
			mem = new Rectangle[rects.size()];
			count = queue.size();
		}

		@Override
		public void nextStep() {
			Pair first = queue.remove();
			Pair second;
			if (first.end + 1 != queue.peek().start) {
				second = queue.removeLast();
			} else {
				second = queue.remove();
			}
			Pair res = merge(first, second);
			queue.add(res);
			count -= 2;
			if (queue.size() == 1)
				for (Rectangle curr : rects)
					curr.setColor(Color.GREEN, g);
			else if (count <= 0) {
				for (Rectangle curr : rects) {
					curr.setColor(Color.BLACK, g);
				}
				count = queue.size();
			}
		}

		@Override
		public boolean hasNextStep() {
			return queue.size() > 1;
		}

		/**
		 * Merges the rectangles between two pairs and returns the result as a pair
		 * 
		 * @param one first pair
		 * @param two second pair
		 * @return merged pair
		 */
		private Pair merge(Pair one, Pair two) {
			int s1 = one.start;
			int s2 = two.start;
			int e1 = one.end;
			int e2 = two.end;
			int ind = 0;
			while (s1 <= e1 && s2 <= e2) {
				if (cmp(s1, s2) <= 0) {
					mem[ind] = rects.get(s1);
					s1++;
				} else {
					mem[ind] = rects.get(s2);
					s2++;
				}
				ind++;
			}
			while (s1 <= e1) {
				mem[ind] = rects.get(s1);
				s1++;
				ind++;
			}
			while (s2 <= e2) {
				mem[ind] = rects.get(s2);
				s2++;
				ind++;
			}
			int st = Math.min(one.start, two.start);
			for (int i = 0; i < ind; i++) {
				sleep((int) (1.5 * delay));
				Rectangle r1 = rects.get(st);
				Rectangle r2 = mem[i];
				r2.setColor(Color.RED, g);
				rects.set(st, Rectangle.mergeTwo(r2, r1, g));
				st++;
			}
			return new Pair(Math.min(one.start, two.start), Math.max(e1, e2));
		}
	}

	/**
	 * Class used to keep track of start and end indices in traditionally recursive
	 * methods. Start/End indices need to be stored in some structure since
	 * SortingIterator requires the sorting to be done iteratively.
	 */
	private static class Pair {
		public int start;
		public int end;

		public Pair(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

	/**
	 * Implements an InsertionSort on the rectangles
	 */
	private class InsertionSort implements SortingIterator {
		int i;
		int j;

		public InsertionSort() {
			i = 0;
			j = 1;
			rects.get(i).setColor(Color.GREEN, g);
			rects.get(j).setColor(Color.RED, g);

		}

		@Override
		public void nextStep() {
			if (j - 1 < 0 || cmp(j, j - 1) >= 0) {
				rects.get(j).setColor(Color.GREEN, g);
				i++;
				j = i + 1;
				if (j < rects.size())
					rects.get(j).setColor(Color.RED, g);
				return;
			}
			swap(j, j - 1);
			j--;
		}

		@Override
		public boolean hasNextStep() {
			return i < rects.size() - 1;
		}

	}

	/**
	 * Implements a BubbleSort on the rectangles
	 */
	private class BubbleSort implements SortingIterator {
		int i;
		int j;

		public BubbleSort() {
			i = 0;
			j = rects.size() - 1;
			rects.get(j).setColor(Color.RED, g);
		}

		@Override
		public void nextStep() {
			if (j == i) {
				i++;
				j = rects.size() - 1;
				rects.get(j).setColor(Color.RED, g);
				for (Rectangle curr : rects)
					if (curr.getColor().equals(Color.BLUE))
						curr.setColor(Color.BLACK, g);
				rects.get(i - 1).setColor(Color.GREEN, g);
				return;
			}
			if (cmp(j, j - 1) >= 0) {
				rects.get(j - 1).setColor(Color.RED, g);
				rects.get(j).setColor(Color.BLUE, g);
			} else {
				rects.get(j - 1).setColor(Color.BLUE, g);
				swap(j, j - 1);
			}
			j--;

		}

		@Override
		public boolean hasNextStep() {
			return i < rects.size();
		}

	}

	public enum Task {
		TOGGLEPAUSE, NEXTSTEP, PERMUTE, BUILDASCENDING, BUILDDESCENDING, BUBBLE, SELECTION, INSERTION, SHELL, QUICK,
		MERGE
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(550, 550);
	}
}
