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

import javax.swing.JFrame;
import java.awt.AWTEvent;

class FenetreGraphique extends JFrame {
    ProdCons commandes;

    FenetreGraphique(ProdCons commandes, String name) {
        super(name);
        this.commandes = commandes;
        enableEvents(AWTEvent.KEY_EVENT_MASK);
    }

    protected void processEvent(AWTEvent e) {
        super.processEvent(e);
        commandes.sendEvent(e);
    }
}
