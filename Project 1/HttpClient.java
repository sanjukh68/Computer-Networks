import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HttpClient {
	
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String RESPONSE_OK = "HTTP/1.1 200 OK\r\n";
	public static final String RESPONSE_404 = "HTTP/1.1 404 NOT FOUND\r\n";
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if(args.length != 4){
			System.out.println("Program takes 4 arguments. Usage: java HttpClient localhost <port number> <Request Type(GET/PUT)> <filepath>");
		}else{
			if (!isNumeric(args[1]))
			{
				System.out.println("Please enter valid Port no");
				return;
			}
         String hostName = args[0];
			int portNo = Integer.parseInt(args[1]);
			String requestType = args[2];
			String filePath =  args[3];
			ObjectOutputStream outs;
			ObjectInputStream ins;
			
		try {
                        
			Socket socket = new Socket(hostName, portNo);
			System.out.println("\n" + "Connected to Server on port " + portNo);
			
			outs = new ObjectOutputStream(socket.getOutputStream());   
			ins = new ObjectInputStream(socket.getInputStream());
			
			if(requestType.equals(GET)){
				System.out.println(requestType + " " + filePath + " HTTP/1.1");
				System.out.println("Host:" + hostName);
				outs.writeObject(requestType + " " + filePath + " HTTP/1.1");
				outs.writeObject("Host:" + hostName);
				
				try {
					
					String response = (String) ins.readObject();

					System.out.println("Status: " + response + "\n");
					if ( response.equals(RESPONSE_OK)) 
					{
						System.out.println("Requested file contains : \n");
						System.out.println("-------------------------------------------------");
						try
						{
							String line = (String) ins.readObject();
							while (line != null) 
							{
								System.out.println(line);
								line = (String) ins.readObject();
							}
                                                        
						}
						
						catch(EOFException e)
						{
                     System.out.println("-------------------------------------------------");
							System.out.println("\nFile is received\n");
						}
						
					}
					else {
						System.out.println(response);
						}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}
			else if ( requestType.equals(PUT)) 
			{
				File file = new File(filePath); 
				if (!(file.isDirectory()) && (file.exists()))
				{
               System.out.println("Sending the file: " + file.getName() + " to server....\n");  
					outs.writeObject(requestType + " " + filePath + " HTTP/1.1");
					
					InputStream fis = new FileInputStream(file);  
					byte [] buffer = new byte[(int)file.length()];

					int noOfBytes = fis.read(buffer);
					try{
						if(noOfBytes > 0)
						{  
							outs.write(buffer, 0, noOfBytes);
							outs.flush();
							System.out.println("File: " + file.getName() + " Sent\n\n");
						}  
					}
					catch(Exception e)
					{
						System.out.println("\n");
					}
					
					fis.close();
					System.out.println("Status Code from server: " + ins.readObject() + "\n");
					System.out.println("Status Message from server: " + ins.readObject() + "\n");
				}
				
				else
				{
					System.out.println("File doesn't exist\n");
					outs.writeObject("Invalid File");
				}
			
			}
			else
			{
				System.out.println("Invalid Request type. Use only GET/PUT \n");
				outs.writeObject("Invalid Request type");
			}
			
			System.out.println("Closing the Socket");
			
			outs.close();
			ins.close();			
			socket.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		} 
		}

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
