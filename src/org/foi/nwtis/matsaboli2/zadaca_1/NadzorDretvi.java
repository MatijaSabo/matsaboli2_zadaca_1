/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;

/**
 *
 * @author Matija Sabolić
 */
public class NadzorDretvi extends Thread {

    Konfiguracija konf;

    public NadzorDretvi(Konfiguracija konf) {
        this.konf = konf;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja u pravilnim vremenskim ciklusima provjerava da li se radne
     * dretve ne izvršavaju predugo. Ukoliko se neka od njih izvršava predugo
     * prekida se njezin rad.
     */
    @Override
    public void run() {
        int trajanjeSpavanja = Integer.parseInt(konf.dajPostavku("intervalNadzorneDretve"));
        int maksVrijemeRadneDretve = Integer.parseInt(konf.dajPostavku("maksVrijemeRadneDretve"));

        while (true) {
            long trenutnoVrijeme = System.currentTimeMillis();

            for (int i = 0; i < ServerSustava.aktivneRadneDretve.size(); i++) {
                if ((trenutnoVrijeme - ServerSustava.aktivneRadneDretve.get(i).getVrijemePocetka()) > maksVrijemeRadneDretve) {
                    System.out.println("Prekinut rad dretve: " + ServerSustava.aktivneRadneDretve.get(i).getName());
                    ServerSustava.aktivneRadneDretve.get(i).interrupt();
                    ServerSustava.aktivneRadneDretve.remove(i);
                }
            }

            long vrijemeZavrsetka = System.currentTimeMillis();

            try {
                sleep(trajanjeSpavanja - (vrijemeZavrsetka - trenutnoVrijeme));
            } catch (InterruptedException ex) {
                System.out.println("ERROR; Problem kod spavanja nadzorne dretve");
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
}
