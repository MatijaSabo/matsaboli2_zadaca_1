/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matsaboli2.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.matsaboli2.konfiguracije.Konfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.matsaboli2.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.matsaboli2.konfiguracije.NemaKonfiguracije;
import static org.foi.nwtis.matsaboli2.zadaca_1.ServerSustava.datoteka;
import static org.foi.nwtis.matsaboli2.zadaca_1.ServerSustava.serverPause;
import static org.foi.nwtis.matsaboli2.zadaca_1.ServerSustava.ss;

/**
 *
 * @author Matija Sabolić
 */
public class RadnaDretva extends Thread {

    Konfiguracija konf;
    Socket socket;
    long vrijeme_pocetka;
    public static Evidencija evidencija = new Evidencija();

    /**
     * Metoda koja vraća vrijeme početka rada dretve
     *
     * @return
     */
    public long getVrijemePocetka() {
        return vrijeme_pocetka;
    }

    public RadnaDretva(Socket socket, Konfiguracija konf) {
        this.socket = socket;
        this.konf = konf;
    }

    /**
     * Metoda koja se poziva kada je dretva prekinuta. Povečava se broj
     * prekinutih zahtjeva i ukupno vrijeme rada dretvi.
     */
    @Override
    public void interrupt() {
        try {
            evidencija.setBrojPrekinutihZahtjeva(true);
            evidencija.setUkupnoVrijemeRadaDretvi(System.currentTimeMillis() - vrijeme_pocetka);

            this.socket.close();
            super.interrupt();
        } catch (IOException ex) {
            System.out.println("ERROR; Problem kod zatvaranja socketa");
        }
    }

    /**
     * Metoda koja prima komandu koju je korisnik poslao na ServerSustava te
     * prema njezinim parametrima poziva odgovarajuću metodu za njezinu obradu.
     * Nakon što se metoda izvrši vraća odgovor korisniku. Metoda također
     * ažurira evidenciju rada.
     */
    @Override
    public void run() {

        InputStream is = null;
        OutputStream os = null;

        evidencija.setBrojZahtjevaSaAdrese(socket.getRemoteSocketAddress().toString());
        evidencija.setUkupnoZahtjeva(true);

        vrijeme_pocetka = System.currentTimeMillis();

        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }

                sb.append((char) znak);
            }

            String acepted_command = sb.toString().trim();

            int command_status = testCommand(acepted_command);
            boolean admin_status = false;

            String action_status = "";

            if (command_status == 1) {
                System.out.println(acepted_command);
                action_status = adminPause(acepted_command);
            } else if (command_status == 2) {
                System.out.println(acepted_command);
                action_status = adminStop(acepted_command);
            } else if (command_status == 3) {
                System.out.println(acepted_command);
                action_status = adminStart(acepted_command);
            } else if (command_status == 4) {
                System.out.println(acepted_command);
                action_status = adminStat(acepted_command);
            } else if (command_status == 5) {
                if (!serverPause) {
                    System.out.println(acepted_command);
                    action_status = userAdd(acepted_command);
                } else {
                    action_status = "ERROR; Server je u pauzi";
                }
            } else if (command_status == 6) {
                if (!serverPause) {
                    System.out.println(acepted_command);
                    action_status = userTest(acepted_command);
                } else {
                    action_status = "ERROR; Server je u pauzi";
                }
            } else {
                if (!serverPause) {
                    System.out.println(acepted_command);
                    action_status = userWait(acepted_command);
                } else {
                    action_status = "ERROR; Server je u pauzi";
                }
            }

            os.write(action_status.getBytes());
            os.flush();

        } catch (IOException ex) {
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

        ServerSustava.aktivneRadneDretve.remove(this.socket);
        evidencija.setBrojUspjesnihZahtjeva(true);
        evidencija.setUkupnoVrijemeRadaDretvi(System.currentTimeMillis() - vrijeme_pocetka);
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja prema radi test komande koju je server zaprimio. Metoda vraća
     * status ovisno o tipu komande.
     *
     * @param command
     * @return
     */
    public int testCommand(String command) {
        String[] explodes = command.split(" ");
        String sintaksa_1 = "^USER ([^\\\\s]+); PASSWD ([^\\\\s]+); (PAUSE|STOP|START|STAT);$";
        String sintaksa_2 = "^USER ([^\\s]+); (ADD|TEST) ([^\\s]+);$";
        String sintaksa_3 = "^USER ([^\\s]+); WAIT ([^\\d]+);$";

        Pattern pattern = Pattern.compile(sintaksa_1);
        Matcher m = pattern.matcher(command);
        boolean valid_1 = m.matches();

        pattern = Pattern.compile(sintaksa_2);
        m = pattern.matcher(command);
        boolean valid_2 = m.matches();

        pattern = Pattern.compile(sintaksa_3);
        m = pattern.matcher(command);
        boolean valid_3 = m.matches();

        if (valid_1) {
            if ("PAUSE;".equals(explodes[4])) {
                return 1;
            } else if ("STOP;".equals(explodes[4])) {
                return 2;
            } else if ("START;".equals(explodes[4])) {
                return 3;
            } else {
                return 4;
            }
        } else if (valid_2) {
            if ("ADD".equals(explodes[2])) {
                return 5;
            } else {
                return 6;
            }
        } else if (valid_3) {
            return 7;
        } else {
            return 0;
        }
    }

    /**
     * Metoda koja provjerava je li korisnik administrator ili nije i prema tome
     * vraća prikladan odgovor.
     *
     * @param command
     * @return
     */
    public boolean testAdmin(String command) {
        String[] explodes = command.split(" ");
        String user = explodes[1].substring(0, (explodes[1].length() - 1));
        String pass = explodes[3].substring(0, (explodes[3].length() - 1));
        try {
            Konfiguracija konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            String admin_file = konf.dajPostavku("adminDatoteka");
            Konfiguracija admin_konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(admin_file);
            String admin_pass = admin_konf.dajPostavku(user);

            if (admin_pass != null && admin_pass.equals(pass)) {
                return true;
            } else {
                return false;
            }

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            return false;
        }
    }

    /**
     * Metoda koja postavlja server u stanje pauze ili ukoliko je server već u
     * pauzi vraća poruku o tome.
     *
     * @param command
     * @return
     */
    public String adminPause(String command) {
        boolean admin_status = testAdmin(command);

        if (admin_status) {
            if (serverPause == true) {
                return "ERROR 01; Server je u stanju pauze";
            } else {
                serverPause = true;
                return "OK";
            }
        } else {
            return "ERROR 00; Korisnik nije administrator ili lozinka nije ispravna";
        }
    }

    /**
     * Metoda koja zatvara rad ServerSocketa i nakon toga radi serijalizaciju
     * evidencije rada.
     *
     * @param command
     * @return
     */
    public String adminStop(String command) {
        boolean admin_status = testAdmin(command);

        if (admin_status) {
            try {
                ss.close();
                SerijalizacijaEvidencije se = new SerijalizacijaEvidencije(konf, evidencija);
                se.start();
                return "OK";
            } catch (IOException ex) {
                return "ERROR 03; Problem kod prekida rada";
            }
        } else {
            return "ERROR 00; Korisnik nije administrator ili lozinka nije ispravna";
        }
    }

    /**
     * Metoda koja uspostavlja normalan rad ServerSocketa ukoliko je bio u
     * pauzi. Ukoliko nije bio u pauzi prikazuje se korisniku odgovarajuća
     * poruka.
     *
     * @param command
     * @return
     */
    public String adminStart(String command) {
        boolean admin_status = testAdmin(command);

        if (admin_status) {
            if (serverPause == true) {
                serverPause = false;
                return "OK";
            } else {
                return "ERROR 02; Server nije u stanju pauze";
            }
        } else {
            return "ERROR 00; Korisnik nije administrator ili lozinka nije ispravna";
        }
    }

    public String adminStat(String command) {
        String s = "OK; LENGTH nnnn <CRLF>";
        return s;
    }

    /**
     * Metoda koja dodaje novu adresu u listu adrsa koje je korisnik dodao za
     * provjeravanje. Ukoliko adresa več postoji u listi ili više nema mjesta za
     * novu adresu korisniku se prikazuje odgovarajuća poruka.
     *
     * @param command
     * @return
     */
    public String userAdd(String command) {
        String[] explodes = command.split(" ");
        String url = explodes[3].substring(0, (explodes[3].length() - 1));

        Konfiguracija konf;
        boolean exists = false;

        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            Integer maksAdresa = Integer.parseInt(konf.dajPostavku("maksAdresa"));

            URL testUrl = new URL(url);

            exists = evidencija.zahtjeviZaAdrese.containsKey(testUrl);

            if (exists) {
                return "ERROR 11; Adresa vec postoji u popisu adresa";
            } else if (evidencija.zahtjeviZaAdrese.size() >= maksAdresa) {
                return "ERROR 10; Adresa ne postoji u popisu adresa ali je popis adresa pun";
            } else {
                evidencija.setZahtjeviZaAdrese(testUrl, "NO", false, maksAdresa);
                return "OK";
            }

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            return "ERROR; Problem kod otvaranja datoteke konfiguracije";
        } catch (MalformedURLException ex) {
            return "ERROR; Problem kod dodavanja URL-a";
        }
    }

    /**
     * Metoda koja vraća zadnji status adrese koju korisnik želi provjeriti.
     * Ukoliko adresa ne postoji u listi adresa korisniku se prikazuje
     * odgovorajuća poruka o tome.
     *
     * @param command
     * @return
     */
    public String userTest(String command) {
        try {
            String[] explodes = command.split(" ");
            String url = explodes[3].substring(0, (explodes[3].length() - 1));
            URL testUrl = new URL(url);

            String odgovor = evidencija.getStatusAdrese(testUrl);
            if ((odgovor != null) && (!odgovor.endsWith("-"))) {
                return "OK; " + odgovor;
            } else {
                return "ERROR 12; Adresa ne postoji u listi adresa";
            }
        } catch (MalformedURLException ex) {
            return "ERROR 12; Adresa ne postoji u listi adresa";
        }
    }
    
    /**
     * Metoda koja radi spavanje dretve zadani broj sekundi.
     * @param command
     * @return 
     */

    public String userWait(String command) {
        String[] explodes = command.split(" ");
        String time = explodes[3].substring(0, (explodes[3].length() - 1));
        try {
            sleep(Integer.parseInt(time) * 1000);
            return "OK";
        } catch (InterruptedException ex) {
            return "ERROR 13; Spavanje nije uspjelo";
        }
    }

}
