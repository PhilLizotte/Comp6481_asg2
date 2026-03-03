package Exc;

/**
* A custom exception thrown when a csv line with too many fields is read.
* 
* @author Philippe Lizotte
*/
public class TooFewFieldsException extends SyntaxException {
	/**
	* Simple default constructor
	*/
	public TooFewFieldsException() {
		super("Too few fields in this book entry! Books can only contain exactly 6 fields.");
	}
	
	/**
	* Constructor that puts the record in the message for display purposes.
	* 
	* @param record the record that caused this exception to be thrown
	*/
	public TooFewFieldsException(String record) {
		super("Too few fields", record);
	}
}