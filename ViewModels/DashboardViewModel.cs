using System.Collections.ObjectModel;
using System.Threading.Tasks;
using CommunityToolkit.Mvvm.Input;
using teledon_management_ui.Models.dto;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class DashboardViewModel(IAuthService authService, ICharityService charityService) : ViewModelBase
{
    public ObservableCollection<CharityDtoViewModel> CharityDtos { get; } = charityService != null
        ? new ObservableCollection<CharityDtoViewModel>(charityService.AllCharitiesWithRaisedSums()
            .ConvertAll(c => new CharityDtoViewModel(c)))
        :
        [
            new CharityDtoViewModel(new CharityDto(1, "Societatea Hermes ABC", 120))
        ];

    public DashboardViewModel() : this(null!, null!)
    {
    }

    public string WelcomeMessage => authService?.LoggedInUser != null
        ? $"Welcome, {authService.LoggedInUser}"
        : "Welcome, User";

    [RelayCommand]
    private async Task LogOut()
    {
        await authService.Logout();
    }
}