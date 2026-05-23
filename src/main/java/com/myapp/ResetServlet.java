package com.myapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/reset")
public class ResetServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Connection con = DBConnection.getConnection();

            Statement st = con.createStatement();

            // 🔥 THIS IS IMPORTANT
            st.executeUpdate("DELETE FROM expenses");

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redirect back to home
        response.sendRedirect("index.html");
    }
}