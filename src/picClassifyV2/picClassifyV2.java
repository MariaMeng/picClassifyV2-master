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



public class picClassifyV2 {
	
	public static void cutCenterImage(String src,String dest,int w,int h) throws IOException{   
        Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");   
        ImageReader reader = (ImageReader)iterator.next();   
        InputStream in=new FileInputStream(src);  
        ImageInputStream iis = ImageIO.createImageInputStream(in);   
        reader.setInput(iis, true);   
        ImageReadParam param = reader.getDefaultReadParam();   
        int imageIndex = 0;   
        Rectangle rect = new Rectangle((reader.getWidth(imageIndex)-w)/2, (reader.getHeight(imageIndex)-h)/2, w, h);    
        param.setSourceRegion(rect);   
        BufferedImage bi = reader.read(0,param);     
        ImageIO.write(bi, "jpg", new File(dest));             
   
    }  
   /* 
    * 图片裁剪二分之一 
    */  
    public static void cutHalfImage(String src,String dest) throws IOException{   
        Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");   
        ImageReader reader = (ImageReader)iterator.next();   
        InputStream in=new FileInputStream(src);  
        ImageInputStream iis = ImageIO.createImageInputStream(in);   
        reader.setInput(iis, true);   
        ImageReadParam param = reader.getDefaultReadParam();   
        int imageIndex = 0;   
        int width = reader.getWidth(imageIndex)/2;   
        int height = reader.getHeight(imageIndex)/2;   
        Rectangle rect = new Rectangle(width/2, height/2, width, height);   
        param.setSourceRegion(rect);   
        BufferedImage bi = reader.read(0,param);     
        ImageIO.write(bi, "jpg", new File(dest));     
    }  
   /* 
    * 图片裁剪通用接口 
    */  
 
   public static void cutImage(String src,String dest,int x,int y,int w,int h) throws IOException{   
          Iterator iterator = ImageIO.getImageReadersByFormatName("jpg");   
          ImageReader reader = (ImageReader)iterator.next();   
          InputStream in=new FileInputStream(src);  
          ImageInputStream iis = ImageIO.createImageInputStream(in);   
          reader.setInput(iis, true);   
          ImageReadParam param = reader.getDefaultReadParam();   
          Rectangle rect = new Rectangle(x, y, w,h);    
          param.setSourceRegion(rect);   
          BufferedImage bi = reader.read(0,param);     
          ImageIO.write(bi, "jpg", new File(dest));             
 
   }   
   /* 
    * 图片缩放 
    */  
   public static void zoomImage(String src,String dest,int w,int h) throws Exception {  
       double wr=0,hr=0;  
       File srcFile = new File(src);  
       File destFile = new File(dest);  
       BufferedImage bufImg = ImageIO.read(srcFile);  
       Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);  
       wr=w*1.0/bufImg.getWidth();  
       hr=h*1.0 / bufImg.getHeight();  
       AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);  
       Itemp = ato.filter(bufImg, null);  
       try {  
           ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile);  
       } catch (Exception ex) {  
           ex.printStackTrace();  
       }  
         
   }  
	
	

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
//      inputStream.close();  
//      outputStream.close();  
    }  
    //边读边写  
    public void copy2() throws IOException{  
        int temp=0;  
        File inputFile;  
        File outputFile;  
        InputStream inputStream = null;  
        OutputStream outputStream = null;  
        while((temp=inputStream.read())!=-1){  
            outputStream.write(temp);  
        }  
        inputStream.close();  
        outputStream.close();  
    }  

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    final JFileChooser fileChooser = new JFileChooser(".");
		JFrame frame=new JFrame("PicClassifyNew");
		final JTextArea picDir=new JTextArea();
		JButton button=new JButton("选择文件夹");
 	    final Container content = frame.getContentPane(); 
 	    MouseListener ml=new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(e.getClickCount()==2){
					fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            	 fileChooser.setDialogTitle("打开文件夹");
	            	 int ret = fileChooser.showOpenDialog(null);
	            	 if (ret == JFileChooser.APPROVE_OPTION) {
	            		//文件夹路径
	            		 String absolutepath=fileChooser.getSelectedFile().getAbsolutePath();
	            		System.out.println(absolutepath);
	            		picDir.setText(absolutepath);
	            		}
	                String dir= picDir.getText();//用户输入的path
	                File d=new File(dir);
	        		File list[] = d.listFiles();//file list 在路径下的文件
                   databean choose=new databean();
                   Connection conn = null;
                   ResultSet rs=null;
                   choose.setDB("newMaria1");//set database name
                   conn=choose.getConn();//connect to database

                   
                   int i=0;
                   String model=null;
                   String time=null;
                   String dpi=null;
                   int height=0;
                   int width = 0;
                   File trainf = new File("e:\\pic\\train\\type.txt");
                   File testf = new File("e:\\pic\\test\\type.txt");
                   File countf=new File("e:\\pic\\count.txt");
                   try{
                   trainf.createNewFile();
                   testf.createNewFile();
                   countf.createNewFile();
                   BufferedReader inputcount = new BufferedReader(new FileReader(countf));//read f
                   //read the count
   				String countnum;
   		        String readcount=null;
   		        int finalcount=0;
   				if((countnum = inputcount.readLine())==null){    					    
                           readcount="0";
   				}
   				else{
   					readcount=countnum;
   					finalcount=Integer.valueOf(readcount);
   				}
   				inputcount.close();	
   				//iterate the pic
                	   while(i<list.length){
                		   String picName=list[i].getName();//get pic name
                           String sql="select  * from picdata where name='"+picName+"' and type!=100";                          
                           rs=choose.executeSQL(sql);
                           if(rs.next()){
                        	   
                		   String oldFilePath=list[i].getAbsolutePath();//get pic path            		   
                		   //String newFilePath="e:\\pic\\train\\";
                		   String newFilePath="C:\\Users\\apple\\Desktop\\研一上学期\\LAB\\pm25 2015-09-21";
                		   
                		   String newSkyPath="e:\\pic\\picSky\\";
                		   String newGroundPath="e:\\pic\\picGround\\";
                		   int type=rs.getInt(11);//返回查询的tuple中type的值
                		   String lable=finalcount+".jpg "+type+"\r\n";
                		   //deal with the path
                		       newFilePath+=type+"\\"+finalcount+".jpg";
                		       newSkyPath+=type+"\\"+finalcount+".jpg";
                		       newGroundPath+=type+"\\"+finalcount+".jpg";
                		     //create a Metadata class to read the info of picture
                		       Metadata metadata = ImageMetadataReader.readMetadata(list[i]);
   							for (Directory directory : metadata.getDirectories()) {
   					            for (Tag tag : directory.getTags()) {
   					                String tagName = tag.getTagName();
   					                String desc = tag.getDescription();
   					                if (tagName.equals("X Resolution")) {
   					                    //图片dpi
   					                    dpi=desc;
   					                } else if (tagName.equals("Date/Time Original")) {
   					                    //拍摄时间
   					                    time=desc;
   					                } else if (tagName.equals("Model")) {
   					                    //手机型号
   					                    model=desc;
   					                }
   					                else if (tagName.equals("Image Height")) {  
   					                    String h=desc;
   					                    String h1[]=h.split(" ");
   					                    height=Integer.valueOf(h1[0]);
   					                } else if (tagName.equals("Image Width")) {  
   					                    String w=desc;
   					                    String w1[]=w.split(" ");
   					                    width=Integer.valueOf(w1[0]);
   					                }
   					            }
   							}
   							   //copy pic file to the classify dir
                			   FileCopy(oldFilePath,newFilePath);
                			   //cut the ground of the pic to the sky dir
                			   cutImage(oldFilePath,newSkyPath,0,0,width,height/2);
                			   //cut the sky oof the pic to the ground dir
                			   cutImage(oldFilePath,newGroundPath,0,height/2,width,height); 
                			   //write the new lable to the txt file
                			   FileOutputStream newlable = new FileOutputStream(trainf, true);

                			   newlable.write(lable.getBytes());  
                		       newlable.close(); 

                		       
                		       finalcount++;
                		       System.out.println("finish dealing with"+picName);
                		   }//end if pic in the database
                           else{
                	  System.out.println("there is no pic"+picName+" info in the database!");
                	           
                               }
                           i++;
                           
               }//end while
                	   //reset the count;
        			   FileOutputStream newCount = new FileOutputStream(countf);
        			   String finalCount=String.valueOf(finalcount);
        		       newCount.write(finalCount.getBytes());
        		       //output the info of this iterate
                	   System.out.println("finish dealing with"+dir);
                       System.out.println("the result set  count is:"+i+"the list of rs is:"+list.length);
                       TextField consoleInfo=new TextField();
   	                   consoleInfo.setText("处理"+dir+"完毕！图片集共有图片"+list.length+"张，复制处理图片"+i+"张。");
   	                   content.add(consoleInfo);
                       // the upper  code copy the pic into a classify dir and cut the pic into two part (also classify)
                   }
                   catch(Exception e1){
                	   e1.printStackTrace();
                   }
                   
           	   }
                   
     
                   

                   
                   
                   
                   
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
