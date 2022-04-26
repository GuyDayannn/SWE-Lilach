package org.cshaifa.spring.entities;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock")
public class Stock implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private shops shop;

    @OneToMany(fetch = FetchType.LAZY)
    private List<CatalogItem> items;

    @ElementCollection
    private List<int> howMany;

    public Stock() {
        this.shop = null;
        this.items = null;
        this.howMany = null;
    }

    public Stock(shops shop) {
        this.shop = shop;
    }

    public int getId() {
        return id;
    }

    public shops getShop() {
        return shop;
    }

    public void setShop(shops shop) {
        this.shop = shop;
    }

    public List<CatalogItem> getItems() {
        return items;
    }

    public List<int> getHowMany() {
        return howMany;
    }

    public void setItems(List<CatalogItem> items) {
        this.items = items;
    }

    public void addItem(CatalogItem item){
        if(items == null) {
            items = new ArrayList<CatalogItem>();
            items.add(item);
            howMany = new ArrayList<int>();
            howMany.add(1);
            return;
        }

        for(int i = 0; i<items.size(); i++) {
            if (items.get(i).equals(item)){
                howMany.set(i, howMany.get(i) + 1);
                return;
            }
        }

        howMany.add(1);
        items.add(item);

    }

    public void removeItem(CatalogItem item){
        int index= 0;
        if(items == null) {
            return;
        }
        for(int i = 0; i<items.size(); i++) {
            if (items.get(i).equals(item)){
                if(howMany.get(i)==1){
                    index = i;
                    howMany.remove(index);
                    items.remove(index);
                }
                else
                    howMany.set(i,howMany.get(i)-1);
            }
        }

    }


}
