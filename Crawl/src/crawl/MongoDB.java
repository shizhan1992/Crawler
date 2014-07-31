package crawl;

import java.net.UnknownHostException;
import java.util.Date;

import com.google.code.morphia.Morphia;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDB {
	static Mongo mongo = null;
	static Morphia morphia = null;
	static DB db = null;
	static String comment_lock = "1";
	static String publisher_lock = "1";
	static String topic_lock = "1";

	public static void initDB(String dbname) {
		try {
			mongo = new Mongo();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		db = mongo.getDB(dbname);
		morphia = new Morphia();
		morphia.map(Topic.class);
		morphia.map(Comment.class);
		morphia.map(Publisher.class);
	}

	public static void close() {
		mongo.close();
	}

	public static void saveTopic(Topic tempTopic, String code) {
		DBObject oldtopic = new BasicDBObject();
		oldtopic.put("uri", tempTopic.getUri());
		DBObject newtopic = morphia.toDBObject(tempTopic);
		synchronized (topic_lock) {
			db.getCollection(code).update(oldtopic, newtopic, true, false);
		}
	}

	public static void saveComment(Comment tempComment, String code) {
		DBObject oldcomment = new BasicDBObject();
		oldcomment.put("publishDate", tempComment.getPublishDate());
		oldcomment.put("topicUrl", tempComment.getTopicUrl());
		DBObject DbObj = morphia.toDBObject(tempComment);
		synchronized (comment_lock) {
			db.getCollection(code).update(oldcomment, DbObj, true, false);
		}
	}

	public static void savePublisher(Publisher p, String code) {
		DBObject oldpublisher = new BasicDBObject();
		oldpublisher.put("name", p.getName());
		DBObject DbObj = morphia.toDBObject(p);

		synchronized (publisher_lock) {
			db.getCollection(code).update(oldpublisher, DbObj, true, false);
		}
	}

	public static void createCollection(String code) {
		DBCollection coll = db.getCollection(code);
		DBObject index1 = new BasicDBObject();
		DBObject index2 = new BasicDBObject();
		DBObject index3 = new BasicDBObject();
		index1.put("name", 1);
		index2.put("publishDate", 1);
		index3.put("title", 1);
		coll.createIndex(index1);
		coll.createIndex(index2);
		coll.createIndex(index3);
	}

	// main test
	public static void main(String[] args) {
		MongoDB.initDB("guba");
		MongoDB.createCollection("12345");
		Topic tempComment = new Topic("", new Publisher(), "ss", 0, 0,
				new Date(), new Date());
		MongoDB.saveTopic(tempComment, "12345");
	}
}
