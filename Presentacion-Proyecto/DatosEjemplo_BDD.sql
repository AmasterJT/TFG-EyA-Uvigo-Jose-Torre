SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE DetallesPedido;
TRUNCATE TABLE Pedidos;
TRUNCATE TABLE Movimientos;
TRUNCATE TABLE Productos;
TRUNCATE TABLE Usuarios;
TRUNCATE TABLE Estanterias;
SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE Roles AUTO_INCREMENT = 7;         -- Continúa después del último ID existente
ALTER TABLE Estanterias AUTO_INCREMENT = 1;
ALTER TABLE Usuarios AUTO_INCREMENT = 1;
ALTER TABLE Productos AUTO_INCREMENT = 1;
ALTER TABLE Palets AUTO_INCREMENT = 1;
ALTER TABLE Movimientos AUTO_INCREMENT = 1;
ALTER TABLE Pedidos AUTO_INCREMENT = 1;
ALTER TABLE DetallesPedido AUTO_INCREMENT = 1;


-- Inserción de datos en la tabla Estanterias
INSERT INTO Estanterias (num_baldas, posiciones_por_balda) VALUES
(8, 24), -- Estanteria 1
(8, 24), -- Estanteria 2
(8, 24), -- Estanteria 3
(8, 24); -- Estanteria 4

-- Inserción de datos en la tabla Usuarios
INSERT INTO Usuarios (nombre, email, contraseña, id_rol) VALUES
('Admin', 'admin@almacen.com', 'hashed_password', 1), -- sysAdmin
('Juan Perez', 'juan.perez@almacen.com', 'hashed_password', 2), -- Gestor Almacén
('Maria Lopez', 'maria.lopez@almacen.com', 'hashed_password', 3), -- Supervisor
('Carlos Diaz', 'carlos.diaz@almacen.com', 'hashed_password', 4), -- Operario
('Luisa Gomez', 'luisa.gomez@almacen.com', 'hashed_password', 5), -- Mantenimiento
('Ana Torres', 'ana.torres@almacen.com', 'hashed_password', 6); -- Administración

-- Inserción de datos en la tabla Productos
INSERT INTO Productos (nombre_producto, descripcion, precio) VALUES
('Lápices', 'Caja de 50 lápices de madera', 10.50),
('Cuadernos', 'Cuadernos tamaño A4 de 100 hojas', 5.75),
('Bolígrafos', 'Paquete de 20 bolígrafos azules', 7.20),
('Marcadores', 'Set de 12 marcadores de colores', 15.00),
('Gomas de borrar', 'Pack de 10 gomas de borrar suaves', 3.50);

-- Inserción de datos en la tabla Palets
INSERT INTO Palets (id_producto, cantidad, estanteria, balda, posicion) VALUES
(1, 100, 1, 2, 3),  -- Lápices: 100 unidades en la estantería 1, balda 2, posición 3
(1, 50, 2, 4, 2),   -- Lápices: 50 unidades en la estantería 2, balda 4, posición 2
(2, 200, 3, 3, 3),  -- Cuadernos: 200 unidades en la estantería 3, balda 3, posición 3
(3, 150, 1, 4, 1),  -- Bolígrafos: 150 unidades en la estantería 1, balda 4, posición 1
(4, 100, 3, 5, 3),  -- Marcadores: 100 unidades en la estantería 3, balda 5, posición 3
(5, 300, 4, 4, 2);  -- Gomas de borrar: 300 unidades en la estantería 4, balda 4, posición 2

-- Inserción de datos en la tabla Movimientos
INSERT INTO Movimientos (id_usuario, id_palet, tipo_movimiento, cantidad, observaciones) VALUES
(2, 1, 'Entrada', 100, 'Recepción de lápices en Zona A - Estante 1'),
(3, 2, 'Salida', 50, 'Envío parcial de lápices a cliente'),
(4, 3, 'Entrada', 200, 'Recepción de cuadernos en Zona B - Estante 3'),
(5, 4, 'Entrada', 150, 'Recepción de bolígrafos en Zona C - Estante 1');

-- Inserción de datos en la tabla Pedidos
INSERT INTO Pedidos (id_usuario, estado) VALUES
(2, 'Pendiente'), -- Pedido creado por el Gestor Almacén
(3, 'Completado'); -- Pedido supervisado por el Supervisor

-- Inserción de datos en la tabla DetallesPedido
INSERT INTO DetallesPedido (id_pedido, id_palet, cantidad) VALUES
(1, 1, 20), -- Pedido 1 incluye 20 unidades del primer palet
(1, 2, 10), -- Pedido 1 incluye 10 unidades del segundo palet
(2, 3, 50); -- Pedido 2 incluye 50 unidades del tercer palet
