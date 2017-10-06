/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Matija Sabolić
 */
public class AdministratorSustava {

    private String parametars;
    private boolean status;
    private String comand;
    private String server;
    private Integer port;
    private String user;
    private String password;
    private String action;

    public AdministratorSustava(String parametars) {
        this.parametars = parametars;
        this.status = false;

        this.status = TestInput(this.parametars);

        if (this.status) {
            this.comand = GenerateComand();
            PokreniAdministratora();

        } else {
            System.out.println("ERROR; Pogrešni ulazni parametri");
        }
    }

    /**
     * Metoda koja provjerava dobiven ulazni parametar. Vraća true ukoliko
     * parametar zadovoljava zadani oblik ili vraća false ukoliko parametar ne
     * zadovoljava zadani oblik.
     *
     * @param parametars
     * @return
     */
    private boolean TestInput(String parametars) {
        String[] exploded = parametars.split(" ");

        String sintaksa = "^-server (((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})|([^\\s]+))$";

        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(exploded[1] + " " + exploded[2]);
        boolean valid = m.matches();

        if (valid) {
            this.server = exploded[2];

            sintaksa = "^-port (8[0-9][0-9][0-9]|9[0-9][0-9][0-9])$";

            pattern = Pattern.compile(sintaksa);
            m = pattern.matcher(exploded[3] + " " + exploded[4]);
            valid = m.matches();

            if (valid) {

                this.port = Integer.parseInt(exploded[4]);

                sintaksa = "^-u ([a-zA-Z0-9_\\-]+)$";

                pattern = Pattern.compile(sintaksa);
                m = pattern.matcher(exploded[5] + " " + exploded[6]);
                valid = m.matches();

                if (valid) {

                    this.user = exploded[6];

                    sintaksa = "^-p ([a-zA-Z0-9\\_\\-\\#\\!]+)$";

                    pattern = Pattern.compile(sintaksa);
                    m = pattern.matcher(exploded[7] + " " + exploded[8]);
                    valid = m.matches();

                    if (valid) {
                        this.password = exploded[8];

                        sintaksa = "^(-pause|-start|-stop|-stat)$";

                        pattern = Pattern.compile(sintaksa);
                        m = pattern.matcher(exploded[9]);
                        valid = m.matches();

                        if (valid) {
                            this.action = exploded[9];
                            return true;
                        } else {
                            return false;
                        }

                    } else {
                        return false;
                    }

                } else {
                    return false;
                }

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Metoda koja generira komandu iz dobivenih ulaznih vrijednosti koja se
     * šalje serveru.
     *
     * @return
     */
    private String GenerateComand() {
        String c;

        if ("-start".equals(this.action)) {
            c = "USER " + this.user + "; PASSWD " + this.password + "; START;";
        } else if ("-stop".equals(this.action)) {
            c = "USER " + this.user + "; PASSWD " + this.password + "; STOP;";
        } else if ("-pause".equals(this.action)) {
            c = "USER " + this.user + "; PASSWD " + this.password + "; PAUSE;";
        } else {
            c = "USER " + this.user + "; PASSWD " + this.password + "; STAT;";
        }

        return c;
    }

    /**
     * Metoda koja radi spajanje na ServerSocket te mu šalje generiranu komandu.
     * Nakon što ServerSocket obradi potrebnu akciju metoda prima njegov odgovor
     * te ga ispisuje korisniku.
     */
    private void PokreniAdministratora() {
        InputStream is = null;
        OutputStream os = null;
        Socket socket = null;

        try {
            socket = new Socket(this.server, this.port);
            is = socket.getInputStream();
            os = socket.getOutputStream();

            os.write(this.comand.getBytes());
            os.flush();
            socket.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            System.out.println(sb);

        } catch (IOException ex) {
            System.out.println("ERROR; Nemoguće spajanje na server");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }

                if (os != null) {
                    os.close();
                }

                if (socket != null) {
                    socket.close();
                }

            } catch (IOException ex) {
                System.out.println("ERROR; Problem kod zatvaranja socketa");
            }
        }
    }

}
