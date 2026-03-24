package me.alexutzzu.teledon.persistence.database;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Logger;

public class LoggingHandler implements InvocationHandler {
    private final Object target;
    private final Logger logger = Logger.getLogger("me.alexutzzu.teledon.persistence.database");

    public LoggingHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("Executing " + method.getName() + " with args: " + Arrays.toString(args));

        long start = System.currentTimeMillis();

        try {
            Object result = method.invoke(target, args);

            long duration = System.currentTimeMillis() - start;
            logger.info("Finished executing " + method.getName() + " in " + duration + "ms");

            return result;
        }
        catch (InvocationTargetException e){
            logger.severe("Method " + method.getName() + " failed: " + e.getCause().getMessage());
            throw e.getCause();
        }
        catch (Exception e) {
            logger.severe("Method " + method.getName() + " failed: " + e.getMessage());
            throw e;
        }
    }
}