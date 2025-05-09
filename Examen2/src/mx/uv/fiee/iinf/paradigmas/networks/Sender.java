package mx.uv.fiee.iinf.paradigmas.networks;

import mx.uv.fiee.iinf.paradigmas.networks.models.Persona;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.Random;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Clase principal que envía objetos serializados a través de la red.
 */
public class Sender {
    static int PORT = 19000;

    /**
     * Método principal que genera objetos Persona y los envía a través de un socket.
     */
    public static void main(String[] args) throws IOException {

        SocketUtils utils = new SocketUtils ();
        Random random = new Random ();

        ByteArrayOutputStream baos = new ByteArrayOutputStream ();
        ObjectOutputStream oos = new ObjectOutputStream (baos);

        while (true) {

            Persona p = new Persona ();
            p.setFullname("Random Name " + random.nextInt (1000)); // Random fullname
            p.setAge(random.nextInt (55)); // Random age between 0 and 55
            p.setUuid (UUID.randomUUID ().toString()); // Random UUID

            oos.writeObject (p);
            oos.flush ();

            byte[] bytes = baos.toByteArray ();
            System.out.println ("Sending object with uuid: " + p.getUuid ());
            utils.Send (bytes);

            baos.reset ();

        }

    }

    /**
     * Clase interna para manejar la conexión del socket y el envío de datos.
     */
    private static class SocketUtils {
        private final SSLSocket socket;

        /**
         * Constructor que inicializa un ServerSocket y espera una conexión entrante.
         * @throws IOException Si ocurre un error al crear el socket.
         */
        SocketUtils() throws IOException {
            System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "contraPOO2");

            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(PORT);

            System.out.println("Esperando conexión segura (TLS)...");
            socket = (SSLSocket) serverSocket.accept();
            System.out.println("Conexión segura establecida.");

            serverSocket.close();
        }

        /**
         * Método para enviar datos a través del socket.
         * @param data Los datos en formato de arreglo de bytes.
         * @throws IOException Si ocurre un error al enviar los datos.
         */
        void Send(byte[] data) throws IOException {
            try {
                System.out.println("Data length: " + data.length);
                socket.getOutputStream().write(data, 0, data.length);
                socket.getOutputStream().flush();
                Thread.sleep(1000);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error sending data (IOException)");
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                throw new RuntimeException("Error sending data (InterruptedException)");
            }
        }
    }

}