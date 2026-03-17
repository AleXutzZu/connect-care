using System;
using CommunityToolkit.Mvvm.ComponentModel;
using Microsoft.Extensions.DependencyInjection;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class MainWindowViewModel : ViewModelBase
{
    public MainWindowViewModel() : this(null!, null!)
    {
    }

    [ObservableProperty] private ObservableObject _currentPage;

    private readonly IServiceProvider _serviceProvider;
    private readonly IAuthService _authService;

    public MainWindowViewModel(IAuthService authService, IServiceProvider serviceProvider)
    {
        _authService = authService;
        _serviceProvider = serviceProvider;

        _currentPage = _serviceProvider.GetRequiredService<LoginViewModel>();

        _authService.OnLoginStateChanged += HandleLoginStateChanged;
    }

    private void HandleLoginStateChanged()
    {
        if (_authService.IsLoggedIn)
        {
            CurrentPage = _serviceProvider.GetRequiredService<DashboardViewModel>();
        }
    }
}