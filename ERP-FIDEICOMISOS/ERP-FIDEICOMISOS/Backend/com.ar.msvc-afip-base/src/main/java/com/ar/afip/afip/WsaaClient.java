package com.ar.afip.afip;

import com.ar.afip.entities.CuitEmisor;
import javax.xml.soap.*;
import org.bouncycastle.util.encoders.Base64;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WsaaClient {

    @Autowired
    private LoginTicketRequestGenerator loginTicketRequestGenerator;

    public String loginCms(CuitEmisor cuit) throws Exception {
        String pfxPath = cuit.getPfxPath();
        String pfxPassword = cuit.getPfxPassword();
        String service = cuit.getService(); // Servicio de Facturación Electrónica

        // Cargar el .pfx (PKCS12)
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(pfxPath), pfxPassword.toCharArray());

        String alias = ks.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, pfxPassword.toCharArray());
        X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);

        // Generar el Login Ticket Request (LTR)
        String ltrXml = loginTicketRequestGenerator.generateLoginTicketRequest(service);

        // Firmar el LTR como CMS
        byte[] signedBytes = signCMS(ltrXml.getBytes(StandardCharsets.UTF_8), privateKey, certificate);
        String signedBase64 = Base64.toBase64String(signedBytes);

        // Enviar a WSAA
        String endpoint = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";
        SOAPMessage request = createSOAPRequest(signedBase64);
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnectionFactory.createConnection();

        SOAPMessage response = connection.call(request, endpoint);
        connection.close();

        // Convertir SOAPMessage a String
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.writeTo(out);
        return out.toString(StandardCharsets.UTF_8);  // <-- ESTO ES CLAVE
    }

    private byte[] signCMS(byte[] data, PrivateKey privateKey, X509Certificate cert) throws Exception {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        org.bouncycastle.cms.CMSTypedData msg = new org.bouncycastle.cms.CMSProcessableByteArray(data);
        org.bouncycastle.cms.CMSSignedDataGenerator gen = new org.bouncycastle.cms.CMSSignedDataGenerator();
        gen.addSignerInfoGenerator(new org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder(
                new org.bouncycastle.operator.bc.BcDigestCalculatorProvider())
                .build(new org.bouncycastle.operator.jcajce.JcaContentSignerBuilder("SHA1withRSA")
                        .setProvider("BC").build(privateKey), cert));
        gen.addCertificate(new org.bouncycastle.cert.jcajce.JcaX509CertificateHolder(cert));
        org.bouncycastle.cms.CMSSignedData sigData = gen.generate(msg, true);
        return sigData.getEncoded();
    }

    private SOAPMessage createSOAPRequest(String cmsBase64) throws Exception {
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage message = mf.createMessage();
        SOAPPart part = message.getSOAPPart();
        SOAPEnvelope envelope = part.getEnvelope();
        SOAPBody body = envelope.getBody();
        SOAPElement loginCms = body.addChildElement("loginCms", "", "http://wsaa.view.sua.dvadac.desein.afip.gov");
        loginCms.addChildElement("in0").addTextNode(cmsBase64);
        message.getMimeHeaders().addHeader("SOAPAction", "loginCms");
        message.saveChanges();
        return message;
    }


    /*public String loginCms(String service, String certPath, String keyPath) throws Exception {
       // 1. Generar XML TRA
        String tra = loginTicketRequestGenerator.generateLoginTicketRequest(service);

        // 2. Cargar Certificado
        X509Certificate cert;
        try (PEMParser reader = new PEMParser(new FileReader(certPath))) {
            cert = (X509Certificate) new JcaX509CertificateConverter().getCertificate((X509CertificateHolder) reader.readObject());
        }

        // 3. Cargar clave privada
        PrivateKey privateKey;
        try (PEMParser reader = new PEMParser(new FileReader(keyPath))) {
            Object keyObject = reader.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            privateKey = converter.getPrivateKey((PrivateKeyInfo) keyObject);
        }

        // 4. Firmar XML como CMS
        CMSTypedData msg = new CMSProcessableByteArray(tra.getBytes());
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(privateKey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
            new JcaDigestCalculatorProviderBuilder().build()).build(signer, cert));
        gen.addCertificates(new JcaCertStore(Collections.singletonList(cert)));
        CMSSignedData signedData = gen.generate(msg, true);
        String cmsBase64 = Base64.toBase64String(signedData.getEncoded());

        // 5. Enviar SOAP a AFIP
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPBody body = message.getSOAPBody();
        SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration("wsaa", "http://wsaa.view.sua.dvadac.desein.afip.gov");
        SOAPBodyElement elem = body.addBodyElement(envelope.createName("loginCms", "wsaa", "http://wsaa.view.sua.dvadac.desein.afip.gov"));
        elem.addChildElement("in0").addTextNode(cmsBase64);

        message.saveChanges();
        message.getMimeHeaders().addHeader("SOAPAction", "loginCms");
        SOAPMessage response = soapConnection.call(message, "https://wsaahomo.afip.gov.ar/ws/services/LoginCms");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.writeTo(out);
        return out.toString();
    }*/
}
