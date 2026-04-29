package me.alexutzzu.teledon.controller;

import me.alexutzzu.teledon.lib.ClientConnection;
import me.alexutzzu.teledon.protos.DonationProtos;
import me.alexutzzu.teledon.protos.MainMessageProtos;
import me.alexutzzu.teledon.protos.ResponseStatusProtos;
import me.alexutzzu.teledon.service.DonationService;
import org.springframework.stereotype.Controller;

@Controller
public class DonationController implements RequestHandler {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @Override
    public MainMessageProtos.MainMessage.PayloadCase getHandlerType() {
        return MainMessageProtos.MainMessage.PayloadCase.DONATIONREQ;
    }

    private DonationProtos.DonationResponse createDonation(DonationProtos.CreateDonationRequestBody requestBody) {
        var donation = donationService.createDonation(requestBody.getCharityId(), requestBody.getDonorId(), requestBody.getAmount());

        return DonationProtos.DonationResponse.newBuilder()
                .setStatus(ResponseStatusProtos.ResponseStatus.OK)
                .setCreateBody(
                        DonationProtos.CreateDonationResponseBody.newBuilder()
                                .setDonation(donation)
                                .build()
                ).build();
    }

    @Override
    public void handleRequest(MainMessageProtos.MainMessage request, ClientConnection connection) {
        if (request.getPayloadCase() != getHandlerType()) {
            throw new RuntimeException("Handler called on incompatible request");
        }

        var response = createDonation(request.getDonationReq().getCreateBody());

        var message = MainMessageProtos.MainMessage.newBuilder()
                .setDonationRes(response)
                .build();

        connection.send(message);

        if (message.getDonationRes().getStatus() == ResponseStatusProtos.ResponseStatus.OK) {
            connection.broadcast(message.toBuilder().setIsUpdatePayload(true).build());
        }
    }
}
