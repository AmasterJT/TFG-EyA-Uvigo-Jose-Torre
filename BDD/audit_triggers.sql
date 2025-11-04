-- ===============================================
-- Audit triggers for tfg_almacenDB
-- Generated automatically
-- ===============================================

CREATE SCHEMA IF NOT EXISTS audit;

CREATE TABLE IF NOT EXISTS audit.audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_time    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  db_name       VARCHAR(64) NOT NULL,
  table_name    VARCHAR(64) NOT NULL,
  operation     ENUM('INSERT','UPDATE','DELETE') NOT NULL,
  executed_by   VARCHAR(128) NOT NULL,
  connection_id BIGINT NOT NULL,
  pk            JSON NULL,
  row_before    JSON NULL,
  row_after     JSON NULL
) ENGINE=InnoDB;

CREATE INDEX IF NOT EXISTS ix_audit_time ON audit.audit_log(event_time);
CREATE INDEX IF NOT EXISTS ix_audit_tbl  ON audit.audit_log(db_name, table_name, operation);

DELIMITER //

CREATE TRIGGER `proveedores_ai_audit`
AFTER INSERT ON `proveedores`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'proveedores', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_proveedor', NEW.id_proveedor), JSON_OBJECT(
            'id_proveedor', NEW.id_proveedor,
            'nombre', NEW.nombre,
            'direccion', NEW.direccion,
            'telefono', NEW.telefono,
            'email', NEW.email,
            'nif_cif', NEW.nif_cif,
            'contacto', NEW.contacto,
            'fecha_registro', NEW.fecha_registro,
            'ultima_actualizacion', NEW.ultima_actualizacion
        ));
END//

CREATE TRIGGER `proveedores_bu_audit`
BEFORE UPDATE ON `proveedores`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'proveedores', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_proveedor', OLD.id_proveedor), JSON_OBJECT(
            'id_proveedor', OLD.id_proveedor,
            'nombre', OLD.nombre,
            'direccion', OLD.direccion,
            'telefono', OLD.telefono,
            'email', OLD.email,
            'nif_cif', OLD.nif_cif,
            'contacto', OLD.contacto,
            'fecha_registro', OLD.fecha_registro,
            'ultima_actualizacion', OLD.ultima_actualizacion
        ), JSON_OBJECT(
            'id_proveedor', NEW.id_proveedor,
            'nombre', NEW.nombre,
            'direccion', NEW.direccion,
            'telefono', NEW.telefono,
            'email', NEW.email,
            'nif_cif', NEW.nif_cif,
            'contacto', NEW.contacto,
            'fecha_registro', NEW.fecha_registro,
            'ultima_actualizacion', NEW.ultima_actualizacion
        ));
END//

CREATE TRIGGER `proveedores_bd_audit`
BEFORE DELETE ON `proveedores`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'proveedores', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_proveedor', OLD.id_proveedor), JSON_OBJECT(
            'id_proveedor', OLD.id_proveedor,
            'nombre', OLD.nombre,
            'direccion', OLD.direccion,
            'telefono', OLD.telefono,
            'email', OLD.email,
            'nif_cif', OLD.nif_cif,
            'contacto', OLD.contacto,
            'fecha_registro', OLD.fecha_registro,
            'ultima_actualizacion', OLD.ultima_actualizacion
        ));
END//

CREATE TRIGGER `permisos_usuarios_ai_audit`
AFTER INSERT ON `permisos_usuarios`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'permisos_usuarios', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_permiso', NEW.id_permiso), JSON_OBJECT(
            'id_permiso', NEW.id_permiso,
            'permiso', NEW.permiso,
            'descripcion', NEW.descripcion
        ));
END//

CREATE TRIGGER `permisos_usuarios_bu_audit`
BEFORE UPDATE ON `permisos_usuarios`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'permisos_usuarios', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_permiso', OLD.id_permiso), JSON_OBJECT(
            'id_permiso', OLD.id_permiso,
            'permiso', OLD.permiso,
            'descripcion', OLD.descripcion
        ), JSON_OBJECT(
            'id_permiso', NEW.id_permiso,
            'permiso', NEW.permiso,
            'descripcion', NEW.descripcion
        ));
END//

CREATE TRIGGER `permisos_usuarios_bd_audit`
BEFORE DELETE ON `permisos_usuarios`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'permisos_usuarios', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_permiso', OLD.id_permiso), JSON_OBJECT(
            'id_permiso', OLD.id_permiso,
            'permiso', OLD.permiso,
            'descripcion', OLD.descripcion
        ));
END//

CREATE TRIGGER `roles_ai_audit`
AFTER INSERT ON `roles`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'roles', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_rol', NEW.id_rol), JSON_OBJECT(
            'id_rol', NEW.id_rol,
            'nombre_rol', NEW.nombre_rol,
            'descripcion', NEW.descripcion
        ));
END//

CREATE TRIGGER `roles_bu_audit`
BEFORE UPDATE ON `roles`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'roles', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_rol', OLD.id_rol), JSON_OBJECT(
            'id_rol', OLD.id_rol,
            'nombre_rol', OLD.nombre_rol,
            'descripcion', OLD.descripcion
        ), JSON_OBJECT(
            'id_rol', NEW.id_rol,
            'nombre_rol', NEW.nombre_rol,
            'descripcion', NEW.descripcion
        ));
END//

CREATE TRIGGER `roles_bd_audit`
BEFORE DELETE ON `roles`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'roles', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_rol', OLD.id_rol), JSON_OBJECT(
            'id_rol', OLD.id_rol,
            'nombre_rol', OLD.nombre_rol,
            'descripcion', OLD.descripcion
        ));
END//

CREATE TRIGGER `rol_permiso_ai_audit`
AFTER INSERT ON `rol_permiso`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'rol_permiso', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_rol', NEW.id_rol, 'id_permiso', NEW.id_permiso), JSON_OBJECT(
            'id_rol', NEW.id_rol,
            'id_permiso', NEW.id_permiso,
            'estado', NEW.estado
        ));
END//

CREATE TRIGGER `rol_permiso_bu_audit`
BEFORE UPDATE ON `rol_permiso`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'rol_permiso', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_rol', OLD.id_rol, 'id_permiso', OLD.id_permiso), JSON_OBJECT(
            'id_rol', OLD.id_rol,
            'id_permiso', OLD.id_permiso,
            'estado', OLD.estado
        ), JSON_OBJECT(
            'id_rol', NEW.id_rol,
            'id_permiso', NEW.id_permiso,
            'estado', NEW.estado
        ));
END//

CREATE TRIGGER `rol_permiso_bd_audit`
BEFORE DELETE ON `rol_permiso`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'rol_permiso', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_rol', OLD.id_rol, 'id_permiso', OLD.id_permiso), JSON_OBJECT(
            'id_rol', OLD.id_rol,
            'id_permiso', OLD.id_permiso,
            'estado', OLD.estado
        ));
END//

CREATE TRIGGER `estanterias_ai_audit`
AFTER INSERT ON `estanterias`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'estanterias', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_estanteria', NEW.id_estanteria), JSON_OBJECT(
            'id_estanteria', NEW.id_estanteria,
            'num_baldas', NEW.num_baldas,
            'posiciones_por_balda', NEW.posiciones_por_balda,
            'posiciones_disponibles', NEW.posiciones_disponibles,
            'zona', NEW.zona
        ));
END//

CREATE TRIGGER `estanterias_bu_audit`
BEFORE UPDATE ON `estanterias`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'estanterias', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_estanteria', OLD.id_estanteria), JSON_OBJECT(
            'id_estanteria', OLD.id_estanteria,
            'num_baldas', OLD.num_baldas,
            'posiciones_por_balda', OLD.posiciones_por_balda,
            'posiciones_disponibles', OLD.posiciones_disponibles,
            'zona', OLD.zona
        ), JSON_OBJECT(
            'id_estanteria', NEW.id_estanteria,
            'num_baldas', NEW.num_baldas,
            'posiciones_por_balda', NEW.posiciones_por_balda,
            'posiciones_disponibles', NEW.posiciones_disponibles,
            'zona', NEW.zona
        ));
END//

CREATE TRIGGER `estanterias_bd_audit`
BEFORE DELETE ON `estanterias`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'estanterias', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_estanteria', OLD.id_estanteria), JSON_OBJECT(
            'id_estanteria', OLD.id_estanteria,
            'num_baldas', OLD.num_baldas,
            'posiciones_por_balda', OLD.posiciones_por_balda,
            'posiciones_disponibles', OLD.posiciones_disponibles,
            'zona', OLD.zona
        ));
END//

CREATE TRIGGER `usuarios_ai_audit`
AFTER INSERT ON `usuarios`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'usuarios', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_usuario', NEW.id_usuario), JSON_OBJECT(
            'id_usuario', NEW.id_usuario,
            'user_name', NEW.user_name,
            'nombre', NEW.nombre,
            'apellido1', NEW.apellido1,
            'apellido2', NEW.apellido2,
            'email', NEW.email,
            'contraseña', NEW.contraseña,
            'id_rol', NEW.id_rol,
            'activo', NEW.activo,
            'fecha_registro', NEW.fecha_registro
        ));
END//

CREATE TRIGGER `usuarios_bu_audit`
BEFORE UPDATE ON `usuarios`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'usuarios', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_usuario', OLD.id_usuario), JSON_OBJECT(
            'id_usuario', OLD.id_usuario,
            'user_name', OLD.user_name,
            'nombre', OLD.nombre,
            'apellido1', OLD.apellido1,
            'apellido2', OLD.apellido2,
            'email', OLD.email,
            'contraseña', OLD.contraseña,
            'id_rol', OLD.id_rol,
            'activo', OLD.activo,
            'fecha_registro', OLD.fecha_registro
        ), JSON_OBJECT(
            'id_usuario', NEW.id_usuario,
            'user_name', NEW.user_name,
            'nombre', NEW.nombre,
            'apellido1', NEW.apellido1,
            'apellido2', NEW.apellido2,
            'email', NEW.email,
            'contraseña', NEW.contraseña,
            'id_rol', NEW.id_rol,
            'activo', NEW.activo,
            'fecha_registro', NEW.fecha_registro
        ));
END//

CREATE TRIGGER `usuarios_bd_audit`
BEFORE DELETE ON `usuarios`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'usuarios', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_usuario', OLD.id_usuario), JSON_OBJECT(
            'id_usuario', OLD.id_usuario,
            'user_name', OLD.user_name,
            'nombre', OLD.nombre,
            'apellido1', OLD.apellido1,
            'apellido2', OLD.apellido2,
            'email', OLD.email,
            'contraseña', OLD.contraseña,
            'id_rol', OLD.id_rol,
            'activo', OLD.activo,
            'fecha_registro', OLD.fecha_registro
        ));
END//

CREATE TRIGGER `clientes_ai_audit`
AFTER INSERT ON `clientes`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'clientes', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_cliente', NEW.id_cliente), JSON_OBJECT(
            'id_cliente', NEW.id_cliente,
            'nombre', NEW.nombre,
            'direccion', NEW.direccion,
            'telefono', NEW.telefono,
            'email', NEW.email,
            'fecha_registro', NEW.fecha_registro,
            'ultima_actualizacion', NEW.ultima_actualizacion
        ));
END//

CREATE TRIGGER `clientes_bu_audit`
BEFORE UPDATE ON `clientes`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'clientes', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_cliente', OLD.id_cliente), JSON_OBJECT(
            'id_cliente', OLD.id_cliente,
            'nombre', OLD.nombre,
            'direccion', OLD.direccion,
            'telefono', OLD.telefono,
            'email', OLD.email,
            'fecha_registro', OLD.fecha_registro,
            'ultima_actualizacion', OLD.ultima_actualizacion
        ), JSON_OBJECT(
            'id_cliente', NEW.id_cliente,
            'nombre', NEW.nombre,
            'direccion', NEW.direccion,
            'telefono', NEW.telefono,
            'email', NEW.email,
            'fecha_registro', NEW.fecha_registro,
            'ultima_actualizacion', NEW.ultima_actualizacion
        ));
END//

CREATE TRIGGER `clientes_bd_audit`
BEFORE DELETE ON `clientes`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'clientes', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_cliente', OLD.id_cliente), JSON_OBJECT(
            'id_cliente', OLD.id_cliente,
            'nombre', OLD.nombre,
            'direccion', OLD.direccion,
            'telefono', OLD.telefono,
            'email', OLD.email,
            'fecha_registro', OLD.fecha_registro,
            'ultima_actualizacion', OLD.ultima_actualizacion
        ));
END//

CREATE TRIGGER `tipos_ai_audit`
AFTER INSERT ON `tipos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'tipos', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_tipo', NEW.id_tipo), JSON_OBJECT(
            'id_tipo', NEW.id_tipo,
            'color', NEW.color
        ));
END//

CREATE TRIGGER `tipos_bu_audit`
BEFORE UPDATE ON `tipos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'tipos', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_tipo', OLD.id_tipo), JSON_OBJECT(
            'id_tipo', OLD.id_tipo,
            'color', OLD.color
        ), JSON_OBJECT(
            'id_tipo', NEW.id_tipo,
            'color', NEW.color
        ));
END//

CREATE TRIGGER `tipos_bd_audit`
BEFORE DELETE ON `tipos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'tipos', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_tipo', OLD.id_tipo), JSON_OBJECT(
            'id_tipo', OLD.id_tipo,
            'color', OLD.color
        ));
END//

CREATE TRIGGER `productos_ai_audit`
AFTER INSERT ON `productos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'productos', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_producto', NEW.id_producto), JSON_OBJECT(
            'id_producto', NEW.id_producto,
            'identificador_producto', NEW.identificador_producto,
            'tipo_producto', NEW.tipo_producto,
            'descripcion', NEW.descripcion,
            'precio', NEW.precio,
            'fecha_registro', NEW.fecha_registro
        ));
END//

CREATE TRIGGER `productos_bu_audit`
BEFORE UPDATE ON `productos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'productos', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_producto', OLD.id_producto), JSON_OBJECT(
            'id_producto', OLD.id_producto,
            'identificador_producto', OLD.identificador_producto,
            'tipo_producto', OLD.tipo_producto,
            'descripcion', OLD.descripcion,
            'precio', OLD.precio,
            'fecha_registro', OLD.fecha_registro
        ), JSON_OBJECT(
            'id_producto', NEW.id_producto,
            'identificador_producto', NEW.identificador_producto,
            'tipo_producto', NEW.tipo_producto,
            'descripcion', NEW.descripcion,
            'precio', NEW.precio,
            'fecha_registro', NEW.fecha_registro
        ));
END//

CREATE TRIGGER `productos_bd_audit`
BEFORE DELETE ON `productos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'productos', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_producto', OLD.id_producto), JSON_OBJECT(
            'id_producto', OLD.id_producto,
            'identificador_producto', OLD.identificador_producto,
            'tipo_producto', OLD.tipo_producto,
            'descripcion', OLD.descripcion,
            'precio', OLD.precio,
            'fecha_registro', OLD.fecha_registro
        ));
END//

CREATE TRIGGER `proveedor_producto_ai_audit`
AFTER INSERT ON `proveedor_producto`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'proveedor_producto', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_proveedor', NEW.id_proveedor, 'id_producto', NEW.id_producto), JSON_OBJECT(
            'id_proveedor', NEW.id_proveedor,
            'id_producto', NEW.id_producto,
            'precio', NEW.precio,
            'unidades_por_palet_default', NEW.unidades_por_palet_default
        ));
END//

CREATE TRIGGER `proveedor_producto_bu_audit`
BEFORE UPDATE ON `proveedor_producto`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'proveedor_producto', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_proveedor', OLD.id_proveedor, 'id_producto', OLD.id_producto), JSON_OBJECT(
            'id_proveedor', OLD.id_proveedor,
            'id_producto', OLD.id_producto,
            'precio', OLD.precio,
            'unidades_por_palet_default', OLD.unidades_por_palet_default
        ), JSON_OBJECT(
            'id_proveedor', NEW.id_proveedor,
            'id_producto', NEW.id_producto,
            'precio', NEW.precio,
            'unidades_por_palet_default', NEW.unidades_por_palet_default
        ));
END//

CREATE TRIGGER `proveedor_producto_bd_audit`
BEFORE DELETE ON `proveedor_producto`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'proveedor_producto', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_proveedor', OLD.id_proveedor, 'id_producto', OLD.id_producto), JSON_OBJECT(
            'id_proveedor', OLD.id_proveedor,
            'id_producto', OLD.id_producto,
            'precio', OLD.precio,
            'unidades_por_palet_default', OLD.unidades_por_palet_default
        ));
END//

CREATE TRIGGER `palets_ai_audit`
AFTER INSERT ON `palets`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'palets', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_palet', NEW.id_palet), JSON_OBJECT(
            'id_palet', NEW.id_palet,
            'identificador', NEW.identificador,
            'id_producto', NEW.id_producto,
            'alto', NEW.alto,
            'ancho', NEW.ancho,
            'largo', NEW.largo,
            'cantidad_de_producto', NEW.cantidad_de_producto,
            'estanteria', NEW.estanteria,
            'balda', NEW.balda,
            'posicion', NEW.posicion,
            'delante', NEW.delante,
            'fecha_registro', NEW.fecha_registro
        ));
END//

CREATE TRIGGER `palets_bu_audit`
BEFORE UPDATE ON `palets`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'palets', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_palet', OLD.id_palet), JSON_OBJECT(
            'id_palet', OLD.id_palet,
            'identificador', OLD.identificador,
            'id_producto', OLD.id_producto,
            'alto', OLD.alto,
            'ancho', OLD.ancho,
            'largo', OLD.largo,
            'cantidad_de_producto', OLD.cantidad_de_producto,
            'estanteria', OLD.estanteria,
            'balda', OLD.balda,
            'posicion', OLD.posicion,
            'delante', OLD.delante,
            'fecha_registro', OLD.fecha_registro
        ), JSON_OBJECT(
            'id_palet', NEW.id_palet,
            'identificador', NEW.identificador,
            'id_producto', NEW.id_producto,
            'alto', NEW.alto,
            'ancho', NEW.ancho,
            'largo', NEW.largo,
            'cantidad_de_producto', NEW.cantidad_de_producto,
            'estanteria', NEW.estanteria,
            'balda', NEW.balda,
            'posicion', NEW.posicion,
            'delante', NEW.delante,
            'fecha_registro', NEW.fecha_registro
        ));
END//

CREATE TRIGGER `palets_bd_audit`
BEFORE DELETE ON `palets`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'palets', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_palet', OLD.id_palet), JSON_OBJECT(
            'id_palet', OLD.id_palet,
            'identificador', OLD.identificador,
            'id_producto', OLD.id_producto,
            'alto', OLD.alto,
            'ancho', OLD.ancho,
            'largo', OLD.largo,
            'cantidad_de_producto', OLD.cantidad_de_producto,
            'estanteria', OLD.estanteria,
            'balda', OLD.balda,
            'posicion', OLD.posicion,
            'delante', OLD.delante,
            'fecha_registro', OLD.fecha_registro
        ));
END//

CREATE TRIGGER `movimientos_ai_audit`
AFTER INSERT ON `movimientos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'movimientos', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_movimiento', NEW.id_movimiento), JSON_OBJECT(
            'id_movimiento', NEW.id_movimiento,
            'id_usuario', NEW.id_usuario,
            'id_palet', NEW.id_palet,
            'tipo_movimiento', NEW.tipo_movimiento,
            'cantidad', NEW.cantidad,
            'fecha_movimiento', NEW.fecha_movimiento,
            'observaciones', NEW.observaciones
        ));
END//

CREATE TRIGGER `movimientos_bu_audit`
BEFORE UPDATE ON `movimientos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'movimientos', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_movimiento', OLD.id_movimiento), JSON_OBJECT(
            'id_movimiento', OLD.id_movimiento,
            'id_usuario', OLD.id_usuario,
            'id_palet', OLD.id_palet,
            'tipo_movimiento', OLD.tipo_movimiento,
            'cantidad', OLD.cantidad,
            'fecha_movimiento', OLD.fecha_movimiento,
            'observaciones', OLD.observaciones
        ), JSON_OBJECT(
            'id_movimiento', NEW.id_movimiento,
            'id_usuario', NEW.id_usuario,
            'id_palet', NEW.id_palet,
            'tipo_movimiento', NEW.tipo_movimiento,
            'cantidad', NEW.cantidad,
            'fecha_movimiento', NEW.fecha_movimiento,
            'observaciones', NEW.observaciones
        ));
END//

CREATE TRIGGER `movimientos_bd_audit`
BEFORE DELETE ON `movimientos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'movimientos', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_movimiento', OLD.id_movimiento), JSON_OBJECT(
            'id_movimiento', OLD.id_movimiento,
            'id_usuario', OLD.id_usuario,
            'id_palet', OLD.id_palet,
            'tipo_movimiento', OLD.tipo_movimiento,
            'cantidad', OLD.cantidad,
            'fecha_movimiento', OLD.fecha_movimiento,
            'observaciones', OLD.observaciones
        ));
END//

CREATE TRIGGER `pedidos_ai_audit`
AFTER INSERT ON `pedidos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'pedidos', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_pedido', NEW.id_pedido), JSON_OBJECT(
            'id_pedido', NEW.id_pedido,
            'codigo_referencia', NEW.codigo_referencia,
            'id_usuario', NEW.id_usuario,
            'id_cliente', NEW.id_cliente,
            'fecha_pedido', NEW.fecha_pedido,
            'fecha_entrega', NEW.fecha_entrega,
            'estado', NEW.estado,
            'hora_salida', NEW.hora_salida
        ));
END//

CREATE TRIGGER `pedidos_bu_audit`
BEFORE UPDATE ON `pedidos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'pedidos', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_pedido', OLD.id_pedido), JSON_OBJECT(
            'id_pedido', OLD.id_pedido,
            'codigo_referencia', OLD.codigo_referencia,
            'id_usuario', OLD.id_usuario,
            'id_cliente', OLD.id_cliente,
            'fecha_pedido', OLD.fecha_pedido,
            'fecha_entrega', OLD.fecha_entrega,
            'estado', OLD.estado,
            'hora_salida', OLD.hora_salida
        ), JSON_OBJECT(
            'id_pedido', NEW.id_pedido,
            'codigo_referencia', NEW.codigo_referencia,
            'id_usuario', NEW.id_usuario,
            'id_cliente', NEW.id_cliente,
            'fecha_pedido', NEW.fecha_pedido,
            'fecha_entrega', NEW.fecha_entrega,
            'estado', NEW.estado,
            'hora_salida', NEW.hora_salida
        ));
END//

CREATE TRIGGER `pedidos_bd_audit`
BEFORE DELETE ON `pedidos`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'pedidos', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_pedido', OLD.id_pedido), JSON_OBJECT(
            'id_pedido', OLD.id_pedido,
            'codigo_referencia', OLD.codigo_referencia,
            'id_usuario', OLD.id_usuario,
            'id_cliente', OLD.id_cliente,
            'fecha_pedido', OLD.fecha_pedido,
            'fecha_entrega', OLD.fecha_entrega,
            'estado', OLD.estado,
            'hora_salida', OLD.hora_salida
        ));
END//

CREATE TRIGGER `detalles_pedido_ai_audit`
AFTER INSERT ON `detalles_pedido`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'detalles_pedido', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_detalle', NEW.id_detalle), JSON_OBJECT(
            'id_detalle', NEW.id_detalle,
            'id_pedido', NEW.id_pedido,
            'id_producto', NEW.id_producto,
            'cantidad', NEW.cantidad,
            'estado_producto_pedido', NEW.estado_producto_pedido
        ));
END//

CREATE TRIGGER `detalles_pedido_bu_audit`
BEFORE UPDATE ON `detalles_pedido`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'detalles_pedido', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_detalle', OLD.id_detalle), JSON_OBJECT(
            'id_detalle', OLD.id_detalle,
            'id_pedido', OLD.id_pedido,
            'id_producto', OLD.id_producto,
            'cantidad', OLD.cantidad,
            'estado_producto_pedido', OLD.estado_producto_pedido
        ), JSON_OBJECT(
            'id_detalle', NEW.id_detalle,
            'id_pedido', NEW.id_pedido,
            'id_producto', NEW.id_producto,
            'cantidad', NEW.cantidad,
            'estado_producto_pedido', NEW.estado_producto_pedido
        ));
END//

CREATE TRIGGER `detalles_pedido_bd_audit`
BEFORE DELETE ON `detalles_pedido`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'detalles_pedido', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_detalle', OLD.id_detalle), JSON_OBJECT(
            'id_detalle', OLD.id_detalle,
            'id_pedido', OLD.id_pedido,
            'id_producto', OLD.id_producto,
            'cantidad', OLD.cantidad,
            'estado_producto_pedido', OLD.estado_producto_pedido
        ));
END//

CREATE TRIGGER `orden_compra_ai_audit`
AFTER INSERT ON `orden_compra`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'orden_compra', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_oc', NEW.id_oc), JSON_OBJECT(
            'id_oc', NEW.id_oc,
            'codigo_referencia', NEW.codigo_referencia,
            'fecha_creacion', NEW.fecha_creacion,
            'observaciones', NEW.observaciones
        ));
END//

CREATE TRIGGER `orden_compra_bu_audit`
BEFORE UPDATE ON `orden_compra`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'orden_compra', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_oc', OLD.id_oc), JSON_OBJECT(
            'id_oc', OLD.id_oc,
            'codigo_referencia', OLD.codigo_referencia,
            'fecha_creacion', OLD.fecha_creacion,
            'observaciones', OLD.observaciones
        ), JSON_OBJECT(
            'id_oc', NEW.id_oc,
            'codigo_referencia', NEW.codigo_referencia,
            'fecha_creacion', NEW.fecha_creacion,
            'observaciones', NEW.observaciones
        ));
END//

CREATE TRIGGER `orden_compra_bd_audit`
BEFORE DELETE ON `orden_compra`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'orden_compra', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_oc', OLD.id_oc), JSON_OBJECT(
            'id_oc', OLD.id_oc,
            'codigo_referencia', OLD.codigo_referencia,
            'fecha_creacion', OLD.fecha_creacion,
            'observaciones', OLD.observaciones
        ));
END//

CREATE TRIGGER `detalle_orden_compra_ai_audit`
AFTER INSERT ON `detalle_orden_compra`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_after)
  VALUES
    (DATABASE(), 'detalle_orden_compra', 'INSERT', USER(), CONNECTION_ID(), JSON_OBJECT('id_detalle_oc', NEW.id_detalle_oc), JSON_OBJECT(
            'id_detalle_oc', NEW.id_detalle_oc,
            'id_oc', NEW.id_oc,
            'id_proveedor', NEW.id_proveedor,
            'id_producto', NEW.id_producto,
            'cantidad', NEW.cantidad,
            'estanteria', NEW.estanteria,
            'balda', NEW.balda,
            'posicion', NEW.posicion,
            'delante', NEW.delante
        ));
END//

CREATE TRIGGER `detalle_orden_compra_bu_audit`
BEFORE UPDATE ON `detalle_orden_compra`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before, row_after)
  VALUES
    (DATABASE(), 'detalle_orden_compra', 'UPDATE', USER(), CONNECTION_ID(), JSON_OBJECT('id_detalle_oc', OLD.id_detalle_oc), JSON_OBJECT(
            'id_detalle_oc', OLD.id_detalle_oc,
            'id_oc', OLD.id_oc,
            'id_proveedor', OLD.id_proveedor,
            'id_producto', OLD.id_producto,
            'cantidad', OLD.cantidad,
            'estanteria', OLD.estanteria,
            'balda', OLD.balda,
            'posicion', OLD.posicion,
            'delante', OLD.delante
        ), JSON_OBJECT(
            'id_detalle_oc', NEW.id_detalle_oc,
            'id_oc', NEW.id_oc,
            'id_proveedor', NEW.id_proveedor,
            'id_producto', NEW.id_producto,
            'cantidad', NEW.cantidad,
            'estanteria', NEW.estanteria,
            'balda', NEW.balda,
            'posicion', NEW.posicion,
            'delante', NEW.delante
        ));
END//

CREATE TRIGGER `detalle_orden_compra_bd_audit`
BEFORE DELETE ON `detalle_orden_compra`
FOR EACH ROW
BEGIN
  INSERT INTO audit.audit_log
    (db_name, table_name, operation, executed_by, connection_id, pk, row_before)
  VALUES
    (DATABASE(), 'detalle_orden_compra', 'DELETE', USER(), CONNECTION_ID(), JSON_OBJECT('id_detalle_oc', OLD.id_detalle_oc), JSON_OBJECT(
            'id_detalle_oc', OLD.id_detalle_oc,
            'id_oc', OLD.id_oc,
            'id_proveedor', OLD.id_proveedor,
            'id_producto', OLD.id_producto,
            'cantidad', OLD.cantidad,
            'estanteria', OLD.estanteria,
            'balda', OLD.balda,
            'posicion', OLD.posicion,
            'delante', OLD.delante
        ));
END//
DELIMITER ;
