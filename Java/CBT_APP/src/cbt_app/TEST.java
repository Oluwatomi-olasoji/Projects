/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package cbt_app;

import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Message;
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
public class TEST extends javax.swing.JFrame {
    
   public static TEST instance = null;
    public ArrayList<ArrayList<String>> GetTestThings(){
        ArrayList<ArrayList<String>> info = new ArrayList<>();
        info.add(Question);
        info.add(optionA);
        info.add(optionB);
        info.add(optionC);
        info.add(optionD);
        info.add(correctAnswer);
        info.add(yourOption);
        return info;
    }
    int i = 0;//for storing each question answer
    private class timer implements Runnable {

        int seconds = 0;
        public timer(int min)
        {
            seconds = min * 60;
            System.out.println("sec: " + String.valueOf(seconds));
        }
        
        @Override
        public void run() {

            while (seconds > 0) {
                seconds--;
                try {
                    sleep(1000);
                    Duration time = Duration.ofSeconds(seconds);
                    String hr = String.valueOf(time.toHoursPart());
                    String min = String.valueOf(time.toMinutesPart());
                    String sec = String.valueOf(time.toSecondsPart());
                    String timeText =  hr+ " hr " + min +" min "  +sec+ " sec " ;
                    jLabel2.setText(timeText);
                   
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if (seconds == 0) {//when the time is up should submit script 
                    JOptionPane.showMessageDialog(rootPane, "Time up!"); 
                    
                    if (jRadioButton3.isSelected()) { //code to save the last selected option
                       yourOption.set(i, jRadioButton3.getText());
                    } else if (jRadioButton4.isSelected()) {
                       yourOption.set(i, jRadioButton4.getText());

                    } else if (jRadioButton5.isSelected()) {
                       yourOption.set(i, jRadioButton5.getText());

                    } else if (jRadioButton6.isSelected()) {
                       yourOption.set(i, jRadioButton6.getText());
                    }
                    
                    submit();
                    setVisible(false);
                }
            }
        }
    }
    

    ArrayList<String> Question = new ArrayList<>();
    ArrayList<String> correctAnswer = new ArrayList<>();
    ArrayList<String> yourOption = new ArrayList<>();
    ArrayList<String> optionA = new ArrayList<>();
    ArrayList<String> optionB = new ArrayList<>();
    ArrayList<String> optionC = new ArrayList<>();
    ArrayList<String> optionD = new ArrayList<>();
    int testTime = 0;

    void setup() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbt_db", "root", "tomiolasoji"); //makes the driver conncet to
            PreparedStatement ps = con.prepareStatement("select * from test_layout");
            int numberOfQuestion = 0;
            

            ResultSet rs = ps.executeQuery();

            while (rs.next()) { //sets these variables the test parameters
                numberOfQuestion = rs.getInt("number_of_questions");
                testTime = rs.getInt("test_time");
                System.err.println(testTime);
            }

            ps = con.prepareStatement("select * from questions"); //sets questions and options
            rs = ps.executeQuery();
            int counter = 0;
            while (rs.next()) {
                counter++;
                Question.add(rs.getString("questions"));
                optionA.add(rs.getString("option_a"));
                optionB.add(rs.getString("option_b"));
                optionC.add(rs.getString("option_c"));
                optionD.add(rs.getString("option_d"));
                correctAnswer.add(rs.getString("correct_ans"));
                yourOption.add("");

                if (counter >= numberOfQuestion) { //this sets the number of questions picked according to lecturer specification
                    break;
                }

            }

            jTextArea1.setText((int)i+1 +". " + Question.get(i));
            jRadioButton3.setText(optionA.get(i));
            jRadioButton4.setText(optionB.get(i));
            jRadioButton5.setText(optionC.get(i));
            jRadioButton6.setText(optionD.get(i));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new form TEST
     */
    public TEST() {
        initComponents();
        setup();
        timer t1 = new timer(testTime);
        (new Thread(t1)).start();
        instance = this;
        
    }

    public void submit(){
       int score = 0;
        if (jRadioButton3.isSelected()) { //code to save the selected option
            yourOption.set(i, jRadioButton3.getText());
        } else if (jRadioButton4.isSelected()) {
            yourOption.set(i, jRadioButton4.getText());

        } else if (jRadioButton5.isSelected()) {
            yourOption.set(i, jRadioButton5.getText());

        } else if (jRadioButton6.isSelected()) {
            yourOption.set(i, jRadioButton6.getText());

        }

        for (int a = 0; a < Question.size(); a++) {
            String yanswer = yourOption.get(a);
            String canswer = correctAnswer.get(a);// TODO add your handling code here:
            if (yanswer == canswer) {
                score++;
            }
        }
        
        setVisible(false); 
        new view_script().setVisible(true);
        
            
            String matno = student_login.GetMatNo();
            String email = "";
            
            try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbt_db", "root", "tomiolasoji");
            PreparedStatement ps = con.prepareStatement("SELECT * FROM student WHERE mat_no=?");

            ps.setString(1, matno);
            ResultSet rs = ps.executeQuery();

            
            while (rs.next()) {
               email = rs.getString(8); //gets the correct password from database which is hasshes       
            }
            
        } catch (Exception e) {
     JOptionPane.showMessageDialog(rootPane, e);
     }      
        
            String subject = "CBT SCORE";        // sends score to email
            String body = "Congratutions your score for the cbt test was " + score;
            String senderEmail = "teloltsk@gmail.com";
            String senderPassword = "eaxrpeucjrxlbzia";
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

     

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
             Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            JOptionPane.showMessageDialog(rootPane, "Email sent");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, e);
        } 
        submit.setEnabled(false); //this allows submition to happen only once
        
        
        //Saves score to db 
        try{
        Class.forName("com.mysql.cj.jdbc.Driver"); //connects to the driver
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cbt_db", "root", "tomiolasoji"); //makes the driver conncet to
            PreparedStatement ps = con.prepareStatement("INSERT INTO score VALUES(?,?)");
            
            ps.setString(1, matno);
            ps.setInt(2, score);
            ps.executeUpdate();
        
        }catch(Exception e){
            e.printStackTrace();}
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        previous = new javax.swing.JButton();
        next = new javax.swing.JButton();
        submit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 255));

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("jRadioButton3");
        jRadioButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("jRadioButton4");
        jRadioButton4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setText("jRadioButton5");
        jRadioButton5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setText("jRadioButton6");
        jRadioButton6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        previous.setForeground(new java.awt.Color(255, 51, 255));
        previous.setText("Previous");
        previous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousActionPerformed(evt);
            }
        });

        next.setForeground(new java.awt.Color(255, 51, 255));
        next.setText("Next");
        next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextActionPerformed(evt);
            }
        });

        submit.setForeground(new java.awt.Color(255, 51, 255));
        submit.setText("Submit");
        submit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Goudy Old Style", 3, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 255));
        jLabel1.setText("Computer Based Test :P");

        jLabel2.setText("Timer");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jRadioButton3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(previous)
                                .addGap(18, 18, 18)
                                .addComponent(next)
                                .addGap(32, 32, 32)
                                .addComponent(submit))
                            .addComponent(jRadioButton6)
                            .addComponent(jRadioButton5)
                            .addComponent(jRadioButton4)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addComponent(jLabel1)))
                        .addGap(0, 26, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel1))
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(previous)
                    .addComponent(next)
                    .addComponent(submit))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void previousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousActionPerformed
        if (i == 0) {
            JOptionPane.showMessageDialog(null, "This is the first question");
        } else {

            if (jRadioButton3.isSelected()) { //code to save the selected option
                yourOption.set(i, jRadioButton3.getText());
            } else if (jRadioButton4.isSelected()) {
                yourOption.set(i, jRadioButton4.getText());

            } else if (jRadioButton5.isSelected()) {
                yourOption.set(i, jRadioButton5.getText());

            } else if (jRadioButton6.isSelected()) {
                yourOption.set(i, jRadioButton6.getText());

            }

            --i;
            jTextArea1.setText((int)i+1 +". " + Question.get(i));
            jRadioButton3.setText(optionA.get(i));
            jRadioButton4.setText(optionB.get(i));
            jRadioButton5.setText(optionC.get(i));
            jRadioButton6.setText(optionD.get(i));

            if (yourOption.get(i) == jRadioButton3.getText()) {
                jRadioButton3.setSelected(true);
            } else if (yourOption.get(i) == jRadioButton4.getText()) {
                jRadioButton4.setSelected(true);
            } else if (yourOption.get(i) == jRadioButton5.getText()) {
                jRadioButton5.setSelected(true);
            } else if (yourOption.get(i) == jRadioButton6.getText()) {
                jRadioButton6.setSelected(true);
            }
            
            
        }

    }//GEN-LAST:event_previousActionPerformed

    private void nextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextActionPerformed
        if (i == (Question.size() - 1)) {
            JOptionPane.showMessageDialog(null, "This is the last question");
        } else {
            if (jRadioButton3.isSelected()) { //code to save the selected option
                yourOption.set(i, jRadioButton3.getText());
            } else if (jRadioButton4.isSelected()) {
                yourOption.set(i, jRadioButton4.getText());

            } else if (jRadioButton5.isSelected()) {
                yourOption.set(i, jRadioButton5.getText());

            } else if (jRadioButton6.isSelected()) {
                yourOption.set(i, jRadioButton6.getText());

            }

            ++i;
            jTextArea1.setText((int)i+1 +". " + Question.get(i));
            jRadioButton3.setText(optionA.get(i));
            jRadioButton4.setText(optionB.get(i));
            jRadioButton5.setText(optionC.get(i));
            jRadioButton6.setText(optionD.get(i));
            buttonGroup1.clearSelection(); //this is to clear the selection of your previous option

            //this is to retain the option if already selected
            if (yourOption.get(i) == jRadioButton3.getText()) {
                jRadioButton3.setSelected(true);
            } else if (yourOption.get(i) == jRadioButton4.getText()) {
                jRadioButton4.setSelected(true);
            } else if (yourOption.get(i) == jRadioButton5.getText()) {
                jRadioButton5.setSelected(true);
            } else if (yourOption.get(i) == jRadioButton6.getText()) {
                jRadioButton6.setSelected(true);
            }
            
            
        }
    }//GEN-LAST:event_nextActionPerformed

    private void submitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitActionPerformed
     submit();
    }//GEN-LAST:event_submitActionPerformed

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
            java.util.logging.Logger.getLogger(TEST.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TEST.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TEST.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TEST.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TEST().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton next;
    private javax.swing.JButton previous;
    public javax.swing.JButton submit;
    // End of variables declaration//GEN-END:variables
}
