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

        if (!catId.equals(bond.catId)) return false;
        if (!merlionId.equals(bond.merlionId)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = merlionId.hashCode();
        result = 31 * result + catId.hashCode();
        return result;
    }
}
