import java.util.ArrayList;


public class PeerManager {
	static ArrayList<Peer> systemArrayList = new ArrayList<Peer>();
	
	
	static Peer getPeer(int peerno){
		return systemArrayList.get(peerno);
	}
}
