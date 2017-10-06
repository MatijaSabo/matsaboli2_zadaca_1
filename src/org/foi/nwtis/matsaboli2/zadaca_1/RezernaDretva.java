/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.IOException;
import java.io.OutputStream;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;

/**
 *
 * @author Matija Sabolić
 */
public class RezernaDretva extends Thread {

    Konfiguracija konf;

    public RezernaDretva(Konfiguracija konf) {
        this.konf = konf;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja se poziva ukoliko više nemamo raspoloživih radnih dretvi.
     * Korisniku se ispisuje poruka o tome te se zatvara socket konekcija.
     */
    @Override
    public void run() {
        OutputStream os = null;
        try {
            RadnaDretva.evidencija.setUkupnoZahtjeva(true);
            String s = "ERROR; Nema slobodne radne dretve";
            os = ServerSustava.korSocket.getOutputStream();
            os.write(s.getBytes());
            os.flush();
            ServerSustava.korSocket.shutdownOutput();
        } catch (IOException ex) {
            System.out.println("ERROR; Problem kod slanja odgovora korisniku");
        } finally {

            try {
                if (os != null) {
                    os.close();
                }

                sleep(10);

                if (ServerSustava.korSocket != null) {
                    ServerSustava.korSocket.close();
                }
            } catch (IOException ex) {
                System.out.println("ERROR; Problem kod zatvaranja socketa");
            } catch (InterruptedException ex) {
                System.out.println("ERROR; Problem kod zatvaranja socketa");
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
}
