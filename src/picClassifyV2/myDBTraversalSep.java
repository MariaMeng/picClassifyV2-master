package picClassifyV2;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class myDBTraversalSep {

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
		// TODO Auto-generated method stub
		final JFileChooser fileChooser = new JFileChooser(".");
		JFrame frame=new JFrame("PicFindSep");
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
 	        		 
 	        		 //���в�ѯ��������ѯ5-9������DB��ID
 	        		String myNewSQL="select * from picdata where time<='2015-09-30 24:00:00' and flag=1 order by id;";
                  	myNewRS=choose.executeSQL(myNewSQL);
                  	//����ѯ���������ArrayList��
                  	ArrayList<Integer> mySelectID =new ArrayList<Integer>();
                  	try {
						while(myNewRS.next()){
							mySelectID.add(myNewRS.getInt("id"));//Store result in the arrayList							
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                  	 
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
	        				
	        				if(mySelectID.contains(myId)){//�������Ƭ�ڲ�ѯ����У����Ƶ��µ��ļ�����
	        					String oldFilePath=list[i].getAbsolutePath();//get absolute path of the original pic
                         		String newFilePath="D:\\SeptemberPictures\\Test";//Store the result in D
                         		newFilePath+="\\"+myId+".jpg";//���¶����µ���Ƭ·������Ƭname
                         		FileCopy(oldFilePath,newFilePath);//copy into new path
                         		myCount++;//Store the occurrence of the satisfied pictures
	        				}
	        				
	        				i++;
 	        			}//End While loop
 	        			
 	        			TextField consoleInfo=new TextField();
 	        			consoleInfo.setText("��"+myCount+"��¼����Ҫ��");
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
	}

}
