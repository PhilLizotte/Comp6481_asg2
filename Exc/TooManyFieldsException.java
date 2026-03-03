package Exc;

/**
* A custom exception thrown when a csv line with too few fields is read.
* 
* @author Philippe Lizotte
*/
public class TooManyFieldsException extends SyntaxException {
	/**
	* Simple default constructor
	*/
	public TooManyFieldsException() {
		super("Too many fields in this book entry! Books can only contain exactly 6 fields.");
	}
	
	/**
	* Constructor that puts the record in the message for display purposes.
	* 
	* @param record the record that caused this exception to be thrown
	*/
	public TooManyFieldsException(String record) {
		super("Too many fields", record);
	}
}