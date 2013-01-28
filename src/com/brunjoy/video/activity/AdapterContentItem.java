package com.brunjoy.video.activity;

import org.teleal.cling.model.meta.Service;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.Item;

public class AdapterContentItem {
    private DIDLObject content;
    private String id;
    private Boolean isContainer;

    public AdapterContentItem(Container container, Service service) {
        // TODO Auto-generated constructor stub
        this.content = container;
        this.id = container.getId( );
        this.isContainer = true;
    }

    public AdapterContentItem(Item item, Service service) {
        // TODO Auto-generated constructor stub
        this.content = item;
        this.id = item.getId( );
        this.isContainer = false;
    }

    public Container getContainer() {
        if (isContainer)
            return (Container) content;
        else {
            return null;
        }
    }

    public Item getItem() {
        if (isContainer)
            return null;
        else
            return (Item) content;
    }


    public Boolean isContainer() {
        return isContainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass( ) != o.getClass( ))
            return false;

        AdapterContentItem that = (AdapterContentItem) o;

        if (!id.equals( that.id ))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return content.hashCode( );
    }

    @Override
    public String toString() {
        return content.getTitle( );
    }
}
