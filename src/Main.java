import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Main.java
 * 
 * @author Samuel C. Donovan
 * 
 * INSTRUCTIONS:
 * Line 25 searches for a file in the current user directory, where this project is being run.
 * To run this on different files, locate the String variable 'dataFile' below (line 25), 
 * and change the last section of text to the file name. It must include the file extension (i.e. .txt). 
 * Then, run the program and a distance matrix will be created from the data file, and the Held-Karp
 * algorithm will be run on that matrix, printing out the best path, cost and running time 
 * after it is finished.
 */
public class Main {

	public static void main(String[] args) {

		long startTime = System.nanoTime(); /* start the timer */

		String dataFile = System.getProperty("user.dir") + File.separator + "data" + File.separator + "test3-21.txt";
		System.out.println("Loading from " + dataFile);

		File file = new File(dataFile); /* create file object to use a scanner on */

		double[][] distanceMatrix = new double[0][0];

		try {
			/* convert the file to a string so it can be split by newlines */
			String fileString = new Scanner(file).useDelimiter("\\A").next();

			/* split the file on newline to get each line of data */
			String[] fileArray = fileString.split("\\n");

			/* distance matrix lengths are equal to the number of cities/lines in the file */
			distanceMatrix = new double[fileArray.length][fileArray.length];

			/* delimit the file by either spaces or tabs */
			String fileDelimiter = fileArray[0].contains(" ") ? " " : "\\t";

			String[] fromCityData, toCityData;

			System.out.println("File loaded.");

			for (int fromCity = 0; fromCity < fileArray.length; fromCity++) {

				/* split current line by the file delimiter, and replace any \n found.
				   this forms the data for the fromCities */
				fromCityData = fileArray[fromCity].trim().replaceAll("\n ", "").split(fileDelimiter);

				for (int toCity = 0; toCity < fileArray.length; toCity++) {

					/* do the same for the toCity's data, a nested loop is required
					   to get the data for both cities and calculate the distance between them */
					toCityData = fileArray[toCity].trim().replaceAll("\n ", "").split(fileDelimiter);

					/* if the fromCity and toCity are the same, the distance is infinity */
					if (fromCity == toCity)
						distanceMatrix[fromCity][toCity] = Double.POSITIVE_INFINITY;
					else
						/* otherwise, calculate the Euclidean distance for the two cities and 
						   put that distance into the distance matrix */
						distanceMatrix[fromCity][toCity] = Math.sqrt(Math
								.pow((Integer.parseInt(toCityData[1]) - Integer.parseInt(fromCityData[1])), 2)
								+ Math.pow((Integer.parseInt(toCityData[2]) - Integer.parseInt(fromCityData[2])), 2));
				}
			}

		} catch (FileNotFoundException fileNotFound) { /* if file is not found, stop the program */
			System.out.println("File not found at " + dataFile);
			return;
		} finally {

			/* construct a HeldKarp object on the distance matrix, and then call the solveTSP()
			   function which will find the best path and its cost */
			HeldKarp heldKarp = new HeldKarp(distanceMatrix);

			System.out.println("Running Held-Karp.\n");
			heldKarp.solveTSP();

			/* calculate running time of the algorithm */
			long endTime = System.nanoTime();
			long totalTime = endTime - startTime;

			double seconds = (double) totalTime / 1000000000.0;

			System.out.println("Running time = " + totalTime + " nano seconds, " + seconds + " seconds");
		}

	}

}
