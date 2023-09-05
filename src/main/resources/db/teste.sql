-- massa de dados
insert into produto(descricao, preco, estoque) values 
    ('Arroz', 10.00, 100),
    ('Feijão', 5.00, 100),
    ('Macarrão', 3.00, 100),
    ('Açúcar', 4.00, 100),
    ('Sal', 2.00, 100),
    ('Óleo', 7.00, 100),
    ('Farinha', 3.00, 100),
    ('Café', 8.00, 100),
    ('Leite', 3.00, 100),
    ('Manteiga', 5.00, 100),
    ('Queijo', 10.00, 100),
    ('Presunto', 10.00, 100),
    ('Mortadela', 10.00, 100),
    ('Pão', 5.00, 100),
    ('Biscoito', 3.00, 100),
    ('Bolacha', 3.00, 100),
    ('Refrigerante', 5.00, 100),
    ('Suco', 5.00, 100),
    ('Cerveja', 5.00, 100),
    ('Vinho', 10.00, 100),
    ('Vodka', 10.00, 100),
    ('Whisky', 10.00, 100),
    ('Cachaça', 10.00, 100),
    ('Cigarro', 10.00, 100),
    ('Chocolate', 5.00, 100),
    ('Bala', 1.00, 100),
    ('Pirulito', 1.00, 100),
    ('Chiclete', 1.00, 100),
    ('Sabonete', 2.00, 100),
    ('Shampoo', 5.00, 100),
    ('Condicionador', 5.00, 100),
    ('Desodorante', 5.00, 100),
    ('Pasta de dente', 3.00, 100),
    ('Escova de dente', 3.00, 100),
    ('Fio dental', 3.00, 100)
    ;
    
-- fluxo de venda
-- inicializa a venda
select f_inicializa_venda('12345678901');
-- insere itens na venda
select f_insere_item_venda(1, 1, 1);
select f_insere_item_venda(1, 2, 1);
select f_insere_item_venda(1, 3, 1);
select * from v_produtos_venda where codigo_venda = 1;
-- mostra o valor total da venda
select f_valor_total(1);
-- atualiza a quantidade de um item na venda
select f_insere_item_venda(1, 1, 2);
select * from v_produtos_venda where codigo_venda = 1;
-- mostra o valor total da venda
select f_valor_total(1);
-- remove um item da venda
select f_insere_item_venda(1, 1, 0);
select * from v_produtos_venda where codigo_venda = 1;
-- mostra o valor total da venda
select f_valor_total(1);
-- finaliza a venda
select f_finaliza_venda(1);
-- mostra o estoque dos produtos vendidos
select descricao, estoque from produto where codigo in (select codigo_produto from item_venda where codigo_venda = 1);
-- cancela a venda
select f_cancela_venda(1);
-- mostra o estoque dos produtos vendidos
select descricao, estoque from produto where codigo in (select codigo_produto from item_venda where codigo_venda = 1);
