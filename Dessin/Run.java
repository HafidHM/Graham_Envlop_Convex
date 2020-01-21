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

import javax.swing.SwingUtilities;

class Run {
    public static void main(String [] args) {
        String name;
        String [] new_args;

        if (args.length > 0) {
            name = args[0];
            new_args = new String[args.length - 1];
            for (int i=1; i<args.length; i++)
                new_args[i-1] = args[i];
        } else {
            name = "Demo";
            new_args = args;
        }
        InterfaceDessin f = new InterfaceDessin(400, 400, name, new_args);
        SwingUtilities.invokeLater(f);
    }
}
