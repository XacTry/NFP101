use std::env;
use std::io::{self, Read};

fn main() {
    let args: Vec<String> = env::args().skip(1).collect();

    let texte = if args.is_empty() {
        let mut s = String::new();
        io::stdin().read_to_string(&mut s).expect("Erreur de lecture");
        s
    } else {
        args.join(" ")
    };

    let mesures: Vec<i32> = texte
        .split_whitespace()
        .map(|m| m.parse().expect("Mesure invalide"))
        .collect();

    let mut normales = 0;
    let mut faibles = 0;
    let mut fortes = 0;

    for &v in &mesures {
        match v {
            x if x >= 0 => normales += 1, // valeur >= 0
            -5..=-1 => faibles += 1,      // -5 <= valeur < 0
            _ => fortes += 1,             // valeur < -5
        }
    }

    println!("Mesures normales : {}", normales);
    println!("Anomalies faibles : {}", faibles);
    println!("Anomalies fortes : {}", fortes);
}