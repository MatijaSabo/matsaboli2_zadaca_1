/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author Matija Sabolić
 */
public class Evidencija implements Serializable {

    public int ukupnoZahtjeva = 0;
    public int brojUspjesnihZahtjeva = 0;
    public int brojPrekinutihZahtjeva = 0;
    public HashMap zahtjeviZaAdrese = new HashMap();
    public int zadnjiBrojRadneDretve = 0;
    public HashMap zahtjeviSaAdrese = new HashMap();
    public long ukupnoVrijemeRadaDretvi = 0;

    /**
     * Metoda koja povečava ili smanjuje ukupni broj zahtjeva ovisno o dobivenom
     * parametru.
     *
     * @param action
     */
    public synchronized void setUkupnoZahtjeva(boolean action) {
        if (action == true) {
            ukupnoZahtjeva = ukupnoZahtjeva + 1;
        } else {
            ukupnoZahtjeva = ukupnoZahtjeva - 1;
        }
    }

    /**
     * Metoda koja vraća ukupni broj zahtjeva.
     *
     * @return
     */
    public synchronized int getUkupnoZahtjeva() {
        return ukupnoZahtjeva;
    }

    /**
     * Metoda koja povečava ili smanjuje broj uspješnih zahtjeva ovisno o
     * dobivenom parametru.
     *
     * @param action
     */
    public synchronized void setBrojUspjesnihZahtjeva(boolean action) {
        if (action == true) {
            brojUspjesnihZahtjeva = brojUspjesnihZahtjeva + 1;
        } else {
            brojUspjesnihZahtjeva = brojUspjesnihZahtjeva - 1;
        }
    }

    /**
     * Metoda koja vraća broj uspješnih zahtjeva.
     *
     * @return
     */
    public synchronized int getBrojUspjesnihZahtjeva() {
        return brojUspjesnihZahtjeva;
    }

    /**
     * Metoda koja povečava ili smanjuje broj prekinutih zahtjeva ovisno o
     * dobivenom ulaznom parametru.
     *
     * @param action
     */
    public synchronized void setBrojPrekinutihZahtjeva(boolean action) {
        if (action == true) {
            brojPrekinutihZahtjeva = brojPrekinutihZahtjeva + 1;
        } else {
            brojPrekinutihZahtjeva = brojPrekinutihZahtjeva - 1;
        }
    }

    /**
     * Metoda koja vraća broj prekinutih zahtjeva.
     *
     * @return
     */
    public synchronized int getBrojPrekinutihZahtjeva() {
        return brojPrekinutihZahtjeva;
    }

    /**
     * Metoda koja postavlja novi zadnji broj radne dretve
     *
     * @param broj
     */
    public synchronized void setZadnjiBrojRadneDretve(Integer broj) {
        zadnjiBrojRadneDretve = broj;
    }

    /**
     * Metoda koja vraća zadnji broj radne dretve.
     *
     * @return
     */
    public synchronized int getZadnjiBrojRadneDretve() {
        return zadnjiBrojRadneDretve;
    }

    /**
     * Metoda koja ovisno o ulaznim parametrima dodaje novu adresu u listu
     * adresa ili radi zamjenu statusa pojedine adrese u listi.
     *
     * @param newUrl
     * @param status
     * @param action
     * @param maxVelicina
     */
    public synchronized void setZahtjeviZaAdrese(URL newUrl, String status, boolean action, int maxVelicina) {
        if (action == true) {
            zahtjeviZaAdrese.replace(newUrl, status);
        } else if (zahtjeviZaAdrese.size() < maxVelicina) {
            zahtjeviZaAdrese.put(newUrl, status);
        }
    }

    /**
     * Metoda koja vraća status adrese koja je zadana kao parametar.
     *
     * @param url
     * @return
     */
    public synchronized String getStatusAdrese(URL url) {
        if (zahtjeviZaAdrese.containsKey(url)) {
            String value = (String) zahtjeviZaAdrese.get(url);
            return value;
        } else {
            return "-";
        }
    }

    /**
     * Metoda koja vraća broj adresa koje su dodane u listu.
     *
     * @return
     */
    public synchronized Integer getBrojZahtjevaZaAdresom() {
        return zahtjeviZaAdrese.size();
    }

    /**
     * Metoda koja dodaje novu adresu u listu adresa sa kojih je došao zahtjev
     * ili povečava njezin broj za 1.
     *
     * @param adresa
     */
    public synchronized void setBrojZahtjevaSaAdrese(String adresa) {
        if (zahtjeviSaAdrese.containsKey(adresa)) {
            int broj = Integer.parseInt(zahtjeviSaAdrese.get(adresa).toString());
            zahtjeviSaAdrese.replace(adresa, broj + 1);
        } else {
            zahtjeviSaAdrese.put(adresa, 1);
        }
    }

    /**
     * Metoda koja vraća broj zahtjeva koji su došli sa adrese koja je zadana.
     *
     * @param adresa
     * @return
     */
    public synchronized Integer getBrojZahtjevaSaAdrese(String adresa) {
        return Integer.parseInt(zahtjeviSaAdrese.get(adresa).toString());
    }

    /**
     * Metoda koja povečava ukupno vrijeme rada dretvi za dobiveno vrijeme.
     *
     * @param time
     */
    public synchronized void setUkupnoVrijemeRadaDretvi(long time) {
        ukupnoVrijemeRadaDretvi = ukupnoVrijemeRadaDretvi + time;
    }

    /**
     * Metoda koja vraća ukupno vrijeme rada dretvi.
     *
     * @return
     */
    public synchronized long getUkupnoVrijemeRadaDretvi() {
        return ukupnoVrijemeRadaDretvi;
    }
}
