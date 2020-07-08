package servlet;


import data.Item;
import ejb.UserEJBLocal;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
    HttpSession newSession;
    private static final long serialVersionUID = 1L;
    @EJB
    private UserEJBLocal ejb;

    //Método Get -> HTTP
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        executeProcess(request, response);
    }

    //Método Post -> HTTP
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        executeProcess(request, response);
    }

    protected void executeProcess(HttpServletRequest request, HttpServletResponse response) throws  ServletException, IOException{
        String action = request.getParameter("action");
        int delete_item = 0, edit_item=0;
        List<Item> items=null;
        System.console().writer().println(action);
        request.setAttribute("flag", "false");


        if(request.getParameter("apagar_item")!=null){
            delete_item = 1;
        }
        if(request.getParameter("editar_item")!=null){
            edit_item = 1;
        }

        if("registarBtn".equalsIgnoreCase(action)){
            request.getRequestDispatcher("registo.jsp").forward(request, response);
        }
        else if("registar_user".equalsIgnoreCase(action)){
            System.console().writer().println("nome: "+request.getParameter("nome"));
            if(!request.getParameter("email").equals("") && !request.getParameter("password").equals("") && !request.getParameter("nome").equals("") && !request.getParameter("pais").equals("")){
                int controlo=ejb.registaUser(request.getParameter("email") , request.getParameter("password"), request.getParameter("nome") , request.getParameter("pais"));
                if(controlo==1)
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                else
                    request.getRequestDispatcher("registo.jsp").forward(request, response);
            }
            else
                System.out.println("erro");
        }

        else if("loginBtn".equalsIgnoreCase(action)){
            newSession = request.getSession();
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        else if("login_user".equalsIgnoreCase(action)){
            System.console().writer().println( "|"+request.getParameter("email") + "|" + request.getParameter("password") + "|");
            if(request.getParameter("email")!=null && request.getParameter("password")!=null){
                int id=ejb.loginUser(request.getParameter("email"), request.getParameter("password"));

                if(id!=-1){
                    newSession.setAttribute("id", id);
                    request.getSession().setAttribute("autenticado", "true");
                    newSession.setAttribute("autenticado", "true");
                    request.getRequestDispatcher("menu.jsp").forward(request, response);

                }
                else {
                    System.console().writer().println("Email ou password incorretos.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    //JSP ERRO
                }
            }
            else {
                //JSP ERRO
                System.console().writer().println("Campo nao preenchio");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }

        else if("editBtn".equalsIgnoreCase(action)){
            request.getRequestDispatcher("editar_perfil.jsp").forward(request, response);
        }
        else if("edit_user".equalsIgnoreCase(action)) {
            System.console().writer().println(Integer.parseInt(String.valueOf(newSession.getAttribute("id"))) + "|" + request.getParameter("nome") + "|" + request.getParameter("pais") + "|" + request.getParameter("password"));
            if (request.getParameter("nome") != null || request.getParameter("pais") != null || request.getParameter("password") != null) {
                ejb.editUser(Integer.parseInt(String.valueOf(newSession.getAttribute("id"))), request.getParameter("nome"), request.getParameter("pais"), request.getParameter("password"));
                request.getRequestDispatcher("menu.jsp").forward(request, response);
            } else {
                System.console().writer().println("Nao alterou nenhum campo");
                request.getRequestDispatcher("editar_perfil.jsp").forward(request, response);
            }
        }

        else if("remove_user".equalsIgnoreCase(action)){
            ejb.removeUser(Integer.parseInt(String.valueOf(newSession.getAttribute("id"))));
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }

        else if("myItemsBtn".equalsIgnoreCase(action)) {
            newSession.setAttribute("flag", true);
            request.setAttribute("flag", "true");
            items = ejb.getUser_items(Integer.parseInt(String.valueOf(newSession.getAttribute("id"))));
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("editItem".equalsIgnoreCase(action)) {
            if(request.getParameter("nome")!=null || request.getParameter("pais")!=null || request.getParameter("categoria")!=null || request.getParameter("preco")!=null){
                ejb.editItem(Integer.parseInt(String.valueOf(newSession.getAttribute("id_item"))), Integer.parseInt(String.valueOf(newSession.getAttribute("id"))), request.getParameter("nome"), request.getParameter("preco"), request.getParameter("pais"), request.getParameter("categoria"), request.getParameter("url_foto"));
                newSession.removeAttribute("id_item");
                newSession.setAttribute("flag", true);
                request.setAttribute("flag", "true");
                items = ejb.getUser_items(Integer.parseInt(String.valueOf(newSession.getAttribute("id"))));
                request.setAttribute("myItems", items);
                request.getRequestDispatcher("myItems.jsp").forward(request, response);
            }
            else
                request.getRequestDispatcher("editItem.jsp").forward(request, response);
        }

        else if(edit_item==1){
            edit_item=0;
            newSession.setAttribute("id_item", Integer.parseInt(request.getParameter("editar_item")));
            request.getRequestDispatcher("editItem.jsp").forward(request, response);
        }

        else if(delete_item==1){
            delete_item = 0;
            int id_item = Integer.parseInt(request.getParameter("apagar_item"));
            ejb.removeItem(id_item,Integer.parseInt(String.valueOf(newSession.getAttribute("id"))));
            items = ejb.getUser_items(Integer.parseInt(String.valueOf(newSession.getAttribute("id"))));
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);

        }
        else if("addItemBtn".equalsIgnoreCase(action)){
            request.getRequestDispatcher("addItem.jsp").forward(request, response);
        }

        else if("addItem".equalsIgnoreCase(action)){
            //ADD ITEM
            //TRANSFERIR PARA A ABA MEUS ITEMS PARA COMPROVAR QUE FOI ADICIONADO
            if(request.getParameter("nome")!=null && request.getParameter("pais")!=null && request.getParameter("categoria")!=null && request.getParameter("preco")!=null){
                ejb.addItem(request.getParameter("nome"), request.getParameter("preco"), request.getParameter("pais"), request.getParameter("categoria"), request.getParameter("url_foto"), Integer.parseInt(String.valueOf(newSession.getAttribute("id"))), request.getParameter("data"));
                newSession.setAttribute("flag", true);
                request.setAttribute("flag", "true");
                items = ejb.getUser_items(Integer.parseInt(String.valueOf(newSession.getAttribute("id"))));
                request.setAttribute("myItems", items);
                request.getRequestDispatcher("myItems.jsp").forward(request, response);
                if((ejb.verifica_bd()%5)==0){
                    ejb.send_email();
                }
            }
            else
                request.getRequestDispatcher("addItem.jsp").forward(request, response);
        }

        else if("nome_crescente".equalsIgnoreCase(action)){
            if(!(boolean)newSession.getAttribute("flag")){
                if(((String)newSession.getAttribute("tipo_pesquisa")).equals("price")){
                    if(newSession.getAttribute("nome_pesquisa")!=null)
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "name", (String) newSession.getAttribute("nome_pesquisa"), (boolean) newSession.getAttribute("flag"));
                    else
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "name", "", (boolean) newSession.getAttribute("flag"));
                }
                else
                    items=ejb.pesquisa_ordenada((String) newSession.getAttribute("input_pesquisa"), null, (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "name", "", (boolean) newSession.getAttribute("flag"));

            }
            else{
                items=ejb.pesquisa_ordenada(Integer.toString((Integer) newSession.getAttribute("id")), null, null, "crescente", "name", "", (boolean)newSession.getAttribute("flag"));
            }
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("nome_decrescente".equalsIgnoreCase(action)){
            if(!(boolean)newSession.getAttribute("flag")){
                if(((String)newSession.getAttribute("tipo_pesquisa")).equals("price")){
                    if(newSession.getAttribute("nome_pesquisa")!=null)
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "name", (String) newSession.getAttribute("nome_pesquisa"), (boolean) newSession.getAttribute("flag"));
                    else
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "name", "", (boolean) newSession.getAttribute("flag"));
                }
                else
                    items=ejb.pesquisa_ordenada((String) newSession.getAttribute("input_pesquisa"), null, (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "name", "", (boolean) newSession.getAttribute("flag"));

            }
            else{
                items=ejb.pesquisa_ordenada(Integer.toString((Integer) newSession.getAttribute("id")), null, null, "decrescente", "name", "", (boolean)newSession.getAttribute("flag"));
            }
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("preco_crescente".equalsIgnoreCase(action)){
            if(!(boolean)newSession.getAttribute("flag")){
                if(((String)newSession.getAttribute("tipo_pesquisa")).equals("price")){
                    if(newSession.getAttribute("nome_pesquisa")!=null)
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "price", (String) newSession.getAttribute("nome_pesquisa"), (boolean) newSession.getAttribute("flag"));
                    else
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "price", "", (boolean) newSession.getAttribute("flag"));
                }
                else
                    items=ejb.pesquisa_ordenada((String) newSession.getAttribute("input_pesquisa"), null, (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "price", "", (boolean) newSession.getAttribute("flag"));

            }
            else{
                items=ejb.pesquisa_ordenada(Integer.toString((Integer) newSession.getAttribute("id")), null, null, "crescente", "price", "", (boolean)newSession.getAttribute("flag"));
            }
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("preco_decrescente".equalsIgnoreCase(action)){
            if(!(boolean)newSession.getAttribute("flag")){
                if(((String)newSession.getAttribute("tipo_pesquisa")).equals("price")){
                    if(newSession.getAttribute("nome_pesquisa")!=null)
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "price", (String) newSession.getAttribute("nome_pesquisa"), (boolean) newSession.getAttribute("flag"));
                    else
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "price", "", (boolean) newSession.getAttribute("flag"));
                }
                else
                    items=ejb.pesquisa_ordenada((String) newSession.getAttribute("input_pesquisa"), null, (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "price", "", (boolean) newSession.getAttribute("flag"));

            }
            else{
                items=ejb.pesquisa_ordenada(Integer.toString((Integer) newSession.getAttribute("id")), null, null, "decrescente", "price", "", (boolean)newSession.getAttribute("flag"));
            }
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("data_crescente".equalsIgnoreCase(action)){
            if(!(boolean)newSession.getAttribute("flag")){
                if(((String)newSession.getAttribute("tipo_pesquisa")).equals("price")){
                    if(newSession.getAttribute("nome_pesquisa")!=null)
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "publish_date", (String) newSession.getAttribute("nome_pesquisa"), (boolean) newSession.getAttribute("flag"));
                    else
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "publish_date", "", (boolean) newSession.getAttribute("flag"));
                }
                else
                    items=ejb.pesquisa_ordenada((String) newSession.getAttribute("input_pesquisa"), null, (String) newSession.getAttribute("tipo_pesquisa"), "crescente", "publish_date", "", (boolean) newSession.getAttribute("flag"));

            }
            else{
                items=ejb.pesquisa_ordenada(Integer.toString((Integer) newSession.getAttribute("id")), null, null, "crescente", "publish_date", "", (boolean)newSession.getAttribute("flag"));
            }
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("data_decrescente".equalsIgnoreCase(action)){
            if(!(boolean)newSession.getAttribute("flag")){
                if(((String)newSession.getAttribute("tipo_pesquisa")).equals("price")){
                    if(newSession.getAttribute("nome_pesquisa")!=null)
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "publish_date", (String) newSession.getAttribute("nome_pesquisa"), (boolean) newSession.getAttribute("flag"));
                    else
                        items=ejb.pesquisa_ordenada((String) newSession.getAttribute("preco_min"), (String) newSession.getAttribute("preco_max"), (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "publish_date", "", (boolean) newSession.getAttribute("flag"));
                }
                else
                    items=ejb.pesquisa_ordenada((String) newSession.getAttribute("input_pesquisa"), null, (String) newSession.getAttribute("tipo_pesquisa"), "decrescente", "publish_date", "", (boolean) newSession.getAttribute("flag"));

            }
            else{
                items=ejb.pesquisa_ordenada(Integer.toString((Integer) newSession.getAttribute("id")), null, null, "decrescente", "publish_date", "", (boolean)newSession.getAttribute("flag"));
            }
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("findBtn".equalsIgnoreCase(action)){
            request.getRequestDispatcher("find.jsp").forward(request, response);
        }

        else if("pesquisa_rapidaBtn".equalsIgnoreCase(action)){
            newSession.setAttribute("flag", false);
            request.setAttribute("flag", "false");
            newSession.setAttribute("input_pesquisa", request.getParameter("pesquisa_rapida"));
            newSession.setAttribute("tipo_pesquisa", "pesquisa_rapida");
            if(request.getParameter("pesquisa_rapida")!=null){
                //ERRO, deve pesquisar só por parte da string
                items = ejb.pesquisa_rapida(request.getParameter("pesquisa_rapida"));
                request.setAttribute("myItems", items);
                request.getRequestDispatcher("myItems.jsp").forward(request, response);
            }
        }

        else if("pesquisaCategoriaBtn".equalsIgnoreCase(action)){
            //ERRO, deve pesquisar tambem, só por parte da string
            newSession.setAttribute("flag", false);
            request.setAttribute("flag", "false");
            newSession.setAttribute("input_pesquisa", request.getParameter("pesquisa_categoria"));
            newSession.setAttribute("tipo_pesquisa", "category");
            newSession.setAttribute("nome_pesquisa", request.getParameter("nome_pesquisa"));
            if(newSession.getAttribute("nome_pesquisa")!=null)
                items = ejb.pesquisa(request.getParameter("pesquisa_categoria"), null, (String) newSession.getAttribute("nome_pesquisa"),"categoria");
            else
                items = ejb.pesquisa(request.getParameter("pesquisa_categoria"), null, "","categoria");
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("pesquisaPaisBtn".equalsIgnoreCase(action)){
            newSession.setAttribute("flag", false);
            request.setAttribute("flag", "false");
            newSession.setAttribute("input_pesquisa", request.getParameter("pesquisa_pais"));
            newSession.setAttribute("tipo_pesquisa", "country");
            newSession.setAttribute("nome_pesquisa", request.getParameter("nome_pesquisa"));
            //ERRO, deve pesquisar tambem, só por parte da string
            if(newSession.getAttribute("nome_pesquisa")!=null)
                items = ejb.pesquisa(request.getParameter("pesquisa_pais"), null, (String) newSession.getAttribute("nome_pesquisa"),"pais");
            else
                items = ejb.pesquisa(request.getParameter("pesquisa_pais"), null, "","categoria");
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("pesquisaPrecoBtn".equalsIgnoreCase(action)){
            newSession.setAttribute("flag", false);
            //ERRO, deve pesquisar tambem, só por parte da string
            newSession.setAttribute("tipo_pesquisa", "price");
            request.setAttribute("flag", "false");
            newSession.setAttribute("preco_min", request.getParameter("pesquisa_preco_min"));
            newSession.setAttribute("preco_max", request.getParameter("pesquisa_preco_max"));
            newSession.setAttribute("nome_pesquisa", request.getParameter("nome_pesquisa"));
            System.console().writer().println("nome de pesquisa  "+ (String) newSession.getAttribute("nome_pesquisa"));
            if(newSession.getAttribute("nome_pesquisa")!=null)
                items = ejb.pesquisa(request.getParameter("pesquisa_preco_min"), request.getParameter("pesquisa_preco_max"), (String) newSession.getAttribute("nome_pesquisa"),"preco");
            else{
                items = ejb.pesquisa(request.getParameter("pesquisa_preco_min"), request.getParameter("pesquisa_preco_max"), "","preco");

            }

            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }

        else if("pesquisaDataBtn".equalsIgnoreCase(action)){
            newSession.setAttribute("flag", false);
            request.setAttribute("flag", "false");
            //ERRO, deve pesquisar tambem, só por parte da string
            newSession.setAttribute("input_pesquisa", request.getParameter("pesquisa_data"));
            newSession.setAttribute("tipo_pesquisa", "publish_date");
            newSession.setAttribute("nome_pesquisa", request.getParameter("nome_pesquisa"));
            if(newSession.getAttribute("nome_pesquisa")!=null)
                items = ejb.pesquisa(request.getParameter("pesquisa_data"), null, (String) newSession.getAttribute("nome_pesquisa"),"data");
            else
                items = ejb.pesquisa(request.getParameter("pesquisa_data"), null, "",null);
            request.setAttribute("myItems", items);
            request.getRequestDispatcher("myItems.jsp").forward(request, response);
        }


        else if("logoutBtn".equalsIgnoreCase(action)){
            //LIMPAR SESSAO
            newSession.invalidate();
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
        else {
            newSession=request.getSession(true);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }

    }

}