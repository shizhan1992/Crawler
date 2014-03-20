package crawl;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class CrawlTime {
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static Date INITIAL_TIME = null;
	public static Date START_TIME   = null;
	public static Date END_TIME     = null;
	
	public static String path = "D://crawl//crawl_log//";
	public static String name = "log.txt";
	
	public static void init() throws ParseException{
		INITIAL_TIME  = format.parse("2013-06-01 00:00:00");
		String lasttime = gettime(path,name);
		if(lasttime != null)
			START_TIME = format.parse(lasttime);
		else
			START_TIME = format.parse("2014-03-01 00:00:00");
		END_TIME   = new Date();
		writefile(path+name, format.format(END_TIME));
	}
	public void tomorrow(String code){
		START_TIME = END_TIME;		
		END_TIME = new Date();
		writefile(path+name, format.format(END_TIME));
	} 
	
	public static String gettime(String pathname, String name){
		File getstring = new File(pathname+name);
		Scanner sc = null;
		if(!getstring.exists()){
			try {
				new File(pathname).mkdirs();
				getstring.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			sc = new Scanner(new FileReader(getstring));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
		String line=null;
		while((sc.hasNextLine()&&(line=sc.nextLine())!=null)){
		    if(!sc.hasNextLine())
		    {
		    	System.out.println(line);
		    	return line;
		    }
		    
		}
		sc.close();
		return null;
	}
	public static void writefile(String pathname, String endtime){
		File log = new File(pathname);
		try {
			FileWriter fileWriter=new FileWriter(log,true);
			fileWriter.write(endtime+"\r\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws ParseException{
		CrawlTime.init();
		System.out.println(CrawlTime.START_TIME);
		System.out.println(CrawlTime.INITIAL_TIME);
		System.out.println(CrawlTime.END_TIME);
	}
}
