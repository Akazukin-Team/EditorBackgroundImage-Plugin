package org.akazukin.intellij.background.bundle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class Bundled<E> {
    E value;
    String message;

    public Bundled(final E value, final String message) {
        this.value = value;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
