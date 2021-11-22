/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Projecte;

import static Projecte.projecte.connectarBD;
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
    public static void main (String[] args) throws SQLException {
        boolean sortir=false;
        connectarBD();
        Scanner teclat = new Scanner (System.in);
        
            //12/11/21
        do{
           System.out.println("^^^^MENU GESTOR PRODUCTES^^^^");
           System.out.println("1.Manteniment de productes A/B/M/C");
           System.out.println("2.Actualitzar stocks");
           System.out.println("3.Generar comanda als proveïdors");
           System.out.println("4.Consultar comandes del dia");
           System.out.println("5.Sortir");
           System.out.println("\nTria una de les opcions");
           
           int opcio=teclat.nextInt();
           
           switch (opcio){
               case 1:
                    gestioProductes ();
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
                   sortir=true;
                   break;
               default:
                   System.out.println("L'Opció no és vàlida");
            }
           
      
          System.out.println(("opció: ")+ opcio); 
          
         
      } while (!sortir);
        desconnexioBD();
    }
          
    static void altaProductes (){
            System.out.println("Alta productes");
        }
    static void actualitzarStocks(){
           System.out.println("Actualitzar Stock");

    }
    static void generarComanda(){
           System.out.println("Generar comanda");
    }
    static void consultarComandes(){
           System.out.println("Consultar comanda");
    }
    
    static void gestioProductes () throws SQLException {
        Scanner teclat = new Scanner (System.in);
        boolean enrere=false;
        do{
           System.out.println("^^^^MENU GESTOR PRODUCTES^^^^");
           System.out.println("1.Llista Productes");
           System.out.println("2.Alta de Productes");
           System.out.println("3.modificar Productes");
           System.out.println("4.Esborrar Productes");
           System.out.println("5.Enrere");
           System.out.println("\nTria una de les opcions");

            
            int opcio=teclat.nextInt();
            


           switch (opcio){
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
                   enrere=true;
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
        String consulta ="SELECT * FROM productes ORDER BY codi";
        PreparedStatement ps = connectarBD.prepareStatement(consulta);
        ResultSet rs=ps.executeQuery();
        
        while (rs.next()){
            System.out.println("codi: " + rs.getInt("codi"));
            System.out.println("nom: " + rs.getString("nom"));
            System.out.println("material: " + rs.getString("material"));
            System.out.println("estoc: " + rs.getInt("estoc"));
        }
    }
    static void altaProducte() throws SQLException{
        Scanner teclat = new Scanner (System.in);
        String consulta = "INSERT INTO productes (codi,nom,material,estoc) values(?,?,?,?)";
        System.out.println("Codi nou.");
        String codi =teclat.nextLine();
        System.out.println("Nom :");
        String nom =teclat.nextLine();
        System.out.println("Material?");
        String material =teclat.nextLine();
        System.out.println("Estoc?");
        int estoc =teclat.nextInt();
        
        PreparedStatement sentencia = null;
 
        try {
          sentencia = connectarBD.prepareStatement(consulta);
          sentencia.setString(1, codi);
          sentencia.setString(2, nom);
          sentencia.setString(3, material);
          sentencia.setInt(4, estoc);
          sentencia.executeUpdate();
        } 
        catch (SQLException sqle) {
          sqle.printStackTrace();
        } 
        finally {
          //Nos aseguramos de cerrar los recursos abiertos
          if (sentencia != null)
            try {
              sentencia.close();
            } catch (SQLException sqle) {
              sqle.printStackTrace();
            }
        }
    }

    public static void modificarProductes() {
        Scanner teclat = new Scanner (System.in);
        System.out.println("Modifiquem productes?");
        String actualitza = "UPDATE productes SET nom = ?, material = ? ,estoc = ? WHERE codi = ?";
        
        System.out.println("Codi:");
        int codi =teclat.nextInt();
        teclat.nextLine();
        System.out.println("Nom :");
        String nom =teclat.nextLine();
        System.out.println("Material");
        String material =teclat.nextLine();
        System.out.println("Estoc");
        int estoc =teclat.nextInt();
        PreparedStatement sentencia = null;
       

        try {
          sentencia = connectarBD.prepareStatement(actualitza);
          sentencia.setInt(4,codi);
          sentencia.setString(1, nom);
          sentencia.setString(2, material);
          sentencia.setInt(3, estoc);
          sentencia.executeUpdate();
        } catch (SQLException sqle) {
          sqle.printStackTrace();
        } finally {
          //Nos aseguramos de cerrar los recursos abiertos
          if (sentencia != null)
            try {
              sentencia.close();
            } catch (SQLException sqle) {
              sqle.printStackTrace();
            }
        }
        System.out.println("Producte Modificat.");
    }

    public static void esborrarProductes() {
        Scanner teclat = new Scanner (System.in);
        System.out.println("Esborrar producte");
        String sentenciaSql = "DELETE FROM productes WHERE codi = ?";
        System.out.println("Codi:");
        int codi =teclat.nextInt();
        PreparedStatement sentencia = null;

        try {
          sentencia = connectarBD.prepareStatement(sentenciaSql);
          sentencia.setInt(1, codi);
          sentencia.executeUpdate();
        } catch (SQLException sqle) {
          sqle.printStackTrace();
        } finally {
          //Nos aseguramos de cerrar los recursos abiertos
          if (sentencia != null)
            try {
              sentencia.close();
            } catch (SQLException sqle) {
              sqle.printStackTrace();
            }
        }
        System.out.println("Producte esborrat.");
    }
    static void connectarBD(){

            String servidor="jdbc:mysql://localhost:3309/";
            String bbdd="empresa";
            String user="root";
            String password="Fat/3232";
            
            try{

                connectarBD = DriverManager.getConnection(servidor + bbdd, user, password);
            }
            catch (SQLException ex) {
                ex.printStackTrace();

            }
    } 
            
}


