class Coffre:
    """Version finale (question 4) : __contenu + __slots__.

    Les versions intermédiaires (questions 1 à 3) sont dans main.py.
    """

    # Seul l'attribut __contenu est autorisé. Python applique aussi le
    # name mangling ici : le slot réellement créé s'appelle _Coffre__contenu.
    # Conséquence : plus de __dict__, donc impossible d'ajouter un attribut.
    __slots__ = ("__contenu",)

    def __init__(self, contenu):
        if contenu < 0:
            raise ValueError("Le contenu initial doit etre positif ou nul")
        self.__contenu = contenu

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
