using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using teledon_management_ui.Models;
using teledon_management_ui.Persistence;

namespace teledon;

public class InMemoryVolunteerRepository : IVolunteerRepository
{
    public InMemoryVolunteerRepository()
    {
        Create(new Volunteer(0, "admin", "123"));
    }

    private readonly ConcurrentDictionary<long, Volunteer> _volunteers = new();

    private static long _idCount = 0;

    private static long GenerateId()
    {
        return Interlocked.Increment(ref _idCount);
    }

    public Volunteer Create(Volunteer data)
    {
        var obj = data with { Id = GenerateId() };
        _volunteers.TryAdd(obj.Id, obj);

        return obj;
    }

    public Volunteer? FindById(long id)
    {
        return _volunteers.GetValueOrDefault(id);
    }

    public Volunteer? Update(Volunteer data)
    {
        if (!_volunteers.ContainsKey(data.Id)) return null;

        _volunteers[data.Id] = data;
        return _volunteers[data.Id];
    }

    public void DeleteById(long id)
    {
        _volunteers.TryRemove(id, out _);
    }

    public Volunteer? FindByUsername(string username)
    {
        return (from volunteer in _volunteers where volunteer.Value.Username.Equals(username) select volunteer.Value)
            .FirstOrDefault();
    }
}