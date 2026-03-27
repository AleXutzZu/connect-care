using System.Collections.Generic;
using teledon_management_ui.Models;
using teledon_management_ui.Models.dto;

namespace teledon_management_ui.Services;

public interface ICharityService
{
    public List<CharityDto> AllCharitiesWithRaisedSums();

    public Charity Create(string name);
}