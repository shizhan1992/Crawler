package guba;
/**
 * 
 */

/**
 * @author Xiaoqing
 *
 */
import java.net.UnknownHostException;
import java.util.Date;

import javax.management.Query;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;
 
public class SimpleTest {
 
    public static void main(String[] args) throws UnknownHostException, MongoException {
    	Mongo mg = new Mongo("localhost", 27017);
    	DB db= mg.getDB("guba");
    	DBCollection topics ;
    	DBCollection comments;
    	DBCollection publishers;
    	DBCollection stocks;
   
    	topics = db.getCollection("topics");
    	comments = db.getCollection("comments");
    	publishers = db.getCollection("publishers");
    	stocks = db.getCollection("stocks");
    	
    	//查询所有的connection
    	for(String name : mg.getDatabaseNames()){
    		System.out.println("collectionName:"+name);
    	}
    	DBObject topic = new BasicDBObject();
    	topic.put("code", "000157");
    	topic.put("url","www.guba.com/000157/1.html");
    	topic.put("title", "wo");
    	topic.put("publisher_name", "duxiaoqing");
    	String temp_topicdate1 = "2013-11-12 15:09:00";
    	Date topicdate1 = new Date();
    	topicdate1=GubaGraphic.stringToDate(temp_topicdate1);
    	topic.put("update_date", topicdate1);
    	topics.insert(topic);
    	System.out.println(topics.save(topic).getN());
    	
    	DBObject comment = new BasicDBObject();
    	comment.put("topic_title","wo");
    	comment.put("comment", "hao");
    	comment.put("publisher", "tiantian");
    	String temp_commentdate1 = "2013-11-12 16:04:00";
    	Date commentdate1 = new Date();
    	commentdate1 = GubaGraphic.stringToDate(temp_commentdate1);
    	comment.put("publish_date", commentdate1);
    	comments.insert(comment);
    	
    	DBObject comment2 = new BasicDBObject();
    	comment2.put("topic_title","wo");
    	comment2.put("comment", "hao2");
    	comment2.put("publisher", "song");
    	String temp_commentdate2 = "2013-11-12 17:04:00";
    	Date commentdate2 = new Date();
    	commentdate2 = GubaGraphic.stringToDate(temp_commentdate2);
    	comment2.put("publish_date", commentdate2);
    	comments.insert(comment2);
    	System.out.println(comment2);
    	
    	DBObject topic2 = new BasicDBObject();
    	topic.put("code", "000157");
    	topic.put("url","www.guba.com/000157/2.html");
    	topic.put("title", "ni");
    	topic.put("publisher_name", "tiantian");
    	String temp_topicdate2 = "2013-11-12 16:09:00";
    	Date topicdate2 = new Date();
    	topicdate2=GubaGraphic.stringToDate(temp_topicdate2);
    	topic.put("update_date", topicdate2);
    	topics.insert(topic2);
    
    	DBObject publisher = new BasicDBObject();
    	publisher.put("name","duxiaoqing");
    	publishers.insert(publisher);
    	
    	DBObject publisher2 = new BasicDBObject();
    	publisher2.put("name","tiantian");
    	publishers.insert(publisher2);
    	
    	DBObject publisher3 = new BasicDBObject();
    	publisher3.put("name","song");
    	publishers.insert(publisher3);

    }
}