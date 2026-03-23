using System.Collections.Generic;
using System.Linq;
using teledon_management_ui.Models;
using teledon_management_ui.Models.dto;
using teledon_management_ui.Persistence;

namespace teledon_management_ui.Services;

public class CharityService(ICharityRepository charityRepository, IDonationRepository donationRepository)
    : ICharityService
{
    public List<CharityDto> AllCharitiesWithRaisedSums()
    {
        var charities = charityRepository.FindAll().ConvertAll(c =>
        {
            var donations = donationRepository.findAllByCharityId(c.Id);

            var donatedSum = donations.Sum(donation => donation.Amount);

            return new CharityDto(c.Id, c.Name, donatedSum);
        });
        return charities;
    }

    public Charity Create(string name)
    {
        return charityRepository.Create(new Charity(0, name));
    }
}