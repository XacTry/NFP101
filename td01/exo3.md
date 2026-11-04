Exercice 3 - Qui doit faire quoi ?

Hypothèses

Les informations fournies lors d'une connexion sont un identifiant et un mot de passe. L'énoncé ne parle pas d'authentification à plusieurs facteurs.

Les "conditions d'accès au service" ne sont pas précisées. Je retiens deux règles courantes : le compte ne doit pas être bloqué, et la connexion n'est autorisée que pendant une plage horaire définie. Après un certain nombre d'échecs consécutifs (par exemple 5), le compte est bloqué automatiquement.

Le blocage peut donc être automatique (trop d'échecs) ou manuel (décision d'un administrateur). La réactivation est toujours manuelle.

"Gérer les comptes" signifie créer, rechercher et supprimer des comptes.

Le mot de passe n'est jamais stocké en clair, seulement son empreinte (hash). C'est une règle de base en cybersécurité : si la base est volée, les mots de passe ne sont pas directement lisibles.

1. Analyse de la proposition du développeur

Avantages : la solution est simple à comprendre au début, tout le code est au même endroit, et il y a peu de classes à écrire. Pour une toute petite application, cela peut suffire.

Limites :

La classe Compte aurait trop de responsabilités : stocker les données d'un compte, vérifier un mot de passe, appliquer des règles d'accès, gérer la liste de tous les comptes et tenir un journal. Plus elle grossit, plus elle devient difficile à comprendre, à tester et à faire évoluer.

Certaines fonctionnalités n'appartiennent pas à un compte. Un objet Compte représente un seul compte, alors que "gérer les comptes" concerne l'ensemble des comptes. De même, l'historique doit enregistrer des tentatives avec un identifiant qui n'existe pas : elles ne peuvent être rattachées à aucun objet Compte.

Les règles d'accès (plage horaire, nombre d'échecs) sont les mêmes pour tous les comptes. Les placer dans Compte les dupliquerait dans chaque instance, et changer une règle obligerait à modifier la classe Compte.

Enfin, il y a un risque de sécurité : tout code qui manipule un compte aurait aussi accès à l'historique et aux fonctions d'administration. Il devient plus difficile de contrôler qui peut faire quoi, et de garantir que l'historique n'est jamais modifié.

2. Organisation proposée

Je propose six classes, chacune avec une responsabilité unique :

- Compte : représente un compte utilisateur
- GestionnaireComptes : gère l'ensemble des comptes
- PolitiqueAcces : contient les règles d'accès au service
- TentativeConnexion : représente une tentative de connexion
- HistoriqueConnexions : conserve et permet de consulter les tentatives
- ServiceAuthentification : coordonne le déroulement d'une connexion

3. Détail des classes

Compte
Responsabilité : contenir les informations d'un compte et protéger son état.
Attributs (privés) : identifiant, empreinteMotDePasse, estBloque, nbEchecsConsecutifs.
Méthodes : verifierMotDePasse(motDePasse), bloquer(), reactiver(), estBloque(), enregistrerEchec(), reinitialiserEchecs(), getNbEchecs().
verifierMotDePasse calcule l'empreinte du mot de passe saisi et la compare à celle stockée. L'empreinte ne sort jamais de l'objet : il n'existe pas de méthode getEmpreinteMotDePasse(). reactiver() remet aussi le compteur d'échecs à zéro.

GestionnaireComptes
Responsabilité : gérer l'ensemble des comptes.
Attribut : la liste des comptes.
Méthodes : creerCompte(identifiant, motDePasse), supprimerCompte(identifiant), trouverCompte(identifiant).
creerCompte refuse un identifiant déjà utilisé.

PolitiqueAcces
Responsabilité : décider si les conditions d'accès sont remplies.
Attributs : nbMaxEchecs, heureDebut, heureFin.
Méthodes : estAutorise(compte, dateHeure), doitEtreBloque(compte).
estAutorise vérifie que le compte n'est pas bloqué et que l'heure est dans la plage autorisée. doitEtreBloque indique si le nombre d'échecs du compte a atteint nbMaxEchecs.

TentativeConnexion
Responsabilité : représenter une tentative de connexion, réussie ou non.
Attributs : identifiantSaisi, dateHeure, adresseIP, reussie, motif (par exemple "mot de passe incorrect", "compte bloqué", "hors plage horaire", "identifiant inconnu").
Méthodes : des méthodes de lecture uniquement (getDateHeure(), estReussie()...). Une tentative ne peut pas être modifiée après sa création.
L'attribut est identifiantSaisi, un simple texte, et non une référence vers un Compte : une tentative peut concerner un identifiant qui n'existe pas.

HistoriqueConnexions
Responsabilité : conserver les tentatives et permettre de les consulter.
Attribut : la liste des tentatives.
Méthodes : enregistrer(tentative), consulter(), consulterParIdentifiant(identifiant), consulterEchecs().
Il n'existe aucune méthode pour modifier ou supprimer une tentative : l'historique sert de trace en cas d'incident, il ne doit pas pouvoir être effacé.

ServiceAuthentification
Responsabilité : coordonner une tentative de connexion.
Attributs : références vers le GestionnaireComptes, la PolitiqueAcces et l'HistoriqueConnexions.
Méthode : seConnecter(identifiant, motDePasse, adresseIP), qui renvoie vrai ou faux.

4. Justification de la répartition

Chaque classe a une seule raison de changer. Si l'entreprise modifie la plage horaire ou le nombre d'échecs autorisés, seule PolitiqueAcces change. Si elle veut stocker l'historique autrement, seul HistoriqueConnexions change. Compte n'est pas touchée.

Chaque donnée est placée dans la classe qui la protège. Compte possède l'empreinte du mot de passe, donc c'est elle qui la vérifie : c'est l'encapsulation. Aucune autre classe n'a besoin de connaître l'empreinte.

Ce qui concerne un seul compte est dans Compte. Ce qui concerne tous les comptes (la liste, les règles communes, l'historique) est dans une classe séparée, dont il n'existe qu'une instance.

Les droits sont plus faciles à contrôler. Le code de connexion utilise ServiceAuthentification, l'interface d'administration utilise GestionnaireComptes et HistoriqueConnexions. Un utilisateur qui se connecte n'a jamais accès à l'historique.

ServiceAuthentification ne contient aucune règle elle-même : elle se contente d'appeler les autres objets dans le bon ordre. Cela évite de recréer une grosse classe qui ferait tout.

5. Collaboration lors d'une tentative de connexion

Exemple : Alice saisit son identifiant et son mot de passe.

1. L'application appelle seConnecter("alice", motDePasse, adresseIP) sur le ServiceAuthentification.

2. Le service demande le compte au GestionnaireComptes avec trouverCompte("alice"). Si aucun compte n'existe, il crée une TentativeConnexion échouée (motif "identifiant inconnu"), l'enregistre dans l'HistoriqueConnexions et renvoie faux.

3. Le service demande à la PolitiqueAcces si l'accès est autorisé avec estAutorise(compte, maintenant). Si le compte est bloqué ou si l'heure est hors plage, la tentative est enregistrée comme échouée avec le motif correspondant, et le service renvoie faux.

4. Le service demande au compte de vérifier le mot de passe avec verifierMotDePasse(motDePasse).

5. Si le mot de passe est faux, le service appelle enregistrerEchec() sur le compte, puis demande à la PolitiqueAcces doitEtreBloque(compte). Si oui, il appelle bloquer() sur le compte. La tentative échouée est enregistrée dans l'historique, et le service renvoie faux.

6. Si le mot de passe est correct, le service appelle reinitialiserEchecs() sur le compte, enregistre une tentative réussie dans l'historique, et renvoie vrai.

Dans tous les cas où la connexion échoue, l'utilisateur reçoit le même message, par exemple "identifiant ou mot de passe incorrect". Le motif précis n'est visible que dans l'historique. Cela évite qu'un attaquant puisse savoir si un identifiant existe.
