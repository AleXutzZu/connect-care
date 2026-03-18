using System.Threading.Tasks;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class DashboardViewModel(IAuthService authService) : ViewModelBase
{
    public DashboardViewModel() : this(null!)
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