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

public class MyNewClassify {

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
		// TODO Auto-generated method stub
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
 	        		 ResultSet rs=null;
 	        		 ResultSet myNewRS=null;
 	        		 choose.setDB("new2");//set database name
 	        		 conn=choose.getConn();//connect to database
 	        		 
 	        		 int i=0;
 	        		 int myCount=0;
 	        		 String model=null;//读取图片的手机型号
 	        		 String time=null;//读取照片的时间
 	        		 
 	        		 try{
 	        			while(i<list.length){
 	        				String picName=list[i].getName();//输出照片name
                            System.out.println(picName);
 	        				String sql="select  * from picdata where name='"+picName+"' and type!=100";                          
                            rs=choose.executeSQL(sql);
                            //System.out.println(rs.getString(2));
                            if(rs.next()){
                            	//读取图片时间和手机型号
                            	Metadata metadata = ImageMetadataReader.readMetadata(list[i]);
                            	for (Directory directory : metadata.getDirectories()) {
                            		for (Tag tag : directory.getTags()) {
                            			String tagName = tag.getTagName();
       					                String desc = tag.getDescription();
       					                if (tagName.equals("Date/Time Original")){
       					                    //拍摄时间
       					                    time=desc;
       					                    //System.out.println(time);//测试可以输出照片时间
       					                } 
       					                else if (tagName.equals("Model")) {
       					                    //手机型号
       					                    model=desc;
       					                    //System.out.println(model);//测试可以输出照片手机型号
       					                }
                            		}
                            	}
                            	//再次进行查询操作
                            	String myNewSQL="select  * from picdata where name='"+picName+"' and type!=100 and model='"+model+"' and time='"+time+"';";
                            	myNewRS=choose.executeSQL(myNewSQL);
                            	if(myNewRS.next()){
                            		myCount++;
                            		String oldFilePath=list[i].getAbsolutePath();//get pic path
                                	//System.out.println(oldFilePath);
                            		String newFilePath="C:\\Users\\apple\\Desktop\\研一上学期\\LAB\\pm25 2015-09-21";
                            		//String newFilePath="C:\\Users\\apple\\Desktop\\MyNewTest";
                            		int myID=myNewRS.getInt(1);
                                	//System.out.println(myID);
                                	int type=myNewRS.getInt(11);//返回查询的tuple中type的值
                                	//System.out.println(type);
                                	newFilePath+="\\"+type+"\\"+myID+".jpg";//重新定义新的照片路径及照片name
                                	//System.out.println(newFilePath);
                                	FileCopy(oldFilePath,newFilePath);//copy into new path
                            	}	
                            	
                            }
                            else{
                          	  	System.out.println("there is no pic"+picName+" info in the database!");
                          	}
                            i++;
 	        			}//End While Loop
 	        			
 	        			TextField consoleInfo=new TextField();
    	                consoleInfo.setText("处理"+dir+"完毕！图片集共有图片"+list.length+"张，复制处理图片"+myCount+"张。");
    	                content.add(consoleInfo);
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
