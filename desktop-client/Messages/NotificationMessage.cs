using System;
using Avalonia.Controls.Notifications;

namespace teledon_management_ui.Messages;

public class NotificationMessage(string message, NotificationType type, TimeSpan timeSpan)
{
    public NotificationMessage(string message, NotificationType type) : this(message, type, TimeSpan.FromSeconds(5))
    {
    }

    public string Message { get; } = message;
    public NotificationType Type { get; } = type;
    public TimeSpan TimeSpan { get; } = timeSpan;
}