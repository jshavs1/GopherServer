import java.io.*;
import java.net.*;
import java.util.*;

class gopher {
	ServerSocket myServer;
	BufferedReader is;
	PrintStream os;
	File directory;
	String orgDir;
	int portNum;
	
	public gopher(String path, int portNum) {
		try {
			this.orgDir = path;
			this.portNum = portNum;
			this.directory = new File(path);
			myServer = new ServerSocket(portNum);
			System.out.println("Server made on port " + portNum);
		}
		catch (IOException e) {
			System.out.println (e);
		}
	}
	
	protected void start() {
		try {
			Socket clientSocket = myServer.accept();
			System.out.println("Client had connected.");
			is = new BufferedReader(new InputStreamReader (clientSocket.getInputStream()));
			os = new PrintStream (clientSocket.getOutputStream());
			os.println(new GopherLine("Welcome to your gopher server! You are in " + this.orgDir, this.portNum).toString());
			os.println(new GopherLine("Type 'goodbye' to close the connection, type 'cd' to return to the main directory.\n", this.portNum));
			List<GopherLine> feed = new ArrayList<GopherLine>();
			feed.addAll(returnDirectory(directory));
			while(true) {
				
				for (GopherLine line: feed){
					os.println(line);
				}
				os.println();
				
				String line = is.readLine();
				File tempDirectory = new File(directory.getPath() +"/"+line);
				
				if (tempDirectory.isDirectory()){
					directory = new File(tempDirectory.getPath());
					feed.clear();
					feed.addAll(returnDirectory(directory));
				}
				else if (tempDirectory.isFile() && tempDirectory.getName().contains(".txt")){
					BufferedReader text = new BufferedReader(new FileReader(tempDirectory));
					String output = text.readLine();
					StringBuilder str = new StringBuilder();
					while(output != null){
						str.append(output + "\n");
						output = text.readLine();
					}
					text.close();
					os.println(str);
					
				}
				else if(line.equals("cd")){
					directory = new File(this.orgDir);
					feed.clear();
					feed.addAll(returnDirectory(directory));
				}
				else if (line.equals("goodbye")){
					is.close();
					os.close();
					clientSocket.close();
					myServer.close();
				}
				else {
					os.println("The file/directory you are looking for does not exist/is not a text file.");
				}
				
				
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public List<GopherLine> returnDirectory(File file){
		String[] list = file.list();
		List<GopherLine> allFiles = new ArrayList<GopherLine>();
		for(String name: list) {
			allFiles.add(new GopherLine(name, file.getPath() + "/" + name, this.portNum));
		}
		return allFiles;
	}
	
	public static void main (String args[]) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the path to the source directory for your gopher server:");
		String path = sc.nextLine();
		System.out.println("Enter the port number you would like to use:");
		int port = sc.nextInt();
		gopher server = new gopher(path, port);
		sc.close();
		server.start();
	}

}


class GopherLine {
	String name;
	char type;
	String path;
	int port;
	File file;
	
	public GopherLine(String name, String path, int port){
		this.name = name;
		this.path = path;
		this.port = port;
		this.file = new File(path);
		if (file.isDirectory()){
			this.type = '1';
		}
		else if(file.isFile() && file.getName().contains(".txt")){
			this.type = '0';
		}
		else if(file.isFile() && file.getName().contains(".gif")){
			this.type = 'g';
		}
		else if(file.isFile() && file.getName().contains(".html")){
			this.type = 'h';
		}
		else if(file.isFile() && (file.getName().contains(".wav") || file.getName().contains(".wave"))){
			this.type = 's';
		}
		else if(file.isFile() && (file.getName().contains(".JPG") || file.getName().contains(".jpg"))){
			this.type = 'I';
		}
		else if(file.isFile() && file.getName().contains(".bin")){
			this.type = '9';
		}
		else{
			this.type = '?';
		}
	}
	
	public GopherLine(String name, int port){
		this.name = name;
		this.port = port;
		this.type = 'i';
		this.path = "";
	}
	
	public String toString(){
		return new String(type + name + "\t" + path + "\t" + port);
	}
}
