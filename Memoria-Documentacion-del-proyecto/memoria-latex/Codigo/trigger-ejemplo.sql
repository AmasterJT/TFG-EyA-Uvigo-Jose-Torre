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