CS655 Programming Assignment 1, Part 1
Stanley To
U38613234
stanto25@bu.edu
--------------------------------------

Submitted Files:
1. Server.java
2. Client.java

--------------------------------------

Note: The code in both Server.java and Client.java is heavily adapted from the provided guide to server sockets provided on the assignment handout.
A link to the tutorial can be accessed here: https://www.baeldung.com/a-guide-to-java-sockets

Server.java
------------
The server accepts, as a command line argument, a port number that it will run at. It creates a server socket and stays on hold until a client socket accepts it. When a connection is established with a client, it repeatedly polls for an input message from a client and sends back the same message.

Example of a command line argument: java Server 580000


Client.java
-----------
The client accepts, as a command line argument, a host name (or IP address) and a port number for the server. It creates a connection using TCP with the server, which is already running at this point. The client program then prompts the user for a message (text string) and sends it to the server using the connection. When it receieves back the message, it prints it and exits.

Example of a command line argument: java Server csa1.bu.edu 580000