package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.BookingOperationException;
import com.kdu.hufflepuff.ibe.model.graphql.Guest;
import com.kdu.hufflepuff.ibe.service.interfaces.GuestService;
import com.kdu.hufflepuff.ibe.util.GraphQLMutations;
import com.kdu.hufflepuff.ibe.util.GraphQLQueries;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {
    private final GraphQlClient graphQlClient;

    @Override
    public Guest createGuestWithId(String fullName, Long guestId) {
        try {
            return graphQlClient.document(GraphQLMutations.CREATE_GUEST)
                .variable("guestId", guestId)
                .variable("guestName", fullName)
                .retrieve("createGuest")
                .toEntity(Guest.class)
                .block();
        } catch (Exception e) {
            throw BookingOperationException.guestCreationFailed("Failed to create guest in GraphQL");
        }
    }

    @Override
    public Guest findGuestById(Long guestId) {
        try {
            return graphQlClient.document(GraphQLQueries.GET_GUEST)
                .variable("guestId", guestId)
                .retrieve("getGuest")
                .toEntity(Guest.class)
                .block();
        } catch (Exception e) {
            throw BookingOperationException.guestNotFound(guestId);
        }
    }
} 