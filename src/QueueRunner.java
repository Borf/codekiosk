import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.json.*;


/**
 * Created by johan on 2017-01-23.
 */
public class QueueRunner extends JFrame
{

    Dimension res;

    int timeLeft = 30;
    int posY = 0;
    int posTimer = 120;
    Process p;

    String id;


    String title = "Downloading.....";



    public QueueRunner(String id)
    {
        this.id = id;
        res = Toolkit.getDefaultToolkit().getScreenSize();


        setUndecorated(true);
        setBackground(new Color(0,0,0,0));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new JPanel()
        {
            public void paintComponent(Graphics g)
            {
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(new Color(0,0,0,0.0f));
                g2.setComposite(AlphaComposite.Clear);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(AlphaComposite.Src);
                super.paintComponent(g);

                g2.setFont(new Font("Calibri", Font.PLAIN, 72));

                g2.setColor(new Color(0.0f, 0.f, 0.0f, 0.5f));
                g2.fill(new RoundRectangle2D.Double(0, res.height-40-72+posY, 700, 40+72, 10, 10));
                g2.setColor(Color.white);
                g2.drawString(title, 0, res.height-40+posY);


                g2.setColor(new Color(0.0f, 0.f, 0.0f, 0.5f));
                g2.fill(new RoundRectangle2D.Double(res.width -120+ posTimer, res.height-40-72, 120, 40+72, 10, 10));
                g2.setColor(Color.white);
                g2.drawString(timeLeft + "", res.width - 120+posTimer, res.height-40);

                g2.setFont(new Font("Calibri", Font.PLAIN, 22));
                g2.drawString("door Johan Talboom", 0, res.height-10+posY);

            }
        });

        new Timer(1000/60, e ->
        {
            if(posY > 0)
                posY--;
            else
                if(posTimer > 0)
                    posTimer--;

            if(!p.isAlive())
            {
                startNext();
            }


            repaint();
        }).start();


        new Timer(1000, e -> { if(timeLeft > 0) timeLeft--;} ).start();

        startNext();


        getContentPane().setBackground(new Color(0,0,0,0.0f));

        setAlwaysOnTop(true);
        setSize(res.width, res.height);
        setVisible(true);
    }

    private void startNext() {
        try {
            posY = 0;
            URL url = new URL("http://codekiosk.borf.nl/api/queue/" + id + "/next");
            URLConnection connection = url.openConnection();
            JsonReader jsonReader = Json.createReader(connection.getInputStream());
            JsonObject item = jsonReader.readObject();
            jsonReader.close();

            System.out.println(item);
            System.out.println("Getting " + item.get("url").toString());

            URL jarUrl = new URL(item.get("url").toString());
            URLConnection con = jarUrl.openConnection();
            con.setUseCaches(false);
            ReadableByteChannel rbc = Channels.newChannel(con.getInputStream());
            FileOutputStream fos = new FileOutputStream("./tmp.jar");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();

            title = item.get("title").toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        posY = 120;
        posTimer = 120;
        timeLeft = 30;
        try {
            p = Runtime.getRuntime().exec("java -jar RunSandboxed.jar tmp.jar");
        } catch (IOException ee) {
            ee.printStackTrace();
        }

    }
}
