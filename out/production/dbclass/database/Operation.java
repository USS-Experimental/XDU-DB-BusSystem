package database;

import com.huawei.shade.org.joda.time.DateTime;

import java.sql.*;


public class Operation {
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
    }

    //查询某个车队下司机的基本信息
    public static ResultSet GetDriverInfo(Connection conn, int team_index) {
        Statement stmt = null;
        ResultSet rc = null;
        try {
            stmt = conn.createStatement();
            //执行查询SQL语句。
            rc = stmt.executeQuery(
                    "SELECT * FROM driver_info WHERE driver_info.team_index=" + team_index + ";"
            );
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
        return rc;
    }
    //查询某名司机在某个时间段的违章详细信息
    public static ResultSet GetDriverViolation(Connection conn, int job_index,
                                          String begin_time, String end_time) {
        PreparedStatement stmt = null;
        ResultSet rc = null;
        try {
            String sql = "SELECT * FROM stat_driver_violation WHERE driver_index=? and " +
                        "dates>=? and dates<=?;";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, job_index);
            stmt.setString(2, begin_time);
            stmt.setString(3, end_time);
            //执行查询SQL语句。
            rc = stmt.executeQuery();
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
        return rc;
    }
    //查询某个车队在某个时间段的违章统计信息
    public static ResultSet GetTeamViolation(Connection conn, int team_index,
                                        String begin_time, String end_time) {
        PreparedStatement stmt = null;
        ResultSet rc = null;
        try {
            //执行查询SQL语句。
            String sql = "SELECT violation_name, COUNT(violation_name) " +
                    "FROM stat_team_violation " +
                    "WHERE team_index=? and dates>=? and dates<=? " +
                    "GROUP BY team_index, violation_name;";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, team_index);
            stmt.setString(2, begin_time);
            stmt.setString(3, end_time);
            //执行查询SQL语句。
            rc = stmt.executeQuery();
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
        return rc;
    }
    //录入司机基本信息
    public static boolean EnterDriverInfo(Connection conn, String id, String phone,
                                       int index, String name, String sex, int age,
                                       String native_place, Date entry_date,
                                       String job, int line) {
        PreparedStatement pst = null;
        boolean ok = false;
        try {
            // 生成预处理语句。
            pst = conn.prepareStatement("INSERT INTO Tmember VALUES (?,?,?,?,?,?,?,?,?)");
            // 插入数据
            pst.setInt(1, index);
            pst.setString(2, name);
            pst.setString(3, sex);
            pst.setInt(4, age);
            pst.setString(5, native_place);
            pst.setDate(6, entry_date);
            pst.setString(7, job);
            pst.setString(8, phone);
            pst.setString(9, id);
            pst.executeUpdate();

            if(!job.equals("队长") && line != 0) {
                pst = conn.prepareStatement("INSERT INTO mem_line VALUES (?,?)");
                pst.setInt(1, index);
                pst.setInt(2, line);
                pst.executeUpdate();
            }

            pst.close();
            ok = true;
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
        return ok;
    }
    //录入汽车基本信息
    public static boolean EnterBusInfo(Connection conn, String license, String brand,
                                    int tot_seat, int bus_age, int line_index) {
        PreparedStatement pst = null;
        boolean ok = false;
        try {
            // 生成预处理语句。
            pst = conn.prepareStatement("INSERT INTO Bus VALUES (?,?,?,?)");
            // 插入数据
            pst.setString(1, license);
            pst.setString(2, brand);
            pst.setInt(3, tot_seat);
            pst.setInt(4, bus_age);
            pst.executeUpdate();

            if(line_index!=0) {
                pst = conn.prepareStatement("INSERT INTO bus_line VALUES (?,?)");
                pst.setString(1, license);
                pst.setInt(2, line_index);
                pst.executeUpdate();
            }

            pst.close();
            ok = true;
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
        return ok;
    }
    //录入司机的违章信息
    public static int EnterViolationInfo(Connection conn, int recoder_index, int vindex,
                                             int driver_index, String license,
                                             String date, String location, String vio) {
        // 首先检查录入者是否有权限（是否为队长或路队长）
        Statement stmt = null;
        ResultSet rc = null;
        PreparedStatement pst = null;
        int ok = 2;
        try {
            stmt = conn.createStatement();
            //执行查询SQL语句。
            System.out.println(recoder_index);
            rc = stmt.executeQuery(
                    "SELECT Sjob FROM Tmember WHERE job_index=" + recoder_index + ";"
            );
            rc.next();
            String curr_job = rc.getString(1);
            stmt.close();
            System.out.println(curr_job);
            System.out.println(curr_job.equals("司机"));
            if(curr_job.equals("司机")) {
                System.out.println(curr_job);
                return 0;  //没有权限
            }
            // 生成预处理语句。
            pst = conn.prepareStatement("INSERT INTO Violation_records VALUES (?,?,?,?,?,?,?)");
            // 插入数据
            pst.setInt(1, vindex);
            pst.setInt(2, driver_index);
            pst.setString(3, license);
            pst.setString(4, date);
            pst.setString(5, location);
            pst.setString(6, vio);
            pst.setInt(7, recoder_index);
            pst.executeUpdate();

            pst.close();
            ok = 1;
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
        return ok;
    }
    /**
     * 主程序，逐步调用各静态方法。
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
