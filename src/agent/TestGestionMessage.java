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
/**
 * Classe de tests pour la gestion de message
 * 
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 0.9
 */
public class TestGestionMessage {
	public static void main(String args[]) {
		/*
		 * les erreurs du système - 2 les erreurs non-bloquantes - 1 les messages de
		 * traitements - 0
		 */
		GestionMessage.message(0, "Ma_Classe", "Aie !!!");
		GestionMessage.message(1, "Ma_Classe", "1 Aie !!!");
		GestionMessage.message(2, "Ma_Classe", "2 Aie !!!");
	}
}
