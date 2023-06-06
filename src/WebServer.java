import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Serves Hangman for HTTP (the browser version of the Hangman game). All
 * methods and fields are static.
 */
public final class WebServer {
	// TCP-Server-Socket definition.
	private static ServerSocket serverSocket;


	/** HTTP EOL sequence. */
	private static final String EOL = "\r\n";

	/**
	 * Session ID to distinguish between the current and previous game sessions.
	 */
	private static int session;

	/** Session ID cookie of the currently handled request. */
	private static int sessionCookie;

	/** Player number of the currently handled request. */
	private static int playerCookie;

	/** Hangman game object. */
	private static Hangman hangman;

	/** Used to save the result message of the previous action. */
	private static String prevMsg;

	/** Number of players. */
	private static final int NUM_PLAYERS = 2;

	/** Currently active player (0 - n-1). */
	private static int curPlayer;

	/**
	 * Indicates whether we are already in-game or still waiting for some
	 * players.
	 */
	private static boolean gameStarted = false;

	/** Indicates whether the game has ended. */
	private static boolean gameEnded = false;

	/**
	 * Initializes the game. Loops until solution is found or hangman is dead.
	 * 
	 * @param argv
	 *            Optional command line arguments.
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
		//Initialize socket, global variables and hangman.
		serverSocket = new ServerSocket(80);
		session = 0;
		curPlayer = 0;
		hangman = new Hangman();




		while (true) {
			//Accept client request.
			Socket clientSocket = serverSocket.accept();

			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			if (!gameStarted && !gameEnded) {
				processInitRequest(br, bw);
			} else if (gameStarted && !gameEnded) {
				processGameRequest(br, bw);
			} else {
				processEndRequest(br, bw);
				if (curPlayer == NUM_PLAYERS)
					break;
			}
			clientSocket.close();

		}
		serverSocket.close();

	}

	/**
	 * Handles HTTP conversation when game has not yet started. Waits for number
	 * of players {@link #NUM_PLAYERS NUM_PLAYERS} to be present.
	 * 
	 * @param br
	 *            The BufferedReader used to read the request.
	 * @param bw
	 *            The BufferedWriter used to write the HTTP response.
	 * @throws Exception
	 */
	private static void processInitRequest(BufferedReader br, BufferedWriter bw)
			throws Exception {
		//Process request and header lines.
		String request = processHeaderLines(br, bw);

		String content = "<HTML><HEAD><META http-equiv=\"refresh\" content=\"2\"><TITLE>Hangman</TITLE></HEAD><BODY>"
				+ "<PRE>[ ] [ ]-[ ]<BR>         |<BR>[ ]     [ ]<BR> |     /<BR>[ ]-[ ]<BR>____________</PRE>"
				+ "Willkommen zu I7Hangman!<BR>Du bist Spieler ";
		//If the player is unknown: set cookies and increment curPlayer.
		if (playerCookie == -1) {
			++curPlayer;
			playerCookie = curPlayer;
			sessionCookie = session;
		}
		content += curPlayer;

		content += ".<BR>Es darf reihum ein Buchstabe geraten werden.<BR>Die Seite lädt automatisch neu.<BR>"
				+ "Warte auf alle Spieler...</BODY></HTML>";


		//Send response to player.
		String cookieLine = "Set-Cookie: session=" + sessionCookie + "; player=" + playerCookie + EOL;
		sendOkResponse(bw, content, cookieLine);

		if (curPlayer == NUM_PLAYERS) {
			gameStarted = true;
			curPlayer = 0;
		}
	}

	/**
	 * Handles HTTP conversation when game is running. Differentiates between
	 * current player and other players.
	 * 
	 * @param br
	 *            The BufferedReader used to read the request.
	 * @param bw
	 *            The BufferedWriter used to write the HTTP response.
	 * @throws Exception
	 */
	private static void processGameRequest(BufferedReader br, BufferedWriter bw)
			throws Exception {
		// TODO Process request and header lines.
		String request = processHeaderLines(br, bw);


		// Construct the response message.
		String content = "<HTML><HEAD><TITLE>Hangman</TITLE>";

		if (playerCookie == curPlayer && request != null && request.startsWith("GET /?letter=")){// Player is current player and form was submitted.

			String stringGuess = request.substring(14).toLowerCase();

			if (true)// TODO Handle single character guess.
			{



			} else if (true)// TODO Handle word guess.
			{





			}
			// TODO Set curPlayer to next player.

			content += "<META http-equiv=\"refresh\" content=\"0;url=/\"></HEAD><BODY>"
					+ "<PRE>[ ] [ ]-[ ]<BR>         |<BR>[ ]     [ ]<BR> |     /<BR>[ ]-[ ]<BR>____________</PRE>"
					+ prevMsg
					+ hangman.getHangmanHtml()
					+ "Spieler "
					+ curPlayer + " ist an der Reihe.";

		} else if (true)// TODO Player is current player.
		{
			content += "</HEAD><BODY>"
					+ "<PRE>[ ] [ ]-[ ]<BR>         |<BR>[ ]     [ ]<BR> |     /<BR>[ ]-[ ]<BR>____________</PRE>"
					+ prevMsg + hangman.getHangmanHtml()
					+ "Du bist an der Reihe, Spieler " + curPlayer + "!"
					+ "<FORM action=\"/\" method=\"get\">";
			for (char i = 'a'; i <= 'z'; ++i) {
				content += "<INPUT type=\"submit\" name=\"letter\" value=\""
						+ i + "\">";
			}
			content += "</FORM><BR><FORM action=\"/\" method=\"get\">"
					+ "<LABEL>Suchbegriff <INPUT name=\"solution\"></LABEL>"
					+ "<BUTTON>Lösen</BUTTON></FORM>";
		} else {
			content += "<META http-equiv=\"refresh\" content=\"2;url=/\"></HEAD><BODY>"
					+ "<PRE>[ ] [ ]-[ ]<BR>         |<BR>[ ]     [ ]<BR> |     /<BR>[ ]-[ ]<BR>____________</PRE>"
					+ prevMsg
					+ hangman.getHangmanHtml()
					+ "Spieler "
					+ curPlayer + " ist an der Reihe.";
		}
		content += "</BODY></HTML>";

		// TODO Send response to player.


		if (hangman.win() || hangman.dead()) {
			gameStarted = false;
			gameEnded = true;
			curPlayer = 0;
		}
	}

	/**
	 * Handles HTTP conversation when game ended.
	 * 
	 * @param br
	 *            The BufferedReader used to read the request.
	 * @param bw
	 *            The BufferedWriter used to write the HTTP response.
	 * @throws Exception
	 */
	private static void processEndRequest(BufferedReader br, BufferedWriter bw)
			throws Exception {
		// TODO Process request and header lines.

		String request = processHeaderLines(br, bw);


		String content = "<HTML><HEAD><TITLE>Hangman</TITLE></HEAD><BODY>"
				+ "<PRE>[ ] [ ]-[ ]<BR>         |<BR>[ ]     [ ]<BR> |     /<BR>[ ]-[ ]<BR>____________</PRE>"
				+ prevMsg + hangman.getHangmanHtml();

		// TODO Add success/fail line with solution word.






		content += "</BODY></HTML>";

		++curPlayer;
		// TODO Send response to player.

	}

	/**
	 * Processes the HTTP request and its header lines.
	 * 
	 * @param br
	 *            The BufferedReader used to read the request.
	 * @param bw
	 *            The BufferedWriter used to write the HTTP response.
	 * @return The request line of the HTTP request if it is a valid game
	 *         related request, otherwise null.
	 * @throws Exception
	 */
	private static String processHeaderLines(BufferedReader br,
			BufferedWriter bw) throws Exception {
		/*
		 * Get the request line of the HTTP request message. Return null if
		 * its length is zero or if end of stream is reached. Print out the
		 * request line to the console. If the request is for "/favicon.ico",
		 * send a 404 response and return null.
		 */
		String request = br.readLine();
		System.out.println(request);

		if(request==null || request.isEmpty()){
			return null;
		}
		StringTokenizer st = new StringTokenizer(request, " ");
		String method = st.nextToken();
		String path = st.nextToken();
		if(path.endsWith("/favicon.ico")){
			sendNotFoundResponse(bw);
			return null;
		}

		sessionCookie = -1;
		playerCookie = -1;

		// Step through all remaining header lines and extract cookies if
		// present (yamyam). Optionally print the header lines to the console.

		String line;
		String cookieLine = "";
		while ((line = br.readLine()) != null) {
			if(line.isEmpty()) break;
			if(line.startsWith("Cookie:")){
				cookieLine = line.substring("Cookie:".length()).trim();
			}
		}
		if(cookieLine!=""){
			StringTokenizer st_cookies = new StringTokenizer(cookieLine, ";");
			while (st_cookies.hasMoreTokens()) {
				String token = st_cookies.nextToken();
				if(token.startsWith(" player=")) {
					String player = token.substring(8);
					playerCookie = Integer.parseInt(player);
				}else if(token.startsWith(" session=")){
					String session = token.substring(9);
					sessionCookie = Integer.parseInt(session);
				}
			}
		}

		//Return
		return request;
	}

	/**
	 * Sends a 404 HTTP response.
	 * 
	 * @param bw
	 *            The BufferedWriter used to write the HTTP response.
	 * @throws Exception
	 */
	private static void sendNotFoundResponse(BufferedWriter bw)
			throws Exception {
		// Construct and send a valid HTTP/1.0 404-response.
		String response = "HTTP/1.1 404 Not Found" + EOL + "Content-Type: text/html"
				+ EOL + EOL;
		response += "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>";
		bw.write(response);
		bw.flush();
	}

	/**
	 * Sends a HTTP response with cookies (if set) and HTML content.
	 * 
	 * @param bw
	 *            The BufferedWriter used to write the HTTP response.
	 * @param cookieLines
	 *            Optional header lines to set cookies.
	 * @param content
	 *            The actual HTML content to be sent to the browser.
	 * @throws Exception
	 */
	private static void sendOkResponse(BufferedWriter bw, String cookieLines,
			String content) throws Exception {
		// Construct and send a valid HTTP/1.0 200-response with the given
		// cookies (if not null) and the given content.

		String response = "HTTP/1.1 200 OK" + EOL + "Content-Type: text/html"
				+ EOL + cookieLines  + EOL + EOL;
		response += content;

		bw.write(response);
		bw.flush();

	}
}
