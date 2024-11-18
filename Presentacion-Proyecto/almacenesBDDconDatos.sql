-- Creación de la tabla Roles

CREATE TABLE Roles (

    id_rol SERIAL PRIMARY KEY,

    nombre_rol TEXT NOT NULL,

    descripcion TEXT

);


-- Inserción de algunos roles por defecto

INSERT INTO Roles (nombre_rol, descripcion) VALUES

('sysAdmin', 'Superusuario con control total del sistema'),

('Gestor Almacén', 'Responsable de la operativa del almacén'),

('Supervisor', 'Encargado de supervisar las operaciones del almacén'),

('Operario', 'Trabajador que realiza tareas diarias en el almacén'),

('Mantenimiento', 'Encargado del mantenimiento de maquinaria e instalaciones'),

('Administración', 'Encargado de tareas administrativas del almacén');


-- Creación de la tabla Usuarios

CREATE TABLE Usuarios (

    id_usuario SERIAL PRIMARY KEY,

    nombre TEXT NOT NULL,

    email TEXT NOT NULL UNIQUE,

    contraseña TEXT NOT NULL,

    id_rol INT,

    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_rol) REFERENCES Roles(id_rol)

);


-- Inserción de usuarios de ejemplo

INSERT INTO Usuarios (nombre, email, contraseña, id_rol) VALUES

('Admin', 'admin@almacen.com', 'hashed_password', 1),

('Juan Pérez', 'juan.perez@almacen.com', 'hashed_password', 2),

('María López', 'maria.lopez@almacen.com', 'hashed_password', 3),

('Carlos Ruiz', 'carlos.ruiz@almacen.com', 'hashed_password', 4),

('Ana Gómez', 'ana.gomez@almacen.com', 'hashed_password', 5);


-- Creación de la tabla Productos

CREATE TABLE Productos (

    id_producto SERIAL PRIMARY KEY,

    nombre_producto TEXT NOT NULL,

    descripcion TEXT,

    precio DECIMAL(10, 2),

    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);


-- Inserción de productos de ejemplo

INSERT INTO Productos (nombre_producto, descripcion, precio) VALUES

('Producto A', 'Descripción del Producto A', 10.50),

('Producto B', 'Descripción del Producto B', 20.00),

('Producto C', 'Descripción del Producto C', 15.75);


-- Creación de la tabla Palets

CREATE TABLE Palets (

    id_palet SERIAL PRIMARY KEY,

    id_producto INT,

    cantidad INT NOT NULL,

    ubicacion TEXT NOT NULL,

    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto)

);


-- Inserción de palets de ejemplo

INSERT INTO Palets (id_producto, cantidad, ubicacion) VALUES

(1, 100, 'A1'),

(2, 200, 'B2'),

(3, 150, 'C3');


-- Creación de la tabla Movimientos

CREATE TABLE Movimientos (

    id_movimiento SERIAL PRIMARY KEY,

    id_usuario INT,

    id_palet INT,

    tipo_movimiento TEXT CHECK (tipo_movimiento IN ('Entrada', 'Salida')) NOT NULL,

    cantidad INT NOT NULL,

    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    observaciones TEXT,

    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),

    FOREIGN KEY (id_palet) REFERENCES Palets(id_palet)

);


-- Inserción de movimientos de ejemplo

INSERT INTO Movimientos (id_usuario, id_palet, tipo_movimiento, cantidad, observaciones) VALUES

(1, 1, 'Entrada', 50, 'Reabastecimiento'),

(2, 2, 'Salida', 30, 'Pedido cliente'),

(3, 3, 'Entrada', 20, 'Devolución');


-- Creación de la tabla Pedidos

CREATE TABLE Pedidos (

    id_pedido SERIAL PRIMARY KEY,

    id_usuario INT,

    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    estado TEXT CHECK (estado IN ('Pendiente', 'Completado', 'Cancelado')) NOT NULL,

    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario)

);


-- Inserción de pedidos de ejemplo

INSERT INTO Pedidos (id_usuario, estado) VALUES

(1, 'Pendiente'),

(2, 'Completado'),

(3, 'Cancelado');


-- Creación de la tabla DetallesPedido

CREATE TABLE DetallesPedido (

    id_detalle SERIAL PRIMARY KEY,

    id_pedido INT,

    id_palet INT,

    cantidad INT NOT NULL,

    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido),

    FOREIGN KEY (id_palet) REFERENCES Palets(id_palet)

);


-- Inserción de detalles de pedido de ejemplo

INSERT INTO DetallesPedido (id_pedido, id_palet, cantidad) VALUES

(1, 1, 10),

(2, 2, 20),

(3, 3, 15);