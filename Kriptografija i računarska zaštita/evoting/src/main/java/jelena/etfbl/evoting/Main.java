package jelena.etfbl.evoting;

import jelena.etfbl.evoting.crypto.*;
import jelena.etfbl.evoting.database.VotingDAO;
import jelena.etfbl.evoting.model.RegistrationService;
import jelena.etfbl.evoting.model.User;
import jelena.etfbl.evoting.model.UserRole;

import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static User currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static void main(String[] args){
        System.out.println("==================================================");
        System.out.println("     SIGURNI E-VOTING SISTEM (PKI + AES/RSA)     ");
        System.out.println("==================================================");

        while(true){
            if(currentUser == null){
                showGuestMenu();
            }else if(currentUser.getUserRole() == UserRole.ORGANIZATOR){
                showOrganizerMenu();
            }else{
                showVoterMenu();
            }
        }

    }

    private static void showGuestMenu(){
        System.out.println("\n--- GLAVNI MENI ---");
        System.out.println("1. Registracija Organizatora");
        System.out.println("2. Registracija Glasača");
        System.out.println("3. Prijava na sistem");
        System.out.println("0. Izlaz");
        System.out.print("Odaberite opciju: ");

        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1":
                    System.out.print("Korisničko ime: "); String oUser = scanner.nextLine();
                    System.out.print("Lozinka: "); String oPass = scanner.nextLine();
                    System.out.print("Naziv organizacije: "); String orgName = scanner.nextLine();
                    System.out.print("MBO / ID broj: "); String idNum = scanner.nextLine();
                    RegistrationService.registrationOrganizer(oUser, oPass, orgName, idNum);
                    break;
                case "2":
                    System.out.print("Korisničko ime: "); String vUser = scanner.nextLine();
                    System.out.print("Lozinka: "); String vPass = scanner.nextLine();
                    System.out.print("Ime: "); String fName = scanner.nextLine();
                    System.out.print("Prezime: "); String lName = scanner.nextLine();
                    RegistrationService.registerVoter(vUser, vPass, fName, lName);
                    break;
                case "3":
                    System.out.print("Korisničko ime: "); String u = scanner.nextLine();
                    System.out.print("Lozinka: "); String p = scanner.nextLine();
                    currentUser = AuthService.login(u, p, "user_keys/" + u + ".p12");
                    break;
                case "0":
                    System.exit(0);
                default:
                    System.out.println("Nepostojeća opcija!");
            }
        } catch (Exception e) {
            System.err.println("Greška: " + e.getMessage());
        }
    }

    private static void showOrganizerMenu(){
        System.out.println("\n--- MENI ORGANIZATORA (" + currentUser.getUsername() + ") ---");
        System.out.println("1. Kreiraj novo glasanje");
        System.out.println("2. Pregledaj moja glasanja");
        System.out.println("3. Dešifruj i prebroj glasove");
        System.out.println("4. Provjeri integritet metapodataka (HMAC)");
        System.out.println("5. Opozovi korisnika (CRL Revokacija)");
        System.out.println("6. Odjava");
        System.out.print("Odaberite opciju: ");

        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1": {
                    System.out.print("Naslov glasanja: "); String title = scanner.nextLine();
                    System.out.print("Opis: "); String desc = scanner.nextLine();
                    System.out.print("Kandidati/opcije (2-5, odvojeni zarezom, npr. Opcija A, Opcija B): ");
                    String optionsRaw = scanner.nextLine();

                    List<String> options = new ArrayList<>();
                    for (String opt : optionsRaw.split(",")) {
                        String trimmed = opt.trim();
                        if (!trimmed.isEmpty()) {
                            options.add(trimmed);
                        }
                    }

                    Timestamp start = readTimestamp("Vrijeme pocetka (yyyy-MM-dd HH:mm), prazno = odmah: ", new Date());
                    Timestamp defaultEnd = new Timestamp(start.getTime() + 24L * 3600 * 1000);
                    Timestamp end = readTimestamp("Vrijeme zavrsetka (yyyy-MM-dd HH:mm), prazno = +24h od pocetka: ", new Date(defaultEnd.getTime()));

                    int vId = VotingService.createVoting(currentUser.getUserId(), title, desc, start, end, options);
                    System.out.println("Uspješno kreirano glasanje sa ID: " + vId);
                    break;
                }
                case "2": {
                    List<VotingDAO.VotingSummary> myVotings = VotingService.getVotingsForOrganizer(currentUser.getUserId());
                    if (myVotings.isEmpty()) {
                        System.out.println("Još niste kreirali nijedno glasanje.");
                        break;
                    }
                    for (VotingDAO.VotingSummary v : myVotings) {
                        System.out.println("--------------------------------------------------");
                        System.out.println("ID: " + v.votingId + " | " + v.title + " | Status: " + v.status);
                        System.out.println("Opis: " + v.description);
                        System.out.println("Pocetak: " + v.startTime + " | Kraj: " + v.endTime);
                        System.out.println("Kandidati: " + v.options);
                    }
                    System.out.println("--------------------------------------------------");
                    break;
                }
                case "3": {
                    System.out.print("Unesite ID glasanja za prebrojavanje: ");
                    int votingId = Integer.parseInt(scanner.nextLine());

                    System.out.print("Unesite Vašu organizatorsku lozinku: ");
                    String orgPassword = scanner.nextLine();

                    TallyService.tallyVotes(votingId, currentUser.getUsername(), orgPassword);
                    break;
                }
                case "4": {
                    System.out.print("Unesite ID glasanja za provjeru integriteta metapodataka: ");
                    int votingId = Integer.parseInt(scanner.nextLine());
                    List<Integer> corrupted = VotingService.verifyMetadataIntegrity(votingId);
                    if (corrupted.isEmpty()) {
                        System.out.println("Integritet metapodataka je OK - svi HMAC potpisi su ispravni.");
                    } else {
                        System.out.println("UPOZORENJE: narusen integritet metapodataka za glasace sa ID-jevima: " + corrupted);
                    }
                    break;
                }
                case "5":
                    System.out.print("Unesite korisničko ime za opoziv: "); String revUser = scanner.nextLine();
                    RevocationService.revokeUser(revUser);
                    break;
                case "6":
                    currentUser = null;
                    break;
                default:
                    System.out.println("Nepostojeća opcija!");
            }
        } catch (Exception e) {
            System.err.println("Greška: " + e.getMessage());
        }
    }

    private static void showVoterMenu() {
        System.out.println("\n--- MENI GLASAČA (" + currentUser.getUsername() + ") ---");
        System.out.println("1. Pregledaj aktivna glasanja i glasaj");
        System.out.println("2. Verifikuj moj glas (receipt kod)");
        System.out.println("3. Odjava");
        System.out.print("Odaberite opciju: ");

        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1": {
                    List<VotingDAO.VotingSummary> votings = VotingService.getActiveVotingsForVoter();
                    if (votings.isEmpty()) {
                        System.out.println("Trenutno nema aktivnih glasanja.");
                        break;
                    }
                    for (VotingDAO.VotingSummary v : votings) {
                        System.out.println("--------------------------------------------------");
                        System.out.println("ID: " + v.votingId + " | " + v.title);
                        System.out.println("Opis: " + v.description);
                        System.out.println("Aktivno do: " + v.endTime);
                        System.out.println("Kandidati:");
                        for (int i = 0; i < v.options.size(); i++) {
                            System.out.println("  " + (i + 1) + ". " + v.options.get(i));
                        }
                    }
                    System.out.println("--------------------------------------------------");

                    System.out.print("\nUnesite ID glasanja: ");
                    int vId = Integer.parseInt(scanner.nextLine());

                    VotingDAO.VotingSummary chosen = null;
                    for (VotingDAO.VotingSummary v : votings) {
                        if (v.votingId == vId) {
                            chosen = v;
                            break;
                        }
                    }
                    if (chosen == null) {
                        System.out.println("Nepostojeći ili neaktivan ID glasanja.");
                        break;
                    }

                    System.out.println("Kandidati za ovo glasanje:");
                    for (int i = 0; i < chosen.options.size(); i++) {
                        System.out.println("  " + (i + 1) + ". " + chosen.options.get(i));
                    }
                    System.out.print("Unesite redni broj kandidata za koga glasate: ");
                    int optionIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (optionIndex < 0 || optionIndex >= chosen.options.size()) {
                        System.out.println("Nepostojeći redni broj kandidata.");
                        break;
                    }
                    String selectedOption = chosen.options.get(optionIndex);

                    String orgUsername = VotingDAO.getOrganizerUsernameForVoting(vId);
                    if (orgUsername == null) {
                        System.out.println("Nije moguce pronaci organizatora ovog glasanja.");
                        break;
                    }
                    X509Certificate orgCert = CertificateUtils.loadPublicCertificate("user_keys/" + orgUsername + ".crt");

                    System.out.print("Potvrdite VAŠU lozinku za potpisivanje glasa: ");
                    String voterPass = scanner.nextLine();

                    String receipt = VotingService.castVote(vId, currentUser.getUserId(), currentUser.getUsername(), voterPass, selectedOption, orgCert);
                    if (receipt != null) {
                        System.out.println("Glasanje uspješno! Vaš receipt kod (sačuvajte ga radi verifikacije): " + receipt);
                    } else {
                        System.out.println("Glas nije prihvaćen (provjerite poruku iznad).");
                    }
                    break;
                }
                case "2": {
                    System.out.print("Unesite receipt kod dobijen prilikom glasanja: ");
                    String receiptHash = scanner.nextLine();
                    boolean exists = VotingService.verifyVote(receiptHash);
                    if (exists) {
                        System.out.println("Vaš glas je pronađen u sistemu - uspješno je zabilježen.");
                    } else {
                        System.out.println("Glas sa ovim receipt kodom nije pronađen.");
                    }
                    break;
                }
                case "3":
                    currentUser = null;
                    break;
                default:
                    System.out.println("Nepostojeća opcija!");
            }
        } catch (Exception e) {
            System.err.println("Greška: " + e.getMessage());
        }
    }

    private static Timestamp readTimestamp(String prompt, Date defaultValue) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            return new Timestamp(defaultValue.getTime());
        }
        try {
            Date parsed = DATE_FORMAT.parse(line);
            return new Timestamp(parsed.getTime());
        } catch (ParseException e) {
            System.out.println("Nepravilan format, koristi se podrazumijevana vrijednost: " + DATE_FORMAT.format(defaultValue));
            return new Timestamp(defaultValue.getTime());
        }
    }
}
