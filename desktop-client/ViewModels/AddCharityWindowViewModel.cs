using System.Threading.Tasks;
using Avalonia.Controls.Notifications;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Exceptions;
using teledon_management_ui.Messages;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class AddCharityWindowViewModel(ICharityService charityService) : ViewModelBase
{
    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(CreateCharityCommand))]
    private string? _charityName;

    private bool CanCreateCharity => !string.IsNullOrEmpty(CharityName);

    [RelayCommand(CanExecute = nameof(CanCreateCharity))]
    private async Task CreateCharityAsync()
    {
        try
        {
            var charity = await charityService.Create(CharityName!);
            WeakReferenceMessenger.Default.Send(new CreateCharityMessage(charity));
        }
        catch (ServiceException e)
        {
            WeakReferenceMessenger.Default.Send(new NotificationMessage($"Failed to create charity: {e.Message}",
                NotificationType.Error));
        }
    }
}