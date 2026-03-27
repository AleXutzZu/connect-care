using System;
using System.Threading.Tasks;

namespace teledon_management_ui.Services;

public interface IAuthService
{
    public enum LoginResult
    {
        Success,
        InvalidCredentials,
    }

    public bool IsLoggedIn { get; }
    public string? LoggedInUser { get; }
    public Task<LoginResult> Login(string username, string password);
    public Task Logout();
    public event Action? OnLoginStateChanged;
}