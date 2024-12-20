import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class StudentPage implements ActionListener {
    int row=0;
    JCheckBox c[] ;
    String studata;
    String id;
    JPanel bottomJPanel, studentPanel,tablePanel,studPage,top,addPanel,add;
    JLabel studentLabel, idLabel,fLabel,lLabel,BDLabel,GLabel,email,dbid,dbfname,dblname,dbBD,dbG,dbemail;
    JTable table ; 
    JButton exitbutton,backbutton,addbutton;
    ResultSet rs1,rs2;
    DefaultTableModel model = new DefaultTableModel();
    CardLayout card;
    JTabbedPane stu ;
    private String pass=System.getenv("PASSWORD");


    public void database(){
        try{        
            Class.forName("com.mysql.cj.jdbc.Driver");
            try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha_uni", "root", pass)){
                String query = "SELECT * FROM student WHERE student_id = ?";
                String q1 = """
                            SELECT course.course_id,course_name,mark
                            FROM student 
                            join student_course on student.student_id = student_course.student_id
                            join course on course.course_id = student_course.course_id
                            where student.student_id = ?;""";
                try(PreparedStatement pst = con.prepareStatement(query);
                    PreparedStatement pst1 = con.prepareStatement(q1)){
                    pst1.setString(1, id);
                    pst.setString(1, id);
                    rs2 = pst1.executeQuery();
                    rs1 = pst.executeQuery();
                    if(rs1.next()){
                        dbid = new JLabel(rs1.getString("student_id"));
                        dbfname = new JLabel(rs1.getString("student_fname"));
                        dblname = new JLabel(rs1.getString("student_lname"));
                        dbBD = new JLabel(rs1.getString("student_birthdate"));
                        dbG = new JLabel(rs1.getString("student_gender"));
                        dbemail = new JLabel(rs1.getString("student_email"));
                    }
                    
                    while(rs2.next()){
                        String data[]={rs2.getString("course_id"),rs2.getString("course_name"),rs2.getString("mark")};
                        model.addRow(data);
                    }
                }
                
            }
        }
        catch(ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }
//insert data into the JTable 

    public StudentPage(String id,CardLayout card,JPanel top) {
        this.id = id;
        this.card=card;
        this.top=top;
        
        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.getTableHeader().setFont( new Font( "Roboto" , Font.BOLD, 15 ));
        model.addColumn("Course-ID");
        model.addColumn("Course-Name");
        model.addColumn("Mark");
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 380));
        database();
        
        studentLabel = new JLabel();
        studentLabel.setText("Student Information");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 27 ));

        idLabel = new JLabel("ID: ");
        fLabel = new JLabel("First-Name: ");
        lLabel = new JLabel("Last-Name: ");
        BDLabel = new JLabel("Birth-Date: ");
        GLabel = new JLabel("Gender: ");
        email = new JLabel("E-mail: ");

        Font font1=new Font("Arial", Font.BOLD, 15);
        Font font2=new Font("Arial", Font.PLAIN, 15);
        
        studentPanel = new JPanel(null);
        //"student info"
        studentLabel.setBounds(200, 5, 300, 50);
        studentPanel.add(studentLabel);
        //ID
        idLabel.setBounds( 95, 65, 100, 50);
        idLabel.setFont(font1);
        studentPanel.add(idLabel);
        dbid.setBounds( 125, 65, 100, 50);
        dbid.setFont(font2);
        studentPanel.add(dbid);//database
        //First name
        fLabel.setBounds( 215, 65, 100, 50);
        fLabel.setFont(font1);
        studentPanel.add(fLabel);
        dbfname.setBounds( 310, 65, 100, 50);
        dbfname.setFont(font2);
        studentPanel.add(dbfname);//database
        //Last name
        lLabel.setBounds( 400, 65, 100, 50);
        lLabel.setFont(font1);
        studentPanel.add(lLabel);
        dblname.setBounds( 495, 65, 100, 50);
        dblname.setFont(font2);
        studentPanel.add(dblname);
        //Gender
        GLabel.setBounds( 65, 105, 100, 50);
        GLabel.setFont(font1);
        studentPanel.add(GLabel);
        dbG.setBounds( 130, 105, 100, 50);
        dbG.setFont(font2);
        studentPanel.add(dbG);//database
        //DoB
        BDLabel.setBounds( 205, 105, 100, 50);
        BDLabel.setFont(font1);
        studentPanel.add(BDLabel);
        dbBD.setBounds(295, 105, 100, 50);
        dbBD.setFont(font2);
        studentPanel.add(dbBD);//database
        //Email
        email.setBounds( 395, 105, 100, 50);
        email.setFont(font1);
        studentPanel.add(email);
        dbemail.setBounds( 450, 105, 200, 50);
        dbemail.setFont(font2);
        studentPanel.add(dbemail);//database
        tablePanel = new JPanel(new GridBagLayout());
        table.setEnabled(false);
        
        JPanel buttonPanel = new JPanel();
        exitbutton = new JButton("EXIT");
        exitbutton.addActionListener(this);
        backbutton = new JButton("BACK");
        backbutton.setPreferredSize(new Dimension(100,35));;
        exitbutton.setPreferredSize(new Dimension(100,35));;
        backbutton.addActionListener(this);
        buttonPanel.add(backbutton);
        buttonPanel.add(exitbutton);
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets =new Insets(2,10,5,10);
        tablePanel.add(scrollPane,gbc);

        
        studentPanel.setPreferredSize(new Dimension(700, 160));
        stu = new JTabbedPane();
        studPage=new JPanel();
        studPage.add(studentPanel, BorderLayout.NORTH);
        studPage.add(tablePanel,BorderLayout.CENTER);
        studPage.add(buttonPanel,BorderLayout.SOUTH);
        stu.addTab("info", studPage);
        add();
        stu.addTab("add", add);
    }
    void add(){
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        addbutton = new JButton("ADD");
        addbutton.addActionListener(this);
        add = new JPanel(new BorderLayout());
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String q = """
                SELECT c.course_id, c.course_name
                FROM course c
                JOIN majors m ON c.major_id = m.major_id
                LEFT JOIN student_course sc ON c.course_id = sc.course_id AND sc.student_id = ?
                WHERE m.major_id = (SELECT major_id FROM student WHERE student_id = ?)
                AND sc.student_id IS NULL;
                    """;
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha_uni", "root", "")) {
                try (PreparedStatement pst = con.prepareStatement(q)) {
                    int i =0;
                    pst.setString(1, id);
                    pst.setString(2, id);
                    ResultSet count = pst.executeQuery();
                    while (count.next()) {
                        row++;
                    }
                    ResultSet rs = pst.executeQuery();
                    c = new JCheckBox[row];
                    while(rs.next()){
                        c[i] = new JCheckBox();
                        c[i].setText(rs.getString("course_name"));
                        c[i].setFont(new Font("Arial", Font.PLAIN, 20));
                        box.add(c[i]);
                        i++;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        add.add(box);
        add.add(addbutton,BorderLayout.SOUTH);
    }

    void addcourse(){
        int cid = 0;
        String q="INSERT into student_course (student_id ,course_id,mark) VALUES (?,?,0);";
        String q1="SELECT course_id from course where course_name = ?";
        for(int i = 0 ;i<row ; i++){
            if(c[i].isSelected()){
                try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha_uni", "root", "")) {
                    try(PreparedStatement statement = con.prepareStatement(q1) ){
                        statement.setString(1, c[i].getText());
                        try (ResultSet rs = statement.executeQuery()) {
                            if(rs.next()){
                                cid=rs.getInt("course_id");
                            }
                        }
                    }
                    try (PreparedStatement pst = con.prepareStatement(q)) {
                        pst.setString(1, id);
                        pst.setInt(2, cid);
                        pst.executeUpdate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == exitbutton){
            System.exit(0);
        }
        else if(e.getSource() == backbutton){
            card.show(top,"login");
        }
        else if (e.getSource() == addbutton){
            addcourse();
            stu.setSelectedIndex(0);
            model.setRowCount(0);
            database();
            add();
        }
    }
   
}
