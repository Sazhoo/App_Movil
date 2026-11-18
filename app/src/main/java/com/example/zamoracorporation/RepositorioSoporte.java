package com.example.zamoracorporation;

import java.util.ArrayList;

public class RepositorioSoporte {
    private ArrayList<TicketSoporte> listaTickets;

    public RepositorioSoporte() {
        this.listaTickets = new ArrayList<>();
    }

    public void registrarTicket(TicketSoporte ticket) {
        if (ticket != null) {
            listaTickets.add(ticket);
        }
    }

    public ArrayList<TicketSoporte> getListaTickets() {
        return listaTickets;
    }

    // Asignación automática: Compara cuántos casos tiene Juan Zamora vs Daniela Rojas
    // y asigna obligatoriamente al que tenga menos (balanceo de carga equitativo)
    public String asignarAnalistaAutomatico() {
        int countJuan = 0;
        int countDaniela = 0;

        for (TicketSoporte t : listaTickets) {
            if (t.getAnalistaAsignado().equalsIgnoreCase("Juan Zamora")) {
                countJuan++;
            } else if (t.getAnalistaAsignado().equalsIgnoreCase("Daniela Rojas")) {
                countDaniela++;
            }
        }

        if (countJuan <= countDaniela) {
            return "Juan Zamora";
        } else {
            return "Daniela Rojas";
        }
    }

    // Filtrar tickets para un administrador específico
    public ArrayList<TicketSoporte> obtenerTicketsPorAnalista(String nombreAnalista) {
        ArrayList<TicketSoporte> filtrados = new ArrayList<>();
        for (TicketSoporte t : listaTickets) {
            if (t.getAnalistaAsignado().equalsIgnoreCase(nombreAnalista)) {
                filtrados.add(t);
            }
        }
        return filtrados;
    }

    // Filtrar tickets para un usuario por su documento
    public ArrayList<TicketSoporte> obtenerTicketsPorUsuario(String documento) {
        ArrayList<TicketSoporte> filtrados = new ArrayList<>();
        for (TicketSoporte t : listaTickets) {
            if (t.getDocumentoIdentidad().trim().equals(documento.trim())) {
                filtrados.add(t);
            }
        }
        return filtrados;
    }
}