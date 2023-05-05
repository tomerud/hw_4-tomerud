package il.ac.tau.cs.sw1.ex4;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class WordPuzzle {
	public static final char HIDDEN_CHAR = '_';

	/*
	 * @pre: template is legal for word
	 */
	public static char[] createPuzzleFromTemplate(String word, boolean[] template) { // Q - 1
		int n = template.length;
		char[] newArray = new char[n];
		for (int i = 0; i < n; i++) {
			if (!template[i]) {
				newArray[i] = word.charAt(i);
			} else {
				newArray[i] = WordPuzzle.HIDDEN_CHAR;
			}
		}
		return newArray;
	}


	public static boolean checkLegalTemplate(String word, boolean[] template) { // Q - 2
		int n = word.length();
		int m = template.length;
		if (n != m) {
			return false;
		}
		int letCount = 0;
		for (int i = 0; i < n; i++) {
			if (!template[i]) {
				letCount++;
			}
		}
		if (letCount == 0 || letCount == n) {
			return false;
		}
		for (int j = 0; j < n; j++) {
			for (int q = 0; q < n; q++) {
				if (word.charAt(j) == word.charAt(q)) {
					if (template[j] != template[q]) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/*
	 * @pre: 0 < k < word.lenght(), word.length() <= 10
	 */
	public static boolean[][] getAllLegalTemplates(String word, int k) { // Q - 3
		int n = word.length();
		int maxBinaryString = (int) Math.pow(2, n);
		boolean[][] optionalTemplates = new boolean[maxBinaryString][n];
		int legalTemplateCounter = 0;
		for (int i = 0; i < maxBinaryString; i++) {
			String curBinaryNum = Integer.toBinaryString((1 << n) | i).substring(1);
			int oneCounter = 0;
			for (int j = 0; j < n; j++) {
				if (curBinaryNum.charAt(j) == '1') {
					oneCounter++;
				}
			}
			if (oneCounter == k) {
				boolean[] candidateTemplate = templateMaker(curBinaryNum);
				if (checkLegalTemplate(word, candidateTemplate)) {
					optionalTemplates[i] = candidateTemplate;
					legalTemplateCounter++;
				}
			}
		}
		boolean[][] legalTemplates = new boolean[legalTemplateCounter][n];
		boolean[] onlyFalseArray = new boolean[n];
		int j = 0;
		for (int i = 0; i < maxBinaryString; i++) {
			if (!Arrays.equals(onlyFalseArray, optionalTemplates[i])) {
				legalTemplates[j] = optionalTemplates[i];
				j++;
			}
		}
		return legalTemplates;
	}



	public static boolean[] templateMaker(String curBinaryNum) {
		int n = curBinaryNum.length();
		boolean [] template = new boolean[n];
		for (int i = 0; i < n; i++){
			if (curBinaryNum.charAt(i) == '1'){
				template[i] = true;
			}
		}
		return template;
	}











	/*
	 * @pre: puzzle is a legal puzzle constructed from word, guess is in [a...z]
	 */
	public static int applyGuess(char guess, String word, char[] puzzle) { // Q - 4
		int n = word.length();
		int count = 0;
		for(int i = 0; i < n; i++){
			if(guess == word.charAt(i) && puzzle[i] == HIDDEN_CHAR ){
				puzzle[i] = guess;
				count ++;
			}
		}
		return count;
	}


	/*
	 * @pre: puzzle is a legal puzzle constructed from word
	 * @pre: puzzle contains at least one hidden character.
	 * @pre: there are at least 2 letters that don't appear in word, and the user didn't guess
	 */
	public static char[] getHint(String word, char[] puzzle, boolean[] already_guessed) { // Q - 5
		int n = puzzle.length;
		char[] hint = new char[2];
		Random rand = new Random();
		int numOfHiddenChars = 0;
		for(int i = 0; i < n; i++){
			if (puzzle[i] == HIDDEN_CHAR) {
				numOfHiddenChars ++;
			}
		}
		char[] hiddenCharsLetters = new char[numOfHiddenChars];
		int index = 0;
		for(int i = 0; i < n; i++) {
			if (puzzle[i] == HIDDEN_CHAR) {
				hiddenCharsLetters[index] = word.charAt(i);
				index++;
			}
		}
		Random random1 = new Random();
		char randomHiddenChar = hiddenCharsLetters[random1.nextInt(numOfHiddenChars)];
		boolean[] possibleWrongChar = already_guessed;
		int twentySix = possibleWrongChar.length;
		for(int i = 0; i < twentySix; i++) {
			char letter = (char) ('a' + i);
			for(int j = 0; j < n; j++){
				 if(word.charAt(j) == letter){
					 possibleWrongChar[i] = true;
				 }
			}
		}
		char[] wrongChars = new char[twentySix];
		int index2 = 0;
		for (int i = 0; i < twentySix; i++) {
			if (!possibleWrongChar[i]) {
				wrongChars[index2] = (char) ('a' + i);
				index2++;
			}
		}
		Random random2 = new Random();
		char randomWrongChar = wrongChars[random2.nextInt(index2)];
		if(randomHiddenChar > randomWrongChar ){
			hint[0] = randomWrongChar;
			hint[1] = randomHiddenChar;
		}
		else{
			hint[0] = randomHiddenChar;
			hint[1] = randomWrongChar;
		}
		return hint;
	}



	public static char[] mainTemplateSettings(String word, Scanner inputScanner) { // Q - 6
		System.out.println("---setting stage---");
		int n = word.length();
		char[] puzzle = new char[n];
		Random random3 = new Random();
		while (true) {
			System.out.println("choose a (1) random or (2) manual template:");
			int template = inputScanner.nextInt();
			if (template == 1) {
				System.out.println("Enter number of hidden characters:");
				int numOfHiddenChars = inputScanner.nextInt();
				boolean[][] allPossibleTemplates = getAllLegalTemplates(word, numOfHiddenChars);
				if (allPossibleTemplates.length == 0) {
					System.out.println("Cannot generate puzzle, try again.");
				}
				else {
					int index = random3.nextInt(allPossibleTemplates.length);
					boolean[] myTemplate = allPossibleTemplates[index];
					puzzle = createPuzzleFromTemplate(word, myTemplate);
					break;
				}
			}
			else {
				System.out.println("Enter your puzzle template:");
				inputScanner.nextLine();
				String Xtemplate = inputScanner.nextLine();
				boolean[] chosenTemplate = templateConvertor(Xtemplate);
				if (!(checkLegalTemplate(word,chosenTemplate))) {
					System.out.println("Cannot generate puzzle, try again.");
				}
				else{
					puzzle = createPuzzleFromTemplate(word,chosenTemplate);
					break;
				}
			}
		}
		return puzzle;
	}
	public static boolean[] templateConvertor(String inputString) {
		String[]inp = inputString.split(",");
		boolean [] ans = new boolean[inp.length];
		for(int i = 0; i< ans.length; i++) {
			if(inp[i].charAt(0)== HIDDEN_CHAR) {
				ans[i] = true;
			}
		}
		return ans;}






	public static void mainGame(String word, char[] puzzle, Scanner inputScanner) { // Q - 7
		System.out.println("--- Game stage ---");
		boolean[] already_guessed = new boolean[26];
		int numHiddenLetters = numOfHiddenLetterCalculator(puzzle);
		int numOfChances = numHiddenLetters + 3;
		while (numOfChances > 0 && numHiddenLetters > 0) {
			System.out.println(puzzle);
			System.out.println("Enter your guess");
			String guess = inputScanner.next();
			if (guess.equals("H")) {
				char[] hint = getHint(word, puzzle, already_guessed);
				System.out.println("Here's a hint for you: choose either " + hint[0] + " or " + hint[1]);
			}
			else {
				already_guessed[guess.charAt(0)- 'a'] = true;
				numOfChances --;
				int n = puzzle.length;
				int changedChars = 0;
				for (int i = 0; i < n; i++) {
					if (word.charAt(i) == guess.charAt(0) && puzzle[i] == HIDDEN_CHAR) {
						puzzle[i] = guess.charAt(0);
						changedChars++;
					}
				}
				int numOfHidden = numOfHiddenLetterCalculator( puzzle);
				if(numOfHidden == 0) {
					System.out.println("Congratulations! You solved the puzzle!");
					break;
				}
				if (changedChars > 0){
					System.out.println("Correct Guess," + numOfChances + " guesses left.");
					if (numOfChances == 0){
						System.out.println("Game over!");
						break;
					}
				}
				if(changedChars == 0) {
					System.out.println("Wrong Guess," + numOfChances + " guesses left.");
					if (numOfChances == 0) {
						System.out.println("Game over!");
						break;
					}
				}

			}
		}
	}





	public static int numOfHiddenLetterCalculator(char[] puzzle) {
		int numOfHiddenLetters = 0;
		int n = puzzle.length;
		for (int i = 0; i < n; i++) {
			if (puzzle[i] == HIDDEN_CHAR) {
				numOfHiddenLetters++;
			}
		}
		return numOfHiddenLetters;
	}








/*************************************************************/
/********************* Don't change this ********************/
/*************************************************************/

	public static void main(String[] args) throws Exception {


		if (args.length < 1){
			throw new Exception("You must specify one argument to this program");
		}
		String wordForPuzzle = args[0].toLowerCase();
		if (wordForPuzzle.length() > 10){
			throw new Exception("The word should not contain more than 10 characters");
		}
		Scanner inputScanner = new Scanner(System.in);
		char[] puzzle = mainTemplateSettings(wordForPuzzle, inputScanner);
		mainGame(wordForPuzzle, puzzle, inputScanner);
		inputScanner.close();
	}



	public static void printSettingsMessage() {
		System.out.println("--- Settings stage ---");
	}

	public static void printEnterWord() {
		System.out.println("Enter word:");
	}

	public static void printSelectNumberOfHiddenChars(){
		System.out.println("Enter number of hidden characters:");
	}
	public static void printSelectTemplate() {
		System.out.println("Choose a (1) random or (2) manual template:");
	}

	public static void printWrongTemplateParameters() {
		System.out.println("Cannot generate puzzle, try again.");
	}

	public static void printEnterPuzzleTemplate() {
		System.out.println("Enter your puzzle template:");
	}


	public static void printPuzzle(char[] puzzle) {
		System.out.println(puzzle);
	}


	public static void printGameStageMessage() {
		System.out.println("--- Game stage ---");
	}

	public static void printEnterYourGuessMessage() {
		System.out.println("Enter your guess:");
	}

	public static void printHint(char[] hist){
		System.out.println(String.format("Here's a hint for you: choose either %s or %s.", hist[0] ,hist[1]));

	}
	public static void printCorrectGuess(int attemptsNum) {
		System.out.println("Correct Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWrongGuess(int attemptsNum) {
		System.out.println("Wrong Guess, " + attemptsNum + " guesses left.");
	}

	public static void printWinMessage() {
		System.out.println("Congratulations! You solved the puzzle!");
	}

	public static void printGameOver() {
		System.out.println("Game over!");
	}

}
