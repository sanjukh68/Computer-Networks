import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer extends Thread{

	public static final int BUFFER_SIZE = 50000;
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String RESPONSE_OK = "200 OK";
	public static final String RESPONSE_404 = "404 Not Found";
	private ServerSocket serverSocket;
	


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(args.length != 1)
		{
			System.out.println("Invalid input! Usage : java HttpServer <port number>");
		}
		else
		{
			if (!isNumeric(args[0]))
			{
				System.out.println("Please enter valid Port number");
				return;
			}
			try
			{
				int portNo=Integer.parseInt(args[0]);
				//starting server thread
				HttpServer serverThread = new HttpServer(portNo);
				serverThread.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void run()
	{
		while(true){
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Connected to " + server.getRemoteSocketAddress() + " through port number " + serverSocket.getLocalPort() +"\n");
				ObjectInputStream ins = new ObjectInputStream(server.getInputStream());  
				ObjectOutputStream outs = new ObjectOutputStream(server.getOutputStream());
				String method=(String) ins.readObject();
				byte [] buffer = new byte[5000000];
				String[] requestType = new String[3];
				if(method.contains("HTTP")){
					requestType = method.split(" ");

					if ( requestType[0].equals(GET)) 
					{
						File file = new File(requestType[1].toString());
						if ( !(file.isDirectory()) && (file.exists()) )
						{
                     System.out.println("Requested file is " + file.getName());	
                     outs.writeObject("HTTP/1.1 200 OK\r\n");						
							
							BufferedReader br = new BufferedReader(new FileReader(file));
							String line = null;						
							while ((line = br.readLine())!= null) 
							{
								outs.writeObject(line);
							}

							System.out.println("file contents of " + file.getName() + " sent \n");	
							br.close();
						}
						else
						{
                     System.out.println("File " + file.getName() + " doesn't exist in the server \n");	
                     outs.writeObject("HTTP/1.1 404 NOT FOUND\r\n");
							outs.writeObject("File " + file.getName() + " doesn't exist in the server");
							
						}

					}
					else if ( requestType[0].equals(PUT)) 
					{
						try
						{	
                     String newfile = requestType[1].toString().split("\\.")[0] + "_server_file." + requestType[1].toString().split("\\.")[1];
                     System.out.println("File name in Server: " + newfile);
							FileOutputStream fos = new FileOutputStream(newfile);
                     int count;  
							while((count=ins.read(buffer)) >= 0){
								fos.write(buffer, 0, count);  
								System.out.println("File successfully saved! \n");
								outs.writeObject(RESPONSE_OK);
								outs.writeObject("File saved in server successfully");
							}
							fos.close(); 
						}

						catch(Exception e)
						{
                     e.printStackTrace();
							System.out.println("\n");
							System.out.println("Error! File not found\n");
							outs.writeObject(RESPONSE_404);
							outs.writeObject("File not saved in server");	
						}
					}

					else
					{
						System.out.println("Use only GET/PUT commands \n");
					}
				}

				ins.close();
				outs.close();				 
				server.close();
			}
			catch(Exception e)  
			{
				e.printStackTrace();
			}
		}
	}

	public HttpServer(int portNumber) throws IOException
	{

		System.out.println( "Starting server on port: " + portNumber + "\n");
		serverSocket = new ServerSocket( portNumber);
      
      //Setting server timeout to 1 day = 86400 Sec = 86400000 millisec
		serverSocket.setSoTimeout(86400000);
	}

	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    Integer.parseInt(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
