package test;

import view.Controller;
import model.CommunicationChannel;

public class TestMain extends Controller {

    public static void main(String[] args) {
        new TestMain();
    }

    public TestMain() {

        try {
            CommunicationChannel c1 = new CommunicationChannel(this);
            CommunicationChannel c2 = new CommunicationChannel(this);

            c1.waitForConnection(6666);
            Thread.sleep(4000);
            c2.connectTo("127.0.0.1", 6666);
            Thread.sleep(1000);

            c1.sendMessage("Hallo von c1");
            Thread.sleep(1000);
            c2.sendMessage("Hallo von c2");

            c1.stop();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void receiveMessage(String m) {
        System.out.println("Received: "+ m);
    }
}
