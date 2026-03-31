package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.exception.DatabaseException;
import me.alexutzzu.teledon.lib.ClientConnection;
import me.alexutzzu.teledon.protos.DonorProtos;
import me.alexutzzu.teledon.protos.MainMessageProtos;
import me.alexutzzu.teledon.protos.ResponseStatusProtos;
import me.alexutzzu.teledon.service.DonorService;

public class DonorController implements RequestHandler {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    private DonorProtos.DonorDtoResponse handleGet(DonorProtos.GetDonorRequestBody requestBody) {
        if (requestBody.hasId()) {
            long id = requestBody.getId();

            try {
                var donor = donorService.getDonor(id);
                DonorProtos.DonorDtoResponse.newBuilder()
                        .setStatus(ResponseStatusProtos.ResponseStatus.OK)
                        .setGetBody(DonorProtos.GetDonorResponseBody.newBuilder().addAllDonors(donor.stream().toList()).build())
                        .build();
            } catch (DatabaseException e) {
                return DonorProtos.DonorDtoResponse.newBuilder()
                        .setStatus(ResponseStatusProtos.ResponseStatus.FAILED)
                        .build();
            }
        }
        try {
            var donors = donorService.getAllDonors();

            return DonorProtos.DonorDtoResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.OK)
                    .setGetBody(DonorProtos.GetDonorResponseBody.newBuilder().addAllDonors(donors).build())
                    .build();

        } catch (DatabaseException e) {
            return DonorProtos.DonorDtoResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.FAILED)
                    .build();
        }
    }

    private DonorProtos.DonorDtoResponse handleCreate(DonorProtos.CreateDonorRequestBody requestBody) {
        try {
            var donor = donorService.createDonor(requestBody.getFirstName(), requestBody.getLastName(), requestBody.getAddress(), requestBody.getPhoneNumber());

            return DonorProtos.DonorDtoResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.OK)
                    .setCreateBody(DonorProtos.CreateDonorResponseBody.newBuilder().setDonor(donor).build())
                    .build();

        } catch (DatabaseException e) {
            return DonorProtos.DonorDtoResponse.newBuilder()
                    .setStatus(ResponseStatusProtos.ResponseStatus.FAILED)
                    .build();
        }
    }

    @Override
    public MainMessageProtos.MainMessage.PayloadCase getHandlerType() {
        return MainMessageProtos.MainMessage.PayloadCase.DONORREQ;
    }

    @Override
    public void handleRequest(MainMessageProtos.MainMessage request, ClientConnection connection) {
        if (request.getPayloadCase() != getHandlerType()) {
            throw new RuntimeException("Handler called on incompatible request");
        }

        DonorProtos.DonorDtoResponse response;

        if (request.getDonorReq().hasGetBody()) {
            response = handleGet(request.getDonorReq().getGetBody());
            connection.send(MainMessageProtos.MainMessage.newBuilder().setDonorRes(response).build());
        } else {
            response = handleCreate(request.getDonorReq().getCreateBody());

            var message = MainMessageProtos.MainMessage.newBuilder()
                    .setDonorRes(response)
                    .build();

            connection.send(message);

            if (message.getDonorRes().getStatus() == ResponseStatusProtos.ResponseStatus.OK) {
                connection.broadcast(message.toBuilder().setIsUpdatePayload(true).build());
            }
        }

    }
}
