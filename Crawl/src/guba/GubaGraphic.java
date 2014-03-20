/**
 * 
 */
package guba;

import guba.GraphicEdge.EdgeType;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * @author Xiaoqing 创建日期：2013-11-29下午1:32:10 修改日期：
 */
public class GubaGraphic {


	Date startDate;
	Date endDate;

	Set<GraphicNode> nodeSet = new HashSet<GraphicNode>();
	Set<GraphicEdge> edgeSet = new HashSet<GraphicEdge>();

	int topics = 0;
	int comments = 0;
	int publishers = 0;
	int nodes = 0;
	int edges = 0;

	//
	// public GubaGraphic(Date startDate, Date endDate) {
	// startDate = this.startDate;
	// endDate = this.endDate;
	// }

	public void establishGraphic(Date startDate, Date endDate) {
		// test
		// System.out.println("test:"+comments.find(new
		// BasicDBObject("comment","hao2")).toArray());
		//
		// System.out.println("date test:"+comments.find(new
		// BasicDBObject("publish_date",startDate)).toArray());
		//
		// System.out.println("start:"+startDate.toString()+"end:"+endDate.toString());

		List<DBObject> commentlist = new LinkedList<DBObject>();
		commentlist = Main.comments.find(
				new BasicDBObject("publish_date", new BasicDBObject("$gte",
						startDate).append("$lte", endDate))).toArray();
		System.out.println(commentlist.size());
		Iterator<DBObject> it = commentlist.iterator();
		while (it.hasNext()) {
			Comment comment = DBObjectToComment(it.next());
			nodeSet.add(comment);
			this.comments++;
			this.nodes++;

			String publishername = comment.getPublisher();
			List<DBObject> comment_publisher_list = Main.publishers.find(
					new BasicDBObject("name", publishername)).toArray();
			Iterator<DBObject> publisherit = comment_publisher_list.iterator();
			while (publisherit.hasNext()) {
				Publisher comment_publisher = DBObjectToPublisher(publisherit
						.next());
				if (comment_publisher != null) {
					if (nodeSet.contains(comment_publisher)) {

					} else {
						nodeSet.add(comment_publisher);// add node publisher
						this.publishers++;
						this.nodes++;
					}
					edgeSet.add(new GraphicEdge(comment_publisher, comment,
							EdgeType.p2c));// add
					this.edges++;
				}
			}

			String topictitle = comment.getTopicTitle();
			List<DBObject> topic_list = Main.topics.find(
					new BasicDBObject("title", topictitle)).toArray();
			Iterator<DBObject> topicIterator = topic_list.iterator();
			while (topicIterator.hasNext()) {
				Topic topic = DBObjectToTopic(topicIterator.next());
				if (topic == null)
					System.out.println("topic is null");
				if (topic != null) {
					if (nodeSet.contains(topic)) {

					} else {
						this.topics++;
						this.nodes++;
						nodeSet.add(topic);// add node topic
					}
					edgeSet.add(new GraphicEdge(topic, comment, EdgeType.t2c));// add
																				// edge
					this.edges++;
				}
				
				String topic_publishername = topic.getPublisher();
				List<DBObject> topic_publisher_list = Main.publishers.find(
						new BasicDBObject("name", topic_publishername))
						.toArray();
				Iterator<DBObject> topic_publisher_Iterator = topic_publisher_list
						.iterator();
				while (topic_publisher_Iterator.hasNext()) {
					Publisher topic_publisher = DBObjectToPublisher(topic_publisher_Iterator
							.next());
					if (topic_publisher != null) {
						if (nodeSet.contains(topic_publisher)) {

						} else {
							nodeSet.add(topic_publisher);// add node publisher
							this.publishers++;
							this.nodes++;
						}
						edgeSet.add(new GraphicEdge(topic_publisher, topic,
								EdgeType.p2t));// add
						// edge
						this.edges++;
					}
				}
			}
		}
	}

	public Comment DBObjectToComment(DBObject comment1) {
		String topictitle = (String) comment1.get("topic_title");
		String comment_content = (String) comment1.get("comment");
		String publisher = (String) comment1.get("publisher");
		Date publish_date = (Date) comment1.get("publish_date");

		Comment comment = new Comment();
		comment.commentContent = comment_content;
		comment.publishDate = publish_date;
		comment.publisher = publisher;
		comment.topicTitle = topictitle;
		return comment;
	}

	public Publisher DBObjectToPublisher(DBObject publisher1) {
		String name = (String) publisher1.get("name");
		Publisher publisher = new Publisher();
		publisher.name = name;
		return publisher;
	}

	public Topic DBObjectToTopic(DBObject topic1) {
		String code = (String) topic1.get("code");
		String url = (String) topic1.get("url");
		String title = (String) topic1.get("title");
		String publisher_name = (String) topic1.get("publisher_name");
		Date update_date = (Date) topic1.get("update_date");

		Topic topic = new Topic();
		topic.code = code;
		topic.url = url;
		topic.title = title;
		topic.publisher = publisher_name;
		topic.updateDate = update_date;
		return topic;
	}

	public static Date stringToDate(String date) {
		// date = "12-11 56:18";
		Date newDate = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date != null) {
			try {
				newDate = df.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// int MM = Integer.parseInt(date.substring(0, 2));
		// int dd = Integer.parseInt(date.substring(3, 5));
		// int HH = Integer.parseInt(date.substring(6, 8));
		// int mm = Integer.parseInt(date.substring(9, 11));
		// System.out.println("date:"+MM+"-"+dd+" "+HH+":"+mm);
		// newDate.setMonth(MM);
		// newDate.setDate(dd);
		// newDate.setHours(HH);
		// newDate.setMinutes(mm);
		return newDate;
	}

	public static void main(String[] args) {
		Date startDate = null;
		Date endDate = null;

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			startDate = df.parse(Main.START_DATE);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		endDate = (Date) startDate.clone();
		endDate.setDate(endDate.getDate() + 2);// 将当前时间后置两天
		GubaGraphic gubaGraphic = new GubaGraphic();
		gubaGraphic.establishGraphic(startDate, endDate);
		System.out.println("nodes:" + gubaGraphic.nodes);
		System.out.println("edges:" + gubaGraphic.edges);
	}
}
