# Cómo debe ser la etiqueta según normativa europea

La referencia que regula esto en Europa (y que usan todas las grandes empresas logísticas) es la norma **GS1-128 / SSCC (Serial Shipping Container Code)**, descrita en:

- **GS1 Logistic Label Specification (GS1 Europe, última versión 23.0, 2024)**
- **UNE-ISO 15394:2018 – Identificación de unidades logísticas mediante código de barras**

---

## 🔹 Estructura básica del código de barras GS1-128

Un palet mixto debe llevar una **etiqueta SSCC única** que lo identifique como unidad logística.

### Ejemplo visual (simplificado)


┌─────────────────────────────────────────────┐
│ SENDER: InterAlmacén Vigo S.L.              │
│ RECEIVER: Editorial Gallega S.A.            │
│ SSCC (00): 3 0845123 000000001 8            │
│ CONTENT: MIXED                              │
│ DATE: 21/10/2025                            │
│---------------------------------------------│
│ (02)08451230000123 → Producto A (GTIN)      │
│ (37)10 → Cajas                              │
│ (02)08451230000456 → Producto B (GTIN)      │
│ (37)20 → Cajas                              │
│---------------------------------------------│
│ Barcode GS1-128 con SSCC                    │
└─────────────────────────────────────────────┘

## 📋 Significado de los campos

| Elemento | AI (Application Identifier) | Descripción |
|-----------|-----------------------------|--------------|
| **SSCC** | (00) | Identificador único de la unidad logística (18 dígitos). Obligatorio. |
| **GTIN del producto** | (02) | Identificador global del artículo comercial (producto A, B...). |
| **Cantidad** | (37) | Número de unidades o cajas de ese GTIN dentro del palet. |
| **Lote / Fecha (si aplica)** | (10) / (15) | Solo si hay trazabilidad por lote o caducidad (no en tu caso). |
| **Contenido mixto** | — | Se marca en texto visible o código adicional (GS1 permite “MIXED”). |

---

En un **palet mixto**, lo más importante es que el **SSCC identifica el palet completo**,  
y en el sistema informático (tu aplicación) se asocia ese SSCC con la **lista de productos y cantidades** que contiene.
