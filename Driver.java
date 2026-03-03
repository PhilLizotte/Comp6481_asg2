import java.io.Serializable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import Exc.SyntaxException;
import Exc.TooManyFieldsException;
import Exc.TooFewFieldsException;
import Exc.UnknownGenreException;
import Exc.MissingFieldException;

class Driver {
	// Helper functions
	public static <T> String arrayToString(T[] arr) {
		StringBuilder s_arr = new StringBuilder("[");
		for (int i = 0; i < arr.length; i++) {
			if (i != 0) s_arr.append(", ");
			s_arr.append("\"");
			s_arr.append(arr[i].toString());
			s_arr.append("\"");
		}
		s_arr.append("]");
		return s_arr.toString();
	}
	
	// -- Main Parts --
	
	/**
	* Part 1 of the driver.
	* Reads all books from a list of .csv files and outputs them
	* to files sorted by genre. Any records with syntax errors
	* are put in their own .txt file instead of their respective
	* genre file. A custom error is thrown and handled whenever
	* a record with a syntax error is read.
	* 
	* @author Philippe Lizotte
	*/
	public static void do_part1() {
		Scanner s;
		String inputDir = "input\\";
		String inputFileName = "part1_input_file_names.txt";
		
		int numBfs = 0;
		
		// Get main input file
		try {
			s = new Scanner(new FileReader(inputDir + inputFileName));
			numBfs = s.nextInt();
			s.nextLine();
		
			// Collect book file names
			String[] bfNames = new String[numBfs];
			for (int i = 0; i < numBfs; i++) {
				if (!s.hasNextLine()) break;
				bfNames[i] = s.nextLine();
			}
			
			s.close();
			
			// Read CSV files and collect their outputs by genre
			Map<String, StringBuilder> builders = new HashMap<>();
			String[] genres = {"CCB", "HCB", "MTV", "MRB", "NEB", "OTR", "SSM", "TPA"};
			for (int i = 0; i < genres.length; i++) {
				builders.put(genres[i], new StringBuilder());
			}
			builders.put("syntaxErrors", new StringBuilder());
			
			String record;
			String[] recordFields;
			String[] fieldNames = {"title", "author", "price", "ISBN", "genre", "year"};
			
			int charCounter;
			
			String genre;
			boolean isValidGenre;
			
			StringBuilder thisBuilder;
			
			StringBuilder errorBuilder;
			boolean anySyntaxErrors;
			
			// Iterate through book file names
			// TO-DO: Print the right stuff when an exception is thrown (see the specs)
			for (int i = 0 ; i < numBfs; i++) {
				try {
					s = new Scanner(new FileReader(inputDir + bfNames[i]));
					errorBuilder = new StringBuilder("syntax error in file: " + bfNames[i] + "\n====================\n");
					anySyntaxErrors = false;
					
					while (s.hasNextLine()) {
						// Process the record. First, make sure it has the right number of fields.
						record = s.nextLine();
						genre = "syntaxErrors";
						
						// Count the number of double-quotes to ensure proper quote formatting
						charCounter = 0;
						for (int j = 0; j < record.length(); j++) {
							if (record.charAt(i) == '\"') {
								charCounter++;
							}
						}
						if (charCounter % 2 == 1) {
							builders.get("syntaxErrors").append(record);
							continue;
						}
						
						// Regex ensures that only commas proceeded by an even number of
						//   double quotes serve as field separators.
						// The second argument (-1) ensures that trailing empty strings
						//   (blank year fields) are not discarded.
						recordFields = record.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
						
						// Look for syntax errors
						try {
							if (recordFields.length > 6) {
								throw new TooManyFieldsException(record);
							} else if (recordFields.length < 6) {
								// throw new TooFewFieldsException(arrayToString(recordFields));
								throw new TooFewFieldsException(record);
							}
							
							for (int j = 0; j < recordFields.length; j++) {
								if (recordFields[j].equals(""))
									throw new MissingFieldException(fieldNames[j], record);
							}
							
							genre = recordFields[4];
							isValidGenre = false;
							for (int j = 0; j < genres.length; j++) {
								if (genre.equals(genres[j])) {
									isValidGenre = true;
									break;
								}
							}
							if (!isValidGenre) {
								throw new UnknownGenreException(record);
							}
							
						} catch(SyntaxException e) {
							errorBuilder.append(e.getMessage() + "\n\n");
							anySyntaxErrors = true;
						}  finally {
							// Whether or not an exception was thrown, append the line
							//  to its stringbuilder by genre (or to the collection of line with syntax errors)
							if (!builders.containsKey(genre)) genre = "syntaxErrors";
							
							thisBuilder = builders.get(genre);
							
							if (thisBuilder.length() != 0) thisBuilder.append("\n");
							thisBuilder.append(record);
							
							if (anySyntaxErrors) System.out.print(errorBuilder.toString());
						}
					}
					
					s.close();
				} catch (FileNotFoundException e) {
					System.out.println(inputDir + bfNames[i] + " does not exist. Moving on to the next file...");
				}
			}
			
			String outputPath = "part1_output\\";
			Map<String, String> outputFileNames = new HashMap<>();
			outputFileNames.put("CCB", "Cartoons_Comics.csv");
			outputFileNames.put("HCB", "Hobbies_Collectibles.csv");
			outputFileNames.put("MTV", "Movies_TV_Books.csv");
			outputFileNames.put("MRB", "Music_Radio_Books.csv");
			outputFileNames.put("NEB", "Nostalgia_Eclectic_Books.csv");
			outputFileNames.put("OTR", "Old_Time_Radio.csv");
			outputFileNames.put("SSM", "Sports_Sports_Memorabilia.csv");
			outputFileNames.put("TPA", "Trains_Planes_Automobiles.csv");
			outputFileNames.put("syntaxErrors", "syntax_error_file.txt");
			
			for (String key: builders.keySet()) {
				try {
					FileWriter fw = new FileWriter(outputPath + outputFileNames.get(key));
					fw.write(builders.get(key).toString());
					fw.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("Input file " + inputDir + inputFileName + " does not exist! Exiting program...");
			System.exit(1);
		}
	}
	
	/**
	* Main function. Only calls the main "parts" of the driver.
	*/
	public static void main(String[] args) {
		do_part1();
	}
}