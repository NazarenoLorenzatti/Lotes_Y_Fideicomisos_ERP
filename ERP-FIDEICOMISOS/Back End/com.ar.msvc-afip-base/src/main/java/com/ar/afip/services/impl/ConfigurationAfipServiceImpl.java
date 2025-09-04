package com.ar.afip.services.impl;

import ar.gov.afip.dif.facturaelectronica.CondicionIvaReceptor;
import ar.gov.afip.dif.facturaelectronica.PtoVenta;
import com.ar.afip.entities.AlicuotasIVA;
import com.ar.afip.entities.Ciudades;
import com.ar.afip.entities.CondicionIvaRecepetor;
import com.ar.afip.entities.CuitEmisor;
import com.ar.afip.entities.PuntosDeVenta;
import com.ar.afip.entities.TipoComprobante;
import com.ar.afip.entities.TipoDeDocumento;
import com.ar.afip.repositories.iAlicuotaIvaDao;
import com.ar.afip.repositories.iCiudadesDao;
import com.ar.afip.repositories.iCondicionesIvaReceptorDao;
import com.ar.afip.repositories.iCuitEmisorDao;
import com.ar.afip.repositories.iPuntosDeVentaDao;
import com.ar.afip.repositories.iTipoComprobanteDao;
import com.ar.afip.repositories.iTipoDeDocumentoDao;
import com.ar.afip.responses.BuildResponsesServicesImpl;
import com.ar.afip.services.iConfigurationAfipService;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@org.springframework.stereotype.Service
public class ConfigurationAfipServiceImpl extends BuildResponsesServicesImpl implements iConfigurationAfipService {

    @Autowired
    private iAlicuotaIvaDao alicuotasIva;

    @Autowired
    private iPuntosDeVentaDao puntosDeVentaDao;

    @Autowired
    private iTipoComprobanteDao tiposComprobanteDao;

    @Autowired
    private iTipoDeDocumentoDao tipoDeDocumentoDao;

    @Autowired
    private iCondicionesIvaReceptorDao condicionIvaDao;

    @Autowired
    private iCuitEmisorDao cuitEmisorDao;

    @Autowired
    private iCiudadesDao ciudadesDao;

    @Override
    public ResponseEntity<?> getPtoDeVenta(Integer nroPtoDeVenta, String cuit) {
        try {
            if (nroPtoDeVenta == null) {
                return this.buildResponse("nOK", "02", "No se envio ningun ID", null, HttpStatus.BAD_REQUEST);
            }
            Optional<CuitEmisor> cuitEmisor = cuitEmisorDao.findByCuit(cuit);
            Optional<PuntosDeVenta> o = puntosDeVentaDao.findByNroPtoVentaAndCuit(nroPtoDeVenta, cuitEmisor.get());
            if (!o.isPresent()) {
                return this.buildResponse("nOK", "02", "No se envio ningun Punto para la combinacion CUIT/Nro pto Venta", null, HttpStatus.BAD_REQUEST);
            }

            return this.buildResponse("OK", "00", "Punto de venta encontrado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener el punto de venta");
        }
    }

    @Override
    public ResponseEntity<?> getTipoComprobante(Integer idAfip) {
        try {
            if (idAfip == null) {
                return this.buildResponse("nOK", "02", "No se envio ningun ID", null, HttpStatus.BAD_REQUEST);
            }
            Optional<TipoComprobante> o = tiposComprobanteDao.findByIdAfip(idAfip);
            if (!o.isPresent()) {
                return this.buildResponse("nOK", "02", "No se obtuvo ningun Comprobante para ese nro", null, HttpStatus.BAD_REQUEST);
            }

            return this.buildResponse("OK", "00", "Tipo de comprobante encontrado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener el tipo de comprobante");
        }
    }

    @Override
    public ResponseEntity<?> getTipoDeDocumento(Integer idAfip) {
        try {
            if (idAfip == null) {
                return this.buildResponse("nOK", "02", "No se envio ningun ID", null, HttpStatus.BAD_REQUEST);
            }
            Optional<TipoDeDocumento> o = tipoDeDocumentoDao.findByIdAfip(idAfip);
            if (!o.isPresent()) {
                return this.buildResponse("nOK", "02", "No se obtuvo ningun Documento para ese nro", null, HttpStatus.BAD_REQUEST);
            }

            return this.buildResponse("OK", "00", "Tipo de documento encontrado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener el tipo de documento");
        }
    }

    @Override
    public ResponseEntity<?> getCondicionIvaReceptor(Integer idAfip) {
        try {
            if (idAfip == null) {
                return this.buildResponse("nOK", "02", "No se envio ningun ID", null, HttpStatus.BAD_REQUEST);
            }
            Optional<CondicionIvaRecepetor> o = condicionIvaDao.findByIdAfip(idAfip);
            if (!o.isPresent()) {
                return this.buildResponse("nOK", "02", "No se encontro ningun tipo de IVA con este ID", null, HttpStatus.BAD_REQUEST);
            }

            return this.buildResponse("OK", "00", "Condicion de IVA encontrado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la condicion frente al IVA");
        }
    }

    @Override
    public ResponseEntity<?> getAlicuotasIva(String descripcion) {
        try {
            if (descripcion == null) {
                return this.buildResponse("nOK", "02", "No se envio ninguna descripcion", null, HttpStatus.BAD_REQUEST);
            }
            Optional<AlicuotasIVA> o = alicuotasIva.findByDescripcion(descripcion);
            if (!o.isPresent()) {
                return this.buildResponse("nOK", "02", "No se encontro ninguna alicuota con esta descripcion", null, HttpStatus.BAD_REQUEST);
            }

            return this.buildResponse("OK", "00", "Alicuota IVA encontrado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la Alicuota");
        }
    }

    @Override
    public ResponseEntity<?> listarPtoDeVenta() {
        try {
            List<PuntosDeVenta> list = puntosDeVentaDao.findAll();
            return this.buildResponse("OK", "00", "lista de Puntos de ventas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de Puntos de venta");
        }
    }

    @Override
    public ResponseEntity<?> listarPtosDeVentaCuits(String cuit) {
        try {
            Optional<CuitEmisor> o = cuitEmisorDao.findByCuit(cuit);
            if(o.isEmpty()){
                 return this.buildResponse("nOK", "02", "No se envio un cuit Valido", null, HttpStatus.BAD_REQUEST);
            }
            List<PuntosDeVenta> list = puntosDeVentaDao.findByCuit(o.get());
            return this.buildResponse("OK", "00", "lista de Puntos de ventas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de Puntos de venta");
        }
    }

    @Override
    public ResponseEntity<?> listarTipoComprobante() {
        try {
            List<TipoComprobante> list = tiposComprobanteDao.findAll();
            return this.buildResponse("OK", "00", "lista de Puntos de ventas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de Comprobantes");
        }
    }

    @Override
    public ResponseEntity<?> listarTipoDeDocumento() {
        try {
            List<TipoDeDocumento> list = tipoDeDocumentoDao.findAll();
            return this.buildResponse("OK", "00", "lista de Puntos de ventas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de Documentos");
        }
    }

    @Override
    public ResponseEntity<?> listarCondicionIvaReceptor() {
        try {
            List<CondicionIvaRecepetor> list = condicionIvaDao.findAll();
            return this.buildResponse("OK", "00", "lista de Puntos de ventas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de Condicion frente al IVA");
        }
    }

    @Override
    public ResponseEntity<?> listarAlicuotasIva() {
        try {
            List<AlicuotasIVA> list = alicuotasIva.findAll();
            return this.buildResponse("OK", "00", "lista de Puntos de ventas", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de Alicuotas");
        }
    }

    @Override
    public ResponseEntity<?> listarCuitEmisores() {
        try {
            List<AlicuotasIVA> list = alicuotasIva.findAll();
            return this.buildResponse("OK", "00", "lista de Cuits Cargados", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de cuits Cargados");
        }
    }

    @Override
    public ResponseEntity<?> getCuitEmisores(String cuit) {
        try {
            if (cuit == null) {
                return this.buildResponse("nOK", "02", "No se envio ninguna descripcion", null, HttpStatus.BAD_REQUEST);
            }
            Optional<CuitEmisor> o = cuitEmisorDao.findByCuit(cuit);
            if (!o.isPresent()) {
                return this.buildResponse("nOK", "02", "No se encontro ninguna Cuit cargado con ese numero", null, HttpStatus.BAD_REQUEST);
            }

            return this.buildResponse("OK", "00", "Cuit Emisor encontrado", o.get(), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener el Cuit");
        }
    }

    @Override
    public ResponseEntity<?> listarCiudades() {
        try {
            List<Ciudades> list = ciudadesDao.findAll();
            return this.buildResponse("OK", "00", "lista de Ciudades", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "Ocurrion un error en el MSVC de AFIP al intentar obtener la lista de Ciudades");
        }
    }

}
