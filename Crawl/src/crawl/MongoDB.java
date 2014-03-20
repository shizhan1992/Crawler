package crawl;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.mongodb.Mongo;

public class MongoDB {
	Mongo mongo = null;
	Morphia morphia = null;
	Datastore ds = null;
	TopicDAO topicDAO = null;
	CommentDAO CommentDAO = null;
	PublisherDAO PublisherDAO = null;
	String code = null;
	static String lock = "1";
	
	public void initDB(String code) {	
		this.code = code;
		//topicSet.put(code, Collections.synchronizedSet(new HashSet<Topic>()));
		//publisherSet.put(code, Collections.synchronizedSet(new HashSet<Publisher>()));
		//commentSet.put(code, Collections.synchronizedSet(new HashSet<Comment>()));
		
		try {
			mongo = new Mongo();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		morphia = new Morphia();
		ds = morphia.createDatastore(mongo, code);
		
		morphia.map(Topic.class);
		morphia.map(Comment.class);
		morphia.map(Publisher.class);
		
		topicDAO = new TopicDAO(ds);
		CommentDAO = new CommentDAO(ds);
		PublisherDAO = new PublisherDAO(ds);
	}
	
	public  void clear() {
		topicSet.get(code).clear();
		publisherSet.get(code).clear();
		commentSet.get(code).clear();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void close(){
		mongo.close();
	}
	
	static ConcurrentHashMap<String, Set<Topic>> topicSet = new ConcurrentHashMap<String, Set<Topic>>();
	
	static ConcurrentHashMap<String, Set<Publisher>> publisherSet = new ConcurrentHashMap<String, Set<Publisher>>();
	
	static ConcurrentHashMap<String, Set<Comment>> commentSet = new ConcurrentHashMap<String, Set<Comment>>();

	public void saveTopic(Topic obj){
		try{
			synchronized(lock){
			Query query = null;
			query = ds.createQuery(Topic.class).field("title").equal(obj.getTitle());
			if(topicDAO.exists(query)){
				topicDAO.deleteByQuery(query);
			}
			topicDAO.save(obj);
		} }catch(Exception x){System.out.println("insert DB error");}
		
	}
	public void savePublisher(Publisher obj){
		try{synchronized(lock){
			Query query = null;
			query = ds.createQuery(Publisher.class).field("name").equal(obj.getName());	
			if(PublisherDAO.exists(query)){
					PublisherDAO.deleteByQuery(query);
			}
			PublisherDAO.save(obj);
		}}catch(Exception x){System.out.println("insert DB error");}
	}
	public void saveComment(Comment obj){
		try{synchronized(lock){
			Query query = null;
			query = ds.createQuery(Comment.class).filter("topicTitle", obj.getTopicTitle())
													.filter("publishDate",obj.getPublishDate());	
			if(!CommentDAO.exists(query)){
				CommentDAO.save(obj);
			}
		}}catch(Exception x){System.out.println("insert DB error");}
	}
	
	/*void saveUglyDB(){
		Query query = null;
		Iterator it = null;
		
		synchronized(topicSet){
			it = topicSet.get(code).iterator(); // 获得一个迭代子 
			while(it.hasNext()) { 
				Topic obj = (Topic) it.next(); // 得到下一个元素 
				query = ds.createQuery(Topic.class).field("title").equal(obj.getTitle());
				if(topicDAO.exists(query)){
					topicDAO.deleteByQuery(query);
				}
				topicDAO.save(obj);
			} 
		}
		
		synchronized(publisherSet){
			it = publisherSet.get(code).iterator(); // 获得一个迭代子 
			while(it.hasNext()) { 
				Publisher obj = (Publisher) it.next(); // 得到下一个元素 
				query = ds.createQuery(Publisher.class).field("name").equal(obj.getName());	
				if(PublisherDAO.exists(query)){
					PublisherDAO.deleteByQuery(query);
				}
				PublisherDAO.save(obj);
			}
		}
		
		synchronized(commentSet){
			it = commentSet.get(code).iterator(); // 获得一个迭代子 
			while(it.hasNext()) { 
				Comment obj = (Comment) it.next(); // 得到下一个元素 
				query = ds.createQuery(Comment.class).filter("topicTitle", obj.getTopicTitle())
													.filter("publishDate",obj.getPublishDate());	
				if(!CommentDAO.exists(query)){
					CommentDAO.save(obj);
				}
			}
		}
		
		
//		topicSet.get(code).clear();
//		publisherSet.get(code).clear();
//		commentSet.get(code).clear();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

/*	public static void main(String args[]){
		HashSet<String> s = new HashSet<String>();
		s.add("111");
		s.add("112");
		System.out.println(s.size());
	}*/

}
