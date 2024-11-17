
# Aplikasi Pengelola Kontak
Latihan 3 - Muhammad Raihan Fadhillah 2210010404

## Daftar Isi
- [Deskripsi](#deskripsi)
- [Prerequisites](#prerequisites)
- [Fitur](#fitur)
- [Cara Menjalankan](#cara-menjalankan)
- [Dokumentasi](#dokumentasi)
- [Screenshots](#screenshots)
- [Contoh Penggunaan](#contoh-penggunaan)
- [Pembuat](#pembuat)

## Deskripsi
Aplikasi Pengelola Kontak adalah program Java berbasis GUI yang memungkinkan pengguna untuk mengelola daftar kontak. 

Pengguna dapat menambahkan, mengubah, menghapus, mengekspor, dan mengimpor kontak dari file CSV. 

Aplikasi ini menggunakan SQLite sebagai basis data untuk menyimpan informasi kontak.

## Prerequisites
1. Java Development Kit (JDK) versi 8 atau lebih tinggi.
2. IDE (Integrated Development Environment) seperti IntelliJ IDEA, Eclipse, atau NetBeans untuk mengembangkan dan menjalankan aplikasi.

## Fitur   
1. **Menambah Kontak**: Tambahkan kontak baru dengan nama, nomor telepon, dan kategori.
2. **Mengubah Kontak**: Ubah informasi kontak yang sudah ada.
3. **Menghapus Kontak**: Hapus kontak dari daftar.
4. **Mencari Kontak**: Cari kontak berdasarkan nama atau nomor telepon.
5. **Ekspor ke CSV**: Ekspor daftar kontak ke file CSV untuk keperluan backup atau analisis lebih lanjut.
6. **Impor dari CSV**: Impor kontak dari file CSV, dengan pengecekan untuk menghindari duplikasi.


## Cara Menjalankan
1. **Clone atau Download Repository** :
    - Clone repository ini atau download sebagai ZIP dan ekstrak.

2. **Buka Proyek di IDE** :
    - Buka IDE Anda dan import proyek Java yang telah diunduh.

3. **Jalankan Aplikasi**:
    - Jalankan kelas AplikasiPengelolaKontak dari IDE Anda.

  
## Dokumentasi
**Helper**
- Connection
```java
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
```

- Read Data
```java
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
```

- Create Data
```java
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
```

- Update Data
```java
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
```

- Delete Data
```java
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
```

- Cek Duplikat Data
```java
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
```

**Main Form**
- Memuat Data ke Dalam JTable
```java
private void loadKontak(String searchQuery) {
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    model.setRowCount(0);
    ResultSet rs = kontakHelper.getKontakData(searchQuery);
    try {
        if (rs != null) {
            while (rs.next()) {
                int id = rs.getInt("id_kontak");
                String nama = rs.getString("nama_kontak");
                String noTelepon = rs.getString("telepon");
                String kategori = rs.getString("kategori");
                model.addRow(new Object[]{id, nama, noTelepon, kategori});
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    } finally {
        // Close the ResultSet here to free resources
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(AplikasiPengelolaKontak.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
```

- Import dari CSV ke Database
```java
private void importFromCSV() {
       String filePath = "kontak.csv"; // Specify the path to the CSV file
       try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
           String line;
           br.readLine(); // Skip the header line
           while ((line = br.readLine()) != null) {
               String[] data = line.split(",");
               if (data.length >= 4) { // Ensure there is enough data
                   String nama = data[1].trim(); // Trim to remove leading/trailing spaces
                   String noTelepon = data[2].trim().replace("'", ""); // Remove single quotes if present
                   String kategori = data[3].trim();

                   // Debugging output
                   System.out.println("Checking for duplicate: " + nama + ", " + noTelepon);

                   // Check if the contact already exists
                   if (kontakHelper.isKontakExists(nama, noTelepon)) {
                       JOptionPane.showMessageDialog(this, "Kontak dengan nama '" + nama + "' dan nomor telepon '" + noTelepon + "' sudah ada. Kontak ini akan diabaikan.", "Duplikat Ditemukan", JOptionPane.WARNING_MESSAGE);
                   } else {
                       kontakHelper.addKontak(nama, noTelepon, kategori);
                   }
               }
           }
           loadKontak(""); // Reload the contacts after import
           JOptionPane.showMessageDialog(this, "Data berhasil diimport dari " + filePath, "Sukses", JOptionPane.INFORMATION_MESSAGE);
       } catch (IOException ex) {
           JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengimpor data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
       }
   }
```

- Export Data ke File CSV
```java
private void exportToCSV() {
        Map<Integer, String[]> kontakMap = new HashMap<>();

        // First, read existing contacts into a map to handle duplicates
        try (ResultSet rs = kontakHelper.getKontakData("")) {
            while (rs.next()) {
                int id = rs.getInt("id_kontak");
                String nama = rs.getString("nama_kontak");
                String noTelepon = rs.getString("telepon");
                String kategori = rs.getString("kategori");

                // Store the contact data in the map
                kontakMap.put(id, new String[]{nama, noTelepon, kategori});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengambil data kontak: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Now, you can write the data to the CSV file
        try (FileWriter csvWriter = new FileWriter("kontak.csv")) {
            csvWriter.append("ID,Nama,No Telepon,Kategori\n");

            // Iterate over the map and write to CSV
            for (Map.Entry<Integer, String[]> entry : kontakMap.entrySet()) {
                int id = entry.getKey();
                String[] data = entry.getValue();
                String nama = data[0];
                String noTelepon = data[1];
                String kategori = data[2];

                // Wrap phone number in single quotes
                csvWriter.append(id + "," + nama + ",'" + noTelepon + "'," + kategori + "\n");
            }

            JOptionPane.showMessageDialog(this, "Data berhasil diexport ke kontak.csv", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengekspor data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
```

- Validasi Digit / Panjang
```java
 if (!noTelepon.matches("\\d{10,13}")) {
            JOptionPane.showMessageDialog(this, "No Telepon harus 10 - 13 digit angka", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
```

- Validasi Input telepon
``` java
 private void txtTeleponKeyTyped(java.awt.event.KeyEvent evt) {                                    
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!Character.isDigit(c) || txtTelepon.getText().length() >= 16) {
            evt.consume();  // Ignore this key event if not a digit or length exceeds 16
        }
    }         
```
## Screenshots




## Contoh Penggunaan
1. **Menambahkan Kontak**: Masukkan nama, nomor telepon, dan pilih kategori. Klik tombol "Tambah" untuk menambahkan kontak.

2. Mengubah Kontak: Pilih kontak dari tabel. Ubah informasi yang diperlukan dan klik tombol "Ubah".

3. Menghapus Kontak: Pilih kontak yang ingin dihapus dan klik tombol "Hapus".

4. Mencari Kontak: Masukkan nama atau nomor telepon di kolom pencarian dan klik tombol "Cari".

5. Ekspor Kontak: Klik tombol "Export" untuk menyimpan kontak ke file kontak.csv.

6. Impor Kontak: Klik tombol "Import" untuk mengimpor kontak dari file kontak.csv.




## Pembuat

- Nama : Muhammad Raihan Fadhillah
- NPM : 2210010404

