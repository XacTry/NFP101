#include <iostream>
#include <vector>
#include <algorithm>
#include <cstdlib>
#include <climits>

// Trouve les deux puissances les plus proches et leur difference.
// Utilisation : ./puissances 5 8 9 3 12
// ou sans argument : saisie des valeurs sur l'entree standard.
int main(int argc, char* argv[]) {
    std::vector<int> p;

    if (argc > 1) {
        for (int i = 1; i < argc; i++) {
            p.push_back(std::atoi(argv[i]));
        }
    } else {
        int x;
        while (std::cin >> x) {
            p.push_back(x);
        }
    }

    if (p.size() < 2) {
        std::cout << "Il faut au moins deux puissances." << std::endl;
        return 1;
    }

    // On trie : les deux valeurs les plus proches sont forcement voisines
    std::sort(p.begin(), p.end());

    int a = p[0], b = p[1];
    int diffMin = INT_MAX;
    for (size_t i = 1; i < p.size(); i++) {
        int d = p[i] - p[i - 1];
        if (d < diffMin) {
            diffMin = d;
            a = p[i - 1];
            b = p[i];
        }
    }

    std::cout << "Puissances : " << a << " " << b << std::endl;
    std::cout << "Difference : " << diffMin << std::endl;
    return 0;
}