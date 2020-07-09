package com.example.instagramclone.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.Date;

@Parcel(analyze={Post.class})
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_LIKE = "likes";
    public static final String KEY_LIKE_COUNT = "likeCount";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public Date getDate() {return getCreatedAt();}

    public void setLikesArray(JSONArray array){put(KEY_LIKE, array);}

    public JSONArray getLikesArray(){return getJSONArray(KEY_LIKE);}

    public int getLikeCount(){return (int) getNumber(KEY_LIKE_COUNT);}

    public void setLikeCount(int count){put(KEY_LIKE_COUNT, count);}


}
