/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.citius.reservas.models;

//import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonManagedReference;

/**
 *
 * @author Esther
 */
@Entity
@DiscriminatorValue("1")    //Es un grupo
@Table(name = "resources_groups")
@NamedQueries(value = {
    @NamedQuery(name = "ResourceGroup.findByName", query = "SELECT rg FROM ResourceGroup rg WHERE rg.name = :name"),
    @NamedQuery(name = "ResourceGroup.findAll", query = "SELECT rg FROM ResourceGroup rg"),})
@XmlRootElement
public class ResourceGroup implements Serializable {

    private static final long serialVersionUID = 2L;
    
    @Id
    @Min(1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @NotNull(message = "error.form.resource.name.required")
    @Size(min = 1, max = 100, message = "error.form.resource.name.size")
    private String name;
    
    private String description;
    
    //Child - serialized
    @JsonManagedReference
    @OneToMany(mappedBy = "group",fetch = FetchType.EAGER)
    private Set<Resource> resources;

    public ResourceGroup() {
    }
    
    public ResourceGroup(Integer id) {
        this.id = id;
    }

    public ResourceGroup(String name) {
        this.name=name;
    }

    public ResourceGroup(String name, Set<Resource> resources) {
        this.name=name;
        this.resources = resources;
    }
    
    public ResourceGroup(String name, String description, Set<Resource> resources) {
        this.name=name;
        this.description=description;
        this.resources = resources;
    }

    @XmlTransient
    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourceGroup other = (ResourceGroup) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResourceGroup{" + "id=" + id + ", name=" + name + ", description=" + description + '}';
    }
}
