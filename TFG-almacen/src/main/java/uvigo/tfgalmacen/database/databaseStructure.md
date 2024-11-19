# Patrón de diseño DAO

El DAO es un patrón de diseño estructural que se utiliza en el desarrollo de software para separar la lógica de acceso a los datos de la lógica de negocio de una aplicación. Su objetivo es proporcionar una interfaz abstracta para interactuar con diferentes fuentes de datos (bases de datos, archivos, servicios web, etc.), ocultando la implementación interna del acceso a los datos.

### Propósito del DAO:

1. Separación de preocupaciones: Permite que la lógica de negocio de la aplicación se mantenga independiente de los detalles de la persistencia de datos (es decir, cómo se almacenan y recuperan los datos).

2. Abstracción del acceso a datos: El DAO proporciona una capa de abstracción que facilita el acceso a datos, evitando que el código de negocio interactúe directamente con la base de datos o cualquier otra fuente de datos.

3. Facilita el mantenimiento y la escalabilidad: Si en el futuro decides cambiar el sistema de almacenamiento o la base de datos (por ejemplo, de MySQL a PostgreSQL), solo necesitarías modificar la implementación del DAO sin afectar la lógica de negocio.

### Funcionamiento del DAO:

Interfaz DAO: Define los métodos de acceso a los datos (por ejemplo, `crear()`, `leer()`, `actualizar()`, `eliminar()`).
Implementación del DAO: Esta clase implementa los métodos definidos en la interfaz DAO y contiene la lógica de acceso a los datos (como consultas SQL o llamadas a una API).

#### Ejemplo:

Supongamos que tienes una base de datos con una tabla `Usuario`. Podrías tener una interfaz `UsuarioDAO` con métodos como:

- `create()`: para insertar un nuevo usuario.
- `read()`: para obtener un usuario específico.
- `update()`: para actualizar los detalles de un usuario.
- `delete()`: para eliminar un usuario.

La implementación de esta interfaz (`UsuarioDAOImpl`) contendría el código específico para interactuar con la base de datos (por ejemplo, usando JDBC para MySQL).

Ventaja principal: Este patrón hace que tu código sea más modular, testable y fácil de mantener.

En resumen, DAO es una técnica que ayuda a manejar la persistencia de datos de manera eficiente, separando las preocupaciones entre la lógica de negocio y el acceso a los datos

#

#

Aquí tienes toda la documentación de los métodos CRUD en formato Markdown, organizada en un solo archivo:

markdown
Copiar código

# Documentación de Métodos CRUD

Esta documentación describe las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para las tablas de la base de datos `tfg_almacenDB`. Los métodos están implementados utilizando el patrón **DAO** (Data Access Object) en Java.

## Tablas:

1. **Usuarios**
2. **Productos**
3. **Palets**
4. **Movimientos**
5. **Pedidos**
6. **DetallesPedido**

## 1. Tabla `Usuarios`

### Crear un nuevo usuario

```java
public void createUsuario(Usuario usuario);
```

- **Método**: `createUsuario()`
- **Descripción**: Inserta un nuevo usuario en la base de datos.
- **Parámetros**:
  - `nombre`: Nombre del usuario.
  - `email`: Correo electrónico del usuario.
  - `contraseña`: Contraseña del usuario (en formato hasheado).
  - `id_rol`: ID del rol asignado al usuario.
- **Retorno**: El ID del usuario creado.

### Leer un usuario por ID

```java
public Usuario getUsuarioById(int id_usuario);
```

- Método: `getUsuarioById()`
- Descripción: Obtiene los detalles de un usuario por su ID.
- Parámetros:
  - `id_usuario`: El ID del usuario.
- Retorno: Un objeto Usuario que contiene los detalles del usuario.

### Leer todos los usuarios

```java
public List<Usuario> getAllUsuarios();
```

- Método: getAllUsuarios()
- Descripción: Obtiene todos los usuarios de la base de datos.
- Retorno: Una lista de objetos Usuario.

Actualizar un usuario
Método: updateUsuario()
Descripción: Actualiza los datos de un usuario existente.
Parámetros:
id_usuario: El ID del usuario a actualizar.
usuario: Un objeto Usuario con los nuevos datos.
Retorno: true si la actualización fue exitosa, false en caso contrario.

java
Copiar código
public boolean updateUsuario(int id_usuario, Usuario usuario);

Eliminar un usuario
Método: deleteUsuario()
Descripción: Elimina un usuario de la base de datos.
Parámetros:
id_usuario: El ID del usuario a eliminar.
Retorno: true si la eliminación fue exitosa, false en caso contrario.
java
Copiar código
public boolean deleteUsuario(int id_usuario); 2. Tabla Productos

## 2. Tabla `Productos`

Crear un nuevo producto
Método: createProducto()
Descripción: Inserta un nuevo producto en la base de datos.
Parámetros:
nombre_producto: Nombre del producto.
descripcion: Descripción del producto.
precio: Precio unitario del producto.
Retorno: El ID del producto creado.
java
Copiar código
public void createProducto(Producto producto);

Leer un producto por ID
Método: getProductoById()
Descripción: Obtiene los detalles de un producto por su ID.
Parámetros:
id_producto: El ID del producto.
Retorno: Un objeto Producto con los detalles del producto.
java
Copiar código
public Producto getProductoById(int id_producto);

Leer todos los productos
Método: getAllProductos()
Descripción: Obtiene todos los productos de la base de datos.
Retorno: Una lista de objetos Producto.
java
Copiar código
public List<Producto> getAllProductos();

Actualizar un producto
Método: updateProducto()
Descripción: Actualiza los datos de un producto existente.
Parámetros:
id_producto: El ID del producto a actualizar.
producto: Un objeto Producto con los nuevos datos.
Retorno: true si la actualización fue exitosa, false en caso contrario.
java
Copiar código
public boolean updateProducto(int id_producto, Producto producto);

Eliminar un producto
Método: deleteProducto()
Descripción: Elimina un producto de la base de datos.
Parámetros:
id_producto: El ID del producto a eliminar.
Retorno: true si la eliminación fue exitosa, false en caso contrario.
java
Copiar código
public boolean deleteProducto(int id_producto); 3. Tabla Palets

## 3. Tabla `Palets`

Crear un nuevo palet
Método: createPalet()
Descripción: Inserta un nuevo palet en la base de datos.
Parámetros:
id_producto: ID del producto asociado al palet.
cantidad: Cantidad de productos en el palet.
ubicacion: Ubicación del palet en el almacén.
Retorno: El ID del palet creado.
java
Copiar código
public void createPalet(Palet palet);

Leer un palet por ID
Método: getPaletById()
Descripción: Obtiene los detalles de un palet por su ID.
Parámetros:
id_palet: El ID del palet.
Retorno: Un objeto Palet con los detalles del palet.
java
Copiar código
public Palet getPaletById(int id_palet);

Leer todos los palets
Método: getAllPalets()
Descripción: Obtiene todos los palets de la base de datos.
Retorno: Una lista de objetos Palet.
java
Copiar código
public List<Palet> getAllPalets();

Actualizar un palet
Método: updatePalet()
Descripción: Actualiza los datos de un palet existente.
Parámetros:
id_palet: El ID del palet a actualizar.
palet: Un objeto Palet con los nuevos datos.
Retorno: true si la actualización fue exitosa, false en caso contrario.
java
Copiar código
public boolean updatePalet(int id_palet, Palet palet);

Eliminar un palet
Método: deletePalet()
Descripción: Elimina un palet de la base de datos.
Parámetros:
id_palet: El ID del palet a eliminar.
Retorno: true si la eliminación fue exitosa, false en caso contrario.
java
Copiar código
public boolean deletePalet(int id_palet); 4. Tabla Movimientos

## 4. Tabla `Movimientos`

Crear un nuevo movimiento
Método: createMovimiento()
Descripción: Inserta un nuevo movimiento en la base de datos.
Parámetros:
id_usuario: ID del usuario que realiza el movimiento.
id_palet: ID del palet relacionado con el movimiento.
tipo_movimiento: Tipo de movimiento ('Entrada' o 'Salida').
cantidad: Cantidad de productos movidos.
observaciones: Observaciones del movimiento.
Retorno: El ID del movimiento creado.
java
Copiar código
public void createMovimiento(Movimiento movimiento);

Leer un movimiento por ID
Método: getMovimientoById()
Descripción: Obtiene los detalles de un movimiento por su ID.
Parámetros:
id_movimiento: El ID del movimiento.
Retorno: Un objeto Movimiento con los detalles del movimiento.
java
Copiar código
public Movimiento getMovimientoById(int id_movimiento);

Leer todos los movimientos
Método: getAllMovimientos()
Descripción: Obtiene todos los movimientos de la base de datos.
Retorno: Una lista de objetos Movimiento.
java
Copiar código
public List<Movimiento> getAllMovimientos();

Actualizar un movimiento
Método: updateMovimiento()
Descripción: Actualiza los datos de un movimiento existente.
Parámetros:
id_movimiento: El ID del movimiento a actualizar.
movimiento: Un objeto Movimiento con los nuevos datos.
Retorno: true si la actualización fue exitosa, false en caso contrario.
java
Copiar código

public boolean updateMovimiento(int id_movimiento, Movimiento movimiento);
Eliminar un movimiento
Método: deleteMovimiento()
Descripción: Elimina un movimiento de la base de datos.
Parámetros:
id_movimiento: El ID del movimiento a eliminar.
Retorno: true si la eliminación fue exitosa, false en caso contrario.
java
Copiar código
public boolean deleteMovimiento(int id_movimiento); 5. Tabla Pedidos y 6. Tabla DetallesPedido

## 5. Tablas `Pedidos`

Los métodos CRUD para las tablas Pedidos y DetallesPedido siguen el mismo patrón que los anteriores, permitiendo la creación, lectura, actualización y eliminación de registros.

Leer un pedido por ID
Método: getPedidoById()
Descripción: Obtiene los detalles de un pedido por su ID.
Parámetros:
id_pedido: El ID del pedido.
Retorno: Un objeto Pedido que contiene los detalles del pedido.
java
Copiar código
public Pedido getPedidoById(int id_pedido);
Leer todos los pedidos
Método: getAllPedidos()
Descripción: Obtiene todos los pedidos de la base de datos.
Retorno: Una lista de objetos Pedido.
java
Copiar código
public List<Pedido> getAllPedidos();
Actualizar un pedido
Método: updatePedido()
Descripción: Actualiza el estado de un pedido.
Parámetros:
id_pedido: El ID del pedido a actualizar.
pedido: Un objeto Pedido con los nuevos datos.
Retorno: true si la actualización fue exitosa, false en caso contrario.
java
Copiar código
public boolean updatePedido(int id_pedido, Pedido pedido);
Eliminar un pedido
Método: deletePedido()
Descripción: Elimina un pedido de la base de datos.
Parámetros:
id_pedido: El ID del pedido a eliminar.
Retorno: true si la eliminación fue exitosa, false en caso contrario.
java
Copiar código
public boolean deletePedido(int id_pedido);

## 6. Tablas `DetallePedido`

Crear un nuevo detalle de pedido
Método: createDetallePedido()
Descripción: Inserta un nuevo detalle en la tabla DetallesPedido, asociando un palet con un pedido.
Parámetros:
id_pedido: ID del pedido.
id_palet: ID del palet que forma parte del pedido.
cantidad: Cantidad de unidades del palet en el pedido.
Retorno: El ID del detalle de pedido creado.
java
Copiar código
public void createDetallePedido(DetallesPedido detallePedido);
Leer un detalle de pedido por ID
Método: getDetallePedidoById()
Descripción: Obtiene los detalles de un pedido específico por su ID.
Parámetros:
id_detalle: El ID del detalle del pedido.
Retorno: Un objeto DetallesPedido que contiene los detalles del pedido.
java
Copiar código
public DetallesPedido getDetallePedidoById(int id_detalle);
Leer todos los detalles de un pedido
Método: getDetallesPorPedido()
Descripción: Obtiene todos los detalles de un pedido específico.
Parámetros:
id_pedido: El ID del pedido.
Retorno: Una lista de objetos DetallesPedido asociados al pedido.
java
Copiar código
public List<DetallesPedido> getDetallesPorPedido(int id_pedido);
Actualizar un detalle de pedido
Método: updateDetallePedido()
Descripción: Actualiza la cantidad de un detalle de pedido específico.
Parámetros:
id_detalle: El ID del detalle a actualizar.
detallePedido: Un objeto DetallesPedido con los nuevos datos.
Retorno: true si la actualización fue exitosa, false en caso contrario.
java
Copiar código
public boolean updateDetallePedido(int id_detalle, DetallesPedido detallePedido);
Eliminar un detalle de pedido
Método: deleteDetallePedido()
Descripción: Elimina un detalle de un pedido de la base de datos.
Parámetros:
id_detalle: El ID del detalle a eliminar.
Retorno: true si la eliminación fue exitosa, false en caso contrario.
java
Copiar código
public boolean deleteDetallePedido(int id_detalle);
