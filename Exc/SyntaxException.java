package Exc;

public class SyntaxException extends Exception {
	public SyntaxException() {
		super("A syntax error occured!");
	}
	
	public SyntaxException(String message) {
		super(message);
	}
	
	public SyntaxException(String errorType, String record) {
		super("Error: " + errorType + "\nRecord: " + record);
	}
}