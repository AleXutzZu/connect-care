using teledon_management_ui.Models;

namespace teledon_management_ui.Services;

public interface IDonationService
{
    public Donation AddDonationToCharity(long charityId, double donationSum, long donorId);
}