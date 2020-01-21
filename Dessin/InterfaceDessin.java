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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class InterfaceDessin extends JComponent implements Runnable,
                                                Fenetre, Evenements, Traitant {
    ProdCons commandes;
    Dispatcher programme;
    int nbCommandes;
    boolean warned;
    int currentId;

    AireDeDessin aire;
    String className;
    String [] args;

    int largeur, hauteur;
    JFrame frame;
    Timer player;
    boolean enPause;
    JButton boutonDebut, boutonFR, boutonPause, boutonFF, boutonFin;
    Icon Play, Pause;
    JSlider position, speed;
    AdapteurPosition positionControl;

    Icon getIcon(String nom) {
        ClassLoader cl = getClass().getClassLoader();
        return new ImageIcon(cl.getResource("Images/" + nom));
    }

    InterfaceDessin(int x, int y, String n, String [] args) {
        nbCommandes = 0;
        warned = false;
        currentId = 0;

        largeur = x;
        hauteur = y;
        className = n;
        this.args = args;
    }

    public void run() {
        // Necessaire pour allouer les structures qui permettrons a
        // l'utilisateur de recevoir des evenements sur ce composant
        repaint();
        
        commandes = new ProdCons(Parameters.maxNewCommands, this);
        programme = new Dispatcher(this, this, className, args);

        aire = new AireDeDessin(commandes, programme);
        frame = new FenetreGraphique(commandes, className);
        Container mainPanel = frame.getContentPane();
        mainPanel.add(aire, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        mainPanel.add(panel, BorderLayout.SOUTH);

        Icon icone = getIcon("Begin.png");
        boutonDebut= new JButton(icone);
        boutonDebut.setFocusable(false);
        boutonDebut.addActionListener(new AdapteurDebut(this));
        panel.add(boutonDebut);
        icone = getIcon("FR.png");
        boutonFR = new JButton(icone);
        boutonFR.setFocusable(false);
        boutonFR.addActionListener(new AdapteurFR(this));
        panel.add(boutonFR);
        enPause = false;
        Play = getIcon("Play.png");
        Pause = getIcon("Pause.png");
        boutonPause = new JButton(Pause);
        boutonPause.setFocusable(false);
        boutonPause.addActionListener(new AdapteurPlay(this));
        panel.add(boutonPause);
        icone = getIcon("FF.png");
        boutonFF = new JButton(icone);
        boutonFF.setFocusable(false);
        boutonFF.addActionListener(new AdapteurFF(this));
        panel.add(boutonFF);
        icone = getIcon("End.png");
        boutonFin = new JButton(icone);
        boutonFin.setFocusable(false);
        boutonFin.addActionListener(new AdapteurFin(this));
        panel.add(boutonFin);

        position = new JSlider(0, 1000, 0);
        position.setFocusable(false);
        positionControl = new AdapteurPosition(this);
        position.addChangeListener(positionControl);
        panel.add(position);

        player = new Timer(1000, new EcouteurDeTimer(this));
        player.setCoalesce(true);
        player.start();

        panel = new JPanel();
        mainPanel.add(panel, BorderLayout.NORTH);
        JLabel label = new JLabel("Speed :");
        panel.add(label);
        speed = new JSlider(0, 1000, 0);
        speed.setFocusable(false);
        speed.addChangeListener(new AdapteurSpeed(this));
        panel.add(speed);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aire.setPreferredSize(new Dimension(largeur, hauteur));
        frame.pack();
        frame.setVisible(true);
    }

    public void rafraichir() {
        if (!aire.canFF() && !enPause)
            basculerPause();
        if (enPause) {
            boutonPause.setIcon(Play);
        } else {
            boutonPause.setIcon(Pause);
        }
        boutonDebut.setEnabled(aire.canFR());
        boutonFR.setEnabled(aire.canFR());
        boutonFin.setEnabled(aire.canFF());
        boutonFF.setEnabled(aire.canFF());
        positionControl.automaticAdjustment = true;
        position.setValue(aire.getPosition());
        frame.repaint();
    }

    void basculerPause() {
        if (enPause) {
            player.restart();
            aire.FF();
            rafraichir();
        } else
            player.stop();
        enPause = !enPause;
    }

    void traiterCommande(Commande c) {
        commandes.inserer(c);
        nbCommandes++;
        if ((nbCommandes >= Parameters.maxCommands) && !warned) {
            System.out.println("ATTENTION : commandes d'affichage nombreuses");
            System.out.println("-> si vous rencontrez cette erreur, votre " +
                               "algorithme est probablement pris dans une " +
                               "boucle infinie. Si ce n'est pas le cas, " +
                               "vous pouvez ignorer cette erreur ou modifier" +
                               " la valeur de maxCommands dans " +
                               "Dessin/Parameters.java");
            warned = true;
        }
    }

    public void tracer(ObjetGraphique o) {
        traiterCommande(new CommandeTracer(o, currentId++, true));
    }

    public void tracerSansDelai(ObjetGraphique o) {
        traiterCommande(new CommandeTracer(o, currentId++, false));
    }

    public void effacer(ObjetGraphique o) {
        traiterCommande(new CommandeEffacer(o, currentId++, true));
    }

    public void effacerSansDelai(ObjetGraphique o) {
        traiterCommande(new CommandeEffacer(o, currentId++, false));
    }

    public int largeur() {
        return aire.getSize().width;
    }

    public int hauteur() {
        return aire.getSize().height;
    }

    public void setDrawAreaSize(int x, int y) {
        try {
            SwingUtilities.invokeAndWait(new Resizer(this, x, y));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public void processEvent(Object e) {
        processEvent((AWTEvent) e);
    }

    public void signalCommand() {
        if (enPause) {
            basculerPause();
        }
        frame.repaint();
    }

    public void waitForEvent() {
        commandes.waitForEvent();
    }
}

class AdapteurDebut implements ActionListener {
    InterfaceDessin f;

    AdapteurDebut(InterfaceDessin f) {
        this.f = f;
    }

    public void actionPerformed(ActionEvent e) {
        f.aire.Debut();
        f.rafraichir();
    }
}

class AdapteurFR implements ActionListener {
    InterfaceDessin f;

    AdapteurFR(InterfaceDessin f) {
        this.f = f;
    }

    public void actionPerformed(ActionEvent e) {
        f.aire.FR();
        f.rafraichir();
    }
}

class AdapteurPlay implements ActionListener {
    InterfaceDessin f;

    AdapteurPlay(InterfaceDessin f) {
        this.f = f;
    }

    public void actionPerformed(ActionEvent e) {
        f.basculerPause();
        f.rafraichir();
    }
}

class AdapteurFF implements ActionListener {
    InterfaceDessin f;

    AdapteurFF(InterfaceDessin f) {
        this.f = f;
    }

    public void actionPerformed(ActionEvent e) {
        f.aire.FF();
        f.rafraichir();
    }
}

class AdapteurFin implements ActionListener {
    InterfaceDessin f;

    AdapteurFin(InterfaceDessin f) {
        this.f = f;
    }

    public void actionPerformed(ActionEvent e) {
        f.aire.Fin();
        f.rafraichir();
    }
}

class AdapteurPosition implements ChangeListener {
    InterfaceDessin f;
    boolean automaticAdjustment;

    AdapteurPosition(InterfaceDessin f) {
        this.f = f;
        automaticAdjustment = false;
    }

    public void stateChanged(ChangeEvent e) {
        if (!automaticAdjustment) {
            f.aire.setPosition(f.position.getValue());
            f.rafraichir();
        }
        automaticAdjustment = false;
    }
}

class AdapteurSpeed implements ChangeListener {
    InterfaceDessin f;

    AdapteurSpeed(InterfaceDessin f) {
        this.f = f;
        setSpeed(Parameters.defaultSpeed);
    }

    public void setSpeed(int value) {
        f.player.setDelay((Parameters.maxDelay - Parameters.minDelay)*
                          (1000 - value)/1000 + Parameters.minDelay);
    }

    public void stateChanged(ChangeEvent e) {
        setSpeed(f.speed.getValue());
    }
}
