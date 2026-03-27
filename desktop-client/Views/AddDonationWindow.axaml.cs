using Avalonia.Controls;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;

namespace teledon_management_ui.Views;

public partial class AddDonationWindow : Window
{
    public AddDonationWindow()
    {
        InitializeComponent();

        WeakReferenceMessenger.Default.Register<AddDonationWindow, CloseDonationWindowMessage>(this,
            static (w, m) => { w.Close(); });
    }
}