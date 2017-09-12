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
package com.daaso.jpa;

import com.daaso.jpa.exception.NonexistentEntityException;
import com.daaso.jpa.exception.PreexistingEntityException;
import com.daaso.jpa.exception.RollbackFailureException;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


/**
 * An abstract class adding to the Jpa Controller Class from Entities template in Netbeans.
 * This is Java 7 and lower version.
 * 
 * @author Omar Abdel Bari
 * @param <E>
 * @param <F>
 */
public abstract class EntityJpaControllerAbstract<E,F> implements java.io.Serializable {
    /**
     * Constructor
     * 
     * @param emf
     * @param entityClass 
     */
    public EntityJpaControllerAbstract(EntityManagerFactory emf, Class<E> entityClass) {
        this.emf = emf;
        this.entityClass = entityClass;
    }
    
    /**FIELDS**/
    private EntityManagerFactory emf = null;
    final private Class<E> entityClass;

    /**GETTERS**/
    /**
     * 
     * @return 
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    /**
     * Users will be required to specify the method of retrieving primary key from this entity
     * @param entity
     * @return 
     */
    protected abstract F getId(E entity);

    /**
     * Persists a new object using default parameters
     * 
     * @param entity 
     * @throws com.daaso.jpa.exception.PreexistingEntityException 
     */
    public void create(E entity) throws PreexistingEntityException, IllegalArgumentException, Exception {
        create(entity, EntityPersistenceProperties.getDefault());
    }
    
    /**
     * Persists a new object
     * 
     * @param entity 
     * @param properties
     * @throws com.daaso.jpa.exception.PreexistingEntityException
     */
    public void create(E entity, EntityPersistenceProperties properties) throws PreexistingEntityException, IllegalArgumentException, Exception {
        EntityManager em = null;
        boolean isSuccessful = false;
        Exception exception = null;
        boolean isAutoCommit = properties.isAutoCommit();
        
        try {
            //If autocommit is on obtain entity manager from factory, otherwise use the one user provides
            if (isAutoCommit) {
                em = getEntityManager();
                em.getTransaction().begin();
            } else {
                em = properties.getEntityManager();
            }
            
            em.persist(entity);

            if (isAutoCommit) {
                em.getTransaction().commit();
            }
            
            isSuccessful = true;
            
        } catch (EntityExistsException ex) {
            exception = ex;
            throw new PreexistingEntityException("Entity already exists.", ex);
            
        } catch (IllegalArgumentException ex) {
            exception = ex;
            throw new IllegalArgumentException ("The provided object is not an entity.", ex);
            
        } /* catch (RollbackFailureException ex) {
            exception = ex;
            throw new RollbackException ("Transaction failed and rolled back", ex);
            
        } */ catch (Exception ex) {
            exception = ex;
            throw new Exception("Unidentified Error", ex);
        }
        
        finally {
            if (!isSuccessful) {
                try {
                    em.getTransaction().rollback();
                } catch (Exception re) {
                    throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
                }
                if (findEntity(getId(entity)).isPresent()) {
                    throw new PreexistingEntityException("Entity with the specified key already exists", exception);
                }
                throw exception;
            }
            if (em != null && isAutoCommit) {
                em.close();
            }
        }
    }

    /**
     * Edits an existing entity
     * 
     * @param entity
     * @throws NonexistentEntityException
     * @throws Exception 
     */
    public void edit(E entity) throws NonexistentEntityException, Exception {
        edit(entity, EntityPersistenceProperties.getDefault());
    }
    
    /**
     * Edits an existing entity
     * 
     * @param entity
     * @param properties
     * @throws NonexistentEntityException
     * @throws Exception 
     */
    public void edit(E entity, EntityPersistenceProperties properties) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        boolean isAutoCommit = properties.isAutoCommit();
        
        try {
            
            if(isAutoCommit) {
                em = getEntityManager();
                em.getTransaction().begin();
            } else {
                em = properties.getEntityManager();
            }
            
            entity = em.merge(entity);
            
            if (isAutoCommit) {
                em.getTransaction().commit();
            }
            
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                F id = getId(entity);
                if (findEntity(id) == null) {
                    throw new NonexistentEntityException("The entity with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null && isAutoCommit) {
                em.close();
            }
        }
    }

    /**
     * 
     * @param id
     * @throws NonexistentEntityException
     * @throws RollbackFailureException
     * @throws Exception 
     */
    public void destroy(F id) throws NonexistentEntityException, RollbackFailureException, Exception {
        destroy(id, EntityPersistenceProperties.getDefault());
    }
    
    /**
     * 
     * @param id
     * @param properties
     * @throws NonexistentEntityException
     * @throws RollbackFailureException
     * @throws Exception 
     */
    public void destroy(F id, EntityPersistenceProperties properties) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        boolean isAutoCommit = properties.isAutoCommit();
        
        try {
            if (isAutoCommit) {
                em = getEntityManager();
                em.getTransaction().begin();
            } else {
                em = properties.getEntityManager();
            }
            
            E entity;
            try {
                entity = em.getReference( entityClass, id);
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The entity with id " + id + " no longer exists.", enfe);
            }
            
            em.remove(entity);
            
            if (isAutoCommit) {
                em.getTransaction().commit();
            }
            
        } finally {
            if (em != null && isAutoCommit) {
                em.close();
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    public List<E> findEntities() {
        return findEntities(true, -1, -1, EntityPersistenceProperties.getDefault());
    }

    /**
     * 
     * @param properties
     * @return 
     */
    public List<E> findEntities(EntityPersistenceProperties properties) {
        return findEntities(true, -1, -1, properties);
    }

    /**
     * 
     * @param maxResults
     * @param firstResult
     * @return 
     */
    public List<E> findEntities(int maxResults, int firstResult) {
        return findEntities(false, maxResults, firstResult, EntityPersistenceProperties.getDefault());
    }
    
    /**
     * 
     * @param maxResults
     * @param firstResult
     * @param properties
     * @return 
     */
    public List<E> findEntities(int maxResults, int firstResult, EntityPersistenceProperties properties) {
        return findEntities(false, maxResults, firstResult, properties);
    }

    /**
     * 
     * @param all
     * @param maxResults
     * @param firstResult
     * @param properties
     * @return 
     */
    private List<E> findEntities(boolean all, int maxResults, int firstResult, EntityPersistenceProperties properties) {
        boolean isAutoCommit = properties.isAutoCommit();
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from( entityClass ));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            if (isAutoCommit) {
                em.close();
            }
        }
    }

    /**
     * Find the entity with the specified key.
     * 
     * @param id
     * @return 
     */
    public Optional<E> findEntity(F id) {
        EntityManager em = getEntityManager();
        Optional<E> optional = Optional.empty();
        
        try {
            E entity = em.find( entityClass, id);
            if (entity != null) {
                optional = Optional.of( entity );
            }
            
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Either entity provided is null or does not match the specified class type.", ex);
        } finally {
            em.close();
        }
        
        return optional;
    }

    /**
     * Get number of existing entities
     * 
     * @return 
     */
    public int getEntityCount() {
        EntityManager em = null;
        
        try {
            em = getEntityManager();
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<E> rt = cq.from( entityClass );
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
