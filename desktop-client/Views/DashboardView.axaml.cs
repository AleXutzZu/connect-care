using Avalonia.Controls;
using CommunityToolkit.Mvvm.Messaging;
using Microsoft.Extensions.DependencyInjection;
using teledon_management_ui.Messages;
using teledon_management_ui.ViewModels;

namespace teledon_management_ui.Views;

public partial class DashboardView : UserControl
{
    public DashboardView()
    {
        InitializeComponent();

        if (Design.IsDesignMode) return;

        WeakReferenceMessenger.Default.Register<DashboardView, OpenDonationCreationWindowMessage>(this, (w, m) =>
        {
            if (App.Services == null) return;

            var dialog = new AddDonationWindow
            {
                DataContext =
                    ActivatorUtilities.CreateInstance<AddDonationWindowViewModel>(App.Services, m.SelectedCharity)
            };

            var topLevel = TopLevel.GetTopLevel(w);

            if (topLevel is Window ownerWindow)
            {
                dialog.ShowDialog<bool>(ownerWindow);
            }
        });
        
        WeakReferenceMessenger.Default.Register<DashboardView, OpenCharityCreationWindowMessage>(this, (w, m) =>
        {
            if (App.Services == null) return;
            var topLevel = TopLevel.GetTopLevel(w);

            var dialog = new AddCharityWindow
            {
                DataContext = App.Services.GetRequiredService<AddCharityWindowViewModel>()
            };

            if (topLevel is Window ownerWindow)
            {
                dialog.ShowDialog(ownerWindow);
            }
        });
    }
}