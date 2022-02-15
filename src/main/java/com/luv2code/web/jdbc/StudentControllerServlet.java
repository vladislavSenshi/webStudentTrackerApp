package com.luv2code.web.jdbc;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudentControllerServlet", value = "/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {

    private StudentDBUtil studentDBUtil;
    @Resource(name="jdbc/web_student_tracker")
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        super.init();

        // create our student db util ... and pass in conn pool / darasource
        try {
            studentDBUtil = new StudentDBUtil(dataSource);
        } catch (Exception exc){
            throw new ServletException(exc);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // list the students ... in MVC fashion

        try {

            // read the "command" parameter
            String theCommand = request.getParameter("command");

            // if command is missing, then default to listing students
            if (theCommand == null){
                theCommand = "LIST";
            }

            // route to the appropriate method
            switch (theCommand) {
                case "LIST":
                    listStudents(request, response);
                    break;

                case "ADD":
                    addStudent(request, response);
                    break;

                case "LOAD":
                    loadStudent(request, response);
                    break;

                case "UPDATE":
                    updateStudent(request, response);
                    break;

                case "DELETE":
                    deleteStudent(request, response);
                    break;

                default:
                    listStudents(request, response);
            }


        } catch (Exception exc) {
            throw new ServletException(exc);
        }


    }

    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // read student id from form data
        String theStudentId = request.getParameter("studentId");

        // delete student from database
        studentDBUtil.deleteStudent(theStudentId);

        // send back to list-students.jsp
        listStudents(request, response);

    }

    private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // read student info from the form data
        int id = Integer.parseInt(request.getParameter("studentId"));
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");


        // create a new student object based on the form data
        Student theStudent = new Student(id, firstName, lastName, email);

        // perform update on a database
        studentDBUtil.updateStudent(theStudent);

        // send them back to the "list students" page
        listStudents(request, response);

    }

    private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // read student id from the form data
        String theStudentId = request.getParameter("studentId");

        // get the student from the database (db util)
        Student theStudent = studentDBUtil.getStudent(theStudentId);

        // list the student in the request attribute
        request.setAttribute("THE_STUDENT", theStudent);

        // send to jsp page: update-student-form.jsp
        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/update-student-form.jsp");
        dispatcher.forward(request, response);
    }

    private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // read a student info from form data
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        // create a new student object
        Student theStudent = new Student(firstName, lastName, email);

        // add this student to the database
        studentDBUtil.addStudent(theStudent);

        // send back to main page (the student list)
        listStudents(request, response);
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // get students from DB util
        List<Student> students = studentDBUtil.getStudents();

        // add students to the request
        request.setAttribute("STUDENT_LIST", students);

        // send to the JSP page (view)
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/list-students.jsp");
        requestDispatcher.forward(request, response);
    }

}
