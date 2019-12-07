package adapter;

public class AdapterPattern {
    public static void main(String[] args) {
        Camera camera = new Camera();
        CameraAdapter captureAdapter = new RemoteCapture(camera);

        camera.capture();
        captureAdapter.capture();
    }
}
