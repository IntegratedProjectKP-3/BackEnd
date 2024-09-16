package com.itbangmodkradankanbanapi.database1.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "userboard")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserBoard {
    @Id
    @Column(name = "boardId", nullable = false)
    private String boardId;

    @Column(name = "userId", nullable = false)
    private String userId;
}
