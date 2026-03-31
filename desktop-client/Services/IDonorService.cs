using System.Collections.Generic;
using System.Threading.Tasks;
using teledon_management_ui.Models;

namespace teledon_management_ui.Services;

public interface IDonorService
{
    public Task<List<Donor>> AllDonors();

    public Task<Donor> CreateDonor(string firstName, string lastName, string phoneNumber, string address);
}