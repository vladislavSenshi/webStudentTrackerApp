package com.luv2code.web.jdbc;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "TestServlet", value = "/TestServlet")
public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Define datasource/connection pool for Resource Injection
    @Resource(name="jdbc/web_student_tracker")
    private DataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Step 1: set up a printwriter
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");

        // Step 2: get a connection to the database
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;

        try {
            myConn = dataSource.getConnection();

        // Step 3: Create SQL statements
            String sql = "select * from student";
            myStmt = myConn.createStatement();

        // Step 4: Execute SQL query
            myRs = myStmt.executeQuery(sql);

        // Step 5: Process the result set
            while (myRs.next()){
                String email = myRs.getString("email");
                out.println(email);
            }

        }
        catch (Exception exc){
            exc.printStackTrace();
        }
    }

}
