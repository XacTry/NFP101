public class CompteFidelite extends Compte {

    public CompteFidelite(String titulaire, int soldeInitial) {
        super(titulaire, soldeInitial);
    }

    // Question 2 : le dépôt et la prime passent tous les deux par Compte.deposer(),
    // donc par ses vérifications (montant positif, pas de dépassement de MAX_VALUE).
    // super.deposer(prime) et non deposer(prime) : sinon on toucherait une prime sur la prime.
    @Override
    public boolean deposer(int montant) {
        if (!super.deposer(montant)) {
            return false;               // dépôt refusé : aucune prime
        }

        int prime = montant / 10;
        super.deposer(prime);           // si la prime est refusée, le dépôt reste accepté

        return true;
    }

    // Question 3
    @Override
    public String description() {
        return super.description() + " | compte fidelite";
    }
}
