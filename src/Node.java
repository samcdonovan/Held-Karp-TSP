/**
 * Node.java
 * 
 * @author Samuel C. Donovan 
 * Created: 29/11/2021
 * Updated: 10/12/2021 
 * 
 * Node holds the subset along with its associated cost
 * and previous city. These are stored in the hash table, 
 * with the subset as their key.
 */
public class Node {

	int[] subset;
	double cost;
	int previousCity; /* the city that this subset comes from, 
						used for backtracking and forming a path */

	/**
	 * Node constructor
	 * 
	 * @param subset Subset of cities
	 * @param cost Associated cost with that subset
	 * @param previousCity City which this subset comes from
	 */
	public Node(int[] subset, double cost, int previousCity) {
		this.subset = subset;
		this.cost = cost;
		this.previousCity = previousCity;
	}

	/**
	 * toString override, prints out the subset as well as its cost and previous
	 * city
	 */
	@Override
	public String toString() {
		String output = "[";
		for (int pos = 0; pos < subset.length; pos++)
			output += subset[pos] + ", ";

		return output += "], cost= " + cost + ", prev city= " + previousCity;
	}
}
