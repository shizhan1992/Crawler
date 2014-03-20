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
	String pageUri;
	//Set<Comment> comment = null;
	String uri = null; 
	String title = null;
	String code = null;
	MongoDB db = null;
	public CommentThread(String pageUri, String uri, String title, String code, MongoDB db){
		this.pageUri = pageUri;
		//this.comment = comment;
		this.title = title;
		this.uri = uri;
		this.code = code;
		this.db = db;
	}
	
	@Override
	public void run() {
		
		try {
			System.out.println(pageUri);
			//��������ҳ
			PageHandle commentpage = new PageHandle();
			String htmlcode = commentpage.downloadpage(pageUri);
			
			//����ҳ�棬���ض�filter��ȡNode
			if(htmlcode!=null){
			Parser parser = new Parser(htmlcode);
			
			parser.setEncoding("utf-8");
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
						//if(compub.toHtml().indexOf("href")>-1){
							commentPublisher = compub.toPlainTextString();
							commentPublisher = commentPublisher.replaceAll("&nbsp;", "");
							commentPublisher = commentPublisher.replaceAll("&amp;", "");
							//System.out.println(commentPublisher);
							
							//�������ݽڵ�
							Node ComStrTag = node.getLastChild().getPreviousSibling()
														.getPreviousSibling().getPreviousSibling();
							//another condition, multiply reply
							
							if(ComStrTag != null)
							    commentStr = ComStrTag.toPlainTextString();
							    //System.out.println(commentStr);
							    //handle the commentStr
							    
							 //�������ڽڵ�
							 Node comdate = ComStrTag.getNextSibling().getNextSibling().getFirstChild().getNextSibling();
							 if(comdate.getText().indexOf("zwlitxb") > -1){
							    String commentDateStr = comdate.toPlainTextString();
							    DateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
								try {
									commentDate = format.parse(commentDateStr);
									//System.out.println(commentDate);
								} catch (Exception e) {
									//handle date error
								}
							}
							
							Publisher p = new Publisher(commentPublisher,0,0);
//							UglyDB.publisherSet.get(code).add(p);
							db.savePublisher(p);
							
							Comment tempComment = new Comment(p, commentDate,
									this.title,commentStr,uri);
							
//						    UglyDB.commentSet.get(code).add(tempComment);
							db.saveComment(tempComment);
							
							//��ӽ�topic��������ۼ���
							//comment.add(tempComment);	
					}
				}
			}
			}
		} catch (Exception e) {
			if (e instanceof ConnectException){
				System.out.println("hehe");
			}
			e.printStackTrace();
		}
	}	
}
