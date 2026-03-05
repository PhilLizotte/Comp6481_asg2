import java.io.Serializable;
import Exc.*;

// -----------------------------------------------------
// Assignment 2
// Part 1
// Written by: Philippe Lizotte - 40261140
// ----------------------------------------------------- 

// This is a class with 6 properties, each with getters and setters.
// - It also keeps track of how many books have been created in all.
// - Two books are considered equal if they have the same ISBN and price.
// - Also has a basic toString and equals override.

/**
* A Book class used mostly for storing book information.
* 
* @author Philippe Lizotte
*/
class Book implements Serializable {
    private String title;
	private String authors;
	private double price;
	private String ISBN;
	private String genre;
	private int year;
	
	private static int createdBooks = 0;
	
	/**
	* Simple constructor that assigns values to all properties.
	*/
	public Book(String title, String authors, double price, String ISBN, String genre, int year)
	throws BadPriceException, BadIsbn10Exception, BadIsbn13Exception, BadYearException {
		setTitle(title);
		setAuthors(authors);
		setPrice(price);
		setISBN(ISBN);
		setGenre(genre);
		setYear(year);
		
		createdBooks++;
	}
	
	/**
	* Simple copy constructor.
	*/
	public Book(Book that)
	throws BadPriceException, BadIsbn10Exception, BadIsbn13Exception, BadYearException {
		setTitle(that.getTitle());
		setAuthors(that.getAuthors());
		setPrice(that.getPrice());
		setISBN(that.getISBN());
		setGenre(that.getGenre());
		setYear(that.getYear());
		
		createdBooks++;
	}
	
	/**
	* Simple getter
	*
	* @return       title property
	*/
	public String getTitle() {
		return title;
	}
	
	/**
	* Simple setter
	*
	* @param       newTitle new title property
	*/
	public void setTitle(String newTitle) {
		this.title = newTitle;
	}
	
	/**
	* Simple getter
	*
	* @return       authors property
	*/
	public String getAuthors() {
		return authors;
	}
	
	/**
	* Simple setter
	*
	* @param       newAuthors new authors property
	*/
	public void setAuthors(String newAuthors) {
		this.authors = newAuthors;
	}
	
	/**
	* Simple getter
	*
	* @return       ISBN property
	*/
	public String getISBN() {
		return ISBN;
	}
	
	/**
	* Setter that validates ISBN entries
	*
	* @param       newISBN new ISBN property
	* @throws      BadIsbn10Exception
	* @throws      BadIsbn13Exception
	*/
	public void setISBN(String newISBN) throws BadIsbn10Exception, BadIsbn13Exception {
		
		String f_msg = "Incorrect 1%d-digit ISBN format! \"%s\" does not follow convention.";
		
		if (newISBN.length() == 10) {
			int sum = 0;
			for (int i = 0; i < 10; i++) {
				char c = newISBN.charAt(i);
				int digit;

				if (i == 9 && (c == 'X' || c == 'x')) { //fixed: X handling
					digit = 10;                 // ISBN10 digit X means 10
				} else if (c >= '0' && c <= '9') {
					digit = c - '0';
				} else {
					throw new BadIsbn10Exception("Invalid ISBN-10 character: " + c);
				}
				sum += (10 - i) * digit;               // fixed: weights 10->1
			}
			
			if (sum % 11 != 0) {
				throw new BadIsbn10Exception(String.format(f_msg, 0, newISBN));
			}
		} else if (newISBN.length() == 13) {
			int sum = 0;
			for (int i = 0; i < 13; i++) {
				int digit = newISBN.charAt(i) - '0';   // fixed: get numeric digit
				int weight = (i % 2 == 0) ? 1 : 3;     // weights 1,3,1,3 .....
				sum += weight * digit;
			}
			
			if (sum % 10 != 0) {
				throw new BadIsbn13Exception(String.format(f_msg, 3, newISBN));
			}
			
		} else {
			// If length isn't 10 or 13, just throw a BadIsbn10Exception
			throw new BadIsbn10Exception("Incorrect number of digits in " + newISBN + ".");
		}
		
		this.ISBN = newISBN;
	}
	
	/**
	* Simple getter
	*
	* @return       price property
	*/
	public double getPrice() {
		return price;
	}
	
	/**
	* Price setter that checks for valid price format.
	*
	* @param       newPrice new price property
	* @throws      BadPriceException
	*/
	public void setPrice(double newPrice) throws BadPriceException {
		if (newPrice < 0) {
			String f_msg = "Incorrect price format! %.2f must not be nonnegative.";
			throw new BadPriceException(String.format(f_msg, newPrice));
		}
		this.price = newPrice;
	}
	
	/**
	* Simple getter
	*
	* @return       genre property
	*/
	public String getGenre() {
		return genre;
	}
	
	/**
	* Simple setter
	*
	* @param       newGenre new genre property
	*/
	public void setGenre(String newGenre) {
		this.genre = newGenre;
	}
	
	/**
	* Simple getter
	*
	* @return       year property
	*/
	public int getYear() {
		return year;
	}
	
	/**
	* Year setter that only accepts years in the inclusive range [1995, 2024]
	*
	* @param       newYear new year property
	* @throws      BadYearException
	*/
	public void setYear(int newYear) throws BadYearException {
		if (newYear < 1995) {
			String f_msg = "Incorrect year format! %d must not be less than 1995 or greater than 2024.";
			throw new BadYearException(String.format(f_msg, newYear));
		}
		if (newYear > 2024) {
			String f_msg = "Incorrect year format! %d must not be less than 1995 or greater than 2024.";
			throw new BadYearException(String.format(f_msg, newYear));
		}
		this.year = newYear;
	}
	
	/**
	* Static function that returns the number of books created using a
	* value that increments whenever the constructor is called.
	*
	* @return      The number of books created up to this point.
	*/
	public static int findNumberOfCreatedBooks() {
		return createdBooks;
	}
	
	/**
	* Comparison function that returns true if the ISBN and price of both books are equal.
	*
	* @param       obj external Object (ideally a Book)
	* @return      boolean value, true if the books are equal.
	*/
	@Override
	public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != Book.class) return false;
		
		Book that = (Book) obj;
		return this.ISBN.equals(that.ISBN) && this.price == that.price; // edit: string .equals
	}
	
	/**
	* Simple toString override
	*
	* @return      A list of this object's properties, formatted for printing.
	*/
    @Override
    public String toString() {
		String f_string = "Author: %s\nTitle: %s\nPrice: %.2f\nISBN: %s\nGenre: %s\nYear: %d";
        return String.format(f_string, getAuthors(), getTitle(), getPrice(), getISBN(), getGenre(), getYear());
    }
}