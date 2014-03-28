package crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.Gson;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class SaveHistoryData {
	public static final String  YAHOO_FINANCE_URL  = "http://q.stock.sohu.com/hisHq?";
	public static final String YAHOO_FINANCE_URL_TODAY = "http://download.finance.yahoo.com/d/quotes.csv?";
	public static DecimalFormat df = new DecimalFormat("#.00");
	public static void main(String[] args) {
//		Set<String> s = AllStock.GetAllStock();
//		Iterator<String> it = s.iterator();
//		while(it.hasNext()){
//			String stockinfo = it.next();
//			String code = stockinfo.substring(1, 7);
//			if(code.startsWith("60")){
//			}
//		}
		SaveHistoryData.getStockCsvData("000157", "20130601", "20140326");
		//SaveHistoryData.AddDataToXls("000157");
	}
	public class Stockdata{
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
	 public static void getStockCsvData(String code, String fromDate,String toDate) {
		 	Stockdata data = GetDataFromJson(code, fromDate, toDate);
	        String[][] hisdata= data.getHq();
	        WritableWorkbook book = null;
			try {
				book = Workbook.createWorkbook(new File("D:/"+code+".xls"));
				WritableSheet sheet = book.createSheet("第一页", 0);  
				String[] title = { "时间", "开盘", "收盘", "振幅", "涨幅",  
		                "最低", "最高", "总手", "总金","换手率" };  
		        for (int i = 0; i < title.length; i++) {  
		            Label lable = new Label(i, 0, title[i]);  
		            sheet.addCell(lable);  
		        }  
		        
			    for(int i=0;i<hisdata.length;i++)
			        for(int j=0;j<hisdata[0].length;j++){
			        	if(j==3){ 
			        		String zhengfu = df.format( 100*( Double.parseDouble(hisdata[i][6])-Double.parseDouble(hisdata[i][5]) )/( Double.parseDouble(hisdata[i][2])-Double.parseDouble(hisdata[i][3]) ));
			        		hisdata[i][3] = zhengfu +"%";
			        	}
			        	Label lable = new Label(j, i+1, hisdata[i][j]);  
			            sheet.addCell(lable);  
			        }
			    
			    book.write();  
		        book.close();  
		        System.out.println("写入成功"); 
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
	 }
	 private static Stockdata GetDataFromJson(String code, String fromDate,
			String toDate) {
		 	String url = YAHOO_FINANCE_URL + "code=cn_" + code + "&start=" + fromDate + "&end=" + toDate + "&order=A&period=d&rt=json";
//	        System.out.println(url);
	        URL MyURL = null;
	        URLConnection con = null;
	        String json="";
	        try {
				MyURL = new URL(url);
				con = MyURL.openConnection();
				 BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		         String line = null;
		         while ((line = reader.readLine()) != null)
		         {
		        	 json = json+line;
//		        	 System.out.println(line);
		         }
		         reader.close();
		         json = json.substring(1, json.length()-1);
//				 System.out.println(json);
			        
			} catch (IOException es) {
				es.printStackTrace();
			} 
			
			Gson gson = new Gson();
			Stockdata data = gson.fromJson(json,Stockdata.class);
			return data;
	}
	public static void AddDataToXls(String code){
		 try {
			 Workbook book = Workbook.getWorkbook(new File("D:/"+code+".xls"));  
			 Sheet sheet = book.getSheet(0);  
			 // 获取行  
			 int length = sheet.getRows();  
//			 System.out.println(length);  
			 WritableWorkbook wbook = Workbook.createWorkbook(new File("D:/"+code+".xls"), book); // 根据book创建一个操作对象  
			 WritableSheet sh = wbook.getSheet(0);// 得到一个工作对象  
			 SimpleDateFormat timedf = new SimpleDateFormat("yyyyMMdd");//设置日期格式
			 String todayDate = timedf.format(new Date());
//	     	 System.out.println(todayDate);
	     	 Stockdata todaydata = GetDataFromJson(code, todayDate, todayDate);
	     	 String[][] data =  todaydata.getHq();
	     	 // 从最后一行开始加  
	    	 for(int j=0;j<data[0].length;j++){
	        	if(j==3){ 
	        		String zhengfu = df.format( 100*( Double.parseDouble(data[0][6])-Double.parseDouble(data[0][5]) )/( Double.parseDouble(data[0][2])-Double.parseDouble(data[0][3]) ));
	        		data[0][3] = zhengfu +"%";
	        	}
	        	Label lable = new Label(j, length, data[0][j]);  
				sh.addCell(lable);
	    	 }
	    	 wbook.write();  
	    	 wbook.close();  
	     }catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 }catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		 } catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	 }
}
