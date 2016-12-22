package picClassifyV2;

import java.sql.Connection;
import java.sql.ResultSet;

public class InsertColumn {

	public databean choose=new databean();
    public Connection conn = null;
    public ResultSet myNewRS=null;
    public void go(){
    	//连接数据库
    	choose.setDB("new2");//set database name
	    conn=choose.getConn();//connect to database
	    try{
	    	/*
	    	for(int i=7;i<=10;i++){	
	    		String myString="alter   table   statistic   add   WeatherTime_"+i+"h   datetime;";
	    		choose.executeUpdateSQL(myString);
	    		String myString2="alter   table   statistic   add   ActualWeatherTime_"+i+"h   datetime;";
	    		choose.executeUpdateSQL(myString2);
	    		String myString3="alter   table   statistic   add   Temperature_"+i+"h   varchar(255);";
	    		choose.executeUpdateSQL(myString3);
	    		String myString4="alter   table   statistic   add   Pressure_"+i+"h   varchar(255);";
	    		choose.executeUpdateSQL(myString4);
	    		String myString5="alter   table   statistic   add   Humidity_"+i+"h   varchar(255);";
	    		choose.executeUpdateSQL(myString5);
	    		String myString6="alter   table   statistic   add   WindSpeed_"+i+"h   varchar(255);";
	    		choose.executeUpdateSQL(myString6);
	    		String myString7="alter   table   statistic   add   WindDirection_"+i+"h   varchar(255);";
	    		choose.executeUpdateSQL(myString7);
	    		String myString8="alter   table   statistic   add   Weather_"+i+"h   varchar(255);";
	    		choose.executeUpdateSQL(myString8);
	    	}
	    	*/
	    	
	    	for(int i=1;i<=3;i++){
	    		String myLast1="alter   table   mylocation   add   Last_location_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast1);
	    		String myLast2="alter   table   mylocation   add   Last_distance_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast2);
	    		String myLast3="alter   table   mylocation   add   Last_PM_ID_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast3);
	    		String myLast4="alter   table   mylocation   add   Last_ActualPMTime_"+i+"h   datetime;";
	    		choose.executeUpdateSQL(myLast4);
	    		String myLast5="alter   table   mylocation   add   Last_pm25_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast5);
	    		String myLast6="alter   table   mylocation   add   Last_pm10_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast6);
	    		String myLast7="alter   table   mylocation   add   Last_co2_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast7);
	    		String myLast8="alter   table   mylocation   add   Last_Weather_ID_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast8);
	    		String myLast9="alter   table   mylocation   add   Last_ActualWeatherTime_"+i+"h   datetime;";
	    		choose.executeUpdateSQL(myLast9);
	    		String myLast10="alter   table   mylocation   add   Last_Temperature_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast10);
	    		String myLast11="alter   table   mylocation   add   Last_Pressure_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast11);
	    		String myLast12="alter   table   mylocation   add   Last_Humidity_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast12);
	    		String myLast13="alter   table   mylocation   add   Last_WindSpeed_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast13);
	    		String myLast14="alter   table   mylocation   add   Last_WindDirection_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast14);
	    		String myLast15="alter   table   mylocation   add   Last_Weather_"+i+"L   varchar(255);";
	    		choose.executeUpdateSQL(myLast15);
	    	}
	    }catch(Exception myException){
	    	myException.printStackTrace();
	    }
	    System.out.println("Finish!");
    }
	public static void main(String[] args) {
		InsertColumn myInsertColumn=new InsertColumn();
		myInsertColumn.go();
	}

}
