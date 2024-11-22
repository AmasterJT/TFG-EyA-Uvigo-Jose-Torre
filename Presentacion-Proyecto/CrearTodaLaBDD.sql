DROP DATABASE tfg_almacenDB;

-- Creación de la base de datos
create database tfg_almacenDB;

use tfg_almacenDB;

-- Creación de la tabla PerimisosUsuarios, donde se definen los distintos permisos que tienen los usuarios
CREATE TABLE
    PerimisosUsuarios (
        id_permiso INT PRIMARY KEY AUTO_INCREMENT,
        permiso VARCHAR(50) NOT NULL, -- nombre del permiso
        descripcion TEXT -- descripción de la utilidad del permiso
    );

-- Inserción de los permisos por defecto
INSERT INTO
    PerimisosUsuarios (permiso, descripcion)
VALUES
    (
        'Gestión de Usuarios',
        'Permiso para crear, editar o eliminar usuarios del sistema.'
    ),
    (
        'Gestión de Productos',
        'Permiso para gestionar la información de los productos, incluyendo la creación, edición y eliminación.'
    ),
    (
        'Gestión de Inventario',
        'Permiso para administrar el inventario, realizar ajustes y mantener registros actualizados.'
    ),
    (
        'Registro de Entradas/Salidas de Productos',
        'Permiso para registrar y gestionar el movimiento de productos dentro y fuera del sistema.'
    ),
    (
        'Gestión de Pedidos',
        'Permiso para realizar, modificar o cancelar pedidos dentro del sistema.'
    ),
    (
        'Generación de Reportes',
        'Permiso para generar reportes detallados sobre diversas actividades dentro del sistema.'
    ),
    (
        'Asignación de Tareas',
        'Permiso para asignar tareas a usuarios o grupos dentro del sistema.'
    ),
    (
        'Acceso al Historial de Movimientos',
        'Permiso para acceder al historial de movimientos y transacciones registradas en el sistema.'
    ),
    (
        'Mantenimiento de Infraestructura',
        'Permiso para realizar tareas de mantenimiento en la infraestructura tecnológica del sistema.'
    ),
    (
        'Gestión Financiera',
        'Permiso para gestionar aspectos financieros, como facturación y pagos dentro del sistema.'
    ),
    (
        'Gestión de Incidencias',
        'Permiso para gestionar y hacer seguimiento a incidencias o problemas reportados en el sistema.'
    );

-- Creación de la tabla Roles, donde se definen los distintos roles del sistema
CREATE TABLE
    Roles (
        id_rol INT PRIMARY KEY AUTO_INCREMENT,
        nombre_rol VARCHAR(20) NOT NULL,
        descripcion TEXT
    );

-- Inserción de los roles por defecto
INSERT INTO
    Roles (nombre_rol, descripcion)
VALUES
    (
        'SysAdmin',
        'Administrador del sistema con todos los privilegios.'
    ),
    (
        'Gestor Almacén',
        'Responsable de la gestión del inventario y almacenes.'
    ),
    (
        'Supervisor',
        'Encargado de supervisar las operaciones y los equipos.'
    ),
    (
        'Operario',
        'Trabajador encargado de tareas específicas operativas.'
    ),
    (
        'Mantenimiento',
        'Responsable del mantenimiento y reparaciones.'
    ),
    (
        'Administración',
        'Gestión administrativa y documentación.'
    ),
    (
        'Proveedor',
        'Entidad o persona que suministra productos o servicios.'
    ),
    (
        'Cliente',
        'Usuario final o consumidor de los productos o servicios.'
    );

-- Creación de una tabla intermedia (relacional) que vincule las tablas Roles y PerimisosUsuarios. Dado que un rol puede tener múltiples permisos
-- y un permiso puede ser asignado a varios roles, la relación entre estas dos tablas es de tipo muchos a muchos.
CREATE TABLE
    RolPermiso (
        id_rol INT,
        id_permiso INT,
        estado ENUM ('activo', 'inactivo', 'ver') NOT NULL,
        PRIMARY KEY (id_rol, id_permiso),
        FOREIGN KEY (id_rol) REFERENCES Roles (id_rol),
        FOREIGN KEY (id_permiso) REFERENCES PerimisosUsuarios (id_permiso)
    );

-- Insertar relación entre Roles y Permisos
-- Para "Gestión de Usuarios"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 1, 'activo'), -- SysAdmin
    (2, 1, 'inactivo'), -- Gestor Almacén
    (3, 1, 'inactivo'), -- Supervisor
    (4, 1, 'inactivo'), -- Operario
    (5, 1, 'inactivo'), -- Mantenimiento
    (6, 1, 'inactivo'), -- Administración
    (7, 1, 'inactivo'), -- Proveedor
    (8, 1, 'inactivo');

-- Cliente
-- Para "Gestión de Productos (CRUD)"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 2, 'activo'), -- SysAdmin
    (2, 2, 'activo'), -- Gestor Almacén
    (3, 2, 'activo'), -- Supervisor
    (4, 2, 'inactivo'), -- Operario
    (5, 2, 'inactivo'), -- Mantenimiento
    (6, 2, 'inactivo'), -- Administración
    (7, 2, 'inactivo'), -- Proveedor
    (8, 2, 'inactivo');

-- Cliente
-- Para "Gestión de Inventario (stock, ubicación)"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 3, 'activo'), -- SysAdmin
    (2, 3, 'activo'), -- Gestor Almacén
    (3, 3, 'activo'), -- Supervisor
    (4, 3, 'ver'), -- Operario
    (5, 3, 'inactivo'), -- Mantenimiento
    (6, 3, 'ver'), -- Administración
    (7, 3, 'inactivo'), -- Proveedor
    (8, 3, 'inactivo');

-- Cliente
-- Para "Registro de Entradas/Salidas de Productos"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 4, 'activo'), -- SysAdmin
    (2, 4, 'activo'), -- Gestor Almacén
    (3, 4, 'activo'), -- Supervisor
    (4, 4, 'activo'), -- Operario
    (5, 4, 'inactivo'), -- Mantenimiento
    (6, 4, 'inactivo'), -- Administración
    (7, 4, 'inactivo'), -- Proveedor
    (8, 4, 'inactivo');

-- Cliente
-- Para "Gestión de Pedidos"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 5, 'activo'), -- SysAdmin
    (2, 5, 'activo'), -- Gestor Almacén
    (3, 5, 'activo'), -- Supervisor
    (4, 5, 'activo'), -- Operario
    (5, 5, 'inactivo'), -- Mantenimiento
    (6, 5, 'ver'), -- Administración
    (7, 5, 'inactivo'), -- Proveedor
    (8, 5, 'activo');

-- Cliente
-- Para "Generación de Reportes (inventario, ventas)"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 6, 'activo'), -- SysAdmin
    (2, 6, 'activo'), -- Gestor Almacén
    (3, 6, 'activo'), -- Supervisor
    (4, 6, 'inactivo'), -- Operario
    (5, 6, 'inactivo'), -- Mantenimiento
    (6, 6, 'activo'), -- Administración
    (7, 6, 'inactivo'), -- Proveedor
    (8, 6, 'inactivo');

-- Cliente
-- Para "Asignación de Tareas (Picking/Packing)"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 7, 'activo'), -- SysAdmin
    (2, 7, 'activo'), -- Gestor Almacén
    (3, 7, 'activo'), -- Supervisor
    (4, 7, 'inactivo'), -- Operario
    (5, 7, 'inactivo'), -- Mantenimiento
    (6, 7, 'inactivo'), -- Administración
    (7, 7, 'inactivo'), -- Proveedor
    (8, 7, 'inactivo');

-- Cliente
-- Para "Acceso al Historial de Movimientos"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 8, 'activo'), -- SysAdmin
    (2, 8, 'activo'), -- Gestor Almacén
    (3, 8, 'activo'), -- Supervisor
    (4, 8, 'ver'), -- Operario
    (5, 8, 'ver'), -- Mantenimiento
    (6, 8, 'ver'), -- Administración
    (7, 8, 'inactivo'), -- Proveedor
    (8, 8, 'inactivo');

-- Cliente
-- Para "Mantenimiento de Infraestructura"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 9, 'activo'), -- SysAdmin
    (2, 9, 'inactivo'), -- Gestor Almacén
    (3, 9, 'ver'), -- Supervisor
    (4, 9, 'inactivo'), -- Operario
    (5, 9, 'activo'), -- Mantenimiento
    (6, 9, 'inactivo'), -- Administración
    (7, 9, 'inactivo'), -- Proveedor
    (8, 9, 'inactivo');

-- Cliente
-- Para "Gestión Financiera (compras/ventas)"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 10, 'activo'), -- SysAdmin
    (2, 10, 'inactivo'), -- Gestor Almacén
    (3, 10, 'inactivo'), -- Supervisor
    (4, 10, 'inactivo'), -- Operario
    (5, 10, 'inactivo'), -- Mantenimiento
    (6, 10, 'activo'), -- Administración
    (7, 10, 'inactivo'), -- Proveedor
    (8, 10, 'ver');

-- Cliente
-- Para "Gestión de Incidencias"
INSERT INTO
    RolPermiso (id_rol, id_permiso, estado)
VALUES
    (1, 11, 'activo'), -- SysAdmin
    (2, 11, 'inactivo'), -- Gestor Almacén
    (3, 11, 'ver'), -- Supervisor
    (4, 11, 'ver'), -- Operario
    (5, 11, 'ver'), -- Mantenimiento
    (6, 11, 'inactivo'), -- Administración
    (7, 11, 'inactivo'), -- Proveedor
    (8, 11, 'inactivo');

-- Cliente
-- Creación de la tabla Estanterias, que almacena la información de cómo son estas
CREATE TABLE
    Estanterias (
        id_estanteria INT PRIMARY KEY AUTO_INCREMENT,
        num_baldas INT NOT NULL,
        posiciones_por_balda INT NOT NULL,
        posiciones_disponibles INT NOT NULL
    );

--  Este TRIGGER calculará el valor de posiciones_disponibles multiplicando las baldas y las posiciones.
DELIMITER / / CREATE TRIGGER calcular_posiciones_disponibles BEFORE INSERT ON estanterias -- establecemos el valor de posiciones_disponibles antes de que se realice la inserción
FOR EACH ROW -- Se aplica a cada fila que se inserta.
BEGIN
-- Calcula el valor de posiciones_disponibles multiplicando balas y posiciones de la nueva fila.
SET
    NEW.posiciones_disponibles = NEW.num_baldas * NEW.posiciones_por_balda;

END;

/ / DELIMITER;

-- Creación de la tabla Usuarios, que almacena la información de los usuarios del sistema
CREATE TABLE
    Usuarios (
        id_usuario INT PRIMARY KEY AUTO_INCREMENT,
        nombre VARCHAR(100) NOT NULL,
        email VARCHAR(100) NOT NULL UNIQUE,
        contraseña VARCHAR(255) NOT NULL,
        id_rol INT,
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_rol) REFERENCES Roles (id_rol)
    );

-- Creación de la tabla Productos, donde se almacenan los productos del almacén
CREATE TABLE
    Productos (
        id_producto INT PRIMARY KEY AUTO_INCREMENT,
        nombre_producto VARCHAR(100) NOT NULL,
        descripcion TEXT,
        precio DECIMAL(10, 2), -- Precio unitario del producto
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Creación de la tabla Palets, donde se almacenan los palets que contienen productos
CREATE TABLE
    Palets (
        id_palet INT PRIMARY KEY AUTO_INCREMENT,
        id_producto INT, -- Hace referencia al tipo de producto que contiene el palet
        cantidad INT NOT NULL, -- Cantidad de productos que contiene el palet
        -- ubicacion VARCHAR(100) NOT NULL, -- Ubicación del palet dentro del almacén
        estanteria INT NOT NULL, -- estanteria donde se encuentra el palet
        balda INT NOT NULL, -- balda donde se encuetra el palet en la estanteria
        posicion INT NOT NULL, -- poscion de la balda donde se encuentra el palet
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
    );

-- Creación de la tabla Movimientos, que almacena la entrada y salida de palets en el almacén
CREATE TABLE
    Movimientos (
        id_movimiento INT PRIMARY KEY AUTO_INCREMENT,
        id_usuario INT, -- Usuario que realizó el movimiento
        id_palet INT, -- Palet afectado por el movimiento
        tipo_movimiento ENUM ('Entrada', 'Salida') NOT NULL,
        cantidad INT NOT NULL, -- Cantidad de productos movidos en el movimiento
        fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        observaciones TEXT,
        FOREIGN KEY (id_usuario) REFERENCES Usuarios (id_usuario),
        FOREIGN KEY (id_palet) REFERENCES Palets (id_palet)
    );

-- Creación de la tabla Pedidos, donde se almacenan los pedidos realizados
CREATE TABLE
    Pedidos (
        id_pedido INT PRIMARY KEY AUTO_INCREMENT,
        id_usuario INT, -- Usuario que realizó el pedido
        fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        estado ENUM ('Pendiente', 'Completado', 'Cancelado') NOT NULL,
        FOREIGN KEY (id_usuario) REFERENCES Usuarios (id_usuario)
    );

-- Creación de la tabla DetallesPedido, que almacena los productos incluidos en cada pedido
CREATE TABLE
    DetallesPedido (
        id_detalle INT PRIMARY KEY AUTO_INCREMENT,
        id_pedido INT, -- Pedido al que pertenece este detalle
        id_palet INT, -- Palet de productos solicitado en el pedido
        cantidad INT NOT NULL, -- Cantidad de productos solicitados
        FOREIGN KEY (id_pedido) REFERENCES Pedidos (id_pedido),
        FOREIGN KEY (id_palet) REFERENCES Palets (id_palet)
    );

-- -------------------------------------------------------------------------------------------------
-- ------------------------------------ DATOS EJEMPLO ----------------------------------------------
-- -------------------------------------------------------------------------------------------------
SET
    FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE DetallesPedido;

TRUNCATE TABLE Pedidos;

TRUNCATE TABLE Movimientos;

TRUNCATE TABLE Productos;

TRUNCATE TABLE Usuarios;

TRUNCATE TABLE Estanterias;

SET
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE Roles AUTO_INCREMENT = 7;

-- Continúa después del último ID existente
ALTER TABLE Estanterias AUTO_INCREMENT = 1;

ALTER TABLE Usuarios AUTO_INCREMENT = 1;

ALTER TABLE Productos AUTO_INCREMENT = 1;

ALTER TABLE Palets AUTO_INCREMENT = 1;

ALTER TABLE Movimientos AUTO_INCREMENT = 1;

ALTER TABLE Pedidos AUTO_INCREMENT = 1;

ALTER TABLE DetallesPedido AUTO_INCREMENT = 1;

-- Inserción de datos en la tabla Estanterias
INSERT INTO
    Estanterias (num_baldas, posiciones_por_balda)
VALUES
    (8, 24), -- Estanteria 1
    (8, 4), -- Estanteria 2
    (10, 24), -- Estanteria 3
    (6, 10); -- Estanteria 4
    
-- Inserción de datos en la tabla Usuarios
INSERT INTO
    Usuarios (nombre, email, contraseña, id_rol)
VALUES
    (
        'Admin',
        'admin@almacen.com',
        'hashed_password',
        1
    ), -- sysAdmin
    (
        'Juan Perez',
        'juan.perez@almacen.com',
        'hashed_password',
        2
    ), -- Gestor Almacén
    (
        'Maria Lopez',
        'maria.lopez@almacen.com',
        'hashed_password',
        3
    ), -- Supervisor
    (
        'Carlos Diaz',
        'carlos.diaz@almacen.com',
        'hashed_password',
        4
    ), -- Operario
    (
        'Luisa Gomez',
        'luisa.gomez@almacen.com',
        'hashed_password',
        5
    ), -- Mantenimiento
    (
        'Ana Torres',
        'ana.torres@almacen.com',
        'hashed_password',
        6
    );

-- Administración
-- Inserción de datos en la tabla Productos
INSERT INTO
    Productos (nombre_producto, descripcion, precio)
VALUES
    ('Lápices', 'Caja de 50 lápices de madera', 10.50),
    (
        'Cuadernos',
        'Cuadernos tamaño A4 de 100 hojas',
        5.75
    ),
    (
        'Bolígrafos',
        'Paquete de 20 bolígrafos azules',
        7.20
    ),
    (
        'Marcadores',
        'Set de 12 marcadores de colores',
        15.00
    ),
    (
        'Gomas de borrar',
        'Pack de 10 gomas de borrar suaves',
        3.50
    );

-- Inserción de datos en la tabla Palets
INSERT INTO
    Palets (
        id_producto,
        cantidad,
        estanteria,
        balda,
        posicion
    )
VALUES
    (1, 100, 1, 2, 3), -- Lápices: 100 unidades en la estantería 1, balda 2, posición 3
    (1, 50, 2, 4, 2), -- Lápices: 50 unidades en la estantería 2, balda 4, posición 2
    (2, 200, 3, 3, 3), -- Cuadernos: 200 unidades en la estantería 3, balda 3, posición 3
    (3, 150, 1, 4, 1), -- Bolígrafos: 150 unidades en la estantería 1, balda 4, posición 1
    (4, 100, 3, 5, 3), -- Marcadores: 100 unidades en la estantería 3, balda 5, posición 3
    (5, 300, 4, 4, 2);

-- Gomas de borrar: 300 unidades en la estantería 4, balda 4, posición 2
-- Inserción de datos en la tabla Movimientos
INSERT INTO
    Movimientos (
        id_usuario,
        id_palet,
        tipo_movimiento,
        cantidad,
        observaciones
    )
VALUES
    (
        2,
        1,
        'Entrada',
        100,
        'Recepción de lápices en Zona A - Estante 1'
    ),
    (
        3,
        2,
        'Salida',
        50,
        'Envío parcial de lápices a cliente'
    ),
    (
        4,
        3,
        'Entrada',
        200,
        'Recepción de cuadernos en Zona B - Estante 3'
    ),
    (
        5,
        4,
        'Entrada',
        150,
        'Recepción de bolígrafos en Zona C - Estante 1'
    );

-- Inserción de datos en la tabla Pedidos
INSERT INTO
    Pedidos (id_usuario, estado)
VALUES
    (2, 'Pendiente'), -- Pedido creado por el Gestor Almacén
    (3, 'Completado');

-- Pedido supervisado por el Supervisor
-- Inserción de datos en la tabla DetallesPedido
INSERT INTO
    DetallesPedido (id_pedido, id_palet, cantidad)
VALUES
    (1, 1, 20), -- Pedido 1 incluye 20 unidades del primer palet
    (1, 2, 10), -- Pedido 1 incluye 10 unidades del segundo palet
    (2, 3, 50);

-- Pedido 2 incluye 50 unidades del tercer palet