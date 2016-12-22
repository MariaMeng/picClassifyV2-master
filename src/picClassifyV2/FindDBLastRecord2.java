package picClassifyV2;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FindDBLastRecord2 {
	
	/*
	 * 与FindDBLastRecord的区别：添加一种情况：如果Dylos数据之前清空过，则TXT数据从头开始读
	 * 
	 */

	databean localBean;//创建默认的Local数据库
	databean serverBean;//创建Server端DB连接
	Connection localConn=null;
	Connection serverConn=null;
	
	public FindDBLastRecord2(){
		//创建Local数据库连接对象
		localBean=new databean();
		localBean.setDB("new2");//set database name
		localConn=localBean.getConn();//connect to database
		
		//创建Server数据库连接的对象
		serverBean=new databean("222.128.13.159","64000","pm25","pm25","123456");
		serverConn=serverBean.getConn();
	}
	
	//该方法返回当前DB表中最近的时间，参数为指定的表名称
	public String FindLastTime(String table){
		//连接数据库
		String lastTime=new String();//保存查询结果得到的时间
		ResultSet myNewRS=null;//查询结果集
		String myNewSQL="";//查询语句
		//Local表bupt_pm25
		if(table.equals("bupt_pm25")){
			myNewSQL="select timeDylos from "+table+" order by timeDylos desc limit 0,1;";
			try{
				myNewRS=localBean.executeSQL(myNewSQL);
				if(myNewRS.next()){
					lastTime=myNewRS.getString(1);
					System.out.println("The last time for table "+table+" is:"+lastTime);
				}//End if
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Failure!!");
			}//End catch
		}
		//Local表bupt_hour_pm
		else if(table.equals("bupt_hour_pm")){
			myNewSQL="select time from "+table+" order by time desc limit 0,1;";
			try{
				myNewRS=localBean.executeSQL(myNewSQL);	
				if(myNewRS.next()){
					Timestamp myStamp=myNewRS.getTimestamp(1);
					System.out.println("The last time for table "+table+" is:"+myStamp.toString());
					lastTime=myStamp.toString();
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Failure!!");
			}//End catch
		}
		//Server表Dylos_pm25
		else if(table.equals("Dylos_pm25")){
			myNewSQL="select Time from "+table+" order by Time desc limit 0,1;";
			try{
				myNewRS=serverBean.executeSQL(myNewSQL);
				if(myNewRS.next()){
					Timestamp myStamp=myNewRS.getTimestamp(1);
					System.out.println("The last time for table "+table+" is:"+myStamp.toString());
					lastTime=myStamp.toString();
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Failure!!");
			}//End catch
		}
     	
     	//返回查询到的最近时间
		return lastTime;
	}
	
	//查找txt中的时间，并更新表中的数据,返回更新记录的条数
	public int UpdateTable(File myfile,String time){
		//System.out.println(myfile.toString());
		int acc=0;//用来统计txt中行数
		BufferedReader myReader;
		
		try {
			myReader = new BufferedReader(new FileReader(myfile));
			String myread;
			
			boolean flag=false;//设置Flag,如果在TXT中找到上次最近的更新时间，则为true
			//如果TXT中找到了对应的时间，则跳出循环
			while((myread=myReader.readLine())!=null){
				if(myread.indexOf(time)!=-1){
					flag=true;
					break;
				}
			}
			System.out.println(flag);
			if(flag==false){//如果没有找到上次更新的最后时间，则从第9行开始读文件
				int round=0;//负责控制次数
				myReader=new BufferedReader(new FileReader(myfile));
				while(round<8){//如果读完前8行之后，就开始正式读数据了
					myReader.readLine();
					round++;
				}
			}
			//从下一个位置到TXT尾，进行插入操作
			String[] tp=null;
			String temp;
			while((temp=myReader.readLine())!=null){
				tp=temp.split(",");
				//for test
				System.out.println();
				for(int i=0;i<tp.length;i++){
					System.out.println("tp["+i+"]: "+tp[i]);
				}
				//插入数据库bupt_pm25表中原始数据值
				String InsertSQL="insert into bupt_pm25 values('"+tp[0]+"',"+tp[1]+","+tp[2]+",null,null,null);";
				int r=localBean.executeUpdateSQL(InsertSQL);//返回DB中被更新的记录的数目
				if(r!=0){//如果更新结果非0，则表示更新成功
					System.out.println("UPDATE FINISH!");
					acc++;
				}else{
					System.out.println("NO UPDATE!");
				}
			}//End while loop
			
			//表bupt_pm25中其余列补全
			String UpdateSQL="update bupt_pm25 set DateAndHour=concat('20',substring(timeDylos,7,2),'-',left(timeDylos,2),'-',substring(timeDylos,4,2),' ',substring(timeDylos,10,2),':00:00'),time=concat('20',substring(timeDylos,7,2),'-',left(timeDylos,2),'-',substring(timeDylos,4,2),' ',substring(timeDylos,10,2),':',right(timeDylos,2),':00'),Date=substring(DateAndHour,1,10);";
			int a=localBean.executeUpdateSQL(UpdateSQL);
			if(a!=0){//如果更新结果非0，则表示更新成功
				System.out.println("Update Finish!");
			}else{
				System.out.println("NO Update！");
			}
			/*------------------以下为更新bupt_hour_pm表---------------*/
			String mytime=FindLastTime("bupt_hour_pm");
			System.out.println(mytime);
			String myUpdate="insert into bupt_hour_pm(time,Small,pm25,Large) select DateAndHour,ROUND(AVG(Small))as AVG_Small,ROUND(AVG(Small)/100)AS AVG_PM25,ROUND(AVG(Large))as AVG_Large from bupt_pm25 where DateAndHour>'"+mytime+"' group by DateAndHour;";
			int b=localBean.executeUpdateSQL(myUpdate);
			if(b!=0){
				System.out.println("finish!");
			}else{
				System.out.println("no done!");
			}
			/*---------------以下为更新Server端中Dylos_pm25表-----------------*/
			String myServerTime=FindLastTime("Dylos_pm25");//返回当前最近的时间
			System.out.println(myServerTime);
			//查询满足时间条件的本地bupt_hour_pm表中的结果集
			String mySelect="SELECT * FROM bupt_hour_pm where time>'"+myServerTime+"' order by time;";
			ResultSet myRS=localBean.executeSQL(mySelect);
			//按照每条结果，更新服务器表
			while(myRS.next()){
				String serverUpdate="insert into Dylos_pm25 values('"+myRS.getString(1)+"',"+myRS.getString(2)+","+myRS.getString(3)+","+myRS.getString(4)+");";
				int c=serverBean.executeUpdateSQL(serverUpdate);
				if(c!=0){
					System.out.println("finish!");
				}else{
					System.out.println("not done!");
				}
			}//End while
			
			/*---------------以下为插入Server端中pm25_Station_Dylos表新数据------------------------*/
			/*
			 * 目的：将站点PM值，站点天气值，实际测量Dylos值汇总
			 */
			String updateTable =  
					"INSERT INTO pm25_Station_Dylos(time,location_pm,location_weather,pm25_Station,pm25_Dylos,Temperature,Humidity,WindSpeed,Pressure)   "+
					"                                                                                                                                    "+
					"SELECT Dylos.time,location_pm,location_weather,pm25_Station,Dylos.pm25 AS pm25_Dylos,Temperature,Humidity,WindSpeed,Pressure        "+
					"FROM                                                                                                                                "+
					"(                                                                                                                                   "+
					"                                                                                                                                    "+
					"    select A.time,location_pm,location_weather,pm25 as pm25_Station,Temperature,Humidity,WindSpeed,Pressure                         "+
					"    from                                                                                                                            "+
					"    (                                                                                                                               "+
					"                                                                                                                                    "+
					"         select concat(substring(pm25.create_time,1,14),'00:00')as time,station_name as location_pm,Round(AVG(pm25),2)AS pm25       "+
					"         from t_pm25 as pm25                                                                                                        "+
					"         where create_time>='2016-09-07 00:00:00' and pm25!='_' and station_name='西城官园'                                         "+
					"         group by time                                                                                                              "+
					"    )AS A                                                                                                                           "+
					"    JOIN                                                                                                                            "+
					"    (                                                                                                                               "+
					"                                                                                                                                    "+
					"         select concat(substring(create_date,1,14),'00:00')as time,city_name as location_weather,                                   "+
					"                ROUND(AVG(temperature),2)AS Temperature,                                                                            "+
					"                ROUND(AVG(humidity),2)AS Humidity,                                                                                  "+
					"                ROUND(AVG(wind_speed),2)AS WindSpeed,                                                                               "+
					"                ROUND(AVG(pressure),2)AS Pressure                                                                                   "+
					"         from t_weather_data                                                                                                        "+
					"         where create_date>'2016-09-07 00:00:00' and city_ID='001'                                                                  "+
					"         GROUP BY time                                                                                                              "+
					"    )AS B ON A.time=B.time                                                                                                          "+
					")AS C JOIN Dylos_pm25 as Dylos ON C.time=Dylos.Time                                                                                 "+
					"Where Dylos.time>                                                                                                                   "+
					"      (select pm25_Station_Dylos.time                                                                                               "+
					"       from pm25_Station_Dylos                                                                                                      "+
					"       order by pm25_Station_Dylos.time desc                                                                                        "+
					"       limit 0,1);                                                                                                                   ";
			int c=serverBean.executeUpdateSQL(updateTable);
			if(c!=0){
				System.out.println("update Table pm_Station_Dylos finish!");
			}else{
				System.out.println("not done!");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Failure!!");
		}//End catch
		
		//返回更新个数
		return acc;
	}
	
	public void ChooseDylosFile(final String myTime){
		
		
		
		String myfilepath="C:/Users/lenovo/Documents/Dylos Logs";
		final JFileChooser fileChooser = new JFileChooser(myfilepath);
		JFrame frame=new JFrame("ChoosetxtFile");
		final JTextArea picDir=new JTextArea();
		
		JButton button=new JButton("选择文件夹");
		final Container content = frame.getContentPane(); 
		MouseListener myListener=new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				int updateNum=0;
				if (e.getClickCount() == 1) {
					fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					 fileChooser.setDialogTitle("打开Dylos数据文件");//弹出的对话框标题
					 int ret = fileChooser.showOpenDialog(null);
					 if (ret == JFileChooser.APPROVE_OPTION) {
						 File myDylos=fileChooser.getSelectedFile();
						 //调用查找文件Key位置的函数
						 updateNum=UpdateTable(myDylos,myTime);
					 }
					 
					//显示更新记录的个数	
			    	TextField consoleInfo=new TextField();
			        consoleInfo.setText("bupt_pm25表更新完毕！共更新"+updateNum+"条记录。");
			        content.add(consoleInfo);
				}//End if for single click
			}
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub	
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub	
			}
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub	
			}
		};
		
		button.addMouseListener(myListener);
		JPanel txt=new JPanel();
		picDir.setColumns(15);
		picDir.setRows(1);
	    txt.add(picDir); 	
		   
	    content.setLayout(new FlowLayout(FlowLayout.LEFT));  
		content.add(txt);
		content.add(button);

		frame.setSize(500,600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FindDBLastRecord2 myRecord=new FindDBLastRecord2();
		String myTime=myRecord.FindLastTime("bupt_pm25");
		myRecord.ChooseDylosFile(myTime);
		
		
	}

}
