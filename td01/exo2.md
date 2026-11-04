Je ne modélise pas les zones d'accès ni les horaires. Ce serait utile dans un vrai système de contrôle d'accès, mais l'énoncé n'en parle pas.

1. Analyse de l'affirmation de l'employé

L'affirmation est fausse, ou du moins non démontrée.

D'abord, les deux badges n'ont pas exactement les mêmes informations : leur identifiant est différent (B1 et B2). L'énoncé dit qu'ils "présentent les mêmes informations dans ce tableau", mais ce n'est vrai que pour les colonnes Titulaire et État.

Ensuite, avoir les mêmes valeurs ne signifie pas être le même objet. Deux objets peuvent être égaux (mêmes valeurs d'attributs) sans être identiques (un seul et même objet). Ici, deux badges physiques existent, donc il y a deux objets, même si leurs valeurs se ressemblent.

Pour conclure, il faudrait connaître le numéro unique de la puce de chaque badge (UID RFID), qui permet de savoir s'il s'agit d'un ou deux badges physiques. Il faudrait aussi les dates d'émission, pour savoir si l'un remplace l'autre, et le matricule du titulaire, pour s'assurer qu'il s'agit bien de la même Alice.

2. Désactivation de B1

B1 et B2 sont deux objets distincts, chacun avec son propre état. Désactiver B1 modifie seulement l'attribut estActif de B1. B2 reste actif, et Alice peut toujours entrer avec.

Ce comportement est normal en programmation objet : modifier une instance n'a aucun effet sur une autre instance, même si leurs valeurs sont identiques. Si B1 et B2 étaient réellement le même objet, la désactivation de B1 désactiverait aussi B2, ce qui montre bien qu'ils ne le sont pas.

Ce point a une conséquence en sécurité. Si B1 est désactivé parce qu'Alice quitte l'entreprise ou parce que son badge a été volé, il faut aussi penser à désactiver B2. Sinon, un accès reste ouvert sans que personne ne s'en rende compte. Le modèle ne le fait pas automatiquement : c'est une règle de gestion à appliquer, par exemple en désactivant tous les badges d'un même titulaire.

3. Classe Badge

Attributs (privés) :

- identifiant : texte, par exemple "B1", unique et non modifiable
- uidPuce : texte, numéro unique gravé dans la puce du badge
- titulaire : référence vers l'employé à qui le badge est attribué
- estActif : booléen
- dateEmission : date

Méthodes :

- activer() : passe estActif à vrai
- desactiver() : passe estActif à faux
- estActif() : indique si le badge est actif
- autoriserAcces() : renvoie vrai si le badge est actif, faux sinon

Les attributs sont privés : on ne peut changer l'état d'un badge que par activer() et desactiver().

Les deux badges de l'énoncé :
B1, instance de Badge : identifiant = "B1", titulaire = Alice, estActif = vrai.
B2, instance de Badge : identifiant = "B2", titulaire = Alice, estActif = vrai.

4. Distinguer les badges

Les badges sont distingués par leur identifiant, et non par leur titulaire ou leur état. Cet identifiant est unique : deux badges ne peuvent pas avoir le même. Il est aussi non modifiable, pour qu'un badge garde la même identité toute sa vie.

Le titulaire et l'état ne peuvent pas servir à distinguer les badges, car plusieurs badges peuvent partager les mêmes valeurs (c'est le cas de B1 et B2), et l'état change au cours du temps.

L'uidPuce fait le lien avec le badge physique. C'est lui que lit le lecteur à l'entrée des locaux, et il permet de vérifier qu'un même badge physique n'a pas été enregistré deux fois.

5. Abstraction

L'abstraction consiste à ne garder du monde réel que ce qui est utile au logiciel. Ici, le logiciel sert à contrôler l'accès aux locaux : il doit savoir quel badge est présenté, à qui il appartient et s'il est autorisé.

Je conserve :

- l'identifiant, pour distinguer les badges dans le système
- l'uidPuce, car c'est ce que le lecteur lit réellement
- le titulaire, pour savoir qui entre
- l'état, pour autoriser ou refuser l'accès
- la date d'émission, pour retrouver le badge le plus récent en cas de remplacement

J'écarte la couleur, la matière, les dimensions, le logo, le fabricant ou le cordon. Ce sont de vraies caractéristiques d'un badge physique, mais elles n'ont aucun rôle dans la décision d'ouvrir ou non une porte. Les ajouter compliquerait le modèle sans rien apporter.

La photo imprimée sur le badge est un cas limite : elle sert à un agent de sécurité pour contrôler visuellement, mais pas au logiciel. Je ne la garde pas, car l'énoncé ne parle que du système.
