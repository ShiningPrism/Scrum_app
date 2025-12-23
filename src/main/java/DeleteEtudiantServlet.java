import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteEtudiantServlet")
public class DeleteEtudiantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String numEtudiant = request.getParameter("numEt");

        if (numEtudiant == null || numEtudiant.isEmpty()) {
            // Redirection avec message si le numéro d'étudiant est manquant
            response.sendRedirect("index.jsp?error=missing");
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);

            String query = "DELETE FROM etudiant WHERE numEt = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, numEtudiant);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Redirection avec message en cas de suppression réussie
                response.sendRedirect("index.jsp?success=true");
            } else {
                // Redirection avec message si aucun étudiant n'a été supprimé
                response.sendRedirect("index.jsp?error=failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Redirection avec message en cas d'erreur lors de la suppression
            response.sendRedirect("index.jsp?error=delete");
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                // Gérer les erreurs de fermeture de la connexion
            }
        }
    }
}
