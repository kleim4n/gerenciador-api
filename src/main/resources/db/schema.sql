-- crie banco de dados para sistema de gerenciamento de vendas e estoque
drop database if exists bd_sistema_gerenciamento;
create database bd_sistema_gerenciamento;
use bd_sistema_gerenciamento;

create table produto (
    codigo int(4) not null auto_increment,
    descricao varchar(80) default null,
    preco double(10,2) default null,
    estoque int(4) default null,
    primary key (codigo)
);

create table cliente (
    codigo int(4) not null auto_increment,
    cpf varchar(11) not null,
    nome varchar(80) default null,
    endereco varchar(80) default null,
    telefone varchar(15) default null,
    primary key (codigo)
);

create table venda (
    codigo int(4) not null auto_increment primary key,
    data date default null,
    codigo_cliente int(4) default null,
    realizada boolean default false,
    desconto double(10,2) default null,
    foreign key (codigo_cliente) references cliente (codigo)
);

create table item_venda (
    codigo int(4) not null auto_increment primary key,
    codigo_venda int(4) default null,
    codigo_produto int(4) default null,
    quantidade int(4) default null,
    foreign key (codigo_venda) references venda (codigo),
    foreign key (codigo_produto) references produto (codigo)
);

-- funções
-- atualiza o estoque(1 aumenta quantidade, -1 diminui quantidade)
delimiter $$
create function f_atualiza_estoque (p_codigo_produto int(4), p_quantidade int(4))
returns boolean
begin
    declare v_estoque_atual int(4);
    select estoque into v_estoque_atual from produto where codigo = p_codigo_produto;
    -- verifica se a quantidade menor que zero
    if v_estoque_atual + p_quantidade < 0 then
        return false;
    end if;
    -- atualiza o estoque
    update produto set estoque = v_estoque_atual + p_quantidade where codigo = p_codigo_produto;
    return true;
end$$
delimiter ;

-- retorna o valor total da venda
delimiter $$
create function f_valor_total (p_codigo_venda int(4))
returns double(10,2)
begin
    declare v_valor_total double(10,2);
    select sum(produto.preco * item_venda.quantidade) into v_valor_total from produto, item_venda where produto.codigo = item_venda.codigo_produto and item_venda.codigo_venda = p_codigo_venda;
    return v_valor_total;
end$$
delimiter ;

-- retorna o valor total da venda com desconto de valor inteiro
delimiter $$
create function f_valor_total_desconto (p_codigo_venda int(4))
returns double(10,2)
begin
    declare v_valor_total_desconto double(10,2);
    select f_valor_total(p_codigo_venda) - venda.desconto into v_valor_total_desconto from venda where venda.codigo = p_codigo_venda;
    return v_valor_total_desconto;
end$$
delimiter ;

-- verifica se o produto está em estoque
delimiter $$
create function f_verifica_estoque (p_codigo_produto int(4), p_quantidade int(4))
returns boolean
begin
    declare v_estoque int(4);
    select estoque into v_estoque from produto where codigo = p_codigo_produto;
    if v_estoque >= p_quantidade then
        return true;
    else
        return false;
    end if;
end$$
delimiter ;

-- finaliza a venda
delimiter $$
create function f_finaliza_venda (p_codigo_venda int(4))
returns boolean
begin
    -- verifica se a venda já foi realizada
    if (select realizada from venda where codigo = p_codigo_venda) = true then
        return false;
    end if;
    -- atualiza o estoque dos produtos
    update produto set estoque = estoque - (select quantidade from item_venda where codigo_venda = p_codigo_venda and codigo_produto = produto.codigo) where codigo = (select codigo_produto from item_venda where codigo_venda = p_codigo_venda and codigo_produto = produto.codigo);
    -- atualiza o status da venda
    update venda set realizada = true where codigo = p_codigo_venda;
    return true;
end$$
delimiter ;

-- cancela a venda e retorna os produtos ao estoque
delimiter $$
create function f_cancela_venda (p_codigo_venda int(4))
returns boolean
begin
    -- verifica se a venda já foi realizada
    if (select realizada from venda where codigo = p_codigo_venda) = false then
        return false;
    end if;
    -- atualiza o status da venda
    update venda set realizada = false where codigo = p_codigo_venda;
    -- atualiza o estoque dos produtos
    update produto set estoque = estoque + (select quantidade from item_venda where codigo_venda = p_codigo_venda and codigo_produto = produto.codigo) where codigo = (select codigo_produto from item_venda where codigo_venda = p_codigo_venda and codigo_produto = produto.codigo);
    return true;
end$$
delimiter ;

-- função que inicializa uma venda
delimiter $$
create function f_inicializa_venda (p_cpf_cliente varchar(11))
returns int(4)
begin
    declare v_now date;
    declare v_codigo_cliente int(4);
    set v_now = now();
    -- se cpf não existir, cria um novo cliente
    if (p_cpf_cliente is not null) then
        if (select count(*) from cliente where cpf = p_cpf_cliente) = 0 then
            insert into cliente (cpf) values (p_cpf_cliente);
        end if;
        select codigo into v_codigo_cliente from cliente where cpf = p_cpf_cliente;
    end if;
    -- cria uma nova venda
    insert into venda (data, codigo_cliente) values (v_now, v_codigo_cliente);
    return (select codigo from venda where data = v_now);
end$$
delimiter ;

-- função que remove um item da venda
delimiter $$
create function f_remove_item_venda (p_codigo_venda int(4), p_codigo_produto int(4))
returns boolean
begin
    -- verifica se a venda já foi realizada
    if (select realizada from venda where codigo = p_codigo_venda) = true then
        return false;
    end if;
    -- remove o item da venda
    delete from item_venda where codigo_venda = p_codigo_venda and codigo_produto = p_codigo_produto;
    return true;
end$$
delimiter ;

-- função que insere ou atualiza um item na venda
delimiter $$
create function f_insere_item_venda (p_codigo_venda int(4), p_codigo_produto int(4), p_quantidade int(4))
returns boolean
begin
    -- verifica se a quantidade é maior que zero
    if p_quantidade < 0 then
        return false;
    end if;
    if p_quantidade = 0 then
        return f_remove_item_venda(p_codigo_venda, p_codigo_produto);
    end if;
    -- verifica se a venda já foi realizada
    if (select realizada from venda where codigo = p_codigo_venda) = true then
        return false;
    end if;
    -- verifica se o produto está em estoque
    if (select f_verifica_estoque(p_codigo_produto, p_quantidade)) = false then
        return false;
    end if;
    -- verifica se o item já existe na venda, se existir, atualiza a quantidade
    if (select count(*) from item_venda where codigo_venda = p_codigo_venda and codigo_produto = p_codigo_produto) > 0 then
        update item_venda set quantidade = p_quantidade where codigo_venda = p_codigo_venda and codigo_produto = p_codigo_produto;
        return true;
    end if;
    -- insere o item na venda
    insert into item_venda (codigo_venda, codigo_produto, quantidade) values (p_codigo_venda, p_codigo_produto, p_quantidade);
    return true;
end$$
delimiter ;

-- retorna o nome dos produtos de uma venda, suas quantidades e seus preços unitários e totais dado um codigo de venda
create view v_produtos_venda as
select produto.descricao, item_venda.quantidade, produto.preco, produto.preco * item_venda.quantidade as total, item_venda.codigo_venda from produto, item_venda where produto.codigo = item_venda.codigo_produto;