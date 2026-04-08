using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;
using Google.Protobuf;
using teledon_management_ui.Protos;

namespace teledon_management_ui.Services;

public interface INetworkService
{
    Task<MainMessage> SendRequestAsync(MainMessage request);
    event Action<MainMessage>? OnUpdateReceived;
}

public class NetworkService : INetworkService
{
    private readonly TcpClient _client;
    private readonly NetworkStream _stream;

    private readonly Queue<TaskCompletionSource<MainMessage>> _responseQueue = new();

    private readonly SemaphoreSlim _sendLock = new(1, 1);

    public event Action<MainMessage>? OnUpdateReceived;

    public NetworkService(string host, int port)
    {
        _client = new TcpClient(host, port);
        _stream = _client.GetStream();
        Task.Run(ListenLoop);
    }


    public async Task<MainMessage> SendRequestAsync(MainMessage request)
        
    {
        var tcs = new TaskCompletionSource<MainMessage>();

        await _sendLock.WaitAsync();
        try
        {
            lock (_responseQueue)
            {
                _responseQueue.Enqueue(tcs);
            }

            request.WriteDelimitedTo(_stream);
            await _stream.FlushAsync();
        }
        finally
        {
            _sendLock.Release();
        }

        var responseEnvelope = await tcs.Task;

        return responseEnvelope;
    }

    private async Task ListenLoop()
    {
        while (_client.Connected)
        {
            var incoming = MainMessage.Parser.ParseDelimitedFrom(_stream);

            if (incoming == null) continue;

            if (incoming.IsUpdatePayload)
            {
                OnUpdateReceived?.Invoke(incoming);
                continue;
            }

            TaskCompletionSource<MainMessage>? tcs;
            lock (_responseQueue)
            {
                _responseQueue.TryDequeue(out tcs);
            }

            tcs?.SetResult(incoming);
        }
    }
}