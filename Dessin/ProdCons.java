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

import java.util.*;

class ProdCons {
    Object [] tab;
    int tete, nombre, masque;
    Queue Events;
    Traitant traitant;

    public ProdCons(int taille, Traitant t) {
        masque = 1;
        while (masque < taille) {
            masque *= 2;
        }
        tab = new Object[masque];
        masque -= 1;
        Events = new LinkedList();
        traitant = t;
        reset();
    }

    public synchronized void reset() {
        tete = 0;
        nombre = 0;
    }

    public synchronized boolean estVide() {
        return nombre == 0;
    }

    public synchronized boolean estPlein() {
        return nombre > masque;
    }

    public synchronized void inserer(Object o) {
        while (nombre == tab.length)
            try {
                wait();
                handleEvents();
            } catch (Exception e) {
            }
        tab[(tete+nombre) & masque] = o;
        nombre++;
        if (nombre == 1)
            traitant.signalCommand();
        notifyAll();
    }

    public synchronized Object extraire() {
        Object resultat;
        while (nombre == 0)
            try {
                wait();
            } catch (Exception e) {
            }
        resultat = tab[tete];
        tete = (tete+1) & masque;
        nombre--;
        notifyAll();
        return resultat;
    }

    private void handleEvents() {
        while (Events.size() > 0) {
            Object e = Events.remove();
            traitant.processEvent(e);
        }
    }

    public synchronized void waitForEvent() {
        while (Events.size() <= 0) {
            try {
                wait();
            } catch (Exception e) {
            }
        }
        handleEvents();
    }

    synchronized void sendEvent(Object e) {
        Events.add(e);
        notifyAll();
    }
}
