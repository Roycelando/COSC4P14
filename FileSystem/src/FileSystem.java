import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileSystem extends Thread {
	int role; // student or professor
	int program; // what program are you int
	Socket client;
	FileInputStream file;
	OutputStream output;
	byte[] chunck = new byte[2024];
	int status = -2;

	FileSystem(Socket client) throws IOException {

		this.client = client;
		output = client.getOutputStream();

	}

	public void run() {

		try {

			file = new FileInputStream(new File("Library/Solution_Manual_of_Discrete_Mathematics.pdf"));
			file.read(chunck, 0, chunck.length);
			output.write(chunck, 0, chunck.length);
			
		} catch (FileNotFoundException fnf) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		}
		
	}


}
