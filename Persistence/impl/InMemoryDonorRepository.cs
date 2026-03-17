using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Threading;
using teledon_management_ui.Models;
using teledon_management_ui.Persistence;

namespace teledon;

public class InMemoryDonorRepository : IDonorRepository
{
    private readonly ConcurrentDictionary<long, Donor> _donors = new();

    private static long _idCount = 0;

    private static long GenerateId()
    {
        return Interlocked.Increment(ref _idCount);
    }

    public Donor Create(Donor data)
    {
        var obj = data with { Id = GenerateId() };
        _donors.TryAdd(obj.Id, obj);

        return obj;
    }

    public Donor? FindById(long id)
    {
        return _donors.GetValueOrDefault(id);
    }

    public Donor? Update(Donor data)
    {
        if (!_donors.ContainsKey(data.Id)) return null;

        _donors[data.Id] = data;
        return _donors[data.Id];
    }

    public void DeleteById(long id)
    {
        _donors.TryRemove(id, out _);
    }
}