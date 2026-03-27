namespace teledon_management_ui.Models;

public record Donation(long Id, Charity Charity, Donor Donor, double Amount);