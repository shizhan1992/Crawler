package guba;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class StockData {

	Map<String, StockOneDayData> daylyData = new HashMap<String, StockOneDayData>();
	String excelFile;

	public static void main(String args[]) {
		StockData myStockData = new StockData();
		myStockData.getDataFromXml("test.xls");
	}

	public StockData() {
                                                                                                                                                                                                                                                                                                                                                                                                          
	}

	public StockData(String excelFile) {
		this.excelFile = excelFile;
	}

	void getDataFromXml(String excelFile) {
		this.excelFile = excelFile;
		getDataFromXml();
	}

	void getDataFromXml() {
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		jxl.Workbook rwb = null;
		try {
			
			// ����Workbook���� ֻ��Workbook����
			// ֱ�Ӵӱ����ļ�����Workbook
			// ������������Workbook
			// InputStream is = new FileInputStream(XmlFile);
			// rwb = Workbook.getWorkbook(is);
			rwb = Workbook.getWorkbook(new File(excelFile));

			// Sheet(���������)����Excel������½ǵ�Sheet1,Sheet2,Sheet3���ڳ�����
			// Sheet���±��Ǵ�0��ʼ��
			// ��ȡ��һ��Sheet��
			Sheet rs = rwb.getSheet(0);
			// ��ȡSheet������������������
//			int rsColumns = rs.getColumns();
			// ��ȡSheet������������������
			int rsRows = rs.getRows();
			// ��ȡָ���µ�Ԫ��Ķ�������
//			System.out.println("StockData 98th priceChange:");
			for (int i = 0; i < rsRows; i++) {
				// for (int j = 0; j < rsColumns; j++) {
				Cell cell;
				cell = rs.getCell(0, i);// Date
				// System.out.print(cell.getContents() + " ");
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date myDate = format.parse(cell.getContents());
				//System.out.println(df.format(myDate));

				cell = rs.getCell(1, i);// openingPrice
				double openPrice = Double.parseDouble(cell.getContents());
				 //System.out.print(openingPrice);

				cell = rs.getCell(2, i);// highPrice
				double highPrice = Double.parseDouble(cell.getContents());
				// System.out.print(openingPrice);
				
				cell = rs.getCell(3, i);// lowPrice
				double lowPrice = Double.parseDouble(cell.getContents());
				// System.out.print(openingPrice);
				
				cell = rs.getCell(4, i);// closingPrice
				double closePrice = Double.parseDouble(cell.getContents());
				// System.out.print(closingPrice);
				
				String tempPriceChange;
				double priceChange;//�Ƿ�
				if(i == 0)
					priceChange=0.0;
				else{
					cell =rs.getCell(5, i);
					tempPriceChange = cell.getContents();					
					//��Stringת��double
					priceChange = new Double(tempPriceChange.substring(0,tempPriceChange.indexOf("%")))/100;				
				}  
				
				String tempAmplitude;
				double amplitude;//���
				if(i == 0)
					amplitude=0.0;
				else{
					cell =rs.getCell(6, i);
					tempAmplitude= cell.getContents();					
					//��Stringת��double
					amplitude = new Float(tempAmplitude.substring(0,tempAmplitude.indexOf("%")))/100;				
				}

				cell = rs.getCell(7, i);// tradingVolume
				double tradingVolume = Long.parseLong(cell.getContents());
				// System.out.print(tradingVolume);

				cell = rs.getCell(8,i);//amount���׽��
				double amount = Double.parseDouble(cell.getContents());
				
				cell = rs.getCell(9,i);//turnRate������
				double turnRate = Double.parseDouble(cell.getContents());
				
				StockOneDayData myData = new StockOneDayData();
				myData.closePrice = closePrice;
				myData.date = myDate;
				myData.openPrice = openPrice;
				myData.tradingVolume = tradingVolume;				
	 			myData.priceChange = priceChange;				
				myData.highprice =highPrice;
				myData.lowprice = lowPrice;
				myData.amplitude = amplitude;
				myData.amount = amount;
				myData.turnRate = turnRate;
				

				daylyData.put(myDate.toString(), myData);
				// }
				// System.out.println("StockData 89th "+df.format(myDate));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// �������ʱ���رն��󣬷���ռ�õ��ڴ�ռ�
			rwb.close();
		}

	}
}
