Exercice 1 - Des ordinateurs sur un réseau

Hypothèses

L'administrateur n'est pas modélisé : c'est l'utilisateur du logiciel, pas un élément à représenter, et aucune information n'est demandée sur lui.
L'état de connexion est représenté par un booléen (estConnecte) plutôt que par un texte. Il n'a que deux valeurs possibles, et un texte permettrait des valeurs invalides. Le texte "Connecté" ou "Déconnecté" est calculé à partir de ce booléen.
Connecter un ordinateur déjà connecté ne fait rien, puisque l'énoncé ne prévoit pas de cas d'erreur et que le résultat attendu est déjà atteint.
Le nom et l'adresse IP ne changent pas, car rien dans l'énoncé ne demande de les modifier.

Classes, attributs et méthodes
Une seule classe suffit : Ordinateur. Les trois ordinateurs du tableau ont la même structure (nom, adresse IP, état) et le même comportement (connexion, déconnexion).
La classe Ordinateur possède trois attributs. Le nom correspond à la colonne "Nom" du tableau, l'adresseIP à la colonne "Adresse IP", et estConnecte à la colonne "État".
Elle possède quatre méthodes. connecter() et deconnecter() répondent à la demande de pouvoir connecter ou déconnecter un ordinateur. getEtat() permet de suivre l'état de connexion, et afficherInfos() permet de consulter les informations de l'ordinateur.
Les attributs sont privés : l'état ne peut changer que par connecter() et deconnecter(). On ne peut donc pas mettre un ordinateur dans un état incohérent depuis l'extérieur. C'est le principe d'encapsulation.

Les trois objets
pc1, instance de Ordinateur : nom = "PC-01", adresseIP = "192.168.1.10", estConnecte = vrai. pc2, instance de Ordinateur : nom = "PC-02", adresseIP = "192.168.1.11", estConnecte = faux. pc3, instance de Ordinateur : nom = "PC-03", adresseIP = "192.168.1.12", estConnecte = vrai.

PC-02 se connecte
L'événement correspond à l'appel de pc2.connecter().
Seul l'objet pc2 est modifié : son attribut estConnecte passe de faux à vrai, et getEtat() renvoie désormais "Connecté" au lieu de "Déconnecté". Son nom et son adresse IP restent identiques.
Les objets pc1 et pc3 ne sont pas touchés, car chaque objet possède son propre état. La classe Ordinateur ne change pas non plus : ce sont les valeurs d'un objet qui évoluent, pas sa définition.

Classe et instance
La classe Ordinateur est le modèle. Elle décrit ce que possède tout ordinateur du réseau (un nom, une adresse IP, un état) et ce qu'il sait faire. Elle ne contient aucune valeur et ne désigne aucun ordinateur réel.
pc1, pc2 et pc3 sont trois instances de cette classe. Elles partagent la même structure et les mêmes méthodes, mais chacune a ses propres valeurs.
La question 3 le montre : quand pc2 se connecte, une seule instance change d'état, sans effet sur la classe ni sur les autres instances. Si un PC-04 est ajouté au réseau, on crée simplement une nouvelle instance, sans modifier la classe.
