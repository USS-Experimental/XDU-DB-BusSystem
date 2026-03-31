package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.CallableStatement;


public class DBTest {
    //创建数据库连接。
    public static Connection GetConnection(String username, String passwd) {
        String driver = "com.huawei.gauss200.jdbc.Driver";
        String sourceURL = "jdbc:gaussdb://10.168.59.129:26000/dbclass";
        Connection conn = null;
        try {
            //加载数据库驱动。
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            //创建数据库连接。
            conn = DriverManager.getConnection(sourceURL, username, passwd);
            System.out.println("Connection succeed!");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return conn;
    };
    //执行普通SQL语句，创建customer_t1表。
    public static void CreateTable(Connection conn) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            //执行普通SQL语句。
            ResultSet rc = stmt.executeQuery("SELECT * FROM team;");
            System.out.println("succeed!");
            //print
            while (rc.next()) {
                System.out.println(rc.getInt(1) + "/t" + rc.getString(2));
            }
            stmt.close();
        } catch (SQLException e) {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
    //执行预处理语句，批量插入数据。
    public static void BatchInsertData(Connection conn) {
        PreparedStatement pst = null;
        try {
            //生成预处理语句。
            pst = conn.prepareStatement("INSERT INTO customer_t1 VALUES (?,?)");
            for (int i = 0; i < 3; i++) {
                //添加参数。
                pst.setInt(1, i);
                pst.setString(2, "data " + i);
                pst.addBatch();
            }
            //执行批处理。
            pst.executeBatch();
            pst.close();
        } catch (SQLException e) {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
    /**
     * 主程序，逐步调用各静态方法。
     * @param args
     */
    public static void main(String[] args) {
        //创建数据库连接。
        Connection conn = GetConnection("s18030100176", "stu_18030100176");
        //创建表。
        //CreateTable(conn);
        //批插数据。
        //BatchInsertData(conn);
        //关闭数据库连接。
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
