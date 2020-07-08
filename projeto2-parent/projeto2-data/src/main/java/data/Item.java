package data;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "ITEMS")
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String name;
    private float price;
    private String country;
    private String category;
    private String publish_date;
    private String foto;
    private int user_id;


    public Item(){
        super();
    }

    public Item(String name, float price, String country, String category, String foto, int user_id, String data){
        super();
        this.name = name;
        this.price = price;
        this.country = country;
        this.category = category;
        this.foto = foto;
        this.user_id = user_id;
        this.publish_date = data;
    }

    public Item(String name, float price, String country, String category, int user_id, String data){
        super();
        this.name = name;
        this.price = price;
        this.country = country;
        this.category = category;
        this.user_id = user_id;
        this.publish_date = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}