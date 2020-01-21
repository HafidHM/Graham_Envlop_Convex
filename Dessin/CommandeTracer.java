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

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.awt.Graphics2D;

public class CommandeTracer implements Commande {
    ObjetGraphique o;
    boolean delay;
    int eraser;
    int id;

    CommandeTracer(ObjetGraphique o, int id, boolean d) {
        this.o = o.clone();
        this.id = id;
        delay = d;
        eraser = -1;
    }

    public void setEraser(int e) {
        if (eraser < 0)
            eraser = e;
    }

    public int getEraser() {
        return eraser;
    }

    public int erases() {
        return id;
    }

    public int getId() {
        return id;
    }

    public ObjetGraphique getObject() {
        return o;
    }

    public void draw(Graphics2D screen, int position) {
        if ((eraser == -1) || (eraser >= position))
            o.draw(screen);
    }

    public boolean hasDelay() {
        return delay;
    }

    public void modify(Map m) {
        int key = o.key();
        List l = (List) m.get(key);
        if (l == null) {
            l = new ArrayList();
            m.put(key, l);
        }
        l.add(this);
    }

    public String toString() {
        return id + " : " + o + " delay " + delay + " eraser " + eraser;
    }
}
