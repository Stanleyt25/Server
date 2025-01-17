import java.net.*;
import java.nio.channels.ClosedByInterruptException;
import java.io.*;

public class EchoServer{
	
	private static ServerSocket serverSocket;
	private static Socket clientSocket;
	private static PrintWriter out;
	private static BufferedReader in;
	
	private static char protocol_phase; // The protocol phase
	private static String measurement_type; // The measurement type, "rtt" if measuring round trip time and "tput" if measuring throughput
	private static int number_probes; // The number of probes
	private static int message_size; // The allotted number of bytes in the probe's payload
	private static int server_delay; // The amount of time in milliseconds that the server waits before echoing back
	
	private static int counter = 0; // Tracks the probe number sequence numbers
	
	public static void start(int port) throws IOException { // Starts the server socket and loops for input until a request for termination is signaled
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		System.out.println("A new client joined.");
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String input;
		
		while (true) { // Loops indefinitely until a termination request is received
			input = in.readLine();
			char type = input.charAt(0);
			
			if(type == 't')  { // If the protocol phase is t --> CTP
				out.println("200 OK: Closing Connection");
				break;
			}

			else if(type == 's') { // If the protocol phase is s --> CSP
				parseCSP(input);
			}
			
			else if(type == 'm') { // If the protocol phase is m --> MP
				counter++; // Increment the internal counter to ensure the probes are providing the RIGHT probe sequence number
				parseMP(input);
				try {
					Thread.sleep(server_delay); // Waits the specified server delay (ms) before echoing back
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				out.println(input);
			}
			
			else { // In the case that none of the appropriate phases is provided, an improper termination is initiated
				out.print("Improper termination... Terminating anyways...");
				break;
			}
		}
	}
	
	public static void stop() throws IOException { // Stops the server socket
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}
	
	public static void parseCSP(String msg) throws IOException{ // CSP Phase: Parses the message and makes sure the formatting is appropriate
		String parameters[] = msg.split(" ");
		
		// Checks whether the message is sufficiently long (six parameters, including the new line character!)
		if(parameters.length != 6) {
			invalidMessage();
			return;
		}
		
		// Check whether first parameter is a character, 's'
		char first = 'a';
		try  {
			first = parameters[0].charAt(0);
		}
		catch (Exception e) {
			System.out.println("The protocol phase is invalid.");
		}
		if(!(first == 's')) {
			invalidMessage();
			return;
		}
		protocol_phase = first;
		
		// Check whether the second parameter is a String, either "rtt" or "tput"
		String second = parameters[1];
		if(!(second.equals("rtt") || second.equals("tput"))) {
			invalidMessage();
			return;
		}
		measurement_type = second;
		
		// Check whether the third parameter is a positive integer
		int third = 0;
		try {
			third = Integer.parseInt(parameters[2]);
		}
		catch (Exception e) {
			System.out.println("The number of probes is invalid.");
		}
		if(third <= 0) {
			invalidMessage();
			return;
		}
		number_probes = third;		
		
		// Check whether the fourth parameter is appropriate for the given measurement type
		int fourth = 0;
		try {
			fourth = Integer.parseInt(parameters[3]);
		}
		catch (Exception e) {
			System.out.println("Message size is invalid.");
		}
		if(measurement_type.equals("rtt") && fourth != 1 && fourth != 100 && fourth != 200 && fourth != 400 && fourth != 800 && fourth != 1000) {
			invalidMessage();
			return;
		}
		else if(measurement_type.equals("tput") && fourth != 1000 && fourth != 2000 && fourth != 4000 && fourth != 8000 && fourth != 16000 && fourth != 32000) {
			invalidMessage();
			return;
		}
		else {
			message_size = fourth;
		}
		
		// Check whether the fifth parameter is an integer > 0
		int fifth = -1;
		try {
			fifth = Integer.parseInt(parameters[4]);
		}
		catch (Exception e) {
			System.out.println("Server delay is invalid.");
		}
		if(fifth < 0) {
			invalidMessage();
			return;
		}
		server_delay = fifth;
		
		// Print the parameters for sanity check!
		System.out.println("PROTOCOL PHASE: " + protocol_phase);
		System.out.println("MEASUREMENT TYPE: " + measurement_type);
		System.out.println("NUMBER OF PROBES: " + number_probes);
		System.out.println("MESSAGE SIZE: " + message_size);
		System.out.println("SERVER DELAY: " + server_delay);
		
		out.println("200 OK: Ready");
	}
	
	public static void parseMP(String msg) throws IOException { // MP Phase: parse each message to make sure it fits the expected format
		String parameters[] = msg.split(" ");
		
		// Checks whether the message is sufficiently long (four parameters, including the new line character!)
		if(parameters.length != 3) {
			System.out.println("Wrong length");
			invalidMeasurementMessage();
			return;
		}
		
		// Check whether first parameter is a character, 'm'
		char first = 'a';
		try  {
			first = parameters[0].charAt(0);
		}
		catch (Exception e) {
			System.out.println("The protocol phase is invalid.");
		}
		if(!(first == 'm')) {
			System.out.println("Wrong first letter");
			invalidMeasurementMessage();
			return;
		}
		
		protocol_phase = first;
		
		// Check whether the second parameter is the appropriate probe sequence number
		int second = 0;
		try {
			second = Integer.parseInt(parameters[1]);
		}
		catch (Exception e) {
			System.out.println("The probe number is invalid.");
		}
		if(second != counter) {
			System.out.println("Wrong sequence #");
			invalidMeasurementMessage();
			return;
		}
	}
	
	public static void invalidMessage() throws IOException { // Helper method, if an invalid message is detected during CSP
		out.println("404 ERROR: Invalid Connection Setup Message");
		EchoServer.stop();
	}
	
	public static void invalidMeasurementMessage() throws IOException { // Helper method, if an invalid message is detected during MP
		out.println("404 ERROR: Invalid Measurement Message");
		EchoServer.stop();
	}
		
	public static void main(String [] args) throws IOException {
		int port = Integer.parseInt(args[0]);
		EchoServer.start(port);
		EchoServer.stop();
	}
}