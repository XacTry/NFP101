from coffre import Coffre


# ========================================
# VERSIONS INTERMEDIAIRES (questions 1 a 3)
# La version finale (question 4) est dans coffre.py.
# ========================================


class CoffrePublic:
    """Question 1 : attribut public."""

    def __init__(self, contenu):
        self.contenu = contenu

    def afficher(self):
        print(f"Coffre : {self.contenu} credits")


class CoffreProtege:
    """Question 2 : convention _contenu + methodes de controle."""

    def __init__(self, contenu):
        self._contenu = contenu

    def afficher(self):
        print(f"Coffre : {self._contenu} credits")

    def get_contenu(self):
        return self._contenu

    def deposer(self, montant):
        if montant <= 0:
            print(f"Depot refuse : montant invalide ({montant})")
            return
        self._contenu += montant

    def retirer(self, montant):
        if montant <= 0:
            print(f"Retrait refuse : montant invalide ({montant})")
            return
        if montant > self._contenu:
            print(f"Retrait refuse : contenu insuffisant ({montant} > {self._contenu})")
            return
        self._contenu -= montant


class CoffreMangle:
    """Question 3 : double soulignement (name mangling), sans __slots__."""

    def __init__(self, contenu):
        self.__contenu = contenu  # stocke sous le nom _CoffreMangle__contenu

    def afficher(self):
        print(f"Coffre : {self.__contenu} credits")

    def get_contenu(self):
        return self.__contenu

    def deposer(self, montant):
        if montant <= 0:
            print(f"Depot refuse : montant invalide ({montant})")
            return
        self.__contenu += montant

    def retirer(self, montant):
        if montant <= 0:
            print(f"Retrait refuse : montant invalide ({montant})")
            return
        if montant > self.__contenu:
            print(f"Retrait refuse : contenu insuffisant ({montant} > {self.__contenu})")
            return
        self.__contenu -= montant


def tester_operations(coffre):
    for nom, montant in [("deposer", 20), ("retirer", 30), ("retirer", 200),
                         ("deposer", -10), ("retirer", -5)]:
        getattr(coffre, nom)(montant)
        print(f"{nom}({montant}) -> contenu = {coffre.get_contenu()}")


def main():
    print("=== Question 1 : attribut public ===")

    coffre = CoffrePublic(100)
    print("Contenu initial :", coffre.contenu)
    coffre.afficher()

    coffre.contenu = -50
    coffre.afficher()
    # Resultat : aucune erreur, le coffre contient -50 credits.
    # Un attribut public peut etre modifie librement depuis l'exterieur,
    # aucune regle (contenu >= 0) ne peut etre garantie.

    print("\n=== Question 2 : convention _contenu ===")

    coffre = CoffreProtege(100)
    tester_operations(coffre)

    coffre._contenu = -50
    print("Apres coffre._contenu = -50 :", coffre.get_contenu())
    # Le _ n'est qu'une convention ("attribut interne, ne pas toucher") :
    # Python ne bloque rien, la modification directe fonctionne.

    print("\n=== Question 3 : name mangling ===")

    coffre = CoffreMangle(100)
    tester_operations(coffre)

    try:
        print(coffre.__contenu)
    except AttributeError as erreur:
        print("Acces a coffre.__contenu impossible :", erreur)

    print("vars(coffre) =", vars(coffre))
    # vars() montre le nom interne : _CoffreMangle__contenu
    # (regle : __attribut devient _NomDeLaClasse__attribut).

    coffre._CoffreMangle__contenu = -50
    print("Apres modification via le nom interne :", coffre.get_contenu())
    # Le name mangling evite les acces accidentels (et les collisions de noms
    # dans les sous-classes), mais quiconque connait le nom interne peut
    # toujours modifier l'attribut.

    # Ecrire coffre.__contenu = ... depuis l'exterieur cree un NOUVEL attribut
    # nomme "__contenu" (pas de mangling hors de la classe) : le vrai contenu
    # n'est pas modifie.
    coffre.__contenu = 999
    print("Apres coffre.__contenu = 999 : get_contenu() =", coffre.get_contenu())
    print("vars(coffre) =", vars(coffre))

    print("\n=== Question 4 : __slots__ ===")

    coffre = Coffre(100)
    tester_operations(coffre)

    try:
        coffre.__contenu = 999
    except AttributeError as erreur:
        print("Ajout de __contenu refuse :", erreur)

    try:
        coffre.attribut_inconnu = 123
    except AttributeError as erreur:
        print("Ajout d'un attribut non declare refuse :", erreur)

    try:
        vars(coffre)
    except TypeError as erreur:
        print("vars() ne fonctionne plus :", erreur)

    print("Slots de la classe :", Coffre.__slots__, "-> nom interne : _Coffre__contenu")

    coffre._Coffre__contenu = -50
    print("Apres modification via le nom interne :", coffre.get_contenu())
    # __slots__ empeche d'ajouter des attributs (plus de __dict__), mais
    # n'empeche pas de modifier un attribut declare via son nom interne.

    # ----------------------------------------
    # Comparaison des mecanismes
    # ----------------------------------------
    # contenu        : public, modifiable par tous, aucune protection.
    # _contenu       : simple convention, rien n'est bloque techniquement.
    # __contenu      : name mangling -> nom interne _Coffre__contenu ; evite
    #                  les acces accidentels mais reste contournable.
    # __slots__      : interdit l'ajout d'attributs non declares et supprime
    #                  __dict__ ; ne protege pas les attributs existants.
    # Conclusion : en Python, l'encapsulation repose sur des conventions et
    # sur la discipline des developpeurs, pas sur une interdiction stricte
    # comme private en Java. Les methodes (deposer, retirer) restent le seul
    # moyen de garantir les regles quand on les utilise.


if __name__ == "__main__":
    main()
