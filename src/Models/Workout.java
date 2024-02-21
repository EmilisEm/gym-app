package Models;

import java.time.LocalDateTime;

public class Workout {
    public int trainerId;
    public int clientId;
    public LocalDateTime start;
    public LocalDateTime end;

    public String toString()
    {
        return "TrenerioId: " + trainerId + ", KlientoId: " + clientId + ", Pradzia: " + start + ", Pabaiga: " + end;
    }
}
