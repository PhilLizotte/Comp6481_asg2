// -----------------------------------------------------
// Assignment 2
// COMP 6481
// Written by: Philippe Lizotte 40261140, Sami Khalayli 40327380
// Due Date: March 6th, 2026
// -----------------------------------------------------

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
	
	public static <T> int findIndex(T elem, T[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(elem)) {
				return i;
			}
		}
		return -1;
	}
	
	public static void clearConsole() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
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
	* @author Sami Khalayli
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
			String[] genres = {"CCB", "HCB", "MTV", "MRB", "NEB", "OTR", "SSM", "TPA"};
			StringBuilder[] builders = new StringBuilder[genres.length + 1];
			for (int i = 0; i < genres.length; i++) {
				builders[i] = new StringBuilder();
			}
			builders[builders.length - 1] = new StringBuilder();
			
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
							StringBuilder b = builders[findIndex(genreLocal, genres)];
							if (b.length() != 0) b.append("\n");
							b.append(record);
						}
					}
					// edit: append file's error block once
					if (anySyntaxErrors) {
						StringBuilder errOut = builders[builders.length - 1];
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
			String[] outputFileNames = {
				"Cartoons_Comics.csv",
				"Hobbies_Collectibles.csv",
				"Movies_TV_Books.csv",
				"Music_Radio_Books.csv",
				"Nostalgia_Eclectic_Books.csv",
				"Old_Time_Radio.csv",
				"Sports_Sports_Memorabilia.csv",
				"Trains_Planes_Automobiles.csv",
				"syntax_error_file.txt",
			};
			
			for (int i = 0; i < builders.length; i++) {
				try {
					FileWriter fw = new FileWriter(outputPath + outputFileNames[i]);
					fw.write(builders[i].toString());
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
	* Part 3
	* Reads 8 binary files from part 2 and de-serializes them into object arrays.
	* 
	* Afterwards, an interactive book navigation system begins to run.
	*
	* The main menu gives three options.
	* 
	* The first option opens its own sub-menu. By entering a positive integer, 
	* the current record and the following (n-1) records are displayed. If
	* there are less than n records until the end of the file, then every
	* record from the current record to the end of the file are displayed with
	* an indicative message that the end of the file has been reached. By
	* entering a negative integer, the current record
	* and the preceeding (|n|-1) records are displayed. Similarly, if there
    * aren't enough records preceeding the current one, all possible records
	* are displayed with an indicitive message that the beginning of the file
	* has been reached. In both cases, the final record displayed becomes the
	* new current record for future prompts. Entering 0 will return to the main
	* menu.
	*   
	* The second option displays a numbered list of all of the available files
	* to browse. When the user enters a number, the book array extracted from
	* the selected file will be used for future record browsing until the file
	* is changed again.
	* 
	* The third option exits the program.
	*/
	public static void do_part3(Scanner consoleScanner) {
		// First, deserialize the binary book array objects from part 2 and store them in a 2D array.
		
		String inputDir = "part2_output/";
		String[] inputFiles = {
			"Cartoons_Comics.csv.ser",
			"Hobbies_Collectibles.csv.ser",
			"Movies_TV_Books.csv.ser",
			"Music_Radio_Books.csv.ser",
			"Nostalgia_Eclectic_Books.csv.ser",
			"Old_Time_Radio.csv.ser",
			"Sports_Sports_Memorabilia.csv.ser",
			"Trains_Planes_Automobiles.csv.ser"
		};
		
		ObjectInputStream ois = null;
		
		Book[][] validBookCollection = new Book[inputFiles.length][];
		
		for (int i = 0; i < inputFiles.length; i++) {
			// Catch exceptions in case anything goes wrong
			try {
				ois = new ObjectInputStream(new FileInputStream(inputDir + inputFiles[i]));
				validBookCollection[i] = (Book[])ois.readObject();
				
			} catch (IOException | ClassNotFoundException e) {
				System.out.println(e.getMessage());
				System.exit(0);
			} finally {
				if (ois != null) try { ois.close(); } catch (IOException e) {}
			}
		}
		
		for (int i = 0; i < validBookCollection.length; i++) {
			System.out.println("Genre " + i + ": " + validBookCollection[i].length + " books");
		}
		
		// Then, do a menu loop
		// - i.e. continuously prompt the user with choices until they exit the menu.
		String consoleInput;
		int numInput = 0;
		boolean mainExit = false;
		
		int currentFileIndex = 0;
		String currentFile;
		Book[] genreCollection;
		
		while (!mainExit) {
			currentFile = inputFiles[currentFileIndex];
			genreCollection = validBookCollection[currentFileIndex];
			
			clearConsole();
			System.out.println("-----------------------------");
			System.out.println("          Main Menu          ");
			System.out.println("-----------------------------");
			System.out.println("v View the selected file: " + currentFile + " (" + genreCollection.length + " records)");
			System.out.println("s Select a file to view");
			System.out.println("x Exit");
			System.out.println("-----------------------------");
			System.out.print("Enter Your Choice: ");
			
			consoleInput = consoleScanner.nextLine().toLowerCase();
			
			switch (consoleInput) {
				case "v":
					// Record navigation menu loop
					clearConsole();
					
					boolean recordExit = false;
					
					int recordIndex = 0;
					int newIndex;
					int startIndex;
					int endIndex;
					
					while (!recordExit) {
						System.out.println("viewing: " + currentFile + " (" + genreCollection.length + " records)");
						System.out.println("Current record: " + (recordIndex + 1));
						System.out.println("Enter a positive integer to view the current record and the following n - 1 records.");
						System.out.println("Enter a negative integer to view the current record and the preceeding |n| - 1 records.");
						System.out.println("Enter 0 to return to the main menu.");
						consoleInput = consoleScanner.nextLine();
						
						clearConsole();
						try {
							numInput = Integer.parseInt(consoleInput);
							
							if (numInput == 0) {
								// Exit record sub-menu
								recordExit = true;
							} else {
								if (numInput > 0) {
									// Iterate forwards through file records if n > 0
									newIndex = recordIndex + numInput - 1;
									
									while (recordIndex <= newIndex && recordIndex < genreCollection.length) {
										System.out.println(genreCollection[recordIndex] + "\n");
										recordIndex++;
									}
									recordIndex--;
									
									if (recordIndex == genreCollection.length - 1) System.out.println("EOF has been reached\n");
								} else {
									// Iterate backwards through file records if n < 0
									newIndex = recordIndex + numInput + 1;
									
									while (recordIndex >= newIndex && recordIndex >= 0) {
										System.out.println(genreCollection[recordIndex] + "\n");
										recordIndex--;
									}
									recordIndex++;
									
									if (recordIndex == 0) System.out.println("BOF has been reached\n");
								}
								System.out.print("Press Enter to continue...");
								consoleScanner.nextLine();
								clearConsole();
							}
							
						} catch (NumberFormatException e) {
							// Handle non-integer inputs
							System.out.println("Please enter an integer.");
							System.out.print("Press Enter to continue...");
							consoleScanner.nextLine();
							clearConsole();
						}
					}
					break;
				case "s":
					// File selection menu
					boolean fileSelectExit = false;
					
					while (!fileSelectExit) {
						clearConsole();
						System.out.println("------------------------------");
						System.out.println("        File Sub-Menu         ");
						System.out.println("------------------------------");
						// print all files as choices
						for (int i = 0; i < inputFiles.length; i++) {
							System.out.println((i + 1) + " " + inputFiles[i] + " (" + validBookCollection[i].length + " records)");
						}
						System.out.println((inputFiles.length + 1) + " Exit");
						System.out.println("------------------------------");
						System.out.print("Enter your choice: ");
						
						consoleInput = consoleScanner.nextLine();
						try {
							// This throws a NumberFormatException for non-integer inputs
							numInput = Integer.parseInt(consoleInput);
							if (numInput <= 0 || numInput > (inputFiles.length + 1))  {
								// Handle inputs out of range
								throw new Exception();
							}
							// Exit menu if valid choice was made
							fileSelectExit = true;
							
						} catch (Exception e) {
							// Handle invalid inputs
							clearConsole();
							System.out.println("Please select an option by entering one of the numbers.");
							System.out.print("Press Enter to continue...");
							consoleScanner.nextLine();
							clearConsole();
						}
					}
					
					if (numInput != (inputFiles.length + 1)) {
						// Change current file if the user did not select the "exit" option
						currentFileIndex = numInput - 1;
					}

					break;
				case "x":
					// leave the menu
					mainExit = true;
					break;
				default:
					// handle invalid inputs
					clearConsole();
					System.out.println("Please select an option: v, s, or x.");
					System.out.print("Press Enter to continue...");
					consoleScanner.nextLine();
					clearConsole();
			}
		}
		
		clearConsole();
	}

	/**
	* Main function.
	* 
	* Calls the "main" parts of the driver.
	* Also contains some console commands to make the application more intuitive for a user.
	*/
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		
		clearConsole();
		System.out.println("Welcone to this book validator and browser!");
		System.out.println("Developped by Philippe Lizotte and Sami Khalayli");
		s.nextLine();
		clearConsole();
		
		do_part1();
		System.out.println("\nPart 1 completed!\n");
		System.out.print("Press Enter to continue...");
		s.nextLine();
		clearConsole();
		
		do_part2();
		System.out.println("\nPart 2 completed!\n");
		System.out.print("Press Enter to continue...");
		s.nextLine();
		clearConsole();
		
		do_part3(s);
		System.out.println("Thank you for using this book record validator and browser!");
		System.out.println("Press enter to exit the program.");
		s.nextLine();
		clearConsole();
		s.close();
	}
}