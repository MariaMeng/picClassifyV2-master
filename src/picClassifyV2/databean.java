package picClassifyV2;
import java.sql.*;
public class databean {
	//连接数据库的Java Bean文件名databean.java
	
	//declare variable
	private Connection conn = null;
	ResultSet rs = null;
	private String server = "localhost";
	private String port = "3309";
	private String db = "newmaria1";
	private String user = "root";
	private String pass = "1111";
	private String drivername="com.mysql.jdbc.Driver";
	private String URL="jdbc:mysql://"+server+":"+port+"/"+db+"?user="+user+"&password="+pass;

	public databean() {
	}
	public databean(String server,String port,String db,String user,String pass){
		URL="jdbc:mysql://"+server+":"+port+"/"+db+"?user="+user+"&password="+pass+"&useUnicode=true&characterEncoding=gb2312";
	}
	
	
	public Connection getConn(){//get database connection
		try{
			Class.forName(drivername).newInstance();
			conn = DriverManager.getConnection(URL);
			System.out.println("DB CONNECTION SUCCESSFULLY!!!!!!!!!!!!!!!!!!!!!!!!!");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Failure!!!!!!!!!!!!!!!");
		}
		return this.conn ;
	}

	public void setServer(String str) {//set server name
		server=str;
	}

	public void setPort(String str) {//set server port
		port = str;
	}

	public void setDB(String str) {//set db name
		URL = "jdbc:mysql://"+server+":"+port+"/"+str+"?user="+user+"&password="+pass;	
	}

	public void setUser(String str) {//set user name
		user = str;
	}

	public void setPass(String str) {//set user name
		pass = str;
	}

	public ResultSet executeSQL(String str) {
		try{
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery(str);
		}catch(Exception e){
			e.printStackTrace();
		}
		return this.rs;
	}
	
	public int executeUpdateSQL(String str) {
		int r=0;
		try{
			Statement stmt = conn.createStatement();
			r = stmt.executeUpdate(str);
		}catch(Exception e){
			e.printStackTrace();
		}
		return r;
	}
}
