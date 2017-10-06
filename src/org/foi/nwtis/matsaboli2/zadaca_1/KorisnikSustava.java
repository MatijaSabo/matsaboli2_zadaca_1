/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

/**
 *
 * @author Matija Sabolić
 */
public class KorisnikSustava {

    /**
     * Metoda koja provjerava prvi argument dobiven preko parametara i prema
     * njegovom obliku kreira objekt odgovarajuće klase.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String parametars = "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        parametars = sb.toString().trim();

        if ("-admin".equals(args[0])) {
            AdministratorSustava administratorSustava = new AdministratorSustava(parametars);
        } else if ("-user".equals(args[0])) {
            KlijentSustava klijentSustava = new KlijentSustava(parametars);
        } else if ("-prikaz".equals(args[0])) {
            PregledSustava pregledSustava = new PregledSustava(parametars);
        } else {
            System.out.println("ERROR; Pogrešni ulazni parametri");
            return;
        }
    }
}
