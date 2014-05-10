package crawl;


import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
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
	public static ExecutorService tt = null;
	
	//get all stock_code from stocklistpage
	public static Set<String> GetAllStock(){
		PageHandle topicpage = new PageHandle();
		String htmlcode = null;
		htmlcode = topicpage.downloadpage(stocklistpage);
		
		if(htmlcode != null){
			Parser parser = null;
			NodeList list = null;
			Set<String> s = new HashSet<String>();
			//System.out.println(htmlcode);
			try {
				parser =Parser.createParser(htmlcode, "utf-8");
				parser.setEncoding("utf-8");
				NodeFilter frameFilter = new LinkRegexFilter("topic,(([6903]0)|(200))");
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
	
	//main Thread 
	void getStock(Set<String> s) {
		int stocksnumber = 0;
		Iterator<String> it = s.iterator();
		MongoDB.initDB("guba");
		//loop crawling all stocks
		while(it.hasNext()){
			//initialize a threadpool
			tt = Executors.newCachedThreadPool();
			int x = 0;
			
			//everytime get [stocknumber] stocks' data, preventing the heap overflowing
 			for(int j=0; j<stocknumber; j++){
				if(it.hasNext()){
					String stockinfo = it.next();
					String code = stockinfo.substring(1, 7);  
					String name = stockinfo.substring(8,stockinfo.length());
					System.out.println("name = "+name+"code ="+code);
					codes[j]=code;
					x++;
				}
			}
			System.out.println(x);
			for(int i=0; i<x; i++){
				tt.execute(new StockStart(codes[i]));
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
			
			stocksnumber+=stocknumber;
			System.out.println("tt������"+((ThreadPoolExecutor) tt).getActiveCount());
			if(((ThreadPoolExecutor) tt).getActiveCount()>0){
				System.out.println("the tt number doesn't equal 0 ");
			}
			tt.shutdown();
			
			System.out.println("-------------"+new Date()+"-------������Ʊ������"+stocksnumber+"---------------");
		}
		MongoDB.close();
	}
	
	public static void main(String args[]){
		while(true){
			Set<String> s = GetAllStock();	
//			Set<String> s = new HashSet<String>();
//			s.add(" 000559����");
//			s.add(" 000001ƽ��");
//			s.add(" 000002���");
//			s.add(" 000003����");
//			s.add(" 000004��ũ");
			if(s != null){
				AllStock allstock = new AllStock();
				allstock.getStock(s);
			}
		}
	}
}
