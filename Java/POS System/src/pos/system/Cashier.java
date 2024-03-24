/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pos.system;
import java.sql.*;
import java.awt.Image;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import static pos.system.Staff_registration.jLabel15;
import static pos.system.Staff_registration.photo;

/**
 *
 * @author telol
 */
public class Cashier extends javax.swing.JFrame {
    String email;
    String staff_name;
    
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
                    }
                    else if (daysDifference <= 0) {
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
    
     private void updateCashier(){
          try {
            // Establishing database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            
            // Check if the email already exists in the cashier table
            String query = "SELECT * FROM cashier WHERE email = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // If the email exists, update the sales count by +1
                int currentSales = rs.getInt("sales");
                int newSales = currentSales + 1;
                updateSales(con, email, newSales);
            } else {
                // If the email doesn't exist, insert a new row with sales count as 0
                insertCashier(con, email, staff_name);
            }
            
            // Close the database connection
            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Method to update the sales count for an existing email
    private static void updateSales(Connection con, String email, int newSales)  {
        try{
        String updateQuery = "UPDATE cashier SET sales = ? WHERE email = ?";
        PreparedStatement updatePs = con.prepareStatement(updateQuery);
        updatePs.setInt(1, newSales);
        updatePs.setString(2, email);
        updatePs.executeUpdate();
        updatePs.close();
        } catch (Exception e){}
        
    }
    
    // Method to insert a new row into the cashier table
    private static void insertCashier(Connection con, String email, String name)  {
        try{
        String insertQuery = "INSERT INTO cashier (email, name, sales) VALUES (?, ?, 0)";
        PreparedStatement insertPs = con.prepareStatement(insertQuery);
        insertPs.setString(1, email);
        insertPs.setString(2, name);
        insertPs.executeUpdate();
        insertPs.close();
        } catch(Exception e){}
    }
    
    //Method to save the items sold to the sales table
    private void saveSoldItems(JTable tb){
      try {
          Class.forName("com.mysql.cj.jdbc.Driver");
          Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
        
           
            for (int row = 0; row < tb.getRowCount(); row++) {
                // Extracting data from jTable1
                String productCode = (String) tb.getValueAt(row, 0);
                String productName = (String) tb.getValueAt(row, 1);
                String quantity = (String)tb.getValueAt(row, 2);
                String pricePerItem =  (String)tb.getValueAt(row, 3);
                String cost = (String) tb.getValueAt(row, 4);

                // Getting the current date
                Date currentDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(currentDate);

                // Inserting data into the sales table
                String query = "INSERT INTO sales VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, formattedDate);
                ps.setString(2, productCode);
                ps.setString(3, productName);
                ps.setString(4, quantity);
                ps.setString(5, cost);

                
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
     
    /**
     * Creates new form Cashier
     */
    public Cashier(String email, String staff_name) {
        initComponents();
        this.email = email;
        this.staff_name = staff_name;
        
        
        System.out.println(email);
        System.out.println(staff_name);
        
        print.setEnabled(false);
         
           
        stock t = new stock(); //starts the stock thread so its alwasy running
        Thread t1 = new Thread(t);
        t1.start();

        expiration j = new expiration(); //starts the stock thread so its alwasy running
        Thread j1 = new Thread(j);
        j1.start();
        
       
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        productName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        qunatityTF = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        receiptTextArea = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        print = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        subtotalLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        CashpaidTF = new javax.swing.JTextField();
        balance_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product code", "Product Name", "Quantity", "Price per item", "Cost"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText("Product Name:");

        productName.setText(" ");

        jLabel2.setText("Quantity:");

        qunatityTF.setText(" ");
        qunatityTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qunatityTFActionPerformed(evt);
            }
        });

        receiptTextArea.setColumns(20);
        receiptTextArea.setRows(5);
        jScrollPane2.setViewportView(receiptTextArea);

        jButton1.setText("Checkout");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        print.setText("Print Reciept");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });

        jButton5.setText("Enter");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel3.setText("Total:");

        jLabel4.setText("Cash paid:");

        CashpaidTF.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(qunatityTF, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(productName, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(subtotalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(balance_label, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CashpaidTF, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(111, 111, 111)
                                .addComponent(jButton1)))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(print)
                        .addGap(91, 91, 91))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(productName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(qunatityTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subtotalLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(print))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(CashpaidTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(balance_label, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        try {
        // Print the contents of the JTextArea
        boolean complete = receiptTextArea.print();
        
        if (complete) {
            // Print successful
            JOptionPane.showMessageDialog(this, "Receipt printed successfully!");
        } else {
            // Print canceled
            JOptionPane.showMessageDialog(this, "Printing canceled.");
        }
    } catch (PrinterException pe) {
        // Printing error
        JOptionPane.showMessageDialog(this, "Error printing: " + pe.getMessage());
    }

    }//GEN-LAST:event_printActionPerformed

    private void qunatityTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qunatityTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_qunatityTFActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
     DefaultTableModel tb = (DefaultTableModel) jTable1.getModel();
String name = productName.getText();
String qty1 = qunatityTF.getText();
//ArrayList<Integer> subtotal = new ArrayList<>(); // Initialize subtotal ArrayList

try {
    if (name.equals("") || qty1.equals("")) {
        JOptionPane.showMessageDialog(rootPane, "Enter product name and quantity");
    } else {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
        PreparedStatement ps = con.prepareStatement("SELECT product_code, product_name, price, expity_date FROM inventory");

        ResultSet rs = ps.executeQuery();
        boolean productFound = false;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar today = Calendar.getInstance();
       
        

         

        while (rs.next()) {
            String pcode = rs.getString("product_code");
            String pname = rs.getString("product_name");
            String price1 = rs.getString("price");
            int price = Integer.valueOf(price1);
            
            Date expiryDate = dateFormat.parse(rs.getString("expity_date"));
            Calendar expiryCalendar = Calendar.getInstance();
            expiryCalendar.setTime(expiryDate);
            
            // Checks if expiry date is within a month or a week
            long daysDifference = daysBetween(today, expiryCalendar);
            

            if (pname.equalsIgnoreCase(name)) {
                String[] rowData = new String[5];
                int qty = 0;
                if (qty1.equals("")) {
                    qty = 0;
                } else {
                    try {
                        qty = Integer.parseInt(qty1);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(rootPane, "Enter a valid number as quantity");
                        return;
                    }
                } 
                if (daysDifference > 0){ //checks if the difference between the expiry date and today's date is more than 1
                int cost = price * qty;
                rowData[0] = pcode;
                rowData[1] = pname;
                rowData[2] = qty1;
                rowData[3] = price1;
                rowData[4] = String.valueOf(cost);

                tb.addRow(rowData);
                productFound = true;
                break;}
                
                else{
                   JOptionPane.showMessageDialog(rootPane, "Product expired");
                   productFound = true;
                   break;
                }
                
            }
        }
        if (!productFound) {
            JOptionPane.showMessageDialog(rootPane, "Product not found");
        }
    }
} catch (Exception e) {
    JOptionPane.showMessageDialog(rootPane, e);
}

     //code to calculate subtotal 
      int sum = 0;
        for(int row = 0; row < jTable1.getRowCount(); row++) {
        String valueStr = (String) jTable1.getValueAt(row, 4);
        int value = Integer.parseInt(valueStr);
        sum += value; 
   }
        subtotalLabel.setText(String.valueOf(sum));
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
           
    int rowCount = jTable1.getRowCount();

    // Initialize the receipt text
    StringBuilder receiptText = new StringBuilder();
    receiptText.append("**************************************************\n");
    receiptText.append("                           Receipt              \n");
    receiptText.append("**************************************************\n\n");
    receiptText.append(String.format("%-10s %-10s %-21s %-10s\n", "Product", "quantity", "PricePerItem", "cost"));

    // Iterate through each row in the jTable
    double totalCost = 0.0;
    for (int i = 0; i < rowCount; i++) {
        // Get product name, quantity, price, and cost from jTable
        String productName = jTable1.getValueAt(i, 1).toString();
        String quantity = jTable1.getValueAt(i, 2).toString();
        String pricePerItem = jTable1.getValueAt(i, 3).toString();
        String cost = jTable1.getValueAt(i, 4).toString();
        
        // Append product information to receipt text
        receiptText.append(String.format("%-15s %-20s %-20s %-10s\n", productName, quantity, pricePerItem, cost));
        
        // Add cost to total cost
        totalCost += Double.parseDouble(cost);
    }

     receiptText.append("\n**************************************************");
     receiptText.append("\nTotal Cost: N" + totalCost + "\n");

    // Get cash paid from JTextField
    double cashPaid = 0;
    if (CashpaidTF.equals("")) { 
        //cashPaid remains as 0 
                } else {
                    try {
                        cashPaid = Double.parseDouble(CashpaidTF.getText());
                        if (cashPaid < totalCost){
                        JOptionPane.showMessageDialog(rootPane, "Insufficient cash");
                        return;
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(rootPane, "Enter a valid amount as cash");
                        return;
                    }
                }
    

    // Calculate balance
    double balance = cashPaid - totalCost;

    // Add cash paid and balance to receipt text
    receiptText.append("Cash Paid: N" + cashPaid + "\n");
    receiptText.append("Balance: N" + balance + "\n");

    // Display the receipt in the JTextArea
    receiptTextArea.setText(receiptText.toString());
    print.setEnabled(true);
    
    //CODE TO UPDATE STOCK IN DATABASE
    try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");

    for (int row = 0; row < jTable1.getRowCount(); row++) {
        String pname = (String) jTable1.getValueAt(row, 1);
        String qtty = (String) jTable1.getValueAt(row, 2);
        int qty = Integer.parseInt(qtty);

        PreparedStatement ps1 = con.prepareStatement("SELECT sales, quantity FROM inventory WHERE product_name = ?");
        ps1.setString(1, pname);

        ResultSet rs1 = ps1.executeQuery();
        
        if (rs1.next()) {
            int sales = rs1.getInt("sales");
            int quan = rs1.getInt("quantity");
            
            int newsale = sales + qty;
            int newqtty = quan - qty;

            PreparedStatement ps = con.prepareStatement("UPDATE inventory SET quantity = ?, sales = ? WHERE product_name = ?");
            ps.setInt(1, newqtty);
            ps.setInt(2, newsale);
            ps.setString(3, pname);

            ps.executeUpdate();
           
        } else {
           JOptionPane.showMessageDialog(null, "Error updating stock");
           break;
        }

        ps1.close();
    }
     JOptionPane.showMessageDialog(null, "Checkout complete\nStocks updated");
    con.close();
} catch (Exception e) {
    e.printStackTrace();
}
//CODE TO UPDATE CASHIER SALES
updateCashier();

//CODE TO SAVE INFO TO SALES TABLE
saveSoldItems(jTable1);
      
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Cashier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Cashier("","").setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField CashpaidTF;
    private javax.swing.JLabel balance_label;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton print;
    private javax.swing.JTextField productName;
    private javax.swing.JTextField qunatityTF;
    private javax.swing.JTextArea receiptTextArea;
    private javax.swing.JLabel subtotalLabel;
    // End of variables declaration//GEN-END:variables
}
