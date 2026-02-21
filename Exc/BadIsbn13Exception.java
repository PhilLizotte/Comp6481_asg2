package Exc;

public class BadIsbn13Exception extends Exception {
	public BadIsbn13Exception() {
		super("Incorrect 13-digit ISBN format! ISBN must follow convention.");
	}
	
	public BadIsbn13Exception(String message) {
		super(message);
	}
}