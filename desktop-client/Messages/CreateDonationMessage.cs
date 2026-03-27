using System.Threading.Tasks;
using CommunityToolkit.Mvvm.Messaging.Messages;
using teledon_management_ui.ViewModels;

namespace teledon_management_ui.Messages;

/**
 * Request message to initiate creation of a new donation.
 */
public class CreateDonationMessage(CharityDtoViewModel selectedCharity)
{
    public CharityDtoViewModel SelectedCharity { get; } = selectedCharity;
}