/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Projecte;

import static Projecte.projecte.connectarBD;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author DAM
 */
public class projecte {

    static Connection connectarBD = null;

    public static void main(String[] args) throws SQLException, IOException {
        boolean sortir = false;
        connectarBD();
        Scanner teclat = new Scanner(System.in);

        //12/11/21
        do {
            System.out.println("^^^^MENU GESTOR PRODUCTES^^^^");
            System.out.println("1.Manteniment de productes A/B/M/C");
            System.out.println("2.Actualitzar stocks");
            System.out.println("3.Generar comanda als proveïdors");
            System.out.println("4.Consultar comandes del dia");
            System.out.println("5.Sortir");
            System.out.println("\nTria una de les opcions");

            int opcio = teclat.nextInt();

            switch (opcio) {
                case 1:
                    gestioProductes();
                    break;
                case 2:
                    actualitzarStocks();
                    break;
                case 3:
                    generarComanda();
                    break;
                case 4:
                    consultarComandes();
                    break;
                case 5:
                    sortir = true;
                    break;
                default:
                    System.out.println("L'Opció no és vàlida");
            }

            System.out.println(("opció: ") + opcio);

        } while (!sortir);
        desconnexioBD();
    }

    static void altaProductes() {
        System.out.println("Alta productes");
    }

    static void actualitzarStocks() throws IOException {
        System.out.println("Actualitzar Stock");
        Scanner teclat = new Scanner(System.in);
        File fitxer3 = new File("MOBLESDAM/entrades processades");
        String actualitza = "UPDATE productes SET estoc = ? WHERE codi = ?";
        System.out.println("Codi:");
        int codi = teclat.nextInt();
        teclat.nextLine();
        System.out.println("Estoc");
        int estoc = teclat.nextInt();
        PreparedStatement sentencia = null;
        try {
            sentencia = connectarBD.prepareStatement(actualitza);
            sentencia.setInt(2, codi);
            sentencia.setInt(1, estoc);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            //Nos aseguramos de cerrar los recursos abiertos
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }

        File fitxer2 = new File("MOBLESDAM/entrades pendents");

        if (fitxer2.isDirectory()) {
            System.out.println("Es un directori");

            File[] fitxers = fitxer2.listFiles();

            for (int i = 0; i < fitxers.length; i++) {
                System.out.println(fitxers[i].getName());
                actualitzarFitxerBD(fitxers[i]);
                moureFitxerAProcessades(fitxers[i]);
            }
        }

    }

    static void moureFitxerAProcessades(File fitxer) throws IOException, FileNotFoundException {
        FileSystem sistemaFitxers = FileSystems.getDefault();
        Path origen = sistemaFitxers.getPath("MOBLESDAM/entrades pendents/" + fitxer.getName());
        Path desti = sistemaFitxers.getPath("MOBLESDAM/entrades processades/" + fitxer.getName());

        Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("S'ha mogut a PROCESSATS el fitxer: " + fitxer.getName());

    }

    static void actualitzarFitxerBD(File fitxer) throws FileNotFoundException, IOException {
        //LECTURA CARACTER A CARACTER
        FileReader reader = new FileReader(fitxer);
        //LECTURA LINEA A LINEA, MOLT MES EFICIENT   
        BufferedReader buffer = new BufferedReader(reader);
        String linea;
        while ((linea = buffer.readLine()) != null) {
            System.out.println(linea);
            String LIMITADOR = ":";

            int posLimit = linea.indexOf(LIMITADOR);
            String nom = linea.substring(0, posLimit);
            int entradaStock = Integer.parseInt(linea.substring(posLimit + 1));
            System.out.println("nom prod : " + nom + "entradaStock : " + entradaStock);
        }
        buffer.close();
        reader.close();

    }

    static void generarComanda() throws SQLException {
        System.out.println("Generar comanda");
        String consulta = "SELECT P.Nom, P.estoc, R.nif, P.Codi ,R.nom FROM productes P, proveidors R WHERE P.estoc <=20 order by R.nom;";
        PreparedStatement ps = connectarBD.prepareStatement(consulta);
        ResultSet rs = ps.executeQuery();
        String proveidorAnt = "";
        while (rs.next()) {
            if (!proveidorAnt.equals(rs.getString("nif"))) {
                proveidorAnt = rs.getString("nif");
                System.out.println("canvi de proveidor " + rs.getString("nif"));
            }
            System.out.print(" codi: " + rs.getInt("codi"));
            System.out.print(" nom: " + rs.getString("nom"));
            System.out.print(" estoc: " + rs.getInt("estoc"));
            System.out.println(" nif: " + rs.getString("nif"));

        }

    }

    static void consultarComandes() {
        System.out.println("Consultar comanda");
    }

    static void gestioProductes() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        boolean enrere = false;
        do {
            System.out.println("^^^^MENU GESTOR PRODUCTES^^^^");
            System.out.println("1.Llista Productes");
            System.out.println("2.Alta de Productes");
            System.out.println("3.modificar Productes");
            System.out.println("4.Esborrar Productes");
            System.out.println("5.Enrere");
            System.out.println("\nTria una de les opcions");

            int opcio = teclat.nextInt();

            switch (opcio) {
                case 1:
                    llistaProductes();
                    break;
                case 2:
                    altaProducte();
                    break;
                case 3:
                    modificarProductes();
                    break;
                case 4:
                    esborrarProductes();
                    break;
                case 5:
                    enrere = true;
                    break;
                default:
                    System.out.println("L'Opció no és vàlida");
            }
        } while (!enrere);

    }

    public static void desconnexioBD() {
        System.out.println("Desconnectat de la BD");
    }

    public static void llistaProductes() throws SQLException {
        System.out.println("Llistem productes");
        String consulta = "SELECT * FROM productes ORDER BY codi";
        PreparedStatement ps = connectarBD.prepareStatement(consulta);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            System.out.println("codi: " + rs.getInt("codi"));
            System.out.println("nom: " + rs.getString("nom"));
            System.out.println("material: " + rs.getString("material"));
            System.out.println("estoc: " + rs.getInt("estoc"));
        }
    }

    static void altaProducte() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        String consulta = "INSERT INTO productes (codi,nom,material,estoc) values(?,?,?,?)";
        System.out.println("Codi nou.");
        String codi = teclat.nextLine();
        System.out.println("Nom :");
        String nom = teclat.nextLine();
        System.out.println("Material?");
        String material = teclat.nextLine();
        System.out.println("Estoc?");
        int estoc = teclat.nextInt();

        PreparedStatement sentencia = null;

        try {
            sentencia = connectarBD.prepareStatement(consulta);
            sentencia.setString(1, codi);
            sentencia.setString(2, nom);
            sentencia.setString(3, material);
            sentencia.setInt(4, estoc);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            //Nos aseguramos de cerrar los recursos abiertos
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
    }

    public static void modificarProductes() {
        Scanner teclat = new Scanner(System.in);
        System.out.println("Modifiquem productes?");
        String actualitza = "UPDATE productes SET nom = ?, material = ? ,estoc = ? WHERE codi = ?";

        System.out.println("Codi:");
        int codi = teclat.nextInt();
        teclat.nextLine();
        System.out.println("Nom :");
        String nom = teclat.nextLine();
        System.out.println("Material");
        String material = teclat.nextLine();
        System.out.println("Estoc");
        int estoc = teclat.nextInt();
        PreparedStatement sentencia = null;

        try {
            sentencia = connectarBD.prepareStatement(actualitza);
            sentencia.setInt(4, codi);
            sentencia.setString(1, nom);
            sentencia.setString(2, material);
            sentencia.setInt(3, estoc);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            //Nos aseguramos de cerrar los recursos abiertos
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
        System.out.println("Producte Modificat.");
    }

    public static void esborrarProductes() {
        Scanner teclat = new Scanner(System.in);
        System.out.println("Esborrar producte");
        String sentenciaSql = "DELETE FROM productes WHERE codi = ?";
        System.out.println("Codi:");
        int codi = teclat.nextInt();
        PreparedStatement sentencia = null;

        try {
            sentencia = connectarBD.prepareStatement(sentenciaSql);
            sentencia.setInt(1, codi);
            sentencia.executeUpdate();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            //Nos aseguramos de cerrar los recursos abiertos
            if (sentencia != null) {
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
            }
        }
        System.out.println("Producte esborrat.");
    }

    static void connectarBD() {

        String servidor = "jdbc:mysql://localhost:3309/";
        String bbdd = "empresa";
        String user = "root";
        String password = "Fat/3232";

        try {

            connectarBD = DriverManager.getConnection(servidor + bbdd, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();

        }
    }

}
