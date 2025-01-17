import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client{
	
	private static Socket clientSocket;
	private static PrintWriter out;
	private static BufferedReader in;
	
	public static void startConnection(String ip, int port) throws IOException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public static String sendMessage(String message) throws IOException{
		out.println(message);
		String response = in.readLine();
		return response;
	}
	
	public static void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}

	public static void echo() throws IOException {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter a message in the form of a string:");
		String message = scan.nextLine();
		System.out.println(sendMessage(message));
		scan.close();
	}

	public static void main(String [] args) throws IOException{
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		Client client = new Client();
		client.startConnection(ip, port);
		client.echo();
		client.stopConnection();
	}
}
