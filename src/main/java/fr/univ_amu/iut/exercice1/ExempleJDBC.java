package fr.univ_amu.iut.exercice1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercice 1 : premier contact avec JDBC. Le jalon de cet exercice est simple mais essentiel :
 * <b>se connecter à une base et lire une table</b>.
 */
public class ExempleJDBC {

  /** URL JDBC d'une base SQLite en mémoire. */
  public static final String URL_MEMOIRE = "jdbc:sqlite::memory:";

  public static void main(String[] args) throws SQLException {
    try (Connection connexion = DriverManager.getConnection(URL_MEMOIRE)) {
      creerEtRemplirTable(connexion);

      System.out.println("Taxons présents dans la base :");
      for (String ligne : lireTaxons(connexion)) {
        System.out.println("  " + ligne);
      }
    }
  }

  /** Prépare la base : crée la table taxon et y insère les espèces. */
  static void creerEtRemplirTable(Connection connexion) throws SQLException {
    try (Statement st = connexion.createStatement()) {
      st.execute("CREATE TABLE taxon (code TEXT PRIMARY KEY, nom_vernaculaire TEXT NOT NULL)");
      st.execute("INSERT INTO taxon VALUES ('Pippip', 'Pipistrelle commune')");
      st.execute("INSERT INTO taxon VALUES ('Nyclei', 'Noctule de Leisler')");
      st.execute("INSERT INTO taxon VALUES ('Tadten', 'Molosse de Cestoni')");
      st.execute("INSERT INTO taxon VALUES ('Rhihip', 'Petit rhinolophe')");
    }
  }

  /** Lit tous les taxons et renvoie, pour chacun, une ligne "code - nom". */
  static List<String> lireTaxons(Connection connexion) throws SQLException {
    List<String> lignes = new ArrayList<>();

    // Requête SQL pour sélectionner les colonnes nécessaires
    String requete = "SELECT code, nom_vernaculaire FROM taxon";

    // Étape 3 & 4 : Utilisation du try-with-resources pour fermer automatiquement
    // Statement et ResultSet
    try (Statement st = connexion.createStatement();
        ResultSet rs = st.executeQuery(requete)) {

      // Étape 4 (suite) : Parcourir le ResultSet tant qu'il y a des lignes
      while (rs.next()) {
        String code = rs.getString("code");
        String nomVernaculaire = rs.getString("nom_vernaculaire");

        // Formatage demandé par le test : "code - nom"
        lignes.add(code + " - " + nomVernaculaire);
      }
    }

    return lignes;
  }
}
