package picClassifyV2;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Before1h2h3hLocation implements ActionListener,MouseListener{
	
	public JFileChooser fileChooser;
	public JFrame frame;
	public JTextArea picDir;
	public JButton button;
	public Container content;
	public JPanel txt;
	public JLabel myLabel;

	public databean choose=new databean();
    public Connection conn = null;
    public ResultSet myNewRS=null;//查找原始照片信息
    public ResultSet myCheck=null;//在PM位置信息表中，查询第X个站点的PM_ID
    public ResultSet myResult=null;//查询相邻站点对应时刻的PM值
    public ResultSet myFindNewTime=null;//查找相邻站点最近时刻的PM值
    public ResultSet myWeatherID=null;//在Weather位置信息表中，查询第X个站点的Weather_ID
    public ResultSet myWeatherData=null;//查询相邻站点对应时刻的Weather值
	public ResultSet myFindWeatherTime=null;//查找相邻站点最近时刻的Weather值
    
	public int id;//store the id for each picture
	public String location=null;//The location variable stores the location for each picture.
	public String time=null;//time store the time for each picture.
	public String PM_ID=null;//保存拍摄照片站点对应的PM_ID
	
	
	ArrayList<ArrayList<Double>> myNewDistance;//保存邻接矩阵的二维数组
	ArrayList<Double> myList;//保存特定站点的到其他站点的距离值，初始时未排序。
	ArrayList<Integer> myOrder;//保存特定站点对应其他站点的站点名称
	ArrayList<String> myLocation= new ArrayList<String>();
	
	String[] myFormerPMID = new String[4];//保存拍摄站点+相邻最近3个站点的PMId
	String[] myLatterPMID=new String[4];//保存相邻最远3个站点的PMId，第一个元素为空
	
	public void mySorting(ArrayList<Double>a,ArrayList<Integer>b){//排序函数，使用插入排序
		int j=0,p=0;//counter
		double temp=0;//store temporary distance 
		int tempForIndex=0;//store index for each location
		for(p=1;p<a.size();p++){
			temp=a.get(p);
			tempForIndex=b.get(p);
			for(j=p;j>0&&a.get(j-1)>temp;j--){
				a.set(j, a.get(j-1));
				b.set(j, b.get(j-1));
			}
			a.set(j, temp);
			b.set(j,tempForIndex);
		}
	}
    
	public void go(){
		//GUI部分
    	fileChooser = new JFileChooser(".");
		frame=new JFrame("ChoosePic");
		picDir=new JTextArea();
		button=new JButton("选择文件夹");
		content = frame.getContentPane();
		txt=new JPanel();
		
		button.addMouseListener(this);
		content.add(button);
		
		picDir.setColumns(15);
		picDir.setRows(1);
		txt.add(picDir); 
		content.add(txt);
    	
		content.setLayout(new FlowLayout(FlowLayout.LEFT)); 
		frame.setSize(500,600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
    	//连接数据库
		choose.setDB("new2");//set database name
	    conn=choose.getConn();//connect to database
	    
	    myLocation.add("北京邮电大学");//0
 	    myLocation.add("车公庄");//1
 	    myLocation.add("奥林匹克森林公园");//2
 	    myLocation.add("MSRA");//3
 	    myLocation.add("北方工业大学");//4
 	    myLocation.add("中关村");//5
 	    myLocation.add("北京大学");//6
 	    myLocation.add("对外经贸大学");//7
 	    myLocation.add("农展馆");//8
 	    myLocation.add("朝阳公园");//9
 	    myLocation.add("天坛");//10
 	    myLocation.add("北京交通大学");//11
 	    myLocation.add("北京理工大学");//12
	}
    
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){
    	if(e.getSource()==button){
    		fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
    		fileChooser.setDialogTitle("打开文件夹");
    		int ret = fileChooser.showOpenDialog(null);
    		if (ret == JFileChooser.APPROVE_OPTION) {
    			
    			File pmdata=fileChooser.getSelectedFile();
				BufferedReader Reader;
    			try{
    				Reader = new BufferedReader(new FileReader(pmdata));
    				String temp=null;
			 		String[] tp=null;
			 	    Double myDouble=null;
			 	    
			 	    //向二维arrayList插入distance数据
			 	    myNewDistance=new ArrayList<ArrayList<Double>>();
			 		while((temp=Reader.readLine())!= null){//readLine函数每次输出指针都会移动
			 			ArrayList<Double> myDoubleStr=new ArrayList<Double>();
			 			tp=temp.split(",");//tp存储每行分割之后的每个String
			 			for(int a=0;a<tp.length;a++){
			 				myDouble=Double.parseDouble(tp[a]);
			 				myDoubleStr.add(myDouble);
			 			}
			 			myNewDistance.add(myDoubleStr);
			 		} 
					
			 		//输出邻接矩阵 
			 		System.out.println("邻接矩阵：");
			 		for(int i=0;i<myNewDistance.size();i++){
			 			for(int j=0;j<myNewDistance.get(i).size();j++){
			 				System.out.print(myNewDistance.get(i).get(j)+" ");
			 			}
			 			System.out.println();
			 		}
			 		System.out.println();
			 		
    				//进行查询操作
    		 	    String myNewSQL="select  * from mylocation where id>=51775 ;";
    		 	    myNewRS=choose.executeSQL(myNewSQL);//查找原始照片信息
    		    	
    		 	    while(myNewRS.next()){//for each picture information in the database
    		 	    	//查询照片的id,拍摄地点和拍摄时间
    		 	    	id=myNewRS.getInt("id");
    		 	    	System.out.println("ID:"+id);
    		 	    	location=myNewRS.getString("location");//location保存拍摄地点名称
    		 	    	//System.out.println("Location:"+location);
    		 	    	if(location.equalsIgnoreCase("北邮")){
    		 	    		location="北京邮电大学";
    		 	    	}
    		 	    	System.out.println("Location:"+location);
    		 	    	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		 	    	Date myDate=myFormat.parse(myNewRS.getString("time"));
    		 	    	SimpleDateFormat myNewFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
    		 	    	time= myNewFormat.format(myDate);//Record the time for each picture
    		 	    	System.out.println("Time:"+time);
    		 	    	
    		 	    	//查找拍摄站点对应的PMID
    		 	    	String myPicPMID="select * from location_pm where location='"+location+"';";
    		 	    	ResultSet myFindPicPMID=choose.executeSQL(myPicPMID);
    		 	    	if(myFindPicPMID.next()){
    		 	    		PM_ID=myFindPicPMID.getString("station_id");
    		 	    		System.out.println("拍摄站点PM_ID:"+PM_ID);
    		 	    		myFormerPMID[0]=PM_ID;//保存拍摄站点PM_ID到数组
    		 	    		
    		 	    		//将照片PM_ID插入表中
    		 	    		String myUpdatePM_ID="update mylocation set PM_ID='"+PM_ID+"' where id="+id+";";
    		 	    		choose.executeUpdateSQL(myUpdatePM_ID);
    		 	    	}
    		 	    	
    		 	    	
    		 	    	//对于照片拍摄地点进行排序
    		 	    	myList = new ArrayList<Double>();//保存到其他站点的距离
				 		myOrder = new ArrayList<Integer>();//保存其他站点的index
				 		for(int j=0;j<myNewDistance.get(myLocation.indexOf(location)).size();j++){
				 			myList.add(myNewDistance.get(myLocation.indexOf(location)).get(j));
				 			myOrder.add(j);
				 		}
				 		mySorting(myList,myOrder);//输入为到每个节点的距离和节点编号
				 		
				 		//测试：输出排序之后到各站点距离结果
				 		System.out.println("排序之后，No."+myLocation.indexOf(location)+" 号站点"+location+"到各站点距离如下：");
				 		for(int i=1;i<=12;i++){//只查看每个站点最近的3个相邻站点距离
				 			System.out.println(myLocation.get(myOrder.get(i))+"--"+myList.get(i)+"km");
				 		}
				 		System.out.println();
				 		
				 		//遍历拍摄地点最近的3个站点，查找对应的PM和Weather信息
				 		for(int j=1;j<=3;j++){
				 			String myPM_ID=null;
				 			String myWeather_ID=null;
				 			
				 			Date myNewDate=myNewFormat.parse(time);//变为整时刻值的时间由String转换为Date类型
	    		 	    	Date myNewWeatherDate=myNewFormat.parse(time);//保存Weather查询时，对应时刻没有数值时，向前查找
				 			
				 			//如果第X个站点为天坛，直接删除myOrder和myList数据
	    		 	    	if(myLocation.get(myOrder.get(j)).equals("天坛")){
	    		 	    		myOrder.remove(j);
	    		 	    		myList.remove(j);
	    		 	    	}
	    		 	    	
	    		 	    	
	    		 	    	
	    		 	    	//在PM位置信息表中，查询第X个站点的PM_ID
	    		 	    	/*
				 			String mySelection="select * from location_pm where location='"+myLocation.get(myOrder.get(j))+"';";
				 			myCheck=choose.executeSQL(mySelection);//在PM位置信息表中，查询第X个站点的PM_ID
				 			if(myCheck.next()){
				 				myPM_ID=myCheck.getString("station_id");//保存第X站点的PM的id
				 				System.out.println(myPM_ID);
				 			}
				 			*/
	    		 	    	//在PM位置信息表中，查询第X站点的PM_ID，并在之前的数组中排出已存在的站点
	    		 	    	while(true){
	    		 	    		//在PM位置信息表中，查询第X个站点的PM_ID
	    		 	    		String mySelection="select * from location_pm where location='"+myLocation.get(myOrder.get(j))+"';";
					 			myCheck=choose.executeSQL(mySelection);//在PM位置信息表中，查询第X个站点的PM_ID
					 			if(myCheck.next()){
					 				myPM_ID=myCheck.getString("station_id");//保存第X站点的PM的id
					 				
					 			}
					 			int p;
					 			for(p=j-1;p>=0;p--){
					 				if(myPM_ID.equals(myFormerPMID[p])){
					 					//删除第X站点的下标和到拍摄站点的距离
					 					myOrder.remove(j);
					 					myList.remove(j);
					 					break;
					 				}
					 			}
					 			if(p==-1){//如果不存在
					 				myFormerPMID[j]=myPM_ID;//保存当前第X站点的PM_ID到数组
					 				System.out.print("最近第"+j+"个站点的PM_ID为："+myPM_ID+"  ");
					 				System.out.println("距离为："+myList.get(j));
					 				break;
					 			}
	    		 	    	}
				 			
				 			//在Weather位置信息表中，查询第X个站点的Weather_ID
				 			String mySelectionWeather="select * from district where location='"+myLocation.get(myOrder.get(j))+"';";
				 			myWeatherID=choose.executeSQL(mySelectionWeather);//在Weather位置信息表中，查询第X个站点的Weather_ID
				 			if(myWeatherID.next()){
				 				myWeather_ID=myWeatherID.getString("id");
				 				//System.out.println("最近第"+j+"个站点的Weather_ID为："+myWeather_ID);
				 			}
				 			
				 			//将相应站点PM及Weather的ID插入到数据库中
				 			String updateLocationIDSQL="update mylocation set location_"+j+"L='"+myLocation.get(myOrder.get(j))+"',distance_"+j+"L='"+myList.get(j)+"',PM_ID_"+j+"L='"+myPM_ID+"',Weather_ID_"+j+"L='"+myWeather_ID+"' where id="+id+";";
				 			choose.executeUpdateSQL(updateLocationIDSQL);
				 			
				 			
				 			//查询相邻站点对应时刻的PM值
				 			String myCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+time+"';";
				 			myResult=choose.executeSQL(myCheckPMSQL);//查询相邻站点对应时刻的PM值
				 			if(myResult.next()){//如果找到对应的时间的PM值
				 				String myPM25=myResult.getString("pm25");
				 				String myPM10=myResult.getString("pm10");
				 				String myCO2=myResult.getString("co2");
				 				
				 				String UpdatePMSQL="update mylocation set pm25_"+j+"L='"+myPM25+"',pm10_"+j+"L='"+myPM10+"',co2_"+j+"L='"+myCO2+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			else{//如果没有查找到对应时间的PM数值，则往上一时刻查找，知道查找到为止
				 				
				 				String myNewTime=null;
				 				do{
				 					myNewDate.setHours(myNewDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+myNewTime+"';";
				 					myFindNewTime=choose.executeSQL(myNewCheckPMSQL);//查找相邻站点最近时刻的PM值
				 				}while(!myFindNewTime.next());
				 				
				 				String UpdatePMSQL="update mylocation set ActualPMTime_"+j+"h='"+myNewTime+"',pm25_"+j+"L='"+myFindNewTime.getString("pm25")+"',pm10_"+j+"L='"+myFindNewTime.getString("pm10")+"',co2_"+j+"L='"+myFindNewTime.getString("co2")+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			
				 			//如果Weather_ID为天坛对应的00107,则执行下一次循环
				 			if(myWeather_ID.equalsIgnoreCase("00107")){
				 				continue;
				 			}
				 			//查询相邻站点对应时刻的Weather值
				 			String myCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+time+"';";
				 			myWeatherData=choose.executeSQL(myCheckWeatherSQL);//查询相邻站点对应时刻的Weather值
				 			if(myWeatherData.next()){//如果对应的Weather有值，则直接更新数据库
				 				String myTemperature=myWeatherData.getString("Temperature");
				 				String myPressure=myWeatherData.getString("Pressure");
				 				String myHumidity=myWeatherData.getString("Humidity");
				 				String myWindSpeed=myWeatherData.getString("WindSpeed");
				 				String myWindDirection=myWeatherData.getString("WindDirection");
				 				String myWeather=myWeatherData.getString("Weather");
				 				
				 				String UpdateWeatherSQL="update mylocation set Temperature_"+j+"L='"+myTemperature+"',Pressure_"+j+"L='"+myPressure+"',Humidity_"+j+"L='"+myHumidity+"',WindSpeed_"+j+"L='"+myWindSpeed+"',WindDirection_"+j+"L='"+myWindDirection+"',Weather_"+j+"L='"+myWeather+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdateWeatherSQL);
				 			}
				 			else{//若对应的Weather无值，则寻找之前小时的数值，知道找到为止，并更新数据库
				 				String myNewTime=null;
				 				do{
				 					myNewWeatherDate.setHours(myNewWeatherDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewWeatherDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+myNewTime+"';";
				 					myFindWeatherTime=choose.executeSQL(myNewCheckWeatherSQL);//查找相邻站点最近时刻的Weather值
				 				}while(!myFindWeatherTime.next());
				 				
				 				String UpdateWeatherSQL="update mylocation set ActualWeatherTime_"+j+"h='"+myNewTime+"',Temperature_"+j+"L='"+myFindWeatherTime.getString("Temperature")+"',Pressure_"+j+"L='"+myFindWeatherTime.getString("Pressure")+"',Humidity_"+j+"L='"+myFindWeatherTime.getString("Humidity")+"',WindSpeed_"+j+"L='"+myFindWeatherTime.getString("WindSpeed")+"',WindDirection_"+j+"L='"+myFindWeatherTime.getString("WindDirection")+"',Weather_"+j+"L='"+myFindWeatherTime.getString("Weather")+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdateWeatherSQL);
				 			}
				 			
				 		}//End for loop
				 		
				 		//遍历拍摄地点最远的3个站点，查找对应的PM和Weather信息
				 		for(int b=1;b<=3;b++){
				 			String myPM_ID=null;
				 			String myWeather_ID=null;
				 			
				 			Date myNewDate=myNewFormat.parse(time);//变为整时刻值的时间由String转换为Date类型
	    		 	    	Date myNewWeatherDate=myNewFormat.parse(time);//保存Weather查询时，对应时刻没有数值时，向前查找
				 			
	    		 	    	//如果倒数第X个站点为天坛，直接删除myOrder和myList数据
	    		 	    	if(myLocation.get(myOrder.get(myOrder.size()-b)).equals("天坛")){
	    		 	    		myOrder.remove(myOrder.size()-b);
	    		 	    		myList.remove(myList.size()-b);
	    		 	    	}
				 			
	    		 	    	//在PM位置信息表中，查询倒数第X站点的PM_ID，并在之前的数组中排出已存在的站点
	    		 	    	while(true){
	    		 	    		//在PM位置信息表中，查询倒数第X个站点的PM_ID
	    		 	    		String mySelection="select * from location_pm where location='"+myLocation.get(myOrder.get(myOrder.size()-b))+"';";
					 			myCheck=choose.executeSQL(mySelection);//在PM位置信息表中，查询第X个站点的PM_ID
					 			if(myCheck.next()){
					 				myPM_ID=myCheck.getString("station_id");//保存第X站点的PM的id
					 				
					 			}
					 			
					 			int p;
					 			for(p=b-1;p>=0;p--){
					 				if(myPM_ID.equals(myLatterPMID[p])){
					 					//删除第X站点的下标和到拍摄站点的距离
					 					myOrder.remove(myOrder.size()-b);
					 					myList.remove(myList.size()-b);
					 					break;
					 				}
					 			}
					 			if(p==-1){//如果不存在
					 				int q;
					 				for(q=3;q>=0;q--){
						 				if(myPM_ID.equals(myFormerPMID[q])){
						 					//删除第X站点的下标和到拍摄站点的距离
						 					myOrder.remove(myOrder.size()-b);
						 					myList.remove(myList.size()-b);
						 					break;
						 				}
						 			}
					 				if(q==-1){//如果都不存在
					 					myLatterPMID[b]=myPM_ID;//保存当前倒数第X站点的PM_ID到数组
						 				System.out.print("最远第"+b+"个站点的PM_ID为："+myPM_ID+"  ");
						 				System.out.println("距离为："+myList.get(myList.size()-b));
						 				break;
					 				}//End if condition
					 			}//End if condition
	    		 	    	}//End while condition
				 			
	    		 	    	//在Weather位置信息表中，查询倒数第X个站点的Weather_ID
				 			String mySelectionWeather="select * from district where location='"+myLocation.get(myOrder.get(myOrder.size()-b))+"';";
				 			myWeatherID=choose.executeSQL(mySelectionWeather);//在Weather位置信息表中，查询第X个站点的Weather_ID
				 			if(myWeatherID.next()){
				 				myWeather_ID=myWeatherID.getString("id");
				 				//System.out.println("最远第"+b+"个站点的Weather_ID为："+myWeather_ID);
				 			}
	    		 	    	
				 			//将相应站点PM及Weather的ID插入到数据库中
				 			String updateLocationIDSQL="update mylocation set Last_location_"+b+"L='"+myLocation.get(myOrder.get(myOrder.size()-b))+"',Last_distance_"+b+"L='"+myList.get(myList.size()-b)+"',Last_PM_ID_"+b+"L='"+myPM_ID+"',Last_Weather_ID_"+b+"L='"+myWeather_ID+"' where id="+id+";";
				 			choose.executeUpdateSQL(updateLocationIDSQL);
				 			
				 			//查询相邻站点对应时刻的PM值
				 			String myCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+time+"';";
				 			myResult=choose.executeSQL(myCheckPMSQL);//查询相邻站点对应时刻的PM值
				 			if(myResult.next()){//如果找到对应的时间的PM值
				 				String myPM25=myResult.getString("pm25");
				 				String myPM10=myResult.getString("pm10");
				 				String myCO2=myResult.getString("co2");
				 				
				 				String UpdatePMSQL="update mylocation set Last_pm25_"+b+"L='"+myPM25+"',Last_pm10_"+b+"L='"+myPM10+"',Last_co2_"+b+"L='"+myCO2+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			else{//如果没有查找到对应时间的PM数值，则往上一时刻查找，知道查找到为止
				 				
				 				String myNewTime=null;
				 				do{
				 					myNewDate.setHours(myNewDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+myNewTime+"';";
				 					myFindNewTime=choose.executeSQL(myNewCheckPMSQL);//查找相邻站点最近时刻的PM值
				 				}while(!myFindNewTime.next());
				 				
				 				String UpdatePMSQL="update mylocation set Last_ActualPMTime_"+b+"h='"+myNewTime+"',Last_pm25_"+b+"L='"+myFindNewTime.getString("pm25")+"',Last_pm10_"+b+"L='"+myFindNewTime.getString("pm10")+"',Last_co2_"+b+"L='"+myFindNewTime.getString("co2")+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			
				 			
				 			//查询相邻站点对应时刻的Weather值
				 			String myCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+time+"';";
				 			myWeatherData=choose.executeSQL(myCheckWeatherSQL);//查询相邻站点对应时刻的Weather值
				 			if(myWeatherData.next()){//如果对应的Weather有值，则直接更新数据库
				 				String myTemperature=myWeatherData.getString("Temperature");
				 				String myPressure=myWeatherData.getString("Pressure");
				 				String myHumidity=myWeatherData.getString("Humidity");
				 				String myWindSpeed=myWeatherData.getString("WindSpeed");
				 				String myWindDirection=myWeatherData.getString("WindDirection");
				 				String myWeather=myWeatherData.getString("Weather");
				 				
				 				String UpdateWeatherSQL="update mylocation set Last_Temperature_"+b+"L='"+myTemperature+"',Last_Pressure_"+b+"L='"+myPressure+"',Last_Humidity_"+b+"L='"+myHumidity+"',Last_WindSpeed_"+b+"L='"+myWindSpeed+"',Last_WindDirection_"+b+"L='"+myWindDirection+"',Last_Weather_"+b+"L='"+myWeather+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdateWeatherSQL);
				 			}
				 			else{//若对应的Weather无值，则寻找之前小时的数值，知道找到为止，并更新数据库
				 				String myNewTime=null;
				 				do{
				 					myNewWeatherDate.setHours(myNewWeatherDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewWeatherDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+myNewTime+"';";
				 					myFindWeatherTime=choose.executeSQL(myNewCheckWeatherSQL);//查找相邻站点最近时刻的Weather值
				 				}while(!myFindWeatherTime.next());
				 				
				 				String UpdateWeatherSQL="update mylocation set Last_ActualWeatherTime_"+b+"h='"+myNewTime+"',Last_Temperature_"+b+"L='"+myFindWeatherTime.getString("Temperature")+"',Last_Pressure_"+b+"L='"+myFindWeatherTime.getString("Pressure")+"',Last_Humidity_"+b+"L='"+myFindWeatherTime.getString("Humidity")+"',Last_WindSpeed_"+b+"L='"+myFindWeatherTime.getString("WindSpeed")+"',Last_WindDirection_"+b+"L='"+myFindWeatherTime.getString("WindDirection")+"',Last_Weather_"+b+"L='"+myFindWeatherTime.getString("Weather")+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdateWeatherSQL);
				 			}
				 		}//End for loop
    		 	    	
    		 	    	System.out.println();
    		 	    	System.out.println();
    		 	    }//End while loop
    		    	
    		    }catch(Exception myException){
    		    	myException.printStackTrace();
    		    }
    			
    		}
    	}
    }
    public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void actionPerformed(ActionEvent e){}
	public static void main(String[] args) {
		Before1h2h3hLocation myInstance = new Before1h2h3hLocation();
		myInstance.go();

	}

}
