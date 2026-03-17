using System;
using System.Threading.Tasks;
using teledon_management_ui.Persistence;

namespace teledon_management_ui.Services;

public class AuthService(IVolunteerRepository volunteerRepository) : IAuthService
{
    public bool IsLoggedIn { get; private set; }

    public async Task<IAuthService.LoginResult> Login(string username, string password)
    {
        //Simulation
        await Task.Delay(500);
        
        var account = volunteerRepository.FindByUsername(username);

        if (account == null || !account.Username.Equals(username) || !account.Password.Equals(password)) return IAuthService.LoginResult.InvalidCredentials;

        IsLoggedIn = true;
        OnLoginStateChanged?.Invoke();
        return IAuthService.LoginResult.Success;
    }

    public event Action? OnLoginStateChanged;
}