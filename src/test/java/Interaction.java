/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

/**
 *
 * @author Omar
 */
@Entity
@Table(name="interaction")
public class Interaction implements Serializable {

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
    
    @Override 
    public String toString() {
        return "com.daaso.profile.entities.ConnectionAssociation["
                + " interactionId=" + interactionId.toString()
                + " created=" + created.toString()
                + " title=" + title
                + " ]";
    }

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
