# TFG-EyA-Uvigo-Jose-Torre



La ampliación se trata de un app de escritorio hecha con Java y la librería gráfica JavaFX





![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-34-24-image.png)

Esta aplicación sirve para gestionar *Almacenes de Consolidación* con dos salidas programadas por día.



- **¿Que es un almacén de consolidación?**: Los almacenes de consolidación son aquellas instalaciones en las que se reciben pedidos individuales (pallets de un único producto), provenientes de diferentes proveedores, centros logísticos o clientes. Estos productos son agrupados y enviados de manera conjunta, con el objetivo de facilitar su transporte.

<img src="https://github.com/AmasterJT/TFG-EyA-Uvigo-Jose-Torre/blob/main/Presentacion-Proyecto/imagenes/Funcionamiento-de-un-almac%C3%A9n-%20de-consolidaci%C3%B3n.png?raw=true" title="" alt="Funcionamiento-de-un-almacén- de-consolidación.png" data-align="center">





**¿Qué hace la aplicación de escritorio?**

- **<mark>Recepción de pallets de productos individuales</mark>:** introducción de nuevos pallets en el almacén.

<img src="file:///C:/Users/joset/AppData/Roaming/marktext/images/2025-11-27-19-40-13-image.png" title="" alt="" data-align="center">

- <mark>Gestión de Pedidos</mark>: los operarios crean los pallets que se vana enviar a los clientes según el pedido recibido.
  
  - <mark>Modificación de los pallets de productos originales</mark>: Cuando un operario modifica la cantidad de algún pallet actualizamos el contenido del pallet de producto individual.
  
  - <mark>Generación de etiquetas SSCC:</mark> Etiquetas para los pallets que se van a enviar.
  
  - También permite ver si hay algunos productos ya introducidos en el pallet que es operario esta haciendo.
  
  
  
  Asignación de pedidos
  
  ![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-41-10-image.png)
  
  
  
  actualización de pallets de producto individual
  
  <img src="file:///C:/Users/joset/AppData/Roaming/marktext/images/2025-11-27-19-41-01-image.png" title="" alt="" data-align="center">
  
  Creación de los pallets para enviar
  
  ![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-46-05-image.png)
  
  
  
  Etiquetado de los pallets y envío
  
  ![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-47-05-image.png)
  
  

- <mark>Reestructuración del almacén:</mark> Permite cambiar la ubicación de los pallets dentro del almacén, eliminar los pallets (ya sea porque se acabo la cantidad o alguna otra razón).
  
  <img src="file:///C:/Users/joset/AppData/Roaming/marktext/images/2025-11-27-19-42-55-image.png" title="" alt="" data-align="center">

- <mark>Generación de la Orden de compra:</mark> Orden para solicitar nuevos pallets de producto individual según las necesidades a nuestros proveedores
  
  ![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-42-30-image.png)

- <mark>Exportación de Data a excel:</mark> Generamos un libro con la información necesaria para hacer estudios en excel (Por defecto el fichero se guarda en la carpeta Descargas).

![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-44-14-image.png)



También Podemos hacer operaciones secundarias como:

- Crear, editar y eliminar usuarios.

- Crear, editar y eliminar pedidos.

- crear nuevos productos (son los productos contenidos en los pallets de producto individual).

- Crear Tipo (Clasificación interna sobre el tipo de pallets de producto individual).

![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-48-23-image.png)





- Ejemplo de la ventana de creación de usuarios:
  
  
  
  <img title="" src="file:///C:/Users/joset/AppData/Roaming/marktext/images/2025-11-27-19-49-36-image.png" alt="" data-align="center">



Tiene también ayudas visuales para visuales para ver como están distribuidos los pallets de producto individual en el almacén y un apartado de inventario para ver donde están los pallets en el almacén.



inventario con filtros

![](C:\Users\joset\AppData\Roaming\marktext\images\2025-11-27-19-37-53-image.png)



<mark>TODAS ESTAS FUNCIONALIDADES ESTARÁN LIMITADAS POR LA GESTION DE ROLOES DE USUARIO</mark>.


