import java.io.*;

import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import Exc.*;

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
		String inputDir = "input/"; //fix: OS neutral
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

						boolean syntaxOk = true;   //edit: new flag so bad records don't go to genre files
						String genreLocal = null;  //edit: store genre only if record is valid
						
						// Count the number of double-quotes to ensure proper quote formatting
						charCounter = 0;
						for (int j = 0; j < record.length(); j++) {
							if (record.charAt(j) == '\"') { // fixed: wrong index
								charCounter++;
							}
						}
						//edit: Dont write this record to genre outputs
						if (charCounter % 2 == 1) {
							try {
								throw new TooFewFieldsException(record); // classify as syntax err
							} catch (SyntaxException e) {
								syntaxOk = false;
								anySyntaxErrors = true;
								errorBuilder.append(e.getMessage()).append("\n\n");
							}
							continue;
						}
						
						// Regex ensures that only commas proceeded by an even number of
						//   double quotes serve as field separators.
						// The second argument (-1) ensures that trailing empty strings
						//   (blank year fields) are not discarded.
						recordFields = record.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

						// Fixed: trim
						for (int j = 0; j < recordFields.length; j++) {
							recordFields[j] = recordFields[j].trim();
						}

						// Look for syntax errors
						try {
							if (recordFields.length > 6) {
								throw new TooManyFieldsException(record);
							} else if (recordFields.length < 6) {
								// throw new TooFewFieldsException(arrayToString(recordFields));
								throw new TooFewFieldsException(record);
							}

							// change equals to isEmpty since we trimmed already
							for (int j = 0; j < recordFields.length; j++) {
								if (recordFields[j].isEmpty())
									throw new MissingFieldException(fieldNames[j], record);
							}

							genreLocal = recordFields[4]; // edit: use local var helps with the general structure of parsing
							isValidGenre = false;
							for (int j = 0; j < genres.length; j++) {
								if (genreLocal.equals(genres[j])) { //edit : updated var
									isValidGenre = true;
									break;
								}
							}
							if (!isValidGenre) {
								throw new UnknownGenreException(record);
							}
							
						} catch(SyntaxException e) {
							syntaxOk = false; // fix: mark invalid; don't write to genre output
							anySyntaxErrors = true;
							errorBuilder.append(e.getMessage() + "\n\n");
						}
						// edit: write only syntactically correct records. Clearer structure
						if (syntaxOk) {
							StringBuilder b = builders.get(genreLocal);
							if (b.length() != 0) b.append("\n");
							b.append(record);
						}
					}
					// edit: append file's error block once
					if (anySyntaxErrors) {
						StringBuilder errOut = builders.get("syntaxErrors");
						if (errOut.length() != 0) errOut.append("\n");
						errOut.append(errorBuilder.toString());
					}

					// edit: for debug purposes, print the block once per file
					if (anySyntaxErrors) System.out.print(errorBuilder.toString());
					
					s.close();
				} catch (FileNotFoundException e) {
					System.out.println(inputDir + bfNames[i] + " does not exist. Moving on to the next file...");
				}
			}
			
			String outputPath = "part1_output/"; // os neutral fix
			new File(outputPath).mkdirs(); //edit: create output folder if it doesn't exist; prevents errors
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
	 * Part 2
	 * -
	 * Reads 8 genre CSV files produced in Part 1 (assuming its syntactically valid),
	 * validates each record for semantic correctness (price, year, ISBN-10/ISBN-13) and
	 * writes semantic errors to {@code semantic_error_file.txt}.
	 *
	 * For each genre CSV file, this method performs 2 passes:
	 *  Pass 1: counts how many semantically valid records exist (and logs invalid ones).</li>
	 *  Pass 2: allocates 1 {@code Book[]} of the exact required size, fills it with valid
	 *       {@code Book} objects and serializes the array into a {@code .ser} file.</li>
	 *
	 * Output {@code .ser} files are written to the {@code part2_output/} directory.
	 * If a genre CSV file is missing then an empty {@code Book[]} is serialized for that file.
	 *
	 * @author Sami Khalayli
	 */
	public static void do_part2() {
		//Hardcode filenames from part 1

		String[] part1GenreCsvFiles = {
				"Cartoons_Comics.csv",
				"Hobbies_Collectibles.csv",
				"Movies_TV_Books.csv",
				"Music_Radio_Books.csv",
				"Nostalgia_Eclectic_Books.csv",
				"Old_Time_Radio.csv",
				"Sports_Sports_Memorabilia.csv",
				"Trains_Planes_Automobiles.csv"
		};

		String inputDir = "part1_output/";
		//Assuming 8 genres
		Scanner sc = null;
		PrintWriter semErr = null;
		String record;
		String[] recordFields;
		String outputDir = "part2_output/";

		try {

			new File(outputDir).mkdirs(); // output folder exists



			String semanticErrPath = outputDir + "semantic_error_file.txt";
			semErr = new PrintWriter(semanticErrPath);

			// ----------------- PASS 1 -----------------
			// Used to check how many valid Book will be stored

			for (int i = 0; i < part1GenreCsvFiles.length; i++) {

				String inName = part1GenreCsvFiles[i];
				String inPath = inputDir + inName;
				String serPath = outputDir + inName + ".ser";
				int validCount = 0;
				boolean doneHeader = false;
				// Pass 1 scope try; flow continues after pass 1
				try {
				sc = new Scanner(new FileReader(inPath));
				//read till EOF (Each file)
				while (sc.hasNextLine()) {
					record = sc.nextLine();
					if (record == null || record.trim().isEmpty()) continue;

					// records to clean fields
					// [0] = title
					// [1] = authors
					// [2] = price
					// [3] = isbn
					// [4] = genre
					// [5] = year
					try {

						recordFields = record.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
					for (int j = 0; j < recordFields.length; j++) {
						recordFields[j] = recordFields[j].trim();
					}
						if (recordFields.length != 6) {
							if (!doneHeader) {
								semErr.print("semantic error in file: " + inName + "\n====================\n");
								doneHeader = true;
							}
							semErr.print("Error: unexpected field count (expected 6, got " + recordFields.length + ")\n");
							semErr.print("Record: " + record + "\n\n");
							continue;
						}

						// Create Book, semantic validation happens in book class
						Book book = new Book(
								recordFields[0],                           // title
								recordFields[1],                           // authors
								Double.parseDouble(recordFields[2]),       // price
								recordFields[3],                           // isbn
								recordFields[4],                           // genre
								Integer.parseInt(recordFields[5])          // year
						);
						validCount++;
					} catch (BadPriceException | BadYearException | BadIsbn10Exception | BadIsbn13Exception e) {
						if (!doneHeader) {
							semErr.print("semantic error in file: " + inName + "\n====================\n");
							doneHeader = true;
						}
						semErr.print("Error: " + e.getMessage() + "\n");
						semErr.print("Record: " + record + "\n\n");

					} catch (NumberFormatException e) {
						if (!doneHeader) {
							semErr.print("semantic error in file: " + inName + "\n====================\n");
							doneHeader = true;
						}
						semErr.print("Error: invalid number format (price/year)\n");
						semErr.print("Record: " + record + "\n\n");
					}

				}
					// good for long runs
					semErr.flush();

				} catch (FileNotFoundException e) {
					System.out.println("File not found (skipping + serialize empty): " + inPath);
					validCount = 0;

				} finally {
					if (sc != null) sc.close();
                }

				System.out.println("Pass 1 validCount for " + inName + " = " + validCount);

				// ----------------- PASS 2 -----------------
				// Store the books and serialize
				Scanner sc2 = null;
				Book[] bkAry = null;
				try {
				sc2 = new Scanner(new FileReader(inPath));
				bkAry = new Book[validCount];
				int booksAdded = 0;

					while (sc2.hasNextLine()) {
						record = sc2.nextLine();
						if (record == null || record.trim().isEmpty()) continue;

						// records to clean fields
						// [0] = title
						// [1] = authors
						// [2] = price
						// [3] = isbn
						// [4] = genre
						// [5] = year
						try {

							recordFields = record.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
							for (int j = 0; j < recordFields.length; j++) {
								recordFields[j] = recordFields[j].trim();
							}
							if (recordFields.length != 6) {
								continue;
							}

							// Create Book, semantic validation happens in book class
							Book book = new Book(
									recordFields[0],                           // title
									recordFields[1],                           // authors
									Double.parseDouble(recordFields[2]),       // price
									recordFields[3],                           // isbn
									recordFields[4],                           // genre
									Integer.parseInt(recordFields[5])          // year
							);
							if (booksAdded < bkAry.length) {   //guard
								bkAry[booksAdded] = book;
								booksAdded++;
							}
							if (booksAdded == validCount){
								System.out.println("WARNING: books ary is full. There may be output truncated if you see this more than once per file.");
							}

						} catch (BadPriceException | BadYearException | BadIsbn10Exception | BadIsbn13Exception | NumberFormatException e) {
						// skip (already logged in pass 1)
					}

				}


				} catch (FileNotFoundException e) {
					System.out.println("File not found in pass2: " + inPath + " (serializing empty array)");
					bkAry = new Book[0]; // serialize empty ary


				} finally {
					if (sc2 != null) sc2.close();
				}

				ObjectOutputStream oos = null;

				try {
					oos = new ObjectOutputStream(new FileOutputStream(serPath));
					oos.writeObject(bkAry);

				} catch (IOException e) {
					System.out.println("[Serialization] Error in OOS." + e.getMessage() + " exiting.");
					System.exit(0);
				}
				finally {
					if (oos != null) try { oos.close(); } catch (IOException e) {}
				}
	}
			semErr.close();

		} catch(IOException e)
		{
			System.out.println("IOEXCEPTION" + e.getMessage() + "exiting.. ");
			System.exit(0);
		}
	}



					/**
                    * Main function. Only calls the main "parts" of the driver.
                    */
	public static void main(String[] args) {
		do_part1();
		do_part2();
	}
}