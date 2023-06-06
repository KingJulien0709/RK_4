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
	// TODO TCP-Server-Socket and -Clients-Sockets definition.
	private static final int PORTNR = 1234;
	private static ServerSocket serverSocket;
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
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
		// TODO Init game and hangman.
		initGame();
		Hangman hangman = new Hangman();
		writeToAll("The game has started. Let's play Hangman!");


		while (true)// TODO Loop until solution is found or hangman is dead.
		{
			// TODO Inform players and read input.
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


			// TODO Process input and inform players.
			// Check for valid input and update game

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


			// TODO Set curPlayer to next player.
			curPlayer = (curPlayer + 1) % NUM_PLAYERS;


		}

		// TODO Inform players about the game result.
		if (hangman.win()) {
			writeToAll("Congratulations! The word has been guessed correctly.");
		} else if (hangman.dead()) {
			writeToAll("Game over! The hangman is dead. The word was: " + hangman.getWord());
		}



		// TODO Close player sockets.
		for (Socket socket : clientSockets) {
			socket.close();
		}


	}

	/**
	 * Initializes sockets until number of players {@link #NUM_PLAYERS
	 * NUM_PLAYERS} is reached.
	 *
	 * @throws Exception
	 */
	private static void initGame() throws Exception {
		// TODO Initialize sockets/arrays and current player.
		serverSocket = new ServerSocket(PORTNR);
		clientSockets = new Socket[NUM_PLAYERS];
		writers = new BufferedWriter[NUM_PLAYERS];
		readers = new BufferedReader[NUM_PLAYERS];
		curPlayer = 0;


		while (true)// TODO Not all players connected
		{
			// TODO Initialize socket and reader/writer for every new connected
			// player.
			Socket clientSocket = serverSocket.accept();
			clientSockets[curPlayer] = clientSocket;
			writers[curPlayer] = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			readers[curPlayer] = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


			// TODO Welcome new player and increment current player.
			writeToAllButCur("Player " + (curPlayer + 1) + " has joined the game!");
			writeToAllButCur("Waiting for more players...");
			curPlayer = (curPlayer + 1) % NUM_PLAYERS;

			if (curPlayer == 0) {
				break; // All players connected, exit the loop
			}


		}
		// TODO Reset current player.
		curPlayer = 0;


		// TODO Prevent more connections to be established. Inform players about
		// start of the game.
		serverSocket.close();
		writeToAll("The game is starting!");


	}

	/**
	 * Writes the String s to all players.
	 *
	 * @param s
	 *            The String to be sent.
	 * @throws Exception
	 */
	private static void writeToAll(String s) throws Exception {
		// TODO
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
	 * @throws Exception
	 */
	private static void writeToAllButCur(String s) throws Exception {
		// TODO
		for (int i = 0; i < NUM_PLAYERS; i++) {
			if (i != curPlayer) {
				writers[i].write(s);
				writers[i].newLine();
				writers[i].flush();
			}
		}

	}

	private static boolean isValidInput(String input) {
		// Implement the validation logic for the user input
		// Return true if the input is valid, false otherwise
		// You can customize this logic based on the requirements of your game

		// Example validation logic: Check if the input is a single letter or a word starting with '!'
		if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
			return true;
		} else if (input.length() > 1 && input.startsWith("!")) {
			return true;
		} else {
			return false;
		}
	}

}
