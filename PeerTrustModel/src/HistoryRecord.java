
public class HistoryRecord {
	private double trustValue;
	private int cycleNum;
	
	
	public HistoryRecord() {
		super();
	}
	public HistoryRecord(double trustValue, int cycleNum) {
		super();
		this.trustValue = trustValue;
		this.cycleNum = cycleNum;
	}
	
	public double getTrustValue() {
		return trustValue;
	}
	public void setTrustValue(double trustValue) {
		this.trustValue = trustValue;
	}
	public int getCycleNum() {
		return cycleNum;
	}
	public void setCycleNum(int cycleNum) {
		this.cycleNum = cycleNum;
	}
	@Override
	public String toString() {
		return "HistoryRecord [trustValue=" + trustValue + ", cycleNum="
				+ cycleNum + "]";
	}	
}
