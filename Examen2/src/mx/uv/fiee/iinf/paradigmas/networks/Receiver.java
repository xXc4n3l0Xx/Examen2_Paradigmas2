package mx.uv.fiee.iinf.paradigmas.networks;

import java.io.IOException;
import java.net.Socket;
import mx.uv.fiee.iinf.paradigmas.networks.models.Persona;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.EOFException;

/**
 * Clase principal que recibe objetos serializados desde un socket.
 */
public class Receiver {

    /**
     * Método principal que inicializa la conexión y recibe objetos.
     */
    public static void main(String[] args) {

        SocketUtils utils = new SocketUtils ("localhost", 19000);
        utils.Receive ();

    }

    /**
     * Clase interna para manejar la conexión del socket y la recepción de datos.
     */
    private static class SocketUtils {

        private SSLSocket socket;

        /**
         * Constructor que inicializa un socket cliente para conectarse a un servidor.
         * @param address Dirección del servidor.
         * @param port Puerto del servidor.
         */
        public SocketUtils(String address, int port) {
            System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "contraPOO2");

            try {
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = (SSLSocket) factory.createSocket(address, port);
                System.out.println("Conexión segura establecida con el servidor.");
            } catch (IOException e) {
                throw new RuntimeException("No se pudo conectar al servidor TLS", e);
            }
        }

        /**
         * Método para recibir objetos serializados desde el socket.
         */

        public void Receive() {
            try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                while (true) {
                    try {
                        Persona p = (Persona) ois.readObject();
                        System.out.println("Received UUID: " + p.getUuid());
                    } catch (ClassNotFoundException | EOFException e) {
                        System.out.println("Fin del flujo o clase no encontrada.");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        }
}