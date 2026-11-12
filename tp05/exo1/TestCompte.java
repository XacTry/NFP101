import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestCompte {

    private static int reussis = 0;
    private static int echecs = 0;

    // ========================================
    // OUTILS DE TEST
    // ========================================

    private static void section(String titre) {
        System.out.println("\n=== " + titre + " ===");
    }

    private static void verifier(String description, boolean condition) {
        if (condition) {
            System.out.println("[OK] " + description);
            reussis++;
        } else {
            System.out.println("[ECHEC] " + description);
            echecs++;
        }
    }

    private static Field attribut(Class<?> classe, String nom) {
        try {
            return classe.getDeclaredField(nom);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private static boolean redefinit(Class<?> classe, String nom, Class<?>... types) {
        try {
            classe.getDeclaredMethod(nom, types);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    // ========================================
    // QUESTION 1 : COMPTE
    // ========================================

    private static void question1() {
        section("Question 1 - Compte");

        Field titulaire = attribut(Compte.class, "titulaire");
        Field solde = attribut(Compte.class, "solde");
        verifier("titulaire est un String private",
            titulaire != null && titulaire.getType() == String.class
                && Modifier.isPrivate(titulaire.getModifiers()));
        verifier("solde est un int protected",
            solde != null && solde.getType() == int.class
                && Modifier.isProtected(solde.getModifiers()));

        Compte c = new Compte("Alice", 100);
        verifier("getTitulaire()", "Alice".equals(c.getTitulaire()));
        verifier("getSolde()", c.getSolde() == 100);
        verifier("description()", "Compte Alice : 100 credits".equals(c.description()));

        verifier("deposer(30) accepte", c.deposer(30) && c.getSolde() == 130);
        verifier("retirer(20) accepte", c.retirer(20) && c.getSolde() == 110);
        verifier("retirer(110) vide le compte", c.retirer(110) && c.getSolde() == 0);

        c = new Compte("Alice", 100);
        verifier("deposer(0) refuse", !c.deposer(0) && c.getSolde() == 100);
        verifier("deposer(-10) refuse", !c.deposer(-10) && c.getSolde() == 100);
        verifier("retirer(0) refuse", !c.retirer(0) && c.getSolde() == 100);
        verifier("retirer(-5) refuse", !c.retirer(-5) && c.getSolde() == 100);
        verifier("retirer(101) refuse", !c.retirer(101) && c.getSolde() == 100);

        Compte plein = new Compte("Bob", Integer.MAX_VALUE - 10);
        verifier("deposer jusqu'a MAX_VALUE accepte",
            plein.deposer(10) && plein.getSolde() == Integer.MAX_VALUE);
        verifier("deposer au-dela de MAX_VALUE refuse",
            !plein.deposer(1) && plein.getSolde() == Integer.MAX_VALUE);

        verifier("solde initial negatif ramene a 0", new Compte("Bob", -5).getSolde() == 0);
    }

    // ========================================
    // QUESTION 2 : COMPTELIMITE
    // ========================================

    private static void question2() {
        section("Question 2 - CompteLimite");

        verifier("CompteLimite herite de Compte",
            CompteLimite.class.getSuperclass() == Compte.class);

        Field plafond = attribut(CompteLimite.class, "plafondRetrait");
        verifier("plafondRetrait est un int private",
            plafond != null && plafond.getType() == int.class
                && Modifier.isPrivate(plafond.getModifiers()));
        verifier("pas d'attribut titulaire duplique",
            attribut(CompteLimite.class, "titulaire") == null);
        verifier("pas d'attribut solde duplique",
            attribut(CompteLimite.class, "solde") == null);
        verifier("pas de getTitulaire() duplique",
            !redefinit(CompteLimite.class, "getTitulaire"));
        verifier("pas de getSolde() duplique",
            !redefinit(CompteLimite.class, "getSolde"));

        CompteLimite c = new CompteLimite("Alice", 100, 30);
        verifier("constructeur : titulaire", "Alice".equals(c.getTitulaire()));
        verifier("constructeur : solde", c.getSolde() == 100);
        verifier("getPlafondRetrait()", c.getPlafondRetrait() == 30);
        verifier("solde initial negatif ramene a 0",
            new CompteLimite("Bob", -5, 30).getSolde() == 0);
        verifier("plafond negatif ramene a 0",
            new CompteLimite("Bob", 100, -5).getPlafondRetrait() == 0);
    }

    // ========================================
    // QUESTION 3 : REDEFINITION
    // ========================================

    private static void question3() {
        section("Question 3 - Redefinition");

        verifier("retirer(int) est redefinie", redefinit(CompteLimite.class, "retirer", int.class));
        verifier("description() est redefinie", redefinit(CompteLimite.class, "description"));
        verifier("deposer(int) n'est pas redefinie",
            !redefinit(CompteLimite.class, "deposer", int.class));

        CompteLimite c = new CompteLimite("Alice", 100, 30);
        verifier("description()",
            "Compte Alice : 100 credits | plafond retrait : 30".equals(c.description()));

        verifier("retirer(20) accepte", c.retirer(20) && c.getSolde() == 80);
        verifier("retirer(30) accepte", c.retirer(30) && c.getSolde() == 50);
        verifier("retirer(40) refuse", !c.retirer(40) && c.getSolde() == 50);
        verifier("retirer(0) refuse", !c.retirer(0) && c.getSolde() == 50);
        verifier("retirer(-5) refuse", !c.retirer(-5) && c.getSolde() == 50);

        CompteLimite petit = new CompteLimite("Bob", 20, 30);
        verifier("retirer(25) refuse si solde insuffisant",
            !petit.retirer(25) && petit.getSolde() == 20);

        verifier("deposer(25) fonctionne sans redefinition",
            c.deposer(25) && c.getSolde() == 75);

        Compte vuCommeCompte = new CompteLimite("Alice", 100, 30);
        verifier("le plafond s'applique via une variable de type Compte",
            !vuCommeCompte.retirer(40) && vuCommeCompte.getSolde() == 100);
    }

    // ========================================
    // MAIN
    // ========================================

    public static void main(String[] args) {
        System.out.println("TP05 - EXERCICE 1 - TEST COMPTE");

        question1();
        question2();
        question3();

        bilan();
    }

    private static void bilan() {
        section("Bilan");
        System.out.println("Reussis : " + reussis);
        System.out.println("Echecs : " + echecs);
    }
}
