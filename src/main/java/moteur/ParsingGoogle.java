package moteur;

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

//utilisation des regex en Java

import agent.Enregistrement;
import agent.GestionMessage;
import agent.Lien;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe traitant la liste de resultats de Google
 *
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class ParsingGoogle {

    /**
     * Enregistrement en cours de construction lors du parsing d'une page
     * de resultats Google. Valide uniquement pendant l'appel a
     * {@link #htmlParsing(String)}.
     */
    private static Enregistrement enregistrement;

    /**
     * Lien en cours de construction pour chaque bloc de resultat individuel.
     * Valide le temps du traitement d'un seul bloc ({@code <p class=g>}).
     */
    private static Lien lien;

    /**
     * Methode de parsing Google generale
     *
     * @param pageHtml Une chaine de caractere.
     * @return boolean
     */
    public static Enregistrement htmlParsing(String pageHtml) {
        enregistrement = new Enregistrement();
        enregistrement.setId(0);
        // Nettoyage de printemps de la page
        // compilation de regex
        GestionMessage.message(0, "ParsingGoogle", "Debut du parcours de la r�ponse Google");
        Pattern htmlNettoyage = Pattern.compile(".+<div>(.+)</div>.+</div>.+");
        Matcher m = htmlNettoyage.matcher(pageHtml);
        boolean test = m.matches();
        if (test) {
            for (int i = 0; i < m.groupCount(); i++) {
                // Maintenant on scinde les blocs de resultats
                // Compilation de la regex
                Pattern htmlBlocResultat = Pattern.compile("<p class=g>");
                // separation en sous-chaines
                String[] itemsBlocResultat = htmlBlocResultat.split(m.group(1));
                for (int j = 1; j < itemsBlocResultat.length; j++) {
                    // on traite chaque blocs
                    if (itemsBlocResultat[j].contains("<span class=w>")) {
                        blocParsingDoc(itemsBlocResultat[j], j);
                    } else {
                        blocParsing(itemsBlocResultat[j], j);
                    }
                }
            }
        } else {

            GestionMessage.message(1, "ParsingGoogle", "Pas de resultat Google trouve");
        }
        GestionMessage.message(0, "ParsingGoogle", "Fin du parcours de la reponse Google");
        return enregistrement;
    }

    /**
     * Methode de nettoyage d'un bloc titre + url + desc pour un resulat commun
     *
     * @param blocHtml Une chaine de caractere.
     * @param rang     le rang du resultat
     */
    private static void blocParsing(String blocHtml, int rang) {
        lien = new Lien();
        lien.setRang(rang);
        // Nettoyage de printemps du bloc
        // compilation de regex
        Pattern htmlNettoyage = Pattern.compile("<a class=l href=\"(.+)<nobr>.*");
        Matcher m = htmlNettoyage.matcher(blocHtml);
        boolean test = m.matches();
        if (test || m.groupCount() == 2) {
            // Maintenant on matche les blocs de resultats
            // Compilation de la regex
            Pattern blocResultat = Pattern.compile("(http.*)" + // 1er bloc
                    // (url)
                    "\">" + "(.*)" + // 2eme bloc (titre)
                    "</a>.*<td class=j><font size=-1>" + "(.*)" + // 3eme bloc
                    // (desc)
                    "<font color=#008000>" + ".*");
            // separation en sous-chaines
            Matcher m2 = blocResultat.matcher(m.group(1).replace("</a> ]", ""));
            boolean test2 = m2.matches();

            if (test2) {
                for (int j = 1; j <= m2.groupCount(); j++) {
                    switch (j) {
                        case 1:
                            lien.setUrl(nettoyageBalisesSimple(m2.group(j)));
                            break;

                        case 2:
                            lien.setTitre(nettoyageBalisesSimple(m2.group(j)));
                            break;

                        default:
                            // Attention au bloc fichier Inconnu !!!
                            if (j == 3 && m2.group(3).contains("<font color=#6f6f6f>")) {
                                // On nettoie le bloc de ses balises superflus
                                // tout est OK on initialise un resultat en
                                // nettoyant les balises simples
                                lien.setDesc(nettoyageBalisesSimple(m2.group(3).replaceAll("<font(.*)</a>", "")));
                            } else {
                                // tout est OK on initialise un resultat en
                                // nettoyant les balises simples
                                lien.setDesc(nettoyageBalisesSimple(m2.group(j)));
                            }
                    }
                }
            } else {
                lien.setUrl(" ");
                lien.setTitre(" ");
                lien.setDesc(" ");
                // Erreur de parsing
                GestionMessage.message(1, "ParsingGoogle", "Erreur dans le 2eme parsing d'un bloc de r�sulat");
            }
        } else {
            lien.setUrl(" ");
            lien.setTitre(" ");
            lien.setDesc(" ");
            // Erreur de parsing
            GestionMessage.message(1, "ParsingGoogle", "Erreur dans le 1er parsing d'un bloc de r�sulat");
        }
        enregistrement.add(lien);
        lien = null;
    }

    /**
     * Methode de nettoyage d'un bloc titre + url + desc pour un resulat document
     * [PDF|PS|...]
     *
     * @param blocHtml Une chaine de caractere.
     * @param rang     le rang du resultat
     */
    private static void blocParsingDoc(String blocHtml, int rang) {
        lien = new Lien();
        lien.setRang(rang);
        // Nettoyage de printemps du bloc document
        // compilation de regex
        Pattern htmlNettoyage = Pattern.compile(".*<a class=l href=\"(.+)<nobr>.*");
        // separation en sous-chaines
        Matcher m = htmlNettoyage.matcher(blocHtml);
        boolean test = m.matches();
        // Des sous chaines ont-elles ete trouvees ?
        if (test || m.groupCount() == 2) {
            // Maintenant on matche les blocs de resultats
            // Compilation de la regex
            Pattern blocResultat = Pattern.compile("(http://.*)" + // 1er bloc
                    // (url)
                    "\">" + "(.*)" + // 2eme bloc (titre)
                    "</a>.*<td class=j><font size=-1>.*[</a>|</span>]<br>" + "(.*)" + // 3eme bloc (descriptif)
                    "<br><font color=#008000>" + ".*");
            // separation en sous-chaines
            // "</a> ]" retire pour aider la moulinette et enlever un 1/2 lien
            // incongru
            Matcher m2 = blocResultat.matcher(m.group(1).replace("</a> ]", ""));
            boolean test2 = m2.matches();
            // le matching a-t-il fonctionne ?
            if (test2) {
                lien.setRang(rang);
                for (int j = 1; j <= m2.groupCount(); j++) {
                    // tout est OK on initialise un resultat en nettoyant les
                    // balises simples
                    switch (j) {
                        case 1:
                            lien.setUrl(nettoyageBalisesSimple(m2.group(j)));
                            break;

                        case 2:
                            lien.setTitre(nettoyageBalisesSimple(m2.group(j)));
                            break;

                        default:
                            lien.setDesc(nettoyageBalisesSimple(m2.group(j)));
                    }
                }

            } else {
                lien.setUrl(" ");
                lien.setTitre(" ");
                lien.setDesc(" ");
                // Erreur de parsing
                GestionMessage.message(1, "ParsingGoogle", "Erreur dans le 2eme parsing d'un bloc de r�sulat document");
            }
        } else {
            lien.setUrl(" ");
            lien.setTitre(" ");
            lien.setDesc(" ");
            // Erreur de parsing
            GestionMessage.message(1, "ParsingGoogle", "Erreur dans le 1er parsing d'un bloc de r�sulat document");
        }
        enregistrement.add(lien);
        lien = null;
    }

    /**
     * Methode qui enleve les balises simples d'une chaine de caracteres balises
     * simples : <b>, </b>, ... balises sans attributs Cette methode est non
     * sensible a la casse
     *
     * @param chaine Une chaine de caractere.
     * @return chaine La chaine nettoyee.
     */
    private static String nettoyageBalisesSimple(String chaine) {
        return chaine.replaceAll("(?i)<[a-z]+>|</[a-z]+>", "");
    }
}
