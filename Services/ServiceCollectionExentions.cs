using Microsoft.Extensions.DependencyInjection;
using teledon_management_ui.Persistence;
using teledon_management_ui.ViewModels;
using teledon;

namespace teledon_management_ui.Services;

public static class ServiceCollectionExentions
{
    public static void AddCommonServices(this IServiceCollection serviceCollection)
    {
        //Add repositories
        serviceCollection.AddSingleton<ICharityRepository, InMemoryCharityRepository>();
        serviceCollection.AddSingleton<IVolunteerRepository, InMemoryVolunteerRepository>();
        serviceCollection.AddSingleton<IDonationRepository, InMemoryDonationRepository>();
        serviceCollection.AddSingleton<IDonorRepository, InMemoryDonorRepository>();

        //Add services
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
    }
}