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

/**
 * This class is used to upload and download files from the Client.
 * Users that are Professors can download files from the Library folder and Upload
 * To the Library folder. Students are only allowed to download from the Library folder.  
 * Professors
 *  
 * @author 
 *
 */
public class FileSystem extends Thread {
	private int role; // student or professor
	private Socket client; // hold the client socket
	private String fileName; // used to hold the file name
	private FileInputStream fileIn; // used to make a stream to a file
	private DataOutputStream output; // used to send data to client 
	private DataInputStream input;  // used to receive data from client 
	private File folder; // Used to get directory of the files
	private File[] listOfFiles; // will hold all the files in my our Library
	private Boolean isStudent; // True if Student false if Professor
	private PrintWriter pwOutput; // used to send date to client
	private BufferedReader brInput; // used to receive date from client
	private Boolean stillUsing = true; // true while the program is running


	FileSystem(Socket client) throws IOException {
		this.client = client; // holds the client socket
		output = new DataOutputStream(client.getOutputStream()); // creates the DataOutputStream
		input= new DataInputStream(client.getInputStream()); // creates the  DataInputStream
		brInput = new BufferedReader(new InputStreamReader(client.getInputStream())); // creates the BufferedReader
		folder = new File("Library"); // creates the folder
		listOfFiles = folder.listFiles(); // gets the list of files in the folder
		pwOutput = new PrintWriter(client.getOutputStream(), true); // creates the PrintWriter
		
		
	}
	/**
	 * This class us used to start the program, 
	 * the "Start" method is invoked on this class.
	 *  
	 */
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

	/**
	 * This class is used to send a file to the Client
	 * 
	 * @param int num: the number of the file client wants 
	 */
	public void uploadFile(int num) {
		
		try { // in case something goes wrong with the file
			if (num >= 0 && num < listOfFiles.length) {
				fileName = listOfFiles[num].getName(); // gets the name of the file
				
				File file = new File("Library/" + fileName); // gets the file we want to send
				fileIn = new FileInputStream(file); // Creates an input stream to the file
				System.out.println("[System]: Sending " + fileName); 

				// we send the file name
				byte[] fileNameBytes = fileName.getBytes(); // will hold the bytes of the file name
				byte[] sendBytes = new byte[(int) file.length()]; // will hold the bytes of file content
				fileIn.read(sendBytes); // reads the file data into the sendBytes array				

				output.writeInt(fileNameBytes.length); // sends the length of the fileName to the client
				output.write(fileNameBytes); // sends the bytes of the fileName to the client
				output.writeInt(sendBytes.length); // sends the length of the content bytes to the client
				output.write(sendBytes); // sends the content bytes to the client

			}
		} catch (FileNotFoundException fnf) { // file not found 
			System.out.println("[System]: File not found");
		} catch (IOException e) { // some IO problem
			System.out.println("[System]: IO exception");
			e.printStackTrace();
		}
		
	}

	/**
	 * The Method is used to download a file from the client.
	 * 
	 * @param input:, 
	 */
	public void downloadFile() {
		try {
			
			int fileNameLength = input.readInt(); // length of the file name
			int contentLength; // length of the file content
			byte[] contentBytes = null; // will hold the bytes of the file content
			byte[] fileNameBytes = null; // will hold the bytes of the fileName

			if (fileNameLength > 0) { // check if there's a file name
				fileNameBytes = new byte[fileNameLength]; 
				input.readFully(fileNameBytes, 0, fileNameBytes.length); // reads all the bytes of the file 
				fileName = new String(fileNameBytes); // Converts the bits to a string

				contentLength = input.readInt(); // get the length of the contents bytes

				if (contentLength > 0) { // if there's content
					contentBytes = new byte[contentLength]; 
					input.readFully(contentBytes, 0, contentBytes.length); // reads all the bytes
				}
				FileOutputStream fout = new FileOutputStream(new File("Library/"+fileName)); // creates a new file in the directory
				fout.write(contentBytes); // Stores the downloaded content onto the file.
				fout.close(); // closes the stream to the file
				
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to send a console menu to the Client.
	 * 
	 * @param option
	 */
	public void sendMenu(int option) {

		if (option == 1) { // option 1 for downloading
			pwOutput.write(listOfFiles.length); // send how many files we have in the library
			pwOutput.write("Select the number of the Book you want to download.\n"); // sends this message to the client

			for (File f : listOfFiles) { // sends the name of each file in the "Library" folder
				pwOutput.write(f.getName() + "\n");
			}
			pwOutput.flush(); // flushes the stream
			int val; // used to store the clients menu selection
			try {
				val = brInput.read(); // reads clients menu selection
				System.out.println("[System]: Trying to upload a file...");
				uploadFile(val); // uploads the file the user selected
				System.out.println("[System]: Done uploading file...");
			} catch (IOException e) {				
				e.printStackTrace();
			}
			
			
		} else if (isStudent == false && option == 2) { // option 2 for uploading, only available to Professors
			System.out.println("[System]: Receiving a file...");
			downloadFile(); // downloads the file user uploaded
			System.out.println("[System]: Done downloading...");
		}

	}

}
