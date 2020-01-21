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

import java.awt.*;
import javax.swing.*;
import java.util.*;

class AireDeDessin extends JComponent {
    ProdCons commandes;
    Dispatcher programme;
    boolean started;
    boolean firstImage;

    java.util.List historique;
    java.util.Map commandsMap;
    int position, maxPosition;
    Dimension taille;

    Image imageCourante;
    Graphics2D zoneImage;
    int positionImage;
    java.util.List cache;
    java.util.List cachePosition;
    int nextCacheable;
    int nextLimit;
    boolean outOfMemoryShown;
    int masque;
    int decal;

    public AireDeDessin(ProdCons commandes, Dispatcher programme) {
        this.commandes = commandes;
        this.programme = programme;
        started = false;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        reset();
    }

    public void reset() {
        taille = null;
        firstImage = true;

        historique = new ArrayList();
        position = 0;
        maxPosition = 0;
        outOfMemoryShown = false;

        commandsMap = new HashMap();
        masque = 1;
        decal = 0;
        while (masque < Parameters.cacheInterval) {
            masque *= 2;
            decal++;
        }
        masque -= 1;
        cache = new ArrayList();
        cachePosition = new ArrayList();
        nextLimit = Parameters.cacheLookahead + masque + 1;
        nextCacheable = masque + 1;
    }

    public boolean canFR() {
        return position > 0;
    }

    public boolean canFF() {
        return !started || (position < maxPosition) ||
               !commandes.estVide();
    }

    public void FR() {
        if (canFR())
            position--;
    }

    public void FF() {
        if (canFF())
            position++;
    }

    public void Debut() {
        position = 0;
    }

    public void Fin() {
        position = maxPosition;
    }

    public void setPosition(int percent) {
        position = Math.round((float) (percent * maxPosition) / 1000);
    }

    public int getPosition() {
        if (maxPosition == 0)
            return 0;
        else
            return position * 1000 / maxPosition;
    }

    private void cacheImageCouranteIfRequired() {
        int i;
        int position = positionImage;
        if (Parameters.enableCache && (position == nextCacheable)) {
            Iterator it = historique.listIterator(position);
            i = 0;
            boolean shouldCache = true;
            while (shouldCache && (i < Parameters.cacheLookahead) &&
                   it.hasNext()) {
                Commande c = (Commande) it.next();
                if (c.erases() < position)
                    shouldCache = false;
                i++;
            }
            if (shouldCache) {
                nextLimit += masque + 1;
                nextCacheable = nextLimit - Parameters.cacheLookahead;
                addImageCouranteToCache();
            } else {
                if ((position + i) > nextLimit) {
                    nextLimit += masque + 1;
                    nextCacheable = nextLimit - Parameters.cacheLookahead;
                    cache.add(cache.get(cache.size() - 1));
                    cachePosition.add(cachePosition.get(
                        cachePosition.size() - 1));
                } else {
                    nextCacheable = position + i;
                }
            }
        }
    }

    private void addImageCouranteToCache() {
        try {
            Image newImage = createImage(taille.width, taille.height);
            Graphics2D zoneCopie = (Graphics2D) newImage.getGraphics();
            zoneCopie.drawImage(imageCourante, 0, 0, null);
            cache.add(newImage);
            cachePosition.add(positionImage);
            //System.out.println("Cached position " + positionImage +
            //                   " at index " + (cache.size() - 1) +
            //                   " nextLimit is " + nextLimit);
        } catch (OutOfMemoryError e) {
            if (!outOfMemoryShown) {
                System.out.println("Pas assez de memoire pour la " +
                    "mise en cache, essayez de consulter les options non " +
                    "standard (java -X) pour augmenter la taille du tas");
                outOfMemoryShown = true;
                // On laisse tout tomber
                Parameters.enableCache = false;
            }
        }
    }

    private int getClosestCachedIndex(int position) {
        if (Parameters.enableCache) {
            int index = position >> decal;
            while ((Integer) cachePosition.get(index) > position)
                index--;
            return index;
        } else {
            return 0;
        }
    }

    private int getCachedPosition(int index) {
        return (Integer) cachePosition.get(index);
    }

    private void updateToRequiredPosition() {
        // On cherche jusqu'a quelle position en arriere vont les nouvelles
        // commandes d'effacement d'un objet graphique
        int i;
        int cacheIndex = -1;
        int objective = positionImage;
        if (position < objective) {
            cacheIndex = getClosestCachedIndex(position);
            objective = getCachedPosition(cacheIndex);
        }
        Iterator it = historique.listIterator(objective);
        if (objective == 0)
            // On entre dans la boucle pour les commandes sans delai
            i=0;
        else
            i=objective;
        while (i<=position && it.hasNext()) {
            Commande c = (Commande) it.next();
            // Mise a jour de la position avec les commandes sans delai
            if ((i == position) && !c.hasDelay())
                position++;
            i++;
            if (i<=position) {
                int erases = c.erases();
                if (erases < objective) {
                    cacheIndex = getClosestCachedIndex(erases);
                    objective = getCachedPosition(cacheIndex);
                    if (objective > 0) {
                        i = objective;
                        it = historique.listIterator(objective);
                    } else {
                        // Retour dans la boucle pour les commandes sans delai
                        i = 0;
                    }
                }
            }
        }
        // Rafraichissement
        if (objective < positionImage)
            restoreImageCourante(cacheIndex);
    }

    private void restoreImageCourante(int index) {
        Image cached = (Image) cache.get(index);
        zoneImage.drawImage(cached, 0, 0, null);
        positionImage = (Integer) cachePosition.get(index);
        //System.out.println("Restored from " + index + " position " +
        //                   positionImage);
    }

    public void paintComponent(Graphics g) {
        // Graphics 2D est le vrai type de l'objet passé en paramètre
        // Le cast permet d'avoir acces a un peu plus de primitives de dessin
        Graphics2D drawable = (Graphics2D) g;

        if (!started) {
            started = true;
            programme.start();
        }

        if (firstImage) {
            taille = getSize();
            firstImage = false;
            imageCourante = createImage(taille.width, taille.height);
            zoneImage = (Graphics2D) imageCourante.getGraphics();

            if (Parameters.requiresOverdraw) {
                // On efface tout
                zoneImage.setPaint(Color.white);
                zoneImage.fillRect(0, 0, taille.width, taille.height);
                zoneImage.setPaint(Color.black);

                positionImage = 0;
                addImageCouranteToCache();
            }
        }

        // On commance par réccupérer toutes les commandes en attente
        int i=0;
        while (!commandes.estVide() && (i<Parameters.maxNewCommands)) {
            Commande c = (Commande) commandes.extraire();
            if (Parameters.requiresOverdraw)
                c.modify(commandsMap);
            historique.add(c);
            maxPosition++;
            i++;
        }

        if (Parameters.requiresOverdraw) {
            // La methode de dessin est un peu lourde : on redessine tout ce
            // qui est present a l'ecran. Cependant, l'avantage est que le
            // resultat est propre contrairement a ce qui se produit en
            // effacant avec des lignes blanches (qui, du coup, effacent tout
            // ce qu'elles croisent : dessin des points, intersections avec
            // d'autres lignes, ...)

            // Avance l'indicateur de position courante pour tenir compte des
            // commandes sans delai et nous ramene, si necessaire, sur une
            // image anterieure presente dans le cache
            updateToRequiredPosition();

            // On dessine
            Iterator itImage = historique.listIterator(positionImage);
            while (positionImage<position) {
				Commande c = null;
                	c = (Commande) itImage.next();
                // System.out.println("Drawing " + c);
                c.draw(zoneImage, position);
                positionImage++;
                cacheImageCouranteIfRequired();
            }
        } else {
            if (position > 0) {
                Iterator it = historique.listIterator(position - 1);
                Commande c = (Commande) it.next();
                boolean foundProperPosition = false;
                while (!foundProperPosition) {
                    Commande next;
                    if (it.hasNext()) {
                        next = (Commande) it.next();
                        if (next.hasDelay()) {
                            foundProperPosition = true;
                        } else {
                            c = next;
                            position++;
                        }
                    } else {
                        foundProperPosition = true;
                    }
                }
                c.draw(zoneImage, position);
            } else {
                // On efface tout
                zoneImage.setPaint(Color.white);
                zoneImage.fillRect(0, 0, taille.width, taille.height);
                zoneImage.setPaint(Color.black);
            }
        }
        drawable.drawImage(imageCourante, 0, 0, null);
    }

    protected void processEvent(AWTEvent e) {
        super.processEvent(e);
        commandes.sendEvent(e);
    }
}
