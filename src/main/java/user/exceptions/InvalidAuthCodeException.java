package user.exceptions;

public class InvalidAuthCodeException extends Exception{
    public InvalidAuthCodeException(){
        super("Invalid Auth Code Passed.");
    }
}
