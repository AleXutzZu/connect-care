using System.Collections.Generic;
using teledon_management_ui.Models;

namespace teledon_management_ui.Persistence;

public interface IDonorRepository : IBasicRepository<Donor>
{
    public List<Donor> FindAll();
}