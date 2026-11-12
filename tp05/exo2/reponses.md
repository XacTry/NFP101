# TP05 — Exercice 2 : Héritage et encapsulation

## Question 1 — Observer le comportement

Version du développeur (`solde` en `protected`, prime ajoutée par `solde += prime`).

**Alice** (`new CompteFidelite("Alice", 100)`, puis `deposer(50)`) :

```
155
```

Le résultat est celui attendu : 100 + 50 + prime de 5 (10 % de 50) = 155.

**Bob** (`new CompteFidelite("Bob", Integer.MAX_VALUE - 100)`, puis `deposer(100)`) :

```
-2147483639
```

### Que constatez-vous ?

Le solde est devenu **négatif**, ce qui viole deux règles de l'exercice 1 :
- un dépôt ne peut pas faire dépasser `Integer.MAX_VALUE` ;
- le solde ne doit jamais être négatif.

Le dépôt de 100 est accepté et porte le solde à `Integer.MAX_VALUE` (2147483647).
La prime de 10 est ensuite ajoutée sans aucune vérification. Le calcul dépasse
la capacité d'un `int`, qui « repart » à `Integer.MIN_VALUE` (overflow) :
2147483647 + 10 donne -2147483648 + 9 = **-2147483639**.

### Quelle instruction provoque ce comportement ?

```java
solde += prime;
```

### Pourquoi les vérifications de `Compte.deposer()` ne suffisent-elles pas ?

`Compte.deposer()` ne vérifie que **le montant qu'on lui passe** (ici 100), et ce
dépôt est valide. La prime, elle, n'est pas ajoutée par `deposer()` : la classe
fille modifie directement l'attribut `solde`, ce que permet la visibilité `protected`.
Elle contourne donc toutes les règles de `Compte`. Une vérification ne protège
un attribut que si tout le monde est **obligé** de passer par elle.

## Question 2 — Corriger l'encapsulation

### Que constatez-vous ?

Avec `private int solde;` dans `Compte`, `CompteFidelite` ne compile plus :

```
/src/CompteFidelite.java:14: error: solde has private access in Compte
        solde += prime;
        ^
1 error
```

### Pourquoi ?

- `protected` : l'attribut est accessible dans la classe, dans ses classes filles
  et dans le même package.
- `private` : l'attribut n'est accessible **que dans la classe `Compte` elle-même**.
  Une classe fille hérite bien de l'attribut (chaque `CompteFidelite` a un solde),
  mais elle ne peut pas le lire ni le modifier directement.

`CompteLimite` compile toujours : elle ne touche jamais `solde` directement et passe
par `super.retirer()`.

### Correction

```java
@Override
public boolean deposer(int montant) {
    if (!super.deposer(montant)) {
        return false;               // dépôt refusé : aucune prime
    }

    int prime = montant / 10;
    super.deposer(prime);           // si la prime est refusée, le dépôt reste accepté

    return true;
}
```

- La prime passe par `Compte.deposer()` et respecte donc les mêmes règles que le dépôt.
- On appelle `super.deposer(prime)` et non `deposer(prime)`. Ce dernier appellerait la
  version redéfinie de `CompteFidelite`, qui calculerait une prime sur la prime.
  Par exemple, un dépôt de 100 rapporterait 10 + 1 = 11 crédits de prime au lieu de 10.
- Le résultat de `super.deposer(prime)` est ignoré : si la prime ne peut pas être
  ajoutée, le dépôt initial reste accepté et la méthode retourne `true`.

## Question 3 — Vérifier la correction

Nouvelle exécution des deux programmes :

```
Alice : 155
Bob   : 2147483647
```

- **Alice** : dépôt + prime correctement ajoutés (155).
- **Bob** : le dépôt de 100 est accepté. La prime de 10 ferait dépasser
  `Integer.MAX_VALUE`, elle est refusée par `Compte.deposer()` et ignorée.
  Le solde reste à `Integer.MAX_VALUE`.
- **Dépôt refusé** : `deposer(0)`, `deposer(-50)` ou un dépôt sur un compte plein
  retournent `false` et le solde ne bouge pas, donc pas de prime.
- **`CompteLimite`** fonctionne toujours avec `solde` en `private`.

Cas particuliers :
- Un dépôt de 1 à 9 crédits ne donne pas de prime : `montant / 10` vaut 0 (division
  entière), et `super.deposer(0)` est refusé.
- Si la prime ne rentre qu'en partie (par exemple 5 crédits de place pour une prime
  de 10), elle est entièrement ignorée : on n'ajoute pas de prime partielle.

`description()` redéfinie avec `super.description()` :

```
Compte Alice : 155 credits | compte fidelite
```

Tests (`TestCompteFidelite`) : **24 réussis, 0 échec**.
