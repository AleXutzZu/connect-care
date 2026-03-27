using System.Collections.Generic;
using teledon_management_ui.Models;

namespace teledon_management_ui.Services;

public interface IDonorService
{
    public List<Donor> AllDonors();

    public Donor CreateDonor(string firstName, string lastName, string phoneNumber, string address);
}