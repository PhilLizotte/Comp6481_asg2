package Exc;

public class TooFewFieldsException extends SyntaxException {
	public TooFewFieldsException() {
		super("Too few fields in this book entry! Books can only contain exactly 6 fields.");
	}
	
	public TooFewFieldsException(String record) {
		super("Too few fields", record);
	}
}