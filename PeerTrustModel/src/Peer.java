import java.util.ArrayList;
import java.util.Random;

public class Peer {
	
	/**
	 * 节点网络属性
	 */
	private int peerNo;//节点编号
	private int peerType;//节点类别
	private double  trueValue;//节点真实信任值
	private ArrayList<Integer> peerNeighbours;//邻居节点编号数组
	/**
	 * 节点本地存储
	 */
	private ArrayList<LocalHistoryRecord> peerLocalHistoryRecords;
	private ArrayList<RecommendRecord> peerRecommendRecords;
	private ArrayList<PeerTrustReliability> peerTrustReliabilities;
	/**
	 * 本地缓存
	 */
	public int lengthofHR() {
		return peerLocalHistoryRecords.size();
	}
	public int lengthofRR() {
		return peerRecommendRecords.size();		
	}
	public int lengthofTR() {
		return peerTrustReliabilities.size();
	}
	
	private ArrayList<ArrayList<TransactionRecord>> listofTransactionRecordList;
	
	public Peer() {
		super();
	}
	public Peer(int peerNo,int peerType, double trueValue) {
		super();
		this.peerNo = peerNo;
		this.peerType = peerType;
		this.trueValue = trueValue;
		
		peerNeighbours = new ArrayList<Integer>();
		peerLocalHistoryRecords = new ArrayList<LocalHistoryRecord>();
		peerRecommendRecords = new ArrayList<RecommendRecord>();
		peerTrustReliabilities = new ArrayList<PeerTrustReliability>();
		
		listofTransactionRecordList = new ArrayList<ArrayList<TransactionRecord>>();
		
	}
	
	/***************************************************************************
	 * 直接信任 
	 ********************************************************************/ 
	/*计算直接信任-信任值
	 *
	 */
	public double calDirectTrustValue(int targerPeerNo,int cur_cycleNum){
		double directTrustValue = 0;
		ArrayList<Double> weightArray = new ArrayList<Double>();
		/*查找记录项  localHistoryRecords中记录的服务节点为targerPeerNo的记录 
		 * 如果有  按照公式加权计算
		 * 如果没有  directTrustValue = 0.5
		 */
		boolean isTransacted=false;
		int index=-1;
		for(int i=0;i<peerLocalHistoryRecords.size();i++){
			if (peerLocalHistoryRecords.get(i).getServerPeerNo() == targerPeerNo) {
				isTransacted = true;
				index = i;
				break;
			}
		}
		if(isTransacted == false){
			directTrustValue = 0.5;
		}else{
			ArrayList<HistoryRecord> tempHistoryRecord = new ArrayList<HistoryRecord>();
			tempHistoryRecord = peerLocalHistoryRecords.get(index).getFeedbackArrayList();
			/*计算时间因素权值*/
			double weight_sum = 0;
			for(int i=0;i<tempHistoryRecord.size();i++){
				int cyclenum = tempHistoryRecord.get(i).getCycleNum();
				double tempweight = Math.exp(-0.05*(cur_cycleNum-cyclenum));                /*****时间衰减因子*******/
				weight_sum = weight_sum + tempweight;
				weightArray.add(tempweight);
			}
	//		System.out.println("weight_sum = "+weight_sum);
			/*计算直接信任信任值*/
			directTrustValue = 0;
			for(int i=0;i<tempHistoryRecord.size();i++){
				double wd = weightArray.get(i);
				double h = tempHistoryRecord.get(i).getTrustValue();
				directTrustValue = directTrustValue+wd/weight_sum*h;
			}
		}
		return directTrustValue;
	}
	/*计算直接信任-可靠度
	 *
	 */
	public double calDirectTrustReliability(double directTrustValue,int targetPeerNo){
		double directTrustReliability = 0;
		//查找对应节点记录
		boolean isTransacted = false;
		int index=-1;
		for(int i=0;i<peerLocalHistoryRecords.size();i++){
			if(peerLocalHistoryRecords.get(i).getServerPeerNo() == targetPeerNo){
				index = i;
				isTransacted = true;
				break;
			}
		}
		if(isTransacted==false){//如果没有记录;directTrustReliability = 0
			directTrustReliability=0;
			return directTrustReliability;
		}
		//至少有一条记录
		ArrayList<HistoryRecord> h = new ArrayList<HistoryRecord>();
		h = peerLocalHistoryRecords.get(index).getFeedbackArrayList();
		double temp = 0;
		for(int i=0;i<h.size();i++){
			temp = temp + (directTrustValue-h.get(i).getTrustValue())*(directTrustValue-h.get(i).getTrustValue());
		}
		directTrustReliability = 1/(temp+1);
		return directTrustReliability;
	}
	
	/***************************************************************************
	 * 推荐信任 
	 ********************************************************************/ 
	/* 节点推荐信息
	 * 节点就节点编号为TargetPeerNo进行推荐，return类型为TransactionRecord 
	 * [诚实][回答直接信任结果]
	 * int serverPeerNo = 目标节点;
	 * int recommandPeerNo = 自己;
	 * double transactionTrustValue = 直接信任的信任值;
	 * double peerTrustValueReliability = 直接信任的可靠度;
	 */
	public TransactionRecord peerRecommand(int targetPeerNo,int curCycle){
		TransactionRecord res = new TransactionRecord();
		res.setServerPeerNo(targetPeerNo);
		res.setRecommandPeerNo(this.peerNo);
		
		double trustValue = calDirectTrustValue(targetPeerNo,curCycle);
		double trustReliability = calDirectTrustReliability(trustValue,targetPeerNo);
		Peer temPeer = PeerManager.getPeer(targetPeerNo);
		if(peerType == 1||peerType == 2){
			//恶意节点类型A，进行恶意服务（trueValue低，对恶意节点A和恶意节点B进行信任值为1的推荐,对其他节点进行恶意推荐）
			if (temPeer.getPeerType()==1||temPeer.getPeerType()==2) {
				trustValue = 1;
				trustReliability = 1;
			}else{
				trustValue = 0;//恶意推荐
				trustReliability = 1;
			}		
		}
		res.setTransactionTrustValue(trustValue);
		res.setPeerTrustValueReliability(trustReliability);
		return res;	
	}
	/*
	 * 广度优先遍历（BFS）查找目标节点
	 * 参数 搜索目标节点
	 * 返回推荐节点名单
	 */
	public ArrayList<Integer> getRecommandList(int recommandNumber,int MaxPeerNumber){
		/*
		 * 首先自己的邻居编号加入
		 * 其次，对每个邻居递归函数getRecommandList 已加入的节点不再加入递归
		 * 如果发现目标节点立即停止本路径递归，不再深入
		 * 【黑想法】 随便弄些节点算了
		 * */
		ArrayList<Integer> res = new ArrayList<Integer>();
		Random random  = new Random();
		for(int t=0;t<recommandNumber;t++){
			boolean isNOT = false;
			int temp = random.nextInt(MaxPeerNumber);
			do{
				temp = random.nextInt(MaxPeerNumber);
				isNOT = false;
				for(int i=0;i<res.size();i++){
					if (res.get(i)==temp||temp==peerNo) {
						isNOT = true;
						break;
					}
				}
			}while(isNOT);
			res.add(new Integer(temp));
		}
		//System.out.println("res:"+res.toString());
		return res;
	}
	/*获得所有关于targetPeerNo的推荐记录   本地+外部*/
	public ArrayList<TransactionRecord> getTransactionRecords(ArrayList<Integer> recommandPeerList,int targetPeerNo,int curcycle){
		ArrayList<TransactionRecord> transactionRecords = new ArrayList<TransactionRecord>();
		//*本地推荐信任*//
		for(int i = 0;i<peerRecommendRecords.size();i++){
			if(peerRecommendRecords.get(i).getServerPeerNo() == targetPeerNo){
				ArrayList<RecommendInfo> targetInfos = peerRecommendRecords.get(i).getRecommendInfos();
				for(int j = 0; j<targetInfos.size();j++){
					TransactionRecord tempRecord = new TransactionRecord();
					tempRecord.setServerPeerNo(targetPeerNo);
					tempRecord.setRecommandPeerNo(targetInfos.get(j).getRecommandPeerNo());
					tempRecord.setTransactionTrustValue(targetInfos.get(j).getRecommandTrustValue());
					tempRecord.setPeerTrustValueReliability(Math.exp(-0.05*targetInfos.get(j).getCycleNum()));/*时间衰减因子*/
					transactionRecords.add(tempRecord);
				}
			}
			break;
		}
		/*外部推荐记录获得与与本地合并*/
		for(int i = 0;i<recommandPeerList.size();i++){
			Peer recommandPeer = PeerManager.getPeer(recommandPeerList.get(i));
			TransactionRecord tempRecord = recommandPeer.peerRecommand(targetPeerNo,curcycle);
			boolean ishad = false;
			for(int j=0;j<transactionRecords.size();j++){
				if(tempRecord.getRecommandPeerNo() == transactionRecords.get(j).getRecommandPeerNo()){
					ishad = true;
					transactionRecords.get(j).setTransactionTrustValue(tempRecord.getTransactionTrustValue());
					transactionRecords.get(j).setPeerTrustValueReliability(tempRecord.getPeerTrustValueReliability());
					break;
				}
			}
			if(!ishad){//将记录加入
				transactionRecords.add(tempRecord);
			}
		}
		/*至此，全部的推荐数据存储在transactionRecords中*/
		return transactionRecords;
	}
	/*计算推荐信任-信任值
	 *
	 */
	public double calRecommandTrustValue(ArrayList<TransactionRecord> transactionRecords){
		double weight_totoal = 0;
		ArrayList<Double> wrArrayList = new ArrayList<Double>();
		for(int i=0;i<transactionRecords.size();i++){
			double wt = transactionRecords.get(i).getPeerTrustValueReliability();
			if(wt == 0){//0会出问题
				wt = 0.01;
			}
			double wr = 0.5;
			for(int j=0;j<peerTrustReliabilities.size();j++){
				if(transactionRecords.get(i).getRecommandPeerNo() == peerTrustReliabilities.get(j).getRecommandPeerNo()){
					wr = peerTrustReliabilities.get(j).getPeerRecommandReliability();
				}
			}
			double wz = wt*wr;
			wrArrayList.add(wz);
			weight_totoal = weight_totoal+wz;
		}
		if(weight_totoal == 0){//如果推荐信任值总权值为0，说明系统刚刚开始，还没有记录，随便取值
			weight_totoal = 1;
		}
		double recommandTrustValue = 0;
		for(int i=0;i<transactionRecords.size();i++){
			double wz = wrArrayList.get(i);
			double trustvalue = transactionRecords.get(i).getTransactionTrustValue();
			recommandTrustValue = recommandTrustValue + wz/weight_totoal*trustvalue;
		}
		return recommandTrustValue;
	}
	/*计算推荐信任-可靠度
	 *
	 */
	public double calRecommandTrustReliability(double recommandTrustValue, ArrayList<TransactionRecord> transactionRecords){
		double recommandTrustReliability = 0;
		double temp = 0;
		for(int i = 0;i<transactionRecords.size();i++){
			double ri = transactionRecords.get(i).getTransactionTrustValue();
			temp = temp + (recommandTrustValue-ri)*(recommandTrustValue-ri);
		}
		recommandTrustReliability = 1/(temp+1);
		return recommandTrustReliability;
	}
	/***************************************************************************
	 * 综合信任 
	 ********************************************************************/ 
	/*
	 *  计算综合信任值
	 *  tpnp 目标节点
	 *  recommandnum 推荐节点数量
	 *  peerMaxNumber 节点数量
	 */
	public double calSynthesizeTrust(int tpno,ArrayList<Integer> recommandList,int cur_cycleNum){
		double synthesizeTrust = 0;
		
		double directTrustValue = calDirectTrustValue(tpno,cur_cycleNum);
		double directTrustReliability = calDirectTrustReliability(directTrustValue, tpno);
		
		ArrayList<TransactionRecord> transactionRecords = getTransactionRecords(recommandList, tpno,cur_cycleNum);
		listofTransactionRecordList.add(transactionRecords);
	
		double recommandTrustValue = calRecommandTrustValue(transactionRecords);
		double recommandTrustReliability = calRecommandTrustReliability(recommandTrustValue, transactionRecords);
		double temp = directTrustReliability+recommandTrustReliability;
		if(temp == 0)
			synthesizeTrust = 0.5;
		else{
			 double a = directTrustReliability/temp;
			synthesizeTrust = a*directTrustValue+(1-a)*recommandTrustValue;
	//		System.out.println("a = "+a);
		}
	//	System.out.println("directTrustValue = "+directTrustValue);
	//	System.out.println("recommandTrustValue ="+recommandTrustValue);
	//	System.out.println("directTrustReliability = "+directTrustReliability);
	//	System.out.println("recommandTrustReliability ="+recommandTrustReliability);
	//	System.out.println("synthesizeTrust ="+synthesizeTrust);
		return synthesizeTrust;
	}
	
	public ArrayList<Integer> selectTargetPeerNoList(int numberOfTarget,int maxPeerNumber){
		Random random = new Random();
		ArrayList<Integer> targetArrayList = new ArrayList<Integer>();
		for(int k=0;k<numberOfTarget;k++){
			boolean isIn = false;
			int temp;
			do{
				isIn = false;
				temp  = random.nextInt(maxPeerNumber);
				for(int i=0;i<targetArrayList.size();i++){
					if (temp == targetArrayList.get(i)) {
						isIn = true;
						break;
					}
				}
			}while(isIn == true);
			targetArrayList.add(new Integer(temp));
		}
		return targetArrayList;
	}
	
	
	
	public int randSelect(ArrayList<Double> trustValueArrayList, double trustTotal, int numberOfTarget){
		Random random2 = new Random();
		double point = random2.nextDouble()*trustTotal;
		double now = 0;//当前概率段
		int choosed = 0;//选中交互节点
		int pos = 0;
		for(pos = 0;pos<numberOfTarget;pos++){
			double low = now;
			double up = now + trustValueArrayList.get(pos);
			if(point>=low&&point<up){
				choosed = pos;
				break;
			}
			now = up;
		}
		return choosed;
	}
	
	
	
	
	/*
	 * 一个节点根据MyTrust算法 计算多个节点的综合信任值，并根据概率进行选择
	 * true 交互结果正确  
	 * false 交互结果错误
	 */
	public boolean MyTrustModel(int numberOfTarget ,int maxPeerNumber,int recommandNumber,int curCycleNum){
		/*初始化*/
		double trustTotal = 0;
		listofTransactionRecordList.clear();
		
		/*选择多个节点*/
		ArrayList<Integer> targetArrayList = selectTargetPeerNoList(numberOfTarget, maxPeerNumber);
		/*计算综合信任值*/
		ArrayList<Double> trustValueArrayList = new ArrayList<Double>();
		/*获得节点推荐列表*/
		ArrayList<Integer> recommandList = getRecommandList(recommandNumber, maxPeerNumber);
		
		
		for(int i=0;i<numberOfTarget;i++){
			//targetArrayList中第i个节点的信任，推荐列表为recommandList,交互轮次为curCycleNum
			double tempValue = calSynthesizeTrust(targetArrayList.get(i), recommandList, curCycleNum);
			trustTotal = trustTotal + tempValue;
			trustValueArrayList.add(tempValue);
		}
		/*做成概率*/
		int pos = randSelect(trustValueArrayList, trustTotal, numberOfTarget);
		
		int choosedPeerNo = targetArrayList.get(pos);
		
		/*交互是否成功*/
		boolean isGood = true;
		if(PeerManager.getPeer(choosedPeerNo).getPeerType() == 0||PeerManager.getPeer(choosedPeerNo).getPeerType() == 2){
			isGood = true;
		}else{
			isGood = false;
		}
		/*update*/
		update(choosedPeerNo,curCycleNum,listofTransactionRecordList.get(pos));
		return isGood;
	}
	/***************************************************************************
	 * 信任更新 
	 ********************************************************************/ 
	/*
	 * 将PeerManager.get(targetPeer).TrustValue 加入 LocalHistoryRecord  targetPeer==serverPeerNo historyRecord
	 */
	public void update(int tranactedPeerNo,int cyclnum,ArrayList<TransactionRecord> nameList){
		
		Peer tranactedPeer = PeerManager.getPeer(tranactedPeerNo);
		/*加入历史记录*/
		updatalocalHistoryValue(tranactedPeer.getPeerNo(),tranactedPeer.getTrueValue(),cyclnum);	  

		/*向其它节点推荐*/
		recommandToOthers(tranactedPeerNo, nameList,cyclnum,tranactedPeer.getTrueValue());
		
		/*修改节点可靠度*/
		updataPeerReliability(0.70, nameList, tranactedPeer.getTrueValue());/*信任容忍度0.5*/
	}
	
	void updatalocalHistoryValue(int tranactedPeerNo,double realValue, int cyclenum){
		addToPeerLocalHistoryList(tranactedPeerNo, realValue, cyclenum);
	}
	void recommandToOthers(int tranactedPeerNo,ArrayList<TransactionRecord> nameList,int cyclnum,double realValue){
		for(int i=0;i<nameList.size();i++){
			int recommandPno = nameList.get(i).getRecommandPeerNo();
			int targetPeerType = PeerManager.getPeer(nameList.get(i).getServerPeerNo()).getPeerType();
			/*向节点recommandPno推荐信任*/
			ArrayList<RecommendRecord> recommandPeerRecommandRecords = 
					PeerManager.getPeer(recommandPno).peerRecommendRecords;
			/*是否有tranacted节点的推荐记录，如果有，是否有来自peerno的记录，如果有，修改记录，否则添加新纪录*/
			boolean tranactedisInList = false;
			for(int j = 0; j<recommandPeerRecommandRecords.size();j++){
				if(recommandPeerRecommandRecords.get(j).getServerPeerNo() == tranactedPeerNo){	
							/*是否有来自peerno的记录*/
					ArrayList<RecommendInfo> jInfos =  recommandPeerRecommandRecords.get(j).getRecommendInfos();
					boolean isinfoshavepeerno = false;
					for(int k = 0;k<jInfos.size();k++){
						if(jInfos.get(k).getRecommandPeerNo() == peerNo){
							/*有推荐记录,修改推荐记录*/
							isinfoshavepeerno = true;
							jInfos.get(k).setCycleNum(cyclnum);
							jInfos.get(k).setRecommandTrustValue(realValue);
							if(peerType==0||peerType==1){
								if(targetPeerType == 0 || targetPeerType == 1){
									jInfos.get(k).setRecommandTrustValue(1);
								}else {
									jInfos.get(k).setRecommandTrustValue(0);
								}
							}
							break;
						}
					}
					if(isinfoshavepeerno == false){
						RecommendInfo temp = new RecommendInfo();
						temp.setCycleNum(cyclnum);
						temp.setRecommandPeerNo(peerNo);
						temp.setRecommandTrustValue(realValue);
						if(peerType==0||peerType==1){
							if(targetPeerType == 0 || targetPeerType == 1){
								temp.setRecommandTrustValue(1);
							}else {
								temp.setRecommandTrustValue(0);
							}
						}
						jInfos.add(temp);
					}
					tranactedisInList = true;
					break;
				}
			}
			if(tranactedisInList == false){
				RecommendInfo tempInfo = new RecommendInfo();
				tempInfo.setCycleNum(cyclnum);
				tempInfo.setRecommandPeerNo(peerNo);
				tempInfo.setRecommandTrustValue(realValue);
				if(peerType==0||peerType==1){
					if(targetPeerType == 0 || targetPeerType == 1){
						tempInfo.setRecommandTrustValue(1);;
					}else {
						tempInfo.setRecommandTrustValue(0);;
					}
				}
				
				RecommendRecord tempRecord = new RecommendRecord();
				tempRecord.setServerPeerNo(tranactedPeerNo);
				ArrayList<RecommendInfo> tArrayList = new ArrayList<RecommendInfo>();
				tempRecord.setRecommendInfos(tArrayList);
				tempRecord.getRecommendInfos().add(tempInfo);
				peerRecommendRecords.add(tempRecord);
			}
		}		
	}
	
	void updataPeerReliability(double factor_tolerate,ArrayList<TransactionRecord> nameList,double realValue){
		for(int i=0;i<nameList.size();i++){
			/*计算偏差*/
			double diff = Math.abs(nameList.get(i).getTransactionTrustValue()-realValue);
			/*查找节点记录,如果没有，那么添加*/
			int indexInLocalReliabilityList = 
					getOnePeerTrustReliability(nameList.get(i).getRecommandPeerNo());//获得推荐节点本地存储位置
			if(indexInLocalReliabilityList == -1){
				/*没有记录*/
				PeerTrustReliability temp = new PeerTrustReliability();
				temp.setRecommandPeerNo(nameList.get(i).getRecommandPeerNo());
				double wr = 0.5;
				double wd = nameList.get(i).getPeerTrustValueReliability();
				if(diff>factor_tolerate){/*推荐失败*/
					
					temp.setRecomandFailure(temp.getRecomandFailure()+1);
					double fengmu = 1-factor_tolerate;
					double offset = (1/fengmu)*diff-factor_tolerate/fengmu;
					wr = wr - wr*offset*wd;
					temp.setPeerRecommandReliability(wr);
				}else{/*推荐成功*/
					
					temp.setRecommandSuccess(temp.getRecommandSuccess()+1);
					double offset = (-1/factor_tolerate)*diff+1;
					wr = wr + (1-wr)*offset*wd;
					temp.setPeerRecommandReliability(wr);
				}
				peerTrustReliabilities.add(temp);
			}else{
				PeerTrustReliability temp = peerTrustReliabilities.get(indexInLocalReliabilityList);
				double n = 1+temp.getRecomandFailure()+temp.getRecommandSuccess();
				double wr = temp.getPeerRecommandReliability();
				double wd = nameList.get(i).getPeerTrustValueReliability();
				if(diff>factor_tolerate){/*推荐失败*/
					
					temp.setRecomandFailure(temp.getRecomandFailure()+1);
					
					double f = temp.getRecomandFailure()/n;
					wr = wr - wr*f*(1-diff/factor_tolerate)*wd;
					temp.setPeerRecommandReliability(wr);
				}else{/*推荐成功*/
					
					temp.setRecommandSuccess(temp.getRecommandSuccess()+1);
					
					double s = temp.getRecommandSuccess()/n;
					wr = wr + (1-wr)*s*(1-diff/factor_tolerate)*wd;
					temp.setPeerRecommandReliability(wr);
				}
			}		
		}
	}

	public int getOnePeerTrustReliability(int indexRecommandNo) {
		int index = -1;
		for(int i = 0;i<peerTrustReliabilities.size();i++){
			if (indexRecommandNo == peerTrustReliabilities.get(i).getRecommandPeerNo()) {
				index = i;
				return index;
			}
		}
		return index;
	}
	/***************************************************************************
	 * 功能 
	 ********************************************************************/ 
	/*
	 * 判断是否是邻居节点
	 */
	public boolean isNeigbour(int peerNo){
		boolean res = false;
		for(int i=0;i<peerNeighbours.size();i++){
			if(peerNeighbours.get(i)==peerNo){
				res = true;
				return res;
			}
		}
		return res;
	}
	/*
	 * 添加邻居节点
	 */
	public void addNeighbour(int neighbourPeerNo){
		if(isNeigbour(peerNo)==false){
			peerNeighbours.add(neighbourPeerNo);
			if(PeerManager.getPeer(neighbourPeerNo).isNeigbour(this.peerNo) == false){
				PeerManager.getPeer(neighbourPeerNo).addNeighbour(this.peerNo);
			}
		}
	}
	
	
	/*将交互结果(targetPeerNo,trueValue,cyclenum,)加入到peerLocalHistoryList*/
	void addToPeerLocalHistoryList(int targetPeerNo,double trueValue,int cycleNum){
		boolean isIn = false;
		int index = -1;
		for(int i = 0;i<peerLocalHistoryRecords.size();i++){
			if (peerLocalHistoryRecords.get(i).getServerPeerNo() == targetPeerNo) {
				isIn = true;
				index = i;
				break;
			}
		}
		if(isIn){
			HistoryRecord temp = new HistoryRecord(trueValue, cycleNum);
			peerLocalHistoryRecords.get(index).addHistoryRecord(temp);
		}else{
			LocalHistoryRecord ltemp = new LocalHistoryRecord(targetPeerNo);
			HistoryRecord temp = new HistoryRecord(trueValue, cycleNum);
			ltemp.getFeedbackArrayList().add(temp);
			/*将LocalHistoryRecord加入到节点peerLocalHistoryRecords中*/
			peerLocalHistoryRecords.add(ltemp);
		}
	}
	
	
	/*
	 * gets/sets
	 */
	public int getPeerNo() {
		return peerNo;
	}
	public void setPeerNo(int peerNo) {
		this.peerNo = peerNo;
	}
	public int getPeerType() {
		return peerType;
	}
	public void setPeerType(int peerType) {
		this.peerType = peerType;
	}
	public double getTrueValue() {
		return trueValue;
	}
	public void setTrueValue(double trueValue) {
		this.trueValue = trueValue;
	}
	public ArrayList<Integer> getPeerNeighbours() {
		return peerNeighbours;
	}
	public void setPeerNeighbours(ArrayList<Integer> peerNeighbours) {
		this.peerNeighbours = peerNeighbours;
	}
	
	/**
	 * @return the peerLocalHistoryRecords
	 */
	public ArrayList<LocalHistoryRecord> getPeerLocalHistoryRecords() {
		return peerLocalHistoryRecords;
	}
	/**
	 * @param peerLocalHistoryRecords the peerLocalHistoryRecords to set
	 */
	public void setPeerLocalHistoryRecords(
			ArrayList<LocalHistoryRecord> peerLocalHistoryRecords) {
		this.peerLocalHistoryRecords = peerLocalHistoryRecords;
	}
	/**
	 * @return the peerRecommendRecords
	 */
	public ArrayList<RecommendRecord> getPeerRecommendRecords() {
		return peerRecommendRecords;
	}
	/**
	 * @param peerRecommendRecords the peerRecommendRecords to set
	 */
	public void setPeerRecommendRecords(
			ArrayList<RecommendRecord> peerRecommendRecords) {
		this.peerRecommendRecords = peerRecommendRecords;
	}
	/**
	 * @return the peerTrustReliabilities
	 */
	public ArrayList<PeerTrustReliability> getPeerTrustReliabilities() {
		return peerTrustReliabilities;
	}
	/**
	 * @param peerTrustReliabilities the peerTrustReliabilities to set
	 */
	public void setPeerTrustReliabilities(
			ArrayList<PeerTrustReliability> peerTrustReliabilities) {
		this.peerTrustReliabilities = peerTrustReliabilities;
	}

	/*
	 * ToString
	 */
	@Override
	public String toString() {
		return "Peer [peerNo=" + peerNo + ", peerType=" + peerType
				+ ", trueValue=" + trueValue + ", peerNeighbours="
				+ peerNeighbours + ",\n peerLocalHistoryRecords="
				+ peerLocalHistoryRecords + ",\n peerRecommendRecords="
				+ peerRecommendRecords + ",\n peerTrustReliabilities="
				+ peerTrustReliabilities + "]";
	}
	
}
