-- Crear la base de datos
CREATE DATABASE gestion_almacenes;

-- Usar la base de datos creada
USE gestion_almacenes;

-- Crear tabla Usuarios
CREATE TABLE Usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    rol ENUM('Admin', 'Operario', 'Cliente') NOT NULL,
    estado ENUM('Activo', 'Inactivo') NOT NULL
);

-- Crear tabla Almacenes
CREATE TABLE Almacenes (
    id_almacen INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(150) NOT NULL,
    capacidad INT NOT NULL
);

-- Crear tabla Productos
CREATE TABLE Productos (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    codigo_barras VARCHAR(50) UNIQUE,
    stock_actual INT NOT NULL,
    stock_minimo INT NOT NULL,
    precio_compra DECIMAL(10, 2) NOT NULL,
    id_proveedor INT,
    id_almacen INT,
    FOREIGN KEY (id_proveedor) REFERENCES Proveedores(id_proveedor),  -- Relación con Proveedores
    FOREIGN KEY (id_almacen) REFERENCES Almacenes(id_almacen)      -- Relación con Almacenes
);

-- Crear tabla Proveedores
CREATE TABLE Proveedores (
    id_proveedor INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(50),
    email VARCHAR(150) UNIQUE
);

-- Crear tabla Clientes
CREATE TABLE Clientes (
    id_cliente INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(150),
    telefono VARCHAR(50),
    email VARCHAR(150) UNIQUE
);

-- Crear tabla Pedidos
CREATE TABLE Pedidos (
    id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    fecha_pedido DATE NOT NULL,
    id_cliente INT,    -- Puede ser nulo si es un pedido de proveedor
    id_proveedor INT,  -- Puede ser nulo si es un pedido de cliente
    id_usuario INT,
    estado ENUM('Pendiente', 'Enviado', 'Completado', 'Cancelado') NOT NULL,
    FOREIGN KEY (id_cliente) REFERENCES Clientes(id_cliente),
    FOREIGN KEY (id_proveedor) REFERENCES Proveedores(id_proveedor),
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario)
);

-- Crear tabla Detalle_Pedidos
CREATE TABLE Detalle_Pedidos (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT,
    id_producto INT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto)
);

-- Crear tabla Movimientos_Stock
CREATE TABLE Movimientos_Stock (
    id_movimiento INT PRIMARY KEY AUTO_INCREMENT,
    id_producto INT,
    cantidad INT NOT NULL,
    tipo_movimiento ENUM('Entrada', 'Salida') NOT NULL,
    fecha_movimiento DATETIME NOT NULL,
    id_almacen INT,
    id_usuario INT,
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto),
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_almacen) REFERENCES Almacenes(id_almacen)
);
