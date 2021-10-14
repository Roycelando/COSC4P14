import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {
		
		int port = 0;
		if (args.length < 1) {
			port = 4000;
		}

		else {
			port = Integer.parseInt(args[0]);
		}
		while (true) {
			System.out.println("Waiting for client to connect...");

			try (ServerSocket serverSocket = new ServerSocket(port)) {
				Socket socket = serverSocket.accept();
				new FileSystem(socket).start();
				System.out.println("Client connected!");

			} catch (IOException ex) {
				System.out.println("Server exception: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

}
