package com.ar.afip.services.impl;

import ar.gov.afip.dif.facturaelectronica.*;
import com.ar.afip.afip.WsaaClient;
import com.ar.afip.entities.*;
import com.ar.afip.repositories.*;
import com.ar.afip.responses.BuildResponsesServicesImpl;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import java.util.Optional;
import javax.xml.namespace.QName;
import com.ar.afip.services.iAfipService;
import java.text.SimpleDateFormat;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@org.springframework.stereotype.Service
public class AfipServiceImpl extends BuildResponsesServicesImpl implements iAfipService {

    @Autowired
    private iAfipTokenDao repoToken;

    @Autowired
    private WsaaClient wsaaClient;

    @Autowired
    private iAlicuotaIvaDao alicuotasIva;

    @Autowired
    private iPuntosDeVentaDao puntosDeVentaDao;

    @Autowired
    private iTipoComprobanteDao tiposComprobanteDao;

    @Autowired
    private iTipoDeDocumentoDao tipoDocumentoDao;

    @Autowired
    private iCondicionesIvaReceptorDao condicionIva;

    @Autowired
    private iCuitEmisorDao cuitEmisorDao;

    private static final String MONEDA_PESOS = "PES";
    private static final String MONEDA_DOLARES = "DOL";
    private static final String DATE_FORMAT = "YYYYMMdd";

    private SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

    public ResponseEntity<?> getOrCreateTA(String service, String cuit) {
        try {
            if (!this.testConnectionAfip()) {
                return this.buildErrorResponse("Error", "-01", "No se pudo establecer la conexion con Afip");
            }
            AfipToken afipToken = findOrSavedToken(service, cuit);
            if (afipToken == null) {
                return this.buildResponse("nOK", "02", "No se pudo conseguir el Token error en Cuit", afipToken, HttpStatus.BAD_REQUEST);
            }
            if (afipToken.getExpiration().after(new Date())) {
                return this.buildResponse("OK", "00", "El token Se encuentra creado y Vigente", afipToken, HttpStatus.OK);
            }
            return this.buildResponse("OK", "00", "Se genero el nuevo token Correctamente", afipToken, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("Error", "-01", "Ocurrio un error en el Servidor");
        }
    }

    private AfipToken findOrSavedToken(String service, String cuit) throws Exception {
        Optional<CuitEmisor> optionalCuit = cuitEmisorDao.findByCuit(cuit);
        if (optionalCuit.isEmpty()) {
            return null;
        }
        Optional<AfipToken> optional = repoToken.findTopByCuitAndServiceOrderByExpirationDesc(optionalCuit.get(), service);

        if (optional.isPresent()) {
            AfipToken existing = optional.get();
            log.info("Fecha del Token: {}", existing.getExpiration().toString());
            if (existing.getExpiration().after(new Date())) {
                return optional.get();
            }
        }
        String data = this.loginCms(optionalCuit.get());
        return this.saveNewToken(parseToken(data), service, optionalCuit.get());
    }

    private AfipToken saveNewToken(Map<String, String> data, String service, CuitEmisor cuit) {
        AfipToken ta = new AfipToken();
        ta.setService(service);
        ta.setToken(data.get("token"));
        ta.setSign(data.get("sign"));
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(data.get("expirationTime"));
        Instant instant = offsetDateTime.toInstant();
        Date date = Date.from(instant);
        ta.setExpiration(date);
        ta.setCuit(cuit);
        return repoToken.save(ta);
    }

    public boolean testConnectionAfip() throws MalformedURLException {
        DummyResponse dummy = getServiceSoap().feDummy();
        log.info("AppServer: " + dummy.getAppServer());
        log.info("DbServer: " + dummy.getDbServer());
        log.info("AuthServer: " + dummy.getAuthServer());
        return dummy.getAppServer().equals("OK") && dummy.getDbServer().equals("OK") && dummy.getAuthServer().equals("OK");
    }

    // 🔹 NUEVO: Consultar puntos de venta
    public FEPtoVentaResponse consultarPuntosDeVenta(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        FEPtoVentaResponse responsePtoVenta = getServiceSoap().feParamGetPtosVenta(auth);
        this.savePuntosDeVenta(responsePtoVenta, cuit);
        return responsePtoVenta;
    }

    // 🔹 NUEVO: Consultar último comprobante
    public FERecuperaLastCbteResponse consultarUltimoComprobante(int ptoVta, int cbteTipo, String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        return getServiceSoap().feCompUltimoAutorizado(auth, ptoVta, cbteTipo);
    }

    // 🔹 Tipos de comprobantes
    public CbteTipoResponse consultarTiposDeComprobantes(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        CbteTipoResponse cbteTipo = getServiceSoap().feParamGetTiposCbte(auth);
        this.saveTipoComprobantes(cbteTipo);
        return cbteTipo;
    }

// 🔹 Tipos de documentos
    public DocTipoResponse consultarTiposDeDocumentos(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        DocTipoResponse tipoDoc = getServiceSoap().feParamGetTiposDoc(auth);
        this.saveTipoDocumentos(tipoDoc);
        return tipoDoc;
    }

// 🔹 Tipos de IVA
    public IvaTipoResponse consultarTiposDeIva(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        IvaTipoResponse ivaTipo = getServiceSoap().feParamGetTiposIva(auth);
        this.saveAlicIva(ivaTipo);
        return ivaTipo;
    }

// 🔹 Tipos de tributos
    public FETributoResponse consultarTiposDeTributos(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        return getServiceSoap().feParamGetTiposTributos(auth);
    }

// 🔹 Tipos de conceptos
    public ConceptoTipoResponse consultarTiposDeConcepto(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        return getServiceSoap().feParamGetTiposConcepto(auth);
    }

// 🔹 Tipos de condicion IVA Receptor
    public CondicionIvaReceptorResponse consultarCondicionDeIVAReceptor(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        CondicionIvaReceptorResponse condicionIvaReceptor = getServiceSoap().feParamGetCondicionIvaReceptor(auth, null);
        this.saveCondicionIva(condicionIvaReceptor);
        return condicionIvaReceptor;
    }

// 🔹 Monedas
    public MonedaResponse consultarMonedas(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        return getServiceSoap().feParamGetTiposMonedas(auth);
    }

// 🔹 Cotización de moneda
    public FECotizacionResponse consultarCotizacionMoneda(String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        System.out.println("fecha " + format.format(new Date()));
        return getServiceSoap().feParamGetCotizacion(auth, MONEDA_DOLARES, format.format(new Date()));
    }

    // 🔸 NUEVO: Factorizado: creación del cliente
    private ServiceSoap getServiceSoap() throws MalformedURLException {
        URL wsdlURL = new URL("https://wswhomo.afip.gov.ar/wsfev1/service.asmx?WSDL");
        QName qname = new QName("http://ar.gov.afip.dif.FEV1/", "Service");
        Service service = new Service(wsdlURL, qname);
        return service.getServiceSoap();
    }

    private FEAuthRequest createAuthReuest(AfipToken token) {
        FEAuthRequest auth = new FEAuthRequest();
        auth.setToken(token.getToken());
        auth.setSign(token.getSign());
        auth.setCuit(Long.parseLong(token.getCuit().getCuit()));
        return auth;
    }

    private String loginCms(CuitEmisor cuit) throws Exception {
        return wsaaClient.loginCms(cuit);
    }

    private Date parseExpirationTime(String expirationTimeStr) {
        OffsetDateTime odt = OffsetDateTime.parse(expirationTimeStr.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return Date.from(odt.toInstant());
    }

    private Map<String, String> parseToken(String soapResponse) throws Exception {
        Map<String, String> map = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8));
        Document soapDoc = db.parse(is);
        String escapedXML = soapDoc.getElementsByTagName("loginCmsReturn").item(0).getTextContent();
        String realXML = org.apache.commons.text.StringEscapeUtils.unescapeXml(escapedXML);
        InputStream realIS = new ByteArrayInputStream(realXML.getBytes(StandardCharsets.UTF_8));
        Document realDoc = db.parse(realIS);

        map.put("token", realDoc.getElementsByTagName("token").item(0).getTextContent());
        map.put("sign", realDoc.getElementsByTagName("sign").item(0).getTextContent());
        map.put("expirationTime", realDoc.getElementsByTagName("expirationTime").item(0).getTextContent());

        return map;
    }

    // 🔹 NUEVO: Solicitar CAE
    public FECAEResponse solicitarCAE(FECAERequest request, String service, String cuit) throws Exception {
        FEAuthRequest auth = createAuthReuest(findOrSavedToken(service, cuit));
        FECAEResponse response = getServiceSoap().fecaeSolicitar(auth, request);
        return response;
    }

    private void saveAlicIva(IvaTipoResponse response) {
        ArrayOfIvaTipo list = response.getResultGet();
        for (IvaTipo tipo : list.getIvaTipo()) {
            if (tipo != null) {
                Optional<AlicuotasIVA> o = alicuotasIva.findByDescripcion(tipo.getDesc());
                AlicuotasIVA iva = o.isPresent()
                        ? o.get()
                        : new AlicuotasIVA();
                iva.setId_Afip(Integer.parseInt(tipo.getId()));
                iva.setDescripcion(tipo.getDesc());
                iva.setValue(Double.parseDouble(tipo.getDesc().replace("%", "")));
                alicuotasIva.save(iva);
            }
        }
    }

    private void saveCondicionIva(CondicionIvaReceptorResponse response) {
        ArrayOfCondicionIvaReceptor list = response.getResultGet();
        for (CondicionIvaReceptor tipo : list.getCondicionIvaReceptor()) {
            if (tipo != null) {
                Optional<CondicionIvaRecepetor> o = condicionIva.findByDescripcion(tipo.getDesc());
                CondicionIvaRecepetor condicion = o.isPresent()
                        ? o.get()
                        : new CondicionIvaRecepetor();
                condicion.setIdAfip(tipo.getId());
                condicion.setDescripcion(tipo.getDesc());
                condicion.setClase_comprobante(tipo.getCmpClase());
                condicionIva.save(condicion);
            }
        }
    }

    private void saveTipoComprobantes(CbteTipoResponse response) {
        ArrayOfCbteTipo list = response.getResultGet();
        Map<String, String> abreviaciones = this.initAbrebiaciones();
        for (CbteTipo tipo : list.getCbteTipo()) {
            if (tipo != null) {
                Optional<TipoComprobante> o = tiposComprobanteDao.findByDescripcion(tipo.getDesc());
                TipoComprobante t = o.isPresent()
                        ? o.get()
                        : new TipoComprobante();
                t.setIdAfip(tipo.getId());
                t.setDescripcion(tipo.getDesc());
                t.setAbrebiatura(abreviaciones.get(tipo.getDesc()));
                tiposComprobanteDao.save(t);
            }
        }
    }

    private void savePuntosDeVenta(FEPtoVentaResponse response, String cuitParam) {
        ArrayOfPtoVenta list = response.getResultGet();
        Optional<CuitEmisor> cuit = cuitEmisorDao.findByCuit(cuitParam);
        if (cuit.isEmpty()) {
            return;
        }

        for (PtoVenta tipo : list.getPtoVenta()) {
            if (tipo != null) {
                Optional<PuntosDeVenta> o = puntosDeVentaDao.findByNroPtoVentaAndCuit(tipo.getNro(), cuit.get());
                PuntosDeVenta p = o.isPresent()
                        ? o.get()
                        : new PuntosDeVenta();
                p.setNroPtoVenta(tipo.getNro());
                p.setNombrePtoVenta(tipo.getEmisionTipo());
                p.setCuit(cuit.get());
                puntosDeVentaDao.save(p);
            }
        }
    }

    private void saveTipoDocumentos(DocTipoResponse response) {
        ArrayOfDocTipo list = response.getResultGet();
        for (DocTipo tipo : list.getDocTipo()) {
            if (tipo != null) {
                Optional<TipoDeDocumento> o = tipoDocumentoDao.findByIdAfip(tipo.getId());
                TipoDeDocumento t = o.isPresent()
                        ? o.get()
                        : new TipoDeDocumento();
                t.setIdAfip(tipo.getId());
                t.setDescripcion(tipo.getDesc());
                tipoDocumentoDao.save(t);
            }
        }
    }

    private Map<String, String> initAbrebiaciones() {
        Map<String, String> abreviaciones = new HashMap<>();
        abreviaciones.put("Factura A", "FA");
        abreviaciones.put("Nota de Débito A", "DA");
        abreviaciones.put("Nota de Crédito A", "CA");
        abreviaciones.put("Factura B", "FB");
        abreviaciones.put("Nota de Débito B", "DB");
        abreviaciones.put("Nota de Crédito B", "CB");
        abreviaciones.put("Recibos A", "RA");
        abreviaciones.put("Notas de Venta al contado A", "VA");
        abreviaciones.put("Recibos B", "RB");
        abreviaciones.put("Notas de Venta al contado B", "VB");
        abreviaciones.put("Liquidacion A", "LA");
        abreviaciones.put("Liquidacion B", "LB");
        abreviaciones.put("Cbtes. A del Anexo I, Apartado A,inc.f),R.G.Nro. 1415", "AXA");
        abreviaciones.put("Cbtes. B del Anexo I,Apartado A,inc. f),R.G. Nro. 1415", "BXB");
        abreviaciones.put("Otros comprobantes A que cumplan con R.G.Nro. 1415", "OCA");
        abreviaciones.put("Otros comprobantes B que cumplan con R.G.Nro. 1415", "OCB");
        abreviaciones.put("Cta de Vta y Liquido prod. A", "CPA");
        abreviaciones.put("Cta de Vta y Liquido prod. B", "CPB");
        abreviaciones.put("Factura C", "FC");
        abreviaciones.put("Nota de Débito C", "DC");
        abreviaciones.put("Nota de Crédito C", "CC");
        abreviaciones.put("Recibo C", "RC");
        abreviaciones.put("Comprobante de Compra de Bienes Usados a Consumidor Final", "CBUC");
        abreviaciones.put("Factura M", "FM");
        abreviaciones.put("Nota de Débito M", "DM");
        abreviaciones.put("Nota de Crédito M", "CM");
        abreviaciones.put("Recibo M", "RM");
        abreviaciones.put("Factura de Crédito electrónica MiPyMEs (FCE) A", "FCEA");
        abreviaciones.put("Nota de Débito electrónica MiPyMEs (FCE) A", "DCEA");
        abreviaciones.put("Nota de Crédito electrónica MiPyMEs (FCE) A", "CCEA");
        abreviaciones.put("Factura de Crédito electrónica MiPyMEs (FCE) B", "FCEB");
        abreviaciones.put("Nota de Débito electrónica MiPyMEs (FCE) B", "DCEB");
        abreviaciones.put("Nota de Crédito electrónica MiPyMEs (FCE) B", "CCEB");
        abreviaciones.put("Factura de Crédito electrónica MiPyMEs (FCE) C", "FCEC");
        abreviaciones.put("Nota de Débito electrónica MiPyMEs (FCE) C", "DCEC");
        abreviaciones.put("Nota de Crédito electrónica MiPyMEs (FCE) C", "CCEC");
        return abreviaciones;
    }

}
