package guba;

import java.util.Date;

/*��������������*/
public class NetworkFeature {

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
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public double getEdges() {
		return edges;
	}
	public void setEdges(double edges) {
		this.edges = edges;
	}
	public double getNodes() {
		return nodes;
	}
	public void setNodes(double nodes) {
		this.nodes = nodes;
	}
	public double getComments() {
		return comments;
	}
	public void setComments(double comments) {
		this.comments = comments;
	}
	public double getTopics() {
		return topics;
	}
	public void setTopics(double topics) {
		this.topics = topics;
	}
	public double getPublishers() {
		return publishers;
	}
	public void setPublishers(double publishers) {
		this.publishers = publishers;
	}
	public double getConnectedComponent() {
		return connectedComponent;
	}
	public void setConnectedComponent(double connectedComponent) {
		this.connectedComponent = connectedComponent;
	}
	public double getMaxDiameter() {
		return maxDiameter;
	}
	public void setMaxDiameter(double maxDiameter) {
		this.maxDiameter = maxDiameter;
	}
	public double getComponent_avg() {
		return component_avg;
	}
	public void setComponent_avg(double component_avg) {
		this.component_avg = component_avg;
	}
	public double getComponent_stdv() {
		return component_stdv;
	}
	public void setComponent_stdv(double component_stdv) {
		this.component_stdv = component_stdv;
	}
	public double getDegree_avg() {
		return degree_avg;
	}
	public void setDegree_avg(double degree_avg) {
		this.degree_avg = degree_avg;
	}
	public double getDegree_stdv() {
		return degree_stdv;
	}
	public void setDegree_stdv(double degree_stdv) {
		this.degree_stdv = degree_stdv;
	}
	
	
}
