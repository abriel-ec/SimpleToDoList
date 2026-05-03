# Plan de transformación: SimpleToDoList → App estilo OneNote

> Documento de análisis (sin código). Sirve como hoja de ruta para el equipo antes de empezar a programar.

---

## 1. Visión del proyecto

Transformar la app actual (lista de tareas simple) en una aplicación tipo **OneNote / Evernote**, donde cada usuario pueda:

- **Iniciar sesión** con su cuenta personal.
- Crear y organizar **cuadernos** (notebooks).
- Dentro de cada cuaderno, crear **secciones** (capítulos / categorías).
- Dentro de cada sección, crear **páginas o notas** con título y contenido.

La idea es pasar de una sola lista plana a una **estructura jerárquica de 3 niveles**:

```
Usuario
 └── Cuaderno
      └── Sección
           └── Página (nota)
```

---

## 2. Estado actual vs. objetivo

| Aspecto | Estado actual | Objetivo |
|---|---|---|
| Usuarios | No existen, app abierta | Login y registro obligatorios |
| Estructura de datos | Una sola lista de tareas | Cuadernos → Secciones → Páginas |
| Backend | JSON Server (mock local) | A decidir (ver sección 4) |
| Persistencia offline | Ninguna | A decidir (Room recomendado más adelante) |
| Pantallas | 3 (Home, Add, Detail) | ~7-8 pantallas |
| Navegación | Plana | Jerárquica con parámetros |
| Arquitectura | MVVM básico | MVVM + Repository |

---

## 3. Modelo de datos propuesto

Estas son las **entidades** mínimas. Cada entidad equivale a una "tabla" o colección.

### 3.1. User (Usuario)
- `id` — identificador único
- `email` — correo único
- `passwordHash` — contraseña cifrada (nunca en texto plano)
- `name` — nombre visible
- `createdAt` — fecha de creación

### 3.2. Notebook (Cuaderno)
- `id`
- `userId` — relación con el dueño
- `title` — nombre del cuaderno
- `color` — opcional, para diferenciarlos visualmente
- `createdAt`, `updatedAt`

### 3.3. Section (Sección)
- `id`
- `notebookId` — a qué cuaderno pertenece
- `title`
- `order` — para ordenarlas
- `createdAt`, `updatedAt`

### 3.4. Page (Página / Nota)
- `id`
- `sectionId` — a qué sección pertenece
- `title`
- `content` — texto de la nota (empezamos con texto plano)
- `createdAt`, `updatedAt`

### 3.5. Relaciones
- 1 Usuario tiene N Cuadernos
- 1 Cuaderno tiene N Secciones
- 1 Sección tiene N Páginas

> **Nota didáctica:** estas relaciones se llaman *uno-a-muchos*. Cada hijo guarda el `id` del padre.

---

## 4. Decisiones técnicas pendientes (a discutir en equipo)

Estas son **bifurcaciones** en el camino. Cada una tiene pros y contras. Hay que elegir antes de empezar a codear.

### 4.1. ¿Dónde guardamos los datos?

| Opción | Pros | Contras | Dificultad |
|---|---|---|---|
| **A. Seguir con JSON Server** | Simple, ya está montado, fácil de compartir el JSON | No tiene auth real, todos ven todo, no es producción | Baja |
| **B. Firebase (Auth + Firestore)** | Auth real listo, sincroniza solo, gratis al inicio | Hay que aprender Firebase, depende de Google | Media |
| **C. Backend propio (Node + DB real)** | Control total, aprenden full-stack | Mucho trabajo extra, hay que hostearlo | Alta |
| **D. Solo Room (BD local en el celu)** | No necesita internet, rápido | No hay multi-dispositivo ni login real | Media |

> **Recomendación didáctica:** empezar con **A (JSON Server)** para entender el flujo, y luego migrar a **B (Firebase)** cuando dominen el patrón Repository. Eso convierte el cambio de backend en un ejercicio de aprendizaje.

### 4.2. ¿Cómo manejamos la autenticación?

- Si eligen **Firebase**, viene resuelto.
- Si siguen con **JSON Server**, hay que simularlo (poco realista pero educativo).
- Tema clave: **nunca guardar contraseñas en texto plano**, siempre hash (BCrypt o similar).

### 4.3. ¿Persistencia offline?

- Ideal: **Room** como caché local + sincronización con el backend.
- Para una primera versión se puede omitir y agregarlo en una segunda iteración.

### 4.4. Editor de notas: ¿texto plano o rich text?

- **Fase 1:** texto plano (como ahora).
- **Fase 2 (opcional):** Markdown o rich text con formato.
- OneNote real soporta dibujos, imágenes, listas — eso queda **fuera de alcance** para no morir en el intento.

---

## 5. Pantallas nuevas (mapa de navegación)

```
┌─────────────────┐
│   Splash/Login  │ ← entrada
└────────┬────────┘
         │
         ├──→ Register
         │
         ▼
┌─────────────────┐
│  NotebookList   │ ← cuadernos del usuario
└────────┬────────┘
         │ (toca un cuaderno)
         ▼
┌─────────────────┐
│  SectionList    │ ← secciones del cuaderno
└────────┬────────┘
         │ (toca una sección)
         ▼
┌─────────────────┐
│   PageList      │ ← páginas de la sección
└────────┬────────┘
         │ (toca una página)
         ▼
┌─────────────────┐
│   PageEditor    │ ← ver/editar contenido
└─────────────────┘
```

Pantallas estimadas: **7 principales** + diálogos para crear/renombrar/borrar.

---

## 6. Arquitectura propuesta

Mantener **MVVM**, pero agregar una capa intermedia: **Repository**.

```
UI (Composables)
    ↕
ViewModel (estado + lógica de pantalla)
    ↕
Repository (decide: ¿pido a la API o a la BD local?)
    ↕
DataSource (Retrofit / Room / Firebase)
```

**¿Por qué Repository?**
Porque cuando tengas Room + API a la vez, el ViewModel no debe preocuparse de dónde vienen los datos. El Repository es el que decide.

> Esto se introduce **en la Fase 3**, no desde el principio. Primero hay que entender por qué duele no tenerlo.

---

## 7. Roadmap por fases

División sugerida en **fases pequeñas y entregables**. Cada fase debería poder mergearse a `main` y la app debería seguir funcionando.

### Fase 0 — Preparación (1-2 días)
- Crear repo en GitHub (si no existe ya).
- Definir convenciones (commits, branches, formato).
- Limpiar código de plantilla (`Greeting`, `GreetingPreview` en `MainActivity`).
- Decidir backend (sección 4.1).

### Fase 1 — Modelo y backend ampliados
- Definir entidades `User`, `Notebook`, `Section`, `Page`.
- Adaptar JSON Server (o configurar Firebase).
- Crear interfaces de API para cada entidad.

### Fase 2 — Autenticación
- Pantallas Login y Register.
- Lógica de sesión (guardar token / userId).
- Redirección según si hay sesión activa.

### Fase 3 — Cuadernos
- Pantalla NotebookList.
- Crear, renombrar y eliminar cuadernos.
- Filtrar por `userId` (cada usuario ve solo los suyos).
- Introducir capa Repository.

### Fase 4 — Secciones
- Pantalla SectionList (al entrar a un cuaderno).
- CRUD de secciones.
- Navegación con parámetros (`notebookId`).

### Fase 5 — Páginas / Notas
- Pantalla PageList y PageEditor.
- CRUD de páginas.
- Guardado automático o manual.

### Fase 6 — Pulido
- Mejoras de UX (loaders, errores, confirmaciones).
- Tema visual consistente.
- Posible: persistencia offline con Room.

---

## 8. Trabajo en equipo (¡importante!)

Como son **2 personas trabajando juntos** y necesitan que ambos suban commits de forma equilibrada, estas son las pautas:

### 8.1. Estrategia de branches

```
main          ← rama estable, siempre debe compilar
 ├── dev      ← rama de integración (opcional)
 ├── feat/login
 ├── feat/notebooks
 ├── feat/sections
 └── fix/...
```

**Regla:** nadie hace commits directos a `main`. Todo entra por **Pull Request**.

### 8.2. Convención de commits

Recomendado: **Conventional Commits**.

| Prefijo | Uso |
|---|---|
| `feat:` | Nueva funcionalidad |
| `fix:` | Corrección de bug |
| `refactor:` | Reorganización sin cambiar comportamiento |
| `docs:` | Cambios en documentación |
| `style:` | Formato (no afecta lógica) |
| `chore:` | Configuración, dependencias |

Ejemplos:
- `feat(notebooks): agregar pantalla de lista de cuadernos`
- `fix(login): corregir validación de email vacío`
- `docs: actualizar plan_onenote.md`

### 8.3. División de tareas (sugerencia)

Dos formas de dividir:

**Opción A — por capa (vertical):**
- Persona 1: backend, modelos, ViewModels.
- Persona 2: UI, Compose, navegación.
- Riesgo: mucha dependencia entre ambos.

**Opción B — por feature (recomendada):**
- Persona 1: Login + Cuadernos.
- Persona 2: Secciones + Páginas.
- Cada uno hace su feature *completa* (modelo + VM + UI).
- Más independencia, menos bloqueos.

### 8.4. Sincronización de commits

Para que ambos tengan **contribución equilibrada y visible** (importante si lo evalúan):

1. **Hacer pull antes de empezar** cada sesión: `git pull origin main`.
2. Trabajar en su propia branch (`feat/...`).
3. Hacer **commits pequeños y frecuentes** (cada paso lógico, no esperar a tener todo listo).
4. Subir su branch a remoto (`git push`) al final de cada sesión.
5. Abrir Pull Request → el otro lo revisa → mergean a `main`.
6. Coordinar para no tocar los **mismos archivos** al mismo tiempo (causa conflictos).

### 8.5. Reglas de oro

- **No mergear sin revisar el PR del otro.** La revisión también es aprendizaje.
- **Un commit = una idea clara.** Evitar "varios cambios" en un commit.
- **Si hay conflicto, hablarlo antes de resolverlo.** No descartar el trabajo del otro.
- **Documentar decisiones** en este `.md` (o en otros) para que ambos estén alineados.

---

## 9. Riesgos y consideraciones

| Riesgo | Mitigación |
|---|---|
| Sobre-alcance ("queremos hacer todo OneNote") | Respetar fases, recortar features |
| Conflictos de Git por trabajar el mismo archivo | División clara de features |
| JSON Server no es realista para auth | Tener claro que es educativo, no producción |
| El editor de notas se complica | Empezar con texto plano, no rich text |
| Falta de pruebas | Al menos validar manualmente cada fase antes de mergear |

---

## 10. Próximos pasos inmediatos

Antes de tocar código, el equipo debería:

1. ☐ Leer este documento juntos y discutir las **decisiones de la sección 4**.
2. ☐ Elegir backend definitivo (4.1).
3. ☐ Decidir cómo dividir tareas (8.3 — Opción A o B).
4. ☐ Configurar el repo: branches `main`/`dev`, plantilla de PR (opcional).
5. ☐ Crear un tablero de tareas (GitHub Projects, Trello, Notion) para tener visibilidad.
6. ☐ Definir la **Fase 0** como primer entregable conjunto.

---

> 📌 Este documento es **vivo**: actualícenlo cuando tomen decisiones o cambien de rumbo.
guia del profe 
> En esta primera actividad se pretende desarrollar una pequeña aplicación móvil nativa Android de TEMÁTICA LIBRE en la que se apliquen los conocimientos teóricos y prácticos trabajados en esta primera parte. De forma paralela, al ser una actividad de elaboración en grupo se trabajará con herramientas y metodologías que favorezcan la gestión de las tareas y el seguimiento del desarrollo realizado.



REQUISITOS DE DISEÑO
El lenguaje base de desarrollo será Kotlin
Se programarán al menos 3 pantallas/Interfaces. Para la definición de los interfaces se usará JetPack Compose.
Se crearán al menos dos componentes reutilizables. Cada componente se considera reutilizado cuando al menos se utiliza en un screen.
Se usará tanto los recursos  de la aplicación como los del Theme definiendo tanto colores, como strings como imágenes en estos lugares
El modelo arquitectónico será el MVVM, por lo tanto: se definirá una estructura de paquetes que separe la capa de datos de la de vista y la de la lógica, se definirá un ViewModel para cada Screen o View, separando en ficheros diferentes cada uno de los elementos, se definirán todos los fichero y clases necesarios para realizar dicha separación.
La fuente de datos puede ser: Almacenado y usado a través de SQLite, a través de ApiRestFul o combinación de cualquiera de las situaciones anteriores.


REQUISITOS DE CALIDAD
El nombre de las variables será significativo, tanto en los identificadores de componentes como en los objetos y variables, intentando utilizar nopmbrado camelCase.
El nombre de las Clases será significativo y seguirá las pautas marcadas en los ejemplos de la asignatura
Se introducirá comentarios al menos en las cabeceras de las clases describiendo el funcionamiento de la clase
Se modularizará y crearán funciones para hacer más legible y reutilizable el código


REQUISITOS DE TRABAJO EN EQUIPO
Se utilizará git y se almacenará el desarrollo en un repositorio remoto, realizando commits individales por cada miembro, pgomezarnalVIU


REQUISITOS DEL ENTREGABLE
El entregable consistirá en un único documento en PDF que contendrá los siguientes elementos
Portada con título de la asignatura, título de la aplicación, miembros y enlace al git de la aplicación (1 página)
Introducción con breve descripción de la motivación y objetivos de la aplicación (1 página máximo)
Tercer apartado con aplicación en funcionamiento
Bien a través de capturas ( máximo 2 páginas).
Bien a través de enlace a un vídeo (máximo 2 minutos)
El repositorio será compartido con el usuario pgomezarnalVIU  