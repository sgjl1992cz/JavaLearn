import java.util.ArrayList;
import java.util.Random;

public class Peer {
	
	/**
	 * �ڵ���������
	 */
	private int peerNo;//�ڵ���
	private int peerType;//�ڵ����
	private double  trueValue;//�ڵ���ʵ����ֵ
	private ArrayList<Integer> peerNeighbours;//�ھӽڵ�������
	/**
	 * �ڵ㱾�ش洢
	 */
	private ArrayList<LocalHistoryRecord> peerLocalHistoryRecords;
	private ArrayList<RecommendRecord> peerRecommendRecords;
	private ArrayList<PeerTrustReliability> peerTrustReliabilities;
	/**
	 * ���ػ���
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
	 * ֱ������ 
	 ********************************************************************/ 
	/*����ֱ������-����ֵ
	 *
	 */
	public double calDirectTrustValue(int targerPeerNo,int cur_cycleNum){
		double directTrustValue = 0;
		ArrayList<Double> weightArray = new ArrayList<Double>();
		/*���Ҽ�¼��  localHistoryRecords�м�¼�ķ���ڵ�ΪtargerPeerNo�ļ�¼ 
		 * �����  ���չ�ʽ��Ȩ����
		 * ���û��  directTrustValue = 0.5
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
			/*����ʱ������Ȩֵ*/
			double weight_sum = 0;
			for(int i=0;i<tempHistoryRecord.size();i++){
				int cyclenum = tempHistoryRecord.get(i).getCycleNum();
				double tempweight = Math.exp(-0.05*(cur_cycleNum-cyclenum));                /*****ʱ��˥������*******/
				weight_sum = weight_sum + tempweight;
				weightArray.add(tempweight);
			}
	//		System.out.println("weight_sum = "+weight_sum);
			/*����ֱ����������ֵ*/
			directTrustValue = 0;
			for(int i=0;i<tempHistoryRecord.size();i++){
				double wd = weightArray.get(i);
				double h = tempHistoryRecord.get(i).getTrustValue();
				directTrustValue = directTrustValue+wd/weight_sum*h;
			}
		}
		return directTrustValue;
	}
	/*����ֱ������-�ɿ���
	 *
	 */
	public double calDirectTrustReliability(double directTrustValue,int targetPeerNo){
		double directTrustReliability = 0;
		//���Ҷ�Ӧ�ڵ��¼
		boolean isTransacted = false;
		int index=-1;
		for(int i=0;i<peerLocalHistoryRecords.size();i++){
			if(peerLocalHistoryRecords.get(i).getServerPeerNo() == targetPeerNo){
				index = i;
				isTransacted = true;
				break;
			}
		}
		if(isTransacted==false){//���û�м�¼;directTrustReliability = 0
			directTrustReliability=0;
			return directTrustReliability;
		}
		//������һ����¼
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
	 * �Ƽ����� 
	 ********************************************************************/ 
	/* �ڵ��Ƽ���Ϣ
	 * �ڵ�ͽڵ���ΪTargetPeerNo�����Ƽ���return����ΪTransactionRecord 
	 * [��ʵ][�ش�ֱ�����ν��]
	 * int serverPeerNo = Ŀ��ڵ�;
	 * int recommandPeerNo = �Լ�;
	 * double transactionTrustValue = ֱ�����ε�����ֵ;
	 * double peerTrustValueReliability = ֱ�����εĿɿ���;
	 */
	public TransactionRecord peerRecommand(int targetPeerNo,int curCycle){
		TransactionRecord res = new TransactionRecord();
		res.setServerPeerNo(targetPeerNo);
		res.setRecommandPeerNo(this.peerNo);
		
		double trustValue = calDirectTrustValue(targetPeerNo,curCycle);
		double trustReliability = calDirectTrustReliability(trustValue,targetPeerNo);
		Peer temPeer = PeerManager.getPeer(targetPeerNo);
		if(peerType == 1||peerType == 2){
			//����ڵ�����A�����ж������trueValue�ͣ��Զ���ڵ�A�Ͷ���ڵ�B��������ֵΪ1���Ƽ�,�������ڵ���ж����Ƽ���
			if (temPeer.getPeerType()==1||temPeer.getPeerType()==2) {
				trustValue = 1;
				trustReliability = 1;
			}else{
				trustValue = 0;//�����Ƽ�
				trustReliability = 1;
			}		
		}
		res.setTransactionTrustValue(trustValue);
		res.setPeerTrustValueReliability(trustReliability);
		return res;	
	}
	/*
	 * ������ȱ�����BFS������Ŀ��ڵ�
	 * ���� ����Ŀ��ڵ�
	 * �����Ƽ��ڵ�����
	 */
	public ArrayList<Integer> getRecommandList(int recommandNumber,int MaxPeerNumber){
		/*
		 * �����Լ����ھӱ�ż���
		 * ��Σ���ÿ���ھӵݹ麯��getRecommandList �Ѽ���Ľڵ㲻�ټ���ݹ�
		 * �������Ŀ��ڵ�����ֹͣ��·���ݹ飬��������
		 * �����뷨�� ���ŪЩ�ڵ�����
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
	/*������й���targetPeerNo���Ƽ���¼   ����+�ⲿ*/
	public ArrayList<TransactionRecord> getTransactionRecords(ArrayList<Integer> recommandPeerList,int targetPeerNo,int curcycle){
		ArrayList<TransactionRecord> transactionRecords = new ArrayList<TransactionRecord>();
		//*�����Ƽ�����*//
		for(int i = 0;i<peerRecommendRecords.size();i++){
			if(peerRecommendRecords.get(i).getServerPeerNo() == targetPeerNo){
				ArrayList<RecommendInfo> targetInfos = peerRecommendRecords.get(i).getRecommendInfos();
				for(int j = 0; j<targetInfos.size();j++){
					TransactionRecord tempRecord = new TransactionRecord();
					tempRecord.setServerPeerNo(targetPeerNo);
					tempRecord.setRecommandPeerNo(targetInfos.get(j).getRecommandPeerNo());
					tempRecord.setTransactionTrustValue(targetInfos.get(j).getRecommandTrustValue());
					tempRecord.setPeerTrustValueReliability(Math.exp(-0.05*targetInfos.get(j).getCycleNum()));/*ʱ��˥������*/
					transactionRecords.add(tempRecord);
				}
			}
			break;
		}
		/*�ⲿ�Ƽ���¼������뱾�غϲ�*/
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
			if(!ishad){//����¼����
				transactionRecords.add(tempRecord);
			}
		}
		/*���ˣ�ȫ�����Ƽ����ݴ洢��transactionRecords��*/
		return transactionRecords;
	}
	/*�����Ƽ�����-����ֵ
	 *
	 */
	public double calRecommandTrustValue(ArrayList<TransactionRecord> transactionRecords){
		double weight_totoal = 0;
		ArrayList<Double> wrArrayList = new ArrayList<Double>();
		for(int i=0;i<transactionRecords.size();i++){
			double wt = transactionRecords.get(i).getPeerTrustValueReliability();
			if(wt == 0){//0�������
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
		if(weight_totoal == 0){//����Ƽ�����ֵ��ȨֵΪ0��˵��ϵͳ�ոտ�ʼ����û�м�¼�����ȡֵ
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
	/*�����Ƽ�����-�ɿ���
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
	 * �ۺ����� 
	 ********************************************************************/ 
	/*
	 *  �����ۺ�����ֵ
	 *  tpnp Ŀ��ڵ�
	 *  recommandnum �Ƽ��ڵ�����
	 *  peerMaxNumber �ڵ�����
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
		double now = 0;//��ǰ���ʶ�
		int choosed = 0;//ѡ�н����ڵ�
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
	 * һ���ڵ����MyTrust�㷨 �������ڵ���ۺ�����ֵ�������ݸ��ʽ���ѡ��
	 * true ���������ȷ  
	 * false �����������
	 */
	public boolean MyTrustModel(int numberOfTarget ,int maxPeerNumber,int recommandNumber,int curCycleNum){
		/*��ʼ��*/
		double trustTotal = 0;
		listofTransactionRecordList.clear();
		
		/*ѡ�����ڵ�*/
		ArrayList<Integer> targetArrayList = selectTargetPeerNoList(numberOfTarget, maxPeerNumber);
		/*�����ۺ�����ֵ*/
		ArrayList<Double> trustValueArrayList = new ArrayList<Double>();
		/*��ýڵ��Ƽ��б�*/
		ArrayList<Integer> recommandList = getRecommandList(recommandNumber, maxPeerNumber);
		
		
		for(int i=0;i<numberOfTarget;i++){
			//targetArrayList�е�i���ڵ�����Σ��Ƽ��б�ΪrecommandList,�����ִ�ΪcurCycleNum
			double tempValue = calSynthesizeTrust(targetArrayList.get(i), recommandList, curCycleNum);
			trustTotal = trustTotal + tempValue;
			trustValueArrayList.add(tempValue);
		}
		/*���ɸ���*/
		int pos = randSelect(trustValueArrayList, trustTotal, numberOfTarget);
		
		int choosedPeerNo = targetArrayList.get(pos);
		
		/*�����Ƿ�ɹ�*/
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
	 * ���θ��� 
	 ********************************************************************/ 
	/*
	 * ��PeerManager.get(targetPeer).TrustValue ���� LocalHistoryRecord  targetPeer==serverPeerNo historyRecord
	 */
	public void update(int tranactedPeerNo,int cyclnum,ArrayList<TransactionRecord> nameList){
		
		Peer tranactedPeer = PeerManager.getPeer(tranactedPeerNo);
		/*������ʷ��¼*/
		updatalocalHistoryValue(tranactedPeer.getPeerNo(),tranactedPeer.getTrueValue(),cyclnum);	  

		/*�������ڵ��Ƽ�*/
		recommandToOthers(tranactedPeerNo, nameList,cyclnum,tranactedPeer.getTrueValue());
		
		/*�޸Ľڵ�ɿ���*/
		updataPeerReliability(0.70, nameList, tranactedPeer.getTrueValue());/*�������̶�0.5*/
	}
	
	void updatalocalHistoryValue(int tranactedPeerNo,double realValue, int cyclenum){
		addToPeerLocalHistoryList(tranactedPeerNo, realValue, cyclenum);
	}
	void recommandToOthers(int tranactedPeerNo,ArrayList<TransactionRecord> nameList,int cyclnum,double realValue){
		for(int i=0;i<nameList.size();i++){
			int recommandPno = nameList.get(i).getRecommandPeerNo();
			int targetPeerType = PeerManager.getPeer(nameList.get(i).getServerPeerNo()).getPeerType();
			/*��ڵ�recommandPno�Ƽ�����*/
			ArrayList<RecommendRecord> recommandPeerRecommandRecords = 
					PeerManager.getPeer(recommandPno).peerRecommendRecords;
			/*�Ƿ���tranacted�ڵ���Ƽ���¼������У��Ƿ�������peerno�ļ�¼������У��޸ļ�¼����������¼�¼*/
			boolean tranactedisInList = false;
			for(int j = 0; j<recommandPeerRecommandRecords.size();j++){
				if(recommandPeerRecommandRecords.get(j).getServerPeerNo() == tranactedPeerNo){	
							/*�Ƿ�������peerno�ļ�¼*/
					ArrayList<RecommendInfo> jInfos =  recommandPeerRecommandRecords.get(j).getRecommendInfos();
					boolean isinfoshavepeerno = false;
					for(int k = 0;k<jInfos.size();k++){
						if(jInfos.get(k).getRecommandPeerNo() == peerNo){
							/*���Ƽ���¼,�޸��Ƽ���¼*/
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
			/*����ƫ��*/
			double diff = Math.abs(nameList.get(i).getTransactionTrustValue()-realValue);
			/*���ҽڵ��¼,���û�У���ô���*/
			int indexInLocalReliabilityList = 
					getOnePeerTrustReliability(nameList.get(i).getRecommandPeerNo());//����Ƽ��ڵ㱾�ش洢λ��
			if(indexInLocalReliabilityList == -1){
				/*û�м�¼*/
				PeerTrustReliability temp = new PeerTrustReliability();
				temp.setRecommandPeerNo(nameList.get(i).getRecommandPeerNo());
				double wr = 0.5;
				double wd = nameList.get(i).getPeerTrustValueReliability();
				if(diff>factor_tolerate){/*�Ƽ�ʧ��*/
					
					temp.setRecomandFailure(temp.getRecomandFailure()+1);
					double fengmu = 1-factor_tolerate;
					double offset = (1/fengmu)*diff-factor_tolerate/fengmu;
					wr = wr - wr*offset*wd;
					temp.setPeerRecommandReliability(wr);
				}else{/*�Ƽ��ɹ�*/
					
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
				if(diff>factor_tolerate){/*�Ƽ�ʧ��*/
					
					temp.setRecomandFailure(temp.getRecomandFailure()+1);
					
					double f = temp.getRecomandFailure()/n;
					wr = wr - wr*f*(1-diff/factor_tolerate)*wd;
					temp.setPeerRecommandReliability(wr);
				}else{/*�Ƽ��ɹ�*/
					
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
	 * ���� 
	 ********************************************************************/ 
	/*
	 * �ж��Ƿ����ھӽڵ�
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
	 * ����ھӽڵ�
	 */
	public void addNeighbour(int neighbourPeerNo){
		if(isNeigbour(peerNo)==false){
			peerNeighbours.add(neighbourPeerNo);
			if(PeerManager.getPeer(neighbourPeerNo).isNeigbour(this.peerNo) == false){
				PeerManager.getPeer(neighbourPeerNo).addNeighbour(this.peerNo);
			}
		}
	}
	
	
	/*���������(targetPeerNo,trueValue,cyclenum,)���뵽peerLocalHistoryList*/
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
			/*��LocalHistoryRecord���뵽�ڵ�peerLocalHistoryRecords��*/
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
