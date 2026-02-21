package Exc;

public class UnknownGenreException extends SyntaxException {
	public UnknownGenreException() {
		super("The genre field does not match any currently valid genre.");
	}
	
	public UnknownGenreException(String record) {
		super("Unknown genre", record);
	}
}