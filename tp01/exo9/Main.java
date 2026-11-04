import java.util.Scanner;

public class Main {

    public static int plusProcheDeZero(int[] temperatures) {
        if (temperatures.length == 0) {
            return 0; // aucune temperature : on affiche 0
        }
        int meilleure = temperatures[0];
        for (int t : temperatures) {
            int ecartT = Math.abs(t);
            int ecartMeilleure = Math.abs(meilleure);
            if (ecartT < ecartMeilleure || (ecartT == ecartMeilleure && t < meilleure)) {
 {
                meilleure = t;
            }
        }
        return meilleure;
    }

    public static void main(String[] args) {
        String[] valeurs = args;
        if (valeurs.length == 0) {
            Scanner sc = new Scanner(System.in);
            String ligne = sc.hasNextLine() ? sc.nextLine().trim() : "";
            sc.close();
            valeurs = ligne.isEmpty() ? new String[0] : ligne.split("\\s+");
        }

        int[] temperatures = new int[valeurs.length];
        for (int i = 0; i < valeurs.length; i++) {
            temperatures[i] = Integer.parseInt(valeurs[i]);
        }

        System.out.println(plusProcheDeZero(temperatures));
    }
}