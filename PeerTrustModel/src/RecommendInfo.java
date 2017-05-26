
public class RecommendInfo {
	private int recommandPeerNo;
	private double recommandTrustValue;
	private int cycleNum;
	
	
	public RecommendInfo() {
		super();
	}
	public RecommendInfo(int recommandPeerNo, double recommandTrustValue,
			int cycleNum) {
		super();
		this.recommandPeerNo = recommandPeerNo;
		this.recommandTrustValue = recommandTrustValue;
		this.cycleNum = cycleNum;
	}
	
	/*
	 * gets/sets
	 */
	public int getRecommandPeerNo() {
		return recommandPeerNo;
	}
	public void setRecommandPeerNo(int recommandPeerNo) {
		this.recommandPeerNo = recommandPeerNo;
	}
	public double getRecommandTrustValue() {
		return recommandTrustValue;
	}
	public void setRecommandTrustValue(double recommandTrustValue) {
		this.recommandTrustValue = recommandTrustValue;
	}
	public int getCycleNum() {
		return cycleNum;
	}
	public void setCycleNum(int cycleNum) {
		this.cycleNum = cycleNum;
	}
	/*
	 * toString()
	 */
	@Override
	public String toString() {
		return "RecommendInfo [recommandPeerNo=" + recommandPeerNo
				+ ", recommandTrustValue=" + recommandTrustValue
				+ ", cycleNum=" + cycleNum + "]";
	}
	
}
