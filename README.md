# JPAController
Revamping an old JPAController template for everyone to use

# Java 8+ Version
If you would like the Java 7 compatible version or just don't like working with Optional class please check [java7 branch](https://github.com/daaso-consultancy/ConciseXMLParser/tree/java7 'java7 branch').

# Include Dependency using Maven
    <dependency>
        <groupId>com.daaso-consultancy</groupId>
        <artifactId>jpa-util</artifactId>
        <version>1.0-java8</version>
    </dependency>

# Package Overview
## EntityJpaControllerAbstract.java
The class that needs to be extended. The first generic is the type of the @Entity it is intended to work with and the second is the same type as the @Id or @EmbeddedId of the class.

This class contains all the default operations needed for most cases and can still be used even when overridden.

## EntityPersistenceProperties.java
A container that only needs to be used when trying to run JpaController operations with non-default behaviour.

By default, autocommit is true. If you would like to not autocommit then see example 3 below.

The EntityPersistenceProperties object would need to then be passed when calling one of the operations available in EntityJpaControllerAbstract class

# Package Interface
## EntityJpaControllerAbstract.java
### Accessible Constructors
    public EntityJpaControllerAbstract(EntityManagerFactory emf, Class<E> entityClass)

### Accessible Methods
    public void create(E entity) throws PreexistingEntityException, IllegalArgumentException, Exception
    public void create(E entity, EntityPersistenceProperties properties) throws PreexistingEntityException, IllegalArgumentException, Exception
    public void destroy(F id) throws NonexistentEntityException, RollbackFailureException, Exception
    public void destroy(F id, EntityPersistenceProperties properties) throws NonexistentEntityException, RollbackFailureException, Exception
    public void edit(E entity) throws NonexistentEntityException, Exception
    public void edit(E entity, EntityPersistenceProperties properties) throws NonexistentEntityException, Exception
    public List<E> findEntities()
    public List<E> findEntities(EntityPersistenceProperties properties
    public List<E> findEntities(int maxResults, int firstResult)
    public List<E> findEntities(int maxResults, int firstResult, EntityPersistenceProperties properties)
    public Optional<E> findEntity(F id)
    public int getEntityCount()
    public EntityManager getEntityManager()
    protected abstract F getId(E entity)

## EntityPersistenceProperties.java
### Accessible Methods
    public void setAutoCommit(boolean autoCommit)
    public boolean isAutoCommit()
    public EntityManager getEntityManager()
    public void setEntityManager(EntityManager entityManager)    
    public static EntityPersistenceProperties getDefault()    

# Pre-requisite
You should already have a JPA entity created. Below is a modified version of one of my files which we will use for the examples that follow.

## Interaction.java
    @Entity
    public class Interaction {

        private static final long serialVersionUID = 1L;

        @Id
        @Column(name = "interaction_id", nullable = false, updatable = false, unique=true)
        @Type(type="uuid-char")
        private UUID interactionId;

        @Column(name = "created", nullable = false, updatable = false, unique=false)
        private LocalDateTime created = LocalDateTime.now();

        @Column(name = "title", nullable = false, updatable = true, length=35, unique=false)
        private String title;

        @Enumerated(EnumType.STRING)
        @Column(name = "type", nullable=true, updatable=true, length=20)
        private InteractionType type;

        public UUID getInteractionId() {
            return interactionId;
        }

        public void setInteractionId(UUID interactionId) {
            this.interactionId = interactionId;
        }

        public LocalDateTime getCreated() {
            return created;
        }

        public void setCreated(LocalDateTime created) {
            this.created = created;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public InteractionType getType() {
            return type;
        }

        public void setType(InteractionType type) {
            this.type = type;
        }   

    }

# Example 1 : Extending the EntityJpaControllerAbstract class correctly
## InteractionJpaController.java
    public class InteractionJpaController extends EntityJpaControllerAbstract<Interaction, UUID> {

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

Note a few things above:
- EntityJpaControllerAbstract needs to be extended.
 - The left-most Generic is actual entity for which this JPA is intended to work with (in this case Interaction)
 - The second is the type of the key (UUID in this case)
    extends EntityJpaControllerAbstract<Interaction, UUID>
- getId method MUST be overridden
 - The return type is the type of the key for the entity (in this case UUID)
 - The parameter must include the entity for which the JpaController is working with (Interaction)
    @Override
    protected UUID getId(Interaction entity) {
        return entity.getInteractionId();
    }
- Constructor should be overriden
 - First parameter is ALWAYS of type EntityManagerFactory
 - The second parameter is ALWAYS a Class object that contains the pertinent Entity class
    public InteractionJpaController(EntityManagerFactory emf, Class<Interaction> entityClass) {
        super(emf, entityClass);
    }
- Operations can be overridden from EntityJpaControllerAbstract
 - When overriding an operation it is recommended you call upon the original method using super variable, such as 
    super.create(interaction, epp);
   
# Example 2 : Using JpaController to call an operation
    Interaction interaction = new Interaction();
    //Set members for interaction

    InteractionJpaController interactionJpaController = new InteractionJpaController(emf, Interaction.class);
    interactionJpaController.create(interaction);

Note:
- Second parameter in the constructor of a JpaController should always be the entity class pertinent to it.

Using try-catch blocks is recommended.    

# Example 3 : Turning autocommit off
When autocommit is turned off, an entity manager must be supplied. This is how you would setup EntityPersistenceProperties.
    EntityManagerFactory emf = //Load emf here;
    EntityManager em = emf.createEntityManager();

    EntityPersistenceProperties epp = new EntityPersistenceProperties();
    epp.setAutoCommit(false);
    epp.setEntityManager(em);

You would then need to pass epp into one of the relevant overloaded operation such as create(entity, epp).
    em.getTransaction().begin();

    InteractionJpaController interactionJpaController = new InteractionJpaController(emf, Interaction.class);
    interactionJpaController.create(interaction, epp);

    //More DB interactions...

    em.getTransaction().commit();

Remember, when autocommit is turned off, it is the responsibility of the programmer to start and complete/revert a transaction.

# Feedback and Suggestions
Please report new issues or desired features to the below email and vote on open issues for which you would like to see the implementation happen. This will help us prioritize the issues.

Email: community@daaso-consultancy.com