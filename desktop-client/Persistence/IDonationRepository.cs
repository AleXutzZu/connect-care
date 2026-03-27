using System.Collections.Generic;
using teledon_management_ui.Models;

namespace teledon_management_ui.Persistence;

public interface IDonationRepository : IBasicRepository<Donation>
{
    public List<Donation> findAllByCharityId(long id);
}