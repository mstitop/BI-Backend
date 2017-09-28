package cn.edu.dbsi.util;

import cn.edu.dbsi.dataetl.util.JobConfig;
import cn.edu.dbsi.model.DbconnInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/26.
 */
public class DBUtils {


    /**
     * 此方法因为是用于测试链接，故不将异常打印出来，在其他情况建议将异常打印
     * @param dbconnInfo
     * @return
     */
    public static int testConn(DbconnInfo dbconnInfo){
        int tag = 0;
        try {
            Class.forName(dbconnInfo.getJdbcname());
            Connection conn = DriverManager.getConnection(dbconnInfo.getUrl(), dbconnInfo.getUsername(), dbconnInfo.getPassword());
            if(conn!=null){
                tag = 1;
                conn.close();
            }
        } catch (SQLException e) {
            //判断用户名和密码
            if(e.getMessage().contains("password"))
                return -1;
            else //URL链接错误或不允许接入
                return -2;
        }catch (ClassNotFoundException e){
            return 0;
        }
        return tag;
    }

    /**
     * 获取数据库连接
     *
     * @return Connection据库连接
     */
    public static Connection getConn(String DB_DRIVER, String DB_URL, String DB_ACCT, String DB_PWD) {
        try {
            Class.forName(DB_DRIVER);
            return DriverManager.getConnection(DB_URL, DB_ACCT, DB_PWD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行查询操作
     *
     * @param sql  查询SQL语句
     * @param args SQL语句 参数
     * @return 返回查询结果
     */
    public static Map<String, Object> query(DbconnInfo dbconnInfo, String sql, Object... args) {
        Connection conn = getConn(dbconnInfo.getJdbcname(), dbconnInfo.getUrl(), dbconnInfo.getUsername(), dbconnInfo.getPassword());// 获取数据连接
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = conn.prepareStatement(sql);
            // 设置参数
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null)
                    pst.setObject(i + 1, args[i]);
            }
            // 执行查询语句
            rs = pst.executeQuery();
            if (rs.next()) {
                return rsToMap(rs);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(conn, pst, rs);
        }
        return null;
    }

    /**
     * 执行查询操作
     *
     * @param sql  查询SQL语句
     * @param args SQL语句 参数
     * @return 返回查询结果
     */
    public static List<Map<String, Object>> list2(DbconnInfo dbconnInfo, String sql, Object... args) {
        if (dbconnInfo.getJdbcname() != null && !"".equals(dbconnInfo.getJdbcname()) && dbconnInfo.getUrl() != null && !"".equals(dbconnInfo.getUrl()) &&
                dbconnInfo.getUsername() != null && !"".equals(dbconnInfo.getUsername()) && dbconnInfo.getPassword() != null && !"".equals(dbconnInfo.getPassword())) {
            Connection conn = getConn(dbconnInfo.getJdbcname(), dbconnInfo.getUrl(), dbconnInfo.getUsername(), dbconnInfo.getPassword());// 获取数据连接
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                pst = conn.prepareStatement(sql);
                // 设置参数
                if (args != null) {
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] != null)
                            pst.setObject(i + 1, args[i]);
                    }
                }
                // 执行查询语句
                rs = pst.executeQuery();
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                while (rs.next()) {
                    Map<String, Object> map = rsToMap2(rs);
                    list.add(map);
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close(conn, pst, rs);
            }
            return null;
        } else {
            return null;
        }
    }

    public static List<Map<String, Object>> list(DbconnInfo dbconnInfo, String sql, Object... args) {
        if (dbconnInfo.getJdbcname() != null && !"".equals(dbconnInfo.getJdbcname()) && dbconnInfo.getUrl() != null && !"".equals(dbconnInfo.getUrl()) &&
                dbconnInfo.getUsername() != null && !"".equals(dbconnInfo.getUsername()) && dbconnInfo.getPassword() != null && !"".equals(dbconnInfo.getPassword())) {
            Connection conn = getConn(dbconnInfo.getJdbcname(), dbconnInfo.getUrl(), dbconnInfo.getUsername(), dbconnInfo.getPassword());// 获取数据连接
            PreparedStatement pst = null;
            ResultSet rs = null;
            try {
                pst = conn.prepareStatement(sql);
                // 设置参数
                if (args != null) {
                    for (int i = 0; i < args.length; i++) {
                        if (args[i] != null)
                            pst.setObject(i + 1, args[i]);
                    }
                }
                // 执行查询语句
                rs = pst.executeQuery();
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                while (rs.next()) {
                    Map<String, Object> map = rsToMap(rs);
                    list.add(map);
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close(conn, pst, rs);
            }
            return null;
        } else {
            return null;
        }
    }


    /**
     * 执行增删改操作
     *
     * @param sql  增删改SQL语句
     * @param args SQL语句 参数
     * @return 返回是否执行成功
     */
    public static boolean update(String DB_DRIVER, String DB_URL, String DB_ACCT, String DB_PWD, String sql, Object... args) {
        PreparedStatement pst = null;
        Connection conn = null;
        try {
            conn = getConn(DB_DRIVER, DB_URL, DB_ACCT, DB_PWD);// 获取数据库连接
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null)
                    pst.setObject(i + 1, args[i]);
            }
            int row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, pst, null);
        }
        return false;
    }

    /**
     * 执行建 HIVE 表语句
     * @param jobConfig
     * @param tableName
     * @param columns
     * @return
     */
    public static  int createHiveTable(JobConfig jobConfig, String tableName,Map<String,String> columns ) {
        Connection conn = getConn(jobConfig.getDriver(),jobConfig.getUrl(),jobConfig.getUsername(),jobConfig.getPassword());// 获取数据连接
        Statement stmt = null;

        String dropSql = "drop table " + tableName;
        StringBuffer createSql = new StringBuffer();
        createSql.append("create table " + tableName + " (");
        int i = 0;
        int count = columns.size();
        for(Map.Entry<String,String> entry: columns.entrySet()){
            i ++;
            createSql.append(entry.getKey());
            createSql.append(" ");
            createSql.append(entry.getValue());
            if(i < count){
                createSql.append(",");
            }
        }
        createSql.append(") row format delimited fields terminated by '"+ jobConfig.getFieldDelimiter() +"'");
        String createSqlFinal  = createSql.toString();


        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(dropSql);
            stmt.executeUpdate(createSqlFinal);

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
    /**
     * 执行建 HIVE 库语句
     * @param jobConfig
     * @param dbName
     * @return
     */
    public static  int createHiveDb(JobConfig jobConfig, String dbName ) {
        Connection conn = getConn(jobConfig.getDriver(),jobConfig.getUrl(),jobConfig.getUsername(),jobConfig.getPassword());// 获取数据连接
        Statement stmt = null;

        String sql = "CREATE DATABASE  "+ dbName;



        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);


            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
    /**
     * 查询相应 hive 数据库中的所有表的字段、类型
     * @param jobConfig
     * @param tableName
     * @return
     */
    public static  List<Map<String, Object>> listTables(JobConfig jobConfig, String tableName ) {
        Connection conn = getConn(jobConfig.getDriver(),jobConfig.getUrl(),jobConfig.getUsername(),jobConfig.getPassword());// 获取数据连接
        Statement stmt = null;

        String descTableSql = "desc " + tableName;

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            stmt = conn.createStatement();

            ResultSet res = stmt.executeQuery(descTableSql);
            while (res.next()) {
                Map<String, Object> map  =  new HashMap<String, Object>();
                map.put("name",res.getString(1).toUpperCase());
                map.put("type",res.getString(2).toLowerCase());
                list.add(map);
            }

            stmt.close();
            conn.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    /**
     * 关闭连接释放资源
     *
     * @param conn 数据库连接
     * @param st   Statement对象
     * @param rs   ResultSet 结果集
     */
    public static void close(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (st != null)
                st.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将结果集转换成Map
     *
     * @param rs 结果集
     * @return map
     * @throws SQLException
     */
    public static Map<String, Object> rsToMap2(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<String, Object>();
        // 获取结果集的元信息(列名，列类型，大小，列数量等等)
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();// 获取结果集中的列的数量
        for (int i = 1; i <= count; i++) {
            // 根据列的下标获取列名称
            // String columnName = rsmd.getColumnName(i);
            Object value = rs.getObject(i);
            map.put("name", value);
        }
        return map;
    }

    public static Map<String, Object> rsToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<String, Object>();
        // 获取结果集的元信息(列名，列类型，大小，列数量等等)
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();// 获取结果集中的列的数量
        for (int i = 1; i <= count; i++) {
            // 根据列的下标获取列名称
            String columnName = rsmd.getColumnLabel(i);
            Object value = rs.getObject(i);
            map.put(columnName, value);
        }
        return map;
    }
}
