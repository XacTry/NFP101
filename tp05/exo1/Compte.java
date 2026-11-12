public class Compte {

    // Question 1 : titulaire privé, solde protégé (accessible aux classes filles)
    private String titulaire;
    protected int solde;

    // Question 1 : un solde initial négatif est ramené à 0
    public Compte(String titulaire, int soldeInitial) {
        this.titulaire = titulaire;
        if (soldeInitial < 0) {
            soldeInitial = 0;
        }
        this.solde = soldeInitial;
    }

    // Question 1 : accesseurs en lecture seule
    public String getTitulaire() {
        return titulaire;
    }

    public int getSolde() {
        return solde;
    }

    // Question 1 : montant strictement positif et sans dépasser Integer.MAX_VALUE
    public boolean deposer(int montant) {
        if (montant <= 0) {
            return false;
        }
        if (montant > Integer.MAX_VALUE - solde) {
            return false;
        }
        solde += montant;
        return true;
    }

    // Question 1 : montant strictement positif et ne dépassant pas le solde
    public boolean retirer(int montant) {
        if (montant <= 0) {
            return false;
        }
        if (montant > solde) {
            return false;
        }
        solde -= montant;
        return true;
    }

    // Question 1
    public String description() {
        return "Compte " + titulaire + " : " + solde + " credits";
    }
}
