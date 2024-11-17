package AplikasiPengelolaKontak;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class kontakHelper {
    private static final String URL = "jdbc:sqlite:kontak_db";

    // Method to get a connection to the database
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException ex) {
            Logger.getLogger(kontakHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }

    // Method to get all contacts
   public static ResultSet getKontakData(String searchQuery) {
        String query = "SELECT * FROM kontak";
        if (!searchQuery.isEmpty()) {
            query += " WHERE nama_kontak LIKE ? OR telepon LIKE ?";
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            if (!searchQuery.isEmpty()) {
                pstmt.setString(1, "%" + searchQuery + "%");
                pstmt.setString(2, "%" + searchQuery + "%");
            }
            rs = pstmt.executeQuery();
            return rs; // Return the ResultSet
        } catch (SQLException ex) {
            Logger.getLogger(kontakHelper.class.getName()).log(Level.SEVERE, null, ex);
            return null; // or throw an exception
        } finally {
            // Do not close the connection here since we are returning the ResultSet
        }
    }

    // Method to add a contact
    public static void addKontak(String nama, String noTelepon, String kategori) {
        String query = "INSERT INTO kontak (nama_kontak, telepon, kategori) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, noTelepon);
            pstmt.setString(3, kategori);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(kontakHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Method to update a contact
    public static void updateKontak(int id, String nama, String noTelepon, String kategori) {
        String query = "UPDATE kontak SET nama_kontak = ?, telepon = ?, kategori = ? WHERE id_kontak = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, noTelepon);
            pstmt.setString(3, kategori);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(kontakHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Method to delete a contact
    public static void deleteKontak(int id) {
        String query = "DELETE FROM kontak WHERE id_kontak = ?";
        try (Connection conn = getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(kontakHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Method to check if a contact already exists
    public static boolean isKontakExists(String nama, String noTelepon) {
    String query = "SELECT COUNT(*) FROM kontak WHERE nama_kontak = ? AND telepon = ?";
    try (Connection conn = getConnection(); 
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, nama);
        pstmt.setString(2, noTelepon);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // If more than 0, then a duplicate exists
            }
        }
    } catch (SQLException ex) {
        Logger.getLogger(kontakHelper.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
}
}