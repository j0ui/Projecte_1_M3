package Projecte;

import static Projecte.projecte.connectarBD;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class projectev2 {

    static Scanner teclat = new Scanner(System.in);
    static Connection connexioBD;
    static String nom_empresa = "Mobles DAM";
    static String direccio_emp = "Tarrega";
    static int telefon = 646163674;

    static FileWriter fitcher = null;
    static BufferedWriter bf = null;
    static PrintWriter escritor = null;

    static String[] proveidors = new String[100];
    static int[] productesCom = new int[100];

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
                    prepararComanda();
                    break;
                case 4:
                    analitzarComandes();
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

    static void gestioProductes() throws SQLException, IOException {
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
                    actualitzarStocks();
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

    static void actualitzarStocks() throws IOException {
        System.out.println("Actualitzar Stock");
        File fitxer2 = new File("MOBLESDAM/entrades pendents");

        if (fitxer2.isDirectory()) {
            File[] fitxers = fitxer2.listFiles();

            for (int i = 0; i < fitxers.length; i++) {
                System.out.println(fitxers[i].getName());
                actualitzarFitxerBD(fitxers[i]);
                moureFitxerAProcessades(fitxers[i]);
            }
        }

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
            int posSep = linea.indexOf(":");
            int codi = Integer.parseInt(linea.substring(0, posSep));
            System.out.println("El codi del producte es: " + codi);
            int estoc = Integer.parseInt(linea.substring(posSep + 1));
            System.out.println("estoc : " + estoc);
        }
        buffer.close();
        reader.close();

    }

    static void actualitzarBD(int codi, int estoc) {
        String actualitza = "UPDATE productes SET estoc = estoc + ? WHERE codi = ?";
        PreparedStatement sentencia = null;
        try {
            sentencia = connectarBD.prepareStatement(actualitza);
            sentencia.setInt(2, codi);
            sentencia.setInt(1, estoc);
            sentencia.executeUpdate();
            System.out.println("S'han afegit " + estoc + " unitats al producte : " + codi);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("no s'ha pogut actualitzar l'estoc");
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

    static void moureFitxer(File fitxers) throws IOException {
        FileSystem sistemaFitxers = FileSystems.getDefault();
        Path origen = sistemaFitxers.getPath("MOBLESDAM/entrades pendents/" + fitxers.getName());
        Path desti = sistemaFitxers.getPath("MOBLESDAM/entrades processades/" + fitxers.getName());

        Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("S'ha mogut a PROCESSATS el fitxer: " + fitxers.getName());

    }

    static void prepararComanda() throws SQLException, IOException {
        int productes = 0;
        int prov = 0;
        int prod = 0;

        System.out.println("GENERA COMANDES");

        String consulta = "select productes.codi, estoc, proveeix.nif, proveidors.nom, proveidors.loclitat, proveidors.telefon from productes join proveeix on productes.codi = proveeix.codi join proveidors on proveeix.nif = proveidors.nif  where estoc < 20 order by proveeix.nif;";

        PreparedStatement ps = connectarBD.prepareStatement(consulta);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            String actproveidor = rs.getString("nif");
            String nomproveidor = rs.getString("nom");
            String localitatProv = rs.getString("loclitat");
            int telefonProv = rs.getInt("telefon");

            crearFitxer(nomproveidor, localitatProv, telefonProv, prov);

            do {

                if (!actproveidor.equals(rs.getString("nif"))) {
                    System.out.println("Ha canviat el proveidor");

                    actproveidor = rs.getString("nif");
                    nomproveidor = rs.getString("nom");

                    escritor.close();

                    prov++;

                    crearFitxer(nomproveidor, localitatProv, telefonProv, prov);

                    prod++;
                    productes = 0;

                }

                System.out.print("codi: " + rs.getInt("codi") + "  ");
                System.out.print("estoc: " + (60 - rs.getInt("estoc")) + "  ");
                System.out.println("NIF: " + rs.getString("nif"));

                escritor.println("\t" + rs.getInt("codi") + "\t \t" + (60 - rs.getInt("estoc")));

                productes++;
                productesCom[prod] = productes;

            } while (rs.next());

            escritor.close();

        }
    }

    static PrintWriter crearFitxer(String nomproveidor, String localitat, int telefonProv, int prov) throws IOException {

        // Crear el fitcher
        fitcher = new FileWriter("MOBLESDAM/Comandes/" + nomproveidor + "_" + LocalDate.now() + ".txt");
        bf = new BufferedWriter(fitcher);
        escritor = new PrintWriter(bf);

        PrintWriter escritor = new PrintWriter(bf);
        escritor.println("^^^^MOBLESDAM^^^^");
        escritor.println("-----------------");
        escritor.println("AV.Tarrega,18 25300");
        escritor.println("973570651");
        escritor.println("moblesdam@contacte.com");
        escritor.println("--------------------------------------------------------");
        escritor.print("Data comanda :");
        escritor.println(LocalDate.now());
        escritor.print("NºComanda : ");
        escritor.println("00001");
        escritor.println("------------------------------");
        escritor.println("Codi              Quantitat         ");
        escritor.println("------------------------------");

        proveidors[prov] = nomproveidor;
        return escritor;

    }

    static void analitzarComandes() {
        llistatProductes();
        mesSolicitat();
        menysSolicitat();
        mitjanaSolicitat();

    }

    static void llistatProductes() {
        System.out.println("Llistat de Productes demanats a proveidors");
        for (int i = 0; productesCom[i] != 0; i++) {
            System.out.println(proveidors[i] + ": " + productesCom[i]);
        }
    }

    static void mesSolicitat() {
        System.out.println("\nProveidor mes sol·licitat");
        int max = productesCom[0];
        int imax = 0;

        for (int i = 0; productesCom[i] != 0; i++) {
            if (productesCom[i] > max) {
                max = productesCom[i];
                imax = i;
            }
        }
        System.out.println("A " + proveidors[imax] + " se li sol·liciten : " + max);

    }

    static void menysSolicitat() {
        System.out.println("\nProveidor menys sol·licitat");
        int min = productesCom[0];
        int imin = 0;

        for (int i = 0; productesCom[i] != 0; i++) {
            if (productesCom[i] < min) {
                min = productesCom[i];
                imin = i;
            }
        }
        System.out.println("A " + proveidors[imin] + " se li sol·liciten : " + min);
    }

    static void mitjanaSolicitat() {
        System.out.println("\nMitjana de productes sol·licitats");

        double mitjana = 0, suma = 0;
        int i;
        for (i=0; productesCom[i] != 0; i++) {
            suma += productesCom[i];

        }
        mitjana = suma / i;
        System.out.println("La mitjana de productes demanats a proveidors es " + mitjana + "\n");

    }

    static void desconnexioBD() {

    }

}
