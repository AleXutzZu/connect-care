using teledon_management_ui.Models;
using teledon_management_ui.Persistence;

namespace teledon_management_ui.Services;

public class DonationService(
    ICharityRepository charityRepository,
    IDonationRepository donationRepository,
    IDonorRepository donorRepository)
    : IDonationService
{
    public Donation AddDonationToCharity(long charityId, double donationSum, long donorId)
    {
        var charity = charityRepository.FindById(charityId);
        if (charity == null) throw new System.NullReferenceException("Charity does not exist");

        var donor = donorRepository.FindById(donorId);

        if (donor == null) throw new System.NullReferenceException("Donor does not exist");

        var donation = donationRepository.Create(new Donation(0, charity, donor, donationSum));
        return donation;
    }
}