public class Compte {

    // Question 4 : attributs privés, inaccessibles depuis l'extérieur
    private String proprietaire;
    private int solde;

    // Question 1 : constructeur
    // Question 4 : refuse un propriétaire vide ou un solde négatif
    public Compte(String proprietaire, int solde) {
        if (proprietaire == null || proprietaire.trim().isEmpty()) {
            throw new IllegalArgumentException("Le proprietaire ne doit pas etre vide");
        }
        if (solde < 0) {
            throw new IllegalArgumentException("Le solde initial doit etre positif ou nul");
        }
        this.proprietaire = proprietaire;
        this.solde = solde;
    }

    // Question 4 : accesseurs en lecture seule
    public String getProprietaire() {
        return proprietaire;
    }

    public int getSolde() {
        return solde;
    }

    // Question 1
    public void afficher() {
        System.out.println(proprietaire + " : " + solde + " credits");
    }

    // Question 2 + 4 : le montant doit être strictement positif
    public void deposer(int montant) {
        if (montant <= 0) {
            System.out.println("Depot refuse : montant invalide (" + montant + ")");
            return;
        }
        solde += montant;
    }

    // Question 2 + 4 : montant strictement positif et ne dépassant pas le solde
    public void retirer(int montant) {
        if (montant <= 0) {
            System.out.println("Retrait refuse : montant invalide (" + montant + ")");
            return;
        }
        if (montant > solde) {
            System.out.println("Retrait refuse : solde insuffisant (" + montant + " > " + solde + ")");
            return;
        }
        solde -= montant;
    }
}
