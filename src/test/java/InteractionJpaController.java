/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.daaso.jpa.EntityJpaControllerAbstract;
import com.daaso.jpa.EntityPersistenceProperties;
import com.daaso.jpa.exception.PreexistingEntityException;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.EntityManagerFactory;
/**
 *
 * @author Omar
 */
public class InteractionJpaController extends EntityJpaControllerAbstract<Interaction, UUID> implements Serializable {

    public InteractionJpaController(EntityManagerFactory emf, Class<Interaction> entityClass) {
        super(emf, entityClass);
    }

    @Override
    protected UUID getId(Interaction entity) {
        return entity.getInteractionId();
    }
    
    @Override
    public void create (Interaction interaction, EntityPersistenceProperties epp) throws PreexistingEntityException, IllegalArgumentException, Exception {
        //Select a UUID that is not already in use
        UUID uuid = UUID.randomUUID();
        
        while (findEntity(uuid) != null) {
            uuid = UUID.randomUUID();
        }
        
        //Update value and call appropriate method
        interaction.setInteractionId(uuid);
        
        //Call parent method
        super.create(interaction, epp);
    }
    
}
