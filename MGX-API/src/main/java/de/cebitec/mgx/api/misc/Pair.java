package de.cebitec.mgx.api.misc;

import java.util.Objects;

/**
 *
 * @author sjaenick
 */
public class Pair<T, U> {

    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public final T getFirst() {
        return first;
    }

    public final U getSecond() {
        return second;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.first);
        hash = 97 * hash + Objects.hashCode(this.second);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        return true;
    }
}
