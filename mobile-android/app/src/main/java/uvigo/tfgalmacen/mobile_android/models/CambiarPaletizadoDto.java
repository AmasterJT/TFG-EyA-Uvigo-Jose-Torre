package uvigo.tfgalmacen.mobile_android.models;

public class CambiarPaletizadoDto {
    private boolean paletizado;

    public CambiarPaletizadoDto(boolean paletizado) {
        this.paletizado = paletizado;
    }

    public boolean isPaletizado() { return paletizado; }
}
