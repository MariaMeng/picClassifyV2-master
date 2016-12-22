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
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import picClassifyV2.databean;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class sensorPictureInsertDB {

	databean localBean;//����Ĭ�ϵ�Local���ݿ�
	databean serverBean;//����Server��DB����
	Connection localConn=null;
	Connection serverConn=null;
	
	public sensorPictureInsertDB(){
		//����Local���ݿ����Ӷ���
	localBean=new databean();
	localBean.setDB("new2");//set database name
	localConn=localBean.getConn();//connect to database
	
	//����Server���ݿ����ӵĶ���
	serverBean=new databean("222.128.13.159","64000","pm25","pm25","123456");
	serverConn=serverBean.getConn();
	}
	
	public void FileCopy(String inputPath,String outputPath) throws IOException{
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
	
	
	
	/** 
     * ��γ�ȸ�ʽ  ת��Ϊ  �ȷ����ʽ ,�����Ҫ�Ļ����Ե��ø÷�������ת��
     * @param point ����� 
     * @return 
     */
	public String pointToLatlong (String point ) {  
		if(point == null)
			return "";
        Double du = Double.parseDouble(point.substring(0, point.indexOf("��")).trim());  
        Double fen = Double.parseDouble(point.substring(point.indexOf("��")+1, point.indexOf("'")).trim());  
        Double miao = Double.parseDouble(point.substring(point.indexOf("'")+1, point.indexOf("\"")).trim());  
        Double duStr = du + fen / 60 + miao / 60 / 60 ;  
        return duStr.toString();  
    }
	
	public void insertDB(){
		String myfilepath="D:/��������Ƭ";//���ļ��еĳ�ʼλ��
		final JFileChooser fileChooser = new JFileChooser(myfilepath);
		JFrame frame=new JFrame("���ļ���");
		final JTextArea picDir=new JTextArea();
		
		JButton button=new JButton("ѡ���ļ���");
		
		final Container content = frame.getContentPane(); 
		MouseListener ml = new MouseListener() {  
	         public void mouseClicked(MouseEvent e) {  
	             if (e.getClickCount() == 1) {  
	            	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            	fileChooser.setDialogTitle("���ļ���");
	            	int ret = fileChooser.showOpenDialog(null);
	            	if(ret == JFileChooser.APPROVE_OPTION){
	            		//�ļ���·��
	            		String absolutepath=fileChooser.getSelectedFile().getAbsolutePath();
	            		System.out.println(absolutepath);
	            		picDir.setText(absolutepath);
	            	}
	                String dir= picDir.getText();
	                File d=new File(dir);
	        		File list[] = d.listFiles();//file list��·�������е��ļ�
	        		
	        		
	        		ResultSet rs=null;
                   
					//String location=null;//����վ��λ��
					int piccount=list.length;//ͳ���ļ��������е���Ƭ��
					int addcount=0;//ͳ�Ƹ��µ���Ƭ��
                   
					String model=null;//���洫�����ͺ�
					for(int i = 0; i < list.length; i++){//����ÿһ����Ƭ
						String id=null;
	        			//��ʼ����Ƭ��Ϣ
	                    String time= null;//����������Ƭʱ��
	                    String latitude=null;
	                    String longitude=null;
	                    String height=null;
	                    //String dpi=null;//����������Ƭ�ֱ���
	        			String path=list[i].getAbsolutePath();
	        			path=path.replaceAll("\\\\", "\\\\\\\\");//replace the "\\" to "\\\\" so that can be insert into database in "\"
	        			String name=list[i].getName();//����������Ƭԭʼ��Ƭ����
	                   
	        			try {
							Metadata metadata = ImageMetadataReader.readMetadata(list[i]);//create a Metadata class to read the info of picture
							Iterable<Directory> dds = metadata.getDirectories();
							for (Directory directory : dds) {
								
					            for (Tag tag : directory.getTags()) {
					                String tagName = tag.getTagName();
					                String desc = tag.getDescription();
					                if (tagName.equals("Date/Time Original")) {
					                	if(desc == null)//���Ϊ��
					                		time="null";
					                	else
					                		time=desc;//����ʱ��
					                    //System.out.println(time+" read time info!");   
					                }else if(tagName.equals("GPS Latitude")){
					                	if(desc==null)//���Ϊ��
					                		latitude="null";
					                	else
					                		latitude=desc;//γ��
					                }else if(tagName.equals("GPS Longitude")){
					                	if(desc==null)//���Ϊ��
					                		longitude="null";
					                	else
					                		longitude=desc;//����
					                }
					            }//End inner for loop
							}//End outer for loop
							
							String l[]=list[i].getParent().split("\\\\");
							int lg=l.length;
							model=l[lg-1];//������������Ƭ����λ����Ϣ
										
							//�����Ƭ������Ϣ
							System.out.println();
							System.out.println("Pic Infor can be seen as follows:");
							System.out.println("time: "+time);
							//System.out.println("dpi: "+dpi);
							System.out.println("model: "+model);
							//System.out.println("location: "+location);	
							System.out.println("path: "+path);
							System.out.println("name:"+name);
							System.out.println("GPS Latitude:"+latitude);
							System.out.println("GPS Latitude:"+pointToLatlong(latitude));
							System.out.println("GPS Longitude:"+longitude);
							System.out.println("GPS Latitude:"+pointToLatlong(longitude));
							
							//System.out.println();
							
							/*---------------------���²���----------------*/
							
							//�鿴��Ƭ��Ϣ��DB���Ƿ��Ѵ���
							String lastdata="select * from sensor where name='"+name+"' and time='"+time+"';";
							ResultSet latest=localBean.executeSQL(lastdata);
							if(!latest.next()){//���DB��û����Ƭ��Ϣ���������Ƭ��Ϣ
									String sql="insert into sensor values(null,'"+time+"','"+model+"','"+name+"',null,null,null,'"+pointToLatlong(longitude)+"','"+pointToLatlong(latitude)+"');";
									int r=localBean.executeUpdateSQL(sql);
									System.out.println("SQL Result:"+r);
									addcount++;//ͳ�Ƹ��µ���Ƭ��	
							}else{
								System.out.println("Exist in DB!!");
							}
							/*------------------���Ʋ���-------------------*/
							/***
							 * �Ȱ���time,model,name���в�ѯID
							 */
							String checkSQL="select * from sensor where name='"+name+"' and model='"+model+"' and time='"+time+"';";
							ResultSet myResult=localBean.executeSQL(checkSQL);
							if(myResult.next()){
								id=myResult.getString("id");
								System.out.println("my ID is:"+id);
							}else{
								System.out.println("Not Find in DB!");
							}
							String oldFilePath=list[i].getAbsolutePath();//get pic path
							String newFilePath="D:/��������Ƭ/����/"+model+"/"+id+".jpg";
							FileCopy(oldFilePath,newFilePath);
								
						} catch (ImageProcessingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
	        			
	        			catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}   
	        		}//End for loop
	                
	        		//��GUI����ʾ���
	        		int sum=addcount;	
	        		TextField consoleInfo=new TextField();
	                consoleInfo.setText(model+"������ϣ�ͼƬ������ͼƬ"+piccount+"�ţ�����ͼƬ"+sum+"�š�");
	                content.add(consoleInfo);
	                
	                
	             }//End if condition for double clicks  
	         }//End for mouseClicked method  
	         public void mouseEntered(MouseEvent e) {}  
	         public void mouseExited(MouseEvent e) {}  
	         public void mousePressed(MouseEvent e) {}  
	         public void mouseReleased(MouseEvent e) {}  
	   }; 
		
	   button.addMouseListener(ml);
	   
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
	
	}//End InsertDB function
	
	public void labelling(){
		while(true){
			System.out.println("----------------------");
			Scanner in=new Scanner(System.in);
			String mystring;
			if((mystring=in.next()).equals("-1")){
				break;
			}else{
				//System.out.println(mystring);
				//System.out.println(in.next());
				String myId=mystring;//��һ������
				String myKey=in.next();//�ڶ�������
				String myValue=in.next();//����������
				if(myKey.equals("pm25")){
					String updateSQL="update sensor set pm25='"+myValue+"' where id='"+myId+"';";
					int r=localBean.executeUpdateSQL(updateSQL);
				}else if(myKey.equals("Tem")){
					String updateSQL="update sensor set temperature='"+myValue+"' where id='"+myId+"';";
					int r=localBean.executeUpdateSQL(updateSQL);
				}
				else if(myKey.equals("Hum")){
					String updateSQL="update sensor set humidity='"+myValue+"' where id='"+myId+"';";
					int r=localBean.executeUpdateSQL(updateSQL);
				}
				
			}
		}
		
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		sensorPictureInsertDB mySensor=new sensorPictureInsertDB();
		//mySensor.insertDB();
		mySensor.labelling();
	}

}
