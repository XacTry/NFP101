public class Main {

    private static void afficherTitre(String titre) {
        System.out.println("\n--- " + titre + " ---");
    }

    private static int somme(Compte... comptes) {
        int total = 0;
        for (Compte c : comptes) {
            total += c.getSolde();
        }
        return total;
    }

    public static void main(String[] args) {

        // ========================================
        // QUESTION 1 - CREER UN PORTEFEUILLE
        // ========================================
        System.out.println("=== Question 1 ===");

        Compte alice = new Compte("Alice", 100);
        Compte bob = new Compte("Bob", 50);
        Compte charlie = new Compte("Charlie", 200);

        Portefeuille portefeuille = new Portefeuille();
        portefeuille.ajouterCompte(alice);
        portefeuille.ajouterCompte(bob);
        portefeuille.ajouterCompte(charlie);

        portefeuille.afficher();

        // ========================================
        // QUESTION 2 - TRANSFERER DES CREDITS
        // ========================================
        System.out.println("\n=== Question 2 ===");
        int sommeInitiale = somme(alice, bob, charlie);
        System.out.println("Somme initiale : " + sommeInitiale);

        afficherTitre("Alice -> Bob : 30");
        portefeuille.transferer(alice, bob, 30);
        portefeuille.afficher();
        System.out.println("Somme : " + somme(alice, bob, charlie));

        afficherTitre("Charlie -> Alice : 50");
        portefeuille.transferer(charlie, alice, 50);
        portefeuille.afficher();
        System.out.println("Somme : " + somme(alice, bob, charlie));

        afficherTitre("Bob -> Charlie : 500");
        portefeuille.transferer(bob, charlie, 500);
        portefeuille.afficher();
        System.out.println("Somme : " + somme(alice, bob, charlie));

        System.out.println("Somme conservee : "
                + (somme(alice, bob, charlie) == sommeInitiale));
        // Un transfert retire puis dépose le même montant :
        // ce qui sort d'un compte entre dans l'autre, la somme ne change pas.

        // ========================================
        // QUESTION 3 - REFERENCES ET COMPOSITION
        // ========================================
        System.out.println("\n=== Question 3 ===");

        Compte refAlice = portefeuille.getCompte(0);
        System.out.println("refAlice == alice : " + (refAlice == alice));
        refAlice.deposer(10);

        afficherTitre("Portefeuille 1 apres depot de 10 via refAlice");
        portefeuille.afficher(); // Alice : 130 -> la modification est visible

        Portefeuille portefeuille2 = new Portefeuille();
        portefeuille2.ajouterCompte(alice);
        portefeuille2.getCompte(0).deposer(5);

        afficherTitre("Portefeuille 2 apres depot de 5");
        portefeuille2.afficher();
        afficherTitre("Portefeuille 1");
        portefeuille.afficher(); // Alice : 135 aussi

        // Explication : les portefeuilles ne stockent pas des copies des comptes
        // mais des références. Les deux tableaux pointent vers le MÊME objet
        // Compte en mémoire : une modification faite via l'un est donc vue
        // par tous ceux qui possèdent une référence vers cet objet.

        // ========================================
        // QUESTION 4 - FUITE DE REFERENCE
        // ========================================
        System.out.println("\n=== Question 4 ===");

        // ---- 4.1 Observer la fuite de référence ----
        Portefeuille p = new Portefeuille();
        p.ajouterCompte(new Compte("Alice", 100));
        p.ajouterCompte(new Compte("Bob", 50));
        p.ajouterCompte(new Compte("Charlie", 200));

        afficherTitre("4.1 Tableau interne retourne directement");
        Compte[] tableau = p.getComptesSansProtection();

        tableau[1] = null;                        // suppression de Bob
        System.out.println("Apres tableau[1] = null :");
        p.afficher();

        tableau[5] = new Compte("Intrus", 999);   // ajout sans ajouterCompte()
        System.out.println("Apres ajout d'un compte en position 5 :");
        p.afficher();

        for (int i = 0; i < tableau.length; i++) { // vidage
            tableau[i] = null;
        }
        System.out.println("Apres vidage du tableau :");
        p.afficher();

        // Explication : private interdit d'écrire p.comptes depuis l'extérieur,
        // mais getComptesSansProtection() renvoie la référence vers ce tableau.
        // L'appelant manipule donc le tableau interne lui-même : il peut
        // supprimer, ajouter (sans les contrôles d'ajouterCompte, ex. doublons)
        // ou tout vider. private protège le nom de l'attribut, pas l'objet
        // vers lequel il pointe une fois sa référence partagée.

        // ---- 4.2 Copie défensive ----
        afficherTitre("4.2 Copie defensive avec clone()");
        Compte a = new Compte("Alice", 100);
        Portefeuille p2 = new Portefeuille();
        p2.ajouterCompte(a);
        p2.ajouterCompte(new Compte("Bob", 50));
        p2.ajouterCompte(new Compte("Charlie", 200));

        Compte[] copie = p2.getComptes();
        System.out.println("copie == tableau interne ? "
                + (copie == p2.getComptesSansProtection()));

        copie[1] = null;
        copie[5] = new Compte("Intrus", 999);
        for (int i = 0; i < copie.length; i++) {
            copie[i] = null;
        }
        System.out.println("Portefeuille apres les memes manipulations sur la copie :");
        p2.afficher(); // inchangé

        // Explication : clone() crée un nouveau tableau. Les manipulations
        // ne touchent que ce nouveau tableau : la structure interne du
        // portefeuille (quels comptes, à quelle place) ne peut plus être
        // modifiée qu'à travers ses propres méthodes.

        // ---- 4.3 Limites de la copie défensive ----
        afficherTitre("4.3 Limites : copie superficielle");
        Compte[] copie2 = p2.getComptes();
        Compte aliceDepuisCopie = copie2[0];
        System.out.println("copie2[0] == a ? " + (aliceDepuisCopie == a));
        aliceDepuisCopie.deposer(50);
        p2.afficher(); // Alice : 150 -> la modification est visible

        // Explication : clone() copie les cases du tableau, c'est-à-dire les
        // références, pas les objets Compte. La copie et le tableau interne
        // pointent vers les mêmes comptes.
        //  - Fuite du tableau (4.1) : l'extérieur peut changer la STRUCTURE
        //    du portefeuille (retirer, remplacer, ajouter des comptes).
        //  - Partage des objets (4.3) : l'extérieur peut changer l'ÉTAT d'un
        //    compte, mais seulement via ses méthodes publiques (deposer,
        //    retirer), donc en respectant les règles de Compte (pas de solde
        //    négatif, montants positifs...).
        // La copie superficielle protège la structure du tableau ; elle ne
        // protège pas l'état des objets contenus. Pour cela il faudrait une
        // copie profonde (copier aussi chaque Compte) ou des objets immuables.
    }
}
