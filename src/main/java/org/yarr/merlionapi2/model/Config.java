package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Config
{
    public final String apiLogin;
    public final String apiPassword;
    public final String mysqlUri;
    public final String mysqlUser;
    public final String mysqlPassword;

    @JsonCreator
    public Config(
            @JsonProperty("api_login") String apiLogin,
            @JsonProperty("api_password") String apiPassword,
            @JsonProperty("mysql_uri") String mysqlUri,
            @JsonProperty("mysql_user") String mysqlUser,
            @JsonProperty("mysql_password") String mysqlPassword)
    {
        this.apiLogin = apiLogin;
        this.apiPassword = apiPassword;
        this.mysqlUri = mysqlUri;
        this.mysqlUser = mysqlUser;
        this.mysqlPassword = mysqlPassword;
    }
}
