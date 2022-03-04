package com.example.give_and_take.models;

import java.io.Serializable;
import java.util.Date;

public class Service implements Serializable {
    public String id, name, image, description, address, author, category, dateTime;
    public Date dateObject;
}
