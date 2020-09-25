package com.vincenterc.rectangledestroyer;

public class RectangleGame extends BaseGame
{
    public void create() 
    {        
        super.create();
        setActiveScreen( new LevelScreen() );
    }
}