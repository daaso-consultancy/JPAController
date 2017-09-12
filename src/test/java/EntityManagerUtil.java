/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Omar
 */
public final class EntityManagerUtil {
    private static EntityManagerFactory emf ;
    
    private EntityManagerUtil() {}

    public static EntityManagerFactory getEmf() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("com.daaso_jpa-postgresql");
        }
        return emf;
    }
}
