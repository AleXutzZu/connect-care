using Avalonia.Controls;
using Avalonia.Threading;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;

namespace teledon_management_ui.Views;

public partial class AddCharityWindow : Window
{
    public AddCharityWindow()
    {
        InitializeComponent();
        
        WeakReferenceMessenger.Default.Register<AddCharityWindow, CreateCharityMessage>(this, static (w, m) =>
        {
            Dispatcher.UIThread.Post(w.Close);
        });
    }
}