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

        int decisio1;

        do {
            System.out.println("Decisions");
            System.out.println("0-Sortir");
            System.out.println("1-Registrar Usuari");
            System.out.println("2-Accedir");
            System.out.println("3-Eliminar tots els usuaris");

            decisio1 = Teclado.leerInt("Decisió?\n");

            switch (decisio1) {
                case 0:
                    System.out.println("Sortint");
                    break;
                case 1:

                    try {
                        //Registrarse
                        RegistrarUsuari();
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

                        System.out.println("No hi ha cap usuari registrat. Tornant al meú...");
                    } else {

                        String userTry = Teclado.leerString("usuari:\n");
                        String contraTry = Teclado.leerString("Contrassenya:\n");
                        boolean usuariCorrecte = verificarUsuari(userTry, contraTry);

                        if (usuariCorrecte) {
                            System.out.println("Opcions usuari:");
                            System.out.println("0-Tornar enrere");
                            System.out.println("1-Encriptar arxius");
                            System.out.println("2-Desencriptar arxius");
                            System.out.println("3-Gestionar dades d'usuari");

                            String userActual = userTry;
                            int decisio2 = dadesIntroInt.nextInt();
                            switch (decisio2) {
                                case 0:
                                    //sortir
                                    break;
                                case 1:
                                    //encriptar arxiu
                                    String nomArxiu = Teclado.leerString("Nom de l'arxiu a encriptar:\n");
                                    //enviem false perque obrirem un arxiu NO encriptat
                                    EncriptarArxiu(nomArxiu);

                                    break;
                                case 2:
                                    //desencriptar
                                    String nomArxiuDesenc = Teclado.leerString("Nom de l'arxiu a desencriptar:\n");
                                    DesencriptarArxiu(nomArxiuDesenc);
                                    break;
                                case 3:

                                    //gestionar dades usuari+eliminem l'usuari i li tornem a demanar les dades sabent ja l'usuari
                                    RepetirDadesUsuari(userActual);
                                    break;
                            }

                        }

                    }

                    break;
                case 3:
                    String confirmacio = Teclado.leerString("Estàs segur? s/n:\n");
                    if (confirmacio.equalsIgnoreCase("s")) {
                        buidarFitxer();

                    } else {
                        System.out.println("Tornant al menú...");
                    }

                    break;
            }

        } while (decisio1 != 0);

    }

    static void RegistrarUsuari() {

        String usuari = Teclado.leerString("Introdueix l'usuari:\n");

        while (usuari.equals("")) {
            System.out.println("Error: missatge buit\n");
            usuari = Teclado.leerString("Introdueix l'usuari:\n");

        }
        //si es true tornem a demanar perque esta repetit
        boolean comprobarUserRepetit = comprovarExistenciaUsuari(usuari);

        while (comprobarUserRepetit) {
            System.out.println("Error: l'usuari ja existeix\n");
            usuari = Teclado.leerString("Introdueix un nou nom d'usuari:\n");

            comprobarUserRepetit = comprovarExistenciaUsuari(usuari);

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

        String contrassenya = Teclado.leerString("Introdueix la contrassenya:\n");

        while (contrassenya.equals("")) {
            System.out.println("Error: missatge buit\n");

            contrassenya = Teclado.leerString("Introdueix la contrassenya:\n");

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

            String email = Teclado.leerString("Introdueix el mail:\n");

            while (email.equals("")) {
                System.out.println("Error: missatge buit\n");
                email = Teclado.leerString("Introdueix el mail:\n");

            }
            guardarUsuariFitxer(nom, cognoms, usuari, contrassenyaEncriptada, email);
        } else {
            System.out.println("La contrassenya no ha coincidit. Tornant al menú.\n");

        }

    }

    static boolean comprovarExistenciaUsuari(String usuari) {
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

    static void buidarFitxer() {
//buida les dades de usuaris
        try {
            PrintWriter pw = new PrintWriter(RUTA_ARXIU_USUARIS);
            pw.close();
            System.out.println("Usuaris eliminats");

        } catch (Exception e) {

        }

    }

    static void EncriptarArxiu(String nomArxiu) {

        try {

            File myObj = new File(nomArxiu + ".txt");

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
                    EscriureFitxer(nomArxiu + ".enc", contingutEncriptat);

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

    static boolean verificarUsuari(String usuariTry, String contrassenyaTry) {

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

    static void RepetirDadesUsuari(String usuariActual) {

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
