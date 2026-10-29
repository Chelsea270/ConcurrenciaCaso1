package org.test.concurrenciacaso1.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import org.test.concurrenciacaso1.model.EntradasCompartidas;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReservaController {

        @FXML
        private Label lblInicio;
        @FXML
        private Label lblDisponibles;
        @FXML
        private Label lblConfirmadas;
        @FXML
        private Label lblRechazadas;

        @FXML
        private ProgressBar pbAgente1;
        @FXML
        private ProgressBar pbAgente2;
        @FXML
        private ProgressBar pbAgente3;
        @FXML
        private Label lblAgente1;
        @FXML
        private Label lblAgente2;
        @FXML
        private Label lblAgente3;

        @FXML
        private TextArea txtRegistro;

        private static final int SolicitudesPorAgente = 10;

        private EntradasCompartidas entradas;
        private final AtomicBoolean enEjecucion = new AtomicBoolean(false);
        private int numeroSimulacion = 0;

        // Se ejecuta solo, al cargar la ventana
        @FXML
        private void initialize() {
            txtRegistro.setEditable(false);
            lblDisponibles.setText("Disponibles: " + EntradasCompartidas.totalEntradas);
            lblConfirmadas.setText("Confirmadas: 0");
            lblRechazadas.setText("Rechazadas: 0");
        }

        // Botón "Iniciar Registro"
        @FXML
        private void onIniciarRegistro() {
            if (!enEjecucion.compareAndSet(false, true)) {
                txtRegistro.appendText(" - Ya hay una simulación en curso. "
                        + "No se crearon agentes adicionales.\n");
                return;
            }

            numeroSimulacion++;
            entradas = new EntradasCompartidas();
            reiniciarInterfaz();
            lblInicio.setText("Simulación #" + numeroSimulacion + " en curso...");
            txtRegistro.appendText("---- INICIO SIMULACIÓN #" + numeroSimulacion + " ----\n");

            Thread agente1 = crearAgente("Agente 1", pbAgente1, lblAgente1);
            Thread agente2 = crearAgente("Agente 2", pbAgente2, lblAgente2);
            Thread agente3 = crearAgente("Agente 3", pbAgente3, lblAgente3);

            agente1.start();
            agente2.start();
            agente3.start();

            Thread supervisor = new Thread(() -> {
                try {
                    agente1.join();
                    agente2.join();
                    agente3.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(this::finalizarSimulacion);
            }, "Supervisor");
            supervisor.setDaemon(true);
            supervisor.start();
        }

        // Crea un hilo para un agente
        private Thread crearAgente(String nombre, ProgressBar pb, Label lbl) {
            Thread hilo = new Thread(() -> procesarSolicitudes(nombre, pb, lbl), nombre);
            hilo.setDaemon(true);
            return hilo;
        }

        // Trabajo de cada agente en segundo plano
        private void procesarSolicitudes(String agente, ProgressBar pb, Label lbl) {
            for (int i = 1; i <= SolicitudesPorAgente; i++) {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 400));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                boolean confirmada = entradas.reservar();
                int numero = i;

                Platform.runLater(() -> {
                    pb.setProgress(numero / (double) SolicitudesPorAgente);
                    lbl.setText(agente + " - solicitud " + numero + " de " + SolicitudesPorAgente);
                    txtRegistro.appendText(agente + " - solicitud " + numero + ": "
                            + (confirmada ? "CONFIRMADA" : "RECHAZADA") + "\n");
                    actualizarContadores();
                });
            }
            Platform.runLater(() -> lbl.setText(agente + " - Terminado"));
        }

        // Muestra los contadores con su título
        private void actualizarContadores() {
            EntradasCompartidas.Estado e = entradas.obtenerEstado();
            lblDisponibles.setText("Disponibles: " + e.disponibles());
            lblConfirmadas.setText("Confirmadas: " + e.confirmadas());
            lblRechazadas.setText("Rechazadas: " + e.rechazadas());
        }

        // Verificación al terminar
        private void finalizarSimulacion() {
            actualizarContadores();
            EntradasCompartidas.Estado e = entradas.obtenerEstado();

            boolean correcta = e.confirmadas() == 20
                    && e.rechazadas() == 10
                    && e.disponibles() == 0
                    && e.disponibles() >= 0
                    && e.disponibles() + e.confirmadas() == EntradasCompartidas.totalEntradas;

            txtRegistro.appendText("---- FIN SIMULACIÓN #" + numeroSimulacion + " ----\n"
                    + "Confirmadas: " + e.confirmadas()
                    + " | Rechazadas: " + e.rechazadas()
                    + " | Disponibles: " + e.disponibles() + "\n"
                    + "Disponibles + confirmadas = " + (e.disponibles() + e.confirmadas()) + "\n"
                    + "Verificación: " + (correcta ? "CORRECTA" : "ERROR") + "\n\n");

            lblInicio.setText("Simulación #" + numeroSimulacion + " finalizada");
            enEjecucion.set(false);
        }

        // Deja todo en 0 antes de cada simulacion.
        private void reiniciarInterfaz() {
            pbAgente1.setProgress(0);
            pbAgente2.setProgress(0);
            pbAgente3.setProgress(0);
            lblAgente1.setText("Agente 1");
            lblAgente2.setText("Agente 2");
            lblAgente3.setText("Agente 3");
            lblDisponibles.setText("Disponibles: " + EntradasCompartidas.totalEntradas);
            lblConfirmadas.setText("Confirmadas: 0");
            lblRechazadas.setText("Rechazadas: 0");
        }

        @FXML
        private void onLimpiarRegistro() {
            txtRegistro.clear();
       }
}

