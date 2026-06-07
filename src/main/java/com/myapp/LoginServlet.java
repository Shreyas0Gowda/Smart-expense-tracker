package com.myapp;

import java.io.IOException;
import java.sql.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
        	Connection con = DBConnection.getConnection();

        	if (con == null) {
        	    response.getWriter().println("Database connection failed");
        	    return;
        	}

        	PreparedStatement ps = con.prepareStatement(
        	    "SELECT * FROM users WHERE username=? AND password=?"
        	);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // ✅ LOGIN SUCCESS

                HttpSession session = request.getSession();
                session.setAttribute("username", username); // ✅ FIXED

                response.sendRedirect("index.html");
            } else {
                response.getWriter().println("Invalid credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}