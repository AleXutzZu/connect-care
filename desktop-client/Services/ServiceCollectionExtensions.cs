using Microsoft.Extensions.DependencyInjection;
using teledon_management_ui.Persistence;
using teledon_management_ui.ViewModels;
using teledon;

namespace teledon_management_ui.Services;

public static class ServiceCollectionExtensions
{
    public static void AddCommonServices(this IServiceCollection serviceCollection)
    {
        //Add repositories
        serviceCollection.AddSingleton<ICharityRepository, InMemoryCharityRepository>();
        serviceCollection.AddSingleton<IDonationRepository, InMemoryDonationRepository>();
        serviceCollection.AddSingleton<IDonorRepository, InMemoryDonorRepository>();

        //Add services
        serviceCollection.AddSingleton<INetworkService, NetworkService>(_ => new NetworkService("localhost", 8080));
        serviceCollection.AddSingleton<IAuthService, AuthService>();
        serviceCollection.AddSingleton<ICharityService, CharityService>();
        serviceCollection.AddSingleton<IDonorService, DonorService>();
        serviceCollection.AddSingleton<IDonationService, DonationService>();


        //Add ViewModels
        serviceCollection.AddTransient<LoginViewModel>();
        serviceCollection.AddTransient<MainWindowViewModel>();
        serviceCollection.AddTransient<DashboardViewModel>();
        serviceCollection.AddTransient<CharityDtoViewModel>();
        serviceCollection.AddTransient<DonorListViewModel>();
        serviceCollection.AddTransient<AddCharityWindowViewModel>();
    }
}