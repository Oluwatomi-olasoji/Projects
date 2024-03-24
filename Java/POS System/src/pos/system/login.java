/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pos.system;

import java.awt.List;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

/**
 *
 * @author telol
 */
public class login extends javax.swing.JFrame {
    String staff_name;
        private static boolean isToday(java.sql.Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
        
    }

    public String Hash(String c) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update((new String(c)).getBytes("UTF8"));
            String passHash = new String(msgDigest.digest());
            return passHash;
        } catch (Exception ex) {

            return c;
        }

    }

    public static ArrayList<String> findBirthdays() {
        ArrayList<String> birthdays = new ArrayList<>();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            String sql = "SELECT staff_name, dob FROM staff_info";

            // Getting the current date
            //LocalDate today = LocalDate.now();
           // day = today.getDayOfMonth();
            //month = today.getMonthValue();

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date birthdayDate = dateFormat.parse(rs.getString("dob"));

                // Convert java.util.Date to java.sql.Date
                java.sql.Date sqlBirthdayDate = new java.sql.Date(birthdayDate.getTime());
                if ((isToday(sqlBirthdayDate))) {
                    birthdays.add(rs.getString("staff_name"));
                }
                
            }
        } catch (Exception e) {

            e.printStackTrace();
            throw new RuntimeException("Error fetching birthdays", e);
        }
        System.out.println(birthdays);
        return birthdays;
        
    }
     public static void sendEmail(ArrayList<String> names) {
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
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            String sql = "SELECT email,staff_name FROM staff_info";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Iterate over the result set and send emails
            while (rs.next()) {
                String toEmail = rs.getString("email");
                String sname = rs.getString("staff_name");
                // Create message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Birthday Notification");
                StringBuilder emailContent = new StringBuilder("Dear" + sname + "\n\n");
                emailContent.append("Don't forget too wish someone a happy birthday!\n\n");
                emailContent.append("Today's birthday celebrants are:\n");
                for (String name : names) {
                    emailContent.append("- ").append(name).append("\n");
                }
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
     
     
    

    /**
     * Creates new form login
     */
    public login() {
        initComponents();
        findBirthdays();
        sendEmail(findBirthdays());
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
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        emailTF3 = new javax.swing.JTextField();
        passwordTF3 = new javax.swing.JTextField();
        login = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(153, 153, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 0, 18)); // NOI18N
        jLabel1.setText("PAU COOPERATIVE POS SYSTEM");

        jLabel3.setText("Email:");

        jLabel4.setText("Password:");

        emailTF3.setText(" ");
        emailTF3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailTF3ActionPerformed(evt);
            }
        });

        passwordTF3.setText(" ");

        login.setText("Login");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(login)
                .addGap(174, 174, 174))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(emailTF3, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(passwordTF3, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(40, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(61, 61, 61))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(64, 64, 64)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(emailTF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordTF3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(30, 30, 30)
                .addComponent(login)
                .addContainerGap(103, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void emailTF3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailTF3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailTF3ActionPerformed

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        try {
            String email = emailTF3.getText();
            String password1 = passwordTF3.getText();
            String password = Hash(password1);
            String department = "";

            Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos_db", "root", "tomiolasoji");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM staff_info WHERE email=?");

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            String cpassword = "";
            while (rs.next()) {
                cpassword = rs.getString(7);//gets the correct password from database which is hashed
                department = rs.getString(5);
                staff_name = rs.getString(2);
               // System.out.println(staff_name);
            }
            //  }
            if (password.equals(cpassword)) {

                if (department.equalsIgnoreCase("IT")) {
                    setVisible(false);
                    new Staff_registration().setVisible(true);
                } else if (department.equalsIgnoreCase("Sales - Cashier")) {
                    setVisible(false);
                    new Cashier(email,staff_name).setVisible(true);
                }else if (department.equalsIgnoreCase("Sales - Manager")) {
                    setVisible(false);
                    new sales_manager_homepg().setVisible(true);
                } else if (department.equalsIgnoreCase("Inventory/Stocks")) {
                    setVisible(false);
                    new inventory_homepage().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Department unrecognized");
                }

            } else {
                JOptionPane.showMessageDialog(rootPane, "Invalid login credentials");
            }
            

        
    } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }     // TODO add your handling code here:
    }//GEN-LAST:event_loginActionPerformed

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
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField emailTF3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton login;
    private javax.swing.JTextField passwordTF3;
    // End of variables declaration//GEN-END:variables
}
