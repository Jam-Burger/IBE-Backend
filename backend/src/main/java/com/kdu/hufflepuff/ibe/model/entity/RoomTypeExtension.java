package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_type_extension")
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeExtension extends BaseEntity {
    @Column(name = "rating")
    private Double rating;

    @Column(name = "num_of_reviews")
    private Long noOfReviews;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @ElementCollection
    @Column(name = "amenity")
    @CollectionTable(name = "room_type_amenities", joinColumns = @JoinColumn(name = "room_type_id"))
    private List<String> amenities = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @Column(name = "image")
    @CollectionTable(name = "room_type_images", joinColumns = @JoinColumn(name = "room_type_id"))
    private List<String> images = new ArrayList<>();
}
