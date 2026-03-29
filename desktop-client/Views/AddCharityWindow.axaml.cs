using Avalonia;
using Avalonia.Controls;
using Avalonia.Markup.Xaml;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;

namespace teledon_management_ui.Views;

public partial class AddCharityWindow : Window
{
    public AddCharityWindow()
    {
        InitializeComponent();
        
        WeakReferenceMessenger.Default.Register<AddCharityWindow, UpdateCharityMessage>(this, static (w, m) =>
        {
            w.Close();
        });
    }
}