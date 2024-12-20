import javax.swing.*;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LoginForm implements ActionListener {
    private JTextField idField;
    private JPasswordField passwordField;
    private JFrame frame;
    private JButton loginButton,registerButton,changeButton,forgetButton;
    private boolean isDark = false;
    private JPanel center,top;
    private JLabel topLabel,loginLabel; 
    private ButtonGroup you;
    private JRadioButton admin,student;
    private ImageIcon icon=new ImageIcon("light.png");
    private String pass=System.getenv("PASSWORD");

    CardLayout card=new CardLayout();
    LoginForm() {
        frame = new JFrame();
        center = new JPanel(new GridBagLayout());
        
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets =new Insets(1,10,20,10);
        gbc.anchor = GridBagConstraints.NORTH;

        //Labels
        topLabel = new JLabel("ALPHA UNIVERSITY");
        topLabel.setFont(new Font("Arial", Font.BOLD, 25));
        topLabel.setForeground(Color.RED);
        center.add(topLabel,gbc);
    
        gbc.gridy = 2;
        loginLabel = new JLabel("Log-in");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 20));
        center.add(loginLabel,gbc);

        //Student id
        gbc.insets =new Insets(20,10,1,10);
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        center.add(new JLabel("Student- ID:"), gbc);
        //
        gbc.insets =new Insets(1,10,10,10);
        idField = new JTextField(20);
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(idField, gbc);

        //Password
        gbc.insets =new Insets(12,10,1,10);
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        center.add(new JLabel("Password:"), gbc);
        //
        gbc.insets =new Insets(1,10,20,10);
        passwordField = new JPasswordField(20);
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(passwordField, gbc);

        //radioButton
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =new Insets(1,10,1,10);
        you=new ButtonGroup();
        student=new JRadioButton("Student",true);
        admin=new JRadioButton("Admin");
        you.add(student);
        you.add(admin);
        gbc.gridy=8;
        center.add(new JLabel("You are:"),gbc);
        gbc.gridy=9;
        center.add(student,gbc);
        gbc.gridy=10;
        center.add(admin,gbc);
        
        //Buttons
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets =new Insets(20,10,0,10);
        loginButton = new JButton("Log in");
        loginButton.addActionListener(this);
        gbc.gridy = 12;
        loginButton.setPreferredSize(new Dimension(160,28));
        center.add(loginButton, gbc);

        gbc.insets =new Insets(0,10,0,10);
        forgetButton = new JButton("Forgot Password");
        forgetButton.setContentAreaFilled(false);
        forgetButton.setForeground(Color.BLUE);
        forgetButton.addActionListener(this);
        gbc.gridy = 13;
        center.add(forgetButton, gbc);
        
        gbc.insets =new Insets(10,10,5,10);
        gbc.gridy=14;
        center.add(new JLabel("OR"), gbc);
        registerButton=new JButton("Register");
        registerButton.addActionListener(this);
        gbc.gridy=15;
        registerButton.setPreferredSize(new Dimension(150,25));
        center.add(registerButton,gbc);
        
        
        //theme change
        changeButton= new JButton(icon);
        changeButton.addActionListener(this);
        JPanel panel = new JPanel(null);
        changeButton.setBounds( 3, 2, 45,43);
        panel.add(changeButton);
        
        //add center to north
        top=new JPanel(card);
        panel.setBounds(650,10,50,45);
        frame.add(panel);
        top.add(center,"login");
        frame.add(top);

        frame.setSize(750, 680);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)  {
        char [] passwordchar = passwordField.getPassword();
        String password = new String(passwordchar);
        
        if(e.getSource() == loginButton){
            if(student.isSelected()){
                loginDataBase();
            }
        
            else if(admin.isSelected() ){
                if(("a").equals(idField.getText()) && ("a").equals(password)){
                    top.add(new admin(card,top).adminPanel,"adminPage");
                    card.show(top,"adminPage");
                }
                else{
                    JOptionPane.showMessageDialog(null, "INVALID INPUT", "INVALID", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if(e.getSource() == registerButton){
            new RegisterForm(frame);
        }
        if(e.getSource() == forgetButton){
            emailpass();
        }
        if(e.getSource() == changeButton){
            isDark=! isDark;
            try {
                UIManager.setLookAndFeel(isDark ?  new FlatMacDarkLaf() :new FlatMacLightLaf());
                com.formdev.flatlaf.FlatLaf.updateUI();
            } 
            catch (UnsupportedLookAndFeelException a) {
                a.printStackTrace();
            }   
        }
    }

    void emailpass(){
        JDialog forgot = new JDialog ();
        JPanel panel= new JPanel(null);
        JTextField T = new JTextField(20);
        JButton b = new JButton();
        b.setText("Get password");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{        
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha_uni", "root", "")){
                        String query = "SELECT student_email FROM student WHERE student_id = ?";
                        String q1 = "SELECT password FROM stud_password WHERE student_id = ?";
                        try(PreparedStatement pst = con.prepareStatement(query);
                            PreparedStatement pst1 = con.prepareStatement(q1)){
                            pst.setInt(1, Integer.parseInt(T.getText()));
                            pst1.setInt(1, Integer.parseInt(T.getText()));
                            ResultSet rs = pst.executeQuery();
                            ResultSet rs1 = pst1.executeQuery();
                            if(rs.next() && rs1.next()){
                                //works perfectly
                                //new GMailer(rs.getString("student_email"),"Alpha University","Your password is "+rs1.getString("password"));
                                JOptionPane.showMessageDialog(null, "we have sent your password to your email", "Message", JOptionPane.ERROR_MESSAGE);
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "ID DOESN'T EXIST", "ERROR", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
                catch( Exception ex){
                    JOptionPane.showMessageDialog(null, "INVALID INPUT", "INVALID", JOptionPane.ERROR_MESSAGE);
                }
            }
            } );
        JLabel l=new JLabel("STUDENT-ID: ");
        l.setBounds(10,5,200,30);
        panel.add(l);
        T.setBounds(20,34,200,25);
        panel.add(T);
        b.setBounds(0,80,241,30);
        panel.add(b);
        panel.setPreferredSize(new Dimension(240,110));
        forgot.add(panel);
        forgot.setTitle("Password");
        forgot.setResizable(false);
        forgot.setLocation(frame.getX()+250,frame.getY()+300);
        forgot.setModal(true);
        forgot.pack();
        forgot.setVisible(true);
    }

    public void loginDataBase() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
    
            // Establish a connection
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha_uni", "root", pass)) {
                
                // Prepare the statement with a parameterized query
                char [] passwordchar = passwordField.getPassword();
                String password = new String(passwordchar);
                String query = "SELECT student_id,password FROM stud_password WHERE student_id = ? AND password = ?";
                try (PreparedStatement pst = con.prepareStatement(query)) {
                    
                    pst.setString(1, idField.getText());
                    pst.setString(2, password);
                    try (ResultSet rs = pst.executeQuery()) {
                        if(rs.next()){
                            if (rs.getString("student_id").equals(idField.getText()) && rs.getString("password").equals(password)) {  
                                top.add(new StudentPage(rs.getString("student_id"),card,top).stu,"studentPage");
                                card.show(top,"studentPage");
                            }
                        }
                        else{
                             JOptionPane.showMessageDialog(null, "INVALID INPUT", "INVALID", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error with connection"); // Handle exceptions appropriately
            e.printStackTrace();
        }
    }
    
    
}
    



