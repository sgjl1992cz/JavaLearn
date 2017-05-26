import java.util.ArrayList;


public class testFunction1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		for(int i = 0;i<10;i++){
			Peer p = new Peer(i, 0, 1);
			PeerManager.systemArrayList.add(p);
		}
		Peer p2 = new Peer(10,1,0);
		PeerManager.systemArrayList.add(p2);
		
		Peer p3 = new Peer(11,2,1);
		PeerManager.systemArrayList.add(p3);
		
		Peer temp = PeerManager.getPeer(0);
		temp.addToPeerLocalHistoryList(10, 0, 0);
		temp.addToPeerLocalHistoryList(10, 0, 1);
		temp.addToPeerLocalHistoryList(10, 0, 2);
		temp.addToPeerLocalHistoryList(10, 0, 3);
		Peer temp2 = PeerManager.getPeer(1);
		temp2.addToPeerLocalHistoryList(10, 0, 0);
		ArrayList<Integer> recommandList = new ArrayList<Integer>();
		recommandList.add(1);
		recommandList.add(2);
		recommandList.add(3);
		recommandList.add(4);
		System.out.println("recommand = " + temp2.peerRecommand(10, 1));
		double res = temp.calSynthesizeTrust(10, recommandList, 4);
		
	    System.out.println(res);
	}

}
