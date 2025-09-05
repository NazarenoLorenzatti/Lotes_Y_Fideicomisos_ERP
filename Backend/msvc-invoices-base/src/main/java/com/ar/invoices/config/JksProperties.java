package com.ar.invoices.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jks")
public class JksProperties {
    private String keystorePath;
    private String keystorePassword;
    private String keystoreKeyAlias;
    private String keystorePrivateKeyPassphrase;

    // Getters y setters
    public String getKeystorePath() { return keystorePath; }
    public void setKeystorePath(String keystorePath) { this.keystorePath = keystorePath; }

    public String getKeystorePassword() { return keystorePassword; }
    public void setKeystorePassword(String keystorePassword) { this.keystorePassword = keystorePassword; }

    public String getKeystoreKeyAlias() { return keystoreKeyAlias; }
    public void setKeystoreKeyAlias(String keystoreKeyAlias) { this.keystoreKeyAlias = keystoreKeyAlias; }

    public String getKeystorePrivateKeyPassphrase() { return keystorePrivateKeyPassphrase; }
    public void setKeystorePrivateKeyPassphrase(String keystorePrivateKeyPassphrase) { this.keystorePrivateKeyPassphrase = keystorePrivateKeyPassphrase; }
}
