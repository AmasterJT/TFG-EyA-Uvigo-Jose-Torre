-- Si queremos volver a crear toda la base de datos desde cero primero la borramos ejecutando
 DROP DATABASE IF EXISTS tfg_almacenDB;

-- Creación de la base de datos
create database  tfg_almacenDB;

use tfg_almacenDB;


-- =======================================
-- TABLA DE PROVEEDORES
-- =======================================
CREATE TABLE proveedores (
    id_proveedor INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    nif_cif VARCHAR(20) UNIQUE,   -- Identificación fiscal
    contacto VARCHAR(100),        -- Persona de contacto
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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


-- Creación de la tabla usuarios
CREATE TABLE
usuarios (
	id_usuario INT PRIMARY KEY AUTO_INCREMENT,
	user_name VARCHAR(100) NOT NULL,
	nombre VARCHAR(100) NOT NULL,
	apellido VARCHAR(100) NOT NULL,
	email VARCHAR(100) NOT NULL UNIQUE,
	contraseña VARCHAR(255) NOT NULL,
	id_rol INT,
	activo INT,
	fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (id_rol) REFERENCES roles (id_rol)
);

-- Creación de la tabla clientes
CREATE TABLE clientes (
	id_cliente INT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(100),
	direccion VARCHAR(200),
	telefono VARCHAR(20),
	email VARCHAR(100) UNIQUE,
	fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Creación de la tabla tipos
CREATE TABLE tipos (
    id_tipo VARCHAR(50) PRIMARY KEY,
    color VARCHAR(20) NOT NULL
);


CREATE TABLE productos (
    id_producto INT PRIMARY KEY AUTO_INCREMENT,
    identificador_producto VARCHAR(100) NOT NULL UNIQUE,
    tipo_producto VARCHAR(50) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tipo_producto) REFERENCES tipos(id_tipo)
);

CREATE TABLE proveedor_producto (
    id_proveedor INT NOT NULL,                      -- FK -> proveedores.id_proveedor
    id_producto  INT NOT NULL,                      -- FK -> productos.id_producto

    precio DECIMAL(10,2) NULL,                      -- Precio acordado con ese proveedor (opcional)
    unidades_por_palet_default INT NOT NULL DEFAULT 1,      -- Nº unidades del producto por palet para ese proveedor

    PRIMARY KEY (id_proveedor, id_producto),

    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
        ON UPDATE CASCADE ON DELETE CASCADE,

    CONSTRAINT chk_unidades_por_palet CHECK (unidades_por_palet_default > 0)
);



-- Creación de la tabla palets
CREATE TABLE palets (
    id_palet INT PRIMARY KEY AUTO_INCREMENT,
    identificador VARCHAR(50) NOT NULL UNIQUE,
    id_producto VARCHAR(100) NOT NULL,
    alto INT NOT NULL,
    ancho INT NOT NULL,
    largo INT NOT NULL,
    cantidad_de_producto INT NOT NULL,
    estanteria INT NOT NULL,
    balda INT NOT NULL,
    posicion INT NOT NULL,
    delante BOOLEAN NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_producto) REFERENCES productos(identificador_producto)
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

CREATE TABLE pedidos (
    id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    codigo_referencia VARCHAR(50) UNIQUE,
    id_usuario INT NULL,
    id_cliente INT NOT NULL,
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_entrega DATE NOT NULL,
    estado ENUM (
        'Pendiente',
        'Completado',
        'En proceso',
        'Cancelado'
    ) NOT NULL,
    hora_salida ENUM (
        'primera_hora',
        'segunda_hora'
    ),
    FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario)
);



CREATE TABLE detalles_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    estado_producto_pedido BOOLEAN,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
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

    SET nuevo_codigo = CONCAT(
        'PED-',
        DATE_FORMAT(NEW.fecha_pedido, '%Y%m%d'),
        '-',
        secuencia_hex
    );

    SET NEW.codigo_referencia = nuevo_codigo;
END;
//
DELIMITER ;


DELIMITER //
CREATE TRIGGER calcular_posiciones_disponibles BEFORE INSERT ON estanterias FOR EACH ROW BEGIN
SET
    NEW.posiciones_disponibles = NEW.num_baldas * NEW.posiciones_por_balda;

END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER generar_user_name
BEFORE INSERT ON usuarios
FOR EACH ROW
BEGIN
    IF NEW.user_name IS NULL OR NEW.user_name = '' THEN
        SET NEW.user_name = LOWER(CONCAT(
            LEFT(NEW.nombre, 1),
            NEW.apellido
        ));
    END IF;
END;
//
DELIMITER ;









/* ===========================
   CABECERA: ORDEN DE COMPRA
   =========================== */
CREATE TABLE orden_compra (
    id_oc INT PRIMARY KEY AUTO_INCREMENT,
    codigo_referencia VARCHAR(50) UNIQUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT NULL
);

-- Generar código OC-YYYYMMDD-XXXXXX (hex consecutivo por día)
DELIMITER //
CREATE TRIGGER generar_codigo_orden_compra
BEFORE INSERT ON orden_compra
FOR EACH ROW
BEGIN
    DECLARE secuencia_diaria INT;
    DECLARE secuencia_hex VARCHAR(10);
    IF NEW.fecha_creacion IS NULL THEN
        SET NEW.fecha_creacion = CURRENT_TIMESTAMP;
    END IF;

    SELECT COUNT(*) + 1 INTO secuencia_diaria
    FROM orden_compra
    WHERE DATE(fecha_creacion) = DATE(NEW.fecha_creacion);

    SET secuencia_hex = LPAD(HEX(secuencia_diaria), 6, '0');
    SET NEW.codigo_referencia = CONCAT('OC-', DATE_FORMAT(NEW.fecha_creacion, '%Y%m%d'), '-', secuencia_hex);
END;
//
DELIMITER ;

CREATE INDEX idx_oc_fecha ON orden_compra (fecha_creacion);
CREATE INDEX idx_oc_codigo ON orden_compra (codigo_referencia);


/* =====================================
   DETALLE: LÍNEAS DE LA ORDEN DE COMPRA
   - Cada fila representa UN palet planificado
   - Se liga por id_oc a la cabecera
   - Se especifica proveedor de esa línea
   - Se especifica producto, cantidad y ubicación (tentativa)
   ===================================== */
CREATE TABLE detalle_orden_compra (
    id_detalle_oc INT PRIMARY KEY AUTO_INCREMENT,
    id_oc INT NOT NULL,                      -- FK a cabecera
    id_proveedor INT NOT NULL,               -- proveedor de esta línea
    id_producto INT NOT NULL,                -- producto a pedir (productos.id_producto)
    cantidad INT NOT NULL CHECK (cantidad > 0),

    -- Ubicación tentativa (pueden dejarse NULL si no se conoce aún)
    estanteria INT NULL,
    balda INT NULL,
    posicion INT NULL,
    delante BOOLEAN NULL,

    -- Integridad referencial
    FOREIGN KEY (id_oc) REFERENCES orden_compra(id_oc),
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto),

    -- Garantiza que el proveedor realmente suministra ese producto
    FOREIGN KEY (id_proveedor, id_producto) REFERENCES proveedor_producto(id_proveedor, id_producto)
);

CREATE INDEX idx_doc_oc       ON detalle_orden_compra (id_oc);
CREATE INDEX idx_doc_prov     ON detalle_orden_compra (id_proveedor);
CREATE INDEX idx_doc_prod     ON detalle_orden_compra (id_producto);



DELIMITER //
CREATE PROCEDURE crear_orden_compra(
  IN  p_observaciones TEXT,
  OUT p_id_oc INT,
  OUT p_codigo_referencia VARCHAR(50)
)
BEGIN
  INSERT INTO orden_compra (observaciones) VALUES (p_observaciones);
  SET p_id_oc = LAST_INSERT_ID();

  SELECT codigo_referencia
    INTO p_codigo_referencia
  FROM orden_compra
  WHERE id_oc = p_id_oc;
END;
//
DELIMITER ;
