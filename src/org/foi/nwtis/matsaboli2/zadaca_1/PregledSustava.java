/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Matija Sabolić
 */
public class PregledSustava {

    private String parametars;
    private boolean status;
    private String datoteka;

    public PregledSustava(String parametars) {
        this.parametars = parametars;
        this.status = false;

        this.status = TestInput(parametars);

        if (this.status) {
            PrintData();
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
        String sintaksa = "^-s ([^\\s]+)$";

        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(exploded[1] + " " + exploded[2]);
        boolean valid = m.matches();

        if (valid) {
            this.datoteka = exploded[2];
            return true;
        } else {
            return false;
        }
    }

    /**
     * Metoda koja radi deserijalizaciju datoteke te ispisuje njezin sadržaj
     * korisniku u čitljivom obliku.
     */
    private void PrintData() {
        try {
            FileInputStream fis = new FileInputStream(this.datoteka);
            Evidencija evidencija;
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                evidencija = (Evidencija) ois.readObject();
            }

            System.out.println("Ispis evidencije rada sustava:");
            System.out.println("-----------------------------------------------");
            System.out.println("Broj ukupnih zahtjeva: " + evidencija.getUkupnoZahtjeva());
            System.out.println("Broj uspješnih zahtjeva: " + evidencija.getBrojUspjesnihZahtjeva());
            System.out.println("Broj prekinutih zahtjeva: " + evidencija.getBrojPrekinutihZahtjeva());
            System.out.println("Ukupno vrijeme rada dretvi: " + String.valueOf(evidencija.getUkupnoVrijemeRadaDretvi()));
            System.out.println("Zadnji broj radne dretve: " + evidencija.getZadnjiBrojRadneDretve());
            System.out.println("");
            System.out.println("Broj zahtjeva sa pojedine adrese:");

            Iterator it = evidencija.zahtjeviSaAdrese.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                System.out.println("Adresa: " + pair.getKey() + " - Broj zahtjeva: " + pair.getValue());
            }

            System.out.println("");
            System.out.println("Unesene adrese:");

            Iterator it2 = evidencija.zahtjeviZaAdrese.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry pair = (Map.Entry) it2.next();

                System.out.println("URL: " + pair.getKey() + " - Validan: " + pair.getValue());
            }
        } catch (IOException i) {
            System.out.println("ERROR; Problem kod otvaranja datoteke evidencije");
        } catch (ClassNotFoundException c) {
            System.out.println("ERROR; Problem kod otvaranja datoteke evidencije");
        }
    }
}
