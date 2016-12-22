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

	databean localBean;//创建默认的Local数据库
	databean serverBean;//创建Server端DB连接
	Connection localConn=null;
	Connection serverConn=null;
	
	public sensorPictureInsertDB(){
		//创建Local数据库连接对象
	localBean=new databean();
	localBean.setDB("new2");//set database name
	localConn=localBean.getConn();//connect to database
	
	//创建Server数据库连接的对象
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
        inputStream.read(b);       //一次性读入  
        outputStream.write(b);   //一次性写入    
    }
	
	
	
	/** 
     * 经纬度格式  转换为  度分秒格式 ,如果需要的话可以调用该方法进行转换
     * @param point 坐标点 
     * @return 
     */
	public String pointToLatlong (String point ) {  
		if(point == null)
			return "";
        Double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());  
        Double fen = Double.parseDouble(point.substring(point.indexOf("°")+1, point.indexOf("'")).trim());  
        Double miao = Double.parseDouble(point.substring(point.indexOf("'")+1, point.indexOf("\"")).trim());  
        Double duStr = du + fen / 60 + miao / 60 / 60 ;  
        return duStr.toString();  
    }
	
	public void insertDB(){
		String myfilepath="D:/传感器照片";//打开文件夹的初始位置
		final JFileChooser fileChooser = new JFileChooser(myfilepath);
		JFrame frame=new JFrame("打开文件夹");
		final JTextArea picDir=new JTextArea();
		
		JButton button=new JButton("选择文件夹");
		
		final Container content = frame.getContentPane(); 
		MouseListener ml = new MouseListener() {  
	         public void mouseClicked(MouseEvent e) {  
	             if (e.getClickCount() == 1) {  
	            	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            	fileChooser.setDialogTitle("打开文件夹");
	            	int ret = fileChooser.showOpenDialog(null);
	            	if(ret == JFileChooser.APPROVE_OPTION){
	            		//文件夹路径
	            		String absolutepath=fileChooser.getSelectedFile().getAbsolutePath();
	            		System.out.println(absolutepath);
	            		picDir.setText(absolutepath);
	            	}
	                String dir= picDir.getText();
	                File d=new File(dir);
	        		File list[] = d.listFiles();//file list在路径下所有的文件
	        		
	        		
	        		ResultSet rs=null;
                   
					//String location=null;//拍摄站点位置
					int piccount=list.length;//统计文件夹中所有的照片数
					int addcount=0;//统计更新的照片数
                   
					String model=null;//保存传感器型号
					for(int i = 0; i < list.length; i++){//遍历每一张照片
						String id=null;
	        			//初始化照片信息
	                    String time= null;//保存拍摄照片时间
	                    String latitude=null;
	                    String longitude=null;
	                    String height=null;
	                    //String dpi=null;//保存拍摄照片分辨率
	        			String path=list[i].getAbsolutePath();
	        			path=path.replaceAll("\\\\", "\\\\\\\\");//replace the "\\" to "\\\\" so that can be insert into database in "\"
	        			String name=list[i].getName();//保存拍摄照片原始照片名称
	                   
	        			try {
							Metadata metadata = ImageMetadataReader.readMetadata(list[i]);//create a Metadata class to read the info of picture
							Iterable<Directory> dds = metadata.getDirectories();
							for (Directory directory : dds) {
								
					            for (Tag tag : directory.getTags()) {
					                String tagName = tag.getTagName();
					                String desc = tag.getDescription();
					                if (tagName.equals("Date/Time Original")) {
					                	if(desc == null)//如果为空
					                		time="null";
					                	else
					                		time=desc;//拍摄时间
					                    //System.out.println(time+" read time info!");   
					                }else if(tagName.equals("GPS Latitude")){
					                	if(desc==null)//如果为空
					                		latitude="null";
					                	else
					                		latitude=desc;//纬度
					                }else if(tagName.equals("GPS Longitude")){
					                	if(desc==null)//如果为空
					                		longitude="null";
					                	else
					                		longitude=desc;//经度
					                }
					            }//End inner for loop
							}//End outer for loop
							
							String l[]=list[i].getParent().split("\\\\");
							int lg=l.length;
							model=l[lg-1];//保存了拍摄照片地理位置信息
										
							//输出照片拍摄信息
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
							
							/*---------------------更新操作----------------*/
							
							//查看照片信息在DB中是否已存在
							String lastdata="select * from sensor where name='"+name+"' and time='"+time+"';";
							ResultSet latest=localBean.executeSQL(lastdata);
							if(!latest.next()){//如果DB中没有照片信息，则插入照片信息
									String sql="insert into sensor values(null,'"+time+"','"+model+"','"+name+"',null,null,null,'"+pointToLatlong(longitude)+"','"+pointToLatlong(latitude)+"');";
									int r=localBean.executeUpdateSQL(sql);
									System.out.println("SQL Result:"+r);
									addcount++;//统计更新的照片数	
							}else{
								System.out.println("Exist in DB!!");
							}
							/*------------------复制操作-------------------*/
							/***
							 * 先按照time,model,name进行查询ID
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
							String newFilePath="D:/传感器照片/复制/"+model+"/"+id+".jpg";
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
	                
	        		//在GUI中显示结果
	        		int sum=addcount;	
	        		TextField consoleInfo=new TextField();
	                consoleInfo.setText(model+"处理完毕！图片集共有图片"+piccount+"张，新增图片"+sum+"张。");
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
				String myId=mystring;//第一个变量
				String myKey=in.next();//第二个变量
				String myValue=in.next();//第三个变量
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
