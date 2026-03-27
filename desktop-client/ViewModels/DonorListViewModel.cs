using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using CommunityToolkit.Mvvm.ComponentModel;
using teledon_management_ui.Models;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class DonorListViewModel : ViewModelBase
{
    public DonorListViewModel(IDonorService donorService)
    {
        _donorService = donorService;
        _donorList = donorService.AllDonors();
        FilteredDonors = new ObservableCollection<Donor>(_donorList);
    }

    private readonly IDonorService _donorService;

    [ObservableProperty] private Donor? _selectedDonor;

    private readonly List<Donor> _donorList;

    [ObservableProperty] private ObservableCollection<Donor> _filteredDonors;

    [ObservableProperty] private string? _searchText;

    partial void OnSearchTextChanged(string? value)
    {
        UpdateFilter();
    }

    private void UpdateFilter()
    {
        FilteredDonors.Clear();

        var search = SearchText ?? "";

        var result = _donorList.Where(d =>
            string.IsNullOrEmpty(search) ||
            d.FirstName.Contains(search, System.StringComparison.CurrentCultureIgnoreCase) ||
            d.LastName.Contains(search, System.StringComparison.CurrentCultureIgnoreCase));

        foreach (var donor in result)
        {
            FilteredDonors.Add(donor);
        }
    }
}