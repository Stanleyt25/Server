import java.net.*;
import java.io.*;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class EchoClient{
	
	private static Socket clientSocket;
	private static PrintWriter out;
	private static BufferedReader in;
	
	private static String measurement_type = "rtt"; // Defaults to rtt
	private static int number_probes = 0; // The number of probes
	private static int message_size = 0; // The allotted message size in bytes for the payload
	
	public static void startConnection(String ip, int port) throws IOException { // Starts the Client socket
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}
	
	public static String sendMessage(String message) throws IOException{ // Sends a message to the server and receives any echo
		out.println(message);
		String response = in.readLine();
		return response;
	}
	
	public static void stopConnection() throws IOException { // Stops the Client socket
		in.close();
		out.close();
		clientSocket.close();
	}

	public static void sendCSP() throws IOException { // CSP Phase --> Prompts user to enter a connection setup message and extracts the necessary information
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter a connection setup message:");
		String message = scan.nextLine();
		System.out.println(sendMessage(message));
		
		String parameters[] = message.split(" ");
		
		try {
			measurement_type = parameters[1];
			number_probes = Integer.parseInt(parameters[2]);
			message_size = Integer.parseInt(parameters[3]);
		}
		catch (Exception e) {
			System.out.println("Invalid measurement type, number of probes, and/or message size");
		}
	}
	
	public static void sendMP(double[] list) throws IOException { // MP Phase --> Prompts user to enter the specified number of probes AND takes measurements for rtt or tput
		// Sends specified number of probe messages to the server
		for(int i = 1; i <= number_probes; i++) {
			
			// START TIME
			double startTime = System.currentTimeMillis(); // TRACK TIME BEFORE SENDING MESSAGE
			Random random = ThreadLocalRandom.current();
			byte[] r = new byte[message_size];
			random.nextBytes(r);
			String msg = "m " + i + " " + Base64.getUrlEncoder().encodeToString(r) + " \n";
			System.out.println(sendMessage(msg));
			double endTime = System.currentTimeMillis(); // TRACK TIME ONCE MESSAGE HAS BEEN ECHOED
			if(measurement_type.equals("rtt")) { // If we're measuring rtt, we record that!
				list[i-1] = endTime - startTime;
			}
			else if(measurement_type.equals("tput")) { // If we're measuring throughput, we record that!
				list[i-1] = ((msg.getBytes().length * .000008) / ((endTime - startTime)/1000)); // 1 byte = 0.000008 megabits and 1 ms = 0.001 seconds
			}
		}
		
		if(measurement_type.equals("rtt")) {
			System.out.println("The average round-trip time is: " + average(list) + " ms.");
		}
		
		else if(measurement_type.equals("tput")) {
			System.out.println("The average throughput is: " + average(list) + " mbps.");
		}
	}
	
	public static void sendCTP() throws IOException { // CTP Phase --> promts user to input "t" to terminate the connection with the Server elegantly
		Scanner scan = new Scanner(System.in);
				
		System.out.println("In order to send a termination request, please enter \"t\".");
		String msg = scan.nextLine();
		System.out.println(sendMessage(msg));
	}
	
	public static double average(double[] list) throws IOException { // A helper method to calculate the average of list of doubles
		double sum = 0.0;
		for(int i = 0; i < list.length; i++) {
			sum += list[i];
		}
		return sum/list.length;
	}

	public static void main(String [] args) throws IOException{
		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		EchoClient.startConnection(ip, port);
		EchoClient.sendCSP();
		double averages[] = new double[number_probes];
		EchoClient.sendMP(averages);
		EchoClient.sendCTP();
		EchoClient.stopConnection();
	}
}
