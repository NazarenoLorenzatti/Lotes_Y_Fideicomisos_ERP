# Fideicomisos ERP

----------EN DESARROLLO ------------

Sistema ERP para la gestión de fideicomisos, desarrollado con **Spring Boot**, **Angular**, **MySQL** y **Docker**.
Funcionalidades, Facturacion con Afip, Modulo de Contabilidad Integrada, Gestion de Clientes, Cobranza, Loteos y Ventas.

## 📌 Arquitectura
- **Backend (Java + Spring Boot)**
- `msvc-afip-base`: Comunicacion y consumo de Web Services ARCA. 
- `msvc-cobranza-base`: Gestion de Cobranza. 
- `msvc-accounting-base`: Modulo de contabilidad, asientos contables, reversas, reportes y devengaciones. 
- `msvc-clients-base`: ABM de clientes. 
- `msvc-invoice-base`: Facturacion contra afip y auxiliar. 
- `msvc-compras-base`: Gestion de compras y pagos a proveedores. 

- **Frontend (Angular)**
  - `ERP-BASE`: Aplicación web para usuarios finales. -- Muy poc Desarrollado -- 

- **Base de datos**
  - MySQL 8, persistencia de datos centralizada.

- **Orquestación**
  - `docker-compose` para levantar todo el ecosistema.