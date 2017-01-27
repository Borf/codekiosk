import javax.json.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by johan on 2017-01-23.
 */
public class QueueOverview extends JFrame {
    public QueueOverview()
    {
        super("Queues");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        setContentPane(new JScrollPane(content));



        try {
            URL url = new URL("http://codekiosk.borf.nl/api/queues");
            URLConnection connection = url.openConnection();
            JsonReader jsonReader = Json.createReader(connection.getInputStream());
            JsonArray queue = jsonReader.readArray();
            jsonReader.close();

            for(int i = 0; i < queue.size(); i++)
            {
                JsonObject item = (JsonObject) queue.get(i);
                final String id = item.get("_id").toString();

                JPanel row = new JPanel(new GridBagLayout());
                row.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.gridx = 0;
                gbc.gridwidth = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.LINE_START;
                gbc.weightx = 0;
                gbc.ipadx = 10;

                row.add(new JLabel(item.get("_id").toString()), gbc);
                gbc.gridx = 1;
                gbc.weightx = 1;
                row.add(new JLabel(item.get("name").toString()), gbc);

                gbc.gridx = 2;
                gbc.weightx = 0;
                row.add(new JButton(new AbstractAction("Start queue")
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        dispose();
                        new QueueRunner(id);
                    }
                }));


                content.add(row);

            }





        } catch (IOException e) {
            e.printStackTrace();
        }






        setVisible(true);
    }


}
