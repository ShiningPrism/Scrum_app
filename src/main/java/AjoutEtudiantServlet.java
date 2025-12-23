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

@WebServlet("/AjoutEtudiantServlet")
public class AjoutEtudiantServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String style = "<style>"
        		 + "body { text-align: center; }"
                 + "form { width: 40%; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 1px; }"
                 + "input[type=text] { width: 100%; padding: 12px 20px; margin: 8px 0; box-sizing: border-box; }"
                 + "input[type=submit] { background-color: #4CAF50; color: white; padding: 14px 20px; margin: 8px 0; border: none; border-radius: 0px; cursor: pointer; }"
                 + "input[type=submit]:hover { background-color: #45a049; }"
                 + "h1 { margin-top: 100px; }"
                 + "</style>";



        out.println(style);

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Ajouter un nouvel étudiant</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Ajouter un nouvel étudiant</h1>");
        out.println("<form action='AjoutEtudiantServlet' method='post'>");
        out.println("<label for='numEt'>Numéro d'étudiant:</label><br>");
        out.println("<input type='text' id='numEt' name='numEt'><br>");
        out.println("<label for='nom'>Nom:</label><br>");
        out.println("<input type='text' id='nom' name='nom'><br>");
        out.println("<label for='moyenne'>Moyenne:</label><br>");
        out.println("<input type='text' id='moyenne' name='moyenne'><br>");
        out.println("<input type='submit' value='Ajouter'>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            String query = "INSERT INTO etudiant (numEt, nom, moyenne) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, numEtudiant);
            preparedStatement.setString(2, nom);
            preparedStatement.setString(3, moyenne);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Redirection vers index.jsp après l'ajout réussi
                response.sendRedirect("index.jsp");
            } else {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Résultat de l'ajout</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Erreur lors de l'ajout de l'étudiant</h1>");
                out.println("</body>");
                out.println("</html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Résultat de l'ajout</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Une erreur s'est produite : " + e.getMessage() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
