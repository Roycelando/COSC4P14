import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		int port;
		byte[] chunck = new byte[2024];
		if (args.length < 1) {
			port = 4000;
		} else {
			port = Integer.parseInt(args[0]);
		}

		try (Socket s = new Socket("localhost", port)) {
			PrintWriter output = new PrintWriter(s.getOutputStream(), true);
			InputStream is = s.getInputStream();
			FileOutputStream fout = new FileOutputStream("Test.pdf");
			is.read(chunck, 0, chunck.length);
			fout.write(chunck, 0, chunck.length);
			fout.close();

		}
		System.exit(0);
	}

}
