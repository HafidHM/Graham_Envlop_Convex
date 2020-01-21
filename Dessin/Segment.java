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

import java.awt.Graphics2D;
import java.awt.Color;

public class Segment implements ObjetGraphique {
    public int x, y, z, t;
    Color c;

    public Segment(int x, int y, int z, int t) {
        this(x,y,z,t,Color.black);
    }

    public Segment(int x, int y, int z, int t, Color c) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
        this.c = c;
    }

    public ObjetGraphique clone() {
        return new Segment(x, y, z, t, c);
    }

    public void draw(Graphics2D g) {
        g.setPaint(c);
        g.drawLine(x, y, z, t);
    }

    public boolean equals(ObjetGraphique o) {
        if (o != null) {
            try {
                Segment s = (Segment) o;
                return (x == s.x) && (y == s.y) && (z == s.z) && (t == s.t);
            } catch (ClassCastException e) {
                return false;
            }
        } else
            return false;
    }

    public int key() {
        return Math.min(x, z) % Parameters.mapSize;
    }

    public String toString() {
        return "("+x+","+y+","+z+","+t+")";
    }
}
