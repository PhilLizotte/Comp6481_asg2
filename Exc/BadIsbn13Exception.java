package Exc;

/**
* A custom exception thrown when an invalid 13-digit ISBN is found.
* 
* @author Philippe Lizotte
*/
public class BadIsbn13Exception extends Exception {
	/**
	* Simple default constructor
	*/
	public BadIsbn13Exception() {
		super("Incorrect 13-digit ISBN format! ISBN must follow convention.");
	}
	
	/**
	* Simple constructor with custom message.
	* 
	* @param message custom message to be displayed
	*/
	public BadIsbn13Exception(String message) {
		super(message);
	}
}