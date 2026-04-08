using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;
using teledon_management_ui.Models;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class DonorListViewModel : ViewModelBase
{
    public DonorListViewModel(IDonorService donorService)
    {
        _donorService = donorService;

        _ = InitializeAsync();

        WeakReferenceMessenger.Default.Register<BroadcastedCreateDonorMessage>(this, (recipient, message) =>
        {
            DonorList.Add(message.Donor);
            UpdateFilter();
        });
    }

    private readonly IDonorService _donorService;

    [ObservableProperty] private Donor? _selectedDonor;

    [ObservableProperty] private ObservableCollection<Donor> _donorList = [];

    [ObservableProperty] private ObservableCollection<Donor> _filteredDonors = [];

    [ObservableProperty] private string? _searchText;

    private async Task InitializeAsync()
    {
        var donors = await _donorService.AllDonors();

        DonorList = new ObservableCollection<Donor>(donors);
        FilteredDonors = new ObservableCollection<Donor>(DonorList);
    }

    partial void OnSearchTextChanged(string? value)
    {
        UpdateFilter();
    }

    private void UpdateFilter()
    {
        FilteredDonors.Clear();

        var search = SearchText ?? "";

        var result = DonorList.Where(d =>
            string.IsNullOrEmpty(search) ||
            d.FirstName.Contains(search, System.StringComparison.CurrentCultureIgnoreCase) ||
            d.LastName.Contains(search, System.StringComparison.CurrentCultureIgnoreCase));

        foreach (var donor in result)
        {
            FilteredDonors.Add(donor);
        }
    }
}