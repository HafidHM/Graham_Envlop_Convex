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

import java.awt.Color;
import java.awt.Graphics2D;

public class Point implements ObjetGraphique {
    public int x, y;
    Color c;

    public Point(int x, int y) {
        this(x, y, Color.black);
    }

    public Point(int x, int y, Color c) {
        this.x = x;
        this.y = y;
        this.c = c;
    }

    public ObjetGraphique clone() {
        return new Point(x, y, c);
    }

    public void draw(Graphics2D g) {
        g.setPaint(c);
        g.drawLine(x-2, y, x+2, y);
        g.drawLine(x, y-2, x, y+2);
    }

    public boolean equals(ObjetGraphique o) {
        if (o != null) {
            try {
                Point p = (Point) o;
                return (x == p.x) && (y == p.y);
            } catch (ClassCastException e) {
                return false;
            }
        } else
            return false;
    }

    public int key() {
        return x % Parameters.mapSize;
    }

    public String toString() {
        return "("+x+","+y+")";
    }
}
