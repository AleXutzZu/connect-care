using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Threading;
using teledon_management_ui.Models;
using teledon_management_ui.Persistence;

namespace teledon;

public class InMemoryCharityRepository : ICharityRepository
{
    private readonly ConcurrentDictionary<long, Charity> _charities = new();

    private static long _idCount = 0;

    private static long GenerateId()
    {
        return Interlocked.Increment(ref _idCount);
    }

    public Charity Create(Charity data)
    {
        var obj = data with { Id = GenerateId() };
        _charities.TryAdd(obj.Id, obj);

        return obj;
    }

    public Charity? FindById(long id)
    {
        return _charities.GetValueOrDefault(id);
    }

    public Charity? Update(Charity data)
    {
        if (!_charities.ContainsKey(data.Id)) return null;

        _charities[data.Id] = data;
        return _charities[data.Id];
    }

    public void DeleteById(long id)
    {
        _charities.TryRemove(id, out _);
    }
}