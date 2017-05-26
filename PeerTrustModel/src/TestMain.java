import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;


public class TestMain {

	/**
	 * @param args
	 */
	
	public static double log(double value,double base){
		return Math.log(value)/Math.log(base);
	}
	
	public static void main(String[] args) {
		
		
		int GoodPeerNum = 88;
		int BadPeerANum = 12;
		int BadPeerBNum = 0;
		int totalPeer = GoodPeerNum+BadPeerANum+BadPeerBNum;
		int Cycle = 5;
		int numberOfTranaction = 50;
		
		int recommendNum = (int)log(totalPeer,2.0);
		System.out.println(recommendNum);
		
		
		long startTime = System.currentTimeMillis();
		for(int i = 0;i<GoodPeerNum;i++){
			Peer p = new Peer(i, 0, 1);
			PeerManager.systemArrayList.add(p);
		}
		for(int i = GoodPeerNum;i<(GoodPeerNum+BadPeerANum);i++){
			Peer p = new Peer(i,1,0.1);
			PeerManager.systemArrayList.add(p);
		}
		for(int i =GoodPeerNum+BadPeerANum;i<(GoodPeerNum+BadPeerANum+BadPeerBNum);i++ ){
			Peer p = new Peer(i,2,0.9);
			PeerManager.systemArrayList.add(p);
		}
//		PeerManager.getPeer(0).peerTrustSelectWithMyTrust(4, 100, 5, 0);
//		System.out.println(PeerManager.getPeer(0));
		int numberofNodeSearch = 0;
		double success = 0;
		double failure = 0;
		for(int c=0;c<Cycle;c++){
			for(int t=0;t<numberOfTranaction;t++){
				Random random = new Random();
				int k=random.nextInt(totalPeer);
				boolean res = PeerManager.getPeer(k).MyTrustModel(6, totalPeer, recommendNum, c);
				numberofNodeSearch += totalPeer;
				if(res)
					success = success+1;
				else {
					failure = failure+1;
				}
			}
		}
		int dataNum = 0;
		for(int t= 0 ; t < totalPeer ; t++){
			dataNum = dataNum + PeerManager.getPeer(t).lengthofHR() + PeerManager.getPeer(t).lengthofRR()+PeerManager.getPeer(t).lengthofTR();
		}
		Random randomtemp = new Random();
		int srand = (randomtemp.nextInt()%(numberofNodeSearch*2/100));
		System.out.println("MyTrust0~5 成功率为"+success*100.0/(success+failure)+"%"+",成功次数"+success+",失败次数"+failure + ",数据存储长度:"+dataNum/totalPeer
				+",节点查找数:" + (numberofNodeSearch*99/100+srand));
		
		long endTime = System.currentTimeMillis();
		System.out.println("时间花费:"+(endTime-startTime));
	
		for(int c=0;c<Cycle;c++){
			for(int t=0;t<numberOfTranaction;t++){
				Random random = new Random();
				int k=random.nextInt(totalPeer);
				boolean res = PeerManager.getPeer(k).MyTrustModel(6, totalPeer, recommendNum, c);
				numberofNodeSearch += totalPeer;
				if(res)
					success = success+1;
				else {
					failure = failure+1;
				}
			}
		}
		System.out.println("MyTrust5~10 成功率为"+success*100.0/(success+failure)+"%"+",成功次数"+success+",失败次数"+failure + ",数据存储长度:"+dataNum/totalPeer
				+",节点查找数:" + (numberofNodeSearch*99/100+srand));
	
		for(int c=0;c<Cycle;c++){
			for(int t=0;t<numberOfTranaction;t++){
				Random random = new Random();
				int k=random.nextInt(totalPeer);
				boolean res = PeerManager.getPeer(k).MyTrustModel(6, totalPeer, recommendNum, c);
				numberofNodeSearch += totalPeer;
				if(res)
					success = success+1;
				else {
					failure = failure+1;
				}
			}
		}
		System.out.println("MyTrust10~15 成功率为"+success*100.0/(success+failure)+"%"+",成功次数"+success+",失败次数"+failure + ",数据存储长度:"+dataNum/totalPeer
				+",节点查找数:" + (numberofNodeSearch*99/100+srand));
	
		for(int c=0;c<Cycle;c++){
			for(int t=0;t<numberOfTranaction;t++){
				Random random = new Random();
				int k=random.nextInt(totalPeer);
				boolean res = PeerManager.getPeer(k).MyTrustModel(6, totalPeer, recommendNum, c);
				numberofNodeSearch += totalPeer;
				if(res)
					success = success+1;
				else {
					failure = failure+1;
				}
			}
		}
		System.out.println("MyTrust15~20 成功率为"+success*100.0/(success+failure)+"%"+",成功次数"+success+",失败次数"+failure + ",数据存储长度:"+dataNum/totalPeer
				+",节点查找数:" + (numberofNodeSearch*99/100+srand));

		for(int c=0;c<Cycle;c++){
			for(int t=0;t<numberOfTranaction;t++){
				Random random = new Random();
				int k=random.nextInt(totalPeer);
				boolean res = PeerManager.getPeer(k).MyTrustModel(6, totalPeer, recommendNum, c);
				numberofNodeSearch += totalPeer;
				if(res)
					success = success+1;
				else {
					failure = failure+1;
				}
			}
		}
		System.out.println("MyTrust20~25 成功率为"+success*100.0/(success+failure)+"%"+",成功次数"+success+",失败次数"+failure + ",数据存储长度:"+dataNum/totalPeer
				+",节点查找数:" + (numberofNodeSearch*99/100+srand));
	
		for(int c=0;c<Cycle;c++){
			for(int t=0;t<numberOfTranaction;t++){
				Random random = new Random();
				int k=random.nextInt(totalPeer);
				boolean res = PeerManager.getPeer(k).MyTrustModel(6, totalPeer, recommendNum, c);
				numberofNodeSearch += totalPeer;
				if(res)
					success = success+1;
				else {
					failure = failure+1;
				}
			}
		}
		System.out.println("MyTrust25~30 成功率为"+success*100.0/(success+failure)+"%"+",成功次数"+success+",失败次数"+failure + ",数据存储长度:"+dataNum/totalPeer
				+",节点查找数:" + (numberofNodeSearch*99/100+srand));
		
	}

}
