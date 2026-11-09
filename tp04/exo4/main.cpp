// TP04 - Exercice 4
// Compilation : g++ -std=c++20 -Wall -Wextra -pedantic main.cpp -o main

#include <iostream>
#include <stdexcept>

#include "compte.hpp"

int main() {
    // ========================================
    // QUESTION 1 - PREMIERE CLASSE
    // ========================================
    std::cout << "=== Question 1 ===\n";

    Compte alice("C001", "Alice", 100);
    Compte bob("C002", "Bob", 50);
    alice.afficher();
    bob.afficher();

    // ========================================
    // QUESTION 2 - COPIES ET REFERENCES
    // ========================================
    std::cout << "\n=== Question 2 ===\n";

    Compte copie = alice;   // nouvel objet, copie de tous les attributs
    copie.deposer(50);
    std::cout << "Copie    : "; copie.afficher();
    std::cout << "Original : "; alice.afficher();   // toujours 100
    std::cout << "&copie == &alice : " << std::boolalpha
              << (&copie == &alice) << '\n';

    Compte& ref = alice;    // autre nom pour le MEME objet
    ref.deposer(10);
    std::cout << "Reference : "; ref.afficher();
    std::cout << "Original  : "; alice.afficher();  // 110
    std::cout << "&ref == &alice : " << (&ref == &alice) << '\n';

    // En C++, Compte b = a; cree un objet distinct (contrairement a Java ou
    // c'est la reference qui est copiee). Pour partager le meme objet, il faut
    // une reference explicite : Compte& r = a;

    // ========================================
    // QUESTION 3 - ENCAPSULATION
    // ========================================
    std::cout << "\n=== Question 3 ===\n";

    Compte c("C003", "Charlie", 100);
    std::cout << "Identifiant : " << c.getIdentifiant()
              << ", proprietaire : " << c.getProprietaire()
              << ", solde : " << c.getSolde()
              << ", actif : " << c.estActif() << '\n';

    c.deposer(20);  c.afficher();   // 120
    c.retirer(30);  c.afficher();   // 90
    c.retirer(200); c.afficher();   // refuse, 90
    c.deposer(-10); c.afficher();   // refuse, 90
    c.retirer(0);   c.afficher();   // refuse, 90

    c.desactiver();
    c.deposer(20);  c.afficher();   // refuse, 90 (inactif)
    c.retirer(20);  c.afficher();   // refuse, 90 (inactif)

    // c.solde = -50;           // ne compile pas : solde est prive
    // copie.identifiant = "X"; // ne compile pas : prive et const

    try {
        Compte invalide("", "Zoe", 10);
    } catch (const std::invalid_argument& e) {
        std::cout << "Creation refusee : " << e.what() << '\n';
    }
    try {
        Compte invalide("C009", "", 10);
    } catch (const std::invalid_argument& e) {
        std::cout << "Creation refusee : " << e.what() << '\n';
    }
    try {
        Compte invalide("C009", "Zoe", -20);
    } catch (const std::invalid_argument& e) {
        std::cout << "Creation refusee : " << e.what() << '\n';
    }

    // ========================================
    // QUESTION 4 - INTERACTION ENTRE DEUX COMPTES
    // ========================================
    std::cout << "\n=== Question 4 ===\n";

    Compte a("C001", "Alice", 100);
    Compte b("C002", "Bob", 50);
    const int somme = a.getSolde() + b.getSolde();

    std::cout << "-- Alice -> Bob : 30\n";
    a.transferer(b, 30);
    a.afficher(); b.afficher();
    std::cout << "Somme conservee : " << (a.getSolde() + b.getSolde() == somme) << '\n';

    std::cout << "-- Bob -> Alice : 20\n";
    b.transferer(a, 20);
    a.afficher(); b.afficher();
    std::cout << "Somme conservee : " << (a.getSolde() + b.getSolde() == somme) << '\n';

    std::cout << "-- Alice -> Bob : 500\n";
    const int avantA = a.getSolde();
    const int avantB = b.getSolde();
    a.transferer(b, 500);
    a.afficher(); b.afficher();
    std::cout << "Aucun compte modifie : "
              << (a.getSolde() == avantA && b.getSolde() == avantB) << '\n';

    std::cout << "-- Cas refuses supplementaires\n";
    a.transferer(a, 10);    // meme compte
    a.transferer(b, -5);    // montant invalide
    b.desactiver();
    a.transferer(b, 10);    // destination inactive
    a.afficher(); b.afficher();

    return 0;
}
