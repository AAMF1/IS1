package ejb;

import data.Item;
import data.User;

import javax.ejb.Local;
import java.util.List;

@Local
public interface UserEJBLocal {

    int registaUser(String email, String password, String nome, String pais);
    int loginUser(String email, String password);
    void editUser(int id, String nome, String pais, String password);
    public List<Item> pesquisa(String pesquisa, String pesquisa2, String nome, String flag);
    public List<Item> getUser_items(int id_user);
    void removeUser(int id);
    void addItem(String nome, String preco, String pais, String categoria, String url_foto, int user_id, String data);
    void editItem(int id_item, int id_user, String nome, String preco, String pais, String categoria, String url_foto);
    public List<Item> pesquisa_rapida(String pesquisa);
    int removeItem(int id_item, int id_user);
    public void send_email();
    public int verifica_bd();
    public List <Item> pesquisa_ordenada(String pesquisa, String pesquisa2, String tipo_pesquisa, String tipo_ordem, String parametro_pesquisa, String nome, boolean flag);

}