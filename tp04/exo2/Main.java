
public class Main {

    public static void main(String[] args) {

        // ========================================
        // QUESTION 1 - CREER UN PORTEFEUILLE
        // ========================================

        // Creer trois comptes :
        // Alice avec 100 credits
        // Bob avec 50 credits
        // Charlie avec 200 credits

        // Creer un portefeuille pouvant contenir 10 comptes

        // Ajouter les trois comptes au portefeuille

        // Afficher le contenu du portefeuille


        // ========================================
        // QUESTION 2 - TRANSFERER DES CREDITS
        // ========================================

        // Transferer 30 credits d'Alice vers Bob

        // Afficher les soldes apres le transfert

        // Transferer 50 credits de Charlie vers Alice

        // Afficher les soldes apres le transfert

        // Tenter de transferer 500 credits de Bob vers Charlie

        // Afficher les soldes apres cette tentative

        // Verifier que la somme des soldes est conservee


        // ========================================
        // QUESTION 3 - REFERENCES ET COMPOSITION
        // ========================================

        // Recuperer une reference vers le compte d'Alice
        // depuis le portefeuille

        // Effectuer un depot sur ce compte

        // Afficher le portefeuille et observer la modification

        // Creer un second portefeuille

        // Ajouter au second portefeuille la meme reference
        // vers le compte d'Alice

        // Modifier le compte d'Alice depuis le second portefeuille

        // Afficher les deux portefeuilles

        // Observer les consequences du partage des references


        // ========================================
        // QUESTION 4 - FUITE DE REFERENCE
        // ========================================

        // Recuperer le tableau retourne par getComptes()

        // Remplacer une reference du tableau par null

        // Ajouter un compte dans un emplacement disponible

        // Vider le tableau en remplacant ses elements par null

        // Afficher le portefeuille apres les modifications

        // Observer la fuite de reference vers le tableau interne

        // Modifier getComptes() pour retourner une copie defensive

        // Refaire les manipulations precedentes sur la copie

        // Verifier que le tableau interne n'est plus modifie

        // Recuperer un compte depuis le tableau copie

        // Effectuer un depot sur ce compte

        // Afficher le portefeuille

        // Observer que les objets Compte restent partages

    }
}
