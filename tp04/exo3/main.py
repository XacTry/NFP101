from coffre import Coffre


def main():
    print("=== Question 1 : attribut public ===")

    coffre = Coffre(100)

    print("Contenu initial :", coffre.contenu)

    # TODO : attribuer une valeur négative
    # directement à l'attribut contenu.

    # TODO : afficher le nouveau contenu.

    print("\n=== Question 2 : convention _contenu ===")

    # TODO : tester les méthodes de dépôt et de retrait.
    #
    # Essayer notamment :
    # deposer(20)
    # retirer(30)
    # retirer(200)
    # deposer(-10)
    # retirer(-5)

    # TODO : essayer de modifier directement _contenu.

    print("\n=== Question 3 : name mangling ===")

    # TODO : essayer d'accéder directement à __contenu.

    # TODO : afficher les attributs avec vars().

    # TODO : retrouver le nom interne de __contenu
    # et essayer de modifier sa valeur.

    print("\n=== Question 4 : __slots__ ===")

    # TODO : essayer d'ajouter un attribut imprévu.

    # TODO : vérifier si un attribut existant
    # peut toujours être modifié directement.


if __name__ == "__main__":
    main()
