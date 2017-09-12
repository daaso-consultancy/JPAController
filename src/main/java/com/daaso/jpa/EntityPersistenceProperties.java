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

import javax.persistence.EntityManager;

/**
 *
 * @author Omar
 */
public class EntityPersistenceProperties {
    /*FIELDS*/
    private boolean autoCommit = true;
    private EntityManager entityManager;

    /*SETTERS AND GETTERS*/
    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /*GET DEFAULT*/
    public static EntityPersistenceProperties getDefault() {
        return new EntityPersistenceProperties();
    }
}
