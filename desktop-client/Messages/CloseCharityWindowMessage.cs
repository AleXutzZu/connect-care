using teledon_management_ui.Models;

namespace teledon_management_ui.Messages;

public class CloseCharityWindowMessage(Charity charity)
{
    public Charity Charity { get; } = charity;
}