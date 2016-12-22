package picClassifyV2;

import picClassifyV2.databean;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;


public class Before1h2h3hData {
	
	public databean choose=new databean();
    public Connection conn = null;
    public ResultSet myNewRS=null;//查询照片原始信息
    public ResultSet myCheck=null;
    public ResultSet myResult=null;
    public ResultSet myFindPMID=null;
    public ResultSet myFindWeatherID=null;
	
    public String time=null,PM_ID=null,Weather_ID=null,pm25=null,pm10=null,co2=null,Temperature=null;
	public int id;
	public String location=null;//获取照片的拍摄地点
	
    public String[][] myRecord= new String[11][11];//保存(当前1小时+前10小时=11小时)的PM和Weather数值
    
    public void go(){
		//连接数据库
		choose.setDB("new2");//set database name
	    conn=choose.getConn();//connect to database
		try{
	    	 //进行查询操作
	 	     String myNewSQL="select  * from statistic where id>=40000;";
	 	     myNewRS=choose.executeSQL(myNewSQL);//查询照片原始信息
	 	     
	 	     while(myNewRS.next()){
	 	    	 id=myNewRS.getInt("id");//获取照片ID
	 	    	 System.out.println(id);
	 	    	 SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	    	 Date myDate=myFormat.parse(myNewRS.getString("time"));
	 	    	 SimpleDateFormat myPreviousFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
	 	    	 time= myPreviousFormat.format(myDate);//获取照片拍摄整点时间
	 	    	 
	 	    	 //Store all related data
	 	    	 myRecord[0][0]=time;//保存拍摄整点PM时间
	 	    	 myRecord[0][1]=myNewRS.getString("pm25");//保存pm25值
	 	    	 myRecord[0][2]=myNewRS.getString("pm10");//保存pm10值
	 	    	 myRecord[0][3]=myNewRS.getString("co2");//保存co2值
	 	    	 myRecord[0][4]=time;//保存拍摄整点Weather时间
	 	    	 myRecord[0][5]=myNewRS.getString("Temperature");//保存Temperature值
	 	    	 myRecord[0][6]=myNewRS.getString("Pressure");//保存Pressure值
	 	    	 myRecord[0][7]=myNewRS.getString("Humidity");//保存Humidity值
	 	    	 myRecord[0][8]=myNewRS.getString("WindSpeed");//保存WindSpeed值
	 	    	 myRecord[0][9]=myNewRS.getString("WindDirection");//保存WindDirection值
	 	    	 myRecord[0][10]=myNewRS.getString("Weather");//保存Weather值
	 	    	 
	 	    	 /*
	 	    	 for(int i=0;i<11;i++){
	 	    		 System.out.println(myRecord[0][i]);
	 	    	 }
	 	    	 System.out.println();
	 	    	 System.out.println();
	 	    	 */
	 	    	 
	 	    	 location=myNewRS.getString("location");//获取照片拍摄站点
	 	    	 //在PM位置信息表中，查询第X个站点的PM_ID
	 	    	 String myFindPMsql="select * from location_pm where location='"+location+"';";
	 	    	 myFindPMID=choose.executeSQL(myFindPMsql);//在PM位置信息表中，查找拍摄照片的站点PMID
	 	    	 if(myFindPMID.next()){
	 	    		PM_ID=myFindPMID.getString("station_id");//保存PMID
	 	    		System.out.println("拍摄站点PMID： "+PM_ID);
	 	    	 }
	 	    	 //在Weather位置信息表中，查询第X个站点的Weather_ID
	 	    	 String myFindWeathersql="select * from district where location='"+location+"';";
	 	    	 myFindWeatherID=choose.executeSQL(myFindWeathersql);//在Weather位置信息表中，查找拍摄站点WeatherID
	 	    	 if(myFindWeatherID.next()){
	 	    		 Weather_ID=myFindWeatherID.getString("id");//保存WeatherID
	 	    		 System.out.println("拍摄站点WeatherID: "+Weather_ID);
	 	    	 }
	 	    	 //将相应站点PM及Weather的ID插入到数据库中
	 	    	 String updateLocationIDSQL="update statistic set PM_ID='"+PM_ID+"',WEATHER_ID='"+Weather_ID+"' where id="+id+";";
	 	    	 choose.executeUpdateSQL(updateLocationIDSQL);
	 	    	 
	 	    	 //获取拍摄站点对应PM_ID和Weather_ID
	 	    	 /*
	 	    	 PM_ID=myNewRS.getString("PM_ID");
	 	    	 System.out.println("PM_ID为："+PM_ID);
	 	    	 Weather_ID=myNewRS.getString("WEATHER_ID");
	 	    	 System.out.println("Weahter_ID为："+Weather_ID);
	 	    	 */
	 	    	 
	 	    	 String previousTime=null;//保存拍摄时间之前X小时的整点时间
	 	    	 for(int j=1;j<=10;j++){//遍历前10小时时间
	 	    		myDate.setHours(myDate.getHours()-1);
	 	    		previousTime=myPreviousFormat.format(myDate);//前X小时的整点时间
	 	    		System.out.println("照片拍摄前"+j+"h时间："+previousTime);
	 	    		
	 	    		//插入前X小时整点时间到表statistic中
	 	    		String updateTime="update statistic set PMTime_"+j+"h='"+previousTime+"',WeatherTime_"+j+"h='"+previousTime+"' where id="+id+";";
	 	    		choose.executeUpdateSQL(updateTime);
	 	    		
	 	    		//查询PM表中数值
	 	    		String mySQL="select  * from all_pm25value where station_id='"+PM_ID+"' and time='"+previousTime+"';";
	 	    		myCheck=choose.executeSQL(mySQL);
	 	    		if(myCheck.next()){
	 	    			System.out.println(myCheck.getString("pm25"));
	 	    			System.out.println(myCheck.getString("pm10"));
	 	    			System.out.println(myCheck.getString("co2"));
	 	    			
	 	    			String myPM25=myCheck.getString("pm25");
	 	    			String myPM10=myCheck.getString("pm10");
	 	    			String myco2=myCheck.getString("co2");
	 	    			
	 	    			String myUpdateSQL="update statistic set pm25_"+j+"h='"+myPM25+"',pm10_"+j+"h='"+myPM10+"',co2_"+j+"h='"+myco2+"' where id="+id;
	 	    			choose.executeUpdateSQL(myUpdateSQL);
	 	    			//store all related data
	 	    			myRecord[j][0]=previousTime;
	 		 	    	myRecord[j][1]=myPM25;
	 		 	    	myRecord[j][2]=myPM10;
	 		 	    	myRecord[j][3]=myco2;
	 	    		}
	 	    		else{
	 	    			//如果没有查到结果，则返回后一小时的查询结果
	 	    			String myUpdateSQL="update statistic set ActualPMTime_"+j+"h='"+myRecord[j-1][0]+"',pm25_"+j+"h='"+myRecord[j-1][1]+"',pm10_"+j+"h='"+myRecord[j-1][2]+"',co2_"+j+"h='"+myRecord[j-1][3]+"' where id="+id;
	 	    			choose.executeUpdateSQL(myUpdateSQL);
	 	    			myRecord[j][0]=myRecord[j-1][0];
	 	    			myRecord[j][1]=myRecord[j-1][1];
	 	    			myRecord[j][2]=myRecord[j-1][2];
	 	    			myRecord[j][3]=myRecord[j-1][3];
	 	    		}
	 	    		
	 	    		//查询Weather表中数值
	 	    		String myWeatherSQL="select  * from weather where id='"+Weather_ID+"' and time='"+previousTime+"';";
	 	    		myResult=choose.executeSQL(myWeatherSQL);
	 	    		if(myResult.next()){
	 	    			System.out.println(myResult.getString("Temperature"));
	 	    			System.out.println(myResult.getString("Pressure"));
	 	    			System.out.println(myResult.getString("Humidity"));
	 	    			System.out.println(myResult.getString("WindSpeed"));
	 	    			System.out.println(myResult.getString("WindDirection"));
	 	    			System.out.println(myResult.getString("Weather"));
	 	    			
	 	    			String myTemperature=myResult.getString("Temperature");
	 	    			String myPressure=myResult.getString("Pressure");
	 	    			String myHumidity=myResult.getString("Humidity");
	 	    			String myWindSpeed=myResult.getString("WindSpeed");
	 	    			String myWindDirection=myResult.getString("WindDirection");
	 	    			String myWeather=myResult.getString("Weather");
	 	    			
	 	    			String myWeatherUpdateSQL="update statistic set Temperature_"+j+"h='"+myTemperature+"',Pressure_"+j+"h='"+myPressure+"',Humidity_"+j+"h='"+myHumidity+"',WindSpeed_"+j+"h='"+myWindSpeed+"',WindDirection_"+j+"h='"+myWindDirection+"',Weather_"+j+"h='"+myWeather+"' where id="+id;
	 	    			choose.executeUpdateSQL(myWeatherUpdateSQL);
	 	    			//store all related value
	 	    			myRecord[j][4]=previousTime;
	 	    			myRecord[j][5]=myTemperature;
	 	    			myRecord[j][6]=myPressure;
	 	    			myRecord[j][7]=myHumidity;
	 	    			myRecord[j][8]=myWindSpeed;
	 	    			myRecord[j][9]=myWindDirection;
	 	    			myRecord[j][10]=myWeather;          
	 	    		}
	 	    		else{
	 	    			//如果没有查到结果，则返回后一小时的查询结果
	 	    			String myWeatherUpdateSQL="update statistic set ActualWeatherTime_"+j+"h='"+myRecord[j-1][4]+"',Temperature_"+j+"h='"+myRecord[j-1][5]+"',Pressure_"+j+"h='"+myRecord[j-1][6]+"',Humidity_"+j+"h='"+myRecord[j-1][7]+"',WindSpeed_"+j+"h='"+myRecord[j-1][8]+"',WindDirection_"+j+"h='"+myRecord[j-1][9]+"',Weather_"+j+"h='"+myRecord[j-1][10]+"' where id="+id;
	 	    			choose.executeUpdateSQL(myWeatherUpdateSQL);
	 	    			myRecord[j][4]=myRecord[j-1][4];
	 	    			myRecord[j][5]=myRecord[j-1][5];
	 	    			myRecord[j][6]=myRecord[j-1][6];
	 	    			myRecord[j][7]=myRecord[j-1][7];
	 	    			myRecord[j][8]=myRecord[j-1][8];
	 	    			myRecord[j][9]=myRecord[j-1][9];
	 	    			myRecord[j][10]=myRecord[j-1][10];
	 	    		}
	 	    		
	 	    	 }//End FOR LOOP
	 	    	 System.out.println();
	 	    	 System.out.println();
	 	    	 
	 	     }//End while condition,each loop is for one picture
	     }catch(Exception myException){
	    	myException.printStackTrace();
	     }
	}
	
	public static void main(String[] args) {
		 Before1h2h3hData myInstance= new Before1h2h3hData();
		 myInstance.go();
	}

}
