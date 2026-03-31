using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Exceptions;
using teledon_management_ui.Messages;
using teledon_management_ui.Protos;
using Charity = teledon_management_ui.Models.Charity;
using CharityDto = teledon_management_ui.Models.dto.CharityDto;

namespace teledon_management_ui.Services;

public class CharityService : ICharityService
{
    private readonly INetworkService _networkService;

    public CharityService(INetworkService networkService)
    {
        _networkService = networkService;
        _networkService.OnUpdateReceived += HandleUpdate;
    }

    private void HandleUpdate(MainMessage message)
    {
        if (message.PayloadCase == MainMessage.PayloadOneofCase.CharityRes &&
            message.CharityRes.BodyCase == CharityDtoResponse.BodyOneofCase.CreateBody)
        {
            var dto = message.CharityRes.CreateBody.Charity;
            WeakReferenceMessenger.Default.Send(
                new BroadcastedCreateCharityMessage(new Charity(dto.Id, dto.Name)));
        }
    }

    public async Task<List<CharityDto>> AllCharitiesWithRaisedSums()
    {
        var response = await _networkService.SendRequestAsync(new MainMessage
        {
            CharityReq = new CharityDtoRequest
            {
                GetBody = new GetCharityRequestBody()
            }
        });

        if (response.CharityRes.Status == ResponseStatus.Failed) return [];

        return response.CharityRes.GetBody.Charities.ToList()
            .ConvertAll(p => new CharityDto(p.Id, p.Name, p.RaisedSum));
    }

    public async Task<Charity> Create(string name)
    {
        var response = await _networkService.SendRequestAsync(new MainMessage
        {
            CharityReq = new CharityDtoRequest
            {
                CreateBody = new CreateCharityRequestBody
                {
                    Name = name
                }
            }
        });

        if (response.CharityRes.Status == ResponseStatus.Failed)
        {
            throw new ServiceException("Failed to create charity");
        }

        var dto = response.CharityRes.CreateBody.Charity;

        return new Charity(dto.Id, dto.Name);
    }
}