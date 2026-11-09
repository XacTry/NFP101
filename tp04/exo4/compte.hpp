#ifndef COMPTE_HPP
#define COMPTE_HPP

#include <iostream>
#include <stdexcept>
#include <string>

// Invariants d'un Compte valide :
//  - identifiant non vide, et jamais modifie apres la construction ;
//  - proprietaire non vide ;
//  - solde >= 0 ;
//  - actif ou inactif (un compte inactif n'effectue plus d'operations).
// Le constructeur etablit ces invariants, les methodes les preservent.
class Compte {
private:
    // Question 3 : const -> l'identifiant ne peut plus changer apres construction.
    const std::string identifiant;
    std::string proprietaire;
    int solde;
    bool actif;

public:
    // Questions 1 et 3 : le constructeur refuse un objet invalide
    // en levant une exception (l'objet n'est alors jamais cree).
    Compte(const std::string& identifiant,
           const std::string& proprietaire,
           int solde)
        : identifiant(identifiant),
          proprietaire(proprietaire),
          solde(solde),
          actif(true) {
        if (identifiant.empty()) {
            throw std::invalid_argument("L'identifiant ne doit pas etre vide");
        }
        if (proprietaire.empty()) {
            throw std::invalid_argument("Le proprietaire ne doit pas etre vide");
        }
        if (solde < 0) {
            throw std::invalid_argument("Le solde initial doit etre positif ou nul");
        }
    }

    // Question 3 : accesseurs en lecture (const : ne modifient pas l'objet)
    const std::string& getIdentifiant() const { return identifiant; }
    const std::string& getProprietaire() const { return proprietaire; }
    int getSolde() const { return solde; }
    bool estActif() const { return actif; }

    // Question 1
    void afficher() const {
        std::cout << identifiant << " - " << proprietaire << " - "
                  << solde << " credits"
                  << (actif ? "" : " (inactif)") << '\n';
    }

    // Question 3 : une operation refusee affiche un message,
    // retourne false et ne modifie pas le compte.
    bool deposer(int montant) {
        if (!actif) {
            std::cout << "Depot refuse : compte " << identifiant << " inactif\n";
            return false;
        }
        if (montant <= 0) {
            std::cout << "Depot refuse : montant invalide (" << montant << ")\n";
            return false;
        }
        solde += montant;
        return true;
    }

    bool retirer(int montant) {
        if (!actif) {
            std::cout << "Retrait refuse : compte " << identifiant << " inactif\n";
            return false;
        }
        if (montant <= 0) {
            std::cout << "Retrait refuse : montant invalide (" << montant << ")\n";
            return false;
        }
        if (montant > solde) {
            std::cout << "Retrait refuse : solde insuffisant ("
                      << montant << " > " << solde << ")\n";
            return false;
        }
        solde -= montant;
        return true;
    }

    void desactiver() {
        actif = false;
    }

    // Question 4 : transfert depuis ce compte vers destination.
    // destination est une reference : c'est le compte existant qui est modifie,
    // pas une copie. Toutes les conditions sont verifiees AVANT de modifier
    // quoi que ce soit, donc un transfert refuse ne change aucun compte.
    bool transferer(Compte& destination, int montant) {
        if (this == &destination) {
            std::cout << "Transfert refuse : source et destination identiques\n";
            return false;
        }
        if (!actif || !destination.actif) {
            std::cout << "Transfert refuse : compte inactif\n";
            return false;
        }
        if (montant <= 0) {
            std::cout << "Transfert refuse : montant invalide (" << montant << ")\n";
            return false;
        }
        if (montant > solde) {
            std::cout << "Transfert refuse : solde insuffisant ("
                      << montant << " > " << solde << ")\n";
            return false;
        }
        solde -= montant;
        destination.solde += montant;
        return true;
    }
};

#endif
