import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/HistogrammeServlet")
public class HistogrammeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Histogramme</title>");
        out.println("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.9.3/Chart.min.js\"></script>");
        out.println("</head>");
        out.println("<body>");
        out.println("<canvas id=\"myChart\" width=\"1300\" height=\"600\"></canvas>"); // Ajuster la taille du canvas

        double average = 0.0;
        double max = 0.0;
        double min = 0.0;

        // Vérification de la connexion à la base de données
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Connexion à la base de données établie avec succès."); // Ajout pour le débogage
            } else {
                System.out.println("Échec de la connexion à la base de données."); // Ajout pour le débogage
                out.println("<p>Échec de la connexion à la base de données.</p>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur de connexion à la base de données: " + e.getMessage()); // Ajout pour le débogage
        }

        try {
            average = getAverage();
            max = getMax();
            min = getMin();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Script pour afficher le graphique
        out.println("<script>");
        out.println("var ctx = document.getElementById('myChart').getContext('2d');");
        out.println("var myChart = new Chart(ctx, {");
        out.println("type: 'bar',");
        out.println("data: {");
        out.println("labels: ['Moyenne', 'Note maximale', 'Note minimale'],");
        out.println("datasets: [{");
        out.println("label: 'Notes',");
        out.println("data: [" + average + ", " + max + ", " + min + "],");
        out.println("backgroundColor: [");
        out.println("'rgba(255, 99, 132, 0.2)',");
        out.println("'rgba(54, 162, 235, 0.2)',");
        out.println("'rgba(255, 206, 86, 0.2)'");
        out.println("],");
        out.println("borderColor: [");
        out.println("'rgba(255, 99, 132, 1)',");
        out.println("'rgba(54, 162, 235, 1)',");
        out.println("'rgba(255, 206, 86, 1)'");
        out.println("],");
        out.println("borderWidth: 1");
        out.println("}]");
        out.println("},");
        out.println("options: {");
        out.println("scales: {");
        out.println("yAxes: [{");
        out.println("ticks: {");
        out.println("beginAtZero: true");
        out.println("}");
        out.println("}]");
        out.println("}");
        out.println("}");
        out.println("});");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
    }

    
    private double getAverage() throws SQLException {
        double average = 0.0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AVG(CAST(moyenne AS DECIMAL)) AS average FROM etudiant")) {
            if (rs.next()) {
                average = rs.getDouble("average");
            }
        }
        return average;
    }

    private double getMax() throws SQLException {
        double max = 0.0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(CAST(moyenne AS DECIMAL)) AS max FROM etudiant")) {
            if (rs.next()) {
                max = rs.getDouble("max");
            }
        }
        return max;
    }

    private double getMin() throws SQLException {
        double min = 0.0;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MIN(CAST(moyenne AS DECIMAL)) AS min FROM etudiant")) {
            if (rs.next()) {
                min = rs.getDouble("min");
            }
        }
        return min;
    }



    private Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            String url = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
            String username = "root";
            String password = "";
            
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Le pilote JDBC n'a pas pu être chargé.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
        return connection;
    }
}
