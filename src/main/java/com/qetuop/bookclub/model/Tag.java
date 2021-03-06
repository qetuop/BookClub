package com.qetuop.bookclub.model;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;  //add @Data before class to get auto generated getter, setters, etc https://projectlombok.org/features/Data
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
//@Table(uniqueConstraints={@UniqueConstraint(columnNames = "value")})
public class Tag {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String value;

    @ManyToMany(mappedBy = "tags", fetch=FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Book> books = new HashSet<>();

    // needed for JPA, NoArgsConstructor created by lombok
    protected Tag() {}

    public Tag(String value) {
        this.value = value;
    }

   /* @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder();
        hcb.append(value);
        return hcb.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag that = (Tag) obj;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(value, that.value);
        return eb.isEquals();
    }*/
}
