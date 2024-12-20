import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class admin implements ActionListener{
    
    String sql[]= new String[3];
    JPanel adminPanel,mainPanel,backPanel,buttonPanel,top;
    JButton view,accept,edit,back,exit;
    CardLayout card=new CardLayout();
    CardLayout cards;
    private String pass=System.getenv("PASSWORD");
    
    admin(CardLayout cards,JPanel top){
        sql[0] = "jdbc:mysql://localhost:3306/alpha_uni";
        sql[1] = "root";
        sql[2] = pass;
        this.top=top;
        this.cards=cards;
        adminPanel=new JPanel(card);
        buttonPanel=new JPanel(null);

        JLabel label=new JLabel("Adminstration Page");
        label.setFont(new Font("Roboto", Font.BOLD, 30));
        label.setBounds(235,60,500,50);

        view = new JButton("View  Students");
        view.setBounds(270,200,200,36);
        view.setFont(new Font("Roboto", Font.PLAIN, 15));
        view.addActionListener(this);

        accept = new JButton("Accept  Students");
        accept.setBounds(270,280,200,36);
        accept.setFont(new Font("Roboto", Font.PLAIN, 15));
        accept.addActionListener(this);

        back = new JButton("Back");
        back.setBounds(240,500,100,36);
        back.setFont(new Font("Roboto", Font.PLAIN, 13));
        back.addActionListener(this);
        
        exit = new JButton("Exit");
        exit.addActionListener(this);
        exit.setFont(new Font("Roboto", Font.PLAIN, 13));
        exit.setBounds(380,500,100,36);
        
        buttonPanel.add(back);
        buttonPanel.add(exit);
        buttonPanel.add(view);
        buttonPanel.add(label);
        buttonPanel.add(accept);

        adminPanel.add(buttonPanel,"main");
        card.show(adminPanel,"main");
    }

    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == view){
            new view(adminPanel,card,sql);
        }
        else if(e.getSource() == accept){
            new accept(adminPanel,card,sql);
        }
        if (e.getSource() == back) {
            cards.show(top,"login");
        } else if(e.getSource() == exit) {
            System.exit(0);
        }
    }

}


class accept implements ActionListener{
    int sid=0;
    int cid=0;
    Random random = new Random();
    String courseString[]=new String[50];
    String sql[];
    String fname,lname,gender,email,birth, major;
    int id;
    JCheckBox c[] = new JCheckBox[10];
    JButton accept,deny,back,exit;//work with the buttons
    JPanel course,acceptPanel,adminPanel,buttonPanel;
    CardLayout card;
    JDialog dialog;

    accept(JPanel adminPanel,CardLayout card,String []sql){
        this.adminPanel=adminPanel;
        this.card=card;
        this.sql=sql;
        try{
            acceptstud();
        } 
        catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
    boolean x=false;
    void acceptstud()throws SQLException{
        JPanel panel = new JPanel(null);
        JLabel acceptLabel=new JLabel("Accept Students");
        JLabel empty=new JLabel("No New Registered Students");
        acceptLabel.setFont(new Font("Arial", Font.BOLD, 27 ));
        empty.setFont(new Font("Arial", Font.PLAIN, 20 ));
        acceptPanel =new JPanel(new BorderLayout());
        adminPanel.add(acceptPanel,"first");
        // Prepare and execute query
        Connection connection =  DriverManager.getConnection(sql[0], sql[1], sql[2]);
        String query = """
            SELECT * FROM temp 
            JOIN majors on temp.major= majors.major_name
            """;
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = statement.executeQuery(query);
        x=(!rs.next());
        rs.beforeFirst();
        
        // Create and add buttons dynamically
        int i=1,y=80;
        while (rs.next()) {
            // button.setBounds(70,y,550,30);
            String firstName = rs.getString("fname");
            String lname = rs.getString("lname");
            String gender = rs.getString("gender");
            String email = rs.getString("email");
            String birthdate = rs.getString("birthdate");
            int result = rs.getInt("12result");
            String major = rs.getString("major");
            int id = rs.getInt("major_id");
            JButton button = new JButton(i+" . "+firstName.toUpperCase()+"            CLICK FOR DETAIL.....");
            button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle button click specific to this first name
                try {
                    acceptform(firstName, lname, gender, email, birthdate, result, major,id);
                } catch (SQLException e1) {}
                System.out.println("Clicked button for " + firstName);
            }
            });
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setBounds(70,y,550,30);
            panel.add(button);
            i++;
            y+=35;
        }
        if (x) {
            empty.setBounds(230,150,300,30);
            panel.add(empty,BorderLayout.CENTER);            
        }
        acceptLabel.setBounds(250,10,300,30);
        panel.add(acceptLabel,BorderLayout.NORTH);
        buttonPanel =new JPanel();
        exit = new JButton("EXIT");
        exit.setPreferredSize(new Dimension(85,27));
        exit.addActionListener(this);
        back = new JButton("BACK");
        back.setPreferredSize(new Dimension(85,27));
        back.addActionListener(this);
        buttonPanel.add(back);
        buttonPanel.add(exit);

        panel.setPreferredSize(new Dimension(740,120));
        acceptPanel.add(panel);
        acceptPanel.add(buttonPanel,BorderLayout.SOUTH);
        card.show(adminPanel,"first");
        // Close resources
        rs.close();
        statement.close();
        connection.close();
    }
    void courseData(boolean x) throws SQLException{
        int i =0;
        String query = """
            SELECT course_name 
            FROM course
            JOIN majors on majors.major_id = course.major_id 
            where majors.major_id = ?
                        """;
        
        
        // query = "SELECT course_name FROM course";
        Connection connection =  DriverManager.getConnection(sql[0], sql[1], sql[2]);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    c[i] = new JCheckBox(rs.getString("course_name"));
                    i++;
                }
            } 
        }
        catch (SQLException e) {
            e.printStackTrace();
        }          
    }

    void acceptform(String fname,String lname,String gender,String email,String birth,int matric,String major,int id) throws SQLException{
        this.fname=fname;this.lname=lname;this.gender=gender;this.email=email;this.birth=birth;this.major=major;this.id=id;
        JPanel acceptPanel1=new JPanel(new BorderLayout());
        adminPanel.add(acceptPanel1,"accept");
        card.show(adminPanel,"accept");
        dialog = new JDialog();
        JPanel p1,p2 ;
        course = new JPanel(new FlowLayout());
        
        try {
            boolean x =false;
            courseData(x);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        
        p1 = new JPanel();
        p2 = new JPanel();
        accept = new JButton("Accept");
        accept.addActionListener(this);
        deny = new JButton("Deny");
        deny.addActionListener(this);

        Font font = new Font("Arial", Font.PLAIN, 14);
        p1.setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        
        gbc.insets =new Insets(10,10,10,10);
        JLabel name=new JLabel("Name: "+fname+" "+lname);
        name.setFont(font);
        p1.add(name,gbc);
        gbc.gridx=1;
        gbc.gridy=0;
        JLabel genderL=new JLabel("Gender: "+gender);
        genderL.setFont(font);
        p1.add(genderL,gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        JLabel emailL=new JLabel("Email: "+email);
        emailL.setFont(font);
        p1.add(emailL,gbc);
        gbc.gridx=1;
        gbc.gridy=1;
        JLabel Bday=new JLabel("BirthDate: "+birth);
        Bday.setFont(font);
        p1.add(Bday,gbc);
        gbc.gridx=0;
        gbc.gridy=2;
        JLabel matricL=new JLabel("Matric: "+matric);
        matricL.setFont(font);
        p1.add(matricL,gbc);
        gbc.gridx=1;
        gbc.gridy=2;
        JLabel majorL=new JLabel("Major: "+major);
        majorL.setFont(font);
        p1.add(majorL,gbc);
        //
        int k =0;
        int f =0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =new Insets(20,10,10,10);

        for(int j =0;j<10;j++){
            gbc.gridx=0+f;
            f++;
            gbc.gridy=3+k;
            if ((j+1) % 2 == 0) {
                k++;
                f=0;
            }
            p1.add(c[j],gbc);
        }
        p2.add(accept);
        p2.add(deny);
        //
        acceptPanel1.add(p1);
        acceptPanel1.add(p2,BorderLayout.SOUTH);
    }
    void courseCheck(){
        for(int i=0;i<10;i++){
            if(c[i].isSelected()){
            String query = "SELECT student_id FROM alpha_uni.student ORDER BY student_id DESC LIMIT 1;";
            String q1="SELECT course_id from course where course_name = ?";
            String q2="INSERT into student_course (student_id ,course_id,mark) VALUES (?,?,0);";
            Connection connection;
            try {
                connection = DriverManager.getConnection(sql[0], sql[1], sql[2]);
                try (PreparedStatement statement = connection.prepareStatement(query) ) {
                    try (ResultSet rs = statement.executeQuery()) {
                        if(rs.next()){
                            sid=rs.getInt("student_id");
                        }
                    }
                }
                try(PreparedStatement statement = connection.prepareStatement(q1) ){
                    statement.setString(1, c[i].getText());
                    try (ResultSet rs = statement.executeQuery()) {
                        if(rs.next()){
                            cid=rs.getInt("course_id");
                            System.out.println(cid);
                        }
                    }
                }
                try(PreparedStatement statement = connection.prepareStatement(q2) ){
                    statement.setInt(1, sid);
                    statement.setInt(2, cid);
                    statement.executeUpdate();
                }
                
            } catch (SQLException e1) {
                e1.printStackTrace();
            }              
            }
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean x=false;
        if (e.getSource() == back) {
            card.show(adminPanel,"main");
        } else if(e.getSource() == exit) {
            System.exit(0);
        }
        if(e.getSource() == accept){
            String query = "INSERT into student(major_id,student_fname,student_lname,student_gender,student_birthdate,student_email) values(?,?,?,?,?,?)";
            String q1 = "INSERT into stud_password(student_id,password) values(?,?)";
            Connection connection;
            try {
                connection = DriverManager.getConnection(sql[0], sql[1], sql[2]);
                try (PreparedStatement statement = connection.prepareStatement(query);
                PreparedStatement s1 = connection.prepareStatement(q1)
                ) {
                    int pass=(random.nextInt(1000)+1);
                    statement.setInt(1, id);
                    statement.setString(2, fname);
                    statement.setString(3, lname);
                    statement.setString(4, gender);
                    statement.setString(5, birth);
                    statement.setString(6, email);
                    statement.executeUpdate();
                    courseCheck();
                    s1.setInt(1,sid);
                    s1.setString(2,pass+"");
                    s1.executeUpdate();
                    //new GMailer(email,"ALPHA UNIVERSITY","We would like to inform you that you have been admitted to our university, your password: "+pass+" ID- "+sid);
                    System.out.println("accepted");
                    x=true;
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Choose CourseF", "INVALID", JOptionPane.ERROR_MESSAGE);
            }              
        }
        if(e.getSource() == deny || x){
            if(x=true){
                try {
                    String query = "DELETE FROM temp where id=?";
                    Connection connection;
                try {
                    connection = DriverManager.getConnection(sql[0], sql[1], sql[2]);
                    PreparedStatement s1 = connection.prepareStatement("Select id from temp where fname=?");
                    s1.setString(1,fname);
                    ResultSet rs = s1.executeQuery();
                    try (PreparedStatement statement = connection.prepareStatement(query) ) {
                        if(rs.next()){
                            statement.setInt(1, rs.getInt("id"));
                            statement.executeUpdate();
                            acceptstud();
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                }catch (Exception e1) {
                    e1.printStackTrace();
                } 
            }
            else{
                String query = "DELETE FROM temp where id=?";
                Connection connection;
                try {
                    connection = DriverManager.getConnection(sql[0], sql[1], sql[2]);
                    PreparedStatement s1 = connection.prepareStatement("Select id from temp where fname=?");
                    s1.setString(1,fname);
                    ResultSet rs = s1.executeQuery();
                    try (PreparedStatement statement = connection.prepareStatement(query) ) {
                        if(rs.next()){
                            statement.setInt(1, rs.getInt("id"));
                            statement.executeUpdate();
                            acceptstud();
                            //new GMailer(email,"ALPHA UNIVERSITY","We would like to inform you that you have been rejected to our university");
                            System.out.println("denied");
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }   
            }           
        }
    }
}




class view implements ActionListener{

    JPanel buttonPanel,viewPanel,adminPanel,topPanel;
    JButton back,exit,filterButton,apply,remove,all;
    JTable table;
    ResultSet rs2;
    DefaultTableModel tbl;
    JScrollPane sc;
    JTextField filterNameInput,filterIdInput;
    CardLayout card;
    String sql[];
    String columns[]={"ID","First Name","Last Name","Gender","Birth-Date","Email","Course-Name","Mark"};
    String q1;
    int x=0;
    String courseString[]=new String[51];
    JComboBox <String> filterCourse;
    CustomTableModel cust;
    
    view(JPanel adminPanel,CardLayout card,String []sql){
        this.card=card;
        this.adminPanel=adminPanel;
        this.sql=sql;
        viewstud();
    } 
    
    
    void viewstud(){
        courseString[0]=" ";
        viewPanel=new JPanel(new BorderLayout());
        adminPanel.add(viewPanel,"view");
        card.show(adminPanel,"view");

        //
        JLabel filterLabel = new JLabel("Filter: ");
        filterLabel.setBounds(90,75,50, 10);
        JLabel filterIdLabel = new JLabel("Stud ID: ");
        filterIdLabel.setBounds(90,85, 60, 25);
        filterIdInput = new JTextField();
        filterIdInput.setBounds(140,85, 50, 25);
        
        JLabel filterNameLabel = new JLabel("First name: ");
        filterNameLabel.setBounds(200,85, 70, 25);
        filterNameInput = new JTextField();
        filterNameInput.setBounds(260,85, 90, 25);
        
        courses();
        JLabel filterCourseLabel = new JLabel("Course: ");
        filterCourse=new JComboBox<>(courseString);
        filterCourseLabel.setBounds(360,85, 80, 25);
        filterCourse.setBounds(410,83, 170, 30);
        
        filterButton= new JButton("Filter");
        filterButton.addActionListener(this);
        filterButton.setBounds(590,83, 60, 30);

        all=new JButton("All");
        all.setBounds(660, 83, 50, 30);
        all.addActionListener(this);

        filterIdInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                filterNameInput.setText("");
                filterCourse.setSelectedIndex(0);
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        filterNameInput.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                filterIdInput.setText("");
                filterCourse.setSelectedIndex(0);
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        JLabel studentLabel = new JLabel("Student Information");
        studentLabel.setFont(new Font("Arial", Font.BOLD, 27 ));
        studentLabel.setBounds(210, 5, 300, 50);
        
        JPanel topJPanel=new JPanel();
        topPanel=new JPanel(null);
        topPanel.setPreferredSize(new Dimension(740, 120));
        topPanel.add(studentLabel);
        topPanel.add(filterLabel);
        topPanel.add(filterIdLabel);
        topPanel.add(filterIdInput);
        topPanel.add(filterNameLabel);
        topPanel.add(filterNameInput);
        topPanel.add(filterCourseLabel);
        topPanel.add(filterCourse);
        topPanel.add(filterButton);
        topPanel.add(all);
        topJPanel.add(topPanel);
        //

        buttonPanel = new JPanel(null);
        exit = new JButton("Exit");
        exit.setBounds(210,450,90,30);
        exit.addActionListener(this);
        back = new JButton("Back");
        back.setBounds(100,450,90,30);
        back.addActionListener(this);
        apply=new JButton("Apply Changes");
        apply.setBounds(320,450,120,30);
        apply.addActionListener(this);
        remove=new JButton("Remove Selected");
        remove.setBounds(450,450,130,30);
        remove.addActionListener(this);

        tbl = new CustomTableModel();
        table = new JTable(tbl);
        table.setRowHeight(40);
        JPanel tableP=new JPanel(null);
        tableP.add(exit);
        tableP.add(back);
        tableP.add(apply);
        tableP.add(remove);
        for(int i=0;i<8;i++){
            tbl.addColumn(columns[i]);
        }
        q1 = """
            SELECT student.student_id,student_fname,student_lname,student_gender,student_birthdate,student_email,course_name,mark
            FROM student 
            join student_course on student.student_id = student_course.student_id
            join course on course.course_id = student_course.course_id
        """;
        database();
        table.getColumnModel().getColumn(0).setPreferredWidth(5);
        table.getColumnModel().getColumn(1).setPreferredWidth(25);
        table.getColumnModel().getColumn(2).setPreferredWidth(25);
        table.getColumnModel().getColumn(3).setPreferredWidth(5);
        table.getColumnModel().getColumn(4).setPreferredWidth(30);
        table.getColumnModel().getColumn(5).setPreferredWidth(55);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(5);
        sc = new JScrollPane(table);
        sc.setBorder(BorderFactory.createEmptyBorder());
        sc.setBounds(5,20,720,400);
        tableP.setPreferredSize(new Dimension(740,500));
        tableP.add(sc);
        viewPanel.add(topJPanel,BorderLayout.NORTH);
        viewPanel.add(tableP);
            
    }
    public void courses(){
        int i=1;
        ResultSet rs;
        try{Connection con = DriverManager.getConnection(sql[0], sql[1], sql[2]);
            try(PreparedStatement pst= con.prepareStatement("Select course_name from course")){
                rs=pst.executeQuery();
                while(rs.next()){
                    courseString[i]=rs.getString("course_name");
                    i++;
                }
            }
        }
        catch(Exception a){
            a.printStackTrace();
        }
    }
    
    public void database(){
        try{Connection con = DriverManager.getConnection(sql[0], sql[1], sql[2]);
            try(PreparedStatement pst1 = con.prepareStatement(q1,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE)){
                if(x==1){
                    pst1.setString(1, filterIdInput.getText());
                }
                if(x==2){
                    pst1.setString(1, filterNameInput.getText());
                }
                if(x==3){
                    pst1.setString(1,(String)filterCourse.getSelectedItem()); 
                }
                int i=0,rowCount=0;
                rs2 = pst1.executeQuery();
                tbl.setRowCount(0);
                if (rs2.last()) {
                    rowCount = rs2.getRow();
                    rs2.beforeFirst(); // Move the cursor back to the beginning of the ResultSet
                }
                String data[][]=new String[rowCount][8];
                while(rs2.next()){
                    data[i][0] = rs2.getString("student_id");
                    data[i][1] = rs2.getString("student_fname");
                    data[i][2] = rs2.getString("student_lname");
                    data[i][3] = rs2.getString("student_gender");
                    data[i][4] = rs2.getString("student_birthdate");
                    data[i][5] = rs2.getString("student_email");
                    data[i][6] = rs2.getString("course_name");
                    data[i][7] = rs2.getString("mark");
                    i++;      
                }
                for (String[] row : data) {
                    tbl.addRow(row);
                }
            }
            
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            card.show(adminPanel,"main");
        }
        else if(e.getSource() == exit) {
            System.exit(0);
        }
        else if(e.getSource()==all){
            x=0;
            filterIdInput.setText("");
            filterNameInput.setText("");
            q1 = """
            SELECT student.student_id,student_fname,student_lname,student_gender,student_birthdate,student_email,course_name,mark
            FROM student 
            join student_course on student.student_id = student_course.student_id
            join course on course.course_id = student_course.course_id
            """;
            database();
        }
        else if(e.getSource() == filterButton){
            if(filterCourse.getSelectedItem()!=" "){
                filterIdInput.setText("");
                filterNameInput.setText("");
                x=3;
                q1="""
                SELECT student.student_id,student_fname,student_lname,student_gender,student_birthdate,student_email,course_name,mark
                FROM student 
                join student_course on student.student_id = student_course.student_id
                join course on course.course_id = student_course.course_id 
                where course.course_name=?;
                        """;
                database();
            }
            else if (!filterIdInput.getText().isEmpty()) {
                x=1;
                q1="""
                    SELECT student.student_id,student_fname,student_lname,student_gender,student_birthdate,student_email,course_name,mark
                    FROM student 
                    join student_course on student.student_id = student_course.student_id
                    join course on course.course_id = student_course.course_id where student.student_id=?;""";
                database();
            }
            else if(!filterNameInput.getText().isEmpty()){
                x=2;
                q1="""
                    SELECT student.student_id,student_fname,student_lname,student_gender,student_birthdate,student_email,course_name,mark
                    FROM student 
                    join student_course on student.student_id = student_course.student_id
                    join course on course.course_id = student_course.course_id where student.student_fname=?""";
                database();
            }
        }
        else if(e.getSource()==apply){
            //saves changes to table
            if(table.isEditing()){
                table.getCellEditor().stopCellEditing();
            }
            //
            saveChanges();
        }
        else if(e.getSource()==remove){
            removeStudent();  
            database();        
        }
        
    }
    public void saveChanges() {
        try {
            Connection connection = DriverManager.getConnection(sql[0], sql[1], sql[2]);
            connection.setAutoCommit(false);
        
            String updateQuery = "UPDATE student_course SET mark = ? WHERE student_id = ? And course_id = (select course_id from course where course_name=?);";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
        
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int rowCount = model.getRowCount();
        
                for (int i = 0; i < rowCount; i++) {
                    String id = (String) model.getValueAt(i, 0);
        
                    // Use Integer.parseInt to convert the String to int
                    int newMark = Integer.parseInt(model.getValueAt(i, 7).toString());
                    String courseName = (String)model.getValueAt(i, 6);
        
                    // System.out.println("id" + id);
                    // System.out.println("mark" + newMark);
                    // System.out.println("row" + rowCount);
        
                    statement.setInt(1, newMark);
                    statement.setString(2, id);
                    statement.setString(3, courseName);
        
                    // Update the database with the new mark
                    statement.executeUpdate();
                }
        
                connection.commit(); // Commit the changes
        
                JOptionPane.showMessageDialog(null, "Changes saved to the database!");
            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback(); // Rollback changes if an exception occurs
            } finally {
                connection.setAutoCommit(true); // Set auto-commit back to true
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    public void removeStudent() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/alpha_uni", "root", "");
            PreparedStatement stmt;
            ListSelectionModel selectionModel = table.getSelectionModel();
            if (selectionModel.isSelectionEmpty()) {
                System.out.println("No row is selected.");
            } else {
                // Get the index of the selected row
                int[] selectedRows = table.getSelectedRows();
                
                for (int row : selectedRows) {
                    String studentId =(String) table.getValueAt(row, 0);

                    stmt = conn.prepareStatement("DELETE FROM stud_password WHERE student_id = ?");
                    stmt.setString(1, studentId);
                    stmt.executeUpdate();
            
                    stmt = conn.prepareStatement("DELETE FROM student_course WHERE student_id = ?");
                    stmt.setString(1, studentId);
                    stmt.executeUpdate();
            
                    stmt = conn.prepareStatement("DELETE FROM student WHERE student_id = ?");
                    stmt.setString(1, studentId);
                    stmt.executeUpdate();
                }
                
                conn.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public class CustomTableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            // Specify which column should be editable
            return column == 7; // Make the eighth column editable
        }
    }
}




