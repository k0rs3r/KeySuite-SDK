package it.kdm.orchestratore.entity.object;

/**
 * Created by antsic on 07/09/16.
 */
public class GroupByPotOwners {

    private long count;
    private String role;
    private String description;


    public GroupByPotOwners() {

    }

    public GroupByPotOwners( String role,long count) {
        this.count = count;
        this.role = role;
    }



    public long getCount() {

        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupByPotOwners)) return false;

        GroupByPotOwners that = (GroupByPotOwners) o;

        if (count != that.count) return false;
        if (!role.equals(that.role)) return false;

        return true;

    }

    @Override
    public int hashCode() {
        int result = (int) (count ^ (count >>> 32));
        result = 31 * result + role.hashCode();
        return result;
    }
}
