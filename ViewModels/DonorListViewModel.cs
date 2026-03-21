using System.Collections.ObjectModel;
using CommunityToolkit.Mvvm.ComponentModel;
using teledon_management_ui.Models;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class DonorListViewModel(IDonorService donorService) : ViewModelBase
{
    private readonly IDonorService _donorService = donorService;

    [ObservableProperty] private Donor _selectedDonor;

    [ObservableProperty] private ObservableCollection<Donor> _donorList = new(donorService.AllDonors());
}