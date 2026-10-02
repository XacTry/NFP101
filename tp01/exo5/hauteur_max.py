import sys


def hauteur_maximale(hauteurs):
    """Renvoie (hauteur max, indice). En cas d'egalite, le premier indice est garde."""
    indice_max = 0
    for i in range(1, len(hauteurs)):
        if hauteurs[i] > hauteurs[indice_max]:
            indice_max = i
    return hauteurs[indice_max], indice_max


def main():
    # Hauteurs passees en arguments, sinon lues sur l'entree standard
    valeurs = sys.argv[1:] if len(sys.argv) > 1 else sys.stdin.read().split()
    hauteurs = [int(v) for v in valeurs]

    if not hauteurs:
        print("Aucune hauteur fournie.")
        return

    hauteur, indice = hauteur_maximale(hauteurs)
    print(f"Hauteur : {hauteur}")
    print(f"Indice : {indice}")


if __name__ == "__main__":
    main()