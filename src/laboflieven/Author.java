package laboflieven;

/**
 * Created by Lieven on 15-11-2015.
 */
public class Author implements Comparable<Author>

{
    public String firstName;
    public String lastName;
    public Long uzId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        if (firstName != null ? !firstName.equals(author.firstName) : author.firstName != null) return false;
        return !(lastName != null ? !lastName.equals(author.lastName) : author.lastName != null);

    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Author o) {
        if (firstName != null && o.firstName != null)
        {
            int compare = firstName.compareTo(o.firstName);
            if (compare != 0) {
                return compare;
            }
        }
        return lastName.compareTo(o.lastName);
    }
}
