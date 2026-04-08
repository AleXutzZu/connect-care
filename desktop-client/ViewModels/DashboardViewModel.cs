using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using Avalonia.Threading;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;
using teledon_management_ui.Models.dto;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class DashboardViewModel : ViewModelBase
{
    [ObservableProperty] private ObservableCollection<CharityDtoViewModel> _charityDtos = new();
    private readonly IAuthService _authService;
    private readonly ICharityService _charityService;

    public DashboardViewModel(ICharityService charityService, IAuthService authService)
    {
        _charityService = charityService;
        _authService = authService;

        _ = InitializeAsync();

        WeakReferenceMessenger.Default.Register<CreateDonationMessage>(this, (recipient, message) =>
        {
            Dispatcher.UIThread.Post(() =>
            {
                // Find the specific charity in the ObservableCollection
                var targetCharity = CharityDtos.FirstOrDefault(c => c.Id == message.CharityId);

                targetCharity?.RaisedSum += message.DonatedSum;
            });
        });

        WeakReferenceMessenger.Default.Register<BroadcastedCreateDonationMessage>(this, (recipient, message) =>
        {
            Dispatcher.UIThread.Post(() =>
            {
                var targetCharity = CharityDtos.FirstOrDefault(c => c.Id == message.Donation.Charity.Id);
                targetCharity?.RaisedSum += message.Donation.Amount;
            });
        });

        WeakReferenceMessenger.Default.Register<CreateCharityMessage>(this,
            (recipient, message) =>
            {
                CharityDtos.Add(new CharityDtoViewModel(new CharityDto(message.Charity.Id, message.Charity.Name, 0)));
            });

        WeakReferenceMessenger.Default.Register<BroadcastedCreateCharityMessage>(this,
            (recipient, message) =>
            {
                CharityDtos.Add(new CharityDtoViewModel(new CharityDto(message.Charity.Id, message.Charity.Name, 0)));
            });
    }


    public string WelcomeMessage => _authService.LoggedInUser != null
        ? $"Welcome, {_authService.LoggedInUser}"
        : "Welcome, User";

    [RelayCommand]
    private async Task LogOutAsync()
    {
        await _authService.Logout();
    }

    [RelayCommand]
    private void CreateCharity()
    {
        WeakReferenceMessenger.Default.Send(new OpenCharityCreationWindowMessage());
    }

    private async Task InitializeAsync()
    {
        var charities = await _charityService.AllCharitiesWithRaisedSums();

        CharityDtos = new ObservableCollection<CharityDtoViewModel>(
            charities.ConvertAll(c => new CharityDtoViewModel(c)));
    }
}