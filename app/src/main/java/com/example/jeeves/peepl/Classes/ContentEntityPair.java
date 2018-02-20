package com.example.jeeves.peepl.Classes;

import java.util.ArrayList;

import opennlp.tools.util.Span;

/**
 * Created by Jeeves on 12/17/2017.
 *
 * This class is designed to ease the passing of content and spans from the OpenNLPProcess class to the Activities
 * that will be displayed on screen. The two ArrayList classes keep the coupled content and entity objects in a
 * data structure that makes accessing values easy within the Activity.java classes.
 */

public class ContentEntityPair {
    private ArrayList<String> content;
    private ArrayList<Span[]> entities;

    public ContentEntityPair()
    {
        content = new ArrayList<String>();
        entities = new ArrayList<Span[]>();
    }

    public ArrayList<String> getContent()
    {
        return content;
    }

    public ArrayList<Span[]> getEntities()
    {
        return entities;
    }

    public void addContent(String content)
    {
        this.content.add(content);
    }

    public void addEntities(Span[] entities)
    {
        this.entities.add(entities);
    }

}

