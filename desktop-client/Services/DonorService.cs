using System.Collections.Generic;
using teledon_management_ui.Models;
using teledon_management_ui.Persistence;

namespace teledon_management_ui.Services;

public class DonorService(IDonorRepository donorRepository) : IDonorService
{
    public List<Donor> AllDonors()
    {
        return donorRepository.FindAll();
    }

    public Donor CreateDonor(string firstName, string lastName, string phoneNumber, string address)
    {
        var donor = donorRepository.Create(new Donor(0, firstName, lastName, address, phoneNumber));
        return donor;
    }
}