
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestCompte {

    private static Class<?> classe;

    private static int reussis = 0;
    private static int echecs = 0;
    private static int manquants = 0;

    // ========================================
    // OUTILS DE TEST
    // ========================================

    private static void section(String titre) {
        System.out.println("\n=== " + titre + " ===");
    }

    private static void verifier(
            String description, boolean condition) {

        if (condition) {
            System.out.println("[OK] " + description);
            reussis++;
        } else {
            System.out.println("[ECHEC] " + description);
            echecs++;
        }
    }

    private static void manquant(String description) {
        System.out.println("[NON IMPLEMENTE] " + description);
        manquants++;
    }

    private static Field attribut(String nom) {
        try {
            Field champ = classe.getDeclaredField(nom);
            champ.setAccessible(true);
            return champ;
        } catch (Exception e) {
            return null;
        }
    }

    
private static Method methode(
        String nom, Class<?>... types) {

    try {
        Method m = classe.getDeclaredMethod(nom, types);
        m.setAccessible(true);
        return m;

    } catch (NoSuchMethodException e) {
        return null;
    }
}


    
private static Object creer(String nom, int solde) {
    try {
        Constructor<?> constructeur =
            classe.getDeclaredConstructor(String.class, int.class);

        constructeur.setAccessible(true);

        return constructeur.newInstance(nom, solde);

    } catch (NoSuchMethodException e) {
        manquant("Constructeur Compte(String, int)");

    } catch (Exception e) {
        verifier("Creation du compte", false);
        System.out.println("Detail : " + e.getMessage());
    }

    return null;
}


    private static Object lire(
            Object objet, String nomAttribut) {

        Field champ = attribut(nomAttribut);

        if (champ == null || objet == null) {
            return null;
        }

        try {
            return champ.get(objet);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean appeler(
            Object objet, String nom, int montant) {

        Method m = methode(nom, int.class);

        if (m == null) {
            manquant("Methode " + nom + "(int)");
            return false;
        }

        try {
            m.invoke(objet, montant);
            return true;
        } catch (Exception e) {
            verifier("Execution de " + nom, false);
            return false;
        }
    }

    // ========================================
    // QUESTION 1 : STRUCTURE DE LA CLASSE
    // ========================================

    private static void question1() {
        section("Question 1 - Structure de Compte");

        Field proprietaire = attribut("proprietaire");
        Field solde = attribut("solde");

        if (proprietaire == null) {
            manquant("Attribut proprietaire");
        } else {
            verifier(
                "proprietaire est de type String",
                proprietaire.getType() == String.class
            );
        }

        if (solde == null) {
            manquant("Attribut solde");
        } else {
            verifier(
                "solde est de type int",
                solde.getType() == int.class
            );
        }

        Object compte = creer("Alice", 100);

        if (compte != null) {
            if (proprietaire != null) {
                verifier(
                    "Le constructeur initialise proprietaire",
                    "Alice".equals(
                        lire(compte, "proprietaire")
                    )
                );
            }

            if (solde != null) {
                verifier(
                    "Le constructeur initialise solde",
                    Integer.valueOf(100).equals(
                        lire(compte, "solde")
                    )
                );
            }
        }

        if (methode("afficher") == null) {
            manquant("Methode afficher()");
        } else {
            verifier("Methode afficher() presente", true);
        }
    }

    // ========================================
    // QUESTION 2 : DEPOT ET RETRAIT
    // ========================================

    private static void question2() {
        section("Question 2 - Depot et retrait");

        if (methode("deposer", int.class) == null) {
            manquant("Methode deposer(int)");
        }

        if (methode("retirer", int.class) == null) {
            manquant("Methode retirer(int)");
        }

        if (attribut("solde") == null) {
            return;
        }

        Object compte = creer("Alice", 100);

        if (compte == null) {
            return;
        }

        if (methode("deposer", int.class) != null) {
            if (appeler(compte, "deposer", 30)) {
                verifier(
                    "deposer(30) augmente le solde",
                    Integer.valueOf(130).equals(
                        lire(compte, "solde")
                    )
                );
            }
        }

        if (methode("retirer", int.class) != null) {
            if (appeler(compte, "retirer", 20)) {
                verifier(
                    "retirer(20) diminue le solde",
                    Integer.valueOf(110).equals(
                        lire(compte, "solde")
                    )
                );
            }
        }
    }

    
private static void verifierMethodePublique(
        String nom, Class<?>... types) {

    Method m = methode(nom, types);

    if (m == null) {
        manquant("Methode " + nom + " absente");
        return;
    }

    verifier(
        "La methode " + nom + " est publique",
        Modifier.isPublic(m.getModifiers())
    );
}

// ========================================
// QUESTION 4 : ENCAPSULATION
// ========================================

private static void question4() {
    section("Question 4 - Encapsulation");

    // 1. Visibilite des attributs

    Field proprietaire = attribut("proprietaire");
    Field solde = attribut("solde");

    if (proprietaire == null) {
        manquant("Attribut proprietaire");
    } else {
        verifier(
            "L'attribut proprietaire est prive",
            Modifier.isPrivate(
                proprietaire.getModifiers()
            )
        );
    }

    if (solde == null) {
        manquant("Attribut solde");
    } else {
        verifier(
            "L'attribut solde est prive",
            Modifier.isPrivate(
                solde.getModifiers()
            )
        );
    }

    // 2. Visibilite des methodes

    verifierMethodePublique("afficher");
    verifierMethodePublique("deposer", int.class);
    verifierMethodePublique("retirer", int.class);
    verifierMethodePublique("getProprietaire");
    verifierMethodePublique("getSolde");

    // 3. Respect des invariants

    if (solde == null) {
        return;
    }

    Object compte = creer("Alice", 100);

    if (compte == null) {
        return;
    }

    if (methode("deposer", int.class) != null) {

        if (appeler(compte, "deposer", -10)) {
            verifier(
                "Depot negatif refuse",
                Integer.valueOf(100).equals(
                    lire(compte, "solde")
                )
            );
        }

        if (appeler(compte, "deposer", 0)) {
            verifier(
                "Depot nul refuse",
                Integer.valueOf(100).equals(
                    lire(compte, "solde")
                )
            );
        }
    }

    if (methode("retirer", int.class) != null) {

        if (appeler(compte, "retirer", 500)) {
            verifier(
                "Retrait excessif refuse",
                Integer.valueOf(100).equals(
                    lire(compte, "solde")
                )
            );
        }

        if (appeler(compte, "retirer", -5)) {
            verifier(
                "Retrait negatif refuse",
                Integer.valueOf(100).equals(
                    lire(compte, "solde")
                )
            );
        }

        if (appeler(compte, "retirer", 0)) {
            verifier(
                "Retrait nul refuse",
                Integer.valueOf(100).equals(
                    lire(compte, "solde")
                )
            );
        }
    }
}


    // ========================================
    // MAIN
    // ========================================

    public static void main(String[] args) {

        System.out.println(
            "TP04 - EXERCICE 1 - TEST COMPTE"
        );

        try {
            classe = Class.forName("Compte");

        } catch (ClassNotFoundException e) {
            manquant("Classe Compte");
            bilan();
            return;
        }

        question1();
        question2();
        question4();

        bilan();
    }

    private static void bilan() {
        section("Bilan");

        System.out.println("Reussis : " + reussis);
        System.out.println("Echecs : " + echecs);
        System.out.println("Non implementes : " + manquants);
    }
}
