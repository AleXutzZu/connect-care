using CommunityToolkit.Mvvm.ComponentModel;
using CommunityToolkit.Mvvm.Input;
using CommunityToolkit.Mvvm.Messaging;
using teledon_management_ui.Messages;
using teledon_management_ui.Services;

namespace teledon_management_ui.ViewModels;

public partial class AddDonationWindowViewModel : ViewModelBase
{
    public string CharityName { get; }

    private readonly long _charityId;

    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(RegisterDonationCommand))]
    private decimal _donationAmount;

    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(RegisterDonationCommand))]
    private string? _donorFirstName;

    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(RegisterDonationCommand))]
    private string? _donorLastName;

    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(RegisterDonationCommand))]
    private string? _donorPhone;

    [ObservableProperty] [NotifyCanExecuteChangedFor(nameof(RegisterDonationCommand))]
    private string? _donorAddress;

    private long? _donorId;
    public DonorListViewModel DonorListVm { get; }

    private readonly IDonorService _donorService;
    private readonly IDonationService _donationService;

    public AddDonationWindowViewModel(CharityDtoViewModel charityDtoViewModel, DonorListViewModel donorListVm,
        IDonorService donorService, IDonationService donationService)
    {
        DonorListVm = donorListVm;
        _donorService = donorService;
        _donationService = donationService;

        DonorListVm.PropertyChanged += (sender, args) =>
        {
            if (args.PropertyName == nameof(DonorListViewModel.SelectedDonor))
            {
                DonorFirstName = DonorListVm.SelectedDonor.FirstName;
                DonorLastName = DonorListVm.SelectedDonor.LastName;
                DonorAddress = DonorListVm.SelectedDonor.Address;
                DonorPhone = DonorListVm.SelectedDonor.PhoneNumber;
                _donorId = DonorListVm.SelectedDonor.Id;
            }
        };

        CharityName = charityDtoViewModel.GetCharityDto().Name;
        _charityId = charityDtoViewModel.GetCharityDto().Id;
    }

    partial void OnDonorFirstNameChanged(string? value)
    {
        _donorId = null;
    }

    partial void OnDonorLastNameChanged(string? value)
    {
        _donorId = null;
    }

    partial void OnDonorAddressChanged(string? value)
    {
        _donorId = null;
    }

    partial void OnDonorPhoneChanged(string? value)
    {
        _donorId = null;
    }

    private bool CanRegisterDonation => !string.IsNullOrEmpty(DonorFirstName) && !string.IsNullOrEmpty(DonorLastName) &&
                                        !string.IsNullOrEmpty(DonorPhone) && !string.IsNullOrEmpty(DonorAddress) &&
                                        DonationAmount > 0;

    [RelayCommand(CanExecute = nameof(CanRegisterDonation))]
    private void RegisterDonation()
    {
        if (_donorId.HasValue)
        {
            // Donor exists so we just create the donation
            _donationService.AddDonationToCharity(_charityId, (double)DonationAmount, _donorId.Value);
        }
        else
        {
            var donor = _donorService.CreateDonor(DonorFirstName!, DonorLastName!, DonorPhone!, DonorAddress!);
            _donationService.AddDonationToCharity(_charityId, (double)DonationAmount, donor.Id);
        }

        WeakReferenceMessenger.Default.Send(new CloseDonationWindowMessage(_charityId, (double)DonationAmount));
    }
}