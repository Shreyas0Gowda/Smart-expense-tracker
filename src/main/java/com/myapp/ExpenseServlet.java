package com.myapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class ExpenseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.html");
            return;
        }

        double amount = Double.parseDouble(request.getParameter("amount"));
        String category = request.getParameter("category");
        String description = request.getParameter("description");

        try {
            Connection con = DBConnection.getConnection();

            // INSERT
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO expenses(amount, category, description, username) VALUES (?, ?, ?, ?)"
            );

            ps.setDouble(1, amount);
            ps.setString(2, category);
            ps.setString(3, description);
            ps.setString(4, username);
            ps.executeUpdate();

            // FETCH
            PreparedStatement ps2 = con.prepareStatement(
                "SELECT * FROM expenses WHERE username=?"
            );

            ps2.setString(1, username);
            ResultSet rs = ps2.executeQuery();

            ArrayList<Expense> expenses = new ArrayList<>();

            // 🔥 CATEGORY VARIABLES
            double food = 0, travel = 0, shopping = 0;
            double medicine = 0, bills = 0, entertainment = 0, other = 0;
            double total = 0;

            while (rs.next()) {
                double amt = rs.getDouble("amount");
                String cat = rs.getString("category").trim();
                String desc = rs.getString("description");

                expenses.add(new Expense(amt, cat, desc));

                total += amt;

                switch (cat.toLowerCase()) {
                case "food": food += amt; break;
                case "travel": travel += amt; break;
                case "shopping": shopping += amt; break;
                case "medicine": medicine += amt; break;
                case "bills": bills += amt; break;
                case "entertainment": entertainment += amt; break;
                default: other += amt;
            }
            }

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<html><head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Dashboard</title>");

            // STYLE
            out.println("<style>");
            out.println("body { margin:0; font-family:Arial; background: linear-gradient(135deg,#1e3c72,#2a5298); display:flex; }");
            out.println(".sidebar { width:200px; height:100vh; background:rgba(0,0,0,0.4); color:white; padding:20px; }");
            out.println(".main { flex:1; padding:30px; color:white; }");
            out.println(".card { background:rgba(255,255,255,0.15); padding:20px; border-radius:10px; margin-bottom:20px; }");
            out.println("table { width:100%; border-collapse:collapse; }");
            out.println("th, td { padding:10px; text-align:center; }");
            out.println("th { background:#00c6ff; }");
            out.println("tr { background:rgba(255,255,255,0.1); }");
            out.println(".dashboard { display:flex; gap:30px; }");
            out.println(".chart { width:350px; }");
            out.println("canvas { max-width:100%; height:300px; }");
            out.println("</style>");

            out.println("</head><body>");

            // SIDEBAR
            out.println("<div class='sidebar'>");
            out.println("<h2>💰 Tracker</h2>");
            out.println("<p>👤 " + username + "</p>");
            out.println("<br><a href='logout'>🚪 Logout</a>");
            out.println("</div>");

            out.println("<div class='main'><div class='dashboard'>");

            // TABLE
            out.println("<div class='card' style='flex:1;'>");
            out.println("<h2>Your Expenses</h2>");

            out.println("<table>");
            out.println("<tr><th>Amount</th><th>Category</th><th>Description</th></tr>");

            for (Expense e : expenses) {
                out.println("<tr>");
                out.println("<td>&#8377;" + e.getAmount() + "</td>");
                out.println("<td>" + e.getCategory() + "</td>");
                out.println("<td>" + e.getDescription() + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("<h3>Total: &#8377;" + total + "</h3>");

            out.println("<br><a href='index.html'>⬅ Back</a>");

            // 🔥 RESET BUTTON (BETTER UI)
            out.println("<br><br>");
            out.println("<form action='reset' method='get'>");
            out.println("<button style='background:#ff4d4d; color:white; padding:10px; border:none; border-radius:8px; cursor:pointer;'>🗑 Reset My Data</button>");
            out.println("</form>");

            out.println("</div>");

            // CHART
            out.println("<div class='card chart'>");
            out.println("<h3>Analytics</h3>");
            out.println("<canvas id='expenseChart'></canvas>");
            out.println("</div>");

            out.println("</div></div>");

            // CHART JS
            out.println("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
            out.println("<script>");
            out.println("const ctx = document.getElementById('expenseChart').getContext('2d');");
            out.println("new Chart(ctx, {");
            out.println("type: 'pie',");
            out.println("data: {");
            out.println("labels: ['Food','Travel','Shopping','Medicine','Bills','Entertainment','Other'],");
            out.println("datasets: [{");
            out.println("data: [" + food + "," + travel + "," + shopping + "," + medicine + "," + bills + "," + entertainment + "," + other + "],");
            out.println("backgroundColor: ['#ff6384','#36a2eb','#ffce56','#8e44ad','#2ecc71','#f39c12','#4bc0c0']");
            out.println("}]");
            out.println("}");
            out.println("});");
            out.println("</script>");

            out.println("</body></html>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}    