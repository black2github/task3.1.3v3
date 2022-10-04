package ru.kata.spring.boot_security.demo.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
// @NamedEntityGraph(name = "Role.users",
//         attributeNodes = @NamedAttributeNode("users")
// )
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true)
    private String name;

    // @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    // @JoinColumn
    // private Set<User> users = new LinkedHashSet<>();

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public Set<User> getUsers() {
    //     return users;
    // }
    //
    // public void setUsers(Set<User> users) {
    //     this.users = users;
    // }

    @Override
    public String toString() {
        return String.format("Role{id=%d, name='%s'}", id, name);
    }

    @Override
    public int hashCode() {
        //return Objects.hash(id, name);
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != Role.class)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        return (getName().equals(((Role) o).getName()));
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
