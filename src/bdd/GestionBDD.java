package bdd;
import java.util.Vector;
import java.sql.*;

import com.mysql.jdbc.Driver;
import agent.*;

/*
 * This file is part of "Méta-moteur".
 *
 * (c) Méta-moteur 2005-2006. All Rights Reserved.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */
 /**
 * Classe contenant toutes les methodes de gestion de base de donnees Mysql
 * 
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class GestionBDD {
	/**
	 * Methode permettant de se connecter a la base de donnees dont les coordonnees sont en attributs
	 * 
	 * @return retourne l'objet representant la connection avec la base
	 */
	public static Connection connectionBDD(){
		Connection connection=null;
		if(Agent.TypeBDD.equals("MySQL")){
			try{
				//ouverture du pilote de connection avec la base
				Driver driver=new com.mysql.jdbc.Driver();
				//enregistrement du pilote
				DriverManager.registerDriver(driver);
				//connection a la base de donnees
				connection=DriverManager.getConnection("jdbc:mysql://"+Agent.HostBDD+"/"+Agent.BaseBDD,Agent.UserBDD,Agent.PassBDD);
			}catch(SQLException e){
				//Affichage d'un message d'erreur en cas de non possibilite de la connection a la base
				GestionMessage.message(2,"GestionBDD","Erreur de connexion a la base de donnees");
			}
		}
		//renvoi de l'objet de connection a la base
		return connection;
	}
	/**
	 * Methode permettant d'effectuer une requete sur une base de donnees, la connection et la deconnection sont effectuees dans la methode
	 * 
	 * @param requete Chaine de caracteres contenant la requete a effectuer
	 * @return retourne un vecteur contenant le autant de vecteurs que de lignes de la reponse, ces vecteurs contiennent des objets contenant le contenu de chaque champ de la base
	 */
	public static Vector envoiRequete(String requete){
		//connection a la base de donnees
		Connection connection=connectionBDD();
		//creation du vecteur qui sera retourne
		Vector retour=new Vector();
		try{
			//preparation de la requete
			Statement statement=connection.createStatement();
			//en cas de requete SELECT il faut recuperer le resultat
			if(requete.substring(0,requete.indexOf(" ")).equals("SELECT")){
				//recuperation du resultat de la requete
				ResultSet resultats=statement.executeQuery(requete);
				//recuperation des metadonnees sur les resultats
				ResultSetMetaData meta=resultats.getMetaData();
				//recuperation du nombre de colonnes a traiter
				int taille=meta.getColumnCount();
				//parcours de l'iterateur de resultats
				while(resultats.next()){
					//creation d'un sous vecteur representant une entree dans la base de donnees
					Vector temp=new Vector();
					//parcours des champs de l'entree de la base
					for(int i=1;i<=taille;i++){
						//ajout des champs au sous vecteur
						temp.add(resultats.getObject(i));
					}
					//ajout du sous vecteur au vecteur a renvoyer
					retour.add(temp);
				}
				//fermeture de l'iterateur
				resultats.close();
			}
			else{
				//execution de la requete
				statement.execute(requete);
			}
			//liberation de la memoire utilisee pour la requete
			statement.close();
			connection.close();
			//affichage d'un message d'information
			GestionMessage.message(0,"GestionBDD","requete "+requete+" envoyee");
		}catch(SQLException e){
			//affichage d'un message en cas d'erreur
			GestionMessage.message(2,"GestionBDD","Erreur d'envoi de la requete "+requete);
		}
		//renvoi du vecteur resultat
		return retour;
	}
	/**
	 * Methode permettant de construire des requetes SQL de type SELECT
	 * 
	 * @param restriction restriction des colones a envoyer dans la requete (* si pas de restriction)
	 * @param table table sur laquelle la requete doit etre effectuee
	 * @param condition conditions a utiliser pour la requete (" " si pas de condition)
	 * @return retourne une requete en langage SQL a partir des infos donnees en entree
	 */
	public static String construitSelect(String restriction,String table,String condition){
		if(!condition.equals(" "))
			return(new String ("SELECT "+restriction+" FROM "+table+" WHERE "+condition));
		else
			return(new String ("SELECT "+restriction+" FROM "+table));
	}
	/**
	 * Methode permettant de construire des requetes SQL de type INSERT
	 * 
	 * @param table table sur laquelle la requete doit etre effectuee
	 * @param valeurs valeurs a affecter aux champs de la table (ATTENTION : il faut les mettre dans l'ordre des champs de la table)
	 * @return retourne une requete en langage SQL a partir des informations donnes en entree
	 */
	public static String construitInsert(String table,String valeurs){
		Connection connection=connectionBDD();
		Vector retour=new Vector();
		try{
			//recuperation d'un vecteur contenant le nom des colonnes de la table dans laquelle l'insertion doit etre operee
			Statement statement=connection.createStatement();
			//envoi d'une requete quelconque sur la table pour en recuperer les noms de colonnes
			ResultSet resultats=statement.executeQuery(new String("SELECT * FROM "+table));
			//recuperation des metadonnees sur la table
			ResultSetMetaData meta=resultats.getMetaData();
			//recuperation du nombre de colonnes
			int taille=meta.getColumnCount();
			for(int i=1;i<=taille;i++)
				//recuperation du nom des colonnes
				retour.add(meta.getColumnName(i));
			//liberation de la memoire necessaire a la requete
			resultats.close();
			statement.close();
			connection.close();
		}catch(SQLException e){
			//affichage d'un message en cas d'erreur
			GestionMessage.message(2,"GestionBDD","Erreur lors de l'acces a la base de donnees pour un insert");
		}
		//creation d'une String pour construire la requete
		String col=new String("");
		int taille=retour.size();
		//creation de la chaine a inserer dans la requete
		for(int i=0;i<taille;i++){
			col+=(String)retour.get(i);
			//pour ne pas ajouter une virgule en trop
			if(i<taille-1)
				col+=",";
		}
		return(new String ("INSERT INTO "+table+" ("+col+") VALUES ("+valeurs+")"));
	}
	/**
	 * Methode permettant de construire des requetes SQL de type UPDATE
	 * 
	 * @param table table sur laquelle la requete doit etre effectuee
	 * @param valeurs valeurs a modifier dans les champs de la table (ATTENTION : il faut les mettre le nom des champs avant la modification de la table)
	 * @param condition conditions a utiliser pour la requete (" " si pas de condition)
	 * @return retourne une requete en langage SQL a partir des informations donnes en entree
	 */
	public static String construitUpdate(String table,String valeurs,String condition){
		if(!condition.equals(" "))
			return(new String ("UPDATE "+table+" SET "+valeurs+" WHERE "+condition));
		else
			return(new String ("UPDATE "+table+" SET "+valeurs));
	}
	/**
	 * Methode permettant d'inserer un enregistrement dans la base de donnees en suivant le schema de la base
	 * 
	 * @param enr enregistrement a ajouter a la base
	 */
	public static void insertEnregistrement(Enregistrement enr){
		String val=new String("");
		//construction de la chaine a ajouter a la requete
		//le 0 permet de gerer l'auto increment
		val+="'0','"+enr.getKeywords()+"'";
		val+=",";
		Vector liens=enr.getLiens();
		//ajout des valeurs a inserer dans la table
		for(int i=0;i<liens.size();i++){
			Lien temp=(Lien)liens.get(i);
			val+="'"+temp.getUrl()+"','"+temp.getTitre()+"','"+temp.getDesc()+"','"+(i+1)+"','"+0+"',";
		}
		//pour la gestion de la duree de vie d'un enregistrement
		val+="'"+System.currentTimeMillis()+"'";
		//creation de la requete
		String requete=GestionBDD.construitInsert(Agent.TableBDD,val);
		//envoi de la requete
		GestionBDD.envoiRequete(requete);
	}
	/**
	 * Methode permettant d'updater le nombre de clics d'une url dans les enregistrements non encore ferme (ajoute un clic si l'url est reconnue a l'url correspondante de tous les enregistrements ouverts de la base)
	 * 
	 * @param url url cliquee a mettre a jour
	 */
	public static void updateURL(String url){
		String condition=new String();
		//creation de la condition pour rechercher les url identiques
		for(int i=1;i<20;i++){
			condition+="`Url"+i+"`='"+url+"' OR";
		}
		condition+="`Url"+20+"`='"+url+"'";
		//construction de la requete pour selectionner les enregistrement contenant une url identique
		String requete=GestionBDD.construitSelect("Uid",Agent.TableBDD,condition);
		//envoi de la requete
		Vector vect=GestionBDD.envoiRequete(requete);
		if(vect.size()!=0){
			//construction de la requete qui permettra de n'avoir que les entrees non fermees
			condition="";
			for(int i=0;i<vect.size()-1;i++){
				condition+="`Uid`='"+((Integer)((Vector)(vect.get(i))).get(0)).intValue()+"' OR";
			}
			condition+="`Uid`='"+((Integer)((Vector)(vect.get(vect.size()-1))).get(0)).intValue()+"'";
			//construction de la requete
			requete=GestionBDD.construitSelect("Uid,Url1,Url2,Url3,Url4,Url5,Url6,Url7,Url8,Url9,Url10,Url11,Url12,Url13,Url14,Url15,Url16,Url17,Url18,Url19,Url20,TimeQuery",Agent.TableBDD,condition);
			//envoi de la requete
			vect=GestionBDD.envoiRequete(requete);
			if(vect.size()!=0){
				for(int i=0;i<vect.size();i++){
					//parcours des resultats
					Vector sousVect=(Vector)vect.get(i);
					String date=(String)sousVect.get(sousVect.size()-1);
					//recuperation de la date (parsage)
					Double nombre=Double.valueOf(date);
					long date2=nombre.longValue();
					//les enregistrements ouverts contenant l'url
					if(System.currentTimeMillis()-(date2+1800000)<0){
						//parcours des url des enregistrements
						for(int j=0;j<20;j++){
							String url2=(String)sousVect.get(j+1);
							if(url2.equals(url)){
								//construction de la requete d'update
								int id=((Integer)(sousVect.get(0))).intValue();
								requete=GestionBDD.construitUpdate(Agent.TableBDD,"Select"+(j+1)+"=Select"+(j+1)+"+1","`Uid`='"+id+"'");
								//envoi de la requete
								GestionBDD.envoiRequete(requete);
							}
						}
					}
				}
			}
		}
	}
	/**
	 * Methode de test de connectivite de la base de donnees, ne fait que verifier que la base de donnees repond
	 * 
	 * @return retourne true si le serveur MySQL repond, false sinon
	 */
	public static boolean testBDD(){
		//tentative de connection a la base
		Connection res=GestionBDD.connectionBDD();
		if(res==null)
			return(false);
		else
			return(true);
	}
}
