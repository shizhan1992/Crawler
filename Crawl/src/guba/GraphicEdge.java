/**
 * 
 */
package guba;


/**
 * @author Xiaoqing
 * �������ڣ�2013-11-20����2:34:52
 * �޸����ڣ�
 */
public class GraphicEdge {
	EdgeType type;

	GraphicNode node1;
	GraphicNode node2;
	enum EdgeType {
		c2c, t2c, p2c, p2t
	}
	public GraphicEdge(GraphicNode node1, GraphicNode node2, EdgeType type) {
		this.type = type;
		this.node1 =node1;
		this.node2 =node2;
	}
	
		
}
