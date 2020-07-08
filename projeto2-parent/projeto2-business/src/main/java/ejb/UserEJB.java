package ejb;

import data.Item;
import data.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Stateless
public class UserEJB implements UserEJBLocal{
    @PersistenceContext(name="Users")
    private EntityManager em;
    private String name="root";
    private String pass="21675508";
    final Logger logger = LoggerFactory.getLogger(UserEJB.class);

    public UserEJB(){}

    @Override
    public int registaUser(String email, String password, String nome, String pais){
        logger.debug("Vai proceder ao registo do utilizador");
        try {
            Query query = em.createQuery("SELECT c FROM User c WHERE mail = '" + email + "'", User.class);
            List<User> lista = query.getResultList();

            if(lista.size()==0){
                password = DigestUtils.sha256Hex(password);
                User user = new User(nome, pais, email, password);
                em.persist(user);
                logger.info("Registo efetuado com sucesso. Dados do utilizador: "+user.getId()+"|"+user.getName()+"|"+user.getCountry()+"|"+user.getMail()+"|"+user.getPassword());
                return 1;
            }
            else {
                logger.info("O email que pretende registar ja existe.");
                return -1;
            }

        } catch (Exception e) {
            logger.error("Got an exception! ");
            logger.error(e.getMessage());
        }
        return -1;
    }

    public int loginUser(String email, String password){
        logger.debug("Vai efetuar o login do utilizador");
        try {
            password = DigestUtils.sha256Hex(password);
            Query query = em.createQuery("SELECT c FROM User c WHERE mail LIKE '" + email + "'  and password LIKE '" + password+"'", User.class);
            List<User> lista = query.getResultList();

            if(lista.size()!=0) {
                logger.info("Log in efetuado com sucesso. Utilizador: " + lista.get(0).getId() + "|" + lista.get(0).getMail() + "|" + lista.get(0).getName() + "|" + lista.get(0).getCountry());
                return lista.get(0).getId();
            }
            else{
                logger.info("Nao foi possivel efetuar o login.");
                return -1;
            }
        } catch (Exception e) {
            logger.error("Got an exception! ");
            logger.error(e.getMessage());
        }

        return -1;
    }

    public void removeUser(int id){
        try {
            Query query = em.createQuery("SELECT c FROM User c WHERE id =" +id, User.class);
            List<User> lista = query.getResultList();
            if(lista.size()!=0){
                logger.debug("Vai remover o utilizador: "+lista.get(0).getId()+"|"+lista.get(0).getName()+"|"+lista.get(0).getMail()
                        +"|"+lista.get(0).getCountry());
                Query query2=em.createQuery("SELECT c FROM Item c WHERE user_id =" +id, Item.class);
                List<Item> lista_i=query2.getResultList();
                for(Item i: lista_i){
                    em.remove(i);
                }
                logger.debug("Removeu os items associados ao utilizador: "+lista.get(0).getId()+"|"+lista.get(0).getName()+"|"+lista.get(0).getMail()
                        +"|"+lista.get(0).getCountry());
                em.remove(lista.get(0));
                logger.info("User e items associados ao mesmo removido com sucesso");
            }
            else
                logger.info("Nao encontrou user");

        } catch (Exception e) {
            logger.error("Got an exception! ");
            logger.error(e.getMessage());
        }
    }

    public void editUser(int id, String nome, String pais, String password){
        logger.debug("Vai proceder as alteraçoes dos dados do utilizador.");
        try{
            Query query=em.createQuery("SELECT c FROM User c where id="+id, User.class);
            List <User> lista=query.getResultList();
            if(lista.size()!=0){
                User user_updated=em.merge(lista.get(0));
                logger.debug("USER ANTES: "+user_updated.getName()+"|"+user_updated.getCountry()+"|"+user_updated.getPassword());
                if(!nome.equals(""))
                    user_updated.setName(nome);
                if(!pais.equals(""))
                    user_updated.setCountry(pais);
                if(!password.equals("")) {
                    password = DigestUtils.sha256Hex(password);
                    user_updated.setPassword(password);
                }
                logger.debug("USER DEPOIS: "+user_updated.getName()+"|"+user_updated.getCountry()+"|"+user_updated.getPassword());
                logger.info("Alteracoes efetuadas com sucesso.");
            }
            else
                logger.info("Nao encontrou nada.");
        }catch (Exception e){
            logger.error("Got an exception! ");
            logger.error(e.getMessage());
        }
    }

    public List<Item> getUser_items(int id_user){
        String res="";
        List <Item> lista = new ArrayList<>();
        logger.debug("Vai buscar os items do utilizador");
        try{
            Query query=em.createQuery("SELECT c FROM Item c where user_id="+id_user, Item.class);
            lista=query.getResultList();

            if(lista.size()!=0)
                logger.info("Lista de items: \n" + lista.size());
            else
                logger.info("Nao encontrou nada.");
            return lista;
        }catch (Exception e){
            logger.error("Got an exception! ");
            logger.error(e.getMessage());
        }
        return lista;
    }

    public List<Item> pesquisa(String pesquisa, String pesquisa2, String nome, String flag){
        List<Item> items = new ArrayList<>();
        Query query;

        System.console().writer().println("NOME  "+nome);
        if(!nome.equals("")){
            System.console().writer().println(pesquisa+"|"+nome+"|"+flag);
            if(flag.compareTo("categoria")==0) query = em.createQuery("SELECT c FROM Item c WHERE category LIKE '%" + pesquisa + "%' and name like '%"+nome+"%'", Item.class);
            else if(flag.compareTo("pais")==0) query = em.createQuery("SELECT c FROM Item c WHERE country LIKE '%" + pesquisa + "%' and name like '%"+nome+"%'", Item.class);
            else if(flag.compareTo("preco")==0) query = em.createQuery("SELECT c FROM Item c WHERE price >= " + Float.parseFloat(pesquisa) + " AND price <= " + Float.parseFloat(pesquisa2)+" and name like '%"+nome+"%'", Item.class);
            else query = em.createQuery("SELECT c FROM Item c WHERE publish_date LIKE '%" + pesquisa + "%' and name like '%"+nome+"%'", Item.class); //CORRIGIR
        }
        else{
            if(flag.compareTo("categoria")==0) query = em.createQuery("SELECT c FROM Item c WHERE category LIKE '%" + pesquisa + "%'", Item.class);
            else if(flag.compareTo("pais")==0) query = em.createQuery("SELECT c FROM Item c WHERE country LIKE '%" + pesquisa + "%'", Item.class);
            else if(flag.compareTo("preco")==0) query = em.createQuery("SELECT c FROM Item c WHERE price >= " + Float.parseFloat(pesquisa) + " AND price <= " + Float.parseFloat(pesquisa2) , Item.class);
            else query = em.createQuery("SELECT c FROM Item c WHERE publish_date LIKE '%" + pesquisa + "%'", Item.class);
        }

        try{
            items = query.getResultList();
            return items;
        }catch(Exception e){
            logger.error("Exception: " + e);
        }

        return items;
    }

    public List <Item> pesquisa_ordenada(String pesquisa, String pesquisa2, String tipo_pesquisa, String tipo_ordem, String parametro_pesquisa, String nome, boolean flag){
        List<Item> items = new ArrayList<>();
        Query query;
        if(!flag){
            if(nome.equals("")){
                System.console().writer().println("ENTROU PESQUISA ORDENADA");
                System.console().writer().println(pesquisa+"|"+pesquisa2+"|"+tipo_pesquisa+"|"+tipo_ordem+"|"+parametro_pesquisa+"|"+nome);
                if(tipo_ordem.compareTo("crescente")==0){
                    if(tipo_pesquisa.compareTo("price")==0)
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" >="+Float.parseFloat(pesquisa)+" and "+tipo_pesquisa+" <= " + Float.parseFloat(pesquisa2) + " ORDER BY "+parametro_pesquisa+" asc", Item.class);
                    else if(tipo_pesquisa.compareTo("pesquisa_rapida")==0){
                        query = em.createQuery("SELECT c FROM Item c WHERE name like '%"+pesquisa+"%' or category like '%"+pesquisa+"%' or country like '%"+pesquisa+"%' ORDER BY '"+parametro_pesquisa+"' asc", Item.class);
                    }
                    else
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" LIKE '%" + pesquisa + "%' ORDER BY "+parametro_pesquisa+" asc", Item.class);
                }
                else{
                    if(tipo_pesquisa.compareTo("price")==0)
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" >="+Float.parseFloat(pesquisa)+" and "+tipo_pesquisa+" <= " + Float.parseFloat(pesquisa2) + " ORDER BY "+parametro_pesquisa+" desc", Item.class);
                    else if(tipo_pesquisa.compareTo("pesquisa_rapida")==0){
                        query = em.createQuery("SELECT c FROM Item c WHERE name like '%"+pesquisa+"%' or category like '%"+pesquisa+"%' or country like '%"+pesquisa+"%' ORDER BY "+parametro_pesquisa+" desc", Item.class);
                    }
                    else
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" LIKE '%" + pesquisa + "%' ORDER BY "+parametro_pesquisa+" desc", Item.class);
                }
            }
            else{
                System.console().writer().println(Float.parseFloat(pesquisa)+"|"+Float.parseFloat(pesquisa2)+"|"+tipo_pesquisa+"|"+tipo_ordem+"|"+parametro_pesquisa+"|"+nome);
                if(tipo_ordem.compareTo("crescente")==0){
                    if(tipo_pesquisa.compareTo("price")==0)
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" >="+Float.parseFloat(pesquisa)+" and "+tipo_pesquisa+" <= " + Float.parseFloat(pesquisa2) + " and name like '%"+nome+"%' ORDER BY "+parametro_pesquisa+" asc", Item.class);
                    else if(tipo_pesquisa.compareTo("pesquisa_rapida")==0){
                        System.console().writer().println("ENTROUUUU");
                        query = em.createQuery("SELECT c FROM Item c WHERE name like '%"+pesquisa+"%' or category like '%"+pesquisa+"%' or country like '%"+pesquisa+"%' and name like '%"+nome+"%' ORDER BY '"+parametro_pesquisa+"' asc", Item.class);
                    }
                    else
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" LIKE '%" + pesquisa + "%' and name like '%"+nome+"%' ORDER BY "+parametro_pesquisa+" asc", Item.class);
                }
                else{
                    if(tipo_pesquisa.compareTo("price")==0)
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" >="+Float.parseFloat(pesquisa)+" and "+tipo_pesquisa+" <= " + Float.parseFloat(pesquisa2) + " and name like '%"+nome+"%' ORDER BY "+parametro_pesquisa+" desc", Item.class);
                    else if(tipo_pesquisa.compareTo("pesquisa_rapida")==0){
                        query = em.createQuery("SELECT c FROM Item c WHERE name like '%"+pesquisa+"%' or category like '%"+pesquisa+"%' or country like '%"+pesquisa+"%' and name like '%"+nome+"%' ORDER BY "+parametro_pesquisa+" desc", Item.class);
                    }
                    else
                        query = em.createQuery("SELECT c FROM Item c WHERE "+tipo_pesquisa+" LIKE '%" + pesquisa + "%' ORDER BY "+parametro_pesquisa+" desc", Item.class);
                }
            }
        }
        else{
            if(tipo_ordem.compareTo("crescente")==0)
                query = em.createQuery("SELECT c FROM Item c WHERE user_id="+Integer.parseInt(pesquisa)+" ORDER BY "+parametro_pesquisa+" asc", Item.class);
            else
                query = em.createQuery("SELECT c FROM Item c WHERE user_id="+Integer.parseInt(pesquisa)+" ORDER BY "+parametro_pesquisa+" desc", Item.class);

        }
        try{
            items = query.getResultList();
            return items;
        }catch(Exception e){
            logger.error("Exception: " + e);
        }

        return items;

    }

    public List<Item> pesquisa_rapida(String pesquisa){
        List <Item> items=new ArrayList<>();
        Query query;
        logger.debug("Vai fazer a pesquisa de tudo que contenha o input do utilizador");
        query=em.createQuery("SELECT c From Item c where name like '%"+pesquisa+"%' or category like '%"+pesquisa+"%' or country like '%"+pesquisa+"%'");
        items=query.getResultList();
        logger.info("Pesquisa efetuada com sucesso.\n");

        return items;
    }

    public void addItem(String nome, String preco, String pais, String categoria, String url_foto, int user_id, String data){
        Item new_item;
        logger.debug("Vai adicionar um novo item ao utilizador");
        if(url_foto.equals("")){
            new_item=new Item(nome, Float.parseFloat(preco), pais, categoria, user_id, data);
            logger.info("DATAAAA: " + new_item.getPublish_date() + " | " + data);
        }
        else{
            new_item=new Item(nome, Float.parseFloat(preco), pais, categoria, url_foto, user_id, data);

            logger.info("DATAAAA: " + new_item.getPublish_date() + " | " + data);
        }
        em.persist(new_item);
        logger.info("Item criado e adicionado com sucesso");
    }


    public void editItem(int id_item, int id_user, String nome, String preco, String pais, String categoria, String url_foto){
        logger.debug("Vai proceder as alteracoes do item pedidas pelo dono");
        try{
            Query query=em.createQuery("SELECT c FROM Item c where id="+id_item+" and user_id="+id_user, Item.class);
            List <Item> lista=query.getResultList();
            if(lista.size()!=0){
                Item item_updated=em.merge(lista.get(0));
                logger.debug("ITEM ANTES: "+item_updated.getName()+"|"+item_updated.getPrice()+"|"+
                        item_updated.getCountry()+"|"+item_updated.getCategory()+"|"+url_foto);
                if(!nome.equals(""))
                    item_updated.setName(nome);
                if(!preco.equals(""))
                    item_updated.setPrice(Float.parseFloat(preco));
                if(!pais.equals(""))
                    item_updated.setCountry(pais);
                if(!categoria.equals(""))
                    item_updated.setCategory(categoria);
                if(!url_foto.equals(""))
                    item_updated.setFoto(url_foto);

                logger.debug("ITEM DEPOIS: "+item_updated.getName()+"|"+item_updated.getPrice()+"|"+
                        item_updated.getCountry()+"|"+item_updated.getCategory()+"|"+url_foto);
                logger.info("Alteracoes efetuadas com sucesso.");
            }
            else
                logger.info("Nao encontrou o item ou nao tem permissoes para altera-lo");
        }catch (Exception e){
            logger.error("Got an exception! ");
            logger.error(e.getMessage());
        }
    }

    public int removeItem(int id_item, int id_user){
        logger.debug("Vai remover o item selecionado pelo utilizador");
        try{
            Query query=em.createQuery("SELECT c FROM Item c where id='"+id_item+"' and user_id='" + id_user + "'", Item.class);
            List <Item> lista=query.getResultList();
            if(lista.size()!=0){
                em.remove(lista.get(0));
                logger.debug("Vai remover o item: "+lista.get(0).getId()+"|"+lista.get(0).getUser_id()+"|"+lista.get(0).getName()+"|"+lista.get(0).getPrice()+"|"+lista.get(0).getCountry()+"|"+lista.get(0).getCategory()+"|"+lista.get(0).getPublish_date());
                logger.info("Item removido com sucesso");
                return 1;
            }
            else
                logger.info("Nao encontrou o item ou nao tem permissoes para remove-lo");
        }catch (Exception e){
            logger.error("Got an exception! ");
            logger.error(e.getMessage());
        }
        return -1;
    }

    public int verifica_bd(){
        int n_items=0;
        try{
            Query query = em.createQuery("SELECT c FROM Item c", Item.class);
            List<Item> lista = query.getResultList();
            n_items = lista.size();

        }catch(Exception e){
            System.out.println(e);
        }

        return n_items;
    }

    public String[] get_emails(){
        List<User> lista = new ArrayList<>();

        try{
            Query query = em.createQuery("SELECT c FROM User c", User.class);
            lista = query.getResultList();

            String [] string = new String[lista.size()];

            for(int i=0; i<lista.size(); i++)
                string[i] = lista.get(i).getMail();

            return string;
        }catch(Exception e){
            System.out.println(e);
        }

        String[] vazio = new String[1];
        return vazio;
    }

    public List<Item> get_last_items(){
        List<Item> items = new ArrayList<>();

        try{
            Query query = em.createQuery("SELECT c FROM Item c ORDER BY id DESC", Item.class);
            query.setMaxResults(5);
            items = query.getResultList();
        }catch(Exception e){
            System.out.println(e);
        }

        return items;
    }

    public void send_email(){
        System.console().writer().println("ENTROU NA FUNÇÃO SEND_EMAIL");
        try{
            System.console().writer().println("ENTROU NO TRY");

            String host = "smtp.gmail.com";
            String from = "mail.teste.is.mei@gmail.com";
            String pass = "mailparatestaris-";
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");


            String [] to = get_emails();

            Session session = Session.getDefaultInstance(props, null);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            // To get the array of addresses
            for( int i=0; i < to.length; i++ ) { // changed from a while loop
                System.console().writer().println("Email: " + to[i]);
                toAddress[i] = new InternetAddress(to[i]);
            }

            System.out.println(Message.RecipientType.TO);

            for( int i=0; i < toAddress.length; i++) { // changed from a while loop
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            List<Item> items = get_last_items();

            message.setSubject("Novidades do MyBay");
            String aux = "";
            aux += "Os seguintes produtos foram lançados: \n\n";

            for(Item i:items){
                aux += "Nome: " + i.getName() + " | Categoria: " + i.getCategory() + " | País: " + i.getCountry() + " | Preço: " + i.getPrice() + " | Data de publicação: " + i.getPublish_date() + "\n";
            }

            message.setText(aux);

            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch(Exception e){
            e.getMessage();
        }
    }

}