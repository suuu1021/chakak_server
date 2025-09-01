package com.green.chakak.chakak.photographer.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@Table(name = "photographer_map")
@Entity
public class PhotographerMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photographer_id", nullable = false)
    private PhotographerProfile photographerProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private PhotographerCategory photographerCategory;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public PhotographerMap(PhotographerProfile photographerProfile, PhotographerCategory photographerCategory){
        this.photographerProfile = photographerProfile;
        this.photographerCategory = photographerCategory;
    }

}
