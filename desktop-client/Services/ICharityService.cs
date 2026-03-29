using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using teledon_management_ui.Models;
using teledon_management_ui.Models.dto;

namespace teledon_management_ui.Services;

public interface ICharityService
{
    public Task<List<CharityDto>> AllCharitiesWithRaisedSums();

    public Task<Charity?> Create(string name);
}