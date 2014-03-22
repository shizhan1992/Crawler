package crawl;



import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class AllStock {
	static String stocklistpage = "http://guba.eastmoney.com/geguba_list.html";
	static int stocknumber = 15;
	static String[] codes = new String[stocknumber];
	MongoDB[] db = new MongoDB[stocknumber];
	static int stocksnumber = 0;
//	public static ExecutorService tt = null;
//	public static ExecutorService ss = null;
	public static ExecutorService tt = null;
	public static ThreadPool ss = null;
	private static Set<String> GetAllStock(){
		PageHandle topicpage = new PageHandle();
		String htmlcode = topicpage.downloadpage(stocklistpage);
		
		if(htmlcode != null){
			Parser parser = null;
			NodeList list = null;
			Set<String> s = new HashSet<String>();
			//System.out.println(htmlcode);
			try {
				parser =Parser.createParser(htmlcode, "utf-8");
				parser.setEncoding("utf-8");
				NodeFilter frameFilter = new LinkRegexFilter("topic");
				list = parser.extractAllNodesThatMatch(frameFilter);
				System.out.println(list.size());
			}catch (ParserException e) {
			e.printStackTrace();
			}
			for (int i = 0; i < list.size(); i++) {
				TagNode tag = (TagNode) list.elementAt(i);
				String stock = tag.toPlainTextString();
			
				String regex1 = "\\(\\d{6}\\).*?";
				Pattern pattern = Pattern.compile(regex1);
				Matcher matcher = pattern.matcher(stock);
				while(matcher.find()){	
					s.add(stock);
				}
			}
			System.out.println(s.size());
			return s;
		}
		return null;
	}
	
	void getStock(Set<String> s) {
		//ss = Executors.newFixedThreadPool(10);
		
    	try {
			CrawlTime.init();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Iterator<String> it = s.iterator();
//		int ix = 0;
//		while(it.hasNext()&& ix<3420){
//			String loop = it.next();
//			ix++;
//		}
//		System.out.println(ix);
		
		while(it.hasNext()){
			tt = Executors.newCachedThreadPool();
			for(int j=0; j<stocknumber; j++){
				String stockinfo = it.next();
				String code = stockinfo.substring(1, 7);  
				String name = stockinfo.substring(8,stockinfo.length());
				System.out.println("name = "+name+"code ="+code);
				codes[j]=code;
				db[j] = new MongoDB();
				db[j].initDB(code);
				
			}
			for(int i=0; i<stocknumber; i++){
				tt.execute(new StockStart(codes[i],db[i]));
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while( ((ThreadPoolExecutor) tt).getActiveCount() > 0){ 
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println(((ThreadPoolExecutor) tt).getActiveCount());
			System.out.println(((ThreadPoolExecutor) tt).getActiveCount());
			for(int i=0; i<stocknumber; i++){
				db[i].close();
			}
			stocksnumber+=stocknumber;
			System.out.println("tt个数："+((ThreadPoolExecutor) tt).getActiveCount());
			if(((ThreadPoolExecutor) tt).getActiveCount()>0){
				System.out.println("the tt number doesn't equal 0 ");
			}
			tt.shutdown();
			
			System.out.println("-------------"+new Date()+"-------已爬股票个数："+stocksnumber+"---------------");
		}
	}
	
	public static void main(String args[]){
			Set<String> s = GetAllStock();	
//			Set<String> s = new HashSet<String>();
//			s.add(" 000559万向");
//			s.add(" 000001平安");
//			s.add(" 000002万科");
//			s.add(" 000003金田");
//			s.add(" 000004国农");
			if(s != null){
				AllStock allstock = new AllStock();
				allstock.getStock(s);
			}
	}
}
