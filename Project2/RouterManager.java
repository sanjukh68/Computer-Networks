import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class RouterManager {

	public static void main(String[] args) throws SocketException, ClassNotFoundException {
		//to enable multicasting
		System.setProperty("java.net.preferIPv4Stack", "true");

		if(args.length < 2) {
			System.out.println("Please enter arguments");
			System.out.println("USAGE: java RouterManager <listening port> <input file>");
			System.exit(0);
		}
		
		ByteArrayInputStream bIStream;
		//multicasting address is used
		InetAddress groupAddress;
		byte[] buffer = new byte[10240];
		DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		MulticastSocket multiSocket = null;
		ObjectInputStream oIStream;
		DatagramSocket outSocket = new DatagramSocket();
		try {
			//Router object that will be sent
			RouterTable rTable = new RouterTable();

			groupAddress = InetAddress.getByName("224.0.0.251");
			multiSocket = new MulticastSocket(Integer.parseInt(args[0]));
			multiSocket.joinGroup(groupAddress);
			rTable.constructInitialTable(args[1]);
			//starting thread for sending process
			new RouterSender(outSocket, rTable, Integer.parseInt(args[0])).start();
			while(true) {
				multiSocket.receive(inPacket);
				byte[] data = inPacket.getData();

				bIStream = new ByteArrayInputStream(data);
				oIStream = new ObjectInputStream(bIStream);
				ArrayList<String> neighbours = new ArrayList<String>(rTable.getdestPrevNeighbors());
				RouterTable objectReceived;
				objectReceived = (RouterTable) oIStream.readObject();

				neighbours.remove(rTable.getRouterName());
				if(neighbours.contains(objectReceived.getRouterName())) {
					rTable.updateRouterTable(rTable, objectReceived);
				}
			}
		}
		catch (IOException exception) {
			System.out.println(exception);
		}
	}
}
