package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;

public class Bond
{
    private final String merlionId;
    private final String catId;
    private final String id;

    @JsonCreator
    public Bond(
            @NotNull @JsonProperty("merlionId") String merlionId,
            @NotNull @JsonProperty("merlionCatId") String catId,
            @NotNull @JsonProperty("id") String id) {
        this.merlionId = merlionId;
        this.catId = catId;
        this.id = id;
    }

    @JsonProperty
    public String merlionId()
    {
        return merlionId;
    }

    @JsonProperty
    public String catId()
    {
        return catId;
    }

    @JsonProperty
    public String id()
    {
        return id;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bond bond = (Bond) o;

        return merlionId.equals(bond.merlionId);

    }

    @Override
    public int hashCode()
    {
        return merlionId.hashCode();
    }

    @Override
    public String toString()
    {
        return "Bond{" +
                "merlionId='" + merlionId + '\'' +
                ", catId='" + catId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
