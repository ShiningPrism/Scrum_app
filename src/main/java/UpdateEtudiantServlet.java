import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateEtudiantServlet")
public class UpdateEtudiantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String numEtudiant = request.getParameter("numEt");
        String nom = request.getParameter("nom");
        String moyenne = request.getParameter("moyenne");

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);

            String query = "UPDATE etudiant SET nom = ?, moyenne = ? WHERE numEt = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, moyenne);
            preparedStatement.setString(3, numEtudiant);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Redirection vers index.jsp
                response.sendRedirect("index.jsp");
            } else {
                out.println("<p>Erreur lors de la mise à jour des détails de l'étudiant.</p>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Une erreur s'est produite : " + e.getMessage() + "</p>");
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<p>Erreur lors de la fermeture de la connexion : " + e.getMessage() + "</p>");
            }
        }
    }
}
