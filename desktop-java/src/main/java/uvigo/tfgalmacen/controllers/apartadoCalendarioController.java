package uvigo.tfgalmacen.controllers;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import uvigo.tfgalmacen.Main;
import uvigo.tfgalmacen.models.Pedido;
import uvigo.tfgalmacen.database.PedidoDAO;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static uvigo.tfgalmacen.database.PedidoDAO.*;

public class apartadoCalendarioController implements Initializable {


    @FXML
    private AnchorPane root;

    private Calendar calendarPendientes, calendarCompletados, calendarEnProceso, calendarCancelados, calendarEnviados;
    private CalendarView calendarView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Crear el CalendarView
        calendarView = new CalendarView();
        calendarView.getCalendarSources().clear();
        // 2. Crear el calendario donde irán tus envíos
        calendarPendientes = new Calendar("Envíos");
        calendarCompletados = new Calendar("Completados");
        calendarEnProceso = new Calendar("En proceso");
        calendarCancelados = new Calendar("Cancelados");
        calendarEnviados = new Calendar("Enviados");

        calendarPendientes.setStyle(Calendar.Style.STYLE1);
        calendarCompletados.setStyle(Calendar.Style.STYLE2);
        calendarEnProceso.setStyle(Calendar.Style.STYLE3);
        calendarCancelados.setStyle(Calendar.Style.STYLE4);
        calendarEnviados.setStyle(Calendar.Style.STYLE5);


        // 3. Crear un CalendarSource (grupo de calendarios)
        CalendarSource calendarSource = new CalendarSource("Almacén");
        calendarSource.getCalendars().add(calendarPendientes);
        calendarSource.getCalendars().add(calendarCompletados);
        calendarSource.getCalendars().add(calendarEnProceso);
        calendarSource.getCalendars().add(calendarCancelados);
        calendarSource.getCalendars().add(calendarEnviados);

        for (Calendar c : calendarSource.getCalendars()) {
            c.setReadOnly(true);
        }

        calendarView.setShowAddCalendarButton(false);
        calendarView.setShowPrintButton(false);
        calendarView.setShowPageSwitcher(true);   // o false si no quieres cambiar día/semana/mes
        calendarView.setShowToolBar(true);       // oculta toda la barra superior
        calendarView.setShowSearchField(false);   // oculta buscador

        calendarView.setEntryFactory(param -> null);           // evitar creación de entries
        calendarView.setEntryEditPolicy(param -> false); // evitar edición
        calendarView.setEntryDetailsCallback((param) -> null); // evitar ventana de detalles


        // 4. Asociar el source al CalendarView
        calendarView.getCalendarSources().add(calendarSource);

        // 5. Fijar la fecha inicial en hoy
        calendarView.setToday(LocalDate.now());
        calendarView.setTime(LocalTime.now());
        calendarView.setShowAddCalendarButton(false);


        // 6. Meter el CalendarView dentro del BorderPane
        root.getChildren().add(calendarView);

        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);


        // 7. (Opcional) Añadir un envío de ejemplo
        cargarEnviosDesdeBD();
    }

    private void crearEnvioDeEjemplo() {
        Entry<?> envio = new Entry<>("Envío pedido #1234");

        envio.changeStartDate(LocalDate.now().plusDays(1));
        envio.changeEndDate(LocalDate.now().plusDays(1));

        envio.changeStartTime(LocalTime.of(10, 0));
        envio.changeEndTime(LocalTime.of(11, 0));

        calendarPendientes.addEntry(envio);
    }

    public void cargarEnviosDesdeBD() {
        // 1) Limpiar el calendario si quieres refrescar todo
        calendarPendientes.clear();

        List<Pedido> listaPedidos = PedidoDAO.getTodosLosPedidos(Main.connection);

        DateTimeFormatter hm = DateTimeFormatter.ofPattern("H:mm");

        for (Pedido pedido : listaPedidos) {
            // Fecha desde BD -> LocalDate
            LocalDate startDate = toLocalDate(pedido.getFechaPedido());
            if (startDate == null) continue; // si no hay fecha, lo saltamos

            // Hora de salida según tu lógica
            String horaSalidaStr = resolverHoraSalida(pedido.getHoraSalida());
            LocalTime startTime = LocalTime.parse(horaSalidaStr, hm);

            // Duración del evento (ajusta a lo que quieras)
            LocalDateTime start = LocalDateTime.of(startDate, startTime);
            LocalDateTime end = start.plusMinutes(60); // por ejemplo 1 hora


            // Crear entrada y configurar
            Entry<Pedido> entry = new Entry<>("Envío " + pedido.getCodigo_referencia());
            entry.changeStartDate(start.toLocalDate());
            entry.changeStartTime(start.toLocalTime());
            entry.changeEndDate(end.toLocalDate());
            entry.changeEndTime(end.toLocalTime());
            entry.setFullDay(false);
            // (opcional) guardar el pedido dentro para recuperarlo al click
            entry.setUserObject(pedido);

            // Añadir al calendario visible
            assert seleccionarCalendarPorEstado(pedido.getEstado()) != null;
            seleccionarCalendarPorEstado(pedido.getEstado()).addEntry(entry);
            // Alternativa equivalente:
            // entry.setCalendar(calendarPendientes);
        }
    }


    private Calendar seleccionarCalendarPorEstado(String estado) {
        return switch (estado) {
            case ESTADO_PENDIENTE -> calendarPendientes;
            case ESTADO_COMPLETADO -> calendarCompletados;
            case ESTADO_EN_PROCESO -> calendarEnProceso;
            case ESTADO_CANCELADO -> calendarCancelados;
            case ESTADO_ENVIADO -> calendarEnviados;
            default -> null;
        };
    }

    /**
     * Convierte java.util.Date / java.sql.Date a LocalDate de forma segura.
     */
    private static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date sql) return sql.toLocalDate();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Tu lógica actual para determinar la hora de salida. Ajusta si HORAS_VALIDAS cambia.
     */
    private String resolverHoraSalida(String horaSalidaBD) {
        if (horaSalidaBD == null) {
            return "9:00";
        } else if (horaSalidaBD.equals(HORAS_VALIDAS.get(0))) {
            return "11:00";
        } else {
            return "17:00";
        }
    }

}
