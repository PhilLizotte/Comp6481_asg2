// -----------------------------------------------------
// Assignment 2
// COMP 6481
// Written by: Philippe Lizotte 40261140, Sami Khalayli 40327380
// Due Date: March 6th, 2026
// -----------------------------------------------------

package Exc;

/**
* A custom exception thrown when an invalid year is found (i.e. outside of some range, depending on context).
* 
* @author Philippe Lizotte
*/
public class BadYearException extends Exception {
	/**
	* Simple default constructor
	*/
	public BadYearException() {
		super("Incorrect year format! Only years in the range [1995-2024] inclusive are accepted.");
	}
	
	/**
	* Simple constructor with custom message.
	* 
	* @param message custom message to be displayed
	*/
	public BadYearException(String message) {
		super(message);
	}
}