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
    public Task<LoginResult> Login(string username, string password);
    public event Action? OnLoginStateChanged;
}