// -----------------------------------------------------
// Assignment 2
// COMP 6481
// Written by: Philippe Lizotte 40261140, Sami Khalayli 40327380
// Due Date: March 6th, 2026
// -----------------------------------------------------

package Exc;

/**
* A custom exception thrown when the <i>genre</i> field
* does not match any entry in the existing list of genres.
* 
* @author Philippe Lizotte
*/
public class UnknownGenreException extends SyntaxException {
	/**
	* Simple default constructor
	*/
	public UnknownGenreException() {
		super("The genre field does not match any currently valid genre.");
	}
	
	/**
	* Constructor that puts the record in the message for display purposes.
	* 
	* @param record the record that caused this exception to be thrown
	*/
	public UnknownGenreException(String record) {
		super("Unknown genre", record);
	}
}