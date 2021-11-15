package it.kdm.doctoolkit.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * Created by lorenxs on 3/12/14.
 */
public class LockStatus {

    private final boolean locked;
    private final String username;

    public LockStatus(boolean locked, String username) {
        if (locked) {
            Preconditions.checkArgument(username != null, "Username cannot be null if locked is true");
        }

        this.locked = locked;
        this.username = username;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass())
                .add("locked", locked)
                .add("by", username)
                .toString();
    }
}
