package Models;

import java.time.LocalDate;

public class Client {
    public int id;
    public String name;
    public String surname;
    public LocalDate birthDate;
    public String email;
    public String gender;

    public String toString()
    {
        return "id: " + id + ", name: " + name + ", surname: " + surname + ", email: " + email;
    }
}
