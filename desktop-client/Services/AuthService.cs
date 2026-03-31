using System;
using System.Threading.Tasks;
using teledon_management_ui.Protos;

namespace teledon_management_ui.Services;

public class AuthService(INetworkService networkService) : IAuthService
{
    public bool IsLoggedIn { get; private set; }

    public string? LoggedInUser { get; private set; }

    public async Task<IAuthService.LoginResult> Login(string username, string password)
    {
        var response = await networkService.SendRequestAsync(new MainMessage
        {
            AuthReq = new AuthUserRequest
            {
                Username = username,
                Password = password
            }
        });

        if (response.AuthRes.Status == ResponseStatus.Failed) return IAuthService.LoginResult.InvalidCredentials;

        IsLoggedIn = true;
        LoggedInUser = username;
        OnLoginStateChanged?.Invoke();
        return IAuthService.LoginResult.Success;
    }

    public async Task Logout()
    {
        await Task.Delay(500);
        IsLoggedIn = false;
        LoggedInUser = string.Empty;

        OnLoginStateChanged?.Invoke();
    }

    public event Action? OnLoginStateChanged;
}