package sortVisualizer;
/**
 * Sorts through a list one step at a time
 * allows for a sorting method to be paused/resumed 
 * while it is sorting
 */
public interface SortingIterator {
	/**
	 * performs the next step of the sorting method
	 */
	public void nextStep();
	/**
	 * Checks if the SortingIterator has another step
	 * @return true if there is another step, otherwise false
	 */
	public boolean hasNextStep();
}
