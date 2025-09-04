package com.ar.base.services.impl;

import com.ar.base.DTOs.ContactoDTO;
import com.ar.base.entities.Contacto;
import com.ar.base.entities.CuentaCorriente;
import com.ar.base.entities.Estados;
import com.ar.base.repositories.iContactoRepository;
import com.ar.base.repositories.iCuentaCorrienteRepository;
import com.ar.base.repositories.iEstadosRepository;
import com.ar.base.responses.BuildResponsesServicesImpl;
import com.ar.base.services.iContactoService;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactoServiceImpl extends BuildResponsesServicesImpl implements iContactoService {

    @Autowired
    private iContactoRepository contactoRepository;

    @Autowired
    private iEstadosRepository estadosRepository;

    @Autowired
    private iCuentaCorrienteRepository cuentaCorrienteRepository;

    private static final Long ESTADO_BORRADOR = 1L;
    private static final Long ESTADO_ACTIVO = 2L;
    private static final Long ESTADO_DEUDOR = 3L;
    private static final Long ESTADO_BAJA_TEMPRANA = 4L;
    private static final Long ESTADO_BAJA = 5L;
    private static final Long ESTADO_ARCHIVADO = 6L;

    @Override
    public ResponseEntity<?> getContacto(Long id) {
        try {
            Optional<Contacto> o = this.findContactoById(id);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", "00", "Contacto Encontrado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> saveContacto(Contacto contacto) {
        try {
            if (contacto == null) {
                return this.buildResponse("Error", "02", "No Se envio ningun contacto para guardar", null, HttpStatus.BAD_REQUEST);
            }
            this.buildSavedContacto(contacto);
            Contacto contactoGuardado = contactoRepository.save(contacto);
            if (contactoGuardado == null) {
                return this.buildResponse("Error", "02", "No Se pudo guardar el Contacto", null, HttpStatus.NOT_FOUND);
            }
            return this.buildResponse("OK", "00", "Contacto Encontrado", contactoGuardado, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al guardar el contacto");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> confirmarContacto(Long id) {
        try {
            Optional<Contacto> o = this.findContactoById(id);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            Contacto contacto = o.get();
            this.setEstado(contacto, ESTADO_ACTIVO);
            Contacto contactoGuardado = contactoRepository.save(contacto);
            if (contactoGuardado != null) {
                return this.buildResponse("OK", "00", "Contacto Confirmado", contactoGuardado, HttpStatus.OK);
            } else {
                return this.buildResponse("Error", "02", "No Se pudo Confirmar el Contacto", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al confirmar el contacto");
        }
    }

    @Override
    public ResponseEntity<?> editContacto(Contacto contacto) {
        try {
            Optional<Contacto> o = this.findContactoById(contacto.getId());
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            contacto = this.buildEditContacto(o, contacto);
            Contacto contactoGuardado = contactoRepository.save(contacto);
            if (contactoGuardado != null) {
                return this.buildResponse("OK", "00", "Contacto Editado", contactoGuardado, HttpStatus.OK);
            } else {
                return this.buildResponse("Error", "02", "No Se pudo editar el Contacto", null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al editar el contacto");
        }
    }

    @Override
    public ResponseEntity<?> getAllContacto() {
        try {
            List<Contacto> list = contactoRepository.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("Error", "02", "No se encontró ningún contacto", null, HttpStatus.BAD_REQUEST);
            }

            List<ContactoDTO> dtoList = list.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());

            return this.buildResponse("OK", "00", "Contactos encontrados", dtoList, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al obtener contactos: {}", e.getMessage(), e);
            return this.buildErrorResponse("ERROR", "-01", "OCURRIÓ UN ERROR EN EL SERVIDOR");
        }
    }

    @Override
    public ResponseEntity<?> getAllContacto2() {
        try {
            List<Contacto> list = contactoRepository.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", "00", "Contacto Encontrado", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR");
        }
    }

    @Override
    public ResponseEntity<?> deleteContacto(Long id) {
        try {
            Optional<Contacto> contacto = this.findContactoById(id);
            if (contacto.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            if (!contacto.get().getEstado().getId().equals(ESTADO_BORRADOR)) {
                return this.buildResponse("Error", "02", "No Se Puede eliminar el Contacto, no esta en Borrador", null, HttpStatus.BAD_REQUEST);
            }
            this.contactoRepository.deleteById(id);
            return this.buildResponse("OK", "00", "Contacto Eliminado", contacto, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al eliminar el contacto");
        }

    }

    private void buildSavedContacto(Contacto contacto) {
        contacto.setFechaDeCreacion(LocalDateTime.now());
        contacto.setCreador_por("NLorenzatti");
        this.setEstado(contacto, ESTADO_BORRADOR);
    }

    private void setEstado(Contacto contacto, Long idEstado) {
        Optional<Estados> o = this.estadosRepository.findById(idEstado);
        if (o.isPresent()) {
            if (o.get().getId().equals(ESTADO_ACTIVO)) {
                CuentaCorriente cc = new CuentaCorriente();
                cc.setContacto(contacto);
                contacto.setCuentaCorriente(cc);
            }
            contacto.setEstado(o.get());
        }
    }

    private Contacto buildEditContacto(Optional<Contacto> o, Contacto contacto) {
        Contacto contactoEdit = o.get();
        contactoEdit.setRazonSocial(contacto.getRazonSocial() == null ? contactoEdit.getRazonSocial() : contacto.getRazonSocial());
        contactoEdit.setNombre(contacto.getNombre() == null ? contactoEdit.getNombre() : contacto.getNombre());
        contactoEdit.setApellido(contacto.getApellido() == null ? contactoEdit.getApellido() : contacto.getApellido());
        contactoEdit.setTipoDocumento(contacto.getTipoDocumento() == null ? contactoEdit.getTipoDocumento() : contacto.getTipoDocumento());
        contactoEdit.setIdAfipTipoDocumento(contacto.getIdAfipTipoDocumento() == null ? contactoEdit.getIdAfipTipoDocumento() : contacto.getIdAfipTipoDocumento());
        contactoEdit.setNumeroDocumento(contacto.getNumeroDocumento() == null ? contactoEdit.getNumeroDocumento() : contacto.getNumeroDocumento());
        contactoEdit.setDireccionFiscal(contacto.getDireccionFiscal() == null ? contactoEdit.getDireccionFiscal() : contacto.getDireccionFiscal());
        contactoEdit.setAltura(contacto.getAltura() == null ? contactoEdit.getAltura() : contacto.getAltura());
        contactoEdit.setPcia(contacto.getPcia() == null ? contactoEdit.getPcia() : contacto.getPcia());
        contactoEdit.setLocalidad(contacto.getLocalidad() == null ? contactoEdit.getLocalidad() : contacto.getLocalidad());
        contactoEdit.setCodigoPostal(contacto.getCodigoPostal() == null ? contactoEdit.getCodigoPostal() : contacto.getCodigoPostal());
        contactoEdit.setCondicionIva(contacto.getCondicionIva() == null ? contactoEdit.getCondicionIva() : contacto.getCondicionIva());
        contactoEdit.setIdCondicionIva(contacto.getIdCondicionIva() == null ? contactoEdit.getIdCondicionIva() : contacto.getIdCondicionIva());
        contactoEdit.setEmail(contacto.getEmail() == null ? contactoEdit.getEmail() : contacto.getEmail());
        contactoEdit.setEmail_auxiliar(contacto.getEmail_auxiliar() == null ? contactoEdit.getEmail_auxiliar() : contacto.getEmail_auxiliar());
        contactoEdit.setCelular(contacto.getCelular() == null ? contactoEdit.getCelular() : contacto.getCelular());
        contactoEdit.setCelular_auxiliar(contacto.getCelular_auxiliar() == null ? contactoEdit.getCelular_auxiliar() : contacto.getCelular_auxiliar());
        contactoEdit.setTelefono(contacto.getTelefono() == null ? contactoEdit.getTelefono() : contacto.getTelefono());
        contactoEdit.setTelefono_auxiliar(contacto.getTelefono_auxiliar() == null ? contactoEdit.getTelefono_auxiliar() : contacto.getTelefono_auxiliar());
        contactoEdit.setIsCliente(contacto.getIsCliente() == null ? contactoEdit.getIsCliente() : contacto.getIsCliente());
        contactoEdit.setIsProveedor(contacto.getIsProveedor() == null ? contactoEdit.getIsProveedor() : contacto.getIsProveedor());
        return contactoEdit;
    }

    private Optional<Contacto> findContactoById(Long id) {
        return contactoRepository.findById(id);
    }

    @Override
    public ResponseEntity<?> setEstadoAutomatico() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ResponseEntity<?> setEstadoBajaTemprana(Long id) {
        try {
            Optional<Contacto> o = this.findContactoById(id);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            this.setEstado(o.get(), ESTADO_BAJA_TEMPRANA);
            return this.buildResponse("OK", "00", "Cambio de estado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al eliminar el contacto");
        }
    }

    @Override
    public ResponseEntity<?> setEstadoBaja(Long id) {
        try {
            Optional<Contacto> o = this.findContactoById(id);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            this.setEstado(o.get(), ESTADO_BAJA);
            return this.buildResponse("OK", "00", "Cambio de estado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al eliminar el contacto");
        }
    }

    @Override
    public ResponseEntity<?> setEstadoArchivado(Long id) {
        try {
            Optional<Contacto> o = this.findContactoById(id);
            if (o.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun contacto con este ID", null, HttpStatus.BAD_REQUEST);
            }
            this.setEstado(o.get(), ESTADO_ARCHIVADO);
            return this.buildResponse("OK", "00", "Cambio de estado", o.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR al eliminar el contacto");
        }
    }

    @Override
    public ResponseEntity<?> getAllEstados() {
        try {
            List<Estados> list = estadosRepository.findAll();
            if (list.isEmpty()) {
                return this.buildResponse("Error", "02", "No Se encontro ningun estado Cargado", null, HttpStatus.BAD_REQUEST);
            }
            return this.buildResponse("OK", "00", "Estados Encontrados", list, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return this.buildErrorResponse("ERROR", "-01", "OCURRIO UN ERROR EN EL SERVIDOR");
        }
    }

    private ContactoDTO toDTO(Contacto contacto) {
        ContactoDTO dto = new ContactoDTO();
        dto.setId(contacto.getId());
        dto.setSaldo(
                contacto.getCuentaCorriente() != null
                ? contacto.getCuentaCorriente().getSaldoCalculado()
                : 0.0
        );
        dto.setRazonSocial(contacto.getRazonSocial());
        dto.setTipoDocumento(contacto.getTipoDocumento());
        dto.setNumeroDocumento(contacto.getNumeroDocumento());
        dto.setLocalidad(contacto.getLocalidad());
        dto.setEmail(contacto.getEmail());
        dto.setEstado(contacto.getEstado());
        dto.setCelular(contacto.getCelular());
        dto.setTelefono(contacto.getTelefono());
        dto.setFechaDeCreacion(
                contacto.getFechaDeCreacion() != null
                ? contacto.getFechaDeCreacion().toLocalDate()
                : null
        );
        dto.setIsCliente(contacto.getIsCliente());
        dto.setIsProveedor(contacto.getIsProveedor());
        dto.setCreador_por(contacto.getCreador_por());
        return dto;
    }

}
