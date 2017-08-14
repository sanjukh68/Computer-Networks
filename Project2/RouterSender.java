import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RouterSender extends Thread {
	private DatagramSocket outSocket;
	private RouterTable tableSent;
	private int portNo;
	DatagramPacket outPacket = null;

	public RouterSender(DatagramSocket outSocket, RouterTable tableSent, int portNo) {
		this.outSocket = outSocket;
		this.tableSent = tableSent;
		this.portNo = portNo;
	}

	public void run() {
		int outputCount = 0;
		try {
			while(true) {
				System.out.println();
				//waiting for 15 seconds before sending router data
				Thread.sleep(15000);
				tableSent.checkEdgeCostChange(tableSent.getRouterName());
				//multicasting address is used
				InetAddress address = InetAddress.getByName("224.0.0.251");
		        ByteArrayOutputStream bOutStream = new ByteArrayOutputStream();
		  	    ObjectOutputStream oOutStream = new ObjectOutputStream(bOutStream);
		  	    oOutStream.writeObject(tableSent);
	            byte[] buff = bOutStream.toByteArray();
	            outPacket = new DatagramPacket(buff, buff.length, address, portNo);
		        outSocket.send(outPacket);
		        System.out.println("------------------ "+ "Output Number "+ ++outputCount+"--------------------");
		        tableSent.printTableDetails(tableSent.getTableDetails());
		        try {
		          Thread.sleep(500);
		        } catch (InterruptedException ie) {
		        }
			}
		}
		catch (IOException exc) {
		      System.out.println(exc);
		      exc.printStackTrace();
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
