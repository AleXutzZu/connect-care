using teledon_management_ui.Models;

namespace teledon_management_ui.Messages;

public class BroadcastedCreateDonorMessage(Donor donor)
{
    public Donor Donor { get; } = donor;
}