public class Portefeuille {

    private static final int CAPACITE = 10;

    // Question 1 : tableau de références vers des objets Compte
    private Compte[] comptes;

    public Portefeuille() {
        comptes = new Compte[CAPACITE]; // 10 emplacements, tous à null
    }

    // Question 1 : ajoute le compte dans le premier emplacement libre
    public void ajouterCompte(Compte compte) {
        if (compte == null) {
            System.out.println("Ajout refuse : compte null");
            return;
        }
        if (contient(compte)) {
            System.out.println("Ajout refuse : compte deja present");
            return;
        }
        for (int i = 0; i < comptes.length; i++) {
            if (comptes[i] == null) {
                comptes[i] = compte;
                return;
            }
        }
        System.out.println("Ajout refuse : portefeuille plein");
    }

    // Question 1
    public void afficher() {
        boolean vide = true;
        for (Compte compte : comptes) {
            if (compte != null) {
                compte.afficher();
                vide = false;
            }
        }
        if (vide) {
            System.out.println("(portefeuille vide)");
        }
    }

    // Vrai si cette référence exacte est dans le portefeuille (comparaison avec ==)
    private boolean contient(Compte compte) {
        for (Compte c : comptes) {
            if (c == compte) {
                return true;
            }
        }
        return false;
    }

    // Question 2 : toutes les règles sont vérifiées AVANT de toucher aux comptes,
    // donc un transfert refusé ne modifie rien.
    public void transferer(Compte source, Compte destination, int montant) {
        if (source == null || destination == null
                || !contient(source) || !contient(destination)) {
            System.out.println("Transfert refuse : compte hors du portefeuille");
            return;
        }
        if (source == destination) {
            System.out.println("Transfert refuse : source et destination identiques");
            return;
        }
        if (montant <= 0) {
            System.out.println("Transfert refuse : montant invalide (" + montant + ")");
            return;
        }
        if (source.getSolde() < montant) {
            System.out.println("Transfert refuse : solde insuffisant ("
                    + source.getProprietaire() + " a " + source.getSolde() + ")");
            return;
        }
        // Le portefeuille passe par les méthodes publiques de Compte :
        // il respecte l'encapsulation de chaque compte.
        source.retirer(montant);
        destination.deposer(montant);
    }

    // Question 3 : retourne la référence vers le compte (pas une copie)
    public Compte getCompte(int index) {
        if (index < 0 || index >= comptes.length) {
            return null;
        }
        return comptes[index];
    }

    // Question 4.2 : copie défensive (copie superficielle).
    // Le tableau retourné est un nouveau tableau, mais il contient
    // les mêmes références vers les objets Compte.
    public Compte[] getComptes() {
        return comptes.clone();
    }

    // Question 4.1 : version AVEC fuite de référence, gardée uniquement
    // pour la démonstration dans Main. Elle retourne le tableau interne
    // lui-même : l'appelant peut le modifier sans passer par ajouterCompte().
    public Compte[] getComptesSansProtection() {
        return comptes;
    }
}
