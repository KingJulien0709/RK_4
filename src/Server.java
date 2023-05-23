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





		while (true)// TODO Loop until solution is found or hangman is dead.
		{
			// TODO Inform players and read input.






			// TODO Process input and inform players.











			// TODO Set curPlayer to next player.

		}

		// TODO Inform players about the game result.






		// TODO Close player sockets.



	}

	/**
	 * Initializes sockets until number of players {@link #NUM_PLAYERS
	 * NUM_PLAYERS} is reached.
	 * 
	 * @throws Exception
	 */
	private static void initGame() throws Exception {
		// TODO Initialize sockets/arrays and current player.






		while (true)// TODO Not all players connected
		{
			// TODO Initialize socket and reader/writer for every new connected
			// player.





			// TODO Welcome new player and increment current player.






		}
		// TODO Reset current player.


		// TODO Prevent more connections to be established. Inform players about
		// start of the game.




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






	}

}
