package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.BookingOperationException;
import com.kdu.hufflepuff.ibe.mapper.GuestExtensionMapper;
import com.kdu.hufflepuff.ibe.model.entity.GuestExtension;
import com.kdu.hufflepuff.ibe.model.graphql.Guest;
import com.kdu.hufflepuff.ibe.repository.jpa.GuestExtensionRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.GuestService;
import com.kdu.hufflepuff.ibe.util.GraphQLMutations;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {
    private final GuestExtensionRepository guestExtensionRepository;
    private final GraphQlClient graphQlClient;

    @Override
    public Guest createGuest(String fullName) {
        return createGuestWithId(fullName, null);
    }

    @Override
    public Guest createGuestWithId(String fullName, Long guestId) {
        try {
            GraphQlClient.RequestSpec requestSpec;

            if (guestId == null) {
                requestSpec = graphQlClient.document(GraphQLMutations.CREATE_GUEST)
                    .variable("guestName", fullName);
            } else {
                requestSpec = graphQlClient.document(GraphQLMutations.CREATE_GUEST_WITH_ID)
                    .variable("guestName", fullName)
                    .variable("guestId", guestId);
            }

            Guest guest = requestSpec
                .retrieve("createGuest")
                .toEntity(Guest.class)
                .block();

            log.info("Guest created in GraphQL: {}", guest);
            return guest;
        } catch (Exception e) {
            log.error("Failed to create guest in GraphQL", e);
            throw BookingOperationException.guestCreationFailed("Failed to create guest in GraphQL");
        }
    }

    @Override
    public Guest findGuestById(Long guestId) {
        try {
            Guest guest = graphQlClient.document(GraphQLQueries.GET_GUEST)
                .variable("guestId", guestId)
                .retrieve("getGuest")
                .toEntity(Guest.class)
                .block();

            log.info("Found guest in GraphQL: {}", guest);
            return guest;
        } catch (Exception e) {
            throw BookingOperationException.guestNotFound(guestId);
        }
    }

    @Override
    @Transactional
    public GuestExtension createGuestExtension(Map<String, String> formData) {
        GuestExtension guestExtension = GuestExtensionMapper.fromFormDataWithValidation(formData);
        return guestExtensionRepository.save(guestExtension);
    }

    @Override
    @Transactional
    public GuestExtension createGuestExtensionWithId(Map<String, String> formData, Long guestId) {
        GuestExtension guestExtension = GuestExtensionMapper.fromFormDataWithValidation(formData);
        guestExtension.setGuestId(guestId);
        return guestExtensionRepository.save(guestExtension);
    }

    @Override
    public GuestExtension findByEmail(String email) {
        return guestExtensionRepository.findByBillingEmail(email).orElse(null);
    }
}