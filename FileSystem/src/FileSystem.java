import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystem extends Thread {
	private int role; // student or professor
	private Socket client; // hold the client socket
	private String fileName; // used to hold the file name
	private FileInputStream fileIn; // used to make a stream to our files
	private DataOutputStream output; // used to send data via the DataOutputStream
	private DataInputStream input; 
	private File folder; // Used to get directory of the files
	private File[] listOfFiles; // will hold all the files in my our Library
	private Boolean isStudent;
	private PrintWriter pwOutput;
	private BufferedReader brInput;
	Boolean stillUsing = true;


	FileSystem(Socket client) throws IOException {
		this.client = client; // holds the client socket
		output = new DataOutputStream(client.getOutputStream()); // creates the DataOutputStream
		input= new DataInputStream(client.getInputStream());
		brInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
		folder = new File("Library"); // creates the folder
		listOfFiles = folder.listFiles(); // gets the list of files in the folder
		pwOutput = new PrintWriter(client.getOutputStream(), true);
		
		
	}

	public void run() {
		try {
			System.out.println("[System]: File system is running....");
			int option;
			int role = (int)brInput.read();
			isStudent = role == 1 ? true : false;
			
			while (stillUsing) {			
				option = brInput.read();
				if((isStudent && option == 2) || (!isStudent && option == 3)) {
					break;
				}
				sendMenu(option);	
			}

			System.out.println("[System]: File system closing..");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.getStackTrace();
		}
	}

	public void uploadFile(int num) {
		
		try {
			if (num >= 0 && num < listOfFiles.length) {
				fileName = listOfFiles[num].getName();
				
				// get the file we want to send
				File file = new File("Library/" + fileName);
				fileIn = new FileInputStream(file);
				fileName = file.getName();
				System.out.println("[System]: Sending -> " + fileName);

				// we send the file name
				byte[] fileNameBytes = fileName.getBytes();
				byte[] sendBytes = new byte[(int) file.length()];
				fileIn.read(sendBytes);

				output.writeInt(fileNameBytes.length);
				output.write(fileNameBytes);

				output.writeInt(sendBytes.length);
				output.write(sendBytes);

			}
		} catch (FileNotFoundException fnf) {
			System.out.println("[System]: File not found");
		} catch (IOException e) {
			System.out.println("[System]: IO exception");
			e.printStackTrace();
		}
		
	}

	public void downloadFile(DataInputStream input) {
		try {
			
			int fileNameLength = input.readInt();
			int contentLength;
			byte[] contentBytes = null;
			byte[] fileNameBytes = null;

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
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	public void sendMenu(int option) {

		if ((isStudent == true && option == 1) || (isStudent == false && option == 1)) {
			pwOutput.write(listOfFiles.length);
			pwOutput.write("Select the number of the Book you want to download.\n");

			for (File f : listOfFiles) {
				pwOutput.write(f.getName() + "\n");
			}
			pwOutput.flush();
			int val;
			try {
				val = brInput.read();
				System.out.println("[System]: Trying to upload a file...");
				uploadFile(val);
				System.out.println("[System]: Done uploading file...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else if (isStudent == false && option == 2) {
			System.out.println("[System]: Receiving a file...");
			downloadFile(input);
			System.out.println("[System]: Done downloading...");
		}

	}

}
