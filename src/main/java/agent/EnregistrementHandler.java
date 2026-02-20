package agent;

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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Titre : classe publique EnregistrementHandler Description : classe permettant
 * de parser un fichier XML avec l'aide du parser XERCES atend la classe
 * DefaultHandler penser a inclure xerces.jar a vos path pour compiler
 *
 * @author Jeremy FRECHARD
 * @author Cecile GIRARD
 * @author Aysel GUNES
 * @author Pierre RAMOS
 * @version 1.0
 */

public class EnregistrementHandler extends DefaultHandler {
    /** Enregistrement en cours de construction pendant le parsing XML. */
    private Enregistrement enregistrement;

    /** Lien en cours de construction lors du traitement d'une balise {@code <link>}. */
    private Lien lien;

    /** Indique que le parseur se trouve a l'interieur d'une balise {@code <keywords>}. */
    private boolean inKeywords;

    /** Indique que le parseur se trouve a l'interieur d'une balise {@code <title>}. */
    private boolean inTitle;

    /** Indique que le parseur se trouve a l'interieur d'une balise {@code <url>}. */
    private boolean inUrl;

    /** Indique que le parseur se trouve a l'interieur d'une balise {@code <desc>}. */
    private boolean inDesc;

    /**
     * Constructeur herite
     */
    public EnregistrementHandler() {
        super();
    }

    /**
     * Methode declenchee a l'ouverture de chaque balise XML.
     * Cree et initialise les objets {@link Enregistrement} et {@link Lien}
     * selon le nom de la balise rencontree. Positionne les drapeaux booleen
     * pour les balises de contenu texte.
     *
     * @param uri       URI de l'espace de noms (peut etre vide)
     * @param localName nom local de la balise (sans prefixe)
     * @param qName     nom qualifie de la balise (avec prefixe si present)
     * @param attributes ensemble des attributs de la balise
     * @throws SAXException si le nom de la balise est inconnu ou si les
     *                      attributs {@code rank} / {@code score} ne sont pas des entiers
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("search")) {
            enregistrement = new Enregistrement();
        } else if (qName.equals("keywords")) {
            inKeywords = true;
        } else if (qName.equals("list")) {
        } else if (qName.equals("link")) {
            lien = new Lien();
            try {
                int rank = Integer.parseInt(attributes.getValue("rank"));
                int score = Integer.parseInt(attributes.getValue("score"));
                lien.setRang(rank);
                lien.setScore(score);
            } catch (Exception e) {
                GestionMessage.message(1, "EnregistrementHandler", "le contenu de rank ou score n'est pas un entier");
                // erreur, le contenu de rank ou score n'est pas un entier
                throw new SAXException(e);
            }
        } else if (qName.equals("title")) {
            inTitle = true;
        } else if (qName.equals("url")) {
            inUrl = true;
        } else if (qName.equals("desc")) {
            inDesc = true;
        } else {
            // erreur, on peut lever une exception
            GestionMessage.message(2, "EnregistrementHandler", "Balise " + qName + " inconnue.");
            throw new SAXException("Balise " + qName + " inconnue.");

        }
    }

    /**
     * Methode declenchee a la fermeture de chaque balise XML.
     * Reinitialise les drapeaux booleen et ajoute le {@link Lien} courant
     * a l'enregistrement une fois la balise {@code <link>} fermee.
     *
     * @param uri       URI de l'espace de noms (peut etre vide)
     * @param localName nom local de la balise (sans prefixe)
     * @param qName     nom qualifie de la balise
     * @throws SAXException si le nom de la balise est inconnu
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("search")) {
        } else if (qName.equals("keywords")) {
            inKeywords = false;
        } else if (qName.equals("list")) {
        } else if (qName.equals("link")) {
            enregistrement.add(lien);
            lien = null;
        } else if (qName.equals("title")) {
            inTitle = false;
        } else if (qName.equals("url")) {
            inUrl = false;
        } else if (qName.equals("desc")) {
            inDesc = false;
        } else {
            // erreur, on peut lever une exception
            GestionMessage.message(2, "EnregistrementHandler", "Balise " + qName + " inconnue.");
            throw new SAXException("Balise " + qName + " inconnue.");
        }
    }

    /**
     * Methode declenchee a chaque fois que du contenu textuel est rencontre entre
     * deux balises. Affecte le texte lu au champ correspondant de
     * l'objet {@link Enregistrement} ou {@link Lien} en cours de construction,
     * selon l'etat des drapeaux booleen.
     *
     * @param ch     tableau de caracteres contenant le texte source
     * @param start  indice du premier caractere a lire dans {@code ch}
     * @param length nombre de caracteres a lire
     * @throws SAXException en cas d'erreur SAX interne
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String lecture = new String(ch, start, length);
        if (inKeywords) {
            enregistrement.setKeywords(lecture);
        } else if (inTitle) {
            // on concatene les caracteres trouves
            lien.setTitre(lien.getTitre() + lecture);
        } else if (inUrl) {
            lien.setUrl(lien.getUrl() + lecture);
        } else if (inDesc) {
            lien.setDesc(lien.getDesc() + lecture);
        }
    }

    /**
     * Methode declenchee au debut du parsing du document XML.
     * Enregistre un message de traitement dans le journal.
     *
     * @throws SAXException en cas d'erreur SAX interne
     */
    @Override
    public void startDocument() throws SAXException {
        GestionMessage.message(0, "EnregistrementHandler", "Debut du parsing XML Resultat");
    }

    /**
     * Methode declenchee a la fin du parsing du document XML.
     * Enregistre un message de traitement dans le journal.
     *
     * @throws SAXException en cas d'erreur SAX interne
     */
    @Override
    public void endDocument() throws SAXException {
        GestionMessage.message(0, "EnregistrementHandler", "Fin du parsing XML R�sultat");
    }

    /**
     * Utilisation du toXhtml de Enregistrement
     *
     * @param bool booleen
     * @return string
     */
    public String toXhtml(boolean bool) {
        return this.enregistrement.toXhtml(bool);
    }

    /**
     * Utilisation du toSql de Enregistrement
     *
     * @return string
     */
    public String toSql() {
        return this.enregistrement.toSql();
    }

    /**
     * Utilisation du toXml de Enregistrement
     *
     * @return string
     */
    public String toXml() {
        return this.enregistrement.toXml();
    }

    /**
     * Methode d'acces a l'enregistrement apres un parsing
     *
     * @return Enregistrement
     */
    public Enregistrement getEnregistrement() {
        return enregistrement;
    }
}