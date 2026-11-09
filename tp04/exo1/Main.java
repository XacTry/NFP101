public class Main {

    public static void main(String[] args) {

        // Question 1 : création et affichage de deux comptes
        System.out.println("=== Question 1 ===");
        Compte alice = new Compte("Alice", 100);
        Compte bob = new Compte("Bob", 50);
        alice.afficher();
        bob.afficher();

        // Question 2 : dépôt et retrait
        System.out.println("\n=== Question 2 ===");
        alice.deposer(30);
        bob.retirer(20);
        alice.afficher();
        bob.afficher();

        // Question 3 : références
        System.out.println("\n=== Question 3 ===");
        Compte autreAlice = alice;          // même objet, deux variables
        autreAlice.deposer(10);
        System.out.println("Solde via alice      : " + alice.getSolde());
        System.out.println("Solde via autreAlice : " + autreAlice.getSolde());
        System.out.println("alice == autreAlice  : " + (alice == autreAlice));

        Compte copieAlice = new Compte("Alice", alice.getSolde()); // nouvel objet
        System.out.println("alice == copieAlice  : " + (alice == copieAlice));

        // Question 4 : encapsulation et opérations refusées
        System.out.println("\n=== Question 4 ===");
        Compte test = new Compte("Alice", 100);
        test.afficher();

        test.deposer(20);
        System.out.print("deposer(20)  -> ");
        test.afficher();

        test.retirer(30);
        System.out.print("retirer(30)  -> ");
        test.afficher();

        test.retirer(500);
        System.out.print("retirer(500) -> ");
        test.afficher();

        test.deposer(-10);
        System.out.print("deposer(-10) -> ");
        test.afficher();

        test.retirer(-5);
        System.out.print("retirer(-5)  -> ");
        test.afficher();

        // Constructeur avec données invalides
        try {
            new Compte("", 10);
        } catch (IllegalArgumentException e) {
            System.out.println("Creation refusee : " + e.getMessage());
        }
        try {
            new Compte("Charlie", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("Creation refusee : " + e.getMessage());
        }
    }
}
