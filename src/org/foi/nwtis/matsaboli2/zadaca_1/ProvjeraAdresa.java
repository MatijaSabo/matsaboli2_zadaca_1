/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import static org.foi.nwtis.matsaboli2.zadaca_1.RadnaDretva.evidencija;

/**
 *
 * @author Matija Sabolić
 */
public class ProvjeraAdresa extends Thread {

    Konfiguracija konf;

    public ProvjeraAdresa(Konfiguracija konf) {
        this.konf = konf;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja u pravilnim vremenskim ciklusima provjerava status pojedine
     * adrese koju je korisnik dodao u listu adresa i ažurira njezin prijašnji
     * status u listi adresa.
     */
    @Override
    public void run() {
        int trajanjeSpavanja = Integer.parseInt(konf.dajPostavku("intervalAdresneDretve"));
        int maksAdresa = Integer.parseInt(konf.dajPostavku("maksAdresa"));

        while (true) {
            long trenutnoVrijeme = System.currentTimeMillis();

            Iterator it = evidencija.zahtjeviZaAdrese.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                URL url = null;
                int responseCode;

                try {
                    url = new URL(pair.getKey().toString());
                    HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                    huc.setRequestMethod("HEAD");
                    responseCode = huc.getResponseCode();
                } catch (MalformedURLException ex) {
                    responseCode = 404;
                } catch (IOException ex) {
                    responseCode = 404;
                }

                if (responseCode == 404) {
                    evidencija.setZahtjeviZaAdrese(url, "NO", true, maksAdresa);
                } else {
                    evidencija.setZahtjeviZaAdrese(url, "YES", true, maksAdresa);
                }
            }

            long vrijemeZavrsetka = System.currentTimeMillis();

            try {
                sleep(trajanjeSpavanja - (vrijemeZavrsetka - trenutnoVrijeme));
            } catch (InterruptedException ex) {
                System.out.println("ERROR; Problem kod spavanja adresne dretve");
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
}
