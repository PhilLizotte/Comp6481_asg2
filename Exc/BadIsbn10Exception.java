package Exc;

public class BadIsbn10Exception extends Exception {
	public BadIsbn10Exception() {
		super("Incorrect 10-digit ISBN format! ISBN must follow convention.");
	}
	
	public BadIsbn10Exception(String message) {
		super(message);
	}
}