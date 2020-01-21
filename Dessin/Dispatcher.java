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

import java.lang.reflect.*;

class Dispatcher implements Runnable {
    Fenetre f;
    Evenements e;
    String className;
    String [] args;
    Thread t;

    Dispatcher(Fenetre f, Evenements e, String n, String [] args) {
        this.f = f;
        this.e = e;
        className = n;
        this.args = args;
    }

    public void run() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class fenetreClass = null, evenementsClass = null, algoClass = null;
        try {
            fenetreClass = cl.loadClass("Dessin.Fenetre");
            evenementsClass = cl.loadClass("Dessin.Evenements");
        } catch (Exception e) {
            System.err.println("Internal bug");
            System.exit(1);
        }

        try {
	    algoClass = cl.loadClass(className);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        Class argsClass = args.getClass();
        Method met = null, met1 = null, met2 = null;
        Throwable e1 = null, e2 = null;
        try {
            met2 = algoClass.getDeclaredMethod("main", fenetreClass, argsClass);
            met = met2;
        } catch (Exception e) {
            e2 = e;
        }
        try {
            met1 = algoClass.getDeclaredMethod("main",
                                      fenetreClass, evenementsClass, argsClass);
            met = met1;
        } catch (Exception e) {
            e1 = e;
        }
        if ((met1 != null) && (met2 != null)) {
            System.err.println("Only one of " + met1 + " and " + met2 +
                               " can be implemented in " + className);
            System.exit(1);
        }
        if (met != null) {
            if (Modifier.isStatic(met.getModifiers())) {
                try {
                    if (met1 != null)
                        met.invoke(null, f, e, args);
                    else
                        met.invoke(null, f, args);
                } catch (InvocationTargetException e) {
                    e.getCause().printStackTrace(System.err);
                    System.exit(1);
                } catch (Exception e) {
                    System.err.print(e);
                    System.exit(1);
                }
            } else {
                System.err.println("Method " + met + " is not static");
                System.exit(1);
            }
        } else {
            System.err.println("None of the starting method found:");
            System.err.println(e1);
            System.err.println(e2);
            System.exit(1);
        }
    }

    public void start() {
        t = new Thread(this);
        t.start();
    }

    void stop() {
        t.stop();
    }

    void interrupt() {
        t.interrupt();
    }
}
