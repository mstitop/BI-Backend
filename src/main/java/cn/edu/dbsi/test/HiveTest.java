package cn.edu.dbsi.test;

import cn.edu.dbsi.dataetl.util.JobConfig;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;


public class HiveTest {

	@Autowired
	private JobConfig jobConfig;
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		/*
		String sql ="select * from kylin_flat_db.dim_location";
		
		Connection con=null;
		Statement stmt=null;
		Map<String, Object> map  =  new HashMap<String, Object>();
	
		try{
			con= HiveConnection.getConnection();
			

			stmt = con.createStatement();


			//stmt.executeUpdate( "use kylin_flat_db");

			ResultSet res = stmt.executeQuery( "DESC bi_1.fact_game");
			while (res.next()) {
				System.out.print(res.getString(1)+"    ");
				System.out.println(res.getString(2) + "    ");
			}

			stmt.close();
			con.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		*/


	}

}
