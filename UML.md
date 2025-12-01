@startuml

class Paradero {
  - id: int
  - nombre: String
  - cola: Queue<Pasajero>
  - posicionX: double
  - posicionY: double
  + agregarPasajero(Pasajero)
  + removerPasajero(): Pasajero
}

class Pasajero {
  - id: int
  - destino: Paradero
  - tiempoLlegada: int
}

class Arco {
  - origen: Paradero
  - destino: Paradero
  - tiempo: int
}

class Grafo {
  - paraderos: List<Paradero>
  - adj: Map<Paradero, List<Arco>>
  + agregarParadero(Paradero)
  + agregarArco(Paradero,Paradero,int)
  + dijkstra(Paradero,Paradero)
}

class Bus {
  - id: int
  - capacidadMax: int = 40
  - pasajeros: List<Pasajero>
  - rutaIda: List<Paradero>
  - rutaVuelta: List<Paradero>
  - indiceActual: int
  - enIda: boolean
  + avanzar10Min()
  + subirPasajeros()
  + bajarPasajeros()
}

class Simulador {
  - grafo: Grafo
  - buses: List<Bus>
  - tiempoActual: int
  - tiemposEspera: Map<Paradero>, List<<integer>>
  + tick()
}

class GUI {
  - simulador: Simulador
  + dibujarGrafo()
  + animarBus(Bus)
}

class Main {
  + main(String[])
}

Grafo *-- Paradero
Grafo *-- Arco
Paradero o-- Pasajero
Bus o-- Pasajero
Simulador *-- Bus
Bus --> Grafo
GUI --> Simulador

@enduml
