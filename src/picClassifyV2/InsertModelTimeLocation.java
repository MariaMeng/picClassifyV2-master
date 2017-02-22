package picClassifyV2;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lenovo on 2017/2/22.
 */
public class InsertModelTimeLocation {
    databean localBean;//����Ĭ�ϵ�Local���ݿ�
    databean serverBean;//����Server��DB����
    Connection localConn=null;
    Connection serverConn=null;
    public InsertModelTimeLocation(){ // constructor
        //����Local���ݿ����Ӷ���
        localBean=new databean();
        localBean.setDB("new2");//set database name
        localConn=localBean.getConn();//connect to database

        //����Server���ݿ����ӵĶ���
        serverBean=new databean("222.128.13.159","64000","pm25","pm25","123456");
        serverConn=serverBean.getConn();
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
    public void openGUI() {
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

                            String l[]=list[i].getParent().split("\\\\"); //c��ָ�·���е�ÿ��Ŀ¼����
                            int lg=l.length;
                            model=l[lg-1];//������������Ƭ�ͺ�

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
                    consoleInfo.setText(model+"������ϣ���"+piccount+"�ţ�����"+sum+"�š�");
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
    }
    public static void main(String[] args) {
        InsertModelTimeLocation my = new InsertModelTimeLocation();
        my.openGUI();
    }
}
