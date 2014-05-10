package crawl;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;

public class PageThread implements Runnable {
	
	TagNode tag = null;
	String frameUrl = null;
	String code = null;
	CrawlTime crawltime = null;
	static long topicCount = 0;
	static ConcurrentHashMap<String, Integer> OtherDateT = new ConcurrentHashMap<String, Integer>();
	public PageThread() {
	}
	
	public PageThread(TagNode tag,String frameUrl,String code,CrawlTime crawltime) {
		this.tag = tag;				//��ǰtopic��ǩ
		this.frameUrl = frameUrl;	//topic����
		this.code = code;
		this.crawltime = crawltime;
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
		tempTopic.setPublisher(p);
		MongoDB.savePublisher(p,code);
		
		
		// 4.����ʱ��
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
		tempTopic.topicPublishDate = publishTime;
//		System.out.println(tempTopic.publishDate);
		
		// 5.���ظ�ʱ��
		if(htmlcode != null)		
		{
			Date tempTime1 = commentpage.GetUpdateTime(htmlcode);
			if(tempTime1 != null)
				tempTopic.updateDate = tempTime1;
			else
				tempTopic.updateDate = tempTopic.topicPublishDate;
		}
		else
				tempTopic.updateDate = tempTopic.topicPublishDate;
//		System.out.println(tempTopic.updateDate);
//		System.out.println(CrawlTime.START_TIME+"   "+CrawlTime.END_TIME);
		//�ж�topic�Ƿ���2013��6��1��֮��
		if(tempTopic.updateDate.after(crawltime.START_TIME) && tempTopic.updateDate.before(crawltime.END_TIME) ){
			//�ж��Ƿ��ڵ�ǰ��ȡʱ�䷶Χ��
			if(tempTopic.topicPublishDate.after(crawltime.INITIAL_TIME)){
				//System.out.println("fabiaoʱ��--------------->"+tempTopic.updateDate);
				//����topic��ȫ����ȡ
//				tempTopic.getDetailComments(code,db);
				if(tempTopic.topicComment >3 )
					AllStock.tt.execute( new CommentThread(tempTopic,code,crawltime));
				MongoDB.saveTopic(tempTopic,code);
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
