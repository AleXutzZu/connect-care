using teledon_management_ui.Models;

namespace teledon_management_ui.Messages;

public class BroadcastedCreateDonationMessage(Donation donation)
{
    public Donation Donation { get; } = donation;
}