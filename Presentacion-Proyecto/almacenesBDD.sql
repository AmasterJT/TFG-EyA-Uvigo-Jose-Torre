-- Creación de la tabla Roles, donde se definen los distintos roles del sistema
CREATE TABLE Roles (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre_rol VARCHAR(50) NOT NULL,
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

-- Creación de la tabla Usuarios, que almacena la información de los usuarios del sistema
CREATE TABLE Usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    contraseña VARCHAR(255) NOT NULL,
    id_rol INT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Roles(id_rol)
);

-- Creación de la tabla Productos, donde se almacenan los productos del almacén
CREATE TABLE Productos (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2),  -- Precio unitario del producto
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Creación de la tabla Palets, donde se almacenan los palets que contienen productos
CREATE TABLE Palets (
    id_palet INT PRIMARY KEY AUTO_INCREMENT,
    id_producto INT,  -- Hace referencia al tipo de producto que contiene el palet
    cantidad INT NOT NULL,  -- Cantidad de productos que contiene el palet
    ubicacion VARCHAR(100) NOT NULL,  -- Ubicación del palet dentro del almacén
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_producto) REFERENCES Productos(id_producto)
);

-- Creación de la tabla Movimientos, que almacena la entrada y salida de palets en el almacén
CREATE TABLE Movimientos (
    id_movimiento INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,  -- Usuario que realizó el movimiento
    id_palet INT,  -- Palet afectado por el movimiento
    tipo_movimiento ENUM('Entrada', 'Salida') NOT NULL,
    cantidad INT NOT NULL,  -- Cantidad de productos movidos en el movimiento
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario),
    FOREIGN KEY (id_palet) REFERENCES Palets(id_palet)
);

-- Creación de la tabla Pedidos, donde se almacenan los pedidos realizados
CREATE TABLE Pedidos (
    id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT,  -- Usuario que realizó el pedido
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('Pendiente', 'Completado', 'Cancelado') NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario)
);

-- Creación de la tabla DetallesPedido, que almacena los productos incluidos en cada pedido
CREATE TABLE DetallesPedido (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT,  -- Pedido al que pertenece este detalle
    id_palet INT,  -- Palet de productos solicitado en el pedido
    cantidad INT NOT NULL,  -- Cantidad de productos solicitados
    FOREIGN KEY (id_pedido) REFERENCES Pedidos(id_pedido),
    FOREIGN KEY (id_palet) REFERENCES Palets(id_palet)
);

-- Ejemplo de inserción de un usuario (SysAdmin) en la tabla Usuarios
INSERT INTO Usuarios (nombre, email, contraseña, id_rol) VALUES
('Admin', 'admin@almacen.com', 'hashed_password', 1);  -- sysAdmin
