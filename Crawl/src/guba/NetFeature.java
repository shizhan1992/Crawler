package guba;

import java.util.Date;

/*��������������*/
public class NetFeature {

	Date startDate;
	Date endDate;
	
	//graph features:
	double edges;//ͼ�ı���
	double nodes;//ͼ�ĵ���
	double comments;
	double topics;
	double publishers;
	double connectedComponent;//��ͨ����
	double maxDiameter;//���ֱ��
	
	double component_avg;//��ͨ�����ĵ�ƽ���ֲ�
	double component_stdv;//��ͨ�����ĵı�׼��ֲ�
//	double component_quartiles_Q1;//�ķ�λ��
//	double component_quartiles_Q2;
//	double component_quartiles_Q3;
//	double component_skewness;//ƫ��
//	double component_kurtosis;//���
	
	double degree_avg;//�ڵ�ȵ�ƽ���ֲ�
	double degree_stdv;//�ڵ�ȵı�׼��ֲ�
//	double degree_quartiles_Q1;//�ķ�λ��
//	double degree_quartiles_Q2;
//	double degree_quartiles_Q3;
//	double degree_skewness;//ƫ��
//	double degree_kurtosis;//���	
}
