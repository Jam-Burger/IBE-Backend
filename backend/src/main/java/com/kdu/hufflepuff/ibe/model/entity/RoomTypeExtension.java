package com.kdu.hufflepuff.ibe.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "room_type_extension")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Long roomTypeId;

    @ElementCollection
    @CollectionTable(
        name = "room_type_image",
        joinColumns = @JoinColumn(name = "room_type_id")
    )
    @Column(name = "image_url", length = 1024)
    private List<String> imageUrls;
}