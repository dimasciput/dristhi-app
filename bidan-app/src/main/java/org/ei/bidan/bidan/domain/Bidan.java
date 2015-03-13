package org.ei.bidan.bidan.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Dimas Ciputra on 3/4/15.
 */
public class Bidan {
    private final String name;
    private final long kartuIbuAncCount;
    private final long kartuIbuCount;
    private final long kartuIbuPNCCount;

    public Bidan(String name, long kartuIbuCount, long kartuIbuAncCount, long kartuIbuPNCCount) {
        this.name = name;
        this.kartuIbuAncCount = kartuIbuAncCount;
        this.kartuIbuCount = kartuIbuCount;
        this.kartuIbuPNCCount = kartuIbuPNCCount;
    }

    public String name() {
        return name;
    }

    public long getKartuIbuAncCount() { return kartuIbuAncCount; }

    public long getKartuIbuCount() { return kartuIbuCount; }

    public long getKartuIbuPNCCount() { return kartuIbuPNCCount; }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
