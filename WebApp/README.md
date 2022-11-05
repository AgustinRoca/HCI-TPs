# GUIA PARA HACER EL TP

## Como correrlo

- Instalar Node y NPM --> Generalmente Node viene con NPM
- En la carpeta WebApp escribir (solo la primera vez) --> npm install
- En la carpeta WebApp escribir (cada vez que se quiere correr) --> npm run dev
- Con un browser abrir el localhost que dice la terminal

Tener en cuenta de que el "npm install" tarda bastante la primera vez

## Estructura

### App.vue

En la carpeta "components", es el archivo principal. Este archivo tiene 3 partes principales, el Navigation Drawer, el AppBar y la core View.

El archivo dice de donde se saca cada componente

### paths.js

En la carpeta "components/router", es el archivo que se encarga de routear toda la pagina. Todas las rutas disponibles van a estar ahi. Si hay que agregar un ruteo se agrega aca.

Path --> Path que se va a usar
Name --> Nombre del path
View --> Nombre de la vista que va a cargar

### views

En la carpeta "components", es la carpeta donde estan todas las vistas

### core

En la carpeta "components", es la carpeta donde estan los componentes principales que se van a usar, los que referencia App.vue.
