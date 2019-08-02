package agent;

/*
 * This file is part of "M�ta-moteur".
 *
 * (c) M�ta-moteur 2005-2006. All Rights Reserved.
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

import org.junit.Test;

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
	private String classToShow = "Ma_Classe";

	@Test
	public void runMessageTests1() {
		/*
		 * les erreurs du syst�me - 2 les erreurs non-bloquantes - 1 les messages de
		 * traitements - 0
		 */
		GestionMessage.message(0, classToShow, "Aie !!!");
	}
	@Test
	public void runMessageTests2() {
		/*
		 * les erreurs du syst�me - 2 les erreurs non-bloquantes - 1 les messages de
		 * traitements - 0
		 */
		GestionMessage.message(1, classToShow, "1 Aie !!!");
	}
	@Test
	public void runMessageTests3() {
		/*
		 * les erreurs du syst�me - 2 les erreurs non-bloquantes - 1 les messages de
		 * traitements - 0
		 */
		GestionMessage.message(2, classToShow, "2 Aie !!!");
	}
}
