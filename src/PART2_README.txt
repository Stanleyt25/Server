CS655 Programming Assignment 1, Part 2
Stanley To
U38613234
stanto25@bu.edu
--------------------------------------

Submitted Files:
1. EchoServer.java
2. EchoClient.java

--------------------------------------

Note: The code in both EchoServer.java and EchoClient.java is heavily adapted from the provided guide to server sockets provided on the assignment handout.
A link to the tutorial can be accessed here: https://www.baeldung.com/a-guide-to-java-sockets

EchoServer.java
------------
The server accepts, as a command line argument, a port number that it will run at. It creates a server socket and stays on hold until a client socket accepts it. When a connection is established with a client, it repeatedly polls for an input message from a client and respond accordingly.

Example of a command line argument: java Server 580000

Once a client connects to the server, "A new client has joined." is printed into the terminal.


EchoClient.java
------------
The client accepts, as a command line argument, a host name (or IP address) and a port number for the server. It creates a connection using TCP with the server, which is already running at this point. The client program then prompts the user for a message (text string) and sends it to the server using the connection. When it receieves back the message, it prints it and exits.

Example of a command line argument: java Server csa1.bu.edu 580000

Once the client connects to the server, the terminal prompts the user for a connection setup message. 

Format of the connection setup message should conform to the following: <PROTOCOL PHASE><WS><MEASUREMENT TYPE><WS><NUMBER OF PROBES><WS><MESSAGE SIZE><WS><SERVER DELAY><WS>\n

------------

PROTOCOL PHASE: the protocol phase of the initial setup will be denoted by the lower case character 's' for the Connection Setup phase. For the Measurement Phase we will be using the lower case character 'm', and for the Connection Termination phase we will be using the lower case character 't'.

WS: A single white space to separate the different fields. Note that in my version, there is a white space between the server delay field and the '\n' new line character.

MEASUREMENT TYPE: Allows client to specify which measurement should be computed: "rtt" to compute the average round trip time and "tput" to compute the average throughput.

NUMBER OF PROBES: Allows the client to specify the number of measurement probes that the server should expect to receive.

MESSAGE SIZE: Specifes the size, in number of bytes, in the probe's payload

SERVER DELAY: Specifies the amount of time, in whole milliseconds (an integer), that the server should wait before echoing the message back

------------

Upon a successful connection and appropriate connection setup message being send, the connection setup message is echo'd, and the terminal prompts the user to send probes equal to the specified number in the connection setup message, one at a time. Each probe message is echoed by the server.

Once all the probe messages have been sent, the average statistic specified at the starting connection setup message is provided, and the user is prompted to input the lowercase character 't' to send a request to terminate the conenction with the server. The lowercase character 't' is echoed by the server.

Example of the terminal:

Output: Enter a connection setup message:
Input: s rtt 2 500 0 \n
Output: s rtt 2 500 0 \n
Input: Please send 2 messages.
Output: Enter Probe Message #1:
Input: m 1 HelloThisIsATest \n
Output: m 1 HelloThisIsATest \n
Output: Enter Probe Message #2:
Input: m 2 ThisIsAnotherTestToTheServer \n
Output: m 2 ThisIsAnotherTestToTheServer \n
Output: The average round-trip time is: 1.5 ms.
Output: In order to send a termination request, please enter "t".
Input: t
Output: t