
// TP04 - Exercice 4 : tests progressifs sans framework
// Compilation : g++ -std=c++20 -Wall -Wextra -pedantic TestCompte.cpp -o TestCompte
// Execution   : ./test_compte

#include <concepts>
#include <iostream>
#include <sstream>
#include <string>
#include <type_traits>
#include <utility>

#if __has_include("compte.hpp")
#include "compte.hpp"
#elif __has_include("compte.h")
#include "compte.h"
#endif

class Compte;

namespace test {

int ok = 0;
int echec = 0;
int absent = 0;

void section(const std::string& titre) {
    std::cout << "\n=== " << titre << " ===\n";
}

void verifier(const std::string& nom, bool resultat) {
    std::cout << (resultat ? "[OK] " : "[ECHEC] ")
              << nom << '\n';

    if (resultat) {
        ++ok;
    } else {
        ++echec;
    }
}

void non_implemente(const std::string& nom) {
    std::cout << "[NON IMPLEMENTE] " << nom << '\n';
    ++absent;
}

void bilan() {
    section("Bilan");

    std::cout << "Reussis : " << ok
              << " | Echecs : " << echec
              << " | Non implementes : " << absent
              << '\n';
}


// ========================================
// DETECTION DES FONCTIONNALITES
// ========================================

template<class T, class = void>
struct est_complete : std::false_type {};

template<class T>
struct est_complete<T, std::void_t<decltype(sizeof(T))>>
    : std::true_type {};

template<class T>
constexpr bool constructeur =
    requires { T("C001", "Alice", 100); };

template<class T>
constexpr bool identifiant =
    requires(const T& c) {
        { c.getIdentifiant() } -> std::convertible_to<std::string>;
    };

template<class T>
constexpr bool proprietaire =
    requires(const T& c) {
        { c.getProprietaire() } -> std::convertible_to<std::string>;
    };

template<class T>
constexpr bool solde =
    requires(const T& c) {
        { c.getSolde() } -> std::convertible_to<int>;
    };

template<class T>
constexpr bool actif =
    requires(const T& c) {
        { c.estActif() } -> std::convertible_to<bool>;
    };

template<class T>
constexpr bool afficher =
    requires(const T& c) {
        c.afficher();
    };

template<class T>
constexpr bool deposer =
    requires(T& c) {
        c.deposer(1);
    };

template<class T>
constexpr bool retirer =
    requires(T& c) {
        c.retirer(1);
    };

template<class T>
constexpr bool desactiver =
    requires(T& c) {
        c.desactiver();
    };

template<class T>
constexpr bool transferer =
    requires(T& a, T& b) {
        a.transferer(b, 1);
    };


// ========================================
// QUESTION 1 - PREMIERE CLASSE
// ========================================

template<class T>
void question1() {
    section("Question 1 - Premiere classe");

    if constexpr (!constructeur<T>) {
        non_implemente(
            "Constructeur Compte(identifiant, proprietaire, solde)"
        );
    } else {
        try {
            T alice("C001", "Alice", 100);
            T bob("C002", "Bob", 50);

            verifier(
                "Creation des comptes Alice et Bob",
                true
            );

            if constexpr (afficher<T>) {
                std::ostringstream sortie;
                auto* ancien = std::cout.rdbuf(sortie.rdbuf());

                try {
                    alice.afficher();
                    bob.afficher();

                    std::cout.rdbuf(ancien);

                    const auto texte = sortie.str();

                    verifier(
                        "afficher() produit les informations des comptes",
                        texte.find("C001") != std::string::npos &&
                        texte.find("Alice") != std::string::npos &&
                        texte.find("C002") != std::string::npos &&
                        texte.find("Bob") != std::string::npos &&
                        texte.find("100") != std::string::npos &&
                        texte.find("50") != std::string::npos
                    );
                } catch (...) {
                    std::cout.rdbuf(ancien);

                    verifier(
                        "afficher() ne leve pas d'exception",
                        false
                    );
                }
            } else {
                non_implemente("Methode afficher() const");
            }
        } catch (...) {
            verifier(
                "Creation des comptes Alice et Bob",
                false
            );
        }
    }
}


// ========================================
// QUESTION 2 - COPIES ET REFERENCES
// ========================================

template<class T>
void question2() {
    section("Question 2 - Copies et references");

    if constexpr (!constructeur<T>) {
        non_implemente("Constructeur necessaire aux copies");
        return;
    } else if constexpr (!std::is_copy_constructible_v<T>) {
        non_implemente("Constructeur de copie");
    } else {
        try {
            T original("C001", "Alice", 100);
            T copie = original;

            verifier(
                "La copie est un objet distinct",
                &copie != &original
            );

            T& reference = original;

            verifier(
                "La reference designe l'objet original",
                &reference == &original
            );

            if constexpr (deposer<T> && solde<T>) {
                copie.deposer(20);

                verifier(
                    "Modifier la copie ne change pas l'original",
                    copie.getSolde() == 120 &&
                    original.getSolde() == 100
                );

                reference.deposer(10);

                verifier(
                    "Modifier par reference change l'original",
                    original.getSolde() == 110
                );
            } else {
                non_implemente(
                    "Verification des soldes de la copie "
                    "et de la reference (getSolde, deposer)"
                );
            }
        } catch (...) {
            verifier(
                "Operations de copie et de reference",
                false
            );
        }
    }
}


// ========================================
// QUESTION 3 - ENCAPSULATION
// ========================================

template<class T>
void question3() {
    section("Question 3 - Encapsulation et invariants");

    if constexpr (identifiant<T>) {
        verifier("Accesseur getIdentifiant()", true);
    } else {
        non_implemente("Accesseur getIdentifiant()");
    }

    if constexpr (proprietaire<T>) {
        verifier("Accesseur getProprietaire()", true);
    } else {
        non_implemente("Accesseur getProprietaire()");
    }

    if constexpr (solde<T>) {
        verifier("Accesseur getSolde()", true);
    } else {
        non_implemente("Accesseur getSolde()");
    }

    if constexpr (actif<T>) {
        verifier("Accesseur estActif()", true);
    } else {
        non_implemente("Accesseur estActif()");
    }

    if constexpr (deposer<T>) {
        verifier("Methode deposer(int)", true);
    } else {
        non_implemente("Methode deposer(int)");
    }

    if constexpr (retirer<T>) {
        verifier("Methode retirer(int)", true);
    } else {
        non_implemente("Methode retirer(int)");
    }

    if constexpr (desactiver<T>) {
        verifier("Methode desactiver()", true);
    } else {
        non_implemente("Methode desactiver()");
    }

    // Verification du constructeur

    if constexpr (
        constructeur<T> &&
        identifiant<T> &&
        proprietaire<T> &&
        solde<T> &&
        actif<T>
    ) {
        try {
            T c("C001", "Alice", 100);

            verifier(
                "Identifiant initialise",
                c.getIdentifiant() == "C001"
            );

            verifier(
                "Proprietaire initialise",
                c.getProprietaire() == "Alice"
            );

            verifier(
                "Solde initialise",
                c.getSolde() == 100
            );

            verifier(
                "Compte initialement actif",
                c.estActif()
            );
        } catch (...) {
            verifier(
                "Initialisation des invariants",
                false
            );
        }
    }

    // Solde negatif interdit

    if constexpr (constructeur<T> && solde<T>) {
        try {
            T c("C003", "Charlie", -20);

            verifier(
                "Le constructeur ne conserve pas un solde negatif",
                c.getSolde() >= 0
            );
        } catch (...) {
            verifier(
                "Le constructeur refuse un solde negatif",
                true
            );
        }
    }

    // Identifiant vide interdit

    if constexpr (constructeur<T> && identifiant<T>) {
        try {
            T c("", "Alice", 100);

            verifier(
                "Le constructeur ne conserve pas un identifiant vide",
                !c.getIdentifiant().empty()
            );
        } catch (...) {
            verifier(
                "Le constructeur refuse un identifiant vide",
                true
            );
        }
    }

    // Proprietaire vide interdit

    if constexpr (constructeur<T> && proprietaire<T>) {
        try {
            T c("C001", "", 100);

            verifier(
                "Le constructeur ne conserve pas un proprietaire vide",
                !c.getProprietaire().empty()
            );
        } catch (...) {
            verifier(
                "Le constructeur refuse un proprietaire vide",
                true
            );
        }
    }

    // Depots et retraits

    if constexpr (
        constructeur<T> &&
        solde<T> &&
        deposer<T> &&
        retirer<T>
    ) {
        try {
            T c("C001", "Alice", 100);

            c.deposer(20);

            verifier(
                "Depot de 20 : solde 120",
                c.getSolde() == 120
            );

            c.retirer(30);

            verifier(
                "Retrait de 30 : solde 90",
                c.getSolde() == 90
            );

            const int avant = c.getSolde();

            c.retirer(200);

            verifier(
                "Retrait excessif refuse",
                c.getSolde() == avant
            );

            c.deposer(-10);

            verifier(
                "Depot negatif refuse",
                c.getSolde() == avant
            );

            c.deposer(0);

            verifier(
                "Depot nul refuse",
                c.getSolde() == avant
            );

            c.retirer(-10);

            verifier(
                "Retrait negatif refuse",
                c.getSolde() == avant
            );

            c.retirer(0);

            verifier(
                "Retrait nul refuse",
                c.getSolde() == avant
            );
        } catch (...) {
            verifier(
                "Operations de depot et retrait sans exception",
                false
            );
        }
    }

    // Desactivation

    if constexpr (
        constructeur<T> &&
        solde<T> &&
        actif<T> &&
        deposer<T> &&
        retirer<T> &&
        desactiver<T>
    ) {
        try {
            T c("C001", "Alice", 100);

            c.desactiver();

            verifier(
                "Compte desactive",
                !c.estActif()
            );

            c.deposer(20);

            verifier(
                "Depot interdit sur compte inactif",
                c.getSolde() == 100
            );

            c.retirer(20);

            verifier(
                "Retrait interdit sur compte inactif",
                c.getSolde() == 100
            );
        } catch (...) {
            verifier(
                "Operations sur compte inactif sans exception",
                false
            );
        }
    }
}


// ========================================
// QUESTION 4 - TRANSFERTS
// ========================================

template<class T>
void question4() {
    section("Question 4 - Transferts entre comptes");

    if constexpr (!transferer<T>) {
        non_implemente(
            "Methode transferer(Compte&, int)"
        );
        return;
    } else if constexpr (
        !(constructeur<T> && solde<T> && desactiver<T>)
    ) {
        non_implemente(
            "Prerequis des tests de transfert "
            "(constructeur, getSolde, desactiver)"
        );
    } else {
        try {
            T alice("C001", "Alice", 100);
            T bob("C002", "Bob", 50);

            // Premier transfert

            alice.transferer(bob, 30);

            verifier(
                "Alice transfere 30 a Bob",
                alice.getSolde() == 70 &&
                bob.getSolde() == 80
            );

            verifier(
                "Somme conservee apres le premier transfert",
                alice.getSolde() + bob.getSolde() == 150
            );

            // Deuxieme transfert

            bob.transferer(alice, 20);

            verifier(
                "Bob transfere 20 a Alice",
                alice.getSolde() == 90 &&
                bob.getSolde() == 60
            );

            verifier(
                "Somme conservee apres le second transfert",
                alice.getSolde() + bob.getSolde() == 150
            );

            // Transfert excessif

            int a = alice.getSolde();
            int b = bob.getSolde();

            alice.transferer(bob, 500);

            verifier(
                "Transfert excessif refuse sans modification",
                alice.getSolde() == a &&
                bob.getSolde() == b
            );

            // Montant nul

            alice.transferer(bob, 0);

            verifier(
                "Transfert nul refuse sans modification",
                alice.getSolde() == a &&
                bob.getSolde() == b
            );

            // Montant negatif

            alice.transferer(bob, -10);

            verifier(
                "Transfert negatif refuse sans modification",
                alice.getSolde() == a &&
                bob.getSolde() == b
            );

            // Transfert vers soi-meme

            alice.transferer(alice, 10);

            verifier(
                "Transfert vers soi-meme refuse",
                alice.getSolde() == a &&
                bob.getSolde() == b
            );

            // Destination inactive

            bob.desactiver();
            alice.transferer(bob, 10);

            verifier(
                "Transfert vers compte inactif refuse",
                alice.getSolde() == a &&
                bob.getSolde() == b
            );

            // Source inactive

            T charlie("C003", "Charlie", 40);
            charlie.desactiver();
            charlie.transferer(alice, 10);

            verifier(
                "Transfert depuis compte inactif refuse",
                charlie.getSolde() == 40 &&
                alice.getSolde() == a
            );
        } catch (...) {
            verifier(
                "Execution des transferts sans exception",
                false
            );
        }
    }
}

} // namespace test


// ========================================
// PROGRAMME PRINCIPAL
// ========================================

int main() {
    std::cout << "TP04 - EXERCICE 4 - TEST COMPTE\n";

    if constexpr (!test::est_complete<Compte>::value) {
        test::non_implemente(
            "Classe Compte dans compte.hpp"
        );
    } else {
        []<class T>() {
            test::question1<T>();
            test::question2<T>();
            test::question3<T>();
            test::question4<T>();
        }.template operator()<Compte>();
    }

    test::bilan();
}
