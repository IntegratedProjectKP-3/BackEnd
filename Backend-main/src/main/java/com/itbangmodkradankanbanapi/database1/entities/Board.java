package com.itbangmodkradankanbanapi.database1.entities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    @Id
    @Column(name = "id",length = 10)
    private String id;
    @Column(nullable = false, name = "name",length = 120)
    private String name;
    @Column(name = "owner_id")
    private String ownerId;
    @Column(name = "created_on" , insertable = false , updatable = false)
    private Date createdOn;
    @Column(name = "updated_on", insertable = false , updatable = false)
    private Date updatedOn;
    @Column(name = "visibility")
    private String visibility;
}