package crawl;



import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;

public class PageThread implements Runnable {
	
	TagNode tag = null;
	String frameUrl = null;
	String code = null;
	MongoDB db = null;
	static long topicCount = 0;
	static ConcurrentHashMap<String, Integer> OtherDateT = new ConcurrentHashMap<String, Integer>();
	public PageThread() {
	}
	
	public PageThread(TagNode tag,String frameUrl,String code,MongoDB db) {
		this.tag = tag;				//当前topic标签
		this.frameUrl = frameUrl;	//topic链接
		this.code = code;
		this.db = db;
	}

	@Override
	public void run() {
		//生成新的topic
		Topic tempTopic = new Topic();
		tempTopic.uri = frameUrl;
		tempTopic.title = tag.toPlainTextString();

		
		// 获取其他的话题信息
		// 1.点击数
		Node fathertag = tag.getParent().getParent();
		Node topicClick = fathertag.getFirstChild().getNextSibling();
		tempTopic.topicClick = Long.parseLong(topicClick
				.toPlainTextString());
		//System.out.println(tempTopic.topicClick);
		
		// 2.回复数
		Node topicComment = topicClick.getNextSibling();
		tempTopic.topicComment = Long.parseLong(topicComment
				.toPlainTextString());
		// System.out.println(tempTopic.topicComment);
		
		// 3.发帖人
		Node topicPublisher = topicClick.getNextSibling()
				.getNextSibling().getNextSibling();
		String topicPublisherName = topicPublisher.toPlainTextString();
		topicPublisherName = topicPublisherName
				.replaceAll("&nbsp;", "");
		topicPublisherName = topicPublisherName.replaceAll("&amp;", "");
		// System.out.println(topicPublisherName);
		//如果已有publisher记录则提取原先对象，否则新建publisher对象
		//System.out.println(UglyDB.publisherSet.size());
		Publisher p = new Publisher(topicPublisherName,0,0);
//		UglyDB.publisherSet.get(code).add(p);
		db.savePublisher(p);
		
		
		// 4.发表时间
		Date publishTime = new Date();
		PageHandle commentpage = new PageHandle();
		String htmlcode = commentpage.downloadpage(frameUrl.substring(0, frameUrl.length()-5)+",d.html");
		if(htmlcode != null)
		{	
			Date tempTime = commentpage.GetPublishDate(htmlcode);
			if(tempTime != null)
				publishTime = tempTime;
		}
		else
			System.out.println("publishtime parser error!!!!!!!!!!!!!!!!!!!!!!");
		tempTopic.publishDate = publishTime;
//		System.out.println(tempTopic.publishDate);
		
		// 5.最后回复时间
		if(htmlcode != null)		
		{
			Date tempTime1 = commentpage.GetUpdateTime(htmlcode);
			if(tempTime1 != null)
				tempTopic.updateDate = tempTime1;
			else
				tempTopic.updateDate = tempTopic.publishDate;
		}
		else
				tempTopic.updateDate = tempTopic.publishDate;
//		System.out.println(tempTopic.updateDate);
//		System.out.println(CrawlTime.START_TIME+"   "+CrawlTime.END_TIME);
		//判断topic是否在2013年6月1号之后
		if(tempTopic.updateDate.after(CrawlTime.START_TIME) && tempTopic.updateDate.before(CrawlTime.END_TIME) ){
			//判断是否在当前爬取时间范围内
			if(tempTopic.publishDate.after(CrawlTime.INITIAL_TIME)){
				//System.out.println("fabiao时间--------------->"+tempTopic.updateDate);
				//新增topic，全部爬取
//				tempTopic.getDetailComments(code,db);
				if(tempTopic.topicComment >3 )
					AllStock.tt.execute( new CommentThread(tempTopic,code,db));
				db.saveTopic(tempTopic);
			}
		}
		else{
			int OtherDateTnum = OtherDateT.get(code).intValue();
			OtherDateT.put(code, ++OtherDateTnum);
			//System.out.println(tempTopic.title);
			//System.out.println(tempTopic.updateDate);
		}
	}

}
