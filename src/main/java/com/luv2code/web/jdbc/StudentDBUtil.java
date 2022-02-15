package com.luv2code.web.jdbc;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class StudentDBUtil {

    private DataSource dataSource;

    public StudentDBUtil(DataSource theDataSource) {
        dataSource = theDataSource;
    }

    public List<Student> getStudents() throws Exception {
        List<Student> students = new ArrayList<>();

        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        try{
            // get a connection
            myConn = dataSource.getConnection();

            // create sql statement
            String sql = "select * from student order by last_name";
            myStmt = myConn.createStatement();

            // execute query
            myRs = myStmt.executeQuery(sql);

            // process result set
            while (myRs.next()){
                // retrieve data from result set row
                int id = myRs.getInt("id");
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");
                // create new student object
                Student tempStudent = new Student(id, firstName, lastName, email);

                // add it to the list of students
                students.add(tempStudent);
            }
            // close JDBC objects

        }
        finally {
            close(myConn, myStmt, myRs);
        }

        return students;

    }

    private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
        try {
            if (myRs != null){
                myRs.close();
            }

            if (myStmt != null){
                myStmt.close();
            }

            if (myConn != null){
                myConn.close();
            }
        } catch (Exception exc){
            exc.printStackTrace();
        }

    }

    public void addStudent(Student theStudent) throws Exception {

        Connection myConn = null;
        PreparedStatement myStmt = null;

        try{
            // get db connection
            myConn = dataSource.getConnection();

            // create sql for insert
            String sql = "insert into student "
                    + "(first_name, last_name, email)"
                    + "values(?, ?, ?)"; // placeholders ("?") cause of using PreparedStatement, it fills in
                                        // that placeholders with actual data

            myStmt = myConn.prepareStatement(sql);

            // set the param values for the student
            myStmt.setString(1, theStudent.getFirstName());
            myStmt.setString(2, theStudent.getLastName());
            myStmt.setString(3, theStudent.getEmail());

            // execute sql insert
            myStmt.execute();
        } finally {
            // clean up JDBC objects
            close(myConn, myStmt, null);
        }


    }

    public Student getStudent(String theStudentId) throws Exception {

        Student theStudent = null;

        Connection myCon = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        int studentId;

        try{
            // convert student id to int
            studentId = Integer.parseInt(theStudentId);

            // get connection to db
            myCon = dataSource.getConnection();

            // create sql to get selected student
            String sql = "select * from student where id=?";

            // create prepared statement
            myStmt = myCon.prepareStatement(sql);

            // set params
            myStmt.setInt(1, studentId);

            // execute statement
            myRs = myStmt.executeQuery();

            // retrieve data from result set row
            if (myRs.next()){
                String firstName = myRs.getString("first_name");
                String lastName = myRs.getString("last_name");
                String email = myRs.getString("email");

                // use the student id to create a new student
                theStudent = new Student(studentId, firstName, lastName, email);
            }
            else {
                throw new Exception("Could not find student id: " + studentId);
            }

            return theStudent;
        }
        finally {
            // clean jdbc objects
            close(myCon, myStmt, myRs);
        }
    }

    public void updateStudent(Student theStudent) throws Exception {

        Connection myCon = null;
        PreparedStatement myStmt = null;

        try {

        // get db connection
        myCon = dataSource.getConnection();

        // create SQL update statement
        String sql = "update student " +
                "set first_name=?, last_name=?, email=? "
                + "where id=?";

        // prepare statement
        myStmt = myCon.prepareStatement(sql);

        // set params
        myStmt.setString(1, theStudent.getFirstName());
        myStmt.setString(2, theStudent.getLastName());
        myStmt.setString(3, theStudent.getEmail());
        myStmt.setInt(4, theStudent.getId());

        // execute SQL statement
        myStmt.execute();
    }
        finally {
            // clear JDBC objects
            close (myCon, myStmt, null);
        }
    }

    public void deleteStudent(String theStudentId) throws Exception {

        Connection myCon = null;
        PreparedStatement myStmt = null;

        try {
            // convert student id to int
            int studentId = Integer.parseInt(theStudentId);

            // get connection to database
            myCon = dataSource.getConnection();

            // create SQL to delete student
            String sql = "delete from student where id=?";

            // prepare statement
            myStmt = myCon.prepareStatement(sql);

            // set params
            myStmt.setInt(1, studentId);

            //execute SQL statement
            myStmt.execute();

        } finally {
            // clean up JDBC objects
            close(myCon, myStmt, null);
        }

    }
}
