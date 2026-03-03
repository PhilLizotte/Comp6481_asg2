package Exc;

/**
* A custom exception thrown when an invalid 10-digit ISBN is found.
* 
* @author Philippe Lizotte
*/
public class BadIsbn10Exception extends SemanticException {
	/**
	* Simple default constructor
	*/
	public BadIsbn10Exception() {
		super("Incorrect 10-digit ISBN format! ISBN must follow convention.");
	}
	
	/**
	* Simple constructor with custom message.
	* 
	* @param message custom message to be displayed
	*/
	public BadIsbn10Exception(String message) {
		super(message);
	}
}