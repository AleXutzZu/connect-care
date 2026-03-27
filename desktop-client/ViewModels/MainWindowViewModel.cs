using System;
using CommunityToolkit.Mvvm.ComponentModel;
using Microsoft.Extensions.DependencyInjection;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class MainWindowViewModel : ViewModelBase
{
    public MainWindowViewModel() : this(null!)
    {
    }

    [ObservableProperty] private ObservableObject _currentPage;

    private readonly IAuthService _authService;

    public MainWindowViewModel(IAuthService authService)
    {
        _authService = authService;

        _currentPage = App.Services!.GetRequiredService<LoginViewModel>();

        _authService.OnLoginStateChanged += HandleLoginStateChanged;
    }

    private void HandleLoginStateChanged()
    {
        if (_authService.IsLoggedIn)
        {
            CurrentPage = App.Services!.GetRequiredService<DashboardViewModel>();
        }
        else
        {
            CurrentPage = App.Services!.GetRequiredService<LoginViewModel>();
        }
    }
}