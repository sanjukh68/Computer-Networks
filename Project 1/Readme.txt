Name: Sanju Kurubara Budi Hall Hiriyanna Gowda
UNCC Id: 800953525

Below steps are for executing the Http Server and Client processes.

For GET Request
----------------------------------------------
Step 1: Build the file using following commands
		javac HttpServer.java
		javac HttpClient.java
Step 2: Execute HttpServer using below command. Make sure to enter the port number greater than 5000.
		java HttpServer <portNumber>
		Eg: java Httpserver 5555
Step 3: Execute HttpClient using below command. Make sure to execute client after server is running. Port number should be same as server's port number
		java HttpClient localhost <PortNumber> <RequestType> <filepath>
		Eg: java HttpClient localhost 5555 GET abc.txt
Step 4: If file exists in server it will return the contents to the client and can be contents can be displayed in client prompt


For PUT Request
----------------------------------------------
Step 1: Build the file using following commands
		javac HttpServer.java
		javac HttpClient.java
Step 2: Execute HttpServer using below command. Make sure to enter the port number greater than 5000.
		java HttpServer <portNumber>
		Eg: java Httpserver 5555
Step 3: Execute HttpClient using below command. Make sure to execute client after server is running. Port number should be same as server's port number
		java HttpClient localhost <PortNumber> <RequestType> <filepath>
		Eg: java HttpClient localhost 5555 PUT abc.txt
Step 4: If file exists in client it will send the file to the server and contents can be displayed in server prompt
