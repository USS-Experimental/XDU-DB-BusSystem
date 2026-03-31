package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.text.ParseException;

import com.huawei.shade.org.joda.time.DateTime;
import database.Operation;

public class Interface {
    private static Connection conn = null;
    private static Operation op = null;
    private static int rec_index = 0;

    public static void main(String[] args) {
        // sql操作 + 创建连接
        op = new Operation();
        conn = op.GetConnection("s18030100176", "stu_18030100176");
        // 创建 JFrame 实例
        JFrame frame = new JFrame("车队信息管理系统");
        // Setting the width and height of frame
        frame.setSize(600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("Button.font", new Font("幼圆", 0, 20));

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);
        // 添加面板
        frame.add(panel);
        // 设置界面可见
        frame.setVisible(true);
    }

    //添加标签
    private static JTextField addLabel(JPanel panel, String name) {
        // 输入信息
        JLabel lisLabel = new JLabel(name);
        lisLabel.setFont(new Font("幼圆", 0, 18));
        panel.add(lisLabel);
        JTextField lisText = new JTextField(10);
        return lisText;
    }

    private static void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 JLabel
        JLabel userLabel = new JLabel("工号：");
        userLabel.setFont(new Font("幼圆", 0, 18));
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        userLabel.setBounds(30,20,80,25);
        panel.add(userLabel);

        /*
         * 创建文本域用于用户输入
         */
        JTextField userText = new JTextField(20);
        userText.setBounds(120,20,265,30);
        panel.add(userText);

        // 输入密码的文本域
        JLabel passwordLabel = new JLabel("姓名：");
        passwordLabel.setFont(new Font("幼圆", 0, 18));
        passwordLabel.setBounds(30,50,80,25);
        panel.add(passwordLabel);

        /*
         *这个类似用于输入的文本域
         * 但是输入的信息会以点号代替，用于包含密码的安全性
         */
        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(120,50,265,30);
        panel.add(passwordText);

        // 创建确定按钮
        JButton loginButton = new JButton("确定");
        loginButton.setFont(new Font("幼圆", 0, 18));
        loginButton.setBounds(425, 30, 100, 40);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rec_index = Integer.parseInt(userText.getText());
                placeButtons(panel);
            }
        });
        panel.add(loginButton);
    }

    private static void placeButtons(JPanel panel) {
        // 输入职位的文本域
        JLabel jobLabel = new JLabel("选择你需要进行的操作：");
        jobLabel.setFont(new Font("幼圆", 0, 18));
        jobLabel.setBounds(30,100,300,25);
        panel.add(jobLabel);

        // 查询某个车队下司机的基本信息
        JButton queryDriverButton = usequeryDriverButton(panel, new JButton("查询某个车队下成员的基本信息"));
        queryDriverButton.setFont(new Font("幼圆", 0, 18));
        queryDriverButton.setBounds(70, 140, 450, 40);
        panel.add(queryDriverButton);

        // 查询某名司机在某个时间段的违章详细信息
        JButton queryDriverVioButton = usequeryDriverVioButton(panel, new JButton("查询某名司机在某个时间段的违章详细信息"));
        queryDriverVioButton.setFont(new Font("幼圆", 0, 18));
        queryDriverVioButton.setBounds(70, 180, 450, 40);
        panel.add(queryDriverVioButton);

        // 查询某个车队在某个时间段的违章统计信息
        JButton queryTeamVioButton = usequeryTeamVioButton(panel, new JButton("查询某个车队在某个时间段的违章统计信息"));
        queryTeamVioButton.setFont(new Font("幼圆", 0, 18));
        queryTeamVioButton.setBounds(70, 220, 450, 40);
        panel.add(queryTeamVioButton);

        // 录入司机基本信息
        JButton entryDriverButton = useEntryDriverButton(panel, new JButton("录入司机基本信息"));
        entryDriverButton.setFont(new Font("幼圆", 0, 18));
        entryDriverButton.setBounds(70, 260, 450, 40);
        panel.add(entryDriverButton);

        // 录入汽车基本信息
        JButton entryBusButton = useEntryBusButton(panel, new JButton("录入汽车基本信息"));
        entryBusButton.setFont(new Font("幼圆", 0, 18));
        entryBusButton.setBounds(70, 300, 450, 40);
        panel.add(entryBusButton);

        // 录入司机的违章信息
        JButton entryDriverVioButton = useEntryVioButton(panel, new JButton("录入司机违章信息"));
        entryDriverVioButton.setFont(new Font("幼圆", 0, 18));
        entryDriverVioButton.setBounds(70, 340, 450, 40);
        panel.add(entryDriverVioButton);
    }

    /***********  以下为执行各个数据库的查询操作  ************/

    private static JButton usequeryDriverButton(JPanel panel, JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("查询车队成员信息");
                frame.setSize(1200, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception es) {
                    es.printStackTrace();
                }
                UIManager.put("Button.font", new Font("幼圆", 0, 20));

                JPanel panel = new JPanel();
                frame.add(panel);

                // 输入信息
                JLabel teamLabel = new JLabel("车队号：");
                teamLabel.setFont(new Font("幼圆", 0, 18));
                panel.add(teamLabel);
                JTextField teamText = new JTextField(10);
                panel.add(teamText);

                JTextArea jta = new JTextArea(10,100);
                panel.add(jta);

                // 显示信息
                JButton useDisplayButton = new JButton("接收信息");
                useDisplayButton.setFont(new Font("幼圆", 0, 18));
                useDisplayButton.setBounds(70, 300, 450, 40);
                useDisplayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int team_index = Integer.parseInt(teamText.getText());
                        ResultSet rc = null;
                        try {
                            String hint = "该车队成员详细统计信息如下：\n";
                            jta.append(hint);
                            // 列名
                            String column = ("工号" + "\t" + "姓名" + "\t" + "性别" + "\t" + "年龄" + "\t"
                                    + "籍贯" + "\t" + "联系电话" + "\t" + "身份证号" + "\t\t" + "入职时间" + "\t"
                                    + "职务" + "\t" + "所属车队" + "\t" + "所属线路" + "\n");
                            jta.append(column);

                            rc = op.GetDriverInfo(conn, team_index);
                            while (rc.next()) {
                                String info = (rc.getInt(1) + "\t" + rc.getString(2) + "\t"  // 编号 姓名
                                        + rc.getString(3) + "\t" + rc.getInt(4) + "\t"  // 性别 年龄
                                        + rc.getString(5) + "\t" + rc.getString(6) + "\t"  // 籍贯 电话号码
                                        + rc.getString(7) + "\t" + rc.getDate(8) + "\t"  // 身份证号 入职日期
                                        + rc.getString(9) + "\t" + rc.getInt(10) + "\t" // 职务 车队号
                                        + rc.getInt(11) + "\n"); //线路号
                                jta.append(info);
                            }
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                panel.add(useDisplayButton);

                frame.setVisible(true);
            }
        });
        return btn;
    }

    private static JButton usequeryDriverVioButton(JPanel panel, JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("查询司机违章信息");
                frame.setSize(850, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception es) {
                    es.printStackTrace();
                }
                UIManager.put("Button.font", new Font("幼圆", 0, 20));

                JPanel panel = new JPanel();
                frame.add(panel);

                // 输入信息
                JTextField indexText = addLabel(panel, "工号"); panel.add(indexText);
                JTextField beginText = addLabel(panel, "开始时间"); panel.add(beginText);
                JTextField endText = addLabel(panel, "结束时间"); panel.add(endText);

                JTextArea jta = new JTextArea(10,70);
                panel.add(jta);

                // 显示信息
                JButton useDisplayButton = new JButton("接收信息");
                useDisplayButton.setFont(new Font("幼圆", 0, 18));
                useDisplayButton.setBounds(70, 300, 450, 40);
                useDisplayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int index = Integer.parseInt(indexText.getText());
                        //Date beg = Date.valueOf(beginText.getText());
                        //Date end = Date.valueOf(endText.getText());
                        String beg = beginText.getText();
                        String end = endText.getText();
                        ResultSet rc = null;
                        try {
                            // 列名
                            String hint = "该司机在该时间段下违章详细信息如下：\n";
                            jta.append(hint);
                            String column = ("工号" + "\t" + "姓名" + "\t" + "车牌号" + "\t" + "时间" + "\t" + "事发地"
                                    + "\t" + "违章" + "\t" + "惩罚" + "\t\t" + "记录人" +"\n");
                            jta.append(column);

                            rc = op.GetDriverViolation(conn, index, beg, end);
                            while (rc.next()) {
                                String info = (rc.getInt(1) + "\t" + rc.getString(2) + "\t"
                                        + rc.getString(3) + "\t" + rc.getDate(4) + "\t"
                                        + rc.getString(6) + "\t" + rc.getString(5) + "\t"
                                        + rc.getString(7) + "\t" + rc.getInt(8) +"\n");
                                jta.append(info);
                            }
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                panel.add(useDisplayButton);

                frame.setVisible(true);
            }
        });
        return btn;
    }

    private static JButton usequeryTeamVioButton(JPanel panel, JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("查询车队违章统计信息");
                frame.setSize(750, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception es) {
                    es.printStackTrace();
                }
                UIManager.put("Button.font", new Font("幼圆", 0, 20));

                JPanel panel = new JPanel();
                frame.add(panel);

                // 输入信息
                JTextField indexText = addLabel(panel, "车队号"); panel.add(indexText);
                JTextField beginText = addLabel(panel, "开始时间"); panel.add(beginText);
                JTextField endText = addLabel(panel, "结束时间"); panel.add(endText);

                JTextArea jta = new JTextArea(10,60);
                panel.add(jta);

                // 显示信息
                JButton useDisplayButton = new JButton("接收信息");
                useDisplayButton.setFont(new Font("幼圆", 0, 18));
                useDisplayButton.setBounds(70, 300, 450, 40);
                useDisplayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int index = Integer.parseInt(indexText.getText());
                        //Date beg = Date.valueOf(beginText.getText());
                        //Date end = Date.valueOf(endText.getText());
                        String beg = beginText.getText();
                        String end = endText.getText();
                        ResultSet rc = null;
                        // 列名
                        String hint = "车队 " + index + " 在该时间段内违章统计如下: \n";
                        jta.append(hint);
                        try {
                            // 列名
                            rc = op.GetTeamViolation(conn, index, beg, end);
                            while (rc.next()) {
                                String info = (rc.getString(1) + "\t" + rc.getInt(2) + "次\n");
                                jta.append(info);
                            }
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                panel.add(useDisplayButton);

                frame.setVisible(true);
            }
        });
        return btn;
    }

    /***********  以下为执行各个数据库的录入操作  ************/

    private static JButton useEntryBusButton(JPanel panel, JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("录入汽车信息");
                frame.setSize(600, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception es) {
                    es.printStackTrace();
                }
                UIManager.put("Button.font", new Font("幼圆", 0, 20));

                JPanel panel = new JPanel();
                frame.add(panel);

                // 输入信息
                JLabel lisLabel = new JLabel("车牌号：");
                lisLabel.setFont(new Font("幼圆", 0, 18));
                panel.add(lisLabel);
                JTextField lisText = new JTextField(10);
                panel.add(lisText);

                JLabel brandLabel = new JLabel("品牌：");
                brandLabel.setFont(new Font("幼圆", 0, 18));
                panel.add(brandLabel);
                JTextField brandText = new JTextField(10);
                panel.add(brandText);

                JLabel seatLabel = new JLabel("总座位数：");
                seatLabel.setFont(new Font("幼圆", 0, 18));
                panel.add(seatLabel);
                JTextField seatText = new JTextField(10);
                panel.add(seatText);

                JLabel ageLabel = new JLabel("车龄：");
                ageLabel.setFont(new Font("幼圆", 0, 18));
                panel.add(ageLabel);
                JTextField ageText = new JTextField(10);
                panel.add(ageText);

                JLabel lineLabel = new JLabel("线路号：");
                lineLabel.setFont(new Font("幼圆", 0, 18));
                panel.add(lineLabel);
                JTextField lineText = new JTextField(10);
                panel.add(lineText);

                JTextArea jta = new JTextArea(10,40);
                panel.add(jta);

                // 显示信息
                JButton useDisplayButton = new JButton("录入信息");
                useDisplayButton.setFont(new Font("幼圆", 0, 18));
                useDisplayButton.setBounds(70, 300, 450, 40);
                useDisplayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String lis = lisText.getText();
                        String brand = brandText.getText();
                        int seat = Integer.parseInt(seatText.getText());
                        int age = Integer.parseInt(ageText.getText());

                        String linetxt = lineText.getText();
                        int line = 0;
                        if(!linetxt.equals("")) line = Integer.parseInt(linetxt);

                        boolean ok = op.EnterBusInfo(conn, lis, brand, seat, age, line);
                        if(ok) jta.append("录入成功！！！");
                        else {
                            JOptionPane.showMessageDialog(panel,"录入失败！","警告",2);
                        }
                    }
                });
                panel.add(useDisplayButton);

                frame.setVisible(true);
            }
        });
        return btn;
    }

    private static JButton useEntryDriverButton(JPanel panel, JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("录入用户信息");
                frame.setSize(700, 400);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception es) {
                    es.printStackTrace();
                }
                UIManager.put("Button.font", new Font("幼圆", 0, 20));

                JPanel panel = new JPanel();
                frame.add(panel);

                // 输入信息
                JTextField indexText = addLabel(panel, "工号"); panel.add(indexText);
                JTextField nameText = addLabel(panel, "姓名"); panel.add(nameText);
                JTextField sexText = addLabel(panel, "性别"); panel.add(sexText);
                JTextField ageText = addLabel(panel, "年龄"); panel.add(ageText);
                JTextField placeText = addLabel(panel, "籍贯"); panel.add(placeText);
                JTextField timeText = addLabel(panel, "入职时间"); panel.add(timeText);
                JTextField jobText = addLabel(panel, "职位"); panel.add(jobText);
                JTextField phoneText = addLabel(panel, "电话号码"); panel.add(phoneText);
                JTextField idText = addLabel(panel, "身份证号"); panel.add(idText);
                JTextField lineText = addLabel(panel, "线路"); panel.add(lineText);

                JTextArea jta = new JTextArea(10,40);
                panel.add(jta);

                // 显示信息
                JButton useDisplayButton = new JButton("录入信息");
                useDisplayButton.setFont(new Font("幼圆", 0, 18));
                useDisplayButton.setBounds(70, 300, 450, 40);
                useDisplayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int index = Integer.parseInt(indexText.getText());
                        String name = nameText.getText();
                        String sex = sexText.getText();
                        int age = Integer.parseInt(ageText.getText());
                        String place = placeText.getText();
                        Date date = Date.valueOf(timeText.getText());
                        String job = jobText.getText();
                        String phone = phoneText.getText();
                        String id = idText.getText();

                        String linetxt = lineText.getText();
                        int line = 0;
                        if(!linetxt.equals("")) line = Integer.parseInt(linetxt);

                        boolean ok = op.EnterDriverInfo(
                                conn, id, phone, index, name, sex, age, place, date, job, line
                        );
                        if(ok) jta.append("录入成功！！！");
                        else {
                            JOptionPane.showMessageDialog(panel,"录入失败！","警告",2);
                        }
                    }
                });
                panel.add(useDisplayButton);

                frame.setVisible(true);
            }
        });
        return btn;
    }

    private static JButton useEntryVioButton(JPanel panel, JButton btn) {
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("录入违章信息");
                frame.setSize(600, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception es) {
                    es.printStackTrace();
                }
                UIManager.put("Button.font", new Font("幼圆", 0, 20));

                JPanel panel = new JPanel();
                frame.add(panel);

                // 输入信息
                JTextField vindexText = addLabel(panel, "违章编号"); panel.add(vindexText);
                JTextField indexText = addLabel(panel, "工号"); panel.add(indexText);
                JTextField lisText = addLabel(panel, "车牌号"); panel.add(lisText);
                JTextField dateText = addLabel(panel, "日期"); panel.add(dateText);
                JTextField vioText = addLabel(panel, "违章"); panel.add(vioText);
                JTextField placeText = addLabel(panel, "事发地"); panel.add(placeText);

                JTextArea jta = new JTextArea(10,40);
                panel.add(jta);

                // 显示信息
                JButton useDisplayButton = new JButton("录入信息");
                useDisplayButton.setFont(new Font("幼圆", 0, 18));
                useDisplayButton.setBounds(70, 300, 450, 40);
                useDisplayButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int vindex = Integer.parseInt(vindexText.getText());
                        int index = Integer.parseInt(indexText.getText());
                        String lis = lisText.getText();
                        String date = dateText.getText();
                        String vio = vioText.getText();
                        String place = placeText.getText();

                        System.out.println(rec_index);
                        int ok = op.EnterViolationInfo(
                                conn, rec_index, vindex, index, lis, date, place, vio
                        );
                        if(ok == 1) jta.append("录入成功！！！");
                        else if(ok == 0){
                            JOptionPane.showMessageDialog(panel,"您没有操作权限！","警告",2);
                        }
                        else {
                            JOptionPane.showMessageDialog(panel,"录入失败！","警告",2);
                        }
                    }
                });
                panel.add(useDisplayButton);

                frame.setVisible(true);
            }
        });
        return btn;
    }
}