using System;

namespace teledon_management_ui.Exceptions;

public class ServiceException(string message) : Exception(message);