import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Client extends JFrame implements MouseMotionListener {

    static final int w = 1366;
    static final int h = 768;
    private static DatagramSocket socket;
    private static InetAddress address;

    static BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

    private static byte[] buf = new byte[8];

    public Client() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    public static void sendToServer(int x, int y) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(x);
        buffer.putInt(y);
        buf = buffer.array();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
    }

    public static void receivedFromServer() throws IOException {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
        int x = buffer.getInt();
        int y = buffer.getInt();
        img.setRGB(x, y, 0xFFFF0000);

    }

    public static void draw(Graphics2D g) {
        g.drawImage(img, 0, 0, null);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Client jf = new Client();
        jf.setSize(w, h);//размер экрана
        jf.setUndecorated(false);//показать заголовок окна
        jf.setTitle("Моя супер программа");
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.createBufferStrategy(2);
        jf.addMouseMotionListener(jf);
        //в бесконечном цикле рисуем новый кадр
        while (true) {
            receivedFromServer();
            long frameLength = 1000 / 60; //пытаемся работать из рассчета  60 кадров в секунду
            long start = System.currentTimeMillis();
            BufferStrategy bs = jf.getBufferStrategy();
            Graphics2D g = (Graphics2D) bs.getDrawGraphics();
            g.clearRect(0, 0, jf.getWidth(), jf.getHeight());
            draw(g);

            bs.show();
            g.dispose();

            long end = System.currentTimeMillis();
            long len = end - start;
            if (len < frameLength) {
                Thread.sleep(frameLength - len);
            }
        }

    }


    @Override
    public void mouseDragged(MouseEvent e) {
        try {
            sendToServer(e.getX(), e.getY());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
