package org.test.concurrenciacaso1.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EntradasCompartidas {
    public static final int totalEntradas = 20;

    private int disponibles = totalEntradas;
    private int confirmadas = 0;
    private int rechazadas = 0;

    private final Lock lock =  new ReentrantLock();

    public synchronized boolean reservar(){
        if (disponibles > 0) {
            disponibles--;
            confirmadas++;
            return true;
        }
        rechazadas++;
        return false;
    }

    public synchronized Estado obtenerEstado(){
        return new Estado(disponibles, confirmadas, rechazadas);
    }
    public record Estado(int disponibles, int confirmadas, int rechazadas)
    {
    }

}
