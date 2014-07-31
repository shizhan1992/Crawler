package crawl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AllStock extends TimerTask {
	private static int stocknumber = 15;
	private static String[] codes = new String[stocknumber];
	public static ExecutorService tt = null;

	// main Thread
	@Override
	public void run() {
		Set<String> s = GetStockRealData.GetAllStock();
		if (s != null) {
			int stocksnumber = 0;
			Iterator<String> it = s.iterator();
			MongoDB.initDB("guba");
			// loop crawling all stocks
			while (it.hasNext()) {
				// initialize a threadpool
				tt = Executors.newCachedThreadPool();
				int x = 0;

				// everytime get [stocknumber] stocks' data, preventing the heap overflowing
				for (int j = 0; j < stocknumber; j++) {
					if (it.hasNext()) {
						String stockinfo = it.next();
						String code = stockinfo.substring(1, 7);
						String name = stockinfo
								.substring(8, stockinfo.length());
						System.out.println("name = " + name + "code =" + code);
						codes[j] = code;
						x++;
					}
				}
				System.out.println(x);
				for (int i = 0; i < x; i++) {
					tt.execute(new StockStart(codes[i]));
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// getActiveCount() return the approximate number of running threads
				while (((ThreadPoolExecutor) tt).getActiveCount() > 0) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				System.out.println(((ThreadPoolExecutor) tt).getActiveCount());
				System.out.println(((ThreadPoolExecutor) tt).getActiveCount());

				stocksnumber += stocknumber;
				System.out.println("tt个数："
						+ ((ThreadPoolExecutor) tt).getActiveCount());
				if (((ThreadPoolExecutor) tt).getActiveCount() > 0) {
					System.out.println("the tt number doesn't equal 0 ");
				}
				tt.shutdown();

				System.out.println("-------------" + new Date()
						+ "-------已爬股票个数：" + stocksnumber + "---------------");
			}
			MongoDB.close();
		} else {
			System.out.println("无法获取股票代码");
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		//设置定时器，每晚23：59执行程序
		final SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd '23:59:00'");
		// 首次运行时间
		Date startTime = null;
		try {
			startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf
					.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Timer t = new Timer();
		t.scheduleAtFixedRate(new AllStock(), startTime, 24 * 60 * 60 * 1000);

	}

}
