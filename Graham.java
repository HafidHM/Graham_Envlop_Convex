import java.util.ArrayList;
import java.util.Stack;
import Dessin.*;
import java.awt.Color;
import java.util.*;

public class Graham{
	 static final double PRECISION=1E-6;
	
	public static void afficheListPoint(ArrayList<Point> listPoints) {  
		//Affichage d'une liste des Points
		for(Point p : listPoints) {
			System.out.println(p);
		}
	}
	
	public static Point Cherche_point_min_Bas(ArrayList<Point> listPoints) {
		//La recherche du point le plus Bas 
		Point pMin = listPoints.get(0);
		for(Point p : listPoints) {
			if((p.y < pMin.y)||(p.y == pMin.y && p.x > pMin.x)) { //On prend le point le plus bas a droite
				pMin = p;
			}
		}
		return pMin;	
	}


	public static double  angle(Point pivot,Point p) {
		//Calculer l'angle d'un point par rapport le pivot 
		int y= p.y - pivot.y;
		int x= p.x - pivot.x;
		double tan = 0;
		if(x==0&&y!=0) {   //Eviter de diviser sur 0 (le cas ou le pivot et le p on le x egale)
			return 90;
		}
	    double angle = Math.toDegrees(Math.atan2(y,x));   
	    return angle;
	}
	
	public static int equal(Point pivot, Point p1, Point p2) {
		//Tester l'egaliter d'angle entre les points 
		if (angle(pivot,p1) < angle(pivot,p2)) {
			 return -1;
		}else if(angle(pivot,p1) > angle(pivot,p2)) {
			  return 1;
		} else {

			// Pour gerer la partie des points du nuage qui sont d'alignés
			if(p1.y<p2.y) {
				return -1;
			}else if(p1.y>p2.y){
				return 1;
			}else {
				return 0;	
			}
			 
		}		
	}
	

	public static ArrayList<Point>  triGraph_parInsertion(Point pivot,ArrayList<Point> listPoints) {

		//Trier la liste des Points avec l'algorithme du tri par Insertion
		int i,j,ind=0;
		Point aux;
		for (j = 0; j < listPoints.size(); j++) {
			ind=j;
	           for(i=j;i<listPoints.size();i++){
	                       if(equal(pivot,listPoints.get(i),listPoints.get(ind))==-1)
	                       {
	                              ind = i;
	                       }
	           }
	                aux = listPoints.get(j);
	                listPoints.set(j, listPoints.get(ind));
	                listPoints.set(ind,aux);
		}
		return listPoints;
	}


	public static int produitVectoriel(Point p1, Point p2, Point p3) { 
		//Retourne le produit vectoriel  de p1, p2, p3 
		return (p2.x - p1.x) * (p3.y - p1.y) - (p3.x - p1.x) * (p2.y - p1.y);
	}
	

	public static void  main(Fenetre f, String [] args) {
		   
		Random r = new Random();
        ArrayList<Point>  listPoints = new ArrayList<Point>();
        int nbPoints;

        // Reccuperation du nombre de points en argument (ou valeur par defaut)
        if (args.length > 0) {
            nbPoints = Integer.parseInt(args[0]);
        } else {
            nbPoints = 5;
        }

        // Generation du nuage avec une petite marge pour ne pas avoir de
        // points contre le bord de la fenetre
        for (int i=0; i<nbPoints; i++) {
            int x = r.nextInt(f.largeur()-20)+10;
            int y = r.nextInt(f.hauteur()-20)+10;
            Point p = new Point(x, y);
            listPoints.add(p);
            f.tracerSansDelai(p);
        }
                    

                        Point pbas= Cherche_point_min_Bas(listPoints);  //la recherche du point le plus bas
                      
 						listPoints = triGraph_parInsertion(Cherche_point_min_Bas(listPoints),listPoints); //trier la liste des points par ordre croissant
 						 f.tracer(new Point(pbas.x, pbas.y,Color.BLUE)); //Colorer le point de depart le plus bas en rouge 
 						 

					    Stack<Point> pile = new Stack<>(); //pile pour enregister lenvlope convex
                        
                        //Ajouter les deux premieres points et tracer le segment entre les deux en rouge 
					    pile.push(listPoints.get(0));
					    pile.push(listPoints.get(1));
					    f.tracer(new Segment(listPoints.get(0).x,listPoints.get(0).y,listPoints.get(1).x,listPoints.get(1).y,Color.red));

                        Point top = null;

						for (int k = 2; k < listPoints.size(); k++) {
							//On color le point de test 
							// Green => point courant de test
							// Black => point normal
							f.tracer(new Point(listPoints.get(k).x, listPoints.get(k).y,Color.GREEN));
							f.tracer(new Point(listPoints.get(k).x, listPoints.get(k).y,Color.BLACK));

					    	while ((pile.size()-1) >=1  && produitVectoriel(pile.get(pile.size()-2), pile.peek(),listPoints.get(k) ) <= 0 ) {
					    		if(produitVectoriel(pile.get(pile.size()-2), pile.peek(),listPoints.get(k)) ==0)	{ //Pour tester qu'un point p1 est à gauche d'une segment [p2 p3], on vérifie que le produit vectoriel est négatif, c'est-à-dire on vérifie que produitVectoriel(p1, p2, p3) ≤ 0
					    			break;
					    		}else {
                                       //Sinon on l'enleve de la pile
					    			top =pile.pop();
					    			//on efface le segment attache avec le sommet de la pile 
					    			f.effacer(new Segment(pile.peek().x,pile.peek().y,top .x,top.y));
					    			
					    		}

							}
							//on trace le segment entre le Sommet de la pile et le point de vert de teste 
							f.tracer(new Segment(pile.peek().x,pile.peek().y,listPoints.get(k).x,listPoints.get(k).y, Color.red));
						    pile.push(listPoints.get(k));
						}
						//on trace le dernier segment entre le Sommet de la pile et le point de depart le plus bas
                         f.tracer(new Segment(pile.peek().x ,pile.peek().y  ,pbas.x  ,pbas.y , Color.red));

                                      
           
	}


}

