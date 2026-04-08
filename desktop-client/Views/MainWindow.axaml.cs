using Avalonia.Controls;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;

namespace teledon_management_ui.Views;

public partial class MainWindow : Window
{
    public MainWindow()
    {
        InitializeComponent();

        WeakReferenceMessenger.Default.Register<MainWindow, NotificationMessage>(this, static (w, m) =>
        {
            w.NotificationManager.CloseAll();
            w.NotificationManager.Show(m.Message, m.Type, m.TimeSpan);
        });
    }
}