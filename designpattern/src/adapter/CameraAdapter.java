package adapter;

public interface CameraAdapter {
    void capture();
}

class RemoteCapture implements CameraAdapter {
    private Camera camera;

    public RemoteCapture(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void capture() {
        System.out.println("remoteCapture button pressed");
        camera.capture();
    }
}
