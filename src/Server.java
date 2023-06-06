import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Serves Hangman for telnet (the terminal version of the Hangman game). All
 * methods and fields are static.
 */
public class Server {
	private static final int PORTNR = 2345;
	private static Socket[] clientSockets;


	/** A BufferedWriter for each player. */
	private static BufferedWriter[] writers;

	/** A BufferedReader for each player; */
	private static BufferedReader[] readers;

	/** Number of players. */
	private static final int NUM_PLAYERS = 2;

	/** Currently active player (0 - n-1). */
	private static int curPlayer;


	/**
	 * Initializes the game. Loops until solution is found or hangman is dead.
	 *
	 * @param argv
	 *            Optional command line arguments.
	 */
	public static void main(String[] argv) throws Exception {
		initGame();
		Hangman hangman = new Hangman();
		writeToAll("The game has started. Let's play Hangman!");


		while (true)
		{
			// Inform players about the current state of the game
			writeToAll(hangman.getHangman());

			// Prompt the current player to enter a letter or guess the word
			writeToAllButCur("Player " + (curPlayer + 1) + ", enter a letter or guess the word with '!':");
			String input = readers[curPlayer].readLine();

			// Check if the input is the abort command
			if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
				writeToAll("The game has been aborted by a player.");
				break; // End the game loop
			}

			if (isValidInput(input)) {
				char letter = input.charAt(0);
				hangman.checkChar(letter);
				writeToAll(hangman.getHangman());

				if (hangman.win() || hangman.dead()) {
					break; // End the game if it's over
				} else {
					curPlayer = (curPlayer + 1) % NUM_PLAYERS; // Switch to the next player
				}
			} else {
				writeToAllButCur("Invalid input! Please enter a letter or guess the word with '!'.");
			}

		}

		if (hangman.win()) {
			writeToAll("Congratulations! The word has been guessed correctly.");
		} else if (hangman.dead()) {
			writeToAll("Game over! The hangman is dead. The word was: " + hangman.getWord());
		}



		for (Socket socket : clientSockets) {
			socket.close();
		}


	}

	/**
	 * Initializes sockets until number of players {@link #NUM_PLAYERS
	 * NUM_PLAYERS} is reached.
	 *
	 */
	private static void initGame() throws Exception {
		ServerSocket serverSocket = new ServerSocket(PORTNR);
		clientSockets = new Socket[NUM_PLAYERS];
		writers = new BufferedWriter[NUM_PLAYERS];
		readers = new BufferedReader[NUM_PLAYERS];
		curPlayer = 0;

		do {
			Socket clientSocket = serverSocket.accept();
			clientSockets[curPlayer] = clientSocket;
			writers[curPlayer] = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			readers[curPlayer] = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			writeToAllButCur("Player " + (curPlayer + 1) + " has joined the game!");
			writeToAllButCur("Waiting for more players...");

			if (curPlayer == NUM_PLAYERS - 1) {
				break;
			}

			curPlayer = (curPlayer + 1) % NUM_PLAYERS;
		} while (true);

		serverSocket.close();
		writeToAll("The game is starting!");
	}


	/**
	 * Writes the String s to all players.
	 *
	 * @param s
	 *            The String to be sent.
	 */
	private static void writeToAll(String s) throws Exception {
		for (BufferedWriter writer : writers) {
			writer.write(s);
			writer.newLine();
			writer.flush();
		}

	}

	/**
	 * Writes the String s to all players but to the current player.
	 *
	 * @param s
	 *            The String to be sent.
	 */
	private static void writeToAllButCur(String s) throws Exception {
		for (int i = 0; i < NUM_PLAYERS; i++) {
			if (i != curPlayer) {
				writers[i].write(s);
				writers[i].newLine();
				writers[i].flush();
			}
		}
	}

	private static boolean isValidInput(String input) {
		if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
			return true;
		} else return input.length() > 1 && input.startsWith("!");
	}

}
