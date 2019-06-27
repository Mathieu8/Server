package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * start of the server side of the application. It does 2 things, beside
 * regulating the server. It also makes a basic GUI that shows that all the
 * connection to the server apart from all the errors and other println's.
 * 
 * 
 * @author Mathieu
 * @version 09/27/2018
 *
 */
public class Server {
	// Number a client
	private int clientNo = 0;

	private String[] getHostAddresses() {
		Set<String> HostAddresses = new HashSet<>();
		try {
			for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				if (!ni.isLoopback() && ni.isUp() && ni.getHardwareAddress() != null) {
					for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
						if (ia.getBroadcast() != null) { // If limited to IPV4
							HostAddresses.add(ia.getAddress().getHostAddress());
						}
					}
				}
			}
		} catch (SocketException e) {
		}
		return HostAddresses.toArray(new String[0]);
	}

	public void runServer() {
		new Thread(() -> {
			try {
				String[] addrs = getHostAddresses();
				for (String b : addrs) {
					ServerGUI.print("Server: InetAddress.getLocalHost() " + b);
				}

				InetAddress addr = InetAddress.getByName(addrs[0]);
				try (ServerSocket serverSocket = new ServerSocket(8002, 50, addr);
						ServerSocket objectServerSocket = new ServerSocket(8001, 50, addr);) {
					ServerGUI.print("Server: #" + clientNo + " MultiThreadServer started at " + new Date());

					while (true) {

						ServerGUI.print("Server: #" + clientNo + " serverSocket.isClosed() " + serverSocket.isClosed());

						// Listen for a new connection request
						Socket socket = serverSocket.accept();
						Socket objectSocket = objectServerSocket.accept();
						ServerGUI.print("Server: #" + clientNo + " Socket.isClosed() " + socket.isClosed());

						// Increment clientNo
						clientNo++;

						// Display the client number
						ServerGUI.print(
								"Server: #" + clientNo + " Starting thread for client " + clientNo + " at " + new Date());
						ServerGUI.print("Server: #" + clientNo + " Amount of Threads active " + Thread.activeCount());

						// Find the client's host name, and IP address
						InetAddress inetAddress = socket.getInetAddress();
						InetAddress inetAddressO = objectSocket.getInetAddress();
						ServerGUI.print("Server: #" + clientNo + " Client " + clientNo + "'s host name is "
								+ inetAddress.getHostName());
						ServerGUI.print("Server: #" + clientNo + " Client " + clientNo + "'s IP Address is "
								+ inetAddress.getHostAddress());
						ServerGUI.print("Server: #" + clientNo + " Object Client " + clientNo + "'s IP Address is "
								+ inetAddressO.getHostAddress());

						// Create and start a new thread for the connection
						new Thread(new HandleAClient(clientNo,this, socket, objectSocket)).start();
					}
				} catch (IOException ex) {
					System.err.println(ex);
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
	
	void clientNo() {
		clientNo--;
	}

	// Define the thread class for handling new connection
}
