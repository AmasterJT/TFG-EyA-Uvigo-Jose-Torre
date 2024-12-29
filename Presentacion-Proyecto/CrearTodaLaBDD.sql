DROP DATABASE tfg_almacenDB;

-- Creación de la base de datos
create database tfg_almacenDB;

use tfg_almacenDB;

-- Creación de la tabla permisos_usuarios
CREATE TABLE
    permisos_usuarios (
        id_permiso INT PRIMARY KEY AUTO_INCREMENT,
        permiso VARCHAR(50) NOT NULL, -- nombre del permiso
        descripcion TEXT -- descripción de la utilidad del permiso
    );

-- Inserción de los permisos por defecto
INSERT INTO
    permisos_usuarios (permiso, descripcion)
VALUES
    ('Gestión de Usuarios', 'Permiso para crear, editar o eliminar usuarios del sistema.'),
    ('Gestión de Productos', 'Permiso para gestionar la información de los productos, incluyendo la creación, edición y eliminación.'),
    ('Gestión de Inventario', 'Permiso para administrar el inventario, realizar ajustes y mantener registros actualizados.'),
    ('Registro de Entradas/Salidas de Productos', 'Permiso para registrar y gestionar el movimiento de productos dentro y fuera del sistema.'),
    ('Gestión de Pedidos', 'Permiso para realizar, modificar o cancelar pedidos dentro del sistema.'),
    ('Generación de Reportes', 'Permiso para generar reportes detallados sobre diversas actividades dentro del sistema.'),
    ('Asignación de Tareas', 'Permiso para asignar tareas a usuarios o grupos dentro del sistema.'),
    ('Acceso al Historial de Movimientos', 'Permiso para acceder al historial de movimientos y transacciones registradas en el sistema.'),
    ('Mantenimiento de Infraestructura', 'Permiso para realizar tareas de mantenimiento en la infraestructura tecnológica del sistema.'),
    ('Gestión Financiera', 'Permiso para gestionar aspectos financieros, como facturación y pagos dentro del sistema.'),
    ('Gestión de Incidencias', 'Permiso para gestionar y hacer seguimiento a incidencias o problemas reportados en el sistema.');

-- Creación de la tabla roles
CREATE TABLE
    roles (
        id_rol INT PRIMARY KEY AUTO_INCREMENT,
        nombre_rol VARCHAR(20) NOT NULL,
        descripcion TEXT
    );

-- Inserción de los roles por defecto
INSERT INTO
    roles (nombre_rol, descripcion)
VALUES
    ('SysAdmin', 'Administrador del sistema con todos los privilegios.'),
    ('Gestor Almacén', 'Responsable de la gestión del inventario y almacenes.'),
    ('Supervisor', 'Encargado de supervisar las operaciones y los equipos.'),
    ('Operario', 'Trabajador encargado de tareas específicas operativas.'),
    ('Mantenimiento', 'Responsable del mantenimiento y reparaciones.'),
    ('Administración', 'Gestión administrativa y documentación.'),
    ('Proveedor', 'Entidad o persona que suministra productos o servicios.'),
    ('Cliente', 'Usuario final o consumidor de los productos o servicios.');

-- Creación de la tabla rol_permiso
CREATE TABLE
    rol_permiso (
        id_rol INT,
        id_permiso INT,
        estado ENUM ('activo', 'inactivo', 'ver') NOT NULL,
        PRIMARY KEY (id_rol, id_permiso),
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol),
        FOREIGN KEY (id_permiso) REFERENCES permisos_usuarios (id_permiso)
    );



-- Insertar relación entre Roles y Permisos
-- Para "Gestión de Usuarios"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 1, 'activo'),
    -- Gestor Almacén
    (2, 1, 'inactivo'),
    -- Supervisor
    (3, 1, 'inactivo'),
    -- Operario
    (4, 1, 'inactivo'),
    -- Mantenimiento
    (5, 1, 'inactivo'),
    -- Administración
    (6, 1, 'inactivo'),
    -- Proveedor
    (7, 1, 'inactivo'),
    -- Cliente
    (8, 1, 'inactivo');

-- Para "Gestión de Productos (CRUD)"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 2, 'activo'),
    -- Gestor Almacén
    (2, 2, 'activo'),
    -- Supervisor
    (3, 2, 'activo'),
    -- Operario
    (4, 2, 'inactivo'),
    -- Mantenimiento
    (5, 2, 'inactivo'),
    -- Administración
    (6, 2, 'inactivo'),
    -- Proveedor
    (7, 2, 'inactivo'),
    -- Cliente
    (8, 2, 'inactivo');

-- Para "Gestión de Inventario (stock, ubicación)"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 3, 'activo'),
    -- Gestor Almacén
    (2, 3, 'activo'),
    -- Supervisor
    (3, 3, 'activo'),
    -- Operario
    (4, 3, 'ver'),
    -- Mantenimiento
    (5, 3, 'inactivo'),
    -- Administración
    (6, 3, 'ver'),
    -- Proveedor
    (7, 3, 'inactivo'),
    -- Cliente
    (8, 3, 'inactivo');

-- Para "Registro de Entradas/Salidas de Productos"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 4, 'activo'),
    -- Gestor Almacén
    (2, 4, 'activo'),
    -- Supervisor
    (3, 4, 'activo'),
    -- Operario
    (4, 4, 'activo'),
    -- Mantenimiento
    (5, 4, 'inactivo'),
    -- Administración
    (6, 4, 'inactivo'),
    -- Proveedor
    (7, 4, 'inactivo'),
    -- Cliente
    (8, 4, 'inactivo');

-- Para "Gestión de Pedidos"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 5, 'activo'),
    -- Gestor Almacén
    (2, 5, 'activo'),
    -- Supervisor
    (3, 5, 'activo'),
    -- Operario
    (4, 5, 'activo'),
    -- Mantenimiento
    (5, 5, 'inactivo'),
    -- Administración
    (6, 5, 'ver'),
    -- Proveedor
    (7, 5, 'inactivo'),
    -- Cliente
    (8, 5, 'activo');

-- Para "Generación de Reportes (inventario, ventas)"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 6, 'activo'),
    -- Gestor Almacén
    (2, 6, 'activo'),
    -- Supervisor
    (3, 6, 'activo'),
    -- Operario
    (4, 6, 'inactivo'),
    -- Mantenimiento
    (5, 6, 'inactivo'),
    -- Administración
    (6, 6, 'activo'),
    -- Proveedor
    (7, 6, 'inactivo'),
    -- Cliente
    (8, 6, 'inactivo');

-- Para "Asignación de Tareas (Picking/Packing)"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 7, 'activo'),
    -- Gestor Almacén
    (2, 7, 'activo'),
    -- Supervisor
    (3, 7, 'activo'),
    -- Operario
    (4, 7, 'inactivo'),
    -- Mantenimiento
    (5, 7, 'inactivo'),
    -- Administración
    (6, 7, 'inactivo'),
    -- Proveedor
    (7, 7, 'inactivo'),
    -- Cliente
    (8, 7, 'inactivo');

-- Para "Acceso al Historial de Movimientos"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 8, 'activo'),
    -- Gestor Almacén
    (2, 8, 'activo'),
    -- Supervisor
    (3, 8, 'activo'),
    -- Operario
    (4, 8, 'ver'),
    -- Mantenimiento
    (5, 8, 'ver'),
    -- Administración
    (6, 8, 'ver'),
    -- Proveedor
    (7, 8, 'inactivo'),
    -- Cliente
    (8, 8, 'inactivo');

-- Para "Mantenimiento de Infraestructura"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 9, 'activo'),
    -- Gestor Almacén
    (2, 9, 'inactivo'),
    -- Supervisor
    (3, 9, 'ver'),
    -- Operario
    (4, 9, 'inactivo'),
    -- Mantenimiento
    (5, 9, 'activo'),
    -- Administración
    (6, 9, 'inactivo'),
    -- Proveedor
    (7, 9, 'inactivo'),
    -- Cliente
    (8, 9, 'inactivo');

-- Para "Gestión Financiera (compras/ventas)"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 10, 'activo'),
    -- Gestor Almacén
    (2, 10, 'inactivo'),
    -- Supervisor
    (3, 10, 'inactivo'),
    -- Operario
    (4, 10, 'inactivo'),
    -- Mantenimiento
    (5, 10, 'inactivo'),
    -- Administración
    (6, 10, 'activo'),
    -- Proveedor
    (7, 10, 'inactivo'),
    -- Cliente
    (8, 10, 'ver');

-- Para "Gestión de Incidencias"
INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    -- SysAdmin
    (1, 11, 'activo'),
    -- Gestor Almacén
    (2, 11, 'inactivo'),
    -- Supervisor
    (3, 11, 'ver'),
    -- Operario
    (4, 11, 'ver'),
    -- Mantenimiento
    (5, 11, 'ver'),
    -- Administración
    (6, 11, 'inactivo'),
    -- Proveedor
    (7, 11, 'inactivo'),
    -- Cliente
    (8, 11, 'inactivo');

-- Creación de la tabla estanterias
CREATE TABLE
    estanterias (
        id_estanteria INT PRIMARY KEY AUTO_INCREMENT,
        num_baldas INT NOT NULL,
        posiciones_por_balda INT NOT NULL,
        posiciones_disponibles INT NOT NULL,
        zona VARCHAR(100)
    );

DELIMITER //
CREATE TRIGGER calcular_posiciones_disponibles
BEFORE INSERT ON estanterias
FOR EACH ROW
BEGIN
    SET NEW.posiciones_disponibles = NEW.num_baldas * NEW.posiciones_por_balda;
END;
//
DELIMITER ;

-- Creación de la tabla usuarios
CREATE TABLE
    usuarios (
        id_usuario INT PRIMARY KEY AUTO_INCREMENT,
        nombre VARCHAR(100) NOT NULL,
        email VARCHAR(100) NOT NULL UNIQUE,
        contraseña VARCHAR(255) NOT NULL,
        id_rol INT,
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
    );

-- Creación de la tabla clientes
CREATE TABLE
    clientes (
        id_cliente INT PRIMARY KEY AUTO_INCREMENT,
        nombre_cliente VARCHAR(100) NOT NULL,
        email_cliente VARCHAR(150) UNIQUE NOT NULL,
        telefono_cliente VARCHAR(20),
        direccion_cliente VARCHAR(255),
        ciudad_cliente VARCHAR(100),
        estado_pais_cliente VARCHAR(100),
        pais_cliente VARCHAR(100),
        fecha_nacimiento DATE,
        estado_cliente BOOLEAN DEFAULT TRUE,
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- Creación de la tabla productos
CREATE TABLE
    productos (
        id_producto INT PRIMARY KEY AUTO_INCREMENT,
        nombre_producto VARCHAR(100) NOT NULL,
        descripcion TEXT,
        precio DECIMAL(10, 2),
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Creación de la tabla palets
CREATE TABLE
    palets (
        id_palet INT PRIMARY KEY AUTO_INCREMENT,
        id_producto INT,
        cantidad INT NOT NULL,
        estanteria INT NOT NULL,
        balda INT NOT NULL,
        posicion INT NOT NULL,
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_producto) REFERENCES productos (id_producto)
    );

-- Creación de la tabla movimientos
CREATE TABLE
    movimientos (
        id_movimiento INT PRIMARY KEY AUTO_INCREMENT,
        id_usuario INT,
        id_palet INT,
        tipo_movimiento ENUM ('Entrada', 'Salida') NOT NULL,
        cantidad INT NOT NULL,
        fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        observaciones TEXT,
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
        FOREIGN KEY (id_palet) REFERENCES palets (id_palet)
    );

-- Creación de la tabla pedidos
CREATE TABLE
    pedidos (
        id_pedido INT PRIMARY KEY AUTO_INCREMENT,
        codigo_referencia VARCHAR(50) UNIQUE,
        id_usuario INT NOT NULL,
        id_cliente INT NOT NULL,
        fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        estado ENUM ('Pendiente', 'Completado', 'En proceso', 'Cancelado') NOT NULL,
        FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente),
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
    );

DELIMITER //
CREATE TRIGGER generar_codigo_referencia
BEFORE INSERT ON pedidos
FOR EACH ROW
BEGIN
    DECLARE secuencia_diaria INT;
    DECLARE secuencia_hex VARCHAR(10);
    DECLARE nuevo_codigo VARCHAR(50);

    SELECT COUNT(*) + 1 INTO secuencia_diaria
    FROM pedidos
    WHERE DATE(fecha_pedido) = DATE(NEW.fecha_pedido);

    SET secuencia_hex = LPAD(HEX(secuencia_diaria), 6, '0');
    SET nuevo_codigo = CONCAT('PED-', DATE_FORMAT(NEW.fecha_pedido, '%Y%m%d'), '-', secuencia_hex);
    SET NEW.codigo_referencia = nuevo_codigo;
END;
//
DELIMITER ;

-- Creación de la tabla detalles_pedido
CREATE TABLE
    detalles_pedido (
        id_detalle INT PRIMARY KEY AUTO_INCREMENT,
        id_pedido INT,
        id_palet INT,
        cantidad INT NOT NULL,
        FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido),
        FOREIGN KEY (id_palet) REFERENCES palets (id_palet)
    );


-- -------------------------------------------------------------------------------------------------
-- ------------------------------------ DATOS EJEMPLO ----------------------------------------------
-- -------------------------------------------------------------------------------------------------
-- Desactivar temporalmente las comprobaciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar las tablas (eliminar todos los registros)
TRUNCATE TABLE detalles_pedido;
TRUNCATE TABLE pedidos;
TRUNCATE TABLE movimientos;
TRUNCATE TABLE productos;
TRUNCATE TABLE usuarios;
TRUNCATE TABLE estanterias;

-- Reactivar las comprobaciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- Establecer valores iniciales para el auto incremento de varias tablas
ALTER TABLE roles AUTO_INCREMENT = 7;
ALTER TABLE estanterias AUTO_INCREMENT = 1;
ALTER TABLE usuarios AUTO_INCREMENT = 1;
ALTER TABLE productos AUTO_INCREMENT = 1;
ALTER TABLE palets AUTO_INCREMENT = 1;
ALTER TABLE movimientos AUTO_INCREMENT = 1;
ALTER TABLE pedidos AUTO_INCREMENT = 1;
ALTER TABLE detalles_pedido AUTO_INCREMENT = 1;

-- Inserción de datos en la tabla Estanterias
INSERT INTO estanterias (num_baldas, posiciones_por_balda)
VALUES
    -- Estantería 1: 8 baldas, 24 posiciones por balda
    (8, 24), 
    -- Estantería 2: 8 baldas, 4 posiciones por balda
    (8, 4), 
    -- Estantería 3: 10 baldas, 24 posiciones por balda
    (10, 24), 
    -- Estantería 4: 6 baldas, 10 posiciones por balda
    (6, 10); 

-- Inserción de datos en la tabla Usuarios
INSERT INTO usuarios (nombre, email, contraseña, id_rol)
VALUES
    -- Usuario SysAdmin
    ('Admin', 'admin@almacen.com', 'Admin', 1),
    -- Usuario Gestor Almacén
    ('Juan Perez', 'juan.perez@almacen.com', '1234', 2),
    -- Usuario Supervisor
    ('Maria Lopez', 'maria.lopez@almacen.com', 'dasd213', 3),
    -- Usuario Operario
    ('Carlos Diaz', 'carlos.diaz@almacen.com', 'fdsfEre23123*', 4),
    -- Usuario Mantenimiento
    ('Luisa Gomez', 'luisa.gomez@almacen.com', '234566', 5),
    -- Usuario Administración
    ('Ana Torres', 'ana.torres@almacen.com', '111111111111', 6);

-- Insertando registros en la tabla Clientes
INSERT INTO clientes (nombre_cliente, email_cliente, telefono_cliente, direccion_cliente, ciudad_cliente, estado_pais_cliente, pais_cliente, fecha_nacimiento, estado_cliente)
VALUES
    -- Cliente 1: Juan Pérez
    ('Juan Pérez', 'juan.perez@example.com', '+52-555-123-4567', 'Av. Reforma 123', 'Ciudad de México', 'CDMX', 'México', '1985-04-15', TRUE),
    -- Cliente 2: María López
    ('María López', 'maria.lopez@example.com', '+52-555-987-6543', 'Calle Independencia 456', 'Monterrey', 'Nuevo León', 'México', '1990-07-22', TRUE),
    -- Cliente 3: Carlos Sánchez
    ('Carlos Sánchez', 'carlos.sanchez@example.com', '+52-812-345-6789', 'Blvd. Hidalgo 789', 'Guadalajara', 'Jalisco', 'México', '1982-11-02', FALSE),
    -- Cliente 4: Ana Rodríguez
    ('Ana Rodríguez', 'ana.rodriguez@example.com', '+1-310-555-1234', '1234 Sunset Blvd', 'Los Ángeles', 'California', 'Estados Unidos', '1995-06-30', TRUE),
    -- Cliente 5: Pedro Gómez
    ('Pedro Gómez', 'pedro.gomez@example.com', '+44-20-7946-0958', '10 Downing Street', 'Londres', 'Inglaterra', 'Reino Unido', '1979-03-18', TRUE),
    -- Cliente 6: Laura Fernández
    ('Laura Fernández', 'laura.fernandez@example.com', NULL, 'Calle Principal 123', 'Madrid', 'Madrid', 'España', '1988-08-10', TRUE),
    -- Cliente 7: Miguel Herrera
    ('Miguel Herrera', 'miguel.herrera@example.com', '+52-333-678-1234', NULL, 'Tijuana', 'Baja California', 'México', '1992-01-05', FALSE),
    -- Cliente 8: Sofía Morales
    ('Sofía Morales', 'sofia.morales@example.com', '+34-655-123-456', 'Av. del Sol 234', 'Barcelona', 'Cataluña', 'España', '1986-12-25', TRUE),
    -- Cliente 9: Diego Martínez
    ('Diego Martínez', 'diego.martinez@example.com', '+54-11-4321-5678', 'Av. 9 de Julio 567', 'Buenos Aires', 'Buenos Aires', 'Argentina', '1993-09-12', TRUE),
    -- Cliente 10: Lucía Jiménez
    ('Lucía Jiménez', 'lucia.jimenez@example.com', '+49-30-5678-1234', 'Unter den Linden 1', 'Berlín', 'Berlín', 'Alemania', '1980-05-08', TRUE);

-- Inserción de datos en la tabla Productos
INSERT INTO productos (nombre_producto, descripcion, precio)
VALUES
    -- Producto 1: Lápices
    ('Lápices', 'Caja de 50 lápices de madera', 10.50),
    -- Producto 2: Cuadernos
    ('Cuadernos', 'Cuadernos tamaño A4 de 100 hojas', 5.75),
    -- Producto 3: Bolígrafos
    ('Bolígrafos', 'Paquete de 20 bolígrafos azules', 7.20),
    -- Producto 4: Marcadores
    ('Marcadores', 'Set de 12 marcadores de colores', 15.00),
    -- Producto 5: Gomas de borrar
    ('Gomas de borrar', 'Pack de 10 gomas de borrar suaves', 3.50);

-- Inserción de datos en la tabla Palets
INSERT INTO palets (id_producto, cantidad, estanteria, balda, posicion)
VALUES
    -- Palet 1: Lápices, 100 unidades en la estantería 1, balda 2, posición 3
    (1, 100, 1, 2, 3),
    -- Palet 2: Lápices, 50 unidades en la estantería 2, balda 4, posición 2
    (1, 50, 2, 4, 2),
    -- Palet 3: Cuadernos, 200 unidades en la estantería 3, balda 3, posición 3
    (2, 200, 3, 3, 3),
    -- Palet 4: Bolígrafos, 150 unidades en la estantería 1, balda 4, posición 1
    (3, 150, 1, 4, 1),
    -- Palet 5: Marcadores, 100 unidades en la estantería 3, balda 5, posición 3
    (4, 100, 3, 5, 3),
    -- Palet 6: Gomas de borrar, 300 unidades en la estantería 4, balda 4, posición 2
    (5, 300, 4, 4, 2);

-- Inserción de datos en la tabla Movimientos
INSERT INTO movimientos (id_usuario, id_palet, tipo_movimiento, cantidad, observaciones)
VALUES
    -- Movimiento 1: Entrada de lápices en Zona A
    (2, 1, 'Entrada', 100, 'Recepción de lápices en Zona A - Estante 1'),
    -- Movimiento 2: Salida de lápices a cliente
    (3, 2, 'Salida', 50, 'Envío parcial de lápices a cliente'),
    -- Movimiento 3: Entrada de cuadernos en Zona B
    (4, 3, 'Entrada', 200, 'Recepción de cuadernos en Zona B - Estante 3'),
    -- Movimiento 4: Entrada de bolígrafos en Zona C
    (5, 4, 'Entrada', 150, 'Recepción de bolígrafos en Zona C - Estante 1');

-- Inserción de datos en la tabla Pedidos
INSERT INTO pedidos (id_usuario, id_cliente, estado)
VALUES
    -- Pedido 1: Hecho por el Gestor Almacén
    (2, 2, 'Pendiente'),
    -- Pedido 2: Hecho por el Supervisor
    (3, 3, 'Completado'),
    -- Pedido 3: Hecho por el Operario
    (4, 4, 'En proceso'),
    -- Pedido 4: Hecho por el Administrador
    (6, 5, 'Pendiente'),
    -- Pedido 5: Hecho por el SysAdmin
    (1, 6, 'Completado'),
    -- Pedido 6: Hecho por el Gestor Almacén
    (2, 7, 'Cancelado'),
    -- Pedido 7: Hecho por el Supervisor
    (3, 8, 'Pendiente'),
    -- Pedido 8: Hecho por el Administrador
    (6, 9, 'Completado'),
    -- Pedido 9: Hecho por el Operario
    (4, 10, 'Pendiente'),
    -- Pedido 10: Hecho por el Gestor Almacén
    (2, 1, 'En proceso');


-- Inserción de datos en la tabla DetallesPedido
INSERT INTO detalles_pedido (id_pedido, id_palet, cantidad)
VALUES
    -- Detalles del Pedido 1: Incluye lápices y bolígrafos
    (1, 1, 20),  -- 20 unidades del primer palet (Lápices)
    (1, 3, 15),  -- 15 unidades del tercer palet (Bolígrafos)
    -- Detalles del Pedido 2: Incluye cuadernos y marcadores
    (2, 2, 10),  -- 10 unidades del segundo palet (Cuadernos)
    (2, 4, 5),   -- 5 unidades del cuarto palet (Marcadores)
    -- Detalles del Pedido 3: Incluye lápices, cuadernos y gomas de borrar
    (3, 1, 30),  -- 30 unidades del primer palet (Lápices)
    (3, 2, 20),  -- 20 unidades del segundo palet (Cuadernos)
    (3, 6, 10),  -- 10 unidades del sexto palet (Gomas de borrar)
    -- Detalles del Pedido 4: Incluye marcadores y bolígrafos
    (4, 4, 25),  -- 25 unidades del cuarto palet (Marcadores)
    (4, 3, 40),  -- 40 unidades del tercer palet (Bolígrafos)
    -- Detalles del Pedido 5: Incluye cuadernos, lápices y gomas de borrar
    (5, 2, 15),  -- 15 unidades del segundo palet (Cuadernos)
    (5, 1, 10),  -- 10 unidades del primer palet (Lápices)
    (5, 6, 5),   -- 5 unidades del sexto palet (Gomas de borrar)
    -- Detalles del Pedido 6: Incluye lápices y marcadores
    (6, 1, 50),  -- 50 unidades del primer palet (Lápices)
    (6, 4, 20),  -- 20 unidades del cuarto palet (Marcadores)
    -- Detalles del Pedido 7: Incluye bolígrafos, cuadernos y marcadores
    (7, 3, 35),  -- 35 unidades del tercer palet (Bolígrafos)
    (7, 2, 25),  -- 25 unidades del segundo palet (Cuadernos)
    (7, 4, 10),  -- 10 unidades del cuarto palet (Marcadores)
    -- Detalles del Pedido 8: Incluye lápices y gomas de borrar
    (8, 1, 40),  -- 40 unidades del primer palet (Lápices)
    (8, 6, 15),  -- 15 unidades del sexto palet (Gomas de borrar)
    -- Detalles del Pedido 9: Incluye bolígrafos y cuadernos
    (9, 3, 20),  -- 20 unidades del tercer palet (Bolígrafos)
    (9, 2, 30),  -- 30 unidades del segundo palet (Cuadernos)
    -- Detalles del Pedido 10: Incluye marcadores, lápices y bolígrafos
    (10, 4, 50), -- 50 unidades del cuarto palet (Marcadores)
    (10, 1, 20), -- 20 unidades del primer palet (Lápices)
    (10, 3, 25); -- 25 unidades del tercer palet (Bolígrafos)
