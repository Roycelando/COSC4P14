import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
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
		int fileNameLength;
		String fileName;
		int contentLength;
		int port;
		byte[] contentBytes = null;
		byte[] fileNameBytes = null;

		if (args.length < 1) {
			port = 4000;
		} else {
			port = Integer.parseInt(args[0]);
		}

		try (Socket s = new Socket("localhost", port)) {
			DataInputStream input = new DataInputStream(s.getInputStream());
			fileNameLength = input.readInt();

			if (fileNameLength > 0) {
				fileNameBytes = new byte[fileNameLength];
				input.readFully(fileNameBytes, 0, fileNameBytes.length);
				fileName = new String(fileNameBytes);

				contentLength = input.readInt();
				
				if (contentLength > 0) {
					contentBytes = new byte[contentLength];
					input.readFully(contentBytes, 0, contentBytes.length);
				}
				FileOutputStream fout = new FileOutputStream(new File(fileName));
				fout.write(contentBytes);
				fout.close();
			}
			
		

		}

	}

}
