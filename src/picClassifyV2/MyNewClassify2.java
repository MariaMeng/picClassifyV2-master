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
        inputStream.read(b);       //һ���Զ���  
        outputStream.write(b);   //һ����д��    
    }
	public static void main(String[] args) {
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
  	        		 ResultSet myNewRS=null;
  	        		 ResultSet myResult=null;
  	        		 ResultSet myFindpicName=null;
  	        		 choose.setDB("new2");//set database name
  	        		 conn=choose.getConn();//connect to database
  	        		 
  	        		 int i=0;
  	        		 int myCount=0;//ͳ���ֻ��ͺŷǿգ�����ʱ��ǿյ���Ƭ��
  	        		 int FmodelTtime=0;//ͳ���ֻ��ͺ�Ϊ�գ�����ʱ��ǿյ���Ƭ��
  	        		 int TmodelFtime=0;//ͳ���ֻ��ͺŷǿգ�����ʱ��Ϊ�յ���Ƭ��
  	        		 int FmodelFtime=0;//ͳ���ֻ��ͺ�Ϊ�գ�����ʱ��Ϊ�յ���Ƭ��
  	        		 
  	        		 try{
  	        			while(i<list.length){//�����ļ����е�������Ƭ
  	        					String picName=list[i].getName();//�����Ƭname
  	        					System.out.println(picName);
  	    
  	        					//��ȡͼƬʱ����ֻ��ͺ�
  	        					String model=null;//��ȡͼƬ���ֻ��ͺ�
  	       	        		    String time=null;//��ȡ��Ƭ��ʱ��
  	       	        		    
  	       	        		    String location=null;//������Ƭ������ص�
  	       	        		    String l[]=list[i].getParent().split("\\\\");
							    int lg=l.length;
							    location=l[lg-1];//������������Ƭ����λ����Ϣ
							    System.out.println("location: "+location);
  	       	        		    
  	        					//��ȡ��Ƭ������ʱ����ֻ��ͺ�
							    Metadata metadata = ImageMetadataReader.readMetadata(list[i]);
                             	for (Directory directory : metadata.getDirectories()) {
                             		for (Tag tag : directory.getTags()) {
                             			String tagName = tag.getTagName();
        					            String desc = tag.getDescription();
        					            if(tagName.equals("Date/Time Original")){
        					                time=desc;//������Ƭ����ʱ��
        					                System.out.println("time:"+time);//���Կ��������Ƭʱ��
        					            } 
        					            else if(tagName.equals("Model")) {
        					                 model=desc;//������Ƭ�ֻ��ͺ�
        					                 System.out.println("model:"+model);//���Կ��������Ƭ�ֻ��ͺ�
        					            }
                             		}//End inner for loop
                             	}//End outer for loop
                             	
                             	if(model!=null){//�����Ƭ�ֻ��ͺŷǿ�
                             		if(time!=null){//�����Ƭ����ʱ��ǿ�
                             			//��DB�в��ұ�����Ƭ����ȡ��ƬID��typeֵ
                                     	
                             			
                             			String myNewSQL="select  * from picdata where name='"+picName+"' and type!=100 and model='"+model+"' and time='"+time+"';";
                                     	myNewRS=choose.executeSQL(myNewSQL);
                                     	if(myNewRS.next()){
                                     		myCount++;
                                     		String oldFilePath=list[i].getAbsolutePath();//get pic path
                                     		String newFilePath="C:\\Users\\apple\\Desktop\\��һ��ѧ��\\LAB\\NewPictureForJan";
                                     		//String newFilePath="C:\\Users\\apple\\Desktop\\��һ��ѧ��\\LAB\\11-12New_Ѧҫ��\\After classification\\12��";
                                     		int myID=myNewRS.getInt("id");
                                         	int type=myNewRS.getInt("type");//���ز�ѯ��tuple��type��ֵ
                                         	newFilePath+="\\"+type+"\\"+myID+".jpg";//���¶����µ���Ƭ·������Ƭname
                                         	
                                         	FileCopy(oldFilePath,newFilePath);//copy into new path
                                     	}	
                                     	else{
                                   	  		System.out.println("There is no picture infor for "+picName+" or Type=100 in the database!");
                                     	}
                                     	
                             		}//End if(��Ƭ����ʱ��ǿ�) condition
                             		else{//�����Ƭ����ʱ��Ϊ��
                             			
                             			System.out.println("Please assign picture's upload time as its time!");
                             			TmodelFtime++;
                             			
                             		}
                             	}//End if(��Ƭ�ֻ��ͺŷǿ�) condition
                             	else{//�����Ƭ�ֻ��ͺ�Ϊ��
                             		if(time!=null){//�����Ƭ����ʱ��ǿ�
                             			//��DB�в��ұ�����Ƭ����ȡ��ƬID��typeֵ,ʡȥ��modelԼ������
                                     	
                             			/*
                             			String myNewSQL="select  * from picdata where name='"+picName+"' and type!=100 and time='"+time+"';";
                                     	myResult=choose.executeSQL(myNewSQL);
                                     	if(myResult.next()){
                                     		FmodelTtime++;
                                     		String oldFilePath=list[i].getAbsolutePath();//get pic path
                                     		String newFilePath="C:\\Users\\apple\\Desktop\\��һ��ѧ��\\LAB\\NewPictureForDec";
                                     		int myID=myResult.getInt("id");
                                         	int type=myResult.getInt("type");//���ز�ѯ��tuple��type��ֵ
                                         	newFilePath+="\\"+type+"\\"+myID+".jpg";//���¶����µ���Ƭ·������Ƭname
                                         	
                                         	FileCopy(oldFilePath,newFilePath);//copy into new path
                                     	}	
                                     	else{
                                   	  		System.out.println("There is no picture infor for "+picName+" or Type=100 in the database!");
                                     	}
                                     	*/
                                     	
                             		}//End if(��Ƭ����ʱ��ǿ�) condition
                             		else{//�����Ƭ����ʱ��Ϊ��
                             			System.out.println("Please assign picture's upload time as its time!");
                             			
                             			
                             			String[] mysplit=picName.split("\\.");
                             			System.out.println(mysplit[0]);
                             			String newName=mysplit[0];
                             			
                             			String oldFilePath=list[i].getAbsolutePath();//get pic path
                             			String newFilePath="C:\\Users\\apple\\Desktop\\��һ��ѧ��\\LAB\\��ƬTimeModelFailed\\1��";
                             			newFilePath+="\\"+location+"\\"+newName+".jpg";//���¶��帴�Ƶ�Ŀ��·��
                             			
                             			FileCopy(oldFilePath,newFilePath);//copy into new path
                             			
                             			//��DB��ͨ��name��ѯ
                             			String myJPEGname=newName+".jpeg";
                             			String myFindNamesql="select * from picdata where name='"+myJPEGname+"' and type!=100";
                             			myFindpicName=choose.executeSQL(myFindNamesql);
                             			if(myFindpicName.next()){
                             				FmodelFtime++;
                             				int myID=myFindpicName.getInt("id");
                                         	int type=myFindpicName.getInt("type");//���ز�ѯ��tuple��type��ֵ
                                         	String mySecondPath="C:\\Users\\apple\\Desktop\\��һ��ѧ��\\LAB\\11-12��Time��Ƭ\\1��";
                                         	mySecondPath+="\\"+type+"\\"+myID+".jpg";//���¶����µ���Ƭ·������Ƭname
                                         	
                                         	FileCopy(oldFilePath,mySecondPath);//copy into new path
                             			}
                             			else{
                             				System.out.println("There is no picture infor for "+myJPEGname+" or Type=100 in the database!");
                             			}
                             			
                             			
                             		}
                             	}//End else condition
                             	
                             	i++;
  	        			}//End While Loop
  	        			
  	        			//��GUI����ʾ���ƽ��
  	        			TextField consoleInfo=new TextField();
     	                consoleInfo.setText("����"+dir+"��ϣ�ͼƬ������ͼƬ"+list.length+"�ţ����ƴ���ͼƬ"+myCount+"�š�");
     	                content.add(consoleInfo);
     	                
     	                System.out.println();
     	                System.out.println("��Ƭmodel�ǿգ�����ʱ��ǿ� ��Ƭ����"+myCount);
     	                System.out.println("��Ƭmodel�ǿգ�����ʱ��Ϊ�� ��Ƭ����"+TmodelFtime);
     	                System.out.println("��ƬmodelΪ�գ�����ʱ��ǿ� ��Ƭ����"+FmodelTtime);
     	                System.out.println("��ƬmodelΪ�գ�����ʱ��Ϊ�� ��Ƭ����"+FmodelFtime);
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
