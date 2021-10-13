import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystem extends Thread {
	int role; // student or professor
	int program; // what program are you int
	Socket client;
	String fileName;
	FileInputStream fileIn;
	DataOutputStream output;
	

	FileSystem(Socket client) throws IOException {

		this.client = client; // holds the client socket
		output = new DataOutputStream(client.getOutputStream());

	}

	public void run() {
		System.out.println("File system is running....");
		try {
			// get the file we want to send
			File file = new File("Library/Solution.pdf");
			fileIn = new FileInputStream(file);
			fileName = file.getName();
			System.out.println("trying to send: " + fileName);
			
			// we send the file name
			byte[] fileNameBytes = fileName.getBytes();
			byte[] sendBytes = new byte[(int)file.length()];
			fileIn.read(sendBytes);
			
			output.writeInt(fileNameBytes.length);
			output.write(fileNameBytes);
			
			output.writeInt(sendBytes.length);
			output.write(sendBytes);
			
	
		} catch (FileNotFoundException fnf) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		}
		
		System.out.println("done");
		
	}


}
