package application;

import java.util.List;

/**
 * A data structure that can store at as many key,value pairs as needed.
 * 
 *
 * May not add any public members to ADT or your implementation.
 * 
 * 
 *
 * @param <K> The node to store
 */
public interface DataStructureADT {

	// Adds data to the data structure and increase the number of nodes.
	// If data is null, throw IllegalNullKeyException;
	// If data is already in data structure, replace value with new value
	void insert(List<String> data) throws IllegalNullKeyException;


	// Returns a list of the data in that node
	// Does not remove key or decrease number of keys
	// If index is not found, throw KeyNotFoundException().
	List<String> get(int index) throws IllegalNullKeyException, KeyNotFoundException;
	
	//Returns a list of all the data in the node with the corresponding farmerID
	List<String> getDataWithID(String farm_id);

	// Returns the number of nodes in the data structure
	int size();

}
