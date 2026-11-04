Exercice 5 - Une tentative de connexion

Hypothèses

Je reprends la politique de l'exercice 4 : un compte est bloqué après trois échecs consécutifs, et une connexion réussie remet le compteur à zéro. L'énoncé de l'exercice 5 ne dit pas si un mot de passe incorrect compte pour un éventuel blocage, mais c'est cohérent avec le reste du TD.

Sur un compte bloqué, la connexion est refusée même si le mot de passe est correct, et le compteur ne change pas.

La trace d'une tentative contient l'identifiant saisi, la date et l'heure, l'adresse IP et le résultat. Elle ne contient jamais le mot de passe saisi, même faux : un mot de passe faux est souvent un mot de passe presque juste, ou le mot de passe d'un autre service.

Quel que soit le motif de l'échec, Alice reçoit le même message ("identifiant ou mot de passe incorrect"). Le motif précis n'apparaît que dans l'historique. Sinon, un attaquant pourrait savoir quels identifiants existent ou quels comptes sont bloqués.

1. Objets intervenant

- serviceAuth, instance de ServiceAuthentification : reçoit la demande et coordonne le traitement
- gestionnaire, instance de GestionnaireComptes : retrouve un compte à partir de son identifiant
- compteAlice, instance de Compte : vérifie le mot de passe et applique la politique de blocage
- tentative, instance de TentativeConnexion : représente la trace d'une tentative
- historique, instance de HistoriqueConnexions : conserve toutes les tentatives

Alice n'est pas un objet du logiciel : c'est l'utilisatrice qui déclenche la demande depuis l'application.

La méthode seConnecter(motDePasse) de Compte, reprise de l'exercice 4, renvoie un résultat parmi trois valeurs : SUCCES, MOT_DE_PASSE_INCORRECT ou COMPTE_BLOQUE. Le service peut ainsi connaître le motif sans avoir accès aux données internes du compte.

2. Connexion réussie

1. Alice saisit "alice" et son mot de passe. L'application appelle serviceAuth.connecter("alice", motDePasse, adresseIP).
   Responsabilité du service : prendre en charge la demande et organiser les étapes.

1. Le service appelle gestionnaire.trouverCompte("alice"), qui renvoie compteAlice.
   Responsabilité du gestionnaire : savoir quels comptes existent.

1. Le service appelle compteAlice.seConnecter(motDePasse). Le compte vérifie qu'il n'est pas bloqué, compare l'empreinte du mot de passe saisi à celle qu'il stocke, remet son compteur d'échecs à 0 et renvoie SUCCES.
   Responsabilité du compte : vérifier le mot de passe et faire respecter ses propres règles. Lui seul connaît l'empreinte et son état.

1. Le service crée une TentativeConnexion avec "alice", la date et l'heure, l'adresse IP et le résultat SUCCES.
   Responsabilité de la tentative : décrire ce qui s'est passé, sans pouvoir être modifiée ensuite.

1. Le service appelle historique.enregistrer(tentative).
   Responsabilité de l'historique : conserver la trace.

1. Le service renvoie "connexion autorisée" à l'application, qui donne accès au service.

1. Autres situations

Mot de passe incorrect : aux étapes 1 et 2, rien ne change. À l'étape 3, le compte constate que l'empreinte ne correspond pas. Il augmente son compteur d'échecs, se bloque lui-même si le compteur atteint 3, puis renvoie MOT_DE_PASSE_INCORRECT. Le service enregistre une tentative échouée avec ce motif et renvoie "connexion refusée". Le service n'a rien à décider sur le blocage : c'est le compte qui l'applique.

Compte bloqué : à l'étape 3, le compte vérifie son état avant même de regarder le mot de passe. Il renvoie COMPTE_BLOQUE sans modifier son compteur. Le service enregistre une tentative échouée avec ce motif et refuse la connexion. Vérifier le blocage en premier garantit qu'un compte bloqué ne permet pas de deviner le mot de passe, même en essayant à l'infini.

Identifiant inconnu : à l'étape 2, le gestionnaire ne trouve aucun compte et renvoie "aucun compte". Le service s'arrête là : il n'y a aucun compte à interroger. Il crée quand même une tentative échouée avec le motif "identifiant inconnu" et l'identifiant saisi, l'enregistre dans l'historique et refuse la connexion. Cette trace est importante en sécurité : de nombreuses tentatives sur des identifiants inexistants peuvent révéler qu'un attaquant teste des noms au hasard.

Dans les trois cas, une tentative est enregistrée, et Alice reçoit le même message d'échec.

4. Schéma des interactions (connexion réussie)

```
Application     serviceAuth      gestionnaire     compteAlice      historique
    |                |                |                |                |
    | connecter("alice", mdp, ip)     |                |                |
    |--------------->|                |                |                |
    |                | trouverCompte("alice")          |                |
    |                |--------------->|                |                |
    |                |  compteAlice   |                |                |
    |                |<---------------|                |                |
    |                | seConnecter(mdp)                |                |
    |                |-------------------------------->|                |
    |                |                |  vérifie, compteur = 0          |
    |                |            SUCCES               |                |
    |                |<--------------------------------|                |
    |                | crée tentative ("alice", date, ip, SUCCES)       |
    |                | enregistrer(tentative)          |                |
    |                |------------------------------------------------->|
    | autorisée      |                |                |                |
    |<---------------|                |                |                |
```

Variantes :

- mot de passe incorrect : seConnecter renvoie MOT_DE_PASSE_INCORRECT, la tentative est enregistrée comme échouée, l'application reçoit "refusée"
- compte bloqué : seConnecter renvoie COMPTE_BLOQUE, même suite
- identifiant inconnu : trouverCompte renvoie "aucun compte", seConnecter n'est jamais appelé, la tentative est enregistrée avec le motif "identifiant inconnu"

5. Ce que chaque objet doit connaître

serviceAuth connaît le gestionnaire et l'historique, pour pouvoir les appeler. Il ne connaît ni les empreintes de mots de passe, ni les règles de blocage : il n'en a pas besoin pour coordonner, et moins il en sait, moins il peut divulguer.

gestionnaire connaît la liste des comptes et leurs identifiants, pour en retrouver un. Il n'a pas besoin de vérifier les mots de passe, il ne fait que trouver le bon objet.

compteAlice connaît son identifiant, l'empreinte de son mot de passe, son état (bloqué ou non) et son nombre d'échecs. Ce sont exactement les informations nécessaires pour vérifier un mot de passe et appliquer la règle des trois échecs. Il ne connaît pas l'historique : enregistrer les traces n'est pas son rôle.

tentative connaît l'identifiant saisi, la date et l'heure, l'adresse IP et le résultat. Elle stocke l'identifiant saisi sous forme de texte, et non une référence vers un compte, car elle doit pouvoir exister même quand l'identifiant ne correspond à aucun compte. Elle ne connaît pas le mot de passe.

historique connaît uniquement la liste des tentatives. Il ne connaît pas les comptes : il conserve des traces, il ne décide de rien.

Le mot de passe saisi par Alice ne fait que transiter : il passe de l'application au service, puis au compte, qui le compare et l'oublie. Aucun objet ne le conserve.
