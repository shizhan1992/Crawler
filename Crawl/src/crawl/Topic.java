package crawl;

import java.io.Serializable;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;


import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;

@Entity(value="Topics",noClassnameStored=true)
public class Topic implements Serializable{
	@Id
	private String id;
	
	@Indexed(value = IndexDirection.ASC, name = "topictitle")
	String title;
	
	@Embedded
	Publisher publisher;
	
	String uri;
	long topicClick;
	long topicComment;
	Date publishDate;
	Date updateDate;

	//Set<Publisher> commentators = new HashSet<Publisher>();
	//Set<NewsSource> newsSources = new HashSet<NewsSource>();
	//Set<Comment> comment = new HashSet<Comment>();

	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getTopicClick() {
		return topicClick;
	}

	public void setTopicClick(long topicClick) {
		this.topicClick = topicClick;
	}

	public long getTopicComment() {
		return topicComment;
	}

	public void setTopicComment(long topicComment) {
		this.topicComment = topicComment;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}



	void getDetailComments(String code, MongoDB db) {
		
		//����ظ�������3��������ҳ���μ����̳߳�������
		if(topicComment > 3){
			for (int i = 0; i <= topicComment / 41; i++) {
				AllStock.tt.execute( new CommentThread(uri.substring(0, uri.length() - 5) + "_"
						+ (i + 1) + ".html", uri, title,code,db));
			}
		}
	}
/*
	public void getCommentFromPage(String pageUri) {
		try {
			System.out.println(pageUri);
			//��������ҳ
			PageHandle commentpage = new PageHandle();
			String htmlcode = commentpage.downloadpage(pageUri);
			//System.out.println(htmlcode);
			
			//����ҳ�棬���ض�filter��ȡNode
			if(htmlcode!=null){
			Parser parser = new Parser(htmlcode);
			
			parser.setEncoding(parser.getEncoding());
			AndFilter filter = new AndFilter( 
	                              new TagNameFilter("div"), 
	                             new HasAttributeFilter("class","zwli clearfix") 
	              ); 
			NodeList list = parser.extractAllNodesThatMatch(filter);
			//System.out.println("hehe"+list.size());
			
			if(list.size()>0)
			{
				//��ʼ��ȡcomments
				for(int i = 0; i<list.size(); i++){
					String commentStr = null;
					String commentPublisher = null;
					Date commentDate = null;
					//��ȡ������node
					Node node = list.elementAt(i).getLastChild().getPreviousSibling()
															.getFirstChild().getNextSibling();
					if(node.getText().indexOf("zwlitxt")>-1){
						//�����������ڵ�
						Node compub = node.getFirstChild().getNextSibling()
													.getFirstChild().getNextSibling();
						//�����ʵ���û�
						if(compub.toHtml().indexOf("href")>-1){
							commentPublisher = compub.toPlainTextString();
							commentPublisher = commentPublisher.replaceAll("&nbsp;", "");
							commentPublisher = commentPublisher.replaceAll("&amp;", "");
							System.out.println(commentPublisher);
							
							//�������ݽڵ�
							Node ComStrTag = node.getLastChild().getPreviousSibling()
														.getPreviousSibling().getPreviousSibling();
							//another condition, multiply reply
							
							if(ComStrTag != null)
							    commentStr = ComStrTag.toPlainTextString();
							    System.out.println(commentStr);
							    //handle the commentStr
							    
							 //�������ڽڵ�
							 Node comdate = ComStrTag.getNextSibling().getNextSibling().getFirstChild().getNextSibling();
							 if(comdate.getText().indexOf("zwlitxb") > -1){
							    String commentDateStr = comdate.toPlainTextString();
							    DateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
								try {
									commentDate = format.parse(commentDateStr);
									System.out.println("commentDate--------------->"+commentDateStr);
								} catch (Exception e) {
									//handle date error
								}
							}
								
								Publisher p = new Publisher(commentPublisher,0,0);
								UglyDB.publisherSet.get(code).add(p);
								
								Comment tempComment = new Comment(p, commentDate,
										this.title,commentStr,uri);
								
							    UglyDB.commentSet.get(code).add(tempComment);
						}
					}
				}
			}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if (e instanceof ConnectException){
				System.out.println("hehe");
			}
			e.printStackTrace();
		}
	}

	
	public static void main(String args[]){
		UglyDB db = new UglyDB();
		db.initDB("000157");
		Topic topic = new Topic();
		topic.getCommentFromPage("http://guba.eastmoney.com/news,000157,94721557.html");
	}
	*/
}
