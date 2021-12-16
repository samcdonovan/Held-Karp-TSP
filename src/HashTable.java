/**
 * HashTable.java
 * 
 * @author Samuel C. Donovan 
 * Created: 01/11/2021
 * Updated: 10/12/2021 
 * 
 * HashTable class, responsible for storing and retrieving 
 * subsets in relatively fast time (O(1) if there are few collisions)
 */
public class HashTable {

	int size;
	int total; /* total number of nodes currently in the table, 
				  used to check if a resize is required */
	Node[] table;

	/**
	 * HashTable constructor, sets the initial size of the hash 
	 * table and then fills the table with null
	 * 
	 * @param size Initial hash table size
	 */
	public HashTable(int size) {
		this.size = size;
		this.total = 0; /* sets total to 0 as nothing has yet been inserted */
		this.table = new Node[size];

		for (int pos = 0; pos < size; pos++)
			table[pos] = null; /* set every position in the table to null */
	}

	/**
	 * Creates a hash code for the given subset. Ideally this hash code would be unique
	 * to help avoid collisions but as the number of cities for the TSP increases,
	 * it becomes more and more unlikely that the hash codes will be unique. Originally,
	 * this hash function used bitshifting to try to generate a unique value but I found this
	 * caused many more collisions than the current method, which involves prime multiplication.
	 * 
	 * @param subset Subset to be hashed
	 * @return int Hash code of the subset
	 */
	public int hash(int[] subset) {

		long hashCode = 17; /* hash code starts at a prime, this helps increase uniqueness */
		int hashPrime = 31;

		/* for each value in the subset, multiple the hash code by hashPrime, 31.
		   Using primes has been found to increase uniqueness of hash functions */
		for (int pos = 0; pos < subset.length; pos++)
			hashCode += hashCode * hashPrime + subset[pos] + pos;

		/* the hashCode can get so large that it exceeds the long max, 
		   and so in order to make it a valid table position, it is multiplied 
		   by -1 if it is less than 0. This is a workaround and as such is not ideal. */
		if (hashCode < 0)
			hashCode *= -1;

		hashCode = Long.parseUnsignedLong("" + hashCode);

		/* mod by the size of the table to ensure that the hash code 
		   is within the range of the table size */
		hashCode = hashCode % size;

		/* the hash must be cast to an int because java primitive arrays do not
		   allow longs to be used as their size or indexes. this is not optimal
		   as it means there will be more collisions but does not drastically
		   affect the performance of this hash table so will suffice for this implementation */
		int hash = (int) hashCode;
		return hash;
	}

	/**
	 * Finds the position of a subset in the table. The method used for this is linear probing
	 * i.e. if the subset is not at the hash code value, it linearly searches from that hash code
	 * position until the subset is found. Whilst this may seem slow, with a good hash function
	 * there should be few collisions and so most subsets should be at their hash code value
	 * 
	 * @param subset Subset to search for
	 * @return int The position of the subset in the table. Returns 
	 * the size of the table (out of bounds) if the subset is not found 
	 */
	public int position(int[] subset) {
		int hashCode = hash(subset);
		int pos = hashCode; // set current index to the hash code of the subset

		/* probes through the table until the subset is found, then returns 
		   that index. Must run at least once. */
		do {

			if (table[pos] != null && arrayEquals(subset, table[pos].subset))
				return pos;

			/* if the end of the hash table has been reached, loop back round to the start of the table */
			if (pos == size - 1)
				pos = 0;
			else
				pos++;

		} while (table[pos] != null && pos != hashCode);

		return size; // returns out of bounds if subset not found
	}

	/**
	 * Search uses the position function to search for a subset
	 * 
	 * @param subset Subset to search for
	 * @return Node The node associated to the subset or null if it does not exist
	 */
	public Node search(int[] subset) {
		int pos = position(subset);

		/* if the index is out of bounds, the subset 
		   does not exist in the table */
		if (pos == size)
			return null;
		else
			return table[pos];
	}

	/** 
	 * Puts a new node into the table, uses linear probing if there is a collision
	 * 
	 * @param subset Subset of the new node
	 * @param cost Cost of the new node
	 * @param previousCity Previous city of the new node
	 */
	public void put(Node newNode) {

		int pos = hash(newNode.subset); // get hash code of new subset

		/* linear probe until an empty position is found or a subset 
		   that is equal to the new subset is found (this should not happen).
		   Only runs if position at the hash code of the new subsets is not empty */
		while (table[pos] != null && !arrayEquals(newNode.subset, table[pos].subset)) {

			if (pos == size - 1)
				pos = 0;
			else
				pos++;
		}

		if (table[pos] != null && arrayEquals(newNode.subset, table[pos].subset)) {
			table[pos].cost = newNode.cost; /* updates cost of subset if it
											 already exists in the table */
		} else {

			/* insert the new node at the position in the table */
			table[pos] = newNode;
			total++;

		}

		/* if the current total number of nodes is 50% of the size,
		   double the size of the table. This helps to avoid collisions and 
		   maintain near constant time for get and put */
		if (total >= size / 2)
			resize(size * 2);

	}

	/**
	 * Resizes the table based on the current total, helps with avoiding collisions.
	 * This is a costly operation and will drastically slow down the Held-Karp algorithm
	 * if it is used many times, but this can be avoided if the initial size is set appropriately
	 * 
	 * @param newSize New size of the hash table
	 */
	public void resize(int newSize) {
		Node[] tempTable = table; // store table in temporary array
		int[] tempSubset;
		double tempCost;
		int tempPreviousCity;
		
		int oldSize = size;

		/* create new table with the new size and fill it with null */
		table = new Node[newSize];
		for (int pos = 0; pos < newSize; pos++)
			table[pos] = null;

		this.size = newSize; /* set size to new size */

		/* fill new table with all of the nodes from the old table */
		for (int pos = 0; pos < oldSize; pos++) {
			if (tempTable[pos] != null) {

				tempSubset = tempTable[pos].subset;
				tempCost = tempTable[pos].cost;
				tempPreviousCity = tempTable[pos].previousCity;

				tempTable[pos] = null;

				put(new Node(tempSubset, tempCost, tempPreviousCity));
				total--; /* minus from total because the put function will increase the total */
			}
		}
	}

	/**
	 * Helper function that checks if two arrays are equal
	 * 
	 * @param originalArray The first array
	 * @param comparisonArray The array to compare to
	 * @return boolean True if the arrays have equal elements in the same positions, false otherwise
	 */
	public boolean arrayEquals(int[] originalArray, int[] comparisonArray) {

		if (originalArray.length != comparisonArray.length)
			return false; // if array lengths aren't equal, returns false

		int count = 0;

		for (int pos = 0; pos < originalArray.length; pos++) {

			/* add one to count if elements in the same position are equal */
			if (originalArray[pos] == comparisonArray[pos])
				count++;
		}

		/* returns true if count is equal to the length of the original array */
		return count == originalArray.length ? true : false;
	}

	/**
	 * toString override
	 * 
	 * @return String Contains all nodes in the table	
	 */
	@Override
	public String toString() {
		String output = "";

		for (int pos = 0; pos < size; pos++) {
			if (table[pos] != null)
				output += pos + " =  " + table[pos] + "\n";
		}

		return output;
	}

}
