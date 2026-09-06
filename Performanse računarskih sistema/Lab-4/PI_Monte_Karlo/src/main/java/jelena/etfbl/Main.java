package jelena.etfbl;

import java.util.Scanner;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Unos broja slučajno generisanih tačaka
        long nTotal = 0;
        while (nTotal <= 0) {
            System.out.print("Unesite broj slučajno generisanih tačaka (npr. 1000000): ");
            if (scanner.hasNextInt()) {
                nTotal = scanner.nextInt();
                if (nTotal <= 0) {
                    System.out.println("  [!] Broj tačaka mora biti pozitivan cijeli broj.");
                }
            } else {
                System.out.println("  [!] Nevažeći unos. Unesite cijeli broj.");
                scanner.next();
            }
        }

        // Unos broja decimalnih mjesta koja treba da se poklapaju
        long decimalPlaces = -1;
        while (decimalPlaces < 0 || decimalPlaces > 15) {
            System.out.print("Unesite broj decimalnih mjesta za provjeru poklapanja (0-15): ");
            if (scanner.hasNextInt()) {
                decimalPlaces = scanner.nextInt();
                if (decimalPlaces < 0 || decimalPlaces > 15) {
                    System.out.println("  [!] Broj decimala mora biti između 0 i 15.");
                }
            } else {
                System.out.println("  [!] Nevažeći unos. Unesite cijeli broj.");
                scanner.next();
            }
        }

        scanner.close();

        // === Monte Karlo simulacija ===
        Random random = new Random();
        long nCirc = 0;           // broj tačaka unutar četvrtine kruga
        long stepSize = Math.max(1, nTotal / 20); // za prikaz napretka

        long startTime = System.currentTimeMillis();

        for (long i = 1; i <= nTotal; i++) {
            double u1 = random.nextDouble(); // slučajna x koordinata [0, 1)
            double u2 = random.nextDouble(); // slučajna y koordinata [0, 1)

            // Provjera da li tačka pada unutar četvrtine kruga (rastojanje < 1)
            double distance = Math.sqrt(u1 * u1 + u2 * u2);
            if (distance < 1.0) { 
                nCirc++;
            }
        }

        long endTime = System.currentTimeMillis();

        // === Rezultati ===
        double estimatedPi = 4.0 * nCirc / nTotal;
        double realPi = Math.PI;
        double absoluteError = Math.abs(realPi - estimatedPi);
        double relativeError = (absoluteError / realPi) * 100.0;

        // Provjera poklapanja decimalnih mjesta
        double tolerance = Math.pow(10, -decimalPlaces);
        boolean matches = absoluteError < tolerance;

        System.out.println();
        System.out.printf( "Broj generisanih tačaka :  %,20d  %n", nTotal);
        System.out.printf( "Tačke unutar kruga (nCirc):  %,17d  %n", nCirc);

        // Formatiranje za prikaz sa zadanim brojem decimala
        String format = "%." + (decimalPlaces + 4) + "f";
        System.out.printf( "Izračunata vrijednost π  :  " + format + "            %n", estimatedPi);
        System.out.printf( "Stvarna vrijednost π     :  " + format + "            %n", realPi);
        System.out.printf( "Apsolutna greška         :  %.10e          %n", absoluteError);
        System.out.printf( "Relativna greška         :  %.8f %%          %n", relativeError);

        // Prikaz poklapanja cifara
        System.out.printf("Traženo poklapanje       :  %d decimalna mjesta%n", decimalPlaces);
        if (matches) {
            System.out.printf("Rezultat poklapanja      :  POKLAPANJE POSTIGNUTO  %n");
        } else {
            System.out.printf("Rezultat poklapanja      :  POKLAPANJE NIJE DOSTIGNUTO %n");
        }

        System.out.printf( "Trajanje simulacije      :  %d ms%n", (endTime - startTime));

        // Vizuelni prikaz poklapenih cifara
        System.out.println();
        System.out.println("  Poređenje cifara:");
        System.out.println("  Stvarno  π = " + formatPiComparison(realPi,      estimatedPi, decimalPlaces));
        System.out.println("  Dobijeno π = " + formatPiComparison(estimatedPi, realPi,      decimalPlaces));
        System.out.println();
    }

    /**
     * Vraća string prikaza broja sa označenim poklapajućim ciframa (do zadanog broja decimala).
     */
    private static String formatPiComparison(double value, double reference, long decimals) {
        String valStr = String.format("%." + (decimals + 5) + "f", value);
        String refStr = String.format("%." + (decimals + 5) + "f", reference);

        StringBuilder sb = new StringBuilder();
        boolean matching = true;

        for (int i = 0; i < valStr.length() && i < refStr.length(); i++) {
            char cv = valStr.charAt(i);
            char cr = refStr.charAt(i);
            if (cv == '.') {
                sb.append('.');
                continue;
            }
            if (matching && cv == cr) {
                sb.append('[').append(cv).append(']'); // označena poklapajuća cifra
            } else {
                matching = false;
                sb.append(cv);
            }
        }

        return sb.toString();
    }
}