package picClassifyV2;
import java.sql.ResultSet;
import picClassifyV2.databean;

public class AddNewPath {
	public static void main(String[] args) {
		databean myDB=new databean();
 		ResultSet myNewRS=null;
 		myDB.setDB("new2");//set database name
 		myDB.getConn();//connect to database
 		try{
 			String myNewSQLQuery="select  * from picdata where time>'2015-12-31 24:00:00' and time<='2016-01-31 24:00:00' and type is not null and type!=100;";
 			myNewRS=myDB.executeSQL(myNewSQLQuery);
 			while(myNewRS.next()){
 				int myID=myNewRS.getInt("id");
 				//System.out.print("MY ID IS: "+myID);
 				int myType=myNewRS.getInt("type");
 				//System.out.println(" MY TYPE IS: "+myType);
 				String myNewPath=myType+"/"+myID+".jpg";
 				String myNewSQLUpdate="update picdata set NewPath='"+myNewPath+"' where id="+myID+";";
 				myDB.executeUpdateSQL(myNewSQLUpdate); 
 			}
 		}catch(Exception myException){
 			myException.printStackTrace();
 		}
 		System.out.println("Finish!!!!!!!!!!!!");
	}
}
