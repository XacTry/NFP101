Exercice 6 - Plusieurs technologies de lecture

Hypothèses

Le rôle d'un lecteur est d'identifier la personne, pas de décider si elle peut entrer. Il lit un identifiant (l'UID de la puce pour un badge RFID, le contenu du code pour un QR code) et le transmet au système de contrôle d'accès. C'est le système qui décide, à partir de ses propres données. Ainsi, la décision reste au même endroit quelle que soit la technologie.

Chaque lecteur est installé à un emplacement précis (une porte d'un bâtiment) et peut être en service ou hors service, par exemple en cas de panne ou de maintenance.

L'énoncé ne dit pas quelle nouvelle technologie l'entreprise veut ajouter. Je prends l'exemple d'un lecteur NFC pour smartphone, qui lit un identifiant enregistré dans le téléphone.

1. Conception proposée

Une classe abstraite Lecteur représente ce qu'ont en commun tous les équipements de lecture. Deux classes en héritent : LecteurRFID et LecteurQRCode.

Une classe ControleAcces utilise les lecteurs sans savoir de quel type ils sont.

Lecteur (abstraite)
Attributs (privés) : identifiant, emplacement, enService.
Méthodes :

- lireIdentifiant() : abstraite, renvoie l'identifiant lu ; chaque technologie la définit à sa manière
- mettreEnService(), mettreHorsService()
- estEnService()
- getEmplacement()

LecteurRFID (hérite de Lecteur)
Attributs propres : frequence (par exemple 13,56 MHz), porteeMaximale.
lireIdentifiant() : détecte un badge à proximité et renvoie l'UID de sa puce.

LecteurQRCode (hérite de Lecteur)
Attribut propre : resolutionCamera.
lireIdentifiant() : capture une image avec la caméra, décode le QR code et renvoie l'identifiant qu'il contient.

ControleAcces
Attributs : la liste des lecteurs installés, la liste des identifiants autorisés.
Méthode : traiterDemande(lecteur), qui demande l'identifiant au lecteur et décide d'ouvrir ou non la porte.

2. Caractéristiques et comportements communs

Caractéristiques communes : tout lecteur a un identifiant (pour le distinguer des autres), un emplacement (pour savoir quelle porte il contrôle) et un état en service ou hors service.

Comportements communs : tout lecteur sait lire un identifiant, être mis en service ou hors service, et indiquer son état.

Ce qui diffère d'une technologie à l'autre, c'est la manière de lire : une antenne radio pour le RFID, une caméra et un décodage d'image pour le QR code. Chaque technologie a aussi ses propres caractéristiques techniques (fréquence, portée, résolution).

L'action "lire un identifiant" est donc commune, mais sa réalisation est propre à chaque technologie. C'est pour cela que lireIdentifiant() est déclarée dans Lecteur mais définie dans chaque classe fille.

3. Utilisation de l'héritage

LecteurRFID et LecteurQRCode héritent de Lecteur. On peut dire qu'un lecteur RFID "est un" lecteur, et qu'un lecteur QR code "est un" lecteur : la relation d'héritage est justifiée.

L'héritage apporte trois avantages ici.

Pas de duplication : l'identifiant, l'emplacement, l'état et les méthodes de mise en service sont écrits une seule fois, dans Lecteur. Les classes filles n'ajoutent que ce qui leur est propre.

Un contrat commun : comme lireIdentifiant() est abstraite dans Lecteur, toute classe fille est obligée de la définir. On est sûr que tout lecteur, quel qu'il soit, sait fournir un identifiant.

Un type commun : le système peut manipuler tous les équipements comme des objets de type Lecteur, ce qui permet le polymorphisme (question 5).

Lecteur est abstraite parce qu'un "lecteur" sans technologie n'existe pas dans la réalité : on ne doit pas pouvoir créer un objet Lecteur directement, seulement un LecteurRFID ou un LecteurQRCode.

4. Utilisation par le système de contrôle d'accès

ControleAcces ne connaît que le type Lecteur. Sa liste contient des lecteurs RFID et des lecteurs QR code mélangés, sans distinction.

Quand une personne se présente à une porte, le système appelle traiterDemande(lecteur) :

1. Il vérifie que le lecteur est en service avec estEnService().
2. Il appelle lecteur.lireIdentifiant() et obtient un identifiant.
3. Il cherche cet identifiant dans la liste des identifiants autorisés.
4. Il ouvre la porte à l'emplacement du lecteur si l'identifiant est autorisé, sinon il refuse.

À aucun moment le système ne teste le type du lecteur ("si c'est un lecteur RFID alors... sinon si c'est un QR code alors..."). Il appelle simplement lireIdentifiant(), et c'est le lecteur qui sait comment faire.

5. Polymorphisme

Le polymorphisme, c'est le fait qu'un même appel de méthode produise un comportement différent selon l'objet réel qui le reçoit.

Dans ma conception, ControleAcces écrit toujours le même appel : lecteur.lireIdentifiant().

Si l'objet est un LecteurRFID, c'est la version RFID qui s'exécute : lecture de la puce par ondes radio.
Si l'objet est un LecteurQRCode, c'est la version QR code qui s'exécute : capture d'image et décodage.

Exemple : la porte principale est équipée d'un lecteur RFID, l'accueil d'un lecteur QR code pour les visiteurs. Le système traite les deux portes avec exactement le même code. Le résultat est le même (un identifiant), mais la façon de l'obtenir change selon l'objet.

Le système manipule des objets de types différents à travers un type commun (Lecteur), et chacun réagit à sa manière : c'est le polymorphisme d'objet demandé par l'énoncé.

6. Ajout d'une nouvelle technologie

Pour ajouter des lecteurs NFC pour smartphone, il suffit de :

1. Créer une classe LecteurNFC qui hérite de Lecteur, avec ses attributs propres si besoin.
2. Définir sa méthode lireIdentifiant(), qui récupère l'identifiant transmis par le téléphone.
3. Ajouter les nouveaux lecteurs à la liste de ControleAcces.

Rien d'autre ne change. La classe Lecteur, les classes LecteurRFID et LecteurQRCode, et la classe ControleAcces restent intactes. Le code existant, déjà testé, n'est pas touché, ce qui répond à la demande de l'entreprise : intégrer de nouveaux équipements sans remettre en cause l'application.

Limite : cela ne fonctionne que si la nouvelle technologie peut fournir un identifiant, comme les autres. Un lecteur biométrique (empreinte digitale), par exemple, ne lit pas un identifiant directement : il capture une empreinte qu'il faut comparer à des modèles enregistrés. Il faudrait alors que le lecteur fasse lui-même cette comparaison et renvoie l'identifiant de la personne reconnue, pour respecter le contrat de lireIdentifiant(). Sinon, il faudrait revoir la classe Lecteur, et l'ajout ne serait plus aussi simple.
