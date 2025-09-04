package com.ar.base.services.impl;

import com.ar.base.DTOs.ResultadoTransferenciaDTO;
import com.ar.base.entities.*;
import com.ar.base.entities.AsientoContable.Estado;
import com.ar.base.entities.AsientoContable.TipoAsiento;
import com.ar.base.entities.AsientoContable.TipoOperacion;
import com.ar.base.repositories.*;
import com.ar.base.responses.BuildResponsesServicesImpl;
import jakarta.transaction.Transactional;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ar.base.services.iTransferenciaInternaService;
import java.time.LocalDate;

@Service
@Slf4j
public class TransferenciaInternaServiceImpl extends BuildResponsesServicesImpl implements iTransferenciaInternaService {

    @Autowired
    private iAsientoContableDao asientosDao;

    @Autowired
    private iMovimientoContableDao movimientosDao;

    @Autowired
    private iCuentaContableDao cuentaContableDao;

    @Autowired
    private iTiposDeEntidadesDao tipoEntidadDao;

    @Autowired
    private iTransferenciaInternaDao transfInternaDao;

    private static final String CODIGO_OK = "00";
    private static final String CODIGO_NOK = "02";
    private static final String CODIGO_ERROR = "-01";

    @Override
    @Transactional
    public ResponseEntity<?> registrarTransferenciaInterna(TransferenciaInterna transferenciaInterna) {
        try {
            CuentaContable cuentaOrigen = cuentaContableDao.findById(transferenciaInterna.getCuentaOrigen().getId())
                    .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));

            CuentaContable cuentaDestino = cuentaContableDao.findById(transferenciaInterna.getCuentaDestino().getId())
                    .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

            if (Objects.equals(cuentaOrigen, cuentaDestino)) {
                return this.buildResponse("nOk", CODIGO_NOK, "Las cuentas de Origen y Destino no pueden ser las mismas", null, HttpStatus.BAD_REQUEST);
            }

            if (transferenciaInterna.getImporte() == null || transferenciaInterna.getImporte() <= 0) {
                return this.buildResponse("nOk", CODIGO_NOK, "El importe debe ser Mayor a Cero", null, HttpStatus.BAD_REQUEST);
            }

            transferenciaInterna.setEstado(TransferenciaInterna.Estado.PENDIENTE);
            transfInternaDao.save(transferenciaInterna);

            log.info("Transferencia interna registrada exitosamente entre {} y {} por ${}",
                    cuentaOrigen.getCodigo() + " " + cuentaOrigen.getNombre(), cuentaDestino.getCodigo() + " " + cuentaDestino.getNombre(), transferenciaInterna.getImporte());

            return this.buildResponse("OK", CODIGO_OK, "Transferencia Interna Guardada", transferenciaInterna, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar guardar una trasnferencia Interna");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> aprobarTransferencia(Long idTransferencia) {
        try {
            TransferenciaInterna transferencia = transfInternaDao.findById(idTransferencia)
                    .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));

            if (transferencia.getEstado() != TransferenciaInterna.Estado.PENDIENTE) {
                return this.buildResponse("nOk", CODIGO_NOK, "La Transferencia No se encuentra Pendiente", null, HttpStatus.BAD_REQUEST);
            }

            TiposDeEntidades tipoEntidad = tipoEntidadDao.findById(transferencia.getTipoEntidad().getId())
                    .orElseThrow(() -> new RuntimeException("Tipo Entidad no encontrado"));

            AsientoContable asiento = new AsientoContable();
            transferencia.setReferencia(String.format("TRANSF-INT-%08d", transferencia.getId()));
            
            this.buildAsiento(asiento, transferencia, tipoEntidad, Estado.APROBADO, TipoAsiento.TRANSFERENCIA_INTERNA);
            asiento = asientosDao.save(asiento);
            if (asiento == null) {
                return this.buildResponse("ERROR", CODIGO_ERROR, "No se pudo guardar el asiento", asiento, HttpStatus.NOT_FOUND);
            }

//            List<MovimientoContable> movimientos = new ArrayList<>();
//            this.guardarMovimientoContable(asiento, movimientos);
            List<AsientoContable> list = new ArrayList();
            list.add(asiento);
            transferencia.setAsientos(list);
            transferencia.setEstado(TransferenciaInterna.Estado.APROBADA);

            transferencia = transfInternaDao.save(transferencia);
            return this.buildResponse("OK", CODIGO_OK, "Transferencia Interna Aprobada", transferencia, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> anularTransferencia(Long idTransferencia) {
        try {
            TransferenciaInterna transferencia = transfInternaDao.findById(idTransferencia)
                    .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
            
            if(transferencia.getEstado() == TransferenciaInterna.Estado.ANULADA){
                return this.buildResponse("nOk", CODIGO_NOK, "Transferencia Ya Anulada", transferencia, HttpStatus.OK);
            }

            if (transferencia.getEstado() == TransferenciaInterna.Estado.PENDIENTE) {
                transferencia.setEstado(TransferenciaInterna.Estado.ANULADA);
                transfInternaDao.save(transferencia);
                return this.buildResponse("Ok", CODIGO_OK, "Transferencia Pendiente Anulada", transferencia, HttpStatus.OK);
            }
            List<AsientoContable> list = new ArrayList();
            for (AsientoContable asiento : transferencia.getAsientos()) {
                asiento.setEstado(AsientoContable.Estado.ANULADO);
                AsientoContable asientoReversion = new AsientoContable();
                this.buildAsientoReversion(asientoReversion, asiento);
                asientosDao.save(asiento);
                asientoReversion = asientosDao.save(asientoReversion);
                list.add(asientoReversion);
//                List<MovimientoContable> movimientos = new ArrayList<>();
//                this.guardarMovimientoContable(asientoReversion, movimientos);
            }
            transferencia.setAsientos(list);
            transferencia.setEstado(TransferenciaInterna.Estado.ANULADA);
            transferencia = transfInternaDao.save(transferencia);
            return this.buildResponse("Ok", CODIGO_OK, "Transferencia Aprobada Anulada", transferencia, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar anular la transfrencia Interna");
        }
    }

    @Override
    public ResponseEntity<?> getTransferencia(Long idTransferencia) {
        try {
            Optional<TransferenciaInterna> optional = transfInternaDao.findById(idTransferencia);
            if (optional.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontraro trasnferencia Interna", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Transferencia Interna encontrada", optional.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar buscar la transferencia");
        }
    }

    @Override
    public ResponseEntity<?> listarTransferencias() {
        try {
            List<TransferenciaInterna> list = transfInternaDao.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("nOK", CODIGO_NOK, "No se encontraron trasnferencias Internas", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", CODIGO_OK, "Lista de transferencias internas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar listar las Transferencias");
        }
    }

    private void buildAsientoReversion(AsientoContable asientoReversion, AsientoContable asiento) {
        asientoReversion.setEstado(AsientoContable.Estado.APROBADO);
        asientoReversion.setFecha(LocalDate.now());
        asientoReversion.setReferenciaExterna("REV-" + asiento.getReferenciaExterna());
        asientoReversion.setDescripcion("REVERSION TRANSFERENCIA INTERNA");
        asientoReversion.setTipoOperacion(TipoOperacion.REVERSA_MOV_INTERNO);
        asientoReversion.setTipoEntidad(asiento.getTipoEntidad());
        asientoReversion.setTipoAsiento(TipoAsiento.ASIENTO_REVERSION);
        asientoReversion.setAsientoOrigen(asiento);
        asientoReversion.setContactoId(asiento.getContactoId());
        asientoReversion.setComprobanteId(asiento.getComprobanteId());
        List<MovimientoContable> list = new ArrayList();
        for (MovimientoContable mov : asiento.getMovimientos()) {
            MovimientoContable movRev = new MovimientoContable();
            Double debe = mov.getHaber();
            Double haber = mov.getDebe();
            movRev.setHaber(haber);
            movRev.setDebe(debe);
            movRev.setAsiento(asientoReversion);
            movRev.setCuenta(mov.getCuenta());
            list.add(movRev);
        }
        asientoReversion.setMovimientos(list);
//        List<MovimientoContable> movimientos = new ArrayList<>();
//        this.guardarMovimientoContable(asientoReversion, movimientos);
    }

    private void buildAsiento(AsientoContable asiento, TransferenciaInterna transferencia, TiposDeEntidades tipoEntidad, Estado estado, TipoAsiento tipo) {

        asiento.setFecha(LocalDate.now());
        asiento.setDescripcion(transferencia.getDescripcion() != null ? transferencia.getDescripcion() : "Transferencia entre cuentas");
        asiento.setEstado(estado);
        asiento.setComprobanteId(transferencia.getId());
        asiento.setReferenciaExterna(transferencia.getReferencia());
        asiento.setTipoOperacion(TipoOperacion.MOVIMIENTO_INTERNO);
        asiento.setTipoAsiento(tipo);
        asiento.setContactoId(transferencia.getContactoId());
        asiento.setTipoEntidad(tipoEntidad);

        // Detalle: Debe en cuenta destino
        MovimientoContable movDebe = this.buildDetalle(transferencia.getCuentaDestino(), transferencia.getImporte(), 0.0, asiento);

        // Detalle: Haber en cuenta origen
        MovimientoContable movHaber = this.buildDetalle(transferencia.getCuentaOrigen(), 0.0, transferencia.getImporte(), asiento);
        asiento.setMovimientos(Arrays.asList(movDebe, movHaber));
    }

    private  MovimientoContable buildDetalle(CuentaContable cuentaDestino, Double debe, Double haber, AsientoContable asiento) {
         MovimientoContable mov = new  MovimientoContable();
        mov.setCuenta(cuentaDestino);
        mov.setDebe(debe);
        mov.setHaber(haber);
        mov.setAsiento(asiento);
        return mov;
    }

    /*public void guardarMovimientoContable(AsientoContable asientoContable, List<MovimientoContable> nuevosMovimientos) {
        for (DetalleAsiento det : asientoContable.getDetalles()) {
            MovimientoContable mov = new MovimientoContable();
            mov.setFecha(new Date());
            mov.setAsiento(asientoContable);
            mov.setCuenta(det.getCuenta());
            mov.setEntidadId(asientoContable.getContactoId());
            mov.setTipoEntidad(asientoContable.getTipoEntidad());
            mov.setDebe(det.getDebe());
            mov.setHaber(det.getHaber());
            mov.setConciliado(false);
            nuevosMovimientos.add(mov);
            movimientosDao.save(mov);
        }
    }*/

//    @Override
//    @Transactional
//    public ResponseEntity<?> registrarTransferenciaAsincronica(Long cuentaOrigenId, Long cuentaDestinoId, Double importe,
//            String descripcion, Long contactoId, Long idTipoEntidad) {
//        try {
//            CuentaContable cuentaOrigen = cuentaContableDao.findById(cuentaOrigenId)
//                    .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
//
//            CuentaContable cuentaDestino = cuentaContableDao.findById(cuentaDestinoId)
//                    .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));
//
//            TiposDeEntidades tipoEntidad = tipoEntidadDao.findById(idTipoEntidad)
//                    .orElseThrow(() -> new RuntimeException("Tipo Entidad no encontrado"));
//
//            if (Objects.equals(cuentaOrigenId, cuentaDestinoId)) {
//                throw new IllegalArgumentException("La cuenta origen y destino no pueden ser iguales");
//            }
//
//            if (importe == null || importe <= 0) {
//                throw new IllegalArgumentException("El importe debe ser mayor a cero");
//            }
//
//            // Asiento 1: salida
//            AsientoContable asientoSalida = new AsientoContable();
//            this.buildAsiento(asientoSalida, descripcion, contactoId, cuentaOrigen, cuentaDestino, importe,
//                    tipoEntidad, Estado.APROBADO, TipoAsiento.TRANSFERENCIA_INTERNA);
//            asientoSalida.setDetalles(Collections.singletonList(this.buildDetalle(cuentaDestino, 0.0, importe, asientoSalida)));
//            asientosDao.save(asientoSalida);
//            List<MovimientoContable> movimientosSalida = new ArrayList<>();
//            guardarMovimientoContable(asientoSalida, movimientosSalida);
//
//            // Asiento 2: entrada
//            AsientoContable asientoEntrada = new AsientoContable();
//            this.buildAsiento(asientoEntrada, descripcion, contactoId, cuentaOrigen, cuentaDestino, importe,
//                    tipoEntidad, Estado.APROBADO, TipoAsiento.TRANSFERENCIA_INTERNA);
//            asientoEntrada.setDetalles(Collections.singletonList(this.buildDetalle(cuentaDestino, importe, 0.0, asientoEntrada)));
//            asientosDao.save(asientoEntrada);
//            List<MovimientoContable> movimientosEntrada = new ArrayList<>();
//            this.guardarMovimientoContable(asientoEntrada, movimientosEntrada);
//
//            ResultadoTransferenciaDTO resp = new ResultadoTransferenciaDTO();
//            resp.setAsientoEntrada(asientoEntrada);
//            resp.setAsientoSalida(asientoSalida);
//
//            log.info("Transferencia interna registrada exitosamente entre {} y {} por ${}",
//                    cuentaOrigen.getCodigo() + " " + cuentaOrigen.getNombre(), cuentaDestino.getCodigo() + " " + cuentaDestino.getNombre(), importe);
//            return this.buildResponse("OK", CODIGO_OK, "Asiento contable Registrado", resp, HttpStatus.OK);
//        } catch (IllegalArgumentException e) {
//            log.error(e.getMessage());
//            return this.buildErrorResponse("Error", CODIGO_ERROR, "Error en el Servidor al intentar conseguir el comprobante");
//        }
//    }
}
