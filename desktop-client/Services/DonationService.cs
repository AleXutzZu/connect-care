using System;
using System.Threading.Tasks;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Exceptions;
using teledon_management_ui.Messages;
using teledon_management_ui.Models;
using teledon_management_ui.Protos;
using Charity = teledon_management_ui.Models.Charity;
using Donation = teledon_management_ui.Models.Donation;

namespace teledon_management_ui.Services;

public class DonationService : IDonationService
{
    private readonly INetworkService _networkService;

    public DonationService(INetworkService networkService)
    {
        _networkService = networkService;
        _networkService.OnUpdateReceived += HandleUpdate;
    }

    private void HandleUpdate(MainMessage message)
    {
        if (message.PayloadCase == MainMessage.PayloadOneofCase.DonationRes &&
            message.DonationRes.BodyCase == DonationResponse.BodyOneofCase.CreateBody)
        {
            var dto = message.DonationRes.CreateBody.Donation;
            WeakReferenceMessenger.Default.Send(
                new BroadcastedCreateDonationMessage(new Donation(dto.Id,
                    new Charity(dto.Charity.Id, dto.Charity.Name),
                    new Donor(dto.Donor.Id, dto.Donor.FirstName, dto.Donor.LastName, dto.Donor.Address,
                        dto.Donor.PhoneNumber),
                    dto.Amount)));
        }
    }

    public async Task<Donation> AddDonationToCharity(long charityId, double donationSum, long donorId)
    {
        var response = await _networkService.SendRequestAsync(new MainMessage
        {
            DonationReq = new DonationRequest
            {
                CreateBody = new CreateDonationRequestBody
                {
                    Amount = donationSum,
                    CharityId = charityId,
                    DonorId = donorId
                }
            }
        });

        if (response.DonationRes.Status == ResponseStatus.Failed)
        {
            throw new ServiceException("Could not create donation");
        }

        var donation = response.DonationRes.CreateBody.Donation;
        return new Donation(
            donation.Id,
            new Charity(donation.Charity.Id, donation.Charity.Name),
            new Donor(donation.Donor.Id, donation.Donor.FirstName, donation.Donor.LastName, donation.Donor.Address,
                donation.Donor.PhoneNumber),
            donation.Amount);
    }
}