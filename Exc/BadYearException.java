package Exc;

public class BadYearException extends Exception {
	public BadYearException() {
		super("Incorrect year format! Only years in the range [1995-2024] inclusive are accepted.");
	}
	
	public BadYearException(String message) {
		super(message);
	}
}