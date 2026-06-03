package fr.univ_amu.iut.exercice3;

import fr.univ_amu.iut.jdbc.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class TaxonDao {

  private final DataSource source;

  public TaxonDao(DataSource source) {
    this.source = source;
  }

  public List<Taxon> findAll() {
    List<Taxon> taxons = new ArrayList<>();
    String sql = "SELECT code, nom_latin, nom_vernaculaire FROM taxon ORDER BY code";

    try (Connection connexion = source.getConnection();
        PreparedStatement ps = connexion.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        taxons.add(depuis(rs));
      }

    } catch (SQLException e) {
      throw new DataAccessException("Erreur lors de la récupération de tous les taxons", e);
    }

    return taxons;
  }

  public Optional<Taxon> getByCode(String code) {
    String sql = "SELECT code, nom_latin, nom_vernaculaire FROM taxon WHERE code = ?";
    Optional<Taxon> resultat = Optional.empty();

    try (Connection connexion = source.getConnection();
        PreparedStatement ps = connexion.prepareStatement(sql)) {

      ps.setString(1, code);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          resultat = Optional.of(depuis(rs));
        }
      }

    } catch (SQLException e) {
      throw new DataAccessException("Erreur lors de la récupération du taxon : " + code, e);
    }

    return resultat;
  }

  private static Taxon depuis(ResultSet rs) throws SQLException {
    return new Taxon(
        rs.getString("code"), rs.getString("nom_latin"), rs.getString("nom_vernaculaire"));
  }
}
