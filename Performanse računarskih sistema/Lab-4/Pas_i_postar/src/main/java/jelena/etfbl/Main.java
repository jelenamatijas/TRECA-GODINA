package jelena.etfbl;

import java.util.Random;

public class Main {

    static final double DT        = 0.001;  // vremenski korak (s)
    static final double UGRIZ_RAD = 0.5;    // udaljenost za ugriz (m)
    static final double MAX_T     = 300.0;  // max trajanje simulacije (s)

    static final int    N_UZORAKA      = 1000;  // broj slucajnih brojeva za testiranje
    static final int    K_INTERVALA    = 10;    // broj intervala za hi-kvadrat test
    static final int    A_GRID         = 5;     // dimenzija grida za serijski test (AxA)
    static final double ALPHA          = 0.05;  // nivo znacajnosti (95% pouzdanost)

    static Random rng = new Random();

    static boolean hiKvadratTest() {
        System.out.println("5.5.1 Goodness-of-fit test (Hi-kvadrat test)");
        System.out.printf("Generisanje %d slucajnih brojeva, %d intervala.%n%n", N_UZORAKA, K_INTERVALA);

        // Broji koliko generisanih vrijednosti pada u svaki interval [i/k, (i+1)/k)
        int[] izmjereno = new int[K_INTERVALA];
        for (int i = 0; i < N_UZORAKA; i++) {
            double u = rng.nextDouble();
            int interval = (int)(u * K_INTERVALA);
            if (interval == K_INTERVALA) interval = K_INTERVALA - 1; // rubni slucaj
            izmjereno[interval]++;
        }

        // Ocekivana vrijednost po intervalu za U(0,1): N/k
        double ocekivano = (double) N_UZORAKA / K_INTERVALA;

        System.out.println("  Interval       | Izmjereno | Ocekivano | (O-E)^2/E");

        double hiKvadrat = 0.0;
        for (int i = 0; i < K_INTERVALA; i++) {
            double razlika = izmjereno[i] - ocekivano;
            double doprinos = (razlika * razlika) / ocekivano;
            hiKvadrat += doprinos;
            System.out.printf("  [%.1f , %.1f)   |    %4d   |   %6.1f  | %8.4f%n",
                    (double) i / K_INTERVALA,
                    (double)(i + 1) / K_INTERVALA,
                    izmjereno[i], ocekivano, doprinos);
        }

        // Stepeni slobode = k - 1 = 9
        int df = K_INTERVALA - 1;

        // Kriticne vrijednosti hi-kvadrat raspodjele (iz tabele, Prilog C skripte)
        // Za alpha=0.05 i df=9 => kriticna vrijednost = 16.919
        double kriticnaVrijednost = hiKvadratKriticnaVrijednost(df, ALPHA);

        System.out.printf("%n  Izracunata hi-kvadrat vrijednost : %.4f%n", hiKvadrat);
        System.out.printf("  Kriticna vrijednost (df=%d, a=%.2f): %.3f%n",
                df, ALPHA, kriticnaVrijednost);

        boolean prosao = hiKvadrat < kriticnaVrijednost;
        if (prosao) {
            System.out.println("  REZULTAT: PROSAO - nema razloga sumnjati u uniformnost raspodjele.");
        } else {
            System.out.println("  REZULTAT: NIJE PROSAO - raspodjela nije uniformna!");
        }
        System.out.println();
        return prosao;
    }


    static boolean serijskiTest() {
        System.out.println("5.5.2 Serijski test nezavisnosti (2D grid)");
        System.out.printf("Generisanje %d parova, grid %dx%d.%n%n",
                N_UZORAKA / 2, A_GRID, A_GRID);

        // Broji tacke u svakoj celiji AxA grida
        int[][] grid = new int[A_GRID][A_GRID];
        int nParova = N_UZORAKA / 2;

        for (int i = 0; i < nParova; i++) {
            double u1 = rng.nextDouble();
            double u2 = rng.nextDouble();
            int col = (int)(u1 * A_GRID);
            int row = (int)(u2 * A_GRID);
            if (col == A_GRID) col = A_GRID - 1;
            if (row == A_GRID) row = A_GRID - 1;
            grid[row][col]++;
        }

        // Prikaz grida
        System.out.println("  Raspodjela tacaka po celijama grida:");
        System.out.print("       ");
        for (int c = 0; c < A_GRID; c++)
            System.out.printf("  u1=[%.1f,%.1f)", (double)c/A_GRID, (double)(c+1)/A_GRID);
        System.out.println();

        for (int r = 0; r < A_GRID; r++) {
            System.out.printf("  u2=[%.1f,%.1f)", (double)r/A_GRID, (double)(r+1)/A_GRID);
            for (int c = 0; c < A_GRID; c++) {
                System.out.printf("    %4d       ", grid[r][c]);
            }
            System.out.println();
        }

        // Hi-kvadrat test na griду
        // Ocekivano po celiji: nParova / (A*A)
        double ocekivanoPoceliji = (double) nParova / (A_GRID * A_GRID);
        double hiKvadrat = 0.0;
        for (int r = 0; r < A_GRID; r++) {
            for (int c = 0; c < A_GRID; c++) {
                double razlika = grid[r][c] - ocekivanoPoceliji;
                hiKvadrat += (razlika * razlika) / ocekivanoPoceliji;
            }
        }

        // Stepeni slobode = A*A - 1 = 24
        int df = A_GRID * A_GRID - 1;
        double kriticnaVrijednost = hiKvadratKriticnaVrijednost(df, ALPHA);

        System.out.printf("%n  Ocekivano po celiji             : %.2f%n", ocekivanoPoceliji);
        System.out.printf("  Izracunata hi-kvadrat vrijednost: %.4f%n", hiKvadrat);
        System.out.printf("  Kriticna vrijednost (df=%d, a=%.2f): %.3f%n",
                df, ALPHA, kriticnaVrijednost);

        boolean prosao = hiKvadrat < kriticnaVrijednost;
        if (prosao) {
            System.out.println("  REZULTAT: PROSAO - vrijednosti su statisticki nezavisne.");
        } else {
            System.out.println("  REZULTAT: NIJE PROSAO - postoje zavisnosti medu vrijednostima!");
        }
        System.out.println();
        return prosao;
    }

    static double hiKvadratKriticnaVrijednost(int df, double alpha) {
        // Kriticne vrijednosti za alpha = 0.05 (95% pouzdanost)
        // Indeks = stepeni slobode (1..30)
        double[] tabela005 = {
                0,       // df=0  (neiskoristen)
                3.841,   // df=1
                5.991,   // df=2
                7.815,   // df=3
                9.488,   // df=4
                11.070,  // df=5
                12.592,  // df=6
                14.067,  // df=7
                15.507,  // df=8
                16.919,  // df=9  <- koristi hi-kvadrat test (k=10)
                18.307,  // df=10
                19.675,  // df=11
                21.026,  // df=12
                22.362,  // df=13
                23.685,  // df=14
                24.996,  // df=15
                26.296,  // df=16
                27.587,  // df=17
                28.869,  // df=18
                30.144,  // df=19
                31.410,  // df=20
                32.671,  // df=21
                33.924,  // df=22
                35.172,  // df=23
                36.415,  // df=24  <- koristi serijski test (A=5, df=24)
                37.652,  // df=25
                38.885,  // df=26
                40.113,  // df=27
                41.337,  // df=28
                42.557,  // df=29
                43.773   // df=30
        };

        if (df >= 1 && df < tabela005.length) {
            return tabela005[df];
        }
        // Za vece df: aproksimacija
        return df + 1.645 * Math.sqrt(2.0 * df);
    }


    // Brzina psa tip 1 (deterministicka, zavisi od globalnog t)
    static double brzinaPas1(double t) {
        return (t < 5.0) ? 2.0 * t : 10.0;
    }

    // Brzina psa tip 2 (zavisi od trun - vremena od posljednjeg pada)
    static double brzinaPas2(double trun) {
        return (trun < 4.0) ? 3.0 * trun : 12.0;
    }

    // Funkcija klizanja: vjerojatnost pada raste s trun
    // P(pad u koraku) = (1 - e^(-0.15 * trun)) * DT
    // Koristi verifikovani RNG
    static boolean slip(double trun) {
        double p = 1.0 - Math.exp(-0.15 * trun);
        return rng.nextDouble() < p * DT;
    }

    // Pozicija postara A: stoji 3s, zatim v=5
    static double pozicijaPostarA(double p0, double t) {
        return (t <= 3.0) ? p0 : p0 + 5.0 * (t - 3.0);
    }

    // Pozicija postara B: odmah krece, v=2.5
    static double pozicijaPostarB(double p0, double t) {
        return p0 + 2.5 * t;
    }


    static double simuliraj(int tipPas, char tipPostar, double p0, boolean ispisi) {
        double px   = 0.0;
        double py   = 20.0;
        double trun = 0.0;

        if (ispisi) {
            System.out.printf("  Pocetak: pas=(%.2f, %.2f), postar x0=%.2f%n", px, py, p0);
        }

        for (double t = 0.0; t <= MAX_T; t += DT) {

            double qx = (tipPostar == 'A')
                    ? pozicijaPostarA(p0, t)
                    : pozicijaPostarB(p0, t);

            double dx   = qx - px;
            double dy   = -py;         // postar je uvijek na y=0
            double dist = Math.sqrt(dx * dx + dy * dy);

            // Provjera ugirza
            if (dist <= UGRIZ_RAD) {
                if (ispisi) {
                    System.out.printf("  UGRIZ u t=%.3f s | pas=(%.3f, %.3f) | postar=(%.3f, 0)%n",
                            t, px, py, qx);
                }
                return t;
            }

            // Brzina i pomak
            double s;
            if (tipPas == 1) {
                s = brzinaPas1(t);
            } else {
                if (t > 0 && slip(trun)) {
                    if (ispisi) {
                        System.out.printf("  Pas pao! t=%.3f, trun=%.3f%n", t, trun);
                    }
                    trun = 0.0;
                    s    = 0.0;
                } else {
                    trun += DT;
                    s = brzinaPas2(trun);
                }
            }

            if (dist > 1e-9) {
                px += (dx / dist) * s * DT;
                py += (dy / dist) * s * DT;
            }
        }

        if (ispisi) System.out.println("  Pas nije stigao postara.");
        return -1.0;
    }


    static void pokrniKombinaciju(int tipPas, char tipPostar) {
        System.out.printf("%n=== Pas tip %d  vs  Postar tip %c ===%n", tipPas, tipPostar);

        // Pocetna pozicija postara generisana verifikovanim RNG-om: U(5, 25)
        double p0 = 5.0 + rng.nextDouble() * 20.0;
        System.out.printf("Pocetna pozicija postara (RNG): p0 = %.4f m%n%n", p0);

        double vrijemeUgirza = simuliraj(tipPas, tipPostar, p0, true);

        if (vrijemeUgirza > 0) {
            System.out.printf("%n  Zakljucak: Pas tip %d je ugrizao postara tip %c za %.3f sekundi.%n",
                    tipPas, tipPostar, vrijemeUgirza);
        } else {
            System.out.printf("%n  Zakljucak: Pas tip %d nije stigao postara tip %c.%n",
                    tipPas, tipPostar);
        }
        System.out.println("------------------------------------------------------");
    }


    public static void main(String[] args) {

        System.out.println("# KORAK 1: VERIFIKACIJA GENERATORA SLUCAJNIH BROJEVA #");
        System.out.println();

        boolean testGOF      = hiKvadratTest();
        boolean testNezavis  = serijskiTest();

        if (!testGOF || !testNezavis) {
            System.out.println("!!! UPOZORENJE: Generator nije prosao verifikaciju.");
            System.out.println("!!! Rezultati simulacije mozda nisu pouzdani.");
            System.out.println();
        } else {
            System.out.println(">>> Oba testa prosla uspjesno.");
            System.out.println(">>> Generator slucajnih brojeva je verifikovan.");
            System.out.println(">>> Simulacija ce dati pouzdane rezultate.");
            System.out.println();
        }


        System.out.println("# KORAK 2: SIMULACIJA (4 kombinacije)                #");

        pokrniKombinaciju(1, 'A');
        pokrniKombinaciju(1, 'B');
        pokrniKombinaciju(2, 'A');
        pokrniKombinaciju(2, 'B');

        System.out.println();
        System.out.println("  Simulacija zavrsena.");
    }
}