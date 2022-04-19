package org.hw2;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flowers")
public class flower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    String color;
    String name;
    int price;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private org.hw2.Image image;

    public flower(String name, String color,int price){
        super();
        this.name = name;
        this.color = color;
        this.price = price;
    }
    public flower(){
        super();
        this.name = "";
        this.color = "";
        this.price = 0;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public  boolean equals(Object obj){
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        flower other = (flower) obj;
        if(other.id != this.id){
            return false;
        }
        if(!name.equals(other.name)){
            return false;
        }
        if(!color.equals(other.color))
            return false;

        return true;
    }
    public org.hw2.Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "flower{" +
                "id=" + id +
                ", color='" + color + '\'' +
                ", name='" + name + '\'' +
                ", price='"+ price + '\'' +
                '}';
    }

}
