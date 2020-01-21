/*
Dessin - package de visualisation pas à pas d'algorithmes de dessin
Copyright (C) 2009 Guillaume Huard
Ce programme est libre, vous pouvez le redistribuer et/ou le modifier selon les
termes de la Licence Publique Générale GNU publiée par la Free Software
Foundation (version 2 ou bien toute autre version ultérieure choisie par vous).

Ce programme est distribué car potentiellement utile, mais SANS AUCUNE
GARANTIE, ni explicite ni implicite, y compris les garanties de
commercialisation ou d'adaptation dans un but spécifique. Reportez-vous à la
Licence Publique Générale GNU pour plus de détails.

Vous devez avoir reçu une copie de la Licence Publique Générale GNU en même
temps que ce programme ; si ce n'est pas le cas, écrivez à la Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
États-Unis.

Contact: Guillaume.Huard@imag.fr
         ENSIMAG - Laboratoire LIG
         51 avenue Jean Kuntzmann
         38330 Montbonnot Saint-Martin
*/
package Dessin;

public class Parameters {
    // Le temps ecoule entre deux commandes graphiques pour les positions
    // extreme du glisseur de selection de vitesse
    public static int minDelay = 30;
    public static int maxDelay = 1000;
    public static int defaultSpeed = 0;
    // Nombre maximum de commandes a traiter avant de suspecter une boucle
    public static int maxCommands = 1000000;
    // Nombre maximum de commandes ajoutees a l'historique entre 2 refresh
    public static int maxNewCommands = 128;
    // Active un mecanisme de cache logiciel
    public static boolean enableCache = true;
    // Nombre de commandes entre deux mises en cache
    public static int cacheInterval = 32768;
    // Nombre de commandes en avance consultees par le cache pour eviter de
    // stocker une position suivie de commandes d'effacement
    public static int cacheLookahead = 16;
    // Taille de la table de hachage pour l'accélération de la recherche de
    // commandes lors de l'effacement
    public static int mapSize = 500;
    // Indique au moteur de dessin si les commandes de trace modifient
    // seulement une partie de l'affichage (et ont donc besoin d'etre tracees
    // les unes par dessus les autres) ou bien renouvellent l'integrelite de
    // l'affichage
    public static boolean requiresOverdraw = true;
}
