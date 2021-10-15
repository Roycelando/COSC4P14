import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

/**
 * This class is used to Connect to the Server and FileSystem class.
 * Professors can download and upload files to the FilesSystem class,
 * While Students can only download files from the FileSystem class.
 * 
 * @author 
 *
 */
public class Client {
	int fileNameLength; // will hold the byte length of a files name
	String fileName; // will hold the name of a file
	int contentLength; // will hold the byte length of a files content
	int port; // will hold the port number of the server
	byte[] contentBytes = null; // will hold the bytes of a files content
	byte[] fileNameBytes = null; // will hold the bytes of a files name
	public boolean isStudent = true; // is True if client is a student
	Socket s; // will hold the socket of the server
	DataInputStream input; // used to receive data from the FileSystem
	DataOutputStream out; // used to send data to the FileSystem
	PrintWriter pwOutput; // used to send data to the FileSystem
	BufferedReader brInput; // used to receive data from the file system
	JFileChooser fc;

	/**
	 * This constructor is use to make the Client object.
	 *
	 * @param port
	 * @param isStudent
	 * @throws InterruptedException
	 **/
	public Client(int port, Boolean isStudent) throws InterruptedException {
		
		this.port = port;  // initializes the port number
		this.isStudent = isStudent; // initializes isStudent
		System.out.println("[Client]: What's the IP of the server?\n[Client]: Type localhost for localhost, or the IP.");
		System.out.print("\tEnter: "); //
		Scanner scan = new Scanner(System.in); // used to get user input
		String ip = scan.next(); // get the IP address from user
		
		try (Socket s = new Socket(ip, port)) { // tires to connect to the socket
			out = new DataOutputStream(s.getOutputStream()); // creates the DataOutputStream
			input = new DataInputStream(s.getInputStream()); // creates the DataInputStream
			brInput = new BufferedReader(new InputStreamReader(s.getInputStream())); // creates the Buffered Reader
			pwOutput = new PrintWriter(s.getOutputStream(), true); // creates the Print Writer
			int value = isStudent == true ? 1 : 0; // used to fetch the correct menu from the FileSystem
			pwOutput.write(value); // sends the value to the FileSystem
			pwOutput.flush(); // flushes the output stream
			while (true) {

				getMenu(); // gets the FileSystem menu from the FileSytem.
			}
		} catch (IOException e) {
			System.out.println("[Client]: There seems to be a problem connecting...");
		}

	}

	/**
	 * This method is used to interact with the FileSystem. 
	 * Professors can Download and Upload files.
	 * Students can only download files from the FileSystem.
	 * 
	 * @param brInput
	 * @throws InterruptedException
	 */
	public void getMenu() throws InterruptedException {
		try {
			Scanner scan = new Scanner(System.in); // used to get user input
			String msg; // used to store FileSystem messages
			
			if (isStudent) { // if the Client is a Student

				int option = 0; // key for the switch statement
				
				while (option != 1 && option != 2) {  // ensures input is valid
					System.out.println("1.Download a file 2. Exit");
					System.out.print("\tEnter: ");
					option = scan.nextInt(); // reads users input
					pwOutput.write(option); // sends users input to the FileSystem
					pwOutput.flush(); // flushes the stream
				}
				
				switch (option) { 
				case 1: { // download a file

					downloadList(); // gets the list of books from the FileSystem
					System.out.print("\tSelect book #: ");
					pwOutput.write(scan.nextInt()); // gets and sends the users input to the FileSystem
					pwOutput.flush(); // flushes the stream
					downloadFile(); // downloads the selected book
					break;
				}
				case 2: { // exits the program
					System.out.println("[Client]: Bye! Have a nice day...");
					System.exit(0); // 
				}
				}

			} else { // if the client is a professor
				int option = 0;
				while (option != 1 && option != 2 && option != 3) { // ensures input is valid
					System.out.println("1.Download a file 2.Upload a file 3. Exit");
					System.out.print("\tEnter: ");
					option = scan.nextInt(); // gets the users input
					pwOutput.write(option); // sends the input to the FileSystem
					pwOutput.flush(); // flushes the stream
				}

				switch (option) {
				case 1: { // download a file
					downloadList(); // gets the list of books
					System.out.print("\t Select a Book #: ");
					pwOutput.write(scan.nextInt()); // gets ands sends the users input to the FileSystem
					pwOutput.flush(); // flushes the stream
					System.out.println("[Client]: Downloading file...");
					downloadFile(); // downloads the file
					System.out.println("[Client]: Download complete...");
					break;
				}
				case 2: { // upload a file
					System.out.println("[Client]: Uploading File...");
					uploadFile(); // uploads the file
					System.out.println("[Clinet]: Done uploading File...");
					break;
				}
				case 3: { // exits the program
					System.out.println("[Client]: Bye! Have a nice day...");
					System.exit(0);
				}
				}

			}
		} catch (InputMismatchException e) {
			System.out.println("Give me a the correct input");
		}
	}

	/**
	 * This method is used to get a list available books 
	 * to download from the fileSystem.
	 * 
	 * @param brInput
	 */
	public void downloadList() {
		String msg;
		try {
			int length = new Integer(brInput.read()); // gets the number of books available
			msg = new String(brInput.readLine()); // gets message from FileSystem
			System.out.println(msg); // prints the message

			for (int i = 0; i < length; i++) { // list the names of all the books available
				msg = new String(brInput.readLine()); 
				System.out.println(i + "." + msg);
			}
			msg = "";

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * The Method is used to download a file from the FileSystem.
	 * 
	 * @param input
	 */
	public void downloadFile() {
		try {
			fileNameLength = input.readInt(); // length of the file name

			if (fileNameLength > 0) { // check if there's a file name
				fileNameBytes = new byte[fileNameLength];
				input.readFully(fileNameBytes, 0, fileNameBytes.length); // reads all the bytes of the file 
				fileName = new String(fileNameBytes); // Converts the bits to a string

				contentLength = input.readInt(); // get the length of the contents bytes

				if (contentLength > 0) { // if there's content
					contentBytes = new byte[contentLength];
					input.readFully(contentBytes, 0, contentBytes.length); // reads all the bytes
				}
				FileOutputStream fout = new FileOutputStream(new File("Downloads/"+fileName)); // creates a new file in the directory
				fout.write(contentBytes);  // Stores the downloaded content onto the file.
				fout.close(); // closes the stream to the file
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to upload files to the FileSystem
	 * 
	 */
	public void uploadFile() {
		try {

			// get the file we want to send
			JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

			int returnValue = jfc.showOpenDialog(null); // get the return value

			if (returnValue == JFileChooser.APPROVE_OPTION) { // checks if the file chooser has been approved
				File selectedFile = jfc.getSelectedFile(); // gets the selected file
			}

			File file = jfc.getSelectedFile(); // stores the file path
			try (FileInputStream fileIn = new FileInputStream(file)) { // creates a stream to the file
				fileName = file.getName(); // gets the name of the file
				System.out.println("[Client]: Sending: " + fileName + " ...");

				// we send the file name
				byte[] fileNameBytes = fileName.getBytes(); // stores the bytes of the file name
				byte[] sendBytes = new byte[(int) file.length()]; // will hold files content bytes
				fileIn.read(sendBytes); // reads the content bytes into sendBytes

				out.writeInt(fileNameBytes.length); // sends file name length to FileSystem
				out.write(fileNameBytes); // sends the file name bytes to FileSystem

				out.writeInt(sendBytes.length); // sends the content byte length to the FileSystem
				out.write(sendBytes); // sends the file's content bytes to the FileSystem

			}

		} catch (FileNotFoundException fnf) { 
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("IO exception");
			e.printStackTrace();
		}

	}


	/**
	 * The main method runs when the program is executed.
	 * 
	 * If you are using a terminal you must type the port number of the server(4000) and 0 if you
	 * are a professor and 1 if you are a student. e.g professor: "java Client 4000 0",
	 * student: "java Client 4000 1"
	 * 
	 * Another way to run the program is to just type "java Client" and the client will default the 
	 * port number to 4000 and the client will ask you if you are a student or a teacher.
	 * 
	 * @param args
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		int port = 0;
		try {
			if (args.length < 1) {
				port = 4000;
				System.out.println("0. if Professor | 1. if Student. ");
				System.out.print("\tEnter: ");
				Scanner scan = new Scanner(System.in);
				int option = scan.nextInt();
				if (option == 0) {

					System.out.println("Welcome Professor.");
					new Client(port, false);
				} else if (option == 1) {
					System.out.println("Welcome you neophyte.");
					new Client(port, true);
				}

				else {
					System.out.println("Sorry you didn't follow the protocol, add 1 Student and 0 for Professor.");
					System.exit(0);
				}
			} else {
				port = Integer.parseInt(args[0]);
				if (args.length < 2) {
					System.out.println("Sorry you didn't follow the protocol, add 1 Student and 0 for Professor.");
					System.exit(0);
				}
				int option = Integer.parseInt(args[1]);

				if (option == 0) {

					System.out.println("Welcome professor.");
					new Client(port, false);
				} else if (option == 1) {
					System.out.println("Welcome you neophyte.");
					new Client(port, true);
				}

				else {
					System.out.println("Sorry you didn't follow the protocol, add 1 Student and 0 for Professor.");
					System.exit(0);
				}
			}
		} catch (InputMismatchException e) {
			System.out.println("Give me the correct value");
		}
	}


}
