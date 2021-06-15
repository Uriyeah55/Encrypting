/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reptepsp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Uri
 */
public class ReptePSP {

    int decisio;
    Scanner dadesIntroInt = new Scanner(System.in);
    Scanner dadesIntroString = new Scanner(System.in);

    static final String RUTA_ARXIU_USUARIS = "./admin/usuaris.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Scanner dadesIntroInt = new Scanner(System.in);

        int choice1;

        do {
            System.out.println("Menu");
            System.out.println("0-Quit");
            System.out.println("1-Register new User");
            System.out.println("2-Log in");
            System.out.println("3-Delete all users");

            choice1 = Teclado.leerInt("Choice?\n");

            switch (choice1) {
                case 0:
                    System.out.println("Exiting...");
                    break;
                case 1:

                    try {
                        //Registrarse
                        RegisterNewUser();
                        break;
                    } catch (Exception e) {
                        System.out.println("Error");
                    }

                    break;
                case 2:

                    //Login usuari creat
                    File fr = new File(RUTA_ARXIU_USUARIS);

                    boolean empty = fr.length() == 0;

                    if (empty) {

                        System.out.println("Currently there are no registed users...");
                    } else {

                        String userTry = Teclado.leerString("Username:\n");
                        String contraTry = Teclado.leerString("Password:\n");
                        boolean correctUser = verifyUser(userTry, contraTry);

                        if (correctUser) {
                            System.out.println("=============");
                            System.out.println("Welcome, " + userTry);
                            System.out.println("Opcions usuari:");
                            System.out.println("0-Tornar enrere");
                            System.out.println("1-Encriptar arxius");
                            System.out.println("2-Desencriptar arxius");
                            System.out.println("3-Gestionar dades d'usuari");

                            String currentUser = userTry;
                            int decisio2 = dadesIntroInt.nextInt();
                            switch (decisio2) {
                                case 0:
                                    //sortir
                                    break;
                                case 1:
                                    //encriptar arxiu
                                    String nomArxiu = Teclado.leerString("Name of the file to encrypt:\n");
                                    //enviem false perque obrirem un arxiu NO encriptat
                                    EncryptFile(nomArxiu);

                                    break;
                                case 2:
                                    //desencriptar
                                    String nomArxiuDesenc = Teclado.leerString("Nom de l'arxiu a desencriptar:\n");
                                    DesencriptarArxiu(nomArxiuDesenc);
                                    break;
                                case 3:

                                    //gestionar dades usuari+eliminem l'usuari i li tornem a demanar les dades sabent ja l'usuari
                                    RepeatUserInfo(currentUser);
                                    break;
                            }

                        }

                    }

                    break;
                case 3:
                    String confirmacio = Teclado.leerString("Are you sure? s/n:\n");
                    if (confirmacio.equalsIgnoreCase("s")) {
                        emptyFile();

                    } else {
                        System.out.println("Tornant al menú...");
                    }

                    break;
            }

        } while (choice1 != 0);

    }

    static void RegisterNewUser() {

        String usuari = Teclado.leerString("Enter the user:\n");

        while (usuari.equals("")) {
            System.out.println("Error: message can't be empty\n");
            usuari = Teclado.leerString("Introdueix l'usuari:\n");

        }
        //si es true tornem a demanar perque esta repetit
        boolean comprobarUserRepetit = checkUserExistence(usuari);

        while (comprobarUserRepetit) {
            System.out.println("Error: user exists\n");
            usuari = Teclado.leerString("Introdueix un nou nom d'usuari:\n");

            comprobarUserRepetit = checkUserExistence(usuari);

        }

        String nom = Teclado.leerString("Introdueix el nom:\n");

        while (nom.equals("")) {
            System.out.println("Error: missatge buit\n");
            nom = Teclado.leerString("Introdueix el nom:\n");

        }

        String cognoms = Teclado.leerString("Introdueix el cognom\n");

        while (cognoms.equals("")) {
            System.out.println("Error: missatge buit\n");
            cognoms = Teclado.leerString("Introdueix el cognom:\n");

        }

        String contrassenya = Teclado.leerString("Password:\n");

        while (contrassenya.equals("")) {
            System.out.println("Error: missatge buit\n");

            contrassenya = Teclado.leerString("Introdueix la contrassenya:\n");

        }

        String repetirContrassenya = Teclado.leerString("Introdueix-la de nou:\n");

        while (repetirContrassenya.equals("")) {
            System.out.println("Error: message can't be empty\n");

            repetirContrassenya = Teclado.leerString("Introdueix-la de nou:\n");

        }

        if (contrassenya.equals(repetirContrassenya)) {

            String contrassenyaEncriptada = "";
            try {

                contrassenyaEncriptada = BCrypt.hashpw(contrassenya, BCrypt.gensalt());

                System.out.println("Cadena Original: " + contrassenya);
                System.out.println("Escriptado     : " + contrassenyaEncriptada);

            } catch (Exception e) {

            }

            String email = Teclado.leerString("Email:\n");

            while (email.equals("")) {
                System.out.println("Error: missatge buit\n");
                email = Teclado.leerString("Email:\n");

            }
            guardarUsuariFitxer(nom, cognoms, usuari, contrassenyaEncriptada, email);
        } else {
            System.out.println("Password didn't match. Going back to the menu...\n");

        }

    }

    static boolean checkUserExistence(String usuari) {
        //System.out.println("busco el usuari " + usuari);
        try {
            FileReader fr = new FileReader(RUTA_ARXIU_USUARIS);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                if (line.contains(usuari + ":")) {
                    System.out.println("l'he trobat");
                    fr.close();
                    return true;
                }
                line = br.readLine();

            }
            fr.close();
            return false;

        } catch (Exception e) {
            System.out.println("Error");
        }
        return false;
    }

    static void emptyFile() {
//buida les dades de usuaris
        try {
            PrintWriter pw = new PrintWriter(RUTA_ARXIU_USUARIS);
            pw.close();
            System.out.println("Usuaris eliminats");

        } catch (Exception e) {

        }

    }

    static void EncryptFile(String fileName) {

        try {

            File myObj = new File(fileName + ".txt");

            if (myObj.exists()) {

                String contingutFitxer = "";
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    contingutFitxer += myReader.nextLine();
                    contingutFitxer += "\n";
                }

                String contrassenya = Teclado.leerString("Contrassenya per encriptar");
                Encriptacions encriptador = new Encriptacions();

                try {
                    String contingutEncriptat = encriptador.encriptar(contingutFitxer, contrassenya);
                    EscriureFitxer(fileName + ".enc", contingutEncriptat);

                } catch (Exception e) {
                    System.out.println("Error");
                }

                myReader.close();

            } else {

                System.out.println("El fitxer no existeix.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    static void DesencriptarArxiu(String nomArxiu) {

        try {

            File myObj = new File(nomArxiu + ".enc");

            if (myObj.exists()) {

                String contingutFitxer = "";
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    contingutFitxer += myReader.nextLine();
                }
                System.out.println(contingutFitxer);

                String contrassenya = Teclado.leerString("Contrassenya per a desencriptar");
                Encriptacions encriptador = new Encriptacions();

                try {
                    String contingutEncriptat = encriptador.desencriptar(contingutFitxer, contrassenya);
                    EscriureFitxer(nomArxiu + "_desencriptat.txt", contingutEncriptat);

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }

                myReader.close();

            } else {

            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    static void EscriureFitxer(String nomFitxer, String contingut) {
        try {

            FileWriter fw = new FileWriter(nomFitxer);
            fw.write(contingut);
            fw.close();
        } catch (Exception e) {
            System.out.println("Error");
        }

    }

    static boolean verifyUser(String usuariTry, String contrassenyaTry) {

        try {
            FileReader fr = new FileReader(RUTA_ARXIU_USUARIS);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                if (line.startsWith(usuariTry + ":")) {
                    String[] llistaPunts = line.split(":");
                    String contrassenyaEncriptada = llistaPunts[llistaPunts.length - 1];
                    return BCrypt.checkpw(contrassenyaTry, contrassenyaEncriptada);
                }
                line = br.readLine();

            }
            fr.close();
            System.out.println("Dades no vàlides. Tornant al menú...");
            return false;

        } catch (Exception e) {
            System.out.println("Error");
        }
        return false;
    }

    static void RepeatUserInfo(String usuariActual) {

        EliminarUsuari(usuariActual);
        //no demanem usuari perque utilitzarem el mateix

        String nom = Teclado.leerString("Nou nom:\n");

        while (nom.equals("")) {
            System.out.println("Error: missatge buit\n");
            nom = Teclado.leerString("Nou nom:\n");

        }

        String cognoms = Teclado.leerString("Nou cognom\n");

        while (cognoms.equals("")) {
            System.out.println("Error: missatge buit\n");
            cognoms = Teclado.leerString("Nou cognom:\n");

        }

        String contrassenya = Teclado.leerString("Nova contrassenya:\n");

        while (contrassenya.equals("")) {
            System.out.println("Error: missatge buit\n");

            contrassenya = Teclado.leerString("Nova contrassenya:\n");

        }

        String repetirContrassenya = Teclado.leerString("Introdueix-la de nou:\n");

        while (repetirContrassenya.equals("")) {
            System.out.println("Error: missatge buit\n");

            repetirContrassenya = Teclado.leerString("Introdueix-la de nou:\n");

        }

        if (contrassenya.equals(repetirContrassenya)) {

            String contrassenyaEncriptada = "";
            try {

                contrassenyaEncriptada = BCrypt.hashpw(contrassenya, BCrypt.gensalt());

                System.out.println("Cadena Original: " + contrassenya);
                System.out.println("Escriptado     : " + contrassenyaEncriptada);

            } catch (Exception e) {

            }

            String email = Teclado.leerString("Nou email:\n");

            while (email.equals("")) {
                System.out.println("Error: missatge buit\n");
                email = Teclado.leerString("Nou email:\n");

            }
            guardarUsuariFitxer(nom, cognoms, usuariActual, contrassenyaEncriptada, email);

        } else {
            System.out.println("La contrassenya no ha coincidit. Tornant al menú.\n");

        }
    }

    static void EliminarUsuari(String nomUsuari) {

        try {

            FileReader fr = new FileReader(RUTA_ARXIU_USUARIS);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer newContent = new StringBuffer();

            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith(nomUsuari + ":")) {

                    newContent.append(line);
                    newContent.append("\n"); // new line
                    //delete
                }
                line = br.readLine();

            }
            fr.close();
            EscriureFitxer(RUTA_ARXIU_USUARIS, newContent.toString());
        } catch (Exception e) {
            System.out.println("Dades no vàlides. Tornant al menú...");

        }

    }

    static void guardarUsuariFitxer(String nom, String cognoms, String usuari, String contrassenyaEncriptada, String email) {

        try {

            String dadesUsuari = usuari + ":" + nom + ":" + cognoms + ":" + email + ":" + contrassenyaEncriptada + "\n";

            FileWriter fw = new FileWriter(RUTA_ARXIU_USUARIS, true);

            fw.write(dadesUsuari);
            fw.close();
            System.out.println("Usuari guardat amb èxit.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
