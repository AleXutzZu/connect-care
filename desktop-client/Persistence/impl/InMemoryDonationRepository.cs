using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using teledon_management_ui.Models;
using teledon_management_ui.Persistence;

namespace teledon;

public class InMemoryDonationRepository : IDonationRepository
{
    private readonly ConcurrentDictionary<long, Donation> _donations = new();
    private static long _idCount = 0;

    private static long GenerateId()
    {
        return Interlocked.Increment(ref _idCount);
    }

    public Donation Create(Donation data)
    {
        var obj = data with { Id = GenerateId() };
        _donations.TryAdd(obj.Id, obj);

        return obj;
    }

    public Donation? FindById(long id)
    {
        return _donations.GetValueOrDefault(id);
    }

    public Donation? Update(Donation data)
    {
        if (!_donations.ContainsKey(data.Id)) return null;

        _donations[data.Id] = data;
        return _donations[data.Id];
    }

    public void DeleteById(long id)
    {
        _donations.TryRemove(id, out _);
    }

    public List<Donation> findAllByCharityId(long id)
    {
        return _donations.Values.Where(x => x.Charity.Id.Equals(id)).ToList();
    }
}