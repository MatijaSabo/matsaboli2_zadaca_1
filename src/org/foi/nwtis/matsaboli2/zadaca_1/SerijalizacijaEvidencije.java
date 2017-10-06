/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;

/**
 *
 * @author Matija Sabolić
 */
public class SerijalizacijaEvidencije extends Thread {

    Konfiguracija konf;
    Evidencija evidencija;

    public SerijalizacijaEvidencije(Konfiguracija konf, Evidencija evidencija) {
        this.konf = konf;
        this.evidencija = evidencija;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja radi serijalizaciju evidencije koju je korisnik poslao kao
     * parametar.
     */
    @Override
    public void run() {
        FileOutputStream fos = null;
        try {
            String output_file = konf.dajPostavku("evidDatoteka");
            fos = new FileOutputStream(output_file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(evidencija);
            oos.close();
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR; Problem kod otvaranja datoteke");
        } catch (IOException ex) {
            System.out.println("ERROR; Problem kod pisanja u datoteku");
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
}
