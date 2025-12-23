import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ListeEtudiantsServlet")
public class ListeEtudiantsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Styles CSS pour centrer les éléments et colorer les boutons
        String style = "<style>"
        		+ "body { text-align: center; }"
                + ".buttons { margin-bottom: 20px; }"
                + ".buttons { margin-top: 20px; }"
                + ".buttons a { margin: 0 10px; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }"
                + ".buttons a:hover { background-color: #45a049; }"
                + ".edit-btn, .delete-btn { background-color: #008CBA; border: none; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; border-radius: 5px; }"
                + ".edit-btn:hover, .delete-btn:hover { background-color: #0073e6; }"
                + "</style>";
        
        out.println(style);
        
        out.println("<div class='buttons'>");
        out.println("<a href='AjoutEtudiantServlet'>Ajouter un nouvel étudiant</a>");
        out.println("<a href='HistogrammeServlet'>Voir l'histogramme</a>");
        out.println("</div>");
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Liste des Étudiants</title>");
        out.println("<style>");
        out.println("table {");
        out.println("    width: 80%;");
        out.println("    border-collapse: collapse;");
        out.println("    margin: 0 auto;"); // Pour centrer le tableau
        out.println("}");
        out.println("th, td {");
        out.println("    border: 1px solid black;");
        out.println("    padding: 8px;");
        out.println("    text-align: center;"); // Pour centrer le contenu des cellules
        out.println("}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<table>");
        out.println("<tr><th>#</th><th>Nom</th><th>Moyenne</th><th>Observation</th><th>Actions</th></tr>");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        double totalMoyenne = 0;
        double minMoyenne = Double.MAX_VALUE;
        double maxMoyenne = Double.MIN_VALUE;
        int count = 0;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/projet_jsp?useSSL=false&characterEncoding=utf-8";
            String username = "root";
            String password = "";
            connection = DriverManager.getConnection(url, username, password);

            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM etudiant");

            while(resultSet.next()) {
                double moyenne = resultSet.getDouble("moyenne");
                totalMoyenne += moyenne;
                minMoyenne = Math.min(minMoyenne, moyenne);
                maxMoyenne = Math.max(maxMoyenne, moyenne);
                count++;

                out.println("<tr>");
                out.println("<td>" + resultSet.getString("numEt") + "</td>");
                out.println("<td>" + resultSet.getString("nom") + "</td>");
                // Affichage de la moyenne avec deux chiffres après la virgule
                out.println("<td>" + formatMoyenne(moyenne) + "</td>");
                // Affichage de l'observation
                out.println("<td>" + getObservation(moyenne) + "</td>");
                out.println("<td>");
                out.println("<form style='display: inline;' action='EditEtudiantServlet' method='GET'>");
                out.println("<input type='hidden' name='numEt' value='" + resultSet.getString("numEt") + "'>");
                out.println("<input type='submit' class='edit-btn' value='Edit'>");
                out.println("</form>");

             // Formulaire pour le bouton Delete avec la boîte de dialogue de confirmation
                out.println("<form style='display: inline;' action='DeleteEtudiantServlet' method='POST' onsubmit='return confirmDelete()'>");
                out.println("<input type='hidden' name='numEt' value='" + resultSet.getString("numEt") + "'>");
                out.println("<input type='submit' class='edit-btn' value='Delete'>");
                out.println("</form>");

            }
        } catch(Exception e) {
            e.printStackTrace();
            out.println("<p>Une erreur s'est produite : " + e.getMessage() + "</p>");
        } finally {
            try {
                if(resultSet != null) resultSet.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
            } catch(Exception e) {
                e.printStackTrace();
                out.println("<p>Erreur lors de la fermeture de la connexion : " + e.getMessage() + "</p>");
            }
        }

        out.println("</table>");

        // Affichage de la moyenne de classe, de la moyenne minimale et maximale
        double moyenneClasse = count > 0 ? totalMoyenne / count : 0;
        out.println("<p>Moyenne de classe: " + formatMoyenne(moyenneClasse) + "</p>");
        out.println("<p>Moyenne minimale: " + formatMoyenne(minMoyenne) + "</p>");
        out.println("<p>Moyenne maximale: " + formatMoyenne(maxMoyenne) + "</p>");
        
        // Script JavaScript pour la boîte de dialogue de confirmation
        out.println("<script>");
        out.println("function confirmDelete() {");
        out.println("    return confirm('Voulez-vous vraiment supprimer cet enregistrement ?');");
        out.println("}");
        out.println("</script>");

        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    // Méthode pour obtenir l'observation en fonction de la moyenne
    private String getObservation(double moyenne) {
        if (moyenne >= 10) {
            return "Admis";
        } else if (moyenne >= 5 && moyenne < 10) {
            return "Redoublant";
        } else {
            return "Exclus";
        }
    }

    // Méthode pour formater la moyenne en affichant uniquement deux chiffres après la virgule si nécessaire
    private String formatMoyenne(double moyenne) {
        // Vérifier si la moyenne est un nombre entier
        if (moyenne == (int) moyenne) {
            // Retourner la moyenne en tant qu'entier
            return String.valueOf((int) moyenne);
        } else {
            // Si la moyenne a des décimales, formater avec deux chiffres après la virgule
            DecimalFormat df = new DecimalFormat("0.##");
            return df.format(moyenne);
        }
    }
}
