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
		this.tag = tag;				//��ǰtopic��ǩ
		this.frameUrl = frameUrl;	//topic����
		this.code = code;
		this.db = db;
	}

	@Override
	public void run() {
		//�����µ�topic
		Topic tempTopic = new Topic();
		tempTopic.uri = frameUrl;
		tempTopic.title = tag.toPlainTextString();

		
		// ��ȡ�����Ļ�����Ϣ
		// 1.�����
		Node fathertag = tag.getParent().getParent();
		Node topicClick = fathertag.getFirstChild().getNextSibling();
		tempTopic.topicClick = Long.parseLong(topicClick
				.toPlainTextString());
		//System.out.println(tempTopic.topicClick);
		
		// 2.�ظ���
		Node topicComment = topicClick.getNextSibling();
		tempTopic.topicComment = Long.parseLong(topicComment
				.toPlainTextString());
		// System.out.println(tempTopic.topicComment);
		
		// 3.������
		Node topicPublisher = topicClick.getNextSibling()
				.getNextSibling().getNextSibling();
		String topicPublisherName = topicPublisher.toPlainTextString();
		topicPublisherName = topicPublisherName
				.replaceAll("&nbsp;", "");
		topicPublisherName = topicPublisherName.replaceAll("&amp;", "");
		// System.out.println(topicPublisherName);
		//�������publisher��¼����ȡԭ�ȶ��󣬷����½�publisher����
		//System.out.println(UglyDB.publisherSet.size());
		Publisher p = new Publisher(topicPublisherName,0,0);
//		UglyDB.publisherSet.get(code).add(p);
		db.savePublisher(p);
		
		
		// 4.����ʱ��
		Date publishTime = new Date();
		PageHandle commentpage = new PageHandle();
		String htmlcode = commentpage.downloadpage(frameUrl);
		if(htmlcode != null)
		{	
			Date tempTime = commentpage.HandlerBody(htmlcode);
			publishTime = tempTime;
		}
		else
			System.out.println("publishtime parser error!!!!!!!!!!!!!!!!!!!!!!");
		tempTopic.publishDate = publishTime;
			
		// 5.���ظ�ʱ��
		Date updateDate = new Date();
			
		long lastpage = (tempTopic.topicComment-1)/40+1;
 		String htmlcode1 = commentpage.downloadpage(frameUrl.substring(0, frameUrl.length() - 5) + "_"
					+ lastpage + ".html");
		if(htmlcode1 != null)		
		{
			Date tempTime1 = commentpage.Handlerlastpage(htmlcode1);
			if(tempTime1 != null)
				updateDate = tempTime1;
		}
		else
			updateDate = tempTopic.publishDate;
		tempTopic.updateDate = updateDate;
		
		//�ж�topic�Ƿ���2013��6��1��֮��
		if(tempTopic.updateDate.after(CrawlTime.START_TIME) && tempTopic.updateDate.before(CrawlTime.END_TIME) ){
			//�ж��Ƿ��ڵ�ǰ��ȡʱ�䷶Χ��
			if(tempTopic.publishDate.after(CrawlTime.INITIAL_TIME)){
				//System.out.println("fabiaoʱ��--------------->"+tempTopic.updateDate);
				//����topic��ȫ����ȡ
				tempTopic.getDetailComments(code,db);
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
