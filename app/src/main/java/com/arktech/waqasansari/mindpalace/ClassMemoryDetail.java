package com.arktech.waqasansari.mindpalace;

import java.io.Serializable;


/**
 * Created by WaqasAhmed on 4/1/2016.
 */
public class ClassMemoryDetail implements Serializable {
    private int id;
    private String Title;
    private String Dated;
    private String Description;
    private String Tags;
    private String Image;
    private String Type;

    //***********************************Setter******************************************//
    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDated(String dated) {
        Dated = dated;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setTags(String tags) {
        Tags = tags;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setType(String type) {
        Type = type;
    }

    //***********************************Getter******************************************//
    public int getId() {
        return id;
    }

    public String getTitle() {
        return Title;
    }

    public String getDated() {
        return Dated;
    }

    public String getDescription() {
        return Description;
    }

    public String getTags() {
        return Tags;
    }

    public String getImage() {
        return Image;
    }

    public String getType() {
        return Type;
    }
}
