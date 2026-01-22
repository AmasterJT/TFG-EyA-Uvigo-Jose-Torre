create database tfg_almacenDB;

use tfg_almacenDB;

CREATE TABLE
    transportistas (
        id_transportista INT PRIMARY KEY AUTO_INCREMENT,
        nombre_empresa VARCHAR(150) NOT NULL,
        nombre_conductor VARCHAR(150) NOT NULL,
        telefono VARCHAR(20),
        email VARCHAR(120),
        matricula VARCHAR(15),
        tipo_transporte VARCHAR(50),
        direccion VARCHAR(200),
        nif_cif VARCHAR(20),
        notas TEXT,
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

INSERT INTO
    transportistas (
        nombre_empresa,
        nombre_conductor,
        telefono,
        email,
        matricula,
        tipo_transporte,
        direccion,
        nif_cif,
        notas
    )
VALUES
    (
        'Transporte Norte S.L.',
        'Juan Pérez',
        '600111111',
        'juan@transnorte.es',
        '1234-ABC',
        'Camión rígido',
        'Polígono Industrial Asipo, Oviedo',
        'B12345678',
        'Transporte regional Asturias'
    ),
    (
        'Logística Atlántica',
        'María López',
        '600222222',
        'maria@logiatlantica.com',
        '2345-BCD',
        'Tráiler',
        'Puerto de Vigo, Vigo',
        'A23456789',
        'Especializada en transporte marítimo'
    ),
    (
        'Rápidos del Sur',
        'Antonio García',
        '600333333',
        'antonio@rapidosur.es',
        '3456-CDE',
        'Furgón',
        'Av. Andalucía 45, Sevilla',
        'B34567890',
        'Reparto urgente'
    ),
    (
        'Cargo Express Levante',
        'Laura Martínez',
        '600444444',
        'laura@cargoexpress.es',
        '4567-DEF',
        'Camión frigorífico',
        'C/ Puerto 12, Valencia',
        'A45678901',
        'Transporte refrigerado'
    ),
    (
        'Transportes Meseta',
        'Carlos Sánchez',
        '600555555',
        'carlos@tmeseta.com',
        '5678-EFG',
        'Camión lona',
        'Polígono Industrial, Valladolid',
        'B56789012',
        'Carga general'
    ),
    (
        'Distribuciones Costa',
        'Elena Ruiz',
        '600666666',
        'elena@distcosta.es',
        '6789-FGH',
        'Furgón',
        'Av. del Mediterráneo 3, Málaga',
        'A67890123',
        'Distribución local'
    ),
    (
        'Logística Central',
        'Pedro Gómez',
        '600777777',
        'pedro@logicentral.com',
        '7890-GHI',
        'Tráiler',
        'Calle Mayor 50, Madrid',
        'B78901234',
        'Rutas nacionales'
    ),
    (
        'TransAlpes Europa',
        'Sergio Navarro',
        '600888888',
        'sergio@transalpes.eu',
        '8901-HIJ',
        'Tráiler',
        'Zona Franca, Barcelona',
        'A89012345',
        'Transporte internacional'
    ),
    (
        'EcoTrans Galicia',
        'Ana Varela',
        '600999999',
        'ana@ecotransgalicia.es',
        '9012-IJK',
        'Camión eléctrico',
        'Polígono do Tambre, Santiago',
        'B90123456',
        'Transporte sostenible'
    ),
    (
        'FastDelivery Norte',
        'Miguel Castro',
        '601000000',
        'miguel@fastnorth.com',
        '0123-JKL',
        'Furgón',
        'Parque Empresarial, Santander',
        'A01234567',
        'Última milla'
    );

CREATE TABLE
    proveedores (
        id_proveedor INT PRIMARY KEY AUTO_INCREMENT,
        nombre VARCHAR(150) NOT NULL,
        direccion VARCHAR(200),
        telefono VARCHAR(20),
        email VARCHAR(100) UNIQUE,
        nif_cif VARCHAR(20) UNIQUE,
        contacto VARCHAR(100),
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE
    permisos_usuarios (
        id_permiso INT PRIMARY KEY AUTO_INCREMENT,
        permiso VARCHAR(50) NOT NULL,
        descripcion TEXT
    );

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

CREATE TABLE
    roles (
        id_rol INT PRIMARY KEY AUTO_INCREMENT,
        nombre_rol VARCHAR(20) NOT NULL,
        descripcion TEXT
    );

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
        'Operario',
        'Trabajador encargado de tareas específicas operativas.'
    ),
    (
        'Administración',
        'Gestión administrativa y documentación.'
    );

CREATE TABLE
    rol_permiso (
        id_rol INT,
        id_permiso INT,
        estado ENUM ('activo', 'inactivo', 'ver') NOT NULL,
        PRIMARY KEY (id_rol, id_permiso),
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol),
        FOREIGN KEY (id_permiso) REFERENCES permisos_usuarios (id_permiso)
    );

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 1, 'activo'),
    (2, 1, 'inactivo'),
    (3, 1, 'inactivo'),
    (4, 1, 'inactivo');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 2, 'activo'),
    (2, 2, 'activo'),
    (3, 2, 'inactivo'),
    (4, 2, 'inactivo');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 3, 'activo'),
    (2, 3, 'activo'),
    (3, 3, 'ver'),
    (4, 3, 'ver');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 4, 'activo'),
    (2, 4, 'activo'),
    (3, 4, 'activo'),
    (4, 4, 'inactivo');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 5, 'activo'),
    (2, 5, 'activo'),
    (3, 5, 'activo'),
    (4, 5, 'ver');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 6, 'activo'),
    (2, 6, 'activo'),
    (3, 6, 'inactivo'),
    (4, 6, 'activo');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 7, 'activo'),
    (2, 7, 'activo'),
    (3, 7, 'inactivo'),
    (4, 7, 'inactivo');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 8, 'activo'),
    (2, 8, 'activo'),
    (3, 8, 'ver'),
    (4, 8, 'ver');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 9, 'activo'),
    (2, 9, 'inactivo'),
    (3, 9, 'inactivo'),
    (4, 9, 'inactivo');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 10, 'activo'),
    (2, 10, 'inactivo'),
    (3, 10, 'inactivo'),
    (4, 10, 'activo');

INSERT INTO
    rol_permiso (id_rol, id_permiso, estado)
VALUES
    (1, 11, 'activo'),
    (2, 11, 'inactivo'),
    (3, 11, 'ver'),
    (4, 11, 'inactivo');

CREATE TABLE
    usuarios (
        id_usuario INT PRIMARY KEY AUTO_INCREMENT,
        user_name VARCHAR(100) NOT NULL,
        nombre VARCHAR(100) NOT NULL,
        apellido1 VARCHAR(100) NOT NULL,
        apellido2 VARCHAR(100) NOT NULL,
        email VARCHAR(100) NOT NULL UNIQUE,
        contraseña VARCHAR(255) NOT NULL,
        id_rol INT,
        activo INT,
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_rol) REFERENCES roles (id_rol) ON DELETE CASCADE ON UPDATE CASCADE
    );

CREATE TABLE
    clientes (
        id_cliente INT PRIMARY KEY AUTO_INCREMENT,
        nombre VARCHAR(100),
        direccion VARCHAR(200),
        telefono VARCHAR(20),
        email VARCHAR(100) UNIQUE,
        latitud DECIMAL(9, 6),
        longitud DECIMAL(9, 6),
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE
    tipos (
        id_tipo VARCHAR(50) PRIMARY KEY,
        color VARCHAR(20) NOT NULL
    );

CREATE TABLE
    productos (
        id_producto INT PRIMARY KEY AUTO_INCREMENT,
        identificador_producto VARCHAR(100) NOT NULL UNIQUE,
        tipo_producto VARCHAR(50) NOT NULL,
        descripcion TEXT,
        precio DECIMAL(10, 2),
        fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (tipo_producto) REFERENCES tipos (id_tipo)
    );

CREATE TABLE
    proveedor_producto (
        id_proveedor INT NOT NULL,
        id_producto INT NOT NULL,
        alto INT NOT NULL,
        ancho INT NOT NULL,
        largo INT NOT NULL,
        precio DECIMAL(10, 2) NULL,
        unidades_por_palet_default INT NOT NULL DEFAULT 1,
        PRIMARY KEY (id_proveedor, id_producto),
        FOREIGN KEY (id_proveedor) REFERENCES proveedores (id_proveedor) ON UPDATE CASCADE ON DELETE CASCADE,
        FOREIGN KEY (id_producto) REFERENCES productos (id_producto) ON UPDATE CASCADE ON DELETE CASCADE,
        CONSTRAINT chk_unidades_por_palet CHECK (unidades_por_palet_default > 0)
    );

CREATE TABLE
    palets (
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
        FOREIGN KEY (id_producto) REFERENCES productos (identificador_producto)
    );

CREATE TABLE
    movimientos (
        id_movimiento INT PRIMARY KEY AUTO_INCREMENT,
        id_usuario INT,
        id_palet INT,
        tipo_movimiento ENUM (
            'Cambio de locacion del palet',
            'Registrar nuevo Palet',
            'Eliminar Palet'
        ) NOT NULL,
        fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        observaciones TEXT,
        FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario),
        FOREIGN KEY (id_palet) REFERENCES palets (id_palet)
    );

CREATE TABLE
    pedidos (
        id_pedido INT PRIMARY KEY AUTO_INCREMENT,
        codigo_referencia VARCHAR(30) NOT NULL UNIQUE,
        id_cliente INT NOT NULL,
        id_usuario INT NULL,
        id_transportista INT NULL,
        estado VARCHAR(20) NOT NULL DEFAULT 'Pendiente',
        fecha_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        fecha_entrega DATE NOT NULL,
        hora_salida ENUM ('primera_hora', 'segunda_hora') NULL,
        palets_del_pedido INT NOT NULL DEFAULT 0,
        enviado TINYINT (1) NOT NULL DEFAULT 0,
        CONSTRAINT fk_pedidos_clientes FOREIGN KEY (id_cliente) REFERENCES clientes (id_cliente) ON UPDATE CASCADE ON DELETE RESTRICT,
        CONSTRAINT fk_pedidos_usuarios FOREIGN KEY (id_usuario) REFERENCES usuarios (id_usuario) ON UPDATE CASCADE ON DELETE SET NULL,
        CONSTRAINT fk_pedidos_transportistas FOREIGN KEY (id_transportista) REFERENCES transportistas (id_transportista) ON UPDATE CASCADE ON DELETE SET NULL
    );

CREATE TABLE
    detalles_pedido (
        id_detalle INT AUTO_INCREMENT PRIMARY KEY,
        id_pedido INT NOT NULL,
        id_producto INT NOT NULL,
        cantidad INT NOT NULL,
        estado_producto_pedido BOOLEAN DEFAULT 0,
        paletizado BOOLEAN DEFAULT 0,
        FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido),
        FOREIGN KEY (id_producto) REFERENCES productos (id_producto),
        KEY uq_pedido_producto (id_pedido, id_producto)
    );

DELIMITER / / CREATE TRIGGER generar_codigo_referencia BEFORE INSERT ON pedidos FOR EACH ROW BEGIN DECLARE secuencia_diaria INT;

DECLARE secuencia_hex VARCHAR(10);

DECLARE nuevo_codigo VARCHAR(50);

SELECT
    COUNT(*) + 1 INTO secuencia_diaria
FROM
    pedidos
WHERE
    DATE (fecha_pedido) = DATE (NEW.fecha_pedido);

SET
    secuencia_hex = LPAD (HEX (secuencia_diaria), 6, '0');

SET
    nuevo_codigo = CONCAT (
        'PED-',
        DATE_FORMAT (NEW.fecha_pedido, '%Y%m%d'),
        '-',
        secuencia_hex
    );

SET
    NEW.codigo_referencia = nuevo_codigo;

END;

/ / DELIMITER;

DELIMITER / / CREATE TRIGGER calcular_posiciones_disponibles BEFORE INSERT ON estanterias FOR EACH ROW BEGIN
SET
    NEW.posiciones_disponibles = NEW.num_baldas * NEW.posiciones_por_balda;

END;

/ / DELIMITER;

DELIMITER / / CREATE TRIGGER generar_user_name BEFORE INSERT ON usuarios FOR EACH ROW BEGIN IF NEW.user_name IS NULL
OR NEW.user_name = '' THEN
SET
    NEW.user_name = LOWER(CONCAT (LEFT (NEW.nombre, 1), NEW.apellido1));

END IF;

END;

/ / DELIMITER;

CREATE TABLE
    orden_compra (
        id_oc INT PRIMARY KEY AUTO_INCREMENT,
        codigo_referencia VARCHAR(50) UNIQUE,
        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        observaciones TEXT NULL
    );

DELIMITER / / CREATE TRIGGER generar_codigo_orden_compra BEFORE INSERT ON orden_compra FOR EACH ROW BEGIN DECLARE secuencia_diaria INT;

DECLARE secuencia_hex VARCHAR(10);

IF NEW.fecha_creacion IS NULL THEN
SET
    NEW.fecha_creacion = CURRENT_TIMESTAMP;

END IF;

SELECT
    COUNT(*) + 1 INTO secuencia_diaria
FROM
    orden_compra
WHERE
    DATE (fecha_creacion) = DATE (NEW.fecha_creacion);

SET
    secuencia_hex = LPAD (HEX (secuencia_diaria), 6, '0');

SET
    NEW.codigo_referencia = CONCAT (
        'OC-',
        DATE_FORMAT (NEW.fecha_creacion, '%Y%m%d'),
        '-',
        secuencia_hex
    );

END;

/ / DELIMITER;

CREATE INDEX idx_oc_fecha ON orden_compra (fecha_creacion);

CREATE INDEX idx_oc_codigo ON orden_compra (codigo_referencia);

CREATE TABLE
    detalle_orden_compra (
        id_detalle_oc INT PRIMARY KEY AUTO_INCREMENT,
        id_oc INT NOT NULL,
        id_proveedor INT NOT NULL,
        id_producto INT NOT NULL,
        cantidad INT NOT NULL CHECK (cantidad > 0),
        estanteria INT NULL,
        balda INT NULL,
        posicion INT NULL,
        delante BOOLEAN NULL,
        FOREIGN KEY (id_oc) REFERENCES orden_compra (id_oc),
        FOREIGN KEY (id_proveedor) REFERENCES proveedores (id_proveedor),
        FOREIGN KEY (id_producto) REFERENCES productos (id_producto),
        FOREIGN KEY (id_proveedor, id_producto) REFERENCES proveedor_producto (id_proveedor, id_producto)
    );

CREATE INDEX idx_doc_oc ON detalle_orden_compra (id_oc);

CREATE INDEX idx_doc_prov ON detalle_orden_compra (id_proveedor);

CREATE INDEX idx_doc_prod ON detalle_orden_compra (id_producto);

DELIMITER / / CREATE PROCEDURE crear_orden_compra (
    IN p_observaciones TEXT,
    OUT p_id_oc INT,
    OUT p_codigo_referencia VARCHAR(50)
) BEGIN
INSERT INTO
    orden_compra (observaciones)
VALUES
    (p_observaciones);

SET
    p_id_oc = LAST_INSERT_ID ();

SELECT
    codigo_referencia INTO p_codigo_referencia
FROM
    orden_compra
WHERE
    id_oc = p_id_oc;

END;

/ / DELIMITER;

CREATE TABLE
    palet_salida (
        id_palet_salida INT PRIMARY KEY AUTO_INCREMENT,
        sscc VARCHAR(18) UNIQUE NOT NULL,
        fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        cantidad_total INT,
        numero_productos INT,
        id_pedido INT,
        FOREIGN KEY (id_pedido) REFERENCES pedidos (id_pedido)
    );

CREATE TABLE
    palet_salida_detalle (
        id_detalle INT PRIMARY KEY AUTO_INCREMENT,
        id_palet_salida INT NOT NULL,
        id_producto INT NOT NULL,
        cajas INT NOT NULL,
        FOREIGN KEY (id_palet_salida) REFERENCES palet_salida (id_palet_salida),
        FOREIGN KEY (id_producto) REFERENCES productos (id_producto)
    );