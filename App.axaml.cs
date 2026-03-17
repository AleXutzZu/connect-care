using System;
using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Data.Core;
using Avalonia.Data.Core.Plugins;
using System.Linq;
using System.Security.Authentication.ExtendedProtection;
using Avalonia.Markup.Xaml;
using Microsoft.Extensions.DependencyInjection;
using teledon_management_ui.Persistence;
using teledon_management_ui.Services;
using teledon_management_ui.ViewModels;
using teledon_management_ui.Views;
using teledon;

namespace teledon_management_ui;

public partial class App : Application
{
    public IServiceProvider? Services { get; private set; }


    public override void Initialize()
    {
        AvaloniaXamlLoader.Load(this);
    }

    public override void OnFrameworkInitializationCompleted()
    {
        var serviceCollection = new ServiceCollection();

        //Add repositories
        serviceCollection.AddSingleton<ICharityRepository, InMemoryCharityRepository>();
        serviceCollection.AddSingleton<IVolunteerRepository, InMemoryVolunteerRepository>();
        serviceCollection.AddSingleton<IDonationRepository, InMemoryDonationRepository>();
        serviceCollection.AddSingleton<IDonorRepository, InMemoryDonorRepository>();

        //Add services
        serviceCollection.AddSingleton<IAuthService, AuthService>();

        serviceCollection.AddTransient<LoginViewModel>();
        serviceCollection.AddTransient<MainWindowViewModel>();
        serviceCollection.AddTransient<DashboardViewModel>();

        Services = serviceCollection.BuildServiceProvider();

        if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
        {
            // Avoid duplicate validations from both Avalonia and the CommunityToolkit. 
            // More info: https://docs.avaloniaui.net/docs/guides/development-guides/data-validation#manage-validationplugins
            DisableAvaloniaDataAnnotationValidation();
            desktop.MainWindow = new MainWindow
            {
                DataContext = Services.GetRequiredService<MainWindowViewModel>(),
            };
        }

        base.OnFrameworkInitializationCompleted();
    }

    private void DisableAvaloniaDataAnnotationValidation()
    {
        // Get an array of plugins to remove
        var dataValidationPluginsToRemove =
            BindingPlugins.DataValidators.OfType<DataAnnotationsValidationPlugin>().ToArray();

        // remove each entry found
        foreach (var plugin in dataValidationPluginsToRemove)
        {
            BindingPlugins.DataValidators.Remove(plugin);
        }
    }
}