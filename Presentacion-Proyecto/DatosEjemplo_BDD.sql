-- Desactivar temporalmente las comprobaciones de claves foráneas
SET
    FOREIGN_KEY_CHECKS = 0;

-- Limpiar las tablas (eliminar todos los registros)
TRUNCATE TABLE detalles_pedido;

TRUNCATE TABLE pedidos;

TRUNCATE TABLE movimientos;

TRUNCATE TABLE productos;

TRUNCATE TABLE usuarios;

TRUNCATE TABLE estanterias;

-- Reactivar las comprobaciones de claves foráneas
SET
    FOREIGN_KEY_CHECKS = 1;

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
INSERT INTO
    estanterias (num_baldas, posiciones_por_balda)
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
INSERT INTO
    usuarios (nombre, email, contraseña, id_rol)
VALUES
    -- Usuario SysAdmin
    (
        'Admin',
        'admin@almacen.com',
        'hashed_password',
        1
    ),
    -- Usuario Gestor Almacén
    (
        'Juan Perez',
        'juan.perez@almacen.com',
        'hashed_password',
        2
    ),
    -- Usuario Supervisor
    (
        'Maria Lopez',
        'maria.lopez@almacen.com',
        'hashed_password',
        3
    ),
    -- Usuario Operario
    (
        'Carlos Diaz',
        'carlos.diaz@almacen.com',
        'hashed_password',
        4
    ),
    -- Usuario Mantenimiento
    (
        'Luisa Gomez',
        'luisa.gomez@almacen.com',
        'hashed_password',
        5
    ),
    -- Usuario Administración
    (
        'Ana Torres',
        'ana.torres@almacen.com',
        'hashed_password',
        6
    );

-- Insertando registros en la tabla Clientes
INSERT INTO
    clientes (
        nombre_cliente,
        email_cliente,
        telefono_cliente,
        direccion_cliente,
        ciudad_cliente,
        estado_pais_cliente,
        pais_cliente,
        fecha_nacimiento,
        estado_cliente
    )
VALUES
    -- Cliente 1: Juan Pérez
    (
        'Juan Pérez',
        'juan.perez@example.com',
        '+52-555-123-4567',
        'Av. Reforma 123',
        'Ciudad de México',
        'CDMX',
        'México',
        '1985-04-15',
        TRUE
    ),
    -- Cliente 2: María López
    (
        'María López',
        'maria.lopez@example.com',
        '+52-555-987-6543',
        'Calle Independencia 456',
        'Monterrey',
        'Nuevo León',
        'México',
        '1990-07-22',
        TRUE
    ),
    -- Cliente 3: Carlos Sánchez
    (
        'Carlos Sánchez',
        'carlos.sanchez@example.com',
        '+52-812-345-6789',
        'Blvd. Hidalgo 789',
        'Guadalajara',
        'Jalisco',
        'México',
        '1982-11-02',
        FALSE
    ),
    -- Cliente 4: Ana Rodríguez
    (
        'Ana Rodríguez',
        'ana.rodriguez@example.com',
        '+1-310-555-1234',
        '1234 Sunset Blvd',
        'Los Ángeles',
        'California',
        'Estados Unidos',
        '1995-06-30',
        TRUE
    ),
    -- Cliente 5: Pedro Gómez
    (
        'Pedro Gómez',
        'pedro.gomez@example.com',
        '+44-20-7946-0958',
        '10 Downing Street',
        'Londres',
        'Inglaterra',
        'Reino Unido',
        '1979-03-18',
        TRUE
    ),
    -- Cliente 6: Laura Fernández
    (
        'Laura Fernández',
        'laura.fernandez@example.com',
        NULL,
        'Calle Principal 123',
        'Madrid',
        'Madrid',
        'España',
        '1988-08-10',
        TRUE
    ),
    -- Cliente 7: Miguel Herrera
    (
        'Miguel Herrera',
        'miguel.herrera@example.com',
        '+52-333-678-1234',
        NULL,
        'Tijuana',
        'Baja California',
        'México',
        '1992-01-05',
        FALSE
    ),
    -- Cliente 8: Sofía Morales
    (
        'Sofía Morales',
        'sofia.morales@example.com',
        '+34-655-123-456',
        'Av. del Sol 234',
        'Barcelona',
        'Cataluña',
        'España',
        '1986-12-25',
        TRUE
    ),
    -- Cliente 9: Diego Martínez
    (
        'Diego Martínez',
        'diego.martinez@example.com',
        '+54-11-4321-5678',
        'Av. 9 de Julio 567',
        'Buenos Aires',
        'Buenos Aires',
        'Argentina',
        '1993-09-12',
        TRUE
    ),
    -- Cliente 10: Lucía Jiménez
    (
        'Lucía Jiménez',
        'lucia.jimenez@example.com',
        '+49-30-5678-1234',
        'Unter den Linden 1',
        'Berlín',
        'Berlín',
        'Alemania',
        '1980-05-08',
        TRUE
    );

-- Inserción de datos en la tabla Productos
INSERT INTO
    productos (nombre_producto, descripcion, precio)
VALUES
    -- Producto 1: Lápices
    ('Lápices', 'Caja de 50 lápices de madera', 10.50),
    -- Producto 2: Cuadernos
    (
        'Cuadernos',
        'Cuadernos tamaño A4 de 100 hojas',
        5.75
    ),
    -- Producto 3: Bolígrafos
    (
        'Bolígrafos',
        'Paquete de 20 bolígrafos azules',
        7.20
    ),
    -- Producto 4: Marcadores
    (
        'Marcadores',
        'Set de 12 marcadores de colores',
        15.00
    ),
    -- Producto 5: Gomas de borrar
    (
        'Gomas de borrar',
        'Pack de 10 gomas de borrar suaves',
        3.50
    );

-- Inserción de datos en la tabla Palets
INSERT INTO
    palets (
        id_producto,
        cantidad,
        estanteria,
        balda,
        posicion
    )
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
INSERT INTO
    movimientos (
        id_usuario,
        id_palet,
        tipo_movimiento,
        cantidad,
        observaciones
    )
VALUES
    -- Movimiento 1: Entrada de lápices en Zona A
    (
        2,
        1,
        'Entrada',
        100,
        'Recepción de lápices en Zona A - Estante 1'
    ),
    -- Movimiento 2: Salida de lápices a cliente
    (
        3,
        2,
        'Salida',
        50,
        'Envío parcial de lápices a cliente'
    ),
    -- Movimiento 3: Entrada de cuadernos en Zona B
    (
        4,
        3,
        'Entrada',
        200,
        'Recepción de cuadernos en Zona B - Estante 3'
    ),
    -- Movimiento 4: Entrada de bolígrafos en Zona C
    (
        5,
        4,
        'Entrada',
        150,
        'Recepción de bolígrafos en Zona C - Estante 1'
    );

-- Inserción de datos en la tabla Pedidos
INSERT INTO
    pedidos (id_usuario, id_cliente, estado)
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
INSERT INTO
    detalles_pedido (id_pedido, id_palet, cantidad)
VALUES
    -- Detalles del Pedido 1: Incluye lápices y bolígrafos
    (1, 1, 20), -- 20 unidades del primer palet (Lápices)
    (1, 3, 15), -- 15 unidades del tercer palet (Bolígrafos)
    -- Detalles del Pedido 2: Incluye cuadernos y marcadores
    (2, 2, 10), -- 10 unidades del segundo palet (Cuadernos)
    (2, 4, 5), -- 5 unidades del cuarto palet (Marcadores)
    -- Detalles del Pedido 3: Incluye lápices, cuadernos y gomas de borrar
    (3, 1, 30), -- 30 unidades del primer palet (Lápices)
    (3, 2, 20), -- 20 unidades del segundo palet (Cuadernos)
    (3, 6, 10), -- 10 unidades del sexto palet (Gomas de borrar)
    -- Detalles del Pedido 4: Incluye marcadores y bolígrafos
    (4, 4, 25), -- 25 unidades del cuarto palet (Marcadores)
    (4, 3, 40), -- 40 unidades del tercer palet (Bolígrafos)
    -- Detalles del Pedido 5: Incluye cuadernos, lápices y gomas de borrar
    (5, 2, 15), -- 15 unidades del segundo palet (Cuadernos)
    (5, 1, 10), -- 10 unidades del primer palet (Lápices)
    (5, 6, 5), -- 5 unidades del sexto palet (Gomas de borrar)
    -- Detalles del Pedido 6: Incluye lápices y marcadores
    (6, 1, 50), -- 50 unidades del primer palet (Lápices)
    (6, 4, 20), -- 20 unidades del cuarto palet (Marcadores)
    -- Detalles del Pedido 7: Incluye bolígrafos, cuadernos y marcadores
    (7, 3, 35), -- 35 unidades del tercer palet (Bolígrafos)
    (7, 2, 25), -- 25 unidades del segundo palet (Cuadernos)
    (7, 4, 10), -- 10 unidades del cuarto palet (Marcadores)
    -- Detalles del Pedido 8: Incluye lápices y gomas de borrar
    (8, 1, 40), -- 40 unidades del primer palet (Lápices)
    (8, 6, 15), -- 15 unidades del sexto palet (Gomas de borrar)
    -- Detalles del Pedido 9: Incluye bolígrafos y cuadernos
    (9, 3, 20), -- 20 unidades del tercer palet (Bolígrafos)
    (9, 2, 30), -- 30 unidades del segundo palet (Cuadernos)
    -- Detalles del Pedido 10: Incluye marcadores, lápices y bolígrafos
    (10, 4, 50), -- 50 unidades del cuarto palet (Marcadores)
    (10, 1, 20), -- 20 unidades del primer palet (Lápices)
    (10, 3, 25);

-- 25 unidades del tercer palet (Bolígrafos)