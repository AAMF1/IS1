<%--
  Created by IntelliJ IDEA.
  User: Tomas
  Date: 04/11/2019
  Time: 18:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html>
<head>
    <title>Adicionar Item</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
    <style>
        .container{
            width: 600px;
        }
    </style>
</head>
<body>
<%if((request.getSession().getAttribute("autenticado"))!=null){%>

<form action="./UserServlet" method="POST">
    <div class="btn-toolbar justify-content-between" role="toolbar" aria-label="Toolbar with button groups">
        <div class="btn-group" role="group" aria-label="First group">
            <button type="submit" class="btn btn-primary" name="action" value="editBtn">  Editar Perfil  </button>
            <div class="dropdown">
                <button class="btn btn-primary dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">  Items  </button>
                <div class="dropdown-menu" aria-labelledby="dropdownMenu2">
                    <button type="submit" class="dropdown-item" type="button" name="action" value="myItemsBtn">  Meus Items  </button>
                    <button type="submit" class="dropdown-item" type="button" name="action" value="addItemBtn">  Inserir Item  </button>
                </div>
            </div>
            <button type="submit" class="btn btn-primary" name="action" value="findBtn">  Procurar  </button>
        </div>
        <div class="input-group">
            <input name="pesquisa_rapida" type="text" class="form-control" placeholder="Pesquisa" aria-label="Input group example" aria-describedby="btnGroupAddon2">
            <div class="input-group-prepend">
                <button  type="submit" class="btn btn-primary" name="action" value="pesquisa_rapidaBtn">  Pesquisa  </button>
            </div>
            <button style="margin-left: 2px" type="submit" class="btn btn-primary" name="action" value="logoutBtn">  Logout  </button>
        </div>
    </div>
</form>

<form action="./UserServlet" method="POST">
    <div class="container" align="center">
        <div class="form-group">
            <label>Nome do Item</label>
            <input  class="form-control" name="nome" aria-describedby="emailHelp" placeholder="Digite o nome do item">
        </div>
        <div class="form-group">
            <label >País</label>
            <input class="form-control" name="pais"  placeholder="Digite o seu país">
        </div>
        <div class="form-group">
            <label>Categoria</label>
            <input class="form-control" name="categoria" placeholder="Digite a categoria do item">
        </div>
        <div class="form-group">
            <label>Preço</label>
            <input class="form-control" name="preco" placeholder="Digite o preço do item">
        </div>
        <div>
            <label>Fotografia do Item</label>
        </div>
        <div class="input-group mb-3">
            <div class="custom-file">
                <input class="form-control" name="url_foto" placeholder="Insira a localizacao do item">
            </div>
        </div>
        <button type="submit" name="action" value="editItem" class="btn btn-primary">Guardar</button>
    </div>
</form>

<%} else{ %>
<h>ERRO!! É necessário autenticação!!</h>
<%}%>

<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>
</html>