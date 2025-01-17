import java.net.*;
import java.nio.channels.ClosedByInterruptException;
import java.io.*;

public class Server{
	
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static PrintWriter out;
	private static BufferedReader in;
	
	public static void start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		System.out.println("A new client joined.");
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String input;
		while (true) {
			input = in.readLine();
			if(".".equals(input))  {
				out.println("Goodbye.");
				break;
			}
			out.println(input);
		}
	}
	
	public static void stop() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}
		
	public static void main(String [] args) throws IOException {
		Server server = new Server();
		int port = Integer.parseInt(args[0]);
		server.start(port);
		server.stop();
	}
}