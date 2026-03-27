namespace teledon_management_ui.Persistence;

public interface IBasicRepository<T>
{
    T Create(T data);
    T? FindById(long id);
    T? Update(T data);
    void DeleteById(long id);
}
