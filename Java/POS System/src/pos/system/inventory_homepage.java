/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pos.system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author telol
 */
public class inventory_homepage extends javax.swing.JFrame {
    
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
        long startTime = startDate.getTimeInMillis();
        long endTime = endDate.getTimeInMillis();
        long diffTime = endTime - startTime;
        return TimeUnit.MILLISECONDS.toDays(diffTime);
    }
    
    private static void checkExpiryDates() {
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

                    } else if (daysDifference <= 7 && daysDifference > 1) {
                        expiringProducts.append(rs.getString("product_name")).append(" (Expiring in a week or less)\n");
                    }
                    else if (daysDifference <= 1) {
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

    /**
     * Creates new form inventory_homepage
     */
    public inventory_homepage() {
        initComponents();
        Delete.setEnabled(false);
        update_level.setEnabled(false);

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Check if a row is selected
                if (!e.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) { // Enables the button when a row is selected
                    Delete.setEnabled(true);
                } else { // Disables the button when no row is selected
                    Delete.setEnabled(false);
                }
            }
        });

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

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        Delete = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pNameTF = new javax.swing.JTextField();
        pCodeTF = new javax.swing.JTextField();
        ManufacturerTF = new javax.swing.JTextField();
        enter = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        PriceTF = new javax.swing.JTextField();
        quantifyTF = new javax.swing.JTextField();
        Upload = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        browse = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        orderlvlTF = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jPanel3 = new javax.swing.JPanel();
        view_inventory = new javax.swing.JButton();
        view_lowStocks = new javax.swing.JButton();
        update_level = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Delete.setText(" Delete row");
        Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Name", "Manufacture", "Manufacturing date", "Exp date", "Price", "Quantity", "Reorder level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        pCodeTF.setText(" ");
        pCodeTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pCodeTFActionPerformed(evt);
            }
        });

        ManufacturerTF.setText(" ");

        enter.setText("Enter");
        enter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterActionPerformed(evt);
            }
        });

        jLabel1.setText("Product Name:");

        jLabel2.setText("Product Code:");

        jLabel3.setText("Manufacture:");

        jLabel4.setText("Manafucturing date:");

        jLabel5.setText("Expiry date:");

        jLabel6.setText("Price:                        N");

        jLabel7.setText("Quantity:");

        PriceTF.setText(" ");
        PriceTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PriceTFActionPerformed(evt);
            }
        });

        quantifyTF.setText(" ");

        Upload.setText("Upload to database");
        Upload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UploadActionPerformed(evt);
            }
        });

        jLabel8.setText("Select File");

        jTextField1.setText("  ");

        browse.setText("Browse");
        browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseActionPerformed(evt);
            }
        });

        jLabel9.setText("Order level:");

        orderlvlTF.setText(" ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(enter)
                            .addComponent(PriceTF, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ManufacturerTF)
                            .addComponent(quantifyTF)
                            .addComponent(orderlvlTF)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addComponent(pNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pCodeTF, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                                .addComponent(Delete)
                                .addGap(103, 103, 103)
                                .addComponent(Upload)
                                .addGap(52, 52, 52))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browse)
                        .addGap(89, 89, 89))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(browse, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Delete)
                            .addComponent(Upload)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pCodeTF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(pNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ManufacturerTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(PriceTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addGap(9, 9, 9)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(quantifyTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(orderlvlTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(enter)))
                .addContainerGap(57, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Stock products", jPanel2);

        view_inventory.setText("View inventory");
        view_inventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_inventoryActionPerformed(evt);
            }
        });

        view_lowStocks.setText("Filter low stock products");
        view_lowStocks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                view_lowStocksActionPerformed(evt);
            }
        });

        update_level.setText("Update low Stock level");
        update_level.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                update_levelActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Code", "Manufacture", "Manufacturing date", "Exp date", "Price", "Quantity", "Reorder level"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(view_inventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addComponent(view_lowStocks)
                .addGap(67, 67, 67)
                .addComponent(update_level)
                .addGap(76, 76, 76))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(view_inventory)
                    .addComponent(view_lowStocks)
                    .addComponent(update_level))
                .addContainerGap(385, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                    .addGap(0, 51, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jTabbedPane1.addTab(" Low Stock ", jPanel3);

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

    private void update_levelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_update_levelActionPerformed
        try{
            DefaultTableModel tableModel = (DefaultTableModel)jTable2.getModel();
            String insertQuery = "UPDATE inventory SET  product_name=?, manufacturer=?, manufacturing_date=?, expity_date=?, price=?,quantity = ?,order_level = ? WHERE product_code = ?";

            Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            String path = "jdbc:mysql://localhost:3306/pos_db";
            String root = "root";
            String pass = "tomiolasoji";
            Connection con = DriverManager.getConnection(path,root , pass);
            PreparedStatement ps = con.prepareStatement(insertQuery);

            for (int row = 0; row < jTable2.getRowCount(); row++) {
                // Start from col = 1 to skip the first column (product_code)
                for (int col = 1; col < jTable2.getColumnCount(); col++) {
                    Object value = jTable2.getValueAt(row, col);
                    ps.setObject(col, value);
                }
                // Sets product_code (the last column) after setting other values
                Object productCode = jTable2.getValueAt(row, 0);
                ps.setObject(jTable2.getColumnCount(), productCode);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(rootPane, "Stcoks updated");
            tableModel.setRowCount(0);

        } catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e);}
    }//GEN-LAST:event_update_levelActionPerformed

    private void view_lowStocksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view_lowStocksActionPerformed
        update_level.setEnabled(true); //the update low stock level gets activated
        DefaultTableModel tb = (DefaultTableModel)jTable2.getModel();
        while(tb.getRowCount()!= 0)
        {
            tb.removeRow(0);
        }
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji"); //makes the driver conncet to
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inventory");

            ResultSet rs =    ps.executeQuery();
            while(rs.next())
            {
                int orderlvl = rs.getInt("order_level");
                int qtty = rs.getInt("quantity");
                if (qtty <= orderlvl){
                    String[] rowData = new String[8];
                    rowData[0] =  rs.getString("product_code");
                    rowData[1] = rs.getString("product_name");
                    rowData[2] =  rs.getString("manufacturer");
                    rowData[3] = rs.getString("manufacturing_date");
                    rowData[4] =  rs.getString("expity_date");
                    rowData[5] =  rs.getString("price");
                    rowData[6] = rs.getString("quantity");
                    rowData[7] = rs.getString("order_level");
                    tb.addRow(rowData);
                }
            }
        }catch(Exception e){
            e.printStackTrace();}
    }//GEN-LAST:event_view_lowStocksActionPerformed

    private void view_inventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_view_inventoryActionPerformed
        DefaultTableModel tb = (DefaultTableModel)jTable2.getModel();
        while(tb.getRowCount()!= 0)
        {
            tb.removeRow(0);
        }
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji"); //makes the driver conncet to
            PreparedStatement ps = con.prepareStatement("SELECT * FROM inventory");

            ResultSet rs =    ps.executeQuery();
            while(rs.next())
            {
                String[] rowData = new String[8];
                rowData[0] =  rs.getString("product_code");
                rowData[1] = rs.getString("product_name");
                rowData[2] =  rs.getString("manufacturer");
                rowData[3] = rs.getString("manufacturing_date");
                rowData[4] =  rs.getString("expity_date");
                rowData[5] =  rs.getString("price");
                rowData[6] = rs.getString("quantity");
                rowData[7] = rs.getString("order_level");
                tb.addRow(rowData);
            }
        }catch(Exception e){
            e.printStackTrace();}
    }//GEN-LAST:event_view_inventoryActionPerformed

    private void browseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseActionPerformed
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files","csv");
        jFileChooser1.setAcceptAllFileFilterUsed(false);
        jFileChooser1.addChoosableFileFilter(filter);
        int open = jFileChooser1.showOpenDialog(null);
        if (open!= jFileChooser1.APPROVE_OPTION){
            return;
        } else{
            String filename = jFileChooser1.getSelectedFile().getAbsolutePath().toString();
            String file = jFileChooser1.getSelectedFile().getName().toString();
            jTextField1.setText(filename);
            if(!filename.contains(".csv")){
                filename = filename + ".csv";
            }
            try {
                String line;
                DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
                BufferedReader br = new BufferedReader(new FileReader(jFileChooser1.getSelectedFile()));
                while ((line = br.readLine()) != null){
                    String[] data = line.split(",");
                    tableModel.addRow(data);
                }

            } catch(Exception e){
                JOptionPane.showMessageDialog(rootPane, "Error reading file");
            }
        }
    }//GEN-LAST:event_browseActionPerformed

    private void UploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UploadActionPerformed
        try{
            DefaultTableModel tableModel = (DefaultTableModel)jTable1.getModel();
            String insertQuery = "INSERT INTO inventory values (?,?,?,?,?,?,?,?,?)";
            Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            String path = "jdbc:mysql://localhost:3306/pos_db";
            String root = "root";
            String pass = "tomiolasoji";
            Connection con = DriverManager.getConnection(path,root , pass);
            PreparedStatement ps = con.prepareStatement(insertQuery);

            for(int row = 0; row< jTable1.getRowCount(); row++){
                for (int col = 0; col <jTable1.getColumnCount(); col++){
                    Object value = jTable1.getValueAt(row, col);
                    ps.setObject(col+1, value);
                    ps.setObject( col + 2, 0);
                }

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(rootPane, "Stocks inserted");
            tableModel.setRowCount(0);

        } catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e);}
    }//GEN-LAST:event_UploadActionPerformed

    private void PriceTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PriceTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PriceTFActionPerformed

    private void enterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterActionPerformed
        String name =pNameTF.getText();
        String code =pCodeTF.getText();
        String manufac =ManufacturerTF.getText();
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        String manu_date =date.format(jDateChooser1.getDate());
        String expDate =date.format(jDateChooser2.getDate());
        String price =PriceTF.getText();
        String qtt =quantifyTF.getText();
        String orderlvl =orderlvlTF.getText();

        String data[] = {code,name,manufac,manu_date,expDate,price,qtt,orderlvl};
        DefaultTableModel td = (DefaultTableModel)jTable1.getModel();
        td.addRow(data);

        pNameTF.setText("");
        pCodeTF.setText("");
        ManufacturerTF.setText("");
        jDateChooser1.setDate(null);
        jDateChooser2.setDate(null);
        PriceTF.setText("");
        quantifyTF.setText("");
        orderlvlTF.setText("");
    }//GEN-LAST:event_enterActionPerformed

    private void pCodeTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pCodeTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pCodeTFActionPerformed

    private void DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteActionPerformed
        DefaultTableModel table = (DefaultTableModel)jTable1.getModel();
        int row = jTable1.getSelectedRow();
        table.removeRow(row);
    }//GEN-LAST:event_DeleteActionPerformed

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
            java.util.logging.Logger.getLogger(inventory_homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(inventory_homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(inventory_homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(inventory_homepage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new inventory_homepage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Delete;
    private javax.swing.JTextField ManufacturerTF;
    private javax.swing.JTextField PriceTF;
    private javax.swing.JButton Upload;
    private javax.swing.JButton browse;
    private javax.swing.JButton enter;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField orderlvlTF;
    private javax.swing.JTextField pCodeTF;
    private javax.swing.JTextField pNameTF;
    private javax.swing.JTextField quantifyTF;
    private javax.swing.JButton update_level;
    private javax.swing.JButton view_inventory;
    private javax.swing.JButton view_lowStocks;
    // End of variables declaration//GEN-END:variables
}
