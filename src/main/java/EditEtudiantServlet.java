import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/EditEtudiantServlet")
public class EditEtudiantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String numEtudiant = request.getParameter("numEt");
        if (numEtudiant == null || numEtudiant.isEmpty()) {
            out.println("<p>Numéro d'étudiant non spécifié.</p>");
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);

            String query = "SELECT * FROM etudiant WHERE numEt = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, numEtudiant);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Styles CSS pour le formulaire
                String style = "<style>"
                            + "body { text-align: center; }"
                            + "form { width: 40%; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 1px; }"
                            + "input[type=text] { width: 100%; padding: 12px 20px; margin: 8px 0; box-sizing: border-box; }"
                            + "input[type=submit] { background-color: #4CAF50; color: white; padding: 14px 20px; margin: 8px 0; border: none; border-radius: 0px; cursor: pointer; }"
                            + "input[type=submit]:hover { background-color: #45a049; }"
                            + "h1 { margin-top: 100px; }"
                            + "</style>";

                // Script JavaScript pour la boîte de dialogue de confirmation
                String script = "<script>"
                              + "function confirmUpdate() {"
                              + "    return confirm('Êtes-vous sûr de vouloir modifier cet étudiant ?');"
                              + "}"
                              + "</script>";

                out.println(style);
                out.println(script);

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Modifier l'étudiant</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Modifier l'étudiant</h1>");
                out.println("<form action='UpdateEtudiantServlet' method='POST' onsubmit='return confirmUpdate()'>");
                out.println("<input type='hidden' name='numEt' value='" + resultSet.getString("numEt") + "'>");
                out.println("Nom: <input type='text' name='nom' value='" + resultSet.getString("nom") + "'><br>");
                out.println("Moyenne: <input type='text' name='moyenne' value='" + resultSet.getString("moyenne") + "'><br>");
                out.println("<input type='submit' value='Enregistrer'>");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            } else {
                out.println("<p>Aucun étudiant trouvé avec ce numéro.</p>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Une erreur s'est produite : " + e.getMessage() + "</p>");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<p>Erreur lors de la fermeture de la connexion : " + e.getMessage() + "</p>");
            }
        }
    }
}
