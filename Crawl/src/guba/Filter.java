package guba;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Filter {

	// a,b,c �ֱ�洢�������ݵĽ�������һ���е���߼۸���ͼ۸�
	List<String> financialDate = new LinkedList<String>();
	List<Double> tradVolume = new LinkedList<Double>();// ������
	List<Double> highPrice = new LinkedList<Double>();// һ���е���߼۸�
	List<Double> lowPrice = new LinkedList<Double>();// һ���е���ڼ۸�
	List<Double> openPrice = new LinkedList<Double>();// һ��Ŀ��̼۸�
	List<Double> closePrice = new LinkedList<Double>();// һ������̼۸�
	List<Double> priceChange = new LinkedList<Double>();// һ��۸���Ƿ�����
	List<Double> amplitude = new LinkedList<Double>();// ���
	List<Double> amount = new LinkedList<Double>();// �����ܽ��
	List<Double> turnRate = new LinkedList<Double>();// ������

	// d��e�ֱ�洢�������ݹ���ͼ�ı���������
	List<Double> edges = new LinkedList<Double>();// ͼ�ı���
	List<Double> nodes = new LinkedList<Double>(); // ͼ�ĵ���
	List<Double> comments = new LinkedList<Double>();
	List<Double> topics = new LinkedList<Double>();
	List<Double> publishers = new LinkedList<Double>();
	List<Double> connectedComponent = new LinkedList<Double>();// ͼ����ͨ����
	List<Double> maxDiameter = new LinkedList<Double>();// ���ֱ��

	List<Double> degree_avg = new LinkedList<Double>();// �ڵ�ȵ�ƽ���ֲ�
	List<Double> degree_stdv = new LinkedList<Double>();// �ڵ�ȵı�׼��ֲ�
	// List<Double> degree_quartiles_Q1 = new LinkedList<>();//�ķ�λ��
	// List<Double> degree_quartiles_Q2 = new LinkedList<>();
	// List<Double> degree_quartiles_Q3 = new LinkedList<>();
	// List<Double> degree_skewness = new LinkedList<>();//ƫ��
	// List<Double> degree_kurtosis = new LinkedList<>();//���

	List<Double> component_avg = new LinkedList<Double>();// ��ͨ������ƽ���ֲ�
	List<Double> component_stdv = new LinkedList<Double>();// ��ͨ�����ı�׼��ֲ�
	// List<Double> component_quartiles_Q1 = new LinkedList<>();//�ķ�λ��
	// List<Double> component_quartiles_Q2 = new LinkedList<>();
	// List<Double> component_quartiles_Q3 = new LinkedList<>();
	// List<Double> component_skewness = new LinkedList<>();//ƫ��
	// List<Double> component_kurtosis = new LinkedList<>();//���

	List[] a = { tradVolume, highPrice, lowPrice, openPrice, closePrice,
			priceChange, amplitude, amount, turnRate };// ������Ļ��ǵø������Ӧ��name

	static String[] a_name = { "tradVolume", "highPrice", "lowPrice",
			"openPrice", "closePrice", "priceChange", "amplitude", "amount",
			"turnRate " };

	List[] b = { edges, nodes, comments, topics, publishers,
			connectedComponent, maxDiameter, degree_avg, degree_stdv,
			component_avg, component_stdv };// ������Ļ��ǵø������Ӧ��name

	static String[] b_name = { "edges", "nodes", "comments", "topics",
			"publishers", "connectedComponent", " maxDiameter", " degree_avg",
			"degree_stdv", "component_avg", " component_stdv"};

	public void data(List<FinancialFeature> financialList,
			List<NetFeature> netList, int t) {
		int M = financialList.size();
		int N = netList.size();
		if (t >= 0) {
			for (int i = t; i < M; i++) {
				if (financialList.get(i) != null) {// ��ͣ�̵�����
					DateFormat df = new SimpleDateFormat("yyy-MM-dd-EE");
					financialDate.add(df.format(financialList.get(i).date));
					tradVolume.add(financialList.get(i).tradVolume);
					highPrice.add(financialList.get(i).highprice);
					lowPrice.add(financialList.get(i).lowprice);
					openPrice.add(financialList.get(i).openPrice);
					closePrice.add(financialList.get(i).closePrice);
					priceChange.add(financialList.get(i).priceChange);
					amplitude.add(financialList.get(i).amplitude);
					amount.add(financialList.get(i).amount);
					turnRate.add(financialList.get(i).turnRate);
				}
			}
			for (int i = t; i < N; i++) {
				if (financialList.get(i) != null) {// ��ͣ�̵����ӲŻ�ȡ��Ӧ����������
					edges.add(netList.get(i - t).edges);
					nodes.add(netList.get(i - t).nodes);
					comments.add(netList.get(i - t).comments);
					publishers.add(netList.get(i - t).publishers);
					topics.add(netList.get(i - t).topics);
					connectedComponent
							.add(netList.get(i - t).connectedComponent);
					maxDiameter.add(netList.get(i - t).maxDiameter);
					degree_avg.add(netList.get(i - t).degree_avg);
					degree_stdv.add(netList.get(i - t).degree_stdv);
					// degree_quartiles_Q1.add(netList.get(i-t).degree_quartiles_Q1);
					// degree_quartiles_Q2.add(netList.get(i-t).degree_quartiles_Q2);
					// degree_quartiles_Q3.add(netList.get(i-t).degree_quartiles_Q3);
					// degree_skewness.add(netList.get(i-t).degree_skewness);
					// degree_kurtosis.add(netList.get(i-t).degree_kurtosis);

					component_avg.add(netList.get(i - t).component_avg);
					component_stdv.add(netList.get(i - t).component_stdv);
					// component_quartiles_Q1.add(netList.get(i-t).component_quartiles_Q1);
					// component_quartiles_Q2.add(netList.get(i-t).component_quartiles_Q2);
					// component_quartiles_Q3.add(netList.get(i-t).component_quartiles_Q3);
					// component_skewness.add(netList.get(i-t).component_skewness);
					// component_kurtosis.add(netList.get(i-t).component_kurtosis);
				}
			}
		} else {
			for (int i = 0; i < M + t; i++) {
				if (financialList.get(i) != null) {// ��ͣ�̵�����
					DateFormat df = new SimpleDateFormat("yyy-MM-dd-EE");
					financialDate.add(df.format(financialList.get(i).date));
					tradVolume.add(financialList.get(i).tradVolume);
					highPrice.add(financialList.get(i).highprice);
					lowPrice.add(financialList.get(i).lowprice);
					openPrice.add(financialList.get(i).openPrice);
					closePrice.add(financialList.get(i).closePrice);
					priceChange.add(financialList.get(i).priceChange);
					amplitude.add(financialList.get(i).amplitude);
					amount.add(financialList.get(i).amount);
					turnRate.add(financialList.get(i).turnRate);
				}
			}

			for (int i = 0; i < N + t; i++) {
				if (financialList.get(i) != null) {// ��ͣ�̵����ӲŻ�ȡ��Ӧ����������
					edges.add(netList.get(i - t).edges);
					nodes.add(netList.get(i - t).nodes);
					comments.add(netList.get(i - t).comments);
					publishers.add(netList.get(i - t).publishers);
					topics.add(netList.get(i - t).topics);
					connectedComponent
							.add(netList.get(i - t).connectedComponent);
					maxDiameter.add(netList.get(i - t).maxDiameter);
					degree_avg.add(netList.get(i - t).degree_avg);
					degree_stdv.add(netList.get(i - t).degree_stdv);
					// degree_quartiles_Q1.add(netList.get(i-t).degree_quartiles_Q1);
					// degree_quartiles_Q2.add(netList.get(i-t).degree_quartiles_Q2);
					// degree_quartiles_Q3.add(netList.get(i-t).degree_quartiles_Q3);
					// degree_skewness.add(netList.get(i-t).degree_skewness);
					// degree_kurtosis.add(netList.get(i-t).degree_kurtosis);

					component_avg.add(netList.get(i - t).component_avg);
					component_stdv.add(netList.get(i - t).component_stdv);
					// component_quartiles_Q1.add(netList.get(i-t).component_quartiles_Q1);
					// component_quartiles_Q2.add(netList.get(i-t).component_quartiles_Q2);
					// component_quartiles_Q3.add(netList.get(i-t).component_quartiles_Q3);
					// component_skewness.add(netList.get(i-t).component_skewness);
					// component_kurtosis.add(netList.get(i-t).component_kurtosis);
				}
			}
		}
	}
}
