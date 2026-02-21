package Exc;

public class MissingFieldException extends SyntaxException {
	public MissingFieldException() {
		super("One or more fields are blank in this entry! Entries must all be non-blank.");
	}
	
	public MissingFieldException(String missingField, String record) {
		super("Missing " + missingField, record);
	}
}