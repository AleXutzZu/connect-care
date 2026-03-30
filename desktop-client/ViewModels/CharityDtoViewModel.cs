using System.Threading.Tasks;
using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;
using teledon_management_ui.Models.dto;

namespace teledon_management_ui.ViewModels;

public partial class CharityDtoViewModel : ViewModelBase
{
    [ObservableProperty] private string _name = string.Empty;
    [ObservableProperty] private long _id;
    [ObservableProperty] private double _raisedSum;

    public CharityDtoViewModel()
    {
    }

    public CharityDtoViewModel(CharityDto charityDto)
    {
        Name = charityDto.Name;
        Id = charityDto.Id;
        RaisedSum = charityDto.RaisedSum;
    }

    public CharityDto GetCharityDto()
    {
        return new CharityDto(Id, Name, RaisedSum);
    }

    [RelayCommand]
    private void CreateDonation()
    {
        WeakReferenceMessenger.Default.Send(new OpenDonationCreationWindowMessage(this));
    }
}