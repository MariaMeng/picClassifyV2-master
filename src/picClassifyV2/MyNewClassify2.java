package picClassifyV2;
import picClassifyV2.databean;
import java.awt.Container;  
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.TextField;

import java.awt.GridLayout;  
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;  
import javax.swing.JPanel;
import javax.swing.JScrollPane;  
import javax.swing.JTextArea;  

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

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

public class MyNewClassify2 {

	public static void FileCopy(String inputPath,String outputPath) throws IOException{
    	File inputFile;  
    	File outputFile;  
    	InputStream inputStream;  
    	OutputStream outputStream;  
        inputFile=new File(inputPath);
        if(!inputFile.exists()) { 
        	System.out.println(inputFile.getName()+" not exist!");
        	return;
        	}
        outputFile=new File(outputPath);  
        inputStream=new FileInputStream(inputFile);  
        outputStream=new FileOutputStream(outputFile);  

        byte b[]=new byte[(int)inputFile.length()];  
        inputStream.read(b);       //一次性读入  
        outputStream.write(b);   //一次性写入    
    }
	public static void main(String[] args) {
		final JFileChooser fileChooser = new JFileChooser(".");
		JFrame frame=new JFrame("PicClassifyNew");
		final JTextArea picDir=new JTextArea();
		JButton button=new JButton("选择文件夹");
 	    final Container content = frame.getContentPane(); 
 	    
 	    MouseListener ml=new MouseListener(){
  		  public void mouseClicked(MouseEvent e) {
  			 if(e.getClickCount()==2){
  				 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  				 fileChooser.setDialogTitle("打开文件夹");
  				 int ret = fileChooser.showOpenDialog(null);
  				 if (ret == JFileChooser.APPROVE_OPTION) {
  					 //文件夹路径
             		 String absolutepath=fileChooser.getSelectedFile().getAbsolutePath();
             		 System.out.println(absolutepath);//打印用户选择的路径
             		 picDir.setText(absolutepath);//在text中显示用户选择的路径
             		 String dir= picDir.getText();//用户输入的path
             		 //System.out.println(dir);
             		 
             		 File d=new File(dir);
  	        		 File list[] = d.listFiles();//file list 在路径下的文件
  	        		 for(File myList:list){
  	        			 // prints file and directory paths
  	        			 System.out.println(myList);//打出路径中的所有文件的路径
  	        		 }
  	        		 
  	        		 databean choose=new databean();
  	        		 Connection conn = null;
  	        		 ResultSet myNewRS=null;
  	        		 ResultSet myResult=null;
  	        		 ResultSet myFindpicName=null;
  	        		 choose.setDB("new2");//set database name
  	        		 conn=choose.getConn();//connect to database
  	        		 
  	        		 int i=0;
  	        		 int myCount=0;//统计手机型号非空，拍摄时间非空的照片数
  	        		 int FmodelTtime=0;//统计手机型号为空，拍摄时间非空的照片数
  	        		 int TmodelFtime=0;//统计手机型号非空，拍摄时间为空的照片数
  	        		 int FmodelFtime=0;//统计手机型号为空，拍摄时间为空的照片数
  	        		 
  	        		 try{
  	        			while(i<list.length){//遍历文件夹中的所有照片
  	        					String picName=list[i].getName();//输出照片name
  	        					System.out.println(picName);
  	    
  	        					//读取图片时间和手机型号
  	        					String model=null;//读取图片的手机型号
  	       	        		    String time=null;//读取照片的时间
  	       	        		    
  	       	        		    String location=null;//保存照片的拍摄地点
  	       	        		    String l[]=list[i].getParent().split("\\\\");
							    int lg=l.length;
							    location=l[lg-1];//保存了拍摄照片地理位置信息
							    System.out.println("location: "+location);
  	       	        		    
  	        					//读取照片的拍摄时间和手机型号
							    Metadata metadata = ImageMetadataReader.readMetadata(list[i]);
                             	for (Directory directory : metadata.getDirectories()) {
                             		for (Tag tag : directory.getTags()) {
                             			String tagName = tag.getTagName();
        					            String desc = tag.getDescription();
        					            if(tagName.equals("Date/Time Original")){
        					                time=desc;//保存照片拍摄时间
        					                System.out.println("time:"+time);//测试可以输出照片时间
        					            } 
        					            else if(tagName.equals("Model")) {
        					                 model=desc;//保存照片手机型号
        					                 System.out.println("model:"+model);//测试可以输出照片手机型号
        					            }
                             		}//End inner for loop
                             	}//End outer for loop
                             	
                             	if(model!=null){//如果照片手机型号非空
                             		if(time!=null){//如果照片拍摄时间非空
                             			//在DB中查找本张照片，获取照片ID和type值
                                     	
                             			
                             			String myNewSQL="select  * from picdata where name='"+picName+"' and type!=100 and model='"+model+"' and time='"+time+"';";
                                     	myNewRS=choose.executeSQL(myNewSQL);
                                     	if(myNewRS.next()){
                                     		myCount++;
                                     		String oldFilePath=list[i].getAbsolutePath();//get pic path
                                     		String newFilePath="C:\\Users\\apple\\Desktop\\研一上学期\\LAB\\NewPictureForJan";
                                     		//String newFilePath="C:\\Users\\apple\\Desktop\\研一上学期\\LAB\\11-12New_薛耀宏\\After classification\\12月";
                                     		int myID=myNewRS.getInt("id");
                                         	int type=myNewRS.getInt("type");//返回查询的tuple中type的值
                                         	newFilePath+="\\"+type+"\\"+myID+".jpg";//重新定义新的照片路径及照片name
                                         	
                                         	FileCopy(oldFilePath,newFilePath);//copy into new path
                                     	}	
                                     	else{
                                   	  		System.out.println("There is no picture infor for "+picName+" or Type=100 in the database!");
                                     	}
                                     	
                             		}//End if(照片拍摄时间非空) condition
                             		else{//如果照片拍摄时间为空
                             			
                             			System.out.println("Please assign picture's upload time as its time!");
                             			TmodelFtime++;
                             			
                             		}
                             	}//End if(照片手机型号非空) condition
                             	else{//如果照片手机型号为空
                             		if(time!=null){//如果照片拍摄时间非空
                             			//在DB中查找本张照片，获取照片ID和type值,省去了model约束条件
                                     	
                             			/*
                             			String myNewSQL="select  * from picdata where name='"+picName+"' and type!=100 and time='"+time+"';";
                                     	myResult=choose.executeSQL(myNewSQL);
                                     	if(myResult.next()){
                                     		FmodelTtime++;
                                     		String oldFilePath=list[i].getAbsolutePath();//get pic path
                                     		String newFilePath="C:\\Users\\apple\\Desktop\\研一上学期\\LAB\\NewPictureForDec";
                                     		int myID=myResult.getInt("id");
                                         	int type=myResult.getInt("type");//返回查询的tuple中type的值
                                         	newFilePath+="\\"+type+"\\"+myID+".jpg";//重新定义新的照片路径及照片name
                                         	
                                         	FileCopy(oldFilePath,newFilePath);//copy into new path
                                     	}	
                                     	else{
                                   	  		System.out.println("There is no picture infor for "+picName+" or Type=100 in the database!");
                                     	}
                                     	*/
                                     	
                             		}//End if(照片拍摄时间非空) condition
                             		else{//如果照片拍摄时间为空
                             			System.out.println("Please assign picture's upload time as its time!");
                             			
                             			
                             			String[] mysplit=picName.split("\\.");
                             			System.out.println(mysplit[0]);
                             			String newName=mysplit[0];
                             			
                             			String oldFilePath=list[i].getAbsolutePath();//get pic path
                             			String newFilePath="C:\\Users\\apple\\Desktop\\研一上学期\\LAB\\照片TimeModelFailed\\1月";
                             			newFilePath+="\\"+location+"\\"+newName+".jpg";//重新定义复制的目标路径
                             			
                             			FileCopy(oldFilePath,newFilePath);//copy into new path
                             			
                             			//在DB中通过name查询
                             			String myJPEGname=newName+".jpeg";
                             			String myFindNamesql="select * from picdata where name='"+myJPEGname+"' and type!=100";
                             			myFindpicName=choose.executeSQL(myFindNamesql);
                             			if(myFindpicName.next()){
                             				FmodelFtime++;
                             				int myID=myFindpicName.getInt("id");
                                         	int type=myFindpicName.getInt("type");//返回查询的tuple中type的值
                                         	String mySecondPath="C:\\Users\\apple\\Desktop\\研一上学期\\LAB\\11-12月Time照片\\1月";
                                         	mySecondPath+="\\"+type+"\\"+myID+".jpg";//重新定义新的照片路径及照片name
                                         	
                                         	FileCopy(oldFilePath,mySecondPath);//copy into new path
                             			}
                             			else{
                             				System.out.println("There is no picture infor for "+myJPEGname+" or Type=100 in the database!");
                             			}
                             			
                             			
                             		}
                             	}//End else condition
                             	
                             	i++;
  	        			}//End While Loop
  	        			
  	        			//在GUI中显示复制结果
  	        			TextField consoleInfo=new TextField();
     	                consoleInfo.setText("处理"+dir+"完毕！图片集共有图片"+list.length+"张，复制处理图片"+myCount+"张。");
     	                content.add(consoleInfo);
     	                
     	                System.out.println();
     	                System.out.println("照片model非空，拍摄时间非空 照片数："+myCount);
     	                System.out.println("照片model非空，拍摄时间为空 照片数："+TmodelFtime);
     	                System.out.println("照片model为空，拍摄时间非空 照片数："+FmodelTtime);
     	                System.out.println("照片model为空，拍摄时间为空 照片数："+FmodelFtime);
  	        		 }catch(Exception myException){
  	        			myException.printStackTrace();
  	        		 } 
  				 }//End if condition
  			 }//End if double clicks condition
  		  }//End mouseClicked method
  		  public void mousePressed(MouseEvent e) {}
  		  public void mouseReleased(MouseEvent e) {}
  		  public void mouseEntered(MouseEvent e) {}
  		  public void mouseExited(MouseEvent e) {}
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
