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
import java.util.Scanner;

public class Client {
	int fileNameLength;
	String fileName;
	int contentLength;
	int port;
	byte[] contentBytes = null;
	byte[] fileNameBytes = null;
	public boolean isStudent = true;
	Socket s;
	DataInputStream input;
	DataOutputStream out;
	PrintWriter pwOutput;
	BufferedReader brInput;

	public Client(int port, Boolean isStudent) throws InterruptedException {
		this.port = port;
		this.isStudent = isStudent;

		try (Socket s = new Socket("localhost", port)) {
			out = new DataOutputStream(s.getOutputStream());
			input = new DataInputStream(s.getInputStream());
			brInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pwOutput = new PrintWriter(s.getOutputStream(), true);
			while (true) {
				int value = isStudent == true ? 1 : 0;
				pwOutput.write(value);
				pwOutput.flush();
				getMenu(brInput);
			}
		} catch (IOException e) {

		}

	}

	public void getMenu(BufferedReader brInput) throws InterruptedException {
		Scanner scan = new Scanner(System.in);
		String msg;
		if (isStudent) {
			int option = 0;

			System.out.println("1.Download a file 2. Exit");
			option = scan.nextInt();
			pwOutput.write(option);
			pwOutput.flush();
	
			switch (option) {
			case 1: {
				System.out.println("Hello");
				downloadList(brInput);
				System.out.print(": ");
				pwOutput.write(scan.nextInt());
				pwOutput.flush();
				downloadFile(input);
				break;
			}
			case 2: {
				System.out.println("Bye! Have a nice day...");
				System.exit(0);
			}
			}
		} else {
			int option = 0;
			while (option != 1 || option != 2 || option != 3) {
				System.out.println("1.Download a file 2. Upload a file 3. Exit");
				option = scan.nextInt();
			}

			switch (option) {
				case 1: {
					System.out.println("Hello");
					downloadList(brInput);
					System.out.print(": ");
					pwOutput.write(scan.nextInt());
					pwOutput.flush();
					downloadFile(input);
					break;
					
				}
				case 2: {
					System.out.print("File name: ");
					uploadFile(scan.next());
					System.out.println("Uploading File...");
					break;
				}
				case 3: {
					System.out.println("Bye! Have a nice day...");
					System.exit(0);
				}
			}

		}
	}

	public void downloadList(BufferedReader brInput) {
		String msg;
		try {
			int length = new Integer(brInput.read());
			System.out.println(length);
			msg = new String(brInput.readLine());
			System.out.println(msg);

			for (int i = 0; i < length; i++) {
				msg = new String(brInput.readLine());
				System.out.println(i + "." + msg);
			}
			msg = "";

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public void downloadFile(DataInputStream input) {
		try {
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
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

	}

	public void uploadFile(String fileName) {
		try {
			// get the file we want to send

			File file = new File("Library/" + fileName);
			FileInputStream fileIn = new FileInputStream(file);
			DataOutputStream output = new DataOutputStream(s.getOutputStream());
			fileName = file.getName();
			System.out.println("trying to send: " + fileName);

			// we send the file name
			byte[] fileNameBytes = fileName.getBytes();
			byte[] sendBytes = new byte[(int) file.length()];
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

	}

	public boolean getIsStudent() {
		return isStudent == true ? true : false;
	}

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		int port = 0;

		if (args.length < 1) {
			port = 4000;
			System.out.println("Type 0 if you are a Professor and 1 if you are a Student. ");
			System.out.print("-> ");
			Scanner scan = new Scanner(System.in);
			int option = scan.nextInt();
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

	}

	public void whoAreYou(String option) {

	}

}
