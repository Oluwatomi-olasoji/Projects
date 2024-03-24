/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pos.system;

import com.toedter.calendar.JDateChooser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author telol
 */
public class sales_manager_homepg extends javax.swing.JFrame {
    
    

    public class stock implements Runnable { // ensures realtime low stock checking

        @Override
        public void run() {
            checkLowStock();
        }
    }

    public class expiration implements Runnable { // ensures realtime low stock checking

        @Override
        public void run() {
            checkExpiryDates();
        }
    }

    public static void checkLowStock() { //this code checks for low stocks and alerts through popups and emails
        StringBuilder lowStockProducts = new StringBuilder("The following goods are running low:\n");

        // Email configuration
        String senderEmail = "teloltsk@gmail.com";
        String senderPassword = "hyfuayznpwxritmg";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inventory");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int orderlvl = rs.getInt("order_level");
                int qtty = rs.getInt("quantity");

                if (qtty <= orderlvl) {
                    String productName = rs.getString("product_name");
                    lowStockProducts.append("- ").append(productName).append("\n");

                }
            }

            if (lowStockProducts.length() > "The following goods are running low:\n".length()) {
                JOptionPane.showMessageDialog(null, lowStockProducts.toString());
            } else {
                JOptionPane.showMessageDialog(null, "No goods are running low.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            PreparedStatement ps = con.prepareStatement("SELECT email, staff_name FROM staff_info WHERE department = ? OR department = ?"); //gets email of people in the concerend departments
            ps.setString(1, "Sales");
            ps.setString(2, "inventory");

            ResultSet rs = ps.executeQuery();

            // Iterate over the result set and send emails
            while (rs.next()) {
                String toEmail = rs.getString("email");
                String sname = rs.getString("staff_name");
                // Create message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Low Stocks Notification");
                StringBuilder emailContent = new StringBuilder("Dear" + sname + "\n\n");
                emailContent.append(lowStockProducts);
                message.setText(emailContent.toString());

                // Send message
                Transport.send(message);
                System.out.println("Email sent to: " + toEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private static long daysBetween(Calendar startDate, Calendar endDate) {
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);

        long startTime = startDate.getTimeInMillis();
        long endTime = endDate.getTimeInMillis();
        long diffTime = endTime - startTime;
        return TimeUnit.MILLISECONDS.toDays(diffTime);
    }

    private static void checkExpiryDates() { //FUNCTION TO CHECK THE EXPIRY DATES OF PRODUCTS AND SEND NOTIFICATIONS
        StringBuilder expiringProducts = new StringBuilder();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji")) {
            String sql = "SELECT product_name, expity_date FROM inventory";
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date expiryDate = dateFormat.parse(rs.getString("expity_date"));

                    Calendar today = Calendar.getInstance();
                    Calendar expiryCalendar = Calendar.getInstance();
                    expiryCalendar.setTime(expiryDate);

                    // Checks if expiry date is within a month or a week
                    long daysDifference = daysBetween(today, expiryCalendar);

                    if (daysDifference <= 30 && daysDifference > 14) {
                        expiringProducts.append(rs.getString("product_name")).append(" (Expiring in a month or less)\n");
                    } else if (daysDifference <= 14 && daysDifference > 7) {
                        expiringProducts.append(rs.getString("product_name")).append(" (Expiring in two weeks or less)\n");

                    } else if (daysDifference <= 7 && daysDifference > 0) { //0 was chosen so that days expiring the next day can still be sold
                        expiringProducts.append(rs.getString("product_name")).append(" (Expiring in a few days)\n");
                    } else if (daysDifference <= 0) {
                        expiringProducts.append(rs.getString("product_name")).append(" has EXPIRIED\n");
                    }
                }
            }

            // Handle expiring products  
            if (expiringProducts.length() > 0) {
                // Email configuration
                String senderEmail = "teloltsk@gmail.com";
                String senderPassword = "hyfuayznpwxritmg";
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                // Create a session with authentication
                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, senderPassword);
                    }
                });

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
                    String sql1 = ("SELECT email, staff_name FROM staff_info WHERE department = ? OR department = ?"); //gets email of people in the concerend departments
                    PreparedStatement ps = conn.prepareStatement(sql1);
                    ps.setString(1, "Sales");
                    ps.setString(2, "inventory");

                    ResultSet rs = ps.executeQuery();

                    // Iterate over the result set and send emails
                    while (rs.next()) {
                        String toEmail = rs.getString("email");
                        String sname = rs.getString("staff_name");
                        // Create message
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(senderEmail));
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                        message.setSubject("Expiring products Notification");
                        StringBuilder emailContent = new StringBuilder("Dear" + sname + "\n\n");
                        emailContent.append("Take note of the following expiring products!\n\n");
                        emailContent.append(expiringProducts);

                        message.setText(emailContent.toString());

                        // Send message
                        Transport.send(message);
                        System.out.println("Email sent to: " + toEmail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null, expiringProducts); //shows a popup
    }

    //CODE TO GENERATE TOP SELLING ITEMS
    private void topSellingItems() {

        try {
            DefaultTableModel tb = (DefaultTableModel) jTable1.getModel();
            // Establishing database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");

            // Query to retrieve top-selling items
            String query = "SELECT product_name, product_code, sales FROM inventory ORDER BY sales DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Populate the table model with data
            while (rs.next()) {
                String productName = rs.getString("product_name");
                String productCode = rs.getString("product_code");
                int sales = rs.getInt("sales");
                tb.addRow(new Object[]{productName, productCode, sales});
            }

            // Close the database connection
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  //CODE TO RETRIEVE CASHIER PERFORMANCE
    private void cashierPerformance() {

        try {
            DefaultTableModel tb = (DefaultTableModel) jTable2.getModel();
            // Establishing database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");

            
            String query = "SELECT email, name, sales FROM cashier ORDER BY sales DESC";
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            // Populate the table model with data
            while (rs.next()) {
                String email = rs.getString("email");
                String name = rs.getString("name");
                int sales = rs.getInt("sales");
                tb.addRow(new Object[]{email, name, sales});
            }

            // Close the database connection
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Creates new form sales_manager_homepage
     */
    public sales_manager_homepg() {
        initComponents();

        stock t = new stock(); //starts the stock thread so its alwasy running
        Thread t1 = new Thread(t);
        t1.start();

        expiration j = new expiration(); //starts the stock thread so its alwasy running
        Thread j1 = new Thread(j);
        j1.start();
        topSellingItems();
        cashierPerformance();
    }
 
    private void DailyReport() {
    DefaultTableModel dayTable = (DefaultTableModel) dailyReportTable.getModel();
    dayTable.setRowCount(0);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String theDate = dateFormat.format(jDateChooser1.getDate());

    String selectQuery = "SELECT product_code, product_name, "
            + "SUM(quantity) AS total_quantity, "
            + "SUM(cost) AS total_cost "
            + "FROM sales "
            + "WHERE date = ? "
            + "GROUP BY product_code, product_name "
            + "ORDER BY total_quantity DESC";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            PreparedStatement ps = connection.prepareStatement(selectQuery)) {

        ps.setString(1, theDate);
        ResultSet resultSet = ps.executeQuery();
        boolean salesFound = false; // Flag to track if any sales were found

        while (resultSet.next()) {
            
            String productCode = resultSet.getString("product_code");
            String productName = resultSet.getString("product_name");
            int totalQuantity = resultSet.getInt("total_quantity");
            double totalCost = resultSet.getDouble("total_cost");

            Object[] row = new Object[]{productCode, productName, totalQuantity, totalCost};
            dayTable.addRow(row);
            
            
       salesFound = true; // Set flag to true if sales are found
        }
        
        // Check if any sales were found
        if (!salesFound) {
            JOptionPane.showMessageDialog(rootPane, "No sales made on " + theDate);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(rootPane, "Error retrieving data: " + e.getMessage());
    }
    //code to get total revenue
    Double totalRevenue = 0.0;
    for (int i = 0; i < dayTable.getRowCount(); i++) {
       
        String revenue = dayTable.getValueAt(i, 3).toString();
        totalRevenue += Double.parseDouble(revenue);
    }
    t_d_revenue.setText(totalRevenue.toString());
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jScrollPane4 = new javax.swing.JScrollPane();
        dailyReportTable = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        t_d_revenue = new javax.swing.JLabel();
        g_d_report = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jDateChooser3 = new com.toedter.calendar.JDateChooser();
        jScrollPane5 = new javax.swing.JScrollPane();
        weeklyReportTable = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        t_w_revenue = new javax.swing.JLabel();
        g_w_report = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        monthlyReportTable1 = new javax.swing.JTable();
        g_m_report = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        t_m_revenue = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        yearTF = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        annualReportTable = new javax.swing.JTable();
        yearTF1 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        g_y_report = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        t_y_revenue = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Code", "Product Name", "No of sales"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Segoe UI Black", 0, 12)); // NOI18N
        jLabel2.setText("TOP SELLERS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(305, 305, 305)
                .addComponent(jLabel2)
                .addContainerGap(335, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Top sellers", jPanel2);

        jLabel1.setText("Enter Cashier email: ");

        jTextField1.setText(" ");

        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Email", "Cashier name", "Total number of sales"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(301, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Cashier information", jPanel3);

        jLabel3.setText("Enter day:");

        dailyReportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product code", "Product name", "Quantity sold", "Revenue generated per item(N)"
            }
        ));
        jScrollPane4.setViewportView(dailyReportTable);

        jLabel4.setText("Total daily revenue: N");

        jLabel6.setText("  ");

        g_d_report.setText("Generate Report");
        g_d_report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g_d_reportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(g_d_report))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(t_d_revenue, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(g_d_report))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(t_d_revenue, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel6)))
                .addContainerGap(99, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Daily Report", jPanel5);

        jLabel5.setText("Start Date:");

        jLabel7.setText(" End Date:");

        weeklyReportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product code", "Product name", "Quantity sold", "Revenue generated per item(N)"
            }
        ));
        jScrollPane5.setViewportView(weeklyReportTable);

        jLabel8.setText("Total Weekly Revenue:   N");

        t_w_revenue.setText(" ");

        g_w_report.setText("Generate Report");
        g_w_report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g_w_reportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(g_w_report))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(t_w_revenue, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
            .addComponent(jScrollPane5)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel7))
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(g_w_report))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(t_w_revenue))
                .addContainerGap(99, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Weekly Report", jPanel6);

        monthlyReportTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product code", "Product name", "Quantity sold", "Revenue generated per item(N)"
            }
        ));
        jScrollPane6.setViewportView(monthlyReportTable1);

        g_m_report.setText("Generate Report");
        g_m_report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g_m_reportActionPerformed(evt);
            }
        });

        jLabel9.setText("Total Monthly Revenue:   N");

        t_m_revenue.setText(" ");

        jLabel11.setText("Enter month:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));

        jLabel10.setText("Enter year:");

        yearTF.setText(" ");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 713, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(138, 138, 138)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(yearTF, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addComponent(g_m_report)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(t_m_revenue, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(yearTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(g_m_report))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(t_m_revenue))
                .addGap(96, 96, 96))
        );

        jTabbedPane2.addTab("Monthly Report", jPanel7);

        annualReportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product code", "Product name", "Quantity sold", "Revenue generated per item(N)"
            }
        ));
        jScrollPane7.setViewportView(annualReportTable);

        yearTF1.setText(" ");

        jLabel12.setText("Enter year:");

        g_y_report.setText("Generate Report");
        g_y_report.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                g_y_reportActionPerformed(evt);
            }
        });

        jLabel13.setText("Total annual Revenue:  N");

        t_y_revenue.setText(" ");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(143, 143, 143)
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(yearTF1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(91, 91, 91)
                        .addComponent(g_y_report))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(t_y_revenue, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(198, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(yearTF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(g_y_report))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(t_y_revenue))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Yearly report", jPanel8);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sales Reports", jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String email = jTextField1.getText();
        DefaultTableModel tb = (DefaultTableModel) jTable2.getModel();
        
        if (email.equals("")) {
                JOptionPane.showMessageDialog(rootPane, "Enter email address"); }
        else{
        try {
            
            // Establishing database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");

            
            String query = "SELECT name, sales FROM cashier WHERE email = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            // Populate the table model with data
            if (rs.next()) {
                tb.setRowCount(0);
                String name = rs.getString("name");
                int sales = rs.getInt("sales");
                tb.addRow(new Object[]{email, name, sales});
            }
            else {
            JOptionPane.showMessageDialog(rootPane,"Email not found");
            }

            // Close the database connection
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }  
        }// TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void g_d_reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g_d_reportActionPerformed
     DailyReport();   // TODO add your handling code here:
    }//GEN-LAST:event_g_d_reportActionPerformed

    private void g_w_reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g_w_reportActionPerformed

    DefaultTableModel dayTable = (DefaultTableModel) weeklyReportTable.getModel();
    dayTable.setRowCount(0);
    t_w_revenue.setText("");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String startDate = dateFormat.format(jDateChooser2.getDate());
    String endDate = dateFormat.format(jDateChooser3.getDate()); // New date for range
    
    if (startDate.isEmpty() || endDate.isEmpty() || startDate.compareTo(endDate) > 0) {
        JOptionPane.showMessageDialog(rootPane, "Invalid date range");
        return; // Exit method if date range is invalid
    }
    
    String selectQuery = "SELECT product_code, product_name, "
            + "SUM(quantity) AS total_quantity, "
            + "SUM(cost) AS total_cost "
            + "FROM sales "
            + "WHERE date BETWEEN ? AND ? " // Change to use date range
            + "GROUP BY product_code, product_name "
            + "ORDER BY total_quantity DESC";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            PreparedStatement ps = connection.prepareStatement(selectQuery)) {

        ps.setString(1, startDate);
        ps.setString(2, endDate); // Set the second date parameter
        ResultSet resultSet = ps.executeQuery();
        
        boolean salesFound = false; // Flag to track if any sales were found

        while (resultSet.next()) {
            String productCode = resultSet.getString("product_code");
            String productName = resultSet.getString("product_name");
            int totalQuantity = resultSet.getInt("total_quantity");
            double totalCost = resultSet.getDouble("total_cost");

            Object[] row = new Object[]{productCode, productName, totalQuantity, totalCost};
            dayTable.addRow(row);
            
            salesFound = true; // Set flag to true if sales are found
        }
        
        // Check if any sales were found
        if (!salesFound) {
            JOptionPane.showMessageDialog(rootPane, "No sales made between " + startDate + " and " + endDate);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(rootPane, "Error retrieving data: " + e.getMessage());
    }
    
    //code to get total revenue
    Double totalRevenue = 0.0;
    for (int i = 0; i < dayTable.getRowCount(); i++) {
        String revenue = dayTable.getValueAt(i, 3).toString();
        totalRevenue += Double.parseDouble(revenue);
    }
    t_w_revenue.setText(totalRevenue.toString());


    }//GEN-LAST:event_g_w_reportActionPerformed

    private void g_m_reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g_m_reportActionPerformed
        DefaultTableModel monthTable = (DefaultTableModel) monthlyReportTable1.getModel();
    monthTable.setRowCount(0); // Clear existing rows
    t_m_revenue.setText(""); // Reset revenue display
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Get the selected month from the JComboBox (assuming it's zero-based index)
    int selectedMonthIndex = jComboBox1.getSelectedIndex();
    if (selectedMonthIndex == -1) {
        JOptionPane.showMessageDialog(rootPane, "Please select a month.");
        return; // Exit method if no month selected
    }

    // Get the entered year from the JTextField
    String yearText = yearTF.getText();
    if (yearText.isEmpty()) {
        JOptionPane.showMessageDialog(rootPane, "Please enter a year.");
        return; // Exit method if no year entered
    }

    int year;
    try {
        year = Integer.parseInt(yearText);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(rootPane, "Invalid year format. Please enter a valid year.");
        return; // Exit method if invalid year format
    }

    // Validate the selected month index
    if (selectedMonthIndex < 0 || selectedMonthIndex > 11) {
        JOptionPane.showMessageDialog(rootPane, "Invalid month selection.");
        return; // Exit method if invalid month selection
    }

    // Set the start date to the first day of the selected month and year
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, selectedMonthIndex, 1);
    String startDate = dateFormat.format(calendar.getTime());

    // Set the end date to the last day of the selected month and year
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    String endDate = dateFormat.format(calendar.getTime());

    String selectQuery = "SELECT product_code, product_name, "
            + "SUM(quantity) AS total_quantity, "
            + "SUM(cost) AS total_cost "
            + "FROM sales "
            + "WHERE date BETWEEN ? AND ? " // Change to use date range
            + "GROUP BY product_code, product_name "
            + "ORDER BY total_quantity DESC";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            PreparedStatement ps = connection.prepareStatement(selectQuery)) {

        ps.setString(1, startDate);
        ps.setString(2, endDate); // Set the second date parameter
        ResultSet resultSet = ps.executeQuery();

        boolean salesFound = false; // Flag to track if any sales were found

        while (resultSet.next()) {
            String productCode = resultSet.getString("product_code");
            String productName = resultSet.getString("product_name");
            int totalQuantity = resultSet.getInt("total_quantity");
            double totalCost = resultSet.getDouble("total_cost");

            Object[] row = new Object[]{productCode, productName, totalQuantity, totalCost};
            monthTable.addRow(row);

            salesFound = true; // Set flag to true if sales are found
        }

        // Check if any sales were found
        if (!salesFound) {
            JOptionPane.showMessageDialog(rootPane, "No sales made in " + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + year);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(rootPane, "Error retrieving data: " + e.getMessage());
    }

    // Code to calculate total revenue
    double totalRevenue = 0.0;
    for (int i = 0; i < monthTable.getRowCount(); i++) {
        String revenue = monthTable.getValueAt(i, 3).toString();
        totalRevenue += Double.parseDouble(revenue);
    }
    t_m_revenue.setText(Double.toString(totalRevenue));
    }//GEN-LAST:event_g_m_reportActionPerformed

    private void g_y_reportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_g_y_reportActionPerformed
        
    DefaultTableModel yearTable = (DefaultTableModel) annualReportTable.getModel();
    yearTable.setRowCount(0); // Clear existing rows
    t_y_revenue.setText(""); // Reset revenue display
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Get the entered year from the JTextField
    String yearText = yearTF1.getText();
    if (yearText.isEmpty()) {
        JOptionPane.showMessageDialog(rootPane, "Please enter a year.");
        return; // Exit method if no year entered
    }

    int year;
    try {
        year = Integer.parseInt(yearText);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(rootPane, "Invalid year format. Please enter a valid year.");
        return; // Exit method if invalid year format
    }

    String startDate = year + "-01-01"; // Start date set to January 1st of the entered year
    String endDate = year + "-12-31"; // End date set to December 31st of the entered year

    String selectQuery = "SELECT product_code, product_name, "
            + "SUM(quantity) AS total_quantity, "
            + "SUM(cost) AS total_cost "
            + "FROM sales "
            + "WHERE YEAR(date) = ? " // Condition for the specific year
            + "GROUP BY product_code, product_name "
            + "ORDER BY total_quantity DESC";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            PreparedStatement ps = connection.prepareStatement(selectQuery)) {

        ps.setInt(1, year); // Set the year parameter
        ResultSet resultSet = ps.executeQuery();

        boolean salesFound = false; // Flag to track if any sales were found

        while (resultSet.next()) {
            String productCode = resultSet.getString("product_code");
            String productName = resultSet.getString("product_name");
            int totalQuantity = resultSet.getInt("total_quantity");
            double totalCost = resultSet.getDouble("total_cost");

            Object[] row = new Object[]{productCode, productName, totalQuantity, totalCost};
            yearTable.addRow(row);

            salesFound = true; // Set flag to true if sales are found
        }

        // Check if any sales were found
        if (!salesFound) {
            JOptionPane.showMessageDialog(rootPane, "No sales made in " + year);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(rootPane, "Error retrieving data: " + e.getMessage());
    }

    // Code to calculate total revenue
    double totalRevenue = 0.0;
    for (int i = 0; i < yearTable.getRowCount(); i++) {
        String revenue = yearTable.getValueAt(i, 3).toString();
        totalRevenue += Double.parseDouble(revenue);
    }
    t_y_revenue.setText(Double.toString(totalRevenue));


    }//GEN-LAST:event_g_y_reportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(sales_manager_homepg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(sales_manager_homepg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(sales_manager_homepg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(sales_manager_homepg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new sales_manager_homepg().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable annualReportTable;
    private javax.swing.JTable dailyReportTable;
    private javax.swing.JButton g_d_report;
    private javax.swing.JButton g_m_report;
    private javax.swing.JButton g_w_report;
    private javax.swing.JButton g_y_report;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable monthlyReportTable1;
    private javax.swing.JLabel t_d_revenue;
    private javax.swing.JLabel t_m_revenue;
    private javax.swing.JLabel t_w_revenue;
    private javax.swing.JLabel t_y_revenue;
    private javax.swing.JTable weeklyReportTable;
    private javax.swing.JTextField yearTF;
    private javax.swing.JTextField yearTF1;
    // End of variables declaration//GEN-END:variables
}
