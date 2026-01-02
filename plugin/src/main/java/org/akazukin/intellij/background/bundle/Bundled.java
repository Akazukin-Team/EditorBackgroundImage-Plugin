package org.akazukin.intellij.background.bundle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class Bundled<E> {
    E value;
    String name;

    public Bundled(final E value, final String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
