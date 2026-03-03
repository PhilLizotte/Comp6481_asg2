package Exc;

/**
* A custom exception thrown when an invalid price value is found (i.e. price < 0).
* 
* @author Philippe Lizotte
*/
public class BadPriceException extends SemanticException {
	/**
	* Simple default constructor
	*/
	public BadPriceException() {
		super("Incorrect price format! Price must be nonnegative.");
	}
	
	/**
	* Simple constructor with custom message.
	* 
	* @param message custom message to be displayed
	*/
	public BadPriceException(String message) {
		super(message);
	}
}