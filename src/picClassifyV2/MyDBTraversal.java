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

public class MyDBTraversal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final JFileChooser fileChooser = new JFileChooser(".");
		JFrame frame=new JFrame("PicClassifyNew");
		final JTextArea picDir=new JTextArea();
		JButton button=new JButton("ѡ���ļ���");
 	    final Container content = frame.getContentPane(); 
 	    
 	   MouseListener ml=new MouseListener(){
 		  public void mouseClicked(MouseEvent e) {
 			 if(e.getClickCount()==2){
 				 fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
 				 fileChooser.setDialogTitle("���ļ���");
 				 int ret = fileChooser.showOpenDialog(null);
 				 if (ret == JFileChooser.APPROVE_OPTION) {
 					 //�ļ���·��
             		 String absolutepath=fileChooser.getSelectedFile().getAbsolutePath();
             		 System.out.println(absolutepath);//��ӡ�û�ѡ���·��
             		 picDir.setText(absolutepath);//��text����ʾ�û�ѡ���·��
             		 String dir= picDir.getText();//�û������path
             		 //System.out.println(dir);
             		 
             		 File d=new File(dir);
 	        		 File list[] = d.listFiles();//file list ��·���µ��ļ�
 	        		 
 	        		 for(File myList:list){
 	        			 // prints file and directory paths
 	        			 System.out.println(myList);//���·���е������ļ���·��
 	        		 }
 	        		 
 	        		 databean choose=new databean();
 	        		 Connection conn = null;
 	        		 //ResultSet rs=null;
 	        		 ResultSet myNewRS=null;
 	        		 choose.setDB("new2");//set database name
 	        		 conn=choose.getConn();//connect to database
 	        		 
 	        		 int i=0;
 	        		 int myCount=0;
 	        		 int myId=0;
 	        		 String[] myName=null;
 	        		 try{
 	        			while(i<list.length){
 	        				String picName=list[i].getName();//�����Ƭname
	        				System.out.println(picName);
	        				myName=picName.split("\\.");//��"."��"|"Ϊ�ָ������ַ�������split����ʱ��Ҫת��һ��
	        				//System.out.println(myName[0]);
	        				myId=Integer.parseInt(myName[0]);//��NAME��ֵת��ID
	        				System.out.println(myId);
	        				
	        				//���в�ѯ����
                         	String myNewSQL="select  * from picdata where id="+myId+";";
                         	myNewRS=choose.executeSQL(myNewSQL);
                         	if(myNewRS.next()){
                         		myCount++;
                         		String updateSql="update picdata set flag=1 where id="+myId+";";
				 				choose.executeUpdateSQL(updateSql);
                         	}
                         	else{
                         		System.out.println(myId+"NOT FIND IN DATABASE!");
                         	}
	        				i++;
 	        			}//End While loop
 	        			
 	        			TextField consoleInfo=new TextField();
 	        			consoleInfo.setText("������"+myCount+"��¼�� Finish!");
     	                content.add(consoleInfo);
 	        			
 	        		}catch(Exception myException){
 	        			
 	        		}
 	        		 
 				 }
 			 }//End If
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
	}//End for main

}
