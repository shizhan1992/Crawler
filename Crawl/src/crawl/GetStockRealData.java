package crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.google.gson.Gson;

public class GetStockRealData {
	public static final String stocklistpage = "http://guba.eastmoney.com/geguba_list.html";
	public static final String SOHU_FINANCE_URL = "http://q.stock.sohu.com/hisHq?";
	public static final String stopstockpath = "D:/stock_realdata/stopstock.txt";
	public static final String suspendstockpath = "D:/stock_realdata/suspendstock.txt";
	public static DecimalFormat df = new DecimalFormat("#.00");
	public static DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

	public static Set<String> GetAllStock() {
		PageHandle pagehandle = new PageHandle();
		String htmlcode = pagehandle.downloadpage(stocklistpage);

		if (htmlcode != null) {
			Parser parser = null;
			NodeList list = null;
			Set<String> s = new HashSet<String>();
			// System.out.println(htmlcode);
			try {
				parser = Parser.createParser(htmlcode, "utf-8");
				parser.setEncoding("utf-8");
				NodeFilter frameFilter = new LinkRegexFilter(
						"topic,(([6903]0)|(200))");
				list = parser.extractAllNodesThatMatch(frameFilter);
				System.out.println(list.size());
			} catch (ParserException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < list.size(); i++) {
				TagNode tag = (TagNode) list.elementAt(i);
				String stock = tag.toPlainTextString();

				String regex1 = "\\(\\d{6}\\).*?";
				Pattern pattern = Pattern.compile(regex1);
				Matcher matcher = pattern.matcher(stock);
				while (matcher.find()) {
					s.add(stock);
				}
			}
			System.out.println(s.size());
			return s;
		}
		return null;
	}

	private static Stockdata GetDataFromJson(String code, String fromDate,
			String toDate) {
		String url = SOHU_FINANCE_URL + "code=cn_" + code + "&start="
				+ fromDate + "&end=" + toDate + "&order=A&period=d&rt=json";
		System.out.println(url);
		URL MyURL = null;
		URLConnection con = null;
		String json = "";
		try {
			MyURL = new URL(url);
			con = MyURL.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				json = json + line;
				// System.out.println(line);
			}
			reader.close();
			json = json.substring(1, json.length() - 1);
			// System.out.println(json);

		} catch (IOException es) {
			es.printStackTrace();
		}

		Gson gson = new Gson();
		Stockdata data = gson.fromJson(json, Stockdata.class);
		// System.out.println(data.getCode()+"    "+data.getStatus() );
		return data;
	}

	public static void writestopstock(String pathname, String code) {
		File log = new File(pathname);
		try {
			FileWriter fileWriter = new FileWriter(log, true);
			fileWriter.write(code + "\r\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void getStockCsvData(String code, String fromDate,
			String toDate) {
		Stockdata data = GetDataFromJson(code, fromDate, toDate);
		if (data == null)
			writestopstock(suspendstockpath, code);
		else {
			String[][] hisdata = data.getHq();
			if (hisdata == null)
				writestopstock(stopstockpath, code);
			else {
				System.out.println(hisdata.length);
				WritableWorkbook book = null;
				try {
					book = Workbook.createWorkbook(new File(
							"D:/stock_realdata/" + code + ".xls"));
					WritableSheet sheet = book.createSheet("第一页", 0);
					String[] title = { "时间", "开盘", "收盘", "振幅", "涨幅", "最低",
							"最高", "总手", "总金", "换手率" };
					for (int i = 0; i < title.length; i++) {
						Label lable = new Label(i, 0, title[i]);
						sheet.addCell(lable);
					}

					for (int i = 0; i < hisdata.length; i++)
						for (int j = 0; j < hisdata[0].length; j++) {
							if (j == 3) {
								String zhengfu = df
										.format(100
												* (Double
														.parseDouble(hisdata[i][6]) - Double
														.parseDouble(hisdata[i][5]))
												/ (Double
														.parseDouble(hisdata[i][2]) - Double
														.parseDouble(hisdata[i][3])));
								hisdata[i][3] = zhengfu + "%";
							}
							Label lable = new Label(j, i + 1, hisdata[i][j]);
							sheet.addCell(lable);
						}
					book.write();
					book.close();
					System.out.println("写入成功");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void AddDataToXls(String code, String todayDate) {
		Stockdata todaydata = GetDataFromJson(code, todayDate, todayDate);
		if (todaydata == null)
			writestopstock(suspendstockpath, code);
		else {
			String[][] hisdata = todaydata.getHq();
			if (hisdata == null)
				writestopstock(stopstockpath, code);
			else {
				try {
					File file = new File("D:/stock_realdata/" + code + ".xls");
					if (!file.exists()) {
						WritableWorkbook book = Workbook.createWorkbook(file);
						WritableSheet sheet = book.createSheet("第一页", 0);
						String[] title = { "时间", "开盘", "收盘", "振幅", "涨幅", "最低",
								"最高", "总手", "总金", "换手率" };
						for (int i = 0; i < title.length; i++) {
							Label lable = new Label(i, 0, title[i]);
							sheet.addCell(lable);
						}
						book.write();
						book.close();
					}
					Workbook book = Workbook.getWorkbook(file);
					Sheet sheet = book.getSheet(0);
					// 获取行
					int length = sheet.getRows();
					// System.out.println(length);
					WritableWorkbook wbook = Workbook
							.createWorkbook(file, book); // 根据book创建一个操作对象
					WritableSheet sh = wbook.getSheet(0);// 得到一个工作对象

					// 从最后一行开始加
					for (int j = 0; j < hisdata[0].length; j++) {
						if (j == 3) {
							String zhengfu = df
									.format(100
											* (Double
													.parseDouble(hisdata[0][6]) - Double
													.parseDouble(hisdata[0][5]))
											/ (Double
													.parseDouble(hisdata[0][2]) - Double
													.parseDouble(hisdata[0][3])));
							hisdata[0][3] = zhengfu + "%";
						}
						Label lable = new Label(j, length, hisdata[0][j]);
						sh.addCell(lable);
					}
					wbook.write();
					wbook.close();
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BiffException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean DeleteFolder(String sPath) {
		boolean flag = false;
		File file = new File(sPath);

		if (!file.exists()) {
			return flag;
		} else {
			return file.delete();
		}
	}

	public static void main(String[] args) {
		Set<String> s = GetAllStock();
		Iterator<String> it = s.iterator();

		while (it.hasNext()) {
			String stockinfo = it.next();
			String code = stockinfo.substring(1, 7);
			System.out.println(code);
			GetStockRealData.getStockCsvData(code, "20140301", "20140418");
		}

		while (true) {
			Date date = new Date();
			if (date.getDay() != 0 && date.getDay() != 6) {
				SimpleDateFormat timedf = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
				String todayDate = timedf.format(new Date());
				System.out.println(todayDate);
				Set<String> sadd = GetAllStock();
				Iterator<String> iter = sadd.iterator();
				while (iter.hasNext()) {
					String stockinfo = iter.next();
					String code = stockinfo.substring(1, 7);
					GetStockRealData.AddDataToXls(code, todayDate);
				}

			}
			try {
				Thread.sleep(24 * 60 * 60 * 1000 - 4 * 60 * 1000);
				GetStockRealData.DeleteFolder(suspendstockpath);
				GetStockRealData.DeleteFolder(stopstockpath);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// GetStockRealData.AddDataToXls("000333","20140326");
		// GetStockRealData.getStockCsvData("000157", "20130601", "20140327");
	}

	public class Stockdata {
		String status;
		String[][] hq;
		String code;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String[][] getHq() {
			return hq;
		}

		public void setHq(String[][] hq) {
			this.hq = hq;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}
}
