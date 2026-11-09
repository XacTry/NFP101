import importlib.util
import inspect
from pathlib import Path

# ========================================
# RESULTATS
# ========================================

reussis = 0
echecs = 0
manquants = 0

Coffre = None


def section(titre):
    print(f"\n=== {titre} ===")


def verifier(description, condition):
    global reussis, echecs

    if condition:
        print(f"[OK] {description}")
        reussis += 1
    else:
        print(f"[ECHEC] {description}")
        echecs += 1


def manquant(description):
    global manquants

    print(f"[NON IMPLEMENTE] {description}")
    manquants += 1


def bilan():
    section("Bilan")
    print(f"Tests reussis : {reussis}")
    print(f"Tests echoues : {echecs}")
    print(f"Non implementes : {manquants}")


# ========================================
# CHARGEMENT DE LA CLASSE
# ========================================


def charger_coffre():
    chemin = Path(__file__).with_name("coffre.py")

    if not chemin.exists():
        manquant("Fichier coffre.py")
        return None

    try:
        spec = importlib.util.spec_from_file_location("module_coffre", chemin)
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)

    except Exception as erreur:
        verifier(f"Chargement de coffre.py ({type(erreur).__name__})", False)
        return None

    classe = getattr(module, "Coffre", None)

    if not inspect.isclass(classe):
        manquant("Classe Coffre")
        return None

    return classe


def creer_coffre(valeur):
    try:
        return Coffre(valeur)

    except TypeError:
        verifier("Constructeur Coffre(valeur_initiale)", False)

    except Exception as erreur:
        verifier(f"Creation du coffre ({type(erreur).__name__})", False)

    return None


# ========================================
# OUTILS DE DETECTION
# ========================================


def methode_presente(nom):
    return callable(getattr(Coffre, nom, None))


def attribut_interne(coffre):
    """
    Detecte l'etape actuelle de l'encapsulation.
    """
    if hasattr(coffre, "_Coffre__contenu"):
        return "_Coffre__contenu"

    if hasattr(coffre, "_contenu"):
        return "_contenu"

    if hasattr(coffre, "contenu"):
        return "contenu"

    return None


def etape_actuelle(coffre):
    nom = attribut_interne(coffre)

    if nom == "contenu":
        return 1

    if nom == "_contenu":
        return 2

    if nom == "_Coffre__contenu":
        if "__slots__" in Coffre.__dict__:
            return 4
        return 3

    return 0


def appeler(coffre, nom, *arguments):
    methode = getattr(coffre, nom, None)

    if not callable(methode):
        manquant(f"Methode {nom}()")
        return False

    try:
        methode(*arguments)
        return True

    except Exception as erreur:
        verifier(f"Execution de {nom}() " f"({type(erreur).__name__})", False)
        return False


def lire_contenu(coffre):
    getter = getattr(coffre, "get_contenu", None)

    if not callable(getter):
        return None

    try:
        return getter()
    except Exception:
        return None


# ========================================
# QUESTION 1 : PREMIERE CLASSE
# ========================================


def question1(coffre, etape):
    section("Question 1 - Premiere classe")

    verifier(
        "Le constructeur initialise le contenu a 100",
        getattr(coffre, attribut_interne(coffre), None) == 100,
    )

    if etape == 1:
        verifier("L'attribut public contenu existe", hasattr(coffre, "contenu"))

        try:
            coffre.contenu = -10

            verifier(
                "L'attribut public peut etre modifie directement", coffre.contenu == -10
            )

        except Exception:
            verifier("L'attribut public peut etre modifie directement", False)

    else:
        print(
            "[ETAPE PRECEDENTE] L'attribut public "
            "a ete remplace pour l'encapsulation."
        )

    # Le nom de la methode d'affichage n'est pas
    # impose explicitement dans l'enonce.
    # On retient ici afficher().
    if methode_presente("afficher"):
        verifier("La methode afficher() existe", True)

        import io
        from contextlib import redirect_stdout

        sortie = io.StringIO()

        try:
            with redirect_stdout(sortie):
                coffre.afficher()

            verifier("afficher() produit un affichage", bool(sortie.getvalue().strip()))

        except Exception:
            verifier("Execution de afficher()", False)

    else:
        manquant("Methode afficher()")


# ========================================
# QUESTION 2 : METHODES ET ENCAPSULATION
# ========================================


def question2(etape):
    section("Question 2 - Methodes et encapsulation")

    if etape < 2:
        manquant("Attribut _contenu")
        manquant("Methode deposer(montant)")
        manquant("Methode retirer(montant)")
        manquant("Methode get_contenu()")
        return

    if etape == 2:
        verifier("L'attribut _contenu existe", True)
    else:
        print("[ETAPE PRECEDENTE] _contenu a ete " "remplace par __contenu.")

    methodes = ["deposer", "retirer", "get_contenu"]

    disponibles = True

    for nom in methodes:
        if methode_presente(nom):
            verifier(f"La methode {nom}() existe", True)
        else:
            manquant(f"Methode {nom}()")
            disponibles = False

    if not disponibles:
        return

    coffre = creer_coffre(100)

    if coffre is None:
        return

    verifier("get_contenu() retourne 100", lire_contenu(coffre) == 100)

    # Depot valide
    if appeler(coffre, "deposer", 20):
        verifier("deposer(20) donne 120", lire_contenu(coffre) == 120)

    # Retrait valide
    if appeler(coffre, "retirer", 30):
        verifier("retirer(30) donne 90", lire_contenu(coffre) == 90)

    # Retrait excessif
    avant = lire_contenu(coffre)

    if appeler(coffre, "retirer", 200):
        verifier("retirer(200) est refuse", lire_contenu(coffre) == avant)

    # Depot negatif
    avant = lire_contenu(coffre)

    if appeler(coffre, "deposer", -10):
        verifier("deposer(-10) est refuse", lire_contenu(coffre) == avant)

    # Depot nul
    avant = lire_contenu(coffre)

    if appeler(coffre, "deposer", 0):
        verifier("deposer(0) est refuse", lire_contenu(coffre) == avant)

    # Retrait negatif
    avant = lire_contenu(coffre)

    if appeler(coffre, "retirer", -5):
        verifier("retirer(-5) est refuse", lire_contenu(coffre) == avant)

    # Retrait nul
    avant = lire_contenu(coffre)

    if appeler(coffre, "retirer", 0):
        verifier("retirer(0) est refuse", lire_contenu(coffre) == avant)

    # Acces direct : convention Python
    if etape == 2:
        try:
            coffre._contenu = -50

            verifier("_contenu reste modifiable directement", coffre._contenu == -50)

        except Exception:
            verifier("_contenu reste modifiable directement", False)


# ========================================
# QUESTION 3 : DOUBLE SOULIGNEMENT
# ========================================


def question3(etape):
    section("Question 3 - Double soulignement")

    if etape < 3:
        manquant("Attribut __contenu")
        return

    coffre = creer_coffre(100)

    if coffre is None:
        return

    nom_interne = "_Coffre__contenu"

    verifier("Le name mangling est applique", hasattr(coffre, nom_interne))

    verifier(
        "__contenu n'est pas directement accessible", not hasattr(coffre, "__contenu")
    )

    # vars() fonctionne normalement avant __slots__.
    if etape == 3:
        try:
            attributs = vars(coffre)

            verifier("vars() revele le nom interne", nom_interne in attributs)

        except TypeError:
            verifier("vars() fonctionne sans __slots__", False)

    else:
        print(
            "[ETAPE PRECEDENTE] vars() peut ne plus "
            "fonctionner apres l'ajout de __slots__."
        )

    # Le nom transforme reste accessible.
    try:
        setattr(coffre, nom_interne, -50)

        verifier(
            "Le nom interne permet de modifier le contenu",
            getattr(coffre, nom_interne) == -50,
        )

    except Exception:
        verifier("Le nom interne permet de modifier le contenu", False)


# ========================================
# QUESTION 4 : __slots__
# ========================================


def question4(etape):
    section("Question 4 - __slots__")

    if "__slots__" not in Coffre.__dict__:
        manquant("Declaration de __slots__")
        return

    slots = Coffre.__dict__["__slots__"]

    if isinstance(slots, str):
        noms_slots = (slots,)
    else:
        try:
            noms_slots = tuple(slots)
        except TypeError:
            noms_slots = ()

    verifier(
        "__slots__ declare __contenu",
        "__contenu" in noms_slots or "_Coffre__contenu" in noms_slots,
    )

    coffre = creer_coffre(100)

    if coffre is None:
        return

    # L'ajout d'un attribut arbitraire doit echouer.
    try:
        coffre.attribut_inconnu = 123

        verifier("Ajout d'un attribut non declare interdit", False)

    except AttributeError:
        verifier("Ajout d'un attribut non declare interdit", True)

    except Exception:
        verifier("Ajout d'un attribut non declare interdit", False)

    # Absence de dictionnaire d'instance.
    verifier("Le coffre ne possede pas de __dict__", not hasattr(coffre, "__dict__"))

    # L'attribut existant reste modifiable
    # avec son nom transforme.
    try:
        coffre._Coffre__contenu = -50

        verifier("L'attribut existant reste modifiable", coffre._Coffre__contenu == -50)

    except Exception:
        verifier("L'attribut existant reste modifiable", False)


# ========================================
# PROGRAMME PRINCIPAL
# ========================================


def main():
    global Coffre

    print("TP04 - EXERCICE 3 - TEST COFFRE")

    Coffre = charger_coffre()

    if Coffre is None:
        bilan()
        return

    coffre = creer_coffre(100)

    if coffre is None:
        bilan()
        return

    etape = etape_actuelle(coffre)

    if etape == 0:
        manquant("Attribut contenu, _contenu ou __contenu")
        bilan()
        return

    question1(coffre, etape)
    question2(etape)
    question3(etape)
    question4(etape)

    bilan()


if __name__ == "__main__":
    main()
