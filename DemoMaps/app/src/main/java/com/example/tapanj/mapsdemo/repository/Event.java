package com.example.tapanj.mapsdemo.repository;

public class Event<T> {
    private boolean hasBeenHandled = false;

    private T content;

    public Event(T content){
        this.content = content;
    }

    public T getContentIfNotHandled(){
        if(this.hasBeenHandled){
            return null;
        }
        else{
            return this.content;
        }
    }

    public T peekContent(){
        return this.content;
    }
}
