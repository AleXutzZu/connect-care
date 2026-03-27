using System.Collections.Generic;
using teledon_management_ui.Models;

namespace teledon_management_ui.Persistence;

public interface ICharityRepository : IBasicRepository<Charity>
{
    public List<Charity> FindAll();
}