package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import java.util.Optional;

class HandleAClient implements Runnable {
	private Socket socket; // A connected socket
	private Socket objectSocket; // A connected socket
	private ServerOptions options = new ServerOptions();
	private Optional<Long> sessionID = Optional.empty();
	private String threadName;
	Server server;

	/**
	 * Construct a thread
	 * 
	 * @param socket
	 * @param objectSocket
	 */
	public HandleAClient(Server server, Socket socket, Socket objectSocket) {
		this.server = server;
		this.socket = socket;
		this.objectSocket = objectSocket;
		ServerGUI.print("in HandleClient()");

	}

	/** Run a thread */
	public void run() {
		ServerGUI.print(threadName + " in run()");
//		 ConnectionToDB.createConn();
//		ServerGUI.print("after ConnectionToDB.createConn()");
		// Create data input and output streams
		try (DataInputStream input = new DataInputStream(socket.getInputStream());
				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
				ObjectInputStream objectInput = new ObjectInputStream(objectSocket.getInputStream());) {
			socket.setSoTimeout(100000);

			while (true) {
				String ipAddress = socket.getInetAddress().getHostAddress();
				ServerGUI.print("\n" + threadName + " running while again \n" + new Date());

				ServerGUI.print(threadName + " input.available(): " + input.available());
				ServerGUI.print(threadName + " waiting for option");
				String option = input.readUTF(); // TODO does this give an error when there is nothing available?

				serverSwitch(option, ipAddress, input, output, objectInput);
			}
		} catch (ClassNotFoundException | IOException e) {
			ServerGUI.print("Error " + e);
			e.printStackTrace();
		} finally {
			ServerGUI.print(threadName + " number of active threads: " + Thread.activeCount());
			ServerGUI.print(threadName + " interrupting thread");
			server.clientNo();
			Thread.interrupted();
		}
	}

	private void serverSwitch(String option, String ipAddress, DataInputStream input, DataOutputStream output,
			ObjectInputStream objectInput) throws ClassNotFoundException, IOException {
		ServerGUI.print(threadName + " option is " + option);
		switch (option) {
		case "Token":
			ServerGUI.print(threadName + " in token");
			sessionID = options.checkToken(input, output);
			break;
		case "Password":
			ServerGUI.print(threadName + " in password");
			sessionID = options.checkPW(input, output, ipAddress);
			break;
		case "BasicMeasurements":
			ServerGUI.print(threadName + " in measurements");
			options.saveMeasurement(sessionID, objectInput);
//			break outer;
			break;
		case "sendNewAccount":
			ServerGUI.print(threadName + " in sendNewAccount");
			sessionID = options.sendNewAccount(input, output);
//			break outer;
			break;
		case "ChangePW":
			ServerGUI.print(threadName + " in changePW");
			options.changePW(input, output, ipAddress);
			break;
		case "Close":
		default:
			ServerGUI.print(threadName + " Close");
			break;
		}
	}

}