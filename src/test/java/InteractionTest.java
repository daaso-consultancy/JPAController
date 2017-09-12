/*
 * Copyright (C) 2017 omara
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.daaso.jpa.EntityPersistenceProperties;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author omara
 */
public class InteractionTest {
    static EntityManagerFactory emf = null; 
    
    public InteractionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        emf = EntityManagerUtil.getEmf();
    }
    
    @AfterClass
    public static void tearDownClass() {
        if (emf != null) {
            emf.close();
        }
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
        
    }

    /**
     * 
     * @throws IllegalArgumentException
     * @throws Exception 
     */
    //@Test
    public void createDefaultTest1() throws IllegalArgumentException, Exception {
        Interaction interaction = new Interaction();
        interaction.setType(InteractionType.GROUP);
        interaction.setTitle("Chicken");
        interaction.setCreated(LocalDateTime.now());
        
        InteractionJpaController interactionController = new InteractionJpaController(emf, Interaction.class);
        interactionController.create(interaction);
    }
    
    /**
     * Testing autocommit off normal success
     * 
     * @throws IllegalArgumentException
     * @throws Exception 
     */
    //@Test
    public void createWithEPPTest1() throws IllegalArgumentException, Exception {
        EntityManager em = emf.createEntityManager();
        
        EntityPersistenceProperties epp = new EntityPersistenceProperties();
        epp.setAutoCommit(false);
        epp.setEntityManager(em);
        
        Interaction interaction = new Interaction();
        interaction.setType(InteractionType.GROUP);
        interaction.setTitle("Chicken");
        interaction.setCreated(LocalDateTime.now());
        
        em.getTransaction().begin();
        
        InteractionJpaController interactionController = new InteractionJpaController(emf, Interaction.class);
        interactionController.create(interaction, epp);
        
        em.getTransaction().commit();
    }
}
