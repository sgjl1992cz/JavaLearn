import java.util.ArrayList;


public class RecommendRecord {
	private int serverPeerNo;
	private ArrayList<RecommendInfo> recommendInfos;
	
	
	

	public RecommendRecord() {
		super();
	}
	public RecommendRecord(int serverPeerNo) {
		super();
		this.serverPeerNo = serverPeerNo;
		this.recommendInfos = new ArrayList<RecommendInfo>();
	}
	
	/*
	 * sets/gets
	 */
	public int getServerPeerNo() {
		return serverPeerNo;
	}
	public void setServerPeerNo(int serverPeerNo) {
		this.serverPeerNo = serverPeerNo;
	}
	public ArrayList<RecommendInfo> getRecommendInfos() {
		return recommendInfos;
	}
	public void setRecommendInfos(ArrayList<RecommendInfo> recommendInfos) {
		this.recommendInfos = recommendInfos;
	}
	/*
	 * toString()
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RecommendRecord [serverPeerNo=" + serverPeerNo
				+ recommendInfos.toString();
	}
	
	
}
