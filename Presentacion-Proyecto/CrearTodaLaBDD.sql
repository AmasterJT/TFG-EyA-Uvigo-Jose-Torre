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
    id_proveedor INT NOT NULL,
    id_producto INT NOT NULL,
    precio DECIMAL(10,2) NULL,   -- opcional: precio acordado con ese proveedor
    PRIMARY KEY (id_proveedor, id_producto),
    FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
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









-- Insertar algunos proveedores de ejemplo
INSERT INTO proveedores (nombre, direccion, telefono, email, nif_cif, contacto)
VALUES
('Distribuciones ACME S.L.', 'Calle Mayor 45, Madrid', '910000111', 'contacto@acme.com', 'B12345678', 'Laura Gómez'),
('Logística Central S.A.', 'Polígono Industrial Sur, Valencia', '963000222', 'info@logisticacentral.es', 'A87654321', 'Mario Ruiz'),
('Frutas del Campo', 'Camino Viejo 12, Sevilla', '955001122', 'ventas@frutascampo.es', 'C44556677', 'Ana Torres');





-- Insertamos usuarios para tener todos los roles (con activo)
INSERT INTO usuarios (user_name, nombre, apellido, email, contraseña, id_rol, activo) VALUES
('AdLo', 'Admin', 'Lopez', 'admin@almacen.com', 'admin', 1, 1), -- SysAdmin (dentro)
('JuPe', 'Juan', 'Perez', 'juan.perez@almacen.com', 'juan123', 2, 1), -- Gestor Almacén (dentro)
('MaLo', 'Maria', 'Lopez', 'maria.lopez@almacen.com', 'maria123', 3, 1), -- Supervisor (dentro)
('CaDi', 'Carlos', 'Diaz', 'carlos.diaz@almacen.com', 'carlos123', 4, 1), -- Operario (dentro)
('LuGo', 'Luisa', 'Gomez', 'luisa.gomez@almacen.com', 'luisa123', 5, 0), -- Mantenimiento (fuera)
('AnTo', 'Ana', 'Torres', 'ana.torres@almacen.com', 'ana123', 6, 0);   -- Administración (fuera)

-- Insertamos operarios (el trigger sigue generando user_name); añadimos activo al final
INSERT INTO usuarios (nombre, apellido, email, contraseña, id_rol, activo) VALUES
('Luis',   'Fernandez', 'luis.fernandez@almacen.com',   'luis123',   4, 1),
('Sofia',  'Martinez',  'sofia.martinez@almacen.com',   'sofia123',  4, 0),
('Javier', 'Romero',    'javier.romero@almacen.com',    'javier123', 4, 1),
('Claudia','Hernandez', 'claudia.hernandez@almacen.com','claudia123',4, 0),
('Pablo',  'Ruiz',      'pablo.ruiz@almacen.com',       'pablo123',  4, 1),
('Andrea', 'Castro',    'andrea.castro@almacen.com',    'andrea123', 4, 0),
('Diego',  'Navarro',   'diego.navarro@almacen.com',    'diego123',  4, 1),
('Valeria','Ortega',    'valeria.ortega@almacen.com',   'valeria123',4, 0),
('Miguel', 'Ramos',     'miguel.ramos@almacen.com',     'miguel123', 4, 1),
('Isabel', 'Vargas',    'isabel.vargas@almacen.com',    'isabel123', 4, 0);


INSERT INTO tipos (id_tipo, color) VALUES ('Tensoactivo', '0.8,0,0');
INSERT INTO tipos (id_tipo, color) VALUES ('Secuestrante', '0,0.8,0');
INSERT INTO tipos (id_tipo, color) VALUES ('Rindente', '0,0,0.8');
INSERT INTO tipos (id_tipo, color) VALUES ('Remojo', '0.8,0.8,0');
INSERT INTO tipos (id_tipo, color) VALUES ('Engrase', '0.8,0,0.8');
INSERT INTO tipos (id_tipo, color) VALUES ('Desencalante', '0,0.8,0.8');
INSERT INTO tipos (id_tipo, color) VALUES ('Conservante', '0.5,0,0');
INSERT INTO tipos (id_tipo, color) VALUES ('Calero', '0,0.5,0');
INSERT INTO tipos (id_tipo, color) VALUES ('Curtición', '0,0,0.5');
INSERT INTO tipos (id_tipo, color) VALUES ('Deslizante', '0.8,0.5,0');
INSERT INTO tipos (id_tipo, color) VALUES ('Hidrofugante', '0,0.8,0.5');
INSERT INTO tipos (id_tipo, color) VALUES ('Resina', '0.8,0,0.5');
INSERT INTO tipos (id_tipo, color) VALUES ('Cera', '0.5,0.5,0');



INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSEPT FP', 'Conservante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('SOLVOTAN XS', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPONAT NF', 'Calero');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA HF', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA HS', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA HP', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSEPT BA', 'Conservante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA PW', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSYL TBK-E', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA PV', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON AP', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCAL AF', 'Desencalante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSYL TBA', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSOL WBF', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSYL TBD', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON BMF', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL K-U-10', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA DP', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON CST', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON COL', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSIST S-BO', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSYL TBK', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('PASTOSOL KC', 'Secuestrante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('ANTIFOAM NSP', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOWET SA', 'Remojo');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA CA', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA SP', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('PASTOSOL HW', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOTAN MON', 'Curtición');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('PASTOSOL DG', 'Tensoactivo');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL AB', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOZYM AX', 'Rindente');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A-10', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA 3A', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCAL DE', 'Desencalante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUKALIN K', 'Secuestrante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOZYM AB', 'Rindente');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A-18', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('SOLVOTAN TACTO', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPONAT LA', 'Calero');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOTEC S', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA E', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA P', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON DBS', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA FP', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA M', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA K-A', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA BT', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSYL ABS', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A-20', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA KT 09', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON DB-80', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA KT 08', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON CM', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA W', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOTEC T', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A-28', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL K-U-310', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOTAN MOW', 'Curtición');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL K-A-30', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('PASTOSOL F', 'Tensoactivo');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('PASTOSOL MD', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOZYM CL', 'Rindente');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSLIP P', 'Deslizante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOZYM CN', 'Rindente');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOL RK', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('PASTOSOL BCN', 'Tensoactivo');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A–90', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSYL HBD', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOTAN OM', 'Curtición');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOSIST D', 'Hidrofugante');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A-30', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL K-U-27', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A-32', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA D-80', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOWET PH', 'Remojo');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPON PEM', 'Engrase');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOZYM CB', 'Rindente');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL A-35', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOFIN CERA A', 'Cera');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOCRYL K-U-327', 'Resina');
INSERT INTO productos (identificador_producto, tipo_producto) VALUES ('TRUPOZYM CH', 'Rindente');














-- ============================================
-- PROVEEDOR: Distribuciones ACME S.L.
-- (Conservante, Cera, Resina, Calero, Curtición)
-- ============================================
INSERT INTO proveedor_producto (id_proveedor, id_producto)
SELECT
  (SELECT id_proveedor FROM proveedores WHERE nombre = 'Distribuciones ACME S.L.') AS id_proveedor,
  p.id_producto
FROM productos p
WHERE p.identificador_producto IN (
  'TRUPOSEPT FP',
  'TRUPOSEPT BA',
  'TRUPONAT NF',
  'TRUPOFIN CERA HF',
  'TRUPOFIN CERA HS',
  'TRUPOFIN CERA HP',
  'TRUPOFIN CERA PW',
  'TRUPOFIN CERA PV',
  'TRUPOCRYL K-U-10',
  'TRUPOFIN CERA DP',
  'TRUPOCRYL AB',
  'TRUPOCRYL A-10',
  'TRUPOFIN CERA 3A',
  'TRUPOCRYL A-18',
  'TRUPONAT LA',
  'TRUPOFIN CERA E',
  'TRUPOFIN CERA P',
  'TRUPOFIN CERA FP',
  'TRUPOFIN CERA M',
  'TRUPOFIN CERA K-A',
  'TRUPOFIN CERA BT',
  'TRUPOCRYL A-20',
  'TRUPOFIN CERA KT 09',
  'TRUPOFIN CERA KT 08',
  'TRUPOFIN CERA W',
  'TRUPOCRYL A-28',
  'TRUPOCRYL K-U-310',
  'TRUPOTAN MOW',
  'TRUPOCRYL K-A-30',
  'TRUPOCRYL A–90',     -- ojo: guion en-dash en tu texto original
  'TRUPOTAN OM',
  'TRUPOCRYL A-30',
  'TRUPOCRYL K-U-27',
  'TRUPOCRYL A-32',
  'TRUPOFIN CERA D-80',
  'TRUPOCRYL A-35',
  'TRUPOFIN CERA A',
  'TRUPOCRYL K-U-327',
  'TRUPOTAN MON'
);

-- ============================================
-- PROVEEDOR: Logística Central S.A.
-- (Engrase, Hidrofugante, Desencalante, Remojo)
-- ============================================
INSERT INTO proveedor_producto (id_proveedor, id_producto)
SELECT
  (SELECT id_proveedor FROM proveedores WHERE nombre = 'Logística Central S.A.') AS id_proveedor,
  p.id_producto
FROM productos p
WHERE p.identificador_producto IN (
  'SOLVOTAN XS',
  'TRUPOSYL TBK-E',
  'TRUPON AP',
  'TRUPOCAL AF',
  'TRUPOSYL TBA',
  'TRUPOSOL WBF',
  'TRUPOSYL TBD',
  'TRUPON BMF',
  'TRUPON CST',
  'TRUPON COL',
  'TRUPOSIST S-BO',
  'TRUPOSYL TBK',
  'ANTIFOAM NSP',
  'TRUPOWET SA',
  'PASTOSOL HW',
  'TRUPOCAL DE',
  'SOLVOTAN TACTO',
  'TRUPOTEC S',
  'TRUPON DBS',
  'TRUPOSYL ABS',
  'TRUPON DB-80',
  'TRUPON CM',
  'TRUPOTEC T',
  'PASTOSOL MD',
  'TRUPOL RK',
  'TRUPOSYL HBD',
  'TRUPOSIST D',
  'TRUPOWET PH',
  'TRUPON PEM'
);

-- ============================================
-- PROVEEDOR: Frutas del Campo
-- (Rindente, Secuestrante, Tensoactivo, Deslizante)
-- ============================================
INSERT INTO proveedor_producto (id_proveedor, id_producto)
SELECT
  (SELECT id_proveedor FROM proveedores WHERE nombre = 'Frutas del Campo') AS id_proveedor,
  p.id_producto
FROM productos p
WHERE p.identificador_producto IN (
  'PASTOSOL KC',
  'PASTOSOL DG',
  'TRUPOZYM AX',
  'TRUKALIN K',
  'TRUPOZYM AB',
  'PASTOSOL F',
  'TRUPOZYM CL',
  'TRUPOSLIP P',
  'TRUPOZYM CN',
  'PASTOSOL BCN',
  'TRUPOZYM CB',
  'TRUPOZYM CH'
);























INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59002', 'TRUPOSYL TBK-E', 884, 896, 740, 515, 1, 1, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14648', 'TRUPOFIN CERA HS', 1358, 723, 638, 854, 1, 1, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33688', 'TRUPOFIN CERA KT 09', 1197, 945, 989, 886, 1, 1, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26431', 'PASTOSOL DG', 772, 848, 869, 811, 1, 1, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('10742', 'TRUPOCRYL A-32', 616, 743, 977, 503, 1, 1, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34744', 'TRUPON DB-80', 1024, 689, 890, 332, 1, 1, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('727', 'TRUPON CST', 1103, 520, 645, 603, 1, 1, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1301', 'TRUPOTEC S', 595, 572, 578, 849, 1, 1, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96006', 'TRUPOCRYL K-U-310', 536, 624, 603, 591, 1, 1, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65991', 'TRUPOFIN CERA CA', 1321, 590, 639, 497, 1, 1, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('30174', 'TRUPON COL', 646, 901, 910, 861, 1, 1, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90483', 'TRUPOSYL TBD', 619, 645, 829, 685, 1, 1, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9472', 'PASTOSOL KC', 912, 900, 668, 860, 1, 1, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96755', 'TRUPOCRYL K-U-27', 747, 789, 637, 907, 1, 1, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12651', 'TRUPOSIST D', 853, 771, 673, 737, 1, 1, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36026', 'TRUPOSYL TBA', 787, 966, 556, 744, 1, 1, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46790', 'TRUPOFIN CERA BT', 1361, 768, 515, 917, 1, 1, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('80695', 'TRUPOCRYL K-U-27', 1493, 645, 821, 790, 1, 1, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84868', 'TRUPON CM', 674, 770, 592, 400, 1, 1, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67147', 'TRUPOFIN CERA P', 1131, 850, 852, 817, 1, 1, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67371', 'TRUPOZYM AB', 1037, 705, 704, 363, 1, 1, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33932', 'PASTOSOL BCN', 1452, 826, 846, 814, 1, 1, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32519', 'PASTOSOL F', 812, 526, 901, 370, 1, 1, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34632', 'TRUPOTEC T', 1175, 951, 697, 849, 1, 1, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86048', 'TRUPOCRYL A-28', 791, 918, 700, 499, 1, 1, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62288', 'TRUPOTEC T', 591, 546, 643, 470, 1, 1, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36920', 'TRUPOSIST D', 736, 750, 977, 393, 1, 1, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36067', 'TRUPON CST', 1339, 968, 592, 308, 1, 2, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('6877', 'TRUPOSYL ABS', 1112, 852, 749, 857, 1, 2, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62819', 'PASTOSOL F', 874, 695, 610, 919, 1, 2, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91658', 'TRUPOSOL WBF', 694, 879, 684, 316, 1, 2, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34334', 'TRUPOSIST S-BO', 733, 982, 897, 780, 1, 2, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87383', 'TRUPOCRYL K-U-310', 712, 535, 734, 757, 1, 2, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59400', 'TRUPOFIN CERA HS', 526, 868, 914, 709, 1, 2, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('617', 'TRUPOL RK', 914, 645, 537, 777, 1, 2, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53142', 'PASTOSOL MD', 816, 864, 546, 879, 1, 2, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34051', 'TRUPON CM', 1081, 909, 804, 903, 1, 2, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66859', 'PASTOSOL HW', 854, 976, 864, 367, 1, 2, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5224', 'TRUPON PEM', 1088, 855, 898, 351, 1, 2, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43201', 'TRUPOFIN CERA HP', 508, 547, 982, 755, 1, 2, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90418', 'TRUPOSYL TBK', 1491, 923, 948, 838, 1, 2, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37116', 'TRUPOTAN OM', 1424, 538, 869, 945, 1, 2, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60546', 'TRUPOSYL TBA', 1059, 768, 588, 562, 1, 2, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26672', 'TRUPOCRYL K-A-30', 845, 735, 740, 869, 1, 2, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('30512', 'ANTIFOAM NSP', 1100, 527, 795, 941, 1, 2, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51209', 'TRUPOFIN CERA CA', 1331, 888, 538, 640, 1, 2, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12685', 'TRUPOFIN CERA KT 09', 980, 532, 517, 647, 1, 2, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22306', 'TRUPOSEPT FP', 919, 743, 990, 904, 1, 2, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67047', 'TRUPOCAL AF', 1132, 927, 600, 706, 1, 2, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95072', 'TRUPOTEC T', 1449, 809, 713, 552, 1, 2, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56107', 'TRUKALIN K', 785, 857, 952, 843, 1, 2, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12', 'TRUPOFIN CERA CA', 1208, 800, 844, 635, 1, 2, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28622', 'PASTOSOL F', 1283, 972, 805, 793, 1, 2, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('42719', 'TRUPOSOL WBF', 880, 673, 909, 668, 1, 2, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98944', 'TRUPOCRYL A–90', 1227, 939, 990, 966, 1, 2, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('89653', 'TRUPOZYM CL', 1107, 966, 604, 901, 1, 2, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90346', 'TRUPON AP', 760, 942, 917, 938, 1, 2, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19666', 'PASTOSOL F', 1198, 788, 571, 624, 1, 3, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5463', 'TRUPOZYM CH', 1360, 893, 811, 860, 1, 3, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('10904', 'TRUPOSYL TBD', 1339, 596, 785, 575, 1, 3, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19309', 'TRUPOSIST S-BO', 1016, 853, 857, 932, 1, 3, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33035', 'TRUPOFIN CERA W', 1036, 741, 925, 802, 1, 3, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52475', 'PASTOSOL KC', 1295, 829, 607, 868, 1, 3, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('70365', 'SOLVOTAN TACTO', 880, 697, 816, 808, 1, 3, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66281', 'TRUPOZYM AB', 1429, 658, 785, 418, 1, 3, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96693', 'TRUPOFIN CERA KT 09', 644, 534, 921, 498, 1, 3, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41752', 'TRUPOTAN OM', 555, 864, 751, 586, 1, 3, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92341', 'TRUPOSYL TBK-E', 1224, 794, 864, 407, 1, 3, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25991', 'TRUPOZYM CN', 1418, 523, 701, 884, 1, 3, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15463', 'TRUPON CM', 1050, 924, 672, 580, 1, 3, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84029', 'TRUPOSYL ABS', 1030, 580, 988, 830, 1, 3, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9180', 'TRUPOSYL HBD', 1399, 510, 831, 939, 1, 3, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90212', 'TRUPOFIN CERA CA', 1345, 852, 509, 1000, 1, 3, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11', 'TRUPOFIN CERA KT 09', 552, 762, 704, 305, 1, 3, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28189', 'TRUPOSLIP P', 641, 779, 709, 942, 1, 3, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65407', 'TRUPOZYM AX', 977, 602, 847, 790, 1, 3, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36098', 'TRUPOTAN OM', 705, 873, 511, 433, 1, 3, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('35856', 'SOLVOTAN TACTO', 551, 583, 536, 784, 1, 3, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15277', 'TRUPOSYL TBK-E', 976, 565, 745, 638, 1, 3, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('85536', 'TRUPOFIN CERA DP', 1191, 724, 692, 597, 1, 3, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('42687', 'TRUPOSOL WBF', 1268, 837, 885, 813, 1, 3, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13327', 'TRUPOZYM CB', 1409, 711, 671, 656, 1, 3, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37014', 'PASTOSOL F', 1427, 722, 517, 886, 1, 3, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86718', 'TRUPOFIN CERA CA', 1411, 929, 985, 588, 1, 3, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46', 'TRUPOZYM AB', 683, 600, 902, 949, 1, 3, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18588', 'TRUPON AP', 1003, 968, 552, 758, 1, 3, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66772', 'TRUPOFIN CERA HF', 544, 554, 759, 893, 1, 4, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('284', 'TRUPOZYM AB', 1342, 898, 693, 699, 1, 4, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('55504', 'TRUPOSYL TBK', 754, 839, 901, 560, 1, 4, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84028', 'PASTOSOL BCN', 874, 789, 842, 321, 1, 4, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4500', 'TRUPOSLIP P', 919, 914, 949, 613, 1, 4, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90904', 'TRUPOFIN CERA M', 983, 963, 824, 947, 1, 4, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83844', 'TRUPON DBS', 511, 561, 778, 738, 1, 4, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94785', 'TRUPOCRYL K-A-30', 1406, 588, 727, 663, 1, 4, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('17920', 'TRUPOFIN CERA D-80', 791, 554, 716, 318, 1, 4, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7407', 'TRUPOCRYL A-35', 835, 639, 829, 745, 1, 4, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65531', 'TRUPOSOL WBF', 1252, 901, 976, 827, 1, 4, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65144', 'TRUPOFIN CERA M', 788, 962, 687, 654, 1, 4, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26845', 'TRUPOFIN CERA A', 509, 972, 964, 710, 1, 4, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92799', 'TRUPOCRYL K-A-30', 735, 902, 901, 328, 1, 4, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('70683', 'TRUPOCAL DE', 584, 918, 598, 476, 1, 4, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56467', 'TRUPON COL', 1175, 536, 878, 495, 1, 4, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53241', 'TRUPOZYM CN', 1418, 583, 889, 939, 1, 4, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90352', 'TRUPON BMF', 1021, 890, 683, 545, 1, 4, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('47716', 'TRUPOCRYL A-28', 1132, 785, 611, 933, 1, 4, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56033', 'TRUPOFIN CERA HP', 898, 912, 824, 663, 1, 4, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('39291', 'TRUPOTAN MON', 763, 630, 623, 635, 1, 4, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62640', 'TRUPOFIN CERA W', 850, 958, 927, 486, 1, 4, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('85080', 'TRUPOFIN CERA HP', 1275, 739, 835, 845, 1, 4, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78448', 'TRUPOSYL ABS', 651, 969, 830, 700, 1, 4, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86271', 'TRUPOFIN CERA SP', 995, 514, 566, 871, 1, 4, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('20422', 'TRUPOFIN CERA BT', 1075, 591, 691, 348, 1, 4, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51552', 'TRUPOFIN CERA PV', 1011, 792, 709, 588, 1, 4, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13412', 'PASTOSOL DG', 1159, 844, 665, 458, 1, 5, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('50540', 'TRUPOCRYL A-28', 689, 830, 793, 751, 1, 5, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92641', 'TRUPON BMF', 1205, 630, 603, 326, 1, 5, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('88155', 'TRUPOSLIP P', 1266, 925, 714, 841, 1, 5, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('61163', 'TRUPOFIN CERA P', 798, 946, 947, 688, 1, 5, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72578', 'TRUPOSOL WBF', 943, 889, 520, 487, 1, 5, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5719', 'TRUPON DBS', 842, 524, 574, 469, 1, 5, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('3905', 'TRUPOZYM AB', 1155, 556, 637, 937, 1, 5, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99098', 'TRUPOFIN CERA K-A', 537, 556, 951, 857, 1, 5, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98717', 'TRUPOCAL DE', 850, 826, 512, 433, 1, 5, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5948', 'TRUPOSIST S-BO', 1369, 662, 606, 811, 1, 5, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('44787', 'TRUPOSLIP P', 503, 625, 647, 837, 1, 5, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22189', 'TRUPOSOL WBF', 602, 540, 778, 475, 1, 5, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66223', 'TRUPOZYM AB', 1123, 893, 508, 543, 1, 5, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99713', 'TRUPOCRYL K-U-27', 920, 830, 570, 601, 1, 5, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82776', 'TRUPOZYM CL', 1375, 905, 852, 982, 1, 5, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65269', 'TRUPOCRYL K-U-327', 566, 635, 640, 703, 1, 5, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('77317', 'TRUPOCRYL A-32', 686, 701, 949, 896, 1, 5, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37732', 'TRUPOFIN CERA HP', 944, 894, 510, 859, 1, 5, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7492', 'TRUPON DBS', 897, 788, 647, 513, 1, 5, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12693', 'TRUPOCRYL K-U-10', 657, 950, 761, 486, 1, 5, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('50374', 'TRUPOFIN CERA K-A', 1018, 582, 529, 885, 1, 5, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14592', 'TRUPOFIN CERA SP', 1034, 736, 946, 605, 1, 5, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14744', 'TRUPON DBS', 610, 827, 670, 780, 1, 5, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59195', 'TRUPOCRYL A–90', 1463, 651, 599, 702, 1, 5, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87461', 'TRUPOCRYL K-U-310', 885, 610, 656, 394, 1, 5, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32015', 'PASTOSOL F', 570, 541, 717, 928, 1, 5, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84141', 'TRUPOFIN CERA D-80', 678, 887, 861, 501, 1, 5, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12397', 'TRUPOFIN CERA M', 530, 631, 539, 492, 1, 5, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63680', 'TRUPOCRYL A-10', 1035, 702, 831, 887, 1, 6, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9810', 'TRUPOCRYL K-U-27', 1293, 648, 756, 596, 1, 6, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18077', 'TRUPOSOL WBF', 1390, 832, 846, 759, 1, 6, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56349', 'TRUPON DB-80', 896, 572, 719, 582, 1, 6, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46087', 'TRUPON BMF', 537, 931, 703, 826, 1, 6, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82408', 'TRUPON BMF', 887, 806, 687, 901, 1, 6, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33652', 'TRUPOWET SA', 697, 540, 926, 861, 1, 6, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11883', 'TRUPOZYM AB', 1454, 990, 930, 950, 1, 6, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71888', 'TRUPOFIN CERA W', 739, 818, 817, 557, 1, 6, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78649', 'TRUPOTEC S', 1277, 901, 631, 434, 1, 6, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('80626', 'TRUPOFIN CERA PW', 767, 863, 552, 631, 1, 6, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36370', 'TRUPOCRYL A-30', 1365, 714, 715, 581, 1, 6, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53898', 'TRUPON COL', 1159, 827, 916, 400, 1, 6, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37792', 'TRUPOTEC T', 948, 908, 792, 992, 1, 6, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71546', 'TRUPOSYL TBK', 1005, 858, 850, 307, 1, 6, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83307', 'TRUPOFIN CERA D-80', 1041, 937, 661, 701, 1, 6, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63129', 'TRUPOFIN CERA DP', 570, 902, 779, 680, 1, 6, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1708', 'TRUPOFIN CERA SP', 1487, 974, 864, 628, 1, 6, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('38436', 'TRUPOTEC S', 1064, 936, 644, 911, 1, 6, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91264', 'TRUPOZYM AB', 1237, 925, 655, 670, 1, 6, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48894', 'TRUPOFIN CERA P', 742, 592, 841, 891, 1, 6, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48287', 'TRUPOCRYL A–90', 1043, 979, 812, 792, 1, 6, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71889', 'TRUPOCRYL A–90', 579, 612, 533, 837, 1, 7, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13644', 'TRUPOSYL HBD', 1082, 835, 556, 665, 1, 7, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('39736', 'TRUPOCRYL A-20', 1031, 865, 910, 343, 1, 7, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33496', 'PASTOSOL MD', 1499, 843, 536, 626, 1, 7, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31020', 'TRUPOZYM CB', 933, 693, 663, 486, 1, 7, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86371', 'TRUPOFIN CERA HS', 1229, 763, 695, 702, 1, 7, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25618', 'TRUPOCRYL K-U-310', 1321, 529, 768, 484, 1, 7, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62772', 'TRUPOZYM AB', 658, 780, 745, 987, 1, 7, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16728', 'TRUPOCRYL A-30', 563, 986, 670, 474, 1, 7, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83610', 'TRUPOFIN CERA FP', 915, 739, 944, 852, 1, 7, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25793', 'TRUPOFIN CERA DP', 1453, 811, 865, 790, 1, 7, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36143', 'TRUPOTAN OM', 1421, 591, 511, 890, 1, 7, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96963', 'TRUPOCRYL A-18', 1022, 960, 900, 690, 1, 7, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98340', 'TRUPOCRYL A-30', 1090, 788, 962, 317, 1, 7, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21428', 'TRUPOCRYL A-10', 1208, 615, 789, 739, 1, 7, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16140', 'PASTOSOL MD', 957, 640, 752, 620, 1, 7, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26361', 'TRUPOSEPT FP', 882, 920, 972, 853, 1, 7, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62295', 'TRUPOTEC T', 617, 695, 824, 809, 1, 7, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5161', 'TRUPOFIN CERA PW', 1448, 988, 858, 519, 1, 7, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('38842', 'TRUPOFIN CERA HF', 1082, 569, 763, 918, 1, 7, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75738', 'PASTOSOL MD', 640, 835, 721, 991, 1, 7, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41271', 'TRUPOL RK', 1348, 910, 776, 442, 1, 7, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86204', 'SOLVOTAN TACTO', 974, 584, 962, 952, 1, 7, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62068', 'TRUPOFIN CERA CA', 1065, 673, 807, 391, 1, 7, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43262', 'TRUPOSLIP P', 1327, 589, 786, 776, 1, 7, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('47230', 'TRUPOFIN CERA K-A', 1383, 899, 731, 351, 1, 7, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('6390', 'TRUPOFIN CERA HF', 1193, 647, 912, 986, 1, 7, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96189', 'PASTOSOL MD', 1135, 879, 507, 991, 1, 8, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28690', 'TRUPOSYL HBD', 1362, 876, 590, 938, 1, 8, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('77798', 'TRUPOCRYL A-35', 750, 731, 775, 646, 1, 8, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('6866', 'TRUPONAT NF', 1184, 651, 844, 516, 1, 8, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('80025', 'PASTOSOL KC', 839, 958, 643, 643, 1, 8, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46617', 'TRUPON BMF', 911, 715, 668, 871, 1, 8, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95032', 'TRUPOFIN CERA P', 676, 687, 803, 799, 1, 8, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('149', 'TRUPOTAN MON', 739, 510, 882, 901, 1, 8, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12861', 'TRUPON DB-80', 608, 977, 858, 814, 1, 8, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7644', 'TRUPON BMF', 526, 567, 696, 678, 1, 8, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75626', 'TRUPOFIN CERA A', 681, 583, 761, 453, 1, 8, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33350', 'TRUPOFIN CERA D-80', 1281, 561, 559, 818, 1, 8, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23224', 'TRUPOSYL TBA', 696, 716, 872, 493, 1, 8, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51594', 'TRUPOZYM AB', 1282, 724, 779, 390, 1, 8, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57303', 'TRUPOFIN CERA PV', 603, 948, 826, 707, 1, 8, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66947', 'TRUPOCRYL A-35', 605, 556, 971, 544, 1, 8, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65569', 'TRUPON CST', 1468, 504, 936, 832, 1, 8, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78612', 'TRUPOCRYL K-U-327', 598, 804, 745, 609, 1, 8, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67520', 'TRUPOCRYL A-28', 1113, 754, 952, 991, 1, 8, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11542', 'TRUKALIN K', 1496, 909, 700, 940, 1, 8, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23928', 'TRUPOFIN CERA A', 1334, 873, 528, 996, 1, 8, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91093', 'TRUPOTAN MOW', 582, 594, 748, 804, 1, 8, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82355', 'TRUPON AP', 1256, 577, 841, 680, 1, 8, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46798', 'TRUPOCRYL A–90', 1264, 956, 726, 617, 1, 8, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('2867', 'TRUPOCRYL A-20', 523, 716, 841, 461, 1, 8, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99736', 'TRUPON AP', 1002, 581, 965, 996, 1, 8, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('2273', 'TRUPONAT LA', 1046, 657, 980, 310, 1, 8, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67350', 'TRUPOCRYL K-A-30', 1171, 544, 586, 847, 1, 8, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82751', 'TRUPOFIN CERA M', 1074, 982, 845, 678, 1, 8, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48256', 'TRUPOCRYL A-30', 1210, 616, 597, 675, 1, 8, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34216', 'TRUPOCRYL A-28', 510, 659, 866, 570, 2, 1, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92236', 'TRUPOFIN CERA HS', 1314, 903, 983, 639, 2, 1, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18740', 'PASTOSOL BCN', 970, 616, 950, 312, 2, 1, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34194', 'TRUPON BMF', 994, 549, 514, 732, 2, 1, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49092', 'TRUPOCRYL A-28', 1200, 756, 800, 666, 2, 1, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13024', 'SOLVOTAN TACTO', 1482, 719, 551, 964, 2, 1, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('20997', 'TRUPOSYL TBA', 1110, 731, 645, 716, 2, 1, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40249', 'TRUPOSLIP P', 1162, 564, 936, 334, 2, 1, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('55385', 'TRUPOWET SA', 1121, 888, 759, 433, 2, 1, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('8437', 'TRUPOZYM CH', 792, 684, 597, 396, 2, 1, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('44561', 'TRUPOCRYL K-A-30', 1494, 954, 950, 842, 2, 1, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92345', 'TRUPOSLIP P', 1210, 925, 867, 560, 2, 1, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46116', 'TRUPOL RK', 784, 695, 691, 677, 2, 1, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63174', 'TRUPOCRYL AB', 1048, 651, 528, 305, 2, 1, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84725', 'PASTOSOL KC', 1170, 797, 528, 823, 2, 1, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94912', 'TRUPOCAL AF', 687, 837, 683, 884, 2, 1, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72676', 'TRUPOCRYL K-U-27', 1222, 964, 848, 347, 2, 1, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('27548', 'TRUPOCRYL A-35', 799, 926, 856, 336, 2, 1, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('44039', 'PASTOSOL BCN', 1249, 623, 604, 527, 2, 1, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45366', 'TRUPOCRYL K-U-327', 564, 592, 973, 405, 2, 1, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99224', 'TRUPON BMF', 943, 514, 544, 835, 2, 1, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56190', 'TRUPOFIN CERA K-A', 1042, 749, 763, 427, 2, 1, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37109', 'TRUPOCRYL K-U-10', 659, 935, 831, 786, 2, 1, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11573', 'TRUPONAT LA', 1153, 668, 952, 902, 2, 1, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99354', 'TRUPOFIN CERA PV', 811, 830, 999, 371, 2, 1, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('58165', 'TRUPOFIN CERA W', 897, 614, 582, 422, 2, 1, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72472', 'TRUPOCRYL K-U-27', 1390, 900, 977, 732, 2, 1, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21088', 'TRUPOTEC T', 769, 634, 597, 438, 2, 1, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41924', 'TRUPONAT NF', 1090, 528, 904, 818, 2, 1, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16943', 'PASTOSOL F', 999, 777, 697, 782, 2, 1, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45166', 'TRUPON DB-80', 559, 857, 938, 741, 2, 1, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('77825', 'TRUPOSIST D', 790, 958, 774, 979, 2, 1, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94388', 'TRUPOCRYL A-10', 712, 930, 988, 416, 2, 1, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45134', 'TRUPOCRYL A-35', 1370, 837, 539, 972, 2, 1, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('80895', 'TRUPOCRYL A-18', 1293, 898, 776, 761, 2, 1, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84078', 'TRUPOSIST D', 1000, 854, 820, 928, 2, 1, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15119', 'TRUPOSYL TBD', 830, 558, 595, 479, 2, 2, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65765', 'TRUPOCRYL A–90', 660, 534, 528, 382, 2, 2, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86468', 'TRUPOFIN CERA HP', 547, 824, 806, 601, 2, 2, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('76352', 'TRUPOSYL TBA', 1118, 808, 590, 378, 2, 2, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('89971', 'ANTIFOAM NSP', 1170, 771, 744, 528, 2, 2, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('85144', 'ANTIFOAM NSP', 1425, 917, 883, 472, 2, 2, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40678', 'TRUKALIN K', 1287, 774, 634, 693, 2, 2, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81303', 'TRUPOCRYL A–90', 836, 686, 681, 784, 2, 2, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33628', 'TRUPOCRYL A-10', 522, 632, 580, 770, 2, 2, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62603', 'TRUPOWET PH', 703, 765, 605, 693, 2, 2, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23299', 'TRUPOSLIP P', 518, 982, 680, 725, 2, 2, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1539', 'TRUPOZYM CB', 1054, 910, 703, 906, 2, 2, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67392', 'TRUPOSYL TBK', 1420, 911, 976, 779, 2, 2, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81843', 'TRUPOFIN CERA 3A', 994, 980, 515, 391, 2, 2, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71718', 'TRUPON PEM', 1016, 842, 788, 696, 2, 2, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83136', 'TRUPON COL', 568, 645, 882, 498, 2, 2, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73865', 'TRUPOZYM CL', 1235, 872, 626, 637, 2, 2, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('97686', 'TRUPON AP', 1445, 711, 779, 964, 2, 2, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40077', 'TRUPOSYL TBD', 580, 883, 539, 959, 2, 2, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73910', 'TRUPOSYL TBD', 680, 502, 575, 574, 2, 2, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19758', 'PASTOSOL MD', 1194, 938, 905, 674, 2, 2, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51659', 'TRUPOCRYL K-A-30', 915, 519, 730, 769, 2, 2, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25713', 'TRUPOFIN CERA A', 619, 701, 827, 578, 2, 2, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('30120', 'TRUPOCRYL K-A-30', 940, 641, 804, 556, 2, 2, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29684', 'TRUPOZYM CL', 521, 670, 819, 833, 2, 2, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36259', 'TRUPOCRYL A-35', 921, 547, 590, 466, 2, 2, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33749', 'TRUPON PEM', 834, 575, 585, 909, 2, 2, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9307', 'TRUPON CM', 1038, 619, 525, 701, 2, 2, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75981', 'TRUPOCRYL A-35', 741, 948, 877, 738, 2, 2, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29662', 'TRUPON PEM', 1184, 695, 579, 628, 2, 2, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43875', 'SOLVOTAN XS', 638, 776, 738, 906, 2, 2, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('93144', 'PASTOSOL F', 834, 856, 551, 424, 2, 3, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48962', 'TRUPON BMF', 616, 726, 813, 722, 2, 3, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9626', 'TRUPOCRYL A-20', 731, 697, 593, 484, 2, 3, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29602', 'TRUPOWET PH', 1468, 647, 918, 533, 2, 3, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25371', 'TRUPOCRYL A–90', 1232, 797, 614, 744, 2, 3, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40230', 'TRUPOFIN CERA SP', 937, 875, 783, 817, 2, 3, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92096', 'TRUPOZYM AB', 579, 533, 789, 350, 2, 3, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71729', 'TRUPOCRYL K-U-327', 1244, 964, 924, 904, 2, 3, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26430', 'TRUPON BMF', 582, 747, 719, 939, 2, 3, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('68182', 'TRUPOFIN CERA HP', 1486, 602, 956, 758, 2, 3, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51866', 'TRUPONAT NF', 862, 558, 991, 722, 2, 3, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36617', 'TRUPOFIN CERA P', 1108, 675, 573, 562, 2, 3, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56748', 'TRUPON DB-80', 1363, 605, 711, 355, 2, 3, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40871', 'SOLVOTAN XS', 1087, 825, 742, 853, 2, 3, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29232', 'TRUPOCRYL A-18', 691, 526, 926, 954, 2, 3, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92497', 'TRUPON COL', 809, 753, 895, 466, 2, 3, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81604', 'TRUPOCRYL K-U-10', 1235, 797, 839, 965, 2, 3, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('44114', 'TRUPOSLIP P', 1367, 771, 959, 847, 2, 3, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('636', 'TRUPOCAL DE', 860, 685, 506, 593, 2, 3, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('35030', 'TRUKALIN K', 738, 663, 951, 811, 2, 3, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('2447', 'PASTOSOL F', 1112, 711, 812, 509, 2, 3, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57428', 'PASTOSOL DG', 566, 707, 891, 599, 2, 3, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60736', 'TRUPOZYM CL', 1069, 844, 944, 779, 2, 3, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('76271', 'TRUPOCRYL A-20', 508, 743, 528, 510, 2, 3, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28493', 'TRUPONAT LA', 701, 931, 605, 684, 2, 3, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('30407', 'PASTOSOL BCN', 1468, 848, 775, 606, 2, 3, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87558', 'TRUPOFIN CERA A', 1057, 845, 506, 728, 2, 3, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5011', 'TRUPOTAN MOW', 635, 664, 910, 874, 2, 3, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18914', 'TRUPOWET SA', 1145, 758, 919, 511, 2, 3, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('80938', 'TRUPOFIN CERA W', 1126, 789, 528, 991, 2, 3, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91012', 'TRUPOFIN CERA HS', 1253, 704, 618, 360, 2, 3, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31127', 'TRUPOSYL ABS', 573, 969, 762, 398, 2, 3, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9292', 'TRUPOCRYL A-10', 1292, 947, 672, 922, 2, 3, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4804', 'TRUPON AP', 979, 531, 593, 733, 2, 4, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29104', 'TRUPOSLIP P', 699, 730, 813, 550, 2, 4, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('35268', 'TRUPOCRYL A-32', 568, 559, 784, 537, 2, 4, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62913', 'TRUPOCRYL K-U-10', 761, 831, 931, 982, 2, 4, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67303', 'TRUPOFIN CERA P', 1354, 617, 688, 354, 2, 4, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('47391', 'TRUPON DBS', 1420, 818, 977, 468, 2, 4, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94827', 'TRUPOSYL TBA', 1296, 775, 522, 933, 2, 4, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99131', 'PASTOSOL MD', 740, 692, 621, 925, 2, 4, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49797', 'TRUPOFIN CERA FP', 1328, 503, 506, 666, 2, 4, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75046', 'TRUPOFIN CERA PV', 1467, 948, 896, 406, 2, 4, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13073', 'TRUPONAT NF', 1223, 803, 618, 401, 2, 4, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71044', 'TRUPOTAN MOW', 999, 760, 518, 908, 2, 4, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60416', 'TRUPON COL', 669, 736, 782, 426, 2, 4, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49765', 'TRUPOCAL DE', 1142, 520, 568, 543, 2, 4, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60265', 'TRUPOCRYL A-18', 1327, 583, 752, 944, 2, 4, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84281', 'TRUPON AP', 1379, 837, 505, 617, 2, 4, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96328', 'TRUPOCRYL A-18', 1227, 834, 889, 561, 2, 4, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48451', 'TRUPOTAN MON', 1075, 767, 629, 960, 2, 4, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92882', 'TRUPON CM', 1060, 610, 809, 941, 2, 4, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28694', 'TRUPOSYL TBK', 1191, 833, 930, 606, 2, 4, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78552', 'TRUPOCRYL K-U-310', 1268, 536, 653, 403, 2, 4, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52855', 'TRUPOCRYL A-32', 1327, 582, 786, 962, 2, 4, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71945', 'TRUPOTAN MOW', 1110, 692, 900, 846, 2, 4, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('88944', 'SOLVOTAN TACTO', 865, 939, 940, 436, 2, 4, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('362', 'TRUPOFIN CERA 3A', 1339, 646, 967, 868, 2, 4, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83622', 'TRUPOFIN CERA CA', 1005, 983, 524, 873, 2, 4, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('2732', 'TRUPOSYL TBD', 1195, 766, 850, 927, 2, 4, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91892', 'TRUPOTEC S', 763, 737, 715, 835, 2, 4, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('38350', 'TRUPOTAN MON', 1096, 913, 633, 880, 2, 5, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62011', 'TRUPOFIN CERA 3A', 509, 683, 539, 608, 2, 5, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23101', 'TRUPOSIST S-BO', 1109, 814, 825, 396, 2, 5, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('3745', 'TRUPOSLIP P', 1195, 821, 683, 726, 2, 5, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('97460', 'TRUPOSYL TBD', 1152, 832, 959, 420, 2, 5, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('8357', 'TRUPOCRYL A-28', 735, 792, 813, 899, 2, 5, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5737', 'TRUPOZYM CL', 1421, 963, 832, 904, 2, 5, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52476', 'TRUPOZYM AX', 1052, 685, 876, 828, 2, 5, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('42288', 'TRUPOCRYL A-18', 1082, 853, 897, 564, 2, 5, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('79258', 'TRUKALIN K', 514, 507, 650, 540, 2, 5, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('2991', 'TRUPOFIN CERA FP', 659, 730, 837, 309, 2, 5, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4703', 'TRUPOTAN OM', 1170, 568, 555, 409, 2, 5, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21553', 'TRUPOFIN CERA E', 670, 983, 807, 433, 2, 5, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34778', 'TRUPOCRYL A-30', 873, 938, 701, 462, 2, 5, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59064', 'TRUPOFIN CERA E', 532, 717, 860, 341, 2, 5, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84350', 'TRUPOSYL TBD', 1075, 528, 501, 577, 2, 5, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49271', 'TRUPOFIN CERA 3A', 781, 647, 923, 806, 2, 5, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('6361', 'TRUPOCRYL A-35', 1415, 819, 649, 580, 2, 5, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('27496', 'TRUPOCRYL K-U-27', 629, 685, 898, 804, 2, 5, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62188', 'PASTOSOL F', 1419, 884, 767, 591, 2, 5, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('27382', 'PASTOSOL F', 1465, 655, 594, 433, 2, 5, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67569', 'PASTOSOL F', 888, 577, 605, 624, 2, 5, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29193', 'TRUPOCRYL A-28', 969, 815, 929, 391, 2, 5, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78414', 'SOLVOTAN XS', 1255, 795, 509, 384, 2, 5, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87710', 'TRUPONAT NF', 1021, 934, 981, 679, 2, 5, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12035', 'SOLVOTAN TACTO', 546, 513, 591, 526, 2, 5, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26262', 'TRUPOTEC S', 1278, 884, 825, 517, 2, 6, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34994', 'TRUPOZYM AB', 617, 815, 527, 729, 2, 6, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81093', 'TRUPOFIN CERA D-80', 1100, 861, 561, 728, 2, 6, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('64078', 'TRUKALIN K', 998, 986, 831, 662, 2, 6, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62238', 'TRUPOSIST D', 1109, 836, 884, 886, 2, 6, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91096', 'TRUPOTAN OM', 1147, 910, 658, 935, 2, 6, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15931', 'TRUPOCRYL K-U-327', 594, 928, 783, 738, 2, 6, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57042', 'TRUPOSIST S-BO', 1329, 964, 512, 419, 2, 6, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('42528', 'TRUPOTEC S', 1037, 680, 592, 691, 2, 6, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87061', 'TRUPOSOL WBF', 1176, 545, 767, 974, 2, 6, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('76787', 'TRUPOCRYL A-20', 1103, 692, 762, 904, 2, 6, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('93953', 'TRUPOFIN CERA HP', 556, 862, 632, 823, 2, 6, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('27924', 'TRUPOFIN CERA KT 08', 990, 612, 890, 757, 2, 6, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75552', 'TRUPOFIN CERA FP', 1011, 585, 997, 657, 2, 6, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73366', 'TRUPOWET PH', 795, 635, 550, 669, 2, 6, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('70428', 'ANTIFOAM NSP', 1271, 746, 652, 494, 2, 6, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90752', 'TRUPOFIN CERA E', 617, 763, 970, 509, 2, 6, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('89094', 'PASTOSOL KC', 830, 906, 623, 642, 2, 6, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28975', 'TRUPOCRYL A-20', 1295, 723, 871, 856, 2, 6, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87843', 'TRUPOFIN CERA A', 1274, 568, 999, 430, 2, 6, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23889', 'TRUPOSIST D', 817, 809, 768, 525, 2, 6, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25005', 'TRUPOFIN CERA FP', 1230, 530, 889, 903, 2, 6, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67882', 'TRUPOFIN CERA D-80', 1462, 650, 659, 996, 2, 6, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37498', 'TRUPON AP', 686, 619, 855, 637, 2, 6, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25726', 'TRUPOZYM CL', 936, 784, 598, 554, 2, 6, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37304', 'TRUPON DBS', 697, 932, 737, 725, 2, 6, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21556', 'TRUPOSYL HBD', 816, 895, 623, 976, 2, 6, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49912', 'TRUPOCRYL A-10', 1264, 910, 909, 305, 2, 6, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95757', 'TRUPOCRYL A-18', 1487, 835, 534, 916, 2, 6, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57399', 'TRUPOCRYL K-U-10', 1113, 818, 979, 860, 2, 6, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87375', 'ANTIFOAM NSP', 736, 787, 716, 837, 2, 7, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92328', 'TRUPOZYM CB', 1287, 640, 504, 607, 2, 7, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('39719', 'TRUPOSIST S-BO', 994, 947, 645, 599, 2, 7, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45303', 'ANTIFOAM NSP', 536, 899, 821, 315, 2, 7, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57393', 'TRUPOFIN CERA BT', 876, 858, 993, 497, 2, 7, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21393', 'TRUPOCRYL A-35', 1023, 944, 714, 666, 2, 7, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19386', 'PASTOSOL BCN', 763, 741, 623, 649, 2, 7, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25632', 'TRUPOFIN CERA D-80', 1283, 846, 717, 666, 2, 7, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('70851', 'ANTIFOAM NSP', 861, 777, 669, 413, 2, 7, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26517', 'TRUPONAT LA', 1409, 915, 948, 689, 2, 7, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87636', 'TRUPOFIN CERA D-80', 1172, 685, 518, 746, 2, 7, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24435', 'TRUPOCRYL K-U-327', 1409, 801, 730, 549, 2, 7, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('10587', 'TRUPOSEPT FP', 553, 686, 727, 994, 2, 7, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63806', 'PASTOSOL DG', 660, 996, 998, 338, 2, 7, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41760', 'TRUPON AP', 1409, 534, 552, 878, 2, 7, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46137', 'TRUPOSIST D', 1185, 617, 967, 792, 2, 7, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87648', 'TRUPOSYL TBK', 882, 783, 690, 894, 2, 7, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('39557', 'TRUPOFIN CERA E', 983, 942, 894, 989, 2, 7, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('8909', 'TRUPOCAL DE', 1428, 632, 868, 564, 2, 7, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21890', 'TRUPOSOL WBF', 983, 531, 779, 703, 2, 7, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41316', 'TRUPOCRYL K-A-30', 932, 680, 826, 916, 2, 7, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('20325', 'TRUPOFIN CERA DP', 1209, 936, 591, 644, 2, 7, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('54368', 'TRUPON DBS', 1443, 614, 523, 812, 2, 7, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('61234', 'TRUPON DBS', 643, 702, 664, 587, 2, 7, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49132', 'SOLVOTAN TACTO', 1015, 565, 756, 767, 2, 7, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22742', 'PASTOSOL DG', 567, 895, 667, 746, 2, 7, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66414', 'TRUPOFIN CERA W', 1391, 508, 625, 967, 2, 8, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45365', 'TRUPOCRYL AB', 1404, 529, 867, 844, 2, 8, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('10227', 'TRUPOFIN CERA PV', 1023, 719, 924, 886, 2, 8, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25097', 'TRUPOFIN CERA E', 1033, 807, 632, 815, 2, 8, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21684', 'TRUPOTAN MON', 715, 527, 604, 534, 2, 8, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7966', 'TRUPOCAL DE', 974, 658, 780, 441, 2, 8, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60978', 'TRUPOSYL ABS', 771, 805, 910, 332, 2, 8, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('42779', 'TRUPOWET SA', 503, 649, 687, 900, 2, 8, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('58589', 'TRUPON CM', 925, 946, 823, 756, 2, 8, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15933', 'TRUPOFIN CERA PV', 1235, 730, 917, 619, 2, 8, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('54502', 'TRUPOCRYL K-A-30', 904, 865, 755, 887, 2, 8, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23969', 'TRUPOTAN MON', 1061, 631, 851, 350, 2, 8, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25802', 'TRUPOCRYL K-A-30', 1475, 672, 778, 500, 2, 8, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('44838', 'TRUPOFIN CERA HF', 1154, 832, 817, 732, 2, 8, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22016', 'TRUPOSYL ABS', 999, 536, 893, 929, 2, 8, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('33541', 'TRUPOWET SA', 601, 750, 867, 798, 2, 8, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1340', 'PASTOSOL DG', 581, 580, 848, 836, 2, 8, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48712', 'TRUPOSEPT BA', 847, 917, 671, 810, 2, 8, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62658', 'TRUPOFIN CERA BT', 1192, 915, 521, 897, 2, 8, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14459', 'TRUPOFIN CERA A', 1427, 887, 922, 869, 2, 8, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('64057', 'SOLVOTAN TACTO', 858, 671, 984, 827, 2, 8, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('54403', 'TRUPOFIN CERA HF', 636, 918, 828, 567, 2, 8, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28485', 'TRUPOZYM AX', 1357, 881, 908, 779, 2, 8, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1450', 'TRUPOZYM AX', 1427, 845, 904, 406, 2, 8, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('70417', 'TRUPOCRYL A-28', 884, 629, 794, 680, 2, 8, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82258', 'TRUPOSYL HBD', 1497, 751, 980, 903, 2, 8, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7774', 'TRUPOFIN CERA HS', 874, 536, 944, 953, 2, 8, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63387', 'TRUPOSLIP P', 1092, 817, 601, 716, 2, 8, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92559', 'PASTOSOL MD', 584, 542, 943, 871, 2, 8, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11165', 'TRUPOFIN CERA D-80', 815, 573, 722, 694, 2, 8, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43738', 'TRUPONAT NF', 1281, 755, 579, 412, 3, 1, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('6086', 'TRUPON PEM', 847, 655, 551, 762, 3, 1, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40870', 'TRUPONAT LA', 653, 967, 706, 840, 3, 1, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34830', 'TRUPOSOL WBF', 661, 605, 533, 659, 3, 1, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('64927', 'TRUPON AP', 1484, 669, 834, 413, 3, 1, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16633', 'ANTIFOAM NSP', 1199, 972, 627, 754, 3, 1, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74578', 'PASTOSOL MD', 1040, 841, 544, 460, 3, 1, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53014', 'TRUPOCRYL A–90', 584, 595, 623, 853, 3, 1, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('97139', 'TRUPOZYM AB', 825, 710, 970, 717, 3, 1, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84827', 'TRUPOFIN CERA PW', 618, 740, 719, 300, 3, 1, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('50504', 'ANTIFOAM NSP', 1166, 680, 736, 550, 3, 1, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81450', 'TRUPOSEPT BA', 1164, 911, 794, 860, 3, 1, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('77163', 'SOLVOTAN XS', 983, 899, 763, 464, 3, 1, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('54577', 'TRUPOFIN CERA SP', 1201, 717, 792, 544, 3, 1, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43145', 'TRUPOCRYL K-A-30', 892, 943, 596, 574, 3, 1, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31987', 'TRUPOCRYL A-28', 1309, 744, 756, 874, 3, 1, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('88063', 'TRUPOFIN CERA D-80', 1154, 866, 621, 729, 3, 1, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95715', 'TRUPOFIN CERA E', 1494, 927, 757, 718, 3, 1, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74994', 'TRUPOSYL TBK', 994, 511, 727, 375, 3, 1, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82023', 'TRUPON PEM', 1233, 758, 706, 318, 3, 1, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40915', 'TRUPOFIN CERA SP', 1013, 757, 860, 940, 3, 1, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32671', 'TRUPOCRYL A-35', 1021, 604, 907, 951, 3, 1, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('55826', 'TRUPOWET PH', 799, 694, 543, 546, 3, 1, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56849', 'TRUPOZYM CL', 1493, 749, 953, 500, 3, 1, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('68466', 'TRUPOZYM AX', 1366, 510, 625, 370, 3, 1, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74614', 'TRUPOCRYL A-32', 551, 989, 768, 746, 3, 1, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60731', 'TRUPOCRYL A-10', 882, 759, 698, 990, 3, 1, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18283', 'PASTOSOL KC', 1360, 619, 694, 447, 3, 1, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22225', 'TRUPON DB-80', 1468, 689, 741, 878, 3, 1, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11293', 'TRUPOFIN CERA W', 1255, 947, 892, 339, 3, 1, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('58775', 'TRUPOFIN CERA KT 08', 1074, 707, 919, 906, 3, 1, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21261', 'PASTOSOL KC', 514, 582, 870, 468, 3, 2, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36804', 'TRUPOCRYL K-U-10', 556, 872, 990, 875, 3, 2, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('68553', 'TRUPOCAL AF', 1184, 828, 646, 905, 3, 2, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13639', 'TRUPOFIN CERA HS', 703, 723, 710, 316, 3, 2, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51078', 'TRUPOSEPT FP', 896, 989, 638, 695, 3, 2, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63768', 'TRUPOTAN MOW', 948, 838, 965, 318, 3, 2, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('76878', 'TRUPOSIST S-BO', 1396, 652, 886, 547, 3, 2, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86935', 'SOLVOTAN XS', 1150, 780, 592, 456, 3, 2, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('10569', 'PASTOSOL MD', 1218, 586, 973, 614, 3, 2, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26395', 'TRUPON CM', 1165, 756, 776, 705, 3, 2, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31578', 'TRUPON PEM', 771, 837, 657, 624, 3, 2, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('47160', 'TRUPOFIN CERA HS', 846, 837, 506, 427, 3, 2, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78787', 'TRUPOFIN CERA CA', 978, 718, 726, 675, 3, 2, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5632', 'TRUPOFIN CERA M', 856, 512, 661, 950, 3, 2, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34290', 'TRUPOCRYL A-30', 798, 650, 636, 722, 3, 2, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59', 'TRUPOFIN CERA DP', 1472, 948, 969, 544, 3, 2, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12269', 'TRUPON COL', 737, 798, 683, 700, 3, 2, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4526', 'TRUPOFIN CERA SP', 1125, 843, 597, 554, 3, 2, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53506', 'TRUPOFIN CERA W', 1437, 704, 523, 753, 3, 2, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('30106', 'TRUPOSYL TBA', 1116, 553, 824, 881, 3, 2, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('69025', 'TRUPOCRYL K-U-327', 1449, 597, 844, 567, 3, 2, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7066', 'TRUPOFIN CERA W', 1393, 975, 572, 421, 3, 2, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('93704', 'TRUPOCAL DE', 1073, 984, 517, 692, 3, 2, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34908', 'TRUKALIN K', 1490, 912, 817, 876, 3, 3, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48140', 'PASTOSOL HW', 1007, 939, 719, 336, 3, 3, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66408', 'TRUPOZYM CH', 1039, 669, 532, 838, 3, 3, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('88172', 'TRUPOZYM CB', 1263, 629, 629, 450, 3, 3, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41598', 'TRUPOSIST S-BO', 1487, 720, 922, 370, 3, 3, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57455', 'TRUPOFIN CERA FP', 874, 505, 634, 404, 3, 3, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5623', 'TRUPOWET PH', 1274, 680, 728, 876, 3, 3, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13880', 'TRUPOSYL ABS', 1226, 801, 951, 821, 3, 3, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28062', 'TRUPOFIN CERA FP', 1361, 805, 585, 660, 3, 3, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32482', 'TRUPOZYM CL', 1317, 688, 791, 493, 3, 3, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('8698', 'TRUPOCRYL K-U-27', 1448, 710, 755, 466, 3, 3, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49990', 'TRUPON CST', 1435, 535, 660, 658, 3, 3, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19752', 'SOLVOTAN XS', 968, 615, 815, 484, 3, 3, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94707', 'TRUPON CST', 548, 623, 680, 624, 3, 3, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66480', 'TRUPOSYL ABS', 857, 789, 624, 680, 3, 3, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49349', 'TRUPOZYM AX', 1090, 669, 988, 794, 3, 3, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41218', 'TRUPON AP', 693, 983, 969, 588, 3, 3, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74484', 'TRUPOCRYL A-20', 856, 645, 597, 993, 3, 3, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98631', 'TRUPOCAL AF', 1244, 770, 771, 433, 3, 3, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66157', 'TRUPOFIN CERA HF', 632, 587, 508, 506, 3, 3, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('79197', 'TRUPOCRYL A-10', 1131, 867, 881, 796, 3, 3, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('64255', 'PASTOSOL MD', 1380, 800, 694, 647, 3, 3, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34223', 'TRUPOSEPT BA', 1089, 638, 613, 509, 3, 3, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('17903', 'TRUPOSIST S-BO', 1372, 545, 813, 443, 3, 3, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('97288', 'TRUPOZYM CB', 1122, 603, 760, 980, 3, 3, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('47842', 'TRUPOFIN CERA CA', 596, 898, 539, 814, 3, 3, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87405', 'TRUPOCAL AF', 787, 523, 525, 965, 3, 3, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84800', 'TRUPON CM', 891, 760, 982, 904, 3, 3, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11118', 'TRUPOFIN CERA P', 968, 559, 932, 806, 3, 3, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('77551', 'TRUPON DBS', 898, 905, 627, 338, 3, 3, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51394', 'TRUPOSYL ABS', 1404, 894, 581, 408, 3, 4, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37884', 'TRUPOTAN MON', 598, 850, 811, 519, 3, 4, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('17647', 'TRUPON DB-80', 504, 900, 745, 554, 3, 4, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29819', 'TRUPOTAN MOW', 1113, 883, 746, 853, 3, 4, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21283', 'TRUPOCAL AF', 1108, 814, 878, 367, 3, 4, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65529', 'TRUPOSIST D', 1228, 623, 669, 910, 3, 4, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14855', 'TRUPOZYM CL', 766, 619, 734, 931, 3, 4, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60651', 'TRUPOFIN CERA CA', 1282, 820, 614, 636, 3, 4, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15079', 'TRUPONAT LA', 1425, 991, 749, 737, 3, 4, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22762', 'TRUPOZYM CN', 1487, 726, 689, 614, 3, 4, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('88928', 'TRUPOFIN CERA K-A', 1332, 604, 885, 518, 3, 4, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99389', 'TRUPOZYM AX', 976, 949, 858, 544, 3, 4, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75596', 'TRUPOSYL TBD', 1331, 762, 890, 409, 3, 4, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('42574', 'TRUPON CM', 842, 781, 829, 489, 3, 4, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('69245', 'SOLVOTAN TACTO', 1194, 882, 769, 634, 3, 4, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91148', 'TRUPOCAL AF', 1437, 571, 973, 564, 3, 4, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('10758', 'TRUPOCRYL A-28', 661, 849, 947, 492, 3, 4, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('765', 'TRUPOZYM CB', 1040, 689, 710, 645, 3, 4, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45404', 'TRUPON BMF', 731, 568, 752, 321, 3, 4, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15378', 'TRUPONAT LA', 1239, 561, 773, 997, 3, 4, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22831', 'TRUPOFIN CERA CA', 1204, 912, 522, 891, 3, 4, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('79460', 'TRUPOFIN CERA HP', 1009, 575, 945, 863, 3, 4, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32670', 'TRUPOCAL DE', 1338, 622, 806, 458, 3, 4, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99135', 'TRUPOCRYL AB', 746, 976, 681, 847, 3, 4, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96364', 'TRUPOZYM AB', 1261, 730, 997, 824, 3, 4, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1180', 'TRUPOSOL WBF', 1422, 674, 844, 692, 3, 4, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82631', 'TRUPOZYM AX', 1098, 825, 532, 553, 3, 5, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96896', 'TRUPOFIN CERA CA', 821, 746, 720, 334, 3, 5, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14020', 'TRUPOSYL TBK', 578, 793, 531, 425, 3, 5, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59584', 'TRUPOSIST D', 1008, 689, 626, 340, 3, 5, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82051', 'TRUPOZYM CN', 700, 759, 855, 964, 3, 5, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12323', 'TRUPOSIST D', 591, 998, 556, 508, 3, 5, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40822', 'TRUPOCRYL K-A-30', 716, 892, 523, 593, 3, 5, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56310', 'TRUPON DBS', 1381, 790, 906, 412, 3, 5, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98213', 'TRUPOL RK', 557, 789, 509, 724, 3, 5, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51049', 'TRUPON DBS', 515, 520, 988, 812, 3, 5, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62728', 'TRUPONAT NF', 1375, 508, 739, 918, 3, 5, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52152', 'TRUPOSIST S-BO', 529, 862, 556, 358, 3, 5, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('65331', 'TRUPOCRYL K-A-30', 672, 601, 881, 470, 3, 5, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13171', 'TRUPOFIN CERA M', 1293, 745, 631, 750, 3, 5, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82792', 'PASTOSOL KC', 650, 810, 814, 462, 3, 5, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('47997', 'TRUPOZYM CL', 1203, 902, 514, 998, 3, 5, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('50311', 'TRUPOFIN CERA M', 593, 713, 869, 561, 3, 5, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31805', 'TRUPOWET PH', 966, 523, 825, 487, 3, 5, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98408', 'TRUPOCRYL A-20', 512, 664, 806, 463, 3, 5, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37343', 'TRUPOCRYL A–90', 1204, 971, 960, 317, 3, 5, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98776', 'TRUPOCAL AF', 560, 655, 610, 418, 3, 5, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('34971', 'TRUPOSIST S-BO', 1413, 903, 846, 466, 3, 5, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52960', 'TRUPOFIN CERA CA', 1375, 733, 890, 446, 3, 5, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56796', 'TRUPOTAN OM', 1116, 692, 711, 588, 3, 5, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24589', 'TRUPOTAN OM', 1216, 593, 968, 768, 3, 5, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1518', 'TRUPOFIN CERA HP', 995, 542, 572, 398, 3, 5, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59525', 'TRUPOTAN MON', 584, 725, 518, 336, 3, 5, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31495', 'TRUPON COL', 939, 991, 662, 591, 3, 5, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('30184', 'PASTOSOL F', 942, 582, 551, 392, 3, 5, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94524', 'TRUPOCRYL A-20', 1481, 847, 537, 413, 3, 5, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82044', 'TRUPOSYL ABS', 555, 725, 509, 546, 3, 6, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81352', 'TRUPON CM', 1330, 698, 945, 713, 3, 6, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('64000', 'TRUPOSIST D', 701, 924, 882, 349, 3, 6, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11838', 'TRUPOZYM CN', 1252, 783, 829, 321, 3, 6, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32144', 'TRUPOFIN CERA W', 774, 513, 808, 647, 3, 6, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25183', 'TRUPOCRYL A-35', 1117, 789, 673, 405, 3, 6, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('8725', 'TRUPOZYM CB', 904, 580, 825, 466, 3, 6, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('498', 'TRUPOZYM CB', 1467, 969, 919, 611, 3, 6, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('89501', 'TRUPOFIN CERA E', 1131, 533, 742, 802, 3, 6, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24956', 'TRUPOCRYL A-32', 1384, 818, 951, 959, 3, 6, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26873', 'TRUPOCRYL K-U-327', 719, 631, 604, 592, 3, 6, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78534', 'TRUPOFIN CERA A', 551, 873, 883, 566, 3, 6, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51088', 'TRUPOFIN CERA E', 837, 554, 692, 448, 3, 6, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83679', 'TRUPOCAL AF', 818, 655, 671, 729, 3, 6, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63416', 'TRUPOWET PH', 983, 861, 813, 315, 3, 6, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21512', 'TRUPOCRYL A–90', 1143, 863, 973, 460, 3, 6, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18720', 'TRUPOFIN CERA E', 1378, 770, 611, 663, 3, 6, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36934', 'TRUPOFIN CERA 3A', 1486, 919, 655, 812, 3, 6, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('68594', 'TRUPON DB-80', 951, 564, 723, 390, 3, 6, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82255', 'TRUPOFIN CERA D-80', 1340, 782, 770, 950, 3, 6, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73727', 'TRUPOCAL AF', 939, 706, 537, 955, 3, 6, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13097', 'TRUPON CST', 1004, 577, 973, 595, 3, 6, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51490', 'TRUPOFIN CERA HP', 782, 798, 837, 779, 3, 6, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('27822', 'TRUPOFIN CERA E', 556, 681, 939, 538, 3, 6, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72887', 'TRUPOCRYL A-18', 1105, 638, 810, 977, 3, 6, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26170', 'TRUPOCRYL A-20', 802, 878, 760, 416, 3, 6, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11502', 'TRUPOZYM AB', 750, 662, 949, 362, 3, 7, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74971', 'PASTOSOL MD', 742, 572, 826, 512, 3, 7, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('35870', 'SOLVOTAN TACTO', 999, 700, 521, 790, 3, 7, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4812', 'TRUPOFIN CERA HP', 904, 695, 663, 344, 3, 7, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74590', 'TRUPOSYL TBA', 1477, 775, 610, 728, 3, 7, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71909', 'TRUPOCRYL A-20', 1089, 787, 719, 995, 3, 7, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13591', 'TRUPOFIN CERA PW', 1331, 601, 611, 649, 3, 7, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57155', 'TRUPOFIN CERA 3A', 1044, 588, 825, 569, 3, 7, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59006', 'TRUPOCRYL A-20', 1233, 695, 892, 693, 3, 7, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94580', 'TRUPOSIST S-BO', 1225, 612, 753, 420, 3, 7, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12370', 'TRUPON BMF', 1010, 728, 637, 692, 3, 7, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('39623', 'TRUPOSIST D', 880, 853, 551, 865, 3, 7, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14617', 'TRUPOCRYL A-30', 694, 598, 780, 794, 3, 7, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52463', 'TRUPOCAL DE', 640, 778, 859, 828, 3, 7, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23256', 'TRUPOCRYL A-20', 731, 597, 690, 876, 3, 7, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12443', 'TRUPOL RK', 862, 800, 740, 878, 3, 7, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78351', 'TRUPONAT NF', 1114, 977, 881, 801, 3, 7, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('20987', 'TRUPOSYL TBK', 735, 801, 990, 801, 3, 7, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90612', 'SOLVOTAN TACTO', 706, 882, 711, 671, 3, 7, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5036', 'TRUPOZYM AB', 1466, 784, 582, 538, 3, 7, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53912', 'SOLVOTAN TACTO', 1325, 904, 771, 975, 3, 7, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63777', 'TRUPON CM', 543, 820, 850, 530, 3, 7, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('17676', 'TRUPOCAL DE', 582, 501, 752, 628, 3, 7, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73024', 'TRUPOCRYL A-30', 1483, 572, 669, 339, 3, 7, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('70285', 'TRUPOFIN CERA W', 606, 594, 544, 457, 3, 7, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96813', 'TRUPOCRYL AB', 672, 588, 669, 897, 3, 7, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71698', 'TRUPOSYL TBK-E', 1269, 947, 895, 391, 3, 7, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('14882', 'PASTOSOL F', 1417, 690, 554, 491, 3, 7, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('12984', 'TRUPOZYM AX', 1122, 510, 583, 534, 3, 7, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73827', 'ANTIFOAM NSP', 583, 923, 901, 430, 3, 7, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('58268', 'TRUPOSOL WBF', 982, 793, 746, 845, 3, 7, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43540', 'TRUPON DBS', 1291, 540, 949, 599, 3, 8, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74879', 'TRUPOZYM AB', 1357, 597, 857, 618, 3, 8, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48225', 'TRUPOCAL DE', 1195, 544, 890, 988, 3, 8, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('80470', 'TRUPOTAN OM', 1191, 931, 810, 779, 3, 8, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7511', 'TRUPOCRYL A-18', 1411, 597, 812, 534, 3, 8, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90585', 'TRUPOZYM CN', 580, 569, 749, 902, 3, 8, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37457', 'TRUPOZYM CL', 546, 564, 873, 935, 3, 8, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72619', 'TRUPOFIN CERA KT 08', 619, 765, 711, 774, 3, 8, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('76251', 'TRUPOSIST S-BO', 1474, 833, 982, 651, 3, 8, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18336', 'SOLVOTAN XS', 871, 516, 996, 650, 3, 8, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86636', 'TRUPOSEPT FP', 1294, 857, 914, 662, 3, 8, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83508', 'TRUPOCRYL A-18', 1202, 904, 570, 941, 3, 8, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57878', 'TRUPON COL', 774, 704, 671, 844, 3, 8, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62145', 'SOLVOTAN TACTO', 1075, 982, 776, 362, 3, 8, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('88914', 'TRUPON DBS', 966, 812, 980, 836, 3, 8, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40431', 'TRUPOFIN CERA K-A', 1477, 926, 595, 817, 3, 8, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90106', 'TRUPOCAL DE', 1321, 992, 643, 542, 3, 8, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62060', 'TRUPOFIN CERA P', 703, 875, 831, 884, 3, 8, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7125', 'TRUPOFIN CERA PW', 1463, 952, 794, 682, 3, 8, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52795', 'TRUPOSYL TBA', 890, 930, 746, 898, 3, 8, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32573', 'TRUPOZYM CH', 510, 530, 943, 792, 3, 8, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72838', 'TRUPOSYL TBK-E', 622, 855, 872, 746, 3, 8, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('93108', 'TRUPOFIN CERA CA', 1468, 728, 611, 431, 3, 8, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82954', 'TRUPON DBS', 1034, 804, 708, 758, 3, 8, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74672', 'TRUPOFIN CERA BT', 1271, 728, 733, 701, 4, 1, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('58486', 'TRUPOZYM AB', 820, 727, 766, 651, 4, 1, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16772', 'TRUPOWET SA', 1149, 765, 695, 372, 4, 1, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4482', 'TRUPOCRYL A-10', 1302, 785, 585, 863, 4, 1, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73903', 'TRUPOFIN CERA HF', 1478, 960, 882, 801, 4, 1, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('50640', 'TRUPOSYL ABS', 1457, 947, 753, 632, 4, 1, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74766', 'TRUPOFIN CERA BT', 879, 520, 917, 959, 4, 1, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22253', 'TRUPOCRYL K-U-10', 1360, 969, 775, 993, 4, 1, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('44626', 'TRUPOFIN CERA M', 715, 877, 998, 643, 4, 1, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('54797', 'TRUKALIN K', 1356, 661, 938, 837, 4, 1, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('54111', 'TRUPOWET PH', 884, 887, 938, 524, 4, 1, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31803', 'TRUPOCRYL K-U-10', 653, 637, 683, 666, 4, 1, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13753', 'TRUPOTAN OM', 528, 550, 755, 884, 4, 1, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72605', 'TRUPOFIN CERA HF', 597, 732, 680, 971, 4, 1, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('10579', 'TRUPOFIN CERA A', 599, 743, 836, 891, 4, 1, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92881', 'TRUPOZYM CN', 557, 950, 984, 708, 4, 1, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45407', 'TRUPOTEC T', 1016, 617, 787, 869, 4, 1, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7366', 'TRUPOFIN CERA HP', 1420, 756, 562, 901, 4, 1, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87472', 'TRUPOZYM AX', 686, 596, 515, 747, 4, 1, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29211', 'TRUPOCRYL A–90', 733, 508, 859, 679, 4, 1, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('76339', 'PASTOSOL MD', 1366, 700, 696, 965, 4, 1, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('88029', 'TRUPOFIN CERA FP', 797, 859, 683, 615, 4, 1, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46176', 'TRUPOSYL ABS', 827, 712, 869, 411, 4, 1, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('76569', 'TRUPOWET SA', 779, 515, 610, 552, 4, 1, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83628', 'TRUPOCRYL A-30', 1473, 664, 731, 735, 4, 1, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23623', 'TRUPOSEPT BA', 1144, 828, 940, 532, 4, 1, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('3843', 'TRUPON COL', 1455, 637, 543, 546, 4, 1, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1233', 'TRUKALIN K', 835, 616, 570, 935, 4, 2, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62995', 'TRUPON BMF', 767, 869, 803, 372, 4, 2, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59759', 'TRUPOSYL ABS', 1295, 905, 893, 812, 4, 2, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('17240', 'TRUPOL RK', 587, 778, 663, 800, 4, 2, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1352', 'TRUPOFIN CERA W', 656, 600, 511, 714, 4, 2, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9798', 'TRUPOSOL WBF', 659, 829, 970, 686, 4, 2, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18395', 'PASTOSOL DG', 941, 638, 896, 537, 4, 2, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5966', 'TRUPOFIN CERA HP', 849, 517, 829, 558, 4, 2, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98894', 'PASTOSOL KC', 1467, 838, 961, 822, 4, 2, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9082', 'TRUPOSOL WBF', 1255, 822, 738, 916, 4, 2, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('56055', 'TRUPOFIN CERA DP', 1494, 935, 594, 439, 4, 2, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16066', 'TRUPOFIN CERA KT 08', 646, 752, 705, 754, 4, 2, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29504', 'TRUPOTAN MON', 1021, 892, 812, 631, 4, 2, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18736', 'TRUPOCRYL A-18', 727, 897, 881, 466, 4, 2, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('68914', 'TRUPOFIN CERA KT 09', 1259, 542, 819, 444, 4, 2, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40596', 'TRUPOTEC S', 1018, 647, 586, 479, 4, 2, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('921', 'TRUPOCRYL A-32', 824, 611, 952, 852, 4, 2, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('17712', 'TRUPOCRYL K-U-310', 1336, 967, 832, 577, 4, 2, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86251', 'TRUPOCRYL A-10', 1472, 693, 505, 770, 4, 2, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82132', 'TRUPOZYM CB', 1072, 721, 987, 721, 4, 2, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63013', 'TRUPOCRYL K-A-30', 1034, 827, 889, 902, 4, 2, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4475', 'PASTOSOL F', 872, 837, 987, 345, 4, 2, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('57594', 'TRUPOCRYL A-28', 856, 544, 943, 960, 4, 2, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('93043', 'TRUPOSYL ABS', 522, 588, 753, 734, 4, 2, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71391', 'TRUPON PEM', 930, 522, 550, 629, 4, 2, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91217', 'PASTOSOL F', 959, 689, 559, 843, 4, 2, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81518', 'TRUPOCRYL K-U-10', 730, 865, 948, 470, 4, 2, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67543', 'TRUPOZYM CH', 1442, 669, 739, 516, 4, 2, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52805', 'TRUPOCRYL K-A-30', 990, 505, 999, 936, 4, 3, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81054', 'TRUPOCRYL A-18', 1073, 770, 608, 572, 4, 3, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('84790', 'TRUPOCAL AF', 545, 606, 553, 529, 4, 3, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59755', 'TRUPOFIN CERA E', 1195, 870, 904, 658, 4, 3, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('93258', 'TRUPOSYL TBD', 1468, 978, 897, 862, 4, 3, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21244', 'TRUPON CST', 1393, 516, 587, 594, 4, 3, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99058', 'TRUPOTAN OM', 1159, 738, 636, 677, 4, 3, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9706', 'TRUPOSOL WBF', 552, 870, 972, 900, 4, 3, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22702', 'TRUPOCRYL A-35', 1023, 967, 705, 739, 4, 3, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('26472', 'ANTIFOAM NSP', 918, 583, 975, 359, 4, 3, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('27088', 'TRUPOCRYL A–90', 681, 725, 823, 512, 4, 3, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24963', 'TRUPOFIN CERA D-80', 596, 720, 594, 922, 4, 3, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1782', 'TRUPOZYM CN', 954, 833, 795, 603, 4, 3, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53637', 'TRUPOTAN MON', 583, 985, 661, 520, 4, 3, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('46700', 'PASTOSOL F', 1362, 899, 927, 858, 4, 3, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24989', 'TRUPOSYL TBA', 781, 657, 876, 874, 4, 3, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98645', 'TRUPOCAL DE', 849, 572, 501, 345, 4, 3, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96160', 'ANTIFOAM NSP', 873, 847, 947, 360, 4, 3, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81981', 'TRUPOCRYL K-U-10', 1029, 925, 696, 599, 4, 3, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('680', 'TRUPON CM', 664, 882, 811, 702, 4, 3, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66267', 'TRUPOSIST D', 1454, 782, 587, 470, 4, 3, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('90813', 'TRUPOFIN CERA A', 517, 797, 716, 992, 4, 3, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37807', 'TRUPONAT NF', 616, 535, 612, 670, 4, 3, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19150', 'SOLVOTAN TACTO', 749, 928, 764, 400, 4, 3, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('97111', 'TRUPON CM', 524, 583, 602, 819, 4, 3, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('61367', 'TRUPOCRYL K-U-27', 1262, 867, 756, 777, 4, 3, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('38924', 'TRUKALIN K', 984, 728, 636, 864, 4, 3, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96999', 'TRUPOFIN CERA HP', 879, 573, 594, 573, 4, 3, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('54190', 'TRUPOFIN CERA E', 1150, 847, 941, 992, 4, 3, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91700', 'TRUPOCAL AF', 1201, 746, 797, 578, 4, 3, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95144', 'TRUPOSOL WBF', 614, 681, 624, 829, 4, 3, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32690', 'TRUPOCRYL A-35', 740, 541, 772, 667, 4, 3, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48118', 'TRUPOL RK', 1292, 850, 539, 505, 4, 4, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59775', 'TRUPOCRYL A-10', 1337, 931, 621, 906, 4, 4, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52578', 'TRUPOTAN MON', 988, 920, 537, 343, 4, 4, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16972', 'TRUPOCAL DE', 890, 687, 790, 418, 4, 4, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('48490', 'TRUPOFIN CERA SP', 775, 535, 989, 745, 4, 4, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53879', 'TRUPOFIN CERA K-A', 795, 508, 758, 926, 4, 4, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29121', 'TRUPOFIN CERA E', 1392, 653, 757, 705, 4, 4, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92254', 'TRUPOFIN CERA D-80', 659, 894, 592, 531, 4, 4, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('58981', 'TRUPOSLIP P', 1260, 694, 654, 426, 4, 4, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('55284', 'TRUPOWET PH', 1387, 831, 773, 662, 4, 4, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78323', 'TRUPON COL', 660, 569, 763, 338, 4, 4, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71096', 'TRUPOWET SA', 587, 708, 639, 748, 4, 4, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49448', 'TRUPOWET PH', 1015, 676, 515, 568, 4, 4, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('96309', 'TRUPOCRYL A-18', 1475, 917, 835, 378, 4, 4, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29968', 'TRUPOTEC S', 669, 547, 685, 735, 4, 4, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52580', 'TRUPOSYL TBA', 860, 622, 629, 621, 4, 4, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31023', 'TRUPOCRYL A–90', 516, 780, 690, 787, 4, 4, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('98632', 'TRUPOCRYL A-18', 718, 565, 870, 490, 4, 4, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1007', 'TRUPOSYL HBD', 1069, 564, 874, 983, 4, 4, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24541', 'TRUPON CST', 911, 950, 555, 592, 4, 4, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81484', 'TRUPOFIN CERA HF', 1029, 822, 630, 979, 4, 4, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('79081', 'PASTOSOL DG', 1095, 670, 666, 964, 4, 4, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62717', 'TRUPOSEPT BA', 1317, 766, 526, 436, 4, 4, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67839', 'TRUKALIN K', 1052, 726, 951, 341, 4, 4, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5626', 'TRUPON CM', 991, 817, 685, 444, 4, 4, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36091', 'PASTOSOL MD', 1366, 595, 977, 329, 4, 4, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87398', 'PASTOSOL MD', 1413, 764, 782, 540, 4, 4, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('38006', 'PASTOSOL HW', 1487, 742, 802, 383, 4, 4, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43430', 'TRUPOZYM CL', 1221, 549, 826, 998, 4, 4, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('80109', 'TRUPOFIN CERA FP', 548, 806, 953, 566, 4, 4, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('50017', 'PASTOSOL DG', 1193, 664, 611, 701, 4, 4, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78994', 'TRUPOFIN CERA A', 1111, 569, 825, 796, 4, 5, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('3764', 'TRUPOFIN CERA KT 09', 1334, 921, 507, 485, 4, 5, 2, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('85908', 'TRUPOCRYL K-U-27', 603, 656, 532, 967, 4, 5, 3, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87526', 'TRUPOFIN CERA KT 09', 748, 750, 876, 517, 4, 5, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40261', 'TRUPOCRYL A-32', 673, 900, 573, 397, 4, 5, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('17233', 'TRUPOSOL WBF', 1362, 888, 591, 538, 4, 5, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40166', 'TRUPOSEPT BA', 591, 625, 920, 747, 4, 5, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11532', 'TRUPOFIN CERA BT', 1295, 541, 660, 429, 4, 5, 8, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('50246', 'TRUPOCRYL A-30', 643, 684, 749, 798, 4, 5, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('53495', 'TRUPOCRYL A-28', 609, 785, 846, 533, 4, 5, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('13202', 'TRUPOFIN CERA HF', 612, 555, 670, 930, 4, 5, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('79594', 'TRUPON CM', 1088, 662, 989, 724, 4, 5, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67064', 'TRUPON DBS', 530, 813, 506, 442, 4, 5, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('41860', 'TRUPOCAL AF', 1115, 513, 748, 352, 4, 5, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('60039', 'ANTIFOAM NSP', 877, 629, 516, 931, 4, 5, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('82368', 'TRUPOTEC T', 1374, 866, 620, 914, 4, 5, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('97549', 'TRUPOZYM AB', 702, 520, 691, 389, 4, 5, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95360', 'TRUPON COL', 1047, 907, 594, 978, 4, 5, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('29135', 'TRUPOZYM CB', 1269, 605, 584, 668, 4, 5, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11484', 'PASTOSOL F', 1196, 965, 735, 983, 4, 5, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19849', 'TRUPOFIN CERA E', 1191, 709, 827, 331, 4, 5, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22046', 'PASTOSOL KC', 735, 733, 659, 988, 4, 5, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81845', 'PASTOSOL BCN', 1338, 883, 960, 775, 4, 5, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45644', 'TRUPOFIN CERA CA', 957, 639, 520, 427, 4, 5, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('21949', 'TRUPON CST', 649, 834, 823, 446, 4, 5, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7158', 'TRUPOFIN CERA PV', 1277, 611, 630, 884, 4, 5, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('11224', 'TRUPOCRYL AB', 1183, 746, 503, 329, 4, 5, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52471', 'TRUPOFIN CERA M', 903, 724, 648, 457, 4, 6, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63119', 'TRUPOCRYL A-35', 948, 736, 730, 707, 4, 6, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31571', 'TRUPOTEC T', 1118, 654, 543, 981, 4, 6, 6, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('73998', 'TRUPOFIN CERA BT', 980, 546, 608, 978, 4, 6, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('55546', 'TRUPOFIN CERA K-A', 1399, 839, 913, 524, 4, 6, 7, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('43533', 'TRUKALIN K', 570, 857, 880, 769, 4, 6, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31492', 'TRUPOFIN CERA BT', 1179, 987, 638, 718, 4, 6, 8, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95212', 'TRUPOZYM CN', 985, 784, 634, 474, 4, 6, 9, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('37835', 'PASTOSOL BCN', 1344, 528, 768, 877, 4, 6, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('95424', 'TRUPOCRYL K-A-30', 726, 884, 900, 767, 4, 6, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87319', 'TRUPOCRYL K-U-10', 667, 983, 547, 828, 4, 6, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('83127', 'TRUPOCRYL K-U-10', 1175, 532, 515, 907, 4, 6, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('15437', 'TRUPOFIN CERA HP', 870, 609, 657, 305, 4, 6, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('27431', 'TRUPOSYL TBK', 1191, 756, 503, 812, 4, 6, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71738', 'TRUPOFIN CERA A', 803, 766, 513, 422, 4, 6, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('32372', 'PASTOSOL DG', 1183, 992, 557, 797, 4, 6, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('85000', 'SOLVOTAN TACTO', 956, 680, 929, 365, 4, 6, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('63733', 'TRUPON CST', 1468, 694, 893, 606, 4, 6, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('9463', 'PASTOSOL F', 705, 700, 925, 448, 4, 6, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('99598', 'TRUPOFIN CERA D-80', 1077, 526, 740, 497, 4, 6, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('3884', 'TRUPOTAN MOW', 1251, 929, 972, 667, 4, 6, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('23852', 'TRUPOCRYL A-18', 612, 797, 570, 789, 4, 6, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('68086', 'TRUPOSYL HBD', 963, 560, 848, 792, 4, 6, 18, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('58657', 'SOLVOTAN XS', 1487, 900, 844, 327, 4, 6, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24323', 'TRUPOFIN CERA BT', 1073, 595, 699, 729, 4, 6, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('64093', 'TRUPON COL', 634, 669, 525, 671, 4, 6, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('52705', 'TRUPOSYL HBD', 618, 699, 647, 346, 4, 6, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('42197', 'TRUPON COL', 1419, 693, 840, 385, 4, 6, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7763', 'SOLVOTAN XS', 696, 773, 757, 346, 4, 6, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('85423', 'TRUPOFIN CERA HP', 801, 781, 797, 956, 4, 6, 22, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72756', 'TRUKALIN K', 1193, 780, 772, 733, 4, 6, 23, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('86054', 'PASTOSOL MD', 1243, 690, 788, 357, 4, 6, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('18755', 'TRUPON DBS', 1062, 557, 759, 620, 4, 7, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('45399', 'TRUPOFIN CERA E', 1382, 929, 657, 700, 4, 7, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16111', 'TRUPOZYM AB', 1466, 722, 564, 655, 4, 7, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('78529', 'TRUPON COL', 1046, 592, 912, 666, 4, 7, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('7829', 'TRUPOZYM CB', 652, 649, 647, 902, 4, 7, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('69160', 'TRUPOCRYL A-18', 908, 668, 979, 904, 4, 7, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72240', 'TRUPOSIST D', 530, 866, 973, 994, 4, 7, 7, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('44046', 'SOLVOTAN XS', 701, 689, 864, 585, 4, 7, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('49612', 'TRUPOFIN CERA K-A', 890, 893, 566, 657, 4, 7, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75418', 'TRUPOSLIP P', 1427, 724, 593, 664, 4, 7, 12, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('24550', 'TRUPOSYL TBK', 1446, 926, 811, 607, 4, 7, 13, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('97795', 'TRUPOSYL TBA', 984, 791, 594, 866, 4, 7, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31589', 'TRUPOSYL ABS', 835, 989, 999, 706, 4, 7, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62884', 'TRUPOSIST S-BO', 908, 989, 520, 313, 4, 7, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('20584', 'TRUKALIN K', 616, 859, 775, 947, 4, 7, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81557', 'TRUPOCRYL A-18', 761, 554, 769, 740, 4, 7, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('31537', 'TRUPOFIN CERA CA', 1192, 912, 810, 630, 4, 7, 17, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('67921', 'TRUPONAT LA', 693, 673, 532, 732, 4, 7, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('25195', 'TRUPON DBS', 1354, 567, 739, 609, 4, 7, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('4269', 'PASTOSOL F', 1455, 691, 962, 584, 4, 7, 19, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19830', 'TRUKALIN K', 1344, 568, 857, 379, 4, 7, 19, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('87560', 'TRUPOCRYL A-30', 856, 877, 989, 526, 4, 7, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91716', 'TRUPOCAL AF', 1371, 550, 831, 723, 4, 7, 20, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('72781', 'TRUPOFIN CERA 3A', 558, 617, 669, 528, 4, 7, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('89229', 'TRUPOCRYL K-U-10', 1235, 748, 513, 645, 4, 7, 23, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('66501', 'PASTOSOL BCN', 595, 635, 614, 818, 4, 7, 24, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('6427', 'TRUPOZYM AB', 546, 847, 879, 774, 4, 7, 24, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5474', 'TRUPOTEC T', 745, 803, 630, 686, 4, 8, 1, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('91815', 'TRUPOFIN CERA P', 927, 652, 751, 601, 4, 8, 1, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40913', 'PASTOSOL HW', 802, 773, 518, 759, 4, 8, 2, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('71895', 'TRUPOCAL DE', 1186, 991, 627, 830, 4, 8, 3, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('75629', 'TRUPOFIN CERA CA', 1134, 849, 749, 834, 4, 8, 4, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('30466', 'TRUPOFIN CERA PW', 1071, 805, 684, 755, 4, 8, 4, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('89855', 'TRUPOSEPT BA', 1142, 969, 595, 400, 4, 8, 5, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('22652', 'TRUPOTEC T', 1390, 709, 991, 946, 4, 8, 5, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('62404', 'TRUPOFIN CERA E', 930, 918, 843, 564, 4, 8, 6, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('81970', 'PASTOSOL KC', 704, 798, 882, 980, 4, 8, 9, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5877', 'TRUPOSYL HBD', 877, 921, 639, 618, 4, 8, 10, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('957', 'TRUPOSIST S-BO', 998, 634, 680, 376, 4, 8, 10, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('1692', 'TRUPOFIN CERA HP', 728, 673, 765, 402, 4, 8, 11, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('51242', 'TRUPOCRYL A-20', 819, 842, 859, 864, 4, 8, 11, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('28616', 'TRUPOCRYL A-30', 926, 685, 849, 941, 4, 8, 12, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('47573', 'TRUPOCAL AF', 1422, 851, 622, 439, 4, 8, 13, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('55088', 'TRUPOFIN CERA KT 09', 725, 714, 949, 609, 4, 8, 14, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('40157', 'TRUPOFIN CERA A', 654, 578, 722, 572, 4, 8, 14, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('64302', 'TRUPOCRYL A-20', 1429, 900, 519, 781, 4, 8, 15, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('5430', 'TRUPONAT LA', 1226, 660, 901, 897, 4, 8, 15, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('19915', 'TRUPOZYM CB', 686, 644, 812, 513, 4, 8, 16, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('16008', 'TRUPOSYL HBD', 1392, 707, 892, 315, 4, 8, 16, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('36448', 'TRUPOCRYL A-20', 1378, 779, 700, 631, 4, 8, 17, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('94677', 'TRUPOSEPT BA', 833, 512, 695, 585, 4, 8, 18, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('74333', 'TRUKALIN K', 1131, 600, 832, 408, 4, 8, 20, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('2059', 'TRUPOFIN CERA 3A', 1433, 526, 737, 456, 4, 8, 21, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('92731', 'TRUPON PEM', 1484, 795, 971, 984, 4, 8, 21, false);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('59211', 'TRUPON DBS', 692, 734, 677, 914, 4, 8, 22, true);
INSERT INTO palets (identificador, id_producto, alto, ancho, largo, cantidad_de_producto, estanteria, balda, posicion, delante) VALUES ('6944', 'TRUPOCAL DE', 1349, 982, 966, 916, 4, 8, 23, true);


    
INSERT INTO clientes (nombre, direccion, telefono, email) VALUES
('Cliente A', 'Calle Falsa 123, Madrid', '600111222', 'cliente.a@example.com'),
('Cliente B', 'Av. Siempre Viva 456, Barcelona', '600333444', 'cliente.b@example.com'),
('Cliente C', 'Paseo Marítimo 789, Valencia', '600555666', 'cliente.c@example.com'),
('Cliente D', 'Rúa Central 10, Vigo', '600777888', 'cliente.d@example.com'),
('Cliente E', 'Calle Mayor 20, Sevilla', '600999000', 'cliente.e@example.com'),
('Cliente F', 'C/ de la Luna 1, Granada', '601111222', 'cliente.f@example.com'),
('Cliente G', 'Plaza Mayor 2, Oviedo', '601333444', 'cliente.g@example.com'),
('Cliente H', 'Av. de la Reina 3, Toledo', '601555666', 'cliente.h@example.com'),
('Cliente I', 'Camino Real 4, Bilbao', '601777888', 'cliente.i@example.com'),
('Cliente J', 'Calle Nueva 5, Málaga', '601999000', 'cliente.j@example.com'),
('Cliente K', 'C/ del Sol 6, Zaragoza', '602111222', 'cliente.k@example.com'),
('Cliente L', 'Av. de América 7, Murcia', '602333444', 'cliente.l@example.com'),
('Cliente M', 'Plaza del Pilar 8, Córdoba', '602555666', 'cliente.m@example.com'),
('Cliente N', 'Calle del Río 9, Valladolid', '602777888', 'cliente.n@example.com'),
('Cliente O', 'Gran Vía 11, Salamanca', '602999000', 'cliente.o@example.com'),
('Cliente P', 'Av. Libertad 12, Pamplona', '603111222', 'cliente.p@example.com'),
('Cliente Q', 'C/ Cartagena 13, Alicante', '603333444', 'cliente.q@example.com'),
('Cliente R', 'Plaza de España 14, Albacete', '603555666', 'cliente.r@example.com'),
('Cliente S', 'Calle las Flores 15, Cádiz', '603777888', 'cliente.s@example.com'),
('Cliente T', 'Av. Andalucía 16, Almería', '603999000', 'cliente.t@example.com'),
('Cliente U', 'Rúa do Sol 17, Lugo', '604111222', 'cliente.u@example.com'),
('Cliente V', 'Calle La Paz 18, Logroño', '604333444', 'cliente.v@example.com'),
('Cliente W', 'Av. Cataluña 19, Pamplona', '604555666', 'cliente.w@example.com'),
('Cliente X', 'Plaza Constitución 21, Santander', '604777888', 'cliente.x@example.com'),
('Cliente Y', 'C/ Nueva 22, Burgos', '604999000', 'cliente.y@example.com'),
('Cliente Z', 'Av. Goya 23, San Sebastián', '605111222', 'cliente.z@example.com'),
('Cliente AA', 'Calle Real 24, León', '605333444', 'cliente.aa@example.com'),
('Cliente AB', 'Av. Asturias 25, Oviedo', '605555666', 'cliente.ab@example.com'),
('Cliente AC', 'Plaza del Mercado 26, Toledo', '605777888', 'cliente.ac@example.com'),
('Cliente AD', 'Calle Sorolla 27, Valencia', '605999000', 'cliente.ad@example.com');

INSERT INTO pedidos (id_usuario, id_cliente, estado, fecha_entrega, hora_salida) VALUES
(NULL, 1, 'Pendiente', '2025-08-10', NULL),
(NULL, 2, 'Pendiente', '2025-08-12', NULL),
(NULL, 3, 'Pendiente', '2025-08-14', NULL),
(NULL, 4, 'Pendiente', '2025-08-16', NULL),
(NULL, 5, 'Pendiente', '2025-08-18', NULL),

(1, 1, 'En proceso', '2025-08-11', 'primera_hora'),
(2, 2, 'En proceso', '2025-08-13', 'segunda_hora'),
(3, 3, 'En proceso', '2025-08-15', 'primera_hora'),
(4, 4, 'En proceso', '2025-08-17', 'segunda_hora'),
(5, 5, 'En proceso', '2025-08-19', 'primera_hora'),

(1, 1, 'Completado', '2025-07-01', NULL),
(2, 2, 'Completado', '2025-07-02', NULL),
(3, 3, 'Completado', '2025-07-03', NULL),
(4, 4, 'Completado', '2025-07-04', NULL),
(5, 5, 'Completado', '2025-07-05', NULL),

(1, 1, 'Cancelado', '2025-06-01', NULL),
(2, 2, 'Cancelado', '2025-06-02', NULL),
(3, 3, 'Cancelado', '2025-06-03', NULL),
(4, 4, 'Cancelado', '2025-06-04', NULL),
(5, 5, 'Cancelado', '2025-06-05', NULL),

(NULL, 1, 'Pendiente', '2025-08-20', NULL),
(NULL, 2, 'Pendiente', '2025-08-21', NULL),
(NULL, 3, 'Pendiente', '2025-08-22', NULL),
(NULL, 4, 'Pendiente', '2025-08-23', NULL),
(NULL, 5, 'Pendiente', '2025-08-24', NULL),

(2, 1, 'En proceso', '2025-08-25', 'segunda_hora'),
(3, 2, 'En proceso', '2025-08-26', 'primera_hora'),
(4, 3, 'En proceso', '2025-08-27', 'segunda_hora'),
(5, 4, 'En proceso', '2025-08-28', 'primera_hora'),
(1, 5, 'En proceso', '2025-08-29', 'segunda_hora'),

(2, 1, 'Completado', '2025-07-10', NULL),
(3, 2, 'Completado', '2025-07-11', NULL),
(4, 3, 'Completado', '2025-07-12', NULL),
(5, 4, 'Completado', '2025-07-13', NULL),
(1, 5, 'Completado', '2025-07-14', NULL),

(2, 1, 'Cancelado', '2025-06-10', NULL),
(3, 2, 'Cancelado', '2025-06-11', NULL),
(4, 3, 'Cancelado', '2025-06-12', NULL),
(5, 4, 'Cancelado', '2025-06-13', NULL),
(1, 5, 'Cancelado', '2025-06-14', NULL),

(NULL, 1, 'Pendiente', '2025-08-30', NULL),
(NULL, 2, 'Pendiente', '2025-08-31', NULL),
(NULL, 3, 'Pendiente', '2025-09-01', NULL),
(NULL, 4, 'Pendiente', '2025-09-02', NULL),
(NULL, 5, 'Pendiente', '2025-09-03', NULL);



-- insertamos los productos en cada pedido:

INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 18, 30, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 68, 14, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 23, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 13, 42, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 25, 20, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 33, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 32, 85, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 12, 82, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 63, 42, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (1, 60, 42, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (2, 9, 22, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (2, 46, 56, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (2, 27, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (2, 77, 74, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (2, 75, 62, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (2, 12, 68, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 76, 30, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 23, 20, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 53, 94, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 60, 43, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 18, 90, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 14, 27, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 34, 82, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 7, 40, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 44, 56, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 6, 66, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 13, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 9, 23, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (3, 80, 37, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 25, 29, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 42, 100, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 62, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 33, 84, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 78, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 14, 62, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 70, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 65, 14, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 19, 31, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (4, 63, 30, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 29, 62, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 23, 53, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 46, 79, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 47, 44, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 49, 81, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 15, 53, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 68, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 25, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 44, 80, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 63, 43, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 51, 17, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (5, 71, 87, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 41, 53, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 64, 26, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 49, 88, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 48, 25, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 28, 48, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 50, 44, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 78, 80, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 31, 94, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 30, 64, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 45, 66, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 20, 60, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (6, 73, 77, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 65, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 67, 91, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 3, 68, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 12, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 71, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 63, 37, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 54, 13, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (7, 11, 14, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 78, 89, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 80, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 49, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 39, 67, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 12, 78, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 21, 76, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 55, 69, TRUE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (8, 1, 36, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (9, 15, 92, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (9, 42, 76, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (9, 47, 72, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (9, 53, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (9, 74, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (9, 10, 79, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (10, 71, 29, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (10, 65, 84, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (10, 44, 78, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (10, 15, 21, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (10, 39, 27, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (10, 46, 43, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (10, 45, 45, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 11, 14, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 51, 86, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 46, 69, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 27, 98, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 78, 96, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 19, 36, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 60, 63, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 15, 85, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 79, 41, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 12, 71, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 47, 68, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 9, 75, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 38, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (11, 49, 15, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 72, 15, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 58, 13, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 71, 94, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 38, 81, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 3, 14, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 25, 82, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 2, 47, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 56, 75, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 24, 85, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (12, 63, 98, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 80, 27, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 59, 76, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 10, 97, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 28, 86, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 52, 100, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 8, 28, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 36, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 75, 86, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 14, 74, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 9, 69, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 2, 98, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 43, 25, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 74, 92, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 39, 70, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (13, 27, 20, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 61, 22, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 20, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 80, 96, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 13, 56, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 34, 15, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 48, 15, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 22, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 24, 82, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 35, 52, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (14, 46, 59, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 70, 96, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 46, 81, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 11, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 71, 75, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 69, 54, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 32, 90, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 15, 63, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 5, 21, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 55, 36, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 35, 51, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 25, 75, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (15, 44, 73, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 58, 71, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 31, 24, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 68, 36, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 57, 20, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 29, 15, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 52, 73, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 34, 33, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 70, 65, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 19, 50, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 51, 33, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 69, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 8, 96, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 43, 59, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 54, 62, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (16, 22, 74, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (17, 50, 71, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (17, 77, 30, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (17, 16, 77, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (17, 71, 28, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (17, 40, 70, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (17, 19, 56, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 15, 96, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 23, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 47, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 21, 73, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 60, 59, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 20, 69, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 37, 78, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 46, 53, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 31, 55, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 63, 61, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 55, 17, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (18, 53, 79, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 31, 42, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 70, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 32, 22, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 52, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 38, 98, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 53, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 10, 46, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 54, 93, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 55, 89, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (19, 45, 84, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 5, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 19, 91, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 67, 24, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 24, 50, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 71, 46, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 64, 28, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 43, 93, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 49, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 11, 62, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (20, 40, 85, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (21, 32, 71, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (21, 52, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (21, 42, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (21, 50, 94, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (21, 61, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (21, 3, 18, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 2, 72, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 28, 58, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 39, 40, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 11, 26, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 66, 41, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 69, 17, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 52, 45, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 27, 54, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 3, 78, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 65, 17, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (22, 79, 65, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 32, 72, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 75, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 25, 44, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 8, 31, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 26, 94, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 62, 72, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 18, 95, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 17, 90, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 13, 65, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 43, 39, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 34, 100, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (23, 50, 40, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 77, 37, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 34, 89, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 24, 48, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 5, 11, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 73, 73, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 69, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 37, 31, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 20, 89, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 9, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 31, 93, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 47, 61, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 65, 11, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 6, 27, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (24, 70, 97, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 54, 82, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 69, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 38, 53, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 23, 72, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 11, 84, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 76, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 74, 35, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 16, 76, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 41, 94, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 53, 15, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 12, 53, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 32, 98, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (25, 34, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (26, 57, 79, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (26, 27, 84, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (26, 73, 56, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (26, 44, 95, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (26, 28, 12, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (27, 19, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (27, 36, 54, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (27, 48, 12, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (27, 28, 58, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (27, 45, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (27, 65, 11, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (27, 23, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 20, 85, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 65, 92, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 50, 94, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 69, 99, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 67, 100, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 29, 71, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 41, 58, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 62, 19, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 76, 45, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (28, 34, 37, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 6, 44, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 72, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 43, 42, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 5, 86, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 66, 99, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 27, 89, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 13, 92, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 54, 61, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 35, 42, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (29, 37, 51, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 54, 53, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 29, 20, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 10, 95, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 51, 74, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 16, 59, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 37, 51, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 63, 93, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 18, 15, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 26, 26, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 65, 55, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 73, 65, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (30, 72, 22, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (31, 72, 54, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (31, 26, 28, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (31, 43, 55, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (31, 80, 90, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (31, 27, 21, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (31, 76, 51, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (31, 57, 70, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 6, 30, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 51, 88, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 15, 35, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 79, 25, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 31, 44, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 19, 54, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 78, 60, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 45, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 73, 28, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 68, 58, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 65, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 5, 73, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (32, 62, 17, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (33, 22, 79, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (33, 17, 65, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (33, 51, 87, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (33, 30, 16, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (33, 6, 39, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 68, 55, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 43, 37, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 50, 74, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 25, 88, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 14, 97, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 40, 41, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 35, 14, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 71, 35, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 24, 91, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 61, 99, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 2, 55, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 8, 47, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 70, 90, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 42, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (34, 16, 99, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 51, 79, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 54, 46, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 52, 49, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 6, 90, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 40, 47, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 9, 93, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 48, 91, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 2, 60, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 44, 17, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (35, 45, 65, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 48, 37, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 67, 47, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 43, 33, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 80, 52, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 49, 18, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 57, 59, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 64, 30, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 50, 96, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 9, 82, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 11, 47, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 6, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 60, 29, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (36, 47, 58, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 12, 40, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 53, 60, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 8, 33, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 75, 52, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 58, 59, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 10, 83, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 44, 38, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 42, 66, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 52, 52, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 22, 31, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 64, 78, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 29, 81, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 45, 70, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 19, 46, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (37, 67, 86, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 23, 20, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 16, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 67, 65, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 66, 50, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 39, 19, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 64, 10, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 73, 27, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 80, 19, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (38, 15, 81, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 49, 61, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 66, 93, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 79, 12, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 37, 14, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 56, 41, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 41, 63, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 16, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 63, 17, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 1, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (39, 72, 57, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 67, 34, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 37, 63, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 21, 21, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 75, 25, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 53, 46, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 47, 25, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 36, 19, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 22, 54, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 3, 24, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 61, 92, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 57, 71, FALSE);
INSERT INTO detalles_pedido (id_pedido, id_producto, cantidad, estado_producto_pedido) VALUES (40, 42, 90, FALSE);