
import java.util.ArrayList;

/**
 * HeldKarp.java
 * 
 * @author Samuel C. Donovan 
 * Created: 20/10/2021 
 * Updated: 10/11/2021 
 * 
 * HeldKarp class, contains all necessary methods for this implementation of the
 * Held-Karp algorithm. This implementation is based on the pseudocode
 * found in Ayoub Abraich's paper: PROJET: PROBLÈME DU VOYAGEUR DE COMMERCE.
 */
public class HeldKarp {

	HashTable hashTable;
	double[][] distanceMatrix;
	ArrayList<Integer> set = new ArrayList<>();

	public HeldKarp(double[][] distanceMatrix) {
		this.distanceMatrix = distanceMatrix;

		/* if the number of cities is less than 15, the hash table size
		   is 1 bitshift by the number of cities, and then squared.
		   if the number of cities is more than 15, to avoid resizing,
		   the size is set to Integer.MAX - 2 billion. Whilst this is not 
		   a robust method for solving the issue of resizing, for the purposes
		   of this implementation, it ensures that for N > 15, the algorithm runs
		   in a relatively fast amount of time */
		int hashTableSize = distanceMatrix.length < 15 ? (1 << distanceMatrix.length) * (1 << distanceMatrix.length)
				: Integer.MAX_VALUE - 2000000000;

		this.hashTable = new HashTable(hashTableSize);

		/* add all of the cities into the set */
		for (int city = 1; city < distanceMatrix.length; city++)
			set.add(city);
	}

	/**
	 * This is the main function of the algorithm, which follows the steps of
	 * the pseudocode, as described above. This theoretical time complexity 
	 * of this algorithm is O(n^2 * 2n), so becomes impractical after about 20 cities.
	 * This implementation is a bottom-up approach, the smaller tasks are computed first and used
	 * to save time when computing the larger tasks later.
	 */
	public void solveTSP() {

		ArrayList<ArrayList<Integer>> subsets;
		int firstCity = 0;

		/* put all of the sets with 1 city in them into the hash table. 
		   Their costs are the cost from 0 to that city, and previous city is 0 */
		for (int city = 1; city < distanceMatrix.length; city++) {

			int[] currentSet = { city }; /* make a subset containing the current city */

			hashTable.put(new Node(currentSet, distanceMatrix[firstCity][city], firstCity));
		}

		/* this is the main loop of the algorithm, it directly follows the pseudocode.
		   it starts from subsetSize 2, as all subsets of size 1 were previously inserted
		   into the hash table */
		for (int subsetSize = 2; subsetSize < distanceMatrix.length; subsetSize++) {

			subsets = getSubsetsOfSize(subsetSize);

			/* it then loops through all subsets of size subsetSize */
			for (ArrayList<Integer> subset : subsets) {

				/* for each city in the subset, find the minimum cost combination/subset
				   for that city, and put it into the hash table. the first iterations of thse nested loops will 
				   be the most costly, calculating the minimum for a large number of combinations, but 
				   these calculations are used for subsequent subsets to drastically save time */
				for (int city : subset) {

					hashTable.put(findMinimumCostSet(city, subset));
				}
			}
		}
		/* after all subsets and combinations have been put into the hash table,
		   the first city is added back to the main set and backtracking is used to 
		   retrieve the shortest path and the best cost */
		set.add(0, firstCity);

		Node bestNode = findMinimumCostSet(0, set);
		int[] bestSubset = bestNode.subset;
		double bestCost = bestNode.cost;

		findBestPath(bestSubset);
		System.out.println("Cost = " + bestCost);
	}

	/**
	 * Calling function of the recursive subset function, passes the 
	 * subset size to that recursive function  
	 * 
	 * @param subsetSize The required size of the subsets
	 * @return ArrayList containing all of the subsets
	 */
	public ArrayList<ArrayList<Integer>> getSubsetsOfSize(int subsetSize) {
		ArrayList<ArrayList<Integer>> subsets = new ArrayList<>();
		getSubsetsRecursive(0, subsetSize, subsets, new ArrayList<>());
		return subsets;
	}

	/**
	 * Recursive function for finding all subsets of a given size. 
	 * Called in the getSubsetsOfSize() function.
	 * 
	 * @param nextPos Next position in the set; increases by one during every recursive call
	 * @param subsetSize Current subset size, used to ensure all subsets are of the input size
	 * @param subsets List containing all created subsets
	 * @param currentSubset The current subset that is being created
	 */
	public void getSubsetsRecursive(int nextPos, int subsetSize, ArrayList<ArrayList<Integer>> subsets,
			ArrayList<Integer> currentSubset) {

		/* if the current subsets' size is equal to the specified size, 
		   the subset is complete and can be added to the list of subsets */
		if (currentSubset.size() == subsetSize) {
			subsets.add(new ArrayList<>(currentSubset));
			return; // break out of this instance of the recursive call
		}

		/* for each index, this loop adds the current city into the current subset,
		   and then calls this method on the next city. Once that instance of the recursive 
		   call has reached the specified subset size, it finishes and this loop moves 
		   back onto the original recursive call, repeating until all subsets have been added */
		for (int currentPos = nextPos; currentPos < set.size(); currentPos++) {

			currentSubset.add(set.get(currentPos));

			/* add one to currentPos to get the next city */
			getSubsetsRecursive(currentPos + 1, subsetSize, subsets, currentSubset);

			/* remove the last city in the current subset to ensure that all subsets are the 
			   correct size, and to move onto the next city after all of the subsets that have
			   the current city as their first element have been created */
			currentSubset.remove(currentSubset.size() - 1);
		}
	}

	/**
	 * Creates all of the "combinations" of a given array. These combinations
	 * are created by getting an element from the array and setting it as the
	 * first element in the combination, then adding the rest of the elements to
	 * the combination (e.g. a combination for {1, 2, 4, 5} could be {4, 1, 2, 5}). 
	 * Aside from the first position, order does not matter.
	 * 
	 * @param array int array to get combinations for
	 * @return int[][] all combinations for the given array.
	 */
	public static int[][] combinations(int[] array) {
		int[][] combinations = new int[array.length][array.length];

		int currentPos = 0;
		for (int row = 0; row < array.length; row++) {
			currentPos = 0;

			/* set the first city of the current combination */
			combinations[row][currentPos] = array[row];

			for (int column = 0; column < array.length; column++) {
				/* if the current column is the same as the current row, 
				   continue to the next city */
				if (row == column)
					continue;

				/* add the rest of the cities to the current combination */
				combinations[row][++currentPos] = array[column];
			}
		}
		return combinations;
	}

	/**
	 * Finds the minimum cost of all combinations of a given subset, that lead to
	 * the specified city
	 * 
	 * @param city The destination city
	 * @param subset Subset to search through
	 * @return Node containing the subset, cost and previous city of the minimum subset
	 */
	public Node findMinimumCostSet(int city, ArrayList<Integer> subset) {

		Node hashNode;
		double cost = Double.POSITIVE_INFINITY, combinationCost;
		int previousCity = 0, firstCity = 0;

		ArrayList<Integer> tempSet = new ArrayList<Integer>(subset);

		/* remove destination city from subset and convert it to a primitive array.
		   this is the array that will be used to generate different combinations */
		tempSet.remove(Integer.valueOf(city));
		int[] setMinusCity = listToArray(tempSet);

		/* set the first position to the destination city, then convert it to a primitive array.
		   this is the subset that will be put into the hash table */
		tempSet.add(0, city);
		int[] currentSet = listToArray(tempSet);

		/* for all combinations of the subset without the current city in, 
		   find the combination with the lowest cost */
		int[][] combinations = combinations(setMinusCity);
		
		for (int[] combination : combinations) {

			hashNode = hashTable.search(combination);

			/* combination cost is equal to the cost of the subset in the hash table
			   plus the cost from the destination city in that subset to the destination city 
			   of the current set */
			combinationCost = hashNode.cost + distanceMatrix[hashNode.subset[0]][city];

			if (combinationCost < cost) {
				cost = combinationCost;
				previousCity = hashNode.subset[0];

				/* if the destination city is the first city in the path, 
				   current set is equal to this combination. This means that
				   the current set would be the best subset, and would have the 
				   optimum cost */
				if (city == firstCity)
					currentSet = combination;
			}
		}
		return new Node(currentSet, cost, previousCity);

	}

	/**
	 * Prints the best path using the final subset, and backtracks through the
	 * previous cities to form the shortest path.
	 * 
	 * @param finalSubset The final subset retrieved from the algorithm. This subset
	 * will contain all cities aside from the first city (0 in this case)
	 */
	public void findBestPath(int[] finalSubset) {
		int[] previousSubset;
		int[] currentSubset = finalSubset;
		int currentCity;

		/* print the path, starting from 0 (the paths always start from 0 in
		   this implementation), and then print the first city from the final subset */
		System.out.print("Path = 0 -> " + finalSubset[0] + " -> ");

		/* these loops handle the backtracking of Held-Karp; they search
		   the hash table for the current subset, retrieve its previous
		   city, remove that city from the current subset and repeat until 
		   the subset is empty */
		for (int size = finalSubset.length - 1; size > 0; size--) {

			/* find the current path and print its previous city to 
			   form part of the shortest path  */
			currentCity = hashTable.search(currentSubset).previousCity;
			System.out.print(currentCity + " -> ");

			previousSubset = currentSubset;

			currentSubset = new int[size]; // set currentSubset to an empty
											// array of the current size
			currentSubset[0] = currentCity;

			for (int index1 = 1, index2 = index1; index1 < currentSubset.length; index1++, index2++) {

				/* fill current subset with cities from previous subset, minus the current city */
				if (previousSubset[index2] == currentCity) {
					index1--;
					continue;
				}
				currentSubset[index1] = previousSubset[index2];
			}
		}
		System.out.print("0"); /* print 0 again to form a complete circuit in the path */
		System.out.println();
	}

	/**
	 * Helper function that converts an ArrayList to a primitive int array
	 * 
	 * @param listToConvert ArrayList to be converted
	 * @return int[] Converted int array
	 */
	public int[] listToArray(ArrayList<Integer> listToConvert) {
		int[] convertedArray = new int[listToConvert.size()];

		for (int pos = 0; pos < listToConvert.size(); pos++)
			convertedArray[pos] = listToConvert.get(pos);

		return convertedArray;
	}
}
