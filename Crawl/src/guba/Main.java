package guba;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class Main {

	/**
	 * @param args
	 */
	static String START_DATE = "2013-11-11 00:00:00";
	static int TEST_DAY = 3;
	static String BasicPath = "D:/Users/duxq/workspace/stockPrediction/";
	static DBCollection topics = null;
	static DBCollection comments = null;
	static DBCollection publishers = null;
	static DBCollection stocks = null;
	static DBCollection results  = null;
	public static void main(String[] args) {

		StockData myStockData = new StockData();
		myStockData.getDataFromXml("test.xls");

		Mongo mg = null;
		try {
			mg = new Mongo();
//			mg = new Mongo("localhost",27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DB db = mg.getDB("guba");
		 topics = db.getCollection("topics");
		 comments = db.getCollection("comments");
		 publishers = db.getCollection("publishers");
		 stocks = db.getCollection("stocks");
		 results = db.getCollection("results");
		
		
		FinancialData fff = new FinancialData();
		NetData nnn = new NetData();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null;
		try {
			startDate = df.parse(START_DATE);
			// endDate = df.parse("2012-10-07 00:00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date start = (Date) startDate.clone();
		LinkedList<FinancialFeature> financialList = fff.obtainFaniacialData(
				start, TEST_DAY);
		LinkedList<NetFeature> netFeatureList = nnn.obtainNetData(start,
				TEST_DAY);
		Filter filter = new Filter();
		filter.data((List<FinancialFeature>) financialList.clone(),
				(List<NetFeature>) netFeatureList.clone(), 1);// 过滤掉停盘的天
		NeuralNetworkData startAnalysis = new NeuralNetworkData();
		startAnalysis.saveData(filter);

		String file1Path = BasicPath+"DataBook.xls";
		File basicDataFile = new File(file1Path);
		System.out.println("file1.length:" + basicDataFile.length());
		
		String file2Path = BasicPath+"normalized.xls";
		File normalizedFile = new File(file2Path);
		
		String file3Path = BasicPath+"TrainSet.xls";
		File trainFile = new File(file3Path);
		
		String file4Path = BasicPath+"src/guba/data/TrainSet.csv";
		File trainSetFile = new File(file4Path);
		
		NeuralNetworkData networkData = new NeuralNetworkData();
		try {
			networkData.copyExcel(file1Path, file2Path);
			System.out.println("file2.length:" + normalizedFile.length());
			networkData.normalization(normalizedFile);// 归一化处理
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 将归一化的数据中的日期去掉，并存为TrainSet.csv供训练使用
		try {
			networkData.copyExcel(file2Path, file3Path);
			System.out.println("file3.length:" + trainFile.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Workbook wb = null;
		WritableWorkbook wwb = null;
		try {
			wb = Workbook.getWorkbook(trainFile);
			wwb = Workbook.createWorkbook(trainFile, wb);// copy
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		WritableSheet ws = wwb.getSheet(0);
		WritableSheet sheet = wwb.getSheet(0);// 获取工作对象
		sheet.removeColumn(sheet.getColumns()-1);
		try {
			wwb.write();
			wwb.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		startAnalysis.saveAsCsv(trainFile, trainSetFile);//将训练集存为csv格式
		
		//第三步、神经网络的训练与测试
		Neuroph tts = new Neuroph();
		tts.train(file4Path);
		tts.testNeuralNetwork();
	}

}
