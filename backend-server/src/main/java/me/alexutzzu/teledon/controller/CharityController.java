package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.exception.DatabaseException;
import me.alexutzzu.teledon.protos.CharityProtos;
import me.alexutzzu.teledon.protos.MainMessageProtos;
import me.alexutzzu.teledon.lib.ClientConnection;
import me.alexutzzu.teledon.protos.ResponseStatusProtos;
import me.alexutzzu.teledon.service.CharityService;

public class CharityController implements RequestHandler {
    private final CharityService charityService;

    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }

    private CharityProtos.CharityDtoResponse getAllCharities() {
        try {
            var charities = charityService.getAllCharities();

            return CharityProtos.CharityDtoResponse.newBuilder()
                    .setGetBody(CharityProtos.GetCharityResponseBody.newBuilder().addAllCharities(charities).build())
                    .setStatus(ResponseStatusProtos.ResponseStatus.OK)
                    .build();
        } catch (DatabaseException e) {
            return CharityProtos.CharityDtoResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.FAILED)
                    .build();
        }
    }

    private CharityProtos.CharityDtoResponse createCharity(CharityProtos.CreateCharityRequestBody requestBody) {
        try {
            var charity = charityService.createCharity(requestBody.getName());

            return CharityProtos.CharityDtoResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.OK)
                    .setCreateBody(CharityProtos.CreateCharityResponseBody.newBuilder().setCharity(charity).build())
                    .build();
        } catch (DatabaseException e) {
            return CharityProtos.CharityDtoResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.FAILED)
                    .build();
        }
    }

    @Override
    public MainMessageProtos.MainMessage.PayloadCase getHandlerType() {
        return MainMessageProtos.MainMessage.PayloadCase.CHARITYREQ;
    }

    @Override
    public void handleRequest(MainMessageProtos.MainMessage request, ClientConnection connection) {
        if (request.getPayloadCase() != getHandlerType()) {
            throw new RuntimeException("Handler called on incompatible request");
        }

        CharityProtos.CharityDtoResponse response;
        if (request.getCharityReq().hasGetBody()) {
            response = getAllCharities();
            connection.send(MainMessageProtos.MainMessage.newBuilder().setCharityRes(response).build());
        } else {
            response = createCharity(request.getCharityReq().getCreateBody());
            var message = MainMessageProtos.MainMessage.newBuilder().setCharityRes(response).build();

            connection.send(message);
            if (response.getStatus() == ResponseStatusProtos.ResponseStatus.OK) {
                connection.broadcast(message.toBuilder().setIsUpdatePayload(true).build());
            }
        }
    }
}
