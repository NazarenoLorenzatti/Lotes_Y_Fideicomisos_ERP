package com.ar.base.services.impl;

import com.ar.base.DTOs.AplicacionManualRequest;
import com.ar.base.DTOs.AplicacionResponseDto;
import com.ar.base.DTOs.ImporteAplicadoEvent;
import com.ar.base.entities.*;
import com.ar.base.entities.MovimientoCuentaCorriente.TipoMovimiento;
import com.ar.base.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ar.base.responses.BuildResponsesServicesImpl;
import com.ar.base.services.iCuentaCorrienteService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CuentaCorrienteServiceImpl extends BuildResponsesServicesImpl implements iCuentaCorrienteService {

    @Autowired
    private iMovimientoCuentaCorrienteRepository movimientoCuentaCorrienteRepository;

    @Autowired
    private iContactoRepository contactoRepository;

    @Autowired
    private iAplicacionMovimientoDao aplicacionRepository;

    @Autowired
    private iCuentaCorrienteRepository cuentaCorrienteRepo;

    @Autowired
    private KafkaEventProducer kafkaProducer;

    @Override
    public ResponseEntity<?> getCuentaCorriente(Contacto contacto) {
        try {
            Optional<Contacto> o = this.findContactoById(contacto.getId());
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            Optional<CuentaCorriente> ccOptional = cuentaCorrienteRepo.findByContacto(o.get());
            if (ccOptional.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro la cuenta Corriente del contacto", null, HttpStatus.BAD_REQUEST);
            }
            CuentaCorriente cc = ccOptional.get();
//            cc.setSaldo(cc.calcularSaldo());
            return this.buildResponse("OK", "00", "Cuenta Corriente Encontrada", cc, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al confirmar el contacto");
        }

    }

    @Override
    public Double calcularSaldoFactura(Long facturaId) {
        MovimientoCuentaCorriente factura = movimientoCuentaCorrienteRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if (!factura.getTipoMovimiento().equals(TipoMovimiento.CREDITO)) {
            throw new IllegalArgumentException("El ID no corresponde a una factura");
        }
        Double totalAplicado = aplicacionRepository.sumImporteAplicadoByMovimientoDestino(factura);
        return factura.getImporte() - (totalAplicado != null ? totalAplicado : 0.0);
    }

    @Override
    @Transactional
    public ResponseEntity<?> registrarMovimiento(Long clienteId, MovimientoCuentaCorriente movimiento) {
        try {
            Optional<Contacto> o = this.findContactoById(clienteId);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            Optional<CuentaCorriente> ccOptional = cuentaCorrienteRepo.findByContacto(o.get());
            if (ccOptional.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro la cuenta Corriente del contacto", null, HttpStatus.BAD_REQUEST);
            }
            movimiento.setCuentaCorriente(ccOptional.get());
            movimiento = this.buildRegistrarMovimiento(movimiento);

            if (movimiento == null) {
                return this.buildResponse("Error", "02", "No Se pudo guardar el movimiento", movimiento, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", "00", "Movimiento Guardado", movimiento, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al registrar el Movimientoi");
        }
    }

    @Override
    public ResponseEntity<?> deleteAplicacion(Long idAplicacion) {
        try {
            Optional<AplicacionMovimiento> o = aplicacionRepository.findById(idAplicacion);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se el movimiento a eliminar", null, HttpStatus.BAD_REQUEST);
            }
            aplicacionRepository.delete(o.get());
            return this.buildResponse("OK", "00", "Aplicacion Eliminada", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al registrar el Movimientoi");
        }
    }

    private MovimientoCuentaCorriente buildRegistrarMovimiento(MovimientoCuentaCorriente movimiento) {
        movimiento.setFecha(new Date());
        MovimientoCuentaCorriente saved = movimientoCuentaCorrienteRepository.save(movimiento);
        if (saved != null) {
            aplicarSiCorresponde(saved);
            return saved;
        }
        return null;
    }

    @Transactional
    @Override
    public ResponseEntity<?> applyMovimiento(AplicacionManualRequest request) {
        try {
            Optional<MovimientoCuentaCorriente> origen = movimientoCuentaCorrienteRepository.findById(request.getMovimientoOrigenId());
            Optional<MovimientoCuentaCorriente> destino = movimientoCuentaCorrienteRepository.findById(request.getMovimientoDestinoId());

            if (origen.get().isOficial() != destino.get().isOficial()) {
                return this.buildResponse("Error", "02", "No Se puede conciliar un movimiento oficial con uno Auxiliar", null, HttpStatus.BAD_REQUEST);
            }

            if (origen.isEmpty() || destino.isEmpty()) {
                List<MovimientoCuentaCorriente> list = new ArrayList();
                list.add(origen.get());
                list.add(destino.get());
                return this.buildResponse("Error", "02", "No Se pudo Conciliar los movimientos", list, HttpStatus.BAD_REQUEST);
            }

            double disponibleOrigen = origen.get().getImporte() - getImporteAplicado(origen.get());
            double pendienteDestino = destino.get().getImporte() - getImporteAplicado(destino.get());

            if (disponibleOrigen <= 0 || pendienteDestino <= 0) {
                return this.buildResponse("Error", "02", "No hay saldo disponible para aplicar", null, HttpStatus.BAD_REQUEST);
            }
            double importeAplicable = Math.min(Math.min(request.getImporte(), disponibleOrigen), pendienteDestino);
            AplicacionMovimiento aplicacion = new AplicacionMovimiento();
            aplicacion.setMovimientoOrigen(origen.get());
            aplicacion.setMovimientoDestino(destino.get());
            aplicacion.setImporteAplicado(importeAplicable);
            aplicacion.setFecha_aplicacion(new Date());
            aplicacionRepository.save(aplicacion);
            this.publicarEventoImporteAplicado(aplicacion);
            return this.buildResponse("OK", "00", "Aplicacion Correcta", aplicacion, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al intentar conciliar");
        }
    }

    private void aplicarSiCorresponde(MovimientoCuentaCorriente nuevo) {
        MovimientoCuentaCorriente.TipoMovimiento tipo = nuevo.getTipoMovimiento();
        MovimientoCuentaCorriente.TipoMovimiento opuesto
                = (tipo == MovimientoCuentaCorriente.TipoMovimiento.CREDITO)
                        ? MovimientoCuentaCorriente.TipoMovimiento.DEBITO
                        : MovimientoCuentaCorriente.TipoMovimiento.CREDITO;

        List<MovimientoCuentaCorriente> pendientes = movimientoCuentaCorrienteRepository.findPendientesPorTipo(
                nuevo.getCuentaCorriente(), opuesto);

        this.aplicar(nuevo, pendientes);
    }

    private void aplicar(MovimientoCuentaCorriente nuevo, List<MovimientoCuentaCorriente> pendientes) {
        double disponible = nuevo.getImporte() - getImporteAplicado(nuevo);

        for (MovimientoCuentaCorriente pendiente : pendientes) {
            double saldoPendiente = pendiente.getSaldoPendiente();

            if (nuevo.isOficial() != pendiente.isOficial()) {
                continue;
            }

            if (saldoPendiente <= 0) {
                continue;
            }
            double aAplicar = Math.min(disponible, saldoPendiente);
            if (aAplicar <= 0) {
                break;
            }
            AplicacionMovimiento aplicacion = new AplicacionMovimiento();

            if (nuevo.getTipoMovimiento() == MovimientoCuentaCorriente.TipoMovimiento.CREDITO) {
                aplicacion.setMovimientoOrigen(nuevo);
                aplicacion.setMovimientoDestino(pendiente);
            } else {
                aplicacion.setMovimientoOrigen(pendiente);
                aplicacion.setMovimientoDestino(nuevo);
            }
            aplicacion.setImporteAplicado(aAplicar);
            aplicacion.setFecha_aplicacion(new Date());
            aplicacion.getMovimientoDestino().setSaldoPendiente(saldoPendiente - aAplicar);
            aplicacion.getMovimientoDestino().setIsApply(true);
            aplicacion.getMovimientoOrigen().setSaldoPendiente(nuevo.getImporte() - aAplicar);
            aplicacion.getMovimientoOrigen().setIsApply(true);
            aplicacionRepository.save(aplicacion);
            this.publicarEventoImporteAplicado(aplicacion);
            disponible -= aAplicar;
            if (disponible <= 0) {
                break;
            }
        }
    }

    private void publicarEventoImporteAplicado(AplicacionMovimiento aplicacion) {
        ImporteAplicadoEvent event = new ImporteAplicadoEvent();
        event.setClienteId(aplicacion.getMovimientoDestino().getCuentaCorriente().getContacto().getId());
        event.setFacturaId(aplicacion.getMovimientoDestino().getComprobanteId());
        event.setFechaSaldado(aplicacion.getFecha_aplicacion());
        event.setImporteAplicado(aplicacion.getImporteAplicado());
        event.setNroComprobante(aplicacion.getMovimientoDestino().getNroComprobante());
        event.setOficial(aplicacion.getMovimientoDestino().isOficial());
        kafkaProducer.publicarEventoImporteAplicado(event);
    }

    private double getImporteAplicado(MovimientoCuentaCorriente movimiento) {
        double origen = movimiento.getAplicacionesComoOrigen().stream()
                .mapToDouble(AplicacionMovimiento::getImporteAplicado)
                .sum();
        double destino = movimiento.getAplicacionesComoDestino().stream()
                .mapToDouble(AplicacionMovimiento::getImporteAplicado)
                .sum();

        return movimiento.getTipoMovimiento() == MovimientoCuentaCorriente.TipoMovimiento.CREDITO ? origen : destino;
    }

    private Optional<Contacto> findContactoById(Long id) {
        if (id == null) {
            return null;
        }
        Optional<Contacto> o = contactoRepository.findById(id);
        if (o.isEmpty()) {
            return null;
        }
        return o;
    }

    @Override
    public ResponseEntity<?> getAplicaciones(Long idMovimientoOrigen, Long idMovimientoDestino)  {
        try {
            Optional<MovimientoCuentaCorriente> o = this.movimientoCuentaCorrienteRepository.findById(idMovimientoOrigen != null ? idMovimientoOrigen : idMovimientoDestino);
            if(o.isEmpty()){
                 return this.buildResponse("Error", "02", "No Se encontro este mov", null, HttpStatus.BAD_REQUEST);
            }
            
            List<AplicacionMovimiento> list = idMovimientoOrigen != null 
                    ?  this.aplicacionRepository.findAllByMovimientoOrigen(o.get())
                    : this.aplicacionRepository.findAllByMovimientoDestino(o.get());
            
            if (list.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun aplicacion a este mov", null, HttpStatus.BAD_REQUEST);
            }
            List<AplicacionResponseDto> listDto = new ArrayList();
            this.buildAplicacionesDTO(listDto, list);
            return this.buildResponse("OK", "00", "Aplicaciones al Movimiento", listDto, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al confirmar el contacto");
        }
    }

    private void buildAplicacionesDTO(List<AplicacionResponseDto> listDto, List<AplicacionMovimiento> list) {
        for (AplicacionMovimiento apply : list) {
            AplicacionResponseDto dto = new AplicacionResponseDto();
            dto.setIdComprobanteOrigen(apply.getMovimientoOrigen() != null ? apply.getMovimientoOrigen().getComprobanteId() : null);
            dto.setNumeroComprobanteOrigen(apply.getMovimientoOrigen() != null ? apply.getMovimientoOrigen().getNroComprobante(): null);
            dto.setImporteComprobanteOrigen(apply.getMovimientoOrigen() != null ? apply.getMovimientoOrigen().getImporte(): null);
            dto.setTipoMovComprobanteOrigen(apply.getMovimientoOrigen() != null ? apply.getMovimientoOrigen().getTipoMovimiento().name() : null);
            
             dto.setIdComprobanteDestino(apply.getMovimientoDestino() != null ? apply.getMovimientoDestino().getComprobanteId() : null);
            dto.setNumeroComprobanteDestino(apply.getMovimientoDestino() != null ? apply.getMovimientoDestino().getNroComprobante(): null);
            dto.setImporteComprobanteDestino(apply.getMovimientoDestino() != null ? apply.getMovimientoDestino().getImporte(): null);
            dto.setTipoMovComprobanteDestino(apply.getMovimientoDestino() != null ? apply.getMovimientoDestino().getTipoMovimiento().name() : null);
            listDto.add(dto);
        }
    }

}
