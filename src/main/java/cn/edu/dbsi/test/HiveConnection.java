package cn.edu.dbsi.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class HiveConnection {
	private static final String DRIVER_CLASS="org.apache.hive.jdbc.HiveDriver";
	private static final String DATABASE_URL="jdbc:hive2://10.1.18.210:10000";

	
	public static Connection getConnection(){
		Connection dbConnection=null;
		try{
			Class.forName(DRIVER_CLASS);
			dbConnection = DriverManager.getConnection(DATABASE_URL,"hive","hive");
		}catch(Exception e){
			e.printStackTrace();
		}
		return dbConnection;
	}
	
	public static void executeSql(String sql) {
		Connection con=null;
		PreparedStatement pt=null;
	
	
		try{
			con=HiveConnection.getConnection();
			
			
			pt=con.prepareStatement(sql);
			pt.execute();
			
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
