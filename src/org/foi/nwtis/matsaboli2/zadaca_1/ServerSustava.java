/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.matsaboli2.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author Matija Sabolić
 */
public class ServerSustava {

    public static ServerSocket ss;
    public static boolean serverPause;
    public static String datoteka;
    public static int redniBrojDretve;
    public static int maksBrojDretvi;
    public static int maksBrojZahtjeva;
    public static ArrayList<RadnaDretva> aktivneRadneDretve = new ArrayList<RadnaDretva>();
    public static Socket korSocket;

    /**
     * Metoda koja provjerava dobivene ulazne parametre te ukoliko zadovoljavaju
     * oblik pokreče ServerSocket.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String sintaksa = "^-konf ([^\\s]+\\.(?i)(txt|xml|bin))( +-load)?$";
        Boolean load = false;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (status) {
            if (m.group(3) != null) {
                load = true;
            }

            datoteka = m.group(1).toString();

            ServerSustava server = new ServerSustava();
            server.pokreniServer(datoteka, load);
        } else {
            System.out.println("Ne odgovara!");
        }

        return;
    }

    /**
     * Metoda koja kreira ServerSocket te za svaku njegovu konekciju kreira
     * radnu ili rezernu dretvu. Metoda pokreće i adresnu, nadzornu, i dretvu za
     * serijalizaciju evidencije rada.
     *
     * @param naziv_datoteke
     * @param load
     */
    private void pokreniServer(String naziv_datoteke, Boolean load) {

        Konfiguracija konf;
        NadzorDretvi nd;
        RezernaDretva rezd;
        ProvjeraAdresa provd;
        SerijalizacijaEvidencije se;

        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(naziv_datoteke);

            if (load) {
                ucitajEvidenciju(konf);
            }

            int port = Integer.parseInt(konf.dajPostavku("port"));
            maksBrojDretvi = Integer.parseInt(konf.dajPostavku("maksBrojRadnihDretvi"));
            maksBrojZahtjeva = Integer.parseInt(konf.dajPostavku("brojZahtjevaZaSerijalizaciju"));

            ss = new ServerSocket(port);

            nd = new NadzorDretvi(konf);
            provd = new ProvjeraAdresa(konf);
            nd.start();
            provd.start();

            serverPause = false;
            redniBrojDretve = 0;

            while (true) {
                korSocket = ss.accept();

                if (aktivneRadneDretve.size() < maksBrojDretvi) {
                    redniBrojDretve++;
                    RadnaDretva rd = new RadnaDretva(korSocket, konf);
                    rd.setName("matsaboli2 - " + redniBrojDretve);
                    rd.start();

                    aktivneRadneDretve.add(rd);

                    RadnaDretva.evidencija.setZadnjiBrojRadneDretve(redniBrojDretve);

                    if (maksBrojZahtjeva <= RadnaDretva.evidencija.getUkupnoZahtjeva()) {
                        se = new SerijalizacijaEvidencije(konf, RadnaDretva.evidencija);
                        se.start();
                    }
                } else {
                    rezd = new RezernaDretva(konf);
                    rezd.start();
                }
            }
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija | IOException ex) {
            System.out.println("Server zatvoren");
        }
    }

    /**
     * Metoda koja radi deserijalizaciju evidencije rada i učitava ju kao
     * postojeću evidenciju.
     *
     * @param konf
     */
    private void ucitajEvidenciju(Konfiguracija konf) {
        String file = konf.dajPostavku("evidDatoteka");

        try {
            FileInputStream fis = new FileInputStream(file);
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                RadnaDretva.evidencija = (Evidencija) ois.readObject();
            }
        } catch (IOException i) {
            System.out.println("ERROR; Problem kod učitavanja datoteke evidencije");
        } catch (ClassNotFoundException c) {
            System.out.println("ERROR; Problem kod učitavanja datoteke evidencije");
        }
    }
}
