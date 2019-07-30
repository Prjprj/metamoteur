package client;

import agent.Agent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import serveur.Serveur;

import static org.junit.Assert.assertNotNull;

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
/**
 * Classe de tests pour le client HTTP
 * 
 * @author Jeremy Frechard
 * @author Cecile Girard
 * @author Aysel Gunes
 * @author Pierre Ramos
 * @version 1.0
 */
public class TestClient {

	@Before
	public void setUp() throws Exception {
		new Agent();
		Serveur.service();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void runClientTests() {
		// tentative de requetes sur le serveur HTTP installe sur la machine du testeur
		assertNotNull(Client.envoiRequeteGET("http://localhost"));
		assertNotNull(Client.envoiRequetePOST("localhost", "/", "essai", 80));
	}
}
