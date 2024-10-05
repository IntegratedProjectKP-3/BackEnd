package com.itbangmodkradankanbanapi.database1.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.NonNull;

@Data
public class StatusDTO {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "statusId")
        private Integer id;
        @NotBlank
        @Column(name = "statusName")
        private String name;
        @Column(name = "statusDescription")
        private String description;
        public void setName(String name) {
                this.name = name.trim();
        }

        public void setDescription(String description) {
                if(description != null){
                        this.description = description.trim();
                }

        }
}
