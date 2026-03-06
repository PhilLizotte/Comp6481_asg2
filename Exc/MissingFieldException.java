// -----------------------------------------------------
// Assignment 2
// COMP 6481
// Written by: Philippe Lizotte 40261140, Sami Khalayli 40327380
// Due Date: March 6th, 2026
// -----------------------------------------------------

package Exc;

/**
* A custom exception thrown when one of the fields in a csv line is blank.
* 
* @author Philippe Lizotte
*/
public class MissingFieldException extends SyntaxException {
	/**
	* Simple default constructor
	*/
	public MissingFieldException() {
		super("One or more fields are blank in this entry! Entries must all be non-blank.");
	}
	
	/**
	* Constructor that puts the missing field type and the record in the message for display purposes.
	* 
	* @param missingField the first field in the record that was found to be blank
	* @param record the record that caused this exception to be thrown
	*/
	public MissingFieldException(String missingField, String record) {
		super("Missing " + missingField, record);
	}
}