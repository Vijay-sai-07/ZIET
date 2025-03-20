package user.exceptions;

public class InvalidLoginAttemptException extends RuntimeException {
  public InvalidLoginAttemptException(String message) {
    super(message);
  }
}
