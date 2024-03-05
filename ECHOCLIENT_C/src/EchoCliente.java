/*
TRABAJO REALIZADO POR CARLOS MAURICIO MÉNDEZ HERNÁNDEZ
FECHA DE ELABORACIÓN: 4 DE MARZO DE 2024
PROGRAMACIÓN AMBIENTE CLIENTE/SERVIDOR
* */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EchoCliente extends JFrame {
    private JTextField txtCliente;
    private JButton btnEnviar;
    private JTextPane txtpServidor;
    private Socket socket;
    private BufferedReader sockInput;
    private PrintWriter sockOutput;

    public EchoCliente() {
        inicializarComponentesGUI();
        conectarServer();
        escucharServer();
    }
//iniciar todos los componentes
    private void inicializarComponentesGUI() {
        txtCliente = new JTextField();
        btnEnviar = new JButton("Enviar");
        txtpServidor = new JTextPane();
        txtpServidor.setPreferredSize(new Dimension(400, 300)); // Tamaño de ejemplo

        // layout
        setLayout(new BorderLayout());
        add(txtCliente, BorderLayout.NORTH);
        add(new JScrollPane(txtpServidor), BorderLayout.CENTER);
        add(btnEnviar, BorderLayout.SOUTH);

        // botoncito para enviar el texto
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensaje();
            }
        });

        //ventana (método para cerrar por botón, tamaño y visibilidad)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setVisible(true);
    }
//metodo para conectar con el servidor
    private void conectarServer() {
        try {
            byte[] byteIP = new byte[]{52, 43, 121, 77};
            InetAddress ip = InetAddress.getByAddress(byteIP);
            int puerto = 9001;
            socket = new Socket(ip, puerto);
            sockOutput = new PrintWriter(socket.getOutputStream(), true);
            sockInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "No se puede identificar el host: " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error de IO: " + e.getMessage());
        }
    }
    //metodo para enviar mensaje
    private void enviarMensaje() {
        String mensaje = txtCliente.getText();
        if (!mensaje.isEmpty()) {
            mensaje("Cliente: " + mensaje + "\n"); //este es para el mensaje del cliente
            sockOutput.println(mensaje); //este es mensaje del servidor
            txtCliente.setText("");
        }
    }
//metodo para escuchar el servidor
    private void escucharServer() {
        new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = sockInput.readLine()) != null) {
                    mensaje("Servidor: " + mensaje + "\n");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al leer del servidor: " + e.getMessage());
            }
        }).start();
    }
    //mostrar mensaje
    private void mensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> txtpServidor.setText(txtpServidor.getText() + mensaje));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EchoCliente();
            }
        });
    }
}

