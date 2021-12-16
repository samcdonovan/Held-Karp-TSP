# Held-Karp implementation for solving the TSP

This is my implementation of the Held-Karp dynamic programming algorithm. It is an exact algorithm, and as such, returns the optimal path and cost for a given data set.

## Why Held-Karp?
The problem for which this project was designed for required an exact solution for all n <= 20 in less than one minute.
This algorithm has a proposed time complexity of O(n^2 * 2n). For n = 20:   20^2 * (2*20) ≈ 16000, which is drastically lower than one minute. Though my implementation runs quite a bit slower than this, (for n = 20, it runs in approx. 20 seconds) it will still run in less than one minute for n <= 20, and will find the shortest path and its cost. 
For n > 20,  the running time starts to get exponentially larger. It becomes infeasible to run this implementation on n > 30, where the running time is several hours long. This is not necessarily due to an issue with my implementation, but is more of an issue with the algorithm and the TSP in general, as the TSP is an NP-Hard problem and as such, finding exact solutions becomes increasingly more expensive the larger n is.
