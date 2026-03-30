using teledon_management_ui.Models;

namespace teledon_management_ui.Messages;

public class BroadcastedCreateCharityMessage(Charity charity)
{
    public Charity Charity { get; } = charity;
}