package com.auth.app.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.cookie")
public class CookieProperties {
    private String domain;
    private boolean secure;
    private String sameSite;
    private String path;

    // Getters y setters
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public boolean isSecure() { return secure; }
    public void setSecure(boolean secure) { this.secure = secure; }

    public String getSameSite() { return sameSite; }
    public void setSameSite(String sameSite) { this.sameSite = sameSite; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

}
