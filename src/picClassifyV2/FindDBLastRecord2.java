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
	 * ��FindDBLastRecord���������һ����������Dylos����֮ǰ��չ�����TXT���ݴ�ͷ��ʼ��
	 * 
	 */

	databean localBean;//����Ĭ�ϵ�Local���ݿ�
	databean serverBean;//����Server��DB����
	Connection localConn=null;
	Connection serverConn=null;
	
	public FindDBLastRecord2(){
		//����Local���ݿ����Ӷ���
		localBean=new databean();
		localBean.setDB("new2");//set database name
		localConn=localBean.getConn();//connect to database
		
		//����Server���ݿ����ӵĶ���
		serverBean=new databean("222.128.13.159","64000","pm25","pm25","123456");
		serverConn=serverBean.getConn();
	}
	
	//�÷������ص�ǰDB���������ʱ�䣬����Ϊָ���ı�����
	public String FindLastTime(String table){
		//�������ݿ�
		String lastTime=new String();//�����ѯ����õ���ʱ��
		ResultSet myNewRS=null;//��ѯ�����
		String myNewSQL="";//��ѯ���
		//Local��bupt_pm25
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
		//Local��bupt_hour_pm
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
		//Server��Dylos_pm25
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
     	
     	//���ز�ѯ�������ʱ��
		return lastTime;
	}
	
	//����txt�е�ʱ�䣬�����±��е�����,���ظ��¼�¼������
	public int UpdateTable(File myfile,String time){
		//System.out.println(myfile.toString());
		int acc=0;//����ͳ��txt������
		BufferedReader myReader;
		
		try {
			myReader = new BufferedReader(new FileReader(myfile));
			String myread;
			
			boolean flag=false;//����Flag,�����TXT���ҵ��ϴ�����ĸ���ʱ�䣬��Ϊtrue
			//���TXT���ҵ��˶�Ӧ��ʱ�䣬������ѭ��
			while((myread=myReader.readLine())!=null){
				if(myread.indexOf(time)!=-1){
					flag=true;
					break;
				}
			}
			System.out.println(flag);
			if(flag==false){//���û���ҵ��ϴθ��µ����ʱ�䣬��ӵ�9�п�ʼ���ļ�
				int round=0;//������ƴ���
				myReader=new BufferedReader(new FileReader(myfile));
				while(round<8){//�������ǰ8��֮�󣬾Ϳ�ʼ��ʽ��������
					myReader.readLine();
					round++;
				}
			}
			//����һ��λ�õ�TXTβ�����в������
			String[] tp=null;
			String temp;
			while((temp=myReader.readLine())!=null){
				tp=temp.split(",");
				//for test
				System.out.println();
				for(int i=0;i<tp.length;i++){
					System.out.println("tp["+i+"]: "+tp[i]);
				}
				//�������ݿ�bupt_pm25����ԭʼ����ֵ
				String InsertSQL="insert into bupt_pm25 values('"+tp[0]+"',"+tp[1]+","+tp[2]+",null,null,null);";
				int r=localBean.executeUpdateSQL(InsertSQL);//����DB�б����µļ�¼����Ŀ
				if(r!=0){//������½����0�����ʾ���³ɹ�
					System.out.println("UPDATE FINISH!");
					acc++;
				}else{
					System.out.println("NO UPDATE!");
				}
			}//End while loop
			
			//��bupt_pm25�������в�ȫ
			String UpdateSQL="update bupt_pm25 set DateAndHour=concat('20',substring(timeDylos,7,2),'-',left(timeDylos,2),'-',substring(timeDylos,4,2),' ',substring(timeDylos,10,2),':00:00'),time=concat('20',substring(timeDylos,7,2),'-',left(timeDylos,2),'-',substring(timeDylos,4,2),' ',substring(timeDylos,10,2),':',right(timeDylos,2),':00'),Date=substring(DateAndHour,1,10);";
			int a=localBean.executeUpdateSQL(UpdateSQL);
			if(a!=0){//������½����0�����ʾ���³ɹ�
				System.out.println("Update Finish!");
			}else{
				System.out.println("NO Update��");
			}
			/*------------------����Ϊ����bupt_hour_pm��---------------*/
			String mytime=FindLastTime("bupt_hour_pm");
			System.out.println(mytime);
			String myUpdate="insert into bupt_hour_pm(time,Small,pm25,Large) select DateAndHour,ROUND(AVG(Small))as AVG_Small,ROUND(AVG(Small)/100)AS AVG_PM25,ROUND(AVG(Large))as AVG_Large from bupt_pm25 where DateAndHour>'"+mytime+"' group by DateAndHour;";
			int b=localBean.executeUpdateSQL(myUpdate);
			if(b!=0){
				System.out.println("finish!");
			}else{
				System.out.println("no done!");
			}
			/*---------------����Ϊ����Server����Dylos_pm25��-----------------*/
			String myServerTime=FindLastTime("Dylos_pm25");//���ص�ǰ�����ʱ��
			System.out.println(myServerTime);
			//��ѯ����ʱ�������ı���bupt_hour_pm���еĽ����
			String mySelect="SELECT * FROM bupt_hour_pm where time>'"+myServerTime+"' order by time;";
			ResultSet myRS=localBean.executeSQL(mySelect);
			//����ÿ����������·�������
			while(myRS.next()){
				String serverUpdate="insert into Dylos_pm25 values('"+myRS.getString(1)+"',"+myRS.getString(2)+","+myRS.getString(3)+","+myRS.getString(4)+");";
				int c=serverBean.executeUpdateSQL(serverUpdate);
				if(c!=0){
					System.out.println("finish!");
				}else{
					System.out.println("not done!");
				}
			}//End while
			
			/*---------------����Ϊ����Server����pm25_Station_Dylos��������------------------------*/
			/*
			 * Ŀ�ģ���վ��PMֵ��վ������ֵ��ʵ�ʲ���Dylosֵ����
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
					"         where create_time>='2016-09-07 00:00:00' and pm25!='_' and station_name='���ǹ�԰'                                         "+
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
		
		//���ظ��¸���
		return acc;
	}
	
	public void ChooseDylosFile(final String myTime){
		
		
		
		String myfilepath="C:/Users/lenovo/Documents/Dylos Logs";
		final JFileChooser fileChooser = new JFileChooser(myfilepath);
		JFrame frame=new JFrame("ChoosetxtFile");
		final JTextArea picDir=new JTextArea();
		
		JButton button=new JButton("ѡ���ļ���");
		final Container content = frame.getContentPane(); 
		MouseListener myListener=new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				int updateNum=0;
				if (e.getClickCount() == 1) {
					fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
					 fileChooser.setDialogTitle("��Dylos�����ļ�");//�����ĶԻ������
					 int ret = fileChooser.showOpenDialog(null);
					 if (ret == JFileChooser.APPROVE_OPTION) {
						 File myDylos=fileChooser.getSelectedFile();
						 //���ò����ļ�Keyλ�õĺ���
						 updateNum=UpdateTable(myDylos,myTime);
					 }
					 
					//��ʾ���¼�¼�ĸ���	
			    	TextField consoleInfo=new TextField();
			        consoleInfo.setText("bupt_pm25�������ϣ�������"+updateNum+"����¼��");
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
