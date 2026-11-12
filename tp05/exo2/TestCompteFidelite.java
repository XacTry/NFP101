import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestCompteFidelite {

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
    // QUESTION 2 : ENCAPSULATION
    // ========================================

    private static void question2() {
        section("Question 2 - Encapsulation");

        Field solde = attribut(Compte.class, "solde");
        verifier("solde est private dans Compte",
            solde != null && Modifier.isPrivate(solde.getModifiers()));

        verifier("CompteFidelite herite de Compte",
            CompteFidelite.class.getSuperclass() == Compte.class);
        verifier("CompteFidelite n'a aucun attribut propre",
            CompteFidelite.class.getDeclaredFields().length == 0);
        verifier("deposer(int) est redefinie",
            redefinit(CompteFidelite.class, "deposer", int.class));
        verifier("Compte n'a pas de methode en plus (getTitulaire, getSolde, deposer, retirer, description)",
            Compte.class.getDeclaredMethods().length == 5);
    }

    // ========================================
    // QUESTION 3 : VERIFICATION DE LA CORRECTION
    // ========================================

    private static void question3() {
        section("Question 3 - Depot et prime");

        CompteFidelite alice = new CompteFidelite("Alice", 100);
        verifier("deposer(50) accepte", alice.deposer(50));
        verifier("depot + prime de 10 % : 100 + 50 + 5 = 155", alice.getSolde() == 155);

        CompteFidelite petit = new CompteFidelite("Alice", 100);
        verifier("deposer(9) accepte sans prime (9 / 10 = 0)",
            petit.deposer(9) && petit.getSolde() == 109);

        section("Question 3 - Integer.MAX_VALUE");

        CompteFidelite bob = new CompteFidelite("Bob", Integer.MAX_VALUE - 100);
        verifier("deposer(100) accepte", bob.deposer(100));
        verifier("prime ignoree, solde = MAX_VALUE (pas de passage en negatif)",
            bob.getSolde() == Integer.MAX_VALUE);

        CompteFidelite presque = new CompteFidelite("Bob", Integer.MAX_VALUE - 105);
        verifier("prime qui ne rentre pas entierement : ignoree (tout ou rien)",
            presque.deposer(100) && presque.getSolde() == Integer.MAX_VALUE - 5);

        CompteFidelite juste = new CompteFidelite("Bob", Integer.MAX_VALUE - 110);
        verifier("depot + prime qui tombent pile sur MAX_VALUE",
            juste.deposer(100) && juste.getSolde() == Integer.MAX_VALUE);

        section("Question 3 - Depot refuse");

        CompteFidelite c = new CompteFidelite("Alice", 100);
        verifier("deposer(0) refuse, pas de prime", !c.deposer(0) && c.getSolde() == 100);
        verifier("deposer(-50) refuse, pas de prime", !c.deposer(-50) && c.getSolde() == 100);

        CompteFidelite plein = new CompteFidelite("Bob", Integer.MAX_VALUE);
        verifier("deposer(100) sur un compte plein refuse",
            !plein.deposer(100) && plein.getSolde() == Integer.MAX_VALUE);

        section("Question 3 - Polymorphisme");

        Compte vuCommeCompte = new CompteFidelite("Alice", 100);
        verifier("la prime s'applique via une variable de type Compte",
            vuCommeCompte.deposer(50) && vuCommeCompte.getSolde() == 155);
        verifier("retirer() herite de Compte",
            vuCommeCompte.retirer(55) && vuCommeCompte.getSolde() == 100);

        section("Question 3 - description()");

        verifier("description() est redefinie",
            redefinit(CompteFidelite.class, "description"));
        verifier("description()",
            "Compte Alice : 155 credits | compte fidelite".equals(alice.description()));
    }

    // ========================================
    // QUESTION 3 : COMPTELIMITE TOUJOURS OK
    // ========================================

    private static void compteLimite() {
        section("Question 3 - CompteLimite avec solde private");

        CompteLimite c = new CompteLimite("Alice", 100, 30);
        verifier("retirer(30) accepte", c.retirer(30) && c.getSolde() == 70);
        verifier("retirer(40) refuse", !c.retirer(40) && c.getSolde() == 70);
        verifier("retirer(0) refuse", !c.retirer(0) && c.getSolde() == 70);
        verifier("deposer(25) accepte", c.deposer(25) && c.getSolde() == 95);
        verifier("description()",
            "Compte Alice : 95 credits | plafond retrait : 30".equals(c.description()));
    }

    // ========================================
    // MAIN
    // ========================================

    public static void main(String[] args) {
        System.out.println("TP05 - EXERCICE 2 - TEST COMPTE FIDELITE");

        question2();
        question3();
        compteLimite();

        bilan();
    }

    private static void bilan() {
        section("Bilan");
        System.out.println("Reussis : " + reussis);
        System.out.println("Echecs : " + echecs);
    }
}
