import java.util.ArrayList;
import java.util.Random;

/**
 * Hangman idea and class based on implementation by Dominik Meißner (Institute
 * of Distributed Systems at Ulm University). <br>
 * Adapted and extended by Lorenz Ammon and Alexander Brummer (Computer Networks
 * and Communication Systems at University of Erlangen-Nürnberg).
 */
public class Hangman {

	/**
	 * Collection of words to be guessed.
	 */
	private static final String[] WORDS = { "Feuerwehr", "Autobahn",
			"Informatik", "Fussball", "Schreibtisch", "Butterbrot",
			"Colaweizen", "Arbeitsamt", "Bundeskanzler", "Kartoffel",
			"Gefrierpunkt", "Schichtenarchitektur", "Protokoll",
			"Ausbreitungsverzoegerung", "Rechnerkommunikation", "Bergdienstag",
			"Klausuraufgabe", "Medienzugriff", "Paketvermittlung",
			"Warteschlange" };

	/** ArrayList containing the correctly guessed letters. */
	private ArrayList<Character> guesses;

	/** ArrayList containing guessed letters which are not part of the word. */
	private ArrayList<Character> misses;

	/** The word to be guessed. */
	private final String word;

	/** Number of wrong guesses. */
	private int numMissed;

	/** Indicates if the solution has already been found. */
	private boolean solved;

	/**
	 * Constructor, creates a new hangman game.
	 */
	public Hangman() {
		Random r = new Random();
		word = WORDS[r.nextInt(WORDS.length)];
		guesses = new ArrayList<Character>();
		misses = new ArrayList<Character>();
		numMissed = 1;
		solved = false;
	}

	/**
	 * Checks whether an entered letter is part of the word or not or has
	 * already been guessed.
	 * 
	 * @param c
	 *            The character to be checked.
	 * @return A short evaluating message.
	 */
	public String checkChar(char c) {
		String msg;

		if (c < 'a' || c > 'z') {
			msg = "Nur kleine Buchstaben von a-z sind erlaubt!\r\n";
			++numMissed;
		} else if (guesses.contains(c) || misses.contains(c)) {
			msg = "Dieser Buchstabe wurde bereits genannt... Besser aufpassen!\r\n";
			++numMissed;
		} else if (word.toLowerCase().indexOf(c) != -1) {
			msg = "Gut geraten! Dieser Buchstabe ist enthalten!\r\n";
			guesses.add(c);
		} else {
			msg = "Leider nicht enthalten!\r\n";
			++numMissed;
			misses.add(c);
		}

		return msg;
	}

	/**
	 * Returns the same as {@link #checkChar(char c) checkChar(char c)}, but
	 * formatted as HTML. Checks whether an entered letter is part of the word
	 * or not or has already been guessed.
	 * 
	 * @param c
	 *            The character to be checked.
	 * @return A short evaluating message formatted as HTML.
	 */
	public String checkCharHtml(char c) {
		String answer = checkChar(c);
		String answerHtml = "<P>" + answer.replace("\r\n", "<BR>") + "</P>";
		return answerHtml;
	}

	/**
	 * Checks whether the entered word is the correct solution.
	 * 
	 * @param input
	 *            The word to be checked.
	 * @return A short evaluation message.
	 */
	public String checkWord(String input) {
		String msg;

		if (input.compareToIgnoreCase(word) == 0) {
			solved = true;
			msg = "Absolut richtig!\r\n";
		} else {
			++numMissed;
			msg = "Leider falsch!\r\n";
		}

		return msg;
	}

	/**
	 * Returns the same as {@link #checkWord(String input) checkWord(String
	 * input)}, but formatted as HTML. Checks whether the entered word is the
	 * correct solution.
	 * 
	 * @param input
	 *            The word to be checked.
	 * @return A short evaluating message formatted as HTML.
	 */
	public String checkWordHtml(String input) {
		String answer = checkWord(input);
		String answerHtml = "<P>" + answer.replace("\r\n", "<BR>") + "</P>";
		return answerHtml;
	}

	/**
	 * Returns the word to be guessed.
	 * 
	 * @return The word to be guessed.
	 */
	public String getWord() {
		return word;
	}

	/**
	 * Returns the current state of hangman including the found and missed
	 * letters.
	 * 
	 * @return The current state of hangman.
	 */
	public String getHangman() {
		StringBuilder hangman = new StringBuilder(128);

		if (numMissed < 2) {
			hangman.append("|       \r\n");
		} else {
			hangman.append("|  +==+  \r\n");
		}

		if (numMissed < 3) {
			hangman.append("|     |\r\n");
		} else {
			hangman.append("|  |  |\r\n");
		}

		if (numMissed < 4) {
			hangman.append("|     |\r\n");
		} else {
			hangman.append("|  o  |\r\n");
		}

		if (numMissed < 5) {
			hangman.append("|     |");
		} else if (numMissed < 6) {
			hangman.append("|  |  |");
		} else if (numMissed < 7) {
			hangman.append("| /|  |");
		} else {
			hangman.append("| /|\\ |");
		}

		hangman.append("\r\n");

		if (numMissed < 8) {
			hangman.append("|     |");
		} else if (numMissed < 9) {
			hangman.append("| /   |");
		} else {
			hangman.append("| / \\ |");
		}

		hangman.append("  Nicht enthalten: ");
		boolean first = true;
		for (Character s : misses) {
			if (first) {
				hangman.append(s);
				first = false;
			} else {
				hangman.append(", ");
				hangman.append(s);
			}
		}
		hangman.append("\r\n");
		hangman.append("|_____|  ");
		hangman.append("\r\n\r\n");

		if (solved) {
			hangman.append(word.toLowerCase());
		} else {
			for (int i = 0; i < word.length(); ++i) {
				if (guesses.contains(word.toLowerCase().charAt(i))) {
					hangman.append(word.toLowerCase().charAt(i));
				} else {
					hangman.append("*");
				}
			}
		}

		return hangman.toString();
	}

	/**
	 * Returns the same as {@link #getHangman() getHangman()}, but formatted as
	 * HTML. Returns the current state of hangman including the found and missed
	 * letters.
	 * 
	 * @return The current state of hangman formatted as HTML.
	 */
	public String getHangmanHtml() {
		String hm = getHangman();
		String hmHtml = "<PRE>" + hm.replace("\r\n", "<BR>") + "</PRE><BR>";
		return hmHtml;
	}

	/**
	 * Checks whether the word has been found.
	 * 
	 * @return True if the word has been found, false otherwise.
	 */
	public boolean win() {
		if (solved)
			return true;
		for (int i = 0; i < word.length(); ++i) {
			if (!guesses.contains(word.toLowerCase().charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the hangman is dead due to too many wrong guesses.
	 * 
	 * @return True if the hangman is dead, false otherwise.
	 */
	public boolean dead() {
		if (numMissed >= 9)
			return true;
		else
			return false;
	}

}
