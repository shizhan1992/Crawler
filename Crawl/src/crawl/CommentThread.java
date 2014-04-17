package crawl;

import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

public class CommentThread implements Runnable {
	String topicUri;
	//Set<Comment> comment = null;
	String uri = null; 
	String title = null;
	String code = null;
	MongoDB db = null;
	CrawlTime crawltime = null;
	boolean isLastComm = false;
	public CommentThread(){
	}
	
	public CommentThread(Topic tempTopic, String code, MongoDB db, CrawlTime crawltime) {
		this.topicUri = tempTopic.uri;
		this.title = tempTopic.title;
		this.code = code;
		this.db = db;
		this.crawltime = crawltime;
	}

//	public static void main(String args[]){
//		CommentThread test = new CommentThread();
//		test.CrawlComment("http://guba.eastmoney.com/news,002202,107467489,d_20.html");
//	}
	
	@Override
	public void run() {
		int pagenum = 1;
		do{
			String commentPage = topicUri.substring(0, topicUri.length()-5)+",d_"+pagenum+".html";
			CrawlComment(commentPage);
			pagenum++;
		}while(!isLastComm);
		
	}

	private void CrawlComment(String commentPage) {
		try {
			System.out.println(commentPage);
			//下载评论页
			PageHandle commentpage = new PageHandle();
			String htmlcode = commentpage.downloadpage(commentPage);
			
			//解析页面，按特定filter提取Node
			if(htmlcode!=null){
			Parser parser = new Parser(htmlcode);
			
			parser.setEncoding("utf-8");
			AndFilter filter = new AndFilter( 
	                              new TagNameFilter("div"), 
	                             new HasAttributeFilter("class","zwli clearfix") 
	              ); 
			NodeList list = parser.extractAllNodesThatMatch(filter);
//			System.out.println("listsize = "+list.size());
			if(list.size()<40||list.size()==0){
				isLastComm = true;
			}
			if(list.size()>0)
			{
				
				//开始提取comments
				for(int i = 0; i<list.size(); i++){
					String commentStr = null;
					String commentPublisher = null;
					Date commentDate = null;
					//提取主内容node
					Node node = list.elementAt(i).getLastChild().getPreviousSibling()
															.getFirstChild().getNextSibling();
					if(node.getText().indexOf("zwlitxt")>-1){
						//评论人姓名节点
						Node compub = node.getFirstChild().getNextSibling()
													.getFirstChild().getNextSibling();
						//如果是实名用户
						//if(compub.toHtml().indexOf("href")>-1){
							commentPublisher = compub.toPlainTextString();
							commentPublisher = commentPublisher.replaceAll("&nbsp;", "");
							commentPublisher = commentPublisher.replaceAll("&amp;", "");
							//System.out.println(commentPublisher);
							
							//评论内容节点
							Node ComStrTag = node.getLastChild().getPreviousSibling()
														.getPreviousSibling().getPreviousSibling();
							//another condition, multiply reply
							
							if(ComStrTag != null)
							    commentStr = ComStrTag.toPlainTextString();
							    //System.out.println(commentStr);
							    //handle the commentStr
							    
							 //评论日期节点
							 Node comdate = ComStrTag.getNextSibling().getNextSibling().getFirstChild().getNextSibling();
							 if(comdate.getText().indexOf("zwlitxb") > -1){
							    String commentDateStr = comdate.toPlainTextString();
							    DateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
								try {
									commentDate = format.parse(commentDateStr);
									//System.out.println(commentDate);
									if(commentDate.before(crawltime.START_TIME)){
										isLastComm = true;
//										System.out.println(commentStr);
									}
								} catch (Exception e) {
									//handle date error
								}
							}
							
							Publisher p = new Publisher(commentPublisher,0,0);
							db.savePublisher(p);
							
							Comment tempComment = new Comment(p, commentDate,
									this.title,commentStr,topicUri);
							
							db.saveComment(tempComment);
							
					}
				}
			}
			}
			else{
				isLastComm = true;
			}
		} catch (Exception e) {
			if (e instanceof ConnectException){
				System.out.println("hehe");
			}
			e.printStackTrace();
		}
		
	}	
	
}
