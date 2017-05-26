
public class PeerTrustReliability {
	private int recommandPeerNo;
	private double peerRecommandReliability;
	private int recommandSuccess;
	private int recomandFailure;
	
	
	public PeerTrustReliability() {
		super();
		this.recommandSuccess = 0;
		this.recomandFailure = 0;
	}

	public PeerTrustReliability(int recommandPeerNo,
			double peerRecommandReliability, int recommandSuccess,
			int recomandFailure) {
		super();
		this.recommandPeerNo = recommandPeerNo;
		this.peerRecommandReliability = peerRecommandReliability;
		this.recommandSuccess = recommandSuccess;
		this.recomandFailure = recomandFailure;
	}

	public int getRecommandPeerNo() {
		return recommandPeerNo;
	}

	public void setRecommandPeerNo(int recommandPeerNo) {
		this.recommandPeerNo = recommandPeerNo;
	}

	public double getPeerRecommandReliability() {
		return peerRecommandReliability;
	}

	public void setPeerRecommandReliability(double peerRecommandReliability) {
		this.peerRecommandReliability = peerRecommandReliability;
	}

	public int getRecommandSuccess() {
		return recommandSuccess;
	}

	public void setRecommandSuccess(int recommandSuccess) {
		this.recommandSuccess = recommandSuccess;
	}

	public int getRecomandFailure() {
		return recomandFailure;
	}

	public void setRecomandFailure(int recomandFailure) {
		this.recomandFailure = recomandFailure;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PeerTrustReliability [recommandPeerNo=" + recommandPeerNo
				+ ", peerRecommandReliability=" + peerRecommandReliability
				+ ", recommandSuccess=" + recommandSuccess
				+ ", recomandFailure=" + recomandFailure + "]";
	}
	
	

}
