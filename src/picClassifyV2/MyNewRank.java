package picClassifyV2;

import picClassifyV2.databean;
import java.awt.Container;  
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.TextField;

import java.awt.GridLayout;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;  
import javax.swing.JPanel;
import javax.swing.JScrollPane;  
import javax.swing.JTextArea;  

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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.iptc.IptcReader;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class MyNewRank {
	public static void mySorting(ArrayList<Double>a,ArrayList<Integer>b){//Using insertion sorting
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final JFileChooser fileChooser = new JFileChooser(".");
		JFrame frame=new JFrame("MyNewRank");
		final JTextArea picDir=new JTextArea();
		JButton button=new JButton("选择文件夹");
 	    final Container content = frame.getContentPane();
 	    
 	    final ArrayList<String> myLocation= new ArrayList<String>();
 	    myLocation.add("北京邮电大学");
 	    myLocation.add("车公庄");
 	    myLocation.add("奥林匹克森林公园");
 	    myLocation.add("MSRA");
 	    myLocation.add("北方工业大学");
 	    myLocation.add("中关村");
 	    myLocation.add("北京大学");
 	    myLocation.add("对外经贸大学");
 	    myLocation.add("农展馆");
 	    myLocation.add("朝阳公园");
 	    myLocation.add("天坛");
 	    myLocation.add("北京交通大学");//11
	    myLocation.add("北京理工大学");//12
 	    
 	    
 	    
 	    Scanner cin=new Scanner(System.in);
 	   	System.out.println("请输入照片地点：");
 	   	final String myInput=cin.nextLine();//myInput变量为照片拍摄地点
 	   	System.out.println("你的照片地点编号为："+myLocation.indexOf(myInput));
 	   	System.out.println(); 
 	   
 	    
 	   	MouseListener ml=new MouseListener(){
 	   	  
 		  public void mouseClicked(MouseEvent e) {
 			 if(e.getClickCount()==2){
 				 fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
 				 fileChooser.setDialogTitle("打开文件夹");
 				 int ret = fileChooser.showOpenDialog(null);
 				 if (ret == JFileChooser.APPROVE_OPTION) {
 					 
 					 //连接数据库
 			 	     databean choose=new databean();
 			 	     Connection conn = null;
 			 	     //ResultSet rs=null;
 			 	     ResultSet myNewRS=null;
 			 	     choose.setDB("new2");//set database name
 			 	     conn=choose.getConn();//connect to database
 					 
 					 File pmdata=fileChooser.getSelectedFile();
					 BufferedReader Reader;
					 try {
						 	Reader = new BufferedReader(new FileReader(pmdata));
						 	String temp=null;
					 		String[] tp=null;
					 	    Double myDouble=null;
					 	    
					 		//Insert into double arrayList,向二维arrayList插入distance数据
					 	    ArrayList<ArrayList<Double>> myNewDistance=new ArrayList<ArrayList<Double>>();
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
					 		
					 		//对于照片所在站点，进行排序
					 		ArrayList<Double> myList = new ArrayList<Double>();//store the distance for each node
					 		ArrayList<Integer> myOrder = new ArrayList<Integer>();//store the index of each node
					 		for(int j=0;j<myNewDistance.get(myLocation.indexOf(myInput)).size();j++){
					 			myList.add(myNewDistance.get(myLocation.indexOf(myInput)).get(j));
					 			myOrder.add(j);
					 		}
					 		
					 		mySorting(myList,myOrder);//输入为到每个节点的距离和节点编号
					 		//排序之后到各站点距离结果
					 		System.out.println("排序之后，No."+myLocation.indexOf(myInput)+" 号站点到各站点距离如下：");
					 		for(int i=1;i<myList.size();i++){
					 			System.out.println(myOrder.get(i)+"--"+myList.get(i)+"km");
					 		}
					 		System.out.println();
					 		
					 		//用户提供日期和时间段（单位：小时）
					  	   	Scanner myScanner=new Scanner(System.in);
					  	   	System.out.println("请输入照片拍摄时间：");
					  	   	String time=myScanner.nextLine();//time变量应为照片的拍摄时间
					  	   	System.out.println("请输入时间段(单位：小时)：");
					  	   	int myDuration=myScanner.nextInt();
					 	   
					  	   	Date date=null;
					  	   	SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
					  	   	try{
					 		   date = format.parse(time);
					 		   date.setHours(date.getHours()-myDuration);
					 		   String myBeginTime = format.format(date);
					 		   System.out.println("开始时间是：");
					 		   System.out.println(myBeginTime);
					 		   
					 		   //输入周围站点名称，如果与照片拍摄站点重复，则重新输入
					 		   /*
					 		   String myPMLocation=null;
					 		   System.out.println("照片站点为:"+myInput);
					 		   do{
					 			   Scanner myScanner2=new Scanner(System.in);
					 			   System.out.println("请输入一个其他站点名称：");
					 			   myPMLocation=myScanner2.nextLine();
					 		   }while(myPMLocation.equals(myInput));
					 		   
					 		   String mySQL="select avg(cast(pm25 as unsigned))as PMAverage from picdata where time> '"+myBeginTime+"' and time< '"+time+"' and location='"+myPMLocation+"' and flag=1 and type!=100;";
					 		   myNewRS=choose.executeSQL(mySQL);
					 		   if(myNewRS.next()){
					 			   System.out.println("在站点"+myPMLocation+"，在时间段："+myBeginTime+"--"+time+"内，PM均值为：");
					 			   System.out.println(myNewRS.getDouble("PMAverage"));
					 		   }
					 		   */
					 		   
					 		   //Store average PM value for all other nodes
					 		   ArrayList<Double> myPMResult=new ArrayList<Double>();
					 		   for(int i=0;i<myOrder.size();i++){
					 			   String myPMLocation=myLocation.get(myOrder.get(i));
					 			   String mySQL="select avg(cast(pm25 as unsigned)) as PMAverage from picdata where time>'"+myBeginTime+"' and time<'"+time+"' and location='"+myPMLocation+"' and flag=1 and type!=100;";
					 			   myNewRS=choose.executeSQL(mySQL);
					 			   if(myNewRS.next()){
					 				  myPMResult.add(myNewRS.getDouble("PMAverage"));
					 			   }
					 		   }
					 		   System.out.println("Length of arraylist myOrder:"+myOrder.size());
					 		   System.out.println("Length of arraylist myPMResult:"+myPMResult.size());
					 		   
					 		   //Output all PM average values 
					 		   
					 		   System.out.println("对于No."+myLocation.indexOf(myInput)+"号站点，在时间段"+myBeginTime+"--"+time+"内，其他各站点平均PM值为：");
					 		   for(int i=0;i<myOrder.size();i++){
					 			   System.out.println(myOrder.get(i)+"--"+myPMResult.get(i));
					 		   }
					 		   System.out.println("测试myPMResult.get(0)的值："+myPMResult.get(0));
					 		   
					 		   
					 		   
					  	   	}catch(Exception myException){
					 		   myException.printStackTrace();
					 	   	}
					 		
					 		
					 		
					 		
					 }catch(Exception myException){
						 
					 } 
 				 }
 			 }
 		  }
 		  public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub	
		  }
		  public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
		  }
		  public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub	
		  }
		  public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub	
		  }
 	   };
 	   
 	   button.addMouseListener(ml);
	   content.add(button);
	   JPanel txt=new JPanel();
	   picDir.setColumns(15);
	   picDir.setRows(1);
	   txt.add(picDir); 
	   content.add(txt);
	   content.setLayout(new FlowLayout(FlowLayout.LEFT)); 
	   frame.setSize(500,600);
	   frame.setVisible(true);
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
