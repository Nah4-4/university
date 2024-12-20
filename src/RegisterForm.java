import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Month;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class RegisterForm implements ActionListener{
    private JDialog register;

    private JTextField fName,lName,matric,email;
    
    private JRadioButton male,female;

    private ButtonGroup gender;

    private JComboBox <Integer> day,year;
    private JComboBox <String> month,major;

    private JButton registerButton;

    RegisterForm(JFrame frame){
        Integer days[] ={1,2,3,4,5,6,7,8,9,10,
                     11,12,13,14,15,16,17,18,19,20,
                     21,22,23,24,25,26,27,28,29,30,31
                    };

        String months[] ={"January","February","March","April",
                          "May","June","July","August","September",
                          "October","November","December"
                        };
        Integer years[] ={1995,1996,1997,1998,1999,2000,2001,2002,2003,2004,2005,2006,
                      2007,2008
                    };
        String [] majors= {"Computer Science","Electrical Engineering","Business Administration","Mechanical Engineering","Architecture"};


        register = new JDialog();
        register.setTitle("Student Registration");
        register.getContentPane().setLayout(new GridLayout(8, 1));
        ((GridLayout) register.getContentPane().getLayout()).setVgap(10);

        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        email = new JTextField(15);
        emailPanel.add(new JLabel("E-MAIL:         "));
        emailPanel.add(email);

        JPanel firstnamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fName = new JTextField(15);
        firstnamePanel.add(new JLabel("First-Name:   "));
        firstnamePanel.add(fName);

        JPanel lastnamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lName = new JTextField(15);
        lastnamePanel.add(new JLabel("Last-Name:   "));
        lastnamePanel.add(lName);

        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gender= new ButtonGroup(); 
        male = new JRadioButton("Male",true);
        female = new JRadioButton("Female");
        gender.add(male);
        gender.add(female);
        checkboxPanel.add(new JLabel("Gender:"));
        checkboxPanel.add(male);
        checkboxPanel.add(female);

        JPanel matricPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        matric = new JTextField(15);
        matricPanel.add(new JLabel("Matric Grade:"));
        matricPanel.add(matric);
        
        JPanel dateofbirthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        day = new JComboBox<>(days);
        day.setMaximumRowCount(4);
        day.setEditable(false);

        month = new JComboBox<>(months);
        month.setMaximumRowCount(4);
        month.setEditable(false);

        year = new JComboBox<>(years);
        year.setMaximumRowCount(4);
        year.setEditable(false);

        dateofbirthPanel.add(new JLabel("Birth Date:"));
        dateofbirthPanel.add(day);
        dateofbirthPanel.add(month);
        dateofbirthPanel.add(year);

        
        major = new JComboBox<>(majors);
        major.setMaximumRowCount(4);
        JPanel majorPanel=new JPanel();
        JLabel majorLabel=new JLabel("Major:    ");
        majorPanel.add(majorLabel);
        majorPanel.add(major);
        major.setEditable(false);
        
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        registerButton = new JButton("Apply");
        registerButton.addActionListener(this);
        registerPanel.add(registerButton);
        
        register.add(firstnamePanel);
        register.add(lastnamePanel);
        register.add(matricPanel);
        register.add(emailPanel);
        register.add(majorPanel);
        register.add(checkboxPanel);
        register.add(dateofbirthPanel);
        register.add(registerPanel);
        
        register.setLocation(frame.getX()+210, frame.getY()+160);
        register.setModal(true);
        register.pack();
        register.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == registerButton){
            if(fName.getText().equals("") || lName.getText().equals("")  || email.getText().equals("")  || matric.getText().equals("")  || !isNumber(matric.getText()) ||  Integer.parseInt(matric.getText()) > 700  || Integer.parseInt(matric.getText()) < 0){
                JOptionPane.showMessageDialog(null, "INVALID INPUT", "INVALID", JOptionPane.ERROR_MESSAGE);
            }
            else {
                try {
                    // Load the MySQL JDBC driver
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    // Establish a connection
                    try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha_uni", "root", "")) {
                        String query = "INSERT INTO temp (fname,lname,gender,email,birthdate,12result,major) VALUES(?,?,?,?,?,?,?)";
                        PreparedStatement pst = con.prepareStatement(query);
                        String selectedMonth=(String) month.getSelectedItem();
                        Month month = Month.valueOf(selectedMonth.toUpperCase());
                        int monthNumber = month.getValue();
                        
                            pst.setString (1, fName.getText());
                            pst.setString(2, lName.getText());
                            if(male.isSelected())pst.setString(3, male.getText());
                            else pst.setString(3, female.getText());
                            pst.setString(4, email.getText());
                            pst.setString(5, day.getSelectedItem()+"-"+monthNumber+"-"+year.getSelectedItem());
                            pst.setInt(6, Integer.parseInt(matric.getText()));
                            pst.setString(7, (String)major.getSelectedItem());
                            pst.executeUpdate();
                            fName.setText("");lName.setText("");email.setText("");matric.setText("");
                        }
                }catch (ClassNotFoundException | SQLException ex) {
                    JOptionPane.showMessageDialog(null, "DATABASE ERROR", "INVALID", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
            
    }
        
    public boolean isNumber(String input) {
        try {
            // Try to parse the input as an integer
            Integer.parseInt(input);
            return true; // Parsing successful, it's a number
        } catch (NumberFormatException e) {
            return false; // Parsing failed, it's not a number
        }
    }
    
}
