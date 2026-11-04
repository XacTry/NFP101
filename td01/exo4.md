Exercice 4 - Protéger un compte utilisateur

Hypothèses

L'énoncé ne dit pas qui peut réactiver un compte. Je considère que seul un administrateur peut le faire : si l'utilisateur pouvait réactiver lui-même son compte, le blocage ne servirait à rien.

L'énoncé ne dit pas ce que devient le compteur d'échecs après une réactivation. Je le remets à zéro. Sinon, le compteur resterait à 3 et le prochain échec bloquerait à nouveau le compte immédiatement.

Une tentative sur un compte bloqué est refusée, même avec le bon mot de passe, et ne modifie pas le compteur. Le compte est déjà bloqué, compter de nouveaux échecs n'apporterait rien.

Un compte ne peut être bloqué qu'automatiquement, après trois échecs. L'énoncé ne prévoit pas de blocage manuel par un administrateur.

Le mot de passe est stocké sous forme d'empreinte (hash), jamais en clair.

1. Classe Compte

Attributs (privés) :

- identifiant : texte, non modifiable
- empreinteMotDePasse : texte
- estBloque : booléen, faux à la création
- nbEchecsConsecutifs : entier, 0 à la création
- MAX_ECHECS : constante égale à 3, commune à tous les comptes

Méthodes publiques :

- seConnecter(motDePasse) : tente une connexion, applique toutes les règles et renvoie vrai si la connexion réussit
- reactiver() : débloque le compte et remet le compteur à zéro, réservée à l'administration
- estBloque() : indique si le compte est bloqué
- getNbEchecs() : renvoie le nombre d'échecs consécutifs
- getIdentifiant() : renvoie l'identifiant

Méthode privée :

- verifierMotDePasse(motDePasse) : compare l'empreinte du mot de passe saisi à celle stockée

Il n'y a aucune méthode qui permette de modifier directement estBloque ou nbEchecsConsecutifs, ni de lire l'empreinte du mot de passe.

Fonctionnement de seConnecter(motDePasse) :

- si le compte est bloqué, la connexion est refusée et rien ne change
- si le mot de passe est correct, le compteur revient à 0 et la connexion réussit
- si le mot de passe est faux, le compteur augmente de 1 ; s'il atteint 3, le compte est bloqué ; la connexion échoue

2. Évolution d'un compte

Un compte est créé : il est actif, avec 0 échec.

Tentative 1, mot de passe faux : échec, compteur à 1, actif.
Tentative 2, mot de passe faux : échec, compteur à 2, actif.
Tentative 3, mot de passe correct : réussite, compteur remis à 0, actif.
Tentative 4, mot de passe faux : échec, compteur à 1, actif.
Tentative 5, mot de passe faux : échec, compteur à 2, actif.
Tentative 6, mot de passe faux : échec, compteur à 3, le compte passe à bloqué.
Tentative 7, mot de passe correct : refusée car le compte est bloqué, compteur toujours à 3.
Un administrateur appelle reactiver() : le compte redevient actif, compteur à 0.
Tentative 8, mot de passe correct : réussite, compteur à 0, actif.

Les tentatives 1 à 3 montrent que seuls les échecs consécutifs comptent : la réussite de la tentative 3 efface les deux échecs précédents. La tentative 7 montre que même le bon mot de passe ne suffit plus une fois le compte bloqué.

3. Risques de la proposition du développeur

Si toutes les parties du logiciel peuvent modifier directement les attributs du compte, les règles de sécurité ne sont plus garanties.

Contournement du blocage : n'importe quel module peut remettre nbEchecsConsecutifs à 0 après chaque échec, ou repasser estBloque à faux. Un attaquant qui exploite une faille dans un seul module peut alors essayer des mots de passe à l'infini (attaque par force brute).

États incohérents : rien n'empêche d'avoir un compte actif avec 5 échecs, un compteur négatif, ou un compte bloqué avec 0 échec. Le comportement du logiciel devient imprévisible.

Règles dupliquées : chaque module qui gère une connexion doit réécrire lui-même la règle des 3 échecs. Il suffit qu'un seul l'oublie ou la code différemment (4 échecs au lieu de 3, oubli de la remise à zéro) pour créer une faille.

Fuite d'information : si l'empreinte du mot de passe est accessible partout, elle peut être lue, copiée ou affichée par erreur, puis attaquée hors ligne.

Difficulté de contrôle : quand un compte est débloqué de façon anormale, il est impossible de savoir quelle partie du code l'a fait, puisque toutes en ont le droit.

4. Organisation des opérations

Les attributs sont privés. Le seul moyen d'agir sur un compte est de passer par ses méthodes publiques.

Toutes les règles de connexion sont regroupées dans une seule méthode, seConnecter(). Elle vérifie le blocage, le mot de passe, met à jour le compteur et bloque le compte si nécessaire, toujours dans le même ordre. Aucun module ne peut faire une partie du travail sans faire le reste, donc la règle est appliquée partout de la même façon.

Il n'existe pas de setter (setEstBloque, setNbEchecs). Les seuls changements d'état possibles sont ceux prévus par la politique : un échec, une réussite ou une réactivation.

La réactivation est la seule opération qui débloque un compte. Elle est réservée à l'administration : seul le module d'administration, après avoir vérifié que l'utilisateur est bien administrateur, a le droit de l'appeler. La même méthode remet aussi le compteur à zéro, donc on ne peut pas oublier l'une des deux actions.

Le constructeur crée toujours un compte valide : actif, avec 0 échec. Il est impossible de créer un compte dans un état incohérent.

Les autres modules peuvent lire l'état du compte (estBloque(), getNbEchecs()), par exemple pour afficher un message, mais jamais le modifier directement.

5. Encapsulation

L'encapsulation consiste à cacher les données d'un objet et à n'autoriser leur modification qu'à travers ses propres méthodes. L'objet est ainsi responsable de rester dans un état valide.

Dans ma conception, la classe Compte cache ses attributs (estBloque, nbEchecsConsecutifs, empreinteMotDePasse) et n'expose que des opérations qui ont un sens pour la politique de sécurité : se connecter, être réactivé, consulter son état.

Concrètement, cela garantit que :

- un compte ne peut être bloqué qu'après trois échecs consécutifs, et débloqué que par une réactivation
- le compteur ne peut pas être modifié arbitrairement
- l'empreinte du mot de passe ne sort jamais de l'objet
- la règle est écrite à un seul endroit ; si l'entreprise passe à 5 échecs, seule la constante MAX_ECHECS change

C'est exactement ce que la proposition du développeur supprimait : en rendant les données modifiables par tous, elle retirait au compte la responsabilité de faire respecter ses propres règles.
