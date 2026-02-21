package Exc;

public class BadPriceException extends Exception {
	public BadPriceException() {
		super("Incorrect price format! Price must be nonnegative.");
	}
	
	public BadPriceException(String message) {
		super(message);
	}
}