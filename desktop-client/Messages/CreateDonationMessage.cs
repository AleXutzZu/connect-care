namespace teledon_management_ui.Messages;

public class CreateDonationMessage(long charityId, double donatedSum)
{
    public long CharityId { get; } = charityId;
    public double DonatedSum { get; } = donatedSum;
}