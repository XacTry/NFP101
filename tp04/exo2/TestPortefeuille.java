
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestPortefeuille {

    private static Class<?> classePortefeuille;
    private static Class<?> classeCompte;

    private static int reussis = 0;
    private static int echecs = 0;
    private static int manquants = 0;

    // ========================================
    // OUTILS
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

    private static Method methode(
            String nom, Class<?>... types) {

        try {
            Method m = classePortefeuille.getDeclaredMethod(
                nom, types
            );
            m.setAccessible(true);
            return m;
        } catch (Exception e) {
            return null;
        }
    }

    private static Field attributComptes() {
        try {
            Field f = classePortefeuille.getDeclaredField(
                "comptes"
            );
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            return null;
        }
    }

    private static Object creerPortefeuille() {
        try {
            Constructor<?> c =
                classePortefeuille.getDeclaredConstructor();

            c.setAccessible(true);
            return c.newInstance();

        } catch (NoSuchMethodException e) {
            manquant("Constructeur Portefeuille()");
        } catch (Exception e) {
            verifier("Creation du portefeuille", false);
        }

        return null;
    }

    private static Object creerCompte(
            String nom, int solde) {

        try {
            Constructor<?> c =
                classeCompte.getDeclaredConstructor(
                    String.class, int.class
                );

            c.setAccessible(true);
            return c.newInstance(nom, solde);

        } catch (Exception e) {
            manquant(
                "Construction d'un Compte(String, int)"
            );
            return null;
        }
    }

    private static Object tableauInterne(
            Object portefeuille) {

        Field f = attributComptes();

        if (f == null || portefeuille == null) {
            return null;
        }

        try {
            return f.get(portefeuille);
        } catch (Exception e) {
            return null;
        }
    }

    private static Object[] contenu(Object tableau) {
        if (tableau == null
                || !tableau.getClass().isArray()) {
            return null;
        }

        int taille = Array.getLength(tableau);
        Object[] resultat = new Object[taille];

        for (int i = 0; i < taille; i++) {
            resultat[i] = Array.get(tableau, i);
        }

        return resultat;
    }

    private static int compter(Object tableau) {
        Object[] elements = contenu(tableau);

        if (elements == null) {
            return -1;
        }

        int nombre = 0;

        for (Object element : elements) {
            if (element != null) {
                nombre++;
            }
        }

        return nombre;
    }

    private static boolean contient(
            Object tableau, Object compte) {

        Object[] elements = contenu(tableau);

        if (elements == null) {
            return false;
        }

        for (Object element : elements) {
            if (element == compte) {
                return true;
            }
        }

        return false;
    }

    private static boolean memeContenu(
            Object[] avant, Object[] apres) {

        if (avant == null || apres == null
                || avant.length != apres.length) {
            return false;
        }

        for (int i = 0; i < avant.length; i++) {
            if (avant[i] != apres[i]) {
                return false;
            }
        }

        return true;
    }

    private static boolean ajouter(
            Object portefeuille, Object compte) {

        Method m = methode(
            "ajouterCompte", classeCompte
        );

        if (m == null) {
            return false;
        }

        try {
            m.invoke(portefeuille, compte);
            return true;
        } catch (Exception e) {
            verifier("Execution de ajouterCompte()", false);
            return false;
        }
    }

    private static boolean transferer(
            Object portefeuille,
            Object source,
            Object destination,
            int montant) {

        Method m = methode(
            "transferer",
            classeCompte,
            classeCompte,
            int.class
        );

        if (m == null) {
            return false;
        }

        try {
            m.invoke(
                portefeuille,
                source,
                destination,
                montant
            );
            return true;
        } catch (Exception e) {
            verifier("Execution de transferer()", false);
            return false;
        }
    }

    private static Integer solde(Object compte) {
        if (compte == null) {
            return null;
        }

        try {
            Method m =
                classeCompte.getDeclaredMethod("getSolde");

            m.setAccessible(true);

            Object resultat = m.invoke(compte);

            if (resultat instanceof Integer) {
                return (Integer) resultat;
            }

        } catch (Exception e) {
            // Getter indisponible.
        }

        return null;
    }

    private static boolean deposer(
            Object compte, int montant) {

        try {
            Method m = classeCompte.getDeclaredMethod(
                "deposer", int.class
            );

            m.setAccessible(true);
            m.invoke(compte, montant);
            return true;

        } catch (Exception e) {
            verifier("Execution de Compte.deposer()", false);
            return false;
        }
    }

    private static Object obtenirComptes(
            Object portefeuille) {

        Method m = methode("getComptes");

        if (m == null) {
            return null;
        }

        try {
            return m.invoke(portefeuille);
        } catch (Exception e) {
            verifier("Execution de getComptes()", false);
            return null;
        }
    }

    // ========================================
    // QUESTION 1 : TABLEAU ET COMPOSITION
    // ========================================

    private static void question1() {
        section("Question 1 - Tableau et composition");

        Field f = attributComptes();

        if (f == null) {
            manquant("Attribut comptes");
        } else {
            verifier(
                "L'attribut comptes est de type Compte[]",
                f.getType().isArray()
                    && f.getType().getComponentType()
                        == classeCompte
            );

            verifier(
                "L'attribut comptes est private",
                Modifier.isPrivate(f.getModifiers())
            );
        }

        Object portefeuille = creerPortefeuille();

        if (portefeuille == null) {
            return;
        }

        Object tableau = tableauInterne(portefeuille);

        verifier(
            "Le constructeur initialise un tableau",
            tableau != null
                && tableau.getClass().isArray()
        );

        if (tableau == null
                || !tableau.getClass().isArray()) {
            return;
        }

        verifier(
            "Le tableau contient 10 emplacements",
            Array.getLength(tableau) == 10
        );

        verifier(
            "Le portefeuille est initialement vide",
            compter(tableau) == 0
        );

        Method ajout = methode(
            "ajouterCompte", classeCompte
        );

        if (ajout == null) {
            manquant("Methode ajouterCompte(Compte)");
        } else {
            verifier(
                "ajouterCompte() est public",
                Modifier.isPublic(ajout.getModifiers())
            );

            Object alice = creerCompte("Alice", 100);
            Object bob = creerCompte("Bob", 50);

            if (alice != null && bob != null
                    && ajouter(portefeuille, alice)
                    && ajouter(portefeuille, bob)) {

                Object apres = tableauInterne(portefeuille);

                verifier(
                    "Alice est ajoutee au portefeuille",
                    contient(apres, alice)
                );

                verifier(
                    "Bob est ajoute au portefeuille",
                    contient(apres, bob)
                );

                verifier(
                    "Les deux comptes sont conserves",
                    compter(apres) == 2
                );

                if (Array.getLength(apres) >= 2) {
                    verifier(
                        "Les premiers emplacements sont utilises",
                        Array.get(apres, 0) == alice
                            && Array.get(apres, 1) == bob
                    );
                }
            }
        }

        Method afficher = methode("afficher");

        if (afficher == null) {
            manquant("Methode afficher()");
        } else {
            verifier(
                "afficher() est public",
                Modifier.isPublic(afficher.getModifiers())
            );

            PrintStream ancienneSortie = System.out;
            ByteArrayOutputStream capture =
                new ByteArrayOutputStream();

            boolean execution = true;

            try {
                System.setOut(new PrintStream(capture));
                afficher.invoke(portefeuille);
            } catch (Exception e) {
                execution = false;
            } finally {
                System.setOut(ancienneSortie);
            }

            verifier(
                "afficher() s'execute sans erreur",
                execution
            );

            if (execution && compter(
                    tableauInterne(portefeuille)) > 0) {

                verifier(
                    "afficher() produit un affichage",
                    !capture.toString().trim().isEmpty()
                );
            }
        }
    }

    // ========================================
    // QUESTION 2 : TRANSFERTS
    // ========================================

    private static void question2() {
        section("Question 2 - Transferts");

        Method transfert = methode(
            "transferer",
            classeCompte,
            classeCompte,
            int.class
        );

        if (transfert == null) {
            manquant(
                "Methode transferer(Compte, Compte, int)"
            );
            return;
        }

        verifier(
            "transferer() est public",
            Modifier.isPublic(transfert.getModifiers())
        );

        if (methode("ajouterCompte", classeCompte) == null) {
            manquant("Methode ajouterCompte(Compte)");
            return;
        }

        Object portefeuille = creerPortefeuille();
        Object alice = creerCompte("Alice", 100);
        Object bob = creerCompte("Bob", 50);
        Object charlie = creerCompte("Charlie", 200);

        if (portefeuille == null
                || alice == null
                || bob == null
                || charlie == null) {
            return;
        }

        if (!ajouter(portefeuille, alice)
                || !ajouter(portefeuille, bob)
                || !ajouter(portefeuille, charlie)) {
            return;
        }

        if (solde(alice) == null
                || solde(bob) == null
                || solde(charlie) == null) {
            manquant("Compte.getSolde()");
            return;
        }

        // Alice transfere 30 a Bob.

        if (transferer(portefeuille, alice, bob, 30)) {
            verifier(
                "Alice possede 70 credits",
                Integer.valueOf(70).equals(solde(alice))
            );

            verifier(
                "Bob possede 80 credits",
                Integer.valueOf(80).equals(solde(bob))
            );

            verifier(
                "La somme reste egale a 350",
                somme(alice, bob, charlie) == 350
            );
        }

        // Charlie transfere 50 a Alice.

        if (transferer(
                portefeuille, charlie, alice, 50)) {

            verifier(
                "Charlie possede 150 credits",
                Integer.valueOf(150).equals(
                    solde(charlie)
                )
            );

            verifier(
                "Alice possede 120 credits",
                Integer.valueOf(120).equals(solde(alice))
            );

            verifier(
                "La somme reste egale a 350",
                somme(alice, bob, charlie) == 350
            );
        }

        // Transferts refuses.

        verifierRefus(
            portefeuille, bob, charlie, 500,
            "Transfert excessif refuse"
        );

        verifierRefus(
            portefeuille, alice, bob, 0,
            "Montant nul refuse"
        );

        verifierRefus(
            portefeuille, alice, bob, -10,
            "Montant negatif refuse"
        );

        verifierRefus(
            portefeuille, alice, alice, 10,
            "Transfert vers soi-meme refuse"
        );

        Object absent = creerCompte("David", 40);

        if (absent != null) {
            verifierRefus(
                portefeuille, alice, absent, 10,
                "Destinataire absent refuse"
            );

            verifierRefus(
                portefeuille, absent, bob, 10,
                "Source absente refusee"
            );
        }
    }

    private static int somme(Object... comptes) {
        int total = 0;

        for (Object compte : comptes) {
            Integer valeur = solde(compte);

            if (valeur == null) {
                return Integer.MIN_VALUE;
            }

            total += valeur;
        }

        return total;
    }

    private static void verifierRefus(
            Object portefeuille,
            Object source,
            Object destination,
            int montant,
            String description) {

        Integer avantSource = solde(source);
        Integer avantDestination = solde(destination);

        if (avantSource == null
                || avantDestination == null) {
            return;
        }

        if (transferer(
                portefeuille, source, destination, montant)) {

            verifier(
                description,
                avantSource.equals(solde(source))
                    && avantDestination.equals(
                        solde(destination)
                    )
            );
        }
    }

    // ========================================
    // QUESTION 4 : FUITE DE REFERENCE
    // ========================================

    private static void question4() {
        section(
            "Question 4 - Fuite de reference "
            + "et copie defensive"
        );

        Method getter = methode("getComptes");

        if (getter == null) {
            manquant("Methode getComptes()");
            return;
        }

        verifier(
            "getComptes() est public",
            Modifier.isPublic(getter.getModifiers())
        );

        verifier(
            "getComptes() retourne Compte[]",
            getter.getReturnType().isArray()
                && getter.getReturnType()
                    .getComponentType() == classeCompte
        );

        if (methode("ajouterCompte", classeCompte) == null) {
            manquant("Methode ajouterCompte(Compte)");
            return;
        }

        Object portefeuille = creerPortefeuille();
        Object alice = creerCompte("Alice", 100);
        Object bob = creerCompte("Bob", 50);

        if (portefeuille == null
                || alice == null
                || bob == null) {
            return;
        }

        if (!ajouter(portefeuille, alice)
                || !ajouter(portefeuille, bob)) {
            return;
        }

        Object interne = tableauInterne(portefeuille);
        Object externe = obtenirComptes(portefeuille);

        if (interne == null || externe == null
                || !interne.getClass().isArray()
                || !externe.getClass().isArray()) {

            verifier(
                "getComptes() retourne un tableau",
                false
            );
            return;
        }

        // 1. Absence de fuite de reference

        verifier(
            "Le tableau retourne est different "
                + "du tableau interne",
            interne != externe
        );

        // 2. Contenu de la copie

        verifier(
            "La copie conserve Alice",
            contient(externe, alice)
        );

        verifier(
            "La copie conserve Bob",
            contient(externe, bob)
        );

        verifier(
            "La copie possede la meme longueur",
            Array.getLength(externe)
                == Array.getLength(interne)
        );

        // 3. Protection contre une modification externe

        Object[] avant = contenu(interne);

        if (Array.getLength(externe) > 0) {
            try {
                Array.set(externe, 0, null);
            } catch (Exception e) {
                verifier(
                    "Modification du tableau retourne",
                    false
                );
            }
        }

        Object[] apres = contenu(
            tableauInterne(portefeuille)
        );

        verifier(
            "Modifier la copie ne modifie pas "
                + "le tableau interne",
            memeContenu(avant, apres)
        );

        // 4. Protection contre le vidage du tableau

        Object nouvelleCopie = obtenirComptes(portefeuille);

        if (nouvelleCopie != null
                && nouvelleCopie.getClass().isArray()) {

            Object[] avantVidage = contenu(
                tableauInterne(portefeuille)
            );

            for (int i = 0;
                    i < Array.getLength(nouvelleCopie);
                    i++) {
                Array.set(nouvelleCopie, i, null);
            }

            verifier(
                "Vider la copie ne vide pas "
                    + "le portefeuille",
                memeContenu(
                    avantVidage,
                    contenu(tableauInterne(portefeuille))
                )
            );
        }

        // 5. Les objets restent partages

        Object copiePartagee =
            obtenirComptes(portefeuille);

        if (copiePartagee == null
                || !copiePartagee.getClass().isArray()) {
            return;
        }

        Object referenceAlice = null;

        for (Object element : contenu(copiePartagee)) {
            if (element == alice) {
                referenceAlice = element;
                break;
            }
        }

        verifier(
            "La copie contient la meme reference "
                + "vers Alice",
            referenceAlice == alice
        );

        Integer soldeAvant = solde(alice);

        if (referenceAlice != null
                && soldeAvant != null
                && deposer(referenceAlice, 20)) {

            verifier(
                "Modifier Alice depuis la copie "
                    + "modifie le compte partage",
                Integer.valueOf(soldeAvant + 20)
                    .equals(solde(alice))
            );

            verifier(
                "Alice reste presente "
                    + "dans le portefeuille",
                contient(
                    tableauInterne(portefeuille),
                    alice
                )
            );
        }
    }

    // ========================================
    // BILAN
    // ========================================

    private static void bilan() {
        section("Bilan");

        System.out.println("Tests reussis : " + reussis);
        System.out.println("Tests echoues : " + echecs);
        System.out.println("Non implementes : " + manquants);
    }

    // ========================================
    // MAIN
    // ========================================

    public static void main(String[] args) {

        System.out.println(
            "TP04 - EXERCICE 2 - TEST PORTEFEUILLE"
        );

        try {
            classePortefeuille =
                Class.forName("Portefeuille");
        } catch (ClassNotFoundException e) {
            manquant("Classe Portefeuille");
            bilan();
            return;
        }

        try {
            classeCompte = Class.forName("Compte");
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
}
