package org.hw2;

import javax.persistence.*;

@Entity
@Table(name ="Images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(mappedBy = "image")
    private flower fl;
    private String pic;

    public Image(){
        super();
        this.fl = null;
        this.pic = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public fl getflower() {
        return fl;
    }

    public void setflower(fl fl) {
        if(Image.this.fl.getImage() == null)
            Image.this.fl.setImage(this);
        else if (! (Image.this.fl.getImage().equals(this)))
            Image.this.fl.setImage(this);
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Image other = (Image) obj;
        if (fl == null) {
            if (other.fl != null)
                return false;
            else
                return true;
        }
        if (! fl.equals(other.fl)) {
            return false;
        }
        if(id != other.id) {
            return false;
        }
        return true;
    }
}
