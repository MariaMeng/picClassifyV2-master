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
    public ResultSet myNewRS=null;//����ԭʼ��Ƭ��Ϣ
    public ResultSet myCheck=null;//��PMλ����Ϣ���У���ѯ��X��վ���PM_ID
    public ResultSet myResult=null;//��ѯ����վ���Ӧʱ�̵�PMֵ
    public ResultSet myFindNewTime=null;//��������վ�����ʱ�̵�PMֵ
    public ResultSet myWeatherID=null;//��Weatherλ����Ϣ���У���ѯ��X��վ���Weather_ID
    public ResultSet myWeatherData=null;//��ѯ����վ���Ӧʱ�̵�Weatherֵ
	public ResultSet myFindWeatherTime=null;//��������վ�����ʱ�̵�Weatherֵ
    
	public int id;//store the id for each picture
	public String location=null;//The location variable stores the location for each picture.
	public String time=null;//time store the time for each picture.
	public String PM_ID=null;//����������Ƭվ���Ӧ��PM_ID
	
	
	ArrayList<ArrayList<Double>> myNewDistance;//�����ڽӾ���Ķ�ά����
	ArrayList<Double> myList;//�����ض�վ��ĵ�����վ��ľ���ֵ����ʼʱδ����
	ArrayList<Integer> myOrder;//�����ض�վ���Ӧ����վ���վ������
	ArrayList<String> myLocation= new ArrayList<String>();
	
	String[] myFormerPMID = new String[4];//��������վ��+�������3��վ���PMId
	String[] myLatterPMID=new String[4];//����������Զ3��վ���PMId����һ��Ԫ��Ϊ��
	
	public void mySorting(ArrayList<Double>a,ArrayList<Integer>b){//��������ʹ�ò�������
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
		//GUI����
    	fileChooser = new JFileChooser(".");
		frame=new JFrame("ChoosePic");
		picDir=new JTextArea();
		button=new JButton("ѡ���ļ���");
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
    	
    	//�������ݿ�
		choose.setDB("new2");//set database name
	    conn=choose.getConn();//connect to database
	    
	    myLocation.add("�����ʵ��ѧ");//0
 	    myLocation.add("����ׯ");//1
 	    myLocation.add("����ƥ��ɭ�ֹ�԰");//2
 	    myLocation.add("MSRA");//3
 	    myLocation.add("������ҵ��ѧ");//4
 	    myLocation.add("�йش�");//5
 	    myLocation.add("������ѧ");//6
 	    myLocation.add("���⾭ó��ѧ");//7
 	    myLocation.add("ũչ��");//8
 	    myLocation.add("������԰");//9
 	    myLocation.add("��̳");//10
 	    myLocation.add("������ͨ��ѧ");//11
 	    myLocation.add("��������ѧ");//12
	}
    
    public void mouseClicked(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){
    	if(e.getSource()==button){
    		fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
    		fileChooser.setDialogTitle("���ļ���");
    		int ret = fileChooser.showOpenDialog(null);
    		if (ret == JFileChooser.APPROVE_OPTION) {
    			
    			File pmdata=fileChooser.getSelectedFile();
				BufferedReader Reader;
    			try{
    				Reader = new BufferedReader(new FileReader(pmdata));
    				String temp=null;
			 		String[] tp=null;
			 	    Double myDouble=null;
			 	    
			 	    //���άarrayList����distance����
			 	    myNewDistance=new ArrayList<ArrayList<Double>>();
			 		while((temp=Reader.readLine())!= null){//readLine����ÿ�����ָ�붼���ƶ�
			 			ArrayList<Double> myDoubleStr=new ArrayList<Double>();
			 			tp=temp.split(",");//tp�洢ÿ�зָ�֮���ÿ��String
			 			for(int a=0;a<tp.length;a++){
			 				myDouble=Double.parseDouble(tp[a]);
			 				myDoubleStr.add(myDouble);
			 			}
			 			myNewDistance.add(myDoubleStr);
			 		} 
					
			 		//����ڽӾ��� 
			 		System.out.println("�ڽӾ���");
			 		for(int i=0;i<myNewDistance.size();i++){
			 			for(int j=0;j<myNewDistance.get(i).size();j++){
			 				System.out.print(myNewDistance.get(i).get(j)+" ");
			 			}
			 			System.out.println();
			 		}
			 		System.out.println();
			 		
    				//���в�ѯ����
    		 	    String myNewSQL="select  * from mylocation where id>=51775 ;";
    		 	    myNewRS=choose.executeSQL(myNewSQL);//����ԭʼ��Ƭ��Ϣ
    		    	
    		 	    while(myNewRS.next()){//for each picture information in the database
    		 	    	//��ѯ��Ƭ��id,����ص������ʱ��
    		 	    	id=myNewRS.getInt("id");
    		 	    	System.out.println("ID:"+id);
    		 	    	location=myNewRS.getString("location");//location��������ص�����
    		 	    	//System.out.println("Location:"+location);
    		 	    	if(location.equalsIgnoreCase("����")){
    		 	    		location="�����ʵ��ѧ";
    		 	    	}
    		 	    	System.out.println("Location:"+location);
    		 	    	SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		 	    	Date myDate=myFormat.parse(myNewRS.getString("time"));
    		 	    	SimpleDateFormat myNewFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
    		 	    	time= myNewFormat.format(myDate);//Record the time for each picture
    		 	    	System.out.println("Time:"+time);
    		 	    	
    		 	    	//��������վ���Ӧ��PMID
    		 	    	String myPicPMID="select * from location_pm where location='"+location+"';";
    		 	    	ResultSet myFindPicPMID=choose.executeSQL(myPicPMID);
    		 	    	if(myFindPicPMID.next()){
    		 	    		PM_ID=myFindPicPMID.getString("station_id");
    		 	    		System.out.println("����վ��PM_ID:"+PM_ID);
    		 	    		myFormerPMID[0]=PM_ID;//��������վ��PM_ID������
    		 	    		
    		 	    		//����ƬPM_ID�������
    		 	    		String myUpdatePM_ID="update mylocation set PM_ID='"+PM_ID+"' where id="+id+";";
    		 	    		choose.executeUpdateSQL(myUpdatePM_ID);
    		 	    	}
    		 	    	
    		 	    	
    		 	    	//������Ƭ����ص��������
    		 	    	myList = new ArrayList<Double>();//���浽����վ��ľ���
				 		myOrder = new ArrayList<Integer>();//��������վ���index
				 		for(int j=0;j<myNewDistance.get(myLocation.indexOf(location)).size();j++){
				 			myList.add(myNewDistance.get(myLocation.indexOf(location)).get(j));
				 			myOrder.add(j);
				 		}
				 		mySorting(myList,myOrder);//����Ϊ��ÿ���ڵ�ľ���ͽڵ���
				 		
				 		//���ԣ��������֮�󵽸�վ�������
				 		System.out.println("����֮��No."+myLocation.indexOf(location)+" ��վ��"+location+"����վ��������£�");
				 		for(int i=1;i<=12;i++){//ֻ�鿴ÿ��վ�������3������վ�����
				 			System.out.println(myLocation.get(myOrder.get(i))+"--"+myList.get(i)+"km");
				 		}
				 		System.out.println();
				 		
				 		//��������ص������3��վ�㣬���Ҷ�Ӧ��PM��Weather��Ϣ
				 		for(int j=1;j<=3;j++){
				 			String myPM_ID=null;
				 			String myWeather_ID=null;
				 			
				 			Date myNewDate=myNewFormat.parse(time);//��Ϊ��ʱ��ֵ��ʱ����Stringת��ΪDate����
	    		 	    	Date myNewWeatherDate=myNewFormat.parse(time);//����Weather��ѯʱ����Ӧʱ��û����ֵʱ����ǰ����
				 			
				 			//�����X��վ��Ϊ��̳��ֱ��ɾ��myOrder��myList����
	    		 	    	if(myLocation.get(myOrder.get(j)).equals("��̳")){
	    		 	    		myOrder.remove(j);
	    		 	    		myList.remove(j);
	    		 	    	}
	    		 	    	
	    		 	    	
	    		 	    	
	    		 	    	//��PMλ����Ϣ���У���ѯ��X��վ���PM_ID
	    		 	    	/*
				 			String mySelection="select * from location_pm where location='"+myLocation.get(myOrder.get(j))+"';";
				 			myCheck=choose.executeSQL(mySelection);//��PMλ����Ϣ���У���ѯ��X��վ���PM_ID
				 			if(myCheck.next()){
				 				myPM_ID=myCheck.getString("station_id");//�����Xվ���PM��id
				 				System.out.println(myPM_ID);
				 			}
				 			*/
	    		 	    	//��PMλ����Ϣ���У���ѯ��Xվ���PM_ID������֮ǰ���������ų��Ѵ��ڵ�վ��
	    		 	    	while(true){
	    		 	    		//��PMλ����Ϣ���У���ѯ��X��վ���PM_ID
	    		 	    		String mySelection="select * from location_pm where location='"+myLocation.get(myOrder.get(j))+"';";
					 			myCheck=choose.executeSQL(mySelection);//��PMλ����Ϣ���У���ѯ��X��վ���PM_ID
					 			if(myCheck.next()){
					 				myPM_ID=myCheck.getString("station_id");//�����Xվ���PM��id
					 				
					 			}
					 			int p;
					 			for(p=j-1;p>=0;p--){
					 				if(myPM_ID.equals(myFormerPMID[p])){
					 					//ɾ����Xվ����±�͵�����վ��ľ���
					 					myOrder.remove(j);
					 					myList.remove(j);
					 					break;
					 				}
					 			}
					 			if(p==-1){//���������
					 				myFormerPMID[j]=myPM_ID;//���浱ǰ��Xվ���PM_ID������
					 				System.out.print("�����"+j+"��վ���PM_IDΪ��"+myPM_ID+"  ");
					 				System.out.println("����Ϊ��"+myList.get(j));
					 				break;
					 			}
	    		 	    	}
				 			
				 			//��Weatherλ����Ϣ���У���ѯ��X��վ���Weather_ID
				 			String mySelectionWeather="select * from district where location='"+myLocation.get(myOrder.get(j))+"';";
				 			myWeatherID=choose.executeSQL(mySelectionWeather);//��Weatherλ����Ϣ���У���ѯ��X��վ���Weather_ID
				 			if(myWeatherID.next()){
				 				myWeather_ID=myWeatherID.getString("id");
				 				//System.out.println("�����"+j+"��վ���Weather_IDΪ��"+myWeather_ID);
				 			}
				 			
				 			//����Ӧվ��PM��Weather��ID���뵽���ݿ���
				 			String updateLocationIDSQL="update mylocation set location_"+j+"L='"+myLocation.get(myOrder.get(j))+"',distance_"+j+"L='"+myList.get(j)+"',PM_ID_"+j+"L='"+myPM_ID+"',Weather_ID_"+j+"L='"+myWeather_ID+"' where id="+id+";";
				 			choose.executeUpdateSQL(updateLocationIDSQL);
				 			
				 			
				 			//��ѯ����վ���Ӧʱ�̵�PMֵ
				 			String myCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+time+"';";
				 			myResult=choose.executeSQL(myCheckPMSQL);//��ѯ����վ���Ӧʱ�̵�PMֵ
				 			if(myResult.next()){//����ҵ���Ӧ��ʱ���PMֵ
				 				String myPM25=myResult.getString("pm25");
				 				String myPM10=myResult.getString("pm10");
				 				String myCO2=myResult.getString("co2");
				 				
				 				String UpdatePMSQL="update mylocation set pm25_"+j+"L='"+myPM25+"',pm10_"+j+"L='"+myPM10+"',co2_"+j+"L='"+myCO2+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			else{//���û�в��ҵ���Ӧʱ���PM��ֵ��������һʱ�̲��ң�֪�����ҵ�Ϊֹ
				 				
				 				String myNewTime=null;
				 				do{
				 					myNewDate.setHours(myNewDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+myNewTime+"';";
				 					myFindNewTime=choose.executeSQL(myNewCheckPMSQL);//��������վ�����ʱ�̵�PMֵ
				 				}while(!myFindNewTime.next());
				 				
				 				String UpdatePMSQL="update mylocation set ActualPMTime_"+j+"h='"+myNewTime+"',pm25_"+j+"L='"+myFindNewTime.getString("pm25")+"',pm10_"+j+"L='"+myFindNewTime.getString("pm10")+"',co2_"+j+"L='"+myFindNewTime.getString("co2")+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			
				 			//���Weather_IDΪ��̳��Ӧ��00107,��ִ����һ��ѭ��
				 			if(myWeather_ID.equalsIgnoreCase("00107")){
				 				continue;
				 			}
				 			//��ѯ����վ���Ӧʱ�̵�Weatherֵ
				 			String myCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+time+"';";
				 			myWeatherData=choose.executeSQL(myCheckWeatherSQL);//��ѯ����վ���Ӧʱ�̵�Weatherֵ
				 			if(myWeatherData.next()){//�����Ӧ��Weather��ֵ����ֱ�Ӹ������ݿ�
				 				String myTemperature=myWeatherData.getString("Temperature");
				 				String myPressure=myWeatherData.getString("Pressure");
				 				String myHumidity=myWeatherData.getString("Humidity");
				 				String myWindSpeed=myWeatherData.getString("WindSpeed");
				 				String myWindDirection=myWeatherData.getString("WindDirection");
				 				String myWeather=myWeatherData.getString("Weather");
				 				
				 				String UpdateWeatherSQL="update mylocation set Temperature_"+j+"L='"+myTemperature+"',Pressure_"+j+"L='"+myPressure+"',Humidity_"+j+"L='"+myHumidity+"',WindSpeed_"+j+"L='"+myWindSpeed+"',WindDirection_"+j+"L='"+myWindDirection+"',Weather_"+j+"L='"+myWeather+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdateWeatherSQL);
				 			}
				 			else{//����Ӧ��Weather��ֵ����Ѱ��֮ǰСʱ����ֵ��֪���ҵ�Ϊֹ�����������ݿ�
				 				String myNewTime=null;
				 				do{
				 					myNewWeatherDate.setHours(myNewWeatherDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewWeatherDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+myNewTime+"';";
				 					myFindWeatherTime=choose.executeSQL(myNewCheckWeatherSQL);//��������վ�����ʱ�̵�Weatherֵ
				 				}while(!myFindWeatherTime.next());
				 				
				 				String UpdateWeatherSQL="update mylocation set ActualWeatherTime_"+j+"h='"+myNewTime+"',Temperature_"+j+"L='"+myFindWeatherTime.getString("Temperature")+"',Pressure_"+j+"L='"+myFindWeatherTime.getString("Pressure")+"',Humidity_"+j+"L='"+myFindWeatherTime.getString("Humidity")+"',WindSpeed_"+j+"L='"+myFindWeatherTime.getString("WindSpeed")+"',WindDirection_"+j+"L='"+myFindWeatherTime.getString("WindDirection")+"',Weather_"+j+"L='"+myFindWeatherTime.getString("Weather")+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdateWeatherSQL);
				 			}
				 			
				 		}//End for loop
				 		
				 		//��������ص���Զ��3��վ�㣬���Ҷ�Ӧ��PM��Weather��Ϣ
				 		for(int b=1;b<=3;b++){
				 			String myPM_ID=null;
				 			String myWeather_ID=null;
				 			
				 			Date myNewDate=myNewFormat.parse(time);//��Ϊ��ʱ��ֵ��ʱ����Stringת��ΪDate����
	    		 	    	Date myNewWeatherDate=myNewFormat.parse(time);//����Weather��ѯʱ����Ӧʱ��û����ֵʱ����ǰ����
				 			
	    		 	    	//���������X��վ��Ϊ��̳��ֱ��ɾ��myOrder��myList����
	    		 	    	if(myLocation.get(myOrder.get(myOrder.size()-b)).equals("��̳")){
	    		 	    		myOrder.remove(myOrder.size()-b);
	    		 	    		myList.remove(myList.size()-b);
	    		 	    	}
				 			
	    		 	    	//��PMλ����Ϣ���У���ѯ������Xվ���PM_ID������֮ǰ���������ų��Ѵ��ڵ�վ��
	    		 	    	while(true){
	    		 	    		//��PMλ����Ϣ���У���ѯ������X��վ���PM_ID
	    		 	    		String mySelection="select * from location_pm where location='"+myLocation.get(myOrder.get(myOrder.size()-b))+"';";
					 			myCheck=choose.executeSQL(mySelection);//��PMλ����Ϣ���У���ѯ��X��վ���PM_ID
					 			if(myCheck.next()){
					 				myPM_ID=myCheck.getString("station_id");//�����Xվ���PM��id
					 				
					 			}
					 			
					 			int p;
					 			for(p=b-1;p>=0;p--){
					 				if(myPM_ID.equals(myLatterPMID[p])){
					 					//ɾ����Xվ����±�͵�����վ��ľ���
					 					myOrder.remove(myOrder.size()-b);
					 					myList.remove(myList.size()-b);
					 					break;
					 				}
					 			}
					 			if(p==-1){//���������
					 				int q;
					 				for(q=3;q>=0;q--){
						 				if(myPM_ID.equals(myFormerPMID[q])){
						 					//ɾ����Xվ����±�͵�����վ��ľ���
						 					myOrder.remove(myOrder.size()-b);
						 					myList.remove(myList.size()-b);
						 					break;
						 				}
						 			}
					 				if(q==-1){//�����������
					 					myLatterPMID[b]=myPM_ID;//���浱ǰ������Xվ���PM_ID������
						 				System.out.print("��Զ��"+b+"��վ���PM_IDΪ��"+myPM_ID+"  ");
						 				System.out.println("����Ϊ��"+myList.get(myList.size()-b));
						 				break;
					 				}//End if condition
					 			}//End if condition
	    		 	    	}//End while condition
				 			
	    		 	    	//��Weatherλ����Ϣ���У���ѯ������X��վ���Weather_ID
				 			String mySelectionWeather="select * from district where location='"+myLocation.get(myOrder.get(myOrder.size()-b))+"';";
				 			myWeatherID=choose.executeSQL(mySelectionWeather);//��Weatherλ����Ϣ���У���ѯ��X��վ���Weather_ID
				 			if(myWeatherID.next()){
				 				myWeather_ID=myWeatherID.getString("id");
				 				//System.out.println("��Զ��"+b+"��վ���Weather_IDΪ��"+myWeather_ID);
				 			}
	    		 	    	
				 			//����Ӧվ��PM��Weather��ID���뵽���ݿ���
				 			String updateLocationIDSQL="update mylocation set Last_location_"+b+"L='"+myLocation.get(myOrder.get(myOrder.size()-b))+"',Last_distance_"+b+"L='"+myList.get(myList.size()-b)+"',Last_PM_ID_"+b+"L='"+myPM_ID+"',Last_Weather_ID_"+b+"L='"+myWeather_ID+"' where id="+id+";";
				 			choose.executeUpdateSQL(updateLocationIDSQL);
				 			
				 			//��ѯ����վ���Ӧʱ�̵�PMֵ
				 			String myCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+time+"';";
				 			myResult=choose.executeSQL(myCheckPMSQL);//��ѯ����վ���Ӧʱ�̵�PMֵ
				 			if(myResult.next()){//����ҵ���Ӧ��ʱ���PMֵ
				 				String myPM25=myResult.getString("pm25");
				 				String myPM10=myResult.getString("pm10");
				 				String myCO2=myResult.getString("co2");
				 				
				 				String UpdatePMSQL="update mylocation set Last_pm25_"+b+"L='"+myPM25+"',Last_pm10_"+b+"L='"+myPM10+"',Last_co2_"+b+"L='"+myCO2+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			else{//���û�в��ҵ���Ӧʱ���PM��ֵ��������һʱ�̲��ң�֪�����ҵ�Ϊֹ
				 				
				 				String myNewTime=null;
				 				do{
				 					myNewDate.setHours(myNewDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckPMSQL="select * from all_pm25value where station_id='"+myPM_ID+"' and time='"+myNewTime+"';";
				 					myFindNewTime=choose.executeSQL(myNewCheckPMSQL);//��������վ�����ʱ�̵�PMֵ
				 				}while(!myFindNewTime.next());
				 				
				 				String UpdatePMSQL="update mylocation set Last_ActualPMTime_"+b+"h='"+myNewTime+"',Last_pm25_"+b+"L='"+myFindNewTime.getString("pm25")+"',Last_pm10_"+b+"L='"+myFindNewTime.getString("pm10")+"',Last_co2_"+b+"L='"+myFindNewTime.getString("co2")+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdatePMSQL);
				 			}
				 			
				 			
				 			//��ѯ����վ���Ӧʱ�̵�Weatherֵ
				 			String myCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+time+"';";
				 			myWeatherData=choose.executeSQL(myCheckWeatherSQL);//��ѯ����վ���Ӧʱ�̵�Weatherֵ
				 			if(myWeatherData.next()){//�����Ӧ��Weather��ֵ����ֱ�Ӹ������ݿ�
				 				String myTemperature=myWeatherData.getString("Temperature");
				 				String myPressure=myWeatherData.getString("Pressure");
				 				String myHumidity=myWeatherData.getString("Humidity");
				 				String myWindSpeed=myWeatherData.getString("WindSpeed");
				 				String myWindDirection=myWeatherData.getString("WindDirection");
				 				String myWeather=myWeatherData.getString("Weather");
				 				
				 				String UpdateWeatherSQL="update mylocation set Last_Temperature_"+b+"L='"+myTemperature+"',Last_Pressure_"+b+"L='"+myPressure+"',Last_Humidity_"+b+"L='"+myHumidity+"',Last_WindSpeed_"+b+"L='"+myWindSpeed+"',Last_WindDirection_"+b+"L='"+myWindDirection+"',Last_Weather_"+b+"L='"+myWeather+"' where id="+id+";";
				 				choose.executeUpdateSQL(UpdateWeatherSQL);
				 			}
				 			else{//����Ӧ��Weather��ֵ����Ѱ��֮ǰСʱ����ֵ��֪���ҵ�Ϊֹ�����������ݿ�
				 				String myNewTime=null;
				 				do{
				 					myNewWeatherDate.setHours(myNewWeatherDate.getHours()-1);
				 					myNewTime=myNewFormat.format(myNewWeatherDate);
				 					//System.out.println(myNewTime);
				 					String myNewCheckWeatherSQL="select * from weather where id='"+myWeather_ID+"' and time='"+myNewTime+"';";
				 					myFindWeatherTime=choose.executeSQL(myNewCheckWeatherSQL);//��������վ�����ʱ�̵�Weatherֵ
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
