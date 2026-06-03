package fr.univ_amu.iut.exercice2;

import fr.univ_amu.iut.jdbc.DataAccessException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

public class BaseDeDonnees {

  private BaseDeDonnees() {}

  public static DataSource surFichier(String chemin) {
    DataSource source = null;

    SQLiteConfig config = new SQLiteConfig();
    config.enforceForeignKeys(true);

    SQLiteDataSource sqlite = new SQLiteDataSource(config);
    sqlite.setUrl("jdbc:sqlite:" + chemin);

    source = sqlite;

    return source;
  }

  public static void initialiser(DataSource source) {
    executerScript(source, "/db/schema.sql");
    executerScript(source, "/db/seed.sql");
  }

  private static void executerScript(DataSource source, String ressource) {
    String sql = lireRessource(ressource);
    try (Connection connexion = source.getConnection();
        Statement st = connexion.createStatement()) {
      for (String instruction : decouperInstructions(sql)) {
        st.execute(instruction);
      }
    } catch (SQLException e) {
      throw new DataAccessException("Échec de l'exécution du script " + ressource, e);
    }
  }

  private static String lireRessource(String chemin) {
    try (InputStream in = BaseDeDonnees.class.getResourceAsStream(chemin)) {
      if (in == null) {
        throw new IllegalStateException("Ressource introuvable : " + chemin);
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException("Lecture impossible : " + chemin, e);
    }
  }

  private static String[] decouperInstructions(String sql) {
    StringBuilder sansCommentaires = new StringBuilder();
    for (String ligne : sql.split("\n")) {
      if (!ligne.strip().startsWith("--")) {
        sansCommentaires.append(ligne).append('\n');
      }
    }
    String[] brutes = sansCommentaires.toString().split(";");
    return java.util.Arrays.stream(brutes)
        .map(String::strip)
        .filter(s -> !s.isEmpty())
        .toArray(String[]::new);
  }
}
