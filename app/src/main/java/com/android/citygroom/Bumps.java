package com.android.citygroom;

public class Bumps
{
    String Bump_Id, User_Id, Bump_Size, Bump_Severity, Location, Bump_Latitude, Bump_Longitude, Loc_Category, Bump_ts;

    public Bumps()
    {
    }

    public Bumps(String bumpId, String userId, String bump_Size, String bump_Severity, String location, String bump_Latitude, String bump_Longitude, String loc_Category, String bump_ts) {
        this.Bump_Id = bumpId;
        this.User_Id = userId;
        this.Bump_Size = bump_Size;
        this.Bump_Severity = bump_Severity;
        this.Location = location;
        this.Bump_Latitude = bump_Latitude;
        this.Bump_Longitude = bump_Longitude;
        this.Loc_Category = loc_Category;
        this.Bump_ts = bump_ts;
    }
}

