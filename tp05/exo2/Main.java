public class Main {

    public static void main(String[] args) {

        // Question 1 / 3 : cas normal
        System.out.println("=== Alice ===");
        CompteFidelite compte = new CompteFidelite("Alice", 100);
        compte.deposer(50);
        System.out.println(compte.getSolde());
        System.out.println(compte.description());

        // Question 1 / 3 : cas limite proche de Integer.MAX_VALUE
        System.out.println("\n=== Bob ===");
        CompteFidelite compteBob = new CompteFidelite("Bob", Integer.MAX_VALUE - 100);
        compteBob.deposer(100);
        System.out.println(compteBob.getSolde());
        System.out.println(compteBob.description());

        // Question 3 : un dépôt refusé ne donne droit à aucune prime
        System.out.println("\n=== Depot refuse ===");
        System.out.println("deposer(1) sur un compte plein -> " + compteBob.deposer(1)
            + " | " + compteBob.getSolde());

        // Question 3 : CompteLimite fonctionne toujours avec solde en private
        System.out.println("\n=== CompteLimite ===");
        CompteLimite limite = new CompteLimite("Alice", 100, 30);
        System.out.println("retirer(30) -> " + limite.retirer(30) + " | " + limite.description());
        System.out.println("retirer(40) -> " + limite.retirer(40) + " | " + limite.description());
        System.out.println("deposer(25) -> " + limite.deposer(25) + " | " + limite.description());
    }
}
