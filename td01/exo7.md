Exercice 7 - Une clé USB inconnue

Les hypothèses sont présentées et justifiées à la question 7. Elles sont utilisées dès les premières questions.

1. Analyse du problème

Le problème contient quatre éléments distincts : une clé USB qui se présente avec un identifiant, un ordinateur sur lequel elle est branchée, une liste d'identifiants autorisés, et un journal des refus.

La décision ("accepter ou refuser") ne dépend que de la liste. Elle ne doit pas être prise par l'ordinateur lui-même, sinon chaque poste aurait sa propre règle. Elle est donc confiée à un objet unique, le contrôleur, que tous les ordinateurs consultent.

Le journal doit pouvoir enregistrer des clés qui ne sont dans aucune liste (c'est justement le cas de USB-999). Une entrée du journal stocke donc l'identifiant de la clé sous forme de texte, et non une référence vers une autorisation.

Par défaut, tout ce qui n'est pas explicitement autorisé est refusé. C'est le principe de sécurité "liste blanche" : une clé inconnue, ou dont l'identifiant n'a pas pu être lu, est refusée.

2. Classes

CleUSB
Responsabilité : représenter une clé branchée.
Attribut : identifiant (texte, non modifiable).
Méthode : getIdentifiant().

Ordinateur
Responsabilité : détecter le branchement d'une clé et appliquer la décision.
Attributs : nom, adresseIP, une référence vers le controleur.
Méthodes : signalerBranchement(cle), autoriserCle(cle), bloquerCle(cle).
L'ordinateur ne décide de rien : il demande au contrôleur et exécute la réponse.

ListeAutorisations
Responsabilité : savoir quelles clés sont autorisées.
Attribut (privé) : l'ensemble des identifiants autorisés.
Méthodes : estAutorisee(identifiant), ajouter(identifiant), retirer(identifiant).
La liste n'est jamais exposée directement : on ne peut que poser une question ou la modifier par ses méthodes.

ControleurUSB
Responsabilité : prendre la décision et faire enregistrer les refus.
Attributs : une référence vers la ListeAutorisations et vers le JournalRefus.
Méthode : decider(cle, ordinateur), qui renvoie ACCEPTEE ou REFUSEE.

TentativeRefusee
Responsabilité : représenter un refus.
Attributs : identifiantCle, nomOrdinateur, dateHeure, motif (par exemple "clé inconnue").
Méthodes : lecture seule. Une tentative ne peut pas être modifiée après sa création.

JournalRefus
Responsabilité : conserver les refus et permettre de les consulter.
Attribut (privé) : la liste des tentatives refusées.
Méthodes : enregistrer(tentative), consulter(), consulterParCle(identifiant), consulterParOrdinateur(nom).
Il n'existe aucune méthode pour supprimer ou modifier une entrée : le journal doit servir de preuve en cas d'incident.

3. Interactions lors du branchement d'une clé

1. L'utilisateur branche une clé sur l'ordinateur PC-01. Le système crée un objet CleUSB avec l'identifiant lu sur la clé.
1. PC-01 appelle controleur.decider(cle, PC-01).
1. Le contrôleur appelle liste.estAutorisee(cle.getIdentifiant()).
1. Si la réponse est oui, le contrôleur renvoie ACCEPTEE. PC-01 appelle autoriserCle(cle) et la clé devient utilisable.
1. Si la réponse est non, le contrôleur crée une TentativeRefusee (identifiant de la clé, PC-01, date et heure, motif), appelle journal.enregistrer(tentative), puis renvoie REFUSEE. PC-01 appelle bloquerCle(cle) et la clé reste inaccessible.

Chaque objet ne fait que sa part : l'ordinateur détecte et applique, la liste répond, le contrôleur décide, le journal conserve.

4. Comportement dans plusieurs situations

Situation de l'énoncé, USB-999 branchée sur PC-01 : la liste ne contient que USB-101, USB-205 et USB-310. estAutorisee("USB-999") renvoie non. Le contrôleur enregistre une tentative refusée (USB-999, PC-01, date, "clé inconnue") et renvoie REFUSEE. La clé est bloquée.

USB-205 branchée sur PC-02 : USB-205 est dans la liste, la clé est acceptée. Rien n'est enregistré dans le journal, puisque l'énoncé ne demande d'enregistrer que les refus.

USB-999 branchée ensuite sur PC-03 : nouveau refus, et nouvelle entrée dans le journal. En consultant consulterParCle("USB-999"), un administrateur voit que la même clé a été essayée sur plusieurs postes. C'est un signal d'alerte : quelqu'un cherche peut-être un ordinateur moins protégé.

L'administrateur ajoute USB-999 à la liste, puis la clé est rebranchée : elle est acceptée. Les refus précédents restent dans le journal, car l'historique ne se réécrit pas.

Clé dont l'identifiant ne peut pas être lu : la clé est refusée avec le motif "identifiant illisible". Selon le principe de liste blanche, ce qui ne peut pas être vérifié est refusé.

5. Suspension temporaire

Le besoin : une clé peut rester dans la liste tout en étant temporairement refusée. Un simple identifiant dans la liste ne suffit plus, car il faut aussi connaître l'état de l'autorisation et sa date de fin de suspension.

J'ajoute donc une classe Autorisation.

Autorisation
Responsabilité : représenter l'autorisation d'une clé et son état.
Attributs (privés) : identifiantCle, estSuspendue, dateFinSuspension.
Méthodes : suspendre(dateFin), reprendre(), estValide(dateHeure).
estValide renvoie vrai si l'autorisation n'est pas suspendue, ou si la date de fin de suspension est dépassée. Une suspension prend donc fin automatiquement, sans intervention de l'administrateur. reprendre() permet de lever la suspension plus tôt.

ListeAutorisations contient désormais des objets Autorisation au lieu de simples identifiants.
estAutorisee(identifiant) cherche l'autorisation correspondante. Si elle n'existe pas, la réponse est non (motif "clé inconnue"). Si elle existe mais n'est pas valide, la réponse est non (motif "autorisation suspendue"). Sinon, la réponse est oui.
Deux méthodes sont ajoutées : suspendre(identifiant, dateFin) et reprendre(identifiant).

Pour que le journal distingue les deux cas de refus, estAutorisee renvoie un résultat parmi trois : AUTORISEE, INCONNUE ou SUSPENDUE.

Exemple : USB-310 est suspendue jusqu'au 15 novembre. Le 10 novembre, elle est branchée sur PC-01 : refus, motif "autorisation suspendue", enregistré dans le journal. Le 16 novembre, la suspension est terminée : la clé est acceptée.

Ce qui ne change pas : CleUSB, Ordinateur, TentativeRefusee et JournalRefus restent identiques. ControleurUSB ne change que pour transmettre le motif au journal. Comme la liste était encapsulée, les autres classes ne savaient pas qu'elle contenait de simples textes, et le changement de structure interne ne les touche pas.

6. Principes de la programmation orientée objet mobilisés

Abstraction : une clé USB réelle a une marque, une capacité, un contenu, une couleur. Le modèle ne garde que son identifiant, seule information utile pour décider. De même, un ordinateur n'est représenté que par son nom, son adresse IP et sa capacité à autoriser ou bloquer une clé.

Encapsulation : la liste des autorisations est privée et ne peut être consultée qu'avec estAutorisee(). Personne ne peut y ajouter une clé sans passer par ajouter(). Le journal ne peut pas être modifié après coup. Une autorisation ne peut être suspendue ou rétablie que par ses propres méthodes. La question 5 montre l'intérêt : la structure interne de la liste a changé sans effet sur les classes qui l'utilisent.

Répartition des responsabilités : chaque classe a un rôle unique (détecter, décider, savoir, conserver). La règle de décision est écrite une seule fois, dans le contrôleur, et appliquée de la même façon sur tous les ordinateurs.

Héritage et polymorphisme : je ne les utilise pas. Le problème ne contient aucune famille d'objets avec des variantes de comportement : il n'y a qu'un seul type de clé et un seul type d'autorisation. Ajouter une hiérarchie de classes compliquerait le modèle sans rien apporter. Ils deviendraient utiles si l'entreprise voulait plusieurs types de règles (par exemple une autorisation valable seulement sur certains postes ou à certaines heures) : on pourrait alors créer une classe abstraite Autorisation avec plusieurs variantes de estValide().

7. Hypothèses et leur influence

L'identifiant est lu sur la clé et considéré comme fiable. En réalité, l'identifiant d'une clé USB (numéro de série) peut être falsifié par une clé malveillante. Je l'accepte comme hypothèse car l'énoncé ne propose pas d'autre moyen d'identification. Influence : la sécurité du système dépend entièrement de cet identifiant. Dans un vrai déploiement, il faudrait compléter par d'autres contrôles, par exemple chiffrer les clés autorisées.

La liste des clés autorisées est commune à toute l'entreprise, et non propre à chaque ordinateur. L'énoncé parle d'"une liste". Influence : il n'y a qu'un seul objet ListeAutorisations, partagé par tous les ordinateurs à travers le contrôleur.

Tout ce qui n'est pas autorisé est refusé (liste blanche), y compris une clé dont l'identifiant est illisible. Influence : la méthode decider renvoie REFUSEE par défaut, et un motif "identifiant illisible" existe.

Seuls les refus sont enregistrés, comme le demande l'énoncé. Influence : il n'existe qu'un JournalRefus, et non un journal de tous les branchements. C'est discutable en sécurité, car enregistrer aussi les clés acceptées permettrait de savoir qui a utilisé quelle clé en cas de fuite de données. Le journal pourrait facilement être étendu en ce sens.

L'ordinateur sur lequel la clé est branchée est enregistré dans le journal. L'énoncé ne le demande pas explicitement, mais un refus sans le poste concerné serait peu exploitable. Influence : decider reçoit l'ordinateur en paramètre, et TentativeRefusee contient nomOrdinateur.

Une suspension a une date de fin, après laquelle elle s'arrête automatiquement. L'énoncé dit seulement "temporairement". Influence : Autorisation contient dateFinSuspension, et estValide reçoit la date courante. Si on avait choisi une suspension levée seulement à la main, un simple booléen aurait suffi.

Seul un administrateur peut modifier la liste ou suspendre une autorisation. Influence : les méthodes ajouter, retirer, suspendre et reprendre ne sont appelées que par le module d'administration, jamais par les ordinateurs.
