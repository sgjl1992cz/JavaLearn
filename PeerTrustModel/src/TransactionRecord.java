
public class TransactionRecord {
	private int serverPeerNo;
	private int recommandPeerNo;
	private double transactionTrustValue;
	private double peerTrustValueReliability;
	
	public TransactionRecord() {
		super();
	}
	public TransactionRecord(int serverPeerNo, int recommandPeerNo,
			double transactionTrustValue, double peerTrustValueReliability) {
		super();
		this.serverPeerNo = serverPeerNo;
		this.recommandPeerNo = recommandPeerNo;
		this.transactionTrustValue = transactionTrustValue;
		this.peerTrustValueReliability = peerTrustValueReliability;
	}
	
	
	
	
	
	public int getServerPeerNo() {
		return serverPeerNo;
	}
	public void setServerPeerNo(int serverPeerNo) {
		this.serverPeerNo = serverPeerNo;
	}
	public int getRecommandPeerNo() {
		return recommandPeerNo;
	}
	public void setRecommandPeerNo(int recommandPeerNo) {
		this.recommandPeerNo = recommandPeerNo;
	}
	public double getTransactionTrustValue() {
		return transactionTrustValue;
	}
	public void setTransactionTrustValue(double transactionTrustValue) {
		this.transactionTrustValue = transactionTrustValue;
	}
	public double getPeerTrustValueReliability() {
		return peerTrustValueReliability;
	}
	public void setPeerTrustValueReliability(double peerTrustValueReliability) {
		this.peerTrustValueReliability = peerTrustValueReliability;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionRecord [serverPeerNo=" + serverPeerNo
				+ ", recommandPeerNo=" + recommandPeerNo
				+ ", transactionTrustValue=" + transactionTrustValue
				+ ", peerTrustValueReliability=" + peerTrustValueReliability
				+ "]";
	}
	
}
