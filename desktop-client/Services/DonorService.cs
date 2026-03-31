using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Exceptions;
using teledon_management_ui.Messages;
using teledon_management_ui.Models;
using teledon_management_ui.Persistence;
using teledon_management_ui.Protos;

namespace teledon_management_ui.Services;

public class DonorService : IDonorService
{
    private readonly INetworkService _networkService;

    public DonorService(INetworkService networkService)
    {
        _networkService = networkService;
        _networkService.OnUpdateReceived += HandleUpdate;
    }
    
    private void HandleUpdate(MainMessage message)
    {
        if (message.PayloadCase == MainMessage.PayloadOneofCase.DonorRes &&
            message.DonorRes.BodyCase == DonorDtoResponse.BodyOneofCase.CreateBody)
        {
            var dto = message.DonorRes.CreateBody.Donor;
            WeakReferenceMessenger.Default.Send(
                new BroadcastedCreateDonorMessage(new Donor(dto.Id, dto.FirstName, dto.LastName, dto.Address, dto.PhoneNumber)));
        }
    }

    public async Task<List<Donor>> AllDonors()
    {
        var response = await _networkService.SendRequestAsync(new MainMessage
        {
            DonorReq = new DonorDtoRequest
            {
                GetBody = new GetDonorRequestBody()
            }
        });
        if (response.DonorRes.Status == ResponseStatus.Failed) return [];

        return response.DonorRes.GetBody.Donors.ToList()
            .ConvertAll(d => new Donor(d.Id, d.FirstName, d.LastName, d.Address, d.PhoneNumber));
    }

    public async Task<Donor> CreateDonor(string firstName, string lastName, string phoneNumber, string address)
    {
        var response = await _networkService.SendRequestAsync(new MainMessage
        {
            DonorReq = new DonorDtoRequest()
            {
                CreateBody = new CreateDonorRequestBody
                {
                    Address = address,
                    FirstName = firstName,
                    LastName = lastName,
                    PhoneNumber = phoneNumber
                }
            }
        });

        if (response.DonorRes.Status == ResponseStatus.Failed)
        {
            throw new ServiceException("Failed to create donor");
        }

        var dto = response.DonorRes.CreateBody.Donor;

        return new Donor(dto.Id, dto.FirstName, dto.LastName, dto.Address, dto.PhoneNumber);
    }
}