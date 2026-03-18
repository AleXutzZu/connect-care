using System.Threading.Tasks;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class LoginViewModel(IAuthService authService) : ViewModelBase
{
    public LoginViewModel() : this(null!) 
    {
        ErrorMessage = "Invalid username or password. Please try again.";
    }
    
    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(LoginCommand))]
    private string? _username;

    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(LoginCommand))]
    private string? _password;

    [ObservableProperty] private string? _errorMessage;

    private bool CanLogin => !string.IsNullOrWhiteSpace(Username) && !string.IsNullOrWhiteSpace(Password);

    [RelayCommand(CanExecute = nameof(CanLogin))]
    private async Task Login()
    {
        ErrorMessage = string.Empty;
        var result = await authService.Login(Username!, Password!);

        switch (result)
        {
            case IAuthService.LoginResult.InvalidCredentials:
                ErrorMessage = "Invalid username or password. Please try again.";
                break;
            case IAuthService.LoginResult.Success:
                break;
        }
    }
}