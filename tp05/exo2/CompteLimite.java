public class CompteLimite extends Compte {

    // Question 2 : seul attribut propre à CompteLimite
    private int plafondRetrait;

    // Question 2 : titulaire et solde sont initialisés par Compte via super(...)
    // Un plafond négatif est ramené à 0
    public CompteLimite(String titulaire, int soldeInitial, int plafondRetrait) {
        super(titulaire, soldeInitial);
        if (plafondRetrait < 0) {
            plafondRetrait = 0;
        }
        this.plafondRetrait = plafondRetrait;
    }

    // Question 2
    public int getPlafondRetrait() {
        return plafondRetrait;
    }

    // Question 3 : on refuse au-delà du plafond, sinon on délègue à Compte
    @Override
    public boolean retirer(int montant) {
        if (montant > plafondRetrait) {
            return false;
        }
        return super.retirer(montant);
    }

    // Question 3
    @Override
    public String description() {
        return super.description() + " | plafond retrait : " + plafondRetrait;
    }
}
