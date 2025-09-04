package com.ar.afip.services.impl;

import ar.gov.afip.dif.facturaelectronica.FERecuperaLastCbteResponse;
import com.ar.afip.entities.ComprobanteSecuencia;
import com.ar.afip.entities.CuitEmisor;
import com.ar.afip.entities.PuntosDeVenta;
import com.ar.afip.entities.TipoComprobante;
import com.ar.afip.repositories.iComprobanteSecuenciaDao;
import com.ar.afip.repositories.iCuitEmisorDao;
import com.ar.afip.repositories.iPuntosDeVentaDao;
import com.ar.afip.repositories.iTipoComprobanteDao;
import com.ar.afip.responses.BuildResponsesServicesImpl;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ComprobanteSecuenciaServiceImpl extends BuildResponsesServicesImpl {

    @Autowired
    private iComprobanteSecuenciaDao secuenciaRepo;

    @Autowired
    private iPuntosDeVentaDao ptoVentaDao;

    @Autowired
    private iTipoComprobanteDao comprobanteDao;

    @Autowired
    private iCuitEmisorDao cuitDao;

    @Autowired
    private AfipServiceImpl afipService;

    @Transactional
    public ResponseEntity<Integer> obtenerUltimoNumero(int idtipo, int nroPunto, String cuit) {
        try {
            Optional<CuitEmisor> cuitOptional = cuitDao.findByCuit(cuit);
            Optional<PuntosDeVenta> ptoOptional = ptoVentaDao.findByNroPtoVentaAndCuit(nroPunto, cuitOptional.get());
            Optional<TipoComprobante> cmbtOptional = comprobanteDao.findByIdAfip(idtipo);

            if (ptoOptional.isEmpty() || cmbtOptional.isEmpty() || cuitOptional.isEmpty()) {
                return (ResponseEntity<Integer>) this.buildResponse("nOk", "02", "No se Pudo obtener la secuencia, informacion insuficiente", null, HttpStatus.BAD_REQUEST);
            }

            ComprobanteSecuencia secuencia = secuenciaRepo.findAndLock(cmbtOptional.get(), ptoOptional.get(), cuitOptional.get())
                    .orElseGet(() -> {
                        ComprobanteSecuencia nueva = new ComprobanteSecuencia();
                        nueva.setTipoComprobante(cmbtOptional.get());
                        nueva.setPuntoVenta(ptoOptional.get());
                        nueva.setCuit(cuitOptional.get());
                        nueva.setUltimoNumero(0);
                        return nueva;
                    });

            Integer nuevoNumero = secuencia.getUltimoNumero();
            secuencia.setUltimoNumero(nuevoNumero);
            return (ResponseEntity<Integer>) this.buildResponse("ok", "00", "Ultimo Numero", nuevoNumero, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return (ResponseEntity<Integer>) this.buildResponse("nOk", "02", "No se Pudo  obtener la secuencia, informacion insuficiente", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> sincronizarNumeracionConAfip() {
        try {
            for (ComprobanteSecuencia s : secuenciaRepo.findAll()) {
                FERecuperaLastCbteResponse ultimoComprobanteAfip = afipService.consultarUltimoComprobante(
                        s.getPuntoVenta().getNroPtoVenta(),
                        s.getTipoComprobante().getIdAfip(),
                        "wsfe",
                        s.getCuit().getCuit());
                log.info("Ultimo ComprobanteAfip {} {} {}", ultimoComprobanteAfip.getCbteTipo(), ultimoComprobanteAfip.getCbteNro(), s.getId());
                if (ultimoComprobanteAfip.getErrors() == null) {
                    Integer ultimoNro = ultimoComprobanteAfip.getCbteNro();
                    s.setUltimoNumero(ultimoNro);
                    secuenciaRepo.save(s);
                }
                log.info("Secuencia Guardada {}", s.getId());
            }
            return this.buildResponse("ok", "00", "Sincronizacion Completada", null, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un Error al intentar Sincronizar numeracion con AFIP");
        }
    }

    @Transactional
    public ResponseEntity<?> guardarSecuencia(int idtipo, int nroPunto, String cuit, int numeroSecuencia) {
        try {
            Optional<CuitEmisor> cuitOptional = cuitDao.findByCuit(cuit);
            Optional<PuntosDeVenta> ptoOptional = ptoVentaDao.findByNroPtoVentaAndCuit(nroPunto, cuitOptional.get());
            Optional<TipoComprobante> cmbtOptional = comprobanteDao.findByIdAfip(idtipo);

            if (ptoOptional.isEmpty() || cmbtOptional.isEmpty() || cuitOptional.isEmpty()) {
                return this.buildResponse("nOk", "02", "No se Pudo guardar la secuencia, informacion insuficiente", null, HttpStatus.BAD_REQUEST);
            }

            Optional<ComprobanteSecuencia> o = secuenciaRepo.findAndLock(cmbtOptional.get(), ptoOptional.get(), cuitOptional.get());
            if (o.isPresent()) {
                ComprobanteSecuencia s = o.get();
                s.setPuntoVenta(ptoOptional.get());
                s.setTipoComprobante(cmbtOptional.get());
                s.setUltimoNumero(numeroSecuencia);
                s.setCuit(cuitOptional.get());
                this.secuenciaRepo.save(s);
                return this.buildResponse("ok", "00", "Secuencia Guardara", s, HttpStatus.OK);
            }
            return this.buildResponse("nOk", "02", "No se Pudo guardar la secuencia, No se encontro", null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildResponse("ERROR", "-01", "No se Pudo guardar la secuencia, Ocurrio un Error", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Transactional
    public ResponseEntity<?> initNumeracionConAfip(String cuit) {
        try {
            List<TipoComprobante> listaComprobantes = comprobanteDao.findAll();
            List<PuntosDeVenta> listaPtosVenta = ptoVentaDao.findAll();
            Optional<CuitEmisor> cuitEmisor = cuitDao.findByCuit(cuit);

            if (listaComprobantes.isEmpty() || listaPtosVenta.isEmpty() || cuitEmisor.isEmpty()) {
                return this.buildResponse("nok", "02", "Primero debe Iniciar La lista de Comprobantes y Punto de ventas", null, HttpStatus.CONFLICT);
            }

            for (TipoComprobante tipoComp : comprobanteDao.findAll()) {
                for (PuntosDeVenta puntoVta : ptoVentaDao.findAll()) {
                    Optional<ComprobanteSecuencia> o = secuenciaRepo.findByTipoComprobanteAndPuntoVentaAndCuit(tipoComp, puntoVta, cuitEmisor.get());
                    ComprobanteSecuencia secuencia = o.isPresent()
                            ? o.get()
                            : new ComprobanteSecuencia();
                    secuencia.setTipoComprobante(tipoComp);
                    secuencia.setPuntoVenta(puntoVta);
                    secuencia.setCuit(cuitEmisor.get());
                    secuenciaRepo.save(secuencia);
                }
            }
            return this.buildResponse("ok", "00", "Se inicio la Numercacion de Comprobantes y Puntos de venta", null, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un Error al intentar Sincronizar numeracion con AFIP");
        }
    }
}
