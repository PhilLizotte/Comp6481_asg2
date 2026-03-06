// -----------------------------------------------------
// Assignment 2
// COMP 6481
// Written by: Philippe Lizotte 40261140, Sami Khalayli 40327380
// Due Date: March 6th, 2026
// -----------------------------------------------------

package Exc;

/**
* A custom exception type thrown while processing entries read from a .csv file.
* 
* @author Philippe Lizotte
*/
public class SemanticException extends Exception {
	/**
	* Simple default constructor
	*/
	public SemanticException() {
		super("A semantic error occured!");
	}
	
	/**
	* Simple constructor with custom message
	*
	* @param message custom message to be displayed
	*/
	public SemanticException(String message) {
		super(message);
	}
	
	
	/**
	* Simple constructor with custom message that specifies error type
	*
	* @param errorType string that specifies what type of error was thrown
	* @param message custom message to be displayed
	*/
	public SemanticException(String errorType, String record) {
		super("Error: " + errorType + "\nRecord: " + record);
	}
}