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
    public ResultSet myNewRS=null;//��ѯ��Ƭԭʼ��Ϣ
    public ResultSet myCheck=null;
    public ResultSet myResult=null;
    public ResultSet myFindPMID=null;
    public ResultSet myFindWeatherID=null;
	
    public String time=null,PM_ID=null,Weather_ID=null,pm25=null,pm10=null,co2=null,Temperature=null;
	public int id;
	public String location=null;//��ȡ��Ƭ������ص�
	
    public String[][] myRecord= new String[11][11];//����(��ǰ1Сʱ+ǰ10Сʱ=11Сʱ)��PM��Weather��ֵ
    
    public void go(){
		//�������ݿ�
		choose.setDB("new2");//set database name
	    conn=choose.getConn();//connect to database
		try{
	    	 //���в�ѯ����
	 	     String myNewSQL="select  * from statistic where id>=40000;";
	 	     myNewRS=choose.executeSQL(myNewSQL);//��ѯ��Ƭԭʼ��Ϣ
	 	     
	 	     while(myNewRS.next()){
	 	    	 id=myNewRS.getInt("id");//��ȡ��ƬID
	 	    	 System.out.println(id);
	 	    	 SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	    	 Date myDate=myFormat.parse(myNewRS.getString("time"));
	 	    	 SimpleDateFormat myPreviousFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
	 	    	 time= myPreviousFormat.format(myDate);//��ȡ��Ƭ��������ʱ��
	 	    	 
	 	    	 //Store all related data
	 	    	 myRecord[0][0]=time;//������������PMʱ��
	 	    	 myRecord[0][1]=myNewRS.getString("pm25");//����pm25ֵ
	 	    	 myRecord[0][2]=myNewRS.getString("pm10");//����pm10ֵ
	 	    	 myRecord[0][3]=myNewRS.getString("co2");//����co2ֵ
	 	    	 myRecord[0][4]=time;//������������Weatherʱ��
	 	    	 myRecord[0][5]=myNewRS.getString("Temperature");//����Temperatureֵ
	 	    	 myRecord[0][6]=myNewRS.getString("Pressure");//����Pressureֵ
	 	    	 myRecord[0][7]=myNewRS.getString("Humidity");//����Humidityֵ
	 	    	 myRecord[0][8]=myNewRS.getString("WindSpeed");//����WindSpeedֵ
	 	    	 myRecord[0][9]=myNewRS.getString("WindDirection");//����WindDirectionֵ
	 	    	 myRecord[0][10]=myNewRS.getString("Weather");//����Weatherֵ
	 	    	 
	 	    	 /*
	 	    	 for(int i=0;i<11;i++){
	 	    		 System.out.println(myRecord[0][i]);
	 	    	 }
	 	    	 System.out.println();
	 	    	 System.out.println();
	 	    	 */
	 	    	 
	 	    	 location=myNewRS.getString("location");//��ȡ��Ƭ����վ��
	 	    	 //��PMλ����Ϣ���У���ѯ��X��վ���PM_ID
	 	    	 String myFindPMsql="select * from location_pm where location='"+location+"';";
	 	    	 myFindPMID=choose.executeSQL(myFindPMsql);//��PMλ����Ϣ���У�����������Ƭ��վ��PMID
	 	    	 if(myFindPMID.next()){
	 	    		PM_ID=myFindPMID.getString("station_id");//����PMID
	 	    		System.out.println("����վ��PMID�� "+PM_ID);
	 	    	 }
	 	    	 //��Weatherλ����Ϣ���У���ѯ��X��վ���Weather_ID
	 	    	 String myFindWeathersql="select * from district where location='"+location+"';";
	 	    	 myFindWeatherID=choose.executeSQL(myFindWeathersql);//��Weatherλ����Ϣ���У���������վ��WeatherID
	 	    	 if(myFindWeatherID.next()){
	 	    		 Weather_ID=myFindWeatherID.getString("id");//����WeatherID
	 	    		 System.out.println("����վ��WeatherID: "+Weather_ID);
	 	    	 }
	 	    	 //����Ӧվ��PM��Weather��ID���뵽���ݿ���
	 	    	 String updateLocationIDSQL="update statistic set PM_ID='"+PM_ID+"',WEATHER_ID='"+Weather_ID+"' where id="+id+";";
	 	    	 choose.executeUpdateSQL(updateLocationIDSQL);
	 	    	 
	 	    	 //��ȡ����վ���ӦPM_ID��Weather_ID
	 	    	 /*
	 	    	 PM_ID=myNewRS.getString("PM_ID");
	 	    	 System.out.println("PM_IDΪ��"+PM_ID);
	 	    	 Weather_ID=myNewRS.getString("WEATHER_ID");
	 	    	 System.out.println("Weahter_IDΪ��"+Weather_ID);
	 	    	 */
	 	    	 
	 	    	 String previousTime=null;//��������ʱ��֮ǰXСʱ������ʱ��
	 	    	 for(int j=1;j<=10;j++){//����ǰ10Сʱʱ��
	 	    		myDate.setHours(myDate.getHours()-1);
	 	    		previousTime=myPreviousFormat.format(myDate);//ǰXСʱ������ʱ��
	 	    		System.out.println("��Ƭ����ǰ"+j+"hʱ�䣺"+previousTime);
	 	    		
	 	    		//����ǰXСʱ����ʱ�䵽��statistic��
	 	    		String updateTime="update statistic set PMTime_"+j+"h='"+previousTime+"',WeatherTime_"+j+"h='"+previousTime+"' where id="+id+";";
	 	    		choose.executeUpdateSQL(updateTime);
	 	    		
	 	    		//��ѯPM������ֵ
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
	 	    			//���û�в鵽������򷵻غ�һСʱ�Ĳ�ѯ���
	 	    			String myUpdateSQL="update statistic set ActualPMTime_"+j+"h='"+myRecord[j-1][0]+"',pm25_"+j+"h='"+myRecord[j-1][1]+"',pm10_"+j+"h='"+myRecord[j-1][2]+"',co2_"+j+"h='"+myRecord[j-1][3]+"' where id="+id;
	 	    			choose.executeUpdateSQL(myUpdateSQL);
	 	    			myRecord[j][0]=myRecord[j-1][0];
	 	    			myRecord[j][1]=myRecord[j-1][1];
	 	    			myRecord[j][2]=myRecord[j-1][2];
	 	    			myRecord[j][3]=myRecord[j-1][3];
	 	    		}
	 	    		
	 	    		//��ѯWeather������ֵ
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
	 	    			//���û�в鵽������򷵻غ�һСʱ�Ĳ�ѯ���
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
