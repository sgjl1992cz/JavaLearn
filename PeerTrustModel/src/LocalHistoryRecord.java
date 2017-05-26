import java.util.ArrayList;


public class LocalHistoryRecord {
	private int serverPeerNo;
	private ArrayList<HistoryRecord> feedbackArrayList;
	
	public LocalHistoryRecord() {
		super();
		this.feedbackArrayList = new ArrayList<HistoryRecord>();
	}
	public LocalHistoryRecord(int serverPeerNo) {
		super();
		this.serverPeerNo = serverPeerNo;
		this.feedbackArrayList = new ArrayList<HistoryRecord>();
	}
	
	public void addHistoryRecord(HistoryRecord newHistoryRecord){
		feedbackArrayList.add(newHistoryRecord);
	}
	
	public HistoryRecord lastHistoryRecord(){
		return feedbackArrayList.get(feedbackArrayList.size()-1);
	}
	public double CalLocalHistoryTrustValue(){
		//TODO
		double fake = 0.1;
		return fake;
	}	
	public int getServerPeerNo() {
		return serverPeerNo;
	}
	public void setServerPeerNo(int serverPeerNo) {
		this.serverPeerNo = serverPeerNo;
	}
	public ArrayList<HistoryRecord> getFeedbackArrayList() {
		return feedbackArrayList;
	}
	public void setFeedbackArrayList(ArrayList<HistoryRecord> feedbackArrayList) {
		this.feedbackArrayList = feedbackArrayList;
	}
	@Override
	public String toString() {
		return "LocalHistoryRecord [serverPeerNo=" + serverPeerNo
				+ feedbackArrayList.toString();
	}
	
}

