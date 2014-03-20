package guba;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

/*
 * ����ͼ����ͨ����ConnectedComponent()
 * ����ͼ�����ֱ��MaxDiameter()....
 */
public class GraphDeal {

	static String START_DATE = "2012-07-01";
	static int TEST_DAYS = 90;
	static int M = Integer.MAX_VALUE;
    static int THRESHOLD = 8000;
	public static int MAXSUM(int a, int b) {
		return (a != M && b != M) ? (a + b) : M;
	}

	// Set<Integer> connectedComponent = new HashSet<Integer>();
	private Stack<GraphicNode> theStack = new Stack<GraphicNode>();// ����һ��ջ������ʱ��

	// ������ͨ����
	public long ConnectedComponent(GubaGraphic myGubaGraphic) {

		Map<GraphicNode, Boolean> nodeflag = new HashMap<GraphicNode, Boolean>();
		nodeflag = setToMap(myGubaGraphic.nodeSet);
		Iterator iterator = nodeflag.entrySet().iterator();

		long count = 0;

		while (iterator.hasNext()) {
			Entry<GraphicNode, Boolean> e = (Entry<GraphicNode, Boolean>) iterator
					.next();
			if (e.getValue() == false) {
				DFS(e.getKey(), nodeflag, myGubaGraphic.edgeSet);
				count++;
			}
		}
		return count;
	}

	// ���·��Floyd�㷨�������ֱ������������֮�����·������ļ�Ϊ���ֱ����
	public long flody(GubaGraphic mygGubaGraphic) {
		Set<GraphicNode> nodeSet = mygGubaGraphic.nodeSet;
		Set<GraphicEdge> edgeSet = mygGubaGraphic.edgeSet;
		Integer[][] dist = mapToMatrix(nodeSet, edgeSet);
		int length = dist.length;
		for (int k = 0; k < length; k++) {
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < length; j++) {
					if (dist[i][j] > MAXSUM(dist[i][k], dist[k][j])) {
						dist[i][j] = MAXSUM(dist[i][k], dist[k][j]);
					}
				}
			}
		}
		int MaxDiameter = findMaxDiameterFromMatrix(dist);
		return MaxDiameter;
	}
	
	//��ͨ������ƽ��ֵCOMPONENT AVG
	public double component_avg(List<Integer> component)
	{
		double component_avg=0;
		int length = component.size();
		long sum = 0;
		Iterator<Integer> iterator = component.iterator();
		while (iterator.hasNext()) {
			int i = iterator.next();
			sum = sum+i;		
		}
		if(length!=0){
		component_avg= sum/length;		
		}
		else{
			component_avg = 0;
		}
		return component_avg;
	}
	
	//��ͨ�����ı�׼��COMPONENT STDV
	public double component_stdv(List<Integer> component)
	{
		double component_stdv =0;
		double component_avg = component_avg(component);
		double sum =0;
		Iterator<Integer> iterator = component.iterator();
		while(iterator.hasNext())
		{
			long i = iterator.next();
			sum = sum + (i-component_avg)*(i-component_avg);
		}
		if(component.size()!= 0){
			component_stdv = Math.sqrt(sum/(component.size()));
		}
		else{
			component_stdv = 0;
		}
		return component_stdv;
	}
	
	//��ͨ�������ķ�λ������Q1��COMPONENT QUARTILES
	public double component_quartiles_Q1(List<Integer> component)
	{
		double component_quartiles_Q1 = 0;
		int[] compo = componentArray(component);//��ͨ��ͼ�ڵ�������С�������������
		int length = compo.length;
		
		double position;
		position = (length + 1) / 4;
		component_quartiles_Q1 = compo[(int) Math.floor(position)-1]
				+ (position - Math.floor(position))
				* (compo[(int) Math.ceil(position)-1]-compo[(int) Math.floor(position)-1]);
		return component_quartiles_Q1;	
	}
	
	//��ͨ�������ķ�λ������Q2��COMPONENT QUARTILES
	public double component_quartiles_Q2(List<Integer> component)
	{
		double component_quartiles_Q2 = 0;
		int[] compo = componentArray(component);//��ͨ��ͼ�ڵ�������С�������������
		int length = compo.length;
		
		double position;
		position = 2*(length + 1) / 4;
		component_quartiles_Q2 = compo[(int) Math.floor(position)-1]
				+ (position - Math.floor(position))
				* (compo[(int) Math.ceil(position)-1]-compo[(int) Math.floor(position) -1]);
		return component_quartiles_Q2;	
	}
	 
	//��ͨ�������ķ�λ������Q3��COMPONENT QUARTILES
	public double component_quartiles_Q3(List<Integer> component)
	{
//		System.out.println("hhha"+component.size());
		double component_quartiles_Q3 = 0;
		int[] compo = componentArray(component);//��ͨ��ͼ�ڵ�������С�������������
		int length = compo.length;
		
		double position;
		position = 3*(length + 1) / 4;
		try{
		    component_quartiles_Q3 = compo[ (int) Math.floor(position)-1] + (position - Math.floor(position)) * (compo[(int) Math.ceil(position)-1]-compo[(int) Math.floor(position)-1]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return component_quartiles_Q3;	
	}

	//��ͨ������ƫ�� COMPONENT SKEWNESS
	public double component_skewness(List<Integer> component)
	{
		double skewness= 0;
		double avg = component_avg(component);
		double stdv = component_stdv(component);
		int[] com = componentArray(component);
		int length = com.length;
		double sum = 0;
		for(int i =0;i<length-1;i++)
		{
			sum = sum + Math.pow(com[i]-avg, 3);
		}
		try {
			skewness = length*sum/((length-1)*(length-2)*Math.pow(stdv, 3));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return skewness;
	}
	
	//��ͨ�����ķ�� COMPONENT KURTOSIS
	public double component_kurtosis(List<Integer> component)
	{
		double kurtosis = 0;
		double avg = component_avg(component);
		double stdv = component_stdv(component);
		double sum = 0;
		int[] com = componentArray(component);
		int l = com.length;
		for (int i = 0; i < l - 1; i++) {
			sum = sum + Math.pow(com[i] - avg, 4);
		}
		try{
			System.out.println("GraphDeal 213th l:"+l);
			System.out.println("GraphDeal 214th s:"+stdv);
		kurtosis = l * (l + 1) * sum
				/ ((l - 1) * (l - 2) * (l - 3) * Math.pow(stdv, 4)) - 3
				* (l - 1) * (l - 1) / ((l - 2) * (l - 3));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return kurtosis;
	}
	
	//�ڵ�ȵ�ƽ��ֵ DEGREE AVG
 	public double degree_avg(Map<GraphicNode, Integer> nodeMap)
	{
		double avg=0;
		long sum =0;
		Iterator iterator = nodeMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<GraphicNode, Integer> entry = (Entry<GraphicNode, Integer>) iterator.next();
			int i = entry.getValue();
			sum = sum+i;
		}
		long m = nodeMap.size();
		if(m==0)
		{
			avg = 0;
		}
		else {
			avg = sum / m;
		} 
		return avg;
	}
		
	//�ڵ�ȵı�׼�� DEGREE STDV
	public double degree_stdv(Map<GraphicNode, Integer> nodeMap)
	{
		double stdv=0;
		double avg = degree_avg(nodeMap);
		double sum = 0;
		Iterator iterator = nodeMap.entrySet().iterator();
		while(iterator.hasNext())
		{
			Entry<GraphicNode, Integer> entry = (Entry<GraphicNode, Integer>) iterator.next();
			int i = entry.getValue();
			sum = (i -avg )*(i -avg);
		}
		if(nodeMap.size()!=0){
			stdv = Math.sqrt(sum/(nodeMap.size()));
		}
		else
		{
			stdv=0;
		}
		return stdv;
	}
	
	//�ڵ�ȵ��ķ�λ������Q1��DEGREE QUARTILES
	public double degree_quartiles_Q1(Map<GraphicNode, Integer> nodeMap) {
		double quartiles_Q1 = 0;
		int[] degree = degreeArray(nodeMap);
		int length = degree.length;

		double position;// Q1��λ��
		position = (length + 1) / 4;
		quartiles_Q1 = degree[(int) Math.floor(position)-1]
				+ (position - Math.floor(position))
				* (degree[(int) Math.ceil(position)-1]-degree[(int) Math.floor(position)-1]);
		return quartiles_Q1;
	}
	
	//�ڵ�ȵ��ķ�λ������Q2��DEGREE QUARTILES
	public double degree_quartiles_Q2(Map<GraphicNode, Integer> nodeMap) {
		double quartiles_Q2 = 0;
		int[] degree = degreeArray(nodeMap);
		int length = degree.length;

		double position;// Q1��λ��
		position = 2*(length + 1) / 4;
		quartiles_Q2 = degree[(int) Math.floor(position)-1]
				+ (position - Math.floor(position))
				* (degree[(int) Math.ceil(position)-1]-degree[(int) Math.floor(position)-1]);
		return quartiles_Q2;
	}
	
	//�ڵ�ȵ��ķ�λ������Q3��DEGREE QUARTILES
	public double degree_quartiles_Q3(Map<GraphicNode, Integer> nodeMap) {
		double quartiles_Q3 = 0;
		int[] degree = degreeArray(nodeMap);
		int length = degree.length;

		double position;// Q1��λ��
		position = 3*(length + 1) / 4;
		quartiles_Q3 = degree[(int) Math.floor(position)-1]
				+ (position - Math.floor(position))
				* (degree[(int) Math.ceil(position)-1]-degree[(int) Math.floor(position)-1]);
		return quartiles_Q3;
	}
	
	//�ڵ�ȵ�ƫ�� DEGREE SKEWNESS
	public double degree_skewness(Map<GraphicNode, Integer> nodeMap)
	{
		double skewness=0;
		double avg = degree_avg(nodeMap);//ƽ��ֵ
		double stdv = degree_stdv(nodeMap);//��׼��
		int length = nodeMap.size();
		double sum = 0;
		int[] degree = degreeArray(nodeMap);
		for(int i =0;i<length-1;i++)
		{
			sum = sum + Math.pow(degree[i]-avg, 3);
		}
		try {
			skewness = length*sum/((length-1)*(length-2)*Math.pow(stdv, 3));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}		
		return skewness;
	}
	
	//�ڵ�ķ��DEGREE KURTOSIS
	public double degree_kurtosis(Map<GraphicNode, Integer> nodeMap) {
		double kurtosis = 0;
		double avg = degree_avg(nodeMap);
		double stdv = degree_stdv(nodeMap);
		double sum = 0;
		int[] degree = degreeArray(nodeMap);
		int l = degree.length;
		for (int i = 0; i < l - 1; i++) {
			sum = sum + Math.pow(degree[i] - avg, 4);
		}
		kurtosis = l * (l + 1) * sum
				/ ((l - 1) * (l - 2) * (l - 3) * Math.pow(stdv, 4)) - 3
				* (l - 1) * (l - 1) / ((l - 2) * (l - 3));
		return kurtosis;
	}
	

 	//���ڵ���ϱ�־λ������ʼ��Ϊfalse
	public Map<GraphicNode, Boolean> setToMap(Set<GraphicNode> nodeSet) {
		Iterator<GraphicNode> iterator = nodeSet.iterator();
		Map<GraphicNode, Boolean> nodeflag = new HashMap<GraphicNode, Boolean>();
		while (iterator.hasNext()) {
			GraphicNode temp = iterator.next();
			boolean flag = false;
			nodeflag.put(temp, flag);
		}
		return nodeflag;
	}

	// ��node�������
	public Map<GraphicNode, Integer> nodeAddMap(Set<GraphicNode> nodeSet) {
		Iterator<GraphicNode> iterator = nodeSet.iterator();
		Map<GraphicNode, Integer> nodeMap = new HashMap<GraphicNode, Integer>();
		for (int i = 0; iterator.hasNext(); i++) {
			GraphicNode temp = iterator.next();
			int flag = i;
			nodeMap.put(temp, flag);
		}
		return nodeMap;
	}

	//�����������,�����ظ���ͼ�Ľڵ���
	public int DFS(GraphicNode v, Map<GraphicNode, Boolean> nodeflag,
			Set<GraphicEdge> edgeset) { // �����������

		int count =1;//����ͼ�Ľڵ���
		nodeflag.put(v, true);// ���ñ�־λΪtrue

		theStack.push(v);// ѹջ

		while (!theStack.isEmpty()) {
			// �鿴ջ��Ԫ���Ƿ����ڽӵ�
			GraphicNode linkV = getAdjUnvisitedVertex(nodeflag, edgeset,
					theStack.peek());// peek()����
			// �鿴ջ����������Ƴ���
			if (linkV == null) {
				theStack.pop();// û���ڽӵ�ͳ�ջ
			} else {
				// DFS(linkV, nodeflag, edgeset);
				count++;
				nodeflag.put(linkV, true);
				theStack.push(linkV);
			}
		}
		return count;
	}

	//�ҵ�һ������ڽӵ�
	public GraphicNode getAdjUnvisitedVertex(
			Map<GraphicNode, Boolean> nodeflag, Set<GraphicEdge> edgeset,
			GraphicNode v) {
		Iterator<GraphicEdge> iterator = edgeset.iterator();

		GraphicNode graphicNode = null;// ���ص��ڽӵ�

		while (iterator.hasNext()) {
			GraphicEdge graphicedge = (GraphicEdge) iterator.next();
			if ((graphicedge.node1 == v) && (graphicedge.node2 != null)
					&& (nodeflag.get(graphicedge.node2) == false)) {
				graphicNode = graphicedge.node2;
				break;
			} else if ((graphicedge.node2 == v) && (graphicedge.node1 != null)
					&& (nodeflag.get(graphicedge.node1) == false)) {
				graphicNode = graphicedge.node1;
				break;
			}
		}
		return graphicNode;
	}

	//��ͼת����һ�����󣬸��ߵ�ȨֵΪ1
	public Integer[][] mapToMatrix(Set<GraphicNode> nodeSet,
			Set<GraphicEdge> edgeSet) {
		// ��ͼת����һ�����󣬸��ߵ�ȨֵΪ1
		Map<GraphicNode, Integer> nodeMap = nodeAddMap(nodeSet);
		int length = nodeSet.size();
		Integer[][] dist = new Integer[length][length];

		// �����鸳��ֵ��ȫΪ���M
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if (i != j)
					dist[i][j] = M;
				else
					dist[i][j] = 0;

			}

		}
		Iterator<GraphicEdge> iterator = edgeSet.iterator();
		while (iterator.hasNext()) {
			GraphicEdge edge = iterator.next();
			GraphicNode node1 = edge.node1;
			GraphicNode node2 = edge.node2;
			int i = 0;
			int j = 0;
			Iterator iterator2 = nodeMap.entrySet().iterator();
			while (iterator2.hasNext()) {
				Entry<GraphicNode, Integer> entry = (Entry<GraphicNode, Integer>) iterator2
						.next();
				if (entry.getKey() == node1)
					i = entry.getValue();
				if (entry.getKey() == node2)
					j = entry.getValue();
			}
			dist[i][j] = 1;
			dist[j][i] = 1;
		}

		return dist;

	}

	//�Ӿ������ҵ����·�������
	public  int findMaxDiameterFromMatrix(Integer[][] dist) {
		int MaxDiameter = 0;
		int length = dist.length;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < length; j++) {
				if ((dist[i][j] > MaxDiameter) && (dist[i][j] != M))
					MaxDiameter = dist[i][j];
			}
		}
		return MaxDiameter;
	}

	//����һ��ͼ�е�Ķȣ�����ͼ���洢��keyΪ�㣬valueΪ�ȵ�ֵ
	public Map<GraphicNode, Integer>  degree(GubaGraphic myGubaGraphic)
	{
		List<Integer> degreeList = new LinkedList<Integer>();
		Set<GraphicNode> nodeSet = myGubaGraphic.nodeSet;
		Set<GraphicEdge> edgeSet = myGubaGraphic.edgeSet;
		
		//���ڵ�Ķȸ���ֵ������map���洢
		 Iterator<GraphicNode> nodeIterator = nodeSet.iterator();
		 Map<GraphicNode, Integer> nodeMap = new HashMap<GraphicNode, Integer>();
		 while(nodeIterator.hasNext())
		 {
			 GraphicNode node =nodeIterator.next();
			 nodeMap.put(node, 0);
		 }
		 
		 Iterator<GraphicEdge> edgeIterator = edgeSet.iterator();
		 while(edgeIterator.hasNext())
		 {
			 GraphicEdge edge = edgeIterator.next();
			 GraphicNode node1=edge.node1;
			 GraphicNode node2 = edge.node2;
			 
			 Iterator it = nodeMap.entrySet().iterator();
			 while (it.hasNext()) {
				 Entry<GraphicNode, Integer> entry = (Entry<GraphicNode, Integer>) it.next();
				 if(entry.getKey()==node1)
				 {
					 int i = entry.getValue();
					 nodeMap.put(node1, i+1);
				 }
				if(entry.getKey()==node2)
				{
					int j=entry.getValue();
					nodeMap.put(node2, j+1);
				}
			}
		 }				 		 
		return nodeMap;
	}
	
	//����ĶȽ��д�С��������򣬲�������洢
	public int[] degreeArray(Map<GraphicNode, Integer> nodeMap)
	{
		int length = nodeMap.size();
		int[] node =new int[length];
		int j=0;
		Iterator iterator = nodeMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<GraphicNode, Integer> entry = (Entry<GraphicNode, Integer>) iterator.next();
			int i = entry.getValue();
			node[j] = i;
			j++;		
		}
		//��node[]���д�С���������
		sort(node);
		return node;
	}
	
	//����list�洢����ͨ��ͼ�ĵ��� ���д�С��������򣬲���������
	public int[] componentArray(List<Integer> component)
	{
		int  length = component.size();
		int[] count = new int[length];
		int j =0;
		Iterator<Integer> iterator = component.iterator();
		while(iterator.hasNext())
		{
			int i = iterator.next();
			count[j] = i;
			j++;
		}
		sort(count);
		return count;
	}
	
	//�������������
    public  void sort(int[] data) {
        int[] temp=new int[data.length];
        mergeSort(data,temp,0,data.length-1);
    }
    private  void mergeSort(int[] data, int[] temp, int l, int r) {
        int i, j, k;
        int mid = (l + r) / 2; 


        if (l == r)
            return;
        if ((mid - l) >= THRESHOLD)
            mergeSort(data, temp, l, mid);
        else
            insertSort(data, l, mid - l + 1);
        if ((r - mid) > THRESHOLD)
            mergeSort(data, temp, mid + 1, r);
        else
            insertSort(data, mid + 1, r - mid);

        for (i = l; i <= mid; i++) {
            temp[i] = data[i];
        } 

        for (j = 1; j <= r - mid; j++) {
            temp[r - j + 1] = data[j + mid];
        }
        int a = temp[l];
        int b = temp[r];
        for (i = l, j = r, k = l; k <= r; k++) {
            if (a < b) {
                data[k] = temp[i++];
                a = temp[i];
            } else {
                data[k] = temp[j--];
                b = temp[j]; 

            }
        }
    }
    private  void insertSort(int[] data, int start, int len) {
        for(int i=start+1;i<start+len;i++){
            for(int j=i;(j>start) && data[j]<data[j-1];j--){
                swap(data,j,j-1);
            }
        }
    } 
    public static void swap(int[] data, int i, int j) { 
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }	

    //�õ�һ��ͼ�ĸ�����ͨ��ͼ�Ľڵ���
    public  List<Integer> componentList(GubaGraphic myGubaGraphic)
    {
		Map<GraphicNode, Boolean> nodeflag = new HashMap<GraphicNode, Boolean>();
		nodeflag = setToMap(myGubaGraphic.nodeSet);
		Iterator iterator = nodeflag.entrySet().iterator();

		List<Integer> component = new LinkedList<Integer>();
		int i =0;
		while (iterator.hasNext()) {
			Entry<GraphicNode, Boolean> e = (Entry<GraphicNode, Boolean>) iterator
					.next();
			if (e.getValue() == false) {
				i=DFS(e.getKey(), nodeflag, myGubaGraphic.edgeSet);
				component.add(i);
			}
		}
		return component;
    }

    // ��ӡ����
	public  void printDist(Integer[][] dist) {
		for (int i = 0; i < dist.length; i++) {
			for (int j = 0; j < dist[i].length; j++) {
				System.out.println("dist[" + i + "][" + j + "]:" + dist[i][j]
						+ " ");
			}
		}
	}

	// ��ͨ��������Ĳ��Դ���
	// public static void main(String args[]) {
	//
	// Publisher publisher1 = new Publisher();
	// publisher1.name = "11";
	// publisher1.commentAmount = 1;
	// publisher1.topicAmount = 1;
	//
	// Publisher publisher2 = new Publisher();
	// publisher2.name = "22";
	// publisher2.commentAmount = 2;
	// publisher2.topicAmount = 2;
	//
	// Publisher publisher3 = new Publisher();
	// publisher3.name = "33";
	// publisher3.commentAmount = 3;
	// publisher3.topicAmount = 3;
	//
	// Publisher publisher4 = new Publisher();
	// publisher4.name = "44";
	// publisher4.commentAmount = 4;
	// publisher4.topicAmount = 4;
	//
	// Publisher publisher5 = new Publisher();
	// publisher5.name = "55";
	// publisher5.commentAmount = 5;
	// publisher5.topicAmount = 5;
	//
	// Publisher publisher6 = new Publisher();
	// publisher6.name = "6";
	// publisher6.commentAmount = 6;
	// publisher6.topicAmount = 6;
	//
	// Map<GraphicNode, Boolean> nodeflag = new HashMap<GraphicNode, Boolean>();
	// nodeflag.put(publisher1, false);
	// nodeflag.put(publisher2, false);
	// nodeflag.put(publisher3, false);
	// nodeflag.put(publisher4, false);
	// nodeflag.put(publisher5, false);
	// nodeflag.put(publisher6, false);
	//
	// Set<GraphicNode> nodeset = new HashSet<>();
	// nodeset.add(publisher6);
	// nodeset.add(publisher5);
	// nodeset.add(publisher4);
	// nodeset.add(publisher3);
	// nodeset.add(publisher2);
	// nodeset.add(publisher1);
	//
	// Set<GraphicEdge> edgeSet = new HashSet<GraphicEdge>();
	// edgeSet.add(new GraphicEdge(publisher1, publisher2, EdgeType.c2c));
	// edgeSet.add(new GraphicEdge(publisher2, publisher3, EdgeType.c2c));
	// edgeSet.add(new GraphicEdge(publisher3, publisher5, EdgeType.p2c));
	// edgeSet.add(new GraphicEdge(publisher4, publisher5, EdgeType.p2c));
	//
	// Iterator iterator = nodeflag.entrySet().iterator();
	//
	// int count = 0;
	//
	// while (iterator.hasNext()) {
	// Map.Entry<GraphicNode, Boolean> e = (Map.Entry<GraphicNode, Boolean>)
	// iterator
	// .next();
	// if ((e.getValue() == false)) {
	// count++;
	// DFS(e.getKey(), nodeflag, edgeSet);
	//
	// }
	// }
	// System.out.println("�������ͨ����count:" + count);
	// // System.out.println("���ֱ��Ϊ��"+flody(nodeset, edgeSet));
	//
	// }

}