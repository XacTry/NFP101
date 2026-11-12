public class Main {

    public static void main(String[] args) {

        // Question 1 : compte classique
        System.out.println("=== Question 1 ===");
        Compte alice = new Compte("Alice", 100);
        System.out.println(alice.description());

        System.out.println("deposer(20)   -> " + alice.deposer(20) + " | " + alice.description());
        System.out.println("retirer(30)   -> " + alice.retirer(30) + " | " + alice.description());
        System.out.println("retirer(500)  -> " + alice.retirer(500) + " | " + alice.description());
        System.out.println("deposer(-10)  -> " + alice.deposer(-10) + " | " + alice.description());
        System.out.println("retirer(0)    -> " + alice.retirer(0) + " | " + alice.description());

        Compte bob = new Compte("Bob", -5);
        System.out.println("Solde initial negatif : " + bob.description());

        Compte plein = new Compte("Charlie", Integer.MAX_VALUE);
        System.out.println("deposer(1) sur MAX_VALUE -> " + plein.deposer(1));

        // Question 2 : compte avec plafond de retrait
        System.out.println("\n=== Question 2 ===");
        CompteLimite limite = new CompteLimite("Alice", 100, 30);
        System.out.println("Titulaire : " + limite.getTitulaire());
        System.out.println("Solde     : " + limite.getSolde());
        System.out.println("Plafond   : " + limite.getPlafondRetrait());

        // Question 3 : redéfinitions
        System.out.println("\n=== Question 3 ===");
        System.out.println(limite.description());
        System.out.println("retirer(20)  -> " + limite.retirer(20) + " | " + limite.description());
        System.out.println("retirer(30)  -> " + limite.retirer(30) + " | " + limite.description());
        System.out.println("retirer(40)  -> " + limite.retirer(40) + " | " + limite.description());
        System.out.println("deposer(25)  -> " + limite.deposer(25) + " | " + limite.description());

        // Polymorphisme : la méthode appelée dépend du type réel de l'objet
        Compte vuCommeCompte = limite;
        System.out.println("retirer(40) via une variable Compte -> " + vuCommeCompte.retirer(40));
    }
}
