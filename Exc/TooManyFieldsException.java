package Exc;

public class TooManyFieldsException extends SyntaxException {
	public TooManyFieldsException() {
		super("Too many fields in this book entry! Books can only contain exactly 6 fields.");
	}
	
	public TooManyFieldsException(String record) {
		super("Too many fields", record);
	}
}