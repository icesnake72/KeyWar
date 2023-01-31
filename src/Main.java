public class Main {
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
        mainFrame.Create("Key War", 500, 500);
        Thread winThread = new Thread(mainFrame);
        winThread.start();
    }
}