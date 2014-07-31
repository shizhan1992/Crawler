package crawl;

import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;

public class StockStart implements Runnable {
	private String code = null;
	private String url = null;
	private ExecutorService tt = null;
	private static final String gubaBaseUri = "http://guba.eastmoney.com/";
	CrawlTime crawltime = null;

	public StockStart(String code) {
		this.code = code;
	}

	public void run() {
		tt = Executors.newFixedThreadPool(85);
		url = "http://guba.eastmoney.com/list," + code + ".html";
		MongoDB.createCollection(code);
		try {
			crawltime = new CrawlTime();
			crawltime.init(code);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		getRecentTopics();
	}

	void getRecentTopics() {
		int OtherDateT = 0;
		int pageNumber = 1;

		do {
			PageThread.OtherDateT.put(code, 0);
			OtherDateT = getTopicFromPage(url.substring(0, url.length() - 5)
					+ "_" + pageNumber + ".html");
			pageNumber++;
		} while (OtherDateT < 7);
		tt.shutdown();
	}

	// 获取topic
	private int getTopicFromPage(String pageUri) {
		try {
			// 下载当前页面
			PageHandle topicpage = new PageHandle();
			String htmlcode = topicpage.downloadpage(pageUri);
			// 解析页面，提取有“news”字段的链接tag
			if (htmlcode != null) {
				Parser parser = new Parser(htmlcode);
				parser.setEncoding(parser.getEncoding());
				NodeFilter frameFilter = new LinkRegexFilter("news");
				NodeList list = parser.extractAllNodesThatMatch(frameFilter);
				// System.out.println(list.size());

				// 将topic依次放入线程池中运行。
				for (int i = 0; i < list.size(); i++) {
					TagNode tag = (TagNode) list.elementAt(i);
					String frameUrl = tag.getAttribute("href");// 提取链接
					if (frameUrl.indexOf("guba") == -1)
						frameUrl = StockStart.gubaBaseUri + frameUrl;
					if (frameUrl.indexOf(this.code) == -1)
						continue;
					tt.execute(new PageThread(tag, frameUrl, code, crawltime));
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (((ThreadPoolExecutor) tt).getActiveCount() > 0) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (list.size() < 80) {
					System.out.println(code + "股票只有一页");
					return 100;
				}
				// System.out.println(code +
				// " topic not in range = "+PageThread.OtherDateT.get(code));
				return PageThread.OtherDateT.get(code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 100;
	}
}
