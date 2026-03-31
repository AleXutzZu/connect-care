using System.Threading.Tasks;
using teledon_management_ui.Models;

namespace teledon_management_ui.Services;

public interface IDonationService
{
    public Task<Donation> AddDonationToCharity(long charityId, double donationSum, long donorId);
}