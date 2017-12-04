package de.mirkoruether.util;

public class MultipleErrorsException extends RuntimeException
{
    private static final long serialVersionUID = 8560183395073382905L;

    private final Throwable[] innerErrors;

    public MultipleErrorsException(String message, Throwable[] innerErrors)
    {
        super(message, innerErrors[0]);
        this.innerErrors = innerErrors;
    }

    public Throwable[] getInnerErrors()
    {
        return innerErrors;
    }
}
